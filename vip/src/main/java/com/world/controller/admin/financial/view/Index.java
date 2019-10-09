package com.world.controller.admin.financial.view;
import java.sql.Timestamp;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.admin.AdminUser;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.dao.FinanChartDao;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanBalance;
import com.world.model.financial.entity.FinanChart;
import com.world.util.DigitalUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/view/", des = "账户视图")
public class Index extends AdminAction {
	
	FinanAccountDao dao = new FinanAccountDao();
	AdminUserDao auDao = new AdminUserDao();
	FinanChartDao fcDao = new FinanChartDao();
	
	@Page(Viewer = "/admins/financial/view/view.jsp")
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		int fundType = intParam("fundType");
		int accountId = intParam("accountId");
		int adminId = intParam("adminId");

		Query<FinanAccount> query = dao.getQuery();
		query.setSql("select * from finanaccount");
		query.setCls(FinanAccount.class);
		int pageSize = 100;
		
		// 将参数保存为attribute
		try {
			if(accountId > 0){
				query.append(" AND id = "+accountId);
			}
			
			if(adminId > 0){
				query.append(" AND adminId = "+adminId);
			}
			
			if(fundType == 0)
				fundType = 2;
			
			setAttr("fundType", fundType);
			
			if(fundType > 0){
				query.append(" AND fundType = "+fundType);
			}
			setAttr("fundType", fundType);
			setAttr("coint", DatabasesUtil.coinProps(fundType));
			
			int roleId = roleId();
			if(roleId != 1 && roleId != 6){
//				query.append(" AND adminId = "+adminId());
			}
			query.append(" AND isDel = 0");
			query.append(" ORDER BY FundType, type, createTime");

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
				
				JSONObject chargeObj = dao.listToObject(dataList, 1, fundType);
				JSONObject withdrawObj = dao.listToObject(dataList, 3, fundType);
				JSONObject saveObj = dao.listToObject(dataList, 2, fundType);
				JSONObject dailyObj = dao.listToObject(dataList, 4, fundType);
				
				JSONArray array = new JSONArray();
				array.add(chargeObj);
				array.add(withdrawObj);
				array.add(dailyObj);
				array.add(saveObj);
				
				double confirmBalance = DigitalUtil.add(DigitalUtil.add(chargeObj.getDouble("confirmBalance"), saveObj.getDouble("confirmBalance")), withdrawObj.getDouble("confirmBalance"));
				confirmBalance = DigitalUtil.add(confirmBalance, dailyObj.getDouble("confirmBalance"));
				double errorBalance = DigitalUtil.add(DigitalUtil.add(chargeObj.getDouble("errorBalance"), saveObj.getDouble("errorBalance")), withdrawObj.getDouble("errorBalance"));
				errorBalance = DigitalUtil.add(errorBalance, dailyObj.getDouble("errorBalance"));
				
				double totalBalance = DigitalUtil.add(confirmBalance, errorBalance);
				
				setAttr("confirmBalance", confirmBalance);
				setAttr("errorBalance", errorBalance);
				setAttr("totalBalance", totalBalance);
				
				setAttr("array", array);
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
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	@Page(Viewer = ".xml")
	public void saveChart() {
		try {
			int fundType = intParam("fundType");
			if (fcDao.saveChart(adminId(), fundType)) {
				WriteRight("操作成功");
			}else{
				WriteError("操作失败");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer = "/admins/financial/view/see.jsp")
	public void seeLast() {
		try {
			int fundType = intParam("fundType");
			List<Bean> charts = (List<Bean>)Data.Query("SELECT * FROM financhart WHERE groupTime = (SELECT MAX(groupTime) FROM financhart) AND adminId = ? AND fundType = ?", new Object[]{adminId(), fundType}, FinanChart.class);
			
			fcDao.setProperties(charts);
			
			setAttr("fcDao", fcDao);
			setAttr("charts", charts);
			setAttr("fundType", fundType);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
}

