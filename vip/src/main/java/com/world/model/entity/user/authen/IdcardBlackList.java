package com.world.model.entity.user.authen;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@Entity(noClassnameStored = true )
public class IdcardBlackList extends StrBaseLongIdEntity{

	private static final long serialVersionUID = 1L;
	private String cardNo;
	private String remark;
	private Timestamp createTime;

	public IdcardBlackList() {
	}

	public IdcardBlackList(Datastore ds, String cardNo, String remark, Timestamp createTime) {
		super(ds);
		this.cardNo = cardNo;
		this.remark = remark;
		this.createTime = createTime;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
