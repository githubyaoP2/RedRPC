package com.red.api.cluster.loadBalance;

import com.red.api.exception.RedServiceException;
import com.red.api.rpc.Referer;
import com.red.api.message.Request;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 负载均衡策略 --
 * 选取目前调用量最少的Referer
 */
public class ActiveWeightLoadBalance extends LoadBalance{

    /*
        如果referer list很大，每次从所有的当中选取最低并发会有性能损耗，所以获取指定size的isAvailable的referer
     */
    private static final int count = 10;

    @Override
    public Referer select(Request request) {
        int start = ThreadLocalRandom.current().nextInt(referers.size());
        int limit = 0;
        int current = 0;

        Referer referer = null;

        while(limit<count && current<referers.size()){
            Referer tmp = referers.get((start+current)%referers.size());
            current ++;

            if(!tmp.isAvailable()){
                continue;
            }

            limit++;

            if(referer == null){
                referer = tmp;
            }else if(tmp.activeRefererCount() < referer.activeRefererCount()){
                referer = tmp;
            }
        }
        return referer;
    }

    @Override
    public void selectToHolder(Request request, List<Referer> refersHolder) {
        List<Referer> referers = this.referers;

        if(referers == null || referers.isEmpty()){
            throw new RedServiceException("No available referers for call request");
        }

        int start = ThreadLocalRandom.current().nextInt(referers.size());
        int limit = 0;
        int current = 0;

        while (limit<count && current<referers.size()){
            Referer tmp = referers.get((start+current)%referers.size());
            current ++;

            if(!tmp.isAvailable()){
                continue;
            }

            limit++;

            refersHolder.add(tmp);
        }

        Collections.sort(refersHolder,new LowActivePriorityComparator());
    }

    static class LowActivePriorityComparator implements Comparator<Referer>{
        @Override
        public int compare(Referer o1, Referer o2) {
            return o1.activeRefererCount() - o2.activeRefererCount();
        }
    }
}
