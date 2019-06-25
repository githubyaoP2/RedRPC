package com.red.api.proxy;

import com.red.api.cluster.Cluster;
import com.red.api.cluster.ha.HaStrategy;
import com.red.api.config.ClientConfig;
import com.red.api.message.RequestMessage;
import com.red.api.message.ResponseFuture;
import com.red.api.rpc.RedVersion;
import com.red.api.transport.DefaultClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ClientInvocationHandler implements InvocationHandler {

    Cluster cluster;
    ClientConfig clientConfig;


    public ClientInvocationHandler(Cluster cluster,ClientConfig clientConfig) {
        this.cluster = cluster;
        this.clientConfig = clientConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setRedVersion(RedVersion.VERSION1);
        requestMessage.setIntefaceName(clientConfig.getInterfaceClass().getName());
        requestMessage.setArguments(args);
        requestMessage.setImplCode(clientConfig.getImplClass());
        requestMessage.setMethodName(method.getName());
        requestMessage.setRetries(clientConfig.getRetries());
        Long requetId = DefaultClient.getRequestId();
        requestMessage.setRequestId(requetId);
        HaStrategy haStrategy = cluster.getHaStrategy();
        ResponseFuture responseFuture = haStrategy.call(requestMessage,cluster.getLoadBalance());
        if(clientConfig.getAsync()){
            return responseFuture;
        }else {
            return responseFuture.get();
        }
    }
}
