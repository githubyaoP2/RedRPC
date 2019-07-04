package com.red.api.rpc;

import com.red.api.config.ProtocolConfig;
import com.red.api.config.ServiceConfig;
import com.red.api.netty.codec.RedDecoder;
import com.red.api.netty.codec.RedEncoder;
import com.red.api.netty.handler.RedMessageHandler;
import com.red.api.netty.server.ServerGuard;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class RedExporter implements Exporter{

    ServiceConfig serviceConfig;
    ServerBootstrap serverBootstrap;
    NioEventLoopGroup receiveGroup;
    NioEventLoopGroup ioGroup;
    ChannelFuture channelFuture;
    ServerGuard serverGuard;

    private ThreadPoolExecutor threadPoolExecutor;//服务端统一业务线程池

    public RedExporter(ServiceConfig serviceConfig){
        this.serviceConfig = serviceConfig;
    }

    @Override
    public void export() {
        serverBootstrap = new ServerBootstrap();
        receiveGroup = new NioEventLoopGroup();
        ioGroup = new NioEventLoopGroup();
        MessageToByteEncoder redEncoder;
        ByteToMessageDecoder redDecoder;
        ProtocolConfig protocolConfig = serviceConfig.getProtocolConfig();
        serverGuard = new ServerGuard(protocolConfig.getMaxConnections());
//        switch (protocolConfig.getCodec()){
//            case "red":
//                redEncoder = new RedEncoder(protocolConfig.getSerialization());
//                redDecoder = new RedDecoder(protocolConfig.getSerialization());
//                break;
//            case "local":
//            default:
//                redEncoder = new RedEncoder(protocolConfig.getSerialization());
//                redDecoder = new RedDecoder(protocolConfig.getSerialization());
//        }
        serverBootstrap.group(receiveGroup,ioGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(serverGuard);
                        nioSocketChannel.pipeline().addLast(new RedDecoder(protocolConfig.getSerialization()));
                        nioSocketChannel.pipeline().addLast(new RedEncoder(protocolConfig.getSerialization()));
                        nioSocketChannel.pipeline().addLast(new RedMessageHandler(threadPoolExecutor));
                    }
                });
        channelFuture = serverBootstrap.bind(new InetSocketAddress(Integer.parseInt(serviceConfig.getPort()))).syncUninterruptibly();
    }

    public void close(){
        if(channelFuture.channel() != null){
            channelFuture.channel().close();
            receiveGroup.shutdownGracefully();
            ioGroup.shutdownGracefully();
            receiveGroup = null;
            ioGroup = null;
        }

        if(serverGuard != null){
            serverGuard.close();
        }

        if(threadPoolExecutor != null){
            threadPoolExecutor.shutdown();
        }
    }

}
