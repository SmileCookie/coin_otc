package com.world.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.world.config.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/26下午8:21
 */
public class VipProduceMqService extends BaseMqService {
    private static final Logger logger = LoggerFactory.getLogger(VipProduceMqService.class);

    public VipProduceMqService(String exchangeName, String routeKey) {
        super(exchangeName,routeKey);
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(GlobalConfig.rabbitmqHost);
            factory.setPort(GlobalConfig.rabbitmqPort);
            factory.setUsername(GlobalConfig.rabbitmqUser);
            factory.setPassword(GlobalConfig.rabbitmqPwd);

            produceChannelDeclare(factory, BuiltinExchangeType.TOPIC);
        } catch (Exception e) {
            logger.error("初始化websocket通知生产者基础mq类异常！", e);
        }
    }
}
