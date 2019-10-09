package com.tenstar;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统状态
 * @author pc
 *
 */
public class SystemStatus {
    private final static Logger log = Logger.getLogger(SystemStatus.class);

    public static String moneyRuning = "moneyRuning";//资金是否运行
    public static String moneyNewWork = "moneyNewWork";//是否有资金的新任务
    
    public static String exchangeRuning = "exchangeRuning";//资金是否运行
    public static String exchangeNewWork = "exchangeNewWork";//是否有资金的新任务
    

    public static String chartDataRuning = "chartDataRuning";//资金是否运行
    public static String chartDataNewWork = "chartDataNewWork";//是否有资金的新任务
    
    
    public static String autoRuning = "autoRuning";//资金是否运行
    public static String autoNewWork = "autoNewWork";//是否有资金的新任务
    
    //定义每个币种的处理状态map默认返回true
    private static Map<String,Boolean> systemStatusMap = new HashMap<String,Boolean>();
     
    public static boolean getSystemStatus(String key){
    	Boolean value =systemStatusMap.get(key);
    	if(value==null){
    		value = true;
    		systemStatusMap.put(key, value);
    	}
    	return value;
    }
    
    public static void setSystemStatus(String key ,boolean value){
    	systemStatusMap.put(key, value);
    }
     
    //public static Market  market=Market.InitMarket(GlobalConfig.getValue("market") + "market");
    
    public static void main(String[] args){
    	Map<String,Boolean>  map = new HashMap<String,Boolean>();
    	map.put("key", true);
    	log.info("key:" + map.get("key"));
    	Boolean v1 = map.get("key1");
    	log.info("key1:" + getSystemStatus("key"));
    	
    }
    
}
