package com.world.util.language;

public class SafeTipsTag {
	public SafeTipsTag(){}
	public SafeTipsTag(String suoDing, String cuoWu, String weiSheZhi) {
		super();
		this.suoDing = suoDing;
		this.cuoWu = cuoWu;
		this.weiSheZhi = weiSheZhi;
	}
	private String suoDing;
	private String cuoWu;
	private String weiSheZhi;
	
	public String getSuoDing() {
		return suoDing;
	}
	public void setSuoDing(String suoDing) {
		this.suoDing = suoDing;
	}
	public String getCuoWu() {
		return cuoWu;
	}
	public void setCuoWu(String cuoWu) {
		this.cuoWu = cuoWu;
	}
	public String getWeiSheZhi() {
		return weiSheZhi;
	}
	public void setWeiSheZhi(String weiSheZhi) {
		this.weiSheZhi = weiSheZhi;
	}
	
}
