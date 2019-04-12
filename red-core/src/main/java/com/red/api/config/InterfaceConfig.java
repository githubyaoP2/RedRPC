package com.red.api.config;

import java.util.List;

public class InterfaceConfig extends AbstractConfig{



    //注册中心列表
    protected List<RegistryConfig> registryConfigList;

    //分组,客户端可陪多个分组，服务端只能以一个分组发布
    protected String group;

    //版本
    protected String version;

    //过滤器
    protected String filter;

    //最大并发调用
    protected Integer actives;

    //是否异步
    protected Boolean async;

    //是否共享channel
    protected Boolean shareChannel;

    //请求超时时间
    protected Integer requestTimeOut;

    //重试次数
    protected Integer retries;

    //是否压缩
    protected Boolean usezip;

    //压缩最小阀值
    protected Integer minSize;

    public List<RegistryConfig> getRegistryConfigList() {
        return registryConfigList;
    }

    public void setRegistryConfigList(List<RegistryConfig> registryConfigList) {
        this.registryConfigList = registryConfigList;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getActives() {
        return actives;
    }

    public void setActives(Integer actives) {
        this.actives = actives;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Boolean getShareChannel() {
        return shareChannel;
    }

    public void setShareChannel(Boolean shareChannel) {
        this.shareChannel = shareChannel;
    }

    public Integer getRequestTimeOut() {
        return requestTimeOut;
    }

    public void setRequestTimeOut(Integer requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Boolean getUsezip() {
        return usezip;
    }

    public void setUsezip(Boolean usezip) {
        this.usezip = usezip;
    }

    public Integer getMinSize() {
        return minSize;
    }

    public void setMinSize(Integer minSize) {
        this.minSize = minSize;
    }
}
