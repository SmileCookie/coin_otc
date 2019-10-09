package com.world.data.mysql;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.world.data.database.DatabaseProp;
import com.world.data.mysql.bean.BeanField;
import com.world.util.ClassUtil;
import com.world.util.date.TimeUtil;

public class DataDaoSupport<T extends Bean> extends DatabaseProp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4478879178621847758L;
	public DataDaoSupport() {}
	public DataDaoSupport(String lan) {}
	protected static Logger log = Logger.getLogger(DataDaoSupport.class.getName());
	protected Timestamp now(){
		return TimeUtil.getNow();
	}
	private Query<T> query;
	
	/**
	 * 为Bean设置数据库属性
	 * @param t
	 */
	public void setCointForBean(T t){
		if(coint != null && t != null){
			t.setCoint(coint);
		}
	}

	public Query<T> getQuery() {
		if(query == null){
			query = new Query<T>();
		}
		query.setDatabase(database);
		return query;
	}
	public void setQuery(Query<T> query) {
		this.query = query;
	}
	/******
	 * 查询一组数据
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 */
	public List<T> find(String sql,Object[] params , Class<T> cls){
		query = new Query<T>(database , sql,params,cls);
		
		return query.getList();
	}
	/****
	 * 查询单个数据
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 */
	public Bean get(String sql,Object[] params , Class<T> cls){
		query = new Query<T>(database , sql,params,cls);
		return query.getOne();
	}
	
	public T getT(String sql,Object[] params , Class<T> cls){
		query = new Query<T>(database , sql,params,cls);
		return query.getOne();
	}
	
	public Bean getById(Class<T> cls , Object id){
		query = new Query<T>(database , "select * from " + cls.getSimpleName() + " where Id = ?" , new Object[]{id},cls);
		return query.getOne();
	}
	
	public int save(String sql,Object o){
		
		return 0;
	}
	/******
	 * 保存数据
	 * @param sql
	 * @param params
	 * @return save 新插入的id
	 */
	public int save(String sql,Object[] params){
		query = new Query<T>(database , sql,params,null);
		
		return query.save();
	}
	
	/***
	 * 添加Bean到数据库
	 * @param b
	 * @return
	 */
	public int insert(T b){
		OneSql os = getTransInsertSql(b);
		return Data.Insert(database, os.getSql(), os.getPrams());
	}

    /***
     * 添加Bean到数据库
     * @param b
     * @return
     */
    public int insertIgnore(T b){
        OneSql os = getTransInsertSql(b);
        String sql = os.getSql().replace("insert into", "insert ignore into");
        return Data.insertNoId(database, sql, os.getPrams());
    }

	/******
	 * 修改数据
	 * @param sql
	 * @param params
	 * @return update 影响的行数
	 */
	public int update(String sql,Object[] params){
		query = new Query<T>(database , sql,params,null);
		
		return query.update();
	}
	
	/******
	 * 删除数据
	 * @param sql
	 * @param params
	 * @return update 影响的行数
	 */
	public int delete(String sql,Object[] params){
		query = new Query<T>(database , sql,params,null);
		return query.update();
	}
	
	public int count(String sql,Object[] params){
		query = new Query<T>(database , sql,params,null);
		return query.count();
	}
	
	public List<T> find(){
		return getQuery().getList();
	}
	
	public List<T> findPage(int pageNo , int pageSize){
		return getQuery().getPageList(pageNo , pageSize);
	}
	public List<T> findPageEntrust(int pageNo , int pageSize ,String database){
		return getQuery().getPage(pageNo ,pageSize ,database);
	}

	public int update(){
		return getQuery().update();
	}
	
	public int delete(){
		return getQuery().update();
	}
	
	public int count(){
		return getQuery().count();
	}
	
	/***
	 * 获取事物型插入语句
	 * @param b  要插入的实体类
	 * @param isInherit  是否包含继承类的字段
	 * @return
	 */
	public OneSql getTransInsertSql(T b){
		return getTransInsertSql(b, false);
	}
		
	/***
	 * 获取事物型插入语句
	 * @param b  要插入的实体类
	 * @param isInherit  是否包含继承类的字段  如果包含需要设置为public
	 * @return
	 */
	public OneSql getTransInsertSql(T b , boolean isInherit){
		Class<?> cls = b.getClass();
		StringBuilder sqlSbl = new StringBuilder();
		String tabName = cls.getSimpleName();
		sqlSbl.append("insert into ")
			.append(tabName)
			.append(" (");
		
		Field[] fields = cls.getDeclaredFields();
		if(isInherit){
			fields = cls.getFields();
		}
		
		List<Object> lists = new ArrayList<Object>();
		int i = 0;
		for(Field f : fields){
			BeanField bf = f.getAnnotation(BeanField.class);
			if(bf != null && !bf.persistence()){
				continue;
			}
			String fname = f.getName();
			Object o = null;
//			try {
//				o = PropertyUtils.getProperty(b, fname);
//				//o = BeanUtils.getProperty(b, fname);
//			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//				log.error(e.toString(), e);
//			}
			 o = ClassUtil.getValByField(cls, fname, b);
			if(o != null){
				if(i > 0){
					sqlSbl.append(",");
				}
				sqlSbl.append(fname);
				lists.add(o);
				i++;
			}
		}
		if(i > 0){
			sqlSbl.append(") values (");
			Object[] obs = new Object[i];
			for(int j = 0 ; j < i; j++){
				obs[j] = lists.get(j);
				if(j > 0){
					sqlSbl.append(",");
				}
				sqlSbl.append("?");
			}
			sqlSbl.append(")");
			return new OneSql(sqlSbl.toString() , -1 , obs , database);
		}
		
		throw new NullPointerException(tabName + "插入的sql语句为空");
	}
	
	/*****
	 * 获取所有字段的字符串
	 * @param sql
	 * @return
	 */
//	public String getColNameStr(String sql){
//		Query q = new Query(sql,null,null);
//		return q.getAllFiled();
//	}
}
