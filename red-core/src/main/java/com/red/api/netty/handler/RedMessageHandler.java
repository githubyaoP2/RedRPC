package com.red.api.netty.handler;

import com.red.api.message.*;
import com.red.api.transport.DefaultClient;
import com.red.api.transport.DefaultServer;
import com.red.api.util.LoggerUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class RedMessageHandler extends SimpleChannelInboundHandler<RedMessage> {

    private ThreadPoolExecutor threadPoolExecutor;//业务线程池
   // private MessageHandler messageHandler;//各种具体消息处理器

    private AtomicInteger rejectNum = new AtomicInteger(0);//拒绝请求数量

    public RedMessageHandler(){

    }

    public RedMessageHandler(ThreadPoolExecutor threadPoolExecutor){
        this.threadPoolExecutor = threadPoolExecutor;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RedMessage redMessage) throws Exception {
        if(threadPoolExecutor == null){
            processMessage(channelHandlerContext,redMessage);
        }else {
            try {
                threadPoolExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        processMessage(channelHandlerContext,redMessage);
                    }
                });
            }catch (RejectedExecutionException ex){
                LoggerUtil.error("业务线程池已满，任务执行失败");
            }
        }
    }

    private void processMessage(ChannelHandlerContext channelHandlerContext,RedMessage redMessage){
        if(redMessage instanceof RequestMessage){
            RequestMessage requestMessage = (RequestMessage) redMessage;
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setRequestId(requestMessage.getRequestId());
            Class[] paramTypes = new Class[requestMessage.getArguments().length];
            StringBuilder sb = new StringBuilder();
            sb.append(requestMessage.getImplCode());
            sb.append(":");
            sb.append(requestMessage.getMethodName());
            sb.append("(");
            int i = 0;
            for (Object o:requestMessage.getArguments()){
                sb.append(o.getClass().getSimpleName());
                paramTypes[i++] = o.getClass();
            }
            sb.append(")");
            if(DefaultServer.implClassMap.containsKey(sb.toString())){
                Object o = DefaultServer.implClassMap.get(sb.toString());
                try {
                    Method m = o.getClass().getMethod(requestMessage.getMethodName(), paramTypes);
                    Object result = m.invoke(o,requestMessage.getArguments());
                    responseMessage.setValue(result);
                }catch (Exception e){
                    responseMessage.setException(e);
                    LoggerUtil.error("");
                }
            }else {
                responseMessage.setValue("未找到对应方法");
            }
            channelHandlerContext.writeAndFlush(responseMessage);
        }else if(redMessage instanceof ResponseMessage){
            ResponseMessage responseMessage = (ResponseMessage) redMessage;
            long requestId = responseMessage.getRequestId();
            ResponseFuture responseFuture = DefaultClient.requetIdMap.get(requestId);
            if(responseMessage.getValue() != null){
                responseFuture.setValue(responseMessage.getValue());
            }else {
                responseFuture.setValue(responseMessage.getException());
            }
            responseFuture.countDown();
        }else if(redMessage instanceof HeartBeatMessage){
            HeartBeatMessage heartBeatMessage = (HeartBeatMessage) redMessage;
        }else {
            LoggerUtil.warn("不支持的数据类型");
        }
    }

//    private
}
