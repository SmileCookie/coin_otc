package com.world.model.statisticalreport.dao;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.statisticalReport.BillAllCount;
import com.world.model.entity.statisticalReport.BillAllCount;
import com.world.model.entity.statisticalReport.BillAllCountVo;
import com.world.util.date.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class BillAllCountDao extends DataDaoSupport<BillAllCount> {


    Logger logger = Logger.getLogger(BillAllCountDao.class);


    public Boolean saveBillAllCount(List<BillAllCount> billCountList, Timestamp beginTime, Timestamp endTime) {
        Boolean flag =false;
        try {
            List<OneSql> sqls = new ArrayList<OneSql>();
            sqls.add(new OneSql("DELETE FROM billallcount where countDate >=? and countDate<=?", -2, new Object[]{TimeUtil.parseDate(beginTime.getTime()), TimeUtil.parseDate(endTime.getTime())}, "messi_ods"));
            for (BillAllCount billCount : billCountList) {
                sqls.add(getSql(billCount));
            }
            flag = Data.doTransWithBatch(sqls);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return flag;

    }


    public List<BillAllCountVo> getList(String fundsType, Date startTime, Date endTime) {
        List<BillAllCountVo> billCountList = new ArrayList<BillAllCountVo>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = TimeUtil.dayBegin(startTime);
        Date end = TimeUtil.dayEnd(endTime);
        try {
            StringBuilder where = new StringBuilder();
            if (!StringUtils.isBlank(fundsType)) {
                where.append(" AND coinType = " + fundsType);
            }
            where.append(" AND countDate >='" + sdf.format(begin) + "'");
            where.append(" AND countDate <'" + sdf.format(end) + "'");
            String w = where.toString();
            if (w.length() > 0) {
                w = " where " + w.substring(4);
            }
            String sql = "select * from billallcount " + w + " order by id";
            Query query = getQuery();
            query.setDatabase("messi_ods");
            query.setSql(sql);
            query.setCls(BillAllCountVo.class);
            billCountList = query.getList();
            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            for (BillAllCountVo billCount : billCountList) {
                billCount.setCountDate(TimeUtil.getSpecifiedDayBefore(billCount.getCountDate()));
                for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                    CoinProps coint = entry.getValue();
                    if (billCount.getCoinType() == coint.getFundsType()) {
                        billCount.setCoinName(coint.getPropTag());
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return billCountList;
    }


    public OneSql getSql(BillAllCount billCount) {

        String sql = "insert into billallCount(";
        String cloumn = "", conditions = "";
        List<Object> values = new LinkedList<Object>();
        cloumn += "coinType,userType,countDate";
        conditions += "?,?,now()";
        values.add(billCount.getCoinType());
        values.add(billCount.getUserType());

        if (billCount.getUserDeposit() != null) {//
            cloumn += ",userdeposit";
            conditions += ",?";
            values.add(billCount.getUserDeposit());

        }
        if (billCount.getCompanyDeposit() != null) {//
            cloumn += ",companydeposit";
            conditions += ",?";
            values.add(billCount.getCompanyDeposit());

        }
        if (billCount.getUserCashIn() != null) {//
            cloumn += ",usercashIn";
            conditions += ",?";
            values.add(billCount.getUserCashIn());

        }
        if (billCount.getCompanyCashIn() != null) {//
            cloumn += ",companycashIn";
            conditions += ",?";
            values.add(billCount.getCompanyCashIn());

        }
        if (billCount.getTransactionFeeUser() != null) {//
            cloumn += ",transactionFeeUser";
            conditions += ",?";
            values.add(billCount.getTransactionFeeUser());

        }
        if (billCount.getTransactionFeeCompany() != null) {//
            cloumn += ",transactionFeeCompany";
            conditions += ",?";
            values.add(billCount.getTransactionFeeCompany());

        }
        if (billCount.getTransactionFee() != null) {//
            cloumn += ",transactionFee";
            conditions += ",?";
            values.add(billCount.getTransactionFee());

        }
        if (billCount.getCashInFee() != null) {//
            cloumn += ",cashInFee";
            conditions += ",?";
            values.add(billCount.getCashInFee());
        }
        if (billCount.getUserRetainedFee() != null) {//
            cloumn += ",userretainedFee";
            conditions += ",?";
            values.add(billCount.getUserRetainedFee());
        }
        if (billCount.getCompanyRetainedFee() != null) {//
            cloumn += ",companyretainedFee";
            conditions += ",?";
            values.add(billCount.getCompanyRetainedFee());
        }
        if (billCount.getUserRetainedFeeOtc() != null) {//
            cloumn += ",userretainedFeeOtc";
            conditions += ",?";
            values.add(billCount.getUserRetainedFeeOtc());
        }
        if (billCount.getCompanyRetainedFeeOtc() != null) {//
            cloumn += ",companyretainedFeeOtc";
            conditions += ",?";
            values.add(billCount.getCompanyRetainedFeeOtc());
        }
        if (billCount.getUserRetainedFeeWallet() != null) {//
            cloumn += ",userretainedFeeWallet";
            conditions += ",?";
            values.add(billCount.getUserRetainedFeeWallet());
        }
        if (billCount.getCompanyRetainedFeeWallet() != null) {//
            cloumn += ",companyretainedFeeWallet";
            conditions += ",?";
            values.add(billCount.getCompanyRetainedFeeWallet());
        }
        if (billCount.getUserRetainedFeeSum() != null) {//
            cloumn += ",userRetainedFeeSum";
            conditions += ",?";
            values.add(billCount.getUserRetainedFeeSum());
        }
        if (billCount.getCompanyRetainedFeeSum() != null) {//
            cloumn += ",companyRetainedFeeSum";
            conditions += ",?";
            values.add(billCount.getCompanyRetainedFeeSum());
        }
        if (billCount.getPositionCount() != null) {//
            cloumn += ",positionCount";
            conditions += ",?";
            values.add(billCount.getPositionCount());
        }

        if (billCount.getBackCapitalUserGbc() != null) {//
            cloumn += ",backCapitalUserGbc";
            conditions += ",?";
            values.add(billCount.getBackCapitalUserGbc());
        }
        if (billCount.getBackCapitalUserUsdt() != null) {//
            cloumn += ",backCapitalUserUsdt";
            conditions += ",?";
            values.add(billCount.getBackCapitalUserUsdt());
        }
        if (billCount.getBackCapitalCompanyGbc() != null) {//
            cloumn += ",backCapitalCompanyGbc";
            conditions += ",?";
            values.add(billCount.getBackCapitalCompanyGbc());
        }
        if (billCount.getBackCapitalCompanyUsdt() != null) {//
            cloumn += ",backCapitalCompanyUsdt";
            conditions += ",?";
            values.add(billCount.getBackCapitalCompanyUsdt());
        }



        sql += cloumn + ") values (" + conditions + ")";
        log.info(sql);
        return new OneSql(sql, -1, values.toArray(), "messi_ods");


    }
}
