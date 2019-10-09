package com.world.controller.admin.Entrust.viture;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.tenstar.HTTPTcp;
import com.tenstar.Message;
import com.tenstar.MessageCancle;
import com.world.controller.IndexServer;
import com.world.model.Market;
import com.world.util.DigitalUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

import net.sf.json.JSONArray;

@FunctionAction(jspPath = "/admins/entrust/viture", des = "虚拟委托")
public class Index extends UserAction  {
	
	private IndexServer server = new IndexServer();
	
	@Page(Viewer = "/admins/entrust/viture/index.jsp")
	public void index(){
		String tab = param("tab");
		if(StringUtils.isEmpty(tab))
			tab = "btc_cny";
		
		Market m= Market.getMarkeByName(tab);
		if(m==null){
			json("",false,L("错误的市场"));
			return;
		} 
		String rtn = null;
		try {
				if(m.listenerOpen){
					rtn = server.getVitureEntrust(m);
				}else{
					 rtn = HTTPTcp.Post(m.ip,m.port,"/getVitureEntrust",null);
				}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		if(rtn != null){
			JSONArray array = JSONArray.fromObject(rtn);
			double [][] result =  new double[array.size()][2];
			int i = 0;
			for (Object object : array) {
				JSONArray d = (JSONArray) object;
				result[i][0] = d.getDouble(0);
				result[i][1] = d.getDouble(1);
				i++;
			}
			setAttr("result", result);
			setAttr("length", i);
			setAttr("tab", tab);
		}
		
	}
	
	
	@Page(Viewer = JSON)
	public void doVitureEntrust(){
		try {
			Market m= Market.getMarkeByName(param("market"));
			if(m==null){
				json("",false,L("错误的市场"));
				return;
			} 
			
			double unitPrice = DigitalUtil.roundDown(doubleParam("unitPrice"), m.exchangeBixDian);
			double number = DigitalUtil.roundDown(doubleParam("number"), m.numberBixDian);
		    
			//long unitPrice=DigitalUtil.longMultiply(price, m.exchangeBixShow);
			//long number=DigitalUtil.longMultiply(count, m.numberBixShow);

			Message myObj = new Message();    
	        myObj.setWebId(m.webId);
	        myObj.setNumbers(BigDecimal.valueOf(number));
	        myObj.setUnitPrice(BigDecimal.valueOf(unitPrice));
	        myObj.setMarket(m.market);
			String rtn = "";
			if(m.listenerOpen){
				rtn = server.vitureEntrust(myObj, m);
			}else{
				 String param=HTTPTcp.ObjectToString(myObj);
			     rtn= HTTPTcp.Post(m.ip,m.port,"/vitureEntrust",param);
			}
	        json(rtn,true, "");
		}catch(Exception ex){
			json("",false, "");
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = JSON)
	public void doCancelVitureEntrust(){
		try{
			Market m=Market.getMarkeByName(param("market"));
			if(m==null){
				Write("",false,L("错误的市场"));
				return;
			}
			
			int webid=m.webId; 
			
			double priceLow = DigitalUtil.roundDown(doubleParam("minPrice"), m.exchangeBixDian);
			double priceHigh = DigitalUtil.roundDown(doubleParam("maxPrice"), m.exchangeBixDian);
			
			//long priceLow=DigitalUtil.longMultiply(minPrice, m.exchangeBixShow);
			//long priceHigh=DigitalUtil.longMultiply(maxPrice, m.exchangeBixShow);
			
			MessageCancle myObj = new MessageCancle();    
			myObj.setWebId(webid); 
			myObj.setPriceLow(BigDecimal.valueOf(priceLow));
			myObj.setPriceHigh(BigDecimal.valueOf(priceHigh));
			myObj.setMarket(m.market);
			String rtn = "";
			if(m.listenerOpen){
				rtn = server.cancelVitureEntrust(myObj, m);
			}else{
				String param=HTTPTcp.ObjectToString(myObj);
				rtn=HTTPTcp.DoRequest2(true,m.ip,m.port,"/cancelVitureEntrust",param);
		        
			}
			json(rtn,true, "");
		 }catch(Exception ex2){
			 json("",false, "");
			 log.error(ex2.toString(), ex2);
	  }
	}
}