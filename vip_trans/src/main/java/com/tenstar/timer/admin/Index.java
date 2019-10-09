package com.tenstar.timer.admin;

import java.math.BigDecimal;
import java.util.List;

import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.util.DigitalUtil;
import org.apache.log4j.Logger;

public class Index {
	private final static Logger log = Logger.getLogger(Index.class);
	
	/**
	 * 获取委托记录,这里跟前台不一样的地方是unitprice=0代表取消的这条命令会显示出来，方便后台管理调试查看
	 * @param entrustId 委托id，指定的委托id，如果为0 不限制
	 * @param webId 网站id 网站id（暂时都设置城8）
	 * @param userId 用户id 用户id   0代表不限制
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
	 * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交） -1计划委托
	 * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
	 */
	public static String getWeiTuoList(long entrustId,int webId,int userId,int pageIndex,int type,long timeFrom,long timeTo,long numberFrom,long numberTo,long priceFrom,long priceTo,long pageSize,int status,Market m){

		if(pageIndex==0){
			pageIndex = 1;
		}
		
		if(pageSize==0) 
			 pageSize=10;
		if(pageSize>200)
			pageSize=200;
		
		//and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
		StringBuilder where=new StringBuilder();
		
		if(entrustId>0)
		{
			where.append(" and entrustId="+entrustId);//仅仅显示这个状态的
		}
		if(userId>0){
			where.append(" and userId="+userId);//仅仅显示这个状态的
		}
		
		if(status!=0){ 
	    	where.append(" and status="+status);//仅仅显示这个状态的
		}
		//status==0 是所有的意思
		if(type>-1){
			where.append(" and types="+type);//类型 
		}
		
	    if(timeFrom>0)
	    	where.append(" and submitTime>="+timeFrom);//时间从
	    if(timeTo>0)
	    	where.append(" and submitTime<="+timeTo);//时间到
	    if(numberFrom>0)
	    	where.append(" and numbers>="+numberFrom);//时间从
	    if(numberTo>0)
	    	where.append(" and numbers<="+numberTo);//时间到
	 
	    if(priceFrom>0)
	    	where.append(" and unitPrice>="+priceFrom);//时间到
	    if(priceTo>0)
	    	where.append(" and unitPrice<="+priceTo);//时间到
	    
	    String w=where.toString(); 
	    if(w.length()>0)
	    	w=" where "+w.substring(4);
	    
	  //  if(w.length()>0)
	  //  	w=w.substring(4); 
		String sql="select SQL_CALC_FOUND_ROWS entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,userId from entrust    "+w+" order by submitTime desc limit ?,?";
		  String countSql="select found_rows() as num";
		List lists = Data.Query(m.db,sql, new Object[]{ (pageIndex-1)*pageSize , pageSize});//提现记录
		List count = (List)Data.GetOne(m.db,countSql, new Object[]{});//提现记录数量
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
        	List beb = (List) b;
        	sb.append(",['"+beb.get(0)+"',"+Market.formatMoney(DigitalUtil.getBigDecimal(beb.get(1)),m)+","+
        			Market.formatNumber(DigitalUtil.getBigDecimal(beb.get(2)),m)+","+Market.formatNumber(DigitalUtil.getBigDecimal(beb.get(3)),m)+","+
        			Market.formatMoneyAndNumber(DigitalUtil.getBigDecimal(beb.get(4)),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+"]");
        } 
        String rtn=sb.toString();
        if(rtn.length()==0) 
        	return "\"count\":\"0\",\"record\":[]"; 
        else
        	return "\"count\":"+count.get(0).toString()+",\"record\":["+rtn.substring(1)+"]"; 
	}
	
	
	/**
	 * 管理用的获取一笔委托的交易详情
	 *   unitPrice=0代表取消命令，在外部是不现实给用户的，但是总管理这里是可以显示出来的，因为取消命令也有可能会失败，所以需要调试
	 * @param webId 网站id 网站id
	 * @param userId 用户id 用户id
	 * @return
	 */
	public static String getDetails(long entrustId,Market m){
		//and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
		StringBuilder where=new StringBuilder();
	    String w=where.toString(); 
		String sql="SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times FROM transrecord WHERE entrustIdBuy=? or entrustIdSell=? ";
		List lists = Data.Query(m.db,sql, new Object[]{entrustId,entrustId});//提现记录
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
        	List beb = (List) b;
        
        	sb.append(",["+beb.get(0)+","+Market.formatMoney(DigitalUtil.getBigDecimal(beb.get(1)),m)+","+
        			Market.formatMoneyAndNumber(DigitalUtil.getBigDecimal(beb.get(2)),m)+","+Market.formatNumber(DigitalUtil.getBigDecimal(beb.get(3)),m)+","+beb.get(4)+","+beb.get(5)+"]");
        }
      
