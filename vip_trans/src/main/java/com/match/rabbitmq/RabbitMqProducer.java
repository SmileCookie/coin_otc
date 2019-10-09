package com.match.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.world.config.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 消息发送
 *
 * @author Jack
 * @since 2019-04-09
 */
public class RabbitMqProducer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqProducer.class);
    private static Connection connection;
    private static Channel channel;


    static {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(GlobalConfig.rabbitmqHost);
        factory.setPort(GlobalConfig.rabbitmqPort);
        factory.setUsername(GlobalConfig.rabbitmqUser);
        factory.setPassword(GlobalConfig.rabbitmqPwd);

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException e) {
            logger.error("RabbitMq connection error.", e);
        } catch (TimeoutException e) {
            logger.error("RabbitMq connection timeout.", e);
        }
    }

    /**
     * 发送消息
     * @param exchange
     * @param routingKey
     * @param message
     * @param retryTimes
     * @return
     */
    public static boolean sendText(String exchange, String routingKey, String message, int retryTimes) {
        try {
            for (int times = 0;times < retryTimes;times++) {
                if (sendText(exchange, routingKey, message)) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("RabbitMq send error.", e);
        }
        return false;
    }

    /**
     * 发送本文消息
     * @param exchange
     * @param routingKey
     * @param message
     * @return
     */
    public static boolean sendText(String exchange, String routingKey, String message) {
        try {
            logger.info("exchange:{}, routingKey:{}, send message:{}", exchange, routingKey, message);
            channel.basicPublish(exchange, routingKey, MessageProperties.TEXT_PLAIN, message.getBytes());
            logger.info("exchange:{}, routingKey:{}, end success", exchange, routingKey);
            return true;
        } catch (IOException e) {
            logger.error("RabbitMq send error.", e);
        }
        return false;
    }
}
