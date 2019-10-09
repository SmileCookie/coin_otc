package com.world.model.balaccount.job.finaccdetailswork.thread;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.util.date.TimeUtil;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.model.balaccount.dao.FinAccDetailsDao;
import com.world.model.balaccount.entity.FinAccDetailsBean;
import com.world.util.request.HttpUtil;

/**
 * <p>标题: 充值记录对账查询</p>
 * <p>描述: 按每个币种进行处理查询</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version
 */
public class FinAccDetailsThread extends Thread {
	/*币种编号2=btc*/
	private int fundType;
	/*币种名称btc*/
	private String fundTypeName;
	/*sql语句*/
	private String sql = "";
	/*拼接后保存到数据库的SQL*/
	private String batchSql = "";
	private static Logger log = Logger.getLogger(FinAccDetailsThread.class);

	public FinAccDetailsThread(int fundType, String fundTypeName) {
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
			sql = "select max(createTime) createTime from finAccDetails where fundsType = " + fundType + "";
			log.info("sql = " + sql);
			FinAccDetailsBean finAccDetailsBean = null;
			finAccDetailsBean = finAccDetailsDao.getT(sql, null, FinAccDetailsBean.class);
			/*当前区块高度*/
			int currBlockHeight = 0;
			Timestamp currAddTime = TimeUtil.getHourFirst();
			int queryBlockHeight = 0;
			if(null != finAccDetailsBean && finAccDetailsBean.getCreateTime() != null) {
				currBlockHeight = finAccDetailsBean.getBlockHeight();
				currAddTime = finAccDetailsBean.getCreateTime();
				if(currBlockHeight > 0) {
					queryBlockHeight = currBlockHeight;
					queryBlockHeight = queryBlockHeight - 30;
				}
			}
			log.info("【" + fundTypeName + "】充值记录对账表中当前的最大时间为【" + currAddTime.getTime() + "】");

			/**
			 * 交易平台发送查询，如果区块高度为空或者0，返回所有记录。
			 * 发送区块高度N>0，返回从N之后的数据
			 */

			/*调用接口的方法*/
			String url = ApiConfig.getValue("tradingcenter.url");
			url += "/openapi/tradingcenter/finance/recharge/record/" + fundTypeName.toUpperCase() + "?addTime=" + currAddTime.getTime();
			log.info("url = " + url);
			String strResult = HttpUtil.doGet(url, null, 10000, 10000);
			JSONObject jsonResult = com.alibaba.fastjson.JSONObject.parseObject(strResult);

			log.info("jSONObject = " + jsonResult);

			/*解析返回值*/
			if(null != jsonResult) {
				/*获取返回的所有数据数组，转换成对象集合*/
				JSONArray datasArray = (JSONArray) jsonResult.get("data");
				if(null != datasArray && datasArray.size() > 0) {
					/*返回的报文数据格式*/
					/**
					 * {"fundType":"BTC","configTime":1490665303000,"blockHeight":122,
					 * "txIdN":"53e8e1db4ddd4d330d38481f07adf69571bd90b8a9cf2291c17873a2cd4381aa_0","confirmations":3,"txAmount":"47.9999616"}
					 */
					/*代表txId加上系列号.格式为xxx_xxx*/
					String txIdN = "";

					/*确认时间*/
					Timestamp configTime = null;

					/*Mongo入库时间*/
					Timestamp addTime = null;

					/*区块高度,确认次数*/
					int blockHeight = 0, confirmTimes = 0;

					/*充值金额*/
					BigDecimal txAmount = null;
					for (int i = 0; i < datasArray.size(); i++) {

						/*获取单条对象记录集合*/
						JSONObject detailInfo = (JSONObject) datasArray.get(i);
						txIdN = detailInfo.getString("txIdN");
						configTime = detailInfo.getTimestamp("configTime");
						addTime = detailInfo.getTimestamp("addTime");
						blockHeight = detailInfo.getIntValue("blockHeight");
						confirmTimes = detailInfo.getIntValue("confirmations");
						txAmount = detailInfo.getBigDecimal("txAmount");

						/*拼接保存的SQL*/
						batchSql += "('" + txIdN + "', '" + txIdN + "', " + txAmount + ", " + fundType + ", 2, "
								+ "" + blockHeight + ", '" + configTime + "', " + confirmTimes + ", '" + addTime + "'),";
					}
				}
			}
			List<OneSql> sqls = null;
			if(!"".equals(batchSql)) {
				batchSql = batchSql.substring(0,batchSql.length() -1)+";";
				/*保存到数据库中*/
				sql = "insert into finAccDetails(txIdN, addHash, txAmount, fundsType, status, blockHeight, configTime, confirmTimes,createTime) values " + batchSql;
				log.info("sql = " + sql);
				String delSql = "DELETE FROM finAccDetails WHERE createTime > '" +currAddTime  + "' and fundsType = "+fundType;
				sqls = new ArrayList<OneSql>();
				sqls.add(new OneSql(delSql, -2, null));
				sqls.add(new OneSql(sql, -2, null));
				Data.doTrans(sqls);
			}
			long endTime = System.currentTimeMillis();
			log.info("充值记录对账币种【" + fundTypeName + "】【核算耗时：" + (endTime - startTime) + "】");

		} catch(Exception e) {
			log.error(e.toString(), e);
//			log.info("error:" + e);
		}

	}
}
