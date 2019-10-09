package com.world.controller.admin.competence.function;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.admin.role.AdminRoleDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.role.AdminRole;
import com.world.web.Page;

import com.world.web.action.BaseAction;
import com.world.web.competence.FunctionManager;
import com.world.web.convention.annotation.FunctionAction;
@FunctionAction(jspPath = "/admins/competence/function/" , des="系统功能")
public class Index extends BaseAction {

	AdminUserDao auDao = new AdminUserDao();
	AdminRoleDao arDao = new AdminRoleDao();

   @Page(Viewer = "/admins/competence/function/list.ftl")
   public void index(){
	//获取参数
	int pageNo = intParam("page");
	String  id = param("id");
    String  roleName=param("roleName");   
    
	Query<AdminRole> q = arDao.getQuery();
	int pageSize = 20;
	
	//将参数保存为attribute
    try{		
         log.info("搜索的sql语句:"+q.toString());
        long total = FunctionManager.getGroups().size();
 		if(total > 0){
 			setAttr("dataList", FunctionManager.getGroups());
 		}
 		setPaging((int)total, pageNo , pageSize);
       }catch(Exception ex){
    	  log.error(ex.toString(), ex);
	      Write("",false,ex.toString());
       }
   }

	// ajax的调用
	@Page(Viewer = "/admins/competence/function/ajax.ftl")
	public void ajax() {
		index();
	}

	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = "/admins/competence/function/aoru.ftl")
	public void aoru() {
		try {
			String id = param("id");
			setAttr("id", id);
			if(id.length() > 0){
				AdminRole data = arDao.getById(id);
				setAttr("curData", data);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = XML)
	public void doAoru() {
		try {
			   String id = param("id");
	           String roleName = param("roleName");
	           String des = param("des");
	          
			int res = 0;
			Datastore ds = auDao.getDatastore();
			if(id.length() > 0){
				Query<AdminRole> query = ds.find(AdminRole.class, "_id", id);   
				UpdateResults<AdminRole> ur = ds.update(query, ds.createUpdateOperations(AdminRole.class)
						.set("roleName", roleName).set("des", des));
				if(!ur.getHadError()){
					res = 2;
				}
			}else{
				AdminRole m = new AdminRole(ds);
				m.setRoleName(roleName);
				m.setDes(des);
				m.setDate(now());
				if(arDao.save(m) != null){
					res = 2;
				}
			}
			if(res>0){
				Write("操作成功",true,"");
				return;
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		Write("未知错误导致添加失败！",false,"");
	}

	@Page(Viewer = XML)
	public void doDel() {
		String id = param("id");
		if(id.length()>0){
			boolean res = true;
			if(res){
				if(auDao.shamDelById(id)){
					Write("删除成功",true,"");
					return;
				}
			}
		}
		Write("未知错误导致删除失败！",false,"");
	}
}
