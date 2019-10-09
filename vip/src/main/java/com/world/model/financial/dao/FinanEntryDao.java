package com.world.model.financial.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.CointTable;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.WalletBean;
import com.world.model.financial.entity.AccountType;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanEntry;
import com.world.model.financial.entity.FinanUseType;
import com.world.util.date.TimeUtil;

public class FinanEntryDao extends DataDaoSupport{
	
	public FinanEntry get(int id){
		return (FinanEntry)super.get("SELECT * FROM finanentry WHERE id = ? AND isDel = ?", new Object[]{id, false}, FinanEntry.class);
	}

	public int delById(int id){
		return super.delete("UPDATE finanentry SET isDel = ? WHERE id = ?", new Object[]{true, id});
	}
	
	/**
	 * 返回一条财务记录的语句
	 * @param entry
	 * @return
	 */
	public OneSql insertSql(FinanEntry entry){
		return insertSql(entry.getAccountId(), entry.getUseTypeId(), entry.getFundType(), entry.getFunds(), entry.getFundsComm(),
				entry.getUserId(), entry.getUserName(), entry.getMemo(), entry.getIp(), entry.getFromAccountId(), entry.getToAccountId(), entry.getConnectionId(), entry.getCreateId(), entry.getCreateTime());
		
	}

	public OneSql insertSql(int accountId, int useTypeId, int fundType, BigDecimal funds, BigDecimal fundsComm, int userId, String userName, String memo, 
			String ip, int fromAccountId, int toAccountId, long connId, int createId, Timestamp createTime){
		BigDecimal currentPrice = BigDecimal.ZERO;
		CoinProps coint = DatabasesUtil.coinProps(fundType);
//		currentPrice = UserCache.getPriceByKey(coint.getPriceKey());
		
		return new OneSql("INSERT INTO finanentry (accountId, useTypeId, fundType, funds, fundsComm, userId, userName, memo, ip, fromAccountId, toAccountId, connectionId, balance, currentPrice, createId, createTime) " +
				"SELECT "+accountId+","+useTypeId+","+fundType+","+funds+","+fundsComm+","+userId+",'"+userName+"','"+memo+"','"+ip+"',"+fromAccountId+","+toAccountId+","+connId+",amount AS balance,"+currentPrice+","+createId+",'"+createTime+"' FROM " +
						"finanAccount WHERE id = "+accountId+" AND isDel = false", 1, new Object[]{});
	}
	
	FinanAccountDao aDao = new FinanAccountDao();
	/**
	 * 针对充值自动保存财务记录 （比特币，莱特币）
	 * 单独的添加,扣除账户金额的通用方法
	 * @return
	 */
	public List<OneSql> saveOneEntry(int accountId, int useTypeId, int fundType, BigDecimal funds, BigDecimal fundsComm, int userId, String userName, String memo, String ip, long connId, int adminId, boolean isAdd){
		FinanEntry entry = new FinanEntry(accountId, useTypeId, fundType, funds, fundsComm, userId, userName, memo, ip, 0, 0, connId);
		Timestamp now = now();
		List<OneSql> paySqls = new ArrayList<OneSql>();
		
		entry.setCreateId(adminId);
		entry.setCreateTime(now);
		
		paySqls.add(aDao.updateMoney(accountId, funds, fundsComm, fundType, isAdd));
		paySqls.add(insertSql(entry));
		
		return paySqls;
	}
	
	public void setProperties(List<Bean> dataList){
		
		List<String> adminIds = new ArrayList<String>();
//		List<String> userIds = new ArrayList<String>();
		
		String accountIds = "";
		String useTypeIds = "";
		String withdrawIds = "";

		for(Bean b : dataList){
			FinanEntry e = (FinanEntry)b;
			adminIds.add(e.getCreateId()+"");
//			if(e.getUserId() > 0)
//				userIds.add(e.getUserId()+"");
			
			accountIds += ","+e.getAccountId();
			useTypeIds += ","+e.getUseTypeId();
			if(e.getUseTypeId() == 4){//RMB提现
				withdrawIds += ","+e.getConnectionId();
			}
		}
		
		Map<String , AdminUser> users = new AdminUserDao().getUserMapByIds(adminIds);
		for(Bean b : dataList){
			FinanEntry e = (FinanEntry)b;
			e.setaUser(users.get(e.getCreateId()+""));
		}
		
		if(accountIds.length() > 1)
			accountIds = accountIds.substring(1);
		
		Map<Integer , FinanAccount> accounts = new FinanAccountDao().getAccountMapByIds(accountIds);
		for(Bean b : dataList){
			FinanEntry e = (FinanEntry)b;
			e.setAccount(accounts.get(e.getAccountId()));
		}
		
		if(useTypeIds.length() > 1){
			useTypeIds = useTypeIds.substring(1);
		}
		Map<Integer , FinanUseType> usetypes = new FinanUseTypeDao().getUseTypeMapByIds(useTypeIds);
		for(Bean b : dataList){
			FinanEntry e = (FinanEntry)b;
			e.setUseType(usetypes.get(e.getUseTypeId()));
		}
		
		
//		if(userIds.size() > 0){
//			Map<String, User> maps = new UserDao().getUserMapByIds(userIds);
//			for(Bean b : dataList){
//				FinanEntry e = (FinanEntry)b;
//				e.setUser(maps.get(e.getUserId()+""));
//			}
//		}
	}
	

