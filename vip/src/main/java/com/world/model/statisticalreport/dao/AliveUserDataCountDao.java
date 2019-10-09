package com.world.model.statisticalreport.dao;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.statisticalReport.AliveUserDataCount;
import com.world.model.entity.statisticalReport.AliveUserDataCountVo;
import com.world.util.date.TimeUtil;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class AliveUserDataCountDao extends DataDaoSupport<AliveUserDataCount> {

    Logger logger = Logger.getLogger(AliveUserDataCountDao.class);


    public Boolean saveBillAllCount(AliveUserDataCount aliveUserDataCount, Timestamp beginTime, Timestamp endTime) {
        Boolean flag = false;
        try {
            List<OneSql> sqls = new ArrayList<OneSql>();
            sqls.add(new OneSql("DELETE FROM aliveuserdatacount where countDate >=? and countDate<=?", -2, new Object[]{TimeUtil.parseDate(beginTime.getTime()), TimeUtil.parseDate(endTime.getTime())}, "messi_ods"));
            sqls.add(getSql(aliveUserDataCount));
            flag = Data.doTransWithBatch(sqls);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return flag;
    }


    public List<AliveUserDataCountVo> getList(Date startTime, Date endTime) {
        List<AliveUserDataCountVo> aliveUserDataCountList = new ArrayList<AliveUserDataCountVo>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = TimeUtil.dayBegin(startTime);
        Date end = TimeUtil.dayEnd(endTime);
        try {
            StringBuilder where = new StringBuilder();
            where.append(" AND countDate >='" + sdf.format(begin) + "'");
            where.append(" AND countDate <'" + sdf.format(end) + "'");
            String w = where.toString();
            if (w.length() > 0) {
                w = " where " + w.substring(4);
            }
            String sql = "select * from aliveuserdatacount " + w + " ";
            aliveUserDataCountList = Data.QueryT("messi_ods",sql, new Object[]{}, AliveUserDataCountVo.class);
            for(AliveUserDataCountVo aliveUserCountVo:aliveUserDataCountList){
                aliveUserCountVo.setCountDate(TimeUtil.getSpecifiedDayBefore(aliveUserCountVo.getCountDate()));
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return aliveUserDataCountList;
    }


    public OneSql getSql(AliveUserDataCount aliveUserDataCount) {

        String sql = "insert into aliveuserdatacount(";
        String cloumn = "", conditions = "";
        List<Object> values = new LinkedList<Object>();
        cloumn += "countDate";
        conditions += "now()";

        if (aliveUserDataCount.getAllDepositUserCount() != null) {//手续费不为空
            cloumn += ",allDepositUserCount";
            conditions += ",?";
            values.add(aliveUserDataCount.getAllDepositUserCount());

        }
        if (aliveUserDataCount.getDepositUserCount() != null) {//手续费不为空
            cloumn += ",depositUserCount";
            conditions += ",?";
            values.add(aliveUserDataCount.getDepositUserCount());

        }
        if (aliveUserDataCount.getFirstDepositCount() != null) {//交易费不为空
            cloumn += ",firstDepositCount";
            conditions += ",?";
            values.add(aliveUserDataCount.getFirstDepositCount());

        }
        if (aliveUserDataCount.getCashInCount() != null) {//
            cloumn += ",cashInCount";
            conditions += ",?";
            values.add(aliveUserDataCount.getCashInCount());

        }
        if (aliveUserDataCount.getCashNullCount() != null) {//
            cloumn += ",cashNullCount";
            conditions += ",?";
            values.add(aliveUserDataCount.getCashNullCount());

        }
        if (aliveUserDataCount.getCashNullNoLoginCount() != null) {//
            cloumn += ",cashNullNoLoginCount";
            conditions += ",?";
            values.add(aliveUserDataCount.getCashNullNoLoginCount());
        }
        if (aliveUserDataCount.getTransactionCount() != null) {//
            cloumn += ",transactionCount";
            conditions += ",?";
            values.add(aliveUserDataCount.getTransactionCount());
        }
        if (aliveUserDataCount.getUserTransactionFeeUsdt() != null) {//
            cloumn += ",userTransactionFeeUsdt";
            conditions += ",?";
            values.add(aliveUserDataCount.getUserTransactionFeeUsdt());
        }
        if (aliveUserDataCount.getCompanyTransactionFeeUsdt() != null) {//
            cloumn += ",companyTransactionFeeUsdt";
            conditions += ",?";
            values.add(aliveUserDataCount.getCompanyTransactionFeeUsdt());
        }if (aliveUserDataCount.getUserTransactionFeeBtc() != null) {//
            cloumn += ",userTransactionFeeBtc";
            conditions += ",?";
            values.add(aliveUserDataCount.getUserTransactionFeeBtc());
        }if (aliveUserDataCount.getCompanyTransactionFeeBtc() != null) {//
            cloumn += ",companyTransactionFeeBtc";
            conditions += ",?";
            values.add(aliveUserDataCount.getCompanyTransactionFeeBtc());
        }if (aliveUserDataCount.getRechargeConversionRate() != null) {//
            cloumn += ",rechargeConversionRate";
            conditions += ",?";
            values.add(aliveUserDataCount.getRechargeConversionRate());
        }if (aliveUserDataCount.getChurnRate() != null) {//
            cloumn += ",churnRate";
            conditions += ",?";
            values.add(aliveUserDataCount.getChurnRate());
        }

        sql += cloumn + ") values (" + conditions + ")";
        log.info(sql);
        return new OneSql(sql, -1, values.toArray(), "messi_ods");


    }

}
