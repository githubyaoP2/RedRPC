package com.red.demo;

import com.red.api.cluster.ha.FailFastHaStrategy;
import com.red.api.cluster.loadBalance.ConsistentHashLoadBalance;
import com.red.api.config.ClientConfig;
import com.red.api.config.ProtocolConfig;
import com.red.api.config.RegistryConfig;
import com.red.api.transport.DefaultClient;

import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    public static void main(String[] args) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setHaStrategy("FailFast");
        clientConfig.setLoadBalance("ConsistentHash");
        clientConfig.setImplClass("HelloServiceImpl");
        clientConfig.setGroup("group1");
        clientConfig.setRetries(3);
        clientConfig.setAsync(false);
        clientConfig.setInterfaceClass(HelloService.class);
        List<RegistryConfig> registryConfigList = new ArrayList<>();
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("127.0.0.1");
        registryConfig.setName("ZooKeeper");
        registryConfig.setConnectTimeOut(1000);
        registryConfig.setRegistrySessionTimeout(1000);
        registryConfig.setRequestTimeOut(1000);
        registryConfigList.add(registryConfig);
        clientConfig.setRegistryConfigList(registryConfigList);
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("RedRPC");
        protocolConfig.setSerialization("jdk");
        protocolConfig.setMaxConnections(30);
        protocolConfig.setIothreads(8);
        protocolConfig.setCodec("red");
//        protocolConfig.setLoadBalance(new ConsistentHashLoadBalance());
//        protocolConfig.setHaStrategy(new FailFastHaStrategy());
        protocolConfig.setClientConnections(2);
        clientConfig.setProtocolConfig(protocolConfig);
        DefaultClient<HelloService> client = new DefaultClient(clientConfig);
        HelloService helloService = client.getRef();
        helloService.hello("李",12);
    }

}
