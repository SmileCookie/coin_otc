package com.world.model.entity.user;

import java.sql.Timestamp;

import com.google.code.morphia.annotations.Embedded;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.BaseEntity;
import com.world.util.date.TimeUtil;

import net.sf.json.JSONArray;

@Embedded
public class UserContact extends BaseEntity{

	/**
	 * 记录用户的联系信息
	 */
	private static final long serialVersionUID = -6839179453330559967L;
	private String safeEmail;//安全邮箱
	private String safeMobile;//手机号
	private Timestamp modifyMobileTime;// 修改手机时间
	private String idCard;//身份证号
	private int cardType;//身份证类型
	private String frontImg;//正面
	private String reverseImg;//反面
	
	private String mCode;//手机号码的国家编号
	
	private String qq;
	private String ww;
	private String msn;
	private String telephone;
	private int province;//省份ID
	private int city;//市
	private int district;//区/县
	private String address;//详细地址
	
	private String zipCode;//邮编
	private Timestamp lastEditeDate;//最后修改时间
	private String emailCode;//邮箱验证码
	private String mobileCode;//手机验证码
	private Timestamp birthday;//生日
	
	private String bankCard;//绑定银行卡号
	
	private String safePwdMiYao;//找回安全密码的临时密钥
	private Timestamp lastMiYaoTime;//创建密钥的时间
	
	private String miyao;
	private String newPwd;//找回密码时生成的新密码，用户亲自启用之后才会生效
	private Timestamp newPwdTime;//找回新密码的时间
	
	private int emailStatu; //status=2说明邮箱已经被验证
	private String checkEmail;
	private Timestamp emailTime;//发送邮件的时间 24小时内效
	private Timestamp mobileCodeEmailTime;//发送手机短信到邮箱的时间
	
	private int mobileStatu;//status=2说明手机已经被验证
	private String checkMobile;
	private Timestamp codeTime;//发送验证码的时间  一分钟后才可以重新发送
	private boolean isReceive;//是否接收短信
	
	private int googleAu;//谷歌双重验证是否开启(0未验证     1验证未开启     2已开启)
	private String secret;
	
	private int cardStatu;//身份认证的状态  0未认证   1等待审核    2通过   3未通过  4 A2未认证  5 A2待审核  6 A2通过  7 A2未通过
	private String checkRealName;//待审核的真实姓名
	private Timestamp applyTime;
	private Timestamp checkTime;
	private String reasons;
	private String memo;
	private int sxbStatu;
	
	private int loginGoogleAuth;	//登录谷歌验证，仅当状态等于2才开启登录谷歌验证 . guosj
	
	private Timestamp closeSafePwdTime;//关闭安全密码的时间，判断时间在两小时之内为关闭，两小时之外或者没有设置时间则为开启
	
	private String emergencyContact;//
	private String emergencyPhone;//

	/*start by xwz*/
	private String loginCheckMobile;//手机号（不带国家码,登录用）
	/*end*/
	private String cardReson;
	private String cardName;

	public String getEmergencyContact() {
		return emergencyContact;
	}

	public void setEmergencyContact(String emergencyContact) {
		this.emergencyContact = emergencyContact;
	}

	public String getEmergencyPhone() {
		return emergencyPhone;
	}

	public void setEmergencyPhone(String emergencyPhone) {
		this.emergencyPhone = emergencyPhone;
	}

	public String getMaoLink(){
		int level = getApproveLevel();
		if(level == 0){
			return "step1";
		}else if(level == 30){
			return "step2";
		}else if(level == 60){
			return "step3";
		}else if(level == 90){
			return "step4";
		}
		return "";
	}
	
	public int getApproveLevel(){
		int level = 0;
		if(emailStatu == AuditStatus.pass.getKey()){
			level = 30;
			if(isPassMobileAuth() || googleAu > 0){
				level = 60;
				if(cardStatu == AuditStatus.pass.getKey()){
					level = 90;
					if(sxbStatu == AuditStatus.pass.getKey()){
						level = 100;
					}
				}
			}
		}
		return level;
	}
	
	public boolean isCanReg(){
		if(emailTime== null)
			return false;


		// TODO: 2017/7/24 suxinjie 初始化运营账号,上线后改为1天 
		if (TimeUtil.getNow().getTime() - emailTime.getTime() > (24 * 60 * 60 * 1000L)) {// 过期了
			return false;
		}
		return true;
	}
	
