package com.red.api.rpc;

import com.red.api.config.ProtocolConfig;
import com.red.api.message.RequestMessage;
import com.red.api.message.ResponseFuture;
import com.red.api.netty.codec.RedDecoder;
import com.red.api.netty.codec.RedEncoder;
import com.red.api.netty.handler.RedMessageHandler;
import com.red.api.transport.DefaultClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RedReferer implements Referer{

    public static final String STATE_AVAIABLE = "available";//可用
    public static final String STATE_UNAVAIABLE = "unAvailable";//不可用
    AtomicInteger errorCount = new AtomicInteger(0);
    public final int ERROR_VALUE = 10;//熔断阀值

    ProtocolConfig protocolConfig;
    Bootstrap bootstrap;
    NioEventLoopGroup nioEventLoopGroup;
    List<Channel> channelList = new ArrayList<>();
    volatile String state = STATE_UNAVAIABLE;
    AtomicInteger activeRefererCount = new AtomicInteger(0);
    final String group;
    final String url;
    ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    public RedReferer(String group,String url, ProtocolConfig protocolConfig) {
        this.url = url;
        this.protocolConfig = protocolConfig;
        this.group = group;
    }

    @Override
    public ResponseFuture request(RequestMessage requestMessage){
        if(isAvailable()){
            activeRefererCount.incrementAndGet();
            Channel channel = channelList.get(threadLocalRandom.nextInt(channelList.size()));
            ResponseFuture responseFuture = new ResponseFuture();
            DefaultClient.requetIdMap.put(requestMessage.getRequestId(),responseFuture);
            channel.writeAndFlush(requestMessage).syncUninterruptibly().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        //ToDO:首先要发送成功，其实要根据收回的结果来判断是增加还是重置失败次数
                        if(responseFuture.isDone()){
                            resetErrorInvoke();
                        }else {
                            addErrorInvoke();
                        }
                    }else {
                        addErrorInvoke();
                    }
                }
            });


            activeRefererCount.decrementAndGet();
            return responseFuture;
        }
        return null;
    }

    @Override
    public void init() {
        nioEventLoopGroup = new NioEventLoopGroup(protocolConfig.getIothreads());
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
        RedMessageHandler redMessageHandler = new RedMessageHandler();
        bootstrap.group(nioEventLoopGroup).
                channel(NioSocketChannel.class).
                handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(redDecoder);
                        nioSocketChannel.pipeline().addLast(redEncoder);
                        nioSocketChannel.pipeline().addLast(redMessageHandler);
                    }
                });
        initConnectPool();
    }

    public void initConnectPool(){
        String[] ipPort = url.split(":");
        for (int i=0; i<protocolConfig.getClientConnections(); i++){
            bootstrap.connect(ipPort[0],Integer.parseInt(ipPort[1])).addListener(new GenericFutureListener<ChannelFuture>() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        channelList.add(future.channel());
                    }
                }
            });
        }
        if(!channelList.isEmpty()){
            state = STATE_AVAIABLE;
        }
    }

    @Override
    public boolean isAvailable() {
        return state == STATE_AVAIABLE;
    }

    @Override
    public int activeRefererCount() {
        return activeRefererCount.get();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public void close(){
        nioEventLoopGroup.shutdownGracefully();
        nioEventLoopGroup = null;
        channelList.forEach(Channel::close);
    }

    public void reConnect(){
        channelList.clear();
        initConnectPool();
    }

    private void addErrorInvoke(){
        errorCount.incrementAndGet();
        if(errorCount.get() > ERROR_VALUE && isAvailable()){
            state = STATE_UNAVAIABLE;
        }
    }

    private void resetErrorInvoke(){
        errorCount.set(0);
        if(!isAvailable()){
            state = STATE_AVAIABLE;
        }
    }
}
