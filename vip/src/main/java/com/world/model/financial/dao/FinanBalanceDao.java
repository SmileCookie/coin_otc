package com.world.model.financial.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.world.data.mysql.Bean;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanBalance;

public class FinanBalanceDao extends DataDaoSupport{
	
	public FinanBalance get(int id){
		return (FinanBalance)super.get("SELECT * FROM finanbalance WHERE id = ? AND isDel = ?", new Object[]{id, false}, FinanBalance.class);
	}

	public int delById(int id){
		return super.delete("UPDATE finanbalance SET isDel = ? WHERE id = ?", new Object[]{true, id});
	}
	
	public OneSql saveSql(FinanBalance balance){
		return new OneSql("INSERT INTO finanbalance (accountId, memo, fundType, amount, createId, createTime, groupTime) VALUES (?,?,?,?,?,?,?)", 1, new Object[]{
				balance.getAccountId(), balance.getMemo(), balance.getFundType(), balance.getAmount(), balance.getCreateId(), balance.getCreateTime(), balance.getGroupTime()
		});
	}
	
	public void setProperties(List<Bean> dataList){
		
		List<String> adminIds = new ArrayList<String>();
		String accountIds = "";

		for(Bean b : dataList){
			FinanBalance e = (FinanBalance)b;
			adminIds.add(e.getCreateId()+"");
			accountIds += ","+e.getAccountId();
		}
		
		Map<String , AdminUser> users = new AdminUserDao().getUserMapByIds(adminIds);
		for(Bean b : dataList){
			FinanBalance e = (FinanBalance)b;
			e.setaUser(users.get(e.getCreateId()+""));
		}
		
		if(accountIds.length() > 1)
			accountIds = accountIds.substring(1);
		
		Map<Integer , FinanAccount> accounts = new FinanAccountDao().getAccountMapByIds(accountIds);
		for(Bean b : dataList){
			FinanBalance e = (FinanBalance)b;
			e.setAccount(accounts.get(e.getAccountId()));
		}
	}
}
