package com.world.controller.admin.financial.entry;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.wallet.WalletDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.dao.FinanEntryDao;
import com.world.model.financial.dao.FinanUseTypeDao;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanEntry;
import com.world.model.financial.entity.FinanUseType;
import com.world.util.date.TimeUtil;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/entry/", des = "账务录入")
public class Index extends FinanAction {
	
	FinanEntryDao dao = new FinanEntryDao();
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
		String userId = param("userId");
		String memo = param("memo");
		int dateTime = intParam("dateTime");
		Timestamp startTime = dateParam("startDate");	
		Timestamp endTime=dateParam("endDate");	
		int orderId = intParam("orderId");
		int eid = intParam("eid");
		int adminId = intParam("adminId");

		Query query = dao.getQuery();
		query.setSql("select * from finanentry");
		query.setCls(FinanEntry.class);
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
			//Start by chendi 用户id置换用户名称
			if(userId.length() > 0){
				query.append(" AND userId = '"+userId+"'");
			}
			//end
			if(memo.length() > 0){
				query.append(" AND memo LIKE '%"+memo+"%'");
			}
			
			if (dateTime > 0) {
				if (dateTime == 1) {// 当天
					query.append(" createTime>='"+TimeUtil.getTodayFirst()+"'");
				} else if (dateTime == 3) {// 近三天
					query.append(" AND createTime>= '" + TimeUtil.getBeforeTime(-2) + "'");
				} else if (dateTime == 7) {// 本周
					query.append(" AND createTime>= cast('" + TimeUtil.getMondayOFWeek() + "' as datetime)");
				} else if (dateTime == 30) {// 本月
					query.append(" AND createTime>= cast('" + TimeUtil.getFirstDayOfMonth() + "' as datetime)");
				} /*else if (dateTime == 60) {// 上个月
					query.append(" AND createTime>= cast('" + TimeUtil.getLastMonthFirstDay() + "' as datetime)");
					query.append(" AND createTime<= cast('" + TimeUtil.getLastMonthLastDay() + "' as datetime)");
				} */else if (dateTime == 5) {// 任意时间
					if(startTime!=null){
						query.append(" and createTime>=cast('"+startTime+"' as datetime)");
					}
					if(endTime!=null){
						query.append(" and createTime<=cast('"+endTime+"' as datetime)");
					}
				}
			}
			
			if(eid > 0){
				if(orderId == 0){
					query.append(" and id = "+eid);
				}else if(orderId > 0){
					query.append(" and id >= "+eid);
				}else if(orderId < 0){
					query.append(" and id <= "+eid);
				}
			}

			int roleId = roleId();
//			if(roleId != 1 && roleId != 6){
//				query.append(" AND createId = "+adminId());
//			}else 
			if(adminId > 0){
				query.append(" AND createId = "+adminId);
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
			
			setAttr("accounts", aDao.findList(0));
			setAttr("useTypes", utDao.findList());
			setAttr("admins", dao.getAdmins());
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
				FinanEntry entry = dao.get(id);
				setAttr("entry", entry);
			}
			
			long connId = longParam("connId");
			int useTypeId = intParam("useTypeId");
			setAttr("connId", connId);
			setAttr("useTypeId", useTypeId);
			
			setAttr("accounts", aDao.findList(0));
			setAttr("useTypes", utDao.findList());
			
