package com.tenstar;

import java.io.Serializable;

/**
 * 自动交易
 * @author pc
 *
 */
public class AutoBean implements Serializable{
	
	private static final long serialVersionUID = 199786878L;  
	
        private int isStart = 0;//是否开启机器人 0未开启
	    private int userId;//用户id
	    private int webId;//网站id
	    private int  entrustQuShi=1;//委托-涨势还是跌势"), //1-100说明涨的幅度   -1-100说明跌的幅度
	    private long  entrustQuJian=30;//仅仅在当前价格这个区间内进行委托
	    private long entrustMaxSell=0;//最大同时挂单出售的数量
	    
	    private long extrustMaxBuy=0;//最大同时购买的数量
	    
	    private long entrustMaxDangWei=0;//最大卖出档位数量
	    
	    
	    
	    //针对已经买入的订单部分，上涨获利设置可以卖出，如果反向下跌一定额度需要卖出止损
	    private long maxUpToSell=0;//盈利：最大上涨多少会卖掉  针对行情上涨趋势的时候
	    private long minUpToSell=0;//盈利：最少上涨多少会卖  针对行情上涨趋势的时候
        private long maxDownToSell=0;//止损：最大下跌多少会卖
       
        private long maxUpPriceSpaceToCancle;//最大上涨多少后还没成交的单子应该取消掉
        
        
        //针对主动做空卖出的部分   下跌趋势的时候会主动做空卖出，卖出后的订单遵循如下规则进行买入补仓，如果上涨一定额度需要止损即使补仓买入
        private long maxDownToBuy=0;//做空盈利：最低下降多少会买入 针对行情下降趋势的时候  
	    private long minDownToBuy=0;//做空盈利：最少下降多少会卖 针对行情下降趋势的时候
        private long maxUpToBuy=0;//止损：最大上涨多少会买
        private long maxDownPriceSpaceToCancle;//最大下跌多少后还没成交的单子应该取消掉
        
        
        
        
        private int maxTimeToTransNoMoney=0;//最大多长时间如果单子没有成交掉，即使原价也可以交易掉
		private int timeSpace=10;//委托最小时间间隔
		
		private long AutoId;//自动化id 0新增 1 编辑
		
		private String autoName;//用户自定义的名称
		
		
		
		public long getEntrustMaxDangWei() {
			return entrustMaxDangWei;
		}
		public void setEntrustMaxDangWei(long entrustMaxDangWei) {
			this.entrustMaxDangWei = entrustMaxDangWei;
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
		public static long getSerialversionuid() {
			return serialVersionUID;
		}
		public int getIsStart() {
			return isStart;
		}
		public void setIsStart(int isStart) {
			this.isStart = isStart;
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
		public int getEntrustQuShi() {
			return entrustQuShi;
		}
		public void setEntrustQuShi(int entrustQuShi) {
			this.entrustQuShi = entrustQuShi;
		}
		public long getEntrustQuJian() {
			return entrustQuJian;
		}
		public void setEntrustQuJian(long entrustQuJian) {
			this.entrustQuJian = entrustQuJian;
		}
		public long getEntrustMaxSell() {
			return entrustMaxSell;
		}
		public void setEntrustMaxSell(long entrustMaxSell) {
			this.entrustMaxSell = entrustMaxSell;
		}
		public long getExtrustMaxBuy() {
			return extrustMaxBuy;
		}
		public void setExtrustMaxBuy(long extrustMaxBuy) {
			this.extrustMaxBuy = extrustMaxBuy;
		}
		
		public long getMaxUpToSell() {
			return maxUpToSell;
		}
		public void setMaxUpToSell(long maxUpToSell) {
			this.maxUpToSell = maxUpToSell;
		}
		public long getMinUpToSell() {
			return minUpToSell;
		}
		public void setMinUpToSell(long minUpToSell) {
			this.minUpToSell = minUpToSell;
		}
		public long getMaxDownToSell() {
			return maxDownToSell;
		}
		public void setMaxDownToSell(long maxDownToSell) {
			this.maxDownToSell = maxDownToSell;
		}
		public long getMaxUpPriceSpaceToCancle() {
			return maxUpPriceSpaceToCancle;
		}
		public void setMaxUpPriceSpaceToCancle(long maxUpPriceSpaceToCancle) {
			this.maxUpPriceSpaceToCancle = maxUpPriceSpaceToCancle;
		}
		public long getMaxDownToBuy() {
			return maxDownToBuy;
		}
		public void setMaxDownToBuy(long maxDownToBuy) {
			this.maxDownToBuy = maxDownToBuy;
		}
		public long getMinDownToBuy() {
			return minDownToBuy;
		}
		public void setMinDownToBuy(long minDownToBuy) {
			this.minDownToBuy = minDownToBuy;
		}
		public long getMaxUpToBuy() {
			return maxUpToBuy;
		}
		public void setMaxUpToBuy(long maxUpToBuy) {
			this.maxUpToBuy = maxUpToBuy;
		}
		public long getMaxDownPriceSpaceToCancle() {
			return maxDownPriceSpaceToCancle;
		}
		public void setMaxDownPriceSpaceToCancle(long maxDownPriceSpaceToCancle) {
			this.maxDownPriceSpaceToCancle = maxDownPriceSpaceToCancle;
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
		
		
		
		
		
		
		
		
}
