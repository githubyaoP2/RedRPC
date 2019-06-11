package com.red.api.rpc;

import com.red.api.config.ProtocolConfig;
import com.red.api.config.ServiceConfig;
import com.red.api.netty.ServerGuard;
import com.red.api.netty.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.concurrent.atomic.AtomicBoolean;

//发布服务类
public interface Exporter {
    void init();
}
