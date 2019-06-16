package com.red.api.cluster.loadBalance;

import com.red.api.rpc.Referer;
import com.red.api.message.RequestMessage;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负载均衡策略--
 * 循环选择
 */
public class RoundRobinLoadBalance extends LoadBalance{

    public static final int MAX_REFERER_COUNT = 10;
    private AtomicInteger idx = new AtomicInteger(0);

    @Override
    public Referer select(RequestMessage requestMessage) {
        List<Referer> referers = this.referers;

        int index = idx.incrementAndGet();
        for(int i=0; i<referers.size(); i++){
            Referer referer = referers.get((i+index)%referers.size());
            if(referer.isAvailable()){
                return referer;
            }
        }
        return null;
    }

    @Override
    public void selectToHolder(RequestMessage requestMessage, List<Referer> refersHolder) {
        List<Referer> referers = this.referers;
        int index = idx.incrementAndGet();
        for(int i = 0,count = 0; i<referers.size() && count<MAX_REFERER_COUNT; i++){
            Referer referer = referers.get((i+index)%referers.size());
            if(referer.isAvailable()){
                refersHolder.add(referer);
                count++;
            }
        }
    }
}
