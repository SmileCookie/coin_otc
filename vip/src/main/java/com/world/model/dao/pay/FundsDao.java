package com.world.model.dao.pay;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.bill.BillType;
import com.world.util.DigitalUtil;
import weibo4j.model.Constants;

public class FundsDao extends DataDaoSupport {
    private static final long serialVersionUID = 1L;

    public FundsDao() {

    }

    public FundsDao(String database) {
        this.database = database;
    }

    /**
     * 增加资金的方法
     *
     * @param amount
     * @param userId
     * @param userName
     * @param reMark
     * @param type
     * @param fees
     * @param adminId
     * @param
     */
    public List<OneSql> addMoney(BigDecimal amount, String userId, String userName, String reMark, int type, int fundsType, BigDecimal fees, String adminId, boolean isBill) {
        List<OneSql> sqls = new ArrayList<OneSql>();
        //order by kinghao 20180726
        sqls.add(new OneSql("update pay_user_wallet set balance=balance+ ? where userId=? AND fundsType = ?", 1, new Object[]{amount, userId, fundsType}));
        if (isBill) {
            sqls.add(addBill(Integer.parseInt(userId), userName, amount, fees, type, reMark, adminId, fundsType));
        }
        return sqls;
    }

    public List<OneSql> addMoney(Timestamp billTime, BigDecimal amount, Timestamp blockTime, String txidN, String userId, String userName, String reMark, int type, int fundsType, long primaryId, BigDecimal fees, String adminId, boolean isBill) {
        List<OneSql> sqls = new ArrayList<OneSql>();
        //order by kinghao 20180726
        sqls.add(new OneSql("update pay_user_wallet set balance=balance+ ? where userId=? AND fundsType = ?", 1, new Object[]{amount, userId, fundsType}));
        if (isBill) {
            sqls.addAll(addBill(billTime, Integer.parseInt(userId), userName, amount, blockTime, txidN, fees, type, reMark, adminId, fundsType, primaryId));
        }
        return sqls;
    }

    /**
     * 减少资金的方法
     *
     * @param amount
     * @param userId
     * @param userName
     * @param reMark
     * @param type
     * @param fees
     * @param adminId
     * @param
     */
    public List<OneSql> subtractMoney(BigDecimal amount, String userId, String userName, String reMark, int type, int fundsType, BigDecimal fees, String adminId, boolean isBill) {
        List<OneSql> sqls = new ArrayList<OneSql>();
        sqls.add(new OneSql("update pay_user_wallet set balance=balance-? where userId=? AND balance >= ? AND fundsType = ?", 1, new Object[]{amount, userId, amount, fundsType}));
        if (isBill) {
            sqls.add(addBill(Integer.parseInt(userId), userName, amount, fees, type, reMark, adminId, fundsType));
        }
        return sqls;
    }

    //order by kinghao 20180726
    public OneSql addBill(int userId, String userName, BigDecimal amount, BigDecimal fees, int type, String reMark, String adminId, int fundsType) {
        String nowSql = "INSERT INTO bill_wallet (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType) " +
                "SELECT " + userId + ",'" + userName + "','" + type + "'," + amount + ",'" + now() + "','" + reMark + "'," + fees
                + ",balance+freez+withdrawFreeze as balance," + fundsType + " from pay_user_wallet where userId=" + userId + " AND fundsType = " + fundsType + " for update";

        return new OneSql(nowSql, 1, new Object[]{});
    }

    public List<OneSql> addBill(Timestamp billTime, int userId, String userName, BigDecimal amount, Timestamp blockTime, String txidN, BigDecimal fees, int type, String reMark, String adminId, int fundsType, long primaryId) {

        List<OneSql> sqls = new ArrayList<OneSql>();

        //新增结算流水
        String nowSql = "INSERT INTO bill_wallet (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType,blockTime,txidN) " +
                "SELECT " + userId + ",'" + userName + "','" + type + "'," + amount + ",'" + billTime + "','" + reMark + "'," + fees
                + ",balance+freez+withdrawFreeze as balance," + fundsType + ",'" + (blockTime == null ? now() : blockTime) + "','" + txidN + "' from pay_user_wallet where userId=" + userId + " AND fundsType = " + fundsType + " for update";
        sqls.add(new OneSql(nowSql, 1, new Object[]{}));

        return sqls;
    }

}
