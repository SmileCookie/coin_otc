package com.world.model.entity.user.authen;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;

@Entity(noClassnameStored = true , value = "bankcard")
public class Bankcard extends LongIdEntity{

	private String name;
	private String idcardno;
	private String bankcardno;
	private String tel;
	private String bankCardType;
	private String cvv2;
	private String expireDate;
	private String bankname;
	private String cardname;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdcardno() {
		return idcardno;
	}
	public void setIdcardno(String idcardno) {
		this.idcardno = idcardno;
	}
	public String getBankcardno() {
		return bankcardno;
	}
	public void setBankcardno(String bankcardno) {
		this.bankcardno = bankcardno;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getBankCardType() {
		return bankCardType;
	}
	public void setBankCardType(String bankCardType) {
		this.bankCardType = bankCardType;
	}
	public String getCvv2() {
		return cvv2;
	}
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getCardname() {
		return cardname;
	}
	public void setCardname(String cardname) {
		this.cardname = cardname;
	}
	
}
