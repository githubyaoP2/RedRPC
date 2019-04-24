package com.red.api.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ExecutorService;

public class ClientHandler extends SimpleChannelInboundHandler {

    //自定义实现适合执行IO密集型任务的线程池，即优先添加线程而不是入队列
//    ExecutorService executorService;
//
//    public ClientHandler(ExecutorService executorService) {
//        this.executorService = executorService;
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }
}
