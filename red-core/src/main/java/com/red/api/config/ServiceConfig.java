package com.red.api.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceConfig<T> extends InterfaceConfig{

    //服务端可以同时以多种协议暴露，客户端只能用一种来访问
    protected ProtocolConfig protocolConfig;

    private Class<T> interfaceClass;

    private T implClass;

    private List<MethodConfig> methodConfigList;

    //protocol1:port1,protocol2:port2
    private String port;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public T getImplClass() {
        return implClass;
    }

    public void setImplClass(T implClass) {
        this.implClass = implClass;
    }

    public List<MethodConfig> getMethodConfigList() {
        return methodConfigList;
    }

    public void setMethodConfigList(List<MethodConfig> methodConfigList) {
        this.methodConfigList = methodConfigList;
    }

    public ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }

    public void setProtocolConfig(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
