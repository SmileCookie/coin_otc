package com.world.util;

import java.sql.Timestamp;
import java.util.Map;

/**
 * <p>标题: 公共方法调用类</p>
 * <p>描述: 此类里面的方法不允许为static</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class CommonUtilNoStatic {
	
	/**
	 * 拼接SQL语句:where 和 and 组合,数据库中字段为int
	 * @param sqlWhere 拼接在sql语句之后
	 * @param mapHasWhere 是否已经有where如果有则拼接and
	 * @param sqlColumn 数据库中的字段
	 * @param intField 类中定义的字段
	 * @param operSign 运算符号 = > >= < <=
	 * @return
	 */
	public String giveSqlWhereByInt(String sqlWhere, Map<String, Boolean> mapHasWhere, String sqlColumn, int intField, String operSign) {
		if(mapHasWhere.get("hasWhere")) {
			sqlWhere += "and " + sqlColumn + " " + operSign + " " + intField + " ";
		} else {
			sqlWhere += "where " + sqlColumn + " " + operSign + " " + intField + " ";
			mapHasWhere.put("hasWhere", true);
		}
		return sqlWhere;
	}
	
	/**
	 * 拼接SQL语句:where 和 and 组合,数据库中字段为str
	 * @param sqlWhere 拼接在sql语句之后
	 * @param mapHasWhere 是否已经有where如果有则拼接and
	 * @param sqlColumn 数据库中的字段
	 * @param strField 类中定义的字段
	 * @param operSign 运算符号 = > >= < <= like
	 * @return
	 */
	public String giveSqlWhereByStr(String sqlWhere, Map<String, Boolean> mapHasWhere, String sqlColumn, String strField, String operSign) {
		if("like".equals(operSign)) {
			strField = "%" + strField + "%";
		}
		if(mapHasWhere.get("hasWhere")) {
			sqlWhere += "and " + sqlColumn + " " + operSign + " '" + strField + "' ";
		} else {
			sqlWhere += "where " + sqlColumn + " " + operSign + " '" + strField + "' ";
			mapHasWhere.put("hasWhere", true);
		}
		return sqlWhere;
	}
	
	/**
	 * 拼接SQL语句:where 和 and 组合,数据库中字段为time
	 * @param sqlWhere 拼接在sql语句之后
	 * @param mapHasWhere 是否已经有where如果有则拼接and
	 * @param sqlColumn 数据库中的字段
	 * @param timeField 类中定义的字段
	 * @param operSign 运算符号 = > >= < <=
	 * @return
	 */
	public String giveSqlWhereByTime(String sqlWhere, Map<String, Boolean> mapHasWhere, String sqlColumn, Timestamp timeField, String operSign) {
		if(mapHasWhere.get("hasWhere")) {
			sqlWhere += "and " + sqlColumn + " " + operSign + " '" + timeField + "' ";
		} else {
			sqlWhere += "where " + sqlColumn + " " + operSign + " '" + timeField + "' ";
			mapHasWhere.put("hasWhere", true);
		}
		return sqlWhere;
	}
	
}
