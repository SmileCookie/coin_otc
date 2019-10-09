package com.world.model.entity.pay.fee;

import com.world.model.entity.SysEnum;

/*****
 * 网站费率详情
 * @author Administrator
 * 
 * 此类对应如下费率说明
 *********
 *********
 * 
 * 			基础版	专业版	vip版
项目			0手续费	比特权分红	分红+极速
现金	人民币充值	在线网银	0.5%，最低1元	0	0
		财付通转账	0	0	0
		支付宝转账	0	0	0
		银行卡转账	0	0	0
		淘宝充值	0	0	0
	人民币提现	15分钟极速到银行卡	1%，最低1元	0.4%（政策上超时赔付）	0
		24小时普通提现到银行卡	0	0	0
		提现到财付通	0.5%，最低1元，单笔最高20000	0.40%	0
		体现到支付宝	1%，最低1元	0.50%	0
	比特币充值	比特币充值	5个确认 手续费0	1个确认 手续费0	1个确认 手续费0
	比特币提现	比特币提现	网络扣除 0.0001，网站不收	网站贴补0.00001网络手续费	网站贴补0.00001网络手续费
交易	交易	买入	0	0.2%比特币+分红权	0.15%比特币+分红权
		卖出	0	0.2%资金+分红权	0.15%比特币+分红权
		单日交易超过1比特币	0	0.01分红权/日	0.02分红权/日
		连续2日交易超过1比特币	0	0.02分红权/日	0.04分红权/日
		连续3日交易超过1比特币	0	0.03分红权/日	0.06分红权/日
		连续4日交易超过1比特币	0	0.04分红权/日	0.07分红权/日
		连续5日交易超过1比特币	0	0.05分红权/日	0.1分红权/日
		连续6日交易超过1比特币	0	0.06分红权/日	0.12分红权/日
		连续7日交易超过1比特币及以上	0	0.07分红权/日	0.14分红权/日
借贷	借贷	借贷	0.2%每日 3倍杠杆	0.15%每日  4倍杠杆	0.1% 5倍杠杆

 * 
 * 
 *********
 *********
 */
public enum FeeDetails implements SysEnum{  
	///////1索引   2说明                     3类型                              4普通版费率    5普通版最小  6 普通版限额    7普通版确认次数                  8专业版费率         9专业版最小  10专业版限额    11专业版确认次数       12vip版费率        13vip版最小  14vip限额    15vip版确认次数
	online(1 , "在线网银" , FeeType.rmbIn ,   	0 ,         0,           0 ,          0             ,  0 ,              0 ,        0 ,            1 ,           0 ,           0,         0 ,           1),
	cftIn(2 , "财付通转账" , FeeType.rmbIn ,                                       0 , 0 , 0 , 0         , 0 , 0 , 0 , 0           , 0 , 0 , 0 , 0),
	zfbIn(3 , "支付宝转账" , FeeType.rmbIn ,                                       0 , 0 , 0 , 0         , 0 , 0 , 0 , 0           , 0 , 0 , 0 , 0),
	bankCardIn(4 , "银行卡转账" , FeeType.rmbIn ,                                  0 , 0 , 0 , 0         , 0 , 0 , 0 , 0           , 0 , 0 , 0 , 0),
	taoBaoIn(5 , "淘宝充值" , FeeType.rmbIn ,                                      0 , 0 , 0 , 0         , 0 , 0 , 0 , 0           , 0 , 0 , 0 , 0),
	
	
	js15Min(11 , "2小时极速到银行卡" , FeeType.rmbOut ,                            0.004 , 1 , 49999 , 0         , 0.003 , 1 , 0 , 0           , 0.0025 , 1 , 0 , 0),
	commonReachCard(12 , "24小时普通提现到银行卡" , FeeType.rmbOut ,               0.004 , 100 , 49999 , 0         , 0.003 , 1 , 0 , 0           , 0.0025 , 1 , 0 , 0),
	reachToCft(13 , "提现到财付通" , FeeType.rmbOut ,                               0.005 , 1 , 20000 , 0         , 0.004 , 1 , 0 , 0           , 0.003 , 1 , 0 , 0),
	rechToZfb(14 , "提现到支付宝" , FeeType.rmbOut ,                                0.01 , 1 , 20000 , 0         , 0.004 , 1 , 0 , 0           , 0.003 , 1 , 0 , 0),
	rechToTb(15 , "提现到淘宝" , FeeType.rmbOut ,                                0.005 , 1 , 20000 , 0         , 0.004 , 1 , 0 , 0           , 0.003 , 1 , 0 , 0),
	
	
	btcIn(21 , "比特币充值" , FeeType.btcIn ,                                      0 , 0 , 0 , 3         , 0 , 0 , 0 , 1           , 0 , 0 , 0 , 1),
	btcOut(22 , "比特币提现" , FeeType.btcOut ,                                    0.0002 , 0.001 , 20 , 0         , 0.0002 , 0.001 , 0 , 0           , 0 , 0 , 0 , 0),

