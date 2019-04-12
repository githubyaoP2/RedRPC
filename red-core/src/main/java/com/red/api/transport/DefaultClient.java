package com.red.api.transport;

import com.red.api.cluster.Cluster;
import com.red.api.config.ClientConfig;
import com.red.api.config.ProtocolConfig;
import com.red.api.util.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultClient<T> {

    //ToDO:group可以配多个，Protoconfig只有一个
    private AtomicBoolean inited = new AtomicBoolean(false);
    private ClientConfig<T> clientConfig;
    //虽然保留多个集群，但每次访问只能选择一个集群访问
    private Map<ProtocolConfig,List<Cluster>> clusterListMap;
    //Bootstrap bootstrap;
    public DefaultClient(ClientConfig clientConfig){
        this.clientConfig = clientConfig;
        //初始化集群
        ProtocolConfig protocolConfig = clientConfig.getProtocolConfig();
        String[] group = clientConfig.getGroup().split(Constants.spliter);
        String interfaceName = clientConfig.getInterfaceClass().getName();
        //Refer类中
//        EventLoopGroup group = new NioEventLoopGroup();
//        bootstrap = new Bootstrap();
//        bootstrap.group(group).channel(NioSocketChannel.class)
//                .option(ChannelOption.TCP_NODELAY, true)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel channel) throws Exception {
//
//                    }
//                });
    }

    public T getRef() {
        return (T)Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{clientConfig.getInterfaceClass()},null);
    }

    public void discover(){
        String[] group = clientConfig.getGroup().split(Constants.separator);
        String interfaceName = clientConfig.getInterfaceClass().getName();
    }

}
