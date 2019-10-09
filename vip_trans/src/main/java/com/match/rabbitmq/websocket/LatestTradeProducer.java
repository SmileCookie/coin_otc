package com.match.rabbitmq.websocket;

import com.alibaba.fastjson.JSONObject;
import com.match.rabbitmq.RabbitmqConstant;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/28上午10:56
 */
public class LatestTradeProducer extends WsProduceMqService {

    public LatestTradeProducer() {
        super(RabbitmqConstant.LATEST_TRADE_EXCHANGE, RabbitmqConstant.LATEST_TRADE_EXCHANGE);
    }

    public void send(String market, String redisKey) {
        JSONObject message = new JSONObject();
        message.put("market", market);
        message.put("redisKey", redisKey);
        sendMessage(message);
    }
}
