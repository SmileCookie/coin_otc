package com.world.controller;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckRegex {
    static Logger log = Logger.getLogger(CheckRegex.class.getName());

    //中国移动号码段
    private static Set<String> CMCC = new HashSet<>(Arrays.asList("134", "135", "136", "137", "138", "139", "147", "150", "151", "152", "157", "158", "159", "178", "182", "183", "184", "187", "188", "198"));

    //中国联通号码段
    private static Set<String> CU = new HashSet<>(Arrays.asList("130", "131", "132", "145", "155", "156", "166", "175", "176", "185", "186"));

    //中国电信号码段
    private static Set<String> CT = new HashSet<>(Arrays.asList("133", "149", "153", "173", "177", "180", "181", "189", "199"));

    // 判断电话
    public static boolean isTelephone(String phonenumber) {
        String phone = "0\\d{2,3}-\\d{7,8}";
        Pattern p = Pattern.compile(phone);
        Matcher m = p.matcher(phonenumber);
        return m.matches();
    }

    // 判断手机号
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^1[3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    // 判断邮箱
    public static boolean isEmail(String email) {
        //"^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        //"^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        String str = "^([a-zA-Z0-9_\\.-])+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    // 判断国际手机号
    public static boolean isPhoneNumber(String mobile) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
// 			PhoneNumber swissNumberProto = phoneUtil.parse(mobile, "CH");
            PhoneNumber swissNumberProto = phoneUtil.parse(mobile, "");
            /*start by xwz 20171113 修复互联网手机号校验不通过的问题*/
            String phone = swissNumberProto.getNationalNumber() + "";
            if (swissNumberProto.getCountryCode() == 86) {
                String phonePrefix = phone.substring(0, 3);
                if (CMCC.contains(phonePrefix) || CU.contains(phonePrefix) || CT.contains(phonePrefix)) {
                    return isMobileNO(phone);
                }
            }
            /*end*/
            return phoneUtil.isValidNumber(swissNumberProto); // returns true
        } catch (NumberParseException e) {
            log.error("NumberParseException was thrown:", e);
        }
        return false;
    }

    // 判断ip
    public static boolean isIP(String ip) {
        Pattern p = Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
        Matcher m = p.matcher(ip);
        return m.matches();
    }

    public static void main(String[] args) {
        String mobile = "+8616612344321";
        Boolean flag = isPhoneNumber(mobile);
        System.out.println("=====" + flag);

    }

}