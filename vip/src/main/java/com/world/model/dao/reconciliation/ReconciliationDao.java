package com.world.model.dao.reconciliation;

import com.sun.org.apache.regexp.internal.RE;
import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.reconciliation.Generalledger;
import com.world.model.entity.reconciliation.Reconciliation;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.world.model.entity.bill.BillType.*;

public class ReconciliationDao extends DataDaoSupport<Reconciliation> {
    Logger logger = Logger.getLogger(ReconciliationDao.class);
    List<Reconciliation> rechargeList = new ArrayList<>();
    List<Reconciliation> withdrawList = new ArrayList<>();
    List<Reconciliation> sysRechargeList = new ArrayList<>();
    List<Reconciliation> sysDeductionList = new ArrayList<>();
    List<Reconciliation> sysSortList = new ArrayList<>();
    List<Reconciliation> icoExchangeList = new ArrayList<>();
    List<Reconciliation> sellList = new ArrayList<>();
    List<Reconciliation> buyList = new ArrayList<>();
    List<Reconciliation> transactionFeeList = new ArrayList<>();
    List<Reconciliation> withdrawFeeList = new ArrayList<>();
    List<Reconciliation> bookBalanceList = new ArrayList<>();
    List<Reconciliation> internalAdjustmentPositiveList = new ArrayList<>();
    List<Reconciliation> internalAdjustmentNegativeList = new ArrayList<>();
    List<Reconciliation> externalAdjustmentPositiveList = new ArrayList<>();
    List<Reconciliation> externalAdjustmentNegativeList = new ArrayList<>();
    List<Reconciliation> backCapitalList = new ArrayList<>();
    List<Reconciliation> backCapitalListFail = new ArrayList<>();
    List<Reconciliation> luckDrawCapitalList = new ArrayList<>();
    Boolean rechargeFlag = false;
    Boolean withdrawFlag = false;
    Boolean sysRechargeFlag = false;
    Boolean sysDeductionFlag = false;
    Boolean sysSortFlag = false;
    Boolean icoExchangeFlag = false;
    Boolean sellFlag = false;
    Boolean buyFlag = false;
    Boolean transactionFeeFlag = false;
    Boolean withdrawFeeFlag = false;
    Boolean bookBalanceFlag = false;
    Boolean internalAdjustmentPositiveFlag = false;
    Boolean internalAdjustmentNegativeFlag = false;
    Boolean externalAdjustmentPositiveFlag = false;
    Boolean externalAdjustmentNegativeFlag = false;
    Boolean backCapitalFlag = false;
    Boolean backCapitalFailFlag = false;
    Boolean luckDrawCapitalFlag = false;


