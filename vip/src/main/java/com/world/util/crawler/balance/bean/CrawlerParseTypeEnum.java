package com.world.util.crawler.balance.bean;

public enum CrawlerParseTypeEnum {

    /**
     * 简单HTML请求解析
     */

    SIMPLE_HTML(1),

    /**
     * 简单JSON解析 GET方式
     */
    SIMPLE_JSON(2),

    /**
     * 简单JSON解析 POST方式
     */
    SIMPLE_JSON_POST(3);

    int value;

    CrawlerParseTypeEnum(int value) {
        this.value = value;
    }

    public static CrawlerParseTypeEnum valueOf(int value){
        CrawlerParseTypeEnum[] enums = CrawlerParseTypeEnum.values();
        for(CrawlerParseTypeEnum typeEnum:enums){
            if(typeEnum.value==value){
                return typeEnum;
            }
        }
        return null;
    }

}
