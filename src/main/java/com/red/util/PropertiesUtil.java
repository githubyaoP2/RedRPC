package com.red.util;

import java.io.*;
import java.util.Properties;

/**
 *  资源文件加载
 */
public class PropertiesUtil {
    final static private Properties properties;

    static{
        properties = new Properties();
        File f = new File(PropertiesUtil.class.getClassLoader().getResource("server").getPath());
        File[] files = f.listFiles();
        for(File file:files){
            if(file.isFile()){
                try {
                    properties.load(new FileInputStream(file));
                }catch (IOException e){

                }
            }
        }
    }

    public static String getProperty(String name){
        return properties.getProperty(name);
    }

}
