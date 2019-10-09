package com.world.util.crawler.balance.parser;

import com.world.util.crawler.balance.BigDecimalRequestParser;
import com.world.util.crawler.balance.bean.CrawlerParseContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @ClassName SimpleHtmlRequestParser
 * @Author hunter
 * @Date 2019-05-27 09:58
 * @Version v1.0.0
 * @Description
 */
public class SimpleHtmlRequestParser extends BigDecimalRequestParser {

    static Logger logger = LoggerFactory.getLogger(SimpleHtmlRequestParser.class);

    @Override
    public BigDecimal parseRequest(CrawlerParseContext crawlerParseContext) {
        if(crawlerParseContext==null){
           throw new NullPointerException("crawlerParseContext 不能为空");
        }
        logger.info("【钱包余额爬虫】SimpleHtmlRequestParser 开始爬取url={} filterStr={} selector={}",
                crawlerParseContext.getUrl(),
                crawlerParseContext.getFilterStr(),
                crawlerParseContext.getSelector());
        Document doc = null;
        try {
            doc = Jsoup.connect(crawlerParseContext.getUrl())
                    .timeout(3000)
                    .userAgent("Mozilla")
                    .get();
        }catch (IOException e) {
            logger.info("【钱包余额爬虫】SimpleHtmlRequestParser 由于网络原因，爬取失败url="+crawlerParseContext.getUrl(),e);
            throw new RuntimeException();
        }catch (Exception e1){
            logger.info("【钱包余额爬虫】SimpleHtmlRequestParser 发生非受控异常，url="+crawlerParseContext.getUrl(),e1);
            throw new RuntimeException();
        }
        logger.debug("【钱包余额爬虫】SimpleHtmlRequestParser 爬取到原报文信息为{}",doc.outerHtml());
        Elements elements = doc.select(crawlerParseContext.getSelector());
        String content = elements.text().replaceAll(",","");
        logger.info("【钱包余额爬虫】SimpleHtmlRequestParser 爬取select后的报文为{}",content);
        if(content==null||"".equalsIgnoreCase(content)){
            return null;
        }
        String prefixBalance = content.trim();
        BigDecimal balance = unitConvert(prefixBalance, crawlerParseContext.getUnit(), crawlerParseContext.getFilterStr());
        logger.info("【钱包余额爬虫】SimpleHtmlRequestParser 解析后的结果为{}",balance);
        return balance;
    }
}
