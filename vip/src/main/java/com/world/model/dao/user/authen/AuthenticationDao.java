package com.world.model.dao.user.authen;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.constant.Const;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.user.authen.AuthenHistory;
import com.world.model.entity.user.authen.Authentication;
import com.world.web.action.Action;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

public class AuthenticationDao extends MongoDao<Authentication, String> implements Action{
	
	public void insertOne(String userId){
		Authentication auth = super.findOne(super.getQuery().filter("userId", userId).filter("isDeleted", false));
		if(auth == null){
			Authentication au = new Authentication(super.getDatastore());
			au.setDeleted(false);
			au.setUserId(userId);
			save(au);
		}
	}
	
	public Authentication getByUserId(String userId){
		Authentication auth = super.findOne(super.getQuery().filter("userId", userId).filter("isDeleted", false));
		return auth;
	}

	public Authentication getByUserId(String userId,int status){
		Query<Authentication> q = getQuery(Authentication.class);
		q.filter("userId", userId);
		q.filter("isDeleted", false);
		q.field("status").notEqual(status);

		Authentication auth = super.findOne(q);
//		Authentication auth = super.findOne(super.getQuery().filter("userId", userId).filter("isDeleted", false).filter("status",status));
		return auth;
	}
	
	/**
	 * 用户提交身份认证信息时更新内容
	 * @param authen
	 * @return
	 */
	public UpdateResults<Authentication> updateAuth(Authentication authen){
		insertOne(authen.getUserId());
		Datastore ds = super.getDatastore();
		Query<Authentication> q = ds.find(Authentication.class, "userId", authen.getUserId());  
		UpdateOperations<Authentication> ops = ds.createUpdateOperations(Authentication.class);
		ops.set("realName", authen.getRealName());
		ops.set("cardId", authen.getCardId()==null?"":authen.getCardId());
		ops.set("submitTime", authen.getSubmitTime());
		ops.set("status", authen.getStatus());
		ops.set("ip", authen.getIp());
		ops.set("areaInfo", authen.getAreaInfo());
		ops.set("serviceStatu", authen.getServiceStatu());
		ops.set("authType", authen.getAuthType());
		ops.set("type", authen.getType());
		ops.set("countryCode",authen.getCountryCode());
		ops.set("countryName",authen.getCountryName());
		ops.set("lockStatus",0);
		ops.set("cardType",authen.getCardType());
		if(StringUtils.isNotBlank(authen.getStartDate())){
			ops.set("startDate",authen.getStartDate());
		}
		if(StringUtils.isNotBlank(authen.getEndDate())){
			ops.set("endDate",authen.getEndDate());
		}

		if (StringUtils.isNotBlank(authen.getLoadImg())) {
			ops.set("loadImg", authen.getLoadImg());
		}
		if (StringUtils.isNotBlank(authen.getFrontalImg())) {
			ops.set("frontalImg", authen.getFrontalImg());
		}
		if (StringUtils.isNotBlank(authen.getBackImg())) {
			ops.set("backImg", authen.getBackImg());
		}
		/*start by xwz 2017-06-09*/
//		if(authen.getImgCode() != null && authen.getImgCode().length() > 0){
//			ops.set("imgCode", authen.getImgCode());
//			ops.set("photo", authen.getPhoto());
//		}
//		ops.set("cardType", authen.getCardType()==null?"":authen.getCardType());
//		ops.set("gegisterCity", authen.getGegisterCity()==null?"":authen.getGegisterCity());
		/*end*/


		/*start by xwz 2017-06-09：增加证件的签发日和到期日*//*
		if(StringUtils.isNotBlank(authen.getStartDate())){
			ops.set("startDate",authen.getStartDate());
		}
		if(StringUtils.isNotBlank(authen.getEndDate())){
			ops.set("endDate",authen.getEndDate());
		}
		*//*end*//*

		if(StringUtils.isNotBlank(authen.getAddrImg())){
			ops.set("addrImg", authen.getAddrImg());
		}*/

		/*if (StringUtils.isNotBlank(authen.getCountryCode())) {
			ops.set("countryCode", authen.getCountryCode());
		}*/
		/*start by xwz 2017-06-09*/
//		if (StringUtils.isNotBlank(authen.getProofAddressImg())) {
//			ops.set("proofAddressImg", authen.getProofAddressImg());
//		}
//		ops.set("simplePass", authen.isSimplePass());
//
//		//个人银行卡信息
//		if (StringUtils.isNotBlank(authen.getBankCard())) {
//			ops.set("bankCard", authen.getBankCard());
//		}
//		if (StringUtils.isNotBlank(authen.getBankTel())) {
//			ops.set("bankTel", authen.getBankTel());
//		}
//		if (StringUtils.isNotBlank(authen.getBankCardType())) {
//			ops.set("bankCardType", authen.getBankCardType());
//		}
//		if (StringUtils.isNotBlank(authen.getBankCvv2())) {
//			ops.set("bankCvv2", authen.getBankCvv2());
//		}
//		if (StringUtils.isNotBlank(authen.getBankExpiredate())) {
//			ops.set("bankExpiredate", authen.getBankExpiredate());
//		}
//
//		//企业认证信息
//		if(StringUtils.isNotBlank(authen.getLegalPersonName())) {
//			ops.set("legalPersonName", authen.getLegalPersonName());
//		}
//		if(StringUtils.isNotBlank(authen.getEnterpriseRegisterNo())) {
//			ops.set("enterpriseRegisterNo", authen.getEnterpriseRegisterNo());
//		}
//		if(StringUtils.isNotBlank(authen.getOrganizationCode())) {
//			ops.set("organizationCode", authen.getOrganizationCode());
//		}
//		if(null != authen.getEnterpriseRegisterDate()) {
//			ops.set("enterpriseRegisterDate", authen.getEnterpriseRegisterDate());
//		}
//		if(StringUtils.isNotBlank(authen.getEnterpriseRegisterAddr())) {
//			ops.set("enterpriseRegisterAddr", authen.getEnterpriseRegisterAddr());
//		}
//		if(StringUtils.isNotBlank(authen.getBusinessLicenseImg())) {
//			ops.set("businessLicenseImg", authen.getBusinessLicenseImg());
//		}
//		if(StringUtils.isNotBlank(authen.getTaxRegistrationCertificateImg())) {
//			ops.set("taxRegistrationCertificateImg", authen.getTaxRegistrationCertificateImg());
//		}
//		if(StringUtils.isNotBlank(authen.getOrganizationCodeImg())) {
//			ops.set("organizationCodeImg", authen.getOrganizationCodeImg());
//		}
//		if(StringUtils.isNotBlank(authen.getLinkerFrontalImg())) {
//			ops.set("linkerFrontalImg", authen.getLinkerFrontalImg());
//		}
//		if(StringUtils.isNotBlank(authen.getLinkerBackImg())) {
//			ops.set("linkerBackImg", authen.getLinkerBackImg());
//		}
//		ops.set("isCardIdBlackList", authen.isCardIdBlackList());
//		if(StringUtils.isNotBlank(authen.getReason())) {
//			ops.set("reason", authen.getReason());
//		}
		/*end*/
		UpdateResults<Authentication> ur = super.update(q, ops);
		return ur;
	}



