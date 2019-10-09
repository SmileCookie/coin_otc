package com.world.model.dao.bill;

import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.statisticalReport.AliveUserDataCount;
import com.world.model.entity.statisticalReport.BillCount;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BillDetailDao extends DataDaoSupport<BillDetails> {
    Logger logger = Logger.getLogger(BillDetailDao.class);

    /**
     * 统计用户资金
     *
     * @param lastUpdateTime
     * @param tsTodayTime
     * @return
     */
    public List<BillCount> sumBillCount(Timestamp lastUpdateTime, Timestamp tsTodayTime) {
        List<BillCount> billCountList = new ArrayList<BillCount>();
        try {
            List<BillDetails> billDetails = new ArrayList<>();
            String sql = "select count(*) as id from bill where sendTime >=? and sendTime < ?";
            logger.info(sql.toString() + TimeUtil.parseDate(lastUpdateTime.getTime()) + "到" + TimeUtil.parseDate(tsTodayTime.getTime()));
            billDetails = (List<BillDetails>) Data.QueryT("messi_ods", sql, new Object[]{TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime())}, BillDetails.class);

            if (!CollectionUtils.isEmpty(billDetails) && billDetails.get(0).getId() > 0) {

                //1 根据配置文件放入币种类型(billCountList.size() = 币种个数)
                Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
                for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                    BillCount billCount = new BillCount();
                    CoinProps coint = entry.getValue();
                    billCount.setCoinType(coint.getFundsType());
                    billCountList.add(billCount);
                }

                //userDeposit
                //2 查询bill表中的用户(utype==01)充值(type=1)金额
                List<BillCount> billUserCl = sumDeposit(1, Const.CUSTOMER_TYPE_NORMAL, lastUpdateTime, tsTodayTime);
                for (BillCount billCount : billCountList) {
                    for (BillCount b : billUserCl) {
                        if (billCount.getCoinType() == b.getCoinType()) {
                            billCount.setUserDeposit(b.getDeposit());
                            billCount.setUserType(Const.CUSTOMER_TYPE_NORMAL);
                        }
                    }
                }

                //companyDeposit
                //3 查询bill表中公司(utype = !01)充值(type=1)金额
                List<BillCount> billCompanyCl = sumDeposit(1, "!01", lastUpdateTime, tsTodayTime);
                for (BillCount billCount : billCountList) {
                    for (BillCount b : billCompanyCl) {
                        if (billCount.getCoinType() == b.getCoinType()) {
                            billCount.setCompanyDeposit(b.getDeposit());
                            billCount.setUserType(b.getUserType());
                        }
                    }
                }

                //userCashIn
                //4 查询bill表中的用户(utype==01)提现(type=2)金额
                List<BillCount> billUserCash = sumDeposit(2, Const.CUSTOMER_TYPE_NORMAL, lastUpdateTime, tsTodayTime);
                for (BillCount billCount : billCountList) {
                    for (BillCount b : billUserCash) {
                        if (billCount.getCoinType() == b.getCoinType()) {
                            billCount.setUserCashIn(b.getDeposit());
                        }

                    }
                }

                //companyCashIn
                //5 查询bill表中公司(utype = !01)提现(type=2)金额
                List<BillCount> billCompanyCash = sumDeposit(2, "!01", lastUpdateTime, tsTodayTime);
                for (BillCount billCount : billCountList) {
                    for (BillCount b : billCompanyCash) {
                        if (billCount.getCoinType() == b.getCoinType()) {
                            billCount.setCompanyCashIn(b.getDeposit());
                        }
                    }
                }

                //transactionFee
                //6 查询交易手续费(b.type in (20,31))
                List<BillCount> billFee = sumServiceCharge(1, lastUpdateTime, tsTodayTime);
                for (BillCount billCount : billCountList) {
                    for (BillCount b : billFee) {
                        if (billCount.getCoinType() == b.getCoinType()) {
                            billCount.setTransactionFee(b.getTransactionFee());
                        }
                    }
                }

                //cashInFee
                //7 查询提现手续费(b.type=2)
                List<BillCount> billFeeCash = sumServiceCharge(2, lastUpdateTime, tsTodayTime);
                for (BillCount billCount : billCountList) {
                    for (BillCount b : billFeeCash) {
                        if (billCount.getCoinType() == b.getCoinType()) {
                            billCount.setCashInFee(b.getTransactionFee());
                        }
                    }
                }

                //transactionFeeUser
                //8 查询用户交易手续费
                List<BillCount> userFee = sumTransationFee(1, lastUpdateTime, tsTodayTime);
                for (BillCount billCount : billCountList) {
                    for (BillCount b : userFee) {
                        if (billCount.getCoinType() == b.getCoinType()) {
                            billCount.setTransactionFeeUser(b.getTransactionFeeUser());
                        }

                    }
                }

                //transactionFeeCompany
                //9 查询企业交易手续费
                List<BillCount> companyFee = sumTransationFee(2, lastUpdateTime, tsTodayTime);
                for (BillCount billCount : billCountList) {
                    for (BillCount b : companyFee) {
                        if (billCount.getCoinType() == b.getCoinType()) {
                            billCount.setTransactionFeeCompany(b.getTransactionFeeCompany());
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return billCountList;
    }

    /**
     * 统计充值／提现金额
     *
     * @param type      资金流水类型-1充值 2提现
     * @param utype     用户类型 01 用户 !01 企业
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return
     */
    public List<BillCount> sumDeposit(int type, String utype, Timestamp beginTime, Timestamp endTime) {
        List<BillCount> billCountList = new ArrayList<BillCount>();
        try {
            String sql = "";
            if (utype.equals("01")) {
                sql = "select fundsType as coinType,sum(amount) as deposit from bill_wallet b left join userinfo u on b.userId=u.id where b.type=? and u.utype=" + utype + " and sendTime>=? and sendTime<? GROUP BY fundsType";
            } else if (utype.equals("!01")) {
                utype = utype.replace("!", "");
                sql = "select fundsType as coinType,sum(amount) as deposit from bill_wallet b left join userinfo u on b.userId=u.id where b.type=? and u.utype !=" + utype + " and sendTime>=? and sendTime<? GROUP BY fundsType";
            }
            log.info("BillDetailDao.sumDeposit." + "时间:" + beginTime + "---" + endTime + ".类型:" + type + ".sql:" + sql);
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{type, TimeUtil.parseDate(beginTime.getTime()), TimeUtil.parseDate(endTime.getTime())}, BillCount.class);
            for (Bean b : list) {
                BillCount billCount = (BillCount) b;
                billCountList.add(billCount);
            }
            log.info("BillDetailDao.sumDeposit.查询结束");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return billCountList;
    }

    /**
     * 交易／提现 手续费
     *
     * @param type 1。交易  2。提现
     * @return
     */
    public List<BillCount> sumServiceCharge(int type, Timestamp beginTime, Timestamp endTime) {
        List<BillCount> billCountList = new ArrayList<BillCount>();
        try {
            String sql = "";
            if (type == 1) {  //交易
                sql = "select fundstype as coinType,IFNULL(sum(fees) , 0) as transactionFee from  bill where type in (20,31) and sendTime>=? and sendTime<? group by fundstype";
            } else if (type == 2) {  //提现
                sql = "select fundstype as coinType,IFNULL(sum(fees) , 0) as transactionFee from  bill_wallet where type =2 and sendTime>=? and sendTime<? group by fundstype";
            }
            log.info("BillDetailDao.sumServiceCharge." + "时间:" + beginTime + "---" + endTime + ".类型:" + type + ".sql:" + sql);
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(beginTime.getTime()), TimeUtil.parseDate(endTime.getTime())}, BillCount.class);
            for (Bean b : list) {
                BillCount billCount = (BillCount) b;
                billCountList.add(billCount);
            }
            log.info("BillDetailDao.sumServiceCharge.查询结束");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return billCountList;
    }


    /**
     * 交易手续费
     *
     * @param type 1。用户  2。企业
     * @return
     */
    public List<BillCount> sumTransationFee(int type, Timestamp beginTime, Timestamp endTime) {
        List<BillCount> billCountList = new ArrayList<BillCount>();
        try {
            String sql = "";
            if (type == 1) {  //用户

                sql = "select  " +
                        "fundstype as coinType," +
                        "IFNULL(sum(fees) , 0)  as transactionFeeUser " +
                        "from  bill b left join userinfo u on b.userId=u.id " +
                        "where type in (20,31) " +
                        "and u.uType='01' " +
                        "and sendTime>=?  and sendTime<? " +
                        "group by fundstype";

            } else if (type == 2) {  //企业

                sql = "select  " +
                        "fundstype as coinType," +
                        "IFNULL(sum(fees) , 0)  as transactionFeeCompany  " +
                        "from  bill b left join userinfo u on b.userId=u.id " +
                        "where type in (20,31)  " +
                        "and u.uType!='01' " +
                        "and sendTime>=? and sendTime<? " +
                        "group by fundstype";

            }
            log.info("BillDetailDao.sumTransationFee." + "时间:" + beginTime + "---" + endTime + ".类型:" + type + ".sql:" + sql);
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(beginTime.getTime()), TimeUtil.parseDate(endTime.getTime())}, BillCount.class);
            for (Bean b : list) {
                BillCount billCount = (BillCount) b;
                billCountList.add(billCount);
            }
            log.info("BillDetailDao.sumTransationFee.查询结束");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return billCountList;
    }


    public AliveUserDataCount sumAliveUserData(Timestamp lastUpdateTime, Timestamp tsTodayTime) {
        AliveUserDataCount aliveUserDataCount = new AliveUserDataCount();
        List<BillDetails> billDetails = new ArrayList<>();
        try {
            String sql = "select count(*) as id from bill where sendTime >=? and sendTime < ?";
            billDetails = (List<BillDetails>) Data.QueryT("messi_ods", sql, new Object[]{TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime())}, BillDetails.class);
            if (!CollectionUtils.isEmpty(billDetails) && billDetails.get(0).getId() > 0) {
                //获取充值用户总数
                AliveUserDataCount allDepositUserCount = getAllDepositUserCount(null, null, 1);
                if (allDepositUserCount != null) {
                    aliveUserDataCount.setAllDepositUserCount(allDepositUserCount.getAllDepositUserCount());
                }
                //获取当日充值用户总数
                AliveUserDataCount depositUserCount = getAllDepositUserCount(lastUpdateTime, tsTodayTime, 1);
                if (depositUserCount != null) {
                    aliveUserDataCount.setDepositUserCount(depositUserCount.getAllDepositUserCount());
                }
                //当日首充人数
                AliveUserDataCount firstDepositCount = getFirstDepositCount(lastUpdateTime, tsTodayTime);
                if (firstDepositCount != null) {
                    aliveUserDataCount.setFirstDepositCount(firstDepositCount.getFirstDepositCount());
                }
                //当日提现人数
                AliveUserDataCount cashInCount = getAllDepositUserCount(lastUpdateTime, tsTodayTime, 2);
                if (cashInCount != null) {
                    aliveUserDataCount.setCashInCount(cashInCount.getAllDepositUserCount());
                }
                //当日提空人数
                AliveUserDataCount cashNullCount = getCashNullCount(lastUpdateTime, tsTodayTime);
                if (cashNullCount != null) {
                    aliveUserDataCount.setCashNullCount(cashNullCount.getCashNullCount());
                }
                //提空且一周内未登陆用户
                AliveUserDataCount cashNullNoLoginCount = getCashNullNoLoginCount();
                if (cashNullNoLoginCount != null) {
                    aliveUserDataCount.setCashNullNoLoginCount(cashNullNoLoginCount.getCashNullNoLoginCount());
                }
                //当日用户交易人数
                AliveUserDataCount transactionCount = getTransactionCount(lastUpdateTime, tsTodayTime);
                if (transactionCount != null) {
                    aliveUserDataCount.setTransactionCount(transactionCount.getTransactionCount());

                }
                //当日纯用户交易量BTC
                AliveUserDataCount transactionUserFeeBtc = getUserTransactionFee(lastUpdateTime, tsTodayTime, "BTC");
                aliveUserDataCount.setUserTransactionFeeBtc(transactionUserFeeBtc.getTransactionFee());

                //当日纯用户交易量USDT
                AliveUserDataCount transactionUserFeeUsdt = getUserTransactionFee(lastUpdateTime, tsTodayTime, "USDT");
                aliveUserDataCount.setUserTransactionFeeUsdt(transactionUserFeeUsdt.getTransactionFee());

                //当日纯公司交易量BTC
                AliveUserDataCount transactionCompanyFeeBtc = getCompanyTransactionFee(lastUpdateTime, tsTodayTime, "BTC");
                aliveUserDataCount.setCompanyTransactionFeeBtc(transactionCompanyFeeBtc.getTransactionFee());
                //当日纯公司交易量USDT
                AliveUserDataCount transactionCompanyFeeUsdt = getCompanyTransactionFee(lastUpdateTime, tsTodayTime, "USDT");
                aliveUserDataCount.setCompanyTransactionFeeUsdt(transactionCompanyFeeUsdt.getTransactionFee());


                //充值转化率
                Long allRegisterCount = getRegisterCount();
                if (allRegisterCount != 0) {
                    double f1 = new BigDecimal((float) aliveUserDataCount.getAllDepositUserCount() / allRegisterCount.intValue()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double f2 = (float) (f1 * 100);
                    BigDecimal f = new BigDecimal(f2);
                    double f3 = f.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    DecimalFormat df = new DecimalFormat("0.00");
                    aliveUserDataCount.setRechargeConversionRate(df.format(f3));
                } else {
                    aliveUserDataCount.setRechargeConversionRate("0.00");
                }

                //用户流失率
                if (0 != aliveUserDataCount.getAllDepositUserCount()) {
                    double f1 = new BigDecimal((float) aliveUserDataCount.getCashNullNoLoginCount() / aliveUserDataCount.getAllDepositUserCount()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double f2 = (float) (f1 * 100);
                    BigDecimal f = new BigDecimal(f2);
                    double f3 = f.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    DecimalFormat df = new DecimalFormat("0.00");
                    aliveUserDataCount.setChurnRate(df.format(f3));
                } else {
                    aliveUserDataCount.setChurnRate("0.00");
                }
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }


        return aliveUserDataCount;
    }


    public AliveUserDataCount getAllDepositUserCount(Timestamp lastUpdateTime, Timestamp tsTodayTime, int type) {
        AliveUserDataCount aliveUserDataCount = null;
        try {
            String sql = "SELECT COUNT(DISTINCT userId) as allDepositUserCount FROM bill_wallet b LEFT JOIN userinfo u on b.userId=u.id where b.type = ? and u.utype = ?";
            StringBuilder where = new StringBuilder();
            if (lastUpdateTime != null) {
                where.append(" and sendTime>='" + TimeUtil.parseDate(lastUpdateTime.getTime()) + "'");
            }
            if (tsTodayTime != null) {
                where.append(" and sendTime<'" + TimeUtil.parseDate(tsTodayTime.getTime()) + "'");
            }
            String w = where.toString();
            sql = sql + w;
            logger.info(sql.toString());
            aliveUserDataCount = (AliveUserDataCount) Data.GetOne("messi_ods", sql, new Object[]{type, Const.CUSTOMER_TYPE_NORMAL}, AliveUserDataCount.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return aliveUserDataCount;
    }

    /**
     * 获取当日首充人数
     *
     * @param lastUpdateTime
     * @param tsTodayTime
     * @return
     */
    public AliveUserDataCount getFirstDepositCount(Timestamp lastUpdateTime, Timestamp tsTodayTime) {
        AliveUserDataCount firstDepositCount = null;
        try {
            String sql = "SELECT count(*) as firstDepositCount FROM( SELECT count(userId) AS count , userId , sendTime , type FROM bill_wallet WHERE type = 1 GROUP BY userId) b LEFT JOIN userinfo u ON b.userId = u.id WHERE b.count = 1 AND u.utype = ? AND b.sendTime >= ? AND sendTime < ?";
            logger.info(sql.toString());
            firstDepositCount = (AliveUserDataCount) Data.GetOne("messi_ods", sql, new Object[]{Const.CUSTOMER_TYPE_NORMAL, TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime())}, AliveUserDataCount.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return firstDepositCount;
    }


    /**
     * 当日提空人数
     *
     * @param lastUpdateTime
     * @param tsTodayTime
     * @return
     */
    public AliveUserDataCount getCashNullCount(Timestamp lastUpdateTime, Timestamp tsTodayTime) {
        AliveUserDataCount cashNullCount = null;
        try {
            BigDecimal bigDecimal = new BigDecimal(0.0001);
            String sql = "SELECT count(*) AS cashNullCount FROM( SELECT sum(balance) AS count , userid FROM( SELECT max(id) id , bill.balance , bill.fundsType , bill.userId FROM bill_wallet bill  WHERE userId IN( SELECT DISTINCT userId FROM bill_wallet b LEFT JOIN userinfo u ON b.userId = u.id WHERE b.type = 2 AND u.utype = '01' AND b.sendTime >=? AND b.sendTime <?) and userId>1000300 GROUP BY userId , fundsType) b GROUP BY b.userId) t WHERE t.count < ?";
            logger.info(sql.toString());
            cashNullCount = (AliveUserDataCount) Data.GetOne("messi_ods", sql, new Object[]{TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime()), bigDecimal}, AliveUserDataCount.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return cashNullCount;
    }


    /**
     * 提空且一周内未登陆用户数量
     *
     * @return
     */
    public AliveUserDataCount getCashNullNoLoginCount() {
        AliveUserDataCount cashNullNoLoginCount = null;
        try {
            BigDecimal bigDecimal = new BigDecimal(0.0001);
            String sql = "SELECT count(*) as cashNullNoLoginCount FROM( SELECT userid , sum(balance) sumBalance , sendTime FROM bill_wallet WHERE id IN( SELECT max(id) FROM bill_wallet GROUP BY userid , fundstype) AND userid IN( SELECT DISTINCT userid FROM bill_wallet b WHERE userid NOT IN( SELECT userId FROM userloginip u WHERE u.date >= ? AND u.date < ? GROUP BY userId) and userid>1000300  AND type = 2) GROUP BY userid) ff WHERE sumBalance < ? ";
            logger.info(sql.toString());
            cashNullNoLoginCount = (AliveUserDataCount) Data.GetOne("messi_ods", sql, new Object[]{TimeUtil.parseDate(TimeUtil.getBeforeTime(-7).getTime()), TimeUtil.parseDate(TimeUtil.getTodayFirst().getTime()), bigDecimal}, AliveUserDataCount.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return cashNullNoLoginCount;
    }

    /**
     * 交易用户数
     */
    public AliveUserDataCount getTransactionCount(Timestamp lastUpdateTime, Timestamp tsTodayTime) {
        AliveUserDataCount transactionCount = null;
        try {
            String sql = "select count(distinct userid) as transactionCount from bill left join userinfo u on u.id = bill.userId where type in ('30', '21') and u.uType= '01' and sendTime >= ? and sendTime <?";
            logger.info(sql.toString());
            transactionCount = (AliveUserDataCount) Data.GetOne("messi_ods", sql, new Object[]{TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime())}, AliveUserDataCount.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return transactionCount;
    }

    /**
     * 根据币种获取用户交易额
     *
     * @param lastUpdateTime
     * @param tsTodayTime
     * @param marketType
     * @return
     */
    public AliveUserDataCount getUserTransactionFee(Timestamp lastUpdateTime, Timestamp tsTodayTime, String marketType) {
        AliveUserDataCount transactionFee = new AliveUserDataCount();
        try {
            AliveUserDataCount buyTransactionFee = new AliveUserDataCount();
            String sqlBuy = "SELECT sum(b.convertPrice) as transactionFee FROM bill b WHERE type IN(21) AND sendTime >= ? AND sendTime < ? AND userId IN( SELECT id FROM userinfo WHERE userinfo.utype = ?) AND b.mainMarket = ?  AND sendTime >= ? AND sendTime < ? and userId  IN(select id from userinfo where userinfo.utype=?) and  b.mainMarket=? ORDER BY sendTime DESC";
            buyTransactionFee = (AliveUserDataCount) Data.GetOne("messi_ods", sqlBuy, new Object[]{TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime()), Const.CUSTOMER_TYPE_NORMAL, marketType, TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime()), Const.CUSTOMER_TYPE_NORMAL, marketType}, AliveUserDataCount.class);
            AliveUserDataCount sellTransactionFee = new AliveUserDataCount();
            String sqlSell = "SELECT sum(b.amount) as transactionFee FROM bill b WHERE type IN(30) AND sendTime >= ? AND sendTime < ? AND userId IN( SELECT id FROM userinfo WHERE userinfo.utype = ?) AND b.mainMarket = ?  and  sendTime >= ? AND sendTime < ? and userId  IN(select id from userinfo where userinfo.utype=?) and  b.mainMarket=? ORDER BY sendTime DESC";
            sellTransactionFee = (AliveUserDataCount) Data.GetOne("messi_ods", sqlSell, new Object[]{TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime()), Const.CUSTOMER_TYPE_NORMAL, marketType, TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime()), Const.CUSTOMER_TYPE_NORMAL, marketType}, AliveUserDataCount.class);
            if (buyTransactionFee.getTransactionFee() == null) {
                buyTransactionFee.setTransactionFee(BigDecimal.ZERO);
            }
            if (sellTransactionFee.getTransactionFee() == null) {
                sellTransactionFee.setTransactionFee(BigDecimal.ZERO);
            }
            transactionFee.setTransactionFee(buyTransactionFee.getTransactionFee().add(sellTransactionFee.getTransactionFee()));
            logger.info(sqlBuy.toString());
            logger.info(sqlSell.toString());
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return transactionFee;

    }

    /**
     * 根据币种获取公司交易额
     *
     * @param lastUpdateTime
     * @param tsTodayTime
     * @param marketType
     * @return
     */
    public AliveUserDataCount getCompanyTransactionFee(Timestamp lastUpdateTime, Timestamp tsTodayTime, String marketType) {
        AliveUserDataCount transactionFee = new AliveUserDataCount();
        try {
            AliveUserDataCount buyTransactionFee = new AliveUserDataCount();
            String sqlBuy = "SELECT sum(b.convertPrice) as transactionFee FROM bill b WHERE type IN(21) AND sendTime >= ? AND sendTime < ? AND userId IN( SELECT id FROM userinfo WHERE userinfo.utype != ?)AND b.mainMarket = ? AND sendTime >= ? AND sendTime < ? and userId  IN(select id from userinfo where userinfo.utype!=?) AND b.mainMarket = ? ORDER BY sendTime DESC";
            buyTransactionFee = (AliveUserDataCount) Data.GetOne("messi_ods", sqlBuy, new Object[]{TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime()), Const.CUSTOMER_TYPE_NORMAL, marketType, TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime()), Const.CUSTOMER_TYPE_NORMAL, marketType}, AliveUserDataCount.class);
            AliveUserDataCount sellTransactionFee = new AliveUserDataCount();
            String sqlSell = "SELECT sum(b.amount) as transactionFee FROM bill b WHERE type IN(30) AND sendTime >= ? AND sendTime < ? AND userId IN( SELECT id FROM userinfo WHERE userinfo.utype != ?) AND b.mainMarket = ? AND sendTime >= ? AND sendTime < ? and userId  IN(select id from userinfo where userinfo.utype!=?)  AND b.mainMarket = ?  ORDER BY sendTime DESC";
            sellTransactionFee = (AliveUserDataCount) Data.GetOne("messi_ods", sqlSell, new Object[]{TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime()), Const.CUSTOMER_TYPE_NORMAL, marketType, TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime()), Const.CUSTOMER_TYPE_NORMAL, marketType}, AliveUserDataCount.class);
            if (buyTransactionFee.getTransactionFee() == null) {
                buyTransactionFee.setTransactionFee(BigDecimal.ZERO);
            }
            if (sellTransactionFee.getTransactionFee() == null) {
                sellTransactionFee.setTransactionFee(BigDecimal.ZERO);
            }
            transactionFee.setTransactionFee(buyTransactionFee.getTransactionFee().add(sellTransactionFee.getTransactionFee()));
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return transactionFee;

    }


    public Long getRegisterCount() {
        Long count = 0L;
        try {
            String sql = "select count(*) from userinfo  WHERE uType ='01' and userName is not null ";
            List<Long> list = (List<Long>) Data.GetOne("messi_ods", sql, null);
            count = list.get(0);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return count;
    }

}
