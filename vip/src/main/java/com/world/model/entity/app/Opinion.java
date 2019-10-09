package com.world.model.entity.app;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;


@Entity(noClassnameStored=true)
public class Opinion extends LongIdEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7954754849968892906L;
	private String content;//
	private String userId;//
	private Date submitDate;//
	private String reply;//
	private Date replyDate;//
	
	public Opinion() {
		super();
	}
	
	public Date getReplyDate() {
		return replyDate;
	}

	public void setReplyDate(Date replyDate) {
		this.replyDate = replyDate;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
}
