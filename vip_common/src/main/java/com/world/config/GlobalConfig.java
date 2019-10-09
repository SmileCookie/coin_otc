package com.world.config;

import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class GlobalConfig {
	private static Logger log = Logger.getLogger(GlobalConfig.class);
	
	private static Object lock              = new Object();
	private static GlobalConfig config     = null;
	private static ResourceBundle rb        = null;
	private static final String CONFIG_FILE = "main";
	
	private GlobalConfig() {
		rb = ResourceBundle.getBundle(CONFIG_FILE);
	}
	
	public static GlobalConfig getInstance() {
		log.info("初始化单例对象GlobalConfig，加载配置文件main.properties");
		synchronized(lock) {
			if(null == config) {
				config = new GlobalConfig();
			}
		}
		for(String key : config.rb.keySet()){
			log.info("读取配置文件main.properties: " + key + "=" + rb.getString(key));
		}

		return (config);
	}
	
	public static String getValue(String key) {
		if(rb == null){
			getInstance();
		}
		try {
			if(rb != null){
				//log.info("main:" + key);
				return (rb.getString(key));
			}else{
				return null;
			}
		} catch (Exception e) {
			// TODO: 2017/7/24 suxinjie 这里暂时先不打印日志,因为有很多配置在陪在文件中没有,会报错,需要统一整理后在开启 
//			log.error(e.toString() + ",key:" + key, e);
		}
		return null;
	}
	
	public final static String userPath = getValue("userPath")==null?"com.world.controller.manage":getValue("userPath");

	public final static boolean debugModel = getValue("debugModel") == null ? false : Boolean.parseBoolean(getValue("debugModel"));
	public final static String baseDomain = getValue("baseDomain");
	public final static String basePckPath = getValue("basePckPath");
	public final static String adminPath = getValue("adminPath");
	public final static String adminLoginPath = getValue("adminLoginPath");
	public final static boolean adminPageOpen = getValue("adminPageOpen") == null ? false : Boolean.parseBoolean(getValue("adminPageOpen"));

	//mongodb
	public final static String mongoDbIp = getValue("mongoDbIp");
	public final static String mongodb_1 = getValue("mongodb_1");
	public final static String mongodbUserName = getValue("mongodbUserName");
	public final static String mongodbPwd = getValue("mongodbPwd");
	
	//mysql
	public final static String mysqlDbIp = getValue("mysqlDbIp");
	public final static String mysqlDbUserName = getValue("mysqlDbUserName");
	public final static String mysqlDbPwd = getValue("mysqlDbPwd");
	public final static String mysqlDb_1 = getValue("mysqlDb_1");

	//启动connection校验定时器,定时器运行时间间隔就是timeBetweenEvictionRunsMillis的值.默认为-1,表示不启动定时器,这里设定为1小时,只要小于mysql的wait_timeout就可以了
	public final static long mysqlDb_TimeBetweenEvictionRunsMillis = getValue("mysqlDb_TimeBetweenEvictionRunsMillis") == null ? -1 : Long.parseLong(getValue("mysqlDb_TimeBetweenEvictionRunsMillis"));
	//connection的空闲时间大于这个值,就直接被关闭,并从连接池中删除
	public final static long mysqlDb_minEvictableIdleTimeMillis = getValue("mysqlDb_minEvictableIdleTimeMillis") == null ? -1 : Long.parseLong(getValue("mysqlDb_minEvictableIdleTimeMillis"));
	//最大连接数量
	public final static int mysqlDb_maxActive = getValue("mysqlDb_maxActive") == null ? 100 : Integer.parseInt(getValue("mysqlDb_maxActive"));
	//初始化连接
	public final static int mysqlDb_initialSize = getValue("mysqlDb_initialSize") == null ? 10 : Integer.parseInt(getValue("mysqlDb_initialSize"));
	//最大空闲连接
	public final static int mysqlDb_maxIdle = getValue("mysqlDb_maxIdle") == null ? 10 : Integer.parseInt(getValue("mysqlDb_maxIdle"));
	//最小空闲连接
	public final static int mysqlDb_minIdle = getValue("mysqlDb_minIdle") == null ? 5 : Integer.parseInt(getValue("mysqlDb_minIdle"));
	//超时等待时间以毫秒为单位 1000等于60秒
	public final static long mysqlDb_maxWait = getValue("mysqlDb_maxWait") == null ? 1000 : Integer.parseInt(getValue("mysqlDb_maxWait"));
	//防止拿到关闭的链接,避免java.sql.SQLException:connection is closed
	public final static String mysqlDb_validationQuery = getValue("mysqlDb_validationQuery") == null ? "select * from dual" : getValue("mysqlDb_validationQuery");

	//btc充值
	public final static String mysqlDbIp_btc = getValue("mysqlDbIp_btc");
	public final static String mysqlDbUserName_btc = getValue("mysqlDbUserName_btc");
	public final static String mysqlDbPwd_btc = getValue("mysqlDbPwd_btc");
	public final static String mysqlDb_1_btc = getValue("mysqlDb_1_btc");
	
	public final static String memcachedIp = getValue("mmcache_1_ip");
	public final static String memcachedPort = getValue("mmcache_1_port");
	
	public final static String market = getValue("market");// 可以公共获取的一个变量，当前市场比如usa
	public final static Set<String> listenMarket = getValue("listenMarket") == null ?
			new HashSet<>() :
			"".equals(getValue("listenMarket").trim()) ? new HashSet<>() : new HashSet(Arrays.asList(getValue("listenMarket").trim().split(",")));
	public final static String currency = getValue("currency");// 当前货币，比如usd
	public final static String currencyN = getValue("currencyN");// 当前货币符号 比如$
	public final static String defaultLanguage = getValue("defaultLanguage");// 当前默认语言，比如针对香港会有繁体中文和英文两种语言
	public final static boolean changeView = getValue("changeView") == null ? false : Boolean.parseBoolean(getValue("changeView"));
//	public final static boolean mobile = getValue("mobile") == null ? false : Boolean.parseBoolean(getValue("mobile"));

	public final static String seccPass = getValue("seccPass");
	public final static String session = getValue("session");
	public final static boolean showCode = Boolean.parseBoolean(getValue("showCode") == null ? "true" : getValue("showCode"));
	public final static int forbidfIp10MinuteTimes = getValue("forbidfIp10MinuteTimes") == null ? 0 : Integer.parseInt(getValue("forbidfIp10MinuteTimes"));// 每10分钟访问次数超过这个数量就禁止整个网站对于他的一切响应
	//是否开启静态处理功能 默认false 
	public final static boolean openStatics = Boolean.parseBoolean(getValue("openStatics") == null ? "false" : getValue("openStatics"));
	public final static boolean openAccessLog = Boolean.parseBoolean(getValue("openAccessLog") == null ? "false" : getValue("openAccessLog"));
	
	public final static boolean cacheOpen = Boolean.parseBoolean(getValue("cacheOpen") == null ? "true" : getValue("cacheOpen"));
	public final static boolean outCacheOpen = Boolean.parseBoolean(getValue("outCacheOpen") == null ? "false" : getValue("outCacheOpen"));
	////选择数据库功能是否打开
	public final static boolean databaseSelectOpen = Boolean.parseBoolean(getValue("databaseSelectOpen") == null ? "false" : getValue("databaseSelectOpen"));
	
	////IP黑白名单功能是否打开
	public final static boolean ipDefenseOpen = Boolean.parseBoolean(getValue("ipDefenseOpen") == null ? "false" : getValue("ipDefenseOpen"));
	
	public final static String sysMsgTipsClass = getValue("sysMsgTipsClass") == null ? "com.world.system.tips.CommonSysTipType" : getValue("sysMsgTipsClass");
	
	public final static boolean ipFromMysql = Boolean.parseBoolean(getValue("ipFromMysql") == null ? "true" : getValue("ipFromMysql"));
	
	public final static boolean openDownTable = Boolean.parseBoolean(getValue("openDownTable") == null ? "false" : getValue("openDownTable"));

	//是否开启地址校验
	public final static boolean isCheckAddress = Boolean.parseBoolean(getValue("isCheckAddress") == null ? "false" : getValue("isCheckAddress"));

	public final static boolean isOpenManagement = Boolean.parseBoolean(getValue("isOpenManagement") == null ? "false" : getValue("isOpenManagement"));

	public final static String scheme = StringUtil.exist(getValue("scheme")) ? getValue("scheme") : "https";
	//zendesk相关配置
	public final static String SHARED_KEY = getValue("SHARED_KEY");
	public final static String SUBDOMAIN = getValue("SUBDOMAIN");

    //外汇请求配置
    public final static String waihuiUrl = getValue("waihuiUrl") == null ? "https://ali-waihui.showapi.com/waihui-list" : getValue("waihuiUrl") ;
    public final static String waihuiAppCode = getValue("waihuiAppCode") == null ? "1e73fbf204d842e3af805ba695a38d58" : getValue("waihuiAppCode");

	// 代理IP
	public final static String proxyIp = getValue("proxyIp");
	// 代理端口
	public final static int proxyPort = getValue("proxyPort") == null ? 9999 : Integer.parseInt(getValue("proxyPort"));
	// 是否开启代理
	public final static boolean proxyEnable = Boolean.parseBoolean(getValue("proxyEnable") == null ? "false" : getValue("proxyEnable"));

    //rabbitmq配置
    public final static String rabbitmqHost = getValue("rabbitmqHost");
    public final static int rabbitmqPort = getValue("rabbitmqPort") == null ? 0 : Integer.parseInt(getValue("rabbitmqPort"));
    public final static String rabbitmqUser = getValue("rabbitmqUser");
    public final static String rabbitmqPwd = getValue("rabbitmqPwd");

    //rabbitmq uri
    public final static String rabbitmqUri = "amqp://" + GlobalConfig.rabbitmqUser + ":" + GlobalConfig.rabbitmqPwd + "@" + GlobalConfig.rabbitmqHost + ":" + GlobalConfig.rabbitmqPort;

    //登录日志和操作日志 队列 rabbitmq配置
	public final static String userLoginLogInfo = getValue("userLoginLogInfo");
	public final static String operateLogInfo = getValue("operateLogInfo");
	public final static String payUserWalletQueue = getValue("payUserWalletQueue");


    // 最新成交记录通知 websocket mq队列信息
    public final static String latestTradeExchange = getValue("latestTradeExchange");
    // 最新盘口挂单通知 websocket mq队列信息
    public final static String dishDepthExchange = getValue("dishDepthExchange");
    // 最新盘口深度通知 websocket mq队列信息
    public final static String userEntrustCompleteExchange = getValue("userEntrustCompleteExchange");

    //redis 缓存配置
    public final static String redisHost = getValue("redisHost");
    public final static int redisPort = getValue("redisPort") == null ? 6379 : Integer.parseInt(getValue("redisPort"));
    public final static String redisPwd = getValue("redisPwd");
    public final static int redisTimeout = getValue("redisTimeout") == null ? 3000 : Integer.parseInt(getValue("redisTimeout"));
    public final static int redisPoolMaxActive = getValue("redisPoolMaxActive") == null ? 256 : Integer.parseInt(getValue("redisPoolMaxActive"));
    public final static int redisPoolMaxIdle = getValue("redisPoolMaxIdle") == null ? 128 : Integer.parseInt(getValue("redisPoolMaxIdle"));
    public final static int redisPoolMinIdle = getValue("redisPoolMinIdle") == null ? 50 : Integer.parseInt(getValue("redisPoolMinIdle"));
    public final static int redisPoolMaxWait = getValue("redisPoolMaxWait") == null ? 1000 : Integer.parseInt(getValue("redisPoolMaxWait"));
    // redis 集群
    public final static String redisClusterNodes = getValue("redisClusterNodes");

    /**
     * coinmarketcap 平台API Key
     */
    public final static String coinmarketcapApiKey = getValue("coinmarketcap_api_key");
	/**
	 * 驾驶舱cookie
	 */
	public final static String cockpit = getValue("cockpitKey");
}