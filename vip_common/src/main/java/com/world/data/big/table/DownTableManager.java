package com.world.data.big.table;
import com.world.config.GlobalConfig;
import com.world.data.big.MysqlDownTable;
import com.world.data.big.MysqlDownTable.DownSql;
import com.world.data.mysql.QueryDataType;

public class DownTableManager{
	
	public static MysqlDownTable mysqlDownTable = null;
	
	private synchronized static void init(){
		if(mysqlDownTable == null){
			mysqlDownTable = MysqlDownTable.getMysqlDownTable();
		}
	}
	
	public static String getProxySql(String sql, QueryDataType queryDataType , Object... params){
		
		if(GlobalConfig.openDownTable){
			if(mysqlDownTable == null){
				init();
			}
			DownSql downSql = mysqlDownTable.getAfterProxySql(sql , queryDataType , params);
			if(downSql != null){
				return downSql.dealedSql;
			}else{
				return sql;
			}
		}
		return sql;
	}
	
	public static DownSql getProxyDownSql(String sql , QueryDataType queryDataType , Object... params){
		if(mysqlDownTable == null){
			init();
		}
		
		return mysqlDownTable.getAfterProxySql(sql ,queryDataType, params);
	}
	
//	public static String getTableName(String sql , Object... params){
//		if(mysqlDownTable == null){
//			init();
//		}
//		return mysqlDownTable.getSrcTableNameByNoStandardSql(sql);
//	}
}
