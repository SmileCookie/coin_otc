package com.world.controller.admin.btc.statistics;

import java.util.List;

import com.world.data.mysql.Data;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/transactions/statistics/" , des = "信息统计")
public class Index extends BaseAction{
	
   @Page(Viewer = DEFAULT_INDEX)
   public void index(){
	   List list = (List) Data.GetOne("btcentrust", "select userIdBuy,sum(numbers)/100000000 from transrecord where unitPrice>0 GROUP BY userIdBuy ORDER BY SUM(numbers) desc;", new Object[]{});
	   
	   log.info(list.get(0));
	   
	   log.info(list.get(1));
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
