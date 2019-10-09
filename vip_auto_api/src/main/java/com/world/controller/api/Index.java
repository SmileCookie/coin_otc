package com.world.controller.api;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.redis.RedisUtil;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.VipResponse;
import com.api.user.UserManager;
import com.api.util.DigitalUtil;
import com.tenstar.HTTPTcp;
import com.tenstar.Message;
import com.tenstar.MessageCancle;
import com.tenstar.RecordMessage;
import com.world.cache.Cache;
import com.world.common.SystemCode;
import com.world.common.VerifiUtil;
import com.world.model.entitys.Order;
import com.world.model.entitys.TransRecord;
import com.world.model.market.Market;
import com.world.web.Page;
import com.world.web.action.UserAction;


/**
 * API交易接口管理
 * @author guosj
 */
public class Index extends UserAction{
	
	private VerifiUtil verifiUtil = VerifiUtil.getInstance();
	private final static int LIMIT_PAGE_SIZE = 100;
	public JSONObject jsonObject;
	private final int webid =6;//表示API来源
	private final static String CACHE_EID = "cjson_eid_";
	
	//------------------app的接口版本判断-------------- end
	
	public void setJsonObject(JSONObject jsonObject){
		this.jsonObject = jsonObject;
	}
	
	public JSONObject getJsonObject(){
		return this.jsonObject;
	}
	
	@Page(Viewer = JSON)
	public void index(){
		
	}
	