	/**
	 * 用户提交图片认证信息时更新内容
	 * @param authen
	 * @return
	 */
	public UpdateResults<Authentication> updateAuthImg(Authentication authen){
		insertOne(authen.getUserId());

		Datastore ds = super.getDatastore();
		Query<Authentication> q = ds.find(Authentication.class, "userId", authen.getUserId());
		UpdateOperations<Authentication> ops = ds.createUpdateOperations(Authentication.class);
		if (StringUtils.isNotBlank(authen.getLoadImg())) {
			ops.set("loadImg", authen.getLoadImg());
		}
		if (StringUtils.isNotBlank(authen.getFrontalImg())) {
			ops.set("frontalImg", authen.getFrontalImg());
		}
		if (StringUtils.isNotBlank(authen.getBackImg())) {
			ops.set("backImg", authen.getBackImg());
		}
		/*start by xwz 2017-06-09*/
//		if(authen.getImgCode() != null && authen.getImgCode().length() > 0){
//			ops.set("imgCode", authen.getImgCode());
//			ops.set("photo", authen.getPhoto());
//		}
//		ops.set("cardType", authen.getCardType()==null?"":authen.getCardType());
//		ops.set("gegisterCity", authen.getGegisterCity()==null?"":authen.getGegisterCity());
		/*end*/


		/*start by xwz 2017-06-09：增加证件的签发日和到期日*//*
		if(StringUtils.isNotBlank(authen.getStartDate())){
			ops.set("startDate",authen.getStartDate());
		}
		if(StringUtils.isNotBlank(authen.getEndDate())){
			ops.set("endDate",authen.getEndDate());
		}
		*//*end*//*
		if (StringUtils.isNotBlank(authen.getLoadImg())) {
			ops.set("loadImg", authen.getLoadImg());
		}
		if (StringUtils.isNotBlank(authen.getFrontalImg())) {
			ops.set("frontalImg", authen.getFrontalImg());
		}
		if (StringUtils.isNotBlank(authen.getBackImg())) {
			ops.set("backImg", authen.getBackImg());
		}

		if(StringUtils.isNotBlank(authen.getAddrImg())){
			ops.set("addrImg", authen.getAddrImg());
		}*/

		/*if (StringUtils.isNotBlank(authen.getCountryCode())) {
			ops.set("countryCode", authen.getCountryCode());
		}*/
		/*start by xwz 2017-06-09*/
//		if (StringUtils.isNotBlank(authen.getProofAddressImg())) {
//			ops.set("proofAddressImg", authen.getProofAddressImg());
//		}
//		ops.set("simplePass", authen.isSimplePass());
//
//		//个人银行卡信息
//		if (StringUtils.isNotBlank(authen.getBankCard())) {
//			ops.set("bankCard", authen.getBankCard());
//		}
//		if (StringUtils.isNotBlank(authen.getBankTel())) {
//			ops.set("bankTel", authen.getBankTel());
//		}
//		if (StringUtils.isNotBlank(authen.getBankCardType())) {
//			ops.set("bankCardType", authen.getBankCardType());
//		}
//		if (StringUtils.isNotBlank(authen.getBankCvv2())) {
//			ops.set("bankCvv2", authen.getBankCvv2());
//		}
//		if (StringUtils.isNotBlank(authen.getBankExpiredate())) {
//			ops.set("bankExpiredate", authen.getBankExpiredate());
//		}
//
//		//企业认证信息
//		if(StringUtils.isNotBlank(authen.getLegalPersonName())) {
//			ops.set("legalPersonName", authen.getLegalPersonName());
//		}
//		if(StringUtils.isNotBlank(authen.getEnterpriseRegisterNo())) {
//			ops.set("enterpriseRegisterNo", authen.getEnterpriseRegisterNo());
//		}
//		if(StringUtils.isNotBlank(authen.getOrganizationCode())) {
//			ops.set("organizationCode", authen.getOrganizationCode());
//		}
//		if(null != authen.getEnterpriseRegisterDate()) {
//			ops.set("enterpriseRegisterDate", authen.getEnterpriseRegisterDate());
//		}
//		if(StringUtils.isNotBlank(authen.getEnterpriseRegisterAddr())) {
//			ops.set("enterpriseRegisterAddr", authen.getEnterpriseRegisterAddr());
//		}
//		if(StringUtils.isNotBlank(authen.getBusinessLicenseImg())) {
//			ops.set("businessLicenseImg", authen.getBusinessLicenseImg());
//		}
//		if(StringUtils.isNotBlank(authen.getTaxRegistrationCertificateImg())) {
//			ops.set("taxRegistrationCertificateImg", authen.getTaxRegistrationCertificateImg());
//		}
//		if(StringUtils.isNotBlank(authen.getOrganizationCodeImg())) {
//			ops.set("organizationCodeImg", authen.getOrganizationCodeImg());
//		}
//		if(StringUtils.isNotBlank(authen.getLinkerFrontalImg())) {
//			ops.set("linkerFrontalImg", authen.getLinkerFrontalImg());
//		}
//		if(StringUtils.isNotBlank(authen.getLinkerBackImg())) {
//			ops.set("linkerBackImg", authen.getLinkerBackImg());
//		}
//		ops.set("isCardIdBlackList", authen.isCardIdBlackList());
//		if(StringUtils.isNotBlank(authen.getReason())) {
//			ops.set("reason", authen.getReason());
//		}
		/*end*/
		UpdateResults<Authentication> ur = super.update(q, ops);
		return ur;
	}
	
