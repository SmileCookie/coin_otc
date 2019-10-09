package com.world.controller.admin.system.dailyrecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.code.morphia.query.Query;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.admin.logs.DailyRecord;
import com.world.model.entity.admin.logs.DailyType;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/system/dailyrecord/", des = "系统日志")
public class Index extends AdminAction {
	DailyRecordDao dao = new DailyRecordDao();
	AdminUserDao auDao = new AdminUserDao();
	
	@Page(Viewer = "/admins/system/dailyrecord/list.ftl")
	public void index() {
		if(!couldSearch()){
			return;
		}
		// 获取参数
		int pageNo = intParam("page");
		String userId = param("userId");//用户名
		int type = intParam("type");
		String memo = param("memo");

		Query<DailyRecord> q = dao.getQuery();
		int pageSize = 20;

		// 将参数保存为attribute
		try {
			// 构建查询条件
			if(userId.length() > 0){
				q.filter("adminId", userId);
			}

			if(type > 0){
				q.filter("typeId", type);
			}

			if(memo.length() > 0){
				Pattern pattern = Pattern.compile("^.*"  + memo+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("memo", pattern);
			}

			log.info("搜索的sql语句:" + q.toString());
			
			q.order("- createTime");

			long total = dao.count(q);
			if (total > 0) {
				List<DailyRecord> dataList = dao.findPage(q, pageNo, pageSize);
				
				List<String> adminIds = new ArrayList<String>();
				for(DailyRecord u : dataList){
					if(u.getAdminId() != null && u.getAdminId().length() > 0)
						adminIds.add(u.getAdminId());
				}
				
				if(adminIds.size() > 0){
					Map<String , AdminUser> users = auDao.getUserMapByIds(adminIds);
					for(DailyRecord u : dataList){
						u.setaUser(users.get(u.getAdminId()));
					}
				}
				
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setAttr("pager", setPaging((int) total, pageNo, pageSize));
			setAttr("admins", auDao.find().asList());
			setAttr("types", EnumUtils.getAll(DailyType.class));
			
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	// ajax的调用
	@Page(Viewer = "/admins/system/dailyrecord/ajax.ftl")
	public void ajax() {
		index();
	}
}

