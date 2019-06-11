package com.red.api.cluster.loadBalance;

import com.red.api.rpc.Referer;
import com.red.api.message.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 负载均衡恶略 --
 * 一致性hash负载均衡
 */
public class ConsistentHashLoadBalance extends LoadBalance{

    private static int loop = 1000;
    private List<Referer> consistentHashReferers;

    @Override
    public void onRefresh(List<Referer> referers) {
        super.onRefresh(referers);
        List<Referer> copyReferers = new ArrayList<>(referers);
        List<Referer> tmpReferers = new ArrayList<>();//虚拟节点
        for(int i=0; i<loop; i++){
            Collections.shuffle(copyReferers);//随机排序
            for(Referer ref:copyReferers){
                tmpReferers.add(ref);
            }
        }
        consistentHashReferers = tmpReferers;
    }

    @Override
    public Referer select(Request request) {
        int hash = getHash(request);
        for(int i=0; i<referers.size(); i++){
            Referer ref = consistentHashReferers.get((hash+i)%consistentHashReferers.size());
            if(ref.isAvailable()){
                return ref;
            }
        }
        return null;
    }

    @Override
    public void selectToHolder(Request request, List<Referer> refersHolder) {
        List<Referer> referers = this.referers;

        int hash = getHash(request);
        for(int i=0; i<referers.size(); i++){
            Referer ref = consistentHashReferers.get((hash+i)%consistentHashReferers.size());
            if(ref.isAvailable()){
                refersHolder.add(ref);
            }

        }    }

    private int getHash(Request request){
        int hashCode;
        if(request.getArguments() == null || request.getArguments().length == 0){
            hashCode = request.hashCode();
        }else {
            hashCode = Arrays.hashCode(request.getArguments());
        }
        return 0x7fffffff & hashCode;
    }
}
