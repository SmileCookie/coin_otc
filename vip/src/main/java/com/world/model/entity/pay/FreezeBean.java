package com.world.model.entity.pay;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.world.data.mysql.Bean;
import com.world.model.entity.user.User;

public class FreezeBean extends Bean{
	private static final long serialVersionUID = 1L;
	private long freezeId; 
	private String userId; 
	private String userName; 
	private Timestamp freezeTime; 

	private int type; 
	private BigDecimal btcNumber; 
	private int statu;
	private long connectionId;
	private String reMark;

	private BigDecimal freezeBanlance;
	
	private String freezShow;
	private String unFreezShow;
	
	private User user;
	
	public String getStatShow() {
		String statShow = "-";
		if(statu == 1){
			statShow="<font color=\"green\">已解冻</font>";
		}else{
			statShow="<font color=\"red\">未解冻</font>";
		}
		return statShow;
	}
	DecimalFormat df = new DecimalFormat("0.00####");
	public String getFreezShow() {
	    if(statu == 0){
	    	freezShow = "<font class='green'>+" + df.format(freezeBanlance.doubleValue()) + "</font>";
	    }else{
	    	freezShow = "<font class='orange'>-" + df.format(freezeBanlance.doubleValue()) + "</font>";
	    }
		return freezShow;
	}

	public String getUnFreezShow() {
		if(statu == 1){
			unFreezShow=df.format(freezeBanlance.doubleValue());
	    }else{
	    	unFreezShow="—";
	    }
		return unFreezShow;
	}
	
	public BigDecimal getFreez(){
		return freezeBanlance;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public FreezeBean(){
		 
	}
	/**
	 * 获取btc冻结相关sql
	 * @param user_Id 用户id
	 * @param user_Name 用户名
	 * @param des 描述
	 * @param type 类型
	 * @param btc 大的btc多少
	 * @param freezId 冻结id
	 * @param connection_Id 连接id
	 */
	public FreezeBean(String user_Id,String user_Name, String reMark, int type,BigDecimal btc,long freezId,long connection_Id){
		this.userId=user_Id;
		this.userName=user_Name;
		this.reMark=reMark;
		this.type=type;
		this.btcNumber=btc;
		this.freezeId=freezId;
		this.connectionId=connection_Id;
	}
    
	public BigDecimal getFreezeBanlance() {
		return freezeBanlance;
	}
	public void setFreezeBanlance(BigDecimal freezeBanlance) {
		this.freezeBanlance = freezeBanlance;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Timestamp getFreezeTime() {
		return freezeTime;
	}
	public void setFreezeTime(Timestamp freezeTime) {
		this.freezeTime = freezeTime;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public BigDecimal getBtcNumber() {
		return btcNumber;
	}
	public void setBtcNumber(BigDecimal btcNumber) {
		this.btcNumber = btcNumber;
	}
	public int getStatu() {
		return statu;
	}
	public void setStatu(int statu) {
		this.statu = statu;
	}
	public long getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}
	public String getReMark() {
		return reMark;
	}
	public void setReMark(String reMark) {
		this.reMark = reMark;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getFreezeId() {
		return freezeId;
	}

	public void setFreezeId(long freezeId) {
		this.freezeId = freezeId;
	}
}
