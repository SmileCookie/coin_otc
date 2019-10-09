package com.world.model.dao.user;

import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.version.Version;
import com.world.web.action.Action;

public class VersionDao extends MongoDao<Version, String> implements Action{
	
}
