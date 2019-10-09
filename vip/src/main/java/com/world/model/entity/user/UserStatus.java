package com.world.model.entity.user;

public enum UserStatus {
	NoSet(0,"未设置"),
	Locked(-2,"被锁定"),//资金密码被锁定
	AuthError(-1,"认证失败"),//密码错误
	AuthSUCCEES(1,"认证成功");//密码正确

	private UserStatus(int _id,String txt){
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
