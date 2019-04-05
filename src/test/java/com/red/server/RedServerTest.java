package com.red.server;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.IdleStateHandler;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class RedServerTest {

    @Test
    public void delete(){
        ZkClient zkClient = new ZkClient("localhost:2181",5000);
        zkClient.addAuthInfo("digest","foo:true".getBytes());
        zkClient.deleteRecursive("/brokers/topics");
        IdleStateHandler idleStateHandler;
        //ChannelOption.ALLOCATOR;
    }
    @Test
    public void parseServicesTest() throws Exception{
        RedServer.startUp();
        System.in.read();
    }
}
