package com.world.controller.admin.trade.trans;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Data;
import com.world.data.mysql.Query;
import com.world.model.dao.TransRecordDao2;
import com.world.model.entity.Market;
import com.world.model.entity.record.TransRecord;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.model.quanttrade.dao.TransRecordDao;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@FunctionAction(jspPath = "/admins/trade/trans/", des = "成交记录")
public class Index extends AdminAction {
	TransRecordDao transRecordDao = new TransRecordDao();
	TransRecordDao2 transRecordDao2 = new TransRecordDao2();
	CommAttrDao commAttrDao = new CommAttrDao();

	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		String currentTab = param("tab");
		if (StringUtils.isBlank(currentTab))
			currentTab =  Market.getDefMarketName();;
		setAttr("tab", currentTab);
		setAttr("markets",Market.getMarketsMap());
		JSONObject marketJson = Market.getMarketByName(currentTab);
		if(marketJson==null){
			log.error("获取不到盘口信息，请检查参数tab"+currentTab);
			return;
		}
		String dbName = marketJson.getString("db");//数据库名称
		transRecordDao.setDatabase(dbName);

		int currentPage = intParam("page");
		currentPage = currentPage < 1 ? 1 : currentPage;
		int pageSize = intParam("pageSize")==0?10:intParam("pageSize");
		long entrustId = longParam("entrustId");
		int userId = intParam("userId");
		int type = -2;
		String typeStr = param("type");
		if(typeStr.length() > 0){
			type = Integer.parseInt(typeStr);
		}
		Timestamp startTime = dateParam("startTime");
		Timestamp endTime = dateParam("endTime");
		double priceFrom = doubleParam("minPrice");
		double priceTo = doubleParam("maxPrice");
		double totalFrom = doubleParam("minTotalPrice");
		double totalTo = doubleParam("maxTotalPrice");
		double numberFrom = doubleParam("minCount");
		double numberTo = doubleParam("maxCount");
		int status = intParam("status");
		long timeFrom = 0;
		long timeTo = 0;
		if(startTime != null){
			timeFrom = startTime.getTime();
		}
		if(endTime != null){
			timeTo = endTime.getTime();
		}
		if(status == 0){
			status = 1;
		}
		if(pageSize==0)
			pageSize=10;
		if(pageSize>200)
			pageSize=200;

		StringBuilder where=new StringBuilder();
		if(entrustId>0){
			where.append(" and (entrustIdSell="+entrustId+" or entrustIdBuy="+entrustId+") ");//仅仅显示这个状态的
		}
		if(userId>0){
			where.append(" and (userIdBuy="+userId+" or userIdSell="+userId+") ");//仅仅显示这个状态的
		}
		if(status>0&&status != 3){
			where.append(" and status="+status);//仅仅显示这个状态的
		}
		//status==0 是所有的意思
		if(type>-2 && type != -1){
			where.append(" and types="+type + " and userIdBuy != 0 and userIdSell != 0 ");//类型
		}
		if(type == -1){
			where.append(" and (userIdBuy = 0 or userIdSell = 0) ");//
		}
		if(timeFrom>0)
			where.append(" and times>="+timeFrom);//时间从
		if(timeTo>0)
			where.append(" and times<="+timeTo);//时间到
		if(numberFrom>0)
			where.append(" and numbers>="+numberFrom);//时间从
		if(numberTo>0)
			where.append(" and numbers<="+numberTo);//时间到

		if(priceFrom>0)
			where.append(" and unitPrice>="+priceFrom);//时间到
		if(priceTo>0)
			where.append(" and unitPrice<="+priceTo);//时间到
		if(totalFrom>0)
			where.append(" and totalPrice>="+totalFrom);//时间到
		if(totalTo>0)
			where.append(" and totalPrice<="+totalTo);//时间到

		String w=where.toString();
		if(w.length()>0)
			w=" where "+w.substring(4);
		String  limit = "limit "+ (currentPage-1)*pageSize+","+pageSize;
		String sql="select * from (select transRecordId,unitPrice,totalPrice,numbers,entrustIdBuy,userIdBuy,entrustIdSell,userIdSell,TYPES,times,status,isCount FROM transrecord    "+w+"  ";
		sql += "union all select transRecordId,unitPrice,totalPrice,numbers,entrustIdBuy,userIdBuy,entrustIdSell,userIdSell,TYPES,times,status,isCount FROM transrecord_all   "+w+" ) fa ";
		sql += " order by times desc "+limit;
		Query<TransRecord> query = transRecordDao.getQuery();
		query.setSql(sql);
		query.setCls(TransRecord.class);
		List<TransRecord> buyList = transRecordDao.find();
		String sqlcount =" SELECT (select count(*) FROM transrecord  "+w+" )+(select count(*) FROM transrecord_all " +w+ ") from dual";
		//提现记录数量
		List count = (List)Data.GetOne(dbName,sqlcount, new Object[]{});
		if(count != null && count.size()>0){
			setPaging(Integer.parseInt(count.get(0).toString()), currentPage , pageSize);
		}
		Map<String, String> userTypeMap = commAttrDao.queryUserTypeMap();
		for(TransRecord transRecord : buyList){
			//设置用户类型卖
			if(userTypeMap.containsKey(transRecord.getUserIdSell() + "")){
				transRecord.setUserSellType(userTypeMap.get(transRecord.getUserIdSell() + ""));
			}

			//设置用户类型买
			if(userTypeMap.containsKey(transRecord.getUserIdBuy() + "")){
				transRecord.setUserBuyType(userTypeMap.get(transRecord.getUserIdBuy() + ""));
			}

		}
		setAttr("dataList", buyList);
		setAttr("status", status);
	}

	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	/**
	 * 获取详情数据
	 */
	@Page(Viewer = ".json"  ,des="更新失败记录")
	public void udpateS() {
		try {
			long transRecordId = Long.parseLong(param("id"));
			JSONObject m = Market.getMarketByName(param("market"));
			if (m == null) {
				json("错误的市场", false, "");
				return;
			}
			String dbName = m.getString("db");
			int updateNum = transRecordDao2.reDueMoney(transRecordId, dbName);
			if(updateNum > 0){
				json("更新成功", true, "");
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
		json("更新失败", false, "");
	}

}
