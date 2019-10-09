package com.world.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redis.RedisUtil;
import com.tenstar.HTTPTcp;
import com.tenstar.RecordMessage;
import com.world.cache.Cache;
import com.world.data.mysql.Query;
import com.world.model.Market;
import com.world.model.entitys.entrust.EntrustBean;
import com.world.model.entitys.entrust.EntrustDao;
import com.world.model.entitys.entrust.PlanEntrustBean;
import com.world.model.entitys.entrust.PlanEntrustDao;
import com.world.util.CommonUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据
 * @author pc
 *
 */
public class Record extends BaseAction {
	PlanEntrustDao planEntrustDao = new PlanEntrustDao();
	EntrustDao entrustDao = new EntrustDao();

	private IndexServer server = new IndexServer();
	//使用新页面，不影响旧页面
	//@Page(Viewer = "/cn/u/moreRecord.jsp")
//	@Page(Viewer = "/cn/u/transRecord.jsp")
	public void index() { 
		String coin = GetPrama(0);
		if(coin.length() == 0){
			coin = "btc";
		}
		if(coin.equals("btc")){
			request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
			setAttr("coinType", "BTC");
			setAttr("coinType_s", "btc");
			setAttr("coinSymbol", "฿");
			setAttr("coinName", "比特币");
			setAttr("moneyType", "CNY");
			setAttr("moneyType_s", "cny");
			setAttr("moneySymbol", "￥");
			setAttr("moneyName", "人民币");
			setAttr("feeRate", Market.getMarket("btc_cny").feeRate);
		}else if(coin.equals("eth")){
			request.setAttribute("serijavascripparam", Market.getMarket("eth_cny"));
			setAttr("coinType", "ETH");
			setAttr("coinType_s", "eth");
			setAttr("coinSymbol", "E");
			setAttr("coinName", "以太币");
			setAttr("moneyType", "CNY");
			setAttr("moneyType_s", "cny");
			setAttr("moneySymbol", "￥");
			setAttr("moneyName", "人民币");
			setAttr("feeRate", Market.getMarket("eth_cny").feeRate);
		}else if(coin.equals("etc")){
			request.setAttribute("serijavascripparam", Market.getMarket("etc_cny"));
			setAttr("coinType", "ETC");
			setAttr("coinType_s", "etc");
			setAttr("coinSymbol", "E");
			setAttr("coinName", "以太币");
			setAttr("moneyType", "CNY");
			setAttr("moneyType_s", "cny");
			setAttr("moneySymbol", "￥");
			setAttr("moneyName", "人民币");
			setAttr("feeRate", Market.getMarket("etc_cny").feeRate);
		}else if(coin.equals("dao")){
			request.setAttribute("serijavascripparam", Market.getMarket("dao_cny"));
			setAttr("coinType", "DAO");
			setAttr("coinType_s", "dao");
			setAttr("coinSymbol", "Ð");
			setAttr("coinName", "道资金");
			setAttr("moneyType", "CNY");
			setAttr("moneyType_s", "cny");
			setAttr("moneySymbol", "￥");
			setAttr("moneyName", "人民币");
			
		}else if(coin.equals("ethbtc")){
			request.setAttribute("serijavascripparam", Market.getMarket("eth_btc"));
			setAttr("coinType", "ETH");
			setAttr("coinType_s", "eth");
			setAttr("coinSymbol", "E");
			setAttr("coinName", "以太币");
			setAttr("moneyType", "BTC");
			setAttr("moneyType_s", "btc");
			setAttr("moneySymbol", "฿");
			setAttr("moneyName", "比特币");
			setAttr("feeRate", Market.getMarket("eth_btc").feeRate);
		}else if(coin.equals("daoeth")){
			request.setAttribute("serijavascripparam", Market.getMarket("dao_eth"));
			setAttr("coinType", "DAO");
			setAttr("coinType_s", "dao");
			setAttr("coinSymbol", "Ð");
			setAttr("coinName", "道资金");
			setAttr("moneyType", "ETH");
			setAttr("moneyType_s", "eth");
			setAttr("moneySymbol", "E");
			setAttr("moneyName", "以太币");
			
		}else if(coin.equals("ltc")){
			request.setAttribute("serijavascripparam", Market.getMarket("ltc_cny"));
			setAttr("coinType", "LTC");
			setAttr("coinType_s", "ltc");
			setAttr("coinSymbol", "Ł");
			setAttr("coinName", "莱特币");
			setAttr("moneyType", "CNY");
			setAttr("moneyType_s", "cny");
			setAttr("moneySymbol", "￥");
			setAttr("moneyName", "人民币");
			setAttr("feeRate", Market.getMarket("ltc_cny").feeRate);
		}else{
			request.setAttribute("serijavascripparam", Market.getMarket("btq_cny"));
			setAttr("coinType", "BTQ");
			setAttr("coinType_s", "btq");
			setAttr("coinSymbol", "Q");
			setAttr("coinName", "比特权");
			setAttr("moneyType", "BTC");
			setAttr("moneyType_s", "btc");
			setAttr("moneySymbol", "฿");
			setAttr("moneyName", "比特币");
			setAttr("feeRate", Market.getMarket("btq_cny").feeRate);
		}
	}
	
//	@Page(Viewer = "/cn/u/moreTrans.jsp")
	public void moretrans() { 
		String coin = GetPrama(0);
		if(coin.length() == 0){
			coin = "btc";
		}
		if(coin.equals("btc")){
			request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
		}else if(coin.equals("eth")){
			request.setAttribute("serijavascripparam", Market.getMarket("eth_cny"));
		}else if(coin.equals("etc")){
			request.setAttribute("serijavascripparam", Market.getMarket("etc_cny"));
		}else if(coin.equals("dao")){
			request.setAttribute("serijavascripparam", Market.getMarket("dao_cny"));
		}else if(coin.equals("ethbtc")){
			request.setAttribute("serijavascripparam", Market.getMarket("eth_btc"));
		}else if(coin.equals("daoeth")){
			request.setAttribute("serijavascripparam", Market.getMarket("dao_eth"));
		}else if(coin.equals("ltc")){
			request.setAttribute("serijavascripparam", Market.getMarket("ltc_cny"));
		}else{
			request.setAttribute("serijavascripparam", Market.getMarket("btq_cny"));
		}
	}
	
