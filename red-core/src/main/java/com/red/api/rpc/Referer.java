package com.red.api.rpc;

import com.red.api.config.ProtocolConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class Referer {

    public abstract void init();

    public abstract boolean isAvailable();

    public abstract int activeRefererCount();

    public abstract String getUrl();

    public abstract String getGroup();

}
