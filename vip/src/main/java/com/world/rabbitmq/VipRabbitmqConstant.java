package com.world.rabbitmq;

import com.world.config.GlobalConfig;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/5/15下午3:49
 */
public class VipRabbitmqConstant {

    /**
     * 登录日志UserLoginIp mq队列信息
     */
    public static final String USER_LOGIN_IP_INFO = GlobalConfig.userLoginLogInfo;
    /**
     * 操作日志 operateLog mq队列信息
     */
    public static final String OPERATE_LOG_INFO = GlobalConfig.operateLogInfo;
    public static final String PAY_USER_WALLET_QUEUE = GlobalConfig.payUserWalletQueue;

}
