package com.red.tmp;

import java.io.Serializable;

public class BHello implements HelloB,Serializable {

    public int value = 2;

    public void hello(){
        System.out.println("BHello");
    }
}
