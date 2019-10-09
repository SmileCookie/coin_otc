package com.world.model.entity.user.relation;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/**
 * 用户关系-角色表
 * @author apple
 */
@Entity(noClassnameStored = true , value = "user_relation_role")
public class UserRelationRole extends StrBaseLongIdEntity {
	
	public UserRelationRole(){
		
	}
	
	public UserRelationRole(Datastore ds){
		super(ds);
	}
	
	private String userId;
	
	private String name;

	//是否已经删除
	public int isDeleted;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
}
