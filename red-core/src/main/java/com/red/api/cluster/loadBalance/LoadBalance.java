package com.red.api.cluster.loadBalance;

import com.red.api.rpc.Referer;
import com.red.api.rpc.Request;

import java.util.List;

public interface LoadBalance {
    void onRefresh(List<Referer> referers);

    Referer select(Request request);

    void selectToHolder(Request request, List<Referer> refersHolder);

}
