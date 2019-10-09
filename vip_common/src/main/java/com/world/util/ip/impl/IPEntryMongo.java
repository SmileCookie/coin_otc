package com.world.util.ip.impl;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

/**
 * <pre>
 * 一条IP范围记录，不仅包括国家和区域，也包括起始IP和结束IP
 * </pre>
 */
@Entity(noClassnameStored=true)
public class IPEntryMongo{
	@Id
	public String _id;
    public String beginIp;
    public String endIp;
    public String country;
    public String area;
    
    public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getBeginIp() {
		return beginIp;
	}

	public void setBeginIp(String beginIp) {
		this.beginIp = beginIp;
	}

	public String getEndIp() {
		return endIp;
	}

	public void setEndIp(String endIp) {
		this.endIp = endIp;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	/**
     * 构造函数
     */
    public IPEntryMongo() {
        beginIp = endIp = country = area = "";
    }
}