	ltcIn(24 , "莱特币充值" , FeeType.ltcIn ,                                      0 , 0 , 0 , 3         , 0 , 0 , 0 , 1           , 0 , 0 , 0 , 1),
	ltcOut(25 , "莱特币提现" , FeeType.ltcOut ,                                    0.002 , 0.01 , 2000 , 0         , 0.002 , 0.01 , 0 , 0           , 0 , 0 , 0 , 0),

	dogeIn(27 , "狗币充值" , FeeType.ltcIn ,                                      0 , 0 , 0 , 3         , 0 , 0 , 0 , 1           , 0 , 0 , 0 , 1),
	dogeOut(28 , "狗币提现" , FeeType.ltcOut ,                                    0 , 100 , 10000 , 0         , 0 , 100 , 0 , 0           , 0 , 0 , 0 , 0),
	
	ethIn(29 , "以太坊充值" , FeeType.ethIn ,                                      0 , 0 , 0 , 3         , 0 , 0 , 0 , 1           , 0 , 0 , 0 , 1),
	ethOut(30 , "以太坊提现" , FeeType.ethOut ,                                    0.00 , 0.1 , 100 , 0         , 0.00 , 100 , 0 , 0           , 0 , 0 , 0 , 0),
	
	ethcIn(100 , "Ethereum Classic充值" , FeeType.ethcIn ,                                      0 , 0 , 0 , 3         , 0 , 0 , 0 , 1           , 0 , 0 , 0 , 1),
	ethcOut(101 , "Ethereum Classic提现" , FeeType.ethcOut ,                                    0.00 , 0.1 , 100 , 0         , 0.00 , 100 , 0 , 0           , 0 , 0 , 0 , 0),
	
	
	buy(31 , "买入" , FeeType.trade ,                                              0 , 0 , 0 , 0         , 0.002 , 0 , 0 , 0           , 0 , 0 , 0 , 0),
	sell(32 , "卖出" , FeeType.trade ,                                             0 , 0 , 0 , 0         , 0.002 , 0 , 0 , 0           , 0 , 0 , 0 , 0),
	than1OneDay(33 , "单日交易超过1比特币" , FeeType.trade ,                        0 , 0 , 0 , 0         , 0.01 , 0 , 0 , 0           , 0.02 , 0 , 0 , 0),
	than1TwoDay(34 , "连续2日交易超过1比特币" , FeeType.trade ,                     0 , 0 , 0 , 0         , 0.02 , 0 , 0 , 0           , 0.04 , 0 , 0 , 0),
	than1ThreeDay(35 , "连续3日交易超过1比特币" , FeeType.trade ,                   0 , 0 , 0 , 0         , 0.03 , 0 , 0 , 0           , 0.06 , 0 , 0 , 0),
	than1FourDay(36 , "连续4日交易超过1比特币" , FeeType.trade ,                    0 , 0 , 0 , 0         , 0.04 , 0 , 0 , 0           , 0.07 , 0 , 0 , 0),
	than1FiveDay(37 , "连续5日交易超过1比特币" , FeeType.trade ,                    0 , 0 , 0 , 0         , 0.05 , 0 , 0 , 0           , 0.01 , 0 , 0 , 0),
	than1SixDay(38 , "连续6日交易超过1比特币" , FeeType.trade ,                     0 , 0 , 0 , 0         , 0.06 , 0 , 0 , 0           , 0.12 , 0 , 0 , 0),
	than1SevenDay(39 , "连续7日交易超过1比特币" , FeeType.trade ,                   0 , 0 , 0 , 0         , 0.07 , 0 , 0 , 0           , 0.14 , 0 , 0 , 0),
	
