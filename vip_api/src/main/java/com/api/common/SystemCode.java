package com.api.common;

import java.util.EnumSet;
import java.util.Iterator;

public enum SystemCode {

	code_402(402, "您的账号已在其他设备上登", "failed"),
	code_1000(1000, "操作成功", "success"),
	code_1001(1001, "一般错误提示", "error tips"),
	
	code_1002(1002, "内部错误", "Internal Error"),
	code_1003(1003, "验证不通过", "Validate No Pass"),
	code_1004(1004, "资金安全密码锁定", "Safe Password Locked"),
	code_1005(1005, "资金安全密码错误", "Safe Password Error"),
	code_1006(1006, "实名认证等待审核或审核不通过", "Audit Or Audit No Pass"),
	code_1013(1013, "本次提现需要您填写资金安全密码", "U have to get a Safe Password!"),

	code_2001(2001 , "人民币账户余额不足" , "Insufficient CNY Balance"), 
	code_2002(2002 , "比特币账户余额不足" , "Insufficient BTC Balance"),
	code_2003(2003 , "莱特币账户余额不足" , "Insufficient LTC Balance"),
	code_2004(2004 , "比特权账户余额不足" , "Insufficient BTQ Balance"),
	
	code_3001(3001 , "挂单没有找到" , "Not Found Order"),
	code_3002(3002 , "无效的金额" , "Invalid Money"),
	code_3003(3003 , "无效的数量" , "Invalid Amount"),
	code_3004(3004 , "用户不存在" , "No Such User"),
	code_3005(3005 , "无效的参数" , "Invalid Arguments"),
	code_3006(3006 , "无效的IP或与绑定的IP不一致", "Invalid Ip Address"),
	code_3007(3007 , "请求时间已失效", "Invalid Ip Request Time"),
	code_3008(3008 , "还款记录中部分执行失败", "Invalid Ip Request Time"),
	
	code_4001(4001 , "API接口被锁定或未启用", "API Locked Or Not Enabled"),
	code_4002(4002 , "请求过于频繁", "Request Too Frequently"),
	
	code_4003(4003 , "用户交易已被锁定，当前不能进行交易", "The state has been locked, the current can not be traded"),
	code_4005(4005 , "有借贷，不能交易BTQ", "Borrowing, can't trade BTQ");


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
	
	public static EnumSet<SystemCode> getAll(){
		return EnumSet.allOf(SystemCode.class);
	}
	
	public static SystemCode getSystemCodeByKey(int Key){
		Iterator<SystemCode> it= getAll().iterator();
		
		while(it.hasNext()){
			SystemCode method = it.next();
			if(method.getKey() == Key){
				return method;
			}
		}
		return null;
	}
	
	/**
	 * 根据错误信息返回SystemCode
	 * @param msg
	 * @return
	 */
	public static SystemCode getSystemCode(String msg){
		//System.out.println(msg);
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
