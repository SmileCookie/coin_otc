package com.yc.entity.sxb;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

/**
 * 资产凭证表
 * @author guosj
 */
@Entity(value = "sxb", noClassnameStored = true)
public class Sxb {
	@Id
	private ObjectId _id;
	
	//公钥地址
	private String publickey;
	//凭证时间
	private long createTime; 

	//用户名
	private String username;
	//真实姓名
	private String realname;
	//用户ID
	private String userid;
	//安保手机
	private String mobile;
	//安保邮箱
	private String email;
	
	//比特币充值地址
	private String btc_recharge_address;
	//赖特币充值地址
	private String ltc_recharge_address;
	//比特币提现地址
	private String btc_withdrawal_address;
	//赖特币提现地址
	private String ltc_withdrawal_address;
	
	//人民币总金额
	private String rmb_balance;
	//人民币可用金额
	private String rmb_free;
	//人民币冻结金额
	private String rmb_freeze;
	
	//比特币总金额
	private String btc_balance;
	//比特币可用金额
	private String btc_free;
	//比特币冻结金额
	private String btc_freeze;
	
	//莱特币总金额
	private String ltc_balance;
	//莱特币可用金额
	private String ltc_free;
	//莱特币冻结金额
	private String ltc_freeze;
	
	//比特权总金额
	private String btq_balance;
	//比特权可用金额
	private String btq_free;
	//比特权冻结金额
	private String btq_freeze;
	
	//已借入的人民币
	private String in_rmb;
	//已借入的比特币
	private String in_btc;
	//已借入的莱特币
	private String in_ltc;
	
	//已借出的人民币
	private String out_rmb;
	//已借出的比特币
	private String out_btc;
	//已借出的莱特币
	private String out_ltc;
	
	//处理状态(0、未生成资产凭证,1、正在处理,2、已成功生成资产凭证,3、生成资产凭证失败)
	private int dealstatus;
	//生成凭证保存的路径
	private String imgpath;
	//加密的字符串
	private String shastr;
	//是否已下载或已查看(0,1)
	private int seeStatus;
	
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId id) {
		_id = id;
	}
	public String getPublickey() {
		return publickey;
	}
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBtc_recharge_address() {
		return btc_recharge_address;
	}
	public void setBtc_recharge_address(String btcRechargeAddress) {
		btc_recharge_address = btcRechargeAddress;
	}
	public String getLtc_recharge_address() {
		return ltc_recharge_address;
	}
	public void setLtc_recharge_address(String ltcRechargeAddress) {
		ltc_recharge_address = ltcRechargeAddress;
	}
	public String getBtc_withdrawal_address() {
		return btc_withdrawal_address;
	}
	public void setBtc_withdrawal_address(String btcWithdrawalAddress) {
		btc_withdrawal_address = btcWithdrawalAddress;
	}
	public String getLtc_withdrawal_address() {
		return ltc_withdrawal_address;
	}
	public void setLtc_withdrawal_address(String ltcWithdrawalAddress) {
		ltc_withdrawal_address = ltcWithdrawalAddress;
	}
	public String getRmb_balance() {
		return rmb_balance;
	}
	public void setRmb_balance(String rmbBalance) {
		rmb_balance = rmbBalance;
	}
	public String getRmb_free() {
		return rmb_free;
	}
	public void setRmb_free(String rmbFree) {
		rmb_free = rmbFree;
	}
	public String getRmb_freeze() {
		return rmb_freeze;
	}
	public void setRmb_freeze(String rmbFreeze) {
		rmb_freeze = rmbFreeze;
	}
	public String getBtc_balance() {
		return btc_balance;
	}
	public void setBtc_balance(String btcBalance) {
		btc_balance = btcBalance;
	}
	public String getBtc_free() {
		return btc_free;
	}
	public void setBtc_free(String btcFree) {
		btc_free = btcFree;
	}
	public String getBtc_freeze() {
		return btc_freeze;
	}
	public void setBtc_freeze(String btcFreeze) {
		btc_freeze = btcFreeze;
	}
	public String getLtc_balance() {
		return ltc_balance;
	}
	public void setLtc_balance(String ltcBalance) {
		ltc_balance = ltcBalance;
	}
	public String getLtc_free() {
		return ltc_free;
	}
	public void setLtc_free(String ltcFree) {
		ltc_free = ltcFree;
	}
	public String getLtc_freeze() {
		return ltc_freeze;
	}
	public void setLtc_freeze(String ltcFreeze) {
		ltc_freeze = ltcFreeze;
	}
	public String getBtq_balance() {
		return btq_balance;
	}
	public void setBtq_balance(String btqBalance) {
		btq_balance = btqBalance;
	}
	public String getBtq_free() {
		return btq_free;
	}
	public void setBtq_free(String btqFree) {
		btq_free = btqFree;
	}
	public String getBtq_freeze() {
		return btq_freeze;
	}
	public void setBtq_freeze(String btqFreeze) {
		btq_freeze = btqFreeze;
	}
	public int getDealstatus() {
		return dealstatus;
	}
	public void setDealstatus(int dealstatus) {
		this.dealstatus = dealstatus;
	}
	public String getImgpath() {
		return imgpath;
	}
	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}
	public String getShastr() {
		return shastr;
	}
	public void setShastr(String shastr) {
		this.shastr = shastr;
	}
	public int getSeeStatus() {
		return seeStatus;
	}
	public void setSeeStatus(int seeStatus) {
		this.seeStatus = seeStatus;
	}
	public String getIn_rmb() {
		return in_rmb;
	}
	public void setIn_rmb(String in_rmb) {
		this.in_rmb = in_rmb;
	}
	public String getIn_btc() {
		return in_btc;
	}
	public void setIn_btc(String in_btc) {
		this.in_btc = in_btc;
	}
	public String getIn_ltc() {
		return in_ltc;
	}
	public void setIn_ltc(String in_ltc) {
		this.in_ltc = in_ltc;
	}
	public String getOut_rmb() {
		return out_rmb;
	}
	public void setOut_rmb(String out_rmb) {
		this.out_rmb = out_rmb;
	}
	public String getOut_btc() {
		return out_btc;
	}
	public void setOut_btc(String out_btc) {
		this.out_btc = out_btc;
	}
	public String getOut_ltc() {
		return out_ltc;
	}
	public void setOut_ltc(String out_ltc) {
		this.out_ltc = out_ltc;
	}
}
