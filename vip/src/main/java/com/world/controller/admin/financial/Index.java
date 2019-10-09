package com.world.controller.admin.financial;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/account/", des = "财务管理")
public class Index extends AdminAction {
	
	AdminUserDao auDao = new AdminUserDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		
	}

}

