package com.world.model.dao.user.relation;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.admin.competence.CompetenceMongoDao;
import com.world.model.entity.user.relation.UserRelationFunction;

public class UserRelationFunctionDao extends CompetenceMongoDao<UserRelationFunction, String>{
	
	//添加一个模块
	public String addOne(UserRelationFunction urf){
		return super.save(urf).getId().toString();
	}
	
	public List<UserRelationFunction> findByUser(String userId){
		Query<UserRelationFunction> query = super.getQuery();
		query.filter("userId", userId);
		query.filter("isDeleted", 0);
		return query.asList();
	}
	
	public List<UserRelationFunction> findByRole(String roleId){
		Query<UserRelationFunction> query = super.getQuery();
		query.filter("roleId", roleId);
		query.filter("isDeleted", 0);
		return query.asList();
	}
	
	//假删除
	public boolean deleteByRole(String roleId){
		Datastore ds = super.getDatastore();
		Query<UserRelationFunction> q = ds.find(UserRelationFunction.class, "roleId", roleId);  
		UpdateOperations<UserRelationFunction> ops = ds.createUpdateOperations(UserRelationFunction.class);
		ops.set("isDeleted", 1);
		
		UpdateResults<UserRelationFunction> result = super.update(q, ops);
		if(result.getHadError())
			return false;
		else 
			return true;
	}
}
