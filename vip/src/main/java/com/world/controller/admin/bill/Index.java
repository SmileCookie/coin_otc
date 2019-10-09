package com.world.controller.admin.bill;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.world.util.date.TimeUtil;
import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.dao.bill.BillDetailDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/bill/", des = "账单明细")
public class Index extends UserAction {

	private static final long serialVersionUID = 1L;
	private BillDetailDao bwDao = new BillDetailDao();

	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		//查询条件
		String tab = param("tab");
		String txnId = param("txnId");
		int currentPage = intParam("page");
		String memo = param("memo");
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		int type = intParam("type");
		String userId = param("userId");
//		String userName = param("userName");
		int fundsType = intParam("fundsType");
		String loadFlag = param("loadFlag");
		if(!"1".equals(loadFlag)){
			startTime = TimeUtil.getMouthFirst();
			endTime = TimeUtil.getTodayLast();
		}
		setAttr("types", EnumUtils.getAll(BillType.class));
		setAttr("ft", DatabasesUtil.getCoinPropMaps());
		if(currentPage<1){
			return;
		}

		Query<BillDetails> query = bwDao.getQuery();
		/*start by xzhang 20171018 线上问题-后台管理账单明细查询 JYPT-1674*/
//		query.setSql("select id,userId,userName,type,status,amount,sendTime,remark,balance,fees,adminId,toAddr,fundsType,entrustId,isFinaAccount,ucmId from (select id,userId,userName,type,status,amount,sendTime,remark,balance,fees,adminId,toAddr,fundsType,entrustId,isFinaAccount,ucmId from bill union all select id,userId,userName,type,status,amount,sendTime,remark,balance,fees,adminId,toAddr,fundsType,entrustId,isFinaAccount,ucmId from bill_all) fa ");
		query.setSql("select id,userId,userName,type,status,amount,sendTime,remark,balance,fees,adminId,toAddr,fundsType,entrustId,isFinaAccount,ucmId from bill ");
		/*end*/
		query.setCls(BillDetails.class);
		
		//query.append(" AND fundsType = "+coint.getFundsType());
		
		if(memo.length() > 0){
			query.append(" and remark like '%"+memo+"%'");
		}
		if(userId.length() > 0){
			query.append(" and userId = '"+userId+"'");
		}
//		if(userName.length() > 0){
//			query.append(" and userName like '%"+userName+"%'");
//		}
		
		if(startTime != null){
			query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
		}
		if(endTime != null){
			query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
		}
		
		if(type > 0){
			query.append(" and type = " + type);
		}
		if(fundsType > 0){
			query.append(" and fundsType = " + fundsType);
		}
		if(txnId.length() > 0){
			query.append(" and id = " + txnId);
		}
		if(tab.length() == 0){
			tab = "all";
		}
		
		if (!tab.equals("all")) {
			if (tab.equals("download")) {
				query.append(" and type = " + BillType.download.getKey());
			} else if (tab.equals("charge")) {
				query.append(" and type = " + BillType.recharge.getKey());
			} else if(tab.equals("sysCharge")){
				query.append(" AND  (type=" + BillType.sysRecharge.getKey() + ")");
			} else if(tab.equals("sysDeduct")){
				query.append(" AND  (type=" + BillType.sysDeduct.getKey() + ")");
			}
		}
		
		int total = query.count();
		if(total > 0){
			query.append("order by id desc");
			//分页查询
			List<BillDetails> weight = bwDao.findPage(currentPage, 50);
			
			setAttr("dataList", weight);
		}
		
		setPaging(total, currentPage , 50);
		
