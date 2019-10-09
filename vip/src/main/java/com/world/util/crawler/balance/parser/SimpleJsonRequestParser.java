package com.world.util.crawler.balance.parser;

import com.world.util.crawler.balance.BigDecimalJsonRequestParser;
import com.world.util.crawler.balance.bean.CrawlerParseContext;
import com.world.util.request.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @ClassName SimpleJsonRequestParser
 * @Author hunter
 * @Date 2019-05-27 10:11
 * @Version v1.0.0
 * @Description
 */
public class SimpleJsonRequestParser extends BigDecimalJsonRequestParser {

    static Logger logger = LoggerFactory.getLogger(SimpleHtmlRequestParser.class);


    @Override
    public BigDecimal parseRequest(CrawlerParseContext crawlerParseContext) {
        if(crawlerParseContext==null){
            throw new NullPointerException("crawlerParseContext 不能为空");
        }
        logger.info("【钱包余额爬虫】requstJson开始爬取url={} filterStr={} selector={}",
                crawlerParseContext.getUrl(),
                crawlerParseContext.getFilterStr(),
                crawlerParseContext.getSelector());
        String jsonRes = null;
        try {
            jsonRes = HttpUtil.doGet(crawlerParseContext.getUrl(), null);
        }catch (IOException e) {
            logger.info("【钱包余额爬虫】requstJson 由于网络原因，爬取失败url="+crawlerParseContext.getUrl(),e);
            throw new RuntimeException();
        }catch (Exception e1){
            logger.info("【钱包余额爬虫】requstJson 发生非受控异常，url="+crawlerParseContext.getUrl(),e1);
            throw new RuntimeException();
        }
        logger.info("【钱包余额爬虫】requstJson爬取到原报文信息为{}",jsonRes);

        String balancestr = super.jsonRequestParser(jsonRes, crawlerParseContext.getSelector());
        BigDecimal balance = super.unitConvert(balancestr, crawlerParseContext.getUnit(),crawlerParseContext.getFilterStr());
        logger.info("【钱包余额爬虫】requstJson解析后的结果为{}", balance);
        return balance;
    }
}
