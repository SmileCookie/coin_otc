package com.world.model.entity.admin.competence;


import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/****
 * 当前角色 一级菜单下的视图功能
 * @author Administrator
 *
 */
@Entity(value = "menu_view_function" , noClassnameStored = true)
public class MenuViewFunction extends StrBaseLongIdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6047569430428481392L;
	private String menuId;//所属地额一级菜单ID
	private String roleId;//角色ID
	private String url;//功能url
	private String[] plateDataIds;//模块数据
	
	private int[] functions;//功能模块
	
	private String name;	//
	
	private boolean hasHouzhuiIsCny;//cny后坠的
	private String noParamUrl;//无参数url
	
	public String getNoParamUrl() {
		return noParamUrl;
	}

	public void setNoParamUrl(String noParamUrl) {
		this.noParamUrl = noParamUrl;
	}

	public boolean isHasHouzhuiIsCny() {
		return hasHouzhuiIsCny;
	}

	public void setHasHouzhuiIsCny(boolean hasHouzhuiIsCny) {
		this.hasHouzhuiIsCny = hasHouzhuiIsCny;
	}

	public String[] getPlateDataIds() {
		return plateDataIds;
	}
	
	public String getPlateDataIdsStr() {
		String ids = "";
		if(plateDataIds != null){
			for(String i : plateDataIds){
				ids += "," + i;
			}
		}
		return ids.length() > 0 ? ids.substring(1) : ids;
	}

	public void setPlateDataIds(String[] plateDataIds) {
		this.plateDataIds = plateDataIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MenuViewFunction() {
		this(null);
	}
	
	public MenuViewFunction(Datastore ds) {
		super(ds);
	}

	public String getId() {
		return myId;
	}

	public void setId(String id) {
		this.myId = id;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUrl() {
		return url;
	}
	public String getParams(){return null;}
	public String getPurl(){return null;}
	/***
	 * 获取格式为url的参数
	 * @param index
	 * @return
	 */
	public String getParams(int index) {
		String[] params = null;
		if (url.length() > 0){
			params = url.split("/");
		}
		if(params != null && params.length > 0){
			return params[params.length - index - 1];
		}
		return "";
	}
	
	public String getPurl(int parentIndex) {
		String[] params = null;
		if (url.length() > 0){
			params = url.split("/");
		}
		if(params != null && params.length > 0){
			String newU = "";
			int last = params.length - parentIndex - 1;
			for(int i = 0;i < last;i++){
				if(params[i] != null && params[i].length() > 0){
					newU += "/" + params[i];
				}
			}
			
			return newU;
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int[] getFunctions() {
		return functions;
	}

	public void setFunctions(int[] functions) {
		this.functions = functions;
	}
	
}
