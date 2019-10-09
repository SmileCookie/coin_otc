package com.world.web.competence;

import java.io.Serializable;

import com.world.web.ViewCode;
import com.world.web.action.Action;

/******
 * 系统功能  
 * @author Administrator
 *
 */
public class Function implements Serializable{

	public Function() {
		super();
	}

	public Function(String url, ViewCode vc, FunctionGroup fg , String des) {
		super();
		this.url = url;
		this.vc = vc;
		this.fg = fg;
		this.des = des;
	}

	private static final long serialVersionUID = -5543653740566736591L;
	private String url;//访问路径
	private ViewCode vc;//函数信息
	private FunctionGroup fg;//函数组
	public String des;//功能描述
	private boolean inRole;///标记当前角色是否有管理权限

	public boolean isInRole() {
		return inRole;
	}

	public void setInRole(boolean inRole) {
		this.inRole = inRole;
	}

	public boolean isListView(){//是否为列表视图
		if(vc.viewerPath.contains(Action.DEFAULT_INDEX)){
			return true;
		}
		return false;
	}
	
	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ViewCode getVc() {
		return vc;
	}

	public void setVc(ViewCode vc) {
		this.vc = vc;
	}

	public FunctionGroup getFg() {
		return fg;
	}

	public void setFg(FunctionGroup fg) {
		this.fg = fg;
	}
	
}
