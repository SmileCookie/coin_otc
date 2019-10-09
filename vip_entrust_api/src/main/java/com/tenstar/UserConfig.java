package com.tenstar;
import java.io.Serializable;
import java.math.BigDecimal;



/**
 * 用户自动委托控制
 * @author pc 电脑
 *
 */
public class UserConfig implements Serializable{
	
	private static final long serialVersionUID = 16777777L;  
	
	private int isStart = 0;//是否开启自动委
	private int  entrustQuShi=1;//委托-涨势还是跌势"), //1-100说明涨的幅度   -1-100说明跌的幅度
	private int safePriceQuJian=0;//距离成交价之间的距离进行委托
	
		    private int userId=0;//用户id
		    private int webId=8;//网站id

		    private long  entrustQuJian=30;//仅仅在当前价格这个区间内进行委托
		    private BigDecimal entrustMaxSell=BigDecimal.ZERO;//最大同时挂单出售的数量
		    
		    private BigDecimal extrustMaxBuy=BigDecimal.ZERO;//最大同时购买的数量
		    private BigDecimal entrustMaxDangWei=BigDecimal.ZERO;//最大委托单位数量
		    
		    //针对已经买入的订单部分，上涨获利设置可以卖出，如果反向下跌一定额度需要卖出止损
		    private BigDecimal maxUpToSell=BigDecimal.ZERO;//盈利：最大上涨多少会卖掉  针对行情上涨趋势的时候
		    private BigDecimal minUpToSell=BigDecimal.ZERO;//盈利：最少上涨多少会卖  针对行情上涨趋势的时候
	        private BigDecimal maxDownToSell=BigDecimal.ZERO;//止损：最大下跌多少会卖
	        private BigDecimal maxUpPriceSpaceToCancle;//最大上涨多少后还没成交的单子应该取消掉
	        
	        //针对主动做空卖出的部分   下跌趋势的时候会主动做空卖出，卖出后的订单遵循如下规则进行买入补仓，如果上涨一定额度需要止损即使补仓买入
	        private BigDecimal maxDownToBuy=BigDecimal.ZERO;//做空盈利：最低下降多少会买入 针对行情下降趋势的时候  
		    private BigDecimal minDownToBuy=BigDecimal.ZERO;//做空盈利：最少下降多少会卖 针对行情下降趋势的时候
	        private BigDecimal maxUpToBuy=BigDecimal.ZERO;//止损：最大上涨多少会买
	        private BigDecimal maxDownPriceSpaceToCancle;//最大下跌多少后还没成交的单子应该取消掉
	        
	        private int maxTimeToTransNoMoney=0;//最大多长时间如果单子没有成交掉，即使原价也可以交易掉
			private int timeSpace=10;//委托最小时间间隔
			private long AutoId;//自动化id 0新增 1 编辑
			
			private String autoName;//用户自定义的名称 
			private String market;
			public int getIsStart() {
				return isStart;
			}
			public void setIsStart(int isStart) {
				this.isStart = isStart;
			}
			public int getEntrustQuShi() {
				return entrustQuShi;
			}
			public void setEntrustQuShi(int entrustQuShi) {
				this.entrustQuShi = entrustQuShi;
			}
			public int getSafePriceQuJian() {
				return safePriceQuJian;
			}
			public void setSafePriceQuJian(int safePriceQuJian) {
				this.safePriceQuJian = safePriceQuJian;
			}
			public int getUserId() {
				return userId;
			}
			public void setUserId(int userId) {
				this.userId = userId;
			}
			public int getWebId() {
				return webId;
			}
			public void setWebId(int webId) {
				this.webId = webId;
			}
			public long getEntrustQuJian() {
				return entrustQuJian;
			}
			public void setEntrustQuJian(long entrustQuJian) {
				this.entrustQuJian = entrustQuJian;
			}
		
			public int getMaxTimeToTransNoMoney() {
				return maxTimeToTransNoMoney;
			}
			public void setMaxTimeToTransNoMoney(int maxTimeToTransNoMoney) {
				this.maxTimeToTransNoMoney = maxTimeToTransNoMoney;
			}
			public int getTimeSpace() {
				return timeSpace;
			}
			public void setTimeSpace(int timeSpace) {
				this.timeSpace = timeSpace;
			}
			public long getAutoId() {
				return AutoId;
			}
			public void setAutoId(long autoId) {
				AutoId = autoId;
			}
			public String getAutoName() {
				return autoName;
			}
			public void setAutoName(String autoName) {
				this.autoName = autoName;
			}
			public String getMarket() {
				return market;
			}
			public void setMarket(String market) {
				this.market = market;
			}
			public BigDecimal getEntrustMaxSell() {
				return entrustMaxSell;
			}
			public void setEntrustMaxSell(BigDecimal entrustMaxSell) {
				this.entrustMaxSell = entrustMaxSell;
			}
			public BigDecimal getExtrustMaxBuy() {
				return extrustMaxBuy;
			}
			public void setExtrustMaxBuy(BigDecimal extrustMaxBuy) {
				this.extrustMaxBuy = extrustMaxBuy;
			}
			public BigDecimal getEntrustMaxDangWei() {
				return entrustMaxDangWei;
			}
			public void setEntrustMaxDangWei(BigDecimal entrustMaxDangWei) {
				this.entrustMaxDangWei = entrustMaxDangWei;
			}
			public BigDecimal getMaxUpToSell() {
				return maxUpToSell;
			}
			public void setMaxUpToSell(BigDecimal maxUpToSell) {
				this.maxUpToSell = maxUpToSell;
			}
			public BigDecimal getMinUpToSell() {
				return minUpToSell;
			}
			public void setMinUpToSell(BigDecimal minUpToSell) {
				this.minUpToSell = minUpToSell;
			}
			public BigDecimal getMaxDownToSell() {
				return maxDownToSell;
			}
			public void setMaxDownToSell(BigDecimal maxDownToSell) {
				this.maxDownToSell = maxDownToSell;
			}
			public BigDecimal getMaxUpPriceSpaceToCancle() {
				return maxUpPriceSpaceToCancle;
			}
			public void setMaxUpPriceSpaceToCancle(BigDecimal maxUpPriceSpaceToCancle) {
				this.maxUpPriceSpaceToCancle = maxUpPriceSpaceToCancle;
			}
			public BigDecimal getMaxDownToBuy() {
				return maxDownToBuy;
			}
			public void setMaxDownToBuy(BigDecimal maxDownToBuy) {
				this.maxDownToBuy = maxDownToBuy;
			}
			public BigDecimal getMinDownToBuy() {
				return minDownToBuy;
			}
			public void setMinDownToBuy(BigDecimal minDownToBuy) {
				this.minDownToBuy = minDownToBuy;
			}
			public BigDecimal getMaxUpToBuy() {
				return maxUpToBuy;
			}
			public void setMaxUpToBuy(BigDecimal maxUpToBuy) {
				this.maxUpToBuy = maxUpToBuy;
			}
			public BigDecimal getMaxDownPriceSpaceToCancle() {
				return maxDownPriceSpaceToCancle;
			}
			public void setMaxDownPriceSpaceToCancle(BigDecimal maxDownPriceSpaceToCancle) {
				this.maxDownPriceSpaceToCancle = maxDownPriceSpaceToCancle;
			}
			
			

}
