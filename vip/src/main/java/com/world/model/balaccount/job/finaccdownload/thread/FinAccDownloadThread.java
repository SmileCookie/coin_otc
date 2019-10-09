package com.world.model.balaccount.job.finaccdownload.thread;

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
import com.world.model.balaccount.job.finaccdetailswork.thread.FinAccDetailsThread;
import com.world.util.request.HttpUtil;

/**
 * <p>标题: 提现记录对账查询</p>
 * <p>描述: 按每个币种进行提现处理查询</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version
 */
public class FinAccDownloadThread extends Thread {
	/*币种编号2=btc*/
	private int fundType;
	/*币种名称btc*/
	private String fundTypeName;
	/*sql语句*/
	private String sql = "";
	/*拼接后保存到数据库的SQL*/
	private String batchSql = "";
	private static Logger log = Logger.getLogger(FinAccDetailsThread.class);

	public FinAccDownloadThread(int fundType, String fundTypeName) {
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
			/*首先查询出该币种已同步到的区块提现交易时间*/
			sql = "select max(createTime) createTime from finAccDownload where fundsType = " + fundType + "";
			log.info("sql = " + sql);
			FinAccDetailsBean finAccDetailsBean = null;
			finAccDetailsBean = finAccDetailsDao.getT(sql, null, FinAccDetailsBean.class);
			/*当前区块高度*/
//			int currBlockHeight = 0;
//			int queryBlockHeight = 0;
			Timestamp currAddTime = null;
			if(null != finAccDetailsBean && finAccDetailsBean.getCreateTime()!=null) {
//				currBlockHeight = finAccDetailsBean.getBlockHeight();
				currAddTime = finAccDetailsBean.getCreateTime();
//				if(currBlockHeight > 0) {
//					queryBlockHeight = currBlockHeight;
//					queryBlockHeight = queryBlockHeight - 30;
//				}
			}else{
				currAddTime = TimeUtil.getHourFirst();
			}
			log.info("【" + fundTypeName + "】提现记录对账表中当前的区块时间为【" + currAddTime + "】");

			/**
			 * 交易平台发送查询，如果区块高度为空或者0，返回所有记录。
			 * 发送区块高度N>0，返回从N之后的数据
			 */
			/*调用接口的方法*/
			/*tradingcenter.url=http\://192.168.3.118\:18080*/
			String url = ApiConfig.getValue("tradingcenter.url");
			url += "/openapi/tradingcenter/finance/withdrawal/record/" + fundTypeName.toUpperCase() + "?successTime=" + currAddTime.getTime();
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
					 * {"fundType":"ZEC","realFee":"","configTime":1497326521000,"blockHeight":112,"txIdN":"__0","txId":"_",
					 * "uuid":"BITGLOBAL_a2749216-4fe9-11e7-9b62-000c29b03439","txAmount":"10.30000000","status":2},
					 */
					/*代表txId加上系列号.格式为xxx_xxx, 唯一标识接口对接使用*/
					String txIdN = "", txId = "", uuid = "";
					/*确认时间*/
					Timestamp configTime = null;
					/*区块高度,确认次数, 成功标志*/
					int blockHeight = 0, status = 0;
					/*提现金额, 实际提现手续费*/
					BigDecimal txAmount = null, realFee = null;

					//提现成功回调时间 = mongo入库时间
					Timestamp recordTime = null;

					for (int i = 0; i < datasArray.size(); i++) {
//						log.info("datasArray.get(i) = " + datasArray.get(i));
						/*获取单条对象记录集合*/
						JSONObject detailInfo = (JSONObject) datasArray.get(i);
//						fundsType = detailInfo.getString("fundType");
						uuid = detailInfo.getString("uuid");
						txId = detailInfo.getString("txId");
						txIdN = detailInfo.getString("txIdN");
						configTime = detailInfo.getTimestamp("configTime");
						recordTime = detailInfo.getTimestamp("recordTime");
//
//						//modify by kinghao 20190121  预防时间戳非毫秒级
//						long time= 9999999999L;
//						if(detailInfo.getTimestamp("dealTime").getTime() >time){
//							configTime =new java.sql.Timestamp(detailInfo.getTimestamp("dealTime").getTime() );
//						}
//						//end
						blockHeight = detailInfo.getIntValue("blockHeight");
//						confirmTimes = detailInfo.getIntValue("confirmations");
						status = detailInfo.getIntValue("status");
						txAmount = detailInfo.getBigDecimal("txAmount");
						realFee = detailInfo.getBigDecimal("realFee");

//						log.info("fundsType = " + fundType + ", txIdN = " + txIdN + ", configTime = " + configTime + ", blockHeight = " + blockHeight);
//						log.info("confirmTimes = " + confirmTimes + ", txAmount = " + txAmount);
						/*拼接保存的SQL*/
//						if((i == 0 && datasArray.size() == 1) || i == datasArray.size() - 1) {
//							batchSql += "('" + uuid + "', '" + txId + "', '" + txIdN + "', " + txAmount + ", " + fundType + ", " + status + ", "
//									 + "" + blockHeight + ", '" + configTime + "', " + realFee + ");";
//						} else {
						batchSql += "('" + uuid + "', '" + txId + "', '" + txIdN + "', " + txAmount + ", " + fundType + ", " + status + ", "
								+ "" + blockHeight + ", '" + configTime + "', " + realFee + ", '" + recordTime +"'),";
//						}

					}
//					log.info("batchSql = " + batchSql);
				}
			}
			List<OneSql> sqls = null;
			if(!"".equals(batchSql)) {
				batchSql = batchSql.substring(0,batchSql.length() -1)+";";
				/*保存到数据库中*/
				sql = "insert into finAccDownload(uuid, txId, txIdN, txAmount, fundsType, status, blockHeight, configTime, realFee, createTime) values " + batchSql;
				log.info("sql = " + sql);
				String delSql = "DELETE FROM finAccDownload WHERE createTime > '" +currAddTime  + "' and fundsType = "+fundType;
				sqls = new ArrayList<OneSql>();
				sqls.add(new OneSql(delSql, -2, new Object[]{}));
				sqls.add(new OneSql(sql, -2, new Object[]{}));
				Data.doTrans(sqls);
			}
			long endTime = System.currentTimeMillis();
			log.info("提现记录对账币种【" + fundTypeName + "】【核算耗时：" + (endTime - startTime) + "】");

		} catch(Exception e) {
			log.error(e.toString(), e);
		}
	}

	public void  sqltest(List<OneSql> sqls){
		Data.doTrans(sqls);
	}

	public static void main(String[] args) {
//		List<OneSql> sqls = null;
//		 String sql = "";
//
//		/*保存到数据库中*/
//		sql = "insert into finAccDownload(uuid, txId, txIdN, txAmount, fundsType, status, blockHeight, configTime, realFee) values ('platform_98fc228e-00c1-45a6-a36c-bc539c22b884', '5426b69a89a88325512bc5991fcfc51059c132311d84ed171561ac84525e8083', '5426b69a89a88325512bc5991fcfc51059c132311d84ed171561ac84525e8083_1', 2.12345678, 2, 2, 751, '1970-01-19 04:54:50.801', -0.00025900),('platform_e2ba8b0b-d837-4bb4-9369-29030e793089', '5426b69a89a88325512bc5991fcfc51059c132311d84ed171561ac84525e8083', '5426b69a89a88325512bc5991fcfc51059c132311d84ed171561ac84525e8083_1', 2.00000000, 2, 2, 751, '1970-01-19 04:54:50.801', -0.00025900),('platform_8e0493ad-88e3-4f49-a571-cd1ac8f853b2', '5426b69a89a88325512bc5991fcfc51059c132311d84ed171561ac84525e8083', '5426b69a89a88325512bc5991fcfc51059c132311d84ed171561ac84525e8083_1', 0.50000000, 2, 2, 751, '1970-01-19 04:54:50.801', -0.00025900),('platform_af34928e-dd24-4e24-a663-d2df676baa83', '5426b69a89a88325512bc5991fcfc51059c132311d84ed171561ac84525e8083', '5426b69a89a88325512bc5991fcfc51059c132311d84ed171561ac84525e8083_2', 2.00000000, 2, 2, 751, '1970-01-19 04:54:50.801', -0.00025900)";
//		log.info("sql = " + sql);
//		String delSql = "DELETE FROM finAccDownload WHERE blockHeight = 0 and fundsType = 2";
//		sqls = new ArrayList<OneSql>();
//		sqls.add(new OneSql(delSql, -2, null));
//		sqls.add(new OneSql(sql, -2, null));

		FinAccDownloadThread finAccDownloadThread = new FinAccDownloadThread(2,"BTC");
		finAccDownloadThread.run();
//		finAccDownloadThread.sqltest(sqls);
	}
}
