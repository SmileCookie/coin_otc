package com.world.controller.admin.user.ips;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.code.morphia.query.Query;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.UserLoginIpDao;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserLoginIp;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/user/ips/", des = "登录IP")
public class Index extends AdminAction {
	UserLoginIpDao dao = new UserLoginIpDao();
	UserDao userDao = new UserDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		String  userId = param("userId");
		String  userName = param("userName").trim();//用户名
		String  ip = param("ip");
		String  city = param("city").trim();

		Query<UserLoginIp> q = dao.getQuery();
		int pageSize = 50;

		// 将参数保存为attribute
		try {
			// 构建查询条件
			if(userId.length() > 0){
				q.filter("userId", userId);
			}
			if(userName.length()>0){//用户名
				Pattern pattern = Pattern.compile("^.*"  + userName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				List<User> list = userDao.find(userDao.getQuery().filter("userName", pattern)).asList();
				List<String> userIds = getIdList(list);
				if(userIds != null){
					q.filter("userId in", userIds);
				}
			}
			if(ip.length()>0){//用户名
				Pattern pattern = Pattern.compile("^.*"  + ip+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("ip", pattern);
			}
			if(city.length()>0){//用户名
//				city = URLDecoder.decode(city, "utf-8");
//				city = new String(city.getBytes("ISO8859-1"), "UTF-8");
				Pattern pattern = Pattern.compile("^.*"  + city+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("city", pattern);
			}

			long total = dao.count(q);
			if (total > 0) {
				q.order("- date");
				List<UserLoginIp> dataList = dao.findPage(q, pageNo, pageSize);
				
				Map<String, String> ids = new HashMap<String, String>();
				List<String> userIds = new ArrayList<String>();
				for(UserLoginIp user : dataList){
					if(!ids.containsKey(user.getUserId())){
						userIds.add(user.getUserId());
						ids.put(user.getUserId(), user.getUserId());
					}
				}
				UserDao uDao = new UserDao();
				Map<String, User> users = uDao.getUserMapByIds(userIds);
				for(UserLoginIp au : dataList){
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
	
	private List<String> getIdList(List<User> list){
		if(list != null && list.size() > 0){
			List<String> userIds = new ArrayList<String>();
			for(User u : list){
				userIds.add(u.getId());
			}
			return userIds;
		}
		return null;
	}
}

