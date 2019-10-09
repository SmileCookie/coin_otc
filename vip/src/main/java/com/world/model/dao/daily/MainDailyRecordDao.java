package com.world.model.dao.daily;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mongo.MongoDao;
import com.world.model.entity.admin.logs.DailyRecord;
import com.world.model.entity.admin.logs.DailyType;

public class MainDailyRecordDao extends MongoDao<DailyRecord, String>{

	public String insertOneRecord(DailyType type, String memo, String adminId, String ip, Timestamp createTime , int userId , BigDecimal amount){
		DailyRecord record = new DailyRecord(getDatastore());
		
		record.setTypeId(type.getKey());
		record.setMemo(memo);
		record.setAdminId(adminId);
		record.setIp(ip);
		record.setCreateTime(createTime);
		
		String nid = super.save(record).getId().toString();
		
		return nid;
	}
	
	public String insertOneRecord(DailyType type, String memo, String adminId, String ip, Timestamp createTime){
		DailyRecord record = new DailyRecord(getDatastore());
		
		record.setTypeId(type.getKey());
		record.setMemo(memo);
		record.setAdminId(adminId);
		record.setIp(ip);
		record.setCreateTime(createTime);
		
		String nid = super.save(record).getId().toString();
		
		return nid;
	}
	
	public String insertOneRecord(DailyType type,String userId, String memo, String adminId, String ip, Timestamp createTime, String loadImg){
		DailyRecord record = new DailyRecord(getDatastore());
		
		record.setUserId(userId);
		record.setTypeId(type.getKey());
		record.setMemo(memo);
		record.setAdminId(adminId);
		record.setIp(ip);
		record.setCreateTime(createTime);
		
		String nid = super.save(record).getId().toString();
		return nid;
	}
}
