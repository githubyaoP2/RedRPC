package com.red.api.config;

public class MethodConfig extends AbstractConfig{

    //方法名
    private String name;

    //超时时间
    private Integer requestTimeout;

    //失败重试次数
    private Integer retries;

    //最大并发调用
    private Integer actives;

    //参数类型
    private Class[] argumentTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getActives() {
        return actives;
    }

    public void setActives(Integer actives) {
        this.actives = actives;
    }

    public Class[] getArgumentTypes() {
        return argumentTypes;
    }

    public void setArgumentTypes(Class[] argumentTypes) {
        this.argumentTypes = argumentTypes;
    }
}
