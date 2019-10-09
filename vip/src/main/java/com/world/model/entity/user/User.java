package com.world.model.entity.user;

import com.Lan;
import com.file.PathUtil;
import com.file.config.FileConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.NotSaved;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.LimitType;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.user.authen.AuditType;
import com.world.model.entity.user.authen.Authentication;
import com.world.model.entity.user.version.UserVersion;
import com.world.util.CommonUtil;
import com.world.util.UserUtil;
import com.world.web.action.ApproveAction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/****
 * 用户主表数据结构
 *
 * @author Administrator
 *
 */
@Entity(noClassnameStored = true)
public class User extends StrBaseLongIdEntity {
	private final static Logger log = Logger.getLogger(User.class);

	public static String use_pwd_key = "use_pwd_";
	public User() {
	}

	public User(Datastore ds) {
		super(ds);
	}

	public User(Datastore ds, String _id, String userName, String ip) {
		this(ds);
		this.myId = _id;
		this.userName = userName;
		this.loginIp = ip;
	}

	public User(String _id, String userName, String language) {
		this.myId = _id;
		this.userName = userName;
		this.language = language;
	}

	private static final long serialVersionUID = -2386115543279873656L;
	private String userName;// 用户名
	private String nickname;// 昵称
	private String pwd;// 登录密码
	private String safePwd;// 安全密码
	private String domain;// 域
	private String email;// 关联email
	private Boolean yesRecharge = false;
	private Integer userQualification; //用户资质  0：未激活 1：普通用户 2:实名认证 3：商家
	private Integer userQualificationLevel; //商家资质级别  31，32，33，34 VIP1，2，3，4


	/*Start by guankaili 20181120 前端要求传引导页标识 */
	//引导标识
	private Boolean guideFlg = false;
	/*end*/


	private String market;//
	private String currency="USD";// 货币名称 USD
	private String currencyN="$";// 货币符号 $
	private String language="en";// 默认语言en

	private double money;// 资金余额
	private double bi;// 金币余额
	private int utype;// 用户类型

	private String realName;// 真实姓名

	private int sex;// 1男 2女3其它
	private String photo;// 头像路径
	private Timestamp uploadTime;// 头像上传时间
	private String checkPhoto;// 未通过审核的照片,供显示使用
	private String reasons;// 头像未通过审核的原因
	private Timestamp registerTime;
	private Timestamp activationTime;

	private Timestamp lastLoginTime;// 最后登陆时间
	private Timestamp previousLogin;// 上次登陆时间
	private String previousLoginIp;// 上次登陆IP
	private String firstRegTime;// 注册账号未验证的时间

	private String loginIp;// 最后登录ip
	private String signature;// 签名
	private String shopId;// 该用户是否开店

	private long onlineTime;// 在线毫秒数
	private int safeLevel;// 安全密码级别
	private int pwdLevel;// 密码级别

	private String nick;// 昵称
	private String recommendName;// 推荐人用户名;
	private String recommendId;// 推荐人用户id;
	private boolean lockRecommend = false;// 锁定推荐，true为不能成为推荐人
	private String subDomain;// 用户子域名（用于推荐）;
	private int subDomainTimes;// 用户子域名修改次数;

	private String userIp;// 假ip
	private String memo;// 备注

	private boolean isLockPwd;
	private boolean isLockSafePwd;

	private boolean isLive;// bbs新增的
	private boolean isDeleted;//
	private int version;//版本  0 普通版  1.专业版
	private String trueIp;//真实ip
	private boolean recNeedReward;

	private Timestamp pwdModifyTime;
	private int safePwdModifyTimes;// 修改资金安全密码次数
	private Timestamp safePwdModifyTime;// 修改资金安全密码时间
	private int modifyTimes;
	private double chargeRan;//充值随机数
	private BigDecimal[] funds;
	private int repayLock;//0正常    1锁定    2有借入（不能交易btq）  3.有未成功的借入

