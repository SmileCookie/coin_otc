package com.world.model.dao;

import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.TransRecord2;
import com.world.util.DigitalUtil;

public class TransRecordDao2 extends DataDaoSupport<TransRecord2>{

	
	/**
	 * 获取资金处理的transrecord
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
	 * @param pageSize 页码大小 最大200
	 * @param status 资金处理状态 0初始状态 1 处理失败 2处理成功   只有等于1的时候，可以放心更新为0，让他重新处理
	 * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合transRecordId,unitPrice,totalPrice,numbers,entrustIdBuy,userIdBuy,entrustIdSell,userIdSell,TYPES,times,status,isCount
	 */
	public static String getMoney(long entrustId,int userId,int pageIndex,int type,long timeFrom,long timeTo,
			double numberFrom,double numberTo,double priceFrom,double priceTo,double totalFrom, double totalTo, long pageSize,int status,String dbName){
		
		if(pageSize==0) 
			 pageSize=10;
		if(pageSize>200)
			pageSize=200;
		
		StringBuilder where=new StringBuilder();
		 

		if(entrustId>0){
			where.append(" and (entrustIdSell="+entrustId+" or entrustIdBuy="+entrustId+") ");//仅仅显示这个状态的
		}
		
	/*	if(transRecordId>0){
			where.append(" and transRecordId="+transRecordId);//仅仅显示这个状态的
		}*/
		
		
		if(userId>0){
			where.append(" and (userIdBuy="+userId+" or userIdSell="+userId+") ");//仅仅显示这个状态的
		}
		
		if(status>0){ 
	    	where.append(" and status="+status);//仅仅显示这个状态的
		}
		/*start by xzhang 20170830 设置成交记录查询取消订单条件 JYPTYY-8*/
		//status==0 是所有的意思
		if(type>-2 && type != -1){
			where.append(" and types="+type);//类型 
		}
		if(type == -1){
			where.append(" and (userIdBuy = 0 or userIdSell = 0) ");//
		}
		/*end*/
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

		//xzhang 20170828 交易记录分页显示功能异常  JYPT-1245
		String sql="select * from (select transRecordId,unitPrice,totalPrice,numbers,entrustIdBuy,userIdBuy,entrustIdSell,userIdSell,TYPES,times,status,isCount FROM transrecord    "+w+"  ";
		sql += "union all select transRecordId,unitPrice,totalPrice,numbers,entrustIdBuy,userIdBuy,entrustIdSell,userIdSell,TYPES,times,status,isCount FROM transrecord_all   "+w+" ) fa ";
		sql += " order by times desc limit ?,?";
		//String countSql="select found_rows() as num";
		log.info("sql显示："+sql);
		String sqlcount =" SELECT (select count(*) FROM transrecord  "+w+" )+(select count(*) FROM transrecord_all " +w+ ") from dual";
		List lists = Data.Query(dbName,sql, new Object[]{(pageIndex-1)*pageSize , pageSize});//提现记
		List count = (List)Data.GetOne(dbName,sqlcount, new Object[]{});//提现记录数量
		StringBuilder sb=new StringBuilder();

        for(Object b : lists){
        	List beb = (List) b;
        	sb.append(",["+beb.get(0)+","+DigitalUtil.getBigDecimal(beb.get(1))+","+
        			DigitalUtil.getBigDecimal(beb.get(2).toString())+","+DigitalUtil.getBigDecimal(beb.get(3))+","+
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
	public static int reDueMoney(long transRecordId,String dbName){
		try{
		int count=Data.Update(dbName,"update transrecord set status=0 where transRecordId=? and status=1 and times<?", new Object[]{transRecordId,System.currentTimeMillis()-10000});
		
		
		return count;
		}catch(Exception ex)
		{
			log.error(ex.toString(), ex);
			return 0;
		}
		
	}
	
}