	/**
	 * 获取用户自己的委托交易记录
	 */
	@Page(Viewer = ".json" )
	public void Get(){
		if(CommonUtil.needInterceptAfterLogin(this, false)){
			return;
		}

		String jsoncallback=request.getParameter("jsoncallback");
		if(!IsLogin()){
			Response.append(jsoncallback + "([{\"lastTime\":-1}])");
			  // toLogin();
			   return;
			}

		Market m=Market.getMarkeByName(GetPrama(0));
		if(m==null){
			Response.append(jsoncallback+"([{\"lastTime\":-2}])"); 
			return;
		}
		int userId=userId();
		/**
		 * /**
	 * 获取用户指定类型的交易管理数据
	 * @param webId 网站id 网站id（暂时都设置城8）
	 * @param userId 用户id 用户id
	 * @param pageIndex 页码从1开始
	 * @param pageSize 页码大小 10
	 * @param type 类型   0 卖出  1 买入  -1不限制
	 * @param timeFrom //时间   System.currentTimeMillis()
	 * @param timeTo
	 * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
	 * @param numberTo//数量查询
	 * @param priceFrom 最低价格
	 * @param priceTo 最高价格
	 * @param pagesize 页码大小 最大200
	 * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
	 * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
	 */
		
		long lastTime = 0;
		int pageIndex = 1;
		int type = -1;
		long timeFrom = 0;
		long timeTo = 0;
		long numberFrom = 0;
		long numberTo = 0;
		long priceFrom = 0;
		long priceTo = 0;
		int pageSize = 0;
		int status = 0;
		int dateTo = 3;
		
		String slastTime = param("lastTime");
		String spageIndex = param("pageIndex");
		String stype = param("type");
		String stimeFrom = param("timeFrom");
		String stimeTo = param("timeTo");
		String snumberFrom = param("numberFrom");
		String snumberTo = param("numberTo");
		String spriceFrom = param("priceFrom");
		String spriceTo = param("priceTo");
		String spageSize = param("pageSize");
		String sstatus = param("status");
		String sdateTo = param("dateTo");
		
		if(slastTime.length() > 0 && StringUtils.isNumeric(slastTime)){
			lastTime=Long.parseLong(slastTime);
		}
		
		if(spageIndex.length() > 0 && StringUtils.isNumeric(spageIndex)){
			pageIndex = Integer.parseInt(spageIndex);
		}
		
		if(stype.length() > 0 && StringUtils.isNumeric(stype)){
			type = Integer.parseInt(stype);
		}
		if(stimeFrom.length() > 0 && StringUtils.isNumeric(stimeFrom)){
			timeFrom = Long.parseLong(stimeFrom);
		}
		if(stimeTo.length() > 0 && StringUtils.isNumeric(stimeTo)){
			timeTo = Long.parseLong(stimeTo);
		}
		if(snumberFrom.length() > 0 && StringUtils.isNumeric(snumberFrom)){
			numberFrom = Long.parseLong(snumberFrom);
		}
		if(snumberTo.length() > 0 && StringUtils.isNumeric(snumberTo)){
			numberTo = Long.parseLong(snumberTo);
		}
		if(spriceFrom.length() > 0 && StringUtils.isNumeric(spriceFrom)){
			priceFrom = Long.parseLong(spriceFrom);
		}
		if(spriceTo.length() > 0 && StringUtils.isNumeric(spriceTo)){
			priceTo = Long.parseLong(spriceTo);
		}
		if(spageSize.length() > 0 && StringUtils.isNumeric(spageSize)){
			pageSize = Integer.parseInt(spageSize);
		}
		if(sstatus.length() > 0){
			status = Integer.parseInt(sstatus);
		}
		if(sdateTo.length() > 0 && StringUtils.isNumeric(sdateTo)){
			dateTo = Integer.parseInt(sdateTo);
		}

		if(type==-1&&pageIndex==1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==3&&pageSize==0){
            String data = RedisUtil.get(m.market+"_userrecord_" + userId);
            if (StringUtil.exist(data)) {
                Response.append(jsoncallback + "([{" + data + "}])");
                return;
            }
		}

		if(type==-1&&pageIndex==1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==3&&pageSize<=30){
            String data = RedisUtil.get(m.market+"_userrecord_" + userId);
            if (StringUtil.exist(data)) {
                data = jsonReplace(data, "record", pageSize);
                data = jsonReplace(data, "precord", pageSize);
                Response.append(jsoncallback + "([{" + data + "}])");
                return;
            }
		}

		if(type==-1&&pageIndex==1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==2&&pageSize==0){
            String data = RedisUtil.get(m.market+"_userrecord_status2_" + userId);
            if (StringUtil.exist(data)) {
                Response.append(jsoncallback + "([{" + data + "}])");
                return;
            }
		}
		if(type==-1&&pageIndex==1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==-1&&pageSize<=30){

            String data = RedisUtil.get(m.market+"_userrecord_" + userId);
            if (StringUtil.exist(data)) {
                data = jsonReplace(data, "record", pageSize);
                data = jsonReplace(data, "precord", pageSize);
                Response.append(jsoncallback + "([{" + data + "}])");
                return;
            }
		}

		RecordMessage myObj = new RecordMessage();    
        myObj.setUserId(userId);
        myObj.setAuth("");
        myObj.setWebId(m.webId);
        myObj.setTypes(type);
        myObj.setTimeFrom(timeFrom);
        myObj.setTimeTo(timeTo);
        myObj.setNumberFrom(numberFrom);
        myObj.setNumberTo(numberTo);
        myObj.setPriceFrom(priceFrom);
        myObj.setPriceTo(priceTo);
        myObj.setPageindex(pageIndex);
        myObj.setPageSize(pageSize); 
        myObj.setStatus(status);
        myObj.setDateTo(dateTo);
        myObj.setMarket(m.market);
        try{
        	RecordMessage rtn2;
        	
        	 String entrustType = param("entrustType");
             String rtn = "";
             if(entrustType.equals("2")){//2为计划委托
                 rtn2 = server.userplanrecord(myObj,m);
             }else{
                 rtn2 = server.userrecord(myObj,m);
             }

        	Response.append(jsoncallback+"([{"+rtn2.getMessage()+"}])"); 
        }catch(Exception ex2){
			log.error("内部异常", ex2);
        	Response.append(jsoncallback+"([{\"lastTime\":0}])"); 
        }
	}

    /**
     * 获取用户自己的委托交易记录
     */
    @Page(Viewer = ".json" )
    public void getHistory(){
        if(CommonUtil.needInterceptAfterLogin(this, false)){
            return;
        }

        String jsoncallback=request.getParameter("jsoncallback");
        if(!IsLogin()){
            Response.append(jsoncallback + "([{\"lastTime\":-1}])");
            // toLogin();
            return;
        }

        Market m=Market.getMarkeByName(GetPrama(0));
        if(m==null){
            Response.append(jsoncallback+"([{\"lastTime\":-2}])");
            return;
        }
        int userId=userId();
        /**
         * /**
         * 获取用户指定类型的交易管理数据
         * @param webId 网站id 网站id（暂时都设置城8）
         * @param userId 用户id 用户id
         * @param pageIndex 页码从1开始
         * @param pageSize 页码大小 10
         * @param type 类型   0 卖出  1 买入  -1不限制
         * @param timeFrom //时间   System.currentTimeMillis()
         * @param timeTo
         * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
         * @param numberTo//数量查询
         * @param priceFrom 最低价格
         * @param priceTo 最高价格
         * @param pagesize 页码大小 最大200
         * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
         * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
         */

        long lastTime = 0;
        int pageIndex = 1;
        int type = -1;
        long timeFrom = 0;
        long timeTo = 0;
        long numberFrom = 0;
        long numberTo = 0;
        long priceFrom = 0;
        long priceTo = 0;
        int pageSize = 0;
        int status = 0;
        int dateTo = 3;

        String slastTime = param("lastTime");
        String spageIndex = param("pageIndex");
        String stype = param("type");
        String stimeFrom = param("timeFrom");
        String stimeTo = param("timeTo");
        String snumberFrom = param("numberFrom");
        String snumberTo = param("numberTo");
        String spriceFrom = param("priceFrom");
        String spriceTo = param("priceTo");
        String spageSize = param("pageSize");
        String sstatus = param("status");
        String sdateTo = param("dateTo");

        if(slastTime.length() > 0 && StringUtils.isNumeric(slastTime)){
            lastTime=Long.parseLong(slastTime);
        }

        if(spageIndex.length() > 0 && StringUtils.isNumeric(spageIndex)){
            pageIndex = Integer.parseInt(spageIndex);
        }

        if(stype.length() > 0 && StringUtils.isNumeric(stype)){
            type = Integer.parseInt(stype);
        }
        if(stimeFrom.length() > 0 && StringUtils.isNumeric(stimeFrom)){
            timeFrom = Long.parseLong(stimeFrom);
        }
        if(stimeTo.length() > 0 && StringUtils.isNumeric(stimeTo)){
            timeTo = Long.parseLong(stimeTo);
        }
        if(snumberFrom.length() > 0 && StringUtils.isNumeric(snumberFrom)){
            numberFrom = Long.parseLong(snumberFrom);
        }
        if(snumberTo.length() > 0 && StringUtils.isNumeric(snumberTo)){
            numberTo = Long.parseLong(snumberTo);
        }
        if(spriceFrom.length() > 0 && StringUtils.isNumeric(spriceFrom)){
            priceFrom = Long.parseLong(spriceFrom);
        }
        if(spriceTo.length() > 0 && StringUtils.isNumeric(spriceTo)){
            priceTo = Long.parseLong(spriceTo);
        }
        if(spageSize.length() > 0 && StringUtils.isNumeric(spageSize)){
            pageSize = Integer.parseInt(spageSize);
        }
        if(sstatus.length() > 0){
            status = Integer.parseInt(sstatus);
        }
        if(sdateTo.length() > 0 && StringUtils.isNumeric(sdateTo)){
            dateTo = Integer.parseInt(sdateTo);
        }

        RecordMessage myObj = new RecordMessage();
        myObj.setUserId(userId);
        myObj.setAuth("");
        myObj.setWebId(m.webId);
        myObj.setTypes(type);
        myObj.setTimeFrom(timeFrom);
        myObj.setTimeTo(timeTo);
        myObj.setNumberFrom(numberFrom);
        myObj.setNumberTo(numberTo);
        myObj.setPriceFrom(priceFrom);
        myObj.setPriceTo(priceTo);
        myObj.setPageindex(pageIndex);
        myObj.setPageSize(pageSize);
        myObj.setStatus(status);
        myObj.setDateTo(dateTo);
        myObj.setMarket(m.market);
        try{
            RecordMessage rtn2;

            if(m.listenerOpen){
                rtn2 = server.userRecordHistory(myObj,m);
            }else{
                String param=HTTPTcp.ObjectToString(myObj);
                log.info("========================"+m.ip+":"+m.port);
                String rtn=HTTPTcp.Post(m.ip,m.port,"/server/userRecordHistory",param);
                rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
            }


            log.info(">>>>>>>>>>>>>>" + rtn2.getMessage());

            Response.append(jsoncallback+"([{"+rtn2.getMessage()+"}])");
        }catch(Exception ex2){
            log.error("内部异常", ex2);
            Response.append(jsoncallback+"([{\"lastTime\":0}])");
        }
    }

	@Page(Viewer = JSON )
	public void traderecord(){
		String jsoncallback=request.getParameter("jsoncallback");
		if(!IsLogin()){
			Response.append(jsoncallback + "([{\"lastTime\":-1}])");
			  // toLogin();
			   return;
		}
		Market m=Market.getMarkeByName(GetPrama(0));
		if(m==null){
			Response.append(jsoncallback+"([{error market}])"); 
			return;
		}
		int userId=userId();

		int pageIndex=request.getParameter("pageIndex")==null?1:Integer.parseInt(request.getParameter("pageIndex"));
		int dateTo = request.getParameter("dateTo")==null?0:Integer.parseInt(request.getParameter("dateTo"));
		if(dateTo != 0 && dateTo != 5){
			dateTo = 0;
		}
		
		String rtn = "";
		
		if((pageIndex == 0 || pageIndex == 1) && dateTo == 0){
			String key = "trade_record_key_" + m.market + userId;
			Object [][] array = (Object[][]) Cache.GetObj(key);
			if(array != null){
				StringBuilder sb = new StringBuilder();
	        	int i = 0;
	        	long count = (Long)array[array.length-1][0];
	        	for (Object[] objects : array) {
	        		if(i == count || objects[1] == null) break;
	        		sb.append(",["+objects[0]+","+objects[1]+","
	            			+objects[2]+","+objects[3]+","+objects[4]+"]");
	        		i++;
				}
	        	
	        	String res = sb.toString();
	        	if(res.length() > 0){
	        		res = res.substring(1);
	        	}
	        	rtn = "\"count\":"+array[array.length-1][0]+",\"record\":["+res+"]"; 
			}
		}
		
		if(StringUtils.isNotEmpty(rtn)){
			Response.append(jsoncallback+"([{"+rtn+"}])"); 
			return;
		}
		
		RecordMessage myObj = new RecordMessage();    
        myObj.setUserId(userId);
        myObj.setWebId(m.webId);
        myObj.setPageindex(pageIndex);
        myObj.setPageSize(dateTo==5?60:10);
        myObj.setDateTo(dateTo);
        myObj.setMarket(m.market);
        try{
        	RecordMessage rtn2;
        	if(m.listenerOpen){
        		 rtn2 = server.traderecord(myObj,m);
        	}else{
        		  String param=HTTPTcp.ObjectToString(myObj);  
                  log.info("[请求转发] 请求转发到业务处理服务器 " + m.ip + ":" + m.port);
        	      rtn=HTTPTcp.Post(m.ip,m.port,"/server/traderecord",param); 
        		  rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
        	}
        	
        	Response.append(jsoncallback+"([{"+rtn2.getMessage()+"}])"); 
		 }catch(Exception ex2){
			 log.error(ex2.toString(), ex2);
			 Response.append(jsoncallback+"([{\"lastTime\":0}])"); 
	     }
	}
	
