package com.world.controller.admin.manager;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.googleauth.GoogleAuthenticator;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.model.dao.admin.role.AdminRoleDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.admin.role.AdminRole;
import com.world.util.string.MD5;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@FunctionAction(jspPath = "/admins/manager/" , des = "人员管理")
public class Index  extends AdminAction {

	AdminUserDao auDao = new AdminUserDao();
	AdminRoleDao arDao = new AdminRoleDao();

    @Page(Viewer = "/admins/manager/list.ftl")
   public void index(){
    	List<AdminRole> roles = arDao.find().asList();
 		setAttr("roles", roles);
    }

	// ajax的调用
	@Page(Viewer = "/admins/manager/ajax.ftl")
	public void ajax() {
		//获取参数
		int pageNo = intParam("page");//Integer.parseInt(param("page"));	
		String  tab=param("tab");      
		String  admId=param("admId");      
	    String  admName=param("admName");      
	    String  admUName=param("admUName");      
	    String  admRoleId=param("admRoleId");      
	    String  secret=param("secret");
	    
	    String variablesCustomer = param("variablesCustomer");
	    
		Query<AdminUser> q = auDao.getQuery();
		int pageSize = 20;
		
		Object allowSyncAdmin = GlobalConfig.getValue("allowSyncAdmin")==null?"false":GlobalConfig.getValue("allowSyncAdmin");
 		setAttr("allowSync", allowSyncAdmin);
		
		//将参数保存为attribute
	    try
	     {		
		   //构建查询条件
	         if(admId.length()>0){
	        	 q.filter("autId =", admId);
	         }
	         if(admName.length()>0){
		         Pattern pattern = Pattern.compile("^.*"  + admName+  ".*$" ,  Pattern.CASE_INSENSITIVE);  
		         q.filter("admName", pattern);
		     }
	         if(admUName.length()>0){
		         Pattern pattern = Pattern.compile("^.*"  + admUName+  ".*$" ,  Pattern.CASE_INSENSITIVE);  
		         q.filter("autUrl", pattern);
		      }
	         
	         if(admRoleId.length() > 0){
	        	 q.filter("admRoleId", Integer.parseInt(admRoleId));
	         }
	         
	         if(secret.equals("1")){
	        	 q.field("secret").notEqual(null);
	         }else if(secret.equals("0")){
	        	 q.field("secret").equal(null);
			 }
	         if(tab.length() > 0){
	        	 q.filter("isLocked", Integer.parseInt(tab));
	         }else {
	        	 tab = "0";
			 }
	         setAttr("tab", tab);
	         
	         if(variablesCustomer.length() > 0){
	        	 int vc = Integer.parseInt(variablesCustomer);
	        	 if(vc == 1){
	        		 q.filter("variablesCustomer =", vc);
	        	 }else{
	        		 q.filter("variablesCustomer <>", vc);
	        	 }
	         }
	         
	         log.info("搜索的sql语句:"+q.toString());

	        long total = auDao.count(q);
	 		if(total > 0){
	 			List<AdminUser> dataList = auDao.findPage(q.order("- createTime"), pageNo, pageSize);
	 			
	 			Map<Object , AdminRole> roles = arDao.getMapByField("id", arDao.getQuery());
	 			for(AdminUser au : dataList){
	 				au.setAr(roles.get(String.valueOf(au.getAdmRoleId())));
	 			}
	 			setAttr("dataList", dataList);
	 		}
	 		setPaging((int)total, pageNo , pageSize);
	 		setAttr("itemCount", total);
			
	       }catch(Exception ex){
	    	  log.error(ex.toString(), ex);
		      Write("",false,ex.toString());
	       }
	}

	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = "/admins/manager/aoru.ftl")
	public void aoru() {
		try {
			String id = param("id");
			setAttr("id", id);
			boolean isShow = true;
			AdminUser fi = null;
			if(!id.equals("0") && id.length() > 0){
				fi = auDao.getById(id);
				
				if(fi.getSecret() == null || fi.getSecret().length() == 0){
				}else{
					isShow = false;
				}
				
				setAttr("curData", fi);
			}
			if(isShow){
				String secret = GoogleAuthenticator.generateSecretKey();
				setAttr("secret", secret);
				setAttr("url", "/ad_admin/getGoogleAuthQr?secret="+secret + "&aname=" + (fi!=null? fi.getAdmName() : ""));
//				log.info(request.getAttribute("url"));
			}
			
			///角色组
			AdminRoleDao arDao = new AdminRoleDao();
			List<AdminRole> roles = arDao.find().asList();
			setAttr("roles", roles);
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
			String code = param("code");
//			if(!isCorrect(code, XML)){
//				return;
//			}
			
			if(!codeCorrect(XML)){
   				return;
   			}
			
			   String id = param("admId");
	           String admName=param("admName");
	           String admUName=param("admUName");
	           String admPassword=param("admPassword");
	           boolean changedPassword=false;
        	   if(admPassword.length()>0){
                    admPassword=MD5.toMD5(admName+admPassword);
                    changedPassword=true;    
        	   }
	           int admRoleId = intParam("admRoleId");
	           String admDes = param("admDes");
	           String admPhoto = request.getParameter("admPhoto");
	           int admSex = intParam("admSex");
	           int admPartId = intParam("admPartId");
	           int loginTime = intParam("loginTime");
	           String borld = param("borld");
	           if(borld==null)
	          	 borld="";
	           String evaBoard=request.getParameter("evaBoard");
	           if(evaBoard==null)
	        	   evaBoard="";
	           int isLocked=Integer.parseInt(request.getParameter("isLocked"));
	           String email = param("email");
	           String telphone = param("telphone");
	           String secret = param("secret");
				int res = 0;
				if(id.length() > 0){
					AdminUser fi = auDao.getById(id);
					
					Datastore ds = auDao.getDatastore();
					Query<AdminUser> query = ds.find(AdminUser.class, "_id", id);   
					UpdateOperations<AdminUser> ops = ds.createUpdateOperations(AdminUser.class);
					if(changedPassword){
						ops.set("admPassword" , admPassword);
					}
					long aCode = longParam("aCode");
					if(aCode > 0){
						
						if(fi.getSecret() == null || fi.getSecret().length() == 0){
							
							if(!isCorrect(secret, aCode, XML)){
								return;
							}
							
							ops.set("secret", secret);
						}
					}
					ops.set("admName" , admName);
					ops.set("admUName" , admUName);
					ops.set("admRoleId" , admRoleId);
					ops.set("admDes" , admDes);
					//ops.set("admPhoto" , admPhoto);
					ops.set("isLocked" , isLocked);
					ops.set("admSex" , admSex);
					ops.set("admPartId" , admPartId);
					ops.set("email", email);
					ops.set("telphone", telphone);
					ops.set("updateId", adminId());
					ops.set("updateTime", now());
					UpdateResults<AdminUser> ur = ds.update(query, ops);
					
					if(!ur.getHadError()){
						res = 2;
					}
				}else{
					AdminUser m = new AdminUser(auDao.getDatastore());
					m.setAdmName(admName);
					m.setAdmUName(admUName);
					m.setAdmPassword(admPassword);
					m.setAdmRoleId(admRoleId);
					m.setAdmDes(admDes);
					m.setAdmPhoto(admPhoto);
					m.setLoginTime(loginTime);
					m.setLastLoginTime(now());
					m.setLastLoginIp(ip());
					m.setIsLocked(isLocked);
					m.setBaseBorld(borld);
					m.setAdmSex(admSex);
					m.setAdmPartId(admPartId);
					m.setEmail(email);
					m.setTelphone(telphone);
					m.setCreateId(adminId());
					m.setCreateTime(now());
					
					long aCode = longParam("aCode");
					if(aCode > 0){
						if(!isCorrect(secret, aCode, XML)){
							return;
						}
						m.setSecret(secret);
					}
					
					if(auDao.save(m) != null){
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
	
	private String see_a_id_key = "see_a_s_id";
	@Page(Viewer = "/admins/manager/seeF.ftl")
	public void seeF(){
		String admId = param("admId");
		Cache.Set(see_a_id_key, admId, 10*60);
	}
	
	@Page(Viewer = "/admins/manager/seeS.ftl")
	public void seeS() {
		String admId = Cache.Get(see_a_id_key);
		if(admId == null){
			setAttr("errmsg", "管理员id为空。");
			return;
		}
		
		String code = param("code");
		if(!isCorrect(code, XML)){
			setAttr("errmsg", "手机验证码错误。");
			return;
		}
		
		if(!codeCorrect(XML)){
			setAttr("errmsg", "谷歌验证码错误。");
			return;
		}
		
		if(admId.length()>0){
			AdminUser admUser = auDao.getById(admId);
//			String secret = GoogleAuthenticator.generateSecretKey();//admUser.getSecret();
			setAttr("secret", admUser.getSecret());
			setAttr("admId", admUser.getAdmId());
			setAttr("url", "/ad_admin/getGoogleAuthQr?secret="+admUser.getSecret());
			Cache.Set(see_a_id_key, admId+"_"+admUser.getSecret(), 10*60);
		}
	}
	
	@Page(Viewer = JSON)
	public void saveSecret() {
		String admId_secret = Cache.Get(see_a_id_key);
		if(admId_secret == null){
			json("参数错误，操作失败。", false, "");
			return;
		}
		String admId = admId_secret.split("\\_")[0];
		String secret = admId_secret.split("\\_")[1];
		long aCode = longParam("aCode");
		
		if(!isCorrect(secret, aCode, JSON)){
			return;
		}
			
		Datastore ds = auDao.getDatastore();
		Query<AdminUser> q = ds.find(AdminUser.class, "_id", admId);   
		UpdateOperations<AdminUser> ops = ds.createUpdateOperations(AdminUser.class);
		ops.set("secret", secret);
		
		UpdateResults<AdminUser> ur = ds.update(q, ops);
		if(!ur.getHadError()){
			json("保存成功", true, "");
			Cache.Delete(see_a_id_key);
		}else{
			json("保存失败", false, "");
		}
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
                auDao.delById(id);
                Write("删除成功",true,"");
                return;
			}
		}
		Write("未知错误导致删除失败！",false,"");
	}
	
	
	@Page(Viewer = XML)
	public void unLocked() {
		if(!codeCorrect(XML)){
			return;
		}
		String id = param("id");
		if(id.length()>0){
			String key = id + "_gauth";
			Cache.Delete(key);
			Write("解锁谷歌验证锁定成功",true,"");
			return;
		}
		Write("未知错误导致删除失败！",false,"");
	}
	
	
	@Page(Viewer = XML)
	public void doSetKefu() {
		if(!codeCorrect(XML)){
			return;
		}
		String id = param("id");
		int set = intParam("set");
		
		if(set != 1){
			set = 0;
		}
		if(id.length()>0){
			boolean res = true;
			if(res){
				Datastore ds = auDao.getDatastore();
				UpdateResults<AdminUser> ur = ds.update(auDao.getQuery().filter("_id =", id), 
						ds.createUpdateOperations(AdminUser.class)
						.set("variablesCustomer", set));
				if(ur.getError() == null){
					Write("设置成功",true,"");
					return;
				}
			}
		}
		Write("未知错误导致操作失败！",false,"");
	}
	
	
	@Page(Viewer = JSON)
	public void checkCode(){
		if(codeCorrect()){
			json("可以直接通过", true, "");
		}else{
			//WriteError("需要验证码");
		}
	}
	
	@Page(Viewer = "/admins/manager/code.ftl")
	public void IframeGoogleCode(){
		String callback=request.getParameter("callback");
		String needClose=param("needClose");

		setAttr("callback", callback);
	  	setAttr("needClose", needClose);

	  	String validType = session.getAdmin(this).validType;//SessionGet("validType");
	  	setAttr("validType", validType);
	}
}
