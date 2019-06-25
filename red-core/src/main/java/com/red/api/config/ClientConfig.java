package com.red.api.config;

import java.util.List;

public class ClientConfig<T> extends InterfaceConfig{

    //服务端可以同时以多种协议暴露，客户端只能用一种来访问
    protected ProtocolConfig protocolConfig;

    private Class<T> interfaceClass;//调用的接口

    private String haStrategy;//高可用策略

    private String loadBalance;//负载均衡策略

    private String implClass;

    public ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }

    public void setProtocolConfig(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getHaStrategy() {
        return haStrategy;
    }

    public void setHaStrategy(String haStrategy) {
        this.haStrategy = haStrategy;
    }

    public String getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }

    public String getImplClass() {
        return implClass;
    }

    public void setImplClass(String implClass) {
        this.implClass = implClass;
    }
}
