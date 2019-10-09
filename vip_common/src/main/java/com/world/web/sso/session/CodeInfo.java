package com.world.web.sso.session;

import java.io.Serializable;

public class CodeInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2039327990664192939L;
	public String type;//类型
	public int times = 0;//发送次数
	public String lastCode;//最后的验证码
	public long lastDate;//最后发送验证码的时间
	public long lastHasValidateTimes = 0;//最后的验证码已经验证的次数
	public String mobileOrEmail;//手机号 或者 邮箱号
	public String lastHasValidateStrs="";//已验证的数字
}
