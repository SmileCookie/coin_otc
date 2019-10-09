package com.world.model.entity.user;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.EnumUtils;
import com.world.util.string.KeyWordType;

/****
 * 用户主表数据结构
 * 
 * @author Administrator
 * 
 */
@Entity(noClassnameStored = true , value = "keyword")
public class Keyword extends StrBaseLongIdEntity {
	public Keyword() {
	}

	public Keyword(Datastore ds) {
		super(ds);
	}

	private static final long serialVersionUID = -2386115543279873656L;
	
	private String word;
	private Timestamp adate;
	private int typeId;
	public KeyWordType getkType() {
		return (KeyWordType)EnumUtils.getEnumByKey(typeId, KeyWordType.class);
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Timestamp getAdate() {
		return adate;
	}

	public void setAdate(Timestamp adate) {
		this.adate = adate;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	
	
}