	private String weibo;//微博号
	private String weixin;//微信号
	private boolean luckyDraw;//是否参与抽奖
	private String qq;

	private int regWay;
	private boolean freez;//是否已冻结
	private boolean warningUser = false;//是否为敏感用户
	private int forbidOutMoney;//禁止划账

	private boolean diffAreaLoginNoCheck = false;//异地登录不验证
	private boolean loginGoogleAuth = false;	//登录二次Google验证开关
	private boolean payMobileAuth = true;		//支付短信验证开关
	private boolean payGoogleAuth = false;		//支付二次Google验证开关
	private boolean payEmailAuth = false;		//Email验证开关

	private int allowOutMoney=0;//申请划账, 状态值-描述: @see AuditStatus
	private int canService=0;// 是否可以电话拜访

	private String weixinOpenId;// openId
	private String weixinNickname;// 微信昵称
	private String weixinHeadImgUrl;// 微信头像
	private Boolean weixinSubscribe = false;// 是否关注

	private String fingerprint;// 手机指纹识别
	private String jpushKey;// jpush推送key

	private boolean unService = false;// 解除服务
	private boolean bannedService = false;// 禁止电话拜访
	private boolean receiveDealSms = true;// 是否接收充值提现成功短信


	private Boolean isGoogleOpen = false;
	private Boolean isSmsOpen = false;

	private List<Combined> bindList;//绑定的类型列表

	private boolean isNeedSafePwd;//true 需要资金安全密码 false 不需要资金安全密码
	private long safePwdExpiration;//0永久关闭

	private String wapUid;//加密uid
	
	private int regMode = 1;// 注册方式 1：网页 2：手机
	
	private int withdrawSecurityLevel = 0;// 提现安全级别  0：正常用户  1：可疑用户  2：危险用户
	private int vipRate;//用户等级
	
	private double totalJifen;//用户总积分
	
	/**认证类型: 个人用户、企业用户*/
	private int authenType = AuditType.individual.getKey();
	
	private int authTimes;// 提交实名认证次数
	private int failAuthTimes;// 实名认证失败次数

	
	/**网页登录APP提醒*/
	private boolean isWebLoginAppNotice = true;
	
	private boolean receiveDealEmail = true;// 是否接收充值提现成功邮件
	private boolean receiveDealPush = true;// 是否接收充值提现成功APP推送
	
	/**登录验证类型*/
	private int loginAuthenType;
	/**交易验证类型*/
	private int tradeAuthenType;
	/**提现验证类型*/
	private int withdrawAuthenType;
    /**提现地址验证类型 0 1 初级模式 2 安全模式*/
    private int withdrawAddressAuthenType;
    private Timestamp withdrawAddressAuthenModifyTime;// 修改提现地址验证时间
    private int withdrawAddressAuthenSwitchStatus; //提现地址校验模式切换状态 0默认未切换 1首次切换 2 非首次切换

	/*start by chendi  20171028 拆分客户类型及展示字段*/
	/**客户类型  01-普通用户，
	 * 04-公司账户-融资融币，05- 测试用户，06-其他用户，07-公司账户-量化交易*/
	private String customerType;
	private String customerOperation;
	/**客户类型展示使用**/
	private String customerTypeView;
	private String customerOperationView;

	public Integer getUserQualification() {
		return userQualification;
	}

	public void setUserQualification(Integer userQualification) {
		this.userQualification = userQualification;
	}

	public Integer getUserQualificationLevel() {
		return userQualificationLevel;
	}

	public void setUserQualificationLevel(Integer userQualificationLevel) {
		this.userQualificationLevel = userQualificationLevel;
	}

	/*start by xwz 来源统计新增字段*/
	private String utmSource;	//来源
	private String utmMedium;	//介质
	/*end*/
	/**
	 * 开启/关闭手机验证开启checkbox框
	 */
	private boolean hasMobileCheckBox;

	/**
	 * 融云token
	 */
	private String rongCloudToken;
	/**
	 * 失败原因
	 */
	private String cardReson;

