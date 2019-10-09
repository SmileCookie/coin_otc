package com.world.model.daos.world;

import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.Market;
import com.world.model.entity.pay.PayUserBean;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;

public class WorldManager {
    private final static Logger log = Logger.getLogger(WorldManager.class);

    private static FundsUserDao payUserDao = new FundsUserDao();

    private static FeeDao feeDao = new FeeDao();

    //private static String exchangeBi = Market.exchangeBiEn;

    //private static String numberBi = Market.numberBiEn;

    /***
     * 查询pay_user 资产状况 fundsType  资金类型
     * 可用资金：balance,冻结资金：freez
     * @return
     */
    public static List<Object> excuteQueryPayUser(TransactionObject txObj, int userId, int fundsType) {
        return txObj.excuteQuery2(payUserDao.excuteQueryPayUserSql(userId, fundsType));
    }

    /**
     * 委托下单查询用户资产
     */
    public static List<Object> excuteQueryPayUserEntrust(TransactionObject txObj, int userId, int fundsType) {
        return txObj.excuteQuery(payUserDao.excuteQueryPayUserEntrust(userId, fundsType));
    }


    /**
     * 委托买入
     *
     * @return
     * @throws Exception
     */
    public static boolean[] buy(int userId, BigDecimal amount, List<OneSql> sqls, Market m) {
        ///0cny  1btc 2ltc 3btq 4eth 5dao 6etc   需要更新缓存的资产
        boolean[] changes = new boolean[]{false, false, false, false, false, false, false};
        try {
            if (m.exchangeBiFundsType != 0) {
                payUserDao.freezCny(userId, amount, m.exchangeBiFundsType, sqls);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            return null;
        }
        return changes;
    }

    /***
     * 委托卖出
     * @param userId
     * @param amount
     * @param sqls
     * @return
     */
    public static boolean[] sell(int userId, BigDecimal amount, List<OneSql> sqls, Market m) {
        ///0cny  1btc 2ltc 3btq 4eth 5dao 6etc
        boolean[] changes = new boolean[]{false, false, false, false, false, false, false};

        try {


            //CurrencyType ct = CurrencyType.getCurrencyType(m.numberBi.toLowerCase());
            if (m.numberBiFundsType != 0) {
                payUserDao.freezSzbs(userId, amount, m.numberBiFundsType, sqls);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            return null;
        }
        return changes;
    }

    /**
     * 取消买入
     *
     * @return
     * @throws Exception
     */
    public static boolean[] cancelBuy(int userId, BigDecimal amount, List<OneSql> sqls, Market m) {
        ///0cny  1btc 2ltc 3btq 4eth 5dao 6etc
        boolean[] changes = new boolean[]{false, false, false, false, false, false, false};
        try {

            if (m.exchangeBiFundsType != 0) {
                payUserDao.unFreezCnyNotDeduct(userId, amount, m.exchangeBiFundsType, sqls);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            return null;
        }

        return changes;
    }

    /***
     * 取消卖出
     * @param userId
     * @param amount
     * @param sqls
     * @return
     */
    public static boolean[] cancelSell(int userId, BigDecimal amount, List<OneSql> sqls, Market m) {
        ///0cny  1btc 2ltc 3btq 4eth 5dao,6etc
        boolean[] changes = new boolean[]{false, false, false, false, false, false, false};

        try {

            //CurrencyType ct = CurrencyType.getCurrencyType(m.numberBi.toLowerCase());
            if (m.numberBiFundsType != 0) {
                payUserDao.unFreezSzbsNotDeduct(userId, amount, m.numberBiFundsType, sqls);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            return null;
        }
        return changes;
    }

    public static PayUserBean getUserBalance(int userId, int fundsType) {
        return payUserDao.getUserBalance(userId, fundsType);
    }

}
