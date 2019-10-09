package com.tenstar.timer.chart;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import com.tenstar.timer.auto.AutoTask;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;
import com.world.model.daos.chart.StatisticsFor24Hour;
import com.world.util.DigitalUtil;

/**
 * 新的改进，每次获取都取得最新的完整数据
 * @author 电脑
 */
public class ChartArrayItem {
	public static Logger log = Logger.getLogger(ChartArrayItem.class);
	public Object[][] data;//分钟的数据//环状结构
	private int currentIndex=0;
	int maxItem=0;
	int type=0;
	int updateMax=20;//增量更新的时候需要多大
	private Market m;
	
	/**
	 * 过去24小时的数据
	 * @return
	 */
	public  double[] get24(){
		long time=System.currentTimeMillis()-1000*60*60*24;
		return getHotData(time);
	}
	/**
	 * 设置多少数据之内的最高量，最低量
	 */
	public double[] getHotData(long time){ 
		 long zengliang=0;
		 if(AutoTask.rc!=null)
			 zengliang=AutoTask.rc.getBaseShowAdd();
		 
		 
		 StatisticsFor24Hour sfh = ChartManager.get24HourInfo(m);//获取24小时统计信息
		 if(sfh != null){
//			 log.info(m.market+" chart add:"+sfh.getHigh()+" "+ sfh.getLow()+" "+ sfh.getTotalNumber() + zengliang+"  "+ sfh.getTotalMoney());
			 //最高，最低，交易量，总金额
			 return new double[]{Market.formatMoney(sfh.getHigh(),m), Market.formatMoney(sfh.getLow(),m), Market.formatNumber(sfh.getTotalNumber().add(BigDecimal.valueOf(zengliang)),m), Market.formatMoney(sfh.getTotalMoney(),m)};
		 }else{
			 return new double[]{0,0,zengliang,0};
		 }
		 
		 
//			List one=(List)Data.GetOne("select max(unitPrice),Min(unitPrice),sum(numbers) from transrecord where unitPrice>0 AND times>=?", new Object[]{time});
//			//log.info("select max(unitPrice),Min(unitPrice),sum(numbers) from transrecord where unitPrice>0 AND times>="+time);
//			 if(one != null){
//				 return new double[]{one.get(0)==null?0:Market.formatMoney(Long.parseLong(one.get(0).toString())),one.get(1)==null?0:Market.formatMoney(Long.parseLong(one.get(1).toString())),one.get(2)==null?0:Market.formatNumber(Long.parseLong(one.get(2).toString())+zengliang)};
//			 }else{
//				 return new double[]{0,0,zengliang};
//			 }
	}   
	  
	/** 
	 * @param name
	 * @param maxday   小时是天的10倍 分钟是小时的10倍  不用保持完整的分钟
	 */
	public ChartArrayItem(int type,int maxItem,com.world.model.Market m2){
		this.type=type;
		this.maxItem=maxItem;
		this.m = m2;
		data=new Object[maxItem][8];
		//reload();
	}
	
	public void reload(Market m){
		int index=0;
		for(int i=0;i<maxItem;i++){
			 List<Bean> li=Data.Query(m.db ,"select * from chartdata where type=? order by chartDataId desc limit ?,200", new Object[]{type,index*1000},ChartDataBean.class);

			 if(li!=null&&li.size()>0){
				 int size=li.size();
				 for(int j=0;j<size&&currentIndex<maxItem;j++){
					 ChartDataBean td=(ChartDataBean)li.get(j);

					 SetData(td,currentIndex);
					currentIndex++;
				 }
		     }else
		    	 break;
			 if(currentIndex==maxItem){
					currentIndex--;
					break;
			 }
			 index++;
		}
		log.info("初始化"+type+"数量："+currentIndex);
		currentIndex=0;//把当前光标重置到0
	}
	/**
	 * 跟数据库同步做一次更新,仅仅更新最后时间的前后各30条
	 */
	public void Update(){
		//reload();
		//log.info("当前："+currentIndex);
		//最后更新时间
//		long lastTime=data[currentIndex][0];
//
//		 List<Bean> liOld=Data.Query("select * from ChartData where type=? and times<=? order by times desc limit 0,5", new Object[]{lastTime,type},ChartDataBean.class);//对于老的更新一般一个周期只需要1个
//		 if(liOld!=null&&liOld.size()>0){
//			 //更新老产品不更改index
//			 int size=liOld.size();
//			
//			 for(int j=0;j<size;j++){
//				 ChartDataBean td=(ChartDataBean)liOld.get(j);
//				     int index=getIndex(j+currentIndex);
//				     SetData(td,index);
//			 } 
//	     }
//		 List<Bean> liNew=Data.Query("select * from ChartData where type=? and times>? order by times  limit 0,?", new Object[]{lastTime,type,updateMax},ChartDataBean.class);
//		 
//		 if(liNew!=null&&liNew.size()>0){
//			 int size=liNew.size();
//			//更新新产品更新index
//			 for(int j=1;j<size;j++){
//				
//				 ChartDataBean td=(ChartDataBean)liNew.get(j-1);
//				 currentIndex=getIndex(currentIndex-1);
//				     int index=getIndex(currentIndex);
//				     SetData(td,index);
//			 }
//	     }
	//	 log.info("当前2："+currentIndex);
	}

