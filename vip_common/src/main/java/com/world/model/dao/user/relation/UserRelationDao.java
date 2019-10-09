package com.world.model.dao.user.relation;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.admin.competence.CompetenceMongoDao;
import com.world.model.entity.user.relation.UserRelation;

public class UserRelationDao extends CompetenceMongoDao<UserRelation, String>{
	
	//添加一个用户关系模弄
	public String addOne(UserRelation ur){
		return super.save(ur).getId().toString();
	}
	
	public boolean updateOne(UserRelation ur){
		Datastore ds = super.getDatastore();
		Query<UserRelation> q = ds.find(UserRelation.class, "_id", ur.getId());  
		UpdateOperations<UserRelation> ops = ds.createUpdateOperations(UserRelation.class);
		ops.set("roleId", ur.getRoleId());
		UpdateResults<UserRelation> result = super.update(q, ops);
		if(result.getHadError())
			return false;
		else 
			return true;
	}
	
	//假删除
	public boolean deleteOne(UserRelation ur){
		Datastore ds = super.getDatastore();
		Query<UserRelation> q = ds.find(UserRelation.class, "_id", ur.getId());  
		UpdateOperations<UserRelation> ops = ds.createUpdateOperations(UserRelation.class);
		ops.set("isDeleted", 1);
		
		UpdateResults<UserRelation> result = super.update(q, ops);
		if(result.getHadError())
			return false;
		else 
			return true;
	}
	
	//假删除
	public boolean deleteByRole(String roleId){
		Datastore ds = super.getDatastore();
		Query<UserRelation> q = ds.find(UserRelation.class, "roleId", roleId);  
		UpdateOperations<UserRelation> ops = ds.createUpdateOperations(UserRelation.class);
		ops.set("isDeleted", 1);
		
		UpdateResults<UserRelation> result = super.update(q, ops);
		if(result.getHadError())
			return false;
		else 
			return true;
	}
	
	public List<UserRelation> findByUser(String userId){
		Query<UserRelation> query = super.getQuery();
		query.filter("userId", userId);
		query.filter("isDeleted", 0);
		return query.asList();
	}
	
	public UserRelation findBySubUser(String subUserId){
		Query<UserRelation> query = super.getQuery();
		query.filter("subUserId", subUserId);
		query.filter("isDeleted", 0);
		return super.findOne(query);
	}
	
	//通过角色查找
	public List<UserRelation> findByRole(String roleId){
		Query<UserRelation> query = super.getQuery();
		query.filter("roleId", roleId);
		query.filter("isDeleted", 0);
		return query.asList();
	}
	
	public List<UserRelation> findByRole(String roleId, String subUserId){
		Query<UserRelation> query = super.getQuery();
		query.filter("roleId", roleId);
		query.filter("subUserId", subUserId);
		query.filter("isDeleted", 0);
		return query.asList();
	}
}
