package com.tenstar;
import java.io.Serializable;



/**
 * 机器人委托的相关参数
 * @author pc
 *
 */
public class RobotShualiangConfig implements Serializable{
	
	private static final long serialVersionUID = 16777777L;  
	
	private int isStart = 0;	//是否启动：默认值0 ，选择范围：0 1
	private double rate = 1.0;	//刷量打折
	private int ktype;	//	k线类型
	private double dfAmount;	//默认数量
	//波动值
	private int startWave;
	
	//波动值
	private int endWave;
	
	private int webId;
	
	private String market;
	
	public int getIsStart() {
		return isStart;
	}
	public void setIsStart(int isStart) {
		this.isStart = isStart;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public double getDfAmount() {
		return dfAmount;
	}
	public void setDfAmount(double dfAmount) {
		this.dfAmount = dfAmount;
	}
	public int getKtype() {
		return ktype;
	}
	public void setKtype(int ktype) {
		this.ktype = ktype;
	}
	public int getWebId() {
		return webId;
	}
	public void setWebId(int webId) {
		this.webId = webId;
	}
	public int getStartWave() {
		if(startWave == 0) startWave = 15;
		return startWave;
	}
	public void setStartWave(int startWave) {
		this.startWave = startWave;
	}
	public int getEndWave() {
		if(endWave == 0) endWave = 30;
		return endWave;
	}
	public void setEndWave(int endWave) {
		this.endWave = endWave;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	
}