	/**
	 * 头像颜色
	 */
	private String color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Boolean getGoogleOpen() {
		return isGoogleOpen;
	}

	public void setGoogleOpen(Boolean googleOpen) {
		isGoogleOpen = googleOpen;
	}

	public Boolean getSmsOpen() {
		return isSmsOpen;
	}

	public void setSmsOpen(Boolean smsOpen) {
		isSmsOpen = smsOpen;
	}

	public Boolean getYesRecharge() {
		return yesRecharge;
	}

	public void setYesRecharge(Boolean yesRecharge) {
		this.yesRecharge = yesRecharge;
	}

	public String getCustomerOperation() {
		return customerOperation;
	}

	public void setCustomerOperation(String customerOperation) {
		this.customerOperation = customerOperation;
	}

	public String getCustomerOperationView() {

		if(StringUtils.isEmpty(customerOperation)){
			return Const.CUSTOMER_OPERATION.get(Const.CUSTOMER_OPERATION_NO_LIMIT);
		}else{
			return Const.CUSTOMER_OPERATION.get(customerOperation);
		}
	}

	public Timestamp getActivationTime() {
		return activationTime;
	}

	public void setActivationTime(Timestamp activationTime) {
		this.activationTime = activationTime;
	}

	public void setCustomerOperationView(String customerOperationView) {
		this.customerOperationView = customerOperationView;
	}

	public String getUtmSource() {
		return utmSource;
	}

	public void setUtmSource(String utmSource) {
		this.utmSource = utmSource;
	}

	public String getUtmMedium() {
		return utmMedium;
	}

	public void setUtmMedium(String utmMedium) {
		this.utmMedium = utmMedium;
	}

	public void setCustomerTypeView(String customerTypeView) {
		this.customerTypeView = customerTypeView;
	}

	public String getCustomerTypeView() {
		if(StringUtils.isEmpty(customerType)){
			return Const.CUSTOMER_TYPE.get(Const.CUSTOMER_TYPE_NORMAL);
		}else{
			return Const.CUSTOMER_TYPE.get(customerType);
		}
	}





	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	/*end*/
	public int getLoginAuthenType() {
		return loginAuthenType;
	}

	public void setLoginAuthenType(int loginAuthenType) {
		this.loginAuthenType = loginAuthenType;
	}

	public int getTradeAuthenType() {
		return tradeAuthenType;
	}

	public void setTradeAuthenType(int tradeAuthenType) {
		this.tradeAuthenType = tradeAuthenType;
	}

	public int getWithdrawAuthenType() {
		return withdrawAuthenType;
	}

	public void setWithdrawAuthenType(int withdrawAuthenType) {
		this.withdrawAuthenType = withdrawAuthenType;
	}

    public int getWithdrawAddressAuthenType() {
        return withdrawAddressAuthenType;
    }

    public void setWithdrawAddressAuthenType(int withdrawAddressAuthenType) {
        this.withdrawAddressAuthenType = withdrawAddressAuthenType;
    }

    public int getAuthTimes() {
		return authTimes;
	}

	public void setAuthTimes(int authTimes) {
		this.authTimes = authTimes;
	}

	public int getFailAuthTimes() {
		return failAuthTimes;
	}

	public void setFailAuthTimes(int failAuthTimes) {
		this.failAuthTimes = failAuthTimes;
	}

	public String getWapUid() {
		return wapUid;
	}

	public void setWapUid(String wapUid) {
		this.wapUid = wapUid;
	}

	public int getSafePwdPeriod(){//1开启 0 关闭 2 关闭6 H
		int period = 2;
		if (isNeedSafePwd) {
			period = 1;
		} else {
			if (safePwdExpiration == 0L) {
				period = 0;
			}
		}
		return period;
	}

	public boolean isPayEmailAuth() {
		if (userContact.getMobileStatu() == 2) {
			return false;
		}
		return payEmailAuth;
	}

