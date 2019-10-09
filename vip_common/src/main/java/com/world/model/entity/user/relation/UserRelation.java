package com.world.model.entity.user.relation;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/**
 * 用户关系表，父子关系 
 * @author apple
 */
@Entity(noClassnameStored = true , value = "user_relation")
public class UserRelation extends StrBaseLongIdEntity {
	
	public UserRelation(){
		
	}
	
	public UserRelation(Datastore ds){
		super(ds);
	}
	//主用户Id
	private String userId;
	//子用户Id
	private String subUserId;
	//子用户用户名
	private String userName;
	//角色Id
	private String roleId;
	//是否已经删除
	private int isDeleted;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSubUserId() {
		return subUserId;
	}

	public void setSubUserId(String subUserId) {
		this.subUserId = subUserId;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
