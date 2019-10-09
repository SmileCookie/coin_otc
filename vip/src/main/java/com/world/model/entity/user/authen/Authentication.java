package com.world.model.entity.user.authen;

import com.file.config.FileConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.user.User;
import com.world.util.CommonUtil;

import java.sql.Timestamp;

@Entity(noClassnameStored = true , value = "authentication")
public class Authentication extends StrBaseLongIdEntity{

	private static final long serialVersionUID = -6839179453330559967L;

	public Authentication(){

	}

	public Authentication(Datastore ds){
		super(ds);
	}

	private String userId;
	/**个人用户表示真实姓名，企业用户则表示企业名称*/
	private String realName;
	private String cardId;//身份证号
	private String loadImg;//用户上传的身份证照片（手持身份证）/手持照
	private String frontalImg;//用户上传的身份证照片（正面图）/护照
	private String backImg;//用户上传的身份证照片（背面图）
	private String proofAddressImg;//用户上传的住址证明
	private String photo;//查询返回的结果照片
	private int status;//实名认证的状态
	private boolean simplePass = false;// 是否已经通过初级认证
	private int rechargeNeedAuth;// 需要实名认证后才可以进行充值 0：不需要认证 1：需要初级认证  2：需要高级认证
	private int cashNeedAuth;// 需要实名认证后才可以进行提现  0：不需要认证 1：需要初级认证  2：需要高级认证
	private int authType;// 1.初级认证 2.高级认证 3.最高级认证
	private Timestamp submitTime;//用户提交验证的时间
	private Timestamp checkTime;//审核的时间
	private String auditor;
	private String ip;
	private String reason;
	private boolean isDeleted;
	private int areaInfo;//(1 , "大陆") , gangao(2 , "港澳台湾地区"),haiwai(3, "海外");
	private String countryCode;// 区号
	private String countryName;// 区号
	private int serviceStatu;//服务查询状态
	private String gegisterCity;//针对港澳地区的登记城市
	private String cardType;//证件类型：1-身份证，2-护照
	private String adminId;
	private String imgCode;//查询出来的图片

	private String bankCard;// 持卡人银行卡号
	private String bankTel;// 银行预留手机号
	private String bankCardType;// 银行卡类别 1：借记卡（储蓄卡） 2：贷记卡（信用卡） 0：存折
	private String bankCvv2;// 信用卡背面后三位数字（银行卡类别为信用卡时必填）
	private String bankExpiredate;// 信用卡有效期，格式为MMYY，例如：0418（银行卡类别为信用卡时必填）


	private String bankId;//银行id，用于查出银行名字，图标等，枚举中的值

	private boolean canOperateCny = true;// 是否可以操作人民币  大陆用户默认可以

	/**认证类型，1：个人用户，2：企业用户*/
	private int type = AuditType.individual.getKey();


	private String startDate;	//证件签发日期
	private String endDate;		//证件到期日期
	private String addrImg;		//住址证明照片
	////////////////////////////////////////以下为企业用户信息
	/**法人*/
	private String legalPersonName;
	/**企业注册号*/
	private String  enterpriseRegisterNo;
	/**组织机构代码*/
	private String  organizationCode;
	/**企业注册日期*/
	private Timestamp enterpriseRegisterDate;
	/**企业注册地址*/
	private String enterpriseRegisterAddr;
	/**用户上传的营业执照图片*/
	private String businessLicenseImg;
	/**税务登记证*/
	private String taxRegistrationCertificateImg;
	/**组织机构代码证*/
	private String organizationCodeImg;
	/**用户本人上传的身份证照片（正面图）*/
	private String linkerFrontalImg;
	/**用户本人上传的身份证照片（背面图）*/
	private String linkerBackImg;
	
	private int infoPass = -1;// 基本信息是否通过
	private int idImgPass = -1;// 身份证照片是否通过
	private int bankPass = -1;// 银行信息是否通过 
	private int proofAddressPass = -1;// 住址证明是否通过


	/**
	 * 锁定状态,0正常，1:锁定
	 */
	private int lockStatus;
	/**
	 * 锁定起始时间
	 */
	private Timestamp lockTime;


	public int getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(int lockStatus) {
		this.lockStatus = lockStatus;
	}

	public Timestamp getLockTime() {
		return lockTime;
	}

