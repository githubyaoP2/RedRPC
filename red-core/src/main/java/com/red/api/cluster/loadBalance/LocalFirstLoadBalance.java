package com.red.api.cluster.loadBalance;

import com.red.api.rpc.Referer;
import com.red.api.rpc.Request;
import com.red.api.util.LoggerUtil;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LocalFirstLoadBalance extends LoadBalance{
    public static final int MAX_REFERER_COUNT = 10;

    @Override
    public Referer select(Request request) {
        List<Referer> referers = this.referers;
        List<Referer> local = null;
        try {
            local = searchLocalReferer(referers, Inet4Address.getLocalHost().getHostAddress());
        }catch (UnknownHostException e){
        }

        if(!local.isEmpty()){
            referers = local;
        }

        Referer referer = null;
        for(int i=0; i<referers.size(); i++){
            Referer tmp = referers.get(i%referers.size());

            if(!tmp.isAvailable()){
                continue;
            }

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

        List<Referer> local = null;
        try {
            local = searchLocalReferer(referers, Inet4Address.getLocalHost().getHostAddress());
        }catch (UnknownHostException e){
        }

        if(!local.isEmpty()){
            Collections.sort(local,new LowActivePriorityComparator());
            refersHolder.addAll(local);
        }

        int start = ThreadLocalRandom.current().nextInt(referers.size());
        int limit = 0;
        int current = 0;

        List<Referer> remoteReferers = new ArrayList<>();
        while(limit<MAX_REFERER_COUNT && current<referers.size()){
            Referer tmp = referers.get((start+current)%referers.size());
            current++;

            if(!tmp.isAvailable() || local.contains(tmp)){
                continue;
            }

            limit++;

            remoteReferers.add(tmp);
        }

        Collections.sort(remoteReferers,new LowActivePriorityComparator());
        refersHolder.addAll(remoteReferers);
    }

    private List<Referer> searchLocalReferer(List<Referer> referers,String localhost){
        List<Referer> local = new ArrayList<>();
        long ip = ipToLong(localhost);
        for(Referer referer : referers){
            long tmp = ipToLong(referer.getUrl());
            if(ip != 0 && ip == tmp){
                local.add(referer);
            }
        }
        return local;
    }

    public static long ipToLong(final String addr) {
        final String[] addressBytes = addr.split("\\.");
        int length = addressBytes.length;
        if (length < 3) {
            return 0;
        }
        long ip = 0;
        try {
            for (int i = 0; i < 4; i++) {
                ip <<= 8;
                ip |= Integer.parseInt(addressBytes[i]);
            }
        } catch (Exception e) {

        }

        return ip;
    }

    static class LowActivePriorityComparator implements Comparator<Referer> {
        @Override
        public int compare(Referer referer1, Referer referer2) {
            return referer1.activeRefererCount() - referer2.activeRefererCount();
        }
    }
}
