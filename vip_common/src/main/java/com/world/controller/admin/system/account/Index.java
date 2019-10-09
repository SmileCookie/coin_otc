package com.world.controller.admin.system.account;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.account.SendEmailAccountDao;
import com.world.model.entity.account.SendEmaillAccount;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import com.world.web.convention.annotation.FunctionAction;

import java.util.List;
import java.util.regex.Pattern;
@FunctionAction(jspPath = "/admins/system/account/" , des="邮件账户")
public class Index extends BaseAction {

	SendEmailAccountDao dao = new SendEmailAccountDao();

   @Page(Viewer = "/admins/system/account/list.ftl")
   public void index(){
	 //获取参数
		int pageNo = intParam("page");	
		String  id = param("id");      
	    String  sendName=param("sendName");      
	    String  fromAddr=param("fromAddr");      
	    
		Query<SendEmaillAccount> q = dao.getQuery();
		int pageSize = 20;
		
		//将参数保存为attribute
	    try
	     {		
		   //构建查询条件
	         if(id.length()>0){
	        	 q.filter("_id =", id);
	         }
	         if(sendName.length()>0){
		         Pattern pattern = Pattern.compile("^.*"  + sendName+  ".*$" ,  Pattern.CASE_INSENSITIVE);  
		         q.filter("sendName", pattern);
		     }
	         if(fromAddr.length()>0){
	        	 Pattern pattern = Pattern.compile("^.*"  + fromAddr+  ".*$" ,  Pattern.CASE_INSENSITIVE);  
	        	 q.filter("fromAddr", pattern);
	         }
	         log.info("搜索的sql语句:"+q.toString());

	        long total = dao.count(q);
	 		if(total > 0){
	 			List<SendEmaillAccount> dataList = dao.findPage(q, pageNo, pageSize);
	 			setAttr("dataList", dataList);
	 		}
	 		setPaging((int)total, pageNo , pageSize);
	       }catch(Exception ex){
	    	  log.error(ex.toString(), ex);
		      Write("",false,ex.toString());
	       }
   }

	// ajax的调用
	@Page(Viewer = "/admins/system/account/ajax.ftl")
	public void ajax() {
		index();
	}

	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = "/admins/system/account/aoru.ftl")
	public void aoru() {
		try {
			String id = param("id");
			setAttr("id", id);
			if(id.length() > 0){
				SendEmaillAccount data = dao.getById(id);
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
			   String sendName = param("sendName");
	           String fromAddr = param("fromAddr");
	           String mailServerHost = param("mailServerHost");
	           String mailServerPort = param("mailServerPort");
	           String emailUserName = param("emailUserName");
	           String emailPassword = param("emailPassword");
	           int status = intParam("status");
	          
			int res = 0;
			Datastore ds = dao.getDatastore();
			if(id.length() > 0){
				Query<SendEmaillAccount> query = ds.find(SendEmaillAccount.class, "_id", id);   
				UpdateResults<SendEmaillAccount> ur = ds.update(query, ds.createUpdateOperations(SendEmaillAccount.class)
						.set("sendName", sendName)
						.set("fromAddr", fromAddr)
						.set("mailServerHost", mailServerHost)
						.set("mailServerPort", mailServerPort)
						.set("emailUserName", emailUserName)
						.set("emailPassword", emailPassword)
						.set("status", status)
						.set("ldate", now()));
				if(!ur.getHadError()){
					res = 2;
				}
			}else{
				SendEmaillAccount m = new SendEmaillAccount(ds);
				m.setSendName(sendName);
				m.setFromAddr(fromAddr);
				m.setMailServerHost(mailServerHost);
				m.setMailServerPort(mailServerPort);
				m.setEmailUserName(emailUserName);
				m.setEmailPassword(emailPassword);
				m.setStatus(status);
				m.setLdate(now());
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
                dao.delById(id);
//				if(dao.delById(id).getError() == null){
                Write("删除成功",true,"");
                return;
//				}
			}
		}
		Write("未知错误导致删除失败！",false,"");
	}
}
