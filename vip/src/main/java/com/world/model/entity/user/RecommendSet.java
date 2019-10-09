package com.world.model.entity.user;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;

@Entity(noClassnameStored = true)
public class RecommendSet extends LongIdEntity {

	private Double register;// 注册奖励，固定额
	private Double recommendRegister;// 推荐注册奖励，固定额
	private Double recharge;// 首次充值奖励，固定额
	private Double recommendRecharge;// 推荐注册奖励，充值额比例，如0.1%
	private Boolean activityStatus = false;// 充值活动是否开启
	private Double activityRecharge;// 充值活动奖励，充值额比例，如0.1%
	private Double activityRechargeCnyEnough;// 活动充值超过多少钱就送activityRechargeEth个以太币
	private Double activityRechargeEth;// 活动奖励以太币
	
	public Double getRegister() {
		return register;
	}
	public void setRegister(Double register) {
		this.register = register;
	}
	public Double getRecommendRegister() {
		return recommendRegister;
	}
	public void setRecommendRegister(Double recommendRegister) {
		this.recommendRegister = recommendRegister;
	}
	public Double getRecharge() {
		return recharge;
	}
	public void setRecharge(Double recharge) {
		this.recharge = recharge;
	}
	public Double getRecommendRecharge() {
		return recommendRecharge;
	}
	public void setRecommendRecharge(Double recommendRecharge) {
		this.recommendRecharge = recommendRecharge;
	}
	public Boolean getActivityStatus() {
		return activityStatus;
	}
	public void setActivityStatus(Boolean activityStatus) {
		this.activityStatus = activityStatus;
	}
	public Double getActivityRecharge() {
		return activityRecharge;
	}
	public void setActivityRecharge(Double activityRecharge) {
		this.activityRecharge = activityRecharge;
	}
	public Double getActivityRechargeEth() {
		return activityRechargeEth;
	}
	public void setActivityRechargeEth(Double activityRechargeEth) {
		this.activityRechargeEth = activityRechargeEth;
	}
	public Double getActivityRechargeCnyEnough() {
		return activityRechargeCnyEnough;
	}
	public void setActivityRechargeCnyEnough(Double activityRechargeCnyEnough) {
		this.activityRechargeCnyEnough = activityRechargeCnyEnough;
	}
	
	
}
