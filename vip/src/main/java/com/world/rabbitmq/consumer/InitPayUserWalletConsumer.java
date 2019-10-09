package com.world.rabbitmq.consumer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.world.config.GlobalConfig;
import com.world.model.dao.user.mem.UserCache;
import com.world.rabbitmq.BaseConsumerMqService;
import com.world.rabbitmq.VipRabbitmqConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/26下午8:21
 */
public class InitPayUserWalletConsumer extends BaseConsumerMqService {
    private static final Logger logger = LoggerFactory.getLogger(InitPayUserWalletConsumer.class);
    public InitPayUserWalletConsumer() {
        super(VipRabbitmqConstant.PAY_USER_WALLET_QUEUE,VipRabbitmqConstant.PAY_USER_WALLET_QUEUE, VipRabbitmqConstant.PAY_USER_WALLET_QUEUE);
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(GlobalConfig.rabbitmqHost);
            factory.setPort(GlobalConfig.rabbitmqPort);
            factory.setUsername(GlobalConfig.rabbitmqUser);
            factory.setPassword(GlobalConfig.rabbitmqPwd);

            consumeChannelDeclare(factory, BuiltinExchangeType.FANOUT);
        } catch (Exception e) {
            logger.error("初始化登录日志通知消费者基础mq类异常！", e);
        }
    }

    @Override
    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) {
        String message = "";
        try {
            message = new String(body, "UTF-8");
            JSONObject jsonObject = JSONUtil.parseObj(message);
            String userId = jsonObject.get("userId") != null ? jsonObject.get("userId").toString() : "";
            // 保存登录IP
            UserCache.resetUserWalletFundsFromDatabase(userId);
            UserCache.resetUserFundsFromDatabase(userId);
            UserCache.resetUserOtcFundsFromDatabase(userId);
            UserCache.resetUserFinancialFunds(userId);
            logger.info("receive MQ message:" + message);
        } catch (Exception e) {
            logger.error("处理MQ消息异常！message:" + message, e);
        } finally {
            // 返回确认状态
            try {
                channel.basicAck(envelope.getDeliveryTag(), false);
            } catch (IOException e) {
                logger.error("MQ ack异常！message:" + message, e);
            }
        }
    }
}
