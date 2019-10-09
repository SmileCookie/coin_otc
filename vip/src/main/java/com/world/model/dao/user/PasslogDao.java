package com.world.model.dao.user;

import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.Passlog;
import com.world.web.action.Action;

public class PasslogDao extends MongoDao<Passlog, String> implements Action{
	
	public Passlog getLogByType(String userId, int type){
		Passlog pwdLog = super.findOne(getQuery().filter("userId", userId).filter("isDeleted", false).filter("type", type));
		return pwdLog;
	}
}
