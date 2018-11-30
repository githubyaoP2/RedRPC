package com.red.metrics;

import com.red.message.MessageType;
import com.red.message.RedMessage;
import com.red.model.MetricModel;
import com.red.util.Constants;

import org.I0Itec.zkclient.ZkClient;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.stream.Collectors;

public class MetricServer {
//    public void startMetric(boolean start){
//        NioEventLoopGroup receiveGroup = new NioEventLoopGroup(1);
//        NioEventLoopGroup ioGroup = new NioEventLoopGroup(5);
//        try {
//            ServerBootstrap serverBootstrap = new ServerBootstrap();
//            serverBootstrap.group(receiveGroup,ioGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        protected void initChannel(SocketChannel channel) throws Exception {
//                            channel.pipeline().addLast(new ObjectEncoder());
//                            channel.pipeline().addLast(new ObjectDecoder(
//                                    ClassResolvers.weakCachingConcurrentResolver(getClass().getClassLoader())));
//                            channel.pipeline().addLast(new ServerHandler());
//                        }
//                    });
//            ChannelFuture future = serverBootstrap.bind(8088).sync();
////            future.addListener(new ChannelFutureListener() {
////                @Override
////                public void operationComplete(ChannelFuture channelFuture) throws Exception {
////                    if(channelFuture.isSuccess()){
////                        startSuc.countDown();
////                    }
////                }
////            });
//            System.out.println("Netty启动");
//            future.channel().closeFuture().sync();
//    }catch (Exception e){
//        }
//    }

    private Map<String,List<String>> serviceMap = new HashMap<>();
    Selector selector;
    boolean flag = true;
    List<SocketChannel> connectedChannel;
    Map<String,MetricModel> result = new HashMap<>();

    public void getAllServerIP(){
        ZkClient zkClient = new ZkClient(Constants.ZK_IP,Constants.ZK_CONNECT_TIMEOUT);
        List<String> children = zkClient.getChildren(Constants.zk_root_path);
        for(String service:children){
            List<String> ips = zkClient.getChildren(Constants.zk_root_path+"/"+service);
            serviceMap.put(service,ips);
        }
    }

    public void connectToAllServer() throws Exception{
        List<String> ipList = serviceMap.entrySet().stream().flatMap(k->k.getValue().stream()).collect(Collectors.toList());
        selector = Selector.open();
        for(String host:ipList) {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            String ip = host.split(":")[0];
            int port = Integer.valueOf(host.split(":")[1]);
            if(socketChannel.connect(new InetSocketAddress(ip,port))){
                connectedChannel.add(socketChannel);
                socketChannel.register(selector,SelectionKey.OP_READ,ip);
                doWrite(socketChannel);
            }else{
                socketChannel.register(selector,SelectionKey.OP_CONNECT);
            }

            while(!flag){
                try{
                    selector.select(1000);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();
                    SelectionKey key = null;
                    while(it.hasNext()){
                        key = it.next();
                        it.remove();
                        try{
                            handleInput(key);
                        }catch (Exception e){

                        }
                    }
                }catch (Exception e){

                }
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            SocketChannel sc = (SocketChannel) key.channel();
            if(key.isConnectable()){
                if(sc.finishConnect()){
                    sc.register(selector,SelectionKey.OP_READ);
                    doWrite(sc);
                }else{
                    System.exit(1);
                }
            }

            if(key.isReadable()){
                ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
                int readBytes = sc.read(readBuffer);
                if(readBytes > 0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    try {
                        RedMessage redMessage = (RedMessage) objectInputStream.readObject();
                        if(redMessage.getMessageType() == MessageType.INFO){
                            String ip = key.attachment().toString();
                            result.put(ip,(MetricModel) redMessage.getMessage());
                        }
                    }catch (Exception e){

                    }
                }else if(readBytes < 0){
                    key.cancel();
                    sc.close();
                }else
                    ;
            }
        }
    }
    // write 应该每次刷新页面时调用
    // ToDo：连接成功到写之间连接断开， 需要抛出异常，由上级循环处理
    private void doWrite(SocketChannel socketChannel){
        try {
            RedMessage redMessage = new RedMessage(null, MessageType.METRIC);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(redMessage);
            byte[] msg = bos.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocateDirect(msg.length);
            buffer.put(msg);
            buffer.flip();
            socketChannel.write(buffer);
        }catch (Exception e){

        }
        //byteArrayOutputStream.w
    }

    public void refresh(){
        connectedChannel.forEach(channel->doWrite(channel));
    }
}
