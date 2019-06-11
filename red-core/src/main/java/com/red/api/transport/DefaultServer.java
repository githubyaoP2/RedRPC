package com.red.api.transport;

import com.red.api.config.ServiceConfig;
import com.red.api.rpc.Exporter;
import com.red.api.util.LoggerUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultServer implements Server{

    private static Map<ServiceConfig,AtomicBoolean> isExport = new ConcurrentHashMap<ServiceConfig, AtomicBoolean>();
    private ServiceConfig serverConfig;

    public DefaultServer(ServiceConfig serviceConfig){
        this.serverConfig = serviceConfig;
    }

    public void export(){
        if(isExport.get(serverConfig).get()){
            LoggerUtil.warn("此服务已发布");
        }
        //创建Provider
        Provider provider = new Provider(serverConfig.getImplClass(),serverConfig.getMethodConfigList());
        //创建Exporter
        Exporter exporter = new Exporter(provider,serverConfig);
        exporter.export();
    }

}
