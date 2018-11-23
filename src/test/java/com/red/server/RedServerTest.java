package com.red.server;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class RedServerTest {

    @Test
    public void delete(){
        ZkClient zkClient = new ZkClient("localhost:2181",5000);
        zkClient.addAuthInfo("digest","foo:true".getBytes());
        zkClient.deleteRecursive("/RedRPC");
    }
    @Test
    public void parseServicesTest() throws Exception{
        RedServer.startUp();
        System.in.read();
    }
}
