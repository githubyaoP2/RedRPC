package com.red.client;

import com.google.common.base.Stopwatch;
import com.red.message.RedMessage;
import com.red.message.RequestMsg;
import com.red.message.ResponseMsg;
import com.red.netty.handler.ClientHandler;
import com.red.util.Constants;
import com.red.util.PatternUtil;
import com.red.util.PingUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;

import java.util.List;
import java.util.concurrent.*;

public class GeneralInvoker {

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
    static Logger logger = Logger.getLogger(GeneralInvoker.class);

    private CountDownLatch waitConnect = new CountDownLatch(1);
    private CountDownLatch waitResult = new CountDownLatch(1);
    private Channel channel;
    private ResponseMsg responseMsg;
    private Stopwatch stopwatch = Stopwatch.createUnstarted();

    public ResponseMsg invoke(RedMessage redMessage){
        try {
            if (channel == null) {
                stopwatch.start();
                String ip ;
                do {
                    ip = getServiceIP(((RequestMsg) redMessage.getMessage()).getInstanceName());
                    final String ip1 = ip;
                    executorService.submit(new Runnable() {
                        public void run() {
                            connectToServer(ip1.split(":")[0], ip1.split(":")[1]);
                        }
                    });
                    waitConnect.await();
                }while(channel == null && ip != null);
                stopwatch.stop();
            }
            //防止Netty没有连接成功
            if(getChannel() != null) {
                getChannel().writeAndFlush(redMessage);
            }
            long last = 0l;
            waitResult.await();
        }catch (Exception e){
            System.out.println(e);
            logger.error("fail!",e);
            responseMsg.setReason(e.getMessage());
            responseMsg.setInvokeSucess(Constants.invoke_fail);
        }
        return getResponseMsg();
    }

    public ResponseMsg invoke(RedMessage redMessage,long delays){
        try {
            if (channel == null) {
                stopwatch.start();
                String ip ;
                do {
                    ip = getServiceIP(((RequestMsg) redMessage.getMessage()).getInstanceName());
                    final String ip1 = ip;
                    executorService.submit(new Runnable() {
                        public void run() {
                            connectToServer(ip1.split(":")[0], ip1.split(":")[1]);
                        }
                    });
                    waitConnect.await(delays, TimeUnit.SECONDS);
                }while(channel == null && ip != null);
                stopwatch.stop();
            }
            //防止Netty没有连接成功
            if(getChannel() != null) {
                getChannel().writeAndFlush(redMessage);
            }
            long last = 0l;
            if((last = delays-stopwatch.elapsed(TimeUnit.SECONDS)) > 0){
                waitResult.await(last ,TimeUnit.SECONDS);
            }else{
                ResponseMsg rsp = new ResponseMsg();
                rsp.setInvokeSucess(Constants.invoke_timeout);
                rsp.setReason("connect timeout");
                return rsp;
            }
        }catch (Exception e){
            System.out.println(e);
            logger.error("fail!",e);
            responseMsg.setReason(e.getMessage());
            responseMsg.setInvokeSucess(Constants.invoke_fail);
        }
        return getResponseMsg();
    }

    /**
     * @param instanceName
     * @throws Exception
     */
    private String getServiceIP(String instanceName) throws Exception{
        ZkClient zkClient = new ZkClient("localhost:2181",5000);
        String path = Constants.zk_root_path + Constants.separator +instanceName;
        List<String> ips =  zkClient.getChildren(path);
        if(ips == null || ips.isEmpty())
            return null;
        for(String ip:ips){
            if(PatternUtil.isIPv4Address(ip)){
                //Socket可能会阻塞很长时间
                if(PingUtil.isReachable(ip)){
                    zkClient.subscribeStateChanges(new IZkStateListener() {
                        //通过在节点上注册Watcher代替Ping进行心跳检测
                        @Override
                        public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                            if (keeperState == Watcher.Event.KeeperState.Disconnected) {
                                setChannel(null);
                            }
                        }

                        @Override
                        public void handleNewSession() throws Exception {
                            //Called after the zookeeper session has expired and a new session has been created.
                        }

                        @Override
                        public void handleSessionEstablishmentError(Throwable throwable) throws Exception {
                            //Called when a session cannot be re-established.
                        }
                    });
                    return ip;
                }
            }
        }
        return null;
    }

    private void connectToServer(String ip,String port){
        final GeneralInvoker generalInvoker = this;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ObjectEncoder());
                            channel.pipeline().addLast(new ObjectDecoder(
                                    ClassResolvers.weakCachingConcurrentResolver(getClass().getClassLoader())));
                            channel.pipeline().addLast(new ClientHandler(generalInvoker));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(ip, Integer.valueOf(port)).sync();
//            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){

        }finally {
//            group.shutdownGracefully();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public CountDownLatch getWait() {
        return waitConnect;
    }

    public ResponseMsg getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(ResponseMsg responseMsg) {
        this.responseMsg = responseMsg;
    }

    public CountDownLatch getWaitResult() {
        return waitResult;
    }

}
