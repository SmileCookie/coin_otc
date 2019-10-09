package com.world.data.mongo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.world.data.mongo.id.StrBaseLongIdEntity.StoredId;

//mongodb自增长id号
public class ids {

	
	  /**  
     * 自增Id  
     * @param idName 自增Id名称  
     * @return Id  
     */  
    public static Integer getId(String idName) {  
        BasicDBObject query = new BasicDBObject();  
        query.put("name", idName);  
  
        BasicDBObject update = new BasicDBObject();  
        update.put("$inc", new BasicDBObject("id", 1));  
  
        MorphiaMongo mm = MorphiaMongoUtil.getMorphiaMongo();
        DB db = mm.getMongo().getDB(Dao.DEFAULT_DBNAME);
        
        DBObject dbObject2 = db.getCollection("autoNumber").findAndModify(query,  
                null, null, false, update, true, true);  
          
        Integer id = (Integer) dbObject2.get("id");  
        return id;  
    }  
    
    public static long getNewNumber(String key){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	
		return Long.valueOf(sdf.format(new Date())+""+getId(key));
	}
    
	public static void saveOrUpdate(String idName, long id){
		 MorphiaMongo mm = MorphiaMongoUtil.getMorphiaMongo();
	     DB db = mm.getMongo().getDB(Dao.DEFAULT_DBNAME);
		 BasicDBObject query = new BasicDBObject("_id", idName);  
		 BasicDBObject update = new BasicDBObject("value", id);  
		 if(db.getCollection("ids").findOne(query) == null){
			 query.put("value", id);
			 db.getCollection("ids").save(query);
		 }else{
			 db.getCollection("ids").findAndModify(query, update);
		 }
	}
	
	public static long getPersistId(String idName){
		 MorphiaMongo mm = MorphiaMongoUtil.getMorphiaMongo();
	     DB db = mm.getMongo().getDB(Dao.DEFAULT_DBNAME);
	     BasicDBObject query = new BasicDBObject("_id", idName);  
	     DBObject dbObject2 = db.getCollection("ids").findOne(query);
	     if(dbObject2 == null)
	    	 return 0;
	     else
	    	 return (Long) dbObject2.get("value");
	}
}
