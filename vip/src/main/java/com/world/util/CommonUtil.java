package com.world.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.SysEnum;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.user.User;
import com.world.web.Pages;
import com.world.web.action.Action;
import com.world.web.sso.SessionUser;
import com.world.web.sso.session.SsoSessionManager;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * @author Micheal Chao
 * @version v0.1
 *          createTime 2016/7/15 10:10
 */
public final class CommonUtil {

    static Logger log = Logger.getLogger(CommonUtil.class.getName());

    private CommonUtil() {
    }

    public static int stringToInt(String str) {
        return stringToInt(str, 0);
    }

    public static int stringToInt(String str, int defaultValue) {
        if (StringUtils.isNotBlank(str)) {
            try {
                return Integer.parseInt(str.trim());
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public static byte stringToByte(String str) {
        byte defaultValue = 0;
        return stringToByte(str, defaultValue);
    }

    public static byte stringToByte(String str, byte defaultValue) {
        if (StringUtils.isNotBlank(str)) {
            try {
                return Byte.parseByte(str.trim());
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public static long stringToLong(String str) {
        return stringToLong(str, 0);
    }

    public static long stringToLong(String str, long defaultValue) {
        if (StringUtils.isNotBlank(str)) {
            try {
                return Long.parseLong(str.trim());
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public static void nullToEmpty(Map<String, Object> inner) {
        for (Entry<String, Object> en : inner.entrySet()) {
            if (null == en.getValue()) {
                inner.put(en.getKey(), "");
            }
        }
    }

    public static int indexofMin(long[] targetAry) {
        long max = 0;///最大值
        int maxIndex = 0;////下标
        for (int i = 0; i < targetAry.length; i++) {
            if (i == 0) {
                max = targetAry[0];
            }
            long temp = targetAry[i];
            if (temp < max) {
                max = temp;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static String getCurrencySymbol(String currency) {
        String symbol = null;
        switch (currency.toLowerCase()) {
            case "cny":
                symbol = "￥";
                break;
            case "btc":
                symbol = "฿";
                break;
            case "ltc":
                symbol = "Ł";
                break;
            case "eth":
            case "etc":
                symbol = "E";
                break;
            default:
                break;
        }

        return symbol;
    }

    public static String getBrowserInfo(HttpServletRequest request) {
        String info = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(info);
        Browser browser = userAgent.getBrowser();
        OperatingSystem os = userAgent.getOperatingSystem();

        String info1 = info.toLowerCase();
        String extend = "";
        StringBuilder sb = new StringBuilder(browser.getName());

        log.info(info);
        if (info1.contains("maxthon")) {
            sb.append(" 遨游浏览器");
        } else if (info1.contains("lbbrowser")) {
            sb.append(" 猎豹浏览器");
        } else if (info1.contains("qq")) {
            sb.append(" QQ浏览器");
        } else if (info1.contains("uc")) {
            sb.append(" UC浏览器");
        } else if (info1.contains("360")) {
            sb.append(" 360浏览器");
        }
        sb.append(", ").append(os.getName());

        return sb.toString();
    }

    public static Timestamp addDay(Timestamp time, int days) {
        long oneday = 24 * 60 * 60 * 1000;
        return new Timestamp(time.getTime() + oneday * days);
    }
    
    public static Timestamp addSecond(Timestamp time, int seconds) {
        long aSecond = 1000;
        return new Timestamp(time.getTime() + aSecond * seconds);
    }

    public static boolean isEmptyCollection(Collection collection) {
        return collection == null || collection.size() < 1 ||
                (collection.size() == 1 && collection.toArray()[0] == null);
    }

    public static boolean isNotEmptyCollection(Collection collection) {
        return !isEmptyCollection(collection);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null)
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String dayStr = null;
        try {
            dayStr = sdf.format(date);
        } catch (Exception ex) {
        }

        return dayStr;
    }

    /**
     * 1. 开启了登录谷歌验证的，判断是否需要跳转到填写谷歌验证页面
     * 2. 异地登录后，判断是否需要跳到填写谷歌验证页面
     * 3. 是否为冻结用户
     * @param pages
     * @param isTip 是否提示或跳转到提示页面
     * @return
     */
    public static boolean needInterceptAfterLogin(Pages pages, boolean isTip) {
        SsoSessionManager.initSession(pages);
        SessionUser su = null;
        if (pages.session != null) {
            su = pages.session.getUser(pages);
        }
        if (pages.session != null && su != null && su.others != null) { // 异地登录谷歌验证
            if(su.others.getBooleanValue("loginNeedGoogleAuth")){
                if(isTip) {
                    if (pages.contentType.equals(Action.CONTENT_TEXT_JAVASCRIPT)) {
                        pages.json(pages.L("为了您的账户安全，登录时需要进行Google验证。"), false, "{\"loginGoogleAuth\" : true}");
                    } else {
                        pages.toGoogleAhen();
                    }
                }
                return true;
            }else if(su.others.getBooleanValue("ipNeedAuthen")){ // 登录时谷歌验证
                if(pages.contentType.equals(Action.CONTENT_TEXT_JAVASCRIPT)) {
                    pages.json(pages.L("异地登录系统，请先认证!"), false, "{\"diffIpLogin\" : true}");
                }else {
                    pages.toIpAhen();
                }
                return true;
            }else if(su.others.getBooleanValue("freezed")){
                if(pages.contentType.equals(Action.CONTENT_TEXT_JAVASCRIPT)) {
                    pages.json(pages.L("账户异常，请联系管理员!"), true, "{\"isLogin\" : false}");
                }else {
                    pages.toFreezPage();
                }
                return true;
            }
        }

        return false;
    }
    
    /**
     * 获取未达到提币确认次数的折合的比特币数量
     * @param userId
     * @param prices
     * @return
     */
    public static BigDecimal getNoConfirmConverts(String userId, JSONObject prices){
    	Map<String, CoinProps> map = DatabasesUtil.getCoinPropMaps();
    	BigDecimal noConfirmConverts = BigDecimal.ZERO;
    	for(Entry<String, CoinProps> entry : map.entrySet()){
    		BigDecimal noConfirm = getNoconfirmWithdrawAmount(userId, entry.getValue());
    		if(prices.containsKey(entry.getKey())){
    			noConfirm = noConfirm.multiply(prices.getBigDecimal(entry.getKey()));
    		}
    		noConfirmConverts = noConfirmConverts.add(noConfirm);
    	}
    	
    	return noConfirmConverts;
    }

    /**
     * 查询确认成功，但不能提现的金额
     * @param userId
     * @param coint
     * @return
     */
    public static BigDecimal getNoconfirmWithdrawAmount(String userId, CoinProps coint){
        BigDecimal number = BigDecimal.ZERO;
        String sql = "select cast(sum(amount) as SIGNED) amount from "+coint.getStag()+"details where userId=? and type=1";

        int successConfirmTimes = coint.getInConfirmTimes();
        int withdrawCofirmTimes = coint.getOutConfirmTimes();

        if(successConfirmTimes < withdrawCofirmTimes){
            sql += " and confirmTimes > " + successConfirmTimes;

        }
        sql += " and confirmTimes < " + withdrawCofirmTimes;

        DetailsBean detail = (DetailsBean)Data.GetOne(sql, new Object[]{userId},DetailsBean.class);
        if(detail != null && detail.getAmount()!=null){
            number = detail.getAmount();
        }
        
        return number;
    }

    /**
     * 把地址标注为已审核，不超额度不需要财务审核直接打币
     * @param coint
     * @param userId
     * @param address
     */
    public static void setAddrHasAuth(CoinProps coint, long userId, String address){
        String sql = "update "+ coint.getStag() +"receiveaddr set ischecked=1 where address=? and userId=? and ischecked=0";
        Data.Update(sql, new Object[]{address, userId+""});
    }

    public static boolean cannotRequest(Map<String, Map<String,Long>> map, String userId){
       Map<String,Long> prevTime = map.get(userId);
       String lastTimeKey = "lastTimeSecond";
       String accessTimesKey = "accessTimes";

        //map.put(userId, System.currentTimeMillis());
        
        if(prevTime == null ){
        	prevTime = new HashMap<String,Long>();
        	prevTime.put(lastTimeKey, System.currentTimeMillis()/1000);
        	prevTime.put(accessTimesKey, 1L);
        	map.put(userId, prevTime);
            return false;
        }else{
        	long currTime = System.currentTimeMillis()/1000;//当前秒数
    		long lastTime = prevTime.get(lastTimeKey);//最后访问秒数
    		
        	if(prevTime.get(accessTimesKey) < 3){
        		if(currTime==lastTime){
        			long accessTimes = prevTime.get(accessTimesKey);
        			accessTimes++;
        			prevTime.put(accessTimesKey,accessTimes);
        		}else{
        			prevTime.put(lastTimeKey, System.currentTimeMillis()/1000);
                	prevTime.put(accessTimesKey, 1L);
        		}
        		return false;
        	}else{
        		if(currTime==lastTime){//同一秒，大于3次返回true
        			long accessTimes = prevTime.get(accessTimesKey);
        			accessTimes++;
        			prevTime.put(accessTimesKey,accessTimes);
        			return true;
        		}else{//不在同一秒了，重新计数
        			prevTime.put(lastTimeKey, System.currentTimeMillis()/1000);
                	prevTime.put(accessTimesKey, 1L);
                	return false;
        		}
        	}
        }

    }


    public static String replaceBlank(String str) {
        String dest = null;
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }

        return dest;
    }
    
    public static String shortRealName(String realName){
        String  shortRealName = null;
        if(realName != null){
            realName = realName.trim();
            int len = realName.length();
            if( len > 1){
                shortRealName = realName.charAt(0) + "*";
                if(len > 2){
                    shortRealName += realName.charAt(len - 1);
                }
            }
        }
        return shortRealName;
    }
    
    public static Map<Integer, String> enumToMap(SysEnum[] sysEnums){
        return EnumUtils.enumToMap(sysEnums);
    }
    
    
    /**
    *
    * @param user
    * @return 0 都不是，1：手机, 2: 邮箱
    */
   public static int checkMobileNoOrEmail(User user){
       int type = 0;
       String sendAddr = user.getUserContact().getSafeMobile();
       if(StringUtils.isBlank(sendAddr)){
           String email = user.getUserContact().getSafeEmail();
           if (StringUtils.isNotBlank(email)) {
               type = 2;
           }
       }else{
           type = 1;
       }

       return type;
   }

   /**
    * 将String转换成boolean.
    */
   public static boolean stringToBoolean(String source, boolean defaultVal) {
       Boolean b = stringToBoolean(source);
       return b == null ? defaultVal : b.booleanValue();
   }
   
   /**
    * 将String转换成Boolean.
    */
   public static Boolean stringToBoolean(String source) {
       if (source != null)
           source = source.trim();
       if (StringUtils.isNotEmpty(source))
           return "1".equals(source) || "true".equalsIgnoreCase(source);
       return null;
   }
   
   public static String getAuditStatusDesc(int key){
       String desc = null;
       AuditStatus[] auditStatusArr = AuditStatus.values();
       for(AuditStatus auditStatus: auditStatusArr){
           if(key == auditStatus.getKey()){
               desc = auditStatus.getValue();
               break;
           }
       }

       return desc;
   }



    /**
     * 四舍五入操作
     *
     * @param d   要操作的数
     * @param len 保留几位有效数字
     * @return
     */
    public static double round(double d, int len) {
        BigDecimal b1 = new BigDecimal(d);
        BigDecimal b2 = new BigDecimal(1);
        // 任何一个数字除以1都是原数字
        // ROUND_HALF_UP是BigDecimal的一个常量,表示进行四舍五入的操作
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 返回有效位数位lang的String的金额格式化数据
     * @param amount 格式化的金额   1.2345678
     * @param lang  保留数点后多少位  6
     * @return  string   1.234568
     * xzhang 20170826
     */
    public static String  getAmountStr(BigDecimal amount,int lang){
        double amountdouble = round(amount.doubleValue(),lang);
        DecimalFormat df   = new DecimalFormat("######0.000000");
        String amountStr = df.format(amountdouble);
        if(amountStr.indexOf(".") > 0){
            //正则表达
            amountStr = amountStr.replaceAll("0+?$", "");//去掉后面无用的零
            amountStr = amountStr.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return amountStr;
    }

    /**
     * 使用 Map按value进行排序
     * @param oriMap
     * @return
     */
    public static Map<String, JSONObject> sortMapByValue(Map<String, JSONObject> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, JSONObject> sortedMap = new LinkedHashMap<>();
        List<Entry<String, JSONObject>> entryList = new ArrayList<>(oriMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, JSONObject>> iter = entryList.iterator();
        Map.Entry<String, JSONObject> tmpEntry;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    /**
     * 比较器类,按照serNum升序排列
     */
    static class MapValueComparator implements Comparator<Map.Entry<String, JSONObject>> {

        @Override
        public int compare(Entry<String, JSONObject> me1, Entry<String, JSONObject> me2) {

            int serNum1 = Integer.parseInt(me1.getValue().get("serNum").toString());
            int serNum2 = Integer.parseInt(me2.getValue().get("serNum").toString());

            return serNum1-serNum2;
        }
    }

    /**
     *
     * @param amount 格式化的金额   1.2345678
     * @param lang  保留数点后多少位  6
     * @return  string   1.234568
     */
    public static String  getAmountAddZERO(BigDecimal amount,int lang){
        double amountdouble = roundDOWN(amount.doubleValue(),lang);
        if(lang == 0){
            return new DecimalFormat("0").format(amountdouble);
        }
        String formatStr = "0.";
        for(int i=0;i<lang;i++){
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(amountdouble);

    }

    /**
     * 四舍五入操作
     *
     * @param d   要操作的数
     * @param len 保留几位有效数字
     * @return
     */
    public static double roundDOWN(double d, int len) {
        BigDecimal b1 = new BigDecimal(d);
        BigDecimal b2 = new BigDecimal(1);
        // 任何一个数字除以1都是原数字
        // ROUND_HALF_UP是BigDecimal的一个常量,表示进行四舍五入的操作
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 将map转成json
     * @param key
     * @param value
     * @return
     */
    public static String mapToJsonStr(String key,String value){
        Map map = new HashMap();
        map.put(key,value);
        String msg = JSONObject.toJSONString(map);
        return msg;
    }

    /**
     * 将map转成json
     * @param key
     * @param value
     * @return
    */
    public static String mapToJsonStr(String msg,String key,String value){
        Map map = new HashMap();
        if(StringUtils.isNotEmpty(msg)){
            JSONObject  jsonObject = JSONObject.parseObject(msg);
            //json对象转Map
            map = (Map<String,Object>)jsonObject;
        }
        if(StringUtils.isEmpty(value)){
            return msg;
        }
        map.put(key,value);
        String newMsg = JSONObject.toJSONString(map);
        return newMsg;
    }


}