	/**
	 * 委托买单或卖单
	 * 
	 * accesskey		用户访问密钥
	 * price		    单价
	 * amount	        交易量
	 * tradeType	    交易类型1/0(buy/sell)
	 * currency         货币类型
	 * safePassword	    安全密码
	 */
	@Page(Viewer = JSON)
	public void order(){
		try{
			String key =  request.getParameter("accesskey");
			BigDecimal price = decimalParam("price");
			BigDecimal amount = decimalParam("amount");
			int isBuy = intParam("tradeType");
			String currency = request.getParameter("currency");
			String customerOrderId = param("customerOrderId");
			
			//用于是否能低价买，高价卖，也能成交自己的单据1=true 0=false
			int mt = intParam("mt");
			
			//------------------------------------------------------------------------
			setJsonObject(null);
			//签名校验
			if(!accessSign(key)){
				return;
			}
			JSONObject json = getJsonObject();
			if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			}
			//=========================================================================================================
			
			JSONObject m = Market.getMarketByName(currency);
			if(m==null){
				WriteMsg(SystemCode.code_1001.getKey(), "Error Market");
				return;
			} 
			
//			//这里作一个临时的ip限制，稳定后就要去掉
//			String[] specialIp = new String[]{"127.0.0.1", "192.168.4.22", "115.29.229.58"};
//			boolean hasIp = false;
//			String ip = ip();
////			if("7c542832-8c08-40f2-af22-76ba6c02fdc2".equals(key)){
////				System.out.println("下单的accesskey是"+key+"\tip="+ip + "\tcurrency="+currency + "\t"+new Date());
////			}
//			for(int i=0;i<specialIp.length;i++){
//				if(ip.equals(specialIp[i])){
//					hasIp = true;
//				}
//			}
//			if(!hasIp && (currency.equalsIgnoreCase("ethbtc") || currency.equalsIgnoreCase("eth_btc") ) ){
//				WriteMsg(SystemCode.code_1001.getKey(), "Error Market--");
//				return;
//			}
			
			
			int userid= Integer.parseInt(json.getString("userId"));	
			
			Message myObj = new Message();    
	        myObj.setUserId(userid);
	        myObj.setWebId(6);
	        myObj.setNumbers(amount);
	        myObj.setTypes(isBuy);
	        myObj.setUnitPrice(price);
	        myObj.setStatus(0);
	        myObj.setMt(mt);
	        myObj.setMarket(m.getString("market"));//市场名称
	        myObj.setMessage( customerOrderId );
	        String param=HTTPTcp.ObjectToString(myObj);
		    String rtn=HTTPTcp.DoRequest2(true,m.getString("ip"),m.getIntValue("port"),"/server/entrust",param);
		      
			Message rtn2 =(Message)HTTPTcp.StringToObject(rtn);
			if(rtn2==null){
				log.error("下单api调用trans_server出错，ip=" + m.getString("ip") + "，port="+m.getIntValue("port"));
			}
			SystemCode code = SystemCode.getSystemCode(rtn2.getStatus());
			
			//systemCode 无法转化 或者rtn2.getCode不等于空的情况下，是交易不成功的
			if(code == null || StringUtils.isNotEmpty(rtn2.getCode())){
				code = SystemCode.getCode(Integer.parseInt(rtn2.getCode()));
				if(code == null)
					code = SystemCode.code_1002;
			}
			
			//成功并返回ID号
			if(code.getKey() == 1000){
				long entrustId = rtn2.getNumbers().longValue();//委托的id
				WriteMsg2(code.getKey(), code.getValue(), entrustId + "");
			}else{
				if(rtn2.getStatus()==138L){	//重复提交的委托单
					long entrustId = rtn2.getNumbers().longValue();//委托的id
					WriteMsg2(code.getKey(), code.getValue()+"，用户委托单id:"+customerOrderId, entrustId + "");
				}else if(rtn2.getStatus()==135){	//trans_server返回的是资金不足
					WriteMsg(SystemCode.code_2001.getKey(), SystemCode.code_2001.getValue());
				}else{
					WriteMsg(code.getKey(), code.getValue());
				}
			}
			return;
		} catch (Exception e) {
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue());
			e.printStackTrace();
		}
	}
	
	/**
	 * 取消委托买单或卖单
	 * 
	 * @param accesskey		用户访问密钥
	 * @param id	挂单ID
	 * @param currency  货币类型(目前仅支持BTL/LTC/ETH)
	 */
	@Page(Viewer = JSON)
	public void cancelOrder(){
		try{
			 String key =  request.getParameter("accesskey");
			 long entityId =Long.parseLong(request.getParameter("id"));
			 String currency = request.getParameter("currency");
				
			 //------------------------------------------------------------------------
			 setJsonObject(null);
			
			 if(!accessSign(key)){
				return;
			 }
			 JSONObject json = getJsonObject();
			 if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			 }
			 //=========================================================================================================
			 
			 JSONObject m = Market.getMarketByName(currency);
			 if(m==null){
				WriteMsg(SystemCode.code_1001.getKey(), "Error Market");
				return;
			 } 
			 
			 int userid= Integer.parseInt(json.getString("userId"));	
			 
			
				
			 MessageCancle myObj = new MessageCancle();    
			 myObj.setUserId(userid);
	         myObj.setWebId(webid); 
	         myObj.setMarket(m.getString("market"));
	         myObj.setEntrustId(entityId);//
	         myObj.setStatus(0);
	         String param=HTTPTcp.ObjectToString(myObj);
	        
		     String rtn=HTTPTcp.DoRequest2(true,m.getString("ip"),m.getIntValue("port"),"/server/cancle",param); 

		     MessageCancle rtn2 =(MessageCancle)HTTPTcp.StringToObject(rtn);
			
			 SystemCode code = SystemCode.getSystemCode(rtn2.getStatus());
			 
			 WriteMsg(code.getKey(), code.getValue());
			 return;
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取买单或卖单
	 * 
	 * @param accesskey		用户访问密钥
	 * @param id	挂单ID
	 * @param currency  货币类型(目前仅支持BTL/LTC/ETH)
	 */
	@Page(Viewer = JSON)
	public void getOrder(){
		try{
			 String key =  request.getParameter("accesskey");
			 String entityId = request.getParameter("id");
			 String currency = request.getParameter("currency");
			 //------------------------------------------------------------------------
			 setJsonObject(null);
			
			 if(!accessSign(key)){
				return;
			 }
			 JSONObject json = getJsonObject();
			 if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			 }
			 //=========================================================================================================
			 
			 JSONObject m = Market.getMarketByName(currency);
			 if(m==null){
				WriteMsg(SystemCode.code_1001.getKey(), "Error Market");
				return;
			 } 

			 int userid= Integer.parseInt(json.getString("userId"));
			 //CACHE_EID + Exchange.da.getName() + "_" + beb.getId()
			 String jmessage = (String)Cache.GetObj(CACHE_EID + m.getString("market") + "_" + entityId);//获取缓存数据
			 
			 if(jmessage == null){
				 RecordMessage myObj = new RecordMessage();
		         myObj.setUserId(userid);
		         myObj.setWebId(webid);
		         myObj.setTypes(101);//详情
		         myObj.setMarket(m.getString("market"));
		         myObj.setMessage(entityId+"");//借用来保存id的
		         String param=HTTPTcp.ObjectToString(myObj);
			     String rtn=HTTPTcp.DoRequest2(true,m.getString("ip"),m.getIntValue("port"),"/server/getOrders",param);

				 RecordMessage rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
				 
				 SystemCode code = SystemCode.getSystemCode(rtn2.getStatus());
				 
				 if(code != null && code.getKey() != 1000){
					 WriteMsg(code.getKey(), code.getValue());
					 return;
				 }else{
					 jmessage = rtn2.getMessage();
				 }
			 }
			 
			 if(jmessage != null && jmessage.length() > 0){
				 json = JSONObject.parseObject("{"+jmessage+"}");
				 JSONArray array = (JSONArray) json.get("record");
				 if(array.size() > 0){
					JSONArray arr = (JSONArray) array.get(0);
			 		Order order = new Order();
				 	order.setId(arr.getString(0));
				 	order.setType(arr.getIntValue(5));
				 	order.setPrice(arr.getDouble(1));
				 	order.setCurrency(currency);
				 	order.setTrade_amount(arr.getDouble(3));
				 	order.setTrade_money(arr.getDouble(4));
					order.setTotal_amount(arr.getDouble(2));
					order.setTrade_date(arr.getLong(6));
					order.setStatus(arr.getIntValue(7));
					order.setFees( arr.size()>8?arr.getDouble(8):0 );
					order = resetOrderStatus(order);
					json = (JSONObject)JSONObject.toJSON(order);
				 }else{
					 WriteMsg(SystemCode.code_3001.getKey(), SystemCode.code_3001.getValue());
					 return;
				 }
				 Response.append(json.toString());
				 return;
			 }
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取多个买单或卖单，10条记录为一页
	 * 
	 * @param accesskey		用户访问密钥
	 * @param tradeType	交易类型1/0(buy/sell)
	 * @param currency  货币类型(目前仅支持BTL/LTC/ETH)
	 * @param pageIndex 当前页码
	 */
	@Page(Viewer = JSON)
	public void getOrders(){
		try{
			 String key =  request.getParameter("accesskey");
			 int tradeType = intParam("tradeType");
			 String currency = request.getParameter("currency");
			 int pageIndex = intParam("pageIndex");
			 if(pageIndex == 0) pageIndex = 1;
			 
			 //------------------------------------------------------------------------
			 setJsonObject(null);
			
			 if(!accessSign(key)){
				return;
			 }
			 JSONObject json = getJsonObject();
			 if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			 }
			 //=========================================================================================================

			 JSONObject m = Market.getMarketByName(currency);
			 if(m==null){
				WriteMsg(SystemCode.code_1001.getKey(), "Error Market");
				return;
			 } 

			 int userId= Integer.parseInt(json.getString("userId"));	
			 
//			 if(type==-1&&pageIndex==1&&status==3){ 
//					Object obj=Cache.Get(m.getString("market")+"_userrecord_version_"+userId);	
//					//log.info("get"+obj);
//					if (obj != null) {
//						if (obj != null && lastTime ==Long.parseLong(obj.toString())) { // 相等
//							Response.append(jsoncallback + "([{\"lastTime\":"
//									+ lastTime + "}])");
//							return;
//						} else {// 时间戳有变化
//							String data = Cache.Get(m.getString("market")+"_userrecord_" + userId);
//							Response.append(jsoncallback + "([{" + data + "}])");
//							return;
//						}
//					} 
//			 }
			 
			 
			  RecordMessage myObj = new RecordMessage();    
		      myObj.setUserId(userId);
		      myObj.setAuth("");
		      myObj.setWebId(webid);
		      myObj.setTypes(tradeType);
		      myObj.setTimeFrom(0);
		      myObj.setTimeTo(0);
		      myObj.setNumberFrom(0);
		      myObj.setNumberTo(0);
		      myObj.setPriceFrom(0);
		      myObj.setPriceTo(0);
		      myObj.setPageindex(pageIndex);
		      myObj.setPageSize(9);
		      myObj.setStatus(0);
		      myObj.setMarket(m.getString("market"));
		      
		        
	          String param=HTTPTcp.ObjectToString(myObj);
		      String rtn=HTTPTcp.DoRequest2(true,m.getString("ip"),m.getIntValue("port"),"/server/getOrders",param);
			  RecordMessage rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
			 
			  SystemCode code = SystemCode.getSystemCode(rtn2.getStatus());
			 
			  if(code != null && code.getKey() != 1000){
				 WriteMsg(code.getKey(), code.getValue());
				 return;
			  }else{
				  json = JSONObject.parseObject("{"+rtn2.getMessage()+"}");
				  JSONArray array = (JSONArray) json.get("record");
				  List<Order> list = new ArrayList<Order>();
				  if(array.size() > 0){
				 	for (int i = 0;i < array.size(); i++) {
				 		JSONArray arr = (JSONArray) array.get(i);
				 		Order order = new Order();
					 	order.setId(arr.getString(0));
					 	order.setType(arr.getIntValue(5));
					 	order.setPrice(arr.getDouble(1));
					 	order.setCurrency(currency);
					 	order.setTrade_amount(arr.getDouble(3));
					 	order.setTrade_money(arr.getDouble(4));
						order.setTotal_amount(arr.getDouble(2));
						order.setTrade_date(arr.getLong(6));
						order.setStatus(arr.getIntValue(7));
						order.setFees( arr.size()>8?arr.getDouble(8):0 );
						order = resetOrderStatus(order);
						list.add(order);
					}
				 	Response.append(JSONArray.toJSON(list).toString()); 
				 	return;
				  }
			  }
			  WriteMsg(SystemCode.code_3001.getKey(), SystemCode.code_3001.getValue());
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取多个买单或卖单，pageSize条记录为一页
	 * 
	 * @param accesskey		用户访问密钥
	 * @param tradeType	交易类型1/0(buy/sell)
	 * @param currency  货币类型(目前仅支持BTL/LTC/ETH)
	 * @param pageIndex 当前页码
	 */
	@Page(Viewer = JSON)
	public void getOrdersNew(){
		try{
			 String key =  request.getParameter("accesskey");
			 int tradeType = intParam("tradeType");
			 String currency = request.getParameter("currency");
			 int pageIndex = intParam("pageIndex");
			 if(pageIndex == 0) pageIndex = 1;
			 int pageSize = intParam("pageSize");
			 if(pageSize == 0) pageSize = 10;
			 if(pageSize >= LIMIT_PAGE_SIZE) pageSize = LIMIT_PAGE_SIZE;
			 
			 //------------------------------------------------------------------------
			 setJsonObject(null);
			
			 if(!accessSign(key)){
				return;
			 }
			 JSONObject json = getJsonObject();
			 if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			 }
			 //=========================================================================================================
			 
			 JSONObject m = Market.getMarketByName(currency);
			 if(m==null){
				WriteMsg(SystemCode.code_1001.getKey(), "Error Market");
				return;
			 } 

			 int userId= Integer.parseInt(json.getString("userId"));	
			 
			  RecordMessage myObj = new RecordMessage();    
		      myObj.setUserId(userId);
		      myObj.setAuth("");
		      myObj.setWebId(webid);
		      myObj.setTypes(tradeType);
		      myObj.setTimeFrom(0);
		      myObj.setTimeTo(0);
		      myObj.setNumberFrom(0);
		      myObj.setNumberTo(0);
		      myObj.setPriceFrom(0);
		      myObj.setPriceTo(0);
		      myObj.setPageindex(pageIndex);
		      myObj.setPageSize(pageSize);
		      myObj.setStatus(0);
		      myObj.setMarket(m.getString("market"));  
	          String param=HTTPTcp.ObjectToString(myObj);
		      String rtn=HTTPTcp.DoRequest2(true,m.getString("ip"),m.getIntValue("port"),"/server/getOrders",param);
			  RecordMessage rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
			 
			  SystemCode code = SystemCode.getSystemCode(rtn2.getStatus());
			 
			  if(code != null && code.getKey() != 1000){
				 WriteMsg(code.getKey(), code.getValue());
				 return;
			  }else{
				  json = JSONObject.parseObject("{"+rtn2.getMessage()+"}");
				  JSONArray array = (JSONArray) json.get("record");
				  List<Order> list = new ArrayList<Order>();
				  if(array.size() > 0){
				 	for (int i = 0;i < array.size(); i++) {
				 		JSONArray arr = (JSONArray) array.get(i);
				 		Order order = new Order();
					 	order.setId(arr.getString(0));
					 	order.setType(arr.getIntValue(5));
					 	order.setPrice(arr.getDouble(1));
					 	order.setCurrency(currency);
					 	order.setTrade_amount(arr.getDouble(3));
					 	order.setTrade_money(arr.getDouble(4));
						order.setTotal_amount(arr.getDouble(2));
						order.setTrade_date(arr.getLong(6));
						order.setStatus(arr.getIntValue(7));
						order.setFees( arr.size()>8?arr.getDouble(8):0 );
						order = resetOrderStatus(order);
						list.add(order);
					}
				 	Response.append(JSONArray.toJSON(list).toString()); 
				 	return;
				  }
			  }
			  WriteMsg(SystemCode.code_3001.getKey(), SystemCode.code_3001.getValue());
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取多个买单和卖单，pageSize条记录为一页
	 * 与gerOrders的区别是取消tradeType字段过滤，可同时获取买单和卖单
	 * 
	 * @param accesskey		用户访问密钥
	 * @param currency  货币类型(目前仅支持BTL/LTC/ETH)
	 * @param pageIndex 当前页码
	 */
	@Page(Viewer = JSON)
	public void getOrdersIgnoreTradeType(){
		try{
			 String key =  request.getParameter("accesskey");
			 String currency = request.getParameter("currency");
			 int pageIndex = intParam("pageIndex");
			 if(pageIndex == 0) pageIndex = 1;
			 int pageSize = intParam("pageSize");
			 if(pageSize == 0) pageSize = 10;
			 if(pageSize >= LIMIT_PAGE_SIZE) pageSize = LIMIT_PAGE_SIZE;
			 
			 //------------------------------------------------------------------------
			 setJsonObject(null);
			
			 if(!accessSign(key)){
				return;
			 }
			 JSONObject json = getJsonObject();
			 if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			 }
			 //=========================================================================================================
			 
			 JSONObject m = Market.getMarketByName(currency);
			 if(m==null){
				WriteMsg(SystemCode.code_1001.getKey(), "Error Market");
				return;
			 } 

			 int userId= Integer.parseInt(json.getString("userId"));	
			 
			 
			 ///先从缓存取出第一页
			 
			 
			  RecordMessage myObj = new RecordMessage();    
		      myObj.setUserId(userId);
		      myObj.setAuth("");
		      myObj.setWebId(webid);
		      myObj.setTypes(-1);
		      myObj.setTimeFrom(0);
		      myObj.setTimeTo(0);
		      myObj.setNumberFrom(0);
		      myObj.setNumberTo(0);
		      myObj.setPriceFrom(0);
		      myObj.setPriceTo(0);
		      myObj.setPageindex(pageIndex);
		      myObj.setPageSize(pageSize);
		      myObj.setStatus(0);
		      myObj.setMarket(m.getString("market"));
	          String param=HTTPTcp.ObjectToString(myObj);
		      String rtn=HTTPTcp.DoRequest2(true,m.getString("ip"),m.getIntValue("port"),"/server/getOrders",param);
			  RecordMessage rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
			 
			  SystemCode code = SystemCode.getSystemCode(rtn2.getStatus());
			 
			  if(code != null && code.getKey() != 1000){
				 WriteMsg(code.getKey(), code.getValue());
				 return;
			  }else{
				  json = JSONObject.parseObject("{"+rtn2.getMessage()+"}");
				  JSONArray array = (JSONArray) json.get("record");
				  List<Order> list = new ArrayList<Order>();
				  if(array.size() > 0){
				 	for (int i = 0;i < array.size(); i++) {
				 		JSONArray arr = (JSONArray) array.get(i);
				 		Order order = new Order();
					 	order.setId(arr.getString(0));
					 	order.setType(arr.getIntValue(5));
					 	order.setPrice(arr.getDouble(1));
					 	order.setCurrency(currency);
					 	order.setTrade_amount(arr.getDouble(3));
					 	order.setTrade_money(arr.getDouble(4));
						order.setTotal_amount(arr.getDouble(2));
						order.setTrade_date(arr.getLong(6));
						order.setStatus(arr.getIntValue(7));
						order.setFees( arr.size()>8?arr.getDouble(8):0 );
						order = resetOrderStatus(order);
						list.add(order);
					}
				 	Response.append(JSONArray.toJSON(list).toString()); 
				 	return;
				  }
			  }
			  WriteMsg(SystemCode.code_3001.getKey(), SystemCode.code_3001.getValue());
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取未成交买单和卖单，pageSize条记录为一页
	 * 
	 * @param accesskey		用户访问密钥
	 * @param currency  货币类型(目前仅支持BTL/LTC/ETH)
	 * @param pageIndex 当前页码
	 */
	@Page(Viewer = JSON)
	public void getUnfinishedOrdersIgnoreTradeType(){
		try{
			 String key =  request.getParameter("accesskey");
			 String currency = request.getParameter("currency");
			 int pageIndex = intParam("pageIndex");
			 if(pageIndex == 0) pageIndex = 1;
			 int pageSize = intParam("pageSize");
			 if(pageSize == 0) pageSize = 10;
			 if(pageSize >= LIMIT_PAGE_SIZE) pageSize = LIMIT_PAGE_SIZE;
			 
			 //------------------------------------------------------------------------
			 setJsonObject(null);
			
			 if(!accessSign(key)){
				return;
			 }
			 JSONObject json = getJsonObject();
			 if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			 }
			 //=========================================================================================================
			 
			 JSONObject m = Market.getMarketByName(currency);
			 if(m==null){
				WriteMsg(SystemCode.code_1001.getKey(), "Error Market");
				return;
			 } 

			 int userId= Integer.parseInt(json.getString("userId"));	
			 
			 JSONArray array = null;
			 pageSize = 10;
			 
			 String data = RedisUtil.get(m.getString("market")+"_userrecord_" + userId);
			 if (data != null && pageIndex<=1) {
				    json = JSONObject.parseObject("{" + data + "}");
				    array = json.getJSONArray("record");
				    log.info("从内存获取数据：" + userId);
			 }
			 if(array == null){
				  RecordMessage myObj = new RecordMessage();    
			      myObj.setUserId(userId);
			      myObj.setAuth("");
			      myObj.setWebId(webid);
			      myObj.setTypes(-1);
			      myObj.setTimeFrom(0);
			      myObj.setTimeTo(0);
			      myObj.setNumberFrom(0);
			      myObj.setNumberTo(0);
			      myObj.setPriceFrom(0);
			      myObj.setPriceTo(0);
			      myObj.setPageindex(pageIndex);
			      myObj.setPageSize(pageSize);
			      myObj.setStatus(3);
			      myObj.setMarket(m.getString("market"));
			      
		          String param=HTTPTcp.ObjectToString(myObj);
			      String rtn=HTTPTcp.DoRequest2(true,m.getString("ip"),m.getIntValue("port"),"/server/getOrders",param);
				  RecordMessage rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
				 
				  SystemCode code = SystemCode.getSystemCode(rtn2.getStatus());
				 
				  if(code != null && code.getKey() != 1000){
					 WriteMsg(code.getKey(), code.getValue());
					 return;
				  }else{
					  json = JSONObject.parseObject("{"+rtn2.getMessage()+"}");
					  array = (JSONArray) json.get("record");
				  }
			  }
			 
			  if(array != null){
				  List<Order> list = new ArrayList<Order>();
				  if(array.size() > 0){
				 	for (int i = 0;i < array.size(); i++) {
				 		JSONArray arr = (JSONArray) array.get(i);
				 		Order order = new Order();
					 	order.setId(arr.getString(0));
					 	order.setType(arr.getIntValue(5));
					 	order.setPrice(arr.getDouble(1));
					 	order.setCurrency(currency);
					 	order.setTrade_amount(arr.getDouble(3));
					 	order.setTrade_money(arr.getDouble(4));
						order.setTotal_amount(arr.getDouble(2));
						order.setTrade_date(arr.getLong(6));
						order.setStatus(arr.getIntValue(7));
						order.setFees( arr.size()>8?arr.getDouble(8):0 );
						order = resetOrderStatus(order);
						list.add(order);
					}
				 	Response.append(JSONArray.toJSON(list).toString()); 
				 	return;
				  }
			  }
			  WriteMsg(SystemCode.code_3001.getKey(), SystemCode.code_3001.getValue());
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取账户信息
	 * @param accesskey		用户访问密钥
	 */
	@Page(Viewer = JSON)
	public void getAccountInfo(){
		try{
			String key = request.getParameter("accesskey");
			//------------------------------------------------------------------------
//			setJsonObject(null);
//			
			if(!accessSign(key)){
				return;
			}
//			
//			JSONObject json = getJsonObject();
//			if(json == null){
//				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
//				return;
//			}
			//=========================================================================================================
			VipResponse response = UserManager.getInstance().getUserByAccessKey(key, 1);
			String resMsg = response.getMsg();
			JSONObject json = null;
			if(resMsg.startsWith("{")){
				json = JSONObject.parseObject(response.getMsg());
			}
			
			if(json != null && response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					//用户是否通过验证放到这里来，也是一样的
					if(VerifiUtil.users == null) VerifiUtil.users = new HashMap<String, Object>();
					JSONObject userObject = (JSONObject) VerifiUtil.users.get("user_object_json_" + key);
					if(!accessSign(key)){	//如果验证不通过的话，并且内存api锁定状态为1，就改为2
						if(userObject == null){
							userObject = (JSONObject) VerifiUtil.users.get("user_object_json_" + key);
						}
						if(userObject.getIntValue("apiStatus") == 1){
							//System.out.println("更改内存API锁住状态为2");
							userObject.put("apiStatus", 2);
						}
						return;
					}
					if(userObject == null){
						userObject = (JSONObject) VerifiUtil.users.get("user_object_json_" + key);
					}
					json = JSONObject.parseObject(json.getString("datas"));
					//json.put("limit", 10);
					
					if(userObject != null && json.containsKey("limit") && userObject.getIntValue("limit") != json.getIntValue("limit")){
						if(json.getIntValue("limit") > 0){
							userObject.put("limit", json.getIntValue("limit"));
						}else{
							userObject.put("limit", 10);
						}
					}
					Response.append(json.toString());
					return;
				}else{
					if("4001".equals(response.getCode())){
						//System.out.println("更改内存API锁住状态为1");
						JSONObject userObject = (JSONObject) VerifiUtil.users.get("user_object_json_" + key);
						userObject.put("apiStatus", 1);
						//被锁住了
						WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
						return;
					}
				}
			}else{
				if("4001".equals(response.getCode())){
					//System.out.println("更改内存API锁住状态为1");
					JSONObject userObject = (JSONObject) VerifiUtil.users.get("user_object_json_" + key);
					if(userObject != null){
						userObject.put("apiStatus", 1);
					}
					//被锁住了
					WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
					return;
				}else{
					log.error("current json:" + json + ",code:" + response.getCode());
				}
			}
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			return;
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取充值地址
	 * @param accesskey		用户访问密钥
	 */
	@Page(Viewer = JSON)
	public void getUserAddress(){
		try{
			String key = request.getParameter("accesskey");
			String currency = param("currency");
			//------------------------------------------------------------------------
			setJsonObject(null);
			
			if(!accessSign(key)){
				return;
			}
			
			JSONObject json = getJsonObject();
			if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			}
			
			//=========================================================================================================
			VipResponse response = UserManager.getInstance().getUserAddress(key, currency);
			String resMsg = response.getMsg();
			json = null;
			if(resMsg.startsWith("{")){
				json = JSONObject.parseObject(response.getMsg());
			}
			
			if(json != null && response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					JSONObject datas = json.getJSONObject("datas");
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getClassName(), true, datas.toString());
					return;
				}else{
					if("4001".equals(response.getCode())){
						//被锁住了
						WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
						return;
					}
				}
			}else{
				if("4001".equals(response.getCode())){
					WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
					return;
				}else{
					log.error("current json:" + json + ",code:" + response.getCode());
					WriteMsg(Integer.parseInt(response.getCode()), json.getString("des"));
					return;
				}
			}
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取认证的提现地址
	 * @param accesskey		用户访问密钥
	 */
	@Page(Viewer = JSON)
	public void getWithdrawAddress(){
		try{
			String key = request.getParameter("accesskey");
			String currency = param("currency");
			//------------------------------------------------------------------------
			setJsonObject(null);
			
			if(!accessSign(key)){
				return;
			}
			
			JSONObject json = getJsonObject();
			if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			}
			
			//=========================================================================================================
			VipResponse response = UserManager.getInstance().getWithdrawAddress(key, currency);
			String resMsg = response.getMsg();
			json = null;
			if(resMsg.startsWith("{")){
				json = JSONObject.parseObject(response.getMsg());
			}
			
			if(json != null && response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					JSONArray datas = json.getJSONArray("datas");
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getClassName(), true, datas.toString());
					return;
				}else{
					if("4001".equals(response.getCode())){
						//被锁住了
						WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
						return;
					}
				}
			}else{
				if("4001".equals(response.getCode())){
					WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
					return;
				}else{
					log.error("current json:" + json + ",code:" + response.getCode());
					WriteMsg(Integer.parseInt(response.getCode()), json.getString("des"));
					return;
				}
			}
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取提现记录
	 * @param accesskey		用户访问密钥
	 */
	@Page(Viewer = JSON)
	public void getWithdrawRecord(){
		try{
			String key = request.getParameter("accesskey");
			String currency = param("currency");
			int pageIndex = intParam("pageIndex");
			if(pageIndex == 0) pageIndex = 1;
			int pageSize = intParam("pageSize");
			if(pageSize == 0) pageSize = 10;
			if(pageSize >= LIMIT_PAGE_SIZE) pageSize = LIMIT_PAGE_SIZE;
			//------------------------------------------------------------------------
			setJsonObject(null);
			
			if(!accessSign(key)){
				return;
			}
			
			JSONObject json = getJsonObject();
			if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			}
			
			//=========================================================================================================
			VipResponse response = UserManager.getInstance().getWithdrawRecord(key, currency, pageIndex, pageSize);
			String resMsg = response.getMsg();
			json = null;
			if(resMsg.startsWith("{")){
				json = JSONObject.parseObject(response.getMsg());
			}
			
			if(json != null && response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					JSONObject datas = json.getJSONObject("datas");
					
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getClassName(), true, datas.toString());
					return;
				}else{
					if("4001".equals(response.getCode())){
						//被锁住了
						WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
						return;
					}
				}
			}else{
				if("4001".equals(response.getCode())){
					WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
					return;
				}else{
					log.error("current json:" + json + ",code:" + response.getCode());
					WriteMsg(Integer.parseInt(response.getCode()), json.getString("des"));
					return;
				}
			}
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取充值记录，虚拟货币
	 * @param accesskey		用户访问密钥
	 */
	@Page(Viewer = JSON)
	public void getChargeRecord(){
		try{
			String key = request.getParameter("accesskey");
			String currency = param("currency");
			int pageIndex = intParam("pageIndex");
			if(pageIndex == 0) pageIndex = 1;
			int pageSize = intParam("pageSize");
			if(pageSize == 0) pageSize = 10;
			if(pageSize >= LIMIT_PAGE_SIZE) pageSize = LIMIT_PAGE_SIZE;
			//------------------------------------------------------------------------
			setJsonObject(null);
			
			if(!accessSign(key)){
				return;
			}
			
			JSONObject json = getJsonObject();
			if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			}
			
			//=========================================================================================================
			VipResponse response = UserManager.getInstance().getChargeRecord(key, currency, pageIndex, pageSize);
			String resMsg = response.getMsg();
			json = null;
			if(resMsg.startsWith("{")){
				json = JSONObject.parseObject(response.getMsg());
			}
			
			if(json != null && response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					JSONObject datas = json.getJSONObject("datas");
					
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getClassName(), true, datas.toString());
					return;
				}else{
					if("4001".equals(response.getCode())){
						//被锁住了
						WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
						return;
					}
				}
			}else{
				if("4001".equals(response.getCode())){
					WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
					return;
				}else{
					log.error("current json:" + json + ",code:" + response.getCode());
					WriteMsg(Integer.parseInt(response.getCode()), json.getString("des"));
					return;
				}
			}
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取充值记录，人民币
	 * @param accesskey		用户访问密钥
	 */
	@Page(Viewer = JSON)
	public void getCnyChargeRecord(){
		/*try{
			String key = request.getParameter("accesskey");
			int pageIndex = intParam("pageIndex");
			if(pageIndex == 0) pageIndex = 1;
			int pageSize = intParam("pageSize");
			if(pageSize == 0) pageSize = 10;
			if(pageSize >= LIMIT_PAGE_SIZE) pageSize = LIMIT_PAGE_SIZE;
			//------------------------------------------------------------------------
			setJsonObject(null);
			
			if(!accessSign(key)){
				return;
			}
			
			JSONObject json = getJsonObject();
			if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			}
			
			//=========================================================================================================
			VipResponse response = UserManager.getInstance().getCnyChargeRecord(key, pageIndex, pageSize);
			String resMsg = response.getMsg();
			json = null;
			if(resMsg.startsWith("{")){
				json = JSONObject.parseObject(response.getMsg());
			}
			
			if(json != null && response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					JSONObject datas = json.getJSONObject("datas");
					
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getClassName(), true, datas.toString());
					return;
				}else{
					if("4001".equals(response.getCode())){
						//被锁住了
						WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
						return;
					}
				}
			}else{
				if("4001".equals(response.getCode())){
					WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
					return;
				}else{
					log.error("current json:" + json + ",code:" + response.getCode());
					WriteMsg(Integer.parseInt(response.getCode()), json.getString("des"));
					return;
				}
			}
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			ex.printStackTrace();
		}*/
	}
	
	/**
	 * 获取充值记录，人民币
	 * @param accesskey		用户访问密钥
	 */
	@Page(Viewer = JSON)
	public void getCnyWithdrawRecord(){
		/*try{
			String key = request.getParameter("accesskey");
			int pageIndex = intParam("pageIndex");
			if(pageIndex == 0) pageIndex = 1;
			int pageSize = intParam("pageSize");
			if(pageSize == 0) pageSize = 10;
			if(pageSize >= LIMIT_PAGE_SIZE) pageSize = LIMIT_PAGE_SIZE;
			//------------------------------------------------------------------------
			setJsonObject(null);
			
			if(!accessSign(key)){
				return;
			}
			
			JSONObject json = getJsonObject();
			if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			}
			
			//=========================================================================================================
			VipResponse response = UserManager.getInstance().getCnyWithdrawRecord(key, pageIndex, pageSize);
			String resMsg = response.getMsg();
			json = null;
			if(resMsg.startsWith("{")){
				json = JSONObject.parseObject(response.getMsg());
			}
			
			if(json != null && response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					JSONObject datas = json.getJSONObject("datas");
					
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getClassName(), true, datas.toString());
					return;
				}else{
					if("4001".equals(response.getCode())){
						//被锁住了
						WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
						return;
					}
				}
			}else{
				if("4001".equals(response.getCode())){
					WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
					return;
				}else{
					log.error("current json:" + json + ",code:" + response.getCode());
					WriteMsg(Integer.parseInt(response.getCode()), json.getString("des"));
					return;
				}
			}
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			ex.printStackTrace();
		}*/
	}
	
	/**
	 * 取消提现操作
	 * @param accesskey		用户访问密钥
	 */
	@Page(Viewer = JSON)
	public void cancelWithdraw(){
		try{
			String key = request.getParameter("accesskey");
			String currency = param("currency");
			long downloadId = longParam("downloadId");
			String safePwd = URLDecoder.decode(param("safePwd") , "utf-8");
			//------------------------------------------------------------------------
			setJsonObject(null);
			
			if(!accessSign(key)){
				return;
			}
			
			JSONObject json = getJsonObject();
			if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			}
			
			if(true){	//暂时停止使用
				WriteMsg(SystemCode.code_1009.getKey(), SystemCode.code_1009.getValue());
				return;
			}
			
			//=========================================================================================================
			VipResponse response = UserManager.getInstance().cancelWithdraw(key, currency, downloadId, safePwd);
			String resMsg = response.getMsg();
			json = null;
			if(resMsg.startsWith("{")){
				json = JSONObject.parseObject(response.getMsg());
			}
			
			if(json != null && response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					JSONObject datas = json.getJSONObject("datas");
					
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getClassName(), true, datas.toString());
					return;
				}else{
					if("4001".equals(response.getCode())){
						//被锁住了
						WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
						return;
					}
				}
			}else{
				if("4001".equals(response.getCode())){
					WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
					return;
				}else{
					log.error("current json:" + json + ",code:" + response.getCode());
					WriteMsg(Integer.parseInt(response.getCode()), json.getString("des"));
					return;
				}
			}
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			ex.printStackTrace();
		}
	}
	
	
	
	/**
	 * 提现操作
	 * @param accesskey		用户访问密钥
	 */
	@Page(Viewer = JSON)
	public void withdraw(){
		try{
			String key = request.getParameter("accesskey");
			String currency = param("currency");
			double amount = doubleParam("amount");
			String receiveAddr = URLDecoder.decode(param("receiveAddr") , "utf-8");
			double fees = doubleParam("fees");
			String safePwd = URLDecoder.decode(param("safePwd") , "utf-8");
			int itransfer = intParam("itransfer");
			//------------------------------------------------------------------------
			setJsonObject(null);
			
			if(!accessSign(key)){
				return;
			}
			
			JSONObject json = getJsonObject();
			if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			}
			
			//=========================================================================================================
			VipResponse response = UserManager.getInstance().withdraw(key, currency, amount, receiveAddr, fees, safePwd, itransfer);
			String resMsg = response.getMsg();
			json = null;
			if(resMsg.startsWith("{")){
				json = JSONObject.parseObject(response.getMsg());
			}
			
			if(json != null && response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					JSONObject datas = json.getJSONObject("datas");
					
					WriteMsg2(SystemCode.code_1000.getKey(), SystemCode.code_1000.getClassName(), datas.getString("downloadId"));
					return;
				}else{
					if("4001".equals(response.getCode())){
						//被锁住了
						WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
						return;
					}
				}
			}else{
				if("4001".equals(response.getCode())){
					WriteMsg(SystemCode.code_4001.getKey(), SystemCode.code_4001.getClassName());
					return;
				}else{
					log.error("current json:" + json + ",code:" + response.getCode());
					WriteMsg(Integer.parseInt(response.getCode()), json.getString("des"));
					return;
				}
			}
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getClassName());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取某用户的交易记录，pageSize条记录为一页
	 * 
	 * @param accesskey		用户访问密钥
	 * @param currency  货币类型(目前仅支持BTL/LTC/ETH)
	 * @param pageIndex 当前页码
	 * @param pageSize 页的长度
	 * @param sinceId 游标id
	 */
	@Page(Viewer = JSON)
	public void getTransRecord(){
		try{
			 String key =  request.getParameter("accesskey");
			 String currency = request.getParameter("currency");
			 int pageSize = intParam("pageSize");
			 if(pageSize == 0) pageSize = 10;
			 if(pageSize >= LIMIT_PAGE_SIZE) pageSize = LIMIT_PAGE_SIZE;
			 Long sinceId = longParam("sinceId");
			 
			 //------------------------------------------------------------------------
			 setJsonObject(null);
			
			 if(!accessSign(key)){
				return;
			 }
			 JSONObject json = getJsonObject();
			 if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			 }
			 //=========================================================================================================
			 
			 JSONObject m = Market.getMarketByName(currency);
			 if(m==null){
				WriteMsg(SystemCode.code_1001.getKey(), "Error Market");
				return;
			 } 

			 int userId= Integer.parseInt(json.getString("userId"));	
			 
			 //如果sinceId为0，先从缓存查下有无数据，无数据获取，有数据就返回
			 String rtn = "";
			 boolean hasGetFromServer = false;	//是否又从trans_server取数据
			 
			 if(sinceId==0){
				 rtn = Cache.Get( m.getString("market")+"_trade_record_"+userId );
			 }
			 
			 if(rtn==null || rtn.length()==0){
				 RecordMessage myObj = new RecordMessage();    
				 myObj.setUserId(userId);
				 myObj.setMessage(sinceId+"");
				 myObj.setPageSize(pageSize);
				 myObj.setMarket(m.getString("market"));
				 String param=HTTPTcp.ObjectToString(myObj);
				 rtn=HTTPTcp.DoRequest2(true,m.getString("ip"),m.getIntValue("port"),"/server/getTradeRecord",param);
				 hasGetFromServer = true;
			 }
			 
			 //sinceId为0的数据，缓存60秒
			 if(sinceId==0 && hasGetFromServer){
				 Cache.Set(m.getString("market")+"_trade_record_"+userId, rtn, 60);
			 }
			 
			 RecordMessage rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
			 
			 SystemCode code = SystemCode.getSystemCode(rtn2.getStatus());
			 
			 if(code != null && code.getKey() != 1000){
				 WriteMsg(code.getKey(), code.getValue());
			 }else{
				  json = JSONObject.parseObject("{"+rtn2.getMessage()+"}");
				  JSONArray array = (JSONArray) json.get("record");
				  JSONArray list = new JSONArray();
				  if(array.size() > 0){
				 	for (int i = 0;i < array.size(); i++) {
				 		//强制分页
				 		if(i==pageSize){
				 			continue;
				 		}
				 		
				 		JSONArray arr = (JSONArray) array.get(i);
				 		TransRecord record = new TransRecord();
				 		record.setTransRecordId( arr.getLong(0) );
				 		record.setUnitPrice( new BigDecimal(arr.getString(1)) );
				 		record.setTotalMoney( new BigDecimal(arr.getString(2)) );
				 		record.setNumbers( new BigDecimal(arr.getString(3)) );
				 		record.setTypes( arr.getIntValue(4) );
				 		record.setSubmitTime( arr.getLong(5) );
				 		record.setEntrustIdBuy( arr.getLong(6) );
				 		record.setEntrustIdSell( arr.getLong(7) );
				 		list.add(record);
					}
				 	Response.append(list.toJSONString()); 
				 	return;
				  }
			  }
			  WriteMsg(SystemCode.code_3008.getKey(), SystemCode.code_3008.getValue());
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue());
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取用户的某条委托单，的交易记录
	 * 
	 * @param accesskey		用户访问密钥
	 * @param currency  货币类型(目前仅支持BTL/LTC/ETH)
	 * @param pageIndex 当前页码
	 * @param pageSize 页的长度
	 * @param sinceId 游标id
	 */
	@Page(Viewer = JSON)
	public void getOrderTransRecord(){
		try{
			 String key =  request.getParameter("accesskey");
			 String currency = request.getParameter("currency");
			 int pageIndex = intParam("pageIndex");
			 if(pageIndex == 0) pageIndex = 1;
			 int pageSize = intParam("pageSize");
			 if(pageSize == 0) pageSize = 10;
			 if(pageSize >= LIMIT_PAGE_SIZE) pageSize = LIMIT_PAGE_SIZE;
			 Long orderId = longParam("orderId");
			 
			 //------------------------------------------------------------------------
			 setJsonObject(null);
			
			 if(!accessSign(key)){
				return;
			 }
			 JSONObject json = getJsonObject();
			 if(json == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());
				return;
			 }
			 //=========================================================================================================
			 
			 JSONObject m = Market.getMarketByName(currency);
			 if(m==null){
				WriteMsg(SystemCode.code_1001.getKey(), "Error Market");
				return;
			 } 

			 int userId= Integer.parseInt(json.getString("userId"));	
			 
			  RecordMessage myObj = new RecordMessage();    
		      myObj.setUserId(userId);
		      myObj.setMessage(orderId+"");
		      myObj.setPageindex(pageIndex);
		      myObj.setPageSize(pageSize);
		      myObj.setMarket(m.getString("market"));  
	          String param=HTTPTcp.ObjectToString(myObj);
		      String rtn=HTTPTcp.DoRequest2(true,m.getString("ip"),m.getIntValue("port"),"/server/getOrderTradeRecord",param);
			  RecordMessage rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
			 
			  SystemCode code = SystemCode.getSystemCode(rtn2.getStatus());
			 
			  if(code != null && code.getKey() != 1000){
				 WriteMsg(code.getKey(), code.getValue());
			  }else{
				  json = JSONObject.parseObject("{"+rtn2.getMessage()+"}");
				  JSONArray array = (JSONArray) json.get("record");
				  JSONArray list = new JSONArray();
				  if(array.size() > 0){
				 	for (int i = 0;i < array.size(); i++) {
				 		JSONArray arr = (JSONArray) array.get(i);
				 		TransRecord record = new TransRecord();
				 		record.setTransRecordId( arr.getLong(0) );
				 		record.setUnitPrice( new BigDecimal(arr.getString(1)) );
				 		record.setTotalMoney( new BigDecimal(arr.getString(2)) );
				 		record.setNumbers( new BigDecimal(arr.getString(3)) );
				 		record.setTypes( arr.getIntValue(4) );
				 		record.setSubmitTime( arr.getLong(5) );
				 		record.setEntrustIdBuy( arr.getLong(6) );
				 		record.setEntrustIdSell( arr.getLong(7) );
				 		list.add(record);
					}
				 	Response.append(list.toJSONString()); 
				 	return;
				  }
			  }
			  WriteMsg(SystemCode.code_3008.getKey(), SystemCode.code_3008.getValue());
		}catch(Exception ex){
			WriteMsg(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue());
			ex.printStackTrace();
		}
	}
	
	/**
	 * btc123用,按用户帐号查找用户信息
	 */
	@Page(Viewer = JSON)
	public void getAccountInfo2(){
		/*try{
			String type = request.getParameter("type");
			String phone = request.getParameter("phone");
			String email = request.getParameter("email");
			String sign = request.getParameter("sign");
			String reqTime = request.getParameter("reqTime");
			String secretKey = ApiConfig.getValue("BTC123_SECRET_KEY");
			secretKey = EncryDigestUtil.digest(secretKey);
			if (StringUtils.isBlank(type)) {
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());
				return;
			}
			String value = type.intern() == "0" ? email : phone;
			if (StringUtils.isBlank(value)) {
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());
				return;
			}
			if(StringUtils.isBlank(sign) || StringUtils.isBlank(secretKey)){
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());
				return;
			}
			String params = "type=" + type + "&phone=" + phone + "&email=" + email;
			
			String hash = EncryDigestUtil.hmacSign(params, secretKey);
			
			if(!hash.equals(sign)){
				WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
				return;
			}
			VipResponse response = UserManager.getInstance().getUserFunds2(type, value);
			JSONObject json = JSONObject.parseObject(response.getMsg());
			
			json = JSONObject.parseObject(json.getJSONArray("datas").get(0));
			Response.append(json);
			return;
			
		}catch(Exception ex){
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
			ex.printStackTrace();
		}*/
	}
	
	/**
	 * 把order的status等于3，trade_amount等于0的，设置为0
	 * @param order
	 * @return
	 */
	private Order resetOrderStatus(Order order){
		if(order.getTrade_amount()==0 && order.getStatus()==3){
			order.setStatus(0);
		}
		//显示手续费
		order.setFees( DigitalUtil.roundDown(order.getFees()*order.getTrade_money(), 8) );
		return order;
	}
	
	private boolean accessSign(String accesskey) throws Exception{
		//请求验证
		Object flag = verifiUtil.validateAuthAccess(this, request);
		if(flag instanceof SystemCode){
			 WriteMsg(((SystemCode)flag).getKey(),((SystemCode) flag).getValue());
			 return false;
		}else{
			 if(!((Boolean)flag)){
				log.error(accesskey + ",签名失败" + SystemCode.code_1003.getKey() + "," + SystemCode.code_1003.getValue());
				log.error("------------------------------------------------------------------------");
				WriteMsg(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue());
				return false;
			 }
		}

//		//ip白名单，稳定后就要去掉
//		String[] specialIp = new String[]{"127.0.0.1", "192.168.4.22", "61.142.74.139"};
//		boolean hasIp = false;
//		String ip = ip();
//		for(int i=0;i<specialIp.length;i++){
//			if(ip.equals(specialIp[i])){
//				hasIp = true;
//			}
//		}
//		if(!hasIp ){
//			WriteMsg(SystemCode.code_1002.getKey(), "Account Exception");
//			return false;
//		}
		
		return true;
	}
	
	
	public static void main(String[] args) {
//		JSONArray array = new JSONArray();
//		array.add("20141228100270285");
//		String id = array.getString(0);
//		System.out.println(id);
		try {
			VipResponse response = UserManager.getInstance().getUserByAccessKey("c333642f-2ee5-4fee-8bb5-25ea8777e3a6", 0);
			JSONObject json = JSONObject.parseObject(response.getMsg());
			json = JSONObject.parseObject(json.getString("datas"));
			System.out.println(JSONObject.parseObject(response.getMsg()).getString("datas"));
			System.out.println(json.getIntValue("limit"));
			json.remove("limit");
			System.out.println(json.containsKey("limit"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}