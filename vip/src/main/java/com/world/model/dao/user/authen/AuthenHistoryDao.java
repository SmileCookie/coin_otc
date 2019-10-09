package com.world.model.dao.user.authen;

import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.authen.AuthenHistory;
import com.world.model.entity.user.authen.Authentication;

public class AuthenHistoryDao extends MongoDao<AuthenHistory, String> {
	
	private static final long serialVersionUID = -6544409483302971734L;

	public AuthenHistory getAuthHis(Authentication auth) {
		AuthenHistory ah = new AuthenHistory(super.getDatastore());
		ah.setUserId(auth.getUserId());
		/**个人用户表示真实姓名，企业用户则表示企业名称*/
		ah.setRealName(auth.getRealName());
		ah.setStartDate(auth.getStartDate());
		ah.setEndDate(auth.getEndDate());
		ah.setCardId(auth.getCardId());
		ah.setLoadImg(auth.getLoadImg());
		ah.setFrontalImg(auth.getFrontalImg());
		ah.setBackImg(auth.getBackImg());
		ah.setProofAddressImg(auth.getProofAddressImg());
		ah.setPhoto(auth.getPhoto());
		ah.setStatus(auth.getStatus());
		ah.setSimplePass(auth.isSimplePass());
		ah.setRechargeNeedAuth(auth.getRechargeNeedAuth());
		ah.setCashNeedAuth(auth.getCashNeedAuth());
		ah.setAuthType(auth.getAuthType());
		ah.setSubmitTime(auth.getSubmitTime());
		ah.setCheckTime(auth.getCheckTime());
		ah.setIp(auth.getIp());
		ah.setReason(auth.getReason());
		ah.setAreaInfo(auth.getAreaInfo());
		ah.setCountryCode(auth.getCountryCode());
		ah.setCountryName(auth.getCountryName());
		ah.setServiceStatu(auth.getServiceStatu());
		ah.setGegisterCity(auth.getGegisterCity());
		ah.setCardType(auth.getCardType());
		ah.setAdminId(auth.getAdminId());
		ah.setImgCode(auth.getImgCode());
		ah.setBankCard(auth.getBankCard());
		ah.setBankTel(auth.getBankTel());
		ah.setBankCardType(auth.getBankCardType());
		ah.setBankCvv2(auth.getBankCvv2());
		ah.setBankExpiredate(auth.getBankExpiredate());
		ah.setBankId(auth.getBankId());
		ah.setType(auth.getType());
		ah.setLegalPersonName(auth.getLegalPersonName());
		ah.setEnterpriseRegisterNo(auth.getEnterpriseRegisterNo());
		ah.setOrganizationCode(auth.getOrganizationCode());
		ah.setEnterpriseRegisterDate(auth.getEnterpriseRegisterDate());
		ah.setEnterpriseRegisterAddr(auth.getEnterpriseRegisterAddr());
		ah.setBusinessLicenseImg(auth.getBusinessLicenseImg());
		ah.setTaxRegistrationCertificateImg(auth.getTaxRegistrationCertificateImg());
		ah.setOrganizationCodeImg(auth.getOrganizationCodeImg());
		ah.setLinkerFrontalImg(auth.getLinkerFrontalImg());
		ah.setLinkerBackImg(auth.getLinkerBackImg());
		ah.setInfoPass(auth.getInfoPass());
		ah.setIdImgPass(auth.getIdImgPass());
		ah.setBankPass(auth.getBankPass());
		ah.setProofAddressPass(auth.getProofAddressPass());
		return ah;
	}
	
	public void updateStatus(String userId, int status, String adminId, String reason) {
		insertOne(userId);
		AuthenHistory authh = super.findOne(super.getQuery().filter("userId", userId).order("-submitTime"));
		Datastore ds = super.getDatastore();
		Query<AuthenHistory> q = ds.find(AuthenHistory.class, "_id", authh.getId());
		UpdateOperations<AuthenHistory> ops = ds.createUpdateOperations(AuthenHistory.class);
		ops.set("status", status);
		ops.set("adminId", adminId);
		ops.set("reason", reason);

		super.update(q, ops);
	}

