/*package com.world.controller.admin.loan.transfer;
import java.util.List;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.loan.dao.P2pUserDao;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/loan/transfer/", des = "转让管理")
public class Index extends UserAction {
	
	P2pUserDao pUDao = new P2pUserDao();
	TransferRecordDao recordDao = new TransferRecordDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");

		Query query = recordDao.getQuery();
		query.setSql("select * from transferRecord");
		query.setCls(TransferRecord.class);
		int pageSize = 10;
		
		query.append("ORDER BY pdate DESC");
		// 将参数保存为attribute
		try {
			int total = recordDao.count();
			if (total > 0) {
				List<Bean> dataList = recordDao.findPage(pageNo, pageSize);
				
				recordDao.initLoanRecord(dataList);//初始化记录
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging(total, pageNo, pageSize);
			setAttr("pageNo", pageNo);
		} catch (Exception ex) {
			log.error(ex.toString());
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
}

*/