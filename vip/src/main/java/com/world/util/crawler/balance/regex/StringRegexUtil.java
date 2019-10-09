package com.world.util.crawler.balance.regex;

import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName StringRegexUtil
 * @Author hunter
 * @Date 2019-05-27 10:15
 * @Version v1.0.0
 * @Description
 */
public class StringRegexUtil {

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{}
     * \\需要第一个替换，否则replace方法替换时会有逻辑bug
     */
    public static String str2Regex(String str) {
        if(StringUtils.isBlank(str)){
            return str;
        }

        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
