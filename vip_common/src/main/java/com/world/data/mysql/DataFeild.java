package com.world.data.mysql;
/**
 * 功能:数据库字段类型类,用于保存字段类型
 * @author Administrator
 */
public class DataFeild {
	//字段名称
	public String feildName;
	//字段长度
	public int length;
	//是否允许为空
	public boolean isNull;
	//字段类型
	public FeildType feildType;
	//是否是自动增长列
	public boolean idAuto=false;
	//是否是主键
	public boolean isKey=false;
	//实例value
	public String defaultValue="";
	//字段注释
	public String comment="";
}

