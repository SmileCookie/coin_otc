package com.world.controller.admin.competence.menu;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.admin.competence.MenuDao;
import com.world.model.dao.admin.competence.MenuViewFunctionDao;
import com.world.model.dao.admin.role.AdminRoleDao;
import com.world.model.entity.admin.competence.MenuViewFunction;
import com.world.model.entity.admin.competence.menu.Menu;
import com.world.model.entity.admin.role.AdminRole;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.competence.FunctionGroup;
import com.world.web.competence.FunctionManager;
import com.world.web.convention.annotation.FunctionAction;
import net.sf.json.JSONArray;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
@FunctionAction(jspPath = "/admins/competence/menu/" , des = "视图菜单")
public class Index extends AdminAction {

	MenuDao dao = new MenuDao();
	MenuViewFunctionDao mvfDao = new MenuViewFunctionDao();
	AdminRoleDao arDao = new AdminRoleDao();
	@Page(Viewer = "/admins/competence/menu/list.ftl")
   public void index(){
	//获取参数
	int pageNo = intParam("page");	
	String  id = param("id");      
    String  roleName=param("name");      
    
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
	         q.filter("name", pattern);
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
	@Page(Viewer = "/admins/competence/menu/ajax.ftl")
	public void ajax() {
		index();
	}

	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = "/admins/competence/menu/aoru.ftl")
	public void aoru() {
		try {
			String id = param("id");
			setAttr("id", id);
			if(id.length() > 0){
				Menu data = dao.getById(id);
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
			if(!codeCorrect(XML)){
				return;
			}
			   String id = param("id");
	           String name = param("name");
	           String des = param("des");
	          
			int res = 0;
			Datastore ds = dao.getDatastore();
			if(id.length() > 0){
				Query<Menu> query = ds.find(Menu.class, "_id", id);   
				UpdateResults<Menu> ur = ds.update(query, ds.createUpdateOperations(Menu.class)
						.set("name", name).set("des", des));
				if(!ur.getHadError()){
					res = 2;
				}
			}else{
				Menu m = new Menu(ds);
				m.setName(name);
				m.setDes(des);
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
		if(!codeCorrect(XML)){
			return;
		}
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
	
	@Page(Viewer = JSON , des = "获取菜单")
	public void jsons() {
		List<Menu> dataList = dao.find(dao.getQuery()).asList();
		log.info(JSONArray.fromObject(dataList).toString());
		json("" , true , JSONArray.fromObject(dataList).toString());
	}
	
	////
	@Page(Viewer = JSON , des = "获取角色菜单") 
	public void roleJsons() {
		//String id = GetCookie(Session.rid);
		
		int rid = roleId();
		
		if(rid > 0){
			String id = String.valueOf(rid);
			AdminRole ar = arDao.getById(id);
			String[] menuIds = ar.getMenuIds();
			AdminRole par = ar.getProle();
			String[] roleIds = new String[]{id};
			if(par != null){//父类功能继承
				String[] pmenuIds = par.getMenuIds();
				menuIds = (String[]) ArrayUtils.addAll(menuIds, pmenuIds);
				roleIds = new String[]{id , par.getId()};
			}
			
			List<Menu> showList = new ArrayList<Menu>();
			
			List<Menu> dataList = dao.find(dao.getQuery().filter("_id in", menuIds)).asList();
			Map<String, FunctionGroup> fgs = FunctionManager.getGroups();
			
			for(Menu m : dataList){
				List<MenuViewFunction> menus = mvfDao.find(mvfDao.getQuery().filter("roleId in", roleIds).filter("menuId =", m.getId())).asList();
				List<MenuViewFunction> sms = new ArrayList<MenuViewFunction>();
				int ss = menus.size();
				
				List<MenuViewFunction> connectRemoves = new ArrayList<MenuViewFunction>();//需要移除无参数function
				for(int i = 0 ;i < ss;i++){
					MenuViewFunction mvf = menus.get(i);
					String url = mvf.getUrl();
					String param = "";
					int id1 = url.indexOf("?");
					
					boolean hasHouzhuiIsCny = false;//有后缀参数，并且参数为cny 的默认的无后缀menufunction可以拿掉
					boolean hasHouzhuiIsEth = false;//有后缀参数，并且参数为eth 的默认的无后缀menufunction可以拿掉
					
					if(id1 > 0){
						param = url.substring(id1 + 1).toUpperCase().split("=")[1];
						url = url.substring(0 , id1);//截取参数后的url
						
						hasHouzhuiIsCny = param.equalsIgnoreCase("cny");
						hasHouzhuiIsEth = param.equalsIgnoreCase("eth");
					}
					FunctionGroup f = fgs.get(url);
					if(f == null && FunctionManager.functions.get(url) == null){
						if(id1 <= 0){
							param = mvf.getParams(0).toUpperCase();
						}
						if(param.equalsIgnoreCase("ltc")){
							f = fgs.get(mvf.getPurl(0));//这里只需要判断一阶
						}
					}
					
					if(f != null && f.getFunctions() != null && f.getFunctions().size() > 1){
						mvf.setHasHouzhuiIsCny(hasHouzhuiIsCny);
						mvf.setName(param + f.getDes());
						sms.add(mvf);
						
						if(hasHouzhuiIsCny || hasHouzhuiIsEth){
							mvf.setNoParamUrl(url);
							connectRemoves.add(mvf);
						}
						
					}
				}
				
				m.setViewFunctions(sms);
				if(sms.size() > 0){
					///清理sms
					if(connectRemoves.size() > 0){
						for(MenuViewFunction cfunction : connectRemoves){
							String noParam = cfunction.getNoParamUrl();
							
							for(int i = 0; i<sms.size();i++){
								MenuViewFunction smsFunction = sms.get(i);
								if(smsFunction.getNoParamUrl() == null && smsFunction.getUrl().equals(noParam)){
									sms.remove(i);
								}
							}
						}
					}
					showList.add(m);
				}
			}
			log.info(JSONArray.fromObject(showList).toString());
			json("" , true , JSONArray.fromObject(showList).toString());
			return;
		}
		json("" , false , "[]");
	}
}
