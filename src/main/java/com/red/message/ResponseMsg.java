package com.red.message;

import java.io.Serializable;

public class ResponseMsg implements Message,Serializable {
    String invokeSucess;
    String reason;
    Object result;

    public String isInvokeSucess() {
        return invokeSucess;
    }

    public void setInvokeSucess(String invokeSucess) {
        this.invokeSucess = invokeSucess;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
