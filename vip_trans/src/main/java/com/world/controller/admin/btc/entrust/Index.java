package com.world.controller.admin.btc.entrust;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/transactions/entrust/" , des = "委托管理")
public class Index extends AdminAction{
	
	@Page(Viewer=DEFAULT_INDEX , des="委托列表")
	public void index(){
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}
}