	public UpdateResults<Authentication> updateSxbAuth(Authentication authen){
		insertOne(authen.getUserId());
		
		Datastore ds = super.getDatastore();
		Query<Authentication> q = ds.find(Authentication.class, "userId", authen.getUserId());  
		UpdateOperations<Authentication> ops = ds.createUpdateOperations(Authentication.class);
		ops.set("sxbAuth", authen.getSxbAuth());
		ops.set("sxbSubmitTime", now());
		
		UpdateResults<Authentication> ur = super.update(q, ops);
		return ur;
	}
	
	/**
	 * 身份信息查询成功之后保存返回的头像
	 * @param photo
	 * @param userId
	 * @return
	 */
	public UpdateResults<Authentication> updatePhoto(String photo, String userId){
		insertOne(userId);
		
		Datastore ds = super.getDatastore();
		Query<Authentication> q = ds.find(Authentication.class, "userId", userId);  
		UpdateOperations<Authentication> ops = ds.createUpdateOperations(Authentication.class);
		ops.set("photo", photo);

		UpdateResults<Authentication> ur = super.update(q, ops);
		return ur;
	}
	
	/**
	 * 验证身份证号是否存在，不判断用户
	 * @param cardId
	 * @return
	 */
	public boolean cardIdValidated(String cardId){
		return cardIdValidated(cardId, null);
	}
	
