package com.world.web.competence;

import java.io.Serializable;
import java.util.List;

import com.world.config.GlobalConfig;

/*****
 * 功能分组
 * @author Administrator
 *
 */
public class FunctionGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4516267304673539999L;
	private Class classType;//类路径
	private String des;//组功能  说明信息
	private String jspBasePkg;//jsp包
	
	private List<Function> functions;///组下的所有功能
	
	private boolean inRole;//
	private boolean plate;//是否有板块划分
	
	public boolean isPlate() {
		return plate;
	}

	public void setPlate(boolean plate) {
		this.plate = plate;
	}

	public String getUrl(){
		return classType.getName().toLowerCase().replace(GlobalConfig.basePckPath, "").replace(".", "/").replace("/index", "");
	}
	
	public boolean isInRole() {
		return inRole;
	}

	public void setInRole(boolean inRole) {
		this.inRole = inRole;
	}
	
	public String getJspBasePkg() {
		return jspBasePkg;
	}
	public void setJspBasePkg(String jspBasePkg) {
		this.jspBasePkg = jspBasePkg;
	}
	public List<Function> getFunctions() {
		return functions;
	}
	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}
	public Class getClassType() {
		return classType;
	}
	public void setClassType(Class classType) {
		this.classType = classType;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
}
