package com.red.api.cluster.loadBalance;

import com.red.api.rpc.Referer;
import com.red.api.rpc.Request;

import java.util.List;

public class ConsistentHashLoadBalance extends LoadBalance{



    @Override
    public void onRefresh(List<Referer> referers) {

    }

    @Override
    public Referer select(Request request) {
        return null;
    }

    @Override
    public void selectToHolder(Request request, List<Referer> refersHolder) {

    }
}
