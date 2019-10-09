package com.world.model.entity.account;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@Entity(noClassnameStored = true)
public class SendEmaillAccount extends StrBaseLongIdEntity{
	public SendEmaillAccount() {
		this(null);
	}
	
	public SendEmaillAccount(Datastore ds) {
		super(ds);
	}
	private  String sendName;
	private  String fromAddr;
	private  String mailServerHost="smtp.exmail.qq.com";
	private  String mailServerPort="25";
	private  String emailUserName="";
	private  String emailPassword="";
	
	private Timestamp ldate;//日期
	private int status;//状态 1 可用 0不可用
	
	public Timestamp getLdate() {
		return ldate;
	}
	public void setLdate(Timestamp ldate) {
		this.ldate = ldate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getFromAddr() {
		return fromAddr;
	}
	public void setFromAddr(String fromAddr) {
		this.fromAddr = fromAddr;
	}
	public String getMailServerHost() {
		return mailServerHost;
	}
	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}
	public String getMailServerPort() {
		return mailServerPort;
	}
	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}
	public String getEmailUserName() {
		return emailUserName;
	}
	public void setEmailUserName(String emailUserName) {
		this.emailUserName = emailUserName;
	}
	public String getEmailPassword() {
		return emailPassword;
	}
	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
}
