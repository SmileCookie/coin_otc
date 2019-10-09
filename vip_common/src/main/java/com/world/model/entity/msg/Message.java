package com.world.model.entity.msg;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
@Entity
public class Message extends StrBaseLongIdEntity{
	private static final long serialVersionUID = -2386115543279873656L;

	
	public Message(){
		
	}
	
	public Message(Datastore ds){
		super(ds);
	}
	private String from;
	private String to;
	private String toAddress;//目标地址，比如email地址，qq地址、微信地址、手机地址等
	private String language;//所在市场，比如不同国家的手机采取不容的发送方式
	private int type;//类型，1、站内信   2、email 3、手机  4、微信    ... 1020代表站内信和email两种同时发送这样可以满足最多100种发送方式的任意组合
	private Timestamp sendTime;
	private int status;//状态，0 原始 1 已发 2已读 3已回复

	private Timestamp readTime;
	private String replyId;//回复id
	private Boolean allowReply;//是否允许回复
	private Boolean isDelOfFrom;
	private Boolean isDelOfTo;
	private String title;
	private String body;
	private Integer msgType;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	
   

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Boolean getAllowReply() {
		return allowReply;
	}
	public void setAllowReply(Boolean allowReply) {
		this.allowReply = allowReply;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String lan) {
		this.language = lan;
	}
	public long getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Timestamp getSendTime() {
		return sendTime;
	}
	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}
	public Timestamp getReadTime() {
		return readTime;
	}
	public void setReadTime(Timestamp readTime) {
		this.readTime = readTime;
	}
	public String getReplyId() {
		return replyId;
	}
	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}
	public Boolean getIsDelOfFrom() {
		return isDelOfFrom;
	}
	public void setIsDelOfFrom(Boolean isDelOfFrom) {
		this.isDelOfFrom = isDelOfFrom;
	}
	public Boolean getIsDelOfTo() {
		return isDelOfTo;
	}
	public void setIsDelOfTo(Boolean isDelOfTo) {
		this.isDelOfTo = isDelOfTo;
	}

	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}
}