	public void setPayEmailAuth(boolean payEmailAuth) {
		this.payEmailAuth = payEmailAuth;
	}

	public boolean isNeedSafePwd() {
		if(StringUtils.isEmpty(safePwd)){
			isNeedSafePwd = true;
		}
		return isNeedSafePwd;
	}

	public void setNeedSafePwd(boolean isNeedSafePwd) {
		this.isNeedSafePwd = isNeedSafePwd;
	}

	public long getSafePwdExpiration() {
		return safePwdExpiration;
	}

	public void setSafePwdExpiration(long safePwdExpiration) {
		this.safePwdExpiration = safePwdExpiration;
	}

	public List<Combined> getBindList() {
		return bindList;
	}

	public void setBindList(List<Combined> bindList) {
		this.bindList = bindList;
	}

	public int getRegWay() {
		return regWay;
	}

	public void setRegWay(int regWay) {
		this.regWay = regWay;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public boolean isLuckyDraw() {
		return luckyDraw;
	}

	public void setLuckyDraw(boolean luckyDraw) {
		this.luckyDraw = luckyDraw;
	}

	public void setFunds(BigDecimal[] funds){
		this.funds = funds;
	}

	public boolean needModifyPwd(){
		long day30 = 30*24*60*60;
		if(Integer.parseInt(myId) < 85000 && (modifyTimes == 0 || pwdModifyTime == null || (System.currentTimeMillis() - pwdModifyTime.getTime())/1000 > day30)){
			//没有修改过的或者修改时间大于30天的提示修改一次密码。
			return true;
		}
		return false;
	}

	public boolean getUseSafePwd(){
		String usekey = Cache.Get(use_pwd_key+myId);
		if(usekey == null){
			return true;
		}
		return false;

//		Timestamp now = TimeUtil.getNow();
//		if(!getHasSafePwd() || userContact.getCloseSafePwdTime() == null || (now.getTime() - userContact.getCloseSafePwdTime().getTime()) > 6*60*60*1000){
//			return true;
//		}else{
//			return false;
//		}
	}

	public String getEncryptedPwd(String pwd){
		return UserUtil.newSafeSecretMethod(myId, pwd);
	}

	/***
	 * 新模式加密  两次md5
	 * @param pwd
	 * @return
	 */
	public String getSafeEncryptedPwd(String pwd){
		return UserUtil.newSafeSecretMethod(myId, pwd);
	}

	public boolean getHasSafePwd(){
		if(StringUtils.isNotEmpty(safePwd)) {//有资金安全密码
			String sPwd = this.getEncryptedPwd("");
			if (sPwd.equals(safePwd)) {
				return false;
			}
			return true;
		} else
			return false;
	}

	public boolean isRecNeedReward() {
		return recNeedReward;
	}

	public void setRecNeedReward(boolean recNeedReward) {
		this.recNeedReward = recNeedReward;
	}

	public String getTrueIp() {
		return trueIp;
	}

	public void setTrueIp(String trueIp) {
		this.trueIp = trueIp;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public UserVersion getUserVersion(){
		return (UserVersion) EnumUtils.getEnumByKey(version, UserVersion.class);
	}

	public boolean isZy(){
		if(version == 1){
			return true;
		}else{
			return false;
		}
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Embedded
	private UserContact userContact;// 用户联系信息

	private String sessionKey;

	private boolean isCanCom;// 是否可以评论

	private float goodEvaluate; // 好评率
	private float badEvaluate; // 差评

	private String adminId;
	private AdminUser adminUser;

	private float commission;

	private String showPhoto;
	private boolean useRandom;//是否使用随机功能

	public boolean isUseRandom() {
		return useRandom;
	}

	public void setUseRandom(boolean useRandom) {
		this.useRandom = useRandom;
	}

	public String getCurrencyN() {
		return currencyN;
	}

	public void setCurrencyN(String currencyN) {
		this.currencyN = currencyN;
	}

	public String getShowPhoto() {
		return showPhoto;
	}

	public void setShowPhoto(String showPhoto) {
		this.showPhoto = showPhoto;
	}

	public float getCommission() {
		return commission;
	}

	public void setCommission(float commission) {
		this.commission = commission;
	}

	public AdminUser getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(AdminUser adminUser) {
		this.adminUser = adminUser;
	}

	public String getLanguage() {
		//TODO 暂时只支持英文，如果支持中英繁的话将此段删除
//		language = "en";
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public String getPreviousLoginIp() {
		return previousLoginIp;
	}

	public void setPreviousLoginIp(String previousLoginIp) {
		this.previousLoginIp = previousLoginIp;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public float getGoodEvaluate() {
		return goodEvaluate;
	}

	public void setGoodEvaluate(float goodEvaluate) {
		this.goodEvaluate = goodEvaluate;
	}

	public float getBadEvaluate() {
		return badEvaluate;
	}

	public void setBadEvaluate(float badEvaluate) {
		this.badEvaluate = badEvaluate;
	}

	public Timestamp getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Timestamp uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getCheckPhoto() {
		return checkPhoto;
	}

	public void setCheckPhoto(String checkPhoto) {
		this.checkPhoto = checkPhoto;
	}

	public String getReasons() {
		return reasons;
	}

	public void setReasons(String reasons) {
		this.reasons = reasons;
	}

	// /获取要显示的头像
	public String getShowPhotos(String prefix) {
		if (photo != null && photo.trim().length() > 0) {
			return PathUtil.getPathByFileName(photo, prefix);
			// return "";
		} else {
			return FileConfig.getValue("imgDomain1")+"/up/0/s/313A313A30_8-" + prefix + ".jpg";
		}
	}

	public String getDefaultPhoto() {
		return getShowPhotos("88x88");
	}

	public boolean isCanCom() {
		return isCanCom;
	}

	public void setCanCom(boolean isCanCom) {
		this.isCanCom = isCanCom;
	}

	public String get_Id() {
		return myId;
	}

	public void set_Id(String _id) {
		this.myId = _id;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getRecommendName() {
		return recommendName;
	}

	public void setRecommendName(String recommendName) {
		this.recommendName = recommendName;
	}

	public String getSafePwd() {
		return safePwd;
	}

	public void setSafePwd(String safePwd) {
		this.safePwd = safePwd;
	}

	public UserContact getUserContact() {
		return userContact;
	}

	public void setUserContact(UserContact userContact) {
		this.userContact = userContact;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getBi() {
		return bi;
	}

	public void setBi(double bi) {
		this.bi = bi;
	}

	public int getUtype() {
		return utype;
	}

	public void setUtype(int utype) {
		this.utype = utype;
	}

	public String getRealName() {
		return realName;
	}

	public Timestamp getPreviousLogin() {
		return previousLogin;
	}

	public void setPreviousLogin(Timestamp previousLogin) {
		this.previousLogin = previousLogin;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Timestamp getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Timestamp registerTime) {
		this.registerTime = registerTime;
	}

	public Timestamp getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public long getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(long onlineTime) {
		this.onlineTime = onlineTime;
	}

	public int getSafeLevel() {
//		switch (safeLevel) {
//		case 40:
//			safeLevel = 2;
//			break;
//		case 60:
//			safeLevel = 3;
//			break;
//		case 80:
//			safeLevel = 4;
//			break;
//		case 100:
//			safeLevel = 5;
//			break;
//		default:
//			safeLevel = 1;
//			break;
//		}
		return safeLevel;
	}

	public void setSafeLevel(int safeLevel) {
		this.safeLevel = safeLevel;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getFirstRegTime() {
		return firstRegTime;
	}

	public void setFirstRegTime(String firstRegTime) {
		this.firstRegTime = firstRegTime;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	
	public boolean isLockGoogle(){
		Object current = Cache.GetObj(myId+"_gauth");
		if(current != null){
			int count = Integer.parseInt(current.toString());
			if(count >= ApproveAction.times){
				return true;
			}
		}
		return false;
	}
	
	public boolean isLockMobile(){
		Object current = Cache.GetObj(myId+"_mce");
		if(current != null){
			int count = Integer.parseInt(current.toString());
			if(count >= ApproveAction.times){
				return true;
			}
		}
		return false;
	}

	public boolean isLockPwd() {
		LimitType lt=LimitType.LoginError;
	    int status=lt.GetStatus(myId);
	    if(status==-1){
	    	isLockPwd=true;
	    }
		return isLockPwd;
	}

	public void setLockPwd(boolean isLockPwd) {
		this.isLockPwd = isLockPwd;
	}

	public boolean isLockSafePwd() {
		LimitType lt=LimitType.SafePassError;
	    int status=lt.GetStatus(myId);
	    if(status==-1){
	    	isLockSafePwd=true;
	    }
		return isLockSafePwd;
	}

	public void setLockSafePwd(boolean isLockSafePwd) {
		this.isLockSafePwd = isLockSafePwd;
	}

	public boolean getIsSafePwd() {
		if (safePwd == null || safePwd.length() < 6)
			return false;
		else
			return true;
	}
	public String  getLanVal(String key , String... ss){
		if(language != null && language.equals("cn")){
			language = "cn";
		}else{
			language = "en";
		}
		return Lan.LanguageFormat(language, key, ss);
	}
	public String  getLanVal(String key){
		return getLanVal(key, new String[]{});
	}

	public Timestamp getPwdModifyTime() {
		return pwdModifyTime;
	}

	public void setPwdModifyTime(Timestamp pwdModifyTime) {
		this.pwdModifyTime = pwdModifyTime;
	}

	public int getModifyTimes() {
		return modifyTimes;
	}

	public void setModifyTimes(int modifyTimes) {
		this.modifyTimes = modifyTimes;
	}

	public double getChargeRan() {
		return chargeRan;
	}

	public void setChargeRan(double chargeRan) {
		this.chargeRan = chargeRan;
	}

	public int getPwdLevel() {
		return pwdLevel;
	}

	public void setPwdLevel(int pwdLevel) {
		this.pwdLevel = pwdLevel;
	}

	public int getRepayLock() {
		return repayLock;
	}

	public void setRepayLock(int repayLock) {
		this.repayLock = repayLock;
	}

	public boolean isFreez() {
		return freez;
	}

	public void setFreez(boolean freez) {
		this.freez = freez;
	}

	public int getForbidOutMoney() {
		return forbidOutMoney;
	}

	public void setForbidOutMoney(int forbidOutMoney) {
		this.forbidOutMoney = forbidOutMoney;
	}

	public boolean getAuthStatus(){
		Authentication au = new AuthenticationDao().getByUserId(this.myId);
		if(au != null && au.getStatus() == AuditStatus.pass.getKey()){
			return true;
		}else{
			return false;
		}
	}

	public static void main(String[] args) {
		User user = new User();
		//user.setLanguage("cn");
		log.info(user.getLanVal("您正在进行BTCBT手机认证，验证码为：%%", "1232"));
	}

	public int getAllowOutMoney() {
		return allowOutMoney;
	}

	public String getAllowOutMoneyShow() {
		return EnumUtils.getEnumByKey(allowOutMoney, AuditStatus.class).getValue();
	}

	public void setAllowOutMoney(int allowOutMoney) {
		this.allowOutMoney = allowOutMoney;
	}

	public String getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}

	public String getSubDomain() {
		return subDomain;
	}

	public void setSubDomain(String subDomain) {
		this.subDomain = subDomain;
	}

	public int getSubDomainTimes() {
		return subDomainTimes;
	}

	public void setSubDomainTimes(int subDomainTimes) {
		this.subDomainTimes = subDomainTimes;
	}

	public int getCanService() {
		return canService;
	}

	public void setCanService(int canService) {
		this.canService = canService;
	}

	public String getWeixinOpenId() {
		return weixinOpenId;
	}

	public void setWeixinOpenId(String weixinOpenId) {
		this.weixinOpenId = weixinOpenId;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public String getJpushKey() {
		return jpushKey;
	}

	public void setJpushKey(String jpushKey) {
		this.jpushKey = jpushKey;
	}

	public boolean isBannedService() {
		return bannedService;
	}

	public void setBannedService(boolean bannedService) {
		this.bannedService = bannedService;
	}

	public boolean isUnService() {
		return unService;
	}

	public void setUnService(boolean unService) {
		this.unService = unService;
	}

	public String getWeixinNickname() {
		return weixinNickname;
	}

	public void setWeixinNickname(String weixinNickname) {
		this.weixinNickname = weixinNickname;
	}

	public String getWeixinHeadImgUrl() {
		return weixinHeadImgUrl;
	}

	public void setWeixinHeadImgUrl(String weixinHeadImgUrl) {
		this.weixinHeadImgUrl = weixinHeadImgUrl;
	}

	public boolean getWeixinSubscribe() {
		return weixinSubscribe;
	}

	public void setWeixinSubscribe(boolean weixinSubscribe) {
		this.weixinSubscribe = weixinSubscribe;
	}

	public boolean isLockRecommend() {
		return lockRecommend;
	}

	public void setLockRecommend(boolean lockRecommend) {
		this.lockRecommend = lockRecommend;
	}

	public boolean isDiffAreaLoginNoCheck() {
		return diffAreaLoginNoCheck;
	}

	public void setDiffAreaLoginNoCheck(boolean diffAreaLoginNoCheck) {
		this.diffAreaLoginNoCheck = diffAreaLoginNoCheck;
	}

	public boolean isLoginGoogleAuth() {
		if (userContact.getGoogleAu() != 2) {
			return false;
		}
		return loginGoogleAuth;
	}

	public void setLoginGoogleAuth(boolean loginGoogleAuth) {
		this.loginGoogleAuth = loginGoogleAuth;
	}

	public boolean isPayGoogleAuth() {
		if (userContact.getGoogleAu() != 2) {
			return false;
		}
		return payGoogleAuth;
	}

	public void setPayGoogleAuth(boolean payGoogleAuth) {
		this.payGoogleAuth = payGoogleAuth;
	}

	public Timestamp getSafePwdModifyTime() {
		return safePwdModifyTime;
	}

	public void setSafePwdModifyTime(Timestamp safePwdModifyTime) {
		this.safePwdModifyTime = safePwdModifyTime;
	}

	public int getSafePwdModifyTimes() {
		return safePwdModifyTimes;
	}

	public void setSafePwdModifyTimes(int safePwdModifyTimes) {
		this.safePwdModifyTimes = safePwdModifyTimes;
	}

	public boolean isPayMobileAuth() {
		if (userContact.getMobileStatu() != 2) {//没有认证手机
			return false;
		}
		return payMobileAuth;
	}

	public void setPayMobileAuth(boolean payMobileAuth) {
		this.payMobileAuth = payMobileAuth;
	}

	public boolean isReceiveDealSms() {
		return receiveDealSms;
	}

	public void setReceiveDealSms(boolean receiveDealSms) {
		this.receiveDealSms = receiveDealSms;
	}

	public int getWithdrawSecurityLevel() {
		return withdrawSecurityLevel;
	}

	public void setWithdrawSecurityLevel(int withdrawSecurityLevel) {
		this.withdrawSecurityLevel = withdrawSecurityLevel;
	}

	public int getRegMode() {
		return regMode;
	}

	public void setRegMode(int regMode) {
		this.regMode = regMode;
	}

	public int getVipRate() {
		return vipRate;
	}

	public void setVipRate(int vipRate) {
		this.vipRate = vipRate;
	}

	public int getAuthenType() {
		return authenType;
	}

	public void setAuthenType(int authenType) {
		this.authenType = authenType;
	}

	public boolean isWebLoginAppNotice() {
		return isWebLoginAppNotice;
	}

	public void setWebLoginAppNotice(boolean webLoginAppNotice) {
		isWebLoginAppNotice = webLoginAppNotice;
	}
	
	public boolean isReceiveDealEmail() {
		return receiveDealEmail;
	}

	public void setReceiveDealEmail(boolean receiveDealEmail) {
		this.receiveDealEmail = receiveDealEmail;
	}

	public boolean isReceiveDealPush() {
		return receiveDealPush;
	}

	public void setReceiveDealPush(boolean receiveDealPush) {
		this.receiveDealPush = receiveDealPush;
	}
	
	public double getTotalJifen() {
		return totalJifen;
	}

	public void setTotalJifen(double totalJifen) {
		this.totalJifen = totalJifen;
	}

	@NotSaved
	private String loginAuthenTypeName;
	@NotSaved
	private String tradeAuthenTypeName;
	@NotSaved
	private String withdrawAuthenTypeName;
	@NotSaved
	private String shortRealName;

	public String getLoginAuthenTypeName() {
		loginAuthenTypeName = LoginAuthenType.MAP.get(loginAuthenType);
		return loginAuthenTypeName;
	}

	public String getTradeAuthenTypeName() {
		tradeAuthenTypeName = TradeAuthenType.MAP.get(tradeAuthenType);
		return tradeAuthenTypeName;
	}

	public String getWithdrawAuthenTypeName() {
		withdrawAuthenTypeName = WithdrawAuthenType.MAP.get(withdrawAuthenType);
		return withdrawAuthenTypeName;
	}

	public String getShortRealName() {
		if(shortRealName == null){
			shortRealName = CommonUtil.shortRealName(realName);
		}
		return shortRealName;
	}



	public void setTradeAuthenTypeName(String tradeAuthenTypeName) {
		this.tradeAuthenTypeName = tradeAuthenTypeName;
	}


    public Timestamp getWithdrawAddressAuthenModifyTime() {
        return withdrawAddressAuthenModifyTime;
    }

	public void setWithdrawAuthenTypeName(String withdrawAuthenTypeName) {
		this.withdrawAuthenTypeName = withdrawAuthenTypeName;
	}

    public void setWithdrawAddressAuthenModifyTime(Timestamp withdrawAddressAuthenModifyTime) {
        this.withdrawAddressAuthenModifyTime = withdrawAddressAuthenModifyTime;
    }

    public int getWithdrawAddressAuthenSwitchStatus() {
        return withdrawAddressAuthenSwitchStatus;
    }

    public void setWithdrawAddressAuthenSwitchStatus(int withdrawAddressAuthenSwitchStatus) {
        this.withdrawAddressAuthenSwitchStatus = withdrawAddressAuthenSwitchStatus;
    }

	public boolean isHasMobileCheckBox() {
		return hasMobileCheckBox;
	}

	public void setHasMobileCheckBox(boolean hasMobileCheckBox) {
		this.hasMobileCheckBox = hasMobileCheckBox;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRongCloudToken() {
		return rongCloudToken;
	}

	public void setRongCloudToken(String rongCloudToken) {
		this.rongCloudToken = rongCloudToken;
	}

	public String getCardReson() {
		return cardReson;
	}

	public void setCardReson(String cardReson) {
		this.cardReson = cardReson;
	}

	public Boolean getGuideFlg() {
		return guideFlg;
	}

	public void setGuideFlg(Boolean guideFlg) {
		this.guideFlg = guideFlg;
	}

	public boolean isWarningUser() {
		return warningUser;
	}

	public void setWarningUser(boolean warningUser) {
		this.warningUser = warningUser;
	}

	/***
	 * 返回新老系统密码 ，隔开
	 * @param pwd
	 * @return
	 */
	public String getSafeEncryptedPwdCombin(String pwd){
		return UserUtil.newSafeSecretMethod(myId, pwd) + "," + UserUtil.generateNewPwd(myId, pwd);
	}

}
