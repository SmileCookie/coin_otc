package com.world.model.dao.pay;

import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.CointTable;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.pay.FreezType;
import com.world.model.entity.pay.FreezeBean;
import com.world.model.entity.pay.OrderNumberGeneration;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class FreezDao extends DataDaoSupport<FreezeBean> {
    private static final long serialVersionUID = -7774525509619263019L;

    public String getTableName() {
        return coint.getStag() + CointTable.freeze;
    }

    public List<OneSql> freez(List<OneSql> sqls, FreezeBean freez) {
        BigDecimal btcNumber = freez.getBtcNumber();
        String userId = freez.getUserId();
        int fundsType = coint.getFundsType();
        //扣除实际金额
        String otherSql = "";
        if (freez.getType() == FreezType.download.getKey()) {
            //提现冻结的要记录值
            otherSql = ",withdrawFreeze=withdrawFreeze+" + btcNumber;
        }
        sqls.add(new OneSql("update pay_user set balance=balance-?,freez=freez+?" + otherSql + " where userId=? and balance>=? AND fundsType = ?",
                1,
                new Object[]{btcNumber, btcNumber, userId, btcNumber, fundsType}
        ));
        //去掉冻结明细
   		/*String freezeSql = "INSERT INTO "+getTableName()+" (freezeId,userId, userName, freezeTime, type,btcNumber,statu, connectionId, freezeBanlance,reMark) " +
   		   					  "select ?,?,?,?,?,?,?,?,freez,? from pay_user where userId=? AND fundsType = ?";
	    sqls.add(new OneSql(freezeSql,-1,new Object[] {id, userId, userName , now(),
			freez.getType(),//提现类型
			btcNumber,
			0,//0冻结 1解冻 
			freez.getConnectionId(),
			freez.getReMark(),
			userId, fundsType
		}));*/
        return sqls;
    }

    public List<OneSql> walletFreez(List<OneSql> sqls, FreezeBean freez) {
        BigDecimal btcNumber = freez.getBtcNumber();
        String userId = freez.getUserId();
        int fundsType = coint.getFundsType();
        //扣除实际金额
//		String otherSql = "";
//		if(freez.getType() == FreezType.download.getKey()){
//			//提现冻结的要记录值
//			otherSql = ",withdrawFreeze=withdrawFreeze+"+btcNumber;
//		}
        sqls.add(new OneSql("update pay_user_wallet set balance=balance-?,freez=freez+? where userId=? and balance>=? AND fundsType = ?",
                1,
                new Object[]{btcNumber, btcNumber, userId, btcNumber, fundsType}
        ));
        return sqls;
    }

    public List<OneSql> walletFreez(List<OneSql> sqls , FreezeBean freez,int fundsType){
        BigDecimal btcNumber = freez.getBtcNumber();
        String userId = freez.getUserId();
        //扣除实际金额
//		String otherSql = "";
//		if(freez.getType() == FreezType.download.getKey()){
//			//提现冻结的要记录值
//			otherSql = ",withdrawFreeze=withdrawFreeze+"+btcNumber;
//		}
        sqls.add(new OneSql("update pay_user_wallet set balance=balance-?,withdrawFreeze=withdrawFreeze+? where userId=? and balance>=? AND fundsType = ?",
                1,
                new Object[]{btcNumber,btcNumber,userId,btcNumber, fundsType}
        ));
        return sqls;
    }

    public long getId() {
        return OrderNumberGeneration.getNewNumber("btcfreeze");
    }

    FundsDao fundsDao = new FundsDao();

    /****
     *
     * @param sqls sqls相关
     * @param freez  解冻相关
     * @param isReduce 是否扣除
     * @return
     */
    public void unFreezSqls(List<OneSql> sqls, FreezeBean freez, BillType billType, boolean isReduce) {

        int fundsType = coint.getFundsType();
//		long id = freez.getFreezeId();
//		String freezeSql = "INSERT INTO "+getTableName()+" (freezeId,userId, userName, freezeTime, type,btcNumber,statu, connectionId,reMark ,freezeBanlance) "
//				+ "select ?,?,?,?,?,?,?,?,?,freez from pay_user where userId=? AND fundsType = ?";

//		Timestamp now = now();
        BigDecimal btcNumber = freez.getBtcNumber();
        String userId = freez.getUserId();
        String userName = freez.getUserName();

        String otherSql = "";
//		if(freez.getType() == FreezType.cashUnFreez.getKey()){
//			//提现解冻的要记录值
//			otherSql = ",withdrawFreeze=withdrawFreeze-"+btcNumber;
//		}

        if (isReduce) {
            sqls.add(new OneSql("update pay_user_wallet set withdrawFreeze=withdrawFreeze-?" + otherSql + " where userid=? and withdrawFreeze>=? AND fundsType = ?", 1,
                    new Object[]{btcNumber, userId, btcNumber, fundsType}));
            //去掉冻结明细
			/*sqls.add(new OneSql(freezeSql, 1, new Object[] { id,
					userId, userName, now, freez.getType(),// 类型
					btcNumber, 1,// 解冻
					freez.getConnectionId(),// 连接之前的id
					freez.getReMark(), userId, fundsType }));*/

            sqls.add(fundsDao.addBill(Integer.parseInt(userId), userName, btcNumber, BigDecimal.ZERO, billType.getKey(), freez.getReMark(), "0", fundsType));

/*			// /扣除卖家比特币
			String nowSql = "INSERT INTO btcDetails (type, status,btcFrom, btcTo, addHash, number, sendimeTime, configTime, remark,userId,userName,price,fees,banlance,financialId) "
					+ "select ?,?,?,?,?,?,?,?,?,?,?,?,?,balance+freez as banlance,? from pay_user where userId=?";
			
			sqls.add(new OneSql(nowSql, 1, new Object[] { btcDetails.getType(), 2, btcDetails.getBtcFrom(),
					btcDetails.getBtcTo(), 0, btcNumber, now, now,
					freez.getReMark(), freez.getUserId(), userName,
					btcDetails.getPrice(), btcDetails.getFees(), btcDetails.getFinancialId(), userId }, database));
*/
        } else {// 失败解冻，那么需要增加资金回来
            sqls.add(new OneSql("update pay_user_wallet set balance=balance+?,withdrawFreeze=withdrawFreeze-?" + otherSql + " where userid=? and withdrawFreeze>=? AND fundsType = ?", 1,
                    new Object[]{btcNumber, btcNumber, freez.getUserId(), btcNumber, fundsType}));
            //去掉冻结明细
			/*sqls.add(new OneSql(freezeSql, 1,new Object[] { id, userId, userName, now,
							freez.getType(),// 类型
							btcNumber,
							1,// 解冻
							freez.getConnectionId(), freez.getReMark(),
							userId , fundsType}));*/
        }
    }

    /****
     * 在原方法的基础上加参数
     * @param sqls sqls相关
     * @param freez  解冻相关
     * @param isReduce 是否扣除
     * @return
     */
    public void unFreezPayUserSqls(List<OneSql> sqls, FreezeBean freez, BillType billType, boolean isReduce, BigDecimal fee) {

        int fundsType = coint.getFundsType();

        BigDecimal btcNumber = freez.getBtcNumber();
        String userId = freez.getUserId();
        String userName = freez.getUserName();

        String otherSql = "";
        if (freez.getType() == FreezType.cashUnFreez.getKey()) {
            //提现解冻的要记录值
            otherSql = "withdrawFreeze=withdrawFreeze-" + btcNumber;
        }
        sqls.add(new OneSql("update pay_user_wallet set " + otherSql + " where userId=? and withdrawFreeze>=? AND fundsType = ?", 1,
                new Object[]{userId, btcNumber, fundsType}));

        sqls.add(fundsDao.addBill(Integer.parseInt(userId), userName, btcNumber, fee, billType.getKey(), freez.getReMark(), "0", fundsType));
        /*Start by guankaili 20190110 账户体系分离调整 */
        if (isReduce) {
//			sqls.add(new OneSql("update pay_user set "+otherSql+" where userId=? and withdrawFreeze>=? AND fundsType = ?",1,
//					new Object[] {userId, btcNumber, fundsType }));
            //Start by  kinghao 20190121 修改sql异常

            //Start by  gkl 20190214 提现解冻成功后添加提现流水(由于是从划转迁移过来的，对账需要类型，所以先按照币币账户划至我的钱包的类型处理，)
            String reMarkOut = "资金划转出";
            String outNowSql = "INSERT INTO bill (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType) " +
                    "SELECT " + userId + ",'" + userName + "','" + Integer.valueOf(BillType.bibiToWalletOut.getKey()) + "'," + btcNumber + ",'" + now() + "','" + reMarkOut + "'," + BigDecimal.ZERO
                    + ",balance+freez as balance," + fundsType + " from pay_user where userId=" + userId + " AND fundsType = " + fundsType + " for update";
            sqls.add(new OneSql(outNowSql, 1, new Object[]{}));

            String reMarkIn = "资金划转入";
            String inNowSql = "INSERT INTO bill_wallet (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType) " +
                    "SELECT " + userId + ",'" + userName + "','" + Integer.valueOf(BillType.bibiToWalletIn.getKey()) + "'," + btcNumber + ",'" + now() + "','" + reMarkIn + "'," + BigDecimal.ZERO
                    + ",balance+freez+withdrawFreeze+" + btcNumber + " as balance," + fundsType + " from pay_user_wallet where userId=" + userId + " AND fundsType = " + fundsType + " for update";
            sqls.add(new OneSql(inNowSql, 1, new Object[]{}));
            //end
            //去掉冻结明细
            //end
            //划转记录
            String fundTransferLogSql = "INSERT INTO fund_transfer_log (uid, amount, fundType, src, dst, time) " +
                    "VALUES (" + userId + "," + btcNumber + "," + fundsType + "," + 2 + "," + 1 + ",'" + now() + "')";
            sqls.add(new OneSql(fundTransferLogSql, 1, new Object[]{}));

        }
//		else {// 失败解冻，那么需要增加资金回来
//			sqls.add(new OneSql("update pay_user_wallet set balance=balance+? where userId=? and fundsType = ?",1,
//					new Object[] { btcNumber, freez.getUserId(), fundsType }));
//		}
        /*End*/
    }

    /****
     * 在原方法的基础上加参数
     * @param sqls sqls相关
     * @param freez  解冻相关
     * @param isReduce 是否扣除
     * @return
     */
    public void unFreezPayUserSqls(Timestamp billTime,List<OneSql> sqls, FreezeBean freez, BillType billType, boolean isReduce, BigDecimal fee, Timestamp blockTime, String txidN,long downloadId) {

        int fundsType = coint.getFundsType();

        BigDecimal btcNumber = freez.getBtcNumber();
        String userId = freez.getUserId();
        String userName = freez.getUserName();

        String otherSql = "";
        if (freez.getType() == FreezType.cashUnFreez.getKey()) {
            //提现解冻的要记录值
            otherSql = "withdrawFreeze=withdrawFreeze-" + btcNumber;
        }
        sqls.add(new OneSql("update pay_user_wallet set " + otherSql + " where userId=? and withdrawFreeze>=? AND fundsType = ?", 1,
                new Object[]{userId, btcNumber, fundsType}));

        sqls.addAll(fundsDao.addBill(billTime,Integer.parseInt(userId), userName, btcNumber, blockTime, txidN, fee, billType.getKey(), freez.getReMark(), "0", fundsType,downloadId));
        /*Start by guankaili 20190110 账户体系分离调整 */
        if (isReduce) {
//			sqls.add(new OneSql("update pay_user set "+otherSql+" where userId=? and withdrawFreeze>=? AND fundsType = ?",1,
//					new Object[] {userId, btcNumber, fundsType }));
            //Start by  kinghao 20190121 修改sql异常

            //Start by  gkl 20190214 提现解冻成功后添加提现流水(由于是从划转迁移过来的，对账需要类型，所以先按照币币账户划至我的钱包的类型处理，)
            String reMarkOut = "资金划转出";
            String outNowSql = "INSERT INTO bill (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType) " +
                    "SELECT " + userId + ",'" + userName + "','" + Integer.valueOf(BillType.bibiToWalletOut.getKey()) + "'," + btcNumber + ",'" + now() + "','" + reMarkOut + "'," + BigDecimal.ZERO
                    + ",balance+freez as balance," + fundsType + " from pay_user where userId=" + userId + " AND fundsType = " + fundsType + " for update";
            sqls.add(new OneSql(outNowSql, 1, new Object[]{}));

            String reMarkIn = "资金划转入";
            String inNowSql = "INSERT INTO bill_wallet (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType) " +
                    "SELECT " + userId + ",'" + userName + "','" + Integer.valueOf(BillType.bibiToWalletIn.getKey()) + "'," + btcNumber + ",'" + now() + "','" + reMarkIn + "'," + BigDecimal.ZERO
                    + ",balance+freez+withdrawFreeze+" + btcNumber + " as balance," + fundsType + " from pay_user_wallet where userId=" + userId + " AND fundsType = " + fundsType + " for update";
            sqls.add(new OneSql(inNowSql, 1, new Object[]{}));
            //end
            //去掉冻结明细
            //end
            //划转记录
            String fundTransferLogSql = "INSERT INTO fund_transfer_log (uid, amount, fundType, src, dst, time) " +
                    "VALUES (" + userId + "," + btcNumber + "," + fundsType + "," + 2 + "," + 1 + ",'" + now() + "')";
            sqls.add(new OneSql(fundTransferLogSql, 1, new Object[]{}));

        }
//		else {// 失败解冻，那么需要增加资金回来
//			sqls.add(new OneSql("update pay_user_wallet set balance=balance+? where userId=? and fundsType = ?",1,
//					new Object[] { btcNumber, freez.getUserId(), fundsType }));
//		}
        /*End*/
    }


    /**
     * 委托成交解冻并扣除资金
     *
     * @param sqls
     * @param freez
     * @param
     */
    public void unFreezSqls(List<OneSql> sqls, FreezeBean freez, BillType billType, String webId) {

        int fundsType = coint.getFundsType();
        long id = freez.getFreezeId();
        Timestamp now = now();
        BigDecimal btcNumber = freez.getBtcNumber();
        String userId = freez.getUserId();
        String userName = freez.getUserName();
        sqls.add(new OneSql("update pay_user set freez=freez-? where userid=? and freez>=? AND fundsType = ?", 1, new Object[]{btcNumber, userId, btcNumber, fundsType}));

//		String freezeSql = "INSERT INTO "+getTableName()+" (freezeId,userId, userName, freezeTime, type,btcNumber,statu, connectionId,reMark ,freezeBanlance) " + "select ?,?,?,?,?,?,?,?,?,freez from pay_user where userId=? AND fundsType = ?";
//		sqls.add(new OneSql(freezeSql, 1, new Object[] { id, userId, userName, now, freez.getType(), btcNumber, 1, freez.getConnectionId(), freez.getReMark(), userId, fundsType }));

        sqls.add(fundsDao.addBill(Integer.parseInt(userId), userName, btcNumber, BigDecimal.ZERO, billType.getKey(), billType.getValue() + "(" + webId + ")", "0", fundsType));
    }
}
