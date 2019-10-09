package com.world.model.dao.lan;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.lan.LanStore;

public class LanStoreDao extends MongoDao<LanStore, String>{
	
	public boolean add(LanStore lan) throws Exception{
		super.ensureIndexes();
		String nid = super.save(lan).getId().toString();
		if(StringUtils.isEmpty(nid)) return false;
		return true;
	}
	
	public boolean update(LanStore lan) throws Exception{
		Datastore ds = super.getDatastore();
		Query<LanStore> q = ds.find(LanStore.class, "_id", lan.getId());  
		UpdateOperations<LanStore> ops = ds.createUpdateOperations(LanStore.class);
		ops.set("key", lan.getKey());
		ops.set("cnValue", lan.getCnValue());
		ops.set("enValue", lan.getEnValue());
		ops.set("isCacheValue", lan.getIsCacheValue());

		UpdateResults<LanStore> ur = super.update(q, ops);

		if(ur.getHadError()) return false;
		return true;
	}
	
	public LanStore get(String key){
		return super.findOne("key", key);
	}
	
	public List<LanStore> like(String key){
		Query<LanStore> q = super.getQuery();
		Pattern pattern = Pattern.compile("^.*"  + key+  ".*$" ,  Pattern.CASE_INSENSITIVE);
		q.filter("key", pattern);
		q.order("- addTime");
		
		return super.findPage(q, 1, 10);
	}
	
	public List<LanStore> all(){
		Query<LanStore> q = super.getQuery();
		q.filter("isCacheValue", 1);
		return q.asList();
	}
}
