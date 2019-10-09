package com.world.controller.manage.account;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.world.data.big.MysqlDownTable;
import com.world.data.mysql.Query;
import com.world.data.mysql.bean.BeanProxy;
import com.world.model.dao.bill.BillDetailDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.bill.BillType;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/cn/manage/account/bill/", des = "账单明细")
public class Bill extends UserAction {

	private static final long serialVersionUID = 1L;
	BillDetailDao bwDao = new BillDetailDao();

	//Close By suxinjie 一期屏蔽该功能
	//@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		initLoginUser();
		//查询条件
		String tab = param("tab");
		int currentPage = intParam("page");
		String memo = param("memo");
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		long entrustId = longParam("entrustId");
		int type = intParam("type");
		int dataType = intParam("datatype");
		
		BillDetailDao bwDao = new BillDetailDao();
		BeanProxy bp = MysqlDownTable.getProxy("bill");
		if(dataType == 1){
			bwDao.setDatabase(bp.tableInfo.targetDatabases()[0]);
		}
		Query<BillDetails> query = bwDao.getQuery();
		query.setSql("select * from bill");
		query.setCls(BillDetails.class);

		
		query.append(" and userId = "+userId());
		
		if(memo.length() > 0){
			query.append(" and remark like '%"+memo+"%'");
		}
		
		if(startTime != null){
			query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
		}
		
		if(endTime != null){
			query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
		}
		
		if(entrustId > 0){
			query.append(" and entrustId = " + entrustId);
		}
		
		if(type > 0){
			query.append(" and type = " + type);
		}
		
		if (!tab.equals("all")) {
			if (tab.equals("upload")) {
				query.append(" and type in (" + BillType.recharge.getKey()+","+BillType.sysRecharge.getKey()+")");
			} else if (tab.equals("download")) {
				query.append(" and type = " + BillType.download.getKey());
			} else if (tab.equals("other")) {
				query.append(" and type > 6 and type <> 14");
			}
		}
		
		int total = query.count();
		if(total > 0){
			query.append("order by id desc");
			//分页查询
			List<BillDetails> weight = bwDao.findPage(currentPage, 15);
			
			request.setAttribute("dataList", weight);
		}
		
		setPaging(total, currentPage , 15);
		
		setAttr("types", EnumUtils.getAll(BillType.class));
		setAttr("curUser", loginUser);
	}

	//Close By suxinjie 一期屏蔽该功能
	//@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}	

	//Close By suxinjie 一期屏蔽该功能
	//@Page(Viewer = "")
	public void exportUser(){
		try {
			List<BillDetails> needUser = getUserList();
			if(needUser == null){
				return;
			}
				
			String [] column = {"sendTime","showType","inout","amount","banlance","remark"};
			String [] tabHead = {"时间","类型","收支","数量","余额","备注"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_bill_details.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public List<BillDetails> getUserList(){
		String tab = param("tab");
		String memo = param("memo");
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		long entrustId = longParam("entrustId");
		int type = intParam("type");
		int dataType = intParam("datatype");
		
		BillDetailDao bwDao = new BillDetailDao();
		Query<BillDetails> query = bwDao.getQuery();
		query.setSql("select * from bill");
		query.setCls(BillDetails.class);
		
		query.append(" and userId = "+userId());
		if(memo.length() > 0){
			query.append(" and remark like '%"+memo+"%'");
		}
		if(startTime != null){
			query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
		}
		if(endTime != null){
			query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
		}
		if(entrustId > 0){
			query.append(" and entrustId = " + entrustId);
		}
		
		if(type > 0){
			query.append(" and type = " + type);
		}
		
		if(tab.length() == 0){
			tab = "all";
		}
		
		if (!tab.equals("all")) {
			if (tab.equals("upload")) {
				query.append(" and type in (" + BillType.recharge.getKey()+","+BillType.sysRecharge.getKey()+")");
			} else if (tab.equals("download")) {
				query.append(" and type = " + BillType.download.getKey());
			} else if (tab.equals("other")) {
				query.append(" and type > 6 and type <> 14");
			}
		}

		int total = query.count();
		if(total > 0){
			query.append("order by id desc");
			List<BillDetails> billDetails = query.getList();
			
			return billDetails;
		}
		return null;
	}
}

