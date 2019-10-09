package com.world.util.ip.impl;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.log4j.Level;



/**
 * 工具类，提供一些方便的方法
 */
public class Util {
	
	/**
     * 从ip的字符串形式得到字节数组形式
     * @param ip 字符串形式的ip
     * @return 字节数组形式的ip
     */
    public static byte[] getIpByteArrayFromString(String ip) {
        byte[] ret = new byte[4];
        StringTokenizer st = new StringTokenizer(ip, ".");
        try {
            ret[0] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
            ret[1] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
            ret[2] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
            ret[3] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
        } catch (Exception e) {
          LogFactory.log("从ip的字符串形式得到字节数组形式报错", Level.ERROR, e);
        }
        return ret;
    }
    /**
     * @param ip ip的字节数组形式
     * @return 字符串形式的ip
     */
    public static String getIpStringFromBytes(byte[] ip) {
    	StringBuilder sb = new StringBuilder();
	    //sb.delete(0, sb.length());
    	sb.append(ip[0] & 0xFF);
    	sb.append('.');   	
    	sb.append(ip[1] & 0xFF);
    	sb.append('.');   	
    	sb.append(ip[2] & 0xFF);
    	sb.append('.');   	
    	sb.append(ip[3] & 0xFF);
    	return sb.toString();
    }
    
    /**
     * 根据某种编码方式将字节数组转换成字符串
     * @param b 字节数组
     * @param offset 要转换的起始位置
     * @param len 要转换的长度
     * @param encoding 编码方式
     * @return 如果encoding不支持，返回一个缺省编码的字符串
     */
    public static String getString(byte[] b, int offset, int len, String encoding) {
        try {
            return new String(b, offset, len, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b, offset, len);
        }
    }

    /**
     * 校验IP格式
     * @param ip
     * @return
     */
    public static boolean ipCheck(String ip){
        // ip的正则表达式
        String ipReg = "^(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.)(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.){2}([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))$";
        Pattern ipPattern = Pattern.compile(ipReg);
        boolean flag = ipPattern.matcher(ip).matches();
        return flag;
    }

    public static void main(String[] args) {
        boolean b = ipCheck("222.173.105.250");
        System.out.println(b);
    }
}


