/*package com.world.controller.admin.app;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.code.morphia.query.Query;
import com.world.model.dao.app.OpinionDao;
import com.world.model.entity.app.Opinion;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/app/opinion/" , des = "用户吐槽管理")
public class UserOpinions extends AdminAction{
	private static final long serialVersionUID = 1049359233781250975L;
	
	OpinionDao opinionDao = new OpinionDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		//查询条件
		int pageNo = intParam("page");
		Timestamp startDate = dateParam("startDate");
		Timestamp endDate = dateParam("endDate");
		Query<Opinion> q = opinionDao.getQuery();
		int pageSize = 20;
		// 将参数保存为attribute
		try {
			// 构建查询条件
			if (null !=startDate) {
				Date d = startDate;//new Date(startDate);
				q.field("submitDate").greaterThanOrEq(d);
			}
			if (null !=endDate) {
				Date d = endDate;//new Date(startDate);
				q.field("submitDate").lessThanOrEq(d);
			}
			q.order("-submitDate");
			log.info("搜索的sql语句:" + q.toString());
			long total = opinionDao.count(q);
			if (total > 0) {
				List<Opinion> dataList = opinionDao.findPage(q, pageNo, pageSize);
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
			setAttr("page", pageNo);
		} catch (Exception ex) {
			log.error(ex.toString());
		}
	}
	@Page(Viewer=DEFAULT_AJAX)
	public void ajax(){
		index();
	}
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			long id = longParam("id");
			if(id > 0){
				Opinion curData = opinionDao.getById(id);
				request.setAttribute("curData", curData);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = XML)
	public void doAoru() {
		try {
			long id = longParam("id");
			String reply = param("reply");
			
			Date today = Calendar.getInstance().getTime();
			opinionDao.update(opinionDao.getQuery().filter("_id =", id), 
					opinionDao.getUpdateOperations()
					.set("replyDate", today)
					.set("reply", reply)
					);
			WriteRight("回复成功");
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			WriteError("回复失败");
		}
	}
	
	@Page(Viewer = XML)
	public void doDel() {
		Long id = longParam("id");
		if (id > 0) {
			boolean res = true;
			if (res) {
				if (opinionDao.deleteByQuery(opinionDao.getQuery().filter("myId", id)).getError() == null) {
					Write("删除成功", true, "");
					return;
				}
			}
		}
		Write("未知错误导致删除失败！", false, "");
	}
	
}
*/