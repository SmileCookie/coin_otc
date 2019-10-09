package com.world.controller.admin.competence.role;

import com.alibaba.fastjson.JSONArray;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.admin.role.AdminRoleDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.role.AdminRole;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

import java.util.List;
import java.util.regex.Pattern;
@FunctionAction(jspPath = "/admins/competence/role/" , des = "角色管理" , plate = true)
public class Index extends AdminAction {

	AdminUserDao auDao = new AdminUserDao();
	AdminRoleDao arDao = new AdminRoleDao();

	@Page(Viewer = "/admins/competence/role/list.ftl")
   public void index(){
	//log.info("当前角色["+GetCookie(Session.rid)+"拥有板块："+mvf.getPlateDataIdsStr()+"]");
		
	//获取参数
	int pageNo = intParam("page");	
	String  id = param("id");      
    String  roleName=param("roleName");      
    
	Query<AdminRole> q = arDao.getQuery();
	int pageSize = 20;
	
	//将参数保存为attribute
    try
     {		
	   //构建查询条件
         if(id.length()>0){
        	 q.filter("_id =", id);
         }
         if(roleName.length()>0){
	         Pattern pattern = Pattern.compile("^.*"  + roleName+  ".*$" ,  Pattern.CASE_INSENSITIVE);  
	         q.filter("roleName", pattern);
	     }
         log.info("搜索的sql语句:"+q.toString());

        long total = arDao.count(q);
 		if(total > 0){
 			List<AdminRole> dataList = arDao.findPage(q, pageNo, pageSize);
 			setAttr("dataList", dataList);
 		}
 		setPaging((int)total, pageNo , pageSize);
       }catch(Exception ex){
    	  log.error(ex.toString(), ex);
	      Write("",false,ex.toString());
       }
   }

	// ajax的调用
	@Page(Viewer = "/admins/competence/role/ajax.ftl")
	public void ajax() {
		index();
	}

	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = "/admins/competence/role/aoru.ftl")
	public void aoru() {
		try {
			String id = param("id");
			setAttr("id", id);
			if(id.length() > 0){
				AdminRole data = arDao.getById(id);
				setAttr("curData", data);
			}
			
			List<AdminRole> all = arDao.find(arDao.getQuery().filter("roleName !=", "超级管理员")).asList();
			setAttr("all", all);
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
			if(!codeCorrect(JSON)){
				return;
			}
		    String id = param("id");
            String roleName = param("roleName");
            String des = param("des");
            String pid = param("pid");
            
            
            
            if(pid.length() > 0){
            	AdminRole pdata = arDao.getById(pid);
            	if(pdata == null){
            		Write("父角色无效！",false,"");
            		return;
            	}else if(pdata.getRoleName().equals("超级管理员")){
            		Write("超级管理员权限不可继承",false,"");
            		return;
            	}
            }
            
            AdminRole pdata = arDao.getByField("roleName", roleName);
            if(pdata != null && !pdata.getId().equals(id)){
            	Write("该角色已存在。",false,"");
            	return;
            }
	          
			int res = 0;
			Datastore ds = arDao.getDatastore();
			if(id.length() > 0){
				
				Query<AdminRole> query = ds.find(AdminRole.class, "_id", id);   
				UpdateResults<AdminRole> ur = ds.update(query, ds.createUpdateOperations(AdminRole.class)
						.set("roleName", roleName).set("des", des).set("pid", pid));
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
		if(!codeCorrect(XML)){
			return;
		}
		String id = param("id");
		if(id.length()>0){
			boolean res = true;
			if(res){
                arDao.delById(id);
                Write("删除成功",true,"");
                return;
			}
		}
		Write("未知错误导致删除失败！",false,"");
	}
	
	@Page(Viewer = JSON)
	public void jsons() {
//		if(!codeCorrect(JSON)){
//			return;
//		}
		List<AdminRole> dataList = arDao.getQuery().order("_id").asList();
		String result = JSONArray.toJSONString(dataList);
		json("" , true , result);
	}
}
