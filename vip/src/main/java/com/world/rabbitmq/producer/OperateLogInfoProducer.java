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
public class OperateLogInfoProducer {
    private final static Logger logger = LoggerFactory.getLogger(OperateLogInfoProducer.class);

    private static Channel channel;
    private static String exchangeName = VipRabbitmqConstant.OPERATE_LOG_INFO;
    private static String routeKey = VipRabbitmqConstant.OPERATE_LOG_INFO;

    static {
        try {
            channel = ProducerUtil.createChannel();
            //声明一个交换机
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static void send(String userName, String userId, int category, String content, String ip, String browserInfo) {
        JSONObject message = new JSONObject();
        message.put("userName", userName);
        message.put("userId", userId);
        message.put("category", category);
        message.put("content", content);
        message.put("ip", ip);
        message.put("browserInfo", browserInfo);
        try {
            ProducerUtil.sendMessage(channel, exchangeName, routeKey, message);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static void sendAddDownloadAddr(String userId, int fundsType, String addr) {
        JSONObject message = new JSONObject();
        message.put("userId", userId);
        message.put("fundsType", fundsType);
        message.put("addr", addr);
        try {
            ProducerUtil.sendMessage(channel, exchangeName, "admin.addDownloadAddr", message);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
