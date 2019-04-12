package com.red.api.rpc;

import com.red.api.config.MethodConfig;
import com.red.api.util.LoggerUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Provider<T> {

    public Provider(Class implClass, List<MethodConfig> methodConfigList){
        this.implClass = implClass;
        for(MethodConfig mc:methodConfigList){
            try {
                Method m = implClass.getMethod(mc.getName(), mc.getArgumentTypes());
                if(m != null){
                    methodMap.put(mc.getName(),m);
                }
            }catch (NoSuchMethodException e){
                LoggerUtil.info("");
            }
        }
    }

    private Class<T> implClass;

    private Map<String,Method> methodMap = new HashMap<String, Method>();

    private Method lookupMethod(String methodName){
        return methodMap.get(methodName);
    }
}
