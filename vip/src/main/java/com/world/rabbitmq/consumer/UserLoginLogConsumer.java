package com.world.rabbitmq.consumer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.world.config.GlobalConfig;
import com.world.model.dao.user.UserLoginIpDao;
import com.world.rabbitmq.BaseConsumerMqService;
import com.world.rabbitmq.VipRabbitmqConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/26下午8:21
 */
public class UserLoginLogConsumer extends BaseConsumerMqService {
    private static final Logger logger = LoggerFactory.getLogger(UserLoginLogConsumer.class);
    private UserLoginIpDao uld = new UserLoginIpDao();
    public UserLoginLogConsumer() {
        super(VipRabbitmqConstant.USER_LOGIN_IP_INFO,VipRabbitmqConstant.USER_LOGIN_IP_INFO, VipRabbitmqConstant.USER_LOGIN_IP_INFO);
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
            String loginName = jsonObject.get("loginName") != null ? jsonObject.get("loginName").toString() : "";
            String userId = jsonObject.get("userId") != null ? jsonObject.get("userId").toString() : "";
            String userName = jsonObject.get("userName") != null ? jsonObject.get("userName").toString() : "";
            String newIp = jsonObject.get("newIp") != null ? jsonObject.get("newIp").toString() : "";
            int terminal = jsonObject.get("terminal") != null ? Integer.valueOf(jsonObject.get("terminal").toString()) : 0;
            String version = jsonObject.get("version") != null ? jsonObject.get("version").toString() : "";
            // 保存登录IP
            uld.add(loginName, userId, userName, newIp, terminal, version);
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

    public static void main(String[] args) {
        int machineId = 1;//最大支持1-9个集群机器部署
 int hashCodeV = UUID.randomUUID().toString().hashCode();
 if(hashCodeV < 0) {//有可能是负数
 hashCodeV = - hashCodeV;
 }
//         0 代表前面补充0     
//         4 代表长度为4     
//         d 代表参数为正数型
        System.out.println(machineId+ String.format("%015d", hashCodeV));


    }
}
