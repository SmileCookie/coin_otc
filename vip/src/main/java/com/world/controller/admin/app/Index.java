package com.world.controller.admin.app;

import com.alibaba.fastjson.JSONObject;
import com.google.code.morphia.query.Query;
import com.world.model.dao.app.AppDao;
import com.world.model.entity.app.App;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

@FunctionAction(jspPath = "/admins/app/" , des = "客户端管理")
public class Index extends AdminAction{
	AppDao dao = new AppDao();
	
	@Page(Viewer = DEFAULT_INDEX)
		public void index(){
		//查询条件
		// 获取参数
		int pageNo = intParam("page");
		String name = param("name");
		String type = param("type");
		String tab = param("tab");
		Query<App> q = dao.getQuery();
		int pageSize = 20;
		// 将参数保存为attribute
		try {
			// 构建查询条件
			if (name.length() > 0) {
				q.field("name").contains(name);
			}
			// 构建查询条件
			if (type.length() > 0) {
				q.field("type").equal(type);
			}
			q.order("-datetime");
			log.info("搜索的sql语句:" + q.toString());
			long total = dao.count(q);
			if (total > 0) {
				List<App> dataList = dao.findPage(q, pageNo, pageSize);

				for(App app : dataList){
					String appName = app.getName();
					JSONObject nameJson = JSONObject.parseObject(appName);
					app.setCnName(nameJson.getString("cn"));
					app.setEnName(nameJson.getString("en"));
					app.setHkName(nameJson.getString("hk"));
				}

				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
			setAttr("tab", tab);
			setAttr("page", pageNo);
		} catch (Exception ex) {
			log.error("内部异常", ex);
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
				App app = dao.getById(id);

				String appName = app.getName();
				JSONObject nameJson = JSONObject.parseObject(appName);
				app.setCnName(nameJson.getString("cn"));
				app.setEnName(nameJson.getString("en"));
				app.setHkName(nameJson.getString("hk"));

				String appRemark = app.getRemark();
				JSONObject remarkJson = JSONObject.parseObject(appRemark);
				app.setCnRemark(remarkJson.getString("cn"));
				app.setEnRemark(remarkJson.getString("en"));
				app.setHkRemark(remarkJson.getString("hk"));

				request.setAttribute("curData", app);
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer = XML)
	public void doAoru() {
		try {
//			if(!codeCorrect(XML)){
//				return;
//			}
			long id = longParam("id");
			String num=param("num");
			String type=param("type");
			String url=param("url");
			boolean released = param("released").equalsIgnoreCase("on") ? true : false;

			String size = param("size");

			String cnName = param("cnName");
			String enName = param("enName");
			String hkName = param("hkName");
			JSONObject name = new JSONObject();
			name.put("cn", cnName);
			name.put("en", enName);
			name.put("hk", hkName);

			String cnRemark = param("cnRemark");
			String enRemark = param("enRemark");
			String hkRemark = param("hkRemark");
			JSONObject remark = new JSONObject();
			remark.put("cn", cnRemark);
			remark.put("en", enRemark);
			remark.put("hk", hkRemark);
			boolean isEnforceUpdate = booleanParam("isEnforceUpdate");

			if(id <= 0) {
				App app = new App();
				app.setName(name.toJSONString());
				app.setNum(num);
				app.setType(type);
				app.setUrl(url);
				app.setRemark(remark.toJSONString());
				app.setReleased(released);
				app.setDatetime(TimeUtil.getNow());
				app.setSize(size);
				app.setUpdateDatetime(TimeUtil.getNow());
				app.setEnforceUpdate(isEnforceUpdate);
				dao.save(app);
			} else {
				dao.update(dao.getQuery().filter("_id =", id),
						dao.getUpdateOperations()
						.set("name", name.toJSONString())
						.set("num", num)
						.set("type", type)
						.set("url", url)
						.set("remark", remark.toJSONString())
						.set("released", released)
						.set("size", size)
						.set("updateDatetime", TimeUtil.getNow())
						.set("isEnforceUpdate", isEnforceUpdate)
						);
			}
			dao.setCache();// 更新缓存
			WriteRight("保存成功");
		} catch (Exception ex) {
			log.error("内部异常", ex);
			WriteError("修改失败");
		}
	}
	
	@Page(Viewer = XML)
	public void doDel() {
		Long id = longParam("id");
		if (id > 0) {
			boolean res = true;
			if (res) {
                dao.deleteByQuery(dao.getQuery().filter("myId", id));
                dao.setCache();// 更新缓存
                Write("删除成功", true, "");
                return;
			}
		}
		Write("未知错误导致删除失败！", false, "");
	}
	

	@Page(Viewer = XML)
	public void changeAppKey() {
		try {
			if(!codeCorrect(XML)){
				return;
			}
			Long id = longParam("id");
			App app = dao.get(id);
			String newKey = RandomStringUtils.randomAlphanumeric(88);
			String newSecret = RandomStringUtils.randomAlphanumeric(88);
			if(app == null) {
				Write("操作失败", false, "");
				return;
			} else {
				String key = app.getKey();
				if (StringUtils.isNotBlank(key))
					newKey = key;
					
				dao.update(dao.getQuery().filter("_id =", id), 
						dao.getUpdateOperations()
						.set("key", newKey)
						.set("secret", newSecret)
						);
//				this.updateCache(newKey, newSecret);
				WriteRight("保存成功");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			WriteError("修改失败");
		}
		
	}
	
}
