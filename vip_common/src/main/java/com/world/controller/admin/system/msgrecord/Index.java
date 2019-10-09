package com.world.controller.admin.system.msgrecord;
import java.util.List;
import java.util.regex.Pattern;

import com.world.config.GlobalConfig;
import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import com.yc.dao.msg.MsgDao;
import com.yc.entity.msg.Msg;
import com.yc.entity.msg.MsgSendStatus;
import com.yc.mongo.MorphiaMongoUtil;

@FunctionAction(jspPath = "/admins/system/msgrecord/", des = "消息日志")
public class Index extends AdminAction {
	MsgDao dao = new MsgDao(MorphiaMongoUtil.getMorphiaMongo());
	
	@Page(Viewer = "/admins/system/msgrecord/list.ftl")
	public void index() {
		if(!couldSearch()){
			return;
		}
		
		// 获取参数
		int pageNo = intParam("page");
		String userId = param("userId");//用户名
		String userName = param("userName");//用户名
		String sendStat = param("sendStat"); 
		int type = intParam("type");
		String title = param("title");
		String receivePhoneNumber = param("phone");
		String receiveEmail = param("email");
		
		Datastore ds = dao.getDatastore();
		Query<Msg> q = ds.createQuery(Msg.class);
		
		int pageSize = 20;

		// 将参数保存为attribute
		try {
			// 构建查询条件
			if(userId.length() > 0){
				q.filter("userId", userId);
			}
			
			if(userName.length() > 0){
				Pattern pattern = Pattern.compile("^.*"  + userName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("userName", pattern);
			}

			if(sendStat.length() > 0){
				q.filter("sendStat", Integer.parseInt(sendStat));
			}
			
			if(type > 0){
				q.filter("type", type);
			}

			if(title.length() > 0){
				Pattern pattern = Pattern.compile("^.*"  + title+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("title", pattern);
			}

			if(receivePhoneNumber.length() > 0){
				Pattern pattern = Pattern.compile("^.*"  + receivePhoneNumber+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("receivePhoneNumber", pattern);
			}

			if(receiveEmail.length() > 0){
				Pattern pattern = Pattern.compile("^.*"  + receiveEmail+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("receiveEmail", pattern);
			}
			
			log.info("搜索的sql语句:" + q.toString());
			
			q.order("- addDate");

			long total = dao.count(q);
			if (total > 0) {
				List<Msg> dataList = dao.findByPage(q, pageNo, pageSize);
				
//				for(Msg m : dataList){
//					String cont = m.getCont();
//					if(cont.indexOf("为：") > 0){
//						cont = cont.replace(cont.substring(cont.indexOf("为："), cont.indexOf("为：")+8), "******");
//					}
//				}
				
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setAttr("pager", setPaging((int) total, pageNo, pageSize));

			// 设置当前环境,生产环境后台消息日志验证码显示为*****
			setAttr("showCode", GlobalConfig.showCode);

		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	// ajax的调用
	@Page(Viewer = "/admins/system/msgrecord/ajax.ftl")
	public void ajax() {
		index();
	}
	
	@Page(Viewer = XML)
	public void reSend() {
		if(!codeCorrect(XML)){
			return;
		}
		
		String id = param("id");
		Datastore ds = dao.getDatastore();
		Query<Msg> q = ds.createQuery(Msg.class).filter("_id =", new ObjectId(id));
		UpdateOperations<Msg> ops = ds.createUpdateOperations(Msg.class).set("sendStat",MsgSendStatus.no.getKey());
		UpdateResults<Msg> res = dao.update(q, ops);
		
		if(res.getError() == null){
			Write("重发成功",true,"");
		}else{
			Write("重发失败",true,"");
		}
		
	}
}

