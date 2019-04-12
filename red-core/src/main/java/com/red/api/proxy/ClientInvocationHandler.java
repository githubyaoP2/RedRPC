package com.red.api.proxy;

import com.red.api.cluster.Cluster;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ClientInvocationHandler implements InvocationHandler {

    Cluster cluster;

    public ClientInvocationHandler(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
