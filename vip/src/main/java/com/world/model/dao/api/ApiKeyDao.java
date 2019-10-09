package com.world.model.dao.api;

import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.api.ApiKey;
import com.world.model.entity.coin.CoinProps;

public class ApiKeyDao extends DataDaoSupport<ApiKey>{
	
	public ApiKeyDao(){
		super();
	}
	
	public ApiKeyDao(CoinProps coint){
		super();
		this.database = coint.database;
	}
	
	//保存记录
	public int saveOne(ApiKey p){
		return Data.Insert(database, "insert into apikey(userId,userName,ipaddrs,accesskey,secretkey,isAct,isLock, isDel, addTime) values(?,?,?,?,?,?,?,?,?)", new Object[] { 
				p.getUserId(), p.getUserName(), p.getIpaddrs(), p.getAccesskey(), p.getSecretkey(), p.getIsAct(), p.getIsLock(), p.getIsDel(), p.getAddTime()
		});
	}
	
	//激活
	public int activate(int id){
		return Data.Update(database, "update apikey set isAct=1 where id=? and isDel=0", new Object[]{id });
	}
	
	//锁定
	public int lock(int id){
		return Data.Update(database, "update apikey set isLock=1 where id=? and isDel=0", new Object[]{id });
	}
	
	//解锁
	public int unlock(int id){
		return Data.Update(database, "update apikey set isLock=0 where id=? and isDel=0", new Object[]{id });
	}
	
	//删除
	public int delete(int id){
		return Data.Update(database, "update apikey set isDel=1 where id=? and isDel=0", new Object[]{id });
	}
	
	//根据用户查询
	public List<ApiKey> findByUser(String userId){
		return Data.QueryT(database, "select * from apikey where userId=? and isDel=0 order by id desc", new Object[]{userId }, ApiKey.class);
	}
	
	//根据公钥查询
	public ApiKey findByKey(String key){
		return (ApiKey) Data.GetOne(database, "select * from apikey where accesskey=? and isDel=0", new Object[]{key }, ApiKey.class);
	}
	
	//根据ID查询
	public ApiKey findById(int id){
		return (ApiKey) Data.GetOne(database, "select * from apikey where id=? and isDel=0", new Object[]{id }, ApiKey.class);
	}
	
	public int count(String userId){
		return super.count("select count(*) from apikey where isDel=0 and userId=?", new Object[]{userId });
	}
	
	//根据用户查询 API key
	public  ApiKey  getUserApiKey(String userId){
		return (ApiKey)Data.GetOne(database,"select * from apikey where userId=? and isDel=0 order by id desc", new Object[]{userId }, ApiKey.class);
	}
	
	//更新用户的API Key
	public int openApiKey(String userId,String accesskey,String secretkey,String ipaddrs){
		return Data.Update(database, "update apikey set isAct=?,accesskey=?,secretkey=?,ipaddrs=? where userId=? and isDel=0 and isAct=0 ", new Object[]{1,accesskey,secretkey,ipaddrs,userId });
	}
	
	//关闭
	public int closeApiKey(String userId){
		return Data.Update(database, "update apikey set isAct=0 where userId=? and isDel=0", new Object[]{userId });
	}
	
	//更新用户的API Key 的IP地址
	public int updateApiKeyIpAddrs(String userId,String ipaddrs){
		return Data.Update(database, "update apikey set ipaddrs=? where userId=? and isDel=0 and isAct=1 ", new Object[]{ipaddrs,userId });
	}
	
	//更新用户的API Key
	public int updateApiKey(String userId,String accesskey,String secretkey){
		return Data.Update(database, "update apikey set isAct=?,accesskey=?,secretkey=?  where userId=? and isDel=0   ", new Object[]{1,accesskey,secretkey ,userId });
	}
	
	//锁定
	public OneSql lock(String userId){
		return new OneSql("update apikey set isLock=1 where userId=? and isDel=0", 1, new Object[]{userId });
	}
	
	//解锁
	public OneSql unlock(String userId){
		return new OneSql("update apikey set isLock=0 where userId=? and isDel=0", 1, new Object[]{userId });
	}
}
