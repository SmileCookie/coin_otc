package com.world.web.action;

/****
 * 处理带有提示的返回值方法
 * @author Administrator
 */
public class ReturnMethodWithTips {

	public ReturnMethodWithTips() {
		super();
	}
	
	public ReturnMethodWithTips(boolean isSuc, String tips) {
		super();
		this.isSuc = isSuc;
		this.tips = tips;
	}
	
	private boolean isSuc;//方法中的业务逻辑是否执行成功
	private String tips;//方法执行后的提示信息
	private String data = "";//其他相关数据
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isSuc() {
		return isSuc;
	}
	public void setSuc(boolean isSuc) {
		this.isSuc = isSuc;
	}
	public String getTips() {
		return tips;
	}
	public void setTips(String tips) {
		this.tips = tips;
	}
}
