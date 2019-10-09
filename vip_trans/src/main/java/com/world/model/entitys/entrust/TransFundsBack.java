package com.world.model.entitys.entrust;

import com.world.data.big.table.TableInfo;
import com.world.data.big.table.UpdateWay;
import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/11/17 3:15 PM
 */

/**
 * 资金回退处理完的数据1小时后迁移到all表中
 */
@TableInfo(databases = {"btcusdtentrust","dashbtcentrust","elfusdtentrust","eosbtcentrust","eosusdtentrust","etcbtcentrust","etcusdtentrust","ethbtcentrust",
                        "ethusdtentrust","iostbtcentrust","iostusdtentrust","kncusdtentrust","linkbtcentrust","ltcbtcentrust","ltcusdtentrust","omgbtcentrust",
                        "qtumusdtentrust","sntusdtentrust","zrxbtcentrust",
                        "manabtcentrust","mcobtcentrust","lrcbtcentrust","dgdbtcentrust","vdsbtcentrust","vdsusdtentrust"
                       }, tableName = "trans_funds_back", tableDown = true, shardNum = 1, field = "userId", updateWay = UpdateWay.ASYNC, asyncFrequency = 300, primaryKey = "id",
           conditions = {"times < (UNIX_TIMESTAMP(NOW()) - 3600) * 1000 and status=1 limit 0,200"})

public class TransFundsBack extends Bean {
    private long id;
    private BigDecimal money;
    private long entrustId;
    private int userId;
    private int fundsType;
    private long times;

    public TransFundsBack() {
    }

    public TransFundsBack(BigDecimal money, long entrustId, int userId, int fundsType, long times) {
        this.money = money;
        this.entrustId = entrustId;
        this.userId = userId;
        this.fundsType = fundsType;
        this.times = times;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public long getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(long entrustId) {
        this.entrustId = entrustId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }
}
