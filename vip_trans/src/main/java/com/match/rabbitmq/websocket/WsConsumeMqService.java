//package com.match.rabbitmq.websocket;
//
//import com.match.rabbitmq.BaseMqService;
//import com.rabbitmq.client.AMQP;
//import com.rabbitmq.client.BuiltinExchangeType;
//import com.rabbitmq.client.ConnectionFactory;
//import com.rabbitmq.client.Consumer;
//import com.rabbitmq.client.Envelope;
//import com.rabbitmq.client.ShutdownSignalException;
//import com.world.config.GlobalConfig;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//
///**
// * <p>@Description: </p>
// *
// * @author buxianguan
// * @date 2018/7/26下午8:21
// */
//public class WsConsumeMqService extends BaseMqService implements Runnable, Consumer {
//    private static final Logger logger = LoggerFactory.getLogger(WsConsumeMqService.class);
//
//    public WsConsumeMqService(String exchangeName, String queueName, String routeKey) {
//        super(exchangeName, queueName, routeKey);
//        try {
//            ConnectionFactory factory = new ConnectionFactory();
//            factory.setHost(GlobalConfig.websocketMqHost);
//            factory.setPort(GlobalConfig.websocketMqPort);
//            factory.setUsername(GlobalConfig.websocketMqUser);
//            factory.setPassword(GlobalConfig.websocketMqPwd);
//
//            consumeChannelDeclare(factory, BuiltinExchangeType.TOPIC);
//        } catch (Exception e) {
//            logger.error("初始化websocket通知消费者基础mq类异常！", e);
//        }
//    }
//
//    @Override
//    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) {
//        String message = "";
//        try {
//            message = new String(body, "UTF-8");
//            logger.info("receive MQ message:" + message);
//        } catch (Exception e) {
//            logger.error("处理MQ消息异常！message:" + message, e);
//        } finally {
//            // 返回确认状态
//            try {
//                channel.basicAck(envelope.getDeliveryTag(), false);
//            } catch (IOException e) {
//                logger.error("MQ ack异常！message:" + message, e);
//            }
//        }
//    }
//
//    @Override
//    public void run() {
//        try {
//            channel.basicConsume(queueName, false, this);
//        } catch (Exception e) {
//            logger.error("MQ接收消息异常！", e);
//        }
//    }
//
//    @Override
//    public void handleConsumeOk(String s) {
//
//    }
//
//    @Override
//    public void handleCancelOk(String s) {
//
//    }
//
//    @Override
//    public void handleCancel(String s) throws IOException {
//
//    }
//
//    @Override
//    public void handleShutdownSignal(String s, ShutdownSignalException e) {
//
//    }
//
//    @Override
//    public void handleRecoverOk(String s) {
//    }
//}
