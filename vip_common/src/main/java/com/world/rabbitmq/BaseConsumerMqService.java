package com.world.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/26下午8:21
 */
public class BaseConsumerMqService extends BaseMqService implements Runnable, Consumer {
    private static final Logger logger = LoggerFactory.getLogger(BaseConsumerMqService.class);

    public BaseConsumerMqService(String exchangeName, String queueName, String routeKey) {
        super(exchangeName, queueName, routeKey);
    }

    @Override
    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) {

    }

    @Override
    public void run() {
        try {
            channel.basicConsume(queueName, false, this);
        } catch (Exception e) {
            logger.error("MQ接收消息异常！", e);
        }
    }

    @Override
    public void handleConsumeOk(String s) {

    }

    @Override
    public void handleCancelOk(String s) {

    }

    @Override
    public void handleCancel(String s) throws IOException {

    }

    @Override
    public void handleShutdownSignal(String s, ShutdownSignalException e) {

    }

    @Override
    public void handleRecoverOk(String s) {
    }
}
