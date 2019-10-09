package com.world.model.entity.admin.role;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.dao.admin.role.AdminRoleDao;

/*****
 * 系统角色
 * @author Administrator
 *
 */
@Entity(value = "admin_role" , noClassnameStored = true)
public class AdminRole extends StrBaseLongIdEntity {
	
	public AdminRole() {
		this(null);
	}
	
	public AdminRole(Datastore ds) {
		super(ds);
	}
	
	private String roleName;//角色名称
	private Timestamp date;//初始化时间
	private String des;//描述
	private String[] menuIds;//一级菜单Id组
	private String pid;//父类角色ID
	private AdminRole prole;
	
	public AdminRole getProle() {
		if(pid != null && pid.length() > 0 && prole == null){
			AdminRoleDao arDao = new AdminRoleDao();
			prole = arDao.getById(pid);
		}
		return prole;
	}

	public void setProle(AdminRole prole) {
		this.prole = prole;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String[] getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(String[] menuIds) {
		this.menuIds = menuIds;
	}

	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}

	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
    
}
