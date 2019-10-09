package com.world.model.dao.admin.role;

import com.world.model.dao.admin.competence.CompetenceMongoDao;
import com.world.model.entity.admin.role.AdminRole;

public class AdminRoleDao extends CompetenceMongoDao<AdminRole, String>{

	public AdminRoleDao() {
		super();
		log.info("初始化类AdminRoleDao()一次");
	}

}
