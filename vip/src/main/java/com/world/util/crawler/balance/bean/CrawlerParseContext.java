package com.world.util.crawler.balance.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @ClassName CrawlerParseContext
 * @Author hunter
 * @Date 2019-05-27 09:44
 * @Version v1.0.0
 * @Description
 */
@Data
public class CrawlerParseContext {

    /**
     * 数据源URL
     */
    String url;

    /**
     * 解析过滤字符串
     */
    String filterStr;

    /**
     * 数据解析selector,用于定位数据
     * html dom选择    -> body > div.container.addr-details > div:nth-child(2) > div:nth-child(1) > dl > dd:nth-child(6)
     * json 自定义规则  -> teacher>students[2]>name
     */
    String selector;


    /**
     * 解析后单位处理  oriNum/unit
     */
    BigDecimal unit;


    /**
     * 解析类型
     */
    CrawlerParseTypeEnum crawlerParseTypeEnum;

    /**
     * post请求参数集合
     */
    Map<String,String> params;

    /**
     * 同类币种执行顺序
     */
    Integer sort;

}
