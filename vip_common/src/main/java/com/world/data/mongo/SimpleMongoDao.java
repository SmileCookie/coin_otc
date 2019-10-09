package com.world.data.mongo;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.Lan;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.entity.coin.CoinProps;
import com.world.util.date.TimeUtil;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

public class SimpleMongoDao<T, K> extends BasicDAO<T, K> implements Dao,Serializable {
	
	
	public String lan = "cn";///默认英文
	public String database = "default";//数据库
	public CoinProps coint;

	public String getDatabase() {
		return database;
	}
	
	public void setCoint(CoinProps coint){
		if(coint != null){
			setDatabase(coint.database);
		}
		this.coint = coint;
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
	
	public void setLan(String lan) {
		this.lan = lan;
	}
	
	/**
	 * 获取资源
	 * @param key key
	 * @return 国际化语言
	 */
    public String L(String key){
    	return Lan.Language(lan, key);
    }
	
  protected static Logger log = Logger.getLogger(SimpleMongoDao.class.getName());

  protected SimpleMongoDao(Mongo mongo, Morphia morphia, String dbName , String entityAlias) {
    super(mongo, morphia, dbName , entityAlias);
  }
  
  protected SimpleMongoDao(Mongo mongo, Morphia morphia, String dbName) {
    super(mongo, morphia, dbName , null);
  }

  
  public  Query<T> getQuery(Class<T> t) {
    Datastore ds = getDatastore();
    return ds.createQuery(t);
  }
  
  public Query<T> getQuery() {
	    return getDatastore().createQuery(entityClazz);
  }
  
  public UpdateOperations<T> getUpdateOperations() {
	    return getDatastore().createUpdateOperations(entityClazz);
  }

  protected Timestamp now() {
    return TimeUtil.getNow();
  }
  
  
  /****
	 * 根据ID获取对应数据
	 * @param id
	 * @return
	 */
	public T getById(Object id){
		return this.findOne(getQuery(entityClazz).filter("_id =", id));
	}
	
	public T getByField(String field , Object value){
		return this.findOne(getQuery(entityClazz).filter(field + " =", value));
	}
	
	public List<T> getListByField(String field , Object value){
		return this.find(getQuery(entityClazz).filter(field + " =", value)).asList();
	}
	
	/*****
	 * 分页查询
	 * @param q 查询条件
	 * @param pageNo 页码
	 * @param pageSize 每页显示数
	 * @return
	 */
	public List<T> findPage(Query<T> q , int pageNo , int pageSize){
		q.offset((pageNo - 1)*pageSize);
		q.limit(pageSize);
		return super.find(q).asList();
	}
	/*****
	 * 根据ID删除数据
	 * @param id
	 * @return
	 */
	public WriteResult delById(String id){
		return delById(entityClazz , id);
	}
	
	/****
	 * 真删除
	 * @param cls
	 * @param id
	 * @return
	 */
	public WriteResult delById(Class<T> cls , String id){
		Query<T> q=null;
		if(id.length() > 15){
			ObjectId oi = new ObjectId(id);
			q = getQuery(cls).filter("_id =", oi);
		}else{
			q = getQuery(cls).filter("_id =", id);
		}
		return super.deleteByQuery(q);
	}
	
	
	/****
	 * 假删除
	 * @param cls
	 * @param id
	 * @return
	 */
	public boolean shamDelById(String id){
		Datastore ds = this.getDatastore();
		Query<T> query = ds.find(entityClazz, "_id", id);   
		UpdateResults<T> ur = ds.update(query, ds.createUpdateOperations(entityClazz)
				.set("del", true));
		return !ur.getHadError();
	}
	/*****
	 * 
	 * @param ids
	 * @return
	 */
	public List<T> getListByIds(K[] ids){
		return getCommonListByIds(ids, entityClazz);
	}
	
	/****
	 * 获取id组的所有数据
	 * @param ids id集合List
	 * @return T数组
	 */
	public List<T> getListByIds(List<K> ids){
		return getListByIds(ids, entityClazz);
	}
	/****
	 * 获取id组的所有数据
	 * @param ids  id集合List
	 * @param c   实体类型
	 * @return
	 */
	public List<T> getListByIds(List<K> ids , Class<T> c){
		return getCommonListByIds(ids , c);
	}
	/****
	 * id数组
	 * @param ids
	 * @param c
	 * @return
	 */
	public List<T> getCommonListByIds(Object ids , Class<T> c){
		Query<T> q=null;
		q = getQuery(c).filter("_id in", ids);
		List<T> list = super.find(q).asList();
		return list;
	}
	/****
	 * 
	 * @param fieldOrkey  字段 - key
	 * @param value
	 * @return
	 */
	public Map<Object , T> getMapByField(String fieldOrkey , Query<T> q){
		List<T> list = this.find(q).asList();;
		Map<Object , T> maps = new LinkedHashMap<Object , T>();
		for(T t : list){
			Object result = null;
			Method[] mss = getEntityClazz().getMethods();
        	for(Method m : mss){
        		if(m.getName().equalsIgnoreCase("get"+fieldOrkey)){
        			try {
						result = m.invoke(t, new Object[]{});
					} catch (IllegalArgumentException e) {
						log.error(e.toString(), e);
					} catch (IllegalAccessException e) {
						log.error(e.toString(), e);
					} catch (InvocationTargetException e) {
						log.error(e.toString(), e);
					}
        		}
        	}
        	maps.put(result , t);
		}
		return maps;
	}
	/***
	 * please use the saves for this
	 * @param entities
	 * @return
	 */
	@Deprecated
	public <E> Iterable<Key<E>> saveAll(final Iterable<E> entities) {
        return  ds.save(entities);
    }
	
	public <E> Iterable<Key<E>> saves(final Iterable<E> entities) {
	    //添加List集合
        return  getDs().insert(entities);
    }
	
//	public void updates(){
//		getCollection().updateMulti(q, o)
//	}
	
	
	/***
	 * 
// create our pipeline operations, first with the $match 
DBObject match = new BasicDBObject("$match", new BasicDBObject("type", "airfare") ); 


 
// build the $projection operation 
DBObject fields = new BasicDBObject("department", 1);
fields.put("amount", 1);

fields.put("_id", 0);

DBObject project = new BasicDBObject("$project", fields ); 


// Now the $group operation

DBObject groupFields = new BasicDBObject( "_id", "$department");
groupFields.put("average", new BasicDBObject( "$avg", "$amount"));
DBObject group = new BasicDBObject("$group", groupFields); 


// run aggregation

AggregationOutput output = collection.aggregate( match, project, group );

	 */
}