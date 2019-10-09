package com.world.model.entity.api;

import java.sql.Timestamp;

import com.world.data.mysql.Bean;

/**
 * api 访问密钥
 * 每个用户只能生成10个密钥对
 * @author guosj
 */
public class ApiKey extends Bean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApiKey() {
		super();
	}
	
	//id
	private long id;
	//用户Id
	private String userId;
	//用户名
	private String userName;
	//ip过滤，多个IP以,号隔开，最终保存格式为[ip1,ip2]
	private String ipaddrs;
	//访问公钥
	private String accesskey;
	//访问私钥
	private String secretkey;
	//是否已经激活
	private int isAct;
	//是否已经锁定
	private int isLock;
	//是否已经删除
	private int isDel;
	//添加时间
	private Timestamp addTime;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIpaddrs() {
		return ipaddrs;
	}
	public void setIpaddrs(String ipaddrs) {
		this.ipaddrs = ipaddrs;
	}
	public String getAccesskey() {
		return accesskey;
	}
	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}
	public String getSecretkey() {
		return secretkey;
	}
	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}
	public int getIsAct() {
		return isAct;
	}
	public void setIsAct(int isAct) {
		this.isAct = isAct;
	}
	public int getIsLock() {
		return isLock;
	}
	public void setIsLock(int isLock) {
		this.isLock = isLock;
	}
	public Timestamp getAddTime() {
		return addTime;
	}
	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}
	public int getIsDel() {
		return isDel;
	}
	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}
}