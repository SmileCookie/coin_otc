package com.world.model.dao.robot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.code.morphia.query.Query;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.robot.RobotConfig;

@SuppressWarnings("serial")
public class RobotDao extends MongoDao<RobotConfig, String>{
	Logger logger = Logger.getLogger(RobotDao.class);
	
	private static Map<String,RobotConfig> robotMap = new HashMap<String,RobotConfig>();
	
	/**
	 * 根据id查询一条机器人
	 * @param id 标识号
	 * @return 查询的唯一一条结果
	 */
	public RobotConfig getById(String id){
		Query<RobotConfig> q = getQuery(RobotConfig.class).filter("_id =", id); 
		return super.findOne(q); 
	}
	
	/**
	 * 添加一条机器人
	 */
	public String addNews(RobotConfig n){
		String nId=super.save(n).getId().toString();
		return nId;
	}

	
	/**
	 * 根据账户查询机器人
	 * @param account 账户名称
	 * @return 查询的唯一一条结果
	 */
	public RobotConfig getByaccount(String account){
		Query<RobotConfig> q = getQuery(RobotConfig.class).filter("account =", account); 
		return super.findOne(q); 
	}
	
	
	/**
	 * 初始化加载数据到内存
	 */
	public void reload(){
		Query<RobotConfig> q = getQuery(RobotConfig.class);
		List<RobotConfig> dataList = this.find(q).asList();
		robotMap.clear();
		if(dataList!=null){
			for(RobotConfig robotConfig:dataList){
				robotMap.put(robotConfig.getId(), robotConfig);
			}
		}
	}
	
	/**
	 * 在缓存中取数据，提高性能
	 * @param id
	 * @return
	 */
	public  RobotConfig getConfigFromCache(String id){
		if(robotMap.isEmpty()){
			reload();//重新加载
		}
		RobotConfig robotConfig = robotMap.get(id);
		return robotConfig;
		
	}
	
}
