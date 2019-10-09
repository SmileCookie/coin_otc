package com.world.rabbitmq.consumer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.world.config.GlobalConfig;
import com.world.model.dao.log.OperateLogDao;
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
public class OperateLogInfoConsumer extends BaseConsumerMqService {
    private static final Logger logger = LoggerFactory.getLogger(OperateLogInfoConsumer.class);
    private OperateLogDao operateLogDao = new OperateLogDao();

    public OperateLogInfoConsumer() {
        super(VipRabbitmqConstant.OPERATE_LOG_INFO, VipRabbitmqConstant.OPERATE_LOG_INFO, VipRabbitmqConstant.OPERATE_LOG_INFO);
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(GlobalConfig.rabbitmqHost);
            factory.setPort(GlobalConfig.rabbitmqPort);
            factory.setUsername(GlobalConfig.rabbitmqUser);
            factory.setPassword(GlobalConfig.rabbitmqPwd);
            consumeChannelDeclare(factory, BuiltinExchangeType.TOPIC);
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
            String userName = jsonObject.get("userName") != null ? jsonObject.get("userName").toString() : "";
            String userId = jsonObject.get("userId") != null ? jsonObject.get("userId").toString() : "";
            Integer category = jsonObject.get("category") != null ? Integer.valueOf(jsonObject.get("category").toString()) : null;
            String content = jsonObject.get("content") != null ? jsonObject.get("content").toString() : "";
            String ip = jsonObject.get("ip") != null ? jsonObject.get("ip").toString() : "";
            String browserInfo = jsonObject.get("browserInfo") != null ? jsonObject.get("browserInfo").toString() : "";
            // 保存
            operateLogDao.record(userId, userName, category, content, ip, browserInfo);
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
