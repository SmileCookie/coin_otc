package com.world.model.statisticalreport.dao;

import com.alibaba.fastjson.JSON;
import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.record.TransRecord;
import com.world.model.entity.statisticalReport.BillAllCount;
import com.world.model.entity.statisticalReport.BillAllCountVo;
import com.world.model.entity.statisticalReport.BillCount;
import com.world.model.statisticalreport.StatisticalReportWork;
import com.world.util.DigitalUtil;
import com.world.util.date.TimeUtil;
import com.world.util.string.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.text.Position;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class BillCountDao extends DataDaoSupport<BillCount> {


    Logger logger = Logger.getLogger(BillCountDao.class);


    public Boolean saveBillCount(List<BillCount> billCountList, Timestamp beginTime, Timestamp endTime) {
        Boolean flag = false;
        try {
            List<OneSql> sqls = new ArrayList<OneSql>();
            sqls.add(new OneSql("DELETE FROM billCount where countDate >=? and countDate<=?", -2, new Object[]{TimeUtil.parseDate(beginTime.getTime()), TimeUtil.parseDate(endTime.getTime())}, "messi_ods"));
            for (BillCount billCount : billCountList) {
                sqls.add(getSql(billCount));
            }
            flag = Data.doTransWithBatch(sqls);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return flag;

    }


    public List<BillCount> getList(String fundsType, Date startTime, Date endTime) {
        List<BillCount> billCountList = new ArrayList<BillCount>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = TimeUtil.dayBegin(startTime);
        Date end = TimeUtil.dayEnd(endTime);
        try {
            StringBuilder where = new StringBuilder();
            if (!StringUtils.isBlank(fundsType)) {
                where.append(" AND coinType = " + Integer.parseInt(fundsType));
            }
            where.append(" AND countDate >='" + sdf.format(begin) + "'");
            where.append(" AND countDate <'" + sdf.format(end) + "'");
            String w = where.toString();
            if (w.length() > 0) {
                w = " where " + w.substring(4);
            }
            String sql = "select * from billcount " + w + " ";
            billCountList = Data.QueryT("messi_ods", sql, null, BillCount.class);

            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            for (BillCount billCount : billCountList) {
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


    public OneSql getSql(BillCount billCount) {

        String sql = "insert into billCount(";
        String cloumn = "", conditions = "";
        List<Object> values = new LinkedList<Object>();
        cloumn += "coinType,userType,countDate";
        conditions += "?,?,now()";
        values.add(billCount.getCoinType());
        values.add(billCount.getUserType());

        if (billCount.getUserDeposit() != null) {//手续费不为空
            cloumn += ",userdeposit";
            conditions += ",?";
            values.add(billCount.getUserDeposit());

        }
        if (billCount.getCompanyDeposit() != null) {//手续费不为空
            cloumn += ",companydeposit";
            conditions += ",?";
            values.add(billCount.getCompanyDeposit());

        }
        if (billCount.getUserCashIn() != null) {//交易费不为空
            cloumn += ",usercashIn";
            conditions += ",?";
            values.add(billCount.getUserCashIn());

        }
        if (billCount.getCompanyCashIn() != null) {//交易费不为空
            cloumn += ",companycashIn";
            conditions += ",?";
            values.add(billCount.getCompanyCashIn());

        }
        if (billCount.getTransactionFee() != null) {//交易费不为空
            cloumn += ",transactionFee";
            conditions += ",?";
            values.add(billCount.getTransactionFee());

        }
        if (billCount.getTransactionFeeUser() != null) {//交易费不为空
            cloumn += ",transactionFeeUser";
            conditions += ",?";
            values.add(billCount.getTransactionFeeUser());

        }
        if (billCount.getTransactionFeeCompany() != null) {//交易费不为空
            cloumn += ",transactionFeeCompany";
            conditions += ",?";
            values.add(billCount.getTransactionFeeCompany());

        }
        if (billCount.getCashInFee() != null) {//交易费不为空
            cloumn += ",cashInFee";
            conditions += ",?";
            values.add(billCount.getCashInFee());

        }

        sql += cloumn + ") values (" + conditions + ")";
        log.info(sql);
        return new OneSql(sql, -1, values.toArray(), "messi_ods");


    }


    /**
     * 统计累计用户资金
     *
     * @return
     */
    public List<BillAllCount> sumBillAllCount() {
        List<BillAllCount> billAllCountList = new ArrayList<>();
        try {
            String sql = "select coinType,sum(userdeposit) as userDeposit,sum(companydeposit) as companyDeposit,sum(usercashIn) as userCashIn,sum(companycashIn) as companyCashIn,sum(transactionFee) as transactionFee,sum(cashInFee) as cashInFee,sum(transactionFeeUser) as transactionFeeUser,sum(transactionFeeCompany) as transactionFeeCompany from billcount group by coinType ";
            billAllCountList = Data.QueryT("messi_ods", sql, null, BillAllCount.class);
            BigDecimal bigDecimal = new BigDecimal(0.0001);
            if (!CollectionUtils.isEmpty(billAllCountList)) {

                log.info("billAllCountList.data:" + JSON.toJSONString(billAllCountList));

                //查询公司账户余额=非用户 (币币)
                Map<Integer, BigDecimal> companyRetainedFeeMap = getCompanyRetainedFee();
                log.info("companyRetainedFeeBibiMap-----" + companyRetainedFeeMap);

                //查询用户账户余额(币币)
                Map<Integer, BigDecimal> userRetainedFeeMap = getUserRetainedFee();
                log.info("userRetainedFeeBibiMap-----" + userRetainedFeeMap);

                //查询公司账户余额=非用户(OTC)
                Map<Integer, BigDecimal> companyRetainedFeeOtcMap = getCompanyRetainedFeeOtc();
                log.info("companyRetainedFeeOtcMap-----" + companyRetainedFeeOtcMap);

                //查询用户账户余额(OTC)
                Map<Integer, BigDecimal> userRetainedFeeOtcMap = getUserRetainedFeeOtc();
                log.info("userRetainedFeeOtcMap-----" + userRetainedFeeOtcMap);

                //查询公司账户余额=非用户(钱包)
                Map<Integer, BigDecimal> companyRetainedFeeWalletMap = getCompanyRetainedFeeWallet();
                log.info("companyRetainedFeeWalletMap-----" + companyRetainedFeeWalletMap);

                //查询用户账户余额(钱包)
                Map<Integer, BigDecimal> userRetainedFeeWalletMap = getUserRetainedFeeWallet();
                log.info("userRetainedFeeWalletMap-----" + userRetainedFeeWalletMap);

                //获取持仓人数
                Map<Integer, Long> positionMap = getPositionCount(bigDecimal);

                for (BillAllCount billAllCount : billAllCountList) {

                    billAllCount.setPositionCount(positionMap.get(new Integer(billAllCount.getCoinType())));

                    /*币币资金留存*/
                    billAllCount.setUserRetainedFee(userRetainedFeeMap.get(new Integer(billAllCount.getCoinType())));
                    billAllCount.setCompanyRetainedFee(companyRetainedFeeMap.get(new Integer(billAllCount.getCoinType())));

                    /*OTC资金留存*/
                    billAllCount.setUserRetainedFeeOtc(userRetainedFeeOtcMap.get(new Integer(billAllCount.getCoinType())));
                    billAllCount.setCompanyRetainedFeeOtc(companyRetainedFeeOtcMap.get(new Integer(billAllCount.getCoinType())));

                    /*钱包资金留存*/
                    billAllCount.setUserRetainedFeeWallet(userRetainedFeeWalletMap.get(new Integer(billAllCount.getCoinType())));
                    billAllCount.setCompanyRetainedFeeWallet(companyRetainedFeeWalletMap.get(new Integer(billAllCount.getCoinType())));

                    /*总留存资金计算*/
                    BigDecimal userRetainedFee = billAllCount.getUserRetainedFee() == null ? BigDecimal.ZERO : billAllCount.getUserRetainedFee();
                    BigDecimal userRetainedFeeOtc = billAllCount.getUserRetainedFeeOtc() == null ? BigDecimal.ZERO : billAllCount.getUserRetainedFeeOtc();
                    BigDecimal userRetainedFeeWallet = billAllCount.getUserRetainedFeeWallet() == null ? BigDecimal.ZERO : billAllCount.getUserRetainedFeeWallet();

                    BigDecimal companyRetainedFee = billAllCount.getCompanyRetainedFee() == null ? BigDecimal.ZERO : billAllCount.getCompanyRetainedFee();
                    BigDecimal companyRetainedFeeOtc = billAllCount.getCompanyRetainedFeeOtc() == null ? BigDecimal.ZERO : billAllCount.getCompanyRetainedFeeOtc();
                    BigDecimal companyRetainedFeeWallet = billAllCount.getCompanyRetainedFeeWallet() == null ? BigDecimal.ZERO : billAllCount.getCompanyRetainedFeeWallet();

                    log.info("userRetainedFee.data:" + userRetainedFee);
                    log.info("userRetainedFeeOtc.data:" + userRetainedFeeOtc);
                    log.info("userRetainedFeeWallet.data:" + userRetainedFeeWallet);
                    log.info("companyRetainedFee.data:" + companyRetainedFee);
                    log.info("companyRetainedFeeOtc.data:" + companyRetainedFeeOtc);
                    log.info("companyRetainedFeeWallet.data:" + companyRetainedFeeWallet);

                    billAllCount.setUserRetainedFeeSum(userRetainedFee.add(userRetainedFeeOtc).add(userRetainedFeeWallet));
                    billAllCount.setCompanyRetainedFeeSum(companyRetainedFee.add(companyRetainedFeeOtc).add(companyRetainedFeeWallet));
                }
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return billAllCountList;
    }

    private Map<Integer, BigDecimal> getUserRetainedFeeWallet() {

        Map<Integer, BigDecimal> userRetainedFeeMap = new HashMap<>();
        List<BillAllCount> billAllCountList = new ArrayList<>();
        Map<Integer, BigDecimal> userMap = new HashMap<>();

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT ");
            sb.append("sum(balance) AS userRetainedFeeWallet , ");
            sb.append("fundsType AS coinType ");
            sb.append("FROM bill_wallet b left join ");
            sb.append("( ");
            sb.append("SELECT ");
            sb.append("max(b.id) id ");
            sb.append("FROM bill_wallet b left join userinfo u on b.userId = u.id ");
            sb.append("where u.utype = '01' ");
            sb.append("GROUP BY b.userid , b.fundsType ");
            sb.append(") a on b.id = a.id ");
            sb.append("where a.id is not null ");
            sb.append("GROUP BY fundsType ");

            String sql = sb.toString();
            log.info("BillCountDao.getUserRetainedFeeWallet.sql:" + sql);

            billAllCountList = Data.QueryT("messi_ods", sql, new Object[]{}, BillAllCount.class);

            for (BillAllCount billAllCount : billAllCountList) {
                userMap.put(billAllCount.getCoinType(), billAllCount.getUserRetainedFeeWallet());
            }

            log.info("Wallet用户资金留存-----" + userMap);

            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            BigDecimal bgCompanyAmount = new BigDecimal(0);

            for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                CoinProps coint = entry.getValue();
                bgCompanyAmount = BigDecimal.ZERO;
                if (userMap.containsKey(coint.getFundsType())) {
                    if (null != userMap.get(coint.getFundsType())) {
                        bgCompanyAmount = userMap.get(coint.getFundsType());
                    }
                }
                userRetainedFeeMap.put(coint.getFundsType(), bgCompanyAmount);
            }

            log.info("BillCountDao.getUserRetainedFeeWallet.查询结束");

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return userRetainedFeeMap;

    }


    private Map<Integer, BigDecimal> getCompanyRetainedFeeWallet() {

        Map<Integer, BigDecimal> companyRetainedFeeMap = new HashMap<>();
        Map<Integer, BigDecimal> companyMap = new HashMap<>();
        List<BillAllCount> billAllCountList = new ArrayList<>();

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT ");
            sb.append("sum(balance) AS companyRetainedFeeWallet , ");
            sb.append("fundsType AS coinType ");
            sb.append("FROM bill_wallet b left join ");
            sb.append("( ");
            sb.append("SELECT ");
            sb.append("max(b.id) id ");
            sb.append("FROM bill_wallet b left join userinfo u on b.userId = u.id ");
            sb.append("where u.utype != '01' ");
            sb.append("GROUP BY b.userid , b.fundsType ");
            sb.append(") a on b.id = a.id ");
            sb.append("where a.id is not null ");
            sb.append("GROUP BY fundsType ");


            String sql = sb.toString();
            log.info("BillCountDao.getCompanyRetainedFeeWallet.sql:" + sql);

            billAllCountList = Data.QueryT("messi_ods", sql, new Object[]{}, BillAllCount.class);
            for (BillAllCount billAllCount : billAllCountList) {
                companyMap.put(billAllCount.getCoinType(), billAllCount.getCompanyRetainedFeeWallet());
            }

            log.info("Wallet公司资金留存 -----" + companyMap);

            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            BigDecimal bgCompanyAmount = new BigDecimal(0);

            for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                CoinProps coint = entry.getValue();
                bgCompanyAmount = BigDecimal.ZERO;
                if (companyMap.containsKey(coint.getFundsType())) {
                    if (null != companyMap.get(coint.getFundsType())) {
                        bgCompanyAmount = companyMap.get(coint.getFundsType());
                    }
                }
                companyRetainedFeeMap.put(coint.getFundsType(), bgCompanyAmount);
            }

            log.info("BillCountDao.getCompanyRetainedFeeWallet.查询结束");

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return companyRetainedFeeMap;


    }

    private Map<Integer, BigDecimal> getUserRetainedFeeOtc() {

        Map<Integer, BigDecimal> userRetainedFeeMap = new HashMap<>();
        List<BillAllCount> billAllCountList = new ArrayList<>();
        Map<Integer, BigDecimal> userMap = new HashMap<>();

        try {

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT  ");
            sb.append("sum(currAmount) AS userRetainedFeeOtc ,  ");
            sb.append("coinTypeId AS coinTypeId,  ");
            sb.append("coinTypeId AS coinType  ");
            sb.append("FROM bill_otc  b left join  ");
            sb.append("(  ");
            sb.append("SELECT  ");
            sb.append("max(b.id) id  ");
            sb.append("FROM bill_otc b left join userinfo u on b.userId = u.id  ");
            sb.append("where u.utype = '01'  ");
            sb.append("GROUP BY b.userid , b.coinTypeId  ");
            sb.append(") a on b.id = a.id  ");
            sb.append("where a.id is not null  ");
            sb.append("GROUP BY coinTypeId  ");

            String sql = sb.toString();
            log.info("BillCountDao.getUserRetainedFeeOtc.sql:" + sql);

            billAllCountList = Data.QueryT("messi_ods", sql, new Object[]{}, BillAllCount.class);

            for (BillAllCount billAllCount : billAllCountList) {
                billAllCount.setCoinType(Integer.parseInt(String.valueOf(billAllCount.getCoinTypeId())));
                userMap.put(billAllCount.getCoinType(), billAllCount.getUserRetainedFeeOtc());
            }

            log.info("OTC用户资金留存-----" + userMap);

            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            BigDecimal bgCompanyAmount = new BigDecimal(0);

            for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                CoinProps coint = entry.getValue();
                bgCompanyAmount = BigDecimal.ZERO;
                if (userMap.containsKey(coint.getFundsType())) {
                    if (null != userMap.get(coint.getFundsType())) {
                        bgCompanyAmount = userMap.get(coint.getFundsType());
                    }
                }
                userRetainedFeeMap.put(coint.getFundsType(), bgCompanyAmount);
            }

            log.info("BillCountDao.getUserRetainedFeeOtc.查询结束");

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return userRetainedFeeMap;
    }

    private Map<Integer, BigDecimal> getCompanyRetainedFeeOtc() {

        Map<Integer, BigDecimal> companyRetainedFeeMap = new HashMap<>();
        Map<Integer, BigDecimal> companyMap = new HashMap<>();
        List<BillAllCount> billAllCountList = new ArrayList<>();

        try {

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT  ");
            sb.append("sum(currAmount) AS companyRetainedFeeOtc , ");
            sb.append("coinTypeId AS coinTypeId, ");
            sb.append("coinTypeId AS coinType ");
            sb.append("FROM bill_otc  b left join ");
            sb.append("( ");
            sb.append("SELECT ");
            sb.append("max(b.id) id ");
            sb.append("FROM bill_otc b left join userinfo u on b.userId = u.id ");
            sb.append("where u.utype != '01' ");
            sb.append("GROUP BY b.userid , b.coinTypeId ");
            sb.append(") a on b.id = a.id ");
            sb.append("where a.id is not null ");
            sb.append("GROUP BY coinTypeId");

            String sql = sb.toString();
            log.info("BillCountDao.getCompanyRetainedFeeOtc.sql:" + sql);

            billAllCountList = Data.QueryT("messi_ods", sql, new Object[]{}, BillAllCount.class);
            for (BillAllCount billAllCount : billAllCountList) {
                billAllCount.setCoinType(Integer.parseInt(String.valueOf(billAllCount.getCoinTypeId())));
                companyMap.put(billAllCount.getCoinType(), billAllCount.getCompanyRetainedFeeOtc());
            }

            log.info("OTC公司资金留存-----" + companyMap);

            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            BigDecimal bgCompanyAmount = new BigDecimal(0);

            for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                CoinProps coint = entry.getValue();
                bgCompanyAmount = BigDecimal.ZERO;
                if (companyMap.containsKey(coint.getFundsType())) {
                    if (null != companyMap.get(coint.getFundsType())) {
                        bgCompanyAmount = companyMap.get(coint.getFundsType());
                    }
                }
                companyRetainedFeeMap.put(coint.getFundsType(), bgCompanyAmount);
            }

            log.info("BillCountDao.getCompanyRetainedFeeOtc.查询结束");

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return companyRetainedFeeMap;
    }


    //获取回购转入账户的USDT消耗
    public BigDecimal getBackCapitalFeeIn() {
        BigDecimal amount = BigDecimal.ZERO;
        try {

            String sql = "SELECT sum(amount) as amount from bill where userID = 1000007 and fundsType =10 and type in (210) and sendTime <?";
            BillDetails billDetails = (BillDetails) Data.GetOne("messi_ods", sql, new Object[]{TimeUtil.parseDate(TimeUtil.dayBegin(new Date()).getTime())}, BillDetails.class);
            if (null != billDetails) {
                amount = billDetails.getAmount();
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return amount;
    }


    //获取回购转处账户的USDT消耗
    public BigDecimal getBackCapitalFeeOut() {
        BigDecimal amount = BigDecimal.ZERO;
        try {

            String sql = "SELECT sum(amount) as amount from bill where userID = 1000007 and fundsType =10 and type in (211) and sendTime <?";
            BillDetails billDetails = (BillDetails) Data.GetOne("messi_ods", sql, new Object[]{TimeUtil.parseDate(TimeUtil.dayBegin(new Date()).getTime())}, BillDetails.class);
            if (null != billDetails) {
                amount = billDetails.getAmount();
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return amount;
    }


    //获取回购账户的USDT余额
    public BigDecimal getBackCapitalBalance() {
        BigDecimal amount = BigDecimal.ZERO;
        try {
            String sql = "SELECT * from bill where userId = 1000007 and fundsType =10 and sendTime <? order by id desc limit 1";
            BillDetails billDetails = (BillDetails) Data.GetOne("messi_ods", sql, new Object[]{TimeUtil.parseDate(TimeUtil.dayBegin(new Date()).getTime())}, BillDetails.class);
            if (null != billDetails) {
                amount = billDetails.getBalance();
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return amount;
    }


    //获取回购与用户成交的GBC
    public BigDecimal getBackCapitalUserGbc() {
        BigDecimal amount = BigDecimal.ZERO;
        try {
            TimeUtil.dayBegin(new Date()).getTime();
            String sql = "SELECT sum(a.numbers) as numbers  FROM( SELECT * FROM transrecord WHERE userIdBuy = 1000007 AND userIdSell NOT IN( 1000377 , 1000386 , 1000039 , 1000136 , 1000055 , 1000144 , 1000232 , 1000263 , 1000084 , 1000093 , 1000097 , 1000107 , 1000116 , 1000258 , 1000038 , 1000007 , 1002781 , 1002850 , 1002795 , 1000028 , 1000095 , 1000061 , 1000002) UNION ALL SELECT * FROM transrecord_all WHERE userIdBuy = 1000007 AND userIdSell NOT IN( 1000377 , 1000386 , 1000039 , 1000136 , 1000055 , 1000144 , 1000232 , 1000263 , 1000084 , 1000093 , 1000097 , 1000107 , 1000116 , 1000258 , 1000038 , 1000007 , 1002781 , 1002850 , 1002795 , 1000028 , 1000095 , 1000061 , 1000002)) a where a.times <? ";
            TransRecord transRecord = (TransRecord) Data.GetOne("gbcusdTentrust", sql, new Object[]{TimeUtil.dayBegin(new Date()).getTime()}, TransRecord.class);
            if (null != transRecord) {
                amount = transRecord.getNumbers();
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return amount;
    }


    //获取回购与用户消耗的USDT
    public BigDecimal getBackCapitalUserUsdt() {
        BigDecimal amount = BigDecimal.ZERO;
        try {
            String sql = "SELECT sum(a.totalPrice) as totalPrice  FROM( SELECT * FROM transrecord WHERE userIdBuy = 1000007 AND userIdSell NOT IN( 1000377 , 1000386 , 1000039 , 1000136 , 1000055 , 1000144 , 1000232 , 1000263 , 1000084 , 1000093 , 1000097 , 1000107 , 1000116 , 1000258 , 1000038 , 1000007 , 1002781 , 1002850 , 1002795 , 1000028 , 1000095 , 1000061 , 1000002) UNION ALL SELECT * FROM transrecord_all WHERE userIdBuy = 1000007 AND userIdSell NOT IN( 1000377 , 1000386 , 1000039 , 1000136 , 1000055 , 1000144 , 1000232 , 1000263 , 1000084 , 1000093 , 1000097 , 1000107 , 1000116 , 1000258 , 1000038 , 1000007 , 1002781 , 1002850 , 1002795 , 1000028 , 1000095 , 1000061 , 1000002)) a where a.times <?";
            TransRecord transRecord = (TransRecord) Data.GetOne("gbcusdtentrust", sql, new Object[]{TimeUtil.dayBegin(new Date()).getTime()}, TransRecord.class);
            if (null != transRecord) {
                amount = transRecord.getTotalPrice();
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return amount;
    }


    //获取回购与公司成交的GBC
    public BigDecimal getBackCapitalCompanyGbc() {
        BigDecimal amount = BigDecimal.ZERO;
        try {
            String sql = "SELECT sum(a.numbers) as numbers  FROM( SELECT * FROM transrecord WHERE userIdBuy = 1000007 AND userIdSell IN( 1000377 , 1000386 , 1000039 , 1000136 , 1000055 , 1000144 , 1000232 , 1000263 , 1000084 , 1000093 , 1000097 , 1000107 , 1000116 , 1000258 , 1000038 , 1000007 , 1002781 , 1002850 , 1002795 , 1000028 , 1000095 , 1000061 , 1000002) UNION ALL SELECT * FROM transrecord_all WHERE userIdBuy = 1000007 AND userIdSell IN( 1000377 , 1000386 , 1000039 , 1000136 , 1000055 , 1000144 , 1000232 , 1000263 , 1000084 , 1000093 , 1000097 , 1000107 , 1000116 , 1000258 , 1000038 , 1000007 , 1002781 , 1002850 , 1002795 , 1000028 , 1000095 , 1000061 , 1000002)) a where a.times <?";
            TransRecord transRecord = (TransRecord) Data.GetOne("gbcusdtentrust", sql, new Object[]{TimeUtil.dayBegin(new Date()).getTime()}, TransRecord.class);
            if (null != transRecord) {
                amount = transRecord.getNumbers();
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return amount;
    }

    //获取回购与公司消耗的USDT
    public BigDecimal getBackCapitalCompanyUsdt() {
        BigDecimal amount = BigDecimal.ZERO;
        try {
            String sql = "SELECT sum(a.totalPrice) as totalPrice  FROM( SELECT * FROM transrecord WHERE userIdBuy = 1000007 AND userIdSell IN( 1000377 , 1000386 , 1000039 , 1000136 , 1000055 , 1000144 , 1000232 , 1000263 , 1000084 , 1000093 , 1000097 , 1000107 , 1000116 , 1000258 , 1000038 , 1000007 , 1002781 , 1002850 , 1002795 , 1000028 , 1000095 , 1000061 , 1000002) UNION ALL SELECT * FROM transrecord_all WHERE userIdBuy = 1000007 AND userIdSell IN( 1000377 , 1000386 , 1000039 , 1000136 , 1000055 , 1000144 , 1000232 , 1000263 , 1000084 , 1000093 , 1000097 , 1000107 , 1000116 , 1000258 , 1000038 , 1000007 , 1002781 , 1002850 , 1002795 , 1000028 , 1000095 , 1000061 , 1000002)) a where a.times <?";
            TransRecord transRecord = (TransRecord) Data.GetOne("gbcusdtentrust", sql, new Object[]{TimeUtil.dayBegin(new Date()).getTime()}, TransRecord.class);
            if (null != transRecord) {
                amount = transRecord.getTotalPrice();
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return amount;
    }


    /**
     * 计算用户账面留存金额
     *
     * @return
     */
    public Map<Integer, BigDecimal> getUserRetainedFee() {
        Map<Integer, BigDecimal> userRetainedFeeMap = new HashMap<>();
        List<BillAllCount> billAllCountList = new ArrayList<>();
        Map<Integer, BigDecimal> userMap = new HashMap<>();
        try {

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT ");
            sb.append("sum(balance) AS userretainedFee , ");
            sb.append("fundsType AS coinType ");
            sb.append("FROM bill b left join ");
            sb.append("( ");
            sb.append("SELECT ");
            sb.append("max(b.id) id ");
            sb.append("FROM bill b left join userinfo u on b.userId = u.id ");
            sb.append("where u.utype = '01' ");
            sb.append("GROUP BY b.userid , b.fundsType ");
            sb.append(") a on b.id = a.id ");
            sb.append("where a.id is not null ");
            sb.append("GROUP BY fundsType ");

            String sql = sb.toString();
            log.info("BillCountDao.getUserRetainedFee.sql:" + sql);

            billAllCountList = Data.QueryT("messi_ods", sql, new Object[]{}, BillAllCount.class);

            for (BillAllCount billAllCount : billAllCountList) {
                userMap.put(billAllCount.getCoinType(), billAllCount.getUserRetainedFee());
            }

            log.info("币币用户资金留存-----" + userMap);

            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            BigDecimal bgCompanyAmount = new BigDecimal(0);

            for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                CoinProps coint = entry.getValue();
                bgCompanyAmount = BigDecimal.ZERO;
                if (userMap.containsKey(coint.getFundsType())) {
                    if (null != userMap.get(coint.getFundsType())) {
                        bgCompanyAmount = userMap.get(coint.getFundsType());
                    }
                }
                userRetainedFeeMap.put(coint.getFundsType(), bgCompanyAmount);
            }

            log.info("BillCountDao.getUserRetainedFee.查询结束");

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return userRetainedFeeMap;
    }

    /**
     * 计算公司账面留存金额
     *
     * @return
     */
    public Map<Integer, BigDecimal> getCompanyRetainedFee() {
        Map<Integer, BigDecimal> companyRetainedFeeMap = new HashMap<>();
        List<BillAllCount> billAllCountList = new ArrayList<>();
        Map<Integer, BigDecimal> companyMap = new HashMap<>();

        try {

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT ");
            sb.append("sum(balance) AS companyRetainedFee, ");
            sb.append("fundsType AS coinType ");
            sb.append("FROM bill b left join ");
            sb.append("( ");
            sb.append("SELECT ");
            sb.append("max(b.id) id ");
            sb.append("FROM bill b left join userinfo u on b.userId = u.id ");
            sb.append("where u.utype != '01' ");
            sb.append("GROUP BY b.userid , b.fundsType ");
            sb.append(") a on b.id = a.id ");
            sb.append("where a.id is not null ");
            sb.append("GROUP BY fundsType ");

            String sql = sb.toString();
            log.info("BillCountDao.getCompanyRetainedFee.sql:" + sql);

            billAllCountList = Data.QueryT("messi_ods", sql, new Object[]{}, BillAllCount.class);

            for (BillAllCount billAllCount : billAllCountList) {
                companyMap.put(billAllCount.getCoinType(), billAllCount.getCompanyRetainedFee());
            }

            log.info("币币公司资金留存-----" + companyMap);

            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            BigDecimal bgCompanyAmount = new BigDecimal(0);
            for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                CoinProps coint = entry.getValue();
                bgCompanyAmount = BigDecimal.ZERO;
                if (companyMap.containsKey(coint.getFundsType())) {
                    if (null != companyMap.get(coint.getFundsType())) {
                        bgCompanyAmount = companyMap.get(coint.getFundsType());
                    }
                }
                companyRetainedFeeMap.put(coint.getFundsType(), bgCompanyAmount);
            }

            log.info("BillCountDao.getCompanyRetainedFee.查询结束");

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return companyRetainedFeeMap;
    }


    public Map<Integer, Long> getPositionCount(BigDecimal bigDecimal) {
        Map<Integer, Long> positionCountMap = new HashMap<>();
        List<BillAllCount> billAllCountList = new ArrayList<>();
        try {
            String sql = "SELECT count(b.userId) as positionCount , b.fundsType as coinType  FROM( SELECT balance , fundsType , userId FROM bill WHERE id IN( SELECT max(id) id FROM bill GROUP BY userid , fundsType) AND userid IN( SELECT id FROM userinfo WHERE utype = '01')) b WHERE balance > ? GROUP BY fundsType";
            billAllCountList = Data.QueryT("messi_ods", sql, new Object[]{bigDecimal}, BillAllCount.class);
            if (!CollectionUtils.isEmpty(billAllCountList)) {
                for (BillAllCount billAllCount : billAllCountList) {
                    positionCountMap.put(billAllCount.getCoinType(), billAllCount.getPositionCount());
                }

            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }


        return positionCountMap;
    }


    public static void main(String[] args) {

        StatisticalReportWork statisticalReportWork = new StatisticalReportWork("", "");
        statisticalReportWork.run();
    }

}
