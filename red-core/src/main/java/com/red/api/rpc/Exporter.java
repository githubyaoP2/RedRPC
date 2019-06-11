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
public class Exporter {

    Provider provider;
    ServiceConfig serviceConfig;
    Channel channel;
    AtomicBoolean exported = new AtomicBoolean(false);
    NioEventLoopGroup receiveGroup;
    NioEventLoopGroup ioGroup;

    public Exporter(Provider provider, ServiceConfig serviceConfig) {
        this.provider = provider;
        this.serviceConfig = serviceConfig;
    }



    public boolean export(){
        if(exported.get())
            return true;
        synchronized (this) {
//            for (ProtocolConfig protocolConfig : serviceConfig.getProtocolConfigList()) {
//                Integer port = serviceConfig.getPort(protocolConfig.getName());
//                receiveGroup = new NioEventLoopGroup();
//                ioGroup = new NioEventLoopGroup(protocolConfig.getIothreads());
//                ServerBootstrap serverBootstrap = new ServerBootstrap();
//                serverBootstrap.group(receiveGroup, ioGroup)
//                        .channel(NioServerSocketChannel.class)
//                        .childHandler(new ChannelInitializer<SocketChannel>() {
//                            protected void initChannel(SocketChannel channel) throws Exception {
//                                channel.pipeline().addLast(new ServerGuard());
//                            }
//                        });
//                ChannelFuture future = serverBootstrap.bind(port).syncUninterruptibly();
//                channel = future.channel();
//                exported.compareAndSet(false,true);
//            }
        }
        return false;
    }


    public void close(){
        if(exported.get()){
            synchronized(this){

            }
        }
    }

}
