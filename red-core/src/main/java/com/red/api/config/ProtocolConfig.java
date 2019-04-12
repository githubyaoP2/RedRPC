package com.red.api.config;

import com.red.api.cluster.ha.HaStrategy;
import com.red.api.cluster.loadBalance.LoadBalance;

public class ProtocolConfig extends AbstractConfig{

    //协议名
    private String name;

    //序列化方式
    private String serialization;

    //编码
    private String codec;

    //io线程池大小
    private Integer iothreads;

    //client连接数
    private Integer clientConnections;

    //业务线程池大小
    private Integer workthreads;

    //请求包长度限制
    private Integer contentLength;

    //支持最大连接数
    private Integer maxConnections;

    private LoadBalance loadBalance;

    private HaStrategy haStrategy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public Integer getIothreads() {
        return iothreads;
    }

    public void setIothreads(Integer iothreads) {
        this.iothreads = iothreads;
    }

    public Integer getClientConnections() {
        return clientConnections;
    }

    public void setClientConnections(Integer clientConnections) {
        this.clientConnections = clientConnections;
    }

    public Integer getWorkthreads() {
        return workthreads;
    }

    public void setWorkthreads(Integer workthreads) {
        this.workthreads = workthreads;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public void setContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    public LoadBalance getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public HaStrategy getHaStrategy() {
        return haStrategy;
    }

    public void setHaStrategy(HaStrategy haStrategy) {
        this.haStrategy = haStrategy;
    }
}
