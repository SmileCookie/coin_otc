package com.world.data.mongo.id;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PrePersist;
import com.world.data.id.IdWorkerUtil;
import com.world.model.entity.BaseEntity;

public class LongIdEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5384331724346757875L;

	@Id protected long myId;
	
	public LongIdEntity() {}
	
	@PrePersist void prePersist(){
		if (myId == 0) {
		    myId = IdWorkerUtil.getId();
		}
	}
	
	public long getMyId() {
		return myId;
	}

	public void setMyId(long myId) {
		this.myId = myId;
	}

	public long getId(){
		return myId;
	}
	
}
