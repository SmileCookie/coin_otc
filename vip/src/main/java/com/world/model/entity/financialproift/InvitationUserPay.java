package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

public class InvitationUserPay extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*主键*/
	private int id;
	/*划出人userId*/
	private int payUserId;
	/*划出人userName*/
	private String payUserName;
	/*接收人userId*/
	private int acceptUserId;
	/*接收人userName*/
	private String acceptUserName;
	/*处理状态0默认，1成功，2失败*/
	private int dealState;
	/*处理备注*/
	private String dealRemark;
	/*处理时间*/
	private String dealTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPayUserId() {
		return payUserId;
	}
	public void setPayUserId(int payUserId) {
		this.payUserId = payUserId;
	}
	public String getPayUserName() {
		return payUserName;
	}
	public void setPayUserName(String payUserName) {
		this.payUserName = payUserName;
	}
	public int getAcceptUserId() {
		return acceptUserId;
	}
	public void setAcceptUserId(int acceptUserId) {
		this.acceptUserId = acceptUserId;
	}
	public String getAcceptUserName() {
		return acceptUserName;
	}
	public void setAcceptUserName(String acceptUserName) {
		this.acceptUserName = acceptUserName;
	}
	public int getDealState() {
		return dealState;
	}
	public void setDealState(int dealState) {
		this.dealState = dealState;
	}
	public String getDealRemark() {
		return dealRemark;
	}
	public void setDealRemark(String dealRemark) {
		this.dealRemark = dealRemark;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
}