	public void insertOne(String userId){
		AuthenHistory authh = super.findOne(super.getQuery().filter("userId", userId));
		if(authh == null){
			AuthenHistory ah = new AuthenHistory(super.getDatastore());
			AuthenticationDao auDao = new AuthenticationDao();
			Authentication auth = auDao.findOne(auDao.getQuery().filter("userId", userId).filter("isDeleted", false).order("-submitTime"));
			if (null != auth) {
				ah = getAuthHis(auth);
				save(ah);
			}
		}
	}

	public AuthenHistory getByUserId(String userId){
		AuthenHistory auth = super.findOne(super.getQuery().filter("userId", userId).filter("isDeleted", false));
		return auth;
	}

	/**
	 * 用户提交身份认证信息时更新内容
	 * @param authen
	 * @return
	 */
	public UpdateResults<AuthenHistory> updateAuth(AuthenHistory authen){
		insertOne(authen.getUserId());

		Datastore ds = super.getDatastore();
		Query<AuthenHistory> q = ds.find(AuthenHistory.class, "userId", authen.getUserId());
		UpdateOperations<AuthenHistory> ops = ds.createUpdateOperations(AuthenHistory.class);
		if (StringUtils.isNotBlank(authen.getRealName())) {
			ops.set("realName", authen.getRealName());
		}
		if (StringUtils.isNotBlank(authen.getCardId())) {
			ops.set("cardId", authen.getCardId());
		}
		if (StringUtils.isNotBlank(authen.getLoadImg())) {
			ops.set("loadImg", authen.getLoadImg());
		}
		if (null != authen.getSubmitTime()) {
			ops.set("submitTime", authen.getSubmitTime());
		}
		if (authen.getStatus() > 0) {
			ops.set("status", authen.getStatus());
		}
		if (null != authen.getIp()) {
			ops.set("ip", authen.getIp());
		}
		ops.set("areaInfo", authen.getAreaInfo());
		ops.set("serviceStatu", authen.getServiceStatu());

		if(authen.getImgCode() != null && authen.getImgCode().length() > 0){
			ops.set("imgCode", authen.getImgCode());
			ops.set("photo", authen.getPhoto());
		}
		if (StringUtils.isNotBlank(authen.getCardType())) {
			ops.set("cardType", authen.getCardType());
		}
		if (StringUtils.isNotBlank(authen.getGegisterCity())) {
			ops.set("gegisterCity", authen.getGegisterCity());
		}

		if (StringUtils.isNotBlank(authen.getBankCard())) {
			ops.set("bankCard", authen.getBankCard());
		}
		if (StringUtils.isNotBlank(authen.getBankTel())) {
			ops.set("bankTel", authen.getBankTel());
		}
		if (StringUtils.isNotBlank(authen.getBankCardType())) {
			ops.set("bankCardType", authen.getBankCardType());
		}
		if (StringUtils.isNotBlank(authen.getBankCvv2())) {
			ops.set("bankCvv2", authen.getBankCvv2());
		}
		if (StringUtils.isNotBlank(authen.getBankExpiredate())) {
			ops.set("bankExpiredate", authen.getBankExpiredate());
		}
		if (StringUtils.isNotBlank(authen.getFrontalImg())) {
			ops.set("frontalImg", authen.getFrontalImg());
		}
		if (StringUtils.isNotBlank(authen.getBackImg())) {
			ops.set("backImg", authen.getBackImg());
		}
		if (StringUtils.isNotBlank(authen.getProofAddressImg())) {
			ops.set("proofAddressImg", authen.getProofAddressImg());
		}
		if (StringUtils.isNotBlank(authen.getCountryCode())) {
			ops.set("countryCode", authen.getCountryCode());
		}
		ops.set("simplePass", authen.isSimplePass());
//		ops.set("rechargeNeedAuth", authen.getRechargeNeedAuth());
//		ops.set("cashNeedAuth", authen.getCashNeedAuth());

		ops.set("type", authen.getType());

		if(StringUtils.isNotBlank(authen.getLegalPersonName())) {
			ops.set("legalPersonName", authen.getLegalPersonName());
		}
		if(StringUtils.isNotBlank(authen.getEnterpriseRegisterNo())) {
			ops.set("enterpriseRegisterNo", authen.getEnterpriseRegisterNo());
		}
		if(StringUtils.isNotBlank(authen.getOrganizationCode())) {
			ops.set("organizationCode", authen.getOrganizationCode());
		}
		if(null != authen.getEnterpriseRegisterDate()) {
			ops.set("enterpriseRegisterDate", authen.getEnterpriseRegisterDate());
		}
		if(StringUtils.isNotBlank(authen.getEnterpriseRegisterAddr())) {
			ops.set("enterpriseRegisterAddr", authen.getEnterpriseRegisterAddr());
		}
		if(StringUtils.isNotBlank(authen.getBusinessLicenseImg())) {
			ops.set("businessLicenseImg", authen.getBusinessLicenseImg());
		}
		if(StringUtils.isNotBlank(authen.getTaxRegistrationCertificateImg())) {
			ops.set("taxRegistrationCertificateImg", authen.getTaxRegistrationCertificateImg());
		}
		if(StringUtils.isNotBlank(authen.getOrganizationCodeImg())) {
			ops.set("organizationCodeImg", authen.getOrganizationCodeImg());
		}
		if(StringUtils.isNotBlank(authen.getLinkerFrontalImg())) {
			ops.set("linkerFrontalImg", authen.getLinkerFrontalImg());
		}
		if(StringUtils.isNotBlank(authen.getLinkerBackImg())) {
			ops.set("linkerBackImg", authen.getLinkerBackImg());
		}

		UpdateResults<AuthenHistory> ur = super.update(q, ops);
		return ur;
	}

