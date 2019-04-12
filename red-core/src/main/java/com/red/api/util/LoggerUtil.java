package com.red.api.util;

import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoggerUtil {

    private Queue<String> loggerMessage = new ConcurrentLinkedQueue<String>();

    public static void info(Method method,String message){}
    public static void info(String message){}
    public static void error(Method method,String message){}
    public static void warn(Method method,String message){}

}
