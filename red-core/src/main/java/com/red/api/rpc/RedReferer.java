package com.red.api.rpc;

import com.red.api.config.ProtocolConfig;
import com.red.api.netty.ClientHandler;
import com.red.api.netty.codec.RedDecoder;
import com.red.api.netty.codec.RedEncoder;
import com.red.api.transport.Channel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;
import java.util.List;

public class RedReferer implements Referer{

    String url;
    ProtocolConfig protocolConfig;
    Bootstrap bootstrap;
    List<Channel> channelList = new ArrayList<>();

    public RedReferer(String url, ProtocolConfig protocolConfig) {
        this.url = url;
        this.protocolConfig = protocolConfig;
    }

    @Override
    public void init() {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(protocolConfig.getIothreads());
        bootstrap = new Bootstrap();
        MessageToByteEncoder redEncoder;// = new RedEncoder(protocolConfig.getSerialization());
        ByteToMessageDecoder redDecoder;// = new RedDecoder(protocolConfig.getSerialization());
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
        ClientHandler clientHandler = new ClientHandler();
        bootstrap.group(nioEventLoopGroup).
                channel(NioSocketChannel.class).
                handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(redDecoder);
                        nioSocketChannel.pipeline().addLast(redEncoder);
                        nioSocketChannel.pipeline().addLast(clientHandler);
                    }
                });
    }

    public void initConnectPool(){
        String[] ipPort = url.split(":");
        for (int i=0; i<protocolConfig.getClientConnections(); i++){
            bootstrap.connect(ipPort[0],Integer.parseInt(ipPort[1])).addListener(new GenericFutureListener<ChannelFuture>() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    future.channel();
                }
            });
        }
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public int activeRefererCount() {
        return 0;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getGroup() {
        return null;
    }
}
