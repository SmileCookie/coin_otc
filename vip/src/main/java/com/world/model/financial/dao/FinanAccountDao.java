package com.world.model.financial.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanBalance;

public class FinanAccountDao extends DataDaoSupport{
	
	public FinanAccount get(int id){
		return (FinanAccount)super.get("SELECT * FROM finanaccount WHERE id = ? AND isDel = ?", new Object[]{id, false}, FinanAccount.class);
	}

	public int delById(int id){
		return super.delete("UPDATE finanaccount SET isDel = ? WHERE id = ?", new Object[]{true, id});
	}
	
	public List<Bean> findList(int adminId){
		return findList(adminId, 0);
	}
	
	public List<Bean> findList(int adminId, int fundType){
		return findList(adminId, fundType, 0);
	}

	/**
	 * 查询充值账户
	 * @param bankAccountId>0  充值账户
	 * @return
	 */
	public List<Bean> findListByType(int bankAccountId){
		return findList(0, DatabasesUtil.coinProps("btc").getFundsType(), 1);
	}

	/**
	 * 根据币种和类型获取钱包余额
	 * @param fundType
	 * @param type
     * @return
     */
	public FinanAccount findBeanByFundTypeAndType(int fundType,int type){
		FinanAccount finanAccount = new FinanAccount();
		String sql = "select * from finanAccount where fundType = ? and type = ? ";
		List<FinanAccount> list = super.find(sql, new Object[]{fundType, type}, FinanAccount.class);
		if(null != list && list.size() > 0){
			finanAccount = list.get(0);
		}
		return finanAccount;
	}
	/**
	 * 查询充值账户
	 * @param adminId
	 * @param fundType
	 * @param bankAccountId   查询充值账户
	 * @return
	 */
	public List<Bean> findList(int adminId, int fundType, int bankAccountId){
		String sql = "";
		if(adminId > 0){
			sql += " AND (adminId = "+adminId + ")";
		}
		if(fundType > 0){
			sql += " AND fundType = "+fundType;
		}
		if(bankAccountId > 0){
			sql += " AND bankAccountId > 0";
		}
		
		return (List<Bean>)super.find("SELECT * FROM finanaccount WHERE isDel = ? "+sql+" ORDER BY createTime", new Object[]{false}, FinanAccount.class);
	}
	
