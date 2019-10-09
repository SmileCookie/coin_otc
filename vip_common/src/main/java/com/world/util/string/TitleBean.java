package com.world.util.string;

public class TitleBean {

	public TitleBean(String shortTitle, String sourceTitle,boolean isSame) {
		super();
		this.shortTitle = shortTitle;
		this.sourceTitle = sourceTitle;
		this.isSame=isSame;
	}
	private String shortTitle;
	private String sourceTitle;
	private boolean isSame;
	public boolean isSame() {
		return isSame;
	}
	public void setSame(boolean isSame) {
		this.isSame = isSame;
	}
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	public String getSourceTitle() {
		return sourceTitle;
	}
	public void setSourceTitle(String sourceTitle) {
		this.sourceTitle = sourceTitle;
	}
}
