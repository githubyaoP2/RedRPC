package com.red.api.transport;

import com.red.api.cluster.Cluster;
import com.red.api.config.ClientConfig;
import com.red.api.config.ProtocolConfig;
import com.red.api.config.RegistryConfig;
import com.red.api.proxy.ClientInvocationHandler;
import com.red.api.registry.Registry;
import com.red.api.rpc.RedReferer;
import com.red.api.rpc.Referer;
import com.red.api.util.Constants;
import org.I0Itec.zkclient.ZkClient;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultClient<T> implements Client<T>{

    private AtomicBoolean inited = new AtomicBoolean(false);
    private ClientConfig<T> clientConfig;
    //虽然保留多个集群，但每次访问只能选择一个集群访问
    private Cluster cluster;
    //Bootstrap bootstrap;
    public DefaultClient(ClientConfig clientConfig){
        this.clientConfig = clientConfig;
        //初始化集群
        ProtocolConfig protocolConfig = clientConfig.getProtocolConfig();
        cluster = new Cluster(protocolConfig.getHaStrategy(),protocolConfig.getLoadBalance());
        String group = clientConfig.getGroup();
        String interfaceName = clientConfig.getInterfaceClass().getName();
        List<String> urls = discoverCommand(group,interfaceName);
        if(urls.isEmpty()){
            urls = discoverService(group,interfaceName);
        }
        for(String url:urls){
            Referer referer = new RedReferer(url,protocolConfig);
            referer.init();
            cluster.addReferer(referer);
        }
        register();
    }

    @Override
    public T getRef(String protocolName) {
        return (T)Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{clientConfig.getInterfaceClass()},new ClientInvocationHandler(cluster));
    }

    /**
     * 从配置命令节点找到对应的分组权重配置
     * @param group
     * @param interfaceName
     * @return
     */
    public List<String> discoverCommand(String group,String interfaceName){
        String url = Constants.project+Constants.separator+group+Constants.separator+interfaceName+Constants.separator+Constants.command;
        List<RegistryConfig> registryConfigList = clientConfig.getRegistryConfigList();
        for(RegistryConfig registryConfig:registryConfigList){
            switch (registryConfig.getRegProtocol()){
                case "zookeeper":
                    String zkAddr = registryConfig.getAddress();
                    int sessionTimeOut = registryConfig.getRegistrySessionTimeout();
                    int connectTimeOut = registryConfig.getConnectTimeOut();
                    //序列化器随后添加
                    ZkClient zkClient = new ZkClient(zkAddr,sessionTimeOut,connectTimeOut);
                    String command = zkClient.readData(url);
                    return parseCommand(command);
                case "consul":
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * 解析权重配置策略
     * @param command
     * @return
     */
    private List<String> parseCommand(String command){
        String[] commands = command.split(Constants.spliter);
        //commands[0]
        return null;
    }

    //服务发现
    public List<String> discoverService(String group,String interfaceName){
        String url = Constants.project+Constants.separator+group+Constants.separator+interfaceName+Constants.separator+Constants.server+Constants.separator;
        List<RegistryConfig> registryConfigList = clientConfig.getRegistryConfigList();
        for(RegistryConfig registryConfig:registryConfigList){
            switch (registryConfig.getRegProtocol()){
                case "zookeeper":
                    String zkAddr = registryConfig.getAddress();
                    int sessionTimeOut = registryConfig.getRegistrySessionTimeout();
                    int connectTimeOut = registryConfig.getConnectTimeOut();
                    //序列化器随后添加
                    ZkClient zkClient = new ZkClient(zkAddr,sessionTimeOut,connectTimeOut);
                    List<String> urls = zkClient.getChildren(url);
                    return urls;
                case "consul":
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    public void register(){

    }

}
