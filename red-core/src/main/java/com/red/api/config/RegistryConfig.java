package com.red.api.config;

public class RegistryConfig extends AbstractConfig{

    //注册配置名称
    private String name;

    //注册协议
    private String regProtocol;

    //注册中心地址
    private String address;

    //注册中心请求超时时间（毫秒）
    private Integer requestTimeOut;

    //注册中心连接超时时间（毫秒）
    private Integer connectTimeOut;

    // 注册中心会话超时时间(毫秒)
    private Integer registrySessionTimeout;

    // 失败后重试的时间间隔
    private Integer registryRetryPeriod;

    // 启动时检查注册中心是否存在
    private String check;

    // 在该注册中心上服务是否暴露
    private Boolean register;

    // 在该注册中心上服务是否引用
    private Boolean subscribe;

    private Boolean isDefault;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegProtocol() {
        return regProtocol;
    }

    public void setRegProtocol(String regProtocol) {
        this.regProtocol = regProtocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRequestTimeOut() {
        return requestTimeOut;
    }

    public void setRequestTimeOut(Integer requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    public Integer getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(Integer connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public Integer getRegistrySessionTimeout() {
        return registrySessionTimeout;
    }

    public void setRegistrySessionTimeout(Integer registrySessionTimeout) {
        this.registrySessionTimeout = registrySessionTimeout;
    }

    public Integer getRegistryRetryPeriod() {
        return registryRetryPeriod;
    }

    public void setRegistryRetryPeriod(Integer registryRetryPeriod) {
        this.registryRetryPeriod = registryRetryPeriod;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
