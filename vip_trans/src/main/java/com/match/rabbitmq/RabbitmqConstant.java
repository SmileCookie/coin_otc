package com.match.rabbitmq;

import com.world.config.GlobalConfig;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/5/15下午3:49
 */
public class RabbitmqConstant {

    /**
     * 最新成交记录通知 websocket mq队列信息
     */
    public static final String LATEST_TRADE_EXCHANGE = GlobalConfig.latestTradeExchange;
    /**
     * 最新盘口挂单通知 websocket mq队列信息
     */
    public static final String DISH_DEPTH_EXCHANGE = GlobalConfig.dishDepthExchange;
    /**
     * 最新盘口深度通知 websocket mq队列信息
     */
    public static final String USER_ENTRUST_COMPLETE_EXCHANGE = GlobalConfig.userEntrustCompleteExchange;

}
