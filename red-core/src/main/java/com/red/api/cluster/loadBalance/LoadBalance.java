package com.red.api.cluster.loadBalance;

import com.red.api.rpc.Referer;
import com.red.api.message.Request;

import java.util.List;

public abstract class LoadBalance {
    public List<Referer> referers;

    public void onRefresh(List<Referer> referers) {
        this.referers = referers;
    }

    abstract Referer select(Request request);

    abstract void selectToHolder(Request request, List<Referer> refersHolder);

}
