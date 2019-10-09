package com.tenstar;
import java.io.Serializable;



/**
 * 机器人委托的相关参数
 * @author pc
 *
 */
public class RobotEntrustConfig implements Serializable{
	
	private static final long serialVersionUID = 16777777L;  
	
	private int isStart = 0;//是否启动：默认值0 ，选择范围：0 1
	private int qujianMaxNum = 100;	//每个区间最多投放数量：默认值100 ，选择范围：0 300   100代表1btc
	private int overNum = 5;	//比如填充2650跟2670区间 买单时，在2670挂5个叠加的0.01-0.1之间的价格，形成成交一大片的感觉） 默认值5，选择范围：0-20
	private int qujianDifference = 20;	//价格区间最小差额：默认值：20，选择范围1-100；20代表2元，当两个价格超过这个价格时候会委托
	private int qujianCancel = 30;		//撤销区间：默认值：30元，大于或者小于30元的小额委托会被撤销，同时定时器也在这个区间内部进行搜索并委托
	private int userId = 0;		//委托用户id号
	private int webId = 0;
	private int dangwei = 30;	//每次操作档位
	private String market="";
	public int getIsStart() {
		return isStart;
	}
	public void setIsStart(int isStart) {
		this.isStart = isStart;
	}
	public int getQujianMaxNum() {
		return qujianMaxNum;
	}
	public void setQujianMaxNum(int qujianMaxNum) {
		this.qujianMaxNum = qujianMaxNum;
	}
	public int getOverNum() {
		return overNum;
	}
	public void setOverNum(int overNum) {
		this.overNum = overNum;
	}
	public int getQujianDifference() {
		return qujianDifference;
	}
	public void setQujianDifference(int qujianDifference) {
		this.qujianDifference = qujianDifference;
	}
	public int getQujianCancel() {
		return qujianCancel;
	}
	public void setQujianCancel(int qujianCancel) {
		this.qujianCancel = qujianCancel;
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
	public int getDangwei() {
		return dangwei;
	}
	public void setDangwei(int dangwei) {
		this.dangwei = dangwei;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	
}