	/**
	 * 验证身份证号是否存在，除去用户自己
	 * @param cardId
	 * @return
	 */
	public boolean cardIdValidated(String cardId, String userId){
		try {
			
			Query<Authentication> q = getQuery(Authentication.class);
			q.filter("cardId", cardId);
			q.filter("isDeleted", false);
			
			if(userId != null){
				
				q.field("userId").notEqual(userId);
			}
			Authentication au = super.findOne(q);
			if(au == null){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return false;
	}
	
	/**
	 * 是否证件号码受限
	 * @param cardId
	 * @param userId
	 * @return
	 */
	public boolean isLimit(String cardId, String userId){
		Query<Authentication> q1 = this.getQuery().filter("cardId", cardId).filter("userId !=", userId);
		long amount = this.count(q1);
		return amount >= Const.MAX_AUTH_AMOUNT;
	}
	
	public boolean isExistsIdcard(String cardId){
		Query q1 = this.getQuery().filter("cardId", cardId);
		return q1.countAll() > 0;
	}

	public boolean hasDepthAuthen(String userId){
		Authentication au = this.getByUserId(userId);
		return au != null && au.getStatus() == AuditStatus.pass.getKey();
	}
	
	public UpdateResults<Authentication> updateNeedAuth(int userId, String type, int needAuth){
		insertOne(userId + "");

		Datastore ds = super.getDatastore();
		Query<Authentication> q = ds.find(Authentication.class, "userId", userId + "");
		UpdateOperations<Authentication> ops = ds.createUpdateOperations(Authentication.class);
		if ("1".equals(type)) {
			ops.set("rechargeNeedAuth", needAuth);
		} else {
			ops.set("cashNeedAuth", needAuth);
		}
		UpdateResults<Authentication> ur = super.update(q, ops);
		return ur;
	}
}
