package com.world.controller.admin.competence.role_menu;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateResults;
import com.world.data.database.DatabasesUtil;
import com.world.model.dao.admin.competence.MenuDao;
import com.world.model.dao.admin.competence.MenuViewFunctionDao;
import com.world.model.dao.admin.competence.PlateDataDao;
import com.world.model.dao.admin.competence.RoleFunctionManager;
import com.world.model.dao.admin.role.AdminRoleDao;
import com.world.model.entity.admin.competence.MenuViewFunction;
import com.world.model.entity.admin.competence.menu.Menu;
import com.world.model.entity.admin.competence.role_plate.PlateData;
import com.world.model.entity.admin.role.AdminRole;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import com.world.web.competence.Function;
import com.world.web.competence.FunctionGroup;
import com.world.web.competence.FunctionManager;
import com.world.web.competence.GroupHierarchy;
import com.world.web.competence.HierarchyManager;
import com.world.web.convention.annotation.FunctionAction;
import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
@FunctionAction(jspPath = "/admins/competence/role_menu/", plate = true)
public class Index extends BaseAction {

	MenuDao dao = new MenuDao();
	AdminRoleDao arDao = new AdminRoleDao();
	MenuViewFunctionDao mvfDao = new MenuViewFunctionDao();

   @Page(Viewer = "/admins/competence/role_menu/list.ftl")
   public void index(){
	//获取参数
	int pageNo = intParam("page");	
	String  id = param("id");      
    String  roleName=param("roleName");      
    
	Query<Menu> q = dao.getQuery();
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
 			List<Menu> dataList = dao.findPage(q, pageNo, pageSize);
 			setAttr("dataList", dataList);
 		}
 		setPaging((int)total, pageNo , pageSize);
       }catch(Exception ex){
    	  log.error(ex.toString(), ex);
	      Write("",false,ex.toString());
       }
   }

	// ajax的调用
	@Page(Viewer = "/admins/competence/role_menu/ajax.ftl")
	public void ajax() {
		index();
	}

	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = "/admins/competence/role_menu/aoru.ftl")
	public void aoru() {
		try {
			String id = param("id");//角色id
			AdminRole ar = arDao.getById(id);
			List<Menu> dataList = dao.find().asList();
			String[] menuIds = ar.getMenuIds();
			for(Menu m : dataList){
				if(menuIds != null && menuIds.length > 0){
					for(String mi : menuIds){
						if(mi.equals(m.getId())){
							m.isInRole = true;
							break;
						}
					}
				}
			}
 			setAttr("dataList", dataList);
 			setAttr("curData", ar);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	/***
	 * 子菜单
	 */
	@Page(Viewer = "/admins/competence/role_menu/smenu_edite.ftl" , plate = true)
	public void sMenus() {
		try {
			String id = param("id");//角色id
			String mid = param("mid");
			AdminRole ar = arDao.getById(id);
			Map<String , FunctionGroup> groups = FunctionManager.getGroups();
//			Set<String> urls = groups.keySet();
			
			Map<Object , MenuViewFunction> maps = mvfDao.getMapByField("url", mvfDao.getQuery().filter("roleId =", id).filter("menuId =", mid));
			String ltcCoin = "?coint=ltc";
			String ethCoin = "?coint=eth";
			for(Iterator<Entry<String , FunctionGroup>>  e = groups.entrySet().iterator(); e.hasNext();){
				Entry<String , FunctionGroup> m = e.next();
				if(maps.containsKey(m.getKey())){///
					m.getValue().setInRole(true);
				}
				FunctionGroup fg = m.getValue();
				for(Function f : fg.getFunctions()){///设置子权限
					if(maps.containsKey(f.getUrl())){///
						f.setInRole(true);
					}else if(maps.containsKey(f.getUrl() + ltcCoin)){
						f.setInRole(true);
						if(!f.getDes().contains(ltcCoin)){
							f.setDes(f.getDes() + ltcCoin);
						}
					}else if(maps.containsKey(f.getUrl() + ethCoin)){
						f.setInRole(true);
						if(!f.getDes().contains(ethCoin)){
							f.setDes(f.getDes() + ethCoin);
						}
					}else{
						f.setInRole(false);
					}
				}
			}
			
 			setAttr("groups", groups);
 			setAttr("curData", ar);
 			setAttr("id", id);
 			setAttr("mid", mid);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = "/admins/competence/role_menu/hierarchy_smenu_edite.ftl" , plate = true)
	public void hierarchyMenus() {///层次菜单
		try {
			String id = param("id");//角色id
			String mid = param("mid");
			AdminRole ar = arDao.getById(id);
			Map<String , FunctionGroup> groups = FunctionManager.getGroups();
			
			GroupHierarchy lastHierachy = HierarchyManager.getLastHierachy();
			
			Map<Object , MenuViewFunction> maps = mvfDao.getMapByField("url", mvfDao.getQuery().filter("roleId =", id).filter("menuId =", mid));
			
			for(Iterator<Entry<String , FunctionGroup>>  e = groups.entrySet().iterator(); e.hasNext();){
				Entry<String , FunctionGroup> m = e.next();
				if(maps.containsKey(m.getKey())){///
					m.getValue().setInRole(true);
				}
				FunctionGroup fg = m.getValue();
				for(Function f : fg.getFunctions()){///设置子权限
					if(maps.containsKey(f.getUrl()) || maps.containsKey(f.getUrl() + "?coint=ltc") || maps.containsKey(f.getUrl() + "?coint=eth")){///
						f.setInRole(true);
					}else{
						f.setInRole(false);
					}
				}
			}
			
 			setAttr("lastHierachy", lastHierachy);
 			setAttr("curData", ar);
 			setAttr("id", id);
 			setAttr("mid", mid);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = XML)
	public void doSmenus() {
		try {
			if(!codeCorrect(XML)){
				return;
			}
			String roleId = param("id");
			String menuId = param("mid");
	        String ids = request.getParameter("ids");
	        String[] urls = null;
	        if(ids.length() > 0){
	        	urls = ids.split(",");
	        }
			int res = 0;
			if(ids.length() > 0){
//				mvfDao.getQuery().filter("roleId =", roleId).filter("menuId =", menuId);
//				Map<Object , MenuViewFunction> maps = mvfDao.getMapByField("url", urls , "in");
				for(String s : urls){
					String[] ufuncs = s.split(":");
					if(ufuncs[1].equals("true")){//保存
						//查找是否已经添加
						long hasC = mvfDao.count(mvfDao.getQuery().filter("url =", ufuncs[0]).filter("menuId =", menuId).filter("roleId =", roleId));
						
						if(hasC <= 0){
							MenuViewFunction mvf = new MenuViewFunction(mvfDao.getDatastore());
							mvf.setUrl(ufuncs[0]);
							mvf.setMenuId(menuId);
							mvf.setRoleId(roleId);
							mvfDao.save(mvf);
						}
					}else{//删除
						mvfDao.deleteByQuery(mvfDao.getQuery().filter("url =", ufuncs[0]).filter("menuId =", menuId).filter("roleId =", roleId));//
					}
				}
				res = 2;
			}
			
			if(res>0){
				RoleFunctionManager.refreshFunctionsByRoleId(roleId);
				Write("操作成功",true,"");
				return;
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		Write("未知错误导致添加失败！",false,"");
	}

	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = XML)
	public void doAoru() {
		try {
			if(!codeCorrect(XML)){
				return;
			}
			String id = param("id");
	        String ids = param("ids");
	        String[] menuIds = null;
	        if(ids.length() > 0){
	        	menuIds = ids.split(",");
	        }
	        
			int res = 0;
			Datastore ds = dao.getDatastore();
			if(id.length() > 0){
				Query<AdminRole> query = ds.find(AdminRole.class, "_id", id);   
				UpdateResults<AdminRole> ur = ds.update(query, ds.createUpdateOperations(AdminRole.class)
						.set("menuIds", menuIds));
				if(!ur.getHadError()){
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
                dao.delById(id);
                Write("删除成功",true,"");
                return;
			}
		}
		Write("未知错误导致删除失败！",false,"");
	}
	
	@Page(Viewer = JSON)
	public void jsons() {
		String roleId = param("roleId");
		if(roleId.length() > 0){
			AdminRole ar = arDao.getById(roleId);
			String[] ids = ar.getMenuIds();
			if(ids != null){
				List<Menu> dataList = dao.find(dao.getQuery().filter("_id in", ids).order("_id")).asList();
				
				//log.info(JSONArray.fromObject(dataList).toString());
				json("" , true , JSONArray.fromObject(dataList).toString());
				return;
			}
		}
		json("" , false , "[]");
	}
	
	@Page(Viewer = JSON)
	public void sjsons() {
		String id = param("id");//角色id
		String mid = param("mid");//一级菜单
		
		if(id.length() > 0){
			List<MenuViewFunction> menus = mvfDao.find(mvfDao.getQuery().filter("roleId =", id).filter("menuId =", mid)).asList();
			Map<String, FunctionGroup> fgs = FunctionManager.getGroups();
			
			List<MenuViewFunction> sms = new ArrayList<MenuViewFunction>();
			int ss = menus.size();
			for(int i = 0 ;i < ss;i++){
				MenuViewFunction mvf = menus.get(i);
				String url = mvf.getUrl();
				String param = "";
				int id1 = url.indexOf("?");
				
				if(id1 > 0){
					param = url.substring(id1 + 1).toUpperCase().split("=")[1];
					url = url.substring(0 , id1);
				}
				FunctionGroup f = fgs.get(url);
				if(f == null && FunctionManager.functions.get(url) == null){
					if(id1 <= 0){
						param = mvf.getParams(0).toUpperCase();
					}
					if(DatabasesUtil.isDatabase(param)){//开启url父类功能
						f = fgs.get(mvf.getPurl(0));//这里只需要判断一阶
					}
				}
				
				if(f != null && f.getFunctions() != null && f.getFunctions().size() > 1){
					mvf.setName(param + f.getDes());
					sms.add(mvf);
				}
			}
			log.info(JSONArray.fromObject(sms).toString());
			json("" , true , JSONArray.fromObject(sms).toString());
			return;
		}
		json("" , false , "[]");
	}
	
	
	/***
	 * 分配板块页面
	 */
	@Page(Viewer = "/admins/competence/role_menu/plate_allocation.ftl")
	public void plate() {
		try {
			PlateDataDao plateDataDao = new PlateDataDao();
			
			String id = param("id");//角色id
			String mid = param("mid");//角色id
			String url = param("url");
			AdminRole ar = arDao.getById(id);
			MenuViewFunction mvf = mvfDao.findOne(mvfDao.getQuery().filter("roleId =", id).filter("menuId =", mid).filter("url =", url));
			List<PlateData> datas = plateDataDao.find(plateDataDao.getQuery().filter("path =", url)).asList();//当前url下的所有板块数据
			
			String[] plateDataIds = mvf.getPlateDataIds();
			for(PlateData pd : datas){
				if(plateDataIds != null){
					for(String pdId : plateDataIds){
						if(pd.getId().equals(pdId)){
							pd.setInRole(true);
							break;
						}
					}
				}
			}
 			setAttr("curData", ar);
 			setAttr("dataList", datas);
 			setAttr("mid", mid);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = XML)
	public void doPlate() {
		String id = param("id");
		String mid = param("mid");
		String ids = param("ids");
		if(id.length()>0){
			String[] plateDataIds = ids.split(",");
			int res = 0;
			Datastore ds = mvfDao.getDatastore();
			Query<MenuViewFunction> query = ds.find(MenuViewFunction.class, "roleId =", id).filter("menuId =", mid);   
			UpdateResults<MenuViewFunction> ur = ds.update(query, ds.createUpdateOperations(MenuViewFunction.class)
					.set("plateDataIds", plateDataIds));
			if(!ur.getHadError()){
				res = 2;
			}
			if(res > 0){
				RoleFunctionManager.refreshFunctionsByRoleId(id);
				Write("操作成功",true,"");
				return;
			}
		}
		Write("未知错误导致操作失败！",false,"");
	}
}
