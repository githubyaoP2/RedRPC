package com.red.api.transport;

import com.red.api.config.ServiceConfig;
import com.red.api.rpc.Exporter;
import com.red.api.rpc.Provider;
import com.red.api.util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultServer implements Server{

    private static Map<ServiceConfig,AtomicBoolean> isExport = new ConcurrentHashMap<ServiceConfig, AtomicBoolean>();
    private List<ServiceConfig> serverConfigs = new ArrayList<ServiceConfig>();

    public void export(){
        for(ServiceConfig serverConfig:serverConfigs){
            if(isExport.get(serverConfig).get()){
                LoggerUtil.info("ee");
            }

            //创建Provider
            Provider provider = new Provider(serverConfig.getImplClass(),serverConfig.getMethodConfigList());
            //创建Exporter
            Exporter exporter = new Exporter(provider,serverConfig);
            exporter.export();
        }

    }

    public void setServerConfigs(List<ServiceConfig> serverConfigs) {
        this.serverConfigs = serverConfigs;
    }
}
