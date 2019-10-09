package com.world.data.mysql.json;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.data.big.table.DownTableManager;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.SQLParamHelper;
import com.world.data.mysql.connection;

public class FastJsonData extends Data{
	/**
	 * 功能:执行一个查询,第一行就是实际数据
	 * 
	 * @param connGroupName
	 *            链接名称
	 * @param progrom
	 *            程序名称或者sql语句
	 * @param param
	 *            参数数组
	 * @return List<List<Object>>
	 */
	public static JSONArray Query(String connGroupName, String progrom, Object[] param) {
		JSONArray rtn = new JSONArray();
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			conn = connection.GetConnection(connGroupName, false);
			log.debug(SQLParamHelper.GetPrama(param, progrom));
			progrom = DownTableManager.getProxySql(progrom, null , param);
			statement = conn.prepareStatement(progrom);// 初始化statemaent
			// 设置参数
			SQLParamHelper.JavaParam2SQLParam(param, statement);

			// 执行查询
			// log.info("执行查询:"+progrom);
			resultset = statement.executeQuery();
			rtn = FastJsonHelper.parseDataEntityBeans(resultset);
			resultset.close();
			statement.close();
			conn.close();
			conn = null;
		} catch (Exception ex) {
			log.error("sql:" + progrom, ex);
		} finally {
			try {
				if (resultset != null) {
					resultset.close();
					resultset = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}

		return rtn;
	}
	
	/**
	 * 迁移不同源的数据库
	 * @param fromDatabase  源数据库
	 * @param toDatabase  目标数据库
	 * @param tableName 表名
	 * @param sql 迁移的sql
	 * @return
	 */
	public static List<OneSql> getTargetSqls(String toDatabase , String tableName , JSONArray lists){
		List<OneSql> sqls = new ArrayList<OneSql>();
		if(lists.size() > 0){
			for(Object o : lists){
				JSONObject jo = (JSONObject)o;
				Set<String> keys = jo.keySet();
				
				String ps = "";
				String fields = "";
				List<Object> params = new ArrayList<Object>();
				
				for(String key : keys){
					Object v = jo.get(key);
//					if(v != null){
						fields += "," + key;
						ps += ",?";
						params.add(v);
//					}
				}
				
				if(fields.length() > 0){
					String insertSql = "insert into "+tableName+"("+fields.substring(1)+") values ("+ps.substring(1)+")";
					sqls.add(new OneSql(insertSql , -1 , params.toArray() , toDatabase , true));//添加批处理
				}
			}
		}
		return sqls;
	}
}
