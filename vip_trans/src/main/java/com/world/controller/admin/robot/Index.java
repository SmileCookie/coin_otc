package com.world.controller.admin.robot;

import java.math.BigDecimal;

import com.tenstar.HTTPTcp;
import com.tenstar.RobotEntrustConfig;
import com.tenstar.robotConfig;
import com.world.cache.Cache;
import com.world.controller.IndexServer;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/" , des = "ROBOT管理")
public class Index extends UserAction  {
	
	private IndexServer server = new IndexServer();
	
	@Page(Viewer = "") 
	public void index(){//因为编码问题，没时间解决，先这样
	}
	
	
	/**系统配置
	 */
	@Page(Viewer = ".xml" ) 
	public void saveautoSys(){ 
//		if(!IsLogin()){
//			   toLogin();
//			   return;
//		}
		   
		Market m=Market.getMarkeByName(GetPrama(0));
		if(m==null){
			Write("",false,"错误的市场");
			return;
		} 
		try{
//			int userid=userId();
		int webid=m.webId; 
		
		robotConfig rc=new robotConfig();
		
		
	 
		rc.setIsStart(Integer.parseInt(request.getParameter("isStart")));//总数量
		rc.setIsStartTrade(Integer.parseInt(request.getParameter("isStartTrade"))); 
		rc.setEntrustQuShi(Integer.parseInt(request.getParameter("entrustQuShi")));//总数量
		
		rc.setSafePriceQuJian(Integer.parseInt(request.getParameter("safePriceQuJian")));//总数量
		
		rc.setEntrustRobotQuJian(Integer.parseInt(request.getParameter("entrustRobotQuJian")));//总数量
		rc.setBaseShowAdd(Long.parseLong(request.getParameter("baseShowAdd")));//总数量
		
		 //以下是买档 外围配置
		rc.setNumberTotalBuyOut(Integer.parseInt(request.getParameter("numberTotalBuyOut")));//总数量
		
		rc.setMaxDangweiBuyOut(Integer.parseInt(request.getParameter("maxDangweiBuyOut")));//总数量
		rc.setLowPriceBuyOut(Integer.parseInt(request.getParameter("lowPriceBuyOut")));//总数量
		rc.setTimesSpaceBuyOut(Integer.parseInt(request.getParameter("timesSpaceBuyOut")));//总数量
		rc.setSplitNumberBuyOut(Integer.parseInt(request.getParameter("splitNumberBuyOut")));//总数量
		rc.setDuiQiBaiFenBiBuyOut(Integer.parseInt(request.getParameter("duiQiBaiFenBiBuyOut")));//总数量
		
	
		
		//以下是卖档 外围配置
		rc.setNumberTotalSellOut(Integer.parseInt(request.getParameter("numberTotalSellOut")));//总数量
		rc.setMaxDangweiSellOut(Integer.parseInt(request.getParameter("maxDangweiSellOut")));//总数量
		rc.setHighPriceSellOut(Integer.parseInt(request.getParameter("highPriceSellOut")));//总数量
		rc.setTimesSpaceSellOut(Integer.parseInt(request.getParameter("timesSpaceSellOut")));//总数量
		rc.setSplitNumberSellOut(Integer.parseInt(request.getParameter("splitNumberSellOut")));//总数量
		rc.setDuiQiBaiFenBiSellOut(Integer.parseInt(request.getParameter("duiQiBaiFenBiSellOut")));//总数量
		
	
		
		//以下是买档 内围配置
		rc.setNumberTotalBuyIn(Integer.parseInt(request.getParameter("numberTotalBuyIn")));//总数量
		rc.setMaxDangweiBuyIn(Integer.parseInt(request.getParameter("maxDangweiBuyIn")));//总数量
		rc.setTimesSpaceBuyIn(Integer.parseInt(request.getParameter("timesSpaceBuyIn")));//总数量
		rc.setSplitNumberBuyIn(Integer.parseInt(request.getParameter("splitNumberBuyIn")));//总数量
		rc.setDuiQiBaiFenBiBuyIn(Integer.parseInt(request.getParameter("duiQiBaiFenBiBuyIn")));//总数量
		
		//以下是卖档 内围配置
		rc.setNumberTotalSellIn(Integer.parseInt(request.getParameter("numberTotalSellIn")));//总数量
		rc.setMaxDangweiSellIn(Integer.parseInt(request.getParameter("maxDangweiSellIn")));//总数量
		rc.setTimesSpaceSellIn(Integer.parseInt(request.getParameter("timesSpaceSellIn")));//总数量
		rc.setSplitNumberSellIn(Integer.parseInt(request.getParameter("splitNumberSellIn")));//总数量
		rc.setDuiQiBaiFenBiSellIn(Integer.parseInt(request.getParameter("duiQiBaiFenBiSellIn")));//总数量
	
		 

	   //每日的总成交量
		rc.setTotalTransEveryDay(Long.parseLong(request.getParameter("totalTransEveryDay")));//总数量
	  
		rc.setTimeSpaceTM(Integer.parseInt(request.getParameter("timeSpaceTM")));//总数量

	    log.info("信息");
    try{
	
    	String rtn=	server.setSystemAuto(rc,m);
	    if(rtn.equals("ok"))
	            Write(rtn,true,rtn+"");
	    else
	  	        Write(rtn,false,rtn+"");
		 
		 }catch(Exception ex2){
	        	Write("保存失败！",false,"");
	        }
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
	}
	
	
	/**系统配置
	 */
	@Page(Viewer = ".xml" ) 
	public void saveEntrustAutoSys() {
//		if(!IsLogin()){
//			   toLogin();
//			   return;
//		}
		   
		Market m=Market.getMarkeByName(GetPrama(0));
		if(m==null){
			Write("",false,"错误的市场");
			return;
		} 
		try{
//			int userid=userId();
		int webid=m.webId; 
		
		RobotEntrustConfig rc=new RobotEntrustConfig();
	 
		rc.setWebId(webid);
		rc.setDangwei(Integer.parseInt(request.getParameter("dangwei")));
		rc.setIsStart(Integer.parseInt(request.getParameter("isStart")));//总数量
		rc.setQujianMaxNum(Integer.parseInt(request.getParameter("qujianMaxNum"))); 
		rc.setOverNum(Integer.parseInt(request.getParameter("overNum")));//总数量
		rc.setQujianDifference(Integer.parseInt(request.getParameter("qujianDifference")));//总数量
		
		rc.setQujianCancel(Integer.parseInt(request.getParameter("qujianCancel")));//总数量
		rc.setUserId(Integer.parseInt(request.getParameter("userId")));//总数量
    try{
    	
    	String rtn= server.setSystemEntrustAuto(rc,m);
	    if(rtn.equals("ok"))
	            Write(rtn,true,rtn+"");
	    else
	  	        Write(rtn,false,rtn+"");
		 
		 }catch(Exception ex2){
	        	Write("保存失败！",false,"");
	        }
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
	}
	

	/**系统配置
	 */
	@Page(Viewer = "/admins/robot/autoUser.jsp" ) 
	public void autoUserBtc(){
		request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
		
		int userId=userId();
		
		Object obj=Cache.Get("userAutoConfigBTC"+userId);
		
		com.tenstar.UserConfig	 rc;
		if(obj!=null)
			rc=(com.tenstar.UserConfig)HTTPTcp.StringToObject(obj.toString());
		else
			rc=new com.tenstar.UserConfig();
		request.setAttribute("rc", rc);
 
	}
	
	/**系统配置
	 */
	@Page(Viewer = "/admins/robot/autoUserLtc.jsp" ) 
	public void autoUserLtc(){ 
        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
		int userId=userId();
		Object obj=Cache.Get("userAutoConfigLTC"+userId);
		com.tenstar.UserConfig	 rc;
		if(obj!=null)
			rc=(com.tenstar.UserConfig)HTTPTcp.StringToObject(obj.toString());
		else
			rc=new com.tenstar.UserConfig();
		request.setAttribute("rc", rc);
	}
	/**系统配置
	 */
	@Page(Viewer = ".xml" ) 
	public void saveAutoUser(){ 
//		if(!IsLogin()){
//			   toLogin();
//			   return;
//		}
		   
		Market m=Market.getMarkeByName(GetPrama(0));
		if(m==null){
			Write("",false,"错误的市场");
			return;
		} 
		try{
			int userid=userId();
		int webid=m.webId; 
		int uierid=userId();
		com.tenstar.UserConfig rc=new com.tenstar.UserConfig();
		
		
	 
		rc.setIsStart(Integer.parseInt(request.getParameter("isStart")));//总数量

		rc.setEntrustQuShi(Integer.parseInt(request.getParameter("entrustQuShi")));//总数量
		
		rc.setSafePriceQuJian(Integer.parseInt(request.getParameter("safePriceQuJian")));//总数量
		
		rc.setUserId(userid);
		rc.setWebId(8);
		


		rc.setEntrustQuJian(Integer.parseInt(request.getParameter("entrustQuJian")));//总数量
		

		rc.setExtrustMaxBuy(BigDecimal.valueOf(Double.parseDouble(request.getParameter("extrustMaxBuy"))));//总数量
		rc.setEntrustMaxDangWei(BigDecimal.valueOf(Double.parseDouble(request.getParameter("entrustMaxDangWei"))));//总数量
			
			    
			    //针对已经买入的订单部分，上涨获利设置可以卖出，如果反向下跌一定额度需要卖出止损
		rc.setMaxUpToSell(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxUpToSell"))));//总数量
		rc.setMinUpToSell(BigDecimal.valueOf(Double.parseDouble(request.getParameter("minUpToSell"))));//总数量
		rc.setMaxDownToSell(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxDownToSell"))));//总数量
		rc.setMaxUpPriceSpaceToCancle(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxUpPriceSpaceToCancle"))));//总数量
			 
		        
		        //针对主动做空卖出的部分   下跌趋势的时候会主动做空卖出，卖出后的订单遵循如下规则进行买入补仓，如果上涨一定额度需要止损即使补仓买入
		rc.setMaxDownToBuy(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxDownToBuy"))));//总数量
		rc.setMinDownToBuy(BigDecimal.valueOf(Double.parseDouble(request.getParameter("minDownToBuy"))));//总数量
		rc.setMaxUpToBuy(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxUpToBuy"))));//总数量
		rc.setMaxDownPriceSpaceToCancle(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxDownPriceSpaceToCancle"))));//总数量
	
		rc.setMaxTimeToTransNoMoney(Integer.parseInt(request.getParameter("maxTimeToTransNoMoney")));//总数量
		rc.setTimeSpace(Integer.parseInt(request.getParameter("timeSpace")));//总数量
		rc.setAutoName("userAutoConfig"+uierid); 
		
	    log.info("信息");
    try{
    	
    	String rtn= server.setUserAuto(rc,m);
	    if(rtn.equals("ok"))
	            Write(rtn,true,rtn+"");
	    else
	  	        Write(rtn,false,rtn+"");
		 
		 }catch(Exception ex2){
	        	Write("保存失败！",false,"");
	        }
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
	}
	
	
	
	
}