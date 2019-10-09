package com.world.controller.admin;

import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/" , des = "后台管理")
public class Index extends AdminAction {
	/*
	 * shouye
	 */
	@Page
	public void index() {
		try {
			go("/");
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	//xzhang 20170824 新增无权限跳转页面
	@Page(Viewer = "/admins/role.jsp")
	public void redrictRole() {
	}
}

