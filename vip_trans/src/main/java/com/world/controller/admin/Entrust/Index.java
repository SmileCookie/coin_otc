package com.world.controller.admin.Entrust;

import com.tenstar.AdminWeiTuo;
import com.tenstar.RecordMessage;
import com.world.model.Market;
import com.world.util.DigitalUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map.Entry;

@FunctionAction(jspPath = "/admins/entrust/", des = "委托记录")
public class Index extends UserAction  {

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
		} catch (Exception ex) {
			ex.toString();
			log.info("");
		}
	}

	/**
	 * 获取委托记录
	 */
	@Page(Viewer = JSON)
	public void entrust() {
		String jsoncallback=request.getParameter("jsoncallback");
		Market m = Market.getMarkeByName(GetPrama(0));
		if (m == null) {
			Response.append(jsoncallback+"([{error market}])"); 
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
		
		double minCount = DigitalUtil.roundDown(doubleParam("minCount"), m.numberBixDian);
		double maxCount = DigitalUtil.roundDown(doubleParam("maxCount"), m.numberBixDian);
		
		long numberFrom = DigitalUtil.longMultiply(minCount, m.numberBixShow);
		long numberTo = DigitalUtil.longMultiply(maxCount, m.numberBixShow);
		
		long priceFrom = DigitalUtil.longMultiply(minPrice, m.exchangeBixShow);
		long priceTo = DigitalUtil.longMultiply(maxPrice, m.exchangeBixShow);
	    
		int status = intParam("status");
		
		long timeFrom = 0;
		long timeTo = 0;
		if(startTime != null){
			timeFrom = startTime.getTime();
		}
		if(endTime != null){
			timeTo = endTime.getTime();
		}

		AdminWeiTuo weituo = new AdminWeiTuo();
		weituo.setEntrustId(entrustId);
		weituo.setUserId(userId);
		weituo.setType(type);
		weituo.setWebId(m.webId);
		weituo.setTimeFrom(timeFrom);
		weituo.setTimeTo(timeTo);
		weituo.setNumberFrom(numberFrom);
		weituo.setNumberTo(numberTo);
		weituo.setPriceFrom(priceFrom);
		weituo.setPriceTo(priceTo);
		weituo.setStatus(status);
		weituo.setPageIndex(pageIndex);
		weituo.setPageSize(pageSize);
		weituo.setMarket(m.market);
		AdminWeiTuo rtn2  = new AdminWeiTuo();
		try {
		
				String rtn=com.tenstar.timer.admin.Index.getWeiTuoList(weituo.getEntrustId(),weituo.getWebId(), weituo.getUserId(), weituo.getPageIndex(), weituo.getType(), weituo.getTimeFrom(),
						weituo.getTimeTo(),
						weituo.getNumberFrom(), weituo.getNumberTo(), weituo.getPriceFrom(),
						weituo.getPriceTo(), weituo.getPageSize(), weituo.getStatus(),m);
		        rtn2.setMessage(rtn);
			if(jsoncallback!=null){
				Response.append(jsoncallback+"([{"+rtn2.getMessage()+"}])"); 
			}else{
				Response.append("[{"+rtn2.getMessage()+"}]"); 
			}
			 
		} catch (Exception ex2) {
			Response.append(jsoncallback+"([{\"count\":-1}])"); 
		}
	}
	
	@Page(Viewer = "/admins/entrust/details.jsp")
	public void details() {
		String jsoncallback = request.getParameter("jsoncallback");
		Market m = Market.getMarkeByName(GetPrama(0));
		if (m == null) {
			Response.append(jsoncallback + "([{error market}])");
			return;
		}
		request.setAttribute("serijavascripparam", m.toString());
		request.setAttribute("entrustId", GetPrama(1));
	} 
	
	/**
	 * 获取详情数据
	 */
	@Page(Viewer = ".json" )
	public void GetDetails() {
		String jsoncallback = request.getParameter("jsoncallback");
		Market m = Market.getMarkeByName(GetPrama(0));
		if (m == null) {
			Response.append(jsoncallback + "([{error market}])");
			return;
		}
		try {
			long entityId = Long.parseLong(GetPrama(1));
			RecordMessage myObj = new RecordMessage();
			myObj.setWebId(m.webId);
			myObj.setTypes(101);// 详情
			myObj.setMessage(entityId + "");// 借用来保存id的
			myObj.setMarket(m.market);
			RecordMessage rtn2 = new RecordMessage();
			String rtn=com.tenstar.timer.admin.Index.getDetails(Long.parseLong(myObj.getMessage()),m);
			rtn2.setMessage(rtn); 
			Response.append("[{" + rtn2.getMessage() + "}]");
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
}