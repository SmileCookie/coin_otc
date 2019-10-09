package com.world.data.mongo.id;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.world.data.mongo.MongoDao;
import com.world.data.mongo.id.StrBaseLongIdEntity.StoredId;

public class IdUtils {
	static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	
	/****
	 * 获取一个新ID
	 * @param collection
	 * @return
	 */
	public static String getNewId(IdCollections collection){
		Datastore ds = new IdsDao().getDatastore();
		
	    Query<StoredId> q = ds.find(StoredId.class, "_id", collection.getValue());
	    UpdateOperations<StoredId> uOps = ds.createUpdateOperations(StoredId.class).inc("value");
	    StoredId newId = ds.findAndModify(q, uOps);
	    if (newId == null) {
	       newId = new StoredId(collection.getValue());
	       ds.save(newId);
	    }
	    
	    newId.pre = sdf.format(new Date());
	    return newId.getCombinationValue();
	}
}

class IdsDao extends MongoDao<StoredId, String>{
	
}
