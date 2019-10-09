package com.world.data.mysql.bean;

import com.world.data.big.table.TableInfo;

public class BeanProxy {

	public BeanProxy(String name, Class<?> classType, TableInfo tableInfo , Class<?> downTableFieldType) {
		this.name = name;
		this.classType = classType;
		this.tableInfo = tableInfo;
		this.downTableFieldType = downTableFieldType;
	}
	public String name;//名字
    public Class<?> classType;//类class字节码
    public TableInfo tableInfo;//注解信息
    public Class<?> downTableFieldType;
    public String[] tablesName;
    
}
