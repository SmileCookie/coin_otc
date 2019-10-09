package com.world.data.mongo.id;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.Transient;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.world.model.entity.BaseEntity;

public class StrBaseLongIdEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5384331724346757875L;

	@Id protected String myId;
	
	@Transient final public Datastore ds;
	
	public StrBaseLongIdEntity() {
		this(null);
	}
	
	public StrBaseLongIdEntity(Datastore ds) {
		this.ds = ds;
	}

	@PrePersist void prePersist(){
		if (myId == null) {
			myId = incId();
		}
	}

	//根据不同的序列化工具添加忽略注解或者明知不以get开头,否则序列化是ds为null,报错
	public String incId() {
		String collName = ds.getCollection(getClass()).getName();
		Query<StoredId> q = ds.find(StoredId.class, "_id", collName);
		UpdateOperations<StoredId> uOps = ds.createUpdateOperations(StoredId.class).inc("value");
		StoredId newId = ds.findAndModify(q, uOps);
		if (newId == null) {
			newId = new StoredId(collName);
			ds.save(newId);
		}
		return newId.getCombinationValue();
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
		
		public StoredId(String name) {
			className = name;
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
	public String getMyId() {
		return myId;
	}

	public void setMyId(String myId) {
		this.myId = myId;
	}

	public String getId(){
		return myId;
	}
}
