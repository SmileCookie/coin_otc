package com.world.controller.admin.api;

import java.util.List;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.api.ApiKeyDao;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.api.ApiKey;
import com.world.model.entity.coin.CoinProps;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;


@FunctionAction(jspPath = "/admins/api/" , des = "api管理列表")
public class Index extends AdminAction{
	
	private static final long serialVersionUID = 1L;
	private ApiKeyDao apiDao = new ApiKeyDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		int currentPage = intParam("page");
		String userName = param("userName");
		String ipaddrs = param("ipaddrs");
		int isAct = intParam("isAct");
		int isLock = intParam("isLock");

		int pageSize = 20;
		
		/*CoinProps coint = DatabasesUtil.coinProps("ltc");
		apiDao.setDatabase(coint.getDatabasesName());
		*/
		Query<ApiKey> query = apiDao.getQuery();
		query.setSql("select * from apikey");
		query.setCls(ApiKey.class);
		
		if(userName.trim().length()>0){
			query.append("userName like '%"+userName+"%'");
		}
		if(ipaddrs.trim().length()>0){
			query.append("ipaddrs like '%"+ipaddrs+"%'");
		}
		if(isAct>0){
			query.append("isAct="+(isAct-1));
		}
		if(isLock>0){
			query.append("isLock="+(isLock-1));
		}
		query.append("isDel=0");
		
		int total = query.count();
		if(total > 0){
			List<ApiKey> APIList = query.getPageList(currentPage, pageSize);//提现记录
			setAttr("dataList", APIList);
		}
		setPaging(total, currentPage, pageSize);
	}
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}	
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		String id = param("id");

		ApiKey record = (ApiKey) apiDao.getById(ApiKey.class, id);
		setAttr("record", record);
	}
	
	@Page(Viewer = JSON)
	public void doAoru() {
		String id = param("id");
		int isAct = intParam("isAct");
		int isLock = intParam("isLock");

		ApiKey apiKey = (ApiKey) apiDao.getById(ApiKey.class, id);
		if(apiKey == null){
			json("修改失败，找不到该记录", false, "");
			return;
		}
		
		int count = apiDao.update("UPDATE ApiKey SET isAct = ? ,isLock=? WHERE id = ?", new Object[]{isAct, isLock, id});
		if(count > 0){
			try {
//				CoinProps coint = DatabasesUtil.coinProps("ltc");
//				apiDao.setDatabase(coint.getDatabasesName());
//				apiDao.update("UPDATE ApiKey SET isAct = ? ,isLock=? WHERE id = ?", new Object[]{isAct, isLock, id});
				//插入一条管理员日志信息
				DailyType type = DailyType.adminOperate;
				new DailyRecordDao().insertOneRecord(type, "修改用户"+apiKey.getUserName()+"的激活状态为："+(isAct==1?"已激活":"未激活")+" 锁定状态为："+(isLock==1?"已锁定":"未锁定"), String.valueOf(adminId()), ip(), now());
			} catch (Exception e) {
				log.error("添加管理员日志失败", e);
			}
			json("设置成功", true, "");
		}else{
			json("设置失败", false, "");
		}
	}
	@Page(Viewer = ".xml")
	public void doDel() {
		try {
			int id = intParam("id");
			if(id == 0){
				WriteError("删除失败，找不到该记录");
			} else {
				CoinProps coint = DatabasesUtil.coinProps("ltc");
				apiDao.setDatabase(coint.getDatabasesName());
				if(apiDao.delete(id)>0) {
//					CoinProps coint = DatabasesUtil.coinProps("ltc");
//					apiDao.setDatabase(coint.getDatabasesName());
//					apiDao.delete(id);
					WriteRight("删除成功");
				} else {
					WriteError("删除失败");
				}
			}
		} catch (Exception ex) {
			log.error("删除异常", ex);
			WriteError("内部异常");
		}
	}
}
