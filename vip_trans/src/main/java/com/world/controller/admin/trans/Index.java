package com.world.controller.admin.trans;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map.Entry;

import com.tenstar.AdminMoney;
import com.tenstar.RecordMessage;
import com.world.model.Market;
import com.world.util.DigitalUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/trans/", des = "成交记录")
public class Index extends AdminAction {

	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		try {
			Iterator<Entry<String,Market>> iter =Market.markets.entrySet().iterator();
			
			String tab = param("tab");
			if (tab.length() == 0){
				while(iter.hasNext()){
					tab = iter.next().getKey();
					break;
				}
			}
			
			Market m = Market.getMarkeByName(tab);
			setAttr("market", m.toString());
			setAttr("tab", tab);
			setAttr("markets",Market.markets);
			setAttr("page", 1);
			setAttr("status", 1);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	/**
	 * 获取委托记录
	 */
	@Page(Viewer = JSON)
	public void transRecord() {
		String jsoncallback=request.getParameter("jsoncallback");
		Market m = Market.getMarkeByName(GetPrama(0));
		if (m == null) {
			json("", false, "错误的市场");
			return;
		}

		int pageIndex = intParam("page");
		if(pageIndex == 0)
			pageIndex = 1;
		int pageSize = 10;

		long entrustId = longParam("entrustId");
		
		int userId = intParam("userId");
		int type = -2;
		String typeStr = param("type");
		if(typeStr.length() > 0){
			type = Integer.parseInt(typeStr);
		}

		Timestamp startTime = dateParam("startTime");
		Timestamp endTime = dateParam("endTime");
		
		double minPrice = DigitalUtil.roundDown(doubleParam("minPrice"), m.exchangeBixDian);
		double maxPrice = DigitalUtil.roundDown(doubleParam("maxPrice"), m.exchangeBixDian);

		double minTotalPrice = DigitalUtil.roundDown(doubleParam("minTotalPrice"), m.exchangeBixDian+m.numberBixDian);
		double maxTotalPrice = DigitalUtil.roundDown(doubleParam("maxTotalPrice"), m.exchangeBixDian+m.numberBixDian);
		
		double minCount = DigitalUtil.roundDown(doubleParam("minCount"), m.numberBixDian);
		double maxCount = DigitalUtil.roundDown(doubleParam("maxCount"), m.numberBixDian);
		
		long priceFrom = DigitalUtil.longMultiply(minPrice, m.exchangeBixShow);
		long priceTo = DigitalUtil.longMultiply(maxPrice, m.exchangeBixShow);

		long totalFrom = DigitalUtil.longMultiply(minTotalPrice, m.exchangeBixShow*m.numberBixShow);
		long totalTo = DigitalUtil.longMultiply(maxTotalPrice, m.exchangeBixShow*m.numberBixShow);

		long numberFrom = DigitalUtil.longMultiply(minCount, m.numberBixShow);
		long numberTo = DigitalUtil.longMultiply(maxCount, m.numberBixShow);
	    
		int status = intParam("status");
		
		long timeFrom = 0;
		long timeTo = 0;
		if(startTime != null){
			timeFrom = startTime.getTime();
		}
		if(endTime != null){
			timeTo = endTime.getTime();
		}

		AdminMoney trans = new AdminMoney();
		trans.setEntrustId(entrustId);
		trans.setUserId(userId);
		trans.setType(type);
		trans.setWebId(m.webId);
		trans.setTimeFrom(timeFrom);
		trans.setTimeTo(timeTo);
		trans.setNumberFrom(numberFrom);
		trans.setNumberTo(numberTo);
		trans.setPriceFrom(priceFrom);
		trans.setPriceTo(priceTo);
		trans.setTotalFrom(totalFrom);
		trans.setTotalTo(totalTo);
		trans.setStatus(status);
		trans.setPageIndex(pageIndex);
		trans.setPageSize(pageSize);
		try {
			  String rtn=com.tenstar.timer.admin.Index.getMoney(trans.getEntrustId(), trans.getTransRecordId(), 8,
					  trans.getUserId(), trans.getPageIndex(), trans.getType(), trans.getTimeFrom(), trans.getTimeTo(), trans.getNumberFrom(), trans.getNumberTo(),
					  trans.getPriceFrom(), trans.getPriceTo(), trans.getTotalFrom(), trans.getTotalTo(), trans.getPageSize(), trans.getStatus(),m);
					  		      
			if(jsoncallback!=null){
				Response.append(jsoncallback+"([{"+rtn+"}])");  
			}else{
				Response.append("[{"+rtn+"}]");  
			}
			
		} catch (Exception ex2) {
			Response.append(jsoncallback+"([{\"count\":-1}])"); 
		}
	}
	
	/**
	 * 获取详情数据
	 */
	@Page(Viewer = ".json" )
	public void udpateS() {
		Market m = Market.getMarkeByName(GetPrama(0));
		if (m == null) {
			json("错误的市场", false, "");
			return;
		}
		try {
			long transRecordId = Long.parseLong(GetPrama(1));
			RecordMessage myObj = new RecordMessage();
			myObj.setWebId(m.webId);
			myObj.setTypes(101);// 详情
			myObj.setMessage(transRecordId + "");// 借用来保存id的
			
			String rtn=""+com.tenstar.timer.admin.Index.reDueMoney(transRecordId,m);
			
			//String param = HTTPTcp.ObjectToString(myObj);
			//String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, "/getadmin/reduemoney", param);

			//RecordMessage rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
			if(Integer.parseInt(rtn) > 0){
				json("更新成功", true, "");
				return;
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		json("更新失败", false, "");
	}
}
