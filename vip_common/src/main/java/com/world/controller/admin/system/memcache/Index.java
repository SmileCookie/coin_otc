/*package com.world.controller.admin.system.memcache;
import java.util.List;
import java.util.regex.Pattern;

import com.google.code.morphia.query.Query;
import com.world.cache.Cache;
import com.world.model.dao.cache.MemcachedDao;
import com.world.model.entity.cache.Memcached;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/system/memcache/", des = "内存管理")
public class Index extends AdminAction {
	MemcachedDao dao = new MemcachedDao();
	
	@Page(Viewer = "/admins/system/memcache/list.ftl")
	public void index() {
		if(!couldSearch()){
			return;
		}
		
		dao.saveData();
		int pageNo = intParam("page");
		String key = param("key").trim();

		Query<Memcached> q = dao.getQuery();
		int pageSize = 50;

		try {
			if(key.length() > 0){
				Pattern pattern = Pattern.compile("^.*"  + key+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("key", pattern);
			}

			long total = dao.count(q);
			if (total > 0) {
				List<Memcached> dataList = dao.findPage(q, pageNo, pageSize);
				
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setAttr("pager", setPaging((int) total, pageNo, pageSize));
			
		} catch (Exception ex) {
			log.error(ex.toString());
			log.error(ex.toString(), ex);
		}
	}

	// ajax的调用
	@Page(Viewer = "/admins/system/memcache/ajax.ftl")
	public void ajax() {
		index();
	}
	
	@Page(Viewer = JSON)
	public void searchKey(){
		String key = param("key");
		if(key != null){
			String obj = Cache.Get(key);
			json(obj, true, "");
		}else{
			json("获取失败", false, "");
		}
	}
}

*/