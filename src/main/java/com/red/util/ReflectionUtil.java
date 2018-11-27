package com.red.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectionUtil {

    public static Class[] getClassType(Object... args){
        Class[] var1 = new Class[args.length];
        List<Class> var2 =  Arrays.stream(args).map(Object::getClass).collect(Collectors.toList());
        return var2.toArray(var1);
    }

    public static boolean ifMethodExist(Map<String,Object> serviceMap, String instanceName, String methodName, Object...params){
        if(serviceMap != null && serviceMap.containsKey(instanceName)){
            try {
                Class clazz = Class.forName(instanceName);
                Method m = clazz.getMethod(methodName,getClassType(params));
                System.out.println(m.getName());
            }catch (ClassNotFoundException|NoSuchMethodException e){
                return false;
            }
            return true;
        }
        return false;
    }
}
