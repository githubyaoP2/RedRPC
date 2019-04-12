package com.red.api.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//服务端连接守卫
//保存并限制连接
public class ServerGuard extends ChannelInboundHandlerAdapter {
    private Map<String,Channel> channels = new ConcurrentHashMap<>();
    private int maxConnections;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }
}
