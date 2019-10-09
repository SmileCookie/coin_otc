package com.world.data.mysql;

import java.io.Serializable;

/********
 * 封装一条事物执行语句
 * @author Administrator
 *
 */
public class OneSql implements Serializable{
	public OneSql(){}
	public OneSql(String sql, int effectRows) {
		super();
		this.sql = sql;
		this.effectRows = effectRows;
	}
	public OneSql(String sql, int effectRows,Object[] prams) {
		this(sql, effectRows);
		this.prams=prams;
	}
	
	public OneSql(String sql, int effectRows,Object[] prams , String database) {
		this(sql, effectRows , prams);
		this.database = database;
	}
	
	public OneSql(String sql, int effectRows,Object[] prams , String database , boolean batch) {
		this(sql, effectRows , prams, database);
		this.batch = batch;
	}
	
	private String sql;//涉及的sql语句   刷新内存的Sql   UPDATE MEMCACHE
	private int effectRows;//sql语句必须影响的行数，-1表示大于0即可    -2表示无限制
	private Object[] prams;///sql语句参数
	private String database = connection.groupName_static;//所在的数据库
	private boolean batch;
	
	public boolean isBatch() {
		return batch;
	}
	public void setBatch(boolean batch) {
		this.batch = batch;
	}
	public String getDatabase() {
		if(database.equals(connection.groupName_static)){
			database = "vip_main";
		}
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public Object[] getPrams() {
		return prams;
	}
	public void setPrams(Object[] prams) {
		this.prams = prams;
	}
	public String getSql() {
		return sql;
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}
	public int getEffectRows() {
		return effectRows;
	}


	public void setEffectRows(int effectRows) {
		this.effectRows = effectRows;
	}

	/*********
	 * 事务是否可继续进行
	 * @param efRows  范围限制在int类型且大于等于0
	 * @return
	 */
	public boolean ifCanNext(int efRows){
		if(efRows>=0){
			if(effectRows==-2){//无限制
				return true;
			}else if(effectRows==-1){//大于0即可
				if(efRows>0){
					return true;
				}
			}else if(effectRows==efRows){//需求影响行数
				return true;
			}
		}
		return false;
	}
	private static final long serialVersionUID = 5748779308880396497L;
}
