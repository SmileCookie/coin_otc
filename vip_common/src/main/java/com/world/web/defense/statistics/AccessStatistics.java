package com.world.web.defense.statistics;

import com.google.code.morphia.annotations.Id;
import com.world.model.entity.BaseEntity;

/***
 * 访问统计
 * @author apple
 *
 */
public class AccessStatistics extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7497905395830673738L;
	@Id
	private long _id;//分钟值作为id  也是主键
	private long times;//访问次数
	
	public long getTimes() {
		return times;
	}
	public void setTimes(long times) {
		this.times = times;
	}
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
	
}
