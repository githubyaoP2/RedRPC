package com.red.api.cluster.loadBalance;

import com.red.api.rpc.Referer;
import com.red.api.rpc.Request;

import java.util.List;

public class RoundRobinLoadBalance extends LoadBalance{

    @Override
    public Referer select(Request request) {
        return null;
    }

    @Override
    public void selectToHolder(Request request, List<Referer> refersHolder) {

    }
}
