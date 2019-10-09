package com.world.controller.admin.loan;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.loan.dao.LoanDao;
import com.world.model.loan.entity.Loan;
import com.world.model.loan.entity.LoanRecord;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/loan/", des = "借贷管理")
public class Index extends AdminAction {
	LoanDao dao = new LoanDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		String tab = param("tab");
		int pageNo = intParam("page");
		String userId = param("userId");
		String userName = param("userName");
		int isIn = intParam("isIn");
		int status = intParam("status");
		int investMark = intParam("investMark");//投资标识
		String withoutLx=param("withoutLx");//免息券
		
		Query query = dao.getQuery();
		query.setSql("select * from loan");
		query.setCls(Loan.class);
		int pageSize = 20;
		boolean search = false;
		// 将参数保存为attribute
		try {
			if (userId.length() > 0) {
				search = true;
				query.append(" AND userId = '" + userId + "'");
			}
			if (userName.length() > 0) {
				search = true;
				query.append(" AND userName like '%" + userName + "%'");
			}
			if(isIn == 1){
				query.append(" AND isIn = 1");
			}else if(isIn == 2){
				query.append(" AND isIn = 0");
			}
			if (status > 0) {
				status = status - 1;
				query.append(" AND status = "+status);
			}
			//投资标识判断，1、2是list.jsp的val，0、1是数据库值
			if (investMark == 1) {
				query.append(" AND investMark = 0");
			} else if (investMark == 2){
				query.append(" AND investMark = 1");
			}
			
			//免息券判断
			if (StringUtils.isNotBlank(withoutLx)) {
				query.append("AND withoutLx = " + withoutLx);
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
				query.append("ORDER BY createTime DESC");
				List<Bean> dataList = dao.findPage(pageNo, pageSize);

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
	
	@Page(Viewer = JSON)
	public void tongji() {
		try {
			String tab = param("tab");
			int pageNo = intParam("page");
			String userId = param("userId");
			String userName = param("userName");
			int isIn = intParam("isIn");
			int status = intParam("status");
			int investMark = intParam("investMark");//投资标识
			String withoutLx=param("withoutLx");//免息券
			
			String ids = param("eIds");
			boolean isAll = booleanParam("isAll");
			
			if(ids.endsWith(",")){
				ids = ids.substring(0, ids.length()-1);
			}
			Query query = dao.getQuery();
			query.setSql("select * from loan");
			query.setCls(Loan.class);
			
			if(isAll){
				if (userId.length() > 0) {
					query.append(" AND userId = '" + userId + "'");
				}
				if (userName.length() > 0) {
					query.append(" AND userName like '%" + userName + "%'");
				}
				if(isIn == 1){
					query.append(" AND isIn = 1");
				}else if(isIn == 2){
					query.append(" AND isIn = 0");
				}
				if (status > 0) {
					status = status - 1;
					query.append(" AND status = "+status);
				}
				//投资标识判断，1、2是list.jsp的val，0、1是数据库值
				if (investMark == 1) {
					query.append(" AND investMark = 0");
				} else if (investMark == 2){
					query.append(" AND investMark = 1");
				}
				
				//免息券判断
				if (StringUtils.isNotBlank(withoutLx)) {
					query.append("AND withoutLx = " + withoutLx);
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
			BigDecimal hasSuccess = BigDecimal.ZERO;
			BigDecimal hasRepayment = BigDecimal.ZERO;
			
			JSONArray total = new JSONArray();
			for(Bean b : list){
				Loan loan = (Loan)b;
				totalAmount = totalAmount.add(loan.getAmount());
				hasSuccess = hasSuccess.add(loan.getHasAmount());
				hasRepayment = hasRepayment.add(loan.getHasRepayment());
			}
			String pattern = "0.00######";//格式代码
			DecimalFormat df = new DecimalFormat();
			df.applyPattern(pattern);
			
			total.add(df.format(totalAmount));
			total.add(df.format(hasSuccess));
			total.add(df.format(hasRepayment));
			
			json("", true, total.toString());
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
}
