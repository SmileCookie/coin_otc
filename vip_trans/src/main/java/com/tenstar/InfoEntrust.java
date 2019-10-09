package com.tenstar;

import com.api.VipResponse;

public class InfoEntrust {
	public Info in;
	public long entrustId;
	public VipResponse rs;

	public InfoEntrust(Info i,long entrustId){
		in=i;
		this.entrustId=entrustId;
	}
	/**
	 * 资金系统返回的错误信息
	 * @param i
	 * @param entrustId
	 * @param rs
	 */
	public InfoEntrust(Info i,long entrustId, VipResponse rs){
		in=i;
		this.entrustId=entrustId;
		this.rs = rs;
	}
}
