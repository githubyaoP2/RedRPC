package com.red.client;

import com.red.message.MessageType;
import com.red.message.RedMessage;
import com.red.message.RequestMsg;
import com.red.message.ResponseMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RedClient {
    //缓存实例到channel的映射
    private static Map<String,GeneralInvoker> connects = new HashMap<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static ResponseMsg executeSync(String instanceName, String methodName, Object... params){
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setInstanceName(instanceName);
        requestMsg.setMethodName(methodName);
        requestMsg.setArgs(params);
        RedMessage redMessage = new RedMessage(requestMsg,MessageType.ClientRequest);
        ResponseMsg responseMsg = null;
        if(exist(instanceName)){
            responseMsg = connects.get(instanceName).invoke(redMessage);
        }else {
            GeneralInvoker invoker = new GeneralInvoker();
            responseMsg = invoker.invoke(redMessage);
            connects.put(instanceName,invoker);
        }
        return responseMsg;
    }

    public static ResponseMsg executeSync(long delays,String instanceName, String methodName, Object... params){
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setInstanceName(instanceName);
        requestMsg.setMethodName(methodName);
        requestMsg.setArgs(params);
        RedMessage redMessage = new RedMessage(requestMsg,MessageType.ClientRequest);
        ResponseMsg responseMsg = null;
        if(exist(instanceName)){
            responseMsg = connects.get(instanceName).invoke(redMessage,delays);
        }else {
            GeneralInvoker invoker = new GeneralInvoker();
            responseMsg = invoker.invoke(redMessage);
            connects.put(instanceName,invoker);
        }
        return responseMsg;
    }

    public static Future<ResponseMsg> executeAsync(String instanceName, String methodName, Object... params){
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setInstanceName(instanceName);
        requestMsg.setMethodName(methodName);
        requestMsg.setArgs(params);
        RedMessage redMessage = new RedMessage(requestMsg,MessageType.ClientRequest);

        Future<ResponseMsg> future;
        if(exist(instanceName)){
            future = executorService.submit(new Callable<ResponseMsg>() {
                @Override
                public ResponseMsg call() throws Exception {
                    return  connects.get(instanceName).invoke(redMessage,10);
                }
            });
        }else {
            GeneralInvoker invoker = new GeneralInvoker();
            future = executorService.submit(new Callable<ResponseMsg>() {
                @Override
                public ResponseMsg call() throws Exception {
                    return  invoker.invoke(redMessage,10);
                }
            });
            connects.put(instanceName,invoker);
        }
        return future;
    }
    private static boolean exist(String instanceName){
        return connects.containsKey(instanceName);
    }
}