	/**
	 * 设置一项
	 * @param sb
	 * @param i
	 */
	public void SetData(ChartDataBean td,int i){
	
		data[i][0]=td.getTimes();
		 data[i][1]=td.getOpen();
		 data[i][2]=td.getHigh();
		 data[i][3]=td.getLow();
		 data[i][4]=td.getClose();
		 data[i][5]=td.getTotalNumber();
		 data[i][6]=td.getMiddle();
		 data[i][7]=td.getTotalMoney();
	}
	 
	/**
	 * 设置一项
	 * @param sb
	 * @param i
	 */
	private void GetData(StringBuilder sb,int i){
//		 Timestamp st=new Timestamp(data[i][0]);
		//  log.info(i+":"+data[i][1]+""+data[i][1]);
		sb.append(",["+Long.parseLong(data[i][0].toString())/1000+",\"\",\"\","+Market.formatMoney(DigitalUtil.getBigDecimal(data[i][1]),m)+","+Market.formatMoney(DigitalUtil.getBigDecimal(data[i][4]),m)
		+","+Market.formatMoney(DigitalUtil.getBigDecimal(data[i][2]),m)+","+Market.formatMoney(DigitalUtil.getBigDecimal(data[i][3]),m)+","+Market.formatNumber(DigitalUtil.getBigDecimal(data[i][5]),m)+"]");
	} 
	/**
	 * 获取实际的index
	 * @param index 
	 * @return
	 */
	public int getIndex(int index){
		if(index<0){
			return maxItem+index;
		}
		else if(index<maxItem)
			return index;
		else{
			return index-maxItem;
		}
	}

	/**
	 * 设置多项
	 * @param sb
	 * @param i
	 */
	private ChartDataBean GetData(StringBuilder sb,int index,int beishu){
		if(Long.parseLong(data[index][0].toString())==0)
    		return null;
		if(beishu==1){
			GetData(sb,index);
			return null;
		}
		 //拷贝出来第一个
		 ChartDataBean td=new  ChartDataBean();
		 td.setTimes(Long.parseLong(data[index][0].toString()));
		 td.setOpen(DigitalUtil.getBigDecimal(data[index][1]));
		 td.setHigh(DigitalUtil.getBigDecimal(data[index][2]));
		 td.setLow(DigitalUtil.getBigDecimal(data[index][3]));
		 td.setClose(DigitalUtil.getBigDecimal(data[index][4]));
		 td.setTotalNumber(DigitalUtil.getBigDecimal(data[index][5]));
		 td.setMiddle(DigitalUtil.getBigDecimal(data[index][6]));
		 td.setTotalMoney(DigitalUtil.getBigDecimal(data[index][7]));
		for(int i=1;i<beishu;i++){
			int nowIndex=getIndex(index+i);
			 if(DigitalUtil.getBigDecimal(data[nowIndex][2]).compareTo(DigitalUtil.getBigDecimal(data[index][2]))>0)
				 td.setHigh(DigitalUtil.getBigDecimal(data[nowIndex][2]));//设置最高价
			 
			 if(DigitalUtil.getBigDecimal(data[nowIndex][3]).compareTo(DigitalUtil.getBigDecimal(data[index][3]))<0)
				 td.setLow(DigitalUtil.getBigDecimal(data[nowIndex][3]));//设置最低价
			 if(i==(beishu-1))
				 td.setClose(DigitalUtil.getBigDecimal(data[nowIndex][4]));//最后一项设置为关
			 td.setTotalNumber(td.getTotalNumber().add(DigitalUtil.getBigDecimal(data[nowIndex][5]))); 
			 td.setTotalMoney(td.getTotalMoney().add(DigitalUtil.getBigDecimal(data[nowIndex][7])));
		 }
		if(td.getTotalNumber().compareTo(BigDecimal.ZERO)>0)
		   td.setMiddle(td.getTotalMoney().divide(td.getTotalNumber(),m.exchangeBixDian,RoundingMode.CEILING));
		 sb.append(",["+td.getTimes()/1000+",\"\",\"\","+Market.formatMoney(td.getOpen(),m)+","+Market.formatMoney(td.getClose(),m)+","+Market.formatMoney(td.getHigh(),m)+","+Market.formatMoney(td.getLow(),m)+","+Market.formatNumber(td.getTotalNumber(),m)+"]");
        return td;
	}
	/**
	 * 获取数据
	 * @param time   时间 小于等于这个时间之前的，这个功能暂时还没实现
	 * @param num  数量
	 * @param doubles  重复，比如5分钟 10分钟 30分钟，是基础数量的倍数
	 * @return
	 */
    public String GetData(long time,int num,int doubles){
    	log.info("获取数据："+time+":"+num+":"+doubles);
    	/** 0 300 1
    	 * 获取交易记录列表
    	 * @param num
    	 * @return
    	 */
    	if(num*doubles>maxItem){
    		num=(maxItem/doubles)-1;//预留一个出来
    	} 
    		StringBuilder sb=new StringBuilder();
            for(int i=(num*doubles-1);i>-1;){

            	if(i>-1){
            	int nowIndex=getIndex(currentIndex+i);
            	
            	GetData(sb,nowIndex,doubles);
            	}else
            		break;
            	 
            	i-=doubles;
            		
            }
    		String rtn=sb.toString();
    		if(rtn.length()>0)
    			rtn=rtn.substring(1);
    		return rtn;
    	
	}
    

}
