package com.red.tmp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestJDKProxy implements InvocationHandler {

    AHello hello;

    public TestJDKProxy(AHello hello) {
        this.hello = hello;
    }

    public static void main(String[] args) {
        AHello hello = new AHello();
        HelloA helloA =  (HelloA) Proxy.newProxyInstance(TestJDKProxy.class.getClassLoader(),new Class[]{HelloA.class},new TestJDKProxy(hello));
        helloA.hello();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(proxy.getClass());
        System.out.println(method);
        System.out.println(hello);
        method.invoke(hello);
        //method.invoke(proxy);
        return proxy;
    }
}
