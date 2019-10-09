package com.cg.module;

/****
 * 模版类型
 * @author Administrator
 *
 */
public enum ModuleType {

	AORU("aoru.jsp") , LIST("list.jsp") , AJAX("ajax.jsp") , DAO("ModuleDao.java") , ACTION("Index.java");
	
	private ModuleType(String path) {
		this.path = path;
	}

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
