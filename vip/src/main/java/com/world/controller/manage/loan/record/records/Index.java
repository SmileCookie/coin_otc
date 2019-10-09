package com.world.controller.manage.loan.record.records;
import com.world.data.mysql.Query;
import com.world.model.entity.EnumUtils;
import com.world.model.loan.dao.LoanRecordDao;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.entity.LoanRecord;
import com.world.model.loan.entity.LoanRecordStatus;
import com.world.model.loan.entity.P2pUser;
import com.world.web.action.UserAction;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class Index extends UserAction {
	
	P2pUserDao pUDao = new P2pUserDao();
	LoanRecordDao recordDao = new LoanRecordDao();
	
	/**
	 * 查看我发布的投资记录情况
	 *
	 */
//	@Page(Viewer = "/cn/manage/loan/record/win.jsp")
	public void index() {
		String userId = userIdStr();
		boolean isIn = booleanParam("isIn");
		int loanId = intParam("lid");
//		int fundType = intParam("fundsType");
		String fundType = param("fundsType");
		String riskManage = param("riskType");
		String withoutLx = param("noRate");
		String status = param("status");
		
		// 获取参数
		int pageNo = intParam("page");
		setAttr("isIn", isIn);

		Query query = recordDao.getQuery();
		query.setSql("select id,status,createTime,amount + hasRepay amount,hasRepay,fundsType,rate from loanrecord");
		query.setCls(LoanRecord.class);
		int pageSize = 10;
		
		if(!userId.equals("0")){
			P2pUser user = (P2pUser) pUDao.getById(userId);
			setAttr("curUser", user);
		}
		
		if(isIn){
			query.append("inUserId='" + userId + "'");
		}else{
			query.append("outUserId='" + userId + "'");
		}
		if(loanId > 0){
			query.append("loanId=" + loanId);
		}
		if (StringUtils.isNotBlank(fundType)) {
			query.append("fundType = " + fundType);
		}
		if (StringUtils.isNotBlank(riskManage)) {
			query.append("riskManage = " + riskManage);
		}
		if (StringUtils.isNotBlank(status)) {
			query.append("status = " + status);
		}
		if (StringUtils.isNotBlank(withoutLx)) {
			if(withoutLx.equals("1")){
				query.append("withoutLxAmount > 0 ");
			}else if (withoutLx.equals("0")) {
				query.append("withoutLxAmount = 0 ");
			}
		}
		
		query.append("ORDER BY field(status,3,0,1,2,4), createTime desc");

		// 将参数保存为attribute
		try {
			int total = recordDao.count();
			if (total > 0) {
				List<LoanRecord> dataList = recordDao.findPage(pageNo, pageSize);
				for(LoanRecord record : dataList) {
					record.setRecordStatusShow(L(((LoanRecordStatus) EnumUtils.getEnumByKey(record.getStatus(), LoanRecordStatus.class)).getValue()));
				}
				//recordDao.setLoan(dataList);
				setAttr("lists", dataList);
				setAttr("itemCount", total);
			}
			setPaging(total, pageNo, pageSize);
			setAttr("pageNo", pageNo);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

//	@Page(Viewer = "/cn/manage/loan/record/winAjax.jsp")
	public void ajax() {
		index();
	}
	

}

