package com.world.model.financial.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanChart;
import com.world.model.financial.entity.FinanEntry;

public class FinanChartDao extends DataDaoSupport{
	FinanAccountDao aDao = new FinanAccountDao();
	FinanEntryDao eDao = new FinanEntryDao();
	
	public OneSql saveSql(int accountId, String memo, int fundType, BigDecimal amount, BigDecimal exceptAmount, Timestamp createTime, long groupTime){
		return saveSql(accountId, memo, fundType, amount, exceptAmount, createTime, groupTime, 0);
	}
	
	public OneSql saveSql(int accountId, String memo, int fundType, BigDecimal amount, BigDecimal exceptAmount, Timestamp createTime, long groupTime, int adminId){
		return new OneSql("INSERT INTO FinanChart (accountId, memo, fundType, amount, exceptAmount, createTime, groupTime, adminId) VALUES (?,?,?,?,?,?,?,?)", 1, new Object[]{
				accountId, memo, fundType, amount, exceptAmount, createTime, groupTime, adminId
		});
	}
	
	public boolean saveChartByHour(Timestamp time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		long groupTime = Long.parseLong(sdf.format(time));
		
		FinanChart chart = (FinanChart) super.get("SELECT * FROM financhart ORDER By groupTime DESC", new Object[]{}, FinanChart.class);
		if(chart != null && chart.getGroupTime() >= groupTime){//还没过一小时
			return false;
		}
		
		List<Bean> accounts = aDao.find("SELECT * FROM finanaccount WHERE type > 0 ORDER BY FundType, type", new Object[]{}, FinanAccount.class);
		if(accounts != null && accounts.size() > 0){
			String ids = "";
			for(Bean b : accounts){
				FinanAccount a = (FinanAccount)b;
				ids += "," + a.getId();
			}
			if(ids.length() > 1){
				ids = ids.substring(1);
			}

			List<OneSql> sqls = new ArrayList<OneSql>();
			Map<Integer, FinanEntry> maps = eDao.getEntryMapByIds(ids);
			String memo = "";
			
			for(Bean b : accounts){
				FinanAccount a = (FinanAccount)b;
				FinanEntry entry = maps.get(a.getId());
				if(entry != null){
					memo = "最后记录财务编号："+entry.getId();
				}else{
					memo = "没有账务记录";
				}
				sqls.add(saveSql(a.getId(), memo, a.getFundType(), a.getAmount(), a.getExceptAmount(), time, groupTime));
			}
			
			return Data.doTrans(sqls);
		}
		
		return false;
	}
	
	public boolean saveChart(int adminId, int fundType){
		Timestamp time = now();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		long groupTime = Long.parseLong(sdf.format(time));
		
		List<Bean> accounts = aDao.find("SELECT * FROM finanaccount WHERE type > 0 AND fundType = ? ORDER BY FundType, type", new Object[]{fundType}, FinanAccount.class);
		if(accounts != null && accounts.size() > 0){
			String ids = "";
			for(Bean b : accounts){
				FinanAccount a = (FinanAccount)b;
				ids += "," + a.getId();
			}
			if(ids.length() > 1){
				ids = ids.substring(1);
			}

			List<OneSql> sqls = new ArrayList<OneSql>();
			Map<Integer, FinanEntry> maps = eDao.getEntryMapByIds(ids);
			String memo = "";
			
			for(Bean b : accounts){
				FinanAccount a = (FinanAccount)b;
				FinanEntry entry = maps.get(a.getId());
				if(entry != null){
					memo = "最后记录财务编号："+entry.getId();
				}else{
					memo = "没有账务记录";
				}
				sqls.add(saveSql(a.getId(), memo, a.getFundType(), a.getAmount(), a.getExceptAmount(), time, groupTime, adminId));
			}
			
			return Data.doTrans(sqls);
		}
		
		return false;
	}
	
	private BigDecimal confirmBalance;//已确认的总金额
	private BigDecimal errorBalance;//有异常的金额
	private BigDecimal totalBalance;//全部金额
	
	public void setProperties(List<Bean> dataList){
		
		if(dataList != null && dataList.size() > 0){
			
			List<String> adminIds = new ArrayList<String>();
			String accountIds = "";
			
			for(Bean b : dataList){
				FinanChart fc = (FinanChart)b;
				adminIds.add(fc.getAdminId()+"");
				accountIds += ","+fc.getAccountId();
			}
			
			Map<String , AdminUser> users = new AdminUserDao().getUserMapByIds(adminIds);
			for(Bean b : dataList){
				FinanChart fc = (FinanChart)b;
				fc.setaUser(users.get(fc.getAdminId()+""));
			}
			
			if(accountIds.length() > 1)
				accountIds = accountIds.substring(1);
			
			Map<Integer , FinanAccount> accounts = new FinanAccountDao().getAccountMapByIds(accountIds);
			for(Bean b : dataList){
				FinanChart fc = (FinanChart)b;
				fc.setAccount(accounts.get(fc.getAccountId()));
			}
			
			Iterator<Integer> it = accounts.keySet().iterator();
			while (it.hasNext()) {
				FinanAccount account = accounts.get(it.next());
				confirmBalance = confirmBalance.add(account.getFunds());
				errorBalance = errorBalance.add(account.getExceptAmount());
				totalBalance = errorBalance.add(confirmBalance);
			}
		}
	}

	public BigDecimal getConfirmBalance() {
		return confirmBalance;
	}

	public void setConfirmBalance(BigDecimal confirmBalance) {
		this.confirmBalance = confirmBalance;
	}

	public BigDecimal getErrorBalance() {
		return errorBalance;
	}

	public void setErrorBalance(BigDecimal errorBalance) {
		this.errorBalance = errorBalance;
	}

	public BigDecimal getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}

	
}
