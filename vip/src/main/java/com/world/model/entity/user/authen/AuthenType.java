package com.world.model.entity.user.authen;

import com.world.model.entity.SysEnum;

/****
 * 用户版本
 * @author Administrator
 *
 */
public enum AuthenType implements SysEnum {
	realname(1, "实名认证") , 
	email(2 , "邮箱认证"),
	mobile(3 , "手机修改"),
	modifyPwd(4 , "修改登录密码"),
	modifySecurityPwd(5 , "修改资金密码"),
	findPwd(6, "找回登录密码"),
	findSecurityPwd(7, "找回资金密码"),
	google(8,"谷歌修改"),
	warningSetting(10, "设置算力预警"),
	closeSafePwd(20, "关闭资金密码"),
	cashSet(21, "提现设置"),
	realnameApply(22,"实名认证申请"),
	googleApply(23,"谷歌修改申请"),
	mobileApply(24,"手机修改申请"),
	setUserInfo(25,"设置个人信息")
	;

	private AuthenType(int key, String value) {
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
