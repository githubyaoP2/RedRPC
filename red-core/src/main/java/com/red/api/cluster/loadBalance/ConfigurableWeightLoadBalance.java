package com.red.api.cluster.loadBalance;

import com.red.api.rpc.Referer;
import com.red.api.rpc.Request;

import java.util.List;

public class ConfigurableWeightLoadBalance extends LoadBalance{
    @Override
    Referer select(Request request) {
        return null;
    }

    @Override
    void selectToHolder(Request request, List<Referer> refersHolder) {

    }
}
