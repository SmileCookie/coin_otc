package com.world.controller.admin.user.authlog;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.query.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.user.User;
import com.world.model.entity.user.authen.AuthenLog;
import com.world.model.entity.user.authen.AuthenType;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/user/authlog/", des = "认证日志")
public class Index extends AdminAction {
	AuthenLogDao dao = new AuthenLogDao();
	AdminUserDao adminDao = new AdminUserDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		String  userId = param("userId");
		String  userName = param("userName");//用户名
		int type = intParam("type");
		
		EnumSet<AuthenType> autype = EnumUtils.getAll(AuthenType.class);
		setAttr("authType", autype);

		Query<AuthenLog> q = dao.getQuery();
		int pageSize = 20;

		// 将参数保存为attribute
		try {
			// 构建查询条件
			if(userId.length() > 0){
				q.filter("userId", userId);
			}
			if(userName.length()>0){//用户名
				User u = new UserDao().getByField("userName", userName);
				if(u!=null)
					q.filter("userId", u.getId());
			}
			if(type > 0){
				q.filter("type", type);
			}
			q.order("- time");
			log.info("搜索的sql语句:" + q.toString());

			long total = dao.count(q);
			if (total > 0) {
				List<AuthenLog> dataList = dao.findPage(q, pageNo, pageSize);
				
				List<String> adminIds = new ArrayList<String>();
				List<String> userIds = new ArrayList<String>();
				for(AuthenLog au : dataList){
					if(au.getAdminId() != null && au.getAdminId().length() > 0)
						adminIds.add(au.getAdminId());
					userIds.add(au.getUserId());
				}
				
				AdminUserDao auDao = new AdminUserDao();
				if(adminIds.size() > 0){
					Map<String , AdminUser> users = auDao.getUserMapByIds(adminIds);
					for(AuthenLog au : dataList){
						au.setaUser(users.get(au.getAdminId()));
					}
				}
				
				UserDao uDao = new UserDao();
				Map<String, User> users = uDao.getUserMapByIds(userIds);
				for(AuthenLog au : dataList){
					au.setUser(users.get(au.getUserId()));
				}
				
				setAttr("dataList", dataList);
			}
			setPaging((int) total, pageNo, pageSize);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
}

