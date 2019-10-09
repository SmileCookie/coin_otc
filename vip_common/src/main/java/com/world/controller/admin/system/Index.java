package com.world.controller.admin.system;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
@FunctionAction(jspPath = "/admins/system/" , des="系统管理")
public class Index extends AdminAction{
	
	@Page
	public void index(){
		
	}

}
