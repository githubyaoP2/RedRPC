package com.red.api.rpc;

import com.red.api.config.ProtocolConfig;
import com.red.api.config.ServiceConfig;
import com.red.api.netty.codec.RedDecoder;
import com.red.api.netty.codec.RedEncoder;
import com.red.api.netty.handler.RedMessageHandler;
import com.red.api.netty.server.ServerGuard;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.net.InetSocketAddress;

public class RedExporter implements Exporter{

    ServiceConfig serviceConfig;
    ServerBootstrap serverBootstrap;
    NioEventLoopGroup receiveGroup;
    NioEventLoopGroup ioGroup;

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
        switch (protocolConfig.getCodec()){
            case "red":
                redEncoder = new RedEncoder(protocolConfig.getSerialization());
                redDecoder = new RedDecoder(protocolConfig.getSerialization());
                break;
            case "local":
            default:
                redEncoder = new RedEncoder(protocolConfig.getSerialization());
                redDecoder = new RedDecoder(protocolConfig.getSerialization());
        }
        serverBootstrap.group(receiveGroup,ioGroup)
                .channel(ServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new ServerGuard(protocolConfig.getMaxConnections()));
                        nioSocketChannel.pipeline().addLast(redDecoder);
                        nioSocketChannel.pipeline().addLast(redEncoder);
                        nioSocketChannel.pipeline().addLast(new RedMessageHandler());
                    }
                });
        serverBootstrap.bind(new InetSocketAddress(Integer.parseInt(serviceConfig.getPort())));
    }

}
