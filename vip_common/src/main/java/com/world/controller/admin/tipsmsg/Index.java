/*package com.world.controller.admin.tipsmsg;

import java.util.List;

import com.world.system.model.TipsMsg;
import com.world.system.model.TipsMsgDao;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.Pages;
import com.world.web.convention.annotation.FunctionAction;


@FunctionAction(jspPath = "/admins/tipsmsg/" , des="消息提示")
public class Index extends Pages {

   @Page(Viewer = "/admins/tipsmsg/ajax.ftl")
   public void index(){
	   TipsMsgDao dao = new TipsMsgDao();
	   List<TipsMsg> list = dao.getTipsMsg(20);
	   setAttr("list", list);
   }
   
   @Page(Viewer = "/admins/tipsmsg/see.ftl")
   public void see(){
	   String id = param("id");
	   TipsMsgDao dao = new TipsMsgDao(); 
	   TipsMsg msg = dao.findById(id);
	   msg.setStatus(1);
	   msg.setSeeTime(TimeUtil.getNow());
	   msg.setSeeTimes(msg.getSeeTimes() + 1);
	   dao.update(msg);
	   setAttr("msg", msg);
   }
}
*/