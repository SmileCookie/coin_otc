/*package com.world.controller.admin.competence.plate_data;

import java.util.List;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.admin.competence.PlateDataDao;
import com.world.model.entity.admin.competence.role_plate.PlateData;
import com.world.model.entity.admin.role.AdminRole;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import com.world.web.competence.FunctionManager;
import com.world.web.convention.annotation.FunctionAction;
@FunctionAction(jspPath = "/admins/competence/plate_data/" , des = "板块数据")
public class Index extends BaseAction {

	PlateDataDao dao = new PlateDataDao();

	@Page(Viewer = DEFAULT_INDEX)
   public void index(){
	//获取参数
	int pageNo = intParam("page");	
	String  id = param("id");      
    String  roleName=param("roleName");      
    
	Query<PlateData> q = dao.getQuery();
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
	         q.filter("admName", pattern);
	     }
         log.info("搜索的sql语句:"+q.toString());

        long total = dao.count(q);
 		if(total > 0){
 			List<PlateData> dataList = dao.findPage(q, pageNo, pageSize);
 			setAttr("dataList", dataList);
 		}
 		setPaging((int)total, pageNo , pageSize);
       }catch(Exception ex){
    	  log.error(ex.toString());
	      Write("",false,ex.toString());
       }
   }

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	*//**
	 * 功能:响应添加的函数
	 *//*
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			String id = param("id");
			setAttr("id", id);
			setAttr("groups", FunctionManager.plateGroups);
			if(id.length() > 0){
				PlateData data = dao.getById(id);
				setAttr("curData", data);
			}
		} catch (Exception ex) {
			log.error(ex.toString());
		}
	}

	*//**
	 * 功能:响应添加的函数
	 *//*
	@Page(Viewer = XML)
	public void doAoru() {
		try {
			   String id = param("id");
	           String dataId = param("dataId");
	           String dataDes = param("dataDes");
	           String path = param("path");
	          
			int res = 0;
			Datastore ds = dao.getDatastore();
			if(id.length() > 0){
				Query<AdminRole> query = ds.find(AdminRole.class, "_id", id);   
				UpdateResults<AdminRole> ur = ds.update(query, ds.createUpdateOperations(AdminRole.class)
						.set("dataId", dataId).set("dataDes", dataDes));
				if(!ur.getHadError()){
					res = 2;
				}
			}else{
				PlateData m = new PlateData(ds);
				m.setDataId(dataId);
				m.setDataDes(dataDes);
				m.setPath(path);
				if(dao.save(m) != null){
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
				if(dao.delById(id).getError() == null){
					Write("删除成功",true,"");
					return;
				}
			}
		}
		Write("未知错误导致删除失败！",false,"");
	}
	
	@Page(Viewer = JSON)
	public void jsons() {
		List<PlateData> dataList = dao.find(dao.getQuery().order("_id")).asList();
		log.info(JSONArray.fromObject(dataList).toString());
		json("" , true , JSONArray.fromObject(dataList).toString());
	}
}
*/