package com.world.util;

import com.world.model.Market;
import com.world.web.Pages;
import com.world.web.action.Action;
import com.world.web.sso.SessionUser;
import com.world.web.sso.session.SsoSessionManager;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public final class CommonUtil {
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

    public static Timestamp addDay(Timestamp time, int days) {
        long oneday = 24 * 60 * 60 * 1000;
        return new Timestamp(time.getTime() + oneday * days);
    }

    public static boolean isEmptyCollection(Collection collection) {
        return collection == null || collection.size() < 1 ||
                (collection.size() == 1 && collection.toArray()[0] == null);
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
        /*
        if (pages.session != null && su != null && su.others != null) { // 异地登录谷歌验证
            if(su.others.getBooleanValue("loginNeedGoogleAuth")){
                if(isTip) {
                    if (pages.contentType.equals(Action.CONTENT_TEXT_JAVASCRIPT)) {
                        pages.json(pages.L("为了您的账户安全，登录时需要进行Google验证。"), false, "{\"loginGoogleAuth\" : true}",true);
                    } else {
                        pages.toGoogleAhen();
                    }
                }
                return true;
            }else if(su.others.getBooleanValue("ipNeedAuthen")){ // 登录时谷歌验证
                if(pages.contentType.equals(Action.CONTENT_TEXT_JAVASCRIPT)) {
                    pages.json(pages.L("异地登录系统，请先认证!"), false, "{\"diffIpLogin\" : true}",true);
                }else {
                    pages.toIpAhen();
                }
                return true;
            }else if(su.others.getBooleanValue("freezed")){
                if(pages.contentType.equals(Action.CONTENT_TEXT_JAVASCRIPT)) {
                    pages.json(pages.L("账户异常，请联系管理员!"), true, "{\"isLogin\" : false}",true);
                }else {
                    pages.toFreezPage();
                }
                return true;
            }
        }*/

        return false;
    }

    /**
     * 使用 Map按value进行排序
     * @param oriMap
     * @return
     */
    public static Map<String, Market> sortMapByValue(Map<String, Market> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, Market> sortedMap = new LinkedHashMap<>();
        List<Entry<String, Market>> entryList = new ArrayList<>(oriMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, Market>> iter = entryList.iterator();
        Map.Entry<String, Market> tmpEntry;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    /**
     * 比较器类,按照serNum升序排列
     */
    static class MapValueComparator implements Comparator<Map.Entry<String, Market>> {

        @Override
        public int compare(Entry<String, Market> me1, Entry<String, Market> me2) {

            int serNum1 = me1.getValue().getSerNum();
            int serNum2 = me2.getValue().getSerNum();

            return serNum1-serNum2;
        }
    }
}
