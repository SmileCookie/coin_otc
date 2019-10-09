package com.world.util.crawler.balance;

import com.world.util.crawler.balance.bean.CrawlerParseContext;
import com.world.util.crawler.balance.parser.SimpleHtmlRequestParser;
import com.world.util.crawler.balance.parser.SimpleJsonPostRequestParser;
import com.world.util.crawler.balance.parser.SimpleJsonRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName BalanceCrawlerUtil
 * @Author hunter
 * @Date 2019-05-27 10:51
 * @Version v1.0.0
 * @Description
 */
public class BalanceCrawlerUtil {

    static SimpleHtmlRequestParser simpleHtmlRequestParser = new SimpleHtmlRequestParser();

    static SimpleJsonRequestParser simpleJsonRequestParser = new SimpleJsonRequestParser();

    static SimpleJsonPostRequestParser simpleJsonPostRequestParser = new SimpleJsonPostRequestParser();

    static Logger logger = LoggerFactory.getLogger(BalanceCrawlerUtil.class);


    public static BigDecimal parseBalance(CrawlerParseContext context){
        switch (context.getCrawlerParseTypeEnum()){
            case SIMPLE_HTML:
                return simpleHtmlRequestParser.parseRequest(context);

            case SIMPLE_JSON:
                return simpleJsonRequestParser.parseRequest(context);

            case SIMPLE_JSON_POST:
                return simpleJsonPostRequestParser.parseRequest(context);
            default:
                return null;
        }
    }

    public static BigDecimal parseBalance(List<CrawlerParseContext> contextList){
        if(contextList==null || contextList.size()==0){
            return null;
        }
        //同币种遍历按优先级执行
        contextList.sort((a, b) -> Integer.compare(a.getSort(), b.getSort()));
        Iterator<CrawlerParseContext> it = contextList.iterator();
        BigDecimal result = null;
        while(result==null && it.hasNext()){
            CrawlerParseContext context = it.next();
            try {
                result = parseBalance(context);
                if(result!=null){
                    return result;
                }
            } catch (Exception e) {
                logger.info("【钱包余额爬虫】爬取出错",e);
                if(it.hasNext()){
                    logger.info("【钱包余额爬虫】,参数：url={},尝试调用下一个地址",context.getUrl());
                }else{
                    logger.info("【钱包余额爬虫】,参数：url={},无地址可尝试调用",context.getUrl());
                }

            }
        }
        return null;
    }


}
