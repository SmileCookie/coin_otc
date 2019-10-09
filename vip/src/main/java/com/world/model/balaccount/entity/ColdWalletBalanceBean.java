package com.world.model.balaccount.entity;

import com.world.data.mysql.Bean;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName ColdWalletBalanceBean
 * @Author hunter
 * @Date 2019-05-27 13:56
 * @Version v1.0.0
 * @Description
 */
@Data
public class ColdWalletBalanceBean extends Bean {

    Integer id;

    /**
     * 资金类型 2:比特币根据config.json配置
     */
    Integer fundsType;

    /**
     * 余额
     */
    BigDecimal balance;

    /**
     * 更新时间
     */
    Date updateDate;

}
