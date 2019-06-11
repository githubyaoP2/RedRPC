package com.red.api.message;

import com.red.api.rpc.RedVersion;

import java.util.Map;

//请求报文
public class Request implements RedMessage{
    private String intefaceName;
    private String methodName;
    private Object[] arguments;
    private Map<String,String> attachments;
    private int retries = 0;
    private long requestId;

    //版本
    RedVersion redVersion;

    public String getIntefaceName() {
        return intefaceName;
    }

    public void setIntefaceName(String intefaceName) {
        this.intefaceName = intefaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public RedVersion getRedVersion() {
        return redVersion;
    }

    public void setRedVersion(RedVersion redVersion) {
        this.redVersion = redVersion;
    }
}
