package com.yc.entity.sxb;
/*****
 * 资产凭证处理状态
 * @author guosj
 */
public enum SxbDealStatus {

	no(0,"未处理"),dealing(1,"处理中"),success(2,"成功"),fail(3,"失败");
	
	private SxbDealStatus(int key, String value) {
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
