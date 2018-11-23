package com.red.netty.handler;

import com.red.message.RedMessage;
import com.red.message.ResponseMsg;
import com.red.client.GeneralInvoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    GeneralInvoker generalInvoker;

    public  ClientHandler(GeneralInvoker generalInvoker){
        this.generalInvoker = generalInvoker;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        generalInvoker.setChannel(ctx.channel());
        generalInvoker.getWait().countDown();
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到消息");
        if(msg instanceof RedMessage){
            RedMessage message = (RedMessage) msg;
            switch (message.getMessageType()){
                case ServerResponse:
                    executeResponse((ResponseMsg) message.getMessage());
                case ACK:
                    executeAck();
                    break;
                default:
                    break;
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    public void executeResponse(ResponseMsg responseMsg){
        generalInvoker.setResponseMsg(responseMsg);
        generalInvoker.getWaitResult().countDown();
    }

    private void executeAck(){
        //ToDO:新启线程循环接收ACK，没收到栅栏-1并迅速重新发送PING，栅栏设置为5，重连5次失败后从缓存map中清除
    }
}