    /**
     * 查询交易平台资金总账表
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<Reconciliation> getList(Date beginTime, Date endTime, int fundsType) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date begin = TimeUtil.dayBegin(beginTime);
            Date end = TimeUtil.dayEnd(endTime);
            StringBuilder where = new StringBuilder();
            where.append(" AND reportDate >='" + sdf.format(begin) + "'");
            where.append(" AND reportDate <'" + sdf.format(end) + "'");
            if (fundsType > 0) {
                where.append(" AND fundsType ='" + fundsType + "'");
            }
            String w = where.toString();
            if (w.length() > 0) {
                w = " where " + w.substring(4);
            }
            String sql = "select * from reconciliation" + w + " ";
            reconciliationList = Data.QueryT("messi_ods", sql, new Object[]{}, Reconciliation.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    /**
     * 查询交易平台资金总账差额
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<Reconciliation> getSubList(Date beginTime, Date endTime, int fundsType) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        List<Reconciliation> endReconciliationList = new ArrayList<>();
        List<Reconciliation> beginReconciliationList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        try {
            Date begin = null;
            if (beginTime != null) {
                begin = TimeUtil.dayBegin(beginTime);
            }
            Date end = TimeUtil.dayEnd(endTime);
            StringBuilder where = new StringBuilder();
            where.append(" AND reportDate >='" + sdf.format(TimeUtil.dayBegin(endTime)) + "'");
            where.append(" AND reportDate <'" + sdf.format(end) + "'");
            if (fundsType > 0) {
                where.append(" AND fundsType ='" + fundsType + "'");
            }
            String w = where.toString();
            if (w.length() > 0) {
                w = " where " + w.substring(4);
            }
            String sql = "select * from reconciliation" + w + " ";
            endReconciliationList = Data.QueryT("messi_ods", sql, new Object[]{}, Reconciliation.class);
            if (beginTime != null) {
                StringBuilder where1 = new StringBuilder();
                where1.append(" AND reportDate >='" + sdf.format(TimeUtil.dayBegin(beginTime)) + "'");
                where1.append(" AND reportDate <'" + sdf.format(TimeUtil.dayEnd(beginTime)) + "'");
                if (fundsType > 0) {
                    where1.append(" AND fundsType ='" + fundsType + "'");
                }
                String w1 = where1.toString();
                if (w1.length() > 0) {
                    w1 = " where " + w1.substring(4);
                }
                String sql1 = "select * from reconciliation" + w1 + " ";
                beginReconciliationList = Data.QueryT("messi_ods", sql1, new Object[]{}, Reconciliation.class);

            }
            for (Reconciliation endReconciliation : endReconciliationList) {
                Reconciliation reconciliation = new Reconciliation();
                if (CollectionUtils.isEmpty(beginReconciliationList)) {
                    reconciliationList = endReconciliationList;
                } else {
                    for (Reconciliation reconciliationBegin : beginReconciliationList) {
                        if (endReconciliation.getFundsType() == reconciliationBegin.getFundsType()) {
                            reconciliation.setFundsType(endReconciliation.getFundsType());
                            reconciliation.setRecharge(endReconciliation.getRecharge().subtract(reconciliationBegin.getRecharge()));
                            reconciliation.setWithdraw(endReconciliation.getWithdraw().subtract(reconciliationBegin.getWithdraw()));
                            reconciliation.setSysRecharge(endReconciliation.getSysRecharge().subtract(reconciliationBegin.getSysRecharge()));
                            reconciliation.setSysDeduction(endReconciliation.getSysDeduction().subtract(reconciliationBegin.getSysDeduction()));
                            reconciliation.setSysSort(endReconciliation.getSysSort().subtract(reconciliationBegin.getSysSort()));
                            reconciliation.setIcoExchange(endReconciliation.getIcoExchange().subtract(reconciliationBegin.getIcoExchange()));
                            reconciliation.setSell(endReconciliation.getSell().subtract(reconciliationBegin.getSell()));
                            reconciliation.setBuy(endReconciliation.getBuy().subtract(reconciliationBegin.getBuy()));
                            reconciliation.setTransactionFee(endReconciliation.getTransactionFee().subtract(reconciliationBegin.getTransactionFee()));
                            reconciliation.setWithdrawFee(endReconciliation.getWithdrawFee().subtract(reconciliationBegin.getWithdrawFee()));
                            reconciliation.setBookBalance(endReconciliation.getBookBalance().subtract(reconciliationBegin.getBookBalance()));
                            reconciliation.setReportDate(endReconciliation.getReportDate());
                            reconciliation.setInternalAdjustmentPositive(endReconciliation.getInternalAdjustmentPositive().subtract(reconciliationBegin.getInternalAdjustmentPositive()));
                            reconciliation.setInternalAdjustmentNegative(endReconciliation.getInternalAdjustmentNegative().subtract(reconciliationBegin.getInternalAdjustmentNegative()));
                            reconciliation.setExternalAdjustmentPositive(endReconciliation.getExternalAdjustmentPositive().subtract(reconciliationBegin.getExternalAdjustmentPositive()));
                            reconciliation.setExternalAdjustmentNegative(endReconciliation.getExternalAdjustmentNegative().subtract(reconciliationBegin.getInternalAdjustmentNegative()));
                            reconciliationList.add(reconciliation);
                        }

                    }
                }


            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 获取充值金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getRecharge(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(amount) as recharge, fundsType from bill_wallet where type = 1 and" + appendSql + " sendTime < ? group by fundsType";
            log.info(sql + " 时间：" + TimeUtil.parseDate(endTime.getTime()));
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(TimeUtil.getTodayFirst().getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 获取提现金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getWithdraw(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(amount) as withdraw, fundsType from bill_wallet where type = 2 and" + appendSql + " sendTime < ? group by fundsType";
            log.info(sql + " 时间：" + TimeUtil.parseDate(endTime.getTime()));
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 获取系统充值金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getSysRecharge(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(amount) as sysRecharge, fundsType from bill_wallet where type = 7 and" + appendSql + "  sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 获取系统扣除金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getSysDeduction(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(amount) as sysDeduction, fundsType from bill_wallet where type = 8 and" + appendSql + "  sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 获取系统分发金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getSysSort(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(amount) as sysSort, fundsType from bill where type = 201 and" + appendSql + "  sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    /**
     * 获取ICO兑换金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getIcoExchange(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = "and sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' ";
        }
        try {
            String sql = "select sum(amount) as icoExchange, fundsType from bill where type in (62, 64) " + appendSql + " and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 获取GBC的ICO兑换金额
     */
    public List<Reconciliation> getGbcIcoExchange(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = "and sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' ";
        }
        try {
            String sql = "select sum(amount) as icoExchange, fundsType from bill where type in (61, 63) " + appendSql + " and sendTime < ? and fundsType = 9";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    /**
     * 获取交易卖出金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getSell(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = "and sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' ";
        }
        try {
            String sql = "select sum(amount) as sell, fundsType from bill where type in (21, 30) " + appendSql + " and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 获取交易买入金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getBuy(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = "and sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' ";
        }
        try {
            String sql = "select sum(amount) as buy, fundsType from bill where type in (20, 31) " + appendSql + " and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    /**
     * 获取交易手续费金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getTransactionFee(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = "and sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' ";
        }
        try {
            String sql = "select sum(fees) as transactionFee, fundsType from bill where type in (20,31) " + appendSql + " and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    /**
     * 获取提现手续费金额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getWithdrawFee(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = "and sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' ";
        }
        try {
            String sql = "select sum(fees) as withdrawFee , fundsType from bill_wallet where type = 2 " + appendSql + " and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 获取账面余额
     *
     * @param endTime
     * @return
     */
    public List<Reconciliation> getBookBalance(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(balance) as bookBalance, fundsType from bill where id in (select max(id) id from bill where" + appendSql + " sendTime < ? and type not in (901,902,903,904) group by userid , fundsType )  group by fundsType ";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    /**
     * 内部调整正
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<Reconciliation> getInternalAdjustmentPositive(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(amount) as internalAdjustmentPositive , fundsType from bill_wallet where type = ? " + appendSql + " and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{InternalAdjustmentPositive.getKey(), TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 内部调整负
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<Reconciliation> getInternalAdjustmentNegative(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(amount) as internalAdjustmentNegative , fundsType from bill_wallet where type = ? " + appendSql + " and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{InternalAdjustmentNegative.getKey(), TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    /**
     * 外部调整正
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<Reconciliation> getExternalAdjustmentPositive(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(amount) as externalAdjustmentPositive , fundsType from bill_wallet where type = ? " + appendSql + " and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{ExternalAdjustmentPositive.getKey(), TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    public List<Reconciliation> getBackCapital(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        try {
            String appendSql = "";
            if (beginTime != null) {
                appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
            }
            String sql = "select sum(amount) as backCapital,fundsType from bill where userId = ? and type = 210 and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{"1000007", TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            log.info("02CS" + sql + "参数" + TimeUtil.parseDate(endTime.getTime()));
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }

    public List<Reconciliation> getBackCapitalFail(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        try {
            String appendSql = "";
            if (beginTime != null) {
                appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
            }
            String sql = "select sum(amount) as backCapitalFail,fundsType from bill where userId = ? and type = 211 and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{"1000007", TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            log.info("02CS" + sql + "参数" + TimeUtil.parseDate(endTime.getTime()));
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    public List<Reconciliation> getluckDrawCapital(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        try {
            String appendSql = "";
            if (beginTime != null) {
                appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
            }
            String sql = "select sum(amount) as luckDrawCapital,fundsType from bill where type = 221 and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    /**
     * 外部调整负
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<Reconciliation> getExternalAdjustmentNegative(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        String appendSql = "";
        if (beginTime != null) {
            appendSql = " sendTime>='" + TimeUtil.parseDate(beginTime.getTime()) + "' and";
        }
        try {
            String sql = "select sum(amount) as externalAdjustmentNegative , fundsType from bill_wallet where type = ? " + appendSql + " and sendTime < ? group by fundsType";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{ExternalAdjustmentNegative.getKey(), TimeUtil.parseDate(endTime.getTime())}, Reconciliation.class);
            for (Bean b : list) {
                Reconciliation reconciliation = (Reconciliation) b;
                reconciliationList.add(reconciliation);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return reconciliationList;
    }


    public List<Reconciliation> getBatchThread(Timestamp beginTime, Timestamp endTime) {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        rechargeFlag = false;
        withdrawFlag = false;
        sysRechargeFlag = false;
        sysDeductionFlag = false;
        sysSortFlag = false;
        icoExchangeFlag = false;
        sellFlag = false;
        buyFlag = false;
        transactionFeeFlag = false;
        withdrawFeeFlag = false;
        bookBalanceFlag = false;
        internalAdjustmentPositiveFlag = false;
        internalAdjustmentNegativeFlag = false;
        externalAdjustmentPositiveFlag = false;
        externalAdjustmentNegativeFlag = false;
        backCapitalFlag = false;
        backCapitalFailFlag = false;
        luckDrawCapitalFlag = false;

        //全币种录入
        Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
        logger.info(System.currentTimeMillis());
        for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
            Reconciliation reconciliation = new Reconciliation();
            CoinProps coint = entry.getValue();
            reconciliation.setFundsType(coint.getFundsType());
            reconciliation.setFundsTypeName(coint.getPropTag());
            reconciliationList.add(reconciliation);
        }
        logger.info(System.currentTimeMillis());
        for (String type : Const.ReconciliationList) {
            Thread thread = new MyThread(beginTime, endTime, type);
            thread.start();
        }
        logger.info(System.currentTimeMillis());
        while (true) {
            if (rechargeFlag && withdrawFlag && sysRechargeFlag && sysDeductionFlag && sysSortFlag && icoExchangeFlag && sellFlag && buyFlag && transactionFeeFlag && withdrawFeeFlag && bookBalanceFlag && internalAdjustmentPositiveFlag && internalAdjustmentNegativeFlag && externalAdjustmentPositiveFlag && externalAdjustmentNegativeFlag && backCapitalFlag && luckDrawCapitalFlag && backCapitalFailFlag) {

                //获取GBC回购转入金额
                logger.info(System.currentTimeMillis());
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation recharge : backCapitalList) {
                        if (reconciliation.getFundsType() == recharge.getFundsType()) {
                            reconciliation.setBackCapital(recharge.getBackCapital());
                        }
                    }
                }

                //获取GBC回购失败退回金额
                logger.info(System.currentTimeMillis());
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation recharge : backCapitalListFail) {
                        if (reconciliation.getFundsType() == recharge.getFundsType()) {
                            reconciliation.setBackCapitalFail(recharge.getBackCapitalFail());
                        }
                    }
                }

                //获取抽奖赠送金额
                logger.info(System.currentTimeMillis());
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation recharge : luckDrawCapitalList) {
                        if (reconciliation.getFundsType() == recharge.getFundsType()) {
                            reconciliation.setLuckDrawCapital(recharge.getLuckDrawCapital());
                        }
                    }
                }

                //获取充值金额
                logger.info(System.currentTimeMillis());
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation recharge : rechargeList) {
                        if (reconciliation.getFundsType() == recharge.getFundsType()) {
                            reconciliation.setRecharge(recharge.getRecharge());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());
                //获取提现金额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation withdraw : withdrawList) {
                        if (reconciliation.getFundsType() == withdraw.getFundsType()) {
                            reconciliation.setWithdraw(withdraw.getWithdraw());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());
                //获取系统充值金额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation sysRecharge : sysRechargeList) {
                        if (reconciliation.getFundsType() == sysRecharge.getFundsType()) {
                            reconciliation.setSysRecharge(sysRecharge.getSysRecharge());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());
                //获取系统扣除金额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation sysDeduction : sysDeductionList) {
                        if (reconciliation.getFundsType() == sysDeduction.getFundsType()) {
                            reconciliation.setSysDeduction(sysDeduction.getSysDeduction());
                        }
                    }
                }
                //获取系统分发金额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation sysDeduction : sysSortList) {
                        if (reconciliation.getFundsType() == sysDeduction.getFundsType()) {
                            reconciliation.setSysSort(sysDeduction.getSysSort());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());
                //获取ICO兑换金额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation icoExchange : icoExchangeList) {
                        if (reconciliation.getFundsType() == icoExchange.getFundsType()) {
                            reconciliation.setIcoExchange(icoExchange.getIcoExchange());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());
                //获取交易卖出金额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation sell : sellList) {
                        if (reconciliation.getFundsType() == sell.getFundsType()) {
                            reconciliation.setSell(sell.getSell());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());
                //获取交易买入金额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation buy : buyList) {
                        if (reconciliation.getFundsType() == buy.getFundsType()) {
                            reconciliation.setBuy(buy.getBuy());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());
                //获取交易手续费金额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation transactionFee : transactionFeeList) {
                        if (reconciliation.getFundsType() == transactionFee.getFundsType()) {
                            reconciliation.setTransactionFee(transactionFee.getTransactionFee());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());
                //获取提现手续费金额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation withdrawFee : withdrawFeeList) {
                        if (reconciliation.getFundsType() == withdrawFee.getFundsType()) {
                            reconciliation.setWithdrawFee(withdrawFee.getWithdrawFee());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());

                //获取账面余额
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation bookBalance : bookBalanceList) {
                        if (reconciliation.getFundsType() == bookBalance.getFundsType()) {
                            reconciliation.setBookBalance(bookBalance.getBookBalance());
                        }
                    }
                }
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation bookBalance : internalAdjustmentPositiveList) {
                        if (reconciliation.getFundsType() == bookBalance.getFundsType()) {
                            reconciliation.setInternalAdjustmentPositive(bookBalance.getInternalAdjustmentPositive());
                        }
                    }
                }
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation bookBalance : internalAdjustmentNegativeList) {
                        if (reconciliation.getFundsType() == bookBalance.getFundsType()) {
                            reconciliation.setInternalAdjustmentNegative(bookBalance.getInternalAdjustmentNegative());
                        }
                    }
                }
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation bookBalance : externalAdjustmentPositiveList) {
                        if (reconciliation.getFundsType() == bookBalance.getFundsType()) {
                            reconciliation.setExternalAdjustmentPositive(bookBalance.getExternalAdjustmentPositive());
                        }
                    }
                }
                for (Reconciliation reconciliation : reconciliationList) {
                    for (Reconciliation bookBalance : externalAdjustmentNegativeList) {
                        if (reconciliation.getFundsType() == bookBalance.getFundsType()) {
                            reconciliation.setExternalAdjustmentNegative(bookBalance.getExternalAdjustmentNegative());
                        }
                    }
                }
                logger.info(System.currentTimeMillis());
                break;

            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
        return reconciliationList;

    }

    public List<Generalledger> generalledgerList(String beginTime, String endTime) {
        List<Generalledger> generalledgerList = new ArrayList<>();
        try {
            String sql = "select * from generalledger where reportDate  >= ? and reportDate  < ? ";
            List<Bean> list = Data.Query("messi_ods", sql, new Object[]{beginTime,endTime}, Generalledger.class);

            for (Bean b : list) {
                Generalledger generalledger = (Generalledger) b;
                generalledgerList.add(generalledger);
            }

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return generalledgerList;
    }


    class MyThread extends Thread {

        public Timestamp beginTime;
        public Timestamp endTime;
        public String type;

        public MyThread(Timestamp beginTime, Timestamp endTime, String type) {
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.type = type;
        }


        public void run() {

            if (type.equals(Const.recharge)) {
                rechargeList = getRecharge(beginTime, endTime);
                rechargeFlag = true;
            } else if (type.equals(Const.withdraw)) {
                withdrawList = getWithdraw(beginTime, endTime);
                withdrawFlag = true;
            } else if (type.equals(Const.sysRecharge)) {
                sysRechargeList = getSysRecharge(beginTime, endTime);
                sysRechargeFlag = true;
            } else if (type.equals(Const.sysDeduction)) {
                sysDeductionList = getSysDeduction(beginTime, endTime);
                sysDeductionFlag = true;
            } else if (type.equals(Const.sysSort)) {
                sysSortList = getSysSort(beginTime, endTime);
                sysSortFlag = true;
            } else if (type.equals(Const.icoExchange)) {
                icoExchangeList = getIcoExchange(beginTime, endTime);
                icoExchangeFlag = true;
            } else if (type.equals(Const.sell)) {
                sellList = getSell(beginTime, endTime);
                sellFlag = true;
            } else if (type.equals(Const.buy)) {
                buyList = getBuy(beginTime, endTime);
                buyFlag = true;
            } else if (type.equals(Const.transactionFee)) {
                transactionFeeList = getTransactionFee(beginTime, endTime);
                transactionFeeFlag = true;
            } else if (type.equals(Const.withdrawFee)) {
                withdrawFeeList = getWithdrawFee(beginTime, endTime);
                withdrawFeeFlag = true;
            } else if (type.equals(Const.bookBalance)) {
                bookBalanceList = getBookBalance(beginTime, endTime);
                bookBalanceFlag = true;
            } else if (type.equals(Const.internalAdjustmentPositiveFlag)) {
                internalAdjustmentPositiveList = getInternalAdjustmentPositive(beginTime, endTime);
                internalAdjustmentPositiveFlag = true;
            } else if (type.equals(Const.internalAdjustmentNegativeFlag)) {
                internalAdjustmentNegativeList = getInternalAdjustmentNegative(beginTime, endTime);
                internalAdjustmentNegativeFlag = true;
            } else if (type.equals(Const.externalAdjustmentPositiveFlag)) {
                externalAdjustmentPositiveList = getExternalAdjustmentPositive(beginTime, endTime);
                externalAdjustmentPositiveFlag = true;
            } else if (type.equals(Const.externalAdjustmentNegativeFlag)) {
                externalAdjustmentNegativeList = getExternalAdjustmentNegative(beginTime, endTime);
                externalAdjustmentNegativeFlag = true;
            } else if (type.equals(Const.backCapitalFlag)) {
                backCapitalList = getBackCapital(beginTime, endTime);
                backCapitalFlag = true;
            } else if (type.equals(Const.luckDrawCapital)) {
                luckDrawCapitalList = getluckDrawCapital(beginTime, endTime);
                luckDrawCapitalFlag = true;
            } else if (type.equals(Const.backCapitalFailFlag)) {
                backCapitalListFail = getBackCapitalFail(beginTime, endTime);
                backCapitalFailFlag = true;
            }


        }
    }


    public Boolean saveReconciliation(List<Reconciliation> reconciliationList, Timestamp beginTime, Timestamp endTime) {
        Boolean flag = false;
        try {
            List<OneSql> sqls = new ArrayList<OneSql>();
            sqls.add(new OneSql("DELETE FROM reconciliation where countDate >=? and countDate<=?", -2, new Object[]{TimeUtil.parseDate(beginTime.getTime()), TimeUtil.parseDate(endTime.getTime())}, "messi_ods"));
            for (Reconciliation reconciliation : reconciliationList) {
                sqls.add(getSql(reconciliation));
            }
            flag = Data.doTransWithBatch(sqls);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return flag;


    }


    public OneSql getSql(Reconciliation reconciliation) {

        String sql = "insert into reconciliation(";
        String cloumn = "", conditions = "";
        List<Object> values = new LinkedList<Object>();
        cloumn += "fundsType,countDate,reportDate";
        conditions += "?,now(),DATE_ADD(now(),INTERVAL -1 day)";
        values.add(reconciliation.getFundsType());

        if (reconciliation.getRecharge() != null) {//
            cloumn += ",recharge";
            conditions += ",?";
            values.add(reconciliation.getRecharge());

        }

        if (reconciliation.getBackCapital() != null) {//
            cloumn += ",backCapital";
            conditions += ",?";
            values.add(reconciliation.getBackCapital());

        }
        if (reconciliation.getBackCapital() != null) {//
            cloumn += ",backCapitalFail";
            conditions += ",?";
            values.add(reconciliation.getBackCapitalFail());

        }
        if (reconciliation.getLuckDrawCapital() != null) {//
            cloumn += ",luckDrawCapital";
            conditions += ",?";
            values.add(reconciliation.getLuckDrawCapital());

        }

        if (reconciliation.getWithdraw() != null) {//
            cloumn += ",withdraw";
            conditions += ",?";
            values.add(reconciliation.getWithdraw());

        }
        if (reconciliation.getSysRecharge() != null) {//
            cloumn += ",sysRecharge";
            conditions += ",?";
            values.add(reconciliation.getSysRecharge());

        }
        if (reconciliation.getSysDeduction() != null) {//
            cloumn += ",sysDeduction";
            conditions += ",?";
            values.add(reconciliation.getSysDeduction());

        }
        if (reconciliation.getSysSort() != null) {//
            cloumn += ",sysSort";
            conditions += ",?";
            values.add(reconciliation.getSysSort());

        }
        if (reconciliation.getIcoExchange() != null) {//
            cloumn += ",icoExchange";
            conditions += ",?";
            values.add(reconciliation.getIcoExchange());

        }
        if (reconciliation.getSell() != null) {//
            cloumn += ",sell";
            conditions += ",?";
            values.add(reconciliation.getSell());

        }
        if (reconciliation.getBuy() != null) {//
            cloumn += ",buy";
            conditions += ",?";
            values.add(reconciliation.getBuy());

        }
        if (reconciliation.getTransactionFee() != null) {//
            cloumn += ",transactionFee";
            conditions += ",?";
            values.add(reconciliation.getTransactionFee());
        }
        if (reconciliation.getWithdrawFee() != null) {//
            cloumn += ",withdrawFee";
            conditions += ",?";
            values.add(reconciliation.getWithdrawFee());
        }
        if (reconciliation.getBookBalance() != null) {//
            cloumn += ",bookBalance";
            conditions += ",?";
            values.add(reconciliation.getBookBalance());
        }
        if (reconciliation.getInternalAdjustmentPositive() != null) {//
            cloumn += ",internalAdjustmentPositive";
            conditions += ",?";
            values.add(reconciliation.getInternalAdjustmentPositive());
        }
        if (reconciliation.getInternalAdjustmentNegative() != null) {//
            cloumn += ",internalAdjustmentNegative";
            conditions += ",?";
            values.add(reconciliation.getInternalAdjustmentNegative());
        }
        if (reconciliation.getExternalAdjustmentPositive() != null) {//
            cloumn += ",externalAdjustmentPositive";
            conditions += ",?";
            values.add(reconciliation.getExternalAdjustmentPositive());
        }
        if (reconciliation.getExternalAdjustmentNegative() != null) {//
            cloumn += ",externalAdjustmentNegative";
            conditions += ",?";
            values.add(reconciliation.getExternalAdjustmentNegative());
        }
        sql += cloumn + ") values (" + conditions + ")";
        log.info(sql);
        return new OneSql(sql, -1, values.toArray(), "messi_ods");


    }

}
