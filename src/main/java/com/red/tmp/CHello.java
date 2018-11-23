package com.red.tmp;

import java.io.Serializable;

public class CHello implements HelloC,Serializable {
    @Override
    public int compute(AHello a, BHello b) {
        return a.value+b.value;
    }
}
