package com.red.server;

import com.red.Model.ServiceModel;
import com.red.Model.ServicesModel;
import com.red.netty.handler.ServerHandler;
import com.red.util.Constants;
import com.red.util.ReflectionUtil;
import com.red.util.XmlUtil;
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
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RedServer {
    static Logger logger = Logger.getLogger(RedServer.class);
    public static Map<String,Object> serviceMap = new HashMap<>();
    static private CountDownLatch latch = new CountDownLatch(1);
    static private CountDownLatch startSuc = new CountDownLatch(1);

    public static void main(String[] args) {
        startUp();
        while(true){

        }
    }

    public static void startUp(){
        try {
            parseServices();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    nettyInit();
                }
            }).start();
            startSuc.await();
            writeToZK();
        }catch (Exception e){
            logger.error("service start error");
        }
    }

    //解析配置文件，获取需要发布的服务
    private static void parseServices() throws Exception{
        ServicesModel servicesModel = XmlUtil.getServiceFromXml(Constants.service_path);
        for(ServiceModel serviceModel : servicesModel.getService()){
            Class instanceClass = Class.forName(serviceModel.getInstanceName());
            serviceMap.put(serviceModel.getInstanceName(),instanceClass.newInstance());
        }
    }

    private static void nettyInit(){
        NioEventLoopGroup receiveGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup ioGroup = new NioEventLoopGroup(50);
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
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        startSuc.countDown();
                    }
                }
            });
            System.out.println("Netty启动");
            future.channel().closeFuture().sync();
            System.out.println("Netty关闭");
        }catch (InterruptedException e){
            logger.error("Broker Server Interrupted"+e);
        }finally {
            receiveGroup.shutdownGracefully();
            ioGroup.shutdownGracefully();
        }
    }

    //写入ZooKeeper
    //ToDO: 删除服务节点事件应与主线程隔离，否则主线程意外中断，服务节点无法删除
    private static void writeToZK(){
        try {
            ZkClient zkClient = new ZkClient("localhost:2181",5000);
            if(!zkClient.exists(Constants.zk_root_path )){
                zkClient.createPersistent(Constants.zk_root_path,true);
            }
            String IP = Inet4Address.getLocalHost().getHostAddress();
            serviceMap.forEach((instanceName,implName)->{
                if(!zkClient.exists(Constants.zk_root_path + Constants.separator + instanceName)){
                    zkClient.createPersistent(Constants.zk_root_path + Constants.separator+ instanceName);
                    zkClient.subscribeChildChanges(Constants.zk_root_path + Constants.separator + instanceName, new IZkChildListener() {
                        @Override
                        public void handleChildChange(String s, List<String> list) throws Exception {
                            if(list.isEmpty()) {
                                zkClient.delete(Constants.zk_root_path + Constants.separator+ instanceName);
                            }
                        }
                    });
                }
                zkClient.createEphemeral(Constants.zk_root_path+ Constants.separator + instanceName+Constants.separator+IP+":8080","hihi".getBytes());
            });
        }catch (Exception e){
            logger.error(e);
        }
    }

    public static boolean isProvide(String instanceName,String methodName,Object...params){
        return ReflectionUtil.ifMethodExist(serviceMap,instanceName,methodName,params);
    }
}
