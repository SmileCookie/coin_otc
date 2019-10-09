package com.world.model.financialproift.userfininfo.thread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.entity.financialproift.UserFinancialInfo;

public class ResetProfitThread extends Thread {
	
	private static Logger log = Logger.getLogger(ResetProfitThread.class.getName());
	private String userId;
	private String userName;
	
	public ResetProfitThread (String userId, String userName) {
		this.userId = userId;
		this.userName = userName;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			/**
			 * 先判断是否触发了复投条件
			 * A：在7月20日投资188VOLLAR，市场价格3USDT，投资基数：564USDT，理论收益：846USDT。
			 * A:在8月1日获得超级节点奖励10USDT，新人奖励10USDT。
			 * A在8月2日划转1000VOLLAR，扣除50VOLLAR。
			 * 划转时判断50VOLLAR≥（10+10）/1.5/3=4.44
			 * 50-4.44归超级节点，4.44自动复投，小于时累计
			 */
			/*执行SQL*/
			String sql = "";
			/**
			 * 各金额字段
			 * curstaticProfit			当前静态收益每次复投清空 
			 * curstaticProfitUsdt		当前静态收益折算USDT每次复投清空
			 * reInvestment				复投基金
			 * reInvestmentUsdt			复投基金USDT
			 */
			BigDecimal curstaticProfit = BigDecimal.ZERO;
			BigDecimal curstaticProfitUsdt = BigDecimal.ZERO;
			BigDecimal reInvestment = BigDecimal.ZERO;
			BigDecimal reInvestmentUsdt = BigDecimal.ZERO;
			/*判断复投资金是否大于当前静态收益(公式加工)*/
			sql = "select curstaticProfit, curstaticProfitUsdt, reInvestment, reInvestmentUsdt from pay_user_financial "
				+ "where userId = " + userId + " and fundstype = 51 and reInvestment > 0 and curstaticProfitUsdt > 0 ";
			log.info("ResetProfitThread sql = " + sql);
			List<BigDecimal> listPayUserFinancial = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
			if (null == listPayUserFinancial || listPayUserFinancial.size() < 1) {
				log.info("理财报警INFO:动态收益划转，复投判断，不需要复投" + userId);
				return;
			} else {
				if (null != listPayUserFinancial.get(0)) {
					curstaticProfit = listPayUserFinancial.get(0);
				}
				if (null != listPayUserFinancial.get(1)) {
					curstaticProfitUsdt = listPayUserFinancial.get(1);
				}
				if (null != listPayUserFinancial.get(2)) {
					reInvestment = listPayUserFinancial.get(2);
				}
				if (null != listPayUserFinancial.get(3)) {
					reInvestmentUsdt = listPayUserFinancial.get(3);
				}
			}
			log.info("reInvestment = " + reInvestment + ", reInvestmentUsdt = " + reInvestmentUsdt + ", "
					+ "curstaticProfit = " + curstaticProfit + ", curstaticProfitUsdt = " + curstaticProfitUsdt);
			/*reInvestment >= curstaticProfitUsdt / 1.5 / 投资时的市场价格 */
			/*当前投资矩阵*/
			String curMatrixLevel = "0";
			BigDecimal curExpectProfitUsdt = BigDecimal.ZERO;
			BigDecimal curInvestAvergPrice = BigDecimal.ZERO;
			BigDecimal vipWeight = BigDecimal.ZERO;
			/*投资次数*/
			int reinTimes = 0;
			sql = "select matrixLevel, expectProfitUsdt, investAvergPrice, reinTimes, vipWeight from fin_userfinancialinfo "
				+ "where userId = " + userId + " and authPayFlag != 1 ";
			log.info("ResetProfitThread sql = " + sql);
			UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
			if (null != userFinancialInfo) {
				curMatrixLevel = userFinancialInfo.getMatrixLevel() + "";
				curExpectProfitUsdt = userFinancialInfo.getExpectProfitUsdt();
				curInvestAvergPrice = userFinancialInfo.getInvestAvergPrice();
				reinTimes = userFinancialInfo.getReinTimes();
				vipWeight = userFinancialInfo.getVipWeight();
			} else {
				log.info("理财报警ERROR:动态收益划转，复投判断，获取用户投资时USDT价格初始化失败" + userId);
				return;
			}
			
			if (curInvestAvergPrice.compareTo(BigDecimal.ZERO) <= 0) {
				log.info("理财报警ERROR:动态收益划转，复投判断，获取用户投资时USDT价格初始化失败:" + userId + ", " + curInvestAvergPrice);
				return;
			}
			log.info("curMatrixLevel = " + curMatrixLevel + ", curExpectProfitUsdt = " + curExpectProfitUsdt + ", "
					+ "curInvestAvergPrice = " + curInvestAvergPrice + ", reinTimes = " + reinTimes);
			/*复投资金计算*/
			BigDecimal doubleThrowAmount = BigDecimal.ZERO;
			doubleThrowAmount = curstaticProfitUsdt.divide(curInvestAvergPrice.multiply(BigDecimal.valueOf(1.5)), 4, BigDecimal.ROUND_DOWN);
			if (reInvestment.compareTo(doubleThrowAmount) < 0) {
				log.info("理财报警INFO:动态收益划转，复投判断，不需要复投" + userId + ", 复投基金 = " + reInvestment + ", 本次需要复投金额 = " + doubleThrowAmount);
				return;
			}
			
			/**
			 * 可以复投
			 */
			/*开启事务*/
			List<OneSql> sqls = new ArrayList<>();
	        TransactionObject txObj = new TransactionObject();
	        long currentTime = System.currentTimeMillis();
	        BigDecimal xxxUsdt = doubleThrowAmount.multiply(curInvestAvergPrice);
	        BigDecimal yyyUsdt = doubleThrowAmount.multiply(curInvestAvergPrice.multiply(BigDecimal.valueOf(1.5)));
	        /*1、插入表productinvest 产品投资表*/
	        /*自动复投标志，0首投，1, 为增投，2释放冻结资金触发 自动复投, 3为手动复投*/
			sql = "insert into fin_productinvest (userId, userName, fundsType, proId, investAmount, investProPeriod, "
				+ "vdsUsdtPrice, investUsdtAmount, expectProfitUsdt, investTime, matrixLevel, doubleThrowFlag, vipWeight) values "
				+ "(" + userId + ", '" + userName + "', 51, 'BWFP1', " + doubleThrowAmount + ", (1 + " + (reinTimes + 1) + "), "
				+ "" + curInvestAvergPrice + ", "
				+ "" + xxxUsdt + ", " + yyyUsdt + ", now(), " + curMatrixLevel + ", 2, " + vipWeight + ")";
			log.info("ResetProfitThread productinvest sql = " + sql);
			sqls.add(new OneSql(sql, 1, null, "vip_financial"));
			
			/**
			 * 1-1 修改 fin_userfinancialinfo  reinTimes
			 */
			sql = "update fin_userfinancialinfo set reinTimes = " + (reinTimes + 1) + " where userId = " + userId;
			log.info("ResetProfitThread fin_userfinancialinfo sql = " + sql);
			sqls.add(new OneSql(sql, 1, null, "vip_financial"));
			/*2、更新product 理财产品表 proTotalUser，proTotalAmount*/
			/*复投，人数不增加，只增加金额*/
			sql = "update fin_product set proTotalAmount = proTotalAmount + " + doubleThrowAmount + "";
			log.info("ResetProfitThread product sql = " + sql);
			sqls.add(new OneSql(sql, 1, null, "vip_financial"));
			
			/**
			 * 2- 1记录复投记录
			 */
			/*转入超级节点资金*/
			boolean toSuperNodeFlag = false;
			BigDecimal toSuperNodeAmount = reInvestment.subtract(doubleThrowAmount);
			if (toSuperNodeAmount.compareTo(BigDecimal.ZERO) > 0) {
				/*记录复投流水表，单独的一个表*/
				sql = "insert into fin_dou_profit_log (userId, userName, fundsType, doubleThrowAmount, toSuperNodeAmount, "
					+ "reinTimes, doubleThrowFlag, creatTime) "
					+ "values (" + userId + ", '" + userName + "', 51, " + doubleThrowAmount + ", " + toSuperNodeAmount + ", "
					+ "(1 + " + (reinTimes + 1) + "), 2, now() )";
				log.info("userAvaTransferAmount sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_financial"));
			}
			