	public void setLockTime(Timestamp lockTime) {
		this.lockTime = lockTime;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	private int silly;//是否有傻单的权限    1为有权限

	private int sxbAuth;	//十星保状态
	private String sxbReason;	//十星保审批信息
	private Timestamp sxbSubmitTime;	//用户提交验证的时间
	private Timestamp sxbCheckTime;	//审核的时间

	private AdminUser aUser;
	private User user;

	private boolean isCardIdBlackList = false;

	public boolean isSill(){
		if(silly == 1){
			return true;
		}
		return false;
	}

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public String getShowArea(){
		AreaInfo area = (AreaInfo)EnumUtils.getEnumByKey(areaInfo, AreaInfo.class);
		return area != null ? area.getValue() : "";
	}

	// /获取要显示的头像
	public String getShowPhotos() {
		if (photo != null && photo.trim().length() > 0) {

			return FileConfig.getValue("imgDomain1")+"/picauth?file="+photo+"&type=1";
		}
		return "";
	}


	public String getShowFrontalImg(String prefix) {
		if (frontalImg != null && frontalImg.trim().length() > 0) {
			return FileConfig.getValue("imgDomain1")+"/picauth?file="+frontalImg.replace(".", "-88x88.")+"&type=1";
		}
		return "";
	}

	public String getShowBackImg(String prefix) {
		if (backImg != null && backImg.trim().length() > 0) {
			return FileConfig.getValue("imgDomain1")+"/picauth?file="+backImg.replace(".", "-88x88.")+"&type=1";
		}
		return "";
	}

	// /获取要显示的头像
	public String getShowLoadImg(String prefix) {
		if (loadImg != null && loadImg.trim().length() > 0) {
			return FileConfig.getValue("imgDomain1")+"/picauth?file="+loadImg.replace(".", "-88x88.")+"&type=1";
		}
		return "";
	}

	public String getDefaultLoadImg() {
		return getShowLoadImg("88x88");
	}

	public String getDefaultFrontalImg() {
		return getShowFrontalImg("88x88");
	}

	public String getDefaultBackImg() {
		return getShowBackImg("88x88");
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getLoadImg() {
		return loadImg;
	}

	public void setLoadImg(String loadImg) {
		this.loadImg = loadImg;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Timestamp submitTime) {
		this.submitTime = submitTime;
	}

	public Timestamp getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Timestamp checkTime) {
		this.checkTime = checkTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getAreaInfo() {
		return areaInfo;
	}

	public void setAreaInfo(int areaInfo) {
		this.areaInfo = areaInfo;
	}

	public int getServiceStatu() {
		return serviceStatu;
	}

	public void setServiceStatu(int serviceStatu) {
		this.serviceStatu = serviceStatu;
	}

	public String getGegisterCity() {
		return gegisterCity;
	}

	public void setGegisterCity(String gegisterCity) {
		this.gegisterCity = gegisterCity;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public AdminUser getaUser() {
		return aUser;
	}

	public void setaUser(AdminUser aUser) {
		this.aUser = aUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getImgCode() {
		return imgCode;
	}

	public void setImgCode(String imgCode) {
		this.imgCode = imgCode;
	}

	public int getSilly() {
		return silly;
	}

	public void setSilly(int silly) {
		this.silly = silly;
	}

	public int getSxbAuth() {
		return sxbAuth;
	}

	public void setSxbAuth(int sxbAuth) {
		this.sxbAuth = sxbAuth;
	}

	public String getSxbReason() {
		return sxbReason;
	}

	public void setSxbReason(String sxbReason) {
		this.sxbReason = sxbReason;
	}

	public Timestamp getSxbSubmitTime() {
		return sxbSubmitTime;
	}

	public void setSxbSubmitTime(Timestamp sxbSubmitTime) {
		this.sxbSubmitTime = sxbSubmitTime;
	}

	public Timestamp getSxbCheckTime() {
		return sxbCheckTime;
	}

	public void setSxbCheckTime(Timestamp sxbCheckTime) {
		this.sxbCheckTime = sxbCheckTime;
	}

	public String getFrontalImg() {
		return frontalImg;
	}

	public void setFrontalImg(String frontalImg) {
		this.frontalImg = frontalImg;
	}

	public String getBackImg() {
		return backImg;
	}

	public void setBackImg(String backImg) {
		this.backImg = backImg;
	}

	public String getBankTel() {
		return bankTel;
	}

	public void setBankTel(String bankTel) {
		this.bankTel = bankTel;
	}

	public String getBankCardType() {
		return bankCardType;
	}

	public void setBankCardType(String bankCardType) {
		this.bankCardType = bankCardType;
	}

	public String getBankCvv2() {
		return bankCvv2;
	}

	public void setBankCvv2(String bankCvv2) {
		this.bankCvv2 = bankCvv2;
	}

	public String getBankExpiredate() {
		return bankExpiredate;
	}

	public void setBankExpiredate(String bankExpiredate) {
		this.bankExpiredate = bankExpiredate;
	}

	public int getAuthType() {
		return authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
	}

	public int getRechargeNeedAuth() {
		return rechargeNeedAuth;
	}

	public void setRechargeNeedAuth(int rechargeNeedAuth) {
		this.rechargeNeedAuth = rechargeNeedAuth;
	}

	public int getCashNeedAuth() {
		return cashNeedAuth;
	}

	public void setCashNeedAuth(int cashNeedAuth) {
		this.cashNeedAuth = cashNeedAuth;
	}

	public boolean isCanOperateCny() {
//		if (areaInfo != 1 && !canOperateCny) {
//			return false;
//		} else {
//			return true;
//		}
		if (areaInfo == 0 || areaInfo == 1) {
			return true;
		}
		return canOperateCny;
	}

	public void setCanOperateCny(boolean canOperateCny) {
		this.canOperateCny = canOperateCny;
	}

	public String getProofAddressImg() {
		return proofAddressImg;
	}

	public void setProofAddressImg(String proofAddressImg) {
		this.proofAddressImg = proofAddressImg;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isSimplePass() {
		return simplePass;
	}

	public void setSimplePass(boolean simplePass) {
		this.simplePass = simplePass;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getEnterpriseRegisterNo() {
		return enterpriseRegisterNo;
	}

	public void setEnterpriseRegisterNo(String enterpriseRegisterNo) {
		this.enterpriseRegisterNo = enterpriseRegisterNo;
	}

	public Timestamp getEnterpriseRegisterDate() {
		return enterpriseRegisterDate;
	}

	public void setEnterpriseRegisterDate(Timestamp enterpriseRegisterDate) {
		this.enterpriseRegisterDate = enterpriseRegisterDate;
	}

	public String getBusinessLicenseImg() {
		return businessLicenseImg;
	}

	public void setBusinessLicenseImg(String businessLicenseImg) {
		this.businessLicenseImg = businessLicenseImg;
	}

	public String getTaxRegistrationCertificateImg() {
		return taxRegistrationCertificateImg;
	}

	public void setTaxRegistrationCertificateImg(String taxRegistrationCertificateImg) {
		this.taxRegistrationCertificateImg = taxRegistrationCertificateImg;
	}

	public String getOrganizationCodeImg() {
		return organizationCodeImg;
	}

	public void setOrganizationCodeImg(String organizationCodeImg) {
		this.organizationCodeImg = organizationCodeImg;
	}

	public String getLinkerFrontalImg() {
		return linkerFrontalImg;
	}

	public void setLinkerFrontalImg(String linkerFrontalImg) {
		this.linkerFrontalImg = linkerFrontalImg;
	}

	public String getLinkerBackImg() {
		return linkerBackImg;
	}

	public void setLinkerBackImg(String linkerBackImg) {
		this.linkerBackImg = linkerBackImg;
	}

	public String getLegalPersonName() {
		return legalPersonName;
	}

	public void setLegalPersonName(String legalPersonName) {
		this.legalPersonName = legalPersonName;
	}

	public String getEnterpriseRegisterAddr() {
		return enterpriseRegisterAddr;
	}

	public void setEnterpriseRegisterAddr(String enterpriseRegisterAddr) {
		this.enterpriseRegisterAddr = enterpriseRegisterAddr;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public int getInfoPass() {
		return infoPass;
	}

	public void setInfoPass(int infoPass) {
		this.infoPass = infoPass;
	}

	public int getIdImgPass() {
		return idImgPass;
	}

	public void setIdImgPass(int idImgPass) {
		this.idImgPass = idImgPass;
	}

	public int getBankPass() {
		return bankPass;
	}

	public void setBankPass(int bankPass) {
		this.bankPass = bankPass;
	}

	public int getProofAddressPass() {
		return proofAddressPass;
	}

	public void setProofAddressPass(int proofAddressPass) {
		this.proofAddressPass = proofAddressPass;
	}

	public boolean isCardIdBlackList() {
		return isCardIdBlackList;
	}

	public void setCardIdBlackList(boolean cardIdBlackList) {
		isCardIdBlackList = cardIdBlackList;
	}

	public boolean isDaluUser(){
		return  this.getAreaInfo() == AreaInfo.dalu.getKey();
	}

	/**
	 * 是否通过高级
	 * @return
	 */
	public boolean isDepthPass(){
		return this.status == AuditStatus.pass.getKey();
	}

	public String getShortRealName() {
		return CommonUtil.shortRealName(realName);
	}


	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getAddrImg() {
		return addrImg;
	}

	public void setAddrImg(String addrImg) {
		this.addrImg = addrImg;
	}
}
