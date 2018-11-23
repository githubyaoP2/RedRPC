package com.red.netty.handler;

import com.red.message.MessageType;
import com.red.message.RedMessage;
import com.red.message.RequestMsg;
import com.red.message.ResponseMsg;
import com.red.server.RedServer;
import com.red.server.ServerInvoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server receive message");
        if(msg instanceof RedMessage){
            RedMessage message = (RedMessage) msg;
            switch (message.getMessageType()){
                case ClientRequest:
                    executeRequest(ctx,(RequestMsg) message.getMessage());
                    break;
                case PING:
                    executePing(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    private void executeRequest(ChannelHandlerContext ctx,RequestMsg request){
        if(RedServer.isProvide(request.getInstanceName(),request.getMethodName(),request.getArgs())){
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        ServerInvoker serverInvoker = new ServerInvoker(request.getInstanceName(),request.getMethodName(),request.getArgs());
                        RedMessage result = serverInvoker.execute();
                        ctx.writeAndFlush(result);
                    }catch (Exception e){
                        System.out.println(e);
                    }
                }
            });
        }
    }

    private void executePing(ChannelHandlerContext ctx){
        RedMessage message = new RedMessage(null,MessageType.ACK);
        ctx.writeAndFlush(message);
    }

}
