package com.yc.entity.msg;
/*****
 * 消息发送状态
 * @author Administrator
 *
 */
public enum MsgSendStatus {

	no(0,"未发送"),sending(1,"发送中"),success(2,"发送成功"),fail(3,"发送失败");
	
	private MsgSendStatus(int key, String value) {
		this.key = key;
		this.value = value;
	}
	private int key;
	private String value;
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
