package com.world.model.dao.msg;
//信息类形状
public enum msgType {
	note(1,"站内信"),
	email(2,"email"),
	weixin(3,"微信"),
	mobelCn(4,"中国手机"),
	mobelUs(5,"美国手机"),
	mobelJp(6,"日本手机"),
	mobelEn(7,"英国手机"),
	mobelFr(8,"法国手机"),
	mobelDe(8,"德国手机手机");

	private msgType(int _id,String txt){
		this.id=_id;
		this.statu=txt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStatu() {
		return statu;
	}
	public void setStatu(String statu) {
		this.statu = statu;
	}
	private int id;
	private String statu;
}