	public boolean isCanSend(){
		if(emailTime== null)
			return true;
		
		if (TimeUtil.getNow().getTime() - emailTime.getTime() > 2 * 60 * 1000) {
			return true;
		}
		return false;
	}
	/**
	 * 是否可以重新发送短信
	 * @return
	 */
	public boolean isCouldPost(){
		if(codeTime == null)
			return true;
		
		if(codeTime != null && System.currentTimeMillis() - codeTime.getTime() < 2 * 60 * 1000){//验证码发送超过8分钟之后才重新发送
	    	return false;
	    }
		return true;
	}
	
	/**
	 * 是否有效
	 * @return
	 */
	public boolean isEffect(){
		if(codeTime == null)
			return false;
		
		if(codeTime != null && System.currentTimeMillis() - codeTime.getTime() < 20 * 60 * 1000){
	    	return true;
	    }
		return false;
	}
	
	public String getShowEmail(){
		return emailStatu == 2 ? "pass" : "";
	}
	
	public String getShowMobile(){
		return mobileStatu > 0 ? "pass" : "";		
	}

	/**
	 * 短信验证码处是否显示语音短信按钮(目前只中国大陆号码的显示)
	 * @return
	 */
	public boolean isShowAudioButton(){
		boolean flag = true;
		if(safeMobile != null && safeMobile.startsWith("+")){
			if(!safeMobile.startsWith("+86")){
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 是否通过手机验证
	 * @return
	 */
	public boolean isPassMobileAuth(){
		return (mobileStatu == 1 || mobileStatu == 2) ? true : false;
	}

	/**
	 * 是否通过Email验证
	 * @return
	 */
	public boolean isPassEmailAuth(){
		return emailStatu == 2;
	}

	/**
	 * 是否通过手机验证
	 * @return
	 */
	public boolean isOpenGoogleAuth(){
		return googleAu == 2;
	}
	
	public String getShowAuth(){
		return isOpenGoogleAuth() ? "yes" : "no";
	}
	
	public String getShowLoginAuth(){
		return isOpenGoogleAuth() ? "yes" : "no";
	}
	
	public JSONArray getShowMemo(){
		JSONArray jsonArray = JSONArray.fromObject(memo);
		return jsonArray;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getGoogleAu() {
		return googleAu;
	}

	public void setGoogleAu(int googleAu) {
		this.googleAu = googleAu;
	}

	public String getmCode() {
		return mCode;
	}

	public void setmCode(String mCode) {
		this.mCode = mCode;
	}

	public boolean isReceive() {
		return isReceive;
	}

	public void setReceive(boolean isReceive) {
		this.isReceive = isReceive;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getShowCardType(){
		String showType = "";
		switch (cardType) {
		case 0:
			showType = "二代身份证";
			break;
		case 1:
			showType = "一代身份证";
			break;
		case 2:
			showType = "临时身份证";
			break;

		default:
			showType = "无类型";
			break;
		}
		return showType;
	}
	
	public int getEmailStatu() {
		return emailStatu;
	}
	public void setEmailStatu(int emailStatu) {
		this.emailStatu = emailStatu;
	}
	public String getCheckEmail() {
		return checkEmail;
	}
	public void setCheckEmail(String checkEmail) {
		this.checkEmail = checkEmail;
	}
	public int getMobileStatu() {
		return mobileStatu;
	}
	public void setMobileStatu(int mobileStatu) {
		this.mobileStatu = mobileStatu;
	}
	public String getCheckMobile() {
		return checkMobile;
	}
	public void setCheckMobile(String checkMobile) {
		this.checkMobile = checkMobile;
	}
	public String getSafeEmail() {
		return safeEmail;
	}
	public void setSafeEmail(String safeEmail) {
		this.safeEmail = safeEmail;
	}
	public String getMiyao() {
		return miyao;
	}

	public void setMiyao(String miyao) {
		this.miyao = miyao;
	}

	public String getMobileCode() {
		return mobileCode;
	}
	public void setMobileCode(String mobileCode) {
		this.mobileCode = mobileCode;
	}
	public String getSafeMobile() {
		return safeMobile;
	}
	public void setSafeMobile(String safeMobile) {
		this.safeMobile = safeMobile;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getQQ() {
		return qq;
	}
	public void setQQ(String qq) {
		this.qq = qq;
	}
	public String getWW() {
		return ww;
	}
	public void setWW(String ww) {
		this.ww = ww;
	}
	public int getProvince() {
		return province;
	}
	public void setProvince(int province) {
		this.province = province;
	}
	public int getCity() {
		return city;
	}
	public void setCity(int city) {
		this.city = city;
	}
	public int getDistrict() {
		return district;
	}
	public void setDistrict(int district) {
		this.district = district;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public Timestamp getLastEditeDate() {
		return lastEditeDate;
	}
	public void setLastEditeDate(Timestamp lastEditeDate) {
		this.lastEditeDate = lastEditeDate;
	}
	public String getEmailCode() {
		return emailCode;
	}
	public void setEmailCode(String emailCode) {
		this.emailCode = emailCode;
	}
	public Timestamp getBirthday() {
		return birthday;
	}
	public void setBirthday(Timestamp birthday) {
		this.birthday = birthday;
	}
	public String getBankCard() {
		return bankCard;
	}
	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}
	public String getSafePwdMiYao() {
		return safePwdMiYao;
	}
	public void setSafePwdMiYao(String safePwdMiYao) {
		this.safePwdMiYao = safePwdMiYao;
	}
	public Timestamp getLastMiYaoTime() {
		return lastMiYaoTime;
	}
	public void setLastMiYaoTime(Timestamp lastMiYaoTime) {
		this.lastMiYaoTime = lastMiYaoTime;
	}
	public int getCardStatu() {
		return cardStatu;
	}
	public void setCardStatu(int cardStatu) {
		this.cardStatu = cardStatu;
	}

	public String getCheckRealName() {
		return checkRealName;
	}

	public void setCheckRealName(String checkRealName) {
		this.checkRealName = checkRealName;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public String getFrontImg() {
		return frontImg;
	}

	public void setFrontImg(String frontImg) {
		this.frontImg = frontImg;
	}

	public String getReverseImg() {
		return reverseImg;
	}

	public void setReverseImg(String reverseImg) {
		this.reverseImg = reverseImg;
	}

	public Timestamp getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Timestamp applyTime) {
		this.applyTime = applyTime;
	}

	public Timestamp getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Timestamp checkTime) {
		this.checkTime = checkTime;
	}

	public String getReasons() {
		return reasons;
	}

	public void setReasons(String reasons) {
		this.reasons = reasons;
	}

	public Timestamp getEmailTime() {
		return emailTime;
	}

	public void setEmailTime(Timestamp emailTime) {
		this.emailTime = emailTime;
	}

	public Timestamp getCodeTime() {
		return codeTime;
	}

	public void setCodeTime(Timestamp codeTime) {
		this.codeTime = codeTime;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Timestamp getCloseSafePwdTime() {
		return closeSafePwdTime;
	}

	public void setCloseSafePwdTime(Timestamp closeSafePwdTime) {
		this.closeSafePwdTime = closeSafePwdTime;
	}

	public Timestamp getMobileCodeEmailTime() {
		return mobileCodeEmailTime;
	}

	public void setMobileCodeEmailTime(Timestamp mobileCodeEmailTime) {
		this.mobileCodeEmailTime = mobileCodeEmailTime;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public Timestamp getNewPwdTime() {
		return newPwdTime;
	}

	public void setNewPwdTime(Timestamp newPwdTime) {
		this.newPwdTime = newPwdTime;
	}

	public int getLoginGoogleAuth() {
		return loginGoogleAuth;
	}

	public void setLoginGoogleAuth(int loginGoogleAuth) {
		this.loginGoogleAuth = loginGoogleAuth;
	}

	public int getSxbStatu() {
		return sxbStatu;
	}

	public void setSxbStatu(int sxbStatu) {
		this.sxbStatu = sxbStatu;
	}

	public Timestamp getModifyMobileTime() {
		return modifyMobileTime;
	}

	public void setModifyMobileTime(Timestamp modifyMobileTime) {
		this.modifyMobileTime = modifyMobileTime;
	}

	public String getLoginCheckMobile() {
		return loginCheckMobile;
	}

	public void setLoginCheckMobile(String loginCheckMobile) {
		this.loginCheckMobile = loginCheckMobile;
	}

	public String getCardReson() {
		return cardReson;
	}

	public void setCardReson(String cardReson) {
		this.cardReson = cardReson;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
}
