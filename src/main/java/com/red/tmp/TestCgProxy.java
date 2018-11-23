package com.red.tmp;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class TestCgProxy implements MethodInterceptor {

    AHello hello;

    public static void main(String[] args) {
        AHello aHello = new AHello();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AHello.class);
        enhancer.setCallback(new TestCgProxy(aHello));
        AHello o = (AHello) enhancer.create();
        o.hello();
    }

    public TestCgProxy(AHello hello) {
        this.hello = hello;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println(o.getClass());
        System.out.println(method);
        System.out.println(methodProxy);
        //method.invoke(o,objects);
        methodProxy.invokeSuper(o,objects);
        return null;
    }
}
