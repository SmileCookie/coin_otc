package com.world.model.entity.ip;

import com.world.data.mysql.Bean;
import com.world.data.mysql.bean.BeanField;

public class IpBean extends Bean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3348271186802148900L;
	
	private int id;
	private String ip;
	@BeanField(persistence = false)
	private int number;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

}