	public int save(FinanAccount account){
		
		int count = super.save("INSERT INTO finanaccount (name, memo, fundType, amount, adminId, bankAccountId, rate, createId, createTime,isDel,type,img) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{
				account.getName(), account.getMemo(), account.getFundType(), account.getAmount(), account.getAdminId(), account.getBankAccountId(), account.getRate(),
				account.getCreateId(), account.getCreateTime(), 0, account.getType(),account.getImg()
		});
		return count;
	}
	
	public int update(FinanAccount account){
		
		int count = super.update("UPDATE finanaccount SET name=?, memo=?, fundType=?, amount=?, adminId=?, bankAccountId=?, rate=?, updateId=?, updateTime=?, type=?, img=? WHERE id=?", new Object[]{
				account.getName(), account.getMemo(), account.getFundType(), account.getAmount(), account.getAdminId(), account.getBankAccountId(), account.getRate(), account.getUpdateId(), account.getUpdateTime()
				,account.getType(), account.getImg() ,account.getId()
		});
		return count;
	}
	
	/**
	 * 账务录入更新余额
	 * @param id
	 * @param funds
	 * @param fundsComm
	 * @param fundType
	 * @param isAdd
	 * @return
	 */
	public OneSql updateMoney(int id, BigDecimal funds, BigDecimal fundsComm, int fundType, boolean isAdd){
		
		String updateSql = "";
		if(!isAdd){
			funds = BigDecimal.ZERO.subtract(funds.abs());
		}
		//支出 money = -10,手续费 -20代表支出20手续费，则总支出为30元
		//支出 money = -10,手续费 +20代表收入20手续费，则实际为收入10元
		funds = funds.add(fundsComm);
		if(funds.compareTo(BigDecimal.ZERO) > 0){
			updateSql = "amount=amount+"+funds;
		}else{
			updateSql = "amount=amount-"+funds.abs();
		}
		
		return new OneSql("UPDATE finanaccount SET "+updateSql +" WHERE id = ? AND isDel = ?", 1, new Object[]{id, false});
	}
	
	/**
	 * 上下班结算更新字段
	 * @param id
	 * @param funds
	 * @param fundsComm
	 * @param fundType
	 * @param isAdd
	 * @return
	 */
	public OneSql updateDayTag(int id, long dayTag){
		
		return new OneSql("UPDATE finanaccount SET dayTag=? WHERE id = ? AND isDel = ?", 1, new Object[]{dayTag, id, false});
	}

	/**
	 * 获取各币种的账号余额
	 * @param type
	 * @return
     */
	public Map<Integer, BigDecimal> getBalanceMap(int type){
		Map<Integer , BigDecimal> maps = new LinkedHashMap<Integer, BigDecimal>();
		List<Bean> beans = (List<Bean>)super.find("SELECT * FROM finanaccount WHERE type = ?", new Object[]{type}, FinanAccount.class);
		if(beans != null && beans.size() > 0){
			for(Bean b : beans){
				FinanAccount a = (FinanAccount)b;
				maps.put(a.getFundType(), a.getAmount());
			}
		}
		return maps;
	}

	
	public Map<Integer , FinanAccount> getAccountMapByIds(String ids){
		Map<Integer , FinanAccount> maps = new LinkedHashMap<Integer, FinanAccount>();
		List<Bean> beans = (List<Bean>)super.find("SELECT * FROM finanaccount WHERE id IN ("+ids+")", new Object[]{}, FinanAccount.class);
		if(beans != null && beans.size() > 0){
			for(Bean b : beans){
				FinanAccount a = (FinanAccount)b;
				maps.put(a.getId(), a);
			}
		}
		return maps;
	}
	
	public void setaUser(List<Bean> dataList){
		List<String> adminIds = new ArrayList<String>();
		for(Bean b : dataList){
			FinanAccount fa = (FinanAccount)b;
			if(fa.getAdminId() > 0)
				adminIds.add(fa.getAdminId()+"");
		}
		
		if(adminIds.size() > 0){
			Map<String , AdminUser> users = new AdminUserDao().getUserMapByIds(adminIds);
			for(Bean b : dataList){
				FinanAccount fa = (FinanAccount)b;
				fa.setaUser(users.get(fa.getAdminId()+""));
			}
		}
	}
	
	public boolean needBalance(int adminId){
		boolean alert = false;
		List<Bean> accounts = findList(adminId, 1);//管理员的账户
		if(accounts != null && accounts.size() > 0){
			long dayTag0 = Long.parseLong(FinanBalance.sdf0.format(now()));
			long dayTag24 = Long.parseLong(FinanBalance.sdf24.format(now()));
			for(Bean b : accounts){
				FinanAccount fa = (FinanAccount)b;
				if(fa.getDayTag()<dayTag0 || fa.getDayTag()==dayTag24){
					alert = true;
					break;
				}
			}
		}
		return alert;
	}
	
	public JSONObject listToObject(List<Bean> dataList, int type, int fundType){
		
		JSONArray array = new JSONArray();
		
		BigDecimal confirmBalance = BigDecimal.ZERO;
		BigDecimal errorBalance = BigDecimal.ZERO; 
		BigDecimal totalBalance = BigDecimal.ZERO;
		
		for(Bean b : dataList){
			FinanAccount fa = (FinanAccount)b;
			if(fa.getType() == type && fa.getFundType() == fundType){//充值账户
				array.add(fa);
				
				confirmBalance = confirmBalance.add(fa.getFunds()); 
				errorBalance = errorBalance.add(fa.getExceptAmount());
			}
		}
		totalBalance = confirmBalance.add(errorBalance);
		
		JSONObject object = new JSONObject();
		object.put("key", type);
		object.put("array", array);
		object.put("confirmBalance", confirmBalance);
		object.put("errorBalance", errorBalance);
		object.put("totalBalance", totalBalance);
		
		return object;
	}
	
	/**
	 * 根据账户资金类型统计出账户的总余额
	 * @param dataList
	 * @param fundType
	 * @return
	 */
	public JSONObject listToObject(List<Bean> dataList, int fundType){
		
		BigDecimal confirmBalance = BigDecimal.ZERO;
		BigDecimal errorBalance = BigDecimal.ZERO; 
		BigDecimal totalBalance = BigDecimal.ZERO;
		
		for(Bean b : dataList){
			FinanAccount fa = (FinanAccount)b;
			if(fa.getFundType() == fundType){//充值账户
				confirmBalance = confirmBalance.add(fa.getFunds()); 
				errorBalance = errorBalance.add(fa.getExceptAmount());
			}
		}
		totalBalance = confirmBalance.add(errorBalance);
		
		JSONObject object = new JSONObject();
		object.put("confirmBalance", confirmBalance);
		object.put("errorBalance", errorBalance);
		object.put("totalBalance", totalBalance);
		
		return object;
	}

}
