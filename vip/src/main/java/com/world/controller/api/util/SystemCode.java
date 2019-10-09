package com.world.controller.api.util;

import java.util.EnumSet;
import java.util.Iterator;

import com.world.model.entity.SysEnum;

public enum SystemCode implements SysEnum{

	code_402(402, "您的账号已在其他设备上登录", "failed"),
	code_1000(1000, "操作成功", "success"),
	code_1001(1001, "一般错误提示", "error tips"),
	
	code_1002(1002, "内部错误", "Internal Error"),
	code_1003(1003, "授权失效，需要重新登录", "Validate No Pass"),
	code_1004(1004, "资金安全密码已锁定", "Transaction password locked"),
	code_1005(1005, "资金安全密码错误", "Transaction Password Error"),
	code_1006(1006, "实名认证等待审核或审核不通过", "Audit Or Audit No Pass"),
	code_1007(1007, "修改后的密码不能和原密码一致", "The new password could not be equals to the old one"),
	code_1008(1008, "登录密码错误", "Safe Password Error"),
	code_1009(1009, "找不到市场", "Cannot find the destination market"),
	code_1010(1010, "您已被删除", "U have been deleted!"),
	code_1011(1011, "本次操作需要您先进行实名认证", "U have to get a RealName-verification!"),
	code_1013(1013, "本次提现需要您填写资金安全密码", "U have to get a Safe Password!"),
	code_1014(1014, "本次操作需要同时验证您的资金安全密码和短信验证码", "U have to get a Safe Audit!"),
	code_1015(1015, "本次操作需要您google验证，请输入移动设备上生成的验证码", "U have to get a Safe Audit!"),
	code_1016(1016, "本次提现需要您指纹验证", "U have to get a Safe Audit!"),
	code_1017(1017, "本次登录为异地登录，需要您短信验证", "U have to get a Safe Audit!"),
	code_1018(1018, "你已开启谷歌登录验证，本次登录需要您的谷歌验证码", "U have to get a Safe Audit!"),
	code_1019(1019, "未知操作类型!", "unknown operation type!"),
	code_1020(1020, "你已同时开启谷歌&短信登录验证，本次登录需要您填写谷歌和短信验证码", "U have to get a Safe Audit!"),
	code_1021(1021, "您的谷歌验证修改申请正在审核中，请等待审核通过!", "U have to get a Safe Audit!"),
	code_1022(1022, "本次操作需要同时验证您的资金安全密码和谷歌验证码", "U have to get a Safe Audit!"),
	code_1023(1023, "您没有进行手机认证和Google认证，暂时不能进行充值/提现业务，为了您的账号安全，请进行手机认证或Google认证。", "U have to get a Safe Audit!"),
	code_1024(1024, "验证码错误，请重新获取", "验证码错误，请重新获取"),
	code_1025(1025, "邮箱注册的用户账号未激活，需要激活后才能登录", "邮箱注册的用户账号未激活，需要激活后才能登录"),
	// TODO: 2017/6/7 国际化时rtn只加载903行，改行不在903之内
//	code_1026(1026, "您未设置资金密码，请设置资金密码后重试", "You have not set the transaction password, please set the transaction password and try again"),

	code_1026(1026, "You have not set the transaction password, please set the transaction password and try again", "You have not set the transaction password, please set the transaction password and try again"),
	code_1027(1027, "价格不能为空", "U have to set the price"),

	code_2001(2001 , "人民币账户余额不足" , "Insufficient CNY Balance"), 
	code_2002(2002 , "比特币账户余额不足" , "Insufficient BTC Balance"),
	code_2003(2003 , "莱特币账户余额不足" , "Insufficient LTC Balance"),
	code_2004(2004 , "比特权账户余额不足" , "Insufficient BTQ Balance"),
	code_2005(2005 , "以太币账户余额不足" , "Insufficient ETH Balance"),
	code_2006(2006 , "DAO账户余额不足" , "Insufficient DAO Balance"),
	code_2007(2007 , "ETC账户余额不足" , "Insufficient ETC Balance"),
	
	code_3001(3001 , "挂单没有找到" , "Not Found Order"),
	code_3002(3002 , "无效的金额" , "Invalid Money"),
	code_3003(3003 , "无效的数量" , "Invalid Amount"),
	code_3004(3004 , "用户不存在" , "The account does not exist"),
	code_3005(3005 , "无效的参数" , "Invalid Arguments"),
	code_3006(3006 , "无效的IP或与绑定的IP不一致", "Invalid Ip Address"),
	code_3007(3007 , "请求时间已失效", "Invalid Ip Request Time"),
	code_3008(3008 , "账号格式错误" , "The account is not formatted properly"),

	code_4001(4001 , "API接口被锁定或未启用", "API Locked Or Not Enabled"),
	code_4002(4002 , "请求过于频繁", "Request Too Frequently"),
	code_4003(4003 , "用户交易已被锁定，当前不能进行交易", "The state has been locked, the current can not be traded"),
	code_4004(4004 , "汇率接口异常", "the API for rate does not work nice"),
	code_4005(4005 , "有借贷，不能买入BTQ", "Borrowing, can't buy BTQ"),
	code_4006(4006 , "您在p2p借贷中有借入的资产，系统不允许您提现。", "Borrowing, can't withdrawl"),
	
	;
	
	private SystemCode(int key, String value , String className) {
		this.key = key;
		this.value = value;
		this.className = className;
	}

	private int key;
	private String value;
	private String className;

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className){
		this.className = className;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public static SystemCode getSystemCodeByKey(int Key){
		Iterator<SystemCode> it= getAll().iterator();
		
		while(it.hasNext()){
			SystemCode systemCode = it.next();
			if(systemCode.getKey() == Key){
				return systemCode;
			}
		}
		return null;
	}
	
	public static EnumSet<SystemCode> getAll(){
		return EnumSet.allOf(SystemCode.class);
	}
	
	/**
	 * 根据错误信息返回SystemCode
	 * @param msg
	 * @return
	 */
	public static SystemCode getSystemCode(String msg){
		//log.info(msg);
		if("委托失败程序错误".equals(msg)){
			return SystemCode.code_1002;
		}
		else if("您的账户余额不足请充值".equals(msg)){
			return SystemCode.code_2001;
		}
		else if("您的比特币余额不足请充值".equals(msg)){
			return SystemCode.code_2002;
		}
		else if("您还有卖比特币委托已经达到您本次购买的报价请取消".equals(msg)){
			return null;
		}
		else if("您还有买比特币的委托已经达到您本次出售的报价请取消".equals(msg)){
			return null;
		}
		else if("您的现金余额不足请充值".equals(msg)){
			return SystemCode.code_2001;
		}
		else if("您的LTC不足以出售".equals(msg)){
			return SystemCode.code_2003;
		}
		else if("您还有卖出LTC委托已经达到您本次购买的报价请先取消".equals(msg)){
			return null;
		}
		else if("您还有买LTC的委托已经达到您本次出售的报价请取消".equals(msg)){
			return null;
		}else{
			return null;
		}
	}
}
