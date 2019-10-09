package com.world.model.dao.user.relation;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.admin.competence.CompetenceMongoDao;
import com.world.model.entity.user.relation.UserRelationModule;
import com.world.model.entity.user.relation.UserRelationRole;

public class UserRelationRoleDao extends CompetenceMongoDao<UserRelationRole, String>{
	//添加一个角色
	public String addOne(UserRelationRole urr){
		return super.save(urr).getId().toString();
	}
	
	public List<UserRelationRole> findByUser(String userId){
		Query<UserRelationRole> query = super.getQuery();
		query.filter("userId", userId);
		query.filter("isDeleted", 0);
		return query.asList();
	}
	
	public long getCount(String userId){
		Query<UserRelationRole> query = super.getQuery();
		query.filter("userId", userId);
		query.filter("isDeleted", 0);
		return super.count(query);
	}
	
	public boolean updateOne(UserRelationRole urr){
		Datastore ds = super.getDatastore();
		Query<UserRelationRole> q = ds.find(UserRelationRole.class, "_id", urr.getId());  
		UpdateOperations<UserRelationRole> ops = ds.createUpdateOperations(UserRelationRole.class);
		ops.set("name", urr.getName());
		ops.set("isDeleted", urr.getIsDeleted());
		
		UpdateResults<UserRelationRole> result = super.update(q, ops);
		if(result.getHadError())
			return false;
		else 
			return true;
	}
}
