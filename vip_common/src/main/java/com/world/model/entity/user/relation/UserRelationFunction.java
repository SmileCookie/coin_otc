package com.world.model.entity.user.relation;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/**
 * 用户关系-模块表
 * @author apple
 */
@Entity(noClassnameStored = true , value = "user_relation_function")
public class UserRelationFunction extends StrBaseLongIdEntity {
	
	public UserRelationFunction(){
		
	}
	
	public UserRelationFunction(Datastore ds){
		super(ds);
	}
	
	//用户id
	private String userId;
	//角色Id
	private String roleId;
	//模块Id
	private int function;
	//是否已经删除
	private int isDeleted;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getFunction() {
		return function;
	}

	public void setFunction(int function) {
		this.function = function;
	}

}
