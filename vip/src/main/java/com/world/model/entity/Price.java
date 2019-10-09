package com.world.model.entity;

import com.alibaba.fastjson.JSONObject;
import com.world.model.loan.worker.LoanAutoFactory;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * @author buxianguan
 * @date 2017/12/21
 */
public class Price {
    private final static Logger logger = Logger.getLogger(Price.class);

    /**
     * 获取币种对应的btc价格，如果没有市场，返回0
     *
     * @param currency
     * @return
     */
    public static BigDecimal getCoinBtcPrice(String currency) {
        BigDecimal price = BigDecimal.ZERO;
        JSONObject prices = LoanAutoFactory.getPrices();
        if ("btc".equals(currency)) {
            price = BigDecimal.ONE;
        } else if("usdt".equals(currency)){
            price = BigDecimal.ONE.divide(prices.getBigDecimal("btc_usdt"), 8, BigDecimal.ROUND_UP);
        }else {
            price = prices.getBigDecimal(currency + "_btc");
            if (null == price) {
                BigDecimal priceUsdt = prices.getBigDecimal(currency + "_usdt");
                if (null != priceUsdt) {
                    price = priceUsdt.divide(prices.getBigDecimal("btc_usdt"), 8, BigDecimal.ROUND_UP);
                } else {
                    price = BigDecimal.ZERO;
                }
            }
        }
        return price;
    }
}
