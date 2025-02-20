package com.yuganji.generator.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.net.util.SubnetUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

@Log4j2
public final class NetUtil {
    private NetUtil() {
        throw new IllegalStateException("NetUtil is Utility class");
    }

    public static long ip2long(String str) {
        long result = 0L;
        try {
            String[] ipAddressInArray = str.split("\\.");
            for (int i = 0; i < ipAddressInArray.length; i++) {
                int power = 3 - i;
                int ip = Integer.parseInt(ipAddressInArray[i]);
                result += ip * Math.pow(256, power);
            }
        } catch (Exception e) {
            log.warn(str + " is not ipv4 type.");
        }
        return result;
    }

    public static long[] getSubnet(String str) {
        long[] result = {0L, 0L};
        try {
            SubnetUtils subnet = new SubnetUtils(str);
            subnet.setInclusiveHostCount(true);
            return new long[] {
                    NetUtil.ip2long(subnet.getInfo().getLowAddress()),
                    NetUtil.ip2long(subnet.getInfo().getHighAddress())
            };
        } catch (Exception e) {
            log.warn(str + " is not ipv4 type.");
        }
        return result;
    }

    public static final String IP_REGEXP = "(\\d+\\.){3}\\d+";

    public static long[] getIpRanges(String str) {
        if (Pattern.matches(NetUtil.IP_REGEXP + "\\/\\d+", str.trim())) {
            return NetUtil.getSubnet(str);
        } else if (Pattern.matches(NetUtil.IP_REGEXP + "(\\s+|)\\~(\\s+|)" + NetUtil.IP_REGEXP, str.trim())) {
            StringTokenizer token = new StringTokenizer(str, "~");
            List<Long> list = new ArrayList<>();
            while (token.hasMoreTokens()) {
                list.add(NetUtil.ip2long(token.nextToken().trim()));
            }
            return list.stream().mapToLong(x -> x).toArray();
        } else {
            return new long[] {
                NetUtil.ip2long(str),
                NetUtil.ip2long(str)
            };
        }
    }

    public static boolean compareIpRange(String ipRangeStr, String ip) {
        long[] ranges = NetUtil.getIpRanges(ipRangeStr);
        long realip = NetUtil.ip2long(ip);
        if (realip >= ranges[0] && realip <= ranges[1]) {
            return true;
        }
        return false;
    }
    
    public static String long2ip(long longIp) {
        return ((longIp >> 24) & 0xFF) + "." +
                ((longIp >> 16) & 0xFF) + "." +
                ((longIp >> 8) & 0xFF) + "." +
                (longIp & 0xFF);
    }
    
    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
    
    public static String getLocalHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
}