        String rtn=sb.toString();
     
        if(rtn.length()==0)
        	rtn= "\"record\":[]"; 
        else
        	rtn= "\"record\":["+rtn.substring(1)+"]"; 
       
        return rtn;
	} 
	
	/**
	 * 获取资金处理的transrecord
	 * @param webId 网站id 网站id（暂时都设置城8）
	 * @param userId 用户id 用户id   0代表不限制
	 * @param pageIndex 页码从1开始
	 * @param pageSize 页码大小 10
	 * @param type 类型   0 卖出  1 买入  -1 取消类型   -2以下是不限制（这个很重要）
	 * @param timeFrom //时间   System.currentTimeMillis()
	 * @param timeTo
	 * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
	 * @param numberTo//数量查询
	 * @param priceFrom 最低价格
	 * @param priceTo 最高价格
	 * @param pagesize 页码大小 最大200
	 * @param status 资金处理状态 0初始状态 1 处理失败 2处理成功   只有等于1的时候，可以放心更新为0，让他重新处理
	 * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合transRecordId,unitPrice,totalPrice,numbers,entrustIdBuy,userIdBuy,entrustIdSell,userIdSell,TYPES,times,status,isCount
	 */
	public static String getMoney(long entrustId,long transRecordId,int webId,int userId,int pageIndex,int type,long timeFrom,long timeTo,long numberFrom,long numberTo,long priceFrom,long priceTo,long totalFrom, long totalTo, long pageSize,int status,Market m){
		
		if(pageSize==0) 
			 pageSize=10;
		if(pageSize>200)
			pageSize=200;
		
		//and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
		StringBuilder where=new StringBuilder();
		 

		if(entrustId>0){
			where.append(" and (entrustIdSell="+entrustId+" or entrustIdBuy="+entrustId+") ");//仅仅显示这个状态的
		}
		
		if(transRecordId>0){
			where.append(" and transRecordId="+transRecordId);//仅仅显示这个状态的
		}
		
		
		if(userId>0){
			where.append(" and (userIdBuy="+userId+" or userIdSell="+userId+") ");//仅仅显示这个状态的
		}
		
		if(status>0){ 
	    	where.append(" and status="+status);//仅仅显示这个状态的
		}
		//status==0 是所有的意思
		if(type>-2){
			where.append(" and types="+type);//类型 
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

	  //  if(w.length()>0)
	  //  	w=w.substring(4); 
		String sql="select SQL_CALC_FOUND_ROWS  transRecordId,unitPrice,totalPrice,numbers,entrustIdBuy,userIdBuy,entrustIdSell,userIdSell,TYPES,times,status,isCount FROM transrecord    "+w+"  order by times desc limit ?,?";
		  String countSql="select found_rows() as num";
		List lists = Data.Query(m.db,sql, new Object[]{(pageIndex-1)*pageSize , pageSize});//提现记录
		List count = (List)Data.GetOne(m.db,countSql, new Object[]{});//提现记录数量
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
        	List beb = (List) b;
        	sb.append(",["+beb.get(0)+","+Market.formatMoney(DigitalUtil.getBigDecimal(beb.get(1)),m)+","+
        			Market.formatMoneyAndNumber(DigitalUtil.getBigDecimal(beb.get(2).toString()),m)+","+Market.formatNumber(DigitalUtil.getBigDecimal(beb.get(3)),m)+","+
                	Long.parseLong(beb.get(4).toString())+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+","+beb.get(9)+","+beb.get(10)+","+beb.get(11)+"]");
        }
        
        String rtn=sb.toString();
        if(rtn.length()==0) 
        	return "\"count\":0,\"record\":[]"; 
        else
        	return "\"count\":"+count.get(0).toString()+",\"record\":["+rtn.substring(1)+"]"; 
	}
	
	/**
	 * 重新执行以下委托,必须执行10秒钟以后才可以执行，否则会报错,没办法服务器重复判断，请客户端做好js防止两次点击问题发生
	 * @param transRecordId
	 * @return
	 */
	public static int reDueMoney(long transRecordId,Market m){
		try{
		int count=Data.Update(m.db,"update transrecord set status=0 where transRecordId=? and status=1 and times<?", new Object[]{transRecordId,System.currentTimeMillis()-10000});
		
		
		return count;
		}catch(Exception ex)
		{
			log.error(ex.toString(), ex);
			return 0;
		}
		
	}
	
	

}
