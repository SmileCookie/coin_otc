/*package com.world.controller.admin.log.chart;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.defense.AccessLogDao;
import com.world.web.defense.statistics.AccessStatistics;
import com.world.web.defense.statistics.AccessStatisticsDao;

@SuppressWarnings("unused")
@FunctionAction(jspPath = "/admins/log/" , des="全站访问统计")
public class Index extends AdminAction{
	
	private static final int Min = 60 * 1000;
    AccessLogDao  dao = new AccessLogDao();
    AccessStatisticsDao asDao = new AccessStatisticsDao();
	
	@Page(Viewer = "/admins/log/siteVisit.ftl")
	public void index(){
		
	}
	
	
	// ajax的调用
	@Page(Viewer = "/admins/log/siteVisit.ftl")
	public void ajax() {
		index();
	}
	
	@Page(Viewer = JSON)
	public void getSiteVisitChart() {
		log.debug("统计全站访问量:");
//		AggregationOutput out = dao.getCollection().aggregate(
//				buildDBObject("{$group: {_id:{minuteFirst:'$minuteFirst'},count:{$sum:1} }}"),
//				buildDBObject("{$sort:{_id:1}}"),
//				buildDBObject("{$project:{_id:0,minuteFirst:'$_id.minuteFirst',count:1}}")
//		);
//		CommandResult comrs = out.getCommandResult();
//		BasicDBList rs= (BasicDBList) comrs.get("result");
////		printCoseTime("统计全站访问量","ms");
//		
//		
//		if(rs.size()>0){
//			long ftime = ((BasicDBObject)rs.get(0)).getLong("minuteFirst");
//			long ltime = TimeUtil.getMinuteFirst().getTime();
//			HashMap<Long, Integer> map = new LinkedHashMap<Long, Integer>();
//			
//			//init
//			for (long i = ftime; i <= ltime; i+=Min) {
//				map.put(i, 0);
//			}
//			
//			//set value
//			for (Object item : rs) {
//				BasicDBObject obj = (BasicDBObject) item;
//				long minuteFirst = obj.getLong("minuteFirst");
//				int count =obj.getInt("count");
//				map.put(minuteFirst, count);
//			}
//			Object[][] datas = new Object[map.size()][2];
//			Object[] keys = map.keySet().toArray();
//			Object[] values = map.values().toArray();
//			for (int i = 0; i < map.size(); i++) {
//				datas[i][0]=keys[i];
//				datas[i][1]=values[i];
//			}
//			json("", true,  Arrays.deepToString(datas));
//		}else{
//			json("错误", false,  "empty!");
//		}
		
		List<AccessStatistics> lists = asDao.findPage(asDao.getQuery().order("-_id"), 1, 1440);
		int size = lists.size();
		if(size>0){ 
			Object[][] datas = new Object[size][2];
			
			for (int i = 0; i < size; i++) {
				AccessStatistics as = lists.get(size - i - 1);
				
				datas[i][0] = as.get_id();
				datas[i][1] = as.getTimes();
			}
			
			json("", true,  Arrays.deepToString(datas));
		}else{
			json("错误", false,  "empty!");
		}
		
		
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
//			Map map = mapper.readValue(str, Map.class);
			JSONObject jb = JSONObject.fromObject(str);
			DBObject dbobj = BasicDBObjectBuilder.start(jb).get();
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