/*package com.world.controller.admin.app;

import java.util.Date;

import com.google.code.morphia.query.Query;
import com.world.model.dao.app.AppSettingDao;
import com.world.model.entity.app.AppSetting;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/app/setup/" , des = "客户端设置")
public class AppSetup extends AdminAction{
	private static final long serialVersionUID = -4715347063797664657L;
	
	AppSettingDao dao = new AppSettingDao();
	
	@Page(Viewer = "aoru.jsp")
	public void index(){
		Query<AppSetting> query = dao.getQuery();
		AppSetting appSetting = dao.findOne(query);
		request.setAttribute("curData", appSetting);
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
				AppSetting app = dao.getById(id);
				request.setAttribute("curData", app);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = XML)
	public void doAoru() {
		try {
			long id = longParam("id");
			int freeFee = intParam("isFreeFee");
			boolean isFreeFee = freeFee==0?false:true;
			int areaVersion=intParam("areaVersion");
			int rechargeBankVersion=intParam("rechargeBankVersion");
			int countryInfoVersion=intParam("countryInfoVersion");
			
			if(id <= 0) {
				Date currentTime = TimeUtil.getNow();
				AppSetting app = new AppSetting();
				app.setCreatetime(currentTime);
				dao.save(app);
			} else {
				dao.update(dao.getQuery().filter("_id =", id), 
						dao.getUpdateOperations()
						.set("isFreeFee", isFreeFee)
						.set("areaVersion", areaVersion)
						.set("rechargeBankVersion", rechargeBankVersion)
						.set("countryInfoVersion", countryInfoVersion)
						);
			}
			WriteRight("保存成功");
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			WriteError("修改失败");
		}
	}
	
}
*/