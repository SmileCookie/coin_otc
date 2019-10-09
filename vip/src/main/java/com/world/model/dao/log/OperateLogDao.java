package com.world.model.dao.log;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.code.morphia.query.Query;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.log.OperateLog;
import com.world.model.entity.user.User;
import com.world.model.enums.LogCategory;
import com.world.util.CommonUtil;
import com.world.util.date.TimeUtil;
import com.yc.mongo.MorphiaMongo;

public class OperateLogDao extends MongoDao<OperateLog, String>{

	public OperateLogDao() {
	}

	public OperateLogDao(MorphiaMongo mm) {
		super(mm.getMongo(), mm.getMorphia(), mm.getDbName());
	}

	public void record(User user, LogCategory logCategory, String content, String ip, HttpServletRequest request){
		try {
			OperateLog log = new OperateLog(getDatastore());

			log.setUserId(user.getId());
			log.setUserName(user.getUserName());
			log.setCategory(logCategory.getKey());
			log.setContent(content + "【" + CommonUtil.getBrowserInfo(request) + "】");
			log.setIp(ip);
			log.setTime(TimeUtil.getNow());

			super.save(log);
		}catch (Exception ex){
			log.error(ex.toString(), ex);
		}
	}

	public void record(String userId,String userName, int category, String content, String ip, String browserInfo){
		try {
			OperateLog log = new OperateLog(getDatastore());

			log.setUserId(userId);
			log.setUserName(userName);
			log.setCategory(category);
			log.setContent(content + "【" + browserInfo + "】");
			log.setIp(ip);
			log.setTime(TimeUtil.getNow());

			super.save(log);
		}catch (Exception ex){
			log.error(ex.toString(), ex);
		}
	}

	public List<OperateLog> findByPage(Query<OperateLog> q, int pageNo, int pageSize){
		List<OperateLog> list=null;
		try {
			q.offset((pageNo - 1)*pageSize);
			q.limit(pageSize);
			list = this.find(q).asList();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return list;
	}
}
