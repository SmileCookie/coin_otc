package com.world.model.dao.user.relation;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.admin.competence.CompetenceMongoDao;
import com.world.model.entity.user.relation.UserRelation;
import com.world.model.entity.user.relation.UserRelationModule;

public class UserRelationModuleDao extends CompetenceMongoDao<UserRelationModule, String>{
	
	//添加一个模块
	public String addOne(UserRelationModule urm){
		return super.save(urm).getId().toString();
	}
	
	public List<UserRelationModule> findByUser(String userId){
		Query<UserRelationModule> query = super.getQuery();
		query.filter("userId", userId);
		query.filter("isDeleted", 0);
		return query.asList();
	}
	
	public List<UserRelationModule> findByRole(String roleId){
		Query<UserRelationModule> query = super.getQuery();
		query.filter("roleId", roleId);
		query.filter("isDeleted", 0);
		return query.asList();
	}
	
	//假删除
	public boolean deleteByRole(String roleId){
		Datastore ds = super.getDatastore();
		Query<UserRelationModule> q = ds.find(UserRelationModule.class, "roleId", roleId);  
		UpdateOperations<UserRelationModule> ops = ds.createUpdateOperations(UserRelationModule.class);
		ops.set("isDeleted", 1);
		
		UpdateResults<UserRelationModule> result = super.update(q, ops);
		if(result.getHadError())
			return false;
		else 
			return true;
	}
}
