package com.file.server;

/****
 * tomcatserver
 * @author Administrator
 *
 */
public class TomcatServer {

	private int id;
	private int sgId;//组id
	private String ip;//机器IP
	private String domain;//域
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSgId() {
		return sgId;
	}
	public void setSgId(int sgId) {
		this.sgId = sgId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
}