	/**
	 * 获取用户自己的委托交易记录统计
	 */
	@Page(Viewer = ".json" )
	public void getStatisticsRecord(){
		String jsoncallback=request.getParameter("jsoncallback");
		if(!IsLogin()){
			Response.append(jsoncallback + "([{\"lastTime\":-1}])");
			  // toLogin();
			   return;
			}
	
		Market m=Market.getMarkeByName(GetPrama(0));
		if(m==null){
			Response.append(jsoncallback+"([{error market}])"); 
			return;
		}
		int userId=userId();
		/**
		 * /**
	 * 获取用户指定类型的交易管理数据
	 * @param webId 网站id 网站id（暂时都设置城8）
	 * @param userId 用户id 用户id
	 * @param pageIndex 页码从1开始
	 * @param pageSize 页码大小 10
	 * @param type 类型   0 卖出  1 买入  -1不限制
	 * @param timeFrom //时间   System.currentTimeMillis()
	 * @param timeTo
	 * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
	 * @param numberTo//数量查询
	 * @param priceFrom 最低价格
	 * @param priceTo 最高价格
	 * @param pagesize 页码大小 最大200
	 * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
	 * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
	 */
		 
		long lastTime=Long.parseLong(request.getParameter("lastTime"));
		int type=request.getParameter("type")==null?-1:Integer.parseInt(request.getParameter("type"));
		long timeFrom=StringUtils.isEmpty(request.getParameter("timeFrom"))?0:Long.parseLong(request.getParameter("timeFrom"));
		long timeTo=StringUtils.isEmpty(request.getParameter("timeTo"))?0:Long.parseLong(request.getParameter("timeTo"));
		long numberFrom=request.getParameter("numberFrom")==null?0:Long.parseLong(request.getParameter("numberFrom"));
		long numberTo=request.getParameter("numberTo")==null?0:Long.parseLong(request.getParameter("numberTo"));
		long priceFrom=request.getParameter("priceFrom")==null?0:Long.parseLong(request.getParameter("priceFrom"));
		long priceTo=request.getParameter("priceTo")==null?0:Long.parseLong(request.getParameter("priceTo"));
		int status=request.getParameter("status")==null?0:Integer.parseInt(request.getParameter("status"));
		
		if(type==-1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==3) {
            String data = RedisUtil.get(m.market+"_userrecord_" + userId);
            if (StringUtil.exist(data)) {
                Response.append(jsoncallback + "([{" + data + "}])");
                return;
            }
		}
		RecordMessage myObj = new RecordMessage();    
        myObj.setUserId(userId);
        myObj.setAuth("");
        myObj.setWebId(m.webId);
        myObj.setTypes(type);
        myObj.setTimeFrom(timeFrom);
        myObj.setTimeTo(timeTo);
        myObj.setNumberFrom(numberFrom);
        myObj.setNumberTo(numberTo);
        myObj.setPriceFrom(priceFrom);
        myObj.setPriceTo(priceTo);
        myObj.setStatus(status);
        myObj.setMarket(m.market);
        try{
        	RecordMessage rtn2;
        	if(m.listenerOpen){
        		rtn2 = server.getStatisticsRecord(myObj,m);
        	}else{
        		 String param=HTTPTcp.ObjectToString(myObj);  
                 log.info("========================"+m.ip+":"+m.port); 
                 String rtn=HTTPTcp.Post(m.ip,m.port,"/server/getStatisticsRecord",param); 
       	      	//log.info(rtn);
       		  	 rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
        	}
        	 
        	Response.append(jsoncallback+"([{"+rtn2.getMessage()+"}])"); 
        }catch(Exception ex2){
        	Response.append(jsoncallback+"([{\"lastTime\":0}])"); 
        }
		
	}
	
	/**
	 * 莱特币交易记录详情
	 */
//	@Page(Viewer = "/cn/u/details.jsp")
	public void btqdefault() {
		request.setAttribute("serijavascripparam", Market.getMarket("btq_cny"));
		request.setAttribute("entrustId", GetPrama(0));
	} 
	/**
	 * 莱特币交易记录详情
	 */
//	@Page(Viewer = "/cn/u/details.jsp")
	public void btcdefault() { 
		request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
		request.setAttribute("entrustId", GetPrama(0));
	} 
	/**
	 * 莱特币交易记录详情
	 */
//	@Page(Viewer = "/cn/u/details.jsp")
	public void ltcdefault() { 
		request.setAttribute("entrustId", GetPrama(0));
		request.setAttribute("serijavascripparam", Market.getMarket("ltc_cny"));
	}
	/**
	 * 以太币交易记录详情
	 */
//	@Page(Viewer = "/cn/u/details.jsp")
	public void ethdefault() { 
		request.setAttribute("entrustId", GetPrama(0));
		request.setAttribute("serijavascripparam", Market.getMarket("eth_cny"));
	}
	/**
	 * 以太币交易记录详情
	 */
//	@Page(Viewer = "/cn/u/details.jsp")
	public void ethbtcdefault() { 
		request.setAttribute("entrustId", GetPrama(0));
		request.setAttribute("serijavascripparam", Market.getMarket("eth_btc"));
	}
	/**
	 * 以太币交易记录详情
	 */
//	@Page(Viewer = "/cn/u/details.jsp")
	public void daoethdefault() { 
		request.setAttribute("entrustId", GetPrama(0));
		request.setAttribute("serijavascripparam", Market.getMarket("dao_eth"));
	}
	/**
	 * 以太币交易记录详情
	 */
//	@Page(Viewer = "/cn/u/details.jsp")
	public void daodefault() { 
		request.setAttribute("entrustId", GetPrama(0));
		request.setAttribute("serijavascripparam", Market.getMarket("dao_cny"));
	}


	/**
	 * 获取详情数据
	 */
	@Page(Viewer = ".json" )
	public void GetDetails(){
		if(!IsLogin()){
			   toLogin();
			   return;
		}
		String jsoncallback=request.getParameter("jsoncallback");
//		int pageIndex = intParam("page");
//		if(pageIndex == 0)
//			pageIndex = 1;
//		int pageSize = 5;
		Market m=Market.getMarkeByName(GetPrama(0)); 
		if(m==null){
			Response.append(jsoncallback+"([{error market}])"); 
			return;
		}
		try{
			int userid=userId(); 
			long entityId=Long.parseLong(GetPrama(1));
			RecordMessage myObj = new RecordMessage();   
	        myObj.setUserId(userid);
	        myObj.setWebId(m.webId);
	        myObj.setTypes(101);//详情
	        myObj.setMessage(entityId+"");//借用来保存id的
	        myObj.setMarket(m.market);
//	        myObj.setPageSize(pageSize);
//			myObj.setPageindex(pageIndex);
	        RecordMessage rtn2 = server.userrecord(myObj,m);
	        //com.alibaba.fastjson.JSONObject dataJson = com.alibaba.fastjson.JSONObject.parseObject(rtn2.toString());
			//int count = dataJson.getInteger("count");
	        Response.append(jsoncallback+"([{"+rtn2.getMessage()+"}])");
			//Response.append("[{"+rtn2.getMessage()+"}]");
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
	}



	public static String jsonReplace(String memJson,String key,int size) {
		if(!StringUtil.exist(memJson)){
			return memJson;
		}
		String str = memJson;
		JSONObject object = JSONObject.parseObject("{" + memJson + "}");
		JSONArray obi1 = (JSONArray) object.get(key);
		JSONArray retObj = new JSONArray();
		if (obi1 != null && obi1.size() > size) {
			for (int i = 0; i < size; i++) {
				retObj.add(obi1.get(i));
			}
			object.put(key, retObj);
			str = object.toString().replaceAll("\\{", "");
			str = str.replaceAll("\\}", "");
		}
		return str;
	}

    /**
     * 获取用户历史委托（已成交和已取消）
     */
    @Page(Viewer = ".json")
    public void getEntrustHistory() {
        if (CommonUtil.needInterceptAfterLogin(this, false)) {
            return;
        }

        String jsoncallback = request.getParameter("jsoncallback");
        if (!IsLogin()) {
            Response.append(jsoncallback + "([{\"lastTime\":-1}])");
            return;
        }

        Market m = Market.getMarkeByName(param("market").toLowerCase());
        if (m == null) {
            Response.append(jsoncallback + "([{error market}])");
            return;
        }
        int userId = userId();

        String type = getByParam("type");
        if (StringUtils.isBlank(type)) {
            type = "-1";
        }

        int includeCancel = intParam("includeCancel");
        int timeType = intParam("timeType");
        int pageNum = intParam("pageNum");
        if (pageNum == 0) {
            pageNum = 1;
        }
        int pageSize = intParam("pageSize");
        if (pageSize == 0) {
            pageSize = 30;
        }

        RecordMessage myObj = new RecordMessage();
        myObj.setUserId(userId);
        myObj.setTypes(Integer.parseInt(type));
        myObj.setPageindex(pageNum);
        myObj.setPageSize(pageSize);
        myObj.setMarket(m.market);
        //用status字段承载includeCancel含义
        myObj.setStatus(includeCancel);
        //用message字段承载timeType
        myObj.setMessage(String.valueOf(timeType));
        try {
            RecordMessage rtn2;

            if (m.listenerOpen) {
                rtn2 = server.getUserEntrustHistory(myObj, m);
            } else {
                String param = HTTPTcp.ObjectToString(myObj);
                String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getUserEntrustHistory", param);
                rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
            }

            JSONObject result = new JSONObject();
            result.put("pageNum", pageNum);

            String dataJsonStr = "{" + rtn2.getMessage() + "}";
            log.info("dataJsonStr:" + dataJsonStr);
            JSONObject dataJson = JSONObject.parseObject(dataJsonStr);
            int count = dataJson.getInteger("count");
            result.put("count", count);

            JSONArray entrusts = new JSONArray();
            JSONArray records = JSONArray.parseArray(dataJson.getString("record"));
            for (int i = 0; i < records.size() && i < pageSize; i++) {
                JSONArray record = records.getJSONArray(i);
                JSONObject jo = new JSONObject();
                jo.put("entrustId", Integer.parseInt(record.get(0).toString()));
                jo.put("date", record.get(6));
                jo.put("type", record.get(5));
                jo.put("price", record.get(1));
                jo.put("amount", record.get(2));
                jo.put("completeNumber", record.get(3));
                jo.put("completeTotalMoney", record.get(4));
                jo.put("status", record.get(7));
				jo.put("market", m.getMarket());
                entrusts.add(jo);
            }
            result.put("records", entrusts);

            Response.append(jsoncallback + "([" + result.toJSONString() + "])");
        } catch (Exception ex2) {
            log.error("内部异常", ex2);
            Response.append(jsoncallback + "([{\"lastTime\":0}])");
        }
    }


	/**
	 * 获取用户历史委托（已成交和已取消）仅包含24成交量，不统计总数
	 */
	@Page(Viewer = ".json")
	public void getEntrustHistoryFor24() {
		if (CommonUtil.needInterceptAfterLogin(this, false)) {
			return;
		}

		String jsoncallback = request.getParameter("jsoncallback");
		if (!IsLogin()) {
			Response.append(jsoncallback + "([{\"lastTime\":-1}])");
			return;
		}

		Market m = Market.getMarkeByName(param("market").toLowerCase());
		if (m == null) {
			Response.append(jsoncallback + "([{error market}])");
			return;
		}
		int userId = userId();

		String type = getByParam("type");
		if (StringUtils.isBlank(type)) {
			type = "-1";
		}

		int includeCancel = intParam("includeCancel");
//		int timeType = intParam("timeType");
		int pageNum = intParam("pageNum");
		if (pageNum == 0) {
			pageNum = 1;
		}
		int pageSize = intParam("pageSize");
		if (pageSize == 0) {
			pageSize = 30;
		}

		RecordMessage myObj = new RecordMessage();
		myObj.setUserId(userId);
		myObj.setTypes(Integer.parseInt(type));
		myObj.setPageindex(pageNum);
		myObj.setPageSize(pageSize);
		myObj.setMarket(m.market);
		//用status字段承载includeCancel含义
		myObj.setStatus(includeCancel);
		//用message字段承载timeType
//		myObj.setMessage(String.valueOf("0"));
		try {
			RecordMessage rtn2;
			rtn2 = server.getUserEntrustHistoryFor24(myObj, m);
//			if (m.listenerOpen) {
//				rtn2 = server.getUserEntrustHistoryFor24(myObj, m);
//			} else {
//				String param = HTTPTcp.ObjectToString(myObj);
//				String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getUserEntrustHistoryFor24", param);
//				rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
//			}

			JSONObject result = new JSONObject();
			result.put("pageNum", pageNum);
			String dataJsonStr = "{" + rtn2.getMessage() + "}";
			log.info("dataJsonStr:" + dataJsonStr);
			JSONObject dataJson = JSONObject.parseObject(dataJsonStr);

			JSONArray entrusts = new JSONArray();
			JSONArray records = JSONArray.parseArray(dataJson.getString("record"));
			for (int i = 0; i < records.size() && i < pageSize; i++) {
				JSONArray record = records.getJSONArray(i);
				JSONObject jo = new JSONObject();
				jo.put("entrustId", Integer.parseInt(record.get(0).toString()));
				jo.put("date", record.get(6));
				jo.put("type", record.get(5));
				jo.put("price", record.get(1));
				jo.put("amount", record.get(2));
				jo.put("completeNumber", record.get(3));
				jo.put("completeTotalMoney", record.get(4));
				jo.put("status", record.get(7));
				jo.put("market", m.getMarket());
				entrusts.add(jo);
			}
			result.put("records", entrusts);

			Response.append(jsoncallback + "([" + result.toJSONString() + "])");
		} catch (Exception ex2) {
			log.error("内部异常", ex2);
			Response.append(jsoncallback + "([{\"lastTime\":0}])");
		}
	}

    /**
     * 获取用户历史成交记录
     */
    @Page(Viewer = ".json")
    public void getTransRecordHistory() {
        if (CommonUtil.needInterceptAfterLogin(this, false)) {
            return;
        }

        String jsoncallback = request.getParameter("jsoncallback");
        if (!IsLogin()) {
            Response.append(jsoncallback + "([{\"lastTime\":-1}])");
            return;
        }

        Market m = Market.getMarkeByName(param("market").toLowerCase());
        if (m == null) {
            Response.append(jsoncallback + "([{error market}])");
            return;
        }
        int userId = userId();

        String type = getByParam("type");
        if (StringUtils.isBlank(type)) {
            type = "-1";
        }
        int timeType = intParam("timeType");
        int pageNum = intParam("pageNum");
        if (pageNum == 0) {
            pageNum = 1;
        }
        int pageSize = intParam("pageSize");
        if (pageSize == 0) {
            pageSize = 30;
        }

        RecordMessage myObj = new RecordMessage();
        myObj.setUserId(userId);
        myObj.setTypes(Integer.parseInt(type));
        myObj.setPageindex(pageNum);
        myObj.setPageSize(pageSize);
        myObj.setMarket(m.market);
        //用message字段承载timeType
        myObj.setMessage(String.valueOf(timeType));
        try {
            RecordMessage rtn2;
			if (m.listenerOpen) {
				rtn2 = server.getUserTransRecordHistory(myObj, m);
			} else {
				String param = HTTPTcp.ObjectToString(myObj);
				String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getUserTransRecordHistory", param);
				rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
			}

            JSONObject result = new JSONObject();
            result.put("pageNum", pageNum);

            String dataJsonStr = "{" + rtn2.getMessage() + "}";
            log.info("dataJsonStr:" + dataJsonStr);
            JSONObject dataJson = JSONObject.parseObject(dataJsonStr);
            int count = dataJson.getInteger("count");
            result.put("count", count);

            JSONArray transRecords = new JSONArray();
            JSONArray records = JSONArray.parseArray(dataJson.getString("record"));

            for (int i = 0; i < records.size() && i < pageSize; i++) {
                JSONArray record = records.getJSONArray(i);
                JSONObject jo = new JSONObject();
                jo.put("date", record.get(5));
                jo.put("price", record.get(1));

                if ("-1".equals(type)) {
                    int userIdBuy = record.getInteger(6);
                    int userIdSell = record.getInteger(7);
                    //如果买卖是一个人，拆成两条记录
                    if (userIdBuy == userIdSell) {
                        JSONObject buy = new JSONObject();
                        buy.put("date", record.get(5));
                        buy.put("price", record.get(1));
                        buy.put("type", 1);
                        buy.put("outAmount", record.get(2));
                        buy.put("intAmount", record.get(3));
                        buy.put("feesBuy",record.get(8));
						buy.put("market", m.getMarket());
                        transRecords.add(buy);

                        JSONObject sell = new JSONObject();
                        sell.put("date", record.get(5));
                        sell.put("price", record.get(1));
                        sell.put("type", 0);
                        sell.put("outAmount", record.get(2));
                        sell.put("intAmount", record.get(3));
						sell.put("feesSell",record.get(9));
						sell.put("market", m.getMarket());
                        transRecords.add(sell);
                        continue;
                    }
                    if (userIdBuy == userId) {
                        jo.put("type", 1);
                        jo.put("outAmount", record.get(2));
                        jo.put("intAmount", record.get(3));
						jo.put("feesBuy",record.get(8));
						jo.put("market", m.getMarket());
                    } else {
                        jo.put("type", 0);
                        jo.put("outAmount", record.get(2));
                        jo.put("intAmount", record.get(3));
						jo.put("feesSell",record.get(9));
						jo.put("market", m.getMarket());
                    }
                } else if ("0".equals(type)) {
                    jo.put("type", 0);
                    jo.put("outAmount", record.get(2));
                    jo.put("intAmount", record.get(3));
					jo.put("feesSell",record.get(9));
					jo.put("market", m.getMarket());
                } else {
                    jo.put("type", 1);
                    jo.put("outAmount", record.get(2));
                    jo.put("intAmount", record.get(3));
					jo.put("feesBuy",record.get(8));
					jo.put("market", m.getMarket());
                }
                transRecords.add(jo);
            }
            result.put("records", transRecords);

            Response.append(jsoncallback + "([" + result.toJSONString() + "])");
        } catch (Exception ex2) {
            log.error("内部异常", ex2);
            Response.append(jsoncallback + "([{\"lastTime\":0}])");
        }
    }


	/**
	 * 获取用户当前委托--计划单,限价单
	 */
	@Page(Viewer = ".json")
	public void getTransRecordNow() {
		try{
			int userId = userId();
			int types = intParam("types");//买入1  卖出 0  不限 -1
			int tab = intParam("tab");//1 计划单   0 限价单
			Market m = Market.getMarkeByName(param("market").toLowerCase());
			int pageIndex = intParam("pageIndex");
			if (pageIndex == 0) {
				pageIndex = 1;
			}
			int pageSize = intParam("pageSize");
			if (pageSize == 0) {
				pageSize = 30;
			}
			if (tab ==1){
				Query<PlanEntrustBean> query = planEntrustDao.getQuery();
				query.setDatabase(m.db);
				query.setSql("select * from  plan_entrust");
				query.setCls(PlanEntrustBean.class);
				query.append(" userId = " + userId);
				query.append(" and status = -1 ");
				if (types >= 0){
					if(types == 1)
						query.append(" and types=1");
					else if(types == 0)
						query.append(" and types=0");

				}
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				int total = query.count();
				if(total > 0){
					query.append("order by submitTime desc");
                    List<PlanEntrustBean> plan = planEntrustDao.findPageEntrust(pageIndex, pageSize,m.db);
                    for(PlanEntrustBean planEntrustBean : plan){
                        Map<String,Object> downloadMap = new HashMap<String,Object>();
                        downloadMap.put("id", planEntrustBean.getEntrustId());

                        BigDecimal unitPrice = planEntrustBean.getUnitPrice();
                        if(unitPrice.compareTo(BigDecimal.ZERO) == 0){
                            unitPrice = planEntrustBean.getUnitPriceProfit();
                        }
                        downloadMap.put("unitPrice", unitPrice);

                        if(planEntrustBean.getTriggerPrice().compareTo(BigDecimal.ZERO) == 0){
                            downloadMap.put("triggerPrice", planEntrustBean.getTriggerPriceProfit());
                        }else{
                            downloadMap.put("triggerPrice", planEntrustBean.getTriggerPrice());
                        }

                        downloadMap.put("types", planEntrustBean.getTypes());

                        BigDecimal number = planEntrustBean.getNumbers();
                        if(number.compareTo(BigDecimal.ZERO) == 0){
                            number = planEntrustBean.getTotalMoney().divide(unitPrice, m.numberBixDian, BigDecimal.ROUND_DOWN);
                        }
                        downloadMap.put("numbers", number);

                        downloadMap.put("completeNumber", planEntrustBean.getCompleteNumber());
                        downloadMap.put("completeTotalMoney", planEntrustBean.getCompleteTotalMoney());
                        downloadMap.put("submitTime", planEntrustBean.getSubmitTime());
                        list.add(downloadMap);
                    }
				}

				Map<String, Object> page = new HashMap<String, Object>();
				page.put("pageIndex", pageIndex);
				page.put("totalCount", total);
				page.put("list", list);
				json("", true, net.sf.json.JSONObject.fromObject(page).toString());
			}else if (tab == 0){
				Query<EntrustBean> query = entrustDao.getQuery();
				query.setDatabase(m.db);
				query.setSql("select * from  entrust");
				query.setCls(EntrustBean.class);
				query.append(" userId= " + userId);
				query.append(" and status = 3 ");
				if (types >= 0){
					if(types == 1)
						query.append(" and types=1");
					else if(types == 0)
						query.append(" and types=0");

				}
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				int total = query.count();
				if(total > 0){
					query.append("order by submitTime desc");
					//分页查询
					List<EntrustBean> entrustBeans = entrustDao.findPageEntrust(pageIndex, pageSize,m.db);
					for(EntrustBean entrustBean : entrustBeans){
						Map<String,Object> downloadMap = new HashMap<String,Object>();
						downloadMap.put("id", entrustBean.getEntrustId());
						downloadMap.put("unitPrice", entrustBean.getUnitPrice());
						downloadMap.put("types", entrustBean.getTypes());
						downloadMap.put("numbers", entrustBean.getNumbers());
						downloadMap.put("completeNumber", entrustBean.getCompleteNumber());
						downloadMap.put("completeTotalMoney", entrustBean.getCompleteTotalMoney());
						downloadMap.put("submitTime", entrustBean.getSubmitTime());
						list.add(downloadMap);
					}
				}

				Map<String, Object> page = new HashMap<String, Object>();
				page.put("pageIndex", pageIndex);
				page.put("totalCount", total);
				page.put("list", list);
				json("", true, net.sf.json.JSONObject.fromObject(page).toString());
			}
		}catch (Exception e) {
			log.error("内部异常", e);
		}
	}


	public static void main(String[] args) {
        String s = "{\"count\":8,\"record\":[['9002',1.5214,0.115,0.115,0.17457,1,1511515720619,2,0.00200000,0.0,8],['9001',1.5214,5.379,5.379,8.0091704,1,1511515373536,2,0.00200000,0.0,8],['8801',1.518,1.853,1.853,2.7261336,1,1511511433901,2,0.00200000,0.0,8],['8203',1.3716,1.337,1.337,1.8218868,1,1511489284357,2,0.00200000,0.0,8],['8201',1.3716,0.124,0.124,0.168516,1,1511489068045,2,0.00200000,0.0,8],['8002',1.415,1.236,1.236,1.679724,1,1511488245852,2,0.00200000,0.0,8],['7801',1.513,0.266,0.266,0.361494,1,1511487100768,2,0.00200000,0.0,8],['7401',1.358,0.29,0.29,0.39382,1,1511398654569,2,0.00200000,0.0,8]]}";
        JSONObject dataJson = JSONObject.parseObject(s);
		log.info(dataJson.toJSONString());
	}
}
