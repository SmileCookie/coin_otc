package com.world.controller.admin.user.iplist;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.iplist.WhiteIpDao;
import com.world.model.entity.iplist.WhiteIp;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import me.chanjar.weixin.common.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

@FunctionAction(jspPath = "/admins/user/iplist/" , des = "API的IP限制管理")
public class Index extends AdminAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -139631220234489998L;
	
	WhiteIpDao dao = new WhiteIpDao();

	@Page(Viewer = DEFAULT_INDEX, Cache = 300)
	public void index(){
		String ip = param("searchIP");//ip地址
		int limit = intParam("searchLimit");//限制次数
		Query<WhiteIp> q = dao.getQuery(WhiteIp.class);
		if(StringUtils.isNotBlank(ip)){
			Pattern pattern = Pattern.compile("^" + ip + ".*$", Pattern.CASE_INSENSITIVE);
			q.filter("ip", pattern);
		}
		if(limit>0){
			q.filter("limit", limit);
		}
		
		
		long total = dao.count(q);
		if (total > 0) {
			List<WhiteIp> dataList = q.asList();

			setAttr("dataList", dataList);
		}
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}
	
	

	@Page(Viewer = XML)
	public void doDel() {
		String id = param("id");
		if (id.length() > 0) {
			boolean res = true;
			if (res) {// .filter("faBuZhe", adminName)
                dao.deleteByQuery(dao.getQuery().filter("_id", id));
                Write("删除成功", true, "");
                //robotDao.reload();//重新刷新数据
                return;
			}
		}
		Write("未知错误导致删除失败！", false, "");
	}

	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			String id = param("id");
			if (id.length() > 0) {
				WhiteIp s =  dao.getById(id);
				setAttr("whiteIp", s);
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	/**
	 * 新增或修改机器人信息
	 */
	@Page(Viewer = ".xml")
	public void doAoru() {
		try {
			String id = param("id");
			String ip = param("ip");//ip地址
			int limit = intParam("limit");//限制次数
			
			int res = 0;
			Datastore ds = dao.getDatastore();
			if (id.length() > 0) {
				Query<WhiteIp> query = ds.find(WhiteIp.class, "_id", id);

				UpdateOperations<WhiteIp> operate = ds.createUpdateOperations(WhiteIp.class).set("ip",ip)//ip地址
						.set("limit", limit)// 限制次数
						.set("createTime", TimeUtil.getNow())
						.set("opUserName", super.adminName());

				UpdateResults<WhiteIp> ur = ds.update(query, operate);
				if (!ur.getHadError()) {
					res = 2;
				}
			} else {
				
				Query<WhiteIp> q = dao.getQuery(WhiteIp.class).filter("ip", ip);
				if(q.asList().size()>0){
					UpdateOperations<WhiteIp> operate = ds.createUpdateOperations(WhiteIp.class)
							.set("ip" , ip)
							.set("limit" , limit)
							.set("createTime", TimeUtil.getNow())
							.set("opUserName", super.adminName());
					UpdateResults<WhiteIp> ur = ds.update(q, operate);
					if (!ur.getHadError()) {
						res = 2;
					}
				} else {
						WhiteIp e = new WhiteIp(ds);
						e.setIp(ip);
						e.setLimit(limit);
						e.setCreateTime(TimeUtil.getNow());
						e.setOpUserName(super.adminName());
						if (dao.save(e) != null) {
							res = 2;
						}
				}
			}
			if (res > 0) {
				WriteRight(L("操作成功"));
				return;
			}else{
				WriteRight(L("操作失败"));
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			WriteRight(L("操作失败"));
		}
	}
	
	
}
