package com.red.api.cluster.loadBalance;

import com.red.api.rpc.Referer;
import com.red.api.rpc.Request;
import io.netty.util.internal.StringUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这里配置的权重是针对组的权重，组内是轮流的
 */
public class ConfigurableWeightLoadBalance extends LoadBalance{

    private static final  RefererListCacheHolder emptyHolder = new EmptyHolder();

    private volatile RefererListCacheHolder holder = emptyHolder;

    private String weightString;

    @Override
    public void onRefresh(List<Referer> referers) {
        super.onRefresh(referers);
        if(referers.isEmpty()){
            holder = emptyHolder;
        }else if(StringUtil.isNullOrEmpty(weightString)){
            holder = new SingleGroupHolder(referers);
        }else {
            holder = new MultiGroupHolder(weightString,referers);
        }
    }

    @Override
    Referer select(Request request) {
        if(holder == emptyHolder)
            return null;
        RefererListCacheHolder h = this.holder;
        Referer r = h.next();
        if(!r.isAvailable()){
            int retryTimes = this.referers.size() -1;
            for(int i=0; i<retryTimes; i++){
                r = h.next();
                if(r.isAvailable()){
                    break;
                }
            }
        }
        if(r.isAvailable()){
            return r;
        }
        return null;
    }

    @Override
    void selectToHolder(Request request, List<Referer> refersHolder) {

    }

    static abstract class RefererListCacheHolder{
        abstract Referer next();
    }

    static class EmptyHolder extends RefererListCacheHolder{
        @Override
        Referer next() {
            return null;
        }
    }

    class SingleGroupHolder extends RefererListCacheHolder{

        private int size;
        private List<Referer> cache;

        public SingleGroupHolder(List<Referer> list) {
            cache = list;
            size = list.size();
        }

        @Override
        Referer next() {
            return cache.get(ThreadLocalRandom.current().nextInt(size));
        }
    }

    class MultiGroupHolder extends RefererListCacheHolder{

        private int randomKeySize = 0;
        private List<String> randomKeyList = new ArrayList<>();//按比重放置个数
        private Map<String,AtomicInteger> cursors = new HashMap<>();//保证单组内轮询
        private Map<String,List<Referer>> groupReferers = new HashMap<>();

        public MultiGroupHolder(String weights,List<Referer> list) {
            String[] groupAndWeights = weights.split(":");
            int[] weightsArr = new int[groupAndWeights.length];
            Map<String,Integer> weightsMap = new HashMap<>(groupAndWeights.length);
            int i=0;
            for(String groupAndWeight:groupAndWeights){
                String[] gw = groupAndWeight.split(":");
                if(gw.length == 2){
                    Integer w = Integer.valueOf(gw[1]);
                    weightsMap.put(gw[0],w);
                    groupReferers.put(gw[0],new ArrayList<Referer>());
                    weightsArr[i++] = w;
                }
            }

            int weightGcd = findGcd(weightsArr);
            if(weightGcd != 1){
                for(Map.Entry<String,Integer> entry: weightsMap.entrySet()) {
                    weightsMap.put(entry.getKey(),entry.getValue()/weightGcd);
                }
            }

            for(Map.Entry<String,Integer> entry : weightsMap.entrySet()){
                for(int j=0; j<entry.getValue(); j++){
                    randomKeyList.add(entry.getKey());
                }
            }
            Collections.shuffle(randomKeyList);
            randomKeySize = randomKeyList.size();

            for(String key:weightsMap.keySet()){
                cursors.put(key,new AtomicInteger(0));
            }

            for(Referer referer:list){
                groupReferers.get(referer.getGroup()).add(referer);
            }
        }

        @Override
        Referer next() {
            String group = randomKeyList.get(ThreadLocalRandom.current().nextInt(randomKeySize));
            AtomicInteger a = cursors.get(group);
            List<Referer> referers = groupReferers.get(group);
            return referers.get(a.getAndIncrement() % referers.size());
        }
        // 求最大公约数
        private int findGcd(int n, int m) {
            return (n == 0 || m == 0) ? n + m : findGcd(m, n % m);
        }

        // 求最大公约数
        private int findGcd(int[] arr) {
            int i = 0;
            for (; i < arr.length - 1; i++) {
                arr[i + 1] = findGcd(arr[i], arr[i + 1]);
            }
            return findGcd(arr[i], arr[i - 1]);
        }
    }


}
