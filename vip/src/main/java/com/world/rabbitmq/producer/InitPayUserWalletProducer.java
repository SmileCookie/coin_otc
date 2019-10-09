package com.world.rabbitmq.producer;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.world.rabbitmq.ProducerUtil;
import com.world.rabbitmq.VipRabbitmqConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/28上午10:56
 */
public class InitPayUserWalletProducer {
    private final static Logger logger = LoggerFactory.getLogger(InitPayUserWalletProducer.class);

    private static Channel channel;
    private static String exchangeName = VipRabbitmqConstant.PAY_USER_WALLET_QUEUE;
    private static String routeKey = VipRabbitmqConstant.PAY_USER_WALLET_QUEUE;

    static {
        try {
            channel = ProducerUtil.createChannel();
            //声明一个交换机
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static void send(String userId) {
        JSONObject message = new JSONObject();
        message.put("userId", userId);
        try {
            ProducerUtil.sendMessage(channel, exchangeName, routeKey, message);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
