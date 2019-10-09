package com.world.util.crawler.balance;

import cn.jpush.api.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.util.crawler.balance.regex.StringRegexUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Ethan
 * @Date 2019-07-04 16:18
 **/

public abstract class BigDecimalJsonRequestParser extends BigDecimalRequestParser{
    /**
     * json字符串解析
     * @param jsonRes json串
     * @param selector json选择器 e.g. 1.data 2.data>amount 3.data>result[0]>balance
     * @return
     */
    public String jsonRequestParser(String jsonRes, String selector) {
        if (StringUtils.isEmpty(jsonRes)) {
            return null;
        }
        //最后字段
        String unFilterRs = null;
        JSON json = JSONObject.parseObject(jsonRes);
        //获取字段选择器
        String[] selectorArray = selector.split("\\>");

        if (selectorArray != null) {
            int length = selectorArray.length;
            for (int i = 0; i < selectorArray.length; i++) {
                //jsonobject or jsonarray key
                String key = selectorArray[i];
                if (key == null || key.equalsIgnoreCase("")) {
                    throw new RuntimeException("爬虫读取配置错误，selector=" + selector);
                }
                //进行正则匹配，是否符合 [x]
                Pattern pattern = Pattern.compile("\\[\\d+\\]");
                Matcher matcher = pattern.matcher(key);
                Boolean arrayFlag = matcher.find();
                if (arrayFlag) {
                    //获得[index]
                    String subSeqStr = matcher.group();
                    //获得JsonArray的name
                    String arrayName = key.replaceAll(StringRegexUtil.str2Regex(subSeqStr), "").trim();
                    //获得JsonArray的index
                    Integer index = Integer.valueOf(subSeqStr.replaceAll("\\[", "").replaceAll("\\]", "").trim());
                    //获得key=${arrayName}的jsonarray
                    JSONArray jsonArray = ((JSONObject) json).getJSONArray(arrayName);
                    //为下级解析准备json
                    json = jsonArray.getJSONObject(index);
                } else {
                    //普通对象或者字段
                    if ((i + 1) == length) {
                        //最后一个是字段
                        unFilterRs = ((JSONObject) json).getString(key.trim());
                        return unFilterRs;
                    } else {
                        //是对象
                        json = ((JSONObject) json).getJSONObject(key.trim());
                    }
                }
            }
        }
        return null;
    }
}
