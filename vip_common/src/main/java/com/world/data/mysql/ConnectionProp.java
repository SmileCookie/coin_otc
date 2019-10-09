package com.world.data.mysql;

public class ConnectionProp {
	public ConnectionProp(String ip, String dbName, String dbUserName,String dbPwd , boolean isOk) {
		super();
		this.ip = ip;
		this.dbName = dbName;
		this.dbUserName = dbUserName;
		this.dbPwd = dbPwd;
		this.isOk = isOk;
	}
	public String ip;
	public String dbName;
	public String dbUserName;
	public String dbPwd;
	public boolean isOk;
}
