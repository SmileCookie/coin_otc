package com.world.web.competence;

import java.util.List;

/****
 * 组层次
 * @author Administrator
 *
 */
public class GroupHierarchy {

	private String des;//本层说明
	private List<GroupHierarchy> sonHierarchys;//子层次
	private FunctionGroup group;//对应函数组
	private String url;
	
	public FunctionGroup getGroup() {
		return group;
	}
	public void setGroup(FunctionGroup group) {
		this.group = group;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public List<GroupHierarchy> getSonHierarchys() {
		return sonHierarchys;
	}
	public void setSonHierarchys(List<GroupHierarchy> sonHierarchys) {
		this.sonHierarchys = sonHierarchys;
	}
}
