package com.world.controller.admin.competence;

import com.world.web.Page;
import com.world.web.Pages;
import com.world.web.convention.annotation.FunctionAction;


@FunctionAction(jspPath = "/admins/competence/" , des="功能/权限")
public class Index extends Pages {

   @Page(Viewer = "/admins/competence/index.ftl")
   public void index(){
	   
   }
   
   @Page(Viewer = "/admins/competence/index.jsp")
   public void aoru(){
	   
   }

}
