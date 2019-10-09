package com.world.util.crawler.balance;

import com.world.util.crawler.balance.bean.CrawlerParseContext;

public interface RequestPraser<T> {

    T parseRequest(CrawlerParseContext crawlerParseContext);

}
