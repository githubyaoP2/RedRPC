package com.red.api.cluster.loadBalance;

import com.red.api.message.RequestMessage;
import com.red.api.rpc.Referer;

import java.util.List;

public abstract class LoadBalance {
    public List<Referer> referers;

    public void onRefresh(List<Referer> referers) {
        this.referers = referers;
    }

    abstract Referer select(RequestMessage requestMessage);

    abstract void selectToHolder(RequestMessage requestMessage, List<Referer> refersHolder);

}
