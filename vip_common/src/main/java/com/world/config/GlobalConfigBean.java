package com.world.config;

public class GlobalConfigBean extends ConfigBean
{
  private String baseDomain;
  private String basePckPath;
  private String adminPath;
  private String adminLoginPath;
  private String mongodbIp;
  private int mongodbPort;
  private String mongodbUserName;
  private String mongodbPwd;
  private String mongodb_1;
  
  private String mongoDb;
  private boolean mongodbAuth;//是否开启mongodb安全认证
  private String memcachedIp;
  private String tk_url;
  private String tk_appkey;
  private String tk_appSecret;
  private String tk_sessionKey;
  private String beanName = "GlobalConfigBean";

  private String market;
  private String currency;
  private String currencyN;
  private String defaultLanguage;
  private boolean debugModel = false;
  private boolean changeView;//是否需要改变语言的同时改变jsp视图，有时间只是简单的改变语言就可以了
  
  private float chargeFeesRate;//充值手续费    比例
  private float transactionsFreesRateForBuyer;//买家交易手续费     比例
  private float transactionsFreesRateForSeller;//卖家交易手续费    比例
  private float withdrawFreesRateForFundsBank;//银行卡提现手续费     比例
  private float withdrawFreesRateForFundsBankMin;//银行卡提现手续费最低额
  
  private float withdrawFreesRateForBtc;//Btc提现手续费     比例
  private float recommendRate;//普通推荐人提成
  private float recommendRateVip_1;//vip1推荐人提成  金   比例数据     1:0.00006
  private float recommendRateVip_2;//vip2推荐人提成  银
  private float recommendRateVip_3;//vip3推荐人提成  铜
  
  private long phoneAuthGift;//手机认证赠送btc数量   此数据为整数  即比特币*100000000后的数据
  private long emailAuthGift;//邮箱认证赠送btc数量   此数据为整数  即比特币*100000000后的数据
  
  private double minWithdraw;//最低提现金额
  private double maxWithdraw;//最高提现金额
  
  private float minTrans;//比特币单笔交易最低金额
  private float maxTrans;//比特币单笔交易最高金额

  private double minBwTrans;//比特权单笔交易最低金额
  private double maxBwTrans;//比特权单笔交易最高金额

  private int sellBtcUserId;//机器人出售btc id
  private int buyBtcUserId;//机器人购买id
  private int levelMarketId;//基准市场
  
  private int robotMaxTradeNum;//机器人最大交易数
  
  public double dayAutoMaxWithdrawAmount;//单日自动最多提现比特币金额
  public double perAutoMaxWithdrawAmount;//单次自动最多提现比特币金额
  
  public double maxTodayWithdraw;//普通版用户每日最高提现额度
  
