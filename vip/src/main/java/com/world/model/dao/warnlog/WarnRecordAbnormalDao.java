package com.world.model.dao.warnlog;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.warnlog.WarnRecordAbnormal;

import java.math.BigDecimal;

/**
 * <p></p>
 *
 * @author zhangwt
 * @date 2019/1/14 15:29
 */
public class WarnRecordAbnormalDao extends DataDaoSupport<WarnRecordAbnormal> {

    public WarnRecordAbnormalDao() {
        setDatabase("vip_main");
    }

    @Override
    public int insert(WarnRecordAbnormal warnRecordAbnormal) {
        String sql = "insert into `coin_qt_recordabnormal` " +
                "(entrust_market,user_numbers,hedging_numbers,numbers,start_time,end_time,date_time,scanning_frequency,scanning_type,save_time,state)" +
                " values ( ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";
        Object[] param = new Object[]{warnRecordAbnormal.getEntrustMarket(), warnRecordAbnormal.getUserNumbers(), warnRecordAbnormal.getHedgingNumbers(), warnRecordAbnormal.getNumbers(),
                warnRecordAbnormal.getStartTime(), warnRecordAbnormal.getEndTime(), warnRecordAbnormal.getDateTime(),
                warnRecordAbnormal.getScanningFrequency(), warnRecordAbnormal.getScanningType(), warnRecordAbnormal.getSaveTime(), warnRecordAbnormal.getState()};
        return Data.Insert(sql, param);
    }

    /**
     * 获取不是某一些人成交的交易记录，用于刷量启用多个账号的监控
     */
    public void saveWarnLog(String market, BigDecimal userCount, BigDecimal hedgeCount, BigDecimal num, long startTime, long endTime) {
        WarnRecordAbnormal log = new WarnRecordAbnormal();
        log.setEntrustMarket(market);
        log.setUserNumbers(userCount);
        log.setHedgingNumbers(hedgeCount);
        log.setNumbers(num);
        log.setStartTime(startTime);
        log.setEndTime(endTime);

        Long now = System.currentTimeMillis();
        log.setDateTime(now);
        log.setSaveTime(now);
        log.setState(0);
        log.setScanningType(1);
        log.setScanningFrequency(30);
        insert(log);
    }

}
