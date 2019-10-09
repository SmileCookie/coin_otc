package com.world.model.entity.admin;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;


/******
 * 管理员操作权限 
 * @author Administrator
 *
 */
@Entity(value = "admin_permissions" , noClassnameStored = true)
public class AdminPermissions extends StrBaseLongIdEntity{
	
	public AdminPermissions(){super(null);}
	public AdminPermissions(Datastore ds) {
		super(ds);
	}
	
	private static final long serialVersionUID = 166739080269646448L;
	private String permiId;
	private String authorityId;
	private String permiUserId;
	private int typeId;
	
	public String getPermiId() {
		return permiId;
	}
	public void setPermiId(String permiId) {
		this.permiId = permiId;
	}
	public String getAuthorityId() {
		return authorityId;
	}
	public void setAuthorityId(String authorityId) {
		this.authorityId = authorityId;
	}
	public String getPermiUserId() {
		return permiUserId;
	}
	public void setPermiUserId(String permiUserId) {
		this.permiUserId = permiUserId;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	
}
