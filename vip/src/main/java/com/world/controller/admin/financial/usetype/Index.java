package com.world.controller.admin.financial.usetype;
import java.util.List;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.financial.dao.FinanUseTypeDao;
import com.world.model.financial.entity.FinanUseType;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/usetype/", des = "收支用途")
public class Index extends AdminAction {
	
	FinanUseTypeDao dao = new FinanUseTypeDao();
	AdminUserDao auDao = new AdminUserDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		int useTypeId = intParam("useTypeId");

		Query query = dao.getQuery();
		query.setSql("select * from finanusetype");
		query.setCls(FinanUseType.class);
		int pageSize = 20;

		// 将参数保存为attribute
		try {
			if(useTypeId > 0){
				query.append(" AND id = "+useTypeId);
			}

			query.append(" AND isDel = 0");
			query.append("ORDER BY createTime");

			long total = query.count();
			if (total > 0) {
				List<FinanUseType> dataList = dao.findPage(pageNo, pageSize);
				
				dao.setaUser(dataList);
				
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
			
			setAttr("useTypes", dao.findList());
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
				FinanUseType useType = dao.get(id);
				setAttr("useType", useType);
			}
			setAttr("fundTypes", DatabasesUtil.getCoinPropMaps());
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doAoru() {
		try {
			int id = intParam("id");
			
			String name = param("name");
			String memo = param("memo");
			int inOut = intParam("inOut");
			int fundType = intParam("fundType");
			int turnRound = intParam("turnRound");
			int type = intParam("type");
			
			FinanUseType usetype = new FinanUseType(name, inOut, memo, turnRound, fundType);
			usetype.setType(type);
			
			int res = 0;
			if (id > 0) {
				usetype.setId(id);
				usetype.setUpdateId(adminId());
				usetype.setUpdateTime(now());
				
				res = dao.update(usetype);
			} else {
				usetype.setCreateId(adminId());
				usetype.setCreateTime(now());
				
				res = dao.save(usetype);
			}
			if (res > 0) {
				WriteRight("操作成功");
			}else{
				WriteError("操作失败");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = XML)
	public void doDel() {
		int id = intParam("id");
		if (id > 0) {
			dao.delById(id);
			
			Write("删除成功。", true, "");
			return;
		}
		Write("未知错误导致删除失败。", false, "");
	}
}

