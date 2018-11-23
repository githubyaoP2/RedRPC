package com.red.server;

import com.red.message.MessageType;
import com.red.message.RedMessage;
import com.red.message.ResponseMsg;
import com.red.metrics.InvokerModel;
import com.red.util.Constants;
import com.red.util.ReflectionUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerInvoker implements MethodInterceptor {

    private static Map<String,InvokerModel> invokerMap = new ConcurrentHashMap<>();

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
            //RedMessage rsp = (RedMessage) method.invoke(o,args);
            ResponseMsg responseMsg = new ResponseMsg();

            try {
                Object result = method.invoke(o, args);
                responseMsg.setInvokeSucess(Constants.invoke_success);
                responseMsg.setResult(result);

            }catch (Exception e){
                System.out.println(e);
                responseMsg.setInvokeSucess(Constants.invoke_fail);
                responseMsg.setReason("params error");
            }
            return new RedMessage(responseMsg,MessageType.ServerResponse);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            //ToDo:超时怎么中断呢
        String name = instanceName+"."+methodName;
        InvokerModel invokerModel = null;
        if((invokerModel = invokerMap.get(name)) == null) {
            invokerModel = new InvokerModel(instanceName, methodName);
            invokerMap.putIfAbsent(name,invokerModel);
        }
        try {
            Object result = methodProxy.invokeSuper(o, args);
            invokerModel.getSuccCount().incrementAndGet();
            return result;
        }catch (Exception e){
            invokerModel.getFailCount().incrementAndGet();
        }
        return null;
    }

}
