package com.world.controller.admin.btc.plan;

import com.world.web.Page;
import com.world.web.action.BaseAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/transactions/planEntrust/" , des = "计划委托")
public class Index extends BaseAction{
	
   @Page(Viewer = DEFAULT_INDEX)
   public void index(){
	   
   }
   
   @Page(Viewer = DEFAULT_AJAX)
   public void ajax(){
	   index(); 
   }

	@Page(Viewer = ".xml")
	public void doCancle() {
		try {
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			WriteError("取消失败:");
		}
	}
	
	public static void main(String[] args) {
		log.info(0.001<0.001);
	}
   
}
