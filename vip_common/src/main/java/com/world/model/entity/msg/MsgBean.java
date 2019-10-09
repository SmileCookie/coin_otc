package com.world.model.entity.msg;

import com.world.data.mysql.Bean;

/****
 * 消息中间类  
 * @author Administrator
 *
 */
public class MsgBean extends Bean{

	private static final long serialVersionUID = 1L;
	
	public MsgBean(){}
	public MsgBean(String userId , String userName,String reason) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.reason = reason;
	}
	
	public MsgBean(String userId , String userName,String reason,String other) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.reason = reason;
		this.other = other;
	}
	
	public MsgBean(String other , String reason) {
		super();
		this.other = other;
		this.reason = reason;
	}
	public MsgBean(String other) {
		super();
		this.other = other;
	}
	
	private String userId;
	private String userName;
	private String reason;
	private String other;
	
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
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
	

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
