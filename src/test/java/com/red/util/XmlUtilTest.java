package com.red.util;

import com.red.Model.ServiceModel;
import com.red.Model.ServicesModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class XmlUtilTest {
    @Test
    public void test1(){
        ServicesModel servicesModel = XmlUtil.getServiceFromXml("src/main/resources/server/ServerList.xml");
        System.out.println(servicesModel);
    }

    @Test
    public void test2(){
        ServicesModel servicesModel = new ServicesModel();
        ServiceModel s1 = new ServiceModel();
        s1.setInterfaceName("HelloA");
        s1.setInstanceName("AHello");
        s1.setMethodName("hello");
        ServiceModel s2 = new ServiceModel();
        s2.setInterfaceName("HelloB");
        s2.setInstanceName("BHello");
        s2.setMethodName("hello");
        List s = new ArrayList();
        s.add(s1);
        s.add(s2);
        servicesModel.setService(s);
        XmlUtil.getXmlFromSeervices(servicesModel);
    }
}
