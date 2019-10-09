package com.world.model.daos.chart;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.world.model.Market;

public class ChartDataFactory {

	protected static Logger log = Logger.getLogger(ChartDataFactory.class.getName());
	
	private static Map<Integer , ChartDataType> types = new HashMap<Integer, ChartDataType>();
	
	/***
	 * 
	 * @param timeType 时间类型   如分1、时2、天3
	 * @param intervalType  频率类型  如5分  10分
	 * @return
	 */
	public static String getJson(int timeType, int intervalType, Market m){
		/*ChartDataType ctype = types.get(timeType);
		
		if(ctype == null){
			ctype = initType(timeType);
		}
		return ctype.getJson(intervalType);*/
		
		return ChartManager.getJson(timeType, intervalType,m);
       
	}
	
	
	public static ChartDataType getChartDataTYpe(int timeType){
		ChartDataType ctype = types.get(timeType);
		
		if(ctype == null){
			ctype = initType(timeType);
		}
		return ctype;
	}
	
	/***
	 * 
	 * @param type 时间类型   如分1、时2、天 3 
	 * @param intervalType  频率类型  如5分  10分
	 * @return
	 */
	public static String getJson(int timeType , int intervalType, int type,Market m){
//		ChartDataType ctype = types.get(timeType);
//		
//		if(ctype == null){
//			ctype = initType(timeType);
//		}
		
		return ChartManager.getJson(intervalType, type,m);
		
        //return ctype.getJson(intervalType, type);
	}
	
	
	public static synchronized ChartDataType initType(int timeType){
		ChartDataType ctype = types.get(timeType);
		if(ctype == null){
			Map<Integer, ChartList> initChartMap = new HashMap<Integer, ChartList>();
			initChartMap.put(1, null);
			if(timeType == 1){
				//initChartMap.put(3, null);//增加3分钟数据add by zhanglinbo
				initChartMap.put(5, null);
//				initChartMap.put(10, null);
				initChartMap.put(15, null);
				initChartMap.put(30, null);
			}else if(timeType == 2){
				initChartMap.put(2, null);
				initChartMap.put(4, null);
				initChartMap.put(6, null);
//				initChartMap.put(8, null);
				initChartMap.put(12, null);
			}else{
				initChartMap.put(3, null);
				initChartMap.put(7, null);
			}
			ctype = new ChartDataType(timeType , initChartMap);
			types.put(timeType, ctype);
		}
		return ctype;
	}
	
	public static Map<Integer, ChartDataType> getTypesInstance() {
		return types;

	}
	
	
	
}


