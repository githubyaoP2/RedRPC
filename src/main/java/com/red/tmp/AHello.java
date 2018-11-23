package com.red.tmp;

import java.io.Serializable;

public class AHello implements HelloA,Serializable {

    public int value = 1;

    public void hello(){
        System.out.println("AHello");
    }
}
