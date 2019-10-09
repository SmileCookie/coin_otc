package com.world.model.entity.level;

import com.google.code.morphia.Datastore;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.SysEnum;
import com.world.model.enums.CoinChargeStatus;

public class IntegralRule extends StrBaseLongIdEntity{

	public IntegralRule(){
		super(null);
	}

	public IntegralRule(Datastore ds) {
		super(ds);
	}

	//积分规则配置
	private int seqNo;//序号
	private String type;//类型

	private String rule;//规则
	private String memo;//备注说明

	/*start by xwz 20170627*/
	private String typeCode;//类型代码
	private String score;			//积分
	private int integType;	//积分类型（1：一次性，2：周期性，3：每次）
	private String period;		//周期（"d":天）
	/*end*/
//	private String operType;	//操作类型

	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public int getIntegType() {
		return integType;
	}

	//获取积分类型描述
	public String getIntegTypeShow(){
		SysEnum sysEnum = EnumUtils.getEnumByKey(integType, IntegType.class);
		return sysEnum.getValue();
	}

	public void setIntegType(int integType) {
		this.integType = integType;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	//	public String getOperType() {
//		return operType;
//	}
//
//	public void setOperType(String operType) {
//		this.operType = operType;
//	}
}