	buyLtc(61 , "买入LTC" , FeeType.trade ,                                              0 , 0 , 0 , 0         , 0.002 , 0 , 0 , 0           , 0 , 0 , 0 , 0),
	sellLtc(62 , "卖出LTC" , FeeType.trade ,                                             0 , 0 , 0 , 0         , 0.002 , 0 , 0 , 0           , 0 , 0 , 0 , 0),
	
	
	loan(51 , "借贷" , FeeType.loan ,                                              0.2 , 0 , 0 , 3         , 0.15 , 0 , 0 , 4           , 0.1 , 0 , 0 , 5)
	;
	
	private FeeDetails(int key, String value, FeeType feeType , 
			double ptFee , double ptMin , double ptEd , int ptConfirmTimes , 
			double zyFee , double zyMin , double zyEd , int zyConfirmTimes ,
			double vip1Fee , double vip1Min , double vip1Ed , int vip1ConfirmTimes) {
		this.key = key;
		this.value = value;
		this.feeType = feeType;
		
		this.ptFee = ptFee;
		this.ptMin = ptMin;
		this.ptEd = ptEd;
		this.ptConfirmTimes = ptConfirmTimes;
		
		this.zyFee = zyFee;
		this.zyMin = zyMin;
		this.zyEd = zyEd;
		this.zyConfirmTimes = zyConfirmTimes;
		
		this.vip1Fee = vip1Fee;
		this.vip1Min = vip1Min;
		this.vip1Ed = vip1Ed;
		this.vip1ConfirmTimes = vip1ConfirmTimes;
	}
	
	private int key;//索引
	private String value;//说明
	private FeeType feeType;//大类
	
	private double ptFee;//费率
	private double ptMin;//最小
	private double ptEd;//普通版额度最大
	private int ptConfirmTimes;//确认次数
	
	private double zyFee;//费率
	private double zyMin;//最小
	private double zyEd;//普通版额度最大
	private int zyConfirmTimes;//确认次数
	
	private double vip1Fee;//费率
	private double vip1Min;//最小
	private double vip1Ed;//普通版额度最大
	private int vip1ConfirmTimes;//确认次数
	
	/****
	 * 获取费率
	 * @param version
	 * @return
	 */
	public double getFeeByVersion(int version){
		if(version == 0){
			return ptFee;
		}else if(version == 1){
			return zyFee;
		}else if(version == 2){
			return vip1Fee;
		}
		return 0;
	}
	/***
	 * 获取最小
	 * @param version
	 * @return
	 */
	public double getMinByVersion(int version){
		if(version == 0){
			return ptMin;
		}else if(version == 1){
			return zyMin;
		}else if(version == 2){
			return vip1Min;
		}
		return 0;
	}
	/***
	 * 获取额度
	 * @param version
	 * @return
	 */
	public double getEdByVersion(int version){
		if(version == 0){
			return ptEd;
		}else if(version == 1){
			return zyEd;
		}else if(version == 2){
			return vip1Ed;
		}
		return 0;
	}
	/****
	 * 获取确认次数    或者  杠杆倍数
	 * @param version
	 * @return
	 */
	public double getConfirmTimesByVersion(int version){
		if(version == 0){
			return ptConfirmTimes;
		}else if(version == 1){
			return zyConfirmTimes;
		}else if(version == 2){
			return vip1ConfirmTimes;
		}
		return 0;
	}
	
	
	public double getZyFee() {
		return zyFee;
	}
	public double getZyMin() {
		return zyMin;
	}
	public double getZyEd() {
		return zyEd;
	}
	public int getZyConfirmTimes() {
		return zyConfirmTimes;
	}
	public double getVip1Fee() {
		return vip1Fee;
	}
	public double getVip1Min() {
		return vip1Min;
	}
	public double getVip1Ed() {
		return vip1Ed;
	}
	public int getVip1ConfirmTimes() {
		return vip1ConfirmTimes;
	}
	public int getPtConfirmTimes() {
		return ptConfirmTimes;
	}
	public double getPtEd() {
		return ptEd;
	}
	public double getPtFee() {
		return ptFee;
	}
	public double getPtMin() {
		return ptMin;
	}
	public int getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	
	public FeeType getFeeType() {
		return feeType;
	}
	
}
