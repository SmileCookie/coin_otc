package com.tenstar;
import java.io.Serializable;



/**
 * 机器人委托的相关参数
 * @author pc
 *
 */
public class robotConfig implements Serializable{
	
	private static final long serialVersionUID = 16777777L;  
	
	private int isStart = 0;//是否开启自动委托
	
	private int isStartTrade =0;//是否开启自动成交
	
	private int  entrustQuShi=1;//委托-涨势还是跌势"), //1-100说明涨的幅度   -1-100说明跌的幅度

	private int safePriceQuJian=10;//距离真实买一卖一之间的价差
	private int  entrustRobotQuJian=20;//(101 , "roobot委托-警戒区间"),//比如50，代表当前价格上下50元之外做小幅度变化机动
	
	private long baseShowAdd=0;//显示的成交量增加的数量
	private String market="";

	    //以下是买档 外围配置
		private int numberTotalBuyOut=200;//总数量
		private int maxDangweiBuyOut=20;//最大档位数
		private int lowPriceBuyOut=200;//最低价格
		private int timesSpaceBuyOut=5;//时间间隔,秒钟
		private int splitNumberBuyOut=3;//单次请求的最大分割数量
		private int duiQiBaiFenBiBuyOut=50;//对其的百分比，用户调节价格向中间价靠齐的位置，数字越大月均匀，越小越集中
		
		//以下是卖档 外围配置
		private int numberTotalSellOut=200;//总数量
		private int maxDangweiSellOut=20;//最大档位数
		private int highPriceSellOut=2500;//最高价格
		private int timesSpaceSellOut=1;//时间间隔,秒钟
		private int splitNumberSellOut=3;//单次请求的最大分割数量
		private int duiQiBaiFenBiSellOut=70;//对其的百分比，用户调节价格向中间价靠齐的位置，数字越大月均匀，越小越集中
		
		//以下是买档 内围配置
		private int numberTotalBuyIn=600;//总数量
		private int maxDangweiBuyIn=20;//最大档位数
		private int timesSpaceBuyIn=3;//时间间隔,秒钟
		private int splitNumberBuyIn=3;//单次请求的最大分割数量
		private int duiQiBaiFenBiBuyIn=70;//对其的百分比，用户调节价格向中间价靠齐的位置，数字越大月均匀，越小越集中
		 
		//以下是卖档 内围配置
		private int numberTotalSellIn=600;//总数量
		private int maxDangweiSellIn=20;//最大档位数
		private int timesSpaceSellIn=1;//时间间隔,秒钟
		private int splitNumberSellIn=3;//单次请求的最大分割数量
		private int duiQiBaiFenBiSellIn=60;//对其的百分比，用户调节价格向中间价靠齐的位置，数字越大月均匀，越小越集中
		 

	   //每日的总成交量
	    private long totalTransEveryDay=30000;
	    	
	
		private int timeSpaceTM=20;//每次大概的时间间隔

		
		

		
		public int getIsStartTrade() {
			return isStartTrade;
		}
		public void setIsStartTrade(int isStartTrade) {
			this.isStartTrade = isStartTrade;
		}
		public long getBaseShowAdd() {
			return baseShowAdd;
		}
		public void setBaseShowAdd(long baseShowAdd) {
			this.baseShowAdd = baseShowAdd;
		}
		public int getSafePriceQuJian() {
			return safePriceQuJian;
		}
		public void setSafePriceQuJian(int safePriceQuJian) {
			this.safePriceQuJian = safePriceQuJian;
		}
		public long getTotalTransEveryDay() {
			return totalTransEveryDay;
		}
		public void setTotalTransEveryDay(long totalTransEveryDay) {
			this.totalTransEveryDay = totalTransEveryDay;
		}
		public int getIsStart() {
			return isStart;
		}
		public void setIsStart(int isStart) {
			this.isStart = isStart;
		}

		public int getTimeSpaceTM() {
			return timeSpaceTM;
		}
		public void setTimeSpaceTM(int timeSpaceTM) {
			this.timeSpaceTM = timeSpaceTM;
		}
		public int getDuiQiBaiFenBiBuyOut() {
			return duiQiBaiFenBiBuyOut;
		}
		public void setDuiQiBaiFenBiBuyOut(int duiQiBaiFenBiBuyOut) {
			this.duiQiBaiFenBiBuyOut = duiQiBaiFenBiBuyOut;
		}
		public int getDuiQiBaiFenBiBuyIn() {
			return duiQiBaiFenBiBuyIn;
		}
		public void setDuiQiBaiFenBiBuyIn(int duiQiBaiFenBiBuyIn) {
			this.duiQiBaiFenBiBuyIn = duiQiBaiFenBiBuyIn;
		}
		public int getDuiQiBaiFenBiSellOut() {
			return duiQiBaiFenBiSellOut;
		}
		public void setDuiQiBaiFenBiSellOut(int duiQiBaiFenBiSellOut) {
			this.duiQiBaiFenBiSellOut = duiQiBaiFenBiSellOut;
		}
		public int getDuiQiBaiFenBiSellIn() {
			return duiQiBaiFenBiSellIn;
		}
		public void setDuiQiBaiFenBiSellIn(int duiQiBaiFenBiSellIn) {
			this.duiQiBaiFenBiSellIn = duiQiBaiFenBiSellIn;
		}
		public int getEntrustQuShi() {
			return entrustQuShi;
		}
		public void setEntrustQuShi(int entrustQuShi) {
			this.entrustQuShi = entrustQuShi;
		}

