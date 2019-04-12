package com.red.api.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceConfig<T> extends InterfaceConfig{

    //服务端可以同时以多种协议暴露，客户端只能用一种来访问
    protected List<ProtocolConfig> protocolConfigList;

    private Class<T> interfaceClass;

    private Class<T> implClass;

    private List<MethodConfig> methodConfigList;

    //protocol1:port1,protocol2:port2
    private Map<String,Integer> export = new HashMap<String, Integer>();

    public Integer getPort(String protocol){
        return export.get(protocol);
    }

    public void addProtocol(String protocol,Integer port){
        export.put(protocol,port);
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Class<T> getImplClass() {
        return implClass;
    }

    public void setImplClass(Class<T> implClass) {
        this.implClass = implClass;
    }

    public List<MethodConfig> getMethodConfigList() {
        return methodConfigList;
    }

    public void setMethodConfigList(List<MethodConfig> methodConfigList) {
        this.methodConfigList = methodConfigList;
    }

    public List<ProtocolConfig> getProtocolConfigList() {
        return protocolConfigList;
    }

    public void setProtocolConfigList(List<ProtocolConfig> protocolConfigList) {
        this.protocolConfigList = protocolConfigList;
    }

    public Map<String, Integer> getExport() {
        return export;
    }

    public void setExport(Map<String, Integer> export) {
        this.export = export;
    }
}
