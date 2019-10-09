package com.world.controller.admin.financial.account;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.daily.MainDailyRecordDao;
import com.world.model.dao.wallet.WalletDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.pay.WalletBean;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanBalance;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/account/", des = "账户管理")
public class Index extends AdminAction {
	
	FinanAccountDao dao = new FinanAccountDao();
	AdminUserDao auDao = new AdminUserDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		int accountId = intParam("accountId");
		int fundType = intParam("fundType");
		int adminId = intParam("adminId");

		Query query = dao.getQuery();
		query.setSql("select * from finanaccount");
		query.setCls(FinanAccount.class);
		int pageSize = 20;
		
		// 将参数保存为attribute
		try {
			if(accountId > 0){
				query.append(" AND id = "+accountId);
			}
			int roleId = roleId();
			if(roleId != 1 && roleId != 6){
//				query.append(" AND adminId = "+adminId());
			}
			if(fundType > 0){
				query.append(" AND fundType = "+fundType);
			}
			if(adminId > 0){
				query.append(" AND adminId = "+adminId);
			}
			query.append(" AND isDel = 0");
			query.append("ORDER BY fundType, id");

			boolean alert = false;
			Timestamp now = now();
			long dayTag0 = Long.parseLong(FinanBalance.sdf0.format(now));
			long dayTag24 = Long.parseLong(FinanBalance.sdf24.format(now));
			
			long total = query.count();
			if (total > 0) {
				List<Bean> dataList = dao.findPage(pageNo, pageSize);
				dao.setaUser(dataList);

				for(Bean b : dataList){
					FinanAccount fa = (FinanAccount)b;
					if(roleId() != 1 && roleId() != 6){
						if(fa.getDayTag()<dayTag0 || fa.getDayTag()==dayTag24){
							alert = true;
							break;
						}
					}
				}
				
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setAttr("alert", alert);
			setPaging((int) total, pageNo, pageSize);
			
			setAttr("accounts", dao.findList(roleId==1||roleId==6?0:adminId()));
			com.google.code.morphia.query.Query<AdminUser> q = auDao.getQuery();
			q.or(
					q.criteria("admRoleId").equal(1),
					q.criteria("admRoleId").equal(6)
				);
			setAttr("admins", auDao.find(q).asList());
			setAttr("ft", DatabasesUtil.getCoinPropMaps());
			
			setAttr("dayTag0", dayTag0);
			setAttr("dayTag24", dayTag24);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			int id = intParam("id");

			if(id > 0){
				FinanAccount fa = dao.get(id);
				setAttr("fa", fa);
			}
			setAttr("admins", auDao.find().asList());
			
			setAttr("ft", DatabasesUtil.getCoinPropMaps());
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doAoru() {
		try {
			int id = intParam("id");
			
			String name = param("name");
			String memo = param("memo");
			
			int adminId = intParam("adminId");
			BigDecimal funds = decimalParam("funds");
			int fundType = intParam("fundType");
			int bankAccountId = intParam("bankAccountId");
			BigDecimal rate = decimalParam("rate");
			int type = intParam("type");
			String img = param("img");

			FinanAccount account = new FinanAccount(name, memo, fundType, funds, adminId, bankAccountId, rate, type, img);
			JSONObject beforeUpdate = new JSONObject();
			JSONObject afterUpdate = new JSONObject();
			afterUpdate.put("name", name);
			afterUpdate.put("fundsType", fundType);
			afterUpdate.put("funds", funds);
			afterUpdate.put("bankAccountId", bankAccountId);
			afterUpdate.put("type", type);
			int res = 0;
			if (id > 0) {
				FinanAccount old = dao.get(id);
				beforeUpdate.put("name", old.getName());
				beforeUpdate.put("fundsType", old.getFundType());
				beforeUpdate.put("funds", old.getFunds());
				beforeUpdate.put("bankAccountId", bankAccountId);
				beforeUpdate.put("type", type);
				
				account.setId(id);
				account.setUpdateId(adminId());
				account.setUpdateTime(now());
				
				res = dao.update(account);
			} else {
				
				account.setCreateId(adminId());
				account.setCreateTime(now());
				
				res = dao.save(account);
			}
			if (res > 0) {
				 //插入一条管理员日志信息
	            DailyType dType = DailyType.adminOperate;
	            String des = id==0?"添加财务账户："+afterUpdate.toJSONString():"修改后的财务账户："+afterUpdate.toJSONString();
	            if(beforeUpdate.size() > 0){
	            	des += ",修改前的参数为："+beforeUpdate.toJSONString();
	            }
	            new MainDailyRecordDao().insertOneRecord(dType, des, String.valueOf(adminId()), ip(), now());
				
				WriteRight("操作成功");
			}else{
				WriteError("操作失败");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = XML)
	public void doDel() {
		int id = intParam("id");
		if (id > 0) {
			dao.delById(id);
			
			Write("删除成功。", true, "");
			return;
		}
		Write("未知错误导致删除失败。", false, "");
	}
	
	@Page(Viewer = ".xml")
	public void setDefault() {
		int id = intParam("id");
		int adminId = adminId();
		
		FinanAccount fa = dao.get(id);
		int fundType = fa.getFundType();
		
		boolean setStat = booleanParam("setStat");

		if (id > 0) {
			if (setStat) {
				String up2 = "update finanaccount set isDefault=0 where adminId=" + adminId + " and isDefault=1 and fundType="+fundType + " AND type = "+fa.getType();
				String up1 = "update finanaccount set isDefault=1 where id=" + id;
				if (!Data.DoTrans(new String[] { up2, up1 })) {
					WriteError(L("操作失败"));
				} else {
					WriteRight(L("操作成功"));
				}
			} else {
				String up1 = "update finanaccount set isDefault=0 where id=" + id;
				if (Data.Update(up1, new Object[] {}) > 0) {
					WriteRight(L("操作成功"));
				} else {
					WriteError(L("操作失败"));
				}
			}
		} else {
			WriteError(L("操作失败"));
		}
	}
	


	/**
	 * 添加比特币账户时自动查询出关联的比特币钱包（根据类型判断是充值还是提现）
	 */
	@Page(Viewer = JSON)
	public void getBtcWallet() {
		int withdraw = intParam("withdraw");
		WalletDao walletDao = new WalletDao();
		walletDao.setCoint(coint);
		List<Bean> wallets = (List<Bean>)Data.Query("SELECT * FROM "+walletDao.getTableName()+" WHERE withdraw = ?", new Object[]{withdraw}, WalletBean.class);
		JSONArray array = JSONArray.fromObject(wallets);
		json("", true, array.toString());
	}

}

