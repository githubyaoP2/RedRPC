package com.red.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class PingUtil {
    //ping基于ICMP，ping成功不代表可以连接成功
    public static void ping(String hostName){
        boolean connect = false;
        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            process = runtime.exec("ping " + hostName);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is,"utf8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("返回值为:"+sb);
            is.close();
            isr.close();
            br.close();

            if (null != sb && !sb.toString().equals("")) {
                if (sb.toString().indexOf("TTL") > 0) {
                    // 网络畅通
                    connect = true;
                } else {
                    // 网络不畅通
                    connect = false;
                }
            }
            System.out.println(connect);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isReachable(String ip){
        Socket socket = null;
        try {
            socket = new Socket(ip.split(":")[0], Integer.valueOf(ip.split(":")[1]));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            try {
                socket.close();
            }catch (Exception e){}
        }
        return true;
    }
}
