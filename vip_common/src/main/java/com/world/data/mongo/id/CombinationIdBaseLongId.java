package com.world.data.mongo.id;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.Transient;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

public class CombinationIdBaseLongId {

	@Id protected String myId;
	
	@Transient final protected Datastore ds;
	
	@Transient final protected String pre;
	
	protected CombinationIdBaseLongId(Datastore ds , String pre) {
		this.ds = ds;
		this.pre = pre;
	}
	
	@PrePersist void prePersist(){
		if (myId == null) {
			String collName = ds.getCollection(getClass()).getName();
		    Query<StoredId> q = ds.find(StoredId.class, "_id", collName);
		    UpdateOperations<StoredId> uOps = ds.createUpdateOperations(StoredId.class).inc("value");
		    StoredId newId = ds.findAndModify(q, uOps);
		    if (newId == null) {
		       newId = new StoredId(collName , pre);
		       ds.save(newId);
		    }
		    
		    myId = newId.getCombinationValue();
		}
	}
	/**
	 * Used to store counters for other entities.
	 * @author skot
	 *
	 */

	@Entity(value="ids", noClassnameStored=true)
	public static class StoredId {
		final @Id String className;
		protected Long value = 1L;
		
		protected String CombinationValue = "";//组合值
		protected String pre = "";//前缀
		
		public StoredId(String name , String pre) {
			className = name;
			this.pre = pre;
		}
		
		protected StoredId(){
			className = "";
		}
		
		public Long getValue() {
			return value;
		}
		///组合ID
		public String getCombinationValue() {
			return pre + value;
		}
	}

}
