package com.red.util;

import java.io.File;

public class Constants {
    public static final String service_path = "src/main/resources/server/ServerList.xml";
    public static final String zk_root_path = "/RedRPC/services";
    public static final boolean SYSTEM_PROPERTY_JMX_METRICS_SUPPORT = true;
    public static final String invoke_success = "SUCESS";
    public static final String invoke_fail = "FAIL";
    public static final String invoke_timeout = "TIMEOUT";
    public static final String separator = "/";//File.separator;


    public static final String ZK_IP = PropertiesUtil.getProperty("zk_server");
    public static final int ZK_CONNECT_TIMEOUT = Integer.valueOf(PropertiesUtil.getProperty("zk_connect_timeout"));
    public static final String HOST_IP = PropertiesUtil.getProperty("host_server");
  //  public static final int isIPv4Address = 0;
}
