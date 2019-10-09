package com.world.model.dao.user;

import java.sql.Timestamp;

import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.RecommendSet;
import com.world.util.date.TimeUtil;

public class RecommendSetDao  extends MongoDao<RecommendSet, Long>{

	
	public Double getRecharge() {
		Double d = 0D;
		try {
			RecommendSet rs = findOne(getQuery());
			if (null != rs) {
				d = rs.getRecharge();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return d;
	}
	
	public Double getRecommendRegister() {
		Double d = 0D;
		try {
			RecommendSet rs = findOne(getQuery());
			if (null != rs) {
				d = rs.getRecommendRegister();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return d;
	}
	
	public Double getRegister() {
		Double d = 0D;
		try {
			RecommendSet rs = findOne(getQuery());
			if (null != rs) {
				d = rs.getRegister();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return d;
	}
	
	public Double getRecommendRecharge() {
		Double d = 0D;
		try {
			RecommendSet rs = findOne(getQuery());
			if (null != rs) {
				d = rs.getRecommendRecharge();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return d;
	}
	
	public Double getActivityRecharge() {
		Double d = 0D;
		try {
			RecommendSet rs = findOne(getQuery());
			if (null != rs && rs.getActivityStatus()) {
				d = rs.getRecommendRecharge();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return d;
	}
	
	public Double getActivityRechargeGiveEth(double money) {
		Double d = 0D;
		try {
			Timestamp startTime = Timestamp.valueOf("2016-04-01 00:00:00");
			Timestamp endTime = Timestamp.valueOf("2016-04-11 00:00:00");
			Timestamp curTime = TimeUtil.getNow();
			if (startTime.compareTo(curTime) <= 0 && endTime.compareTo(curTime) >= 0) {
				RecommendSet rs = findOne(getQuery());
				if (null != rs && money >= rs.getActivityRechargeCnyEnough()) {
					d = rs.getActivityRechargeEth();
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return d;
	}
}
