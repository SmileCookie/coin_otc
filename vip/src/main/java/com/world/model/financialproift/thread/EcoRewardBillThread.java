package com.world.model.financialproift.thread;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.pay.FreezDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.financialproift.FinProfitAssignDetail;
import com.world.util.date.TimeUtil;
import com.world.util.financialproift.FinancialProiftUtils;

public class EcoRewardBillThread extends Thread {

    private static Logger log = Logger.getLogger(EcoRewardBillThread.class.getName());
    FreezDao freezDao = new FreezDao();
    public EcoRewardBillThread(FinProfitAssignDetail finProfitAssignDetail, CountDownLatch countDownLatch) {
        this.finProfitAssignDetail = finProfitAssignDetail;
        this.countDownLatch = countDownLatch;
    }

    private FinProfitAssignDetail finProfitAssignDetail;
    private CountDownLatch countDownLatch;

	@Override
    public void run() {

        try {
        	/*4、事务执行本次收益分配结算信息*/
            TransactionObject txObj = new TransactionObject();
            List<OneSql> sqls = new ArrayList<>();
            long currentTime = System.currentTimeMillis();
            String currentDate = TimeUtil.getFormatCurrentDateTime20();
            //获取Work中传入的该笔分配的信息
            Integer profitUserId = finProfitAssignDetail.getProfituserid();
            String profitUserName = finProfitAssignDetail.getProfitusername();
            String profitVid = finProfitAssignDetail.getProfitvid();
            BigDecimal profitAmount = finProfitAssignDetail.getProfitamount().setScale(8, BigDecimal.ROUND_DOWN);
            BigDecimal usdtAmount = finProfitAssignDetail.getUsdtamount();
            String assignIds = finProfitAssignDetail.getAssignIds();
            CoinProps coin = DatabasesUtil.coinProps(51);
            BigDecimal fees = coin.getMinFees();
            //获得用户结算的分配表ID
            if (assignIds.endsWith(",")) {
                assignIds = assignIds.substring(0, assignIds.length() - 1);
            }
            String[] assignIdArr = assignIds.split(",");

            log.info("待结算用户 profitUserId = " + profitUserId
                    + ", 待结算VDS profitAmount = " + profitAmount.toPlainString()
                    + ", 待结算USDT usdtAmount  = " + usdtAmount.toPlainString()
                    + ", 结算来源ID = " + assignIds);
            /*SQL*/
            String sql = "";
            /*出局判断*/
			boolean outUserFlag = false;
			outUserFlag = FinancialProiftUtils.outUserJudge(profitUserId + "");
			/*流水类型*/
			int fundsTypeSelf = 5350;
            String fundsNameSelf = "VDS生态回馈";
			if (outUserFlag) {
				/**
				 * 3、公司理财账户点位资金接收	pay_user_financial balance = balance + douProfitAmount
				 * lvwa1900@163.com
				 * 用户ID：1216832 
				 */
				sql = "update pay_user_financial set balance = balance + " + profitAmount + " "
					+ "where userid = 1216832 and fundstype = 51 ";
				log.info("理财报警:HierarchyThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
				/*4、1216832 记录流水fundsTypeComp  fundsNameComp */
				sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
					+ "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
					+ "select 1216832, userName, " + fundsTypeSelf + ", " + profitAmount + ", "
					+ "" + currentTime + " , (balance + profit + insureInvestFreezeAmount), "
					+ "51, '" + fundsNameSelf + "', '" + profitUserId + "' , 0, 0, 0 "
					+ "from pay_user_financial where userid = 1216832 and fundstype = 51 for update";
				log.info("理财报警:HierarchyThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
			} else {
				/** 线程处理整体业务逻辑
	             * 1、vip_main - 增加用户生态奖励收益金额
	             * 2、vip_main - 增加用户生态奖励收益结算流水
	             * 3、vip_financial - 将本次分配的结算标记 billStatus 修改为1 已结算
	             * 4、事务执行本次收益分配结算信息
	             */
	            /**
	             * 1	pay_user_financial 	更新 ecologySystemAmount（生态回馈VDS数量），更新 ecologySystemAmountUsdt（生态回馈USDT数量） 资金类型 51（VDS）
	             * 2	bill_financial		存入 type 类型5350 增加分配的VDS金额
	             * 3	pay_user_financial  更新 balance 余额字段，扣除VDS生态回馈金额
	             * 4	bill_financial		存入 type 5110 资金划转出理财至钱包
	             * 5	pay_user_wallet		更新钱包 balance 余额字段，增加VDS生态回馈金额
	             * 6	bill_wallet			存入 type 1250 资金划转入理财至钱包
	             * 7	fund_transfer_log	存入划转记录
	             * 8	vdsdownload			存入提现记录
	             * 9	downloadsummary		存入提现记录汇总表
	             * 10	pay_user_wallet		更新 balance 余额字段 扣除VDS生态回馈金额
	             * 11	fin_profit_assign_detail	更新结算标志 flag = 1
	             */
	            /*1.1、vip_main - 增加用户生态奖励收益金额(币种 VDS) 更新 pay_user_financial 表余额 balance 字段*/
	            /*ecologySystemAmount, ecologySystemAmountUsdt*/
	            sql = "update pay_user_financial set "
	            	+ "balance = balance + " + profitAmount + ", "
	            	+ "ecologySystemAmount = ecologySystemAmount + " + profitAmount + ", "
	            	+ "ecologySystemAmountUsdt = ecologySystemAmountUsdt + " + usdtAmount + " "
	            	+ "where userid = " + profitUserId + " and fundstype = 51 ";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
//	            transactionObject.excuteUpdate(new OneSql(sql, 1, null, "vip_main"));
	            /*2、vip_main - 增加用户生态奖励收益结算流水 流水类型为 5350 */
	            /**
	             * 1	pay_user_financial 	更新 ecologySystemAmount（生态回馈VDS数量），更新 ecologySystemAmountUsdt（生态回馈USDT数量） 资金类型 51（VDS）
	             * 2	bill_financial		存入 type 类型5350 增加分配的VDS金额
	             */
	            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
	                + "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
	                + "select " + profitUserId + ", userName, 5350 , " + profitAmount + ","
	                + "'" + currentTime + "' , (balance + profit + insureInvestFreezeAmount), "
	                + "51, 'VDS生态回馈', '分配来源ID个数:" + assignIdArr.length + "', " + usdtAmount + ", 0, 0 "
	                + "from pay_user_financial where userid = " + profitUserId + " and fundstype = 51 for update ";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
	            //sqls.add(new OneSql(sql, 1, null, "vip_main"));
//	            transactionObject.excuteUpdate(new OneSql(sql, 1, null, "vip_main"));
	            /**
	             * 生成划转记录 从理财到钱包
	             * 保存提现地址 finProfitAssignDetail  profitvid 提币地址 有可能没在用户提现地址
	             * 生成打币记录   分红打币
	             * 
	             */
	            //理财资金扣减
	            /*sqls.add(new OneSql("update pay_user_financial set balance = balance - ? where userId=? AND fundsType = ? and balance > ?",1,
	                    new Object[] {profitUserId, profitAmount, 51 }));*/
	            /** 线程处理整体业务逻辑
	             * 1、vip_main - 生成划转记录
	             * 2、vip_main - 增加用户生态奖励收益结算流水
	             * 3、vip_financial - 将本次分配的结算标记 billStatus 修改为1 已结算
	             * 4、事务执行本次收益分配结算信息
	             */
	            sql = "update pay_user_financial set balance = balance - " + profitAmount + " "
	            	+ "where userId = " + profitUserId + " and fundsType = 51 and balance >= " + profitAmount + "";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
//	            transactionObject.excuteUpdate(new OneSql("update pay_user_financial set balance = balance - ? 
//	            where userId=? AND fundsType = ? and balance > ?",1,
//	                    new Object[] {profitUserId, profitAmount, 51,profitAmount }));
	            //钱包资金累加
	            /*sqls.add(new OneSql("update pay_user_walllet set balance = balance + ? where userId=? AND fundsType = ?",1,
	                    new Object[] {profitUserId, profitAmount, 51 }));*/
	            sql = "update pay_user_wallet set balance = balance + " + profitAmount + " "
	            	+ "where userId = " + profitUserId + " and fundsType = 51 ";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
//	            transactionObject.excuteUpdate(new OneSql("update pay_user_walllet set balance = balance + ? where userId=? AND fundsType = ?",1,
//	                    new Object[] {profitUserId, profitAmount, 51 }));
	            //理财流水出
	            String reMarkOut = "资金划转出理财至钱包";
	            sql = "INSERT INTO bill_financial (userId, userName, type, amount, createTime, remark, fees, balance, fundsType) " 
	            	+ "SELECT " + profitUserId + ", userName, 5110, " + profitAmount + ", " + currentTime + ", '" + reMarkOut + "', " + BigDecimal.ZERO
	                +", (balance + profit + insureInvestFreezeAmount) as balance, 51 "
	                + "from pay_user_financial where userId = " +profitUserId + " AND fundsType = 51 for update ";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
	            //sqls.add(new OneSql(outNowSql, 1, new Object[]{}));
//	            transactionObject.excuteUpdate(new OneSql(outNowSql, 1, new Object[]{}));
	            //钱包流水出
	            String reMarkIn = "资金划转入理财至钱包";
	            sql = "INSERT INTO bill_wallet (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType) " 
	            	+ "SELECT " + profitUserId + ", userName, 1250, " + profitAmount + ", '" + currentDate + "', '" + reMarkIn + "', "
	            	+ "" + BigDecimal.ZERO + ", (balance + freez + withdrawFreeze), 51 from pay_user_wallet "
	            	+ "where userId = " + profitUserId + " AND fundsType = 51 for update ";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
	            //sqls.add(new OneSql(inNowSql, 1, new Object[]{}));
//	            transactionObject.excuteUpdate(new OneSql(inNowSql, 1, new Object[]{}));
	            //资金划转表记录
	            sql = "INSERT INTO fund_transfer_log (uid, amount, fundType, src, dst, time) "
	            	+ "VALUES (" + profitUserId + ", " + profitAmount + ", 51, 5, 1, '" + currentDate + "')";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
//	            String fundTransferLogSql = "INSERT INTO fund_transfer_log (uid, amount, fundType, src, dst, time) " +
//	                    "VALUES ("+profitUserId+","+profitAmount+","+51+","+5+","+1+",'"+time+"')";
	            //sqls.add(new OneSql(fundTransferLogSql, 1, new Object[]{}));
//	            transactionObject.excuteUpdate(new OneSql(fundTransferLogSql, 1, new Object[]{}));
	            //TODO:用户提现地址表数据量较大,暂不插入
	            //提现记录表插入：vdsdownload 与downloadsummary
//	            BigDecimal amountBtc = Price.getCoinBtcPrice("vds").multiply(profitAmount).setScale(8, BigDecimal.ROUND_DOWN);
	            BigDecimal amountBtc = BigDecimal.ZERO;
	            String uuid = UUID.randomUUID().toString();
	            String opUnique = profitUserId + "_";
	            /*sqls.add(new OneSql("INSERT INTO vdsdownload (userId, userName, amount, submitTime, status, managerId, manageName, toAddress, remark, isDel, fees, opUnique, uuid, amountBtc, memo, addressMemo) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", -2, new Object[] {profitUserId, finProfitAssignDetail.getProfitusername(), profitAmount, time, 0,// 状态
	                    0, "", finProfitAssignDetail.getProfitvid(), "", 0, fees, opUnique, uuid, amountBtc, "", ""}));*/
	            sql = "INSERT INTO vdsdownload (userId, userName, amount, submitTime, status, managerId, manageName, toAddress, remark, "
	            	+ "isDel, fees, opUnique, uuid, amountBtc, memo, addressMemo) "
	            	+ "VALUES (" + profitUserId + ", '" + profitUserName + "', " + profitAmount + ",  '" + currentDate + "', "
	            	+ "0, 0, '', '" + profitVid + "', 'VDS生态回馈提现', 0, " + fees+ ", '" + opUnique + "', "
	            	+ "'" + uuid + "', " + amountBtc + ",'', '')";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            txObj.excuteUpdate(new OneSql(sql, 1, null, "vip_main"));
//	            transactionObject.excuteUpdate(new OneSql("INSERT INTO vdsdownload (userId, userName, amount, submitTime, 
//	            status, managerId, manageName, toAddress, remark, isDel, fees, opUnique, uuid, amountBtc, memo, addressMemo) " 
//	            	+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", -2, 
//	            	new Object[] {profitUserId, finProfitAssignDetail.getProfitusername(), profitAmount, time, 0,// 状态
//	                    0, "", finProfitAssignDetail.getProfitvid(), "VDS分红提现", 0, fees, opUnique, uuid, amountBtc, "", ""}));

//	            String sqlCount = "SELECT LAST_INSERT_ID()";
//	            BigInteger downloadId = BigInteger.ZERO;
//	            List<BigInteger> list = (List<BigInteger>) Data.GetOne(sqlCount, null);
//	            if(list != null && list.size() > 0){
//	            	downloadId = list.get(0);
//	            } else {
//	            	txObj = null;
//	            	sqls = null;
//	            	return;
//	            }
	            
	            String sqlCount = "SELECT LAST_INSERT_ID()";
	            BigInteger downloadId = BigInteger.ONE;
	            List<Object> list = (List<Object>) txObj.excuteQuery(new OneSql(sqlCount, 1, new Object[]{}, "vip_main"));
	            if (list != null && list.size() > 0) {
	            	downloadId = (BigInteger) list.get(0);
	            } else {
	            	txObj = null;
	            	sqls = null;
	            	return;
	            }
	            if (downloadId.compareTo(BigInteger.ZERO) <= 0) {
	            	txObj = null;
	            	sqls = null;
	            	return;
	            }
	            
	            
	            //提现汇总表中增加记录
	            /*sqls.add(new OneSql("INSERT INTO downloadsummary (downloadId, fundsType, userId, userName, amount, submitTime, status, managerId, manageName, toAddress, remark, isDel, fees, opUnique, uuid, amountBtc, memo, addressMemo) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", -2, new Object[] { downloadId, 51, profitUserId, finProfitAssignDetail.getProfitusername(), profitAmount,time, 0,// 状态
	                    0, "", finProfitAssignDetail.getProfitvid(), "", 0, fees, opUnique, uuid, amountBtc, "", ""}))*/;
	            sql = "INSERT INTO downloadsummary (downloadId, fundsType, userId, userName, amount, submitTime, status, managerId, manageName, "
	            	+ "toAddress, remark, isDel, fees, opUnique, uuid, amountBtc, memo, addressMemo) "
	            	+ "values (" + downloadId + ", 51, " + profitUserId + ", '" + profitUserName + "', " + profitAmount + ", '" + currentDate + "', "
	            	+ "0, 0, '', '" + profitVid + "', 'VDS生态回馈提现', 0, " + fees + ", '" + opUnique + "', "
	            	+ "'" + uuid + "', " + amountBtc + ", '', '')";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            txObj.excuteUpdate(new OneSql(sql, 1, null, "vip_main"));
//	                    transactionObject.excuteUpdate(new OneSql("INSERT INTO downloadsummary (downloadId, fundsType, userId, userName, amount, "
//	                    		+ "submitTime, status, managerId, manageName, toAddress, remark, isDel, fees, opUnique, uuid, amountBtc, "
//	                    		+ "memo, addressMemo) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", -2, 
//	                    		new Object[] { count, 51, profitUserId, finProfitAssignDetail.getProfitusername(), profitAmount,time, 0,// 状态
//	                    0, "", finProfitAssignDetail.getProfitvid(), "VDS分红提现", 0, fees, opUnique, uuid, amountBtc, "", ""}));
	            sql = "update pay_user_wallet set balance = balance - " + profitAmount + ", withdrawFreeze = withdrawFreeze + " + profitAmount + " "
	            	+ "where userId = " + profitUserId + " and balance >= " + profitAmount + " AND fundsType = 51 ";
	            log.info("理财报警:EcoRewardBillThread sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
			}

            
//            FreezeBean freez = new FreezeBean(profitUserId.toString(), finProfitAssignDetail.getProfitusername(), "VDS" + "提现", FreezType.download.getKey(), profitAmount, 0, 0);
//            freezDao.walletFreez(sqls, freez,51);
            /*3、vip_financial - 将 fin_profit_assign_detail 表本次分配集的结算标记 flag 修改为1 已结算*/
            sql = "update fin_profit_assign_detail set flag = 1 where id in (" + assignIds + ")";
            log.info("理财报警:EcoRewardBillThread sql = " + sql);
            sqls.add(new OneSql(sql, assignIdArr.length, null, "vip_financial"));
            
            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
//                log.info("理财报警REWARDINFO:profitUserId【" + profitUserId + "】生态回馈结算成功");
            } else {
                log.info("理财报警REWARDERROR:profitUserId【" + profitUserId + "】 生态回馈结算失败");
            }
        } catch (Exception e) {
            log.info("理财报警REWARDERROR:EcoRewardAssignThread", e);
        } finally {
            countDownLatch.countDown();
        }

    }

}