		setAttr("curUser", loginUser);
		setAttr("loadFlag","1");
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}	
	@Page(Viewer = "")
	public void exportUser(){
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<BillDetails> needUser = getUserList();
			//20170826 xzhang 修改报表展示数据信息字段JYPT-1250
			String [] column = {"id","userId","showType","coinName","amount","remark","sendTime"};
			String [] tabHead = {"流水号","用户编号","类型","资金类型","数量","备注","时间"};
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
		String txnId = param("txnId");
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		int type = intParam("type");
		String userId = param("userId");
		String userName = param("userName");

		String loadFlag = param("loadFlag");
		if(!"1".equals(loadFlag)){
			startTime = TimeUtil.getMouthFirst();
			endTime = TimeUtil.getTodayLast();
		}
		
		Query<BillDetails> query = bwDao.getQuery();
		/*start by xzhang 20171018 线上问题-后台管理账单明细查询 JYPT-1674*/
//		query.setSql("select * from (select * from bill union all select * from bill_all) fa ");
		query.setSql("select * from bill ");
		/*end*/
		query.setCls(BillDetails.class);

		if(memo.length() > 0){
			query.append(" and remark like '%"+memo+"%'");
		}
		if(userId.length() > 0){
			query.append(" and userId = '"+userId+"'");
		}
		if(userName.length() > 0){
			query.append(" and userName like '%"+userName+"%'");
		}
		
		if(startTime != null){
			query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
		}
		if(endTime != null){
			query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
		}
		
		if(type > 0){
			query.append(" and type = " + type);
		}
		if(txnId.length() > 0){
			query.append(" and id = " + txnId);
		}
		
		if(tab.length() == 0){
			tab = "all";
		}
		
		if (!tab.equals("all")) {
			if (tab.equals("download")) {
				query.append(" and type = " + BillType.download.getKey());
			} else if (tab.equals("charge")) {
				query.append(" and type = " + BillType.recharge.getKey());
			} else if(tab.equals("sysCharge")){
				query.append(" AND  (type=" + BillType.sysRecharge.getKey() + ")");
			} else if(tab.equals("sysDeduct")){
				query.append(" AND  (type=" + BillType.sysDeduct.getKey() + ")");
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
	@Page(Viewer = JSON)
	public void tongji(){
		//查询条件
		String tab = param("tab");
		int currentPage = intParam("page");
		String txnId = param("txnId");
		String memo = param("memo");
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		int type = intParam("type");
		String userId = param("userId");

		String loadFlag = param("loadFlag");
		if(!"1".equals(loadFlag)){
			startTime = TimeUtil.getMouthFirst();
			endTime = TimeUtil.getTodayLast();
		}
		Query<BillDetails> query = bwDao.getQuery();
		query.setSql("select * from bill");
		query.setCls(BillDetails.class);
		
		String ids = param("eIds");
		boolean isAll = booleanParam("isAll");
		
		if(isAll){
		
			if(memo.length() > 0){
				query.append(" and remark like '%"+memo+"%'");
			}
			if(userId.length() > 0){
				query.append(" and userId = '"+userId+"'");
			}

			if(startTime != null){
				query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
			}
			if(endTime != null){
				query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
			}
			
			if(type > 0){
				query.append(" and type = " + type);
			}
			
			if(tab.length() == 0){
				tab = "all";
			}
			if(txnId.length() > 0){
				query.append(" and id = " + txnId);
			}
			
			if (!tab.equals("all")) {
				if (tab.equals("download")) {
					query.append(" and type = " + BillType.download.getKey());
				} else if (tab.equals("charge")) {
					query.append(" and type = " + BillType.recharge.getKey());
				} else if(tab.equals("sysCharge")){
					query.append(" AND  (type=" + BillType.sysRecharge.getKey() + ")");
				} else if(tab.equals("sysDeduct")){
					query.append(" AND  (type=" + BillType.sysDeduct.getKey() + ")");
				}
			}
		}else{
			if(ids.endsWith(",")){
				ids = ids.substring(0, ids.length()-1);
			}
			query.append(" AND id IN ("+ids+")");
		}
		List<BillDetails> list = bwDao.find();
		
		BigDecimal totalMoney = BigDecimal.ZERO;
		for(Bean b : list){
			BillDetails bdb = (BillDetails) b;
			totalMoney = totalMoney.add(bdb.getAmount());
		}
		JSONArray array = new JSONArray();
		array.add(totalMoney);
		setAttr("loadFlag","1");
		json("", true, array.toString());
	}
}

