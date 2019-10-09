package com.world.model.financial.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.world.data.mysql.Bean;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanError;
import com.world.model.financial.entity.FinanUseType;

public class FinanErrorDao extends DataDaoSupport{
	
	public FinanError get(int id){
		return (FinanError)super.get("SELECT * FROM FinanError WHERE id = ? AND isDel = ?", new Object[]{id, false}, FinanError.class);
	}

	public int save(FinanError error){
		
		int count = super.save("INSERT INTO FinanError (accountId, useTypeId, fundType, money, commission, memo, status, ip, createId, createTime) " +
				"VALUES (?,?,?,?,?,?,?,?,?,?)", new Object[]{
				error.getAccountId(), error.getUseTypeId(), error.getFundType(), error.getMoney(), error.getCommission(),
			    error.getMemo(), error.getStatus(), error.getIp(), error.getCreateId(), error.getCreateTime()
		});
		return count;
	}
	
	public int update(long connId, int userId, String userName, int status, String memo, int adminId, Timestamp now, int id){
		
		int count = super.update("UPDATE FinanError SET connectionId=?, userId=?, userName=?, status=?, memo=?, updateId=?, updateTime=? WHERE id=?", 
					new Object[]{
						connId, userId, userName, status, memo, adminId, now, id
					});
		return count;
	}
	
	public OneSql updateSql(long connId, int userId, String userName, int status, String memo, int adminId, Timestamp now, int id){
		return new OneSql("UPDATE FinanError SET connectionId=?, userId=?, userName=?, status=?, memo=?, updateId=?, updateTime=? WHERE id=?", 1,
				new Object[]{
				connId, userId, userName, status, memo, adminId, now, id
			});
	}

	public void setProperties(List<Bean> dataList){
		
		List<String> adminIds = new ArrayList<String>();
		
		String accountIds = "";
		String useTypeIds = "";

		for(Bean b : dataList){
			FinanError e = (FinanError)b;
			adminIds.add(e.getCreateId()+"");
			
			accountIds += ","+e.getAccountId();
			useTypeIds += ","+e.getUseTypeId();
		}
		
		Map<String , AdminUser> users = new AdminUserDao().getUserMapByIds(adminIds);
		for(Bean b : dataList){
			FinanError e = (FinanError)b;
			e.setaUser(users.get(e.getCreateId()+""));
		}
		
		if(accountIds.length() > 1)
			accountIds = accountIds.substring(1);
		
		Map<Integer , FinanAccount> accounts = new FinanAccountDao().getAccountMapByIds(accountIds);
		for(Bean b : dataList){
			FinanError e = (FinanError)b;
			e.setAccount(accounts.get(e.getAccountId()));
		}
		
		if(useTypeIds.length() > 1){
			useTypeIds = useTypeIds.substring(1);
		}
			
		Map<Integer , FinanUseType> usetypes = new FinanUseTypeDao().getUseTypeMapByIds(useTypeIds);
		for(Bean b : dataList){
			FinanError e = (FinanError)b;
			e.setUseType(usetypes.get(e.getUseTypeId()));
		}
	}
}
