package com.red.demo;

import com.red.api.cluster.ha.FailFastHaStrategy;
import com.red.api.cluster.loadBalance.ConsistentHashLoadBalance;
import com.red.api.config.MethodConfig;
import com.red.api.config.ProtocolConfig;
import com.red.api.config.RegistryConfig;
import com.red.api.config.ServiceConfig;
import com.red.api.transport.DefaultServer;

import java.util.ArrayList;
import java.util.List;

public class ServiceTest {
    public static void main(String[] args) {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setInterfaceClass(HelloService.class);
        serviceConfig.setImplClass(new HelloServiceImpl());
        List<MethodConfig> methodConfigList = new ArrayList<>();
        serviceConfig.setMethodConfigList(methodConfigList);//
        List<RegistryConfig> registryConfigList = new ArrayList<>();
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("127.0.0.1");
        registryConfig.setName("ZooKeeper");
        registryConfig.setConnectTimeOut(1000);
        registryConfig.setRegistrySessionTimeout(1000);
        registryConfig.setRequestTimeOut(1000);
        registryConfigList.add(registryConfig);
        serviceConfig.setRegistryConfigList(registryConfigList);
        serviceConfig.setPort("8080");
        serviceConfig.setActives(1000);//
        serviceConfig.setAsync(true);
//        serviceConfig.setFilter();
        serviceConfig.setGroup("group1");
        serviceConfig.setRetries(10);
//        serviceConfig.setUsezip(false);
//        serviceConfig.setMinSize(1);
        serviceConfig.setRequestTimeOut(10);
//        serviceConfig.setShareChannel(true);
//        serviceConfig.setVersion("");
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setCodec("Red");
        protocolConfig.setContentLength(1024);//报文最大长度
        protocolConfig.setHaStrategy(new FailFastHaStrategy());
        protocolConfig.setLoadBalance(new ConsistentHashLoadBalance());
        protocolConfig.setIothreads(10);
        protocolConfig.setMaxConnections(100);
        protocolConfig.setSerialization("JDK");
        protocolConfig.setName("ZooKeeper");
//        protocolConfig.setClientConnections();//
        serviceConfig.setProtocolConfig(protocolConfig);
        DefaultServer defaultServer = new DefaultServer();
        defaultServer.publish(serviceConfig);
    }
}
