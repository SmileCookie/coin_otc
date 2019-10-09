/*package com.world.controller.admin.log;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.google.code.morphia.Key;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.defense.AccessLogDao;
import com.world.web.defense.IpDefense;
import com.world.web.defense.IpDefenseDao;
import com.world.web.defense.IpDefenseData;


@FunctionAction(jspPath = "/admins/log/" , des="IP防御")
public class Index extends AdminAction{
	
    AccessLogDao  dao = new AccessLogDao();
	 
	IpDefenseDao ipDefenseDao = new IpDefenseDao();
	private int pageSize = 10;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Page(Viewer = "/admins/log/list.ftl")
	public void index(){
		
		String ip = param("ip");
		int pageNo = intParam("page");
		if(pageNo<=0){
			pageNo = 1;
		}

		
		
		 * 按IP,按URL统计访问次数: 
		 
		
		log.debug("按IP,按URL统计访问次数: ");
		
		List<DBObject> querys = new ArrayList<DBObject>();

		buildDBObject(querys,isIp(ip),"{$match:{ip: '%0'}}",ip);
		buildDBObject(querys,"{$group: {_id:{ip:'$ip',url:'$urls',minute:'$minuteFirst'},count_i_u_m:{$sum:'$times'} } }");
		buildDBObject(querys,"{$match:{count_i_u_m:{$gte: 0}}}");
		buildDBObject(querys,"{$sort:{count_i_u_m:-1}}");
		buildDBObject(querys,"{$group: {_id:{ip:'$_id.ip',url:'$_id.url'},highest_mi:{$first:'$_id.minute'},highest_count:{$first:'$count_i_u_m'},count_i_u:{$sum:'$count_i_u_m'} }}");
		buildDBObject(querys,"{$sort:{'count_i_u':-1}}");
		buildDBObject(querys,"{$group: {_id:'$_id.ip',total:{$sum:'$count_i_u'},urls:{ $push: {url:'$_id.url',accessCount:'$count_i_u',highest_mi:'$highest_mi',highest_count:'$highest_count'}} } }");
		buildDBObject(querys,"{$sort:{'total':-1}}");
		buildDBObject(querys,true,"{ $skip : %0 }",(pageNo-1) * pageSize + ""); //分页
		buildDBObject(querys,true,"{ $limit : %0 }",pageNo * pageSize + "" );
		buildDBObject(querys,"{$project: {_id: 0, ip:'$_id', total:1, urls:{url:1,accessCount:1,highest_mi:1,highest_count:1   }}}");

		
		
		DBObject firstOp = querys.get(0);
		DBObject[] additionalOps = querys.subList(1, querys.size()).toArray(new DBObject[querys.size()-1]);
		AggregationOutput out = dao.getCollection().aggregate(firstOp, additionalOps);
		CommandResult comrs = out.getCommandResult();
		BasicDBList rs= (BasicDBList) comrs.get("result");
		int total = 0;
		
		for (Object item : rs) {
			LinkedHashMap<String, Object> dbo = (LinkedHashMap<String, Object>) item;
			List list = (List) dbo.get("urls");
			
			if (!isIp(ip)) {
				//限制每IP显示访问最高前5条url, 如果单独查找某个IP,则显示全部
				int limit = 5;
				if (list.size()>limit) {
					List subList = list.subList(0, limit);
					list.retainAll(subList);
				}
				setAttr("limit", true);
			}
			
			Map map;
			for (Object obj : list) {
				map = (Map) obj;
				long highest_mi = (Long) map.get("highest_mi");
				map.put("highest_mi", new Date(highest_mi));
			}
		}
		
		if (!isIp(ip)) {
			//记录数
			List ips = dao.getCollection().distinct("ip");
			total = ips.size();
		}else{
			total = rs.size();
		}
		
		
		 * 装饰列表,标识黑白名单,
		 
		
		QueryResults<IpDefenseData> blackRs = ipDefenseDao.find();
		List<IpDefenseData> blacks = blackRs.asList();
		Map<String,IpDefenseData> blackIps =new LinkedHashMap<String,IpDefenseData>();
		for (IpDefenseData ipobj : blacks) {
			blackIps.put(ipobj.getIp(),ipobj);
		}
		Set<String> keySet = blackIps.keySet();
		
		for (Object item : rs) {
			LinkedHashMap<String, Object> dbo = (LinkedHashMap<String, Object>) item;
			String _ip = (String) dbo.get("ip");
			List<String> ips =getIpPolymorphic(_ip);
			boolean exists = CollectionUtils.containsAny(keySet, ips);
			if(exists){
				IpDefenseData ipData = null;	
				//按优先级去取, 匹配符越少 优先级越高, 如:192.168.2.3, 192.168.2.*, 192.168.*.* , 192.*.*.*
				for (String _ip2: ips) {
					ipData = blackIps.get(_ip2);
					if(ipData!=null){
						break;
					}
				}
				//ipDate 必不等于null
				dbo.put("type", ipData.getType());
				dbo.put("expire", getDifferenceTime(ipData.getExpire()));
			}
		}
		
		
		 * 白名单
		 
		
		QueryResults<IpDefenseData> whitesResult = ipDefenseDao.find(ipDefenseDao.createQuery());
		List<IpDefenseData> whites = whitesResult.asList();
		
		
		
		setAttr("page", pageNo);
		setAttr("itemCount", total );
		setAttr("dataList", rs);
		setAttr("whites", whites);
		setPaging( total, pageNo, pageSize);
		
		
	}



	// ajax的调用
	@Page(Viewer = "/admins/log/ajax.ftl")
	public void ajax() {
		index();
	}
	

	// ajax的调用
	@Page(Viewer = "/admins/log/detailParams.ftl")
	public void detailParams() {
		String ip = param("ip");
		String url = param("url");
		
		AggregationOutput out = dao.getCollection().aggregate(
				buildDBObject("{$match:{ip: '%0',urls:'%1'}}",ip,url),
				buildDBObject("{$group: {_id:{params:'$params'},count:{$sum:1} }}"),
				buildDBObject("{$sort:{count:-1}}"),
				buildDBObject("{$project:{_id:0,params:'$_id.params',count:1}}")
		);
		CommandResult comrs = out.getCommandResult();
		BasicDBList rs= (BasicDBList) comrs.get("result");
		
		
		setAttr("dataList", rs);
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
		try {
		   String ip = param("ip");
           long expire = dateParam("expire").getTime();
           int type = intParam("type");
	          
			if(ip.length() > 0){
				boolean success = ipDefenseDao.addHeibai(ip, type, expire);
				if(success){
					Write("操作成功, 已添加: <b>" + ip + "</b> 到 <b>" + (type==0?"黑":"白") + "名单</b> , <br/> <b>"
							+ TimeUtil.getDateToString(dateParam("expire")) + "</b> 后恢复, 即  <b>"
							+ getDifferenceTime(expire) + "</b> 后", true, "");
					return;
				}
			}
			
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		Write("未知错误导致添加失败！",false,"");
	}

	
	*//**
	 * 删除
	 *//*
	@Page(Viewer = XML)
	public void doDel() {
		String ip = param("ip");
		WriteResult result = ipDefenseDao.deleteByQuery(ipDefenseDao.createQuery().filter("ip", ip));
		int n = result.getN();
		if(n>0){
			Write("操作成功", true, "");
		}else{
			Write("未知错误导致删除失败！", false, "");
		}
		
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

	
	*//**
	 * 获取过期时间的友好显示, 显示:xx天xx小时
	 *//*
	public static String getDifferenceTime(long date) {
		if(date == 0)
			return null;
		long cha = TimeUtil.getMinuteFirst(date).getTime() -TimeUtil.getMinuteFirst(System.currentTimeMillis()).getTime();
		
		long day=cha/(24*60*60*1000); 
		long hour=(cha/(60*60*1000)-day*24); 
		long min=((cha/(60*1000))-day*24*60-hour*60); 
		String str ="";
		if(day>0){
			str +=day + "天" ;
			if(hour>0){
				str += hour + "小时";
			}
			
		}else if(hour>0){
			str +=hour + "小时" ;
			if(min>0){
				str += min + "分钟";
			}
		}else if(min>0){
			str += min + "分钟";
		}
		return str;
	}

	

	private static boolean isIp(String ip) {
		return ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
	}
	
	private static void buildDBObject(List<DBObject> list, String str) {
		list.add(buildDBObject(str));
	}
	
	private static void buildDBObject(List<DBObject> list, boolean check, String str, String... params) {
		if(check){
			list.add(buildDBObject(str, params));
		}
	}

	private static DBObject buildDBObject(String str) {
		try {
			str=str.replaceAll("'", "");
			str=str.replaceAll("\"", "");
			str=str.replaceAll("([A-Za-z0-9_\\.$/?&^%$#@!]+)", "\"$1\"");
			str=str.replaceAll("\"(\\d+)\"", "$1");
			log.debug(str);
			JSONObject josn = JSONObject.fromObject(str);
			DBObject dbobj = BasicDBObjectBuilder.start(josn).get();
//			log.debug(dbobj);
			return dbobj;
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return new BasicDBObject();

	}
	
	private static DBObject buildDBObject(String str, String... params) {
	
		if (params!=null) {
			for (int i = 0; i < params.length; i++) {
				str =str.replaceFirst("%"+ i, params[i]);
			}
		}
		return buildDBObject(str);
	}
	
	
	
}
*/