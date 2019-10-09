package com.match.rabbitmq.websocket;

import com.alibaba.fastjson.JSONObject;
import com.match.rabbitmq.RabbitmqConstant;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/28上午10:56
 */
public class DishDepthProducer extends WsProduceMqService {

    public DishDepthProducer() {
        super(RabbitmqConstant.DISH_DEPTH_EXCHANGE, RabbitmqConstant.DISH_DEPTH_EXCHANGE);
    }

    public void send(String market, String type, String redisKey) {
        JSONObject message = new JSONObject();
        message.put("market", market);
        message.put("type", type);
        message.put("redisKey", redisKey);
        sendMessage(message);
    }
}
