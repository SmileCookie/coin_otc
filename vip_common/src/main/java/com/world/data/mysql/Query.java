package com.world.data.mysql;

import java.util.List;

import org.apache.log4j.Logger;

import com.world.data.big.MysqlDownTable;

/*****
 * 数据库操作
 * @author Administrator
 *
 */
public class Query<T extends Bean> {
	protected static Logger log = Logger.getLogger(Query.class.getName());
	public Query(){}
	
	public Query(String sql,Object[] params,Class<T> cls) {
		super();
		this.sql=sql;
		this.params=params;
		this.cls=cls;
	}
	
	public Query(String database , String sql,Object[] params,Class<T> cls) {
		this(sql, params, cls);
		this.database = database;
	}
	
	private String sql;//要查询语句
	private Object[] params;//参数列表
	private Class<T> cls;//对应的实体类
	private String database = "default";//数据库
	private QueryDataType queryDataType = QueryDataType.DEFAULT;

	public QueryDataType getQueryDataType() {
		return queryDataType;
	}

	public void setQueryDataType(QueryDataType queryDataType) {
		this.queryDataType = queryDataType;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public Class<?> getCls() {
		return cls;
	}

	public Query<T> setCls(Class<T> cls) {
		this.cls = cls;
		return this;
	}

	public String getSql() {
		return sql;
	}
	public String append(String condition){
		return appendSql(condition);
	}
	public String appendSql(String condition) {//添加条件
		condition = " " + MysqlDownTable.getStandardSql(condition);
		
		if(condition.indexOf("order") > -1){
			sql += " " + condition;
		}else{
			int whereIndex = sql.indexOf("where");
			int onIndex = sql.indexOf(" on ");
			if(whereIndex > -1 || onIndex > -1){
				if(condition.indexOf(" and ") > -1){
					sql += condition;
				}else{
					sql += " and " + condition;
				}
			}else{
				if(condition.toLowerCase().trim().indexOf("and ") == 0){
					condition = condition.replaceFirst("and", "");//去除and
				}
				sql += " where " + condition + " ";
			}
		}
		return sql;
	}
	
	public Query<T> setSql(String sql) {
		this.sql = sql;
		return this;
	}
	public Object[] getParams() {
		return params;
	}
	public Query<T> setParams(Object[] params) {
		this.params = params;
		return this;
	}
	
	public List<T> getList(){
		return Data.QueryT(database , sql , params ,cls , queryDataType);
	}
	
	public T getOne(){
		return Data.getOneT(database , sql , params ,cls, queryDataType);
	}
	
	public List<T> getPageList(int pageNo , int pageSize){
		if(sql.indexOf("limit") < 0){//未含有分页sql
			pageNo = pageNo < 1 ? 1 : pageNo;
			sql += " limit " + pageSize * (pageNo - 1)+"," + pageSize + " ";
		}
		log.info("执行sql查询语句："+sql);
		return Data.QueryT(database , sql , params ,cls , queryDataType);
	}

	public List<T> getPage(int pageNo , int pageSize,String database){
		if(sql.indexOf("limit") < 0){//未含有分页sql
			pageNo = pageNo < 1 ? 1 : pageNo;
			sql += " limit " + pageSize * (pageNo - 1)+"," + pageSize + " ";
		}
		log.info("执行sql查询语句："+sql);
		return Data.QueryT(database , sql , params ,cls , queryDataType);
	}

	
	public int save(){
		return Data.Insert(database , sql, params);
	}
	
	public int update(){
		return Data.Update(database , sql, params);
	}
	
	public int count(){

		String countSql = null;
		if(sql.toLowerCase().contains("group by")){
			countSql = "select count(*) from (" + sql + ") t";
		}else {
			countSql = "select count(*) " + sql.substring(sql.toLowerCase().indexOf("from"));
		}
		// 查询记录条数
		List l2 = (List) Data.Query(database , countSql , params);
		int count = 0;
		if(l2.size() > 0){
			count = Integer.parseInt(((List) l2.get(0)).get(0).toString());
		}
		return count;
	}
}
