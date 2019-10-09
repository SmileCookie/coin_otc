package com.world.model.financial.entity;

import com.world.data.mysql.Bean;

/**
 * 财务系统
 * 公司账户表格  支持的银行
 * @author Administrator
 *
 */

public class FinanBank extends Bean {
	public FinanBank() {
		super();
	}
	
	public FinanBank(String name,String tag,String img,double withdrawLimit , String cftTag , String epayTag, String yeepayTag, int withdrawBank) {
		super();
		this.name = name;
	}
	
	private static final long serialVersionUID = -8791435182844009949L;
  
	private int id;
    private String name;
	private String tag;
	private String cftTag;
	private String epayTag;
	private String yeepayTag;
	private String img;
	private String memo;
	private double withdrawLimit;
	private int withdrawBank;//是否是提现银行    1为提现银行，2不是
	private boolean isDel;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getCftTag() {
		return cftTag;
	}

	public void setCftTag(String cftTag) {
		this.cftTag = cftTag;
	}

	public String getEpayTag() {
		return epayTag;
	}

	public void setEpayTag(String epayTag) {
		this.epayTag = epayTag;
	}

	public String getYeepayTag() {
		return yeepayTag;
	}

	public void setYeepayTag(String yeepayTag) {
		this.yeepayTag = yeepayTag;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getWithdrawBank() {
		return withdrawBank;
	}

	public void setWithdrawBank(int withdrawBank) {
		this.withdrawBank = withdrawBank;
	}

	public double getWithdrawLimit() {
		return withdrawLimit;
	}

	public void setWithdrawLimit(double withdrawLimit) {
		this.withdrawLimit = withdrawLimit;
	}

	public boolean isDel() {
		return isDel;
	}

	public void setDel(boolean isDel) {
		this.isDel = isDel;
	}

 }   
