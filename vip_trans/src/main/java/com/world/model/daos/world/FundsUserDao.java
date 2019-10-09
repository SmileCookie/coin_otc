package com.world.model.daos.world;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.common.BillType;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserBean;
import com.world.util.DigitalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FundsUserDao extends DataDaoSupport implements MysqlDatabase {


    private final static String userFundsKey = "user_funds_";

    private final static String userFundsAndLoanKey = "user_funds_loan_";

    private static ExecutorService updateFundsPool = Executors.newFixedThreadPool(20);
    private static final Logger logger = LoggerFactory.getLogger(FundsUserDao.class);

    /**
     * 根据用户ID查询用户指定币种类型的资产和冻结金额,用户姓名。
     *
     * @param userId    用户ID
     * @param fundsType 资产类型
     * @return 返回执行的sql语句
     * @author zhanglinbo 20160920
     */
    public OneSql excuteQueryPayUserSql(int userId, int fundsType) {
//        return new OneSql("select balance,freez,userName from pay_user where userId=? and fundsType=? for update",
//                -2, new Object[]{userId, fundsType}, WORLD_DATABASE);

        return new OneSql("select balance,freez,userName from pay_user where userId=" + userId +
                " and fundsType=" + fundsType + " for update", -2, new Object[]{}, WORLD_DATABASE);
    }

    /**
     * 委托资产查询
     *
     * @param userId
     * @param fundsType
     * @return
     */
    public OneSql excuteQueryPayUserEntrust(int userId, int fundsType) {
        return new OneSql("select balance,freez,userName from pay_user where userId=? and fundsType=? ",
                -2, new Object[]{userId, fundsType}, WORLD_DATABASE);
    }


    /****
     * 冻结资产  为购买币准备
     *
     * @param userId    用户ID
     * @param amount    冻结资金金额
     * @param fundsType 资金类型
     * @param sqls
     * @author zhanglinbo 20160920
     */

    public void freezCny(int userId, BigDecimal amount, int fundsType, List<OneSql> sqls) {
        sqls.add(new OneSql("update pay_user set balance=balance-?,freez=freez+? where userId=? and fundsType=? and balance>=?", 1,
                new Object[]{amount, amount, userId, fundsType, amount}, WORLD_DATABASE));
    }
    /*
    public void freezBtc(int userId, long lamount, List<OneSql> sqls){
		
		sqls.add(new OneSql("update btcUser set account=account-?,freeze=freeze+? where userId=? and account>=?", 1, 
				new Object[]{lamount, lamount, userId, lamount}, WORLD_DATABASE));
	}
	
	public void freezEth(int userId, long lamount, List<OneSql> sqls){
		
		sqls.add(new OneSql("update pay_user set eths=eths-?,freezEths=freezEths+? where userId=? and eths>=?", 1, 
				new Object[]{lamount, lamount, userId, lamount}, WORLD_DATABASE));
	}
	
	public void freezLtc(int userId, long lamount, List<OneSql> sqls) {
		sqls.add(new OneSql("update pay_user set ltcs=ltcs-?,freezLtcs=freezLtcs+? where userId=? and ltcs>=?", 1, 
				new Object[]{lamount, lamount, userId, lamount}, WORLD_DATABASE));
	}

	public void freezBtq(int userId, double amount, List<OneSql> sqls) {
		sqls.add(new OneSql("update pay_user set sellWeight=sellWeight+? where userId=? and couldUseWeight>=?", 1, 
				new Object[]{amount, userId, amount}, WORLD_DATABASE));
	}
	*/

    /**
     * 冻结山寨币资金，待卖出
     *
     * @param userId    用户ID
     * @param lamount   冻结币种金额
     * @param fundsType 币种资金类型
     * @param sqls      返回执行的语句
     * @author zhanglinbo 20160920
     */
    public void freezSzbs(int userId, BigDecimal lamount, int fundsType, List<OneSql> sqls) {
        sqls.add(new OneSql("update pay_user set balance=balance-?,freez=freez+? where userId=? and fundsType=? and balance>=?", 1,
                new Object[]{lamount, lamount, userId, fundsType, lamount}, WORLD_DATABASE));
    }

    /**
     * 解冻CNY资产不扣除
     *
     * @param userId    用户ID
     * @param amount    资产金额
     * @param fundsType 资金类型
     * @param sqls
     * @author zhanglinbo 20160920
     */
    public void unFreezCnyNotDeduct(int userId, BigDecimal amount, int fundsType, List<OneSql> sqls) {
        //double maxWuCha = 0.001d;
        //double duiBi = DigitalUtil.sub(amount, maxWuCha);

        sqls.add(new OneSql("update pay_user set balance=balance+?,freez=freez-? where userId=? and fundsType=? and freez>=?", 1,
                new Object[]{amount, amount, userId, fundsType, amount}, WORLD_DATABASE));
    }
	
	/*
	public void unFreezBtcNotDeduct(int userId, long amount, List<OneSql> sqls){
		long maxWuCha = 10000;
		long duiBi =amount - maxWuCha;
		
		sqls.add(new OneSql("update btcUser set account=account+?,freeze=freeze-? where userId=? and freeze>=?", 1, 
				new Object[]{amount, amount, userId, duiBi}, WORLD_DATABASE));
	}
	
	public void unFreezEthNotDeduct(int userId, long amount, List<OneSql> sqls){
		sqls.add(new OneSql("update pay_user set eths=eths+?,freezEths=freezEths-? where userId=? and freezEths>=?", 1, 
				new Object[]{amount, amount, userId, amount}, WORLD_DATABASE));
	}
	
	public void unFreezLtcNotDeduct(int userId, long amount, List<OneSql> sqls) {
		sqls.add(new OneSql("update pay_user set ltcs=ltcs+?,freezLtcs=freezLtcs-? where userId=? and freezLtcs>=?", 1, 
				new Object[]{amount, amount, userId, amount}, WORLD_DATABASE));
	}

	public void unFreezBtqNotDeduct(int userId, double amount, List<OneSql> sqls) {
		sqls.add(new OneSql("update pay_user set sellWeight=sellWeight-? where userId=? and sellWeight>=?", 1, 
				new Object[]{amount, userId, amount}, WORLD_DATABASE));
	}
	*/

    /***
     * 解冻并回滚资产。后期添加的山寨币种
     *
     * @param userId    用户ID
     * @param amount    资金变动金额
     * @param fundsType 资金类型
     * @param sqls
     * @author zhanglinbo 20160920
     */
    public void unFreezSzbsNotDeduct(int userId, BigDecimal amount, int fundsType, List<OneSql> sqls) {

        sqls.add(new OneSql("update pay_User set balance=balance+?,freez=freez-? where UserId=? and fundsType=? and freez>=?", 1,
                new Object[]{amount, amount, userId, fundsType, amount}, WORLD_DATABASE));
    }


    /**
     * 解冻 资产并扣除
     *
     * @param userId    用户ID
     * @param amount    资金数量
     * @param fundsType 资金类型
     * @return 返回执行的sql
     * @author zhanglinbo 20160920
     */
    public OneSql unFreezCnyDeduct(int userId, BigDecimal amount, int fundsType) {
        return new OneSql("update pay_user set freez=freez-? where userId=? and fundsType=? and freez>=?", 1,
                new Object[]{amount, userId, fundsType, amount}, WORLD_DATABASE);

//        return new OneSql("update pay_user set freez=freez-" + amount + " where userId=" + userId +
//                " and fundsType=" + fundsType + " and freez>=" + amount, 1, new Object[]{}, WORLD_DATABASE);
    }


    /***
     * 解冻并扣除后期添加的山寨币种
     *
     * @param userId    用户ID
     * @param lamount   卖出币数量
     * @param fundsType 资金币种类型
     *                  //	 * @param sqls 返回执行语句
     * @author zhanglinbo 20160920
     */
    public OneSql unFreezSzbsDeduct(int userId, BigDecimal lamount, int fundsType) {
        return new OneSql("update pay_user set freez=freez-? where userId=? and fundsType=? and freez>=?", 1,
                new Object[]{lamount, userId, fundsType, lamount}, WORLD_DATABASE);

//        return new OneSql("update pay_user set freez=freez-" + lamount + " where userId=" + userId +
//                " and fundsType=" + fundsType + " and freez>=" + lamount, 1, new Object[]{}, WORLD_DATABASE);
    }


    /**
     * 解冻CNY资产并扣除
     *
     * @param userId    用户ID
     * @param amount    资产金额
     * @param fundsType 资金类型
     *                  //	 * @param sqls 返回执行语句
     * @author zhanglinbo 20160920
     */
    public OneSql addCny(int userId, BigDecimal amount, int fundsType) {
        return new OneSql("update pay_user set balance=balance+? where userId=? and fundsType=?", 1,
                new Object[]{amount, userId, fundsType}, WORLD_DATABASE);

//        return new OneSql("update pay_user set balance=balance+" + amount + " where userId=" + userId +
//                " and fundsType=" + fundsType, 1, new Object[]{}, WORLD_DATABASE);
    }

    /***
     * 解冻并扣除后期添加的山寨币种
     *
     * @param userId    用户ID
     * @param amount    买到币数量
     * @param fundsType 资金类型
     * @return 返回执行sql
     * @author zhanglinbo 20160920
     */
    public OneSql addSzbs(int userId, BigDecimal amount, int fundsType) {
        return new OneSql("update pay_user set balance=balance+? where userId=? and fundsType=?", 1,
                new Object[]{amount, userId, fundsType}, WORLD_DATABASE);

//        return new OneSql("update pay_user set balance=balance+" + amount + " where userId=" + userId +
//                " and fundsType=" + fundsType, 1, new Object[]{}, WORLD_DATABASE);
    }

    /**
     * 记录详细综合账单信息
     *
     * @param type         账单类型
     * @param userId       用户ID
     * @param changeAmount 资产变动金额
     * @param fees         手续费金额
     * @param fundsType    变动资金类型1：CNY 2：BTC 5：ETH 6：ETC
     * @param entrustId    委托ID
     * @param txObj        事务操作对象
     * @return 执行sql语句
     * @author zhanglinbo 20160920
     */
    public OneSql getInsertBillSql(BillType type, int userId, BigDecimal changeAmount, BigDecimal fees, int fundsType, long entrustId, TransactionObject txObj, String mainMarket, String subMarket, BigDecimal convertPrice) {

        String sql = "insert into bill(";
        String cloumn = "", conditions = "";
        List<Object> values = new LinkedList<Object>();
        cloumn += "userId,type,sendTime,fundsType,mainMarket,subMarket";
        conditions += "?,?,now(),?,?,?";
        values.add(userId);
        values.add(type.getKey());
        values.add(fundsType);
        values.add(mainMarket);
        values.add(subMarket);
        if (fees.compareTo(BigDecimal.ZERO) != 0) {//手续费不为空
            cloumn += ",fees";
            conditions += ",?";
            values.add(fees);

        }
        if (convertPrice.compareTo(BigDecimal.ZERO) != 0) {//交易费不为空
            cloumn += ",convertPrice";
            conditions += ",?";
            values.add(convertPrice);

        }


        List<Object> payUserObj = null;
        if (changeAmount.compareTo(BigDecimal.ZERO) != 0) {
            cloumn += ",amount,balance,userName";
            conditions += ",?,?,?";
            values.add(changeAmount);

            payUserObj = WorldManager.excuteQueryPayUser(txObj, userId, fundsType);
            values.add(DigitalUtil.getBigDecimal(payUserObj.get(0)).add(DigitalUtil.getBigDecimal(payUserObj.get(1))));
            values.add(payUserObj.get(2));//用户姓名
        }

        sql += cloumn + ") values (" + conditions + ")";
        log.info(sql);
        return new OneSql(sql, -1, values.toArray(), WORLD_DATABASE);
    }


    /**
     * 记录详细综合账单信息
     *
     * @param type         账单类型
     * @param userId       用户ID
     * @param changeAmount 资产变动金额
     * @param fees         手续费金额
     * @param fundsType    变动资金类型1：CNY 2：BTC 5：ETH 6：ETC
     * @param txObj        事务操作对象
     * @return 执行sql语句
     * @author zhanglinbo 20160920
     */
    public OneSql getInsertBillSqlNew(BillType type, int userId, BigDecimal changeAmount, BigDecimal fees, int fundsType, String sendTime, String mainMarket, String subMarket, BigDecimal convertPrice, TransactionObject txObj, long transRecordId) {
        String sql = "insert into bill(";
        String cloumn = "", conditions = "";
        List<Object> values = new LinkedList<Object>();
        cloumn += "userId,type,sendTime,fundsType,mainMarket,subMarket";
        conditions += "?,?,?,?,?,?";
        values.add(userId);
        values.add(type.getKey());
        values.add(sendTime);
        values.add(fundsType);
        values.add(mainMarket);
        values.add(subMarket);
        if (fees.compareTo(BigDecimal.ZERO) != 0) {//手续费不为空
            cloumn += ",fees";
            conditions += ",?";
            values.add(fees);

        }
        if (convertPrice.compareTo(BigDecimal.ZERO) != 0) {//交易费不为空
            cloumn += ",convertPrice";
            conditions += ",?";
            values.add(convertPrice);
        }

        List<Object> payUserObj = null;
        if (changeAmount.compareTo(BigDecimal.ZERO) != 0) {
            cloumn += ",amount,balance,userName,remark";
            conditions += ",?,?,?,?";
            values.add(changeAmount);

            payUserObj = WorldManager.excuteQueryPayUser(txObj, userId, fundsType);
            if (null != payUserObj) {
                values.add(DigitalUtil.getBigDecimal(payUserObj.get(0)).add(DigitalUtil.getBigDecimal(payUserObj.get(1))));
                values.add(payUserObj.get(2));//用户姓名

                String remark = payUserObj.get(0).toString() + "," + payUserObj.get(1).toString() + "," + transRecordId;
                values.add(remark);
            }
        }

        sql += cloumn + ") values (" + conditions + ")";
        return new OneSql(sql, -1, values.toArray(), WORLD_DATABASE);
    }

    public OneSql getInsertBillSqlNew2(BillType type, int userId, BigDecimal changeAmount, BigDecimal fees, int fundsType, String sendTime, String mainMarket, String subMarket, BigDecimal convertPrice, TransactionObject txObj, long transRecordId) {
        String sql = "insert into bill(";
        String cloumn = "", conditions = "";
        cloumn += "userId,type,sendTime,fundsType,mainMarket,subMarket";
        conditions += "" + userId + "," + type.getKey() + ",'" + sendTime + "'," + fundsType + ",'" + mainMarket + "','" + subMarket + "'";
//        conditions += "" + userId + "," + type.getKey() + ",now()," + fundsType + ",'" + mainMarket + "','" + subMarket + "'";
        if (fees.compareTo(BigDecimal.ZERO) != 0) {//手续费不为空
            cloumn += ",fees";
            conditions += "," + fees;
        }
        if (convertPrice.compareTo(BigDecimal.ZERO) != 0) {//交易费不为空
            cloumn += ",convertPrice";
            conditions += "," + convertPrice;
        }

        List<Object> payUserObj = null;
        if (changeAmount.compareTo(BigDecimal.ZERO) != 0) {
            cloumn += ",amount,balance,userName,remark";

            payUserObj = WorldManager.excuteQueryPayUser(txObj, userId, fundsType);

            if (null != payUserObj) {
                log.info(" [资金处理] 插入bill表 pay_user 数据:" + JSONObject.toJSONString(payUserObj) + ", transRecordId:" + transRecordId);

                conditions += "," + changeAmount + "," + DigitalUtil.getBigDecimal(payUserObj.get(0)).add(DigitalUtil.getBigDecimal(payUserObj.get(1))) +
                        ",'" + payUserObj.get(2) + "','" + payUserObj.get(0).toString() + "," + payUserObj.get(1).toString() + "," + transRecordId + "'";
            }
        }

        sql += cloumn + ") values (" + conditions + ")";
//        log.info(" [资金处理] 插入bill表 sql:" + sql);
        return new OneSql(sql, -1, null, WORLD_DATABASE);
    }

    public void updateFundsByPool(int userId) {
        updateFundsPool.execute(() -> {
            updateFundsByChange(userId);
        });
    }

    public void updateFunds(BillType type, int userId) {
        ///0cny  1btc 2ltc 3btq 4eth 5dao 6etc
        updateFundsByChange(userId);
    }

    public void updateFundsByChange2(int userId) {
        long startTime = System.currentTimeMillis();
        Map<String, PayUserBean> userMaps = new LinkedHashMap<>();

        JSONArray joArr = new JSONArray();
        List<Bean> payUserFunds = Data.Query(WORLD_DATABASE, "select * from pay_user where userId=? ORDER BY fundsType ", new Object[]{userId + ""}, PayUserBean.class);

        Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
        Iterator<CoinProps> iter = coinPropsMap.values().iterator();
        if (payUserFunds != null && payUserFunds.size() > 0) {
            while (iter.hasNext()) {
                CoinProps coin = iter.next();
                boolean has = false;
                for (int i = 0; i < payUserFunds.size(); i++) {
                    PayUserBean payUser = (PayUserBean) payUserFunds.get(i);

                    if (coin.getFundsType() == payUser.getFundsType()) {
                        JSONObject json = new JSONObject();
                        json.put("balance", payUser.getBalance());//可用余额
                        json.put("freeze", payUser.getFreez());//冻结余额
                        json.put("fundsType", payUser.getFundsType());//资金类型
                        json.put("propTag", coin.getPropTag());//属性标签
                        json.put("coinFullNameEn", coin.getPropEnName());//币种全称
                        json.put("unitTag", coin.getUnitTag());//符号
                        json.put("total", payUser.getBalance().add(payUser.getFreez()));//总金额
                        json.put("canCharge", coin.isCanCharge());
                        json.put("canWithdraw", coin.isCanWithdraw());
                        json.put("eventFreez", payUser.getEventFreez());
                        joArr.add(json);

                        payUser.setCoint(coin);
                        userMaps.put(coin.getPropTag().toLowerCase(), payUser);
                        has = true;
                        break;
                    }
                }

                if (!has) {
                    JSONObject json = new JSONObject();
                    json.put("balance", BigDecimal.ZERO);
                    json.put("freeze", BigDecimal.ZERO);
                    json.put("fundsType", coin.getFundsType());
                    json.put("propTag", coin.getPropTag());//属性标签
                    json.put("coinFullNameEn", coin.getPropEnName());//币种全称
                    json.put("unitTag", coin.getUnitTag());//符号
                    json.put("total", BigDecimal.ZERO);//总金额
                    json.put("canCharge", coin.isCanCharge());
                    json.put("canWithdraw", coin.isCanWithdraw());
                    joArr.add(json);

                    PayUserBean payUser = getById(userId + "", coin.getFundsType());
                    if (payUser != null) {
                        payUser.setCoint(coin);
                        payUser.setBalance(BigDecimal.ZERO);
                        payUser.setFreez(BigDecimal.ZERO);
                        payUser.setTotal(BigDecimal.ZERO);
                        payUser.setFundsType(coin.getFundsType());
                        payUser.setInWait(BigDecimal.ZERO);
                        payUser.setInSuccess(BigDecimal.ZERO);
                        payUser.setOutWait(BigDecimal.ZERO);
                        payUser.setOutSuccess(BigDecimal.ZERO);
                        payUser.setOverdraft(BigDecimal.ZERO);
                        payUser.setInterestOfDay(BigDecimal.ZERO);
                        payUser.setWithdrawFreeze(BigDecimal.ZERO);
                        userMaps.put(coin.getPropTag().toLowerCase(), payUser);
                    }

                }

            }
        } else {
            //用户没找到删除缓存  此处是必要的的，防止依赖内存的project在取不到值得情况下做出错误的处理
            Cache.Delete(userFundsKey + userId);
            return;
        }

        if (joArr != null && joArr.size() > 0) {

            //TODO 临时方案,将最后一位的usdt放到第一位
//            JSONArray result = new JSONArray();
//            result.add(joArr.get(joArr.size() - 1));
//
//            joArr.remove(joArr.size() - 1);
//
//            result.addAll(joArr);
//
//            Cache.SetObj(userFundsKey + userId, result, 300);
            Cache.SetObj(userFundsKey + userId, joArr, 300);
        }

        if (userMaps != null && !userMaps.isEmpty()) {
            Map<String, String> stringObjectMap = new HashMap<>();
            for (String key : userMaps.keySet()) {
                String js = JSON.toJSONString(userMaps.get(key));
                stringObjectMap.put(key, js);
            }
            Cache.SetObj(userFundsAndLoanKey + userId, stringObjectMap, 300);
        }
        logger.info("updateFundsByChange end. execute: "+ (System.currentTimeMillis() - startTime) +"ms");
    }

    public void updateFundsByChange(int userId) {
        long startTime = System.currentTimeMillis();
//        Map<String, PayUserBean> userMaps2 = new LinkedHashMap<>();
//        Map<String, String> userMaps = new HashMap<>();

        JSONArray joArr = new JSONArray();
        List<PayUserBean> payUserFunds = Data.QueryT(WORLD_DATABASE, "select * from pay_user where userId=? ", new Object[]{userId}, PayUserBean.class);

        if (CollectionUtils.isNotEmpty(payUserFunds)) {
            Map<Integer, PayUserBean> payUserFundsTypeMap = Maps.uniqueIndex(payUserFunds, new Function<PayUserBean, Integer>() {
                @Override
                public Integer apply(PayUserBean payUser) {
                    return payUser.getFundsType();
                }
            });

            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            for (CoinProps coin : coinPropsMap.values()) {
                PayUserBean payUser = payUserFundsTypeMap.get(coin.getFundsType());
                if (null != payUser) {
                    JSONObject json = new JSONObject();
                    json.put("balance", payUser.getBalance());//可用余额
                    json.put("freeze", payUser.getFreez());//冻结余额
                    json.put("fundsType", payUser.getFundsType());//资金类型
                    json.put("propTag", coin.getPropTag());//属性标签
                    json.put("coinFullNameEn", coin.getPropEnName());//币种全称
                    json.put("unitTag", coin.getUnitTag());//符号
                    json.put("total", payUser.getBalance().add(payUser.getFreez()));//总金额
                    json.put("canCharge", coin.isCanCharge());
                    json.put("canWithdraw", coin.isCanWithdraw());
                    json.put("eventFreez", payUser.getEventFreez());
                    joArr.add(json);

                    payUser.setCoint(coin);
//                    userMaps.put(coin.getPropTag().toLowerCase(), JSON.toJSONString(payUser));
                } else {
                    JSONObject json = new JSONObject();
                    json.put("balance", BigDecimal.ZERO);
                    json.put("freeze", BigDecimal.ZERO);
                    json.put("fundsType", coin.getFundsType());
                    json.put("propTag", coin.getPropTag());//属性标签
                    json.put("coinFullNameEn", coin.getPropEnName());//币种全称
                    json.put("unitTag", coin.getUnitTag());//符号
                    json.put("total", BigDecimal.ZERO);//总金额
                    json.put("canCharge", coin.isCanCharge());
                    json.put("canWithdraw", coin.isCanWithdraw());
                    joArr.add(json);

//                    payUser = new PayUserBean();
//                    payUser.setCoint(coin);
//                    payUser.setBalance(BigDecimal.ZERO);
//                    payUser.setFreez(BigDecimal.ZERO);
//                    payUser.setTotal(BigDecimal.ZERO);
//                    payUser.setFundsType(coin.getFundsType());
//                    payUser.setInWait(BigDecimal.ZERO);
//                    payUser.setInSuccess(BigDecimal.ZERO);
//                    payUser.setOutWait(BigDecimal.ZERO);
//                    payUser.setOutSuccess(BigDecimal.ZERO);
//                    payUser.setOverdraft(BigDecimal.ZERO);
//                    payUser.setInterestOfDay(BigDecimal.ZERO);
//                    payUser.setWithdrawFreeze(BigDecimal.ZERO);
//                    userMaps.put(coin.getPropTag().toLowerCase(), JSON.toJSONString(payUser));
                }
            }
        } else {
            //用户没找到删除缓存  此处是必要的的，防止依赖内存的project在取不到值得情况下做出错误的处理
            Cache.Delete(userFundsKey + userId);
            return;
        }

        if (joArr.size() > 0) {
            Cache.SetObj(userFundsKey + userId, joArr, 300);
        }

//        if (!userMaps.isEmpty()) {
////            Map<String, String> stringObjectMap = new HashMap<>();
////            for (String key : userMaps.keySet()) {
////                String js = JSON.toJSONString(userMaps.get(key));
////                stringObjectMap.put(key, js);
////            }
//            Cache.SetObj(userFundsAndLoanKey + userId, userMaps, 300);
//        }
        logger.info("updateFundsByChange end. userId:" + userId + ", execute: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void clearUserFundsCache(int userId) {
        Cache.Delete(userFundsKey + userId);
        Cache.Delete(userFundsAndLoanKey + userId);
    }

    public static JSONArray listToJsonArray(List list) {
        JSONArray jsonArray = new JSONArray();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            JSONObject obj = (JSONObject) list.get(i);
            jsonArray.add(obj);
        }
        return jsonArray;
    }
	/*end*/

    /***
     * @param id
     * @return
     */
    public PayUserBean getById(String id, int fundsType) {
        PayUserBean pub = (PayUserBean) Data.GetOne(WORLD_DATABASE, "select * from pay_user where userId=? AND fundsType = ?", new Object[]{id, fundsType}, PayUserBean.class);
        if (pub != null) {
            pub.setCoint(DatabasesUtil.coinProps(fundsType));
        }
        return pub;
    }

    public PayUserBean getUserBalance(int userId, int fundsType) {
        return (PayUserBean) Data.GetOne(WORLD_DATABASE, "select * from pay_user where userId=? AND fundsType = ?", new Object[]{userId, fundsType}, PayUserBean.class);
    }

    public static void main(String[] args) {
        List<PayUserBean> re  = Data.QueryT(WORLD_DATABASE, "select * from pay_user where userId=? ORDER BY fundsType ", new Object[]{1003401}, PayUserBean.class);
        System.out.println(re);
    }
}
