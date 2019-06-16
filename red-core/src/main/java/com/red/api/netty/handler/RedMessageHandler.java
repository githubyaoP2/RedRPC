package com.red.api.netty.handler;

import com.red.api.message.HeartBeatMessage;
import com.red.api.message.RedMessage;
import com.red.api.message.RequestMessage;
import com.red.api.message.ResponseMessage;
import com.red.api.util.LoggerUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

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
            processMessage(redMessage);
        }else {
            try {
                threadPoolExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        processMessage(redMessage);
                    }
                });
            }catch (RejectedExecutionException ex){
                LoggerUtil.error("业务线程池已满，任务执行失败");
            }
        }
    }

    private void processMessage(RedMessage redMessage){
        if(redMessage instanceof RequestMessage){
            RequestMessage requestMessage = (RequestMessage) redMessage;
        }else if(redMessage instanceof ResponseMessage){

        }else if(redMessage instanceof HeartBeatMessage){

        }else {
            LoggerUtil.warn("不支持的数据类型");
        }
    }

//    private
}
