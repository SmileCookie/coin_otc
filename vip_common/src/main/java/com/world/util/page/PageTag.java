package com.world.util.page;

public class PageTag {
	public PageTag(){}
	public PageTag(String gong, String xiang, String ye, String diYiYe,
			String shangYiYe, String xiaYiYe, String tiaoZhuan, String jMyTitle , String jErrormsg) {
		super();
		this.gong = gong;
		this.xiang = xiang;
		this.ye = ye;
		this.diYiYe = diYiYe;
		this.shangYiYe = shangYiYe;
		this.xiaYiYe = xiaYiYe;
		this.tiaoZhuan = tiaoZhuan;
		this.jMyTitle = jMyTitle;
		this.jErrormsg = jErrormsg;
	}
	
	//("共" , "项" , "页" ,"第一页" , "上一页" ,"下一页" ,"跳转")
	
	private String gong;
	private String xiang;
	
	private String ye;
	private String diYiYe;
	private String shangYiYe;
	private String xiaYiYe;
	private String tiaoZhuan;
	private String jMyTitle;
	private String jErrormsg;
	
	
	public String getjMyTitle() {
		return jMyTitle;
	}
	public void setjMyTitle(String jMyTitle) {
		this.jMyTitle = jMyTitle;
	}
	public String getjErrormsg() {
		return jErrormsg;
	}
	public void setjErrormsg(String jErrormsg) {
		this.jErrormsg = jErrormsg;
	}
	public String getGong() {
		return gong;
	}
	public void setGong(String gong) {
		this.gong = gong;
	}
	public String getXiang() {
		return xiang;
	}
	public void setXiang(String xiang) {
		this.xiang = xiang;
	}
	public String getYe() {
		return ye;
	}
	public void setYe(String ye) {
		this.ye = ye;
	}
	public String getDiYiYe() {
		return diYiYe;
	}
	public void setDiYiYe(String diYiYe) {
		this.diYiYe = diYiYe;
	}
	public String getShangYiYe() {
		return shangYiYe;
	}
	public void setShangYiYe(String shangYiYe) {
		this.shangYiYe = shangYiYe;
	}
	public String getXiaYiYe() {
		return xiaYiYe;
	}
	public void setXiaYiYe(String xiaYiYe) {
		this.xiaYiYe = xiaYiYe;
	}
	public String getTiaoZhuan() {
		return tiaoZhuan;
	}
	public void setTiaoZhuan(String tiaoZhuan) {
		this.tiaoZhuan = tiaoZhuan;
	}
	
	
	
}
