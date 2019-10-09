package com.world.data.mysql.json;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.SQLParamHelper;

public class FastJsonHelper extends SQLParamHelper {

	public static JSONArray parseDataEntityBeans(ResultSet rs) throws Exception {

		JSONArray listResult = new JSONArray();
		ResultSetMetaData meta = rs.getMetaData();
		int m_Cols = meta.getColumnCount();
		while (rs.next()) {
			JSONObject list = new JSONObject();
			for (int i = 1; i <= m_Cols; i++) {
				int t = meta.getColumnType(i);
				String colName = meta.getColumnName(i);
				if (Types.TINYINT == t) {
					Object obj = rs.getObject(i);
					if (obj == null || Integer.parseInt(obj.toString()) == 0) {
						list.put(colName, Boolean.FALSE);
					} else {
						list.put(colName, Boolean.TRUE);
					}
				} else {
					Object obj = rs.getObject(i);
					list.put(colName, obj);
				}
			}
			listResult.add(list);
		}

		return listResult;

	}
}