			setAttr("ft", DatabasesUtil.getCoinPropMaps());
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doAoru() {
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<OneSql> sqls = saveEntrySqls(); 
			if(sqls == null){
				return;
			}
			
			syncWallet(sqls);
			
			if (Data.doTrans(sqls)) {
				WriteRight("操作成功");
			}else{
				WriteError("财务录入执行失败。");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = JSON)
	public void tongji() {
		try {
			String ids = param("eIds");
			boolean isAll = booleanParam("isAll");
			
			if(ids.endsWith(",")){
				ids = ids.substring(0, ids.length()-1);
			}
			
			String sqls = "SELECT * FROM finanentry WHERE id IN ("+ids+")";
			if(isAll){
				sqls = "SELECT * FROM finanentry";
			}
			List<Bean> list = (List<Bean>)Data.Query(sqls, new Object[]{}, FinanEntry.class);
			
			dao.setProperties(list);
		
			BigDecimal inTotalMoney = BigDecimal.ZERO;
			BigDecimal outTotalMoney = BigDecimal.ZERO;
			for(Bean b : list){
				FinanEntry entry = (FinanEntry)b;
				if(entry.getIsIn().equals("收入")){
					inTotalMoney = inTotalMoney.add(entry.getFunds()).add(entry.getFundsComm());
				}else{
					outTotalMoney = outTotalMoney.add(entry.getFunds().abs()).subtract(entry.getFundsComm());
				}
			}
			JSONObject obj = new JSONObject();
			obj.put("inTotalMoney", String.valueOf(inTotalMoney));
			obj.put("outTotalMoney", String.valueOf(outTotalMoney));
			
			json("", true, obj.toString());
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	@Page(Viewer = JSON)
	public void getAccount() {
		int fundType = intParam("fundType");
		List<Bean> lists = aDao.findList(0, fundType);
		JSONArray array = new JSONArray();
		int roleId = roleId();
		if(lists != null && lists.size() > 0){
			for(Bean b : lists){
				FinanAccount fa = (FinanAccount)b;
				JSONObject obj = new JSONObject();
				obj.put("id", fa.getId());
				obj.put("name", fa.getName());
				if(roleId == 1 || roleId == 6){
					obj.put("money", fa.getFunds());
				}
				array.add(obj);
			}
		}
		json("", true, array.toString());
	}
	
	@Page(Viewer = JSON)
	public void getUseType() {
		int fundType = intParam("fundType");
		List<FinanUseType> lists = new FinanUseTypeDao().findList(fundType);
		JSONArray array = new JSONArray();
		if(lists != null && lists.size() > 0){
			for(FinanUseType fut : lists){
				JSONObject obj = new JSONObject();
				obj.put("id", fut.getId());
				obj.put("name", fut.getName());
				obj.put("turn", fut.getTurnRound());
				array.add(obj);
			}
		}
		json("", true, array.toString());
	}
	
	private void syncWallet(List<OneSql> sqls){
		
		int accountId = intParam("accountId");
		int fundType = intParam("fundType");
		int toAccountId = intParam("toAccountId");
		
		int useTypeId = intParam("useTypeId");
		FinanUseType useType = utDao.get(useTypeId);
		
		BigDecimal funds = decimalParam("funds");
		BigDecimal fundsComm = decimalParam("fundsComm");//手续费
		BigDecimal add = funds.abs();
		BigDecimal realFee = fundsComm.abs();
		
		CoinProps coint = DatabasesUtil.coinProps(fundType);
		
		WalletDao walletDao = new WalletDao();
		walletDao.setCoint(coint);
		//钱包
		FinanAccount fa = new FinanAccountDao().get(accountId);
		int walletId = fa.getBankAccountId();
		if(toAccountId > 0 || funds.compareTo(BigDecimal.ZERO) < 0 || useType.getIsIn() == 2){
			if(walletId > 0){
				sqls.add(new OneSql("update "+walletDao.getTableName()+" set btcs=btcs-?-? where walletId=?" , 1 , new Object[]{add, realFee , walletId}, coint.getDatabasesName()));
			}
		}
		if(toAccountId > 0){
			accountId = toAccountId;
		}
	
		if(toAccountId > 0 || useType.getIsIn() == 1&&funds.compareTo(BigDecimal.ZERO)>0){
			fa = new FinanAccountDao().get(accountId);
			walletId = fa.getBankAccountId();
			if(walletId > 0){
				sqls.add(new OneSql("update "+walletDao.getTableName()+" set btcs=btcs+? where walletId=?" , 1 , new Object[]{add , walletId}, coint.getDatabasesName()));
			}
		}
	}
	
	@Page(Viewer = "")
	public void exportUser(){
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<Bean> needUser = getUserList();
				
			String [] column = {"accountName","isIn","funds","fundsComm","balance","useTypeName","userName","connectionId","memo","createTime"};//{"userName","submitTime","toAddress","amount","showStat"};
			String [] tabHead = {"账户名","收支","金额","手续费","余额","用途","用户名","业务编号","备注","时间"};//{"用户名","提交时间","提现地址","数量","状态"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
			OutputStream out = response.getOutputStream();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_entry_"+sdf.format(now())+".xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public List<Bean> getUserList(){
		int accountId = intParam("accountId");
		int useTypeId = intParam("useTypeId");
		String name = param("userName");
		String memo = param("memo");
		int dateTime = intParam("dateTime");
		Timestamp startTime = dateParam("startDate");	
		Timestamp endTime=dateParam("endDate");	
		int orderId = intParam("orderId");
		int eid = intParam("eid");
		int adminId = intParam("adminId");

		Query query = dao.getQuery();
		query.setSql("select * from finanentry");
		query.setCls(FinanEntry.class);
		
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
		
		if (dateTime > 0) {
			if (dateTime == 1) {// 当天
				query.append(" createTime>='"+TimeUtil.getTodayFirst()+"'");
			} else if (dateTime == 3) {// 近三天
				query.append(" AND createTime>= '" + TimeUtil.getBeforeTime(-2) + "'");
			} else if (dateTime == 7) {// 本周
				query.append(" AND createTime>= cast('" + TimeUtil.getMondayOFWeek() + "' as datetime)");
			} else if (dateTime == 30) {// 本月
				query.append(" AND createTime>= cast('" + TimeUtil.getFirstDayOfMonth() + "' as datetime)");
			} /*else if (dateTime == 60) {// 上个月
				query.append(" AND createTime>= cast('" + TimeUtil.getLastMonthFirstDay() + "' as datetime)");
				query.append(" AND createTime<= cast('" + TimeUtil.getLastMonthLastDay() + "' as datetime)");
			} */else if (dateTime == 5) {// 任意时间
				if(startTime!=null){
					query.append(" and createTime>=cast('"+startTime+"' as datetime)");
				}
				if(endTime!=null){
					query.append(" and createTime<=cast('"+endTime+"' as datetime)");
				}
			}
		}
		
		if(eid > 0){
			if(orderId == 0){
				query.append(" and id = "+eid);
			}else if(orderId > 0){
				query.append(" and id >= "+eid);
			}else if(orderId < 0){
				query.append(" and id <= "+eid);
			}
		}

		int roleId = roleId();
//		if(roleId != 1 && roleId != 6){
//			query.append(" AND createId = "+adminId());
//		}else 
		if(adminId > 0){
			query.append(" AND createId = "+adminId);
		}
		
		query.append(" AND isDel = 0");
		query.append("ORDER BY createTime, id");
		
		int total = query.count();
		if(total > 0){
			List<Bean> dataList = query.getList();
			dao.setProperties(dataList);
			
			for(Bean b : dataList){
				FinanEntry entry = (FinanEntry)b;
				entry.setAccountName(entry.getAccount().getName());
				entry.setUseTypeName(entry.getUseType().getName());
			}
			
			return dataList;
		}
		return null;
	}
}