	/**
	 * 查询出当前账户发生的最后一笔财务记录
	 * @param ids
	 * @return
	 */
	public Map<Integer, FinanEntry> getEntryMapByIds(String ids){
		Map<Integer , FinanEntry> maps = new LinkedHashMap<Integer, FinanEntry>();
		Timestamp ts = TimeUtil.getAfterDate(-2);
		List<Bean> entrys = (List<Bean>)Data.Query("SELECT * FROM finanentry WHERE accountId IN ("+ids+") AND createTime < ? order by createTime", new Object[]{ts}, FinanEntry.class);
		if(entrys != null && entrys.size() > 0){
			for(Bean b : entrys){
				FinanEntry a = (FinanEntry)b;
				maps.put(a.getAccountId(), a);
			}
		}
		return maps;
	}

	/**
	 * 有用户充值理财时自动添加到财务账户
	 * @return
	 */
	public void syncFinanAccount(List<OneSql> sqls, CoinProps coint, DetailsBean bdb){
		sqls.add(new OneSql("UPDATE "+coint.getStag()+"details SET succonfirm = 1 WHERE detailsId = ? and succonfirm = 0", 1, new Object[]{bdb.getDetailsId()}));

		FinanUseTypeDao useTypeDao = new FinanUseTypeDao();
		FinanUseType usetype = useTypeDao.getByType(1);
		int useTypeId = usetype.getId();

		FinanAccount fa = (FinanAccount) new FinanAccountDao().get("SELECT * FROM finanaccount WHERE isDel = false AND fundType = ? AND type = ? AND isDefault = true", new Object[]{coint.getFundsType(), AccountType.charge.getKey()}, FinanAccount.class);
		sqls.addAll(new FinanEntryDao().saveOneEntry(fa.getId(), useTypeId, coint.getFundsType(), bdb.getAmount(), BigDecimal.ZERO, Integer.parseInt(bdb.getUserId()), bdb.getUserName(), "充值"+coint.getTag()+"自动确认", "127.0.0.1", bdb.getDetailsId(), 0, true));
	}



	/**
	 * 提币成功录入
	 * @return
	 */
	public void syncDownloadFinanAccount(List<OneSql> sqls, CoinProps coint, DownloadBean db){
		sqls.add(new OneSql("UPDATE "+coint.getStag()+CointTable.download+" SET confirm = true WHERE id = ? and confirm = 0", 1, new Object[]{db.getId()}));
		
		FinanUseTypeDao useTypeDao = new FinanUseTypeDao();
		FinanUseType usetype = useTypeDao.getByType(2);
		int useTypeId = usetype.getId();
		
		FinanAccount fa = (FinanAccount) new FinanAccountDao().get("SELECT * FROM finanaccount WHERE type = 3 AND fundType = ? AND isDefault = true", new Object[]{coint.getFundsType()}, FinanAccount.class);
		sqls.addAll(new FinanEntryDao().saveOneEntry(fa.getId(), useTypeId, coint.getFundsType(), db.getAmount(), BigDecimal.ZERO.subtract(db.getRealFee()), Integer.parseInt(db.getUserId()), db.getUserName(), "提币成功", "127.0.0.1", db.getId(), 0, false));
	} 
	
	/**
	 * 获取财务管理员和财务人员和超级管理员
	 * @return
	 */
	public List<AdminUser> getAdmins(){
		AdminUserDao auDao = new AdminUserDao();
		Integer ids[] = new Integer[]{1, 3, 6};
		List<AdminUser> admins = auDao.find(auDao.getQuery().filter("admRoleId in", ids)).asList();
		
		return admins;
	}
}
