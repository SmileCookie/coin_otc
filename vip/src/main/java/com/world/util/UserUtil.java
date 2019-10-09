package com.world.util;

import com.world.config.GlobalConfig;
import com.world.util.base58.Base58;
import com.world.util.base58.Base58CheckContents;
import com.world.util.base58.Base58CheckUtil;
import com.world.util.sign.EncryDigestUtil;
import com.world.util.string.MD5;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtil {
    static Logger log = Logger.getLogger(UserUtil.class);

    public static boolean checkNick(String nick) {
        boolean flag = false;
        try {
            String check = "^[0-9a-zA-Z\u4e00-\u9fa5]+$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(nick);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }

        return flag;
    }

    /**
     * 用户名只能是数字和小写字母，且不能以数字开头
     *
     * @param userName
     * @return
     */
    public static boolean isRightNick(String userName) {
        boolean b = false;
        try {
            Pattern pattern = Pattern.compile("^[a-z]+[a-z0-9]*");
            Matcher matcher = pattern.matcher(userName);
            b = matcher.matches();
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return b;
    }

    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }

        return flag;
    }

    /**
     * 新的密码加密方法
     *
     * @return
     */
    public static String newSafeSecretMethod(String userId, String pwd) {
        return EncryDigestUtil.digestSha256(EncryDigestUtil.digestSha256(userId + pwd));///MD5.toMD5(MD5.toMD5(userId + pwd)+userId);
    }

    /**
     * 字符串加密的一种方法
     *
     * @return
     */
    public static String secretMethod(String pwd) {
        return EncryDigestUtil.digestSha256(EncryDigestUtil.digestSha256(pwd));
    }

    //加密短信验证码的方法
    public static String secretMobileCode(int type, String ip, String code) {
        return type + "_" + ip + "_" + secretMethod(code + ip);
    }

    /**
     * 获取真实姓名的编写
     *
     * @return
     */
    public static String getShortName(String realName) {
        if (realName != null && realName.length() > 1) {

            String shortName = realName.substring(0, 1);
            for (int i = 1; i < realName.length(); i++) {
                shortName += "*";
            }
            return shortName;
        } else {
            return realName;
        }
    }

    /**
     * 获取身份证号的简写
     *
     * @return
     */
    public static String getShortCardId(String cardId) {
        if (cardId != null && cardId.length() > 6) {

            String shortCard = cardId.substring(0, 1);
            for (int i = 1; i < cardId.length() - 1; i++) {
                shortCard += "*";
            }
            shortCard += cardId.substring(cardId.length() - 1);
            return shortCard;
        } else {
            return "";
        }
    }

    public static String GetRadomStr() {
        String[] str = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Random r = new Random();
        int length = 8;
        String ls = "";
        for (int j = 0; j < length; j++) {
            int a = r.nextInt(str.length);
            ls += str[a];
        }
        return ls;
    }

    //xx*****@qq.com
    public static String shortEmail(String email) {
        if (!StringUtils.isEmpty(email)) {
            String[] esplit = email.split("\\@");
            String prev = esplit[0];
            String suffix = esplit[1];
            if (email.indexOf("@") > 10) {
                email = prev.substring(0, 3) + "****" + prev.substring(prev.length() - 3) + "@" + suffix;
            } else if (email.indexOf("@") > 5) {
                email = prev.substring(0, 2) + "****" + prev.substring(prev.length() - 1) + "@" + suffix;
            } else {
                email = prev.substring(0, 1) + "****" + "@" + suffix;
            }
        }
        return email;
    }

    //xx****.com
    public static String shortEmail2(String email) {
        if (!StringUtils.isEmpty(email)) {
            String[] esplit = email.split("\\@");
            String prev = esplit[0];
            String suffix = esplit[1];
            suffix = suffix.substring(suffix.indexOf("."));
            if (email.indexOf("@") > 10) {
                email = prev.substring(0, 3) + "****" + suffix;
            } else if (email.indexOf("@") > 5) {
                email = prev.substring(0, 2) + "****" + suffix;
            } else {
                email = prev.substring(0, 1) + "****" + suffix;
            }
        }
        return email;
    }

    /**
     * 云算力记录用户名简写
     */
    public static String shortUserName(String userName) {
        if (StringUtils.isNotEmpty(userName)) {
            int length = userName.length();
            String start = "";
            String end = "";
            if (length == 3) {
                start = userName.substring(0, 1);
                end = userName.substring(length - 1);
            }
            if (length == 4) {
                start = userName.substring(0, 1);
                end = userName.substring(length - 2);
            }
            if (length >= 5) {
                start = userName.substring(0, 2);
                end = userName.substring(length - 3);
            }
            return (start + "***" + end);
        }
        return "";
    }

//	public static boolean checkAddress(String currency, String address){
//		//是否开启地址校验
//		if(!GlobalConfig.isCheckAddress) {
//			return true;
//		}
//		currency = currency.toLowerCase();
//		boolean b = false;
//		Pattern pattern = null;
//		try {
//			if(currency.equalsIgnoreCase("btc")){
//				pattern = Pattern.compile("^[1|3][a-zA-Z1-9]{26,33}$");
//			}else if(currency.equalsIgnoreCase("ltc")){
//				pattern = Pattern.compile("^[l|L|3][0-9a-zA-Z]{26,33}$");
//			}else if(currency.indexOf("etc") == 0){
//				pattern = Pattern.compile("^[0][x][0-9a-zA-Z]{40,50}$");
//			}else{
//				return true;
//			}
//			if(pattern != null){
//				Matcher matcher = pattern.matcher(address);
//				b = matcher.matches();
//			}
//
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//		return b;
//	}

    /**
     * add by xwz 20170802
     * 替换原来的校验地址方法
     *
     * @param address
     * @param coinName
     * @return
     */
    public static boolean checkAddress(String coinName, String address) {
        //是否开启地址校验
        if (!GlobalConfig.isCheckAddress) {
            return true;
        }
        coinName = coinName.toLowerCase();
        byte[] base58ByteArr = null;       //使用base58将address转为byte[]
        String base58Str = new String();   //使用Base58将address转成字符串
        Map<String, String> checkCoinAddrMap = Base58CheckUtil.checkCoinAddrMap;
        Map<String, BigDecimal> erc20TokenUnitMao = Base58CheckUtil.ERC20_TOKEN_UNIT_MAP;

        try {

            if ("etc".equals(coinName) || "eth".equals(coinName) || erc20TokenUnitMao.containsKey(coinName.toUpperCase())) {//etc,eth,gbc
                if (Base58CheckUtil.isHexNumber(address) && address.length() == 42 && address.startsWith("0x")) {//是16进制数字
                    return true;
                }
                //新增neo地址校验
            } else if ("neo".equalsIgnoreCase(coinName)) {
                Pattern pattern = Pattern.compile("^[A][0-9a-zA-Z]{33}$");
                Matcher matcher = pattern.matcher(address);
                return matcher.matches();
                //新增eos不进行地址校验
            } else if ("eos".equalsIgnoreCase(coinName)) {
                String regex = "^[a-z1-5]{12}$";
                if (address.matches(regex)) {
                    return true;
                }
            } else {//其他币种
                Base58CheckContents contents = Base58CheckUtil.parseBase58Check(address);//对地址进行解码
                if (Base58CheckUtil.isValid(contents)) {//是否是合法的加密地址
                    base58ByteArr = Base58.decode(address);
                    base58Str = Base58CheckUtil.bytesToHexString(base58ByteArr);//转成16进制数字
                    coinName = coinName.toLowerCase();
                    if (!"usdt".equals(coinName) && !"btc".equals(coinName)) {
                        if (checkCoinAddrMap.containsKey(coinName) && (base58Str.startsWith(checkCoinAddrMap.get(coinName)) || base58Str.startsWith("32"))) {//包含该币种且解析后的字符串以固定字符串开头
                            return true;
                        }
                    } else {
                        if (checkCoinAddrMap.containsKey(coinName) && (base58Str.startsWith(checkCoinAddrMap.get(coinName)) || base58Str.startsWith("05"))) {//包含该币种且解析后的字符串以固定字符串开头
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("判断提现地址出错，币种：" + coinName + ",address:" + address + "，错误信息：", e);
        }
        return false;
    }

    public static void main(String[] args) {
        String address1 = "MS9BSijwgZd87goan2e12CkqVCVPn8vFXv";
        System.out.println(checkAddress("LTC", address1));
//		System.out.println(Base58CheckUtil.bytesToHexString(Base58.decode(address)));
    }

    public static String generateNewPwd(String uid, String pwd) {
        return MD5.toMD5(uid + pwd);
    }

}
