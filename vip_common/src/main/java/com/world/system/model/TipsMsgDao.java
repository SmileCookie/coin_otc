package com.world.system.model;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.admin.competence.CompetenceMongoDao;

public class TipsMsgDao extends CompetenceMongoDao<TipsMsg, String>{
	
	public TipsMsg findById(String id){
		Query<TipsMsg> q = getQuery(TipsMsg.class).filter("_id =", id);
		return super.findOne(q);
	}
	
	public List<TipsMsg> getTipsMsg(int pageSize){
		Query<TipsMsg> q = getQuery(TipsMsg.class).order("addTime").limit(pageSize);
		return q.asList();
	}
	
	public List<TipsMsg> getTipsMsg(int status, int pageSize){
		Query<TipsMsg> q = getQuery(TipsMsg.class).filter("status =", status).order("addTime").limit(pageSize);
		return q.asList();
	}
	
	public UpdateResults<TipsMsg> update(TipsMsg msg){
		Datastore ds = super.getDatastore();
		Query<TipsMsg> q = ds.find(TipsMsg.class, "_id", msg.getId());  
		UpdateOperations<TipsMsg> ops = ds.createUpdateOperations(TipsMsg.class);
		
		ops.set("status",msg.getStatus());
		ops.set("seeTime", msg.getSeeTime());
		ops.set("seeTimes", msg.getSeeTimes());

		UpdateResults<TipsMsg> ur = super.update(q, ops);
		return ur;
	}
}
