package com.world.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.world.config.GlobalConfig;
import com.world.util.string.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2019/1/7 11:21 AM
 */
public class ProducerUtil {
    public static Channel createChannel() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(GlobalConfig.rabbitmqHost);
        factory.setPort(GlobalConfig.rabbitmqPort);
        factory.setUsername(GlobalConfig.rabbitmqUser);
        factory.setPassword(GlobalConfig.rabbitmqPwd);
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }

    public static void sendMessage(Channel channel, String exchangeName, String routeKey, Object object) throws Exception {
        //声明一个消息头部
        Map<String, Object> header = new HashMap<>();
        AMQP.BasicProperties.Builder b = new AMQP.BasicProperties.Builder();
        header.put("charset", "utf-8");
        b.headers(header);
        AMQP.BasicProperties bp = b.build();

        channel.basicPublish(exchangeName, routeKey, bp, object.toString().getBytes());
    }



}
