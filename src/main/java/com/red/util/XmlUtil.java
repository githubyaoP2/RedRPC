package com.red.util;

import com.red.Model.ServiceModel;
import com.red.Model.ServicesModel;
import com.sun.jmx.remote.internal.Unmarshal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class XmlUtil {
    public static ServicesModel getServiceFromXml(String filepath){

        try {
            JAXBContext context = JAXBContext.newInstance(ServicesModel.class,ServiceModel.class);    // 获取上下文对象
            Unmarshaller unmarshal = context.createUnmarshaller();// 根据上下文获取unmarshaller对象
            ServicesModel servicesModel = (ServicesModel) unmarshal.unmarshal(new File(filepath));
            return servicesModel;
        }catch (JAXBException e){
            System.out.println(e);
        }

        return null;
    }

    public static void getXmlFromSeervices(ServicesModel servicesModel){

        try {
            JAXBContext context = JAXBContext.newInstance(ServicesModel.class,ServiceModel.class);    // 获取上下文对象
            Marshaller marshal = context.createMarshaller();// 根据上下文获取unmarshaller对象
            marshal.marshal(servicesModel,System.out);
        }catch (JAXBException e){
            System.out.println(e);
        }
    }
}
