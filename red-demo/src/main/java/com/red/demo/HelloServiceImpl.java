package com.red.demo;

public class HelloServiceImpl implements HelloService{
    @Override
    public void hello(String name, int age) {
        System.out.println(name+" "+age);
    }
}
