package com.world.model.financial.entity;

import com.world.model.entity.SysEnum;

/**
 * 除了手动录入账务时  其他需要录入账务的枚举类
 * @author Administrator
 *
 */

public enum EntryType implements SysEnum{
	
	rmbRemmittance(1, "RMB汇款确认"),
	rmbYeebaoCharge(2, "易宝充值确认"),
	rmbShuangqianCharge(3, "双乾充值确认"),
	rmbAlipayCharge(4, "支付宝充值确认"),
	rmbTenpayCharge(5, "财付通充值确认"),
	rmbBankCharge(6, "银行卡充值确认"),
	
	rmbWithdrawOne(10, "人民币单笔提现"),
	rmbWithdrawMore(11, "人民币多笔提现"),
	rmbWithdrawSucToFail(12, "人民币提现成功后失败"),//返还用户资金，系统资金要增加
	
	rmbSysCharge(15, "RMB系统充值"),
	rmbSysDeduct(16, "RMB系统扣除"),
	
	btcAutoCharge(20, "BTC充值确认"),
	btcSysCharge(21, "BTC系统充值"),
	btcSysDeduct(22, "BTC系统扣除"),
	btcDownloadSuc(23, "BTC下载成功"),
	btcDownloadSucToFail(24, "BTC下载成功后失败")////返还用户资金，系统资金要增加
	
	;	
	
	private EntryType(int key, String value) {
		this.key = key;
		this.value = value;
	}

	private int key;
	private String value;

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
