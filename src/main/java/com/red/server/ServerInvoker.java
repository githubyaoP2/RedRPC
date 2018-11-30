package com.red.server;

import com.red.message.MessageType;
import com.red.message.RedMessage;
import com.red.message.ResponseMsg;
import com.red.model.MetricModel;
import com.red.model.MetricServiceModel;
import com.red.util.Constants;
import com.red.util.ReflectionUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerInvoker implements MethodInterceptor {

    private static Map<String,MetricServiceModel> invokerMap = new ConcurrentHashMap<>();

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private String instanceName;
    private String methodName;
    private Object[] args;

    public ServerInvoker(String instanceName, String methodName,Object[] args) {
        this.instanceName = instanceName;
        this.methodName = methodName;
        this.args = args;
    }

    public RedMessage execute(){
        try {
            Class proxyClass = Class.forName(instanceName);
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(proxyClass);
            enhancer.setCallback(this);
            Object o = enhancer.create();
            Method method = proxyClass.getMethod(methodName,ReflectionUtil.getClassType(args));
            ResponseMsg responseMsg = new ResponseMsg();

            try {
                Object result = method.invoke(o, args);
                responseMsg.setInvokeSucess(Constants.invoke_success);
                responseMsg.setResult(result);
            }catch (Exception e){
                System.out.println(e);
                responseMsg.setInvokeSucess(Constants.invoke_fail);
                responseMsg.setReason(e.getMessage());
            }
            return new RedMessage(responseMsg,MessageType.ServerResponse);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String name = instanceName+"."+methodName;
        MetricServiceModel metricServiceModel = null;
        if((metricServiceModel = invokerMap.get(name)) == null) {
            metricServiceModel = new MetricServiceModel(instanceName, methodName);
            invokerMap.putIfAbsent(name, metricServiceModel);
        }
        try {
//            Future future = executorService.submit(()->{
//                try {
                    Object result = methodProxy.invokeSuper(o, args);
//                    return result;
//                }catch (Exception e){
//                    throw new RunTimeException();
//                }
//            });

            metricServiceModel.getInvokeCount().incrementAndGet();
            metricServiceModel.getSuccCount().incrementAndGet();
            return result;
        }catch (Exception e){
            metricServiceModel.getFailCount().incrementAndGet();
        }
        return null;
    }

}
