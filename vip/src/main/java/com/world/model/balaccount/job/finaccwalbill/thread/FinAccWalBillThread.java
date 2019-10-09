package com.world.model.balaccount.job.finaccwalbill.thread;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.entity.usercap.dao.CommAttrDao;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.model.balaccount.dao.FinAccDetailsDao;
import com.world.model.balaccount.entity.FinAccDetailsBean;
import com.world.util.date.TimeUtil;
import com.world.util.request.HttpUtil;

/**
 * <p>标题: 钱包流水同步查询</p>
 * <p>描述: 按每个币种进行处理查询</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version
 */
public class FinAccWalBillThread extends Thread {
	/*币种编号2=btc*/
	private int fundType;
	/*币种名称btc*/
	private String fundTypeName;
	/*sql语句*/
	private String sql = "";
	/*拼接后保存到数据库的SQL*/
	/*finPayCenterWalletBill*/
	private String batchBillTxSql = "";
	private String batchBillTxNSql = "";
	private static Logger log = Logger.getLogger(FinAccWalBillThread.class);

	public FinAccWalBillThread(int fundType, String fundTypeName) {
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
			sql = "select max(blockHeight) blockHeight from finAccWalletBill where fundsType = " + fundType + "";
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
			log.info("【" + fundTypeName + "】钱包流水记录对账表中当前的区块高度为【" + currBlockHeight + "】");

			/**
			 * 交易平台发送查询，如果区块高度为空或者0，返回所有记录。
			 * 发送区块高度N>0，返回从N之后的数据
			 */
			/*调用接口的方法 配置参数tradingcenter.url=http\://192.168.3.118\:18080*/
			String url = ApiConfig.getValue("tradingcenter.url");
			url += "/openapi/tradingcenter/finance/wallet/record/" + fundTypeName.toUpperCase() + "?blockHeight=" + queryBlockHeight;
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
					 * {"fundType":"BTC","configTime":1490665303000,"blockHeight":122,
					 * "txIdN":"53e8e1db4ddd4d330d38481f07adf69571bd90b8a9cf2291c17873a2cd4381aa_0","confirmations":3,"txAmount":"47.9999616"}
					 */

					/*字典表*/
					CommAttrDao commAttrDao = new CommAttrDao();
					/*代表txId,uuid:唯一标识接口对接使用.walId:钱包ID,walName:钱包名称,txIdN */
					String txId = "", uuid = "", walId = "", walName = "", vn = "", toAddress = "", txIdN = "";
					/*确认时间*/
					Timestamp configTime = null, addTime = null;
					/**
					 * 区块高度,确认次数
					 * dealType:交易类型1 充值,2 提现(热提),3 冷钱包到热钱包转账(冷),4 热钱包到冷钱包转账(热冲)，5 其他到热提，6 其他到冷，7 冷到其他,8：热提到其他
					 * walletType:提现钱包类型1:充值热钱包,2:提现热钱包
					 */
					int blockHeight = 0, confirmTimes = 0, dealType = 0, walType = 0;
					/*交易金额,手续费,钱包余额,倍数*/
					BigDecimal txAmount = null, txNAmount = null, fee = null, walBalance = null, baseZoom = null;
					for (int i = 0; i < datasArray.size(); i++) {
						log.info("datasArray.get(i) = " + datasArray.get(i));
						/*获取单条对象记录集合*/
						JSONObject detailInfo = (JSONObject) datasArray.get(i);
//						fundsType = detailInfo.getString("fundType");
						/*获取各字段信息*/
						txId = detailInfo.getString("txId");
						fee = detailInfo.getBigDecimal("fee");
						addTime = detailInfo.getTimestamp("addTime");
						txAmount = detailInfo.getBigDecimal("amount");
						dealType = detailInfo.getIntValue("dealType");
						configTime = new java.sql.Timestamp(detailInfo.getTimestamp("dealTime").getTime() * 1000);
						blockHeight = detailInfo.getIntValue("blockHeight");
						confirmTimes = detailInfo.getIntValue("confirmations");
						walId = detailInfo.getString("walletId");
						walName = detailInfo.getString("walletName");
						walBalance = detailInfo.getBigDecimal("walletBalance");
						walType = detailInfo.getIntValue("walletType");

						baseZoom = detailInfo.getBigDecimal("baseZoom");

						if(null == baseZoom) {
							baseZoom = BigDecimal.ONE;
						}
						/*除以相应倍数*/
						if(null != txAmount) {
							//如果是omg的话，那么手续费需要处理，但是交易金额不需要
							if(!"omg".equals(fundTypeName.toLowerCase())){
								txAmount = txAmount.divide(baseZoom);
							}
						} else {
							txAmount = BigDecimal.ZERO;
						}
						if(null != fee) {
							fee = fee.divide(baseZoom);
						} else {
							fee = BigDecimal.ZERO;
						}
						if(null != walBalance) {
							walBalance = walBalance.divide(baseZoom);
						} else {
							walBalance = BigDecimal.ZERO;
						}

//						log.info("fundsType = " + fundType + ", txIdN = " + txIdN + ", configTime = " + configTime + ", blockHeight = " + blockHeight);
//						log.info("confirmTimes = " + confirmTimes + ", txAmount = " + txAmount);

						JSONArray addrsArray = (JSONArray) detailInfo.get("toAddressList");
						if(dealType == 2 &&  commAttrDao.checkSpecialAddress(addrsArray.toString(),10000005,fundType)){
							dealType = 8;
						}

						/*拼接保存的SQL*/
//						if ((i == 0 && datasArray.size() == 1) || i == datasArray.size() - 1) {
//							batchBillTxSql += "('" + walId + "', '" + walName + "', " + fundType + ", '" + uuid + "', '" + txId + "', "
//									 	   + "" + txAmount + ", " + fee + ", " + blockHeight + ", '" + addTime + "', '" + configTime + "', "
//									       + "" + confirmTimes + ", " + walBalance + ", " + dealType + ", " + walType + ", '" + baseZoom + "', "
//									       + "'" + TimeUtil.getNow() + "');";
//						} else {
						batchBillTxSql += "('" + walId + "', '" + walName + "', " + fundType + ", '" + uuid + "', '" + txId + "', "
								+ "" + txAmount + ", " + fee + ", " + blockHeight + ", '" + addTime + "', '" + configTime + "', "
								+ "" + confirmTimes + ", " + walBalance + ", " + dealType + ", " + walType + ", '" + baseZoom + "', "
								+ "'" + TimeUtil.getNow() + "'),";
//						}
						/*toAddressList*/
						/*获取返回的所有数据数组，转换成对象集合第二层*/
						for (int j = 0; j < addrsArray.size(); j++) {
							log.info("addrsArray.get(j) = " + addrsArray.get(j));
							/*获取单条对象记录集合*/
							JSONObject addrsInfo = (JSONObject) addrsArray.get(j);
							log.info("addrsInfo = " + addrsInfo);
							vn = addrsInfo.getString("vn");
							toAddress = addrsInfo.getString("toAddress");
							txNAmount = addrsInfo.getBigDecimal("amount");
							txIdN = txId + "_" +  vn;
							log.info("vn = " + vn + ", toAddress = " + toAddress + ", txNAmount = " + txNAmount);
							if(null != txNAmount) {
//								//如果是omg的话，那么手续费需要处理，但是交易金额不需要，但原始数据需要处理
								txNAmount = txNAmount.divide(baseZoom);
							} else {
								txNAmount = BigDecimal.ZERO;
							}

							if ((i == 0 && datasArray.size() == 1 && j == 0 && addrsArray.size() == 1) || (i == datasArray.size() - 1 && j == addrsArray.size() - 1)) {
								batchBillTxNSql += "('" + walId + "', '" + walName + "', " + fundType + ", '" + uuid + "', '" + txId + "', "
										+ "" + txAmount + ", " + fee + ", " + blockHeight + ", '" + addTime + "', '" + configTime + "', "
										+ "" + confirmTimes + ", " + walBalance + ", " + dealType + ", " + walType + ", '" + baseZoom + "', "
										+ "'" + txIdN + "', '" + toAddress + "', " + txNAmount + ", '" + TimeUtil.getNow() + "');";
							} else {
								batchBillTxNSql += "('" + walId + "', '" + walName + "', " + fundType + ", '" + uuid + "', '" + txId + "', "
										+ "" + txAmount + ", " + fee + ", " + blockHeight + ", '" + addTime + "', '" + configTime + "', "
										+ "" + confirmTimes + ", " + walBalance + ", " + dealType + ", " + walType + ", '" + baseZoom + "', "
										+ "'" + txIdN + "', '" + toAddress + "', " + txNAmount + ", '" + TimeUtil.getNow() + "'),";
							}

						}

					}
//					log.info("batchSql = " + batchSql);
				}
			}
			List<OneSql> sqls = null;
			if(!"".equals(batchBillTxSql)) {
				batchBillTxSql = batchBillTxSql.substring(0,batchBillTxSql.length() -1)+";";
				/*Bill保存到数据库中*/
				sql = "insert into finAccWalletBill(walId, walName, fundsType, uuid, txId, txAmount, fee, blockHeight, addTime, configTime, "
						+ "confirmTimes, walBalance, dealType, walType, baseZoom, createTime) values " + batchBillTxSql;
				log.info("sql = " + sql);
				String delBillSql = "DELETE FROM finAccWalletBill WHERE blockHeight = "+currBlockHeight +" and fundsType = "+fundType+"; ";
				batchBillTxNSql = batchBillTxNSql.substring(0,batchBillTxNSql.length() -1)+";";
				/*BillDetail保存到数据库中*/
				String detailSql = "insert into finAccWalletBillDetails(walId, walName, fundsType, uuid, txId, txAmount, fee, blockHeight, addTime, configTime, "
						+ "confirmTimes, walBalance, dealType, walType, baseZoom, txIdN, toAddress, txNAmount, createTime) values " + batchBillTxNSql;
				String delBillDetailSql = "DELETE FROM finAccWalletBillDetails WHERE blockHeight = "+currBlockHeight +" and fundsType = "+fundType +"; ";
				sqls = new ArrayList<OneSql>();
				sqls.add(new OneSql(delBillSql, -2, null));
				sqls.add(new OneSql(sql, -2, null));
				sqls.add(new OneSql(delBillDetailSql, -2, null));
				sqls.add(new OneSql(detailSql, -2, null));
				Data.doTrans(sqls);
			}


			long endTime = System.currentTimeMillis();
			log.info("钱包流水记录对账币种【" + fundTypeName + "】【核算耗时：" + (endTime - startTime) + "】");

		} catch(Exception e) {
			log.error(e.toString(), e);
		}

	}
}
