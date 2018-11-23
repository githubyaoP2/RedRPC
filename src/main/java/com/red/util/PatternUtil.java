package com.red.util;

import java.util.regex.Pattern;

public class PatternUtil {

    private static final Pattern IPV4_PATTERN =
            Pattern.compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\:(\\d)*$");
    private static final Pattern PORT =
            Pattern.compile("");

    public static boolean isIPv4Address(String ip){
        return IPV4_PATTERN.matcher(ip).matches();
    }
}