  private String seccPass;//session的安全加密秘钥
  
 
  private int forbidfIp10MinuteTimes;//每10分钟访问次数超过这个数量就禁止整个网站对于他的一切响应
  private String session;
  


public String getSession() {
	return session;
}

public  void setSession(String session) {
	this.session = session;
}

public int getForbidfIp10MinuteTimes() {
	return forbidfIp10MinuteTimes;
}

public void setForbidfIp10MinuteTimes(int forbidfIp10MinuteTimes) {
	this.forbidfIp10MinuteTimes = forbidfIp10MinuteTimes;
}

public String getSeccPass() {
	return seccPass;
}

public void setSeccPass(String seccPass) {
	this.seccPass = seccPass;
}

public double getMaxTodayWithdraw() {
	return maxTodayWithdraw;
}

public void setMaxTodayWithdraw(double maxTodayWithdraw) {
	this.maxTodayWithdraw = maxTodayWithdraw;
}

public double getDayAutoMaxWithdrawAmount() {
	return dayAutoMaxWithdrawAmount;
}

public void setDayAutoMaxWithdrawAmount(double dayAutoMaxWithdrawAmount) {
	this.dayAutoMaxWithdrawAmount = dayAutoMaxWithdrawAmount;
}

public double getPerAutoMaxWithdrawAmount() {
	return perAutoMaxWithdrawAmount;
}

public void setPerAutoMaxWithdrawAmount(double perAutoMaxWithdrawAmount) {
	this.perAutoMaxWithdrawAmount = perAutoMaxWithdrawAmount;
}

public int getRobotMaxTradeNum() {
	return robotMaxTradeNum;
}

public void setRobotMaxTradeNum(int robotMaxTradeNum) {
	this.robotMaxTradeNum = robotMaxTradeNum;
}

public int getLevelMarketId() {
	return levelMarketId;
}

public void setLevelMarketId(int levelMarketId) {
	this.levelMarketId = levelMarketId;
}

public int getSellBtcUserId() {
	return sellBtcUserId;
}

public void setSellBtcUserId(int sellBtcUserId) {
	this.sellBtcUserId = sellBtcUserId;
}

public int getBuyBtcUserId() {
	return buyBtcUserId;
}

public void setBuyBtcUserId(int buyBtcUserId) {
	this.buyBtcUserId = buyBtcUserId;
}

public float getWithdrawFreesRateForFundsBank() {
	return withdrawFreesRateForFundsBank;
}

public void setWithdrawFreesRateForFundsBank(float withdrawFreesRateForFundsBank) {
	this.withdrawFreesRateForFundsBank = withdrawFreesRateForFundsBank;
}

public float getWithdrawFreesRateForFundsBankMin() {
	return withdrawFreesRateForFundsBankMin;
}

public void setWithdrawFreesRateForFundsBankMin(
		float withdrawFreesRateForFundsBankMin) {
	this.withdrawFreesRateForFundsBankMin = withdrawFreesRateForFundsBankMin;
}

public float getWithdrawFreesRateForBtc() {
	return withdrawFreesRateForBtc;
}

public void setWithdrawFreesRateForBtc(float withdrawFreesRateForBtc) {
	this.withdrawFreesRateForBtc = withdrawFreesRateForBtc;
}

public float getMinTrans() {
	return minTrans;
}

public void setMinTrans(float minTrans) {
	this.minTrans = minTrans;
}

public float getMaxTrans() {
	return maxTrans;
}

public void setMaxTrans(float maxTrans) {
	this.maxTrans = maxTrans;
}

public double getMinBwTrans() {
	return minBwTrans;
}

public void setMinBwTrans(double minBwTrans) {
	this.minBwTrans = minBwTrans;
}

public double getMaxBwTrans() {
	return maxBwTrans;
}

public void setMaxBwTrans(double maxBwTrans) {
	this.maxBwTrans = maxBwTrans;
}

public double getMinWithdraw() {
	return minWithdraw;
}

public void setMinWithdraw(double minWithdraw) {
	this.minWithdraw = minWithdraw;
}

public double getMaxWithdraw() {
	return maxWithdraw;
}

public void setMaxWithdraw(double maxWithdraw) {
	this.maxWithdraw = maxWithdraw;
}

public long getPhoneAuthGift() {
	return phoneAuthGift;
}

public void setPhoneAuthGift(long phoneAuthGift) {
	this.phoneAuthGift = phoneAuthGift;
}

public long getEmailAuthGift() {
	return emailAuthGift;
}

public void setEmailAuthGift(long emailAuthGift) {
	this.emailAuthGift = emailAuthGift;
}

public float getRecommendRate() {
	return recommendRate;
}

public void setRecommendRate(float recommendRate) {
	this.recommendRate = recommendRate;
}

public float getRecommendRateVip_1() {
	return recommendRateVip_1;
}

public void setRecommendRateVip_1(float recommendRateVip_1) {
	this.recommendRateVip_1 = recommendRateVip_1;
}

public float getRecommendRateVip_2() {
	return recommendRateVip_2;
}

public void setRecommendRateVip_2(float recommendRateVip_2) {
	this.recommendRateVip_2 = recommendRateVip_2;
}

public float getRecommendRateVip_3() {
	return recommendRateVip_3;
}

public void setRecommendRateVip_3(float recommendRateVip_3) {
	this.recommendRateVip_3 = recommendRateVip_3;
}

public float getChargeFeesRate() {
	return chargeFeesRate;
}

public void setChargeFeesRate(float chargeFeesRate) {
	this.chargeFeesRate = chargeFeesRate;
}

public float getTransactionsFreesRateForBuyer() {
	return transactionsFreesRateForBuyer;
}

public void setTransactionsFreesRateForBuyer(float transactionsFreesRateForBuyer) {
	this.transactionsFreesRateForBuyer = transactionsFreesRateForBuyer;
}

public float getTransactionsFreesRateForSeller() {
	return transactionsFreesRateForSeller;
}

public void setTransactionsFreesRateForSeller(
		float transactionsFreesRateForSeller) {
	this.transactionsFreesRateForSeller = transactionsFreesRateForSeller;
}


public boolean isMongodbAuth() {
	return mongodbAuth;
}

public void setMongodbAuth(boolean mongodbAuth) {
	this.mongodbAuth = mongodbAuth;
}

public boolean isChangeView() {
	return changeView;
}

public void setChangeView(boolean changeView) {
	this.changeView = changeView;
}

public String getMongodb_1() {
	return mongodb_1;
}
  
public String getMarket() {
	return market;
}

public void setMarket(String market) {
	this.market = market;
}

public String getCurrency() {
	return currency;
}

public void setCurrency(String currency) {
	this.currency = currency;
}

public String getCurrencyN() {
	return currencyN;
}

public void setCurrencyN(String currencyN) {
	this.currencyN = currencyN;
}

public String getDefaultLanguage() {
	return defaultLanguage;
}

public void setDefaultLanguage(String defaultLanguage) {
	this.defaultLanguage = defaultLanguage;
}

public void setMongodb_1(String mongodb_1) {
	this.mongodb_1 = mongodb_1;
}
public boolean isDebugModel() {
    return this.debugModel;
  }
  public void setDebugModel(boolean debugModel) {
    this.debugModel = debugModel;
  }
  public String getBaseDomain() {
    return this.baseDomain;
  }
  public void setBaseDomain(String baseDomain) {
    this.baseDomain = baseDomain;
  }
  public String getBasePckPath() {
    return this.basePckPath;
  }
  public void setBasePckPath(String basePckPath) {
    this.basePckPath = basePckPath;
  }
  public String getAdminPath() {
    return this.adminPath;
  }
  public void setAdminPath(String adminPath) {
    this.adminPath = adminPath;
  }
  public String getAdminLoginPath() {
    return this.adminLoginPath;
  }
  public void setAdminLoginPath(String adminLoginPath) {
    this.adminLoginPath = adminLoginPath;
  }
  public String getMongodbIp() {
    return this.mongodbIp;
  }
  public void setMongodbIp(String mongodbIp) {
    this.mongodbIp = mongodbIp;
  }
  public int getMongodbPort() {
    return this.mongodbPort;
  }
  public void setMongodbPort(int mongodbPort) {
    this.mongodbPort = mongodbPort;
  }
  public String getMongodbUserName() {
    return this.mongodbUserName;
  }
  public void setMongodbUserName(String mongodbUserName) {
    this.mongodbUserName = mongodbUserName;
  }
  public String getMongodbPwd() {
    return this.mongodbPwd;
  }
  public void setMongodbPwd(String mongodbPwd) {
    this.mongodbPwd = mongodbPwd;
  }
 
  public String getMongoDb() {
    return this.mongoDb;
  }
  public void setMongoDb(String mongoDb) {
    this.mongoDb = mongoDb;
  }
  public String getMemcachedIp() {
    return this.memcachedIp;
  }
  public void setMemcachedIp(String memcachedIp) {
    this.memcachedIp = memcachedIp;
  }
  public String getTk_url() {
    return this.tk_url;
  }
  public void setTk_url(String tk_url) {
    this.tk_url = tk_url;
  }
  public String getTk_appkey() {
    return this.tk_appkey;
  }
  public void setTk_appkey(String tk_appkey) {
    this.tk_appkey = tk_appkey;
  }
  public String getTk_appSecret() {
    return this.tk_appSecret;
  }
  public void setTk_appSecret(String tk_appSecret) {
    this.tk_appSecret = tk_appSecret;
  }
  public String getTk_sessionKey() {
    return this.tk_sessionKey;
  }
  public void setTk_sessionKey(String tk_sessionKey) {
    this.tk_sessionKey = tk_sessionKey;
  }
  public String getBeanName() {
    return this.beanName;
  }
  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }
}