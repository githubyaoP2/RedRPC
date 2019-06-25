package com.red.api.rpc;

import com.red.api.config.ProtocolConfig;
import com.red.api.message.RequestMessage;
import com.red.api.message.ResponseFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

//服务调用类
public interface Referer {

    void init();

    boolean isAvailable();

    int activeRefererCount();

    String getUrl();

    String getGroup();

    public ResponseFuture request(RequestMessage requestMessage);

}
