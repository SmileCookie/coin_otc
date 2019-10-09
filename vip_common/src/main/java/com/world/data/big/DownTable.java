package com.world.data.big;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.world.data.big.MysqlDownTable.SrcTableAndTargetDatabase;
import com.world.data.big.table.TableInfo;

/***
 * 》》》本系统设计的分表系统是基于主表（保存近段时间的数据,并用来参与新数据的添加）   过期后将其分表到其他的（兄弟表）中，此步骤为异步进行同步的《《《《
 * 	   1.主表用来保存用户最近的数据，并提供条件数据快速查询，满足当前业务需求.
 *     2.分表的数据一般都是有条件的，比如说交易的数据分表里面不会存在状态为：（未成交）的数据记录.
 * 
 * 1.取模分表
 * 2.根据时间维度进行分表
 * 3.自定义的Hash
 * 
 * 拆分表
 * 
 * @author apple
 * T为分库标准的类型  如依赖用户id   类型为Integer
 */
public abstract class DownTable {
	protected static Logger log = Logger.getLogger(DownTable.class.getName());
	

	public static String getTableName(TableInfo tableInfo , Object fieldValue){
		if(tableInfo.shardNum() <= 1){

			return tableInfo.tableName() + "_all";
		}
		
		if(fieldValue == null){
			return tableInfo.tableName();
		}
        long identify = Long.parseLong(fieldValue.toString());
        long tag = identify % tableInfo.shardNum();
        return tableInfo.tableName() + "_" + tag;
	}
	
	public static String[] tablesName(TableInfo tableInfo){
		String[] databases = tableInfo.databases();
		
		String[] targetDatabases = tableInfo.targetDatabases();
		boolean isSame = true;
		if(databases != null && databases.length > 0){
			for(int i = 0;i < databases.length; i++){
				SrcTableAndTargetDatabase statd = new SrcTableAndTargetDatabase();
				statd.srcDatabase = databases[i];
				
				if(targetDatabases.length > 0){
					statd.targetDatabase = targetDatabases[i];
				}else{
					statd.targetDatabase = databases[i];
				}
				
				if(!statd.srcDatabase.equals(statd.targetDatabase)){
					isSame = false;
				}
			}
		}
		
		if(tableInfo.shardNum() <= 1){
			if(isSame){
				return new String[]{tableInfo.tableName() + "_all"};
			}else{
				return new String[]{tableInfo.tableName()};
			}
		}
		
		String[] tables = new String[tableInfo.shardNum()];
		for(int i = 0;i<tableInfo.shardNum();i++){
			tables[i] = tableInfo.tableName() + "_" + i;
		}
		return tables;
	}

}
