package com.red.api.netty.server;

import com.red.api.util.LoggerUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//服务端连接守卫
//保存并限制连接
public class ServerGuard extends ChannelInboundHandlerAdapter {
    private Map<String,Channel> channels = new ConcurrentHashMap<>();

    private int maxConnections;

    public ServerGuard(int maxConnections){
        this.maxConnections = maxConnections;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if(channels.size() >= maxConnections){
            LoggerUtil.info("超出最大连接机器数限制");
            channel.close();
        }else {
            channels.put(getUniqueConnectKey(channel),channel);
            ctx.fireChannelRegistered();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.remove(getUniqueConnectKey(channel));
        ctx.fireChannelUnregistered();
    }

    private String getUniqueConnectKey(Channel channel){
        InetSocketAddress localAddress = (InetSocketAddress)channel.localAddress();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        StringBuilder sb = new StringBuilder();
        if(localAddress == null || localAddress.getAddress() == null){
            sb.append("null");
        }else {
            sb.append(localAddress.getAddress().getHostAddress()).append(":").append(localAddress.getPort());
        }
        sb.append("--");
        if(remoteAddress == null || remoteAddress.getAddress() == null){
            sb.append("null");
        }else {
            sb.append(remoteAddress.getAddress().getHostAddress()).append(":").append(remoteAddress.getPort());
        }
        return sb.toString();
    }
}
