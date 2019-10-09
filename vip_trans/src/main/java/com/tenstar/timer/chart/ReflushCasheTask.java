package com.tenstar.timer.chart;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.world.model.Market;



public class ReflushCasheTask extends TimerTask{

	private static Logger log = Logger.getLogger(ReflushCasheTask.class);
	
	private int datatype = 1;
	
	private Market m;

	public ReflushCasheTask(int datatype,Market m) {
		super();
		this.datatype = datatype;
		this.m =m;
		
	}




	@Override
	public void run() {
		if(datatype==1){
			 ChartArray ca = ChartData.caMap.get(m.market);
			//更新分钟缓存
			 ca.reflushMinute(m);
//			log.info("reflush Minute Cashe ===");
			
//		}else if (datatype==2) {
//			//更新小时缓存
			 ca.reflushHour(m); 
//			log.info("reflush Hour Cashe ===");
//			
//		}else if (datatype==3) {
//			//更新天缓存
			 ca.reflushDay(m);
//			log.info("reflush Day Cashe ===");

			//ChartArray array = new ChartArray(Market.market, 360, 1000, 3600);
			log.info("===reflush wisdom chartarray===");
			//array.reflushMinute();
			//array.reflushHour();
			//array.reflushDay();
		}
		
	}
	
}