		public int getEntrustRobotQuJian() {
			return entrustRobotQuJian;
		}
		public void setEntrustRobotQuJian(int entrustRobotQuJian) {
			this.entrustRobotQuJian = entrustRobotQuJian;
		}
		public int getNumberTotalBuyOut() {
			return numberTotalBuyOut;
		}
		public void setNumberTotalBuyOut(int numberTotalBuyOut) {
			this.numberTotalBuyOut = numberTotalBuyOut;
		}
		public int getMaxDangweiBuyOut() {
			return maxDangweiBuyOut;
		}
		public void setMaxDangweiBuyOut(int maxDangweiBuyOut) {
			this.maxDangweiBuyOut = maxDangweiBuyOut;
		}
		public int getLowPriceBuyOut() {
			return lowPriceBuyOut;
		}
		public void setLowPriceBuyOut(int lowPriceBuyOut) {
			this.lowPriceBuyOut = lowPriceBuyOut;
		}
		public int getTimesSpaceBuyOut() {
			return timesSpaceBuyOut;
		}
		public void setTimesSpaceBuyOut(int timesSpaceBuyOut) {
			this.timesSpaceBuyOut = timesSpaceBuyOut;
		}
		public int getSplitNumberBuyOut() {
			return splitNumberBuyOut;
		}
		public void setSplitNumberBuyOut(int splitNumberBuyOut) {
			this.splitNumberBuyOut = splitNumberBuyOut;
		}
		public int getNumberTotalBuyIn() {
			return numberTotalBuyIn;
		}
		public void setNumberTotalBuyIn(int numberTotalBuyIn) {
			this.numberTotalBuyIn = numberTotalBuyIn;
		}
		public int getMaxDangweiBuyIn() {
			return maxDangweiBuyIn;
		}
		public void setMaxDangweiBuyIn(int maxDangweiBuyIn) {
			this.maxDangweiBuyIn = maxDangweiBuyIn;
		}
		public int getTimesSpaceBuyIn() {
			return timesSpaceBuyIn;
		}
		public void setTimesSpaceBuyIn(int timesSpaceBuyIn) {
			this.timesSpaceBuyIn = timesSpaceBuyIn;
		}
		public int getSplitNumberBuyIn() {
			return splitNumberBuyIn;
		}
		public void setSplitNumberBuyIn(int splitNumberBuyIn) {
			this.splitNumberBuyIn = splitNumberBuyIn;
		}
		public int getNumberTotalSellOut() {
			return numberTotalSellOut;
		}
		public void setNumberTotalSellOut(int numberTotalSellOut) {
			this.numberTotalSellOut = numberTotalSellOut;
		}
		public int getMaxDangweiSellOut() {
			return maxDangweiSellOut;
		}
		public void setMaxDangweiSellOut(int maxDangweiSellOut) {
			this.maxDangweiSellOut = maxDangweiSellOut;
		}
		public int getHighPriceSellOut() {
			return highPriceSellOut;
		}
		public void setHighPriceSellOut(int highPriceSellOut) {
			this.highPriceSellOut = highPriceSellOut;
		}
		public int getTimesSpaceSellOut() {
			return timesSpaceSellOut;
		}
		public void setTimesSpaceSellOut(int timesSpaceSellOut) {
			this.timesSpaceSellOut = timesSpaceSellOut;
		}
		public int getSplitNumberSellOut() {
			return splitNumberSellOut;
		}
		public void setSplitNumberSellOut(int splitNumberSellOut) {
			this.splitNumberSellOut = splitNumberSellOut;
		}
		public int getNumberTotalSellIn() {
			return numberTotalSellIn;
		}
		public void setNumberTotalSellIn(int numberTotalSellIn) {
			this.numberTotalSellIn = numberTotalSellIn;
		}
		public int getMaxDangweiSellIn() {
			return maxDangweiSellIn;
		}
		public void setMaxDangweiSellIn(int maxDangweiSellIn) {
			this.maxDangweiSellIn = maxDangweiSellIn;
		}
		public int getTimesSpaceSellIn() {
			return timesSpaceSellIn;
		}
		public void setTimesSpaceSellIn(int timesSpaceSellIn) {
			this.timesSpaceSellIn = timesSpaceSellIn;
		}
		public int getSplitNumberSellIn() {
			return splitNumberSellIn;
		}
		public void setSplitNumberSellIn(int splitNumberSellIn) {
			this.splitNumberSellIn = splitNumberSellIn;
		}
		public String getMarket() {
			return market;
		}
		public void setMarket(String market) {
			this.market = market;
		}
		
}
