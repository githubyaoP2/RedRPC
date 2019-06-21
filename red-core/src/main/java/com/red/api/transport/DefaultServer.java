package com.red.api.transport;

import com.red.api.config.MethodConfig;
import com.red.api.config.RegistryConfig;
import com.red.api.config.ServiceConfig;
import com.red.api.registry.Registry;
import com.red.api.rpc.Exporter;
import com.red.api.rpc.RedExporter;
import com.red.api.util.LoggerUtil;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ToDo:实现细节需要调整，是否多个服务共享Channel。如果是，那么服务发布与否的标识就是地址加端口，然后；否则应该加上组和实现类。
 */
public class DefaultServer implements Server{

    private static Map<String,Boolean> isServerInit = new ConcurrentHashMap<>();
    
    private static Map<ServiceConfig,Boolean> isExport = new ConcurrentHashMap<>();
    
    //以实现类、方法、参数为key的实现类，保存服务端已发布的服务
    public static Map<String,Object> implClassMap = new ConcurrentHashMap<>();

    

    @Override
    public void publish(ServiceConfig serviceConfig){
        if(isExport.get(serviceConfig)){
            LoggerUtil.warn("此服务已发布");
            return;
        }
        //如果此端口已绑定nio，则不需要再次初始化，即channel共享
        if(!isServerInit.get(generateAddress(serviceConfig))){
            Exporter exporter = new RedExporter(serviceConfig);
            exporter.export();
        }
        String implClassName = serviceConfig.getImplClass().getClass().getSimpleName();
        List<MethodConfig> methodConfigList = serviceConfig.getMethodConfigList();
        methodConfigList.forEach(methodConfig -> {
            StringBuilder sb = new StringBuilder(implClassName);
            sb.append(":");
            sb.append(methodConfig.getName());
            sb.append("(");
            Class[] params = methodConfig.getArgumentTypes();
            for (Class clazz:params){
                sb.append(clazz.getSimpleName());
            }
            sb.append(")");
            implClassMap.put(sb.toString(),serviceConfig.getImplClass());
        });

    }

    //IP一样，直接以端口区分
    private String generateAddress(ServiceConfig serviceConfig){
        return serviceConfig.getPort();
    }

    private void registerToZK(ServiceConfig serviceConfig){
        List<RegistryConfig> registryConfigList = serviceConfig.getRegistryConfigList();
        for (RegistryConfig registryConfig:registryConfigList){
            if("ZooKeeper".equals(registryConfig.getName())){
                String address = registryConfig.getAddress()+":2181";
                String path = address+"";
                ZkClient zkClient = new ZkClient(address,registryConfig.getRequestTimeOut(),registryConfig.getConnectTimeOut());
            }
        }
    }
}
