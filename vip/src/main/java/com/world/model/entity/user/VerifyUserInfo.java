package com.world.model.entity.user;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/**
 * 
 * 新增功能，用户提交修改手机或GOOGLE验证码时
 * 不再直接通过验证码给予用户修改，在不改变User变的情况下
 * 插入一条数据到该表里，等待客服验证审核通过后直接为用户修改.
 * 
 * @author guosj
 */
@Entity(noClassnameStored=true)
public class VerifyUserInfo extends StrBaseLongIdEntity {
	
	public VerifyUserInfo() {
		super();
	}
	public VerifyUserInfo(Datastore ds) {
		super(ds);
	}
	//用户Id
	private String userId;
	//用户登录名
	private String userName;
	//修改类型(1、手机，2、GOOGLE，3、挂失手机)
	private int type;
	//用户修改内容，手机为手机号，GOOGLE为GOOGLE密钥
	private String info;
	//如果是手机的话，多了个区号
	private String mcode;
	//当客服审批通过后，把用户修改之前的信息记录在此字段中，防止误操作。
	private String beforeInfo;
	//0、待审核，1、不通过，2、已通过，3、被撤回
	//当用户通过后，会直接修改用户的请求信息
	private int status;
	//审核内容
	private String memo;
	//申请时间
	private long addTime;
	//审核时间
	private long verifyTime;
	//审核者
	private String adminId;
	//申请者IP
	private String ip;
	//是否验证了旧手机/google
	private int verifyOldInfo;
	//注册年份
	private int regYear;
	//注册月份
	private int regMonth;
	//首次充值币种
	private int firstDepositCurrency;
	//首次充值金额
	private String firstDepositAmount;
	//首次提现币种
	private int firstWithdrawCurrency;
	//首次提现金额
	private String firstWithdrawAmount;
	
	public int getRegYear() {
		return regYear;
	}
	public void setRegYear(int regYear) {
		this.regYear = regYear;
	}
	public int getRegMonth() {
		return regMonth;
	}
	public void setRegMonth(int regMonth) {
		this.regMonth = regMonth;
	}
	public int getFirstDepositCurrency() {
		return firstDepositCurrency;
	}
	public void setFirstDepositCurrency(int firstDepositCurrency) {
		this.firstDepositCurrency = firstDepositCurrency;
	}
	public String getFirstDepositAmount() {
		return firstDepositAmount;
	}
	public void setFirstDepositAmount(String firstDepositAmount) {
		this.firstDepositAmount = firstDepositAmount;
	}
	public int getFirstWithdrawCurrency() {
		return firstWithdrawCurrency;
	}
	public void setFirstWithdrawCurrency(int firstWithdrawCurrency) {
		this.firstWithdrawCurrency = firstWithdrawCurrency;
	}
	public String getFirstWithdrawAmount() {
		return firstWithdrawAmount;
	}
	public void setFirstWithdrawAmount(String firstWithdrawAmount) {
		this.firstWithdrawAmount = firstWithdrawAmount;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getBeforeInfo() {
		return beforeInfo;
	}
	public void setBeforeInfo(String beforeInfo) {
		this.beforeInfo = beforeInfo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public long getAddTime() {
		return addTime;
	}
	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}
	public long getVerifyTime() {
		return verifyTime;
	}
	public void setVerifyTime(long verifyTime) {
		this.verifyTime = verifyTime;
	}
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	
	public String getMcode() {
		return mcode;
	}
	public void setMcode(String mcode) {
		this.mcode = mcode;
	}
	
	public String getInfoShow(){
		if(StringUtils.isEmpty(info)){
			return "暂无修改内容";
		}else{
			return info.substring(0, 3) + "***" + info.substring(info.length() - 3);
		}
	}
	
	public String getBeforeInfoShow(){
		if(StringUtils.isEmpty(beforeInfo)){
			return "暂无修改内容";
		}else{
			return beforeInfo.substring(0, 3) + "***" + beforeInfo.substring(beforeInfo.length() - 3);
		}
	}
	public String getAddTimeShowString(){
		Timestamp t = new Timestamp(addTime);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t);
	}

	public Timestamp getAddTimeShow(){
		return new Timestamp(addTime);
	}

	public String getVerifyTimeShowString(){
		if(verifyTime == 0) return "-";
		Timestamp t = new Timestamp(verifyTime);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t);
	}
	public Timestamp getVerifyTimeShow(){
		return new Timestamp(verifyTime);
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getVerifyOldInfo() {
		return verifyOldInfo;
	}
	public void setVerifyOldInfo(int verifyOldInfo) {
		this.verifyOldInfo = verifyOldInfo;
	}
	
}
