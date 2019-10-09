package com.world.util.crawler.balance.parser;

import com.api.util.http.HttpUtil;
import com.world.util.crawler.balance.BigDecimalJsonRequestParser;
import com.world.util.crawler.balance.bean.CrawlerParseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @Author Ethan
 * @Date 2019-07-04 09:13
 **/

public class SimpleJsonPostRequestParser extends BigDecimalJsonRequestParser {

    static Logger logger = LoggerFactory.getLogger(SimpleJsonPostRequestParser.class);
    @Override
    public BigDecimal parseRequest(CrawlerParseContext crawlerParseContext) {
        if (crawlerParseContext == null) {
            throw new NullPointerException("crawlerParseContext 不能为空");
        }
        logger.info("【钱包余额爬虫】requstPostJson开始爬取url={} filterStr={} selector={},params={}",
                crawlerParseContext.getUrl(),
                crawlerParseContext.getFilterStr(),
                crawlerParseContext.getSelector(),
                crawlerParseContext.getParams());

        String jsonRes = null;
        try {
            jsonRes = HttpUtil.doPost(crawlerParseContext.getUrl(), crawlerParseContext.getParams(), 5000, 5000);
        } catch (IOException e) {
            logger.info("【钱包余额爬虫】requstPostJson 由于网络原因，爬取失败url=" + crawlerParseContext.getUrl(), e);
            throw new RuntimeException();
        } catch (Exception e1) {
            logger.info("【钱包余额爬虫】requstPostJson 发生非受控异常，url=" + crawlerParseContext.getUrl(), e1);
            throw new RuntimeException();
        }
        logger.info("【钱包余额爬虫】requstPostJson爬取到原报文信息为{}", jsonRes);

        String balancestr = super.jsonRequestParser(jsonRes, crawlerParseContext.getSelector());
        BigDecimal balance = super.unitConvert(balancestr,crawlerParseContext.getUnit(),crawlerParseContext.getFilterStr());
        if (balance != null) {
            logger.info("【钱包余额爬虫】requstPostJson解析后的结果为{}", balance);
        }
        return balance;
    }
}
