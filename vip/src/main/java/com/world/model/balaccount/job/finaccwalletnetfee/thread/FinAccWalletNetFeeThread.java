package com.world.model.balaccount.job.finaccwalletnetfee.thread;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.model.balaccount.dao.FinAccDetailsDao;
import com.world.model.balaccount.entity.FinAccDetailsBean;
import com.world.util.date.TimeUtil;
import com.world.util.request.HttpUtil;

/**
 * <p>标题: 网络费记录对账查询</p>
 * <p>描述: 按每个币种进行处理查询</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version
 */
public class FinAccWalletNetFeeThread extends Thread {
	/*币种编号2=btc*/
	private int fundType;
	/*币种名称btc*/
	private String fundTypeName;
	/*sql语句*/
	private String sql = "";
	/*拼接后保存到数据库的SQL*/
	private String batchSql = "";
	private static Logger log = Logger.getLogger(FinAccWalletNetFeeThread.class);

	public FinAccWalletNetFeeThread(int fundType, String fundTypeName) {
		this.fundType = fundType;
		this.fundTypeName = fundTypeName;
	}

	@Override
	public void run() {
		/*按币种进行处理*/
		log.info("fundType = " + fundType + ", fundTypeName = " + fundTypeName);
		try {
			/*记录核算开始时间*/
			long startTime = System.currentTimeMillis();
			/*设置查询*/
			FinAccDetailsDao finAccDetailsDao = new FinAccDetailsDao();
			/*首先查询出该币种已同步到的区块高度*/
			sql = "select max(blockHeight) blockHeight from finAccNetFeeRecord where fundsType = " + fundType + "";
			log.info("sql = " + sql);
			FinAccDetailsBean finAccDetailsBean = null;
			finAccDetailsBean = finAccDetailsDao.getT(sql, null, FinAccDetailsBean.class);
			/*当前区块高度*/
			int currBlockHeight = 0;
			int queryBlockHeight = 0;
			if(null != finAccDetailsBean) {
				currBlockHeight = finAccDetailsBean.getBlockHeight();
				if(currBlockHeight > 0) {
					queryBlockHeight = currBlockHeight;
					queryBlockHeight --;
				}
			}
			log.info("【" + fundTypeName + "】网络费记录对账表中当前的区块高度为【" + currBlockHeight + "】");

			/**
			 * 交易平台发送查询，如果区块高度为空或者0，返回所有记录。
			 * 发送区块高度N>0，返回从N之后的数据
			 */
			/*调用接口的方法*/
			String url = ApiConfig.getValue("tradingcenter.url");
			url += "/openapi/tradingcenter/finance/netFee/record/" + fundTypeName.toUpperCase() + "?blockHeight=" + queryBlockHeight;
			log.info("url = " + url);
			String strResult = HttpUtil.doGet(url, null, 10000, 10000);
			//.parseObject(result);
			JSONObject jsonResult = com.alibaba.fastjson.JSONObject.parseObject(strResult);

			log.info("jSONObject = " + jsonResult);
			/*解析返回值*/
			if(null != jsonResult) {
				/*获取返回的所有数据数组，转换成对象集合*/
				JSONArray datasArray = (JSONArray) jsonResult.get("data");
				if(null != datasArray && datasArray.size() > 0) {
//					log.info("dataArray.size() = " + datasArray.size());
					/*返回的报文数据格式*/
					/**
					 * {"fundType":"BTC","configTime":1498808984,"netCost":"0.001","blockHeight":3190,
					 * "txIdN":"35cf674acdc93a78ecd5b0e291e32472575b6096e05c949d04c09c1855e23cd1_0",
					 * "walId":"BGBTCWithdraw0001","txId":"35cf674acdc93a78ecd5b0e291e32472575b6096e05c949d04c09c1855e23cd1",
					 * "walType":2,"uuid":"fe1dda0d-44f4-446a-ac91-426d93c6ca6c","walName":"BTC提现钱包0001","txAmount":"0.999"}
					 */
					/*代表txId,txIdN,uuid:唯一标识接口对接使用,walId:钱包ID,walName:钱包名称 */
					String txId = "", txIdN = "", uuid = "", walId = "", walName = "";
					/*确认时间*/
					Timestamp configTime = null;
					/**
					 * 区块高度,确认次数
					 * dealType:交易类型1 充值,2 提现(热提),3 冷钱包到热钱包转账(冷),4 热钱包到冷钱包转账(热冲).
					 * walletType:提现钱包类型1:充值热钱包,2:提现热钱包
					 */
					int blockHeight = 0, dealType = 0, walType = 0;
					/*充值金额*/
					BigDecimal txAmount = null, netCost = null;
					for (int i = 0; i < datasArray.size(); i++) {
//						log.info("datasArray.get(i) = " + datasArray.get(i));
						/*获取单条对象记录集合*/
						JSONObject detailInfo = (JSONObject) datasArray.get(i);
//						fundsType = detailInfo.getString("fundType");
						txId = detailInfo.getString("txId");
						txIdN = detailInfo.getString("txIdN");
						uuid = detailInfo.getString("uuid");
						walId = detailInfo.getString("walId");
						walName = detailInfo.getString("walName");
						walType = detailInfo.getIntValue("walType");
						txAmount = detailInfo.getBigDecimal("txAmount");
						/*预留fundsType*/
						dealType = detailInfo.getIntValue("dealType");
						netCost = detailInfo.getBigDecimal("netCost");
						blockHeight = detailInfo.getIntValue("blockHeight");
//						configTime = detailInfo.getTimestamp("configTime");
						configTime = new java.sql.Timestamp(detailInfo.getTimestamp("configTime").getTime() * 1000);
//						confirmTimes = detailInfo.getIntValue("confirmations");

//						log.info("fundsType = " + fundType + ", txIdN = " + txIdN + ", configTime = " + configTime + ", blockHeight = " + blockHeight);
//						log.info("confirmTimes = " + confirmTimes + ", txAmount = " + txAmount);
						/*拼接保存的SQL*/
//						if((i == 0 && datasArray.size() == 1) || i == datasArray.size() - 1) {
//							batchSql += "('" + txId + "', '" + txIdN + "', '" + uuid + "', '" + walId + "', '" + walName + "', " + walType + ", "
//									 + "" + txAmount + ", " + fundType + ", " + dealType + ", " + netCost + ", "
//									 + "" + blockHeight + ", '" + configTime + "', '" + TimeUtil.getNow() + "');";
//						} else {
						batchSql += "('" + txId + "', '" + txIdN + "', '" + uuid + "', '" + walId + "', '" + walName + "', " + walType + ", "
								+ "" + txAmount + ", " + fundType + ", " + dealType + ", " + netCost + ", "
								+ "" + blockHeight + ", '" + configTime + "', '" + TimeUtil.getNow() + "'),";
//						}

					}
//					log.info("batchSql = " + batchSql);
				}
			}
			List<OneSql> sqls = null;
			if(!"".equals(batchSql)) {
				batchSql = batchSql.substring(0,batchSql.length() -1)+";";
				/*保存到数据库中*/
				sql = "insert into finAccNetFeeRecord(txId, txIdN, uuid, walId, walName, walType, txAmount, fundsType, feeDirType, netCost, "
						+ "blockHeight, configTime, createTime) values " + batchSql;
				log.info("sql = " + sql);
				String delSql = "DELETE FROM finAccNetFeeRecord WHERE blockHeight = "+currBlockHeight +" and fundsType = "+fundType;
				sqls = new ArrayList<OneSql>();
				sqls.add(new OneSql(delSql, -2, null));
				sqls.add(new OneSql(sql, -2, null));
				Data.doTrans(sqls);
			}
			long endTime = System.currentTimeMillis();
			log.info("网络费记录对账币种【" + fundTypeName + "】【核算耗时：" + (endTime - startTime) + "】");

		} catch(Exception e) {
			log.error(e.toString(), e);
		}

	}
}
