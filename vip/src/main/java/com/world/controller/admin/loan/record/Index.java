package com.world.controller.admin.loan.record;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.Query;
import com.world.model.loan.dao.LoanRecordDao;
import com.world.model.loan.entity.LoanRecord;
import com.world.model.loan.entity.LoanRecordStatus;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.entity.RepayOfQi;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/loan/record/", des = "借贷记录")
public class Index extends AdminAction {
	LoanRecordDao dao = new LoanRecordDao();

	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		String tab = param("tab");
		int pageNo = intParam("page");
		String inUserId = param("inUserId");
		String inUserName = param("inUserName");
		String outUserId = param("outUserId");
		String outUserName = param("outUserName");
		int isIn = intParam("isIn");
		int status = intParam("status");
		String dir = param("dir");
		if(StringUtils.isBlank(dir)){
			dir = "createTime";
		}
		Query query = dao.getQuery();
		query.setSql("select * from loanrecord");
		query.setCls(LoanRecord.class);
		int pageSize = 20;

		boolean search = false;
		// 将参数保存为attribute
		try {
			if (inUserId.length() > 0) {
				search = true;
				query.append(" AND inUserId = '" + inUserId + "'");
			}
			if (inUserName.length() > 0) {
				search = true;
				query.append(" AND inUserName like '%" + inUserName + "%'");
			}
			if (outUserId.length() > 0) {
				search = true;
				query.append(" AND outUserId = '" + outUserId + "'");
			}
			if (outUserName.length() > 0) {
				search = true;
				query.append(" AND outUserName like '%" + outUserName + "%'");
			}
			if (isIn == 1) {
				query.append(" AND isIn = 1");
			} else if(isIn == 2){
				query.append(" AND isIn = 0");
			}
			if (status > 0) {
				query.append(" AND status = "+status);
			}

			if(tab.length() == 0)
				tab = "0";
			if(!tab.equals("0")){
				query.append(" AND fundsType = " + tab + "");
			}
			
			if (search) {
			}
			long total = query.count();
			if (total > 0) {
				query.append("ORDER BY " + dir + " DESC");
				List<Bean> dataList = dao.findPage(pageNo, pageSize);

				setP2pUser(dataList);
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
			setAttr("coinMaps",DatabasesUtil.getCoinPropMaps());
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	/**
	 * 更新需要平仓的记录的状态为还款中,表示继续借款
	 */
	@Page(Viewer = XML)
	public void continueLoan(){
		if(!codeCorrect(XML)){
			return;
		}
		
		int id = intParam("id");
		LoanRecord record = (LoanRecord) dao.get("SELECT * FROM loanrecord WHERE id = ?", new Object[]{id}, LoanRecord.class);
		if(record == null){
			WriteError("没有借贷记录。");
			return;
		}
		if(record.getStatus() != LoanRecordStatus.forceRepay.getKey()){
			WriteError("当前记录不是需要平仓的记录。");
			return;
		}
		
		P2pUser user = (P2pUser)Data.GetOne("SELECT * FROM p2pUser WHERE userId = ?", new Object[]{record.getInUserId()}, P2pUser.class);
		if(user.isRepayLock()){
			WriteError("平仓状态已锁定，不能更改状态。");
			return;
		} 
		
		int count = Data.Update("UPDATE loanrecord SET status = ? WHERE id = ? AND status = ?", 
				new Object[]{LoanRecordStatus.Returning.getKey(), id, LoanRecordStatus.forceRepay.getKey()});
		if(count > 0){
			WriteRight("记录更新成功。");
			return;
		}else{
			WriteError("记录更新失败。");
			return;
		}
	}
	
	/**
	 * 对需要平仓的用户进行强制平仓
	 */
	@Page(Viewer = XML)
	public void force(){
		if(!codeCorrect(XML)){
			return;
		}
		
		String userId = param("userId");
		P2pUser user = (P2pUser)Data.GetOne("SELECT * FROM p2pUser WHERE userId = ?", new Object[]{userId}, P2pUser.class);
		if(userId == null){
			WriteError("借贷用户不存在。");
			return;
		}
		
		LoanRecordDao lrDao = new LoanRecordDao();
		lrDao.cancelAndAutoRepay(user, false);
		
		WriteRight("平仓请求已发送。");
	}
	
	private void setP2pUser(List<Bean> dataList){
		String userIds = "";
		Hashtable<String, String> table = new Hashtable<String, String>();
		for(Bean b : dataList){
			LoanRecord record = (LoanRecord)b;
			if(table.contains(record.getInUserId())){
				continue;
			}
			table.put(record.getInUserId(), record.getInUserId());
			userIds += ","+record.getInUserId();
		}
		if(userIds.length() > 1){
			userIds = userIds.substring(1);
		}
		
		List<Bean> p2pUsers = (List<Bean>)Data.Query("SELECT * FROM p2pUser WHERE userId IN ("+userIds+")", new Object[]{}, P2pUser.class);
		Map<String, P2pUser> maps = new HashMap<String, P2pUser>();
		if(p2pUsers != null && p2pUsers.size() > 0){
			for(Bean b : p2pUsers){
				P2pUser user = (P2pUser)b;
				maps.put(user.getUserId(), user);
			}
		}
		
		for(Bean b : dataList){
			LoanRecord record = (LoanRecord)b;
			record.setP2pUser(maps.get(record.getInUserId())); 
		}
	}
	
	@Page(Viewer = JSON)
	public void tongji() {
		try {
			String tab = param("tab");
			int pageNo = intParam("page");
			String inUserId = param("inUserId");
			String inUserName = param("inUserName");
			String outUserId = param("outUserId");
			String outUserName = param("outUserName");
			int isIn = intParam("isIn");
			int status = intParam("status");
			String dir = param("dir");
			if(StringUtils.isBlank(dir)){
				dir = "createTime";
			}
			
			String ids = param("eIds");
			boolean isAll = booleanParam("isAll");
			
			if(ids.endsWith(",")){
				ids = ids.substring(0, ids.length()-1);
			}
			Query query = dao.getQuery();
			query.setSql("select * from loanrecord");
			query.setCls(LoanRecord.class);
			
			if(isAll){
				if (inUserId.length() > 0) {
					query.append(" AND inUserId = '" + inUserId + "'");
				}
				if (inUserName.length() > 0) {
					query.append(" AND inUserName like '%" + inUserName + "%'");
				}
				if (outUserId.length() > 0) {
					query.append(" AND outUserId = '" + outUserId + "'");
				}
				if (outUserName.length() > 0) {
					query.append(" AND outUserName like '%" + outUserName + "%'");
				}
				if (isIn == 1) {
					query.append(" AND isIn = 1");
				} else if(isIn == 2){
					query.append(" AND isIn = 0");
				}
				if (status > 0) {
					query.append(" AND status = "+status);
				}

				if(tab.length() == 0)
					tab = "0";
				if(!tab.equals("0")){
					query.append(" AND fundsType = " + tab + "");
				}
			}else{
				query.append(" id IN ("+ids+")");
			}
			
			List<Bean> list = dao.find();
		
			BigDecimal totalAmount = BigDecimal.ZERO;
			BigDecimal hasRepay = BigDecimal.ZERO;
			
			JSONArray total = new JSONArray();
			for(Bean b : list){
				LoanRecord record = (LoanRecord)b;
				totalAmount = totalAmount.add(record.getAmount().add(record.getHasRepay()));
				hasRepay = hasRepay.add(record.getHasRepay());
			}
			String pattern = "0.00######";//格式代码
			DecimalFormat df = new DecimalFormat();
			df.applyPattern(pattern);
			
			total.add(df.format(totalAmount));
			total.add(df.format(hasRepay));
			
			json("", true, total.toString());
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
}
