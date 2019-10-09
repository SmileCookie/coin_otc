package com.world.model.dao.app;

import java.util.List;

import com.google.code.morphia.query.Query;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.app.Opinion;

public class OpinionDao extends MongoDao<Opinion, Long> {
	private static final long serialVersionUID = -2531540252311678541L;

	public List<Opinion> search(Query<Opinion> q){
		//q.offset((pageIndex-1)*pageSize).limit(pageSize);
		return super.find(q).asList();
	}
	
}
