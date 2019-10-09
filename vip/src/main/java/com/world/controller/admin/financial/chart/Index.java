/*package com.world.controller.admin.financial.chart;
import java.sql.Timestamp;
import java.util.List;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.financial.dao.FinanChartDao;
import com.world.model.financial.entity.FinanChart;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/chart/", des = "账户快照")
public class Index extends AdminAction {
	
	AdminUserDao auDao = new AdminUserDao();
	FinanChartDao fcDao = new FinanChartDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		int fundType = intParam("fundType");
		int accountId = intParam("accountId");
		int system = intParam("system");//是否是系统创建，默认显示管理员
		Timestamp startTime = dateParam("startDate");

		Query query = fcDao.getQuery();
		query.setSql("select * from financhart");
		query.setCls(FinanChart.class);
		int pageSize = 20;
		
		// 将参数保存为attribute
		try {
			if(accountId > 0){
				query.append(" AND id = "+accountId);
			}
			
			if(fundType == 0)
				fundType = 1;
			
			if(fundType > 0){
				query.append(" AND fundType = "+fundType);
			}
			
			if(system == 0){
				query.append(" AND adminId > 0");
			}else{
				query.append(" AND adminId = 0");
			}
			
			if(startTime!=null){
				query.append(" and createTime>=cast('"+TimeUtil.getTodayFirst(startTime)+"' as datetime)");
				query.append(" and createTime< cast('"+TimeUtil.getTodayLast(startTime)+"' as datetime)");
			}
			
			if(startTime == null){
				query.append(" and createTime>='"+TimeUtil.getTodayFirst()+"'");
			}
			
			setAttr("fundType", fundType);
			
			query.append(" GROUP BY groupTime ORDER BY createTime DESC");

			String countSql = "select count(*) from ("+query.getSql()+") temp";
			@SuppressWarnings("rawtypes")
			List l2 = (List) Data.GetOne(countSql , query.getParams());
			long total = l2 != null ? Integer.parseInt(l2.get(0).toString()) : 0;
			if (total > 0) {
				List<Bean> dataList = fcDao.findPage(pageNo, pageSize);
				
				fcDao.setProperties(dataList);
				
				setAttr("dataList", dataList);
			}
			setAttr("itemCount", total);
			setPaging((int) total, pageNo, pageSize);
			
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	@Page(Viewer = "/admins/financial/view/see.jsp")
	public void see() {
		try {
			long groupTime = longParam("groupTime");
			int fundType = intParam("fundType");
			
			List<Bean> charts = (List<Bean>)Data.Query("SELECT * FROM financhart WHERE groupTime = ? AND fundType = ?", new Object[]{groupTime, fundType}, FinanChart.class);
			
			fcDao.setProperties(charts);
			
			setAttr("fcDao", fcDao);
			setAttr("charts", charts);
			setAttr("fundType", fundType);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
}

*/