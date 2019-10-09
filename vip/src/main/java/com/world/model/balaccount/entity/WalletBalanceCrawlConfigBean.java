package com.world.model.balaccount.entity;

import com.world.data.mysql.Bean;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName WalletBalanceCrawlConfigBean
 * @Author hunter
 * @Date 2019-05-27 13:40
 * @Version v1.0.0
 * @Description
 */
@Data
public class WalletBalanceCrawlConfigBean extends Bean {

    Integer id;

    /**
     * 资金类型 2:比特币根据config.json配置
     */
    Integer fundsType;

    /**
     * 爬虫类型 crawlerParseTypeEnum 1:SIMPLE_HTML 2:SIMPLE_JSON
     */
    Integer crawlerType;

    /**
     * 爬取目标地址
     */
    String crawlerUrl;

    /**
     * 钱包地址
     */
    String walletAddress;

    /**
     * 钱包类型 1-冷钱包 2-热提钱包 3-热冲钱包
     */
    Integer walletType;

    /**
     * 过滤参数
     */
    String filterStr;

    /**
     * 字段选择器
     */
    String selector;

    /**
     * 单位处理
     */
    BigDecimal unit;

    /**
     * 状态 0-停用 1-正常
     */
    Integer status;

    /**
     * 更新时间
     */
    Date updateDate;

    /**
     * POST提交方式参数
     */
    String params;

    /**
     * 同类币种执行顺序
     */
    Integer sort;
}
