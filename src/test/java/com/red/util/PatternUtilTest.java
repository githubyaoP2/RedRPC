package com.red.util;

import org.junit.Test;

public class PatternUtilTest {
    @Test
    public void test(){
        System.out.println(PatternUtil.isIPv4Address("10.39.211.138:8081"));
    }
}
