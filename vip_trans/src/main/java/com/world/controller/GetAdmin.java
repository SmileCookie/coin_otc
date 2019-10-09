package com.world.controller;

import com.match.entrust.MemEntrustMatchProcessor;
import com.tenstar.AdminMoney;
import com.tenstar.AdminWeiTuo;
import com.tenstar.HTTPTcp;
import com.tenstar.Info;
import com.tenstar.RecordMessage;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.UserAction;

public class GetAdmin extends UserAction{
	 
	
	
	@Page(Viewer = ".json" , Cache = 60) 
	public void index(){
		Response.append("[ok]");
	}

	

	/**
	 * 获取最新交易信息
	 * 参数pagesize代表档位  5 10 20 50
	 */
	@Page
	public void getWeiTuo(){
		try{
		    String post=request.getParameter("messageBody");
		    
		    AdminWeiTuo msg=(AdminWeiTuo)HTTPTcp.StringToObject(post); 
		    Market m = Market.getMarkeByName(msg.getMarket());
		    log.info("获取管理委托"+msg.getPageSize());
		    	String rtn=com.tenstar.timer.admin.Index.getWeiTuoList(msg.getEntrustId(), msg.getWebId(), msg.getUserId(), msg.getPageIndex(), msg.getType(), msg.getTimeFrom(),
		    			msg.getTimeTo(),
		    			msg.getNumberFrom(), msg.getNumberTo(), msg.getPriceFrom(),
		    			msg.getPriceTo(), msg.getPageSize(), msg.getStatus(),m);
		        msg.setMessage(rtn);
			String r=HTTPTcp.ObjectToString(msg);
			if(r==null)
				r=" ";
			response.getWriter().write(r); 	
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  AdminWeiTuo msg=new AdminWeiTuo();
			  msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msg.setStatus(Info.GetMessageError.getNum());
		 	 String rtn=HTTPTcp.ObjectToString(msg);
		 	try{
		 	 response.getWriter().write( rtn);
		 	}catch(Exception ex2){
		 		log.error(ex2.toString(), ex2);
		 	}
		}
	}
	
	/**
	 * 获取委托成交详情
	 */
	@Page
	public void transrecord(){
		try{
		    String post=request.getParameter("messageBody");
		    //log.info(post);
		    
		    RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post); 
		    Market m = Market.getMarkeByName(msg.getMarket());
		    log.info(msg.getTypes());
		    if(msg.getTypes()==101){//详情
		    	String rtn=com.tenstar.timer.admin.Index.getDetails(Long.parseLong(msg.getMessage()),m);
		        msg.setMessage(rtn); 
		    }else{//列表
		        String rtn= MemEntrustMatchProcessor.da.getTop(msg.getWebId(),
		    		msg.getUserId(),
		    		msg.getPageindex(),
		    		msg.getTypes(),
		    		msg.getTimeFrom(),
		    		msg.getTimeTo(),
		    		msg.getNumberFrom(),
		    		msg.getNumberTo(), 
		    		msg.getPriceFrom(),
		    		msg.getPriceTo(),
		    		msg.getPageSize(),
		    		msg.getStatus(),
		    		m
		    		);
		         msg.setMessage(rtn);
		    }
			String r=HTTPTcp.ObjectToString(msg);
			if(r==null)
				r=" ";
			response.getWriter().write( r); 	
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msg=new RecordMessage();
			  msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
	    	  msg.setStatus(Info.DoCancleFailPriceError.getNum());
		 	 String rtn=HTTPTcp.ObjectToString(msg);
		 	try{
		 	 response.getWriter().write( rtn);
		 	}catch(Exception ex2){
		 		log.error(ex2.toString(), ex2);
		 	}
		}
	}
	

	/**
	 * 获取最新交易信息
	 * 参数pagesize代表档位  5 10 20 50
	 */
	@Page
	public void getDetails(){ 
		try{
		    String post=request.getParameter("messageBody");
		    Market m = Market.getMarkeByName("btc_cny");
		    AdminWeiTuo msg=(AdminWeiTuo)HTTPTcp.StringToObject(post); 
		    log.info("获取管理委托"+msg.getPageSize());
		    	String rtn=com.tenstar.timer.admin.Index.getDetails(msg.getEntrustId(),m);
			String r=HTTPTcp.ObjectToString(msg);
			if(r==null)
				r=" ";
			response.getWriter().write(r); 	
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  AdminWeiTuo msg=new AdminWeiTuo();
			  msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msg.setStatus(Info.GetMessageError.getNum());
		 	 String rtn=HTTPTcp.ObjectToString(msg);
		 	try{
		 	 response.getWriter().write( rtn);
		 	}catch(Exception ex2){
		 		log.error(ex2.toString(), ex2);
		 	}
		}
	}
	/**
	 * 获取最新交易信息
	 * 参数pagesize代表档位  5 10 20 50
	 */
	@Page
	public void getMoney(){
		try{
		    String post=request.getParameter("messageBody");
		    Market m = Market.getMarkeByName("etc_btc");
		    AdminMoney msg=(AdminMoney)HTTPTcp.StringToObject(post); 
		    log.info("获取管理委托"+msg.getPageSize());
		    String rtn=com.tenstar.timer.admin.Index.getMoney(msg.getEntrustId(), msg.getTransRecordId(), 8,
msg.getUserId(), msg.getPageIndex(), msg.getType(), msg.getTimeFrom(), msg.getTimeTo(), msg.getNumberFrom(), msg.getNumberTo(),
msg.getPriceFrom(), msg.getPriceTo(), msg.getTotalFrom(), msg.getTotalTo(), msg.getPageSize(), msg.getStatus(),m);
		        msg.setMessage(rtn);
			String r=HTTPTcp.ObjectToString(msg);
			if(r==null)
				r=" ";
			response.getWriter().write(r); 	
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  AdminMoney msg=new AdminMoney();
			  msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msg.setStatus(Info.GetMessageError.getNum());
		 	 String rtn=HTTPTcp.ObjectToString(msg);
		 	try{
		 	 response.getWriter().write( rtn);
		 	}catch(Exception ex2){
		 		log.error(ex2.toString(), ex2);
		 	}
		}
	}
	
	
	/**
	 * 重新处理资金
	 * 用numbers代替 transrecordId
	 * 参数pagesize代表档位  5 10 20 50
	 */
	@Page
	public void reDueMoney(){
		try{
		    String post=request.getParameter("messageBody");
		    Market m =Market.getMarket(request.getParameter("market"));
		    RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post); 
		  
		    	String rtn=""+com.tenstar.timer.admin.Index.reDueMoney(Long.parseLong(msg.getMessage()),m);
		        msg.setMessage(rtn);
			String r=HTTPTcp.ObjectToString(msg);
			if(r==null)
				r=" ";
			response.getWriter().write(r); 	
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msg=new RecordMessage();
			  msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msg.setStatus(Info.GetMessageError.getNum());
		 	 String rtn=HTTPTcp.ObjectToString(msg);
		 	try{
		 	 response.getWriter().write( rtn);
		 	}catch(Exception ex2){
		 		log.error(ex2.toString(), ex2);
		 	}
		}
	}
	
}
