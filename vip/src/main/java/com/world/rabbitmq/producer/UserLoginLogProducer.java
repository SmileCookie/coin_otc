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
public class UserLoginLogProducer {
    private final static Logger logger = LoggerFactory.getLogger(UserLoginLogProducer.class);

    private static Channel channel;
    private static String exchangeName = VipRabbitmqConstant.USER_LOGIN_IP_INFO;
    private static String routeKey = VipRabbitmqConstant.USER_LOGIN_IP_INFO;

    static {
        try {
            channel = ProducerUtil.createChannel();
            //声明一个交换机
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static void send(String loginName, String userId, String userName, String newIp, int terminal, String version) {
        JSONObject message = new JSONObject();
        message.put("loginName", loginName);
        message.put("userId", userId);
        message.put("userName", userName);
        message.put("newIp", newIp);
        message.put("terminal", terminal);
        message.put("version", version);
        try {
            ProducerUtil.sendMessage(channel, exchangeName, routeKey, message);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static void main(String[] args) {
        UserLoginLogProducer.send("test", "1003569", "test", "192.168.2.10", 1, "");
    }
}
