package com.world.controller.admin.loan.p2puser.investorapply;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.loan.dao.InvestorApplyDao;
import com.world.model.loan.entity.MyInvestorApply;
import com.world.model.loan.entity.P2pUser;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FunctionAction(jspPath = "/admins/loan/p2puser/investorapply/", des = "借贷人申请")
public class Index extends AdminAction {

	InvestorApplyDao dao = new InvestorApplyDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		try {
			String tab = param("tab");
			int pageNo = intParam("page");
			String userId = param("userId");
			String userName = param("userName");

			int pageSize = 20;

			if(StringUtils.isBlank(tab)){
				tab = "untreated";
			}
			setAttr("tab", tab);

			Query<MyInvestorApply> q = dao.getQuery(MyInvestorApply.class);

			if (StringUtils.isNotBlank(userId)) {
				q.filter("userId", userId);
			}
			if (StringUtils.isNotBlank(userName)) {
				q.filter("userName", userName);
			}
			if (!"all".equals(tab)) {
				if ("untreated".equals(tab)) {
					q.filter("status", 1);
				} else if ("open".equals(tab)) {
					q.filter("status", 2);
				} else if ("reject".equals(tab)) {
					q.filter("status", 3);
				}
			}
			q.order("-date");
			long total = dao.count(q);
			if (total > 0) {
				List<MyInvestorApply> dataList = dao.findPage(q.order("- _id"), pageNo, pageSize);

				String userIds = "";
				for (MyInvestorApply ia : dataList) {
					if ("".equals(userIds)) {
						userIds += "'" + ia.getUserId() + "'";
					} else {
						userIds += ",'" + ia.getUserId() + "'";
					}
				}
				
				String sql = "select * from p2puser where userId in ("+userIds+") ";
				List<Bean> list = Data.Query(sql, new Object[]{}, P2pUser.class);
				Map<String, P2pUser> p2pMap = new HashMap<String, P2pUser>();
				if (null != list && list.size() > 0) {
					for (int i=0;i<list.size();i++) {
						P2pUser pu = (P2pUser) list.get(i);
						p2pMap.put(pu.getUserId(), pu);
					}
				}
				
				for (MyInvestorApply ia : dataList) {
					ia.setP2pUser(p2pMap.get(ia.getUserId())); 
				}
				setAttr("dataList", dataList);
			}
			setPaging((int) total, pageNo, pageSize);

		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	@Page(Viewer = XML)
	public void doDel() {
		long id = longParam("id");
		if (id > 0) {
            dao.deleteByQuery(dao.getQuery().filter("_id", id));
            Write("删除成功", true, "");
            return;
		}
		Write("未知错误导致删除失败！", false, "");
	}
	
	@Page(Viewer = XML)
	public void rejectInvStatus() {
		int userId = intParam("userId");
		if(userId==0) {
			Write("用户编号不能为空",false,"{}");
			return;
		}
		
		Datastore ds = dao.getDatastore();
		com.google.code.morphia.query.Query<MyInvestorApply> query = ds.find(MyInvestorApply.class, "userId", userId+"");
		UpdateOperations<MyInvestorApply> operate = ds.createUpdateOperations(MyInvestorApply.class).set("status", 3);
		UpdateResults<MyInvestorApply> ur = ds.update(query, operate);
		if (!ur.getHadError()) {
			Write("操作成功", true, "");
		}else{
			Write("操作失败", false, "");
		}
	}
}