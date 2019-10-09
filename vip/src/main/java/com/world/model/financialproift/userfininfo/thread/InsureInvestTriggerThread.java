package com.world.model.financialproift.userfininfo.thread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.redis.RedisUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.entity.financialproift.FinUserInsureInvest;
import com.world.model.entity.financialproift.UserFinancialInfo;
import com.world.util.financialproift.FinancialProiftUtils;

public class InsureInvestTriggerThread extends Thread {
	/* 保险投资实体类 */
	private FinUserInsureInvest finUserInsureInvest;
	/* sql语句 */
	private String sql = "";
	private static Logger log = Logger.getLogger(InsureInvestTriggerThread.class.getName());
	private CountDownLatch countDownLatch;

	public InsureInvestTriggerThread(FinUserInsureInvest finUserInsureInvest, CountDownLatch countDownLatch) {
		this.finUserInsureInvest = finUserInsureInvest;
		this.countDownLatch = countDownLatch;
	}
	
	@Override
	public void run() {
		try {
			/**
			 * 获取需要的字段信息
			 * 用户编号，用户名，邀请码，VID
			 * 投资矩阵，投资矩阵金额，触发价格
			 */
			int id = finUserInsureInvest.getId();
			int userId = finUserInsureInvest.getUserId();
			String userName = finUserInsureInvest.getUserName();
			/*保险投资金额，触发价格*/
			BigDecimal insureInvestAmount = finUserInsureInvest.getInsureInvestAmount();
			BigDecimal triggerPrice = finUserInsureInvest.getTriggerPrice();
			log.info("id = " + id + ", userId = " + userId);
			if (insureInvestAmount.compareTo(BigDecimal.ONE) <= 0 || triggerPrice.compareTo(BigDecimal.ZERO) <= 0) {
				/*直接return*/
            	log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】用户保险数量异常 = " + userId + ", "
            			+ "投资数量 = " + insureInvestAmount + ", 触发价格 = " + triggerPrice);
            	return;
			}
			
			/**
			 * 1、先判断用户是否投资过理财，如果没有投资过则先自动生成主账号的理财记录
			 * 2、更新保险投资信息
			 */
			/*'0默认值，1已保存，2已支付,3复投中',*/
            int authPayFlag = 0;
			sql = "select authPayFlag, matrixLevel, expectProfitUsdt, outSurplusVDS, investAvergPrice, vipFlag from fin_userfinancialinfo "
	            + "where userId = " + userId;
			log.info("sql = " + sql);
            UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
            if (null != userFinancialInfo) {
                authPayFlag = userFinancialInfo.getAuthPayFlag();
            }
            log.info("authPayFlag = " + authPayFlag);
//            /*保存操作标志，支付操作标志*/
//            boolean saveState = false;
//            boolean payState = false;
//            /*后续操作标志*/
//            boolean dealState = false;
            
            if (2 == authPayFlag) {
            	userInsurancePayInterface(id, userId, userName, insureInvestAmount, triggerPrice);
            } else {
            	/*直接return*/
            	log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】用户支付状态异常 = " + userId + ", authPayFlag = " + authPayFlag);
            	return;
            }
            
//            if (2 != authPayFlag) {
//            	
//            } else if ("1".equals(authPayFlag)) {
//            	/*保存过，只需要支付*/
//            	payState = userProductInfoPayInterface(id, userId, userName, investLevel, vdsUsdt);
//            	if (payState) {
//            		dealState = true;
//            	}
//            } else {
//            	log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】用户已存在，不需要触发，用户编号 = " + userId + ", authPayFlag = " + authPayFlag);
//            	dealState = true;
//            }
			
            /*可以进行后续操作*/
//			if (dealState) {
//				/*更新投资状态*/
//	            sql = "update fin_userinsureinvest set investState = 2, triggerFlag = 1 "
//	            	+ "where id = " + id + " and triggerFlag = 0 and investState = 0 ";
//	            log.info("sql = " + sql);
//	            Data.Update("vip_financial", sql, null);
//			} else {
//				log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】触发保存失败,用户编号 = " + userId + ", "
//						+ "saveState = " + saveState + ", payState = " + payState);
//			}
		} catch (Exception e) {
			log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】InsureInvestTriggerThread", e);
		} finally {
			countDownLatch.countDown();
		}
	}
	
	@SuppressWarnings("unchecked")
    public boolean userInsurancePayInterface(int id, int userId, String userName, BigDecimal insureInvestAmount, BigDecimal bdVdsUsdtPrice) {
		boolean payState = false;
        try {
            String sql = "";
			/*参数校验,先分别设置校验不通过时的返回值*/
			/*获取产品状态标志，先从Redis获取,存放时间10秒*/
            String proState = RedisUtil.get("financial_proState");
            String proAmount = RedisUtil.get("financial_proAmount");
            BigDecimal bdProAmount = BigDecimal.ZERO;
            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);
            
			/*投资金额*/
            bdProAmount = new BigDecimal(proAmount);

            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);
            if (!"1".equals(proState)) {
				/*该产品理财投资已结束！*/
                log.info("理财报警WARN:保险投资触发投资该产品理财投资已结束");
                return payState;
            }
            
            /*先检查资金是否有足够的资金*/
            sql = "select insureInvestFreezeAmount from pay_user_financial where userid = " + userId + " and fundstype = 51";
            log.info("userProductInfoPay pay_user_financial balance sql = " + sql);
            List<BigDecimal> listPayUserFinancial = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
            BigDecimal userBalance = BigDecimal.ZERO;
            if (null == listPayUserFinancial || listPayUserFinancial.size() < 1) {
                log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】保险投资触发投资理财账户初始化失败 = " + userId);
                return payState;
            } else {
                if (null != listPayUserFinancial.get(0)) {
                    userBalance = listPayUserFinancial.get(0);
                }
                if (userBalance.compareTo(insureInvestAmount) < 0) {
                    return payState;
                }
            }
            
            /*判断用户是否满足支付条件，同时检查是否已支付过*/
            /*'0默认值，1已认证，2已支付',*/
