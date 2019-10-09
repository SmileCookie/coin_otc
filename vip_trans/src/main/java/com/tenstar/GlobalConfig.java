package com.tenstar;
import java.util.ResourceBundle;

public class GlobalConfig {
	private static Object lock              = new Object();
	private static GlobalConfig config     = null;
	private static ResourceBundle rb        = null;
	private static final String CONFIG_FILE = "global";
	public static boolean  mongodbAuth=false;
	public static String mongodbPwd="";
	public static String mongodbUserName="";
	public static String mongodb_1="world";
	public static String mongoDb; 
	public static String memserverName="";//memchached的ip
	public static int memport=0;//memcached端口
	
	/**
	 * 交易的产品名称
	 */
	public static String productType="比特币";
	public static int productInt=2;
	/**
	 * 交易的产品费率
	 */
	public static long productFee=100000000;
	
	/**
	 * 交易的购买方名称
	 */
	public static String moneyType="人民币";
	public static int moneyTypeInt=1;
	/**
	 * 交易的购买方费率
	 */
	public static long moneyFee=100;
	

	private GlobalConfig() {
		rb = ResourceBundle.getBundle(CONFIG_FILE);
	}
	
	public static GlobalConfig getInstance() {
		synchronized(lock) {
			if(null == config) {
				config = new GlobalConfig();
			}
		}
		return (config);
	}
	
	public static String getValue(String key) {
		if(rb == null){
			getInstance();
		}
		if(rb != null){
			return (rb.getString(key));
		}else{
			return null;
		}
		
	}
}
