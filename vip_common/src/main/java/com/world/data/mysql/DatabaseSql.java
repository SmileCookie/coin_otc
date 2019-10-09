package com.world.data.mysql;

import java.util.List;

public class DatabaseSql {

	public DatabaseSql(String database, boolean batch, String batchSql ,List<OneSql> sqls) {
		super();
		this.database = database;
		this.batch = batch;
		this.batchSql = batchSql;
		this.sqls = sqls;
	}
	private String database;//数据库
	private boolean batch;//是否批量
	private List<OneSql> sqls;
	private String batchSql;//批量时的语句
	
	public String getBatchSql() {
		return batchSql;
	}
	public void setBatchSql(String batchSql) {
		this.batchSql = batchSql;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public boolean isBatch() {
		return batch;
	}
	public void setBatch(boolean batch) {
		this.batch = batch;
	}
	public List<OneSql> getSqls() {
		return sqls;
	}
	public void setSqls(List<OneSql> sqls) {
		this.sqls = sqls;
	}
}
