package com.world.model.balaccount.job.finaccwalall.thread;

import java.math.BigDecimal;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import com.world.util.request.HttpUtil;

/**
 * <p>标题: 钱包余额记录对账查询</p>
 * <p>描述: 按每个币种获取后解析每个钱包类型进行处理</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class FinAccWalAllRecordThread extends Thread {
	/*币种编号2=btc*/
	private int fundType;
	/*币种名称btc*/
	private String fundTypeName;
	/*sql语句*/
	private String sql = "";
	private static Logger log = Logger.getLogger(FinAccWalAllRecordThread.class);
	
	public FinAccWalAllRecordThread(int fundType, String fundTypeName) {
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
			
			/*调用接口的方法*/
			/*获取热提钱包余额*/
			String url = "http://192.168.5.112:18080/openapi/tradingcenter/withdrawal/balance/";
			url += fundTypeName.toUpperCase();
			log.info("url = " + url);
			String strResult = HttpUtil.doGet(url, null, 10000, 10000);
			//.parseObject(result);
			JSONObject jsonResult = com.alibaba.fastjson.JSONObject.parseObject(strResult);
			log.info("jSONObject = " + jsonResult);
			/*解析返回值*/
			BigDecimal amount = null;
			if(null != jsonResult) {
				/*获取返回的所有数据数组，转换成对象集合*/
				/*返回的报文数据格式*/
				/**
				 * {"status":0,"data":4.12864767}
				 */
				amount = jsonResult.getBigDecimal("data");
				if(null != amount) {
					/*保存到数据库中*/
					sql = "update finanaccount set curTotalAmount = " + amount + " where type = 3 and fundType = " + fundType + "";
					log.info("sql = " + sql);
				}
			}
			
			
			
//			finAccDetailsDao.save(sql, null);
			
			long endTime = System.currentTimeMillis();
			log.info("充值记录对账币种【" + fundTypeName + "】【核算耗时：" + (endTime - startTime) + "】");
			
		} catch(Exception e) {
			log.error(e.toString(), e);
		}
		
	}
}
