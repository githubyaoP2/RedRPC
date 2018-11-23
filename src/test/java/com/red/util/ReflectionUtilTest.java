package com.red.util;

import com.red.tmp.AHello;
import com.red.tmp.BHello;
import com.red.tmp.CHello;
import org.junit.Test;

public class ReflectionUtilTest {
    @Test
    public void test(){
        Object[] o = {new AHello(),new BHello(), new CHello()};
        Class[] c = ReflectionUtil.getClassType(o);
        System.out.println(c);
    }
}
