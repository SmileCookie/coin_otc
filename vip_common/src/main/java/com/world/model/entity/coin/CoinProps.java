package com.world.model.entity.coin;

import java.io.Serializable;
import java.math.BigDecimal;


/***
 * 电子货币属性
 * @author apple
 *
 */
public class CoinProps implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String coinParam = "coint";
	
	public CoinProps(){}
	
	public CoinProps(int fundsType, String databaseKey, String propCnName, String propTag,String outDatabaseName,String unitTag) {
		this.databaseKey = databaseKey;
		this.fundsType = fundsType;
		this.propCnName = propCnName;
		this.propTag = propTag;
		this.outDatabaseName = outDatabaseName;
		this.unitTag = unitTag;
//		this.isERC=isERC();
	}
	
	private int fundsType;
	private String databaseKey;//对应的数据库键值   btc
	private String databasesName;//对应的数据库名称 
	private String propCnName;//属性中文名称           比特币 
	private String propEnName;//属性英文名称           BTC
	private String propTag;//属性的标识符号anlian    BTC
	private String outDatabaseName;//外部关联数据库名称 
	private String unitTag;//单位符号            "฿"
	private String value;
	public String database;             //
	private String web;//网站地址查看基路径   
	private boolean storage;//是否开通存储
	private boolean isCoin;//是否是商户版充值地址
	private String priceKey;//交易缓存价格的键值
	private BigDecimal dayFreetrial;//日免审额度
	private BigDecimal dayCash;//每日允许提现的额度
	private BigDecimal timesCash;//次提现额度
	private BigDecimal minFees;//交易手续费
	private int inConfirmTimes;//充值到账的确认次数
	private int outConfirmTimes;//允许提现的确认次数
	private BigDecimal minCash;//最小提现额度
    private boolean canCharge = true;//是否支持充值
    private boolean canWithdraw = true;//是否支持提现
    private boolean display = true;//是否显示
	private int serNum;//排序
    private boolean isERC;//是否是 ERC2.0 币种
    private String imgUrl;//币种图片url   add by kinghao 20181121
	private int agreement;


	public int getAgreement() {
		return agreement;
	}

	public void setAgreement(int agreement) {
		this.agreement = agreement;
	}

	public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isERC() {
        return isERC;
    }

    public void setERC(boolean ERC) {
        isERC = ERC;
    }

    public boolean isCoin() {
		return isCoin;
	}

	public void setCoin(boolean isCoin) {
		this.isCoin = isCoin;
	}

	public boolean isStorage() {
		return storage;
	}

	public void setStorage(boolean storage) {
		this.storage = storage;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public String getCnname(){
		return propCnName;
	}
	public String getEnname(){
		return propCnName;
	}
	
	public String getTag(){
		return propTag;
	}
	
	public String getStag(){
		return propTag.toLowerCase();
	}
	
	public String getUtag(){
		return unitTag;
	}
	
	public String getUnitTag() {
		return unitTag;
	}

	public void setUnitTag(String unitTag) {
		this.unitTag = unitTag;
	}

	public String getOutDatabaseName() {
		return outDatabaseName;
	}

	public void setOutDatabaseName(String outDatabaseName) {
		this.outDatabaseName = outDatabaseName;
	}

	public String getPropEnName() {
		return propEnName;
	}
	public void setPropEnName(String propEnName) {
		this.propEnName = propEnName;
	}
	public String getPropTag() {
		return propTag;
	}
	public void setPropTag(String propTag) {
		this.propTag = propTag;
	}
	public String getDatabaseKey() {
		return databaseKey;
	}
	public void setDatabaseKey(String databaseKey) {
		this.databaseKey = databaseKey;
	}
	public String getDatabasesName() {
		return databasesName;
	}
	public void setDatabasesName(String databasesName) {
		this.databasesName = databasesName;
	}
	public String getPropCnName() {
		return propCnName;
	}
	public void setPropCnName(String propCnName) {
		this.propCnName = propCnName;
	}
	
	public String getCoinParam() {
		return coinParam;
	}
	
	/**
	 * 获取大写开头的标识
	 * @return
	 */
	public String getUpTag(){
		return propTag.substring(0, 1) + propTag.substring(1, propTag.length()).toLowerCase();
	}


	public int getFundsType() {
		return fundsType;
	}


	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public String getPriceKey() {
		return priceKey;
	}

	public void setPriceKey(String priceKey) {
		this.priceKey = priceKey;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public BigDecimal getMinFees() {
		return minFees;
	}

	public void setMinFees(BigDecimal minFees) {
		this.minFees = minFees;
	}

	public BigDecimal getDayFreetrial() {
		return dayFreetrial;
	}

	public void setDayFreetrial(BigDecimal dayFreetrial) {
		this.dayFreetrial = dayFreetrial;
	}

	public BigDecimal getDayCash() {
		return dayCash;
	}

	public void setDayCash(BigDecimal dayCash) {
		this.dayCash = dayCash;
	}

	public BigDecimal getMinCash() {
		return minCash;
	}

	public void setMinCash(BigDecimal minCash) {
		this.minCash = minCash;
	}

	public BigDecimal getTimesCash() {
		return timesCash;
	}

	public void setTimesCash(BigDecimal timesCash) {
		this.timesCash = timesCash;
	}

	public int getInConfirmTimes() {
		return inConfirmTimes;
	}

	public void setInConfirmTimes(int inConfirmTimes) {
		this.inConfirmTimes = inConfirmTimes;
	}

	public int getOutConfirmTimes() {
		return outConfirmTimes;
	}

	public void setOutConfirmTimes(int outConfirmTimes) {
		this.outConfirmTimes = outConfirmTimes;
	}

	public static String getCoinparam() {
		return coinParam;
	}

    public boolean isCanCharge() {
        return canCharge;
    }

    public void setCanCharge(boolean canCharge) {
        this.canCharge = canCharge;
    }

    public boolean isCanWithdraw() {
        return canWithdraw;
    }

    public void setCanWithdraw(boolean canWithdraw) {
        this.canWithdraw = canWithdraw;
    }

	public int getSerNum() {
		return serNum;
	}

	public void setSerNum(int serNum) {
		this.serNum = serNum;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}
}
