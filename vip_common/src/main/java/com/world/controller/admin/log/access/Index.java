/*package com.world.controller.admin.log.access;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.google.code.morphia.query.Query;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.defense.AccessLog;
import com.world.web.defense.AccessLogDao;


@FunctionAction(jspPath = "/admins/log/access/" , des="访问日志")
public class Index extends AdminAction{
	
    *//**
	 * 
	 *//*
	private static final long serialVersionUID = -6611150964118287895L;

	AccessLogDao  dao = new AccessLogDao();
	 
	private int pageSize = 100;
	
	@Page(Viewer = "/admins/log/access/list.ftl")
	public void index(){
		
		String ip = param("ip");
		String adminName = param("adminName");
		String userName = param("userName");
		String urls = param("urls");
		int pageNo = intParam("page");
		if(pageNo<=0){
			pageNo = 1;
		}
		Query<AccessLog> query = dao.getQuery();
		
		if(ip.length() > 0){
			query.filter("ip =", ip);
		}
		
		if(adminName.length() > 0){
			query.filter("adminName =", adminName);
		}
		
		if(userName.length() > 0){
			query.filter("userName =", userName);
		}
		
		if(urls.length() > 0){
			query.filter("urls =", urls);
		}
		
		query.order("-minuteFirst");
		
		List<AccessLog> dataList = dao.findPage(query, pageNo, pageSize);
		
		if(dataList != null && dataList.size() > 0){
			for(AccessLog ac : dataList){
				ac.setDate(new Timestamp(ac.getMinuteFirst()));
			}
		}
		
		setAttr("itemCount", 1000000);
		setAttr("dataList", dataList);
		setPaging(1000000, pageNo, pageSize);
	}



	// ajax的调用
	@Page(Viewer = "/admins/log/access/ajax.ftl")
	public void ajax() {
		index();
	}
	

	// ajax的调用
	@Page(Viewer = "/admins/log/detailParams.ftl")
	public void detailParams() {
	}
	
	*//**
	 * 功能:添加页面
	 *//*
	@Page(Viewer = "/admins/log/aoru.ftl")
	public void aoru() {
		String ip = param("ip");
		String type = param("type");
		setAttr("ip", ip);
		setAttr("type", type);
		setAttr("expire", DateFormatUtils.format(TimeUtil.getAfterDay(1), "yyyy-MM-dd HH:mm"));
		
	}

	*//**
	 * 功能:响应添加的函数
	 *//*
	@Page(Viewer = XML)
	public void doAoru() {
		
	}
	

	*//**
	 * 获取某个IP的所有匹配情况, 匹配符越少 优先级越高 <br>
	 * 192.168.2.3 ==>192.168.2.3,  192.168.2.*, 192.168.*.* , 192.*.*.*,
	 *//*
	private static List<String> getIpPolymorphic(String _ip) {
		String[] ips = StringUtils.split(_ip, ".");
		List<String> rs = new ArrayList<String>();
		rs.add(_ip);
		rs.add(ips[0]  + "." + ips[1] + "." + ips[2] + ".*");
		rs.add(ips[0]  + "." + ips[1] + ".*.*");
		rs.add(ips[0] + ".*.*.*");
		return rs;
	}
	
}
*/