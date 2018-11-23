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
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.concurrent.*;

public class GeneralInvoker {

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
    static Logger logger = Logger.getLogger(GeneralInvoker.class);

    private CountDownLatch latch = new CountDownLatch(1);
    private CountDownLatch waitConnect = new CountDownLatch(1);
    private CountDownLatch waitResult = new CountDownLatch(1);
    private Channel channel;
    private ResponseMsg responseMsg;
    private Stopwatch stopwatch = Stopwatch.createUnstarted();

    public ResponseMsg invoke(RedMessage redMessage){
        return invoke(redMessage,0);
    }

    public ResponseMsg invoke(RedMessage redMessage,long delays){
        try {
            if (channel == null) {
                stopwatch.start();

                String ip ;//= getServiceIP(((RequestMsg) redMessage.getMessage()).getInstanceName());
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
                //ToDo:定时发送PING确认保存的有效
            }
            long last = 0l;
            if((last = delays-stopwatch.elapsed(TimeUnit.SECONDS)) > 0){
                waitResult.await(last ,TimeUnit.SECONDS);
            }
        }catch (Exception e){
            //ToDo:一个IP连接超时需要尝试连接新的IP，getIP与connect结合起来变成循环
            System.out.println(e);
            logger.error("connect to server fail!");
        }
        return getResponseMsg();
    }

    /**
     * @param instanceName
     * @throws Exception
     */
    private String getServiceIP(String instanceName) throws Exception{
        //获取地址后需要向其注册Watcher
//        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 50000, new Watcher() {
//            public void process(WatchedEvent watchedEvent) {
//                if(Event.KeeperState.SyncConnected == watchedEvent.getState()){
//                    if(Event.EventType.None == watchedEvent.getType()&&null == watchedEvent.getPath()) {
//                        latch.countDown();
//                        System.out.println("ZooKeeper connected");
//                    }
//                }
//            }
//        });
//        latch.await();
        ZkClient zkClient = new ZkClient("localhost:2181",5000);
        String path = Constants.zk_root_path + Constants.separator +instanceName;
        List<String> ips =  zkClient.getChildren(path);
        if(ips == null || ips.isEmpty())
            return null;
        //ToDo:没有负载均衡，请求会分布到同一台机器
        //Do:socket建立成功到建立netty连接的过程中服务器断掉会导致调用直接返回,需要重新获取IP
        for(String ip:ips){
            if(PatternUtil.isIPv4Address(ip)){
                //Socket可能会阻塞很长时间
                if(PingUtil.isReachable(ip)){
                    zkClient.subscribeStateChanges(new IZkStateListener() {
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
            ChannelFuture channelFuture = bootstrap.connect(ip, Integer.valueOf(port));
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){

        }finally {
            group.shutdownGracefully();
        }

        //心跳检测服务端是否有效，否则重新获取IP
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

//    public void startHeartBeatTask(){
//        Timer timer = new Timer();
//        CyclicBarrier barrier = new CyclicBarrier(4, new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                RedMessage redMessage = new RedMessage(null,MessageType.PING);
//                getChannel().writeAndFlush(redMessage);
//
//            }
//        },60000l,60000l);
//    }
}
