package com.red.metrics;

import com.red.netty.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class MetricServer {
    public void startMetric(boolean start){
        NioEventLoopGroup receiveGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup ioGroup = new NioEventLoopGroup(5);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(receiveGroup,ioGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ObjectEncoder());
                            channel.pipeline().addLast(new ObjectDecoder(
                                    ClassResolvers.weakCachingConcurrentResolver(getClass().getClassLoader())));
                            channel.pipeline().addLast(new ServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(8088).sync();
//            future.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                    if(channelFuture.isSuccess()){
//                        startSuc.countDown();
//                    }
//                }
//            });
            System.out.println("Netty启动");
            future.channel().closeFuture().sync();
    }catch (Exception e){
        }
    }
}
