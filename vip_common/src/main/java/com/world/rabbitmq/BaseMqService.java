package com.world.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>@Description: 消息队列基础服务</p>
 *
 * @author buxianguan
 * @date 2018/5/15上午10:25
 */
public class BaseMqService {
    private final static Logger logger = LoggerFactory.getLogger(BaseMqService.class);

    public Channel channel;
    public Connection connection;
    public String exchangeName;
    public String queueName;
    public String routeKey;

    public BaseMqService(String exchangeName, String queueName, String routeKey) {
        this.exchangeName = exchangeName;
        this.queueName = queueName;
        this.routeKey = routeKey;
    }

    public BaseMqService(String exchangeName, String routeKey) {
        this.exchangeName = exchangeName;
        this.routeKey = routeKey;
    }

    public void produceChannelDeclare(ConnectionFactory factory, BuiltinExchangeType exchangeType) throws Exception {
        connection = factory.newConnection();
        channel = connection.createChannel();

        //声明一个交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true);
    }

    public void consumeChannelDeclare(ConnectionFactory factory, BuiltinExchangeType exchangeType) throws Exception {
        connection = factory.newConnection();
        channel = connection.createChannel();

        //声明一个交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true);
        //声明一个持久化队列
        channel.queueDeclare(queueName, true, false, false, null);
        //将交换机和队列通过routeKey绑定
        channel.queueBind(queueName, exchangeName, routeKey);
    }

    public void sendMessage(Object object) {
        try {
            //声明一个消息头部
            Map<String, Object> header = new HashMap<>();
            AMQP.BasicProperties.Builder b = new AMQP.BasicProperties.Builder();
            header.put("charset", "utf-8");
            b.headers(header);
            AMQP.BasicProperties bp = b.build();

            channel.basicPublish(exchangeName, routeKey, bp, object.toString().getBytes());
        } catch (IOException e) {
            logger.error("mq发送消息异常！", e);
        }
    }
}
