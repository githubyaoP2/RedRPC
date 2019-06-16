package com.red.api.cluster.loadBalance;

import com.red.api.exception.RedServiceException;
import com.red.api.message.RequestMessage;
import com.red.api.rpc.Referer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 负载均衡策略--
 *
 * 随机选择
 */
public class RandomLoadBalance extends LoadBalance{


    @Override
    public Referer select(RequestMessage requestMessage) {
        List<Referer> referers = this.referers;
        if(referers == null){
            throw new RedServiceException("No available referers for call requestMessage");
        }
        Referer referer = null;
        if(referers.size() == 1){
            return referers.get(0).isAvailable()? referers.get(0) : null;
        }else if(referers.size() > 1){
            int idx = (int)(ThreadLocalRandom.current().nextDouble()*referers.size());
            for(int i=0; i<referers.size(); i++){
                referer = referers.get((i+idx) % referers.size());
            }
        }
        return referer;
    }

    @Override
    public void selectToHolder(RequestMessage requestMessage, List<Referer> refersHolder) {
        List<Referer> referers = this.referers;
        if(referers == null){
            throw new RedServiceException("No available referers for call requestMessage");
        }

        if(referers.size() == 1 && referers.get(0).isAvailable()){
            refersHolder.add(referers.get(0));
        }else if(referers.size() > 1){
            int idx = (int)(ThreadLocalRandom.current().nextDouble()*referers.size());
            for(int i=0; i<referers.size(); i++){
                Referer referer = referers.get((i+idx) % referers.size());
                if(referer.isAvailable()){
                    refersHolder.add(referer);
                }
            }
        }
    }
}
