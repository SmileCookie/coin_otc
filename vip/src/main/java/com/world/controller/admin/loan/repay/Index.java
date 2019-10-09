package com.world.controller.admin.loan.repay;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.loan.dao.RepayOfQiDao;
import com.world.model.loan.entity.RepayOfQi;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/loan/repay/", des = "还款记录")
public class Index extends UserAction {
	RepayOfQiDao dao = new RepayOfQiDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		int pageNo = intParam("page");
		String tab = param("tab");
		int loanRecordId = intParam("loanRecordId");
		String inUserId = param("inUserId");
		String inUserName = param("inUserName");
		String outUserId = param("outUserId");
		
		Query q = dao.getQuery();
		q.setSql("select * from repayOfQi");
		q.setCls(RepayOfQi.class);
		
		int pageSize = 20;
		
		JSONArray array = new JSONArray();
		
		if(tab.length() == 0)
			tab = "0";
		if(!tab.equals("0")){
			q.append(" AND fundType = " + tab + "");
		}
		
		if(loanRecordId > 0){
			q.append(" loanRecordId = ?");
			array.add(loanRecordId);
		}
		if(inUserId.length() > 0){
			q.append(" userId = ?");
			array.add(inUserId);
		}
		if(inUserName.length() > 0){
			q.append(" userName = ?");
			array.add(inUserName);
		}
		if(outUserId.length() > 0){
			q.append(" outUserId = ?");
			array.add(outUserId);
		}
		if(array.size() > 0){
			q.setParams(array.toArray());
		}
		
		int total = dao.count();
 		if(total > 0){
 			q.append("order by forecastDate desc");
 			List<Bean> list = dao.findPage(pageNo, pageSize);
 			setAttr("dataList", list);
 		}
		setPaging(total, pageNo, pageSize);
		setAttr("itemCount", total);
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	@Page(Viewer = JSON)
	public void tongji() {
		try {
			String tab = param("tab");
			int loanRecordId = intParam("loanRecordId");
			String inUserId = param("inUserId");
			String inUserName = param("inUserName");
			String outUserId = param("outUserId");
			
			String ids = param("eIds");
			boolean isAll = booleanParam("isAll");
			
			if(ids.endsWith(",")){
				ids = ids.substring(0, ids.length()-1);
			}
			Query q = dao.getQuery();
			q.setSql("select * from repayOfQi");
			q.setCls(RepayOfQi.class);
			
			if(isAll){
				if(tab.length() == 0)
					tab = "0";
				if(!tab.equals("0")){
					q.append(" AND fundsType = " + tab + "");
				}
				
				JSONArray array = new JSONArray();
				if(loanRecordId > 0){
					q.append(" loanRecordId = ?");
					array.add(loanRecordId);
				}
				if(inUserId.length() > 0){
					q.append(" userId = ?");
					array.add(inUserId);
				}
				if(inUserName.length() > 0){
					q.append(" userName = ?");
					array.add(inUserName);
				}
				if(outUserId.length() > 0){
					q.append(" outUserId = ?");
					array.add(outUserId);
				}
				if(array.size() > 0){
					q.setParams(array.toArray());
				}
			}else{
				q.append(" id IN ("+ids+")");
			}
			
			List<Bean> list = dao.find();
		
			BigDecimal benjin = BigDecimal.ZERO;
			BigDecimal lixi = BigDecimal.ZERO;
			BigDecimal fwf = BigDecimal.ZERO;
			
			JSONArray total = new JSONArray();
			for(Bean b : list){
				RepayOfQi qi = (RepayOfQi)b;
				benjin = benjin.add(qi.getBenJin());
				lixi = lixi.add(qi.getLiXi());
				fwf = fwf.add(qi.getLxFwf());
			}
			String pattern = "0.00######";//格式代码
			DecimalFormat df = new DecimalFormat();
			df.applyPattern(pattern);
			
			total.add(df.format(benjin));
			total.add(df.format(lixi));
			total.add(df.format(fwf));
			
			json("", true, total.toString());
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
}