			/**
			 * 3、更新复投基金，重置静态收益
			 * curstaticProfit			当前静态收益每次复投清空 
			 * curstaticProfitUsdt		当前静态收益折算USDT每次复投清空
			 * reInvestment				复投基金
			 * reInvestmentUsdt			复投基金USDT
			 * 余额没有变化，所以没有记录流水
			 */
			sql = "update pay_user_financial set reInvestment = 0, reInvestmentUsdt = 0, curstaticProfit = 0, curstaticProfitUsdt = 0 "
				+ "where userid = " + userId + " and fundstype = 51 "
				+ "and reInvestment - " + reInvestment + " >= 0 ";
			log.info("ResetProfitThread pay_user_financial sql = " + sql);
			sqls.add(new OneSql(sql, 1, null, "vip_main"));
			/**
			 *4、如果有多余资金转入超级节点
			 */
			if (toSuperNodeAmount.compareTo(BigDecimal.ZERO) <= 0) {
				log.info("理财报警ERROR:动态收益划转，复投判断，不需要转入超级节点" + userId + ", 复投基金 = " + reInvestment + ", 本次复投金额 = " + doubleThrowAmount);
			} else {
				/**
				 * 复投基金多余资金转入 5386	pay_user_financial balance = balance + douProfitAmount
				 * lvwa1900@163.com
				 * 用户ID：1216832 
				 */
				String fundsTypeComp = "5386";
				String fundsNameComp = "复投基金转入VIP分红奖励";
				sql = "update pay_user_financial set balance = balance + " + toSuperNodeAmount + " where userid = 1216832 and fundstype = 51 ";
				log.info("理财报警:ResetProfitThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
				/*1216832 记录流水fundsTypeComp  fundsNameComp */
				sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
					+ "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
					+ "select 1216832, userName, " + fundsTypeComp + ", " + toSuperNodeAmount + ", "
					+ "" + currentTime + " , (balance + profit + insureInvestFreezeAmount), "
					+ "51, '" + fundsNameComp + "', '" + userId + "', 0, (1 + " + (reinTimes + 1) + "), " + curMatrixLevel + " "
					+ "from pay_user_financial where userid = 1216832 and fundstype = 51 for update";
				log.info("理财报警:ResetProfitThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
				toSuperNodeFlag = true;
			}
			
			txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
            	log.info("理财报警INFO:动态收益划转，复投成功" + userId + ", 复投基金 = " + reInvestment + ", 本次复投金额  = " + doubleThrowAmount + ", 转入超级节点金额 = " + toSuperNodeAmount);
            } else {
            	log.info("理财报警ERROR:动态收益划转，复投失败" + userId + ", 复投基金 = " + reInvestment + ", 本次复投金额  = " + doubleThrowAmount + ", 转入超级节点金额 = " + toSuperNodeAmount);
            	return;
            }
			/*调用复投接口*/
        	/*调用接口进行数据对接激活*/
			boolean rollBackFlag = false;
			/*复投逻辑*/
			try {
				/*调用接口进行数据对接保存 doPostData*/
				Map<String, Object> objectMap = new HashMap<String, Object>();
				objectMap.put("userId", userId);
				objectMap.put("oldLevel", curMatrixLevel);
				objectMap.put("level", curMatrixLevel);
				objectMap.put("levelNum", doubleThrowAmount);
				objectMap.put("vdsPrice", curInvestAvergPrice.divide(BigDecimal.valueOf(1.5), 4, BigDecimal.ROUND_DOWN));
				objectMap.put("expectProfit", curExpectProfitUsdt);
				/*投资类型（2为增投，3为复投）*/
				objectMap.put("investType", 3);
				/*复投次数*/
				objectMap.put("reinTimes", (reinTimes + 1));
				log.info("objectMap = " + objectMap);
				String urlFinancial = ApiConfig.getValue("urlfinancial.url");
				urlFinancial += "/vdsapollo/op/increaseOrRein";
				log.info("urlFinancial = " + urlFinancial);
				String resultInterface = "";
				/*接口返回码和返回消息*/
				String resultInterfaceCode = "";
				String resultInterfaceMsg = "";
				resultInterface = doPostData(urlFinancial, objectMap);
				JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
				log.info("jsonResultInterface = " + jsonResultInterface);
				if (null != jsonResultInterface) {
					if (null != jsonResultInterface.getString("code")) {
						resultInterfaceCode = jsonResultInterface.getString("code");
					}
					if(null != jsonResultInterface.getString("message")) {
						resultInterfaceMsg = jsonResultInterface.getString("message");
					}
					log.info("resultInterfaceCode = " + resultInterfaceCode);
					if (!"200".equals(resultInterfaceCode)) {
						if ("400".equals(resultInterfaceCode)) {
							if (!"用户已激活FFF".equals(resultInterfaceMsg)) {
								rollBackFlag = true;
								log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
							}
						} else {
							rollBackFlag = true;
							log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
						}
					}
				} else {
					rollBackFlag = true;
					log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
				}
			} catch (Exception e) {
				rollBackFlag = true;
				log.info("理财报警INTERFACE:userProductInfoPay", e);
			}
			/**
			 * 回滚操作
			 * 流水此处可删除，但是如果是复投就不能直接这么做
			 */
			if (rollBackFlag) {
				sqls = new ArrayList<>();
		        txObj = new TransactionObject();
		        /**
		         * 
		         */
		        /*1、回滚投资记录*/
		        sql = "delete from fin_productinvest "
			        + "where userId = " + userId + " and investProPeriod = (1 + " + (reinTimes + 1) + ") "
			        + "and matrixLevel = " + curMatrixLevel + "";
		        log.info("理财报警:ResetProfitThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_financial"));
				/**
				 * 1-1 修改 fin_userfinancialinfo  reinTimes
				 */
				sql = "update fin_userfinancialinfo set reinTimes = " + (reinTimes) + " where userId = " + userId;
				log.info("ResetProfitThread fin_userfinancialinfo sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_financial"));
				/*2、回滚，更新product 理财产品表 proTotalUser，proTotalAmount*/
				sql = "update fin_product set proTotalAmount = proTotalAmount - " + doubleThrowAmount + "";
				log.info("理财报警:ResetProfitThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_financial"));
				/**
				 * 2-1
				 */
				if (toSuperNodeAmount.compareTo(BigDecimal.ZERO) > 0) {
					/*记录复投流水表，单独的一个表*/
					sql = "delete from fin_dou_profit_log "
						+ "where userId = " + userId + " and fundsType = 51 and reinTimes = (1 + " + (reinTimes + 1) + ")";
					log.info("userAvaTransferAmount sql = " + sql);
					sqls.add(new OneSql(sql, 1, null, "vip_financial"));
				}
				/*3、回滚复投金额*/
				sql = "update pay_user_financial set "
					+ "reInvestment = " + reInvestment + ", "
					+ "reInvestmentUsdt = " + reInvestmentUsdt + ", "
					+ "curstaticProfit = " + curstaticProfit + ", "
					+ "curstaticProfitUsdt = " + curstaticProfitUsdt + " "
					+ "where userid = " + userId + " and fundstype = 51 ";
				log.info("理财报警:ResetProfitThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
				if (toSuperNodeFlag) {
					sql = "update pay_user_financial set balance = balance - " + toSuperNodeAmount + " where userid = 1216832 and fundstype = 51";
					log.info("理财报警:ResetProfitThread sql = " + sql);
					sqls.add(new OneSql(sql, 1, null, "vip_main"));
					
					sql = "delete from bill_financial where userid = 1216832 and fundstype = 51 and type = 5386 "
						+ "and investProPeriod = (1 + " + (reinTimes + 1) + ") and remark = '" + userId + "' ";
					log.info("理财报警:ResetProfitThread sql = " + sql);
					sqls.add(new OneSql(sql, 1, null, "vip_main"));
				}
				
				/*回滚处理*/
				txObj.excuteUpdateList(sqls);
	            if (txObj.commit()) {
	            	/*回滚成功*/
	            	log.info("理财报警WARN:动态收益划转，复投回滚成功" + userId + ", 复投基金 = " + reInvestment + ", 本次复投金额  = " + doubleThrowAmount + ", 转入超级节点金额 = " + toSuperNodeAmount);
	            } else {
	            	log.info("理财报警ERROR:动态收益划转，复投回滚失败" + userId + ", 复投基金 = " + reInvestment + ", 本次复投金额  = " + doubleThrowAmount + ", 转入超级节点金额 = " + toSuperNodeAmount);
	            }
	            
			}
			
		} catch (Exception e) {
			log.info("理财报警ERROR:ResetProfitThread", e);
		} finally {
			/*资金变动，重新更新资金池*/
            Cache.Delete("user_financial_" + userId);
    	}
	}
	
	public String doPostData(String url, Map<String,Object> objectMap) throws Exception {
		String result = "";
		try {
			String dataJson = com.alibaba.fastjson.JSON.toJSONString(objectMap);
	        jodd.http.HttpRequest request = jodd.http.HttpRequest.post(url);
	        request.query("data",dataJson);
	        jodd.http.HttpResponse response = request.send();
	        result = response.bodyText();
		} catch (Exception e) {
			throw new Exception(e);
		}
		return result;
	}
	
	public static void main (String[] args) {
		ResetProfitThread rpt = new ResetProfitThread("1219422", "zsh201923@163.com");
		rpt.run();
	}
	
}
