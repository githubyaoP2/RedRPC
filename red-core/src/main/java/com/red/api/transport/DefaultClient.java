package com.red.api.transport;

import com.red.api.cluster.Cluster;
import com.red.api.config.ClientConfig;
import com.red.api.config.ProtocolConfig;
import com.red.api.proxy.ClientInvocationHandler;
import com.red.api.rpc.Referer;
import com.red.api.util.Constants;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultClient<T> {

    private AtomicBoolean inited = new AtomicBoolean(false);
    private ClientConfig<T> clientConfig;
    //虽然保留多个集群，但每次访问只能选择一个集群访问
    private Map<ProtocolConfig,Cluster> clusterMap;
    //Bootstrap bootstrap;
    public DefaultClient(ClientConfig clientConfig){
        this.clientConfig = clientConfig;
        //初始化集群
        ProtocolConfig protocolConfig = clientConfig.getProtocolConfig();
        Cluster cluster = new Cluster();
        String group = clientConfig.getGroup();
        String interfaceName = clientConfig.getInterfaceClass().getName();
        List<String> urls = discoverCommand(group,interfaceName);
        if(urls.isEmpty()){
            urls = discoverService(group,interfaceName);
        }
        for(String url:urls){
            Referer referer = new Referer();
            referer.init();
            cluster.addReferer(referer);
        }
        register();
    }

    public T getRef(String protocolName) {
        return (T)Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{clientConfig.getInterfaceClass()},new ClientInvocationHandler(clusterMap.get(protocolName)));
    }

    public List<String> discoverCommand(String group,String interfaceName){
        return null;
    }

    public List<String> discoverService(String group,String interfaceName){
        return null;
    }

    public void register(){

    }

}
