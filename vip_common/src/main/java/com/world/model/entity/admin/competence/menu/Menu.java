package com.world.model.entity.admin.competence.menu;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.admin.competence.MenuViewFunction;

/*****
 * 管理员后台管理左侧菜单选项
 * @author Administrator
 *
 */
@Entity(noClassnameStored = true)
public class Menu extends StrBaseLongIdEntity {

	public Menu() {
		this(null);
	}
	
	public Menu(Datastore ds) {
		super(ds);
	}
	
	private String name;//菜单名称
	private String des;//描述
	private List<MenuViewFunction> viewFunctions;//视图函数
	private int sequence = 0;//顺序号
	
	public boolean isInRole;//角色手否拥有
	
	public boolean isInRole() {
		return isInRole;
	}

	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getId() {
		return myId;
	}
	public void setId(String id) {
		this.myId = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public List<MenuViewFunction> getViewFunctions() {
		return viewFunctions;
	}
	public void setViewFunctions(List<MenuViewFunction> viewFunctions) {
		this.viewFunctions = viewFunctions;
	}
}
