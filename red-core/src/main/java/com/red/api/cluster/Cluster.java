package com.red.api.cluster;

import com.red.api.cluster.ha.HaStrategy;
import com.red.api.cluster.loadBalance.LoadBalance;
import com.red.api.rpc.Referer;
import com.red.api.rpc.Request;
import com.red.api.rpc.Response;

import java.util.List;

public class Cluster {
    HaStrategy haStrategy;
    LoadBalance loadBalance;
    List<Referer> refererList;

    public Response call(Request request){
       return haStrategy.call(request,loadBalance);
    }

    //刷新集群
    public void refreshReferers(List<Referer> refererList){
        this.refererList = refererList;
    }

    public HaStrategy getHaStrategy() {
        return haStrategy;
    }

    public void setHaStrategy(HaStrategy haStrategy) {
        this.haStrategy = haStrategy;
    }

    public LoadBalance getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public List<Referer> getRefererList() {
        return refererList;
    }

    public void setRefererList(List<Referer> refererList) {
        this.refererList = refererList;
    }

    public void addReferer(Referer referer){
        refererList.add(referer);
    }
}