//            String authPayFlag = "0";
            /*当前投资矩阵*/
            String curMatrixLevel = "0";
            /*投资次数*/
            int reinTimes = 0;
            /**
             * 
             */
            BigDecimal curInsureAmount = BigDecimal.ZERO;
            BigDecimal curInsureWeight = BigDecimal.ZERO;
            BigDecimal curInsureAvergPrice = BigDecimal.ZERO;
            BigDecimal curInsureExpectProfitUsdt = BigDecimal.ZERO;
            Date curInsureTime = null;
            sql = "select authPayFlag, matrixLevel, reinTimes, insureAmount, insureAvergPrice, insureWeight, insureTime, insureExpectProfitUsdt "
                + "from fin_userfinancialinfo where userId = " + userId;
            UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
            if (null != userFinancialInfo) {
            	curInsureAmount = userFinancialInfo.getInsureAmount();
            	curInsureWeight = userFinancialInfo.getInsureWeight();
            	curInsureAvergPrice = userFinancialInfo.getInsureAvergPrice();
            	curInsureExpectProfitUsdt = userFinancialInfo.getInsureExpectProfitUsdt();
                reinTimes = userFinancialInfo.getReinTimes();
                curMatrixLevel = userFinancialInfo.getMatrixLevel() + "";
                curInsureTime = userFinancialInfo.getInsureTime();
            }
            
            /*权重*/
            BigDecimal addInsureWeight = FinancialProiftUtils.giveVIPInsuranceWeight(insureInvestAmount);
            BigDecimal targetInsureWeight = curInsureWeight.add(addInsureWeight).setScale(0, BigDecimal.ROUND_DOWN);
            
			/*保险投资金额和预计收益金额预先处理*/
            BigDecimal xxxUsdt = bdVdsUsdtPrice.multiply(insureInvestAmount).setScale(4, BigDecimal.ROUND_DOWN);
            BigDecimal yyyUsdt = bdVdsUsdtPrice.multiply(insureInvestAmount.multiply(BigDecimal.valueOf(1.5))).setScale(4, BigDecimal.ROUND_DOWN);
            
            /*新的保险投资均价*/
            BigDecimal bdInsureAvergPrice = BigDecimal.ZERO;
            /*最终保险投资金额*/
            BigDecimal targetInsureAmount = insureInvestAmount.add(curInsureAmount);
            /*保险最终预期收益*/
            BigDecimal targetInsureExpectProfitUsdt = yyyUsdt.add(curInsureExpectProfitUsdt);
            bdInsureAvergPrice = targetInsureExpectProfitUsdt.divide(targetInsureAmount.multiply(BigDecimal.valueOf(1.5)), 4, BigDecimal.ROUND_DOWN);
            
            /**
             * 产品状态为开启，而且满足支付条件，开始进行支付处理。开启事务。
             * 1、插入表productinvest		产品投资表
             * 2、更新product				理财产品表		proTotalUser，proTotalAmount
             * 3、更新userfinancialinfo	用户理财信息表	
             * 4、更新 fin_userinsureinvest
             * 5、更新pay_user_financial	理财资金表		balance
             * 6、插入bill_financial 		理财流水表		理财投资188 流水类型 5301 支出
             */
            List<OneSql> sqls = new ArrayList<>();
            TransactionObject txObj = new TransactionObject();
            long currentTime = System.currentTimeMillis();
	        /*1、插入表productinvest 产品投资表  投资类型 4 保险投资*/
            sql = "insert into fin_productinvest (userId, userName, fundsType, proId, investAmount, investProPeriod, "
                + "vdsUsdtPrice, investUsdtAmount, expectProfitUsdt, investTime, matrixLevel, vipWeight, doubleThrowFlag) values "
                + "(" + userId + ", '" + userName + "', 51, 'BWFP1', " + insureInvestAmount + ", (1 + " + (reinTimes + 1) + "), " + bdVdsUsdtPrice + ", "
                + "" + xxxUsdt + ", " + yyyUsdt + ", now(), " + curMatrixLevel + ", " + targetInsureWeight + ", 4 )";
            log.info("userProductInfoPay productinvest sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            
			/*2、更新product 理财产品表 proTotalUser，proTotalAmount*/
            sql = "update fin_product set proTotalAmount = proTotalAmount + " + insureInvestAmount + "";
            log.info("userProductInfoPay product sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            
			/*3、更新userfinancialinfo 用户理财信息表 authPayFlag 0默认值，1已认证，2已支付。invitationCode*/
            /**
             * 新增字段：investAvergPrice, investAmount, expectProfitUsdt, outSurplusVDS
             * bdInvestAvergPrice 新均价
             * curExpectProfitUsdt 原始预期1.5倍收益
             */
            sql = "update fin_userfinancialinfo set "
                + "insureWeight = " + targetInsureWeight + ", "
                + "insureAvergPrice = " + bdInsureAvergPrice + ", "
                + "insureAmount = " + targetInsureAmount + ", "
                + "insureExpectProfitUsdt = " + targetInsureExpectProfitUsdt + ", "
                + "insureTime = now() "
                + "where userId = " + userId + " and authPayFlag = 2 ";
            log.info("userProductInfoPay userfinancialinfo sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            
            /*4、更新 fin_userinsureinvest*/
            sql = "update fin_userinsureinvest set "
				+ "insureInvestSurplusAmount = insureInvestSurplusAmount - investLevelAmount, "
				+ "investState = 2, triggerFlag = 1 "
				+ "where id = " + id + " "
				+ "and insureInvestSurplusAmount - investLevelAmount >=0 and triggerFlag = 0 and investState = 0 ";
			log.info("InsureInvestTriggerThread...sql = " + sql);
			sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            
			/*5、更新pay_user_financial	理财资金表 balance*/
            sql = "update pay_user_financial set insureInvestFreezeAmount = insureInvestFreezeAmount - " + insureInvestAmount + " "
                + "where userid = " + userId + " and fundstype = 51 and insureInvestFreezeAmount - " + insureInvestAmount + " >= 0";
            log.info("userProductInfoPay pay_user_financial sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
			
            /*6、插入bill_financial 理财担保投资1份，子账号排位支付投资188时记账 流水类型 5302 支出*/
            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                + "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
                + "select " + userId + ", '" + userName + "', 5302, " + insureInvestAmount + ", "
                + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
                + "51, '理财保险投资', '" + userId + "', " + bdVdsUsdtPrice + ", (1 + " + (reinTimes + 1) + "), " + curMatrixLevel + " "
                + "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
            log.info("userProductInfoPay bill_financial sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
            
            /*执行提交事务*/
            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
            	/*支付成功*/
                log.info("用户【 " + userId + "】支付成功");
            } else {
                return payState;
            }
            
            /*调用激活接口*/
        	/*调用接口进行数据对接激活*/
            boolean rollBackFlag = false;
            
            try {
            	/*调用接口进行数据对接保存 doPostData*/
                Map<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put("userId", userId);
                objectMap.put("oldLevel", curMatrixLevel);
                objectMap.put("level", curMatrixLevel);
                objectMap.put("levelNum", insureInvestAmount);
                objectMap.put("vdsPrice", bdVdsUsdtPrice);
                objectMap.put("expectProfit", yyyUsdt);
                /*自动复投标志，0首投，1, 为增投，2释放冻结资金触发 自动复投, 3为手动复投,4保险-投保*/
                objectMap.put("investType", 4);
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
                    if (null != jsonResultInterface.getString("message")) {
                        resultInterfaceMsg = jsonResultInterface.getString("message");
                    }
                    log.info("resultInterfaceCode = " + resultInterfaceCode);
                    if (!"200".equals(resultInterfaceCode)) {
                        if ("400".equals(resultInterfaceCode)) {
                            if (!"用户已激活".equals(resultInterfaceMsg)) {
                                rollBackFlag = true;
                                log.info("理财报警INTERFACE:保险投资触发投资userProductInfoPay = " + jsonResultInterface);
                            }
                        } else {
                            rollBackFlag = true;
                            log.info("理财报警INTERFACE:保险投资触发投资userProductInfoPay = " + jsonResultInterface);
                        }
                    }
                } else {
                    rollBackFlag = true;
                    log.info("理财报警INTERFACE:保险投资触发投资userProductInfoPay = " + jsonResultInterface);
                }
            } catch (Exception e) {
                rollBackFlag = true;
                log.info("理财报警INTERFACE:保险投资触发投资userProductInfoPay", e);
            }
            
            /**
             * 回滚操作，此处暂时先不回滚
             * 流水此处可删除，但是如果是复投就不能直接这么做
             */
            if (rollBackFlag) {
                sqls = new ArrayList<>();
                txObj = new TransactionObject();
				/*1、回滚，插入表productinvest 产品投资表*/
                sql = "delete from fin_productinvest where userId = " + userId + " and investProPeriod = (1 + " + (reinTimes + 1) + ") "
                    + "and matrixLevel = " + curMatrixLevel + " and doubleThrowFlag = 4 ";
                log.info("userProductInfoPay productinvest sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
				
				/*2、回滚，更新product 理财产品表 proTotalUser，proTotalAmount*/
                sql = "update fin_product set proTotalAmount = proTotalAmount - " + insureInvestAmount + "";
                log.info("userProductInfoPay product sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
				/*3、更新userfinancialinfo 用户理财信息表 authPayFlag 0默认值，1已认证，2已支付。invitationCode*/
                /**
                 * 新增字段：investAvergPrice, investAmount, expectProfitUsdt, outSurplusVDS
                 * bdInvestAvergPrice 新均价
                 * curExpectProfitUsdt 原始预期1.5倍收益
                 */
                
                sql = "update fin_userfinancialinfo set "
                    + "insureWeight = " + curInsureWeight + ", "
                    + "insureAvergPrice = " + curInsureAvergPrice + ", "
                    + "insureAmount = " + curInsureAmount + ", "
                    + "insureExpectProfitUsdt = " + curInsureExpectProfitUsdt + ", "
                    + "insureTime = " + curInsureTime + " "
                    + "where userId = " + userId + " and authPayFlag = 2 ";
                log.info("userProductInfoPay userfinancialinfo sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                
                sql = "update fin_userinsureinvest set "
    				+ "insureInvestSurplusAmount = insureInvestSurplusAmount + investLevelAmount, "
    				+ "investState = 0, triggerFlag = 0 "
    				+ "where id = " + id + " "
    				+ "and insureInvestSurplusAmount + investLevelAmount <= insureInvestAmount and triggerFlag = 1 and investState = 2 ";
    			log.info("InsureInvestTriggerThread...sql = " + sql);
    			sqls.add(new OneSql(sql, 1, null, "vip_financial"));
    			
				/*4、更新pay_user_financial	理财资金表 balance*/
                sql = "update pay_user_financial set insureInvestFreezeAmount = insureInvestFreezeAmount + " + insureInvestAmount + " "
                    + "where userid = " + userId + " and fundstype = 51 ";
                log.info("userProductInfoPay pay_user_financial sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));
                
                /*5、插入bill_financial 理财保险投资 流水类型 5302 支出*/
                sql = "delete from bill_financial where userid = " + userId + " and fundstype = 51 and type = 5302 "
                    + "and matrixLevel = " + curMatrixLevel + " and investProPeriod = (1 + " + (reinTimes + 1) + ") ";
                log.info("userProductInfoPay bill_financial sql = " + sql);
                sqls.add(new OneSql(sql, -1, null, "vip_main"));
                
				/*回滚处理*/
                txObj.excuteUpdateList(sqls);
                if (txObj.commit()) {
	            	/*支付成功*/
                    log.info("理财报警REWARDWARN:【保险投资触发监控扫描-定时任务】保险投资触发投资用户【 " + userId + "】回滚处理成功");
                } else {
                    log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】保险投资触发投资支付回滚失败,userId = " + userId);
                    return payState;
                }
            }
            
            String resultMsg = "您相当于出售 188 Vollar 获得 xxx USDT<br />理论收益为 yyy USDT";
            resultMsg = resultMsg.replaceAll("xxx", "" + xxxUsdt);
            resultMsg = resultMsg.replaceAll("yyy", "" + yyyUsdt);
            resultMsg = resultMsg.replaceAll("188", "" + insureInvestAmount);
            log.info("理财报警REWARDINFO:【保险投资触发监控扫描-定时任务】保险投资触发投资 = " + userId + " " + resultMsg);
            payState = true;
        } catch (Exception e) {
            log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】", e);
        }
        return payState;
    }
	
	public String doPostData(String url, Map<String, Object> objectMap) throws Exception {
        String result = "";
        try {
            String dataJson = com.alibaba.fastjson.JSON.toJSONString(objectMap);
            jodd.http.HttpRequest request = jodd.http.HttpRequest.post(url);
            request.query("data", dataJson);
            jodd.http.HttpResponse response = request.send();
            result = response.bodyText();
        } catch (Exception e) {
            throw new Exception(e);
        }
        return result;
    }
}