	public UpdateResults<AuthenHistory> updateNeedAuth(int userId, String type, int needAuth){
		insertOne(userId + "");

		Datastore ds = super.getDatastore();
		Query<AuthenHistory> q = ds.find(AuthenHistory.class, "userId", userId + "");
		UpdateOperations<AuthenHistory> ops = ds.createUpdateOperations(AuthenHistory.class);
		if ("1".equals(type)) {
			ops.set("rechargeNeedAuth", needAuth);
		} else {
			ops.set("cashNeedAuth", needAuth);
		}
		UpdateResults<AuthenHistory> ur = super.update(q, ops);
		return ur;
	}

	public UpdateResults<AuthenHistory> updateSxbAuth(AuthenHistory authen){
		insertOne(authen.getUserId());

		Datastore ds = super.getDatastore();
		Query<AuthenHistory> q = ds.find(AuthenHistory.class, "userId", authen.getUserId());
		UpdateOperations<AuthenHistory> ops = ds.createUpdateOperations(AuthenHistory.class);
		ops.set("sxbAuth", authen.getSxbAuth());
		ops.set("sxbSubmitTime", now());

		UpdateResults<AuthenHistory> ur = super.update(q, ops);
		return ur;
	}

	/**
	 * 身份信息查询成功之后保存返回的头像
	 * @param photo
	 * @param userId
	 * @return
	 */
	public UpdateResults<AuthenHistory> updatePhoto(String photo, String userId){
		insertOne(userId);

		Datastore ds = super.getDatastore();
		Query<AuthenHistory> q = ds.find(AuthenHistory.class, "userId", userId);
		UpdateOperations<AuthenHistory> ops = ds.createUpdateOperations(AuthenHistory.class);
		ops.set("photo", photo);

		UpdateResults<AuthenHistory> ur = super.update(q, ops);
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

			Query<AuthenHistory> q = getQuery(AuthenHistory.class);
			q.filter("cardId", cardId);
			q.filter("isDeleted", false);

			if(userId != null){

				q.field("userId").notEqual(userId);
			}

			AuthenHistory au = super.findOne(q);
			if(au == null)
				return true;
			else
				return false;
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return false;
	}
}
