package com.world.model.dao.user.authen;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.world.data.mongo.MongoDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.user.User;
import com.world.model.entity.user.authen.AuthenLog;
import com.world.model.entity.user.authen.AuthenType;

public class AuthenLogDao extends MongoDao<AuthenLog, String>{

	/**
	 * 增加一条管理员日志
	 * @param type
	 * @param memo
	 * @param adminId
	 * @param ip
	 * @param date
	 * @return
	 */
	public String insertOneRecord(String userId, String adminId, String memo, String ip, Timestamp time){
		AuthenLog log = new AuthenLog(getDatastore());
		
		log.setUserId(userId);
		log.setAdminId(adminId);
		log.setDes(memo);
		log.setIp(ip);
		log.setTime(time);
		
		String nid = super.save(log).getId().toString();
		return nid;
	}

	public String insertOneRecord(int type, String userId, String adminId, String memo, String ip){
		try {
			AuthenLog log = new AuthenLog(getDatastore());
			
			log.setType(type);
			log.setUserId(userId);
			log.setAdminId(adminId);
			log.setDes(memo);
			log.setIp(ip);
			log.setTime(now());
			
			String nid = super.save(log).getId().toString();
			return nid;
		} catch (Exception e) {
			log.error("添加用户日志信息失败。", e);
		}
		return null;
	}
	
	public String save(int type, String userId, String memo, String ip){
		return insertOneRecord(type, userId, "0", memo, ip);
	}
	
	public List<AuthenLog> findAuthenLog(String userId){
		List<AuthenLog> logs = super.find(getQuery().filter("userId", userId).order("- time").limit(10)).asList();
		List<String> userIds = new ArrayList<String>();
		for(AuthenLog u : logs){
			userIds.add(u.getUserId());
		}
		
		if(userIds.size() > 0){
			Map<String , User> users = new UserDao().getUserMapByIds(userIds);
			for(AuthenLog u : logs){
				u.setUser(users.get(u.getUserId()));
			}
		}
		return logs;
	}
	
	//是否可以转出资产，手机变动5天之内需要审核
	public boolean couldOutFunds(String userId){
		AuthenLogDao logDao = new AuthenLogDao();
		AuthenLog phoneLog = logDao.findOne(logDao.getQuery().filter("userId", userId).filter("type", AuthenType.mobile.getKey()).order("-time"));
		if(phoneLog == null || phoneLog != null && (System.currentTimeMillis() - phoneLog.getTime().getTime()) >= 1000*60*60*24*5){
			return true;
		}
		return false;
	}
}
