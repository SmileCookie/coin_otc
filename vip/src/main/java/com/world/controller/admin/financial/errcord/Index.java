/*package com.world.controller.admin.financial.errcord;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.dao.FinanErrorDao;
import com.world.model.financial.dao.FinanUseTypeDao;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanError;
import com.world.model.financial.entity.FinanUseType;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/error/", des = "异常记录")
public class Index extends FinanAction {
	
	FinanErrorDao dao = new FinanErrorDao();
	FinanAccountDao aDao = new FinanAccountDao();
	FinanUseTypeDao utDao = new FinanUseTypeDao();
	
	AdminUserDao auDao = new AdminUserDao();
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		int accountId = intParam("accountId");
		int useTypeId = intParam("useTypeId");
		String name = param("userName");
		String memo = param("memo");
		Timestamp startTime = dateParam("startDate");	
		Timestamp endTime=dateParam("endDate");	
		String status = param("status");

		Query query = dao.getQuery();
		query.setSql("select * from FinanError");
		query.setCls(FinanError.class);
		int pageSize = 20;

		// 将参数保存为attribute
		try {
			if(accountId > 0){
				query.append(" AND accountId = "+accountId);
			}			
			if(useTypeId > 0){
				query.append(" AND useTypeId = "+useTypeId);
			}
			if(name.length() > 0){
				query.append(" AND userName LIKE '%"+name+"%'");
			}
			if(memo.length() > 0){
				query.append(" AND memo LIKE '%"+memo+"%'");
			}
			
			if(startTime!=null){
				query.append(" and createTime>=cast('"+startTime+"' as datetime)");
			}
			if(endTime!=null){
				query.append(" and createTime<=cast('"+endTime+"' as datetime)");
			}
			
			if(status.length() > 0){
				query.append(" AND status = "+Integer.parseInt(status));
			}

			query.append(" AND isDel = 0");
			query.append("ORDER BY createTime DESC, id DESC");

			long total = dao.count();
			if (total > 0) {
				List<Bean> dataList = dao.findPage(pageNo, pageSize);
				
				dao.setProperties(dataList);
				
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
			
			setAttr("accounts", aDao.findListByType(1));
		} catch (Exception ex) {
			log.error(ex.toString());
			log.error(ex.toString(), ex);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	@Page(Viewer = "/admins/financial/error/deal.jsp")
	public void deal() {
		try {
			int id = intParam("id");

			if(id > 0){
				FinanError error = dao.get(id);
				setAttr("error", error);
				
				FinanAccount account = aDao.get(error.getAccountId());
				setAttr("account", account);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	
	
	@Page(Viewer = ".xml")
	public void cancel() {
		try {
			int id = intParam("id");
			
			FinanError error = dao.get(id);
			
			if(error.getStatus() == 1){
				WriteError("该记录已经处理过。");
				return;
			}
			
			List<OneSql> sqls = new ArrayList<OneSql>();
			sqls.add(new OneSql("UPDATE FinanError SET status=?, updateId=?, updateTime=? WHERE id=?", 1, new Object[]{2, adminId(), now(), id}));
			sqls.add(new OneSql("update finanaccount set exceptMoney = exceptMoney-"+error.getMoney() + " where id = ? AND exceptMoney >= "+error.getMoney(), 1, new Object[]{error.getAccountId()}));
			
			if(Data.doTrans(sqls)){
				WriteRight("操作成功");
			}else{
				WriteError("操作失败");
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			int id = intParam("id");

			if(id > 0){
				FinanError error = dao.get(id);
				setAttr("error", error);
			}
			
			setAttr("accounts", aDao.findListByType(1));
			setAttr("useTypes", utDao.findList());
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doAoru() {
		try {
			saveError();
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	private void saveError(){
		int accountId = intParam("accountId");
		int useTypeId = intParam("useTypeId");
		
		FinanUseType useType = utDao.get(useTypeId);
		if(useType == null){
			WriteError("用途不存在。");
			return;
		}
		
		double funds = doubleParam("funds");
		double fundsComm = doubleParam("fundsComm");

		int fundType = intParam("fundType");
		
		FinanAccount account = aDao.get(accountId);
		if(account.getFundType() != fundType){
			WriteError("该账户的资金类型与您输入的资金类型不符。");
			return;
		}
		
		String memo = param("memo");
		
		FinanError error = new FinanError(accountId, useTypeId, fundType, funds, fundsComm, memo, ip());
		Timestamp now = now();
		
		error.setCreateId(adminId());
		error.setCreateTime(now);
		
		int count = dao.save(error);
		if (count > 0) {
			Data.Update("update finanaccount set exceptMoney = exceptMoney+"+funds + " where id = ?", new Object[]{accountId});
			WriteRight("保存成功");
		}else{
			WriteError("操作失败");
		}
	}
}

*/