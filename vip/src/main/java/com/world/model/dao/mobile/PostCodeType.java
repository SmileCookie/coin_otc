package com.world.model.dao.mobile;

import com.world.model.entity.SysEnum;

public enum PostCodeType implements SysEnum{

	register(1 , "用户注册", "验证码：%s，注册账号。", "用户注册", "用户注册", false, false),
	mobileAuth(2, "手机认证", "验证码：%s，手机认证。", "手机认证", "手机认证", false, true),
	editMobile(3, "修改手机认证", "验证码：%s，修改手机认证。", "修改手机认证", "修改手机认证", false, false),
	googleAuth(5, "谷歌认证", "验证码：%s，谷歌认证。", "谷歌认证", "谷歌认证", false, true),
	emailAuth(7, "邮箱认证", "验证码：%s，邮箱认证。", "邮箱认证", "邮箱认证", false, true),


	cash(8 , "%%提币", "您的帐号正在提币%%，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "%%提币", "您的帐号正在进行提币操作。", true, false),
	safeAuth(14,"安全设置", "验证码：%s，安全设置。", "重置二次验证", "您的帐号正在重置二次验证。", false, false),
	addAddr(10, "新增%%提现地址", "验证码：%s，新增%%提现地址。", "新增%%提现地址", "您的账号正在进行新增%%提现地址。", true, false),

	loanForce(13, "借贷平仓预警", "尊敬的[%s]，您的总额已不足借款总额的1.2倍，请您及时处理，余额不足借款总额的1.1倍时系统将强制为您平仓。", "借贷平仓预警", "借贷平仓预警", false, false),
	
	superManagerAuth(15, "管理员身份验证", "验证码：%s，管理员身份验证。", "管理员身份验证", "管理员身份验证", false, false),
	
	resetPassword(16, "找回登录密码", "您的帐号正在重置登录密码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "重置登录密码", "您的帐号正在重置登录密码。", false, false),
	resetSafePassword(17, "找回资金密码", "验证码：%s，找回资金密码。", "找回资金密码", "找回资金密码", false, false),
	setSafePassword(18, "设置资金密码", "验证码：%s，设置资金密码。", "设置资金密码", "设置资金密码", false, false),
	updateSafePassword(21, "重置资金密码", "验证码：%s，重置资金密码。", "修改资金密码", "您的帐号正在重置资金密码。", false, false),
	resetSafePwd(19 , "重置资金密码", "尊敬的客户：您的账户资金密码已修改成功，24小时内不能进行提币操作，如非本人操作请联系客服。", "重置资金密码", "重置资金密码", false, false),
	close2HPwd(20, "关闭资金密码", "验证码：%s，关闭交易密码两小时免输入。", "关闭资金密码", "关闭资金密码", true, false),

	updateLoginPassword(30, "修改登录密码", "验证码：%s，重置登录密码。", "修改登录密码", "您的帐号正在修改登录密码。", false, false),
	setMobile(31, "录入手机", "您的帐号正在绑定手机号码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "录入手机", "录入手机", false, false),
	updMobile(32, "修改手机", "您的帐号正在修改手机号码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "修改手机", "修改手机", false, false),
	updMobileCheck(33, "校验原始手机号", "您的帐号正在绑定手机号码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "校验原始手机号", "校验原始手机号", false, false),
	openMobileVerify(34, "开启手机验证", "您的帐号正在开启手机验证，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "开启手机验证", "开启手机验证", false, false),
	closeMobileVerify(35, "关闭手机验证", "您的帐号正在关闭手机验证，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "您的帐号正在关闭手机验证。", "您的帐号正在关闭手机验证。", false, false),
	closeEmailVerify(36, "关闭手机邮箱验证", "验证码：%s，关闭手机邮箱验证。", "关闭手机验证", "您的帐号正在关闭手机验证。", false, false),
	closeGoogleVerify(37, "关闭谷歌验证", "验证码：%s，关闭谷歌验证。", "关闭谷歌验证", "您的帐号正在关闭谷歌验证。", false, false),
	updateLoginPasswordByMoblie(38, "修改登录密码手机验证", "您的帐号正在修改登录密码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "您的帐号正在修改登录密码。", "您的帐号正在修改登录密码。", false, false),
	updateSafePasswordByMobile(39, "重置资金密码手机验证", "您的帐号正在修改资金密码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "您的帐号正在重置资金密码。", "您的帐号正在重置资金密码。", false, false),

	logVIP(63 , "用户登录", "您正在登录帐号，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "用户登录", "用户登录", false, false),
	authenPass(64 , "您已通过身份认证", "恭喜您，您的身份认证已通过审核，系统已为您提高提现限额。如果此行为并非您本人操作，请尽快通过我们的工单系统联系客服人员。", "您已通过身份认证", "您已通过身份认证", false, false),
	authenNoPass(66 , "您的身份认证未通过审核", "很抱歉，您的身份认证未通过审核，您可登录平台重新认证。如果此行为并非您本人操作，请尽快通过我们的工单系统联系客服人员。", "您的身份认证未通过审核", "您的身份认证未通过审核", false, false),
	diffIpAuth(65, "异地登录验证", "验证码：%s，异地登录。如非本人操作，请及时修改登录密码。", "异地登录验证", "异地登录验证", false, false),
	appRegVIP(71 , "APP用户注册", "验证码：%s，APP注册账号。", "APP用户注册", "您正在注册btcwinex帐号。", false, false),
	appLogVIP(72 , "APP用户登录", "验证码：%s，APP用户登录。", "APP用户登录", "APP用户登录", false, false),
	appOtherVIP(73 , "APP用户登录", "验证码：%s，APP用户登录。", "APP用户登录", "APP用户登录", false, false),
	mobileLoss(94, "手机挂失", "验证码：%s，手机挂失。", "手机挂失", "手机挂失", true, false),
	appResetSecondVerify(100 , "重置二次验证", "验证码：%s，重置二次验证。", "重置二次验证", "您的帐号正在重置二次验证。", false, false),
	appForgetLoginPwd(101 , "忘记密码", "验证码：%s，忘记密码。", "忘记密码", "您的帐号正在重置登录密码。", false, false),
	appWithdraw(102 , "%%提币", "您的帐号正在提币%%，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "%%提币", "您的帐号正在进行提币操作。", false, false),
	appupdateSafePassword(103, "重置资金密码", "验证码：%s，重置资金密码。", "重置资金密码", "您的帐号正在重置资金密码。", false, false),
	appUpdateLoginPassword(104, "修改登录密码", "验证码：%s，重置登录密码。", "修改登录密码", "您的帐号正在修改登录密码。", false, false),
	appSetMobile(105, "录入手机", "您的帐号正在绑定手机号码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "录入手机", "录入手机", false, false),
	appUpdMobile(106, "修改手机", "您的帐号正在绑定手机号码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "修改手机", "修改手机", false, false),
	appUpdMobileCheck(107, "校验原始手机号", "您的帐号正在绑定手机号码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "校验原始手机号", "校验原始手机号", false, false),
	appOpenMobileVerify(108, "开启手机验证", "您的帐号正在开启手机验证，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "开启手机验证", "开启手机验证", false, false),
	appCloseMobileVerify(109, "关闭手机验证", "您的帐号正在关闭手机验证，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "您的帐号正在关闭手机验证。", "您的帐号正在关闭手机验证。", false, false),
	appCloseEmailVerify(110, "关闭手机邮箱验证", "验证码：%s，关闭手机邮箱验证。", "关闭手机验证", "您的帐号正在关闭手机验证。", false, false),
	appCloseGoogleVerify(111, "关闭谷歌验证", "验证码：%s，关闭谷歌验证。", "关闭谷歌验证", "您的帐号正在关闭谷歌验证。", false, false),
	appUpdateLoginPasswordByMoblie(112, "修改登录密码手机验证", "您的帐号正在修改登录密码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "您的帐号正在修改登录密码。", "您的帐号正在修改登录密码。", false, false),
	appUpdateSafePasswordByMobile(113, "重置资金密码手机验证", "您的帐号正在重置资金密码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "您的帐号正在重置资金密码。", "您的帐号正在重置资金密码。", false, false),
	appForgetLoginPwdByMobile(114 , "忘记密码", "您的帐号正在重置登录密码，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "忘记密码", "您的帐号正在重置登录密码。", false, false),
	appWithdrawByMobile(115 , "%%提币", "您的帐号正在提币%%，验证码：[%s]，10分钟有效，切勿将验证码告知他人。", "%%提币", "您的帐号正在进行提币操作。", false, false),



	;
	
	private PostCodeType(int key, String value, String des, String emailTitle, String emailInfo, boolean needPwd, boolean needMobile) {
		this.key = key;
		this.value = value;
		this.des = des;
		this.emailTitle = emailTitle;
		this.emailInfo = emailInfo;
		this.isNeedPwd = needPwd;
		this.isNeedMobile = needMobile;
	}

	private int key;
	private String value;
	private String des;
	private String emailTitle;
	private String emailInfo;
	private boolean isNeedPwd;
	private boolean isNeedMobile;

    @Override
    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getEmailTitle() {
        return emailTitle;
    }

    public void setEmailTitle(String emailTitle) {
        this.emailTitle = emailTitle;
    }

    public String getEmailInfo() {
        return emailInfo;
    }

    public void setEmailInfo(String emailInfo) {
        this.emailInfo = emailInfo;
    }

    public boolean isNeedPwd() {
        return isNeedPwd;
    }

    public void setNeedPwd(boolean needPwd) {
        isNeedPwd = needPwd;
    }

    public boolean isNeedMobile() {
        return isNeedMobile;
    }

    public void setNeedMobile(boolean needMobile) {
        isNeedMobile = needMobile;
    }
}
