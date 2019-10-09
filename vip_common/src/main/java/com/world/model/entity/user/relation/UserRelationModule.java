package com.world.model.entity.user.relation;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/**
 * 用户关系-模块表
 * @author apple
 */
@Entity(noClassnameStored = true , value = "user_relation_module")
public class UserRelationModule extends StrBaseLongIdEntity {
	
	public UserRelationModule(){
		
	}
	
	public UserRelationModule(Datastore ds){
		super(ds);
	}
	
	//用户id
	private String userId;
	//角色Id
	private String roleId;
	//模块Id
	private String module;
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

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
}
