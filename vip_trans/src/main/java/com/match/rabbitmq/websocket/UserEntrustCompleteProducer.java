package com.match.rabbitmq.websocket;

import com.alibaba.fastjson.JSONObject;
import com.match.rabbitmq.RabbitmqConstant;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/28上午10:56
 */
public class UserEntrustCompleteProducer extends WsProduceMqService {

    public UserEntrustCompleteProducer() {
        super(RabbitmqConstant.USER_ENTRUST_COMPLETE_EXCHANGE, RabbitmqConstant.USER_ENTRUST_COMPLETE_EXCHANGE);
    }

    public void send(int userId, int entrustCount, int transRecordCount) {
        JSONObject message = new JSONObject();
        message.put("userId", userId);
        message.put("ts", System.currentTimeMillis());

        JSONObject data = new JSONObject();
        data.put("countPlace", entrustCount);
        data.put("countDeals", transRecordCount);
        message.put("data", data.toJSONString());

        sendMessage(message);
    }
}
