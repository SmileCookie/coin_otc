package com.world.controller;


import com.Lan;
import com.match.entrust.MemEntrustMatchProcessor;
import com.match.entrust.MemEntrustSwitchService;
import com.redis.RedisUtil;
import com.tenstar.Info;
import com.tenstar.InfoEntrust;
import com.tenstar.Message;
import com.tenstar.MessageCancle;
import com.tenstar.RecordMessage;
import com.tenstar.SystemStatus;
import com.tenstar.UserConfig;
import com.tenstar.robotConfig;
import com.tenstar.timer.chart.ChartData;
import com.tenstar.timer.entrust.DataArray;
import com.tenstar.timer.entrust.Interface;
import com.tenstar.timer.wisdom.ChartArray;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.model.entitys.record.TransRecord;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IndexServer  {

	private String lan = "en";
	public static Logger log = Logger.getLogger(IndexServer.class);
	private DataArray dataArray = new DataArray();
	//public static Market market=Market.InitMarket(GlobalConfig.getValue("market")+"market");
	public IndexServer(){
		//log.info(GlobalConfig.getValue("market"));
	}
	public IndexServer(String lan){
		this.lan = lan;
	}
 
	/**
	 * 进行委托
	 * @param msg 参数对象
	 */
    public Message Entrust(Message msg, Market m) {
        try {
            if (msg.getTypes() == 1 || msg.getTypes() == 0) {
                //进行委托
                InfoEntrust in = Interface.doEntrust(msg.getTypes(), msg.getUserId(), Market.ffNumber(msg.getNumbers(), m), Market.ffMoney(msg.getUnitPrice(), m), msg.getWebId(), msg.getWebId(), msg.getStatus(), msg.getMt(), msg.getMessage(), m);
                if (in.rs != null && in.rs.getMsg() != null) {
                    JSONObject json = JSONObject.fromObject(in.rs.getMsg());
                    in.in.setMessage(json.getString("des"));
                    msg.setCode(in.rs.getCode());
                }

                String smsg = in.in.getMessage();
                if (in.in.equals(Info.DoEntrustFaildPromError)) {
                    smsg = Lan.Language(lan, "系统繁忙，请重新委托！");
                }

                msg.setMessage(smsg);
                msg.setStatus(in.in.getNum());
                msg.setNumbers(BigDecimal.valueOf(in.entrustId));
                if (in.in.equals(Info.DoEntrustSuccess)) {
                    //触发交割功能
                    SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.exchangeNewWork, true);
                }
            } else {
                msg.setMessage(Info.DoEntrustFaildUnKnowType.getMessage());
                msg.setStatus(Info.DoEntrustFaildUnKnowType.getNum());
                msg.setNumbers(BigDecimal.ZERO);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            Message msgerr = new Message();
            msgerr.setMessage(Lan.Language(lan, "系统繁忙，请重新委托！"));
            msgerr.setStatus(Info.DoEntrustFaildPromError.getNum());
            msgerr.setNumbers(BigDecimal.ZERO);
            return msgerr;
        }
        return msg;
    }

    /**
	 *
	 * 取消单个委托
	 * @param msg
	 */
	public MessageCancle cancle(MessageCancle msg,Market m){
		try{
		      Info in=Interface.doCancle(msg.getUserId(), msg.getEntrustId(),m);
		      if(in==Info.DoCancleSuccess){
		    	//SystemStatus.exchangeNewWork=true; 
		    	SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
		      }
		      msg.setMessage(in.getMessage());
	    	  msg.setStatus(in.getNum());
	
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			 MessageCancle msgerr=new  MessageCancle();
			 msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
			 msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
		 	return msgerr;
		}
		return msg;
	}

    /**
     * 批量取消
     *
     * @param msg
     */
    public Message cancelByIdsForBrush(Message msg, Market m) {
        try {
            List<Long> ids = new ArrayList<>();
            String strIds = msg.getMessage();
            for (String strId : strIds.split(",")) {
                ids.add(Long.parseLong(strId));
            }
            int count = Interface.doCancleByIdsForBrush(msg.getUserId(), ids, m);
            if (count > 0) {
                // SystemStatus.exchangeNewWork=true;
                SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.exchangeNewWork, true);
            }
            msg.setStatus(count);

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            Message msgerr = new Message();
            msgerr.setMessage(Info.DoCancleFailPriceError.getMessage() + ":" + ex.toString());
            msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
            return msgerr;
        }
        return msg;
    }

	/**
	 * 批量取消
	 * @param msg
	 */
	public MessageCancle cancelmore(MessageCancle msg,Market m){
		try{
			    int count=Interface.doCancle(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getType(),m);
			    if(count>0)
			      // SystemStatus.exchangeNewWork=true; 
			    	SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
			    msg.setStatus(count);
			   
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  MessageCancle msgerr=new MessageCancle();
			  msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
			  msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
			  return msgerr;
		}
		return msg;
	}

    /**
     * 批量取消，刷量用
     * @param msg
     */
    public MessageCancle cancelmoreForBrush(MessageCancle msg,Market m){
        try{
            int count=Interface.doCancleForBrush(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getType(),m);
            if(count>0){
                SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
            }
            msg.setStatus(count);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            MessageCancle msgerr=new MessageCancle();
            msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
            return msgerr;
        }
        return msg;
    }

    /**
	 * 取消市场全部订单
	 * @param m
	 */
	public MessageCancle cancelAll(Market m){
		MessageCancle msg=new MessageCancle();
		try{
			int count=Interface.doCancleAll(m);
			if(count>0){
				SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
			}
			msg.setStatus(count);
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
			msg.setStatus(Info.DoCancleFailPriceError.getNum());
			return msg;
		}
		return msg;
	}
	/**
	 * 批量委托下单
	 * @param msg 传参对象
	 * @return 返回处理结果对象
	 * @author zhanglinbo 20160914
	 */
	public MessageCancle entrustmore(MessageCancle msg,Market m){
		try{
			String rtn=Interface.doEntrustMore(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getNumbers(),msg.getType(),m);
			if(rtn!=null)
				//SystemStatus.exchangeNewWork=true; 
				SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
			    // log.info(rtn); 
			msg.setMessage(rtn); 
			msg.setStatus(Info.DoEntrustSuccess.getNum());
		}catch(Exception ex){ 
			  log.error(ex.toString(), ex);
			  MessageCancle msgerr=new MessageCancle();
			  msgerr.setMessage("0:0:0");
			  msgerr.setStatus(Info.DoEntrustFaildPromError.getNum());
			  return msgerr;
		}
		return msg;
	}
	
	
	/**
	 * 交易记录查询
	 * @param msg 传参数对象
	 * @return 返回结果参数对象
	 * @author zhanglinbo 20160914
	 */
	public RecordMessage traderecord(RecordMessage msg,Market m){
		try{
		    String rtn = "";
		    if((msg.getPageindex() == 1 || msg.getPageindex() == 0) && msg.getDateTo()==0){
		    	rtn = MemEntrustMatchProcessor.da.getTraderecordFromMem(msg.getUserId(),m);
		    }else{
		    	rtn = MemEntrustMatchProcessor.da.getTraderecord(msg.getUserId(), msg.getPageindex(), msg.getPageSize(), msg.getDateTo(),m);
		    }
	        msg.setMessage(rtn);
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
			  msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
			  return msgerr;
		}
		return msg;
	}
	
	/**
	 * 获取用户记录
	 * @param msg
	 * @return
	 */
	public RecordMessage userrecord(RecordMessage msg,Market m){
		try{
		    if(msg.getTypes()==101){//详情
		    	String rtn=getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()),m);
		        msg.setMessage(rtn);
		    }else{//列表
		        String rtn= dataArray.getTop(msg.getWebId(),
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
		    		msg.getDateTo(),
		    		m
		    		);
		         msg.setMessage(rtn);
		    }
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
			  msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
		 	  return msgerr;
		}
		return msg;
	}

	/**
	 * 用户主动生成用户数据
	 * @param userId 用户id 用户id
	 * @return
	 */
	public  String getDetails(int userId,long entrustId,Market m){
/*//		QueryDataType qdt = QueryDataType.DEFAULT;
//		if(type == 2){
//			qdt = QueryDataType.DOWN;
//		}
		//and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
		StringBuilder where=new StringBuilder();
	    String w=where.toString();
	    //String sql = DownTableManager.getProxySql("select transRecordId,unitPrice,totalPrice,numbers,TYPES,times from transrecord where  ((userIdBuy="+userId+" and entrustIdBuy="+entrustId+") or (userIdSell="+userId+" AND entrustIdSell="+entrustId+")) and unitPrice>0", QueryDataType.DEFAULT);
	    //ETC盘查询费率
        String feeRateFiled = "";
			feeRateFiled=" ,(select feeRate from entrust where entrustId = "+entrustId+") feeRate ";
		String sql="SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times "+feeRateFiled+" FROM transrecord WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0 ";
		List lists = Data.Query(m.db,sql, new Object[]{userId,entrustId,userId,entrustId});//提现记录
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
        	List beb = (List) b;

        	sb.append(",["+beb.get(0)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
        			Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+beb.get(4)+","+beb.get(5));
        	sb.append(","+beb.get(6)+"]");

        }

        String rtn=sb.toString();

        if(rtn.length()==0)
        	rtn= "\"record\":[]";
        else
        	rtn= "\"record\":["+rtn.substring(1)+"]";

        return rtn;*/

		// FIXME: 2017/8/17 suxinjie 查询迁移后的数据
		String sql = "SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times FROM transrecord WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0 union all " +
				"SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times FROM transrecord_all WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0";

		List lists = Data.Query(m.db, sql, new Object[]{userId, entrustId, userId, entrustId, userId, entrustId, userId, entrustId});
		StringBuilder sb=new StringBuilder();
		for(Object b : lists){
			List beb = (List) b;
			sb.append(",["+beb.get(0) + ","
					+ Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m) + ","
					+ Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m) + ","
					+ Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m) + ","
					+ beb.get(4) + ","
					+ beb.get(5) +",'"
					+m.getMarket()+"']");
		}

		String rtn=sb.toString();
		if(rtn.length()==0)
			rtn= "\"record\":[]";
		else
			rtn= "\"record\":["+rtn.substring(1)+"]";
		return rtn;
	}

	/**
	 * 获取用户委托记录明显
	 * @param msg
	 * @return
	 */
	public List<TransRecord> userRecordDetails(RecordMessage msg, Market m){
		try{
			List<TransRecord> list = new DataArray().getTransRecordDetails(msg.getUserId(), Long.parseLong(msg.getMessage()),m);
			return list;
		}catch(Exception ex){
			log.error(ex.toString(), ex);;
		}
		return null;
	}

    /**
     * 获取用户记录，不走缓存，直接插库 union all 表，目前给APP
     * @param msg
     * @return
     */
    public RecordMessage userrecordNoCache(RecordMessage msg,Market m){
        try{
            if(msg.getTypes()==101){//详情
                String rtn=MemEntrustMatchProcessor.da.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()),m);
                msg.setMessage(rtn);
            }else{//列表
                String rtn=MemEntrustMatchProcessor.da.getTopNoCache(msg.getWebId(),
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
                        msg.getDateTo(),
                        m
                );
                msg.setMessage(rtn);
            }
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msgerr=new RecordMessage();
            msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
            return msgerr;
        }
        return msg;
    }

    /**
     * 获取用户历史记录 entrust_all 表
     * @param msg
     * @return
     */
    public RecordMessage userRecordHistory(RecordMessage msg,Market m){
        try{
            String rtn=MemEntrustMatchProcessor.da.getUserRecordHistory(msg.getWebId(),
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
                    msg.getDateTo(),
                    m
            );
            msg.setMessage(rtn);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msgerr=new RecordMessage();
            msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
            return msgerr;
        }
        return msg;
    }

	/**
	 * 获取用户计划委托数据
	 */
	public RecordMessage userplanrecord(RecordMessage msg,Market m){
		try{
		  //  String post=request.getParameter("messageBody");
		   //log.info(post);
		   // RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post); 
		   // log.info(msg.getTypes());
		    //列表
		        String rtn= dataArray.getTopPlan(msg.getWebId(),
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
		    		msg.getDateTo(),
		    		m
		    		);
		         msg.setMessage(rtn);
		    
			 	
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
	    	  msg.setStatus(Info.DoCancleFailPriceError.getNum());
	    	  return msgerr;
		}
		return msg;
	}
	
	
	/**
	 * 获取用户成交记录
	 * @param msg 传参对象
	 * @return 返回结果对象
	 * @author zhanglinbo 20160914
	 */
	public RecordMessage  getTradeRecord(RecordMessage msg,Market m){
		try{
		    String rtn = MemEntrustMatchProcessor.da.getUserTransRecord(msg.getUserId(), Long.parseLong(msg.getMessage()), msg.getPageSize() ,m);
		    msg.setMessage(rtn);
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
	    	  msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
		 	 return msgerr;
		}
		return msg;
	}
	
	/**
	 * 获取用户交易记录
	 */
	public RecordMessage  getOrderTradeRecord(RecordMessage msg,Market m){
		try{
		    String rtn = MemEntrustMatchProcessor.da.getOrderTransRecord(msg.getUserId(), Long.parseLong(msg.getMessage()),m);
		    msg.setMessage(rtn);
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
			  msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
		 	return msgerr;
		}
		return msg;
	}
	
	/**
	 * 获取用户委托详情或列表
	 * @param msg
	 * @return
	 */
	public RecordMessage getOrders(RecordMessage msg,Market m){
		try{
		    //log.info("getOrders:types:" + msg.getTypes() + ",userId:" + msg.getUserId() + ",status:" + msg.getStatus() + ",msg:" + msg.getMessage() + ",ip:" + ip());
		    if(msg.getTypes()==101 && StringUtils.isNotEmpty(msg.getMessage())){//详情
		    	String rtn=MemEntrustMatchProcessor.da.getOrderByEntrustId(msg.getUserId(), Long.parseLong(msg.getMessage()),m);
		        msg.setMessage(rtn); 
		    }else{//列表
		        String rtn=MemEntrustMatchProcessor.da.getTop(msg.getWebId(),
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
			 	
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
			  msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
		 	return msgerr;
		}
		return msg;
	}
	
	/**
	 * 获取统计数据
	 */
	public RecordMessage getStatisticsRecord(RecordMessage msg,Market m){
		try{
	        String rtn=MemEntrustMatchProcessor.da.getStatisticsRecord(msg.getWebId(),
	    		msg.getUserId(),
	    		msg.getTypes(),
	    		msg.getTimeFrom(),
	    		msg.getTimeTo(),
	    		msg.getNumberFrom(),
	    		msg.getNumberTo(), 
	    		msg.getPriceFrom(),
	    		msg.getPriceTo(),
	    		msg.getStatus(),
	    		m
	    		);
		    msg.setMessage(rtn);
			
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
		 	return msgerr;
		}
		return msg;
	}
	
	/**
	 * 获取最新交易信息
	 * 参数pagesize代表档位  5 10 20 50
	 */
	public RecordMessage getTrade(RecordMessage msg,Market m){
		try{
		    log.info("获取trade"+msg.getPageSize());
		    	//String rtn=MemEntrustMatchProcessor.da.SetTrade(msg.getPageSize(),m);//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
		    String rtn ="";
		    msg.setMessage(rtn);
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
			  msgerr.setStatus(Info.GetMessageError.getNum());
			  return msgerr;
		}
		return msg;
	}
	
	
	/**
	 * 获取最新ticker信息
	 * 
	 */
	public RecordMessage getticker(RecordMessage msg,Market m){ 
		try{
			String rtn=ChartData.GetTiker(m);//.da.GetDepth50();//.SetTrade(msg.getPageSize());//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
		    if(rtn==null)
		    	rtn=" ";
		    	msg.setMessage(rtn);
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msgerr.setStatus(Info.GetMessageError.getNum());
		 	 return msgerr;
		}
		return msg;
	}

	/**
	 * 获取盘口档位深度
	 *
	 */
	public RecordMessage getdepth(RecordMessage msg,Market m){
		try{
		    String rtn = "[]";
		    if(msg.getPageSize() <= 50){
		    	rtn=MemEntrustMatchProcessor.da.GetDepth50(m);//.GetDepth200();//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
		    }else{
		    	if(msg.getPageSize() == 100 || msg.getPageSize() == 200 || msg.getPageSize() == 300 || msg.getPageSize() == 400 || msg.getPageSize() == 500){
		    		//rtn=MemEntrustMatchProcessor.da.GetDepthN(msg.getPageSize(),m);//.GetDepth200();//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
		    	}
		    }
		    //log.info(rtn.length());
		    msg.setMessage(rtn);
			
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msgerr.setStatus(Info.GetMessageError.getNum());
		 	return msgerr;
		}
		return msg;
	}  
	/**
	 * 获取最新交易信息
	 * 参数pagesize代表档位  5 10 20 50
	 */
	public RecordMessage  getTradeOuter(RecordMessage msg,Market m){
		try{
		    	String rtn=MemEntrustMatchProcessor.da.GetTradeListOuter(msg.getPageindex(),m);//MemEntrustMatchProcessor.da.SetTrade(msg.getPageSize());//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
		        msg.setMessage(rtn);
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msgerr.setStatus(Info.GetMessageError.getNum());
		 	 return msgerr;
		}
		return msg;
	}
	
	/**
	 * 同步数据
	 * 参数pagesize代表档位  5 10 20 50 
	 */
	public RecordMessage syndata(RecordMessage msg){ 
		try{
		/*	SystemStatus.exchangeRuning=false;
		    SystemStatus.chartDataRuning=false;
		  //  MemEntrustMatchProcessor.da.ReInit();//.SetTrade(msg.getPageSize());//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
		    SystemStatus.exchangeRuning=true;
		    SystemStatus.chartDataRuning=true;*/
		    msg.setMessage("true");
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msgerr.setStatus(Info.GetMessageError.getNum());
		 	return msgerr;
		}
		return msg;
	}
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public Message data(Message msg,Market m){ 
		try{
			com.tenstar.timer.chart.ChartArray ca = ChartData.caMap.get(m.market);
			String rtn=ca.Get(msg.getTypes(),m);//"
			msg.setMessage("ok");
		}catch(Exception ex){
			Message msgerr=new Message();
			  msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msgerr.setStatus(Info.GetMessageError.getNum());
	    	  return msgerr;
		}
		return msg;
	}
	
	/**
	 * 
	 */
	public Message wisdom(Message msg,Market m){ 
		try{
			ChartArray ca = new ChartArray(m.market,360,1000,3600);
			String rtn = ca.Get(msg.getTypes(),m);//"
			msg.setMessage("ok");
		}catch(Exception ex){
			Message msgerr=new Message();
			msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	msgerr.setStatus(Info.GetMessageError.getNum());
		  return msgerr;
		}
		return msg;
	}
	
	/**
	 * 获取当前价格
	 * 没有参数
	 */
	public RecordMessage getCurrentPrice(RecordMessage msg,Market m){
		try{
		      String rtn=MemEntrustMatchProcessor.da.getCurrentPrice(m);//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
		      msg.setMessage(rtn);
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  RecordMessage msgerr=new RecordMessage();
			  msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
	    	  msgerr.setStatus(Info.GetMessageError.getNum());
		 	  return msgerr;
		}
		return msg;
	}
	

	/**
	 * 设置系统自动项
	 */
	public String setSystemAuto(robotConfig msg,Market m){
		String result ="";
		try{
			Interface.setSystemAuto(msg,m);
			result= "ok";
		}catch(Exception ex){ 
			result =  ex.toString();
		}
		return result;
	}
	
	/**
	 * 设置系统自动项
	 * @return 返回处理结果
	 */
	public String setSystemEntrustAuto(com.tenstar.RobotEntrustConfig msg,Market m){
		String result ="";
		try{
			     //String post=request.getParameter("messageBody");
			     //com.tenstar.RobotEntrustConfig msg= (com.tenstar.RobotEntrustConfig)HTTPTcp.StringToObject(post);
			    // log.info("保存");
			     Interface.setSystemEnstrustAuto(msg,m);
			     result = "ok";
		}catch(Exception ex){ 
			result = ex.toString();
		}
		return result;
	}
	
	/**
	 * 设置系统自动项
	 */
	
	public String saveAutoShualiangParams(com.tenstar.RobotShualiangConfig msg,Market m){
		String result ="";
		try{
			     //String post=request.getParameter("messageBody");
			    // com.tenstar.RobotShualiangConfig msg= (com.tenstar.RobotShualiangConfig)HTTPTcp.StringToObject(post);
			    // log.info("保存");
			     Interface.saveAutoShualiangParams(msg,m);
			     result = "ok";
		}catch(Exception ex){ 
			result = ex.toString();
		}
		return result;
	}
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String setUserAuto(UserConfig msg,Market m){
		String result ="";
		try{
		    // String post=request.getParameter("messageBody");
		    // UserConfig msg=(UserConfig)HTTPTcp.StringToObject(post);
		    // log.info("保存");
		     Interface.setUserAuto(msg,m);
		     result = "ok";
		}catch(Exception ex){ 
	 		result = ex.toString();
		}
		return result;
	}
	
	
	/**
	 * 虚拟进行委托
	 */
	public String vitureEntrust(Message msg,Market m){
		String result ="";
		try{  
		    //String post=request.getParameter("messageBody");
		    //Message msg=(Message)HTTPTcp.StringToObject(post);
		  //  MemEntrustMatchProcessor.da.updateEntrustSystem2((int)msg.getUnitPrice(),msg.getNumbers());
		  //  MemEntrustMatchProcessor.da.SetTrade(m);//整体更新一次数据
		    result = "success"; 	
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			result = "fail";
		}
		return result;
	}
	
	/**
	 * 取消虚拟委托
	 * @param msg
	 * @return
	 */
	public String  cancelVitureEntrust(MessageCancle msg,Market m){
		String result ="";
		try{
			 //String post=request.getParameter("messageBody");
			 //MessageCancle msg=(MessageCancle)HTTPTcp.StringToObject(post);
			/* long priceLow = msg.getPriceLow();
			 long priceHigh = msg.getPriceHigh();
			 for (int i = (int) priceLow; i<=priceHigh; i++) {
				 MemEntrustMatchProcessor.da.data[i][1]=0;
			 }
			 MemEntrustMatchProcessor.da.SetTrade(m);//整体更新一次数据
			 result = "success"; 	
			 */
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			result = "fail";
		}
		return result;
	}
	
	
	public String getVitureEntrust(Market m){
		 String result = "";
		/*  for (int i = 1000; i<1000000; i++) {
			 if(MemEntrustMatchProcessor.da.data[i][1] != 0){
				 result += "[" + Market.formatMoney(i,m) + "," + Math.abs(Market.formatNumber(MemEntrustMatchProcessor.da.data[i][1],m)) + "],";
			 }
		 }
		 if(StringUtils.isNotEmpty(result))
			 result = result.substring(0, result.length() - 1);
		 result = "[" + result + "]";
		 */
	 	return result;
	}
	
	
	/**
	 * 获取市场深度
	 */
	public RecordMessage  getDeepth(RecordMessage msg,Market m) {
		/*try {
			String rtn = MemEntrustMatchProcessor.da.getDeepth(msg.getPageindex(), msg.getDateTo(), msg.getPageSize(),m);
			msg.setMessage(rtn);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			RecordMessage msgerr = new RecordMessage();
			msgerr.setMessage(Info.GetMessageError.getMessage() + ":" + ex.toString());
			msgerr.setStatus(Info.GetMessageError.getNum());
			return msgerr;
		}*/
		return msg;
	}
	
	
	/**
	 * 进行计划委托
	 */
	public Message planEntrust(Message msg,Market m){
		try{  
		    if(msg.getTypes()==1||msg.getTypes()==0){
		    	//进行委托
		    	//log.info("here2");
		    	InfoEntrust in=Interface.doPlanEntrust(msg.getTypes(), msg.getUserId(),msg.getNumbers(), msg.getUnitPrice(),msg.getWebId(), 
		    			msg.getWebId(),msg.getStatus(), msg.getMt(), msg.getMessage(),msg.getTriggerPrice(),msg.getUnitPriceProfit(),msg.getTriggerPriceProfit(),msg.getTotalMoney(),m);
			  
			    if(in.rs != null && in.rs.getMsg() != null){
			    	JSONObject json = JSONObject.fromObject(in.rs.getMsg());
			    	in.in.setMessage(json.getString("des"));
			    	msg.setCode(in.rs.getCode());
			    }
		       //log.info("here3"+"处理结果："+in.in.getMessage());
			    
			   String smsg = in.in.getMessage();
			   if(in.in.equals(Info.DoEntrustFaildPromError)){
				   smsg = Lan.Language(lan,"系统繁忙，请重新委托！");
			   }
			    
		       msg.setMessage(smsg);
	    	   msg.setStatus(in.in.getNum()); 
	    	   msg.setNumbers(BigDecimal.valueOf(in.entrustId));
		       if(in.equals(Info.DoEntrustSuccess)){//如果没成功就返回状态信息
		        	//触发交割功能
			    	//SystemStatus.exchangeNewWork=true; 
			    	SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
		       }
		       
		    } else{
		    	   msg.setMessage(Info.DoEntrustFaildUnKnowType.getMessage());
		    	   msg.setStatus(Info.DoEntrustFaildUnKnowType.getNum());
		    	   msg.setNumbers(BigDecimal.ZERO);
		    }
		}catch(Exception ex){
			   log.error(ex.toString(), ex);
			   Message msgerr=new Message();
			   msgerr.setMessage(Lan.Language(lan,"系统繁忙，请重新委托！"));
	    	   msgerr.setStatus(Info.DoEntrustFaildPromError.getNum());
	    	   msgerr.setNumbers(BigDecimal.ZERO);
	    	  return msgerr;
		}
		return msg;
	}
	
	/**
	 * 取消单项计划委托
	 */
	public MessageCancle canclePlanEntrust(MessageCancle msg,Market m){
		try{
		    Info in=Interface.doCanclePlanEntrust(msg.getUserId(), msg.getEntrustId(),m);
		      if(in==Info.DoCancleSuccess){
		    	//SystemStatus.exchangeNewWork=true; 
		    	SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
		     }
		      msg.setMessage(in.getMessage());
	    	  msg.setStatus(in.getNum());
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			 MessageCancle msgerr=new  MessageCancle();
			 msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
	    	 msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
		 	 return msgerr;
		}
		return msg;
	}
	
	/**
	 * 批量取消计划委托 
	 */
	public MessageCancle  cancelmorePlanEntrust(MessageCancle msg,Market m){
		try{
			int count = 0;
			/*if(msg.getMessage()!=null && msg.getMessage().equals("ALL")){
				count+=	Interface.doCancle(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getType(),m);
			}*/
			count+=Interface.doCancleMorePlanEntrust(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getType(),m);
			if(count>0)
				//SystemStatus.exchangeNewWork=true; 
			SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
			msg.setStatus(count);
		}catch(Exception ex){
			  log.error(ex.toString(), ex);
			  MessageCancle msgerr=new MessageCancle();
			  msgerr.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
	    	  msgerr.setStatus(Info.DoCancleFailPriceError.getNum());
		 	return msgerr;
		}
		return msg;
	}

    /**
     * 获取用户历史委托（已成交和已取消）
     * @param msg
     * @return
     */
    public RecordMessage getUserEntrustHistory(RecordMessage msg,Market m){
        try{
            String table = "";
            if ("0".equals(msg.getMessage())) {
                table = "entrust";
            }else{
                table = "entrust_all";
            }
            String rtn = MemEntrustMatchProcessor.da.getUserEntrustHistory(msg.getUserId(),
                    msg.getTypes(),
                    msg.getStatus(),
                    msg.getPageindex(),
                    msg.getPageSize(),
                    m,
                    table
            );
            msg.setMessage(rtn);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msgerr=new RecordMessage();
            msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msgerr.setStatus(Info.GetMessageError.getNum());
            return msgerr;
        }
        return msg;
    }


	/**
	 * 获取用户历史委托（已成交和已取消）仅包含24成交量，不统计总数
	 * @param msg
	 * @return
	 */
	public RecordMessage getUserEntrustHistoryFor24(RecordMessage msg,Market m){
		try{
			String table = "entrust";
			String rtn = getUserEntrustHistoryFor24(msg.getUserId(),
					msg.getTypes(),
					msg.getStatus(),
					msg.getPageindex(),
					msg.getPageSize(),
					m,
					table
			);
			msg.setMessage(rtn);
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			RecordMessage msgerr=new RecordMessage();
			msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
			msgerr.setStatus(Info.GetMessageError.getNum());
			return msgerr;
		}
		return msg;
	}
	/**
	 * 获取用户历史委托（已成交和已取消）仅包含24成交量，不统计总数
	 */
	public String getUserEntrustHistoryFor24(int userId, int type, int includeCancel, int pageNum, int pageSize, Market m, String table) {
		//首次进入页面，走缓存
		if (pageNum == 1 && includeCancel == 0 && type == -1 &&pageSize <= 30) {
			String cacheData = RedisUtil.get(m.market + "_userrecord_status2_" + userId);
			if (cacheData != null && cacheData.length() > 0) {
				return cacheData;
			}
		}

		StringBuilder where = new StringBuilder();
		where.append(" userId=" + userId);
		if (type > -1) {
			where.append(" and types=" + type);
		}
		if (includeCancel == 0) {
			where.append(" and status=2");
		} else {
			where.append(" and status between 1 and 2");
		}
		String w = where.toString();

//		String sql = getOneSql(w, table);
		String sql="select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feeRate,0 as triggerPrice,webId from "+table+" where  "+w+"  and unitPrice>0 ";
		sql += " order by submitTime desc limit ?,?";
		List lists = Data.Query(m.db, sql, new Object[]{(pageNum - 1) * pageSize, pageSize});
		StringBuilder sb = new StringBuilder();
		for (Object b : lists) {
			List beb = (List) b;
			sb.append(",['" + beb.get(0) + "'," + Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())), m) + "," +
					Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())), m) + "," + Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())), m) + "," +
					Market.formatMoneyAndNumber((BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString()))), m) + "," + beb.get(5) + "," + beb.get(6) + "," + beb.get(7) + "," + beb.get(8) + "," + Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())), m) + "," + beb.get(10) + ",'"+m.getMarket()+"']");

		}
		String rtn = sb.toString();
		if (rtn.length() > 0) {
			rtn = "[" + rtn.substring(1) + "]";
		} else {
			rtn = "[]";
		}

		if (rtn.length() == 0) {
			return "\"record\":[]";
		} else {
			return "\"record\":" + rtn;
		}
	}
    /**
     * 获取用户成交记录
     * @param msg
     * @return
     */
    public RecordMessage getUserTransRecordHistory(RecordMessage msg,Market m){
        try{
            String table = "";
            if ("0".equals(msg.getMessage())) {
                table = "transrecord";
            }else{
                table = "transrecord_all";
            }
            String rtn = MemEntrustMatchProcessor.da.getUserTransRecordHistory(msg.getUserId(),
                    msg.getTypes(),
                    msg.getPageindex(),
                    msg.getPageSize(),
                    m,
                    table
            );
            msg.setMessage(rtn);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msgerr=new RecordMessage();
            msgerr.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msgerr.setStatus(Info.GetMessageError.getNum());
            return msgerr;
        }
        return msg;
    }

    public boolean switchMatchOpen(Message msg) {
        try {
            if (msg.getTypes() == 0) {
                MemEntrustSwitchService.closeMatchSwitch(msg.getMarket());
            } else {
                MemEntrustSwitchService.openMatchSwitch(msg.getMarket());
            }
            return MemEntrustSwitchService.getMatchSwitch(msg.getMarket());
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            return false;
        }
    }
}