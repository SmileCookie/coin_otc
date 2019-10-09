package com.world.data.big;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.config.GlobalConfig;
import com.world.controller.Base;
import com.world.data.big.table.TableInfo;
import com.world.data.big.table.UpdateWay;
import com.world.data.mysql.Bean;
import com.world.data.mysql.ConnectionProp;
import com.world.data.mysql.CrudeWay;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.QueryDataType;
import com.world.data.mysql.connection;
import com.world.data.mysql.bean.BeanProxy;
import com.world.data.mysql.json.FastJsonData;
import com.world.model.BeanTag;
import com.world.model.dao.task.TaskFactory;
import com.world.model.dao.task.Worker;
import com.world.util.path.FindClass;

/***
 * mysql分表
 */
public class MysqlDownTable extends DownTable{
	
	private MysqlDownTable(){}
	
	static MysqlDownTable mysqlDownTable = null;
	
	public static MysqlDownTable getMysqlDownTable() {
		if(mysqlDownTable == null){
			synchronized (MysqlDownTable.class) {
				if(mysqlDownTable == null){
					mysqlDownTable = new MysqlDownTable();
				}
			}
		}
		return mysqlDownTable;
	}

	private final static String BLANK = " ";
	private final static String EQUALS = "=";
	private static Map<String , BeanProxy> beanProxys = new HashMap<String, BeanProxy>();
	private static List<BeanProxy> downProxys = new ArrayList<BeanProxy>();
	public static boolean init = false;
	
	static{
		if(GlobalConfig.openDownTable){
			final ExecutorService es = Executors.newSingleThreadExecutor();
			es.execute(new Runnable() {
				public void run() { 
					while(true){
						if(Base.systemStarted){
							init();
							if(judgeDownTable()){
								es.shutdown();
								break;
							}else{
								
							}
						}
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							log.error(e.toString(), e);
						}
					}
				}
			});
		}
	}
	
	/***
	 * 初始化
	 */
	public synchronized static void init() {
			if(init){
				return;
			}
			log.info("------------------------初始化分表----------------------");
			Set<Class<?>> classes = FindClass.getClasses(BeanTag.class.getPackage());
			Iterator<Class<?>> it = classes.iterator();

			while (it.hasNext()) {
				Class<?> cs = it.next();

				if(cs.getSuperclass() == Bean.class){
					TableInfo tableInfo = cs.getAnnotation(TableInfo.class);
					Class<?> downTableFieldType = null;
					if(tableInfo != null){
						try {
							downTableFieldType = cs.getDeclaredField(tableInfo.field()).getClass();
						} catch (NoSuchFieldException e) {
							log.error(e.toString(), e);
						} catch (SecurityException e) {
							log.error(e.toString(), e);
						}
					}
					BeanProxy bp = new BeanProxy(cs.getName() , cs , tableInfo , downTableFieldType);

					if(tableInfo != null && tableInfo.tableDown()){
						downProxys.add(bp);
						beanProxys.put(tableInfo.tableName().toLowerCase(), bp);
					}

				}
			}

			init = true;
			if(!GlobalConfig.openDownTable){
				return;
			}
			//judgeDownTable();
	}
	
	/***
	 * 判断拆分表是否存在不存在就创建
	 */
	public synchronized static boolean judgeDownTable() {
		if(!init){
			log.info("MysqlDownTable未初始化..........");
			return false;
		}
		if(downProxys.size() > 0){
			for(BeanProxy bp : downProxys){
				bp.tablesName = tablesName(bp.tableInfo);
				
				String[] databases = bp.tableInfo.databases();
				
				String[] targetDatabases = bp.tableInfo.targetDatabases();
				
				
				if(databases != null && databases.length > 0){
					List<SrcTableAndTargetDatabase> tdatabases = new ArrayList<SrcTableAndTargetDatabase>();
					
					for(int i = 0;i < databases.length; i++){
						SrcTableAndTargetDatabase statd = new SrcTableAndTargetDatabase();
						statd.srcDatabase = databases[i];
						
						if(targetDatabases.length > 0){
							statd.targetDatabase = targetDatabases[i];
						}else{
							statd.targetDatabase = databases[i];
						}
						tdatabases.add(statd);
					}
					
					
					for(SrcTableAndTargetDatabase sdatabase : tdatabases){
						ConnectionProp targetCp = connection.getProp(sdatabase.targetDatabase);
						ConnectionProp srcCp = connection.getProp(sdatabase.srcDatabase);
						if(srcCp != null && targetCp != null){
							for(String table : bp.tablesName){
								//create table if not exists entrust_all like entrust;
								String sql = "create table if not exists " + table + " like " + bp.tableInfo.tableName();
								
								if(!sdatabase.isSame()){
									String createSql = "show create table " + bp.tableInfo.tableName();
									List ress = (List)Data.GetOne(srcCp.dbName, createSql, new Object[]{});
									
									if(ress != null){
										String srcCreateSql = (String) ress.get(1);
										String createTable = "CREATE TABLE `"+bp.tableInfo.tableName().toLowerCase()+"`";
										if(srcCreateSql.startsWith(createTable)){
											sql = srcCreateSql.replace(createTable, "CREATE TABLE IF NOT EXISTS `"+table+"`");
										}
									}
								}
								
								try {
									connection.GetConnection(srcCp.dbName);
									connection.GetConnection(targetCp.dbName);
									if(targetCp.isOk){
										Data.Update(targetCp.dbName ,sql , false , null);
									}else{
										connection.GetConnection(srcCp.dbName);
										log.error(targetCp.dbName + "等待初始化完成[ids]...............");
										return false;
									}
								} catch (Exception e) {
									log.error(e.toString(), e);
								}
							}
						}
					}
					////启动异步同步线程
					dataSync(bp);
				}
			}
			
		}
		return true;
	}
	
	@SuppressWarnings("serial")
	public static void dataSync(final BeanProxy bp){
		final TableInfo tableInfo = bp.tableInfo;
		
		if(tableInfo != null && tableInfo.updateWay() == UpdateWay.ASYNC){
			int i = 0;
			final int targetLength = tableInfo.targetDatabases().length;
			for(final String database : tableInfo.databases()){
				ConnectionProp cp = connection.getProp(database);
				if(cp == null){
					continue;
				}
				
				String targetDatabase = database;
				if(targetLength > 0){
					if(targetLength > i){
						targetDatabase = tableInfo.targetDatabases()[i];
					}
				}
				
				final String td = targetDatabase;
				
				TaskFactory.work(new Worker(bp.tableInfo.tableName() , bp.tableInfo.tableName() , true){
					@Override
					public void run() {
						super.run();
						log.info("开启定时处理任务:" + tableInfo.tableName() + ",database:" + database);
						
						
						doTransfer(bp , database , td);
					}
				}, tableInfo.asyncFrequency() * 1000 , true);
				i++;
			}
		}
	}
	
	/***
	 * 处理数据同步
	 * 
	 *  1、跨服务器复制表中数据
		insert into openrowset('sqloledb','localhost';'sa';'123',Test.dbo.Table_B) 
		select * from Test.dbo.Table_A 
		//启用Ad Hoc Distributed Queries：
		exec sp_configure 'show advanced options',1
		reconfigure
		exec sp_configure 'Ad Hoc Distributed Queries',1
		reconfigure  www.2cto.com  
		 
		//使用完成后，关闭Ad Hoc Distributed Queries：
		exec sp_configure 'Ad Hoc Distributed Queries',0
		reconfigure
		exec sp_configure 'show advanced options',0
		reconfigure
	 * 
	 * 
	 * @param tableInfo
	 */
	public static void doTransfer(BeanProxy bp , String database , String targetDatabase){
		ConnectionProp cp = connection.getProp(database);
		ConnectionProp tcp = connection.getProp(targetDatabase);
		
		SrcTableAndTargetDatabase statd = new SrcTableAndTargetDatabase();
		statd.srcDatabase = database;
		statd.targetDatabase = targetDatabase;
		
		if(cp == null){
			log.error("源数据库未初始化，无法继续...");
			return;
		}
		
		if(tcp == null){
			log.error("目标数据库未初始化，无法继续...");
			return;
		}
		
		TableInfo tableInfo = bp.tableInfo;
		
		String[] conditions = tableInfo.conditions();
		
		if(conditions.length <= 0){
			log.error(tableInfo.tableName() + ":未注明异步迁移条件");
		}
		
		
		
		for(String condition : conditions){
			String sql="select * from " + tableInfo.tableName() + " where " + condition;
			JSONArray lists = null;
			List<OneSql> sqls = null;
			boolean hasEnd = false;
			
			List<String> preparedParam = new ArrayList<String>();
			
			for(int i = 0;i<condition.length();i++){
				char curChar = condition.charAt(i);
				if(curChar == '?'){
					preparedParam.add("?");
				}
			}
			Object[] objs = new Object[preparedParam.size()];
			if(preparedParam.size() > 0){
				for(int i = 0;i<preparedParam.size(); i++){
					String mothod = tableInfo.conditionsParams()[i];
					Object obj;
					try {
						obj = bp.classType.newInstance();
						
						Method curMethod = bp.classType.getMethod(mothod);
						
						if(curMethod != null){
							Object res = curMethod.invoke(obj);
							objs[i] = res;
						}
					} catch (InstantiationException e) {
						log.error(e.toString(), e);
					} catch (IllegalAccessException e) {
						log.error(e.toString(), e);
					} catch (NoSuchMethodException e) {
						log.error(e.toString(), e);
					} catch (SecurityException e) {
						log.error(e.toString(), e);
					} catch (IllegalArgumentException e) {
						log.error(e.toString(), e);
					} catch (InvocationTargetException e) {
						log.error(e.toString(), e);
					}
				}
			}
			
			while (!hasEnd) {
				long t1 = System.currentTimeMillis();
				//@SuppressWarnings("unchecked")
				//Class<? extends Bean> clz = (Class<? extends Bean>) bp.classType;
				log.info("执行查询语句：" + sql);
				lists = FastJsonData.Query(cp.dbName , sql, objs);
				
				long t2 = System.currentTimeMillis();
				log.info("--------------查询------------------"+(t2-t1)+"ms");
				if(lists.size() == 0){
					hasEnd = true;
					break;
				}
				sqls = new ArrayList<OneSql>();
				String ids = "";
				for(int i = 0; i < lists.size(); i ++){
					String sqlDeal = "";
					JSONObject li = (JSONObject)lists.get(i);
					Object primaryKey = null;
					try {
						primaryKey = li.get(tableInfo.primaryKey());//BeanUtils.getProperty(li, tableInfo.primaryKey());
						Object shardField = li.get(tableInfo.field());//BeanUtils.getProperty(li, tableInfo.field());
						if(tableInfo.shardNum() > 1 && primaryKey != null){//分sql语句插入
							String tableName = getTableName(tableInfo , shardField);
							
							String orderLimit = orderLimit(sqlDeal);
							if(orderLimit != null){
								sqlDeal = sqlDeal.replace(orderLimit, "");
							}
							if(statd.isSame()){
								sqlDeal += "select * from " + tableInfo.tableName() + " where " + tableInfo.primaryKey() + "=" + primaryKey;
								sqls.add(new OneSql("INSERT INTO  " + tableName + " " + sqlDeal , -2, new Object[]{}, targetDatabase));
							}else{
								JSONArray array = new JSONArray();
								array.add(li);
								sqls.addAll(FastJsonData.getTargetSqls(targetDatabase, tableName, array));
							}
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
					
					if(primaryKey != null){
						if(i < lists.size() - 1){
							ids += primaryKey.toString() + ",";
						}else{
							ids += primaryKey.toString();
						}
					}
				}
				
				if(tableInfo.shardNum() <= 1){
					if(statd.isSame()){
						sqls.add(new OneSql("INSERT IGNORE INTO " + tableInfo.tableName() + "_all "+"select * from " + tableInfo.tableName() + " where "+tableInfo.primaryKey()+" IN ("+ids+")", -2, new Object[]{}, targetDatabase));
					}else{
						sqls = FastJsonData.getTargetSqls(targetDatabase, tableInfo.tableName(), lists);
					}
				}
				
				sqls.add(new OneSql("DELETE FROM "+tableInfo.tableName()+" WHERE "+tableInfo.primaryKey()+" IN ("+ids+")", -2, null , database));
				
				
				if(Data.doTransWithBatch(sqls)){
					long t3 = System.currentTimeMillis();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						log.error(e.toString(), e);
					}
					log.info("--------------执行------------------"+(t3-t1)+"ms");
					log.info("成功转移" +lists.size() + "条数据到新表中。休息50ms...");
				}else{
					log.error("处理数据出错。休息1分钟...");
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						log.error(e.toString(), e);
					}
				}
			}
		}
		
		
	}
	
	public static BeanProxy getProxy(String name){
		if(!init){
			init();
		}
		return beanProxys.get(name);
	}
	
	public static String getStandardSql(String srcSql){
		String[] strs = srcSql.split(BLANK);
		StringBuffer newSql = new StringBuffer();
		boolean beforeIsEqual = false;
		boolean isFirst = true;
		for(String stt : strs){
			if(!stt.equals("")){
				if(!stt.equals(EQUALS)){
					if(!stt.contains("'")){//把除了值外的字符转化为小写  包括关键字和表明 、 字段名
						stt = stt.toLowerCase();
					}
					if(!beforeIsEqual){
						if(!isFirst){//第一个不用加空格
							newSql.append(BLANK);
						}else{
							isFirst = false;
						}
						
					}else{
						beforeIsEqual = false;
					}
				}else{
					beforeIsEqual = true;
				}
				newSql.append(stt);
			}
		}
		return newSql.toString();
	}
	
	private DownSql getSrcTableNameByStandardSql(String sql){
		String tableName = null;
		CrudeWay way = null;
		try {
			String[] sqls = sql.split(BLANK);
			if(sqls[0].equalsIgnoreCase("insert")){
				tableName = sqls[2];
				int ind = tableName.indexOf("(");
				if(ind > 0){
					tableName = tableName.substring(0 , ind);
				}
				way = CrudeWay.INSERT;
			}else if(sqls[0].equalsIgnoreCase("select")){
				int tableNameIndex = 0;
				for(int i = 0;i<sqls.length;i++){
					if(sqls[i].equals("from")){
						tableNameIndex = i + 1;
						break;
					}
				}
				
				if(tableNameIndex == 0){
					log.debug("没有找到表名,非表查询sql：" + sql);
				}else{
					tableName = sqls[tableNameIndex];
				}
				way = CrudeWay.SELECT;
			}else if(sqls[0].equalsIgnoreCase("update")){
				tableName = sqls[1];
				way = CrudeWay.UPDATE;
			}else if(sqls[0].equalsIgnoreCase("delete")){
				tableName = sqls[1];
				way = CrudeWay.DELETE;
			}else{
				tableName = sqls[1];
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		DownSql downSql = null;
		if(tableName != null){
			downSql = new DownSql(sql , tableName , way);
		}
		return downSql;
	}
	
	/***
	 * 获取分表字段的值
	 * @param tableInfo
	 * @param sql
	 * @param params
	 * @return  如果sql不存在分表字段则返回空值
	 */
	private Object getValue(DownSql downSql){
		
		String tag = downSql.tableInfo.field().toLowerCase() + "=";
		Object fieldValue = null;
		int fieldIndex = downSql.stardardSql.indexOf(tag);
		
		if(fieldIndex > 0){
			// FIXME: 2017/8/17 suxinjie 字段转化为小写
			if(downSql.stardardSql.indexOf(downSql.tableInfo.field().toLowerCase() + "=?") > 0){
				int paramIndex = downSql.stardardSql.substring(0 , fieldIndex).lastIndexOf("?");
				if(paramIndex < 0){
					paramIndex = 0;
				}
				fieldValue = downSql.params[paramIndex];
			}else{
				String esql = downSql.stardardSql.substring(fieldIndex + tag.length());
				fieldValue = Long.parseLong(esql.split(BLANK)[0]);
			}
		}
		return fieldValue;
	}
	
	private void tableNameDeal(DownSql downSql){
		String tableName = downSql.tableInfo.tableName();//getSrcTableNameByStandardSql(sql);
		Object fieldValue = getValue(downSql);
		String sql = downSql.stardardSql;
		if(fieldValue != null){
			sql = sql.replace(BLANK + tableName + BLANK , BLANK + getTableName(downSql.tableInfo , fieldValue) + BLANK);
		}else{
			//不存在分表字段
			if(downSql.way == CrudeWay.SELECT){
				downSql.tables = tablesName(downSql.tableInfo);
				int tableLength = downSql.tables.length;
				if(tableLength > 1){
					//union
					StringBuffer sbf = new StringBuffer();
					String orderLimit = orderLimit(sql);
					if(orderLimit != null){
						sql = sql.replace(orderLimit, "");
					}
					
					
					Object[] newParams = null;
					if(sql.contains("?")){
						newParams = new Object[(tableLength + 1) * downSql.params.length];
						int i = 0;
						for(int j = 0;j<newParams.length;j++){
							newParams[j] = downSql.params[i];
							i++;
							if(i == downSql.params.length){
								 i = 0;
							}
						}
						
						downSql.params = newParams;
					}
					
					sbf.append("(");
					sbf.append(sql);
					sbf.append(")");
					//
					for(int i=0;i<tableLength;i++){
						sbf.append(" union all ");
						sbf.append("(");
						sbf.append(sql.replace(BLANK + tableName + BLANK , BLANK + downSql.tables[i] + BLANK));
						sbf.append(")");
					}
					if(orderLimit != null){
						sbf.append(" " + orderLimit);
					}
					
					sql = sbf.toString();
				}
			}
		}
		downSql.dealedSql = sql;
	}
	
	/**
	 * 看word是否在lineText中存在，支持正则表达式
	 * @param lineText
	 * @param word
	 * @return
	 */
	private static boolean isContains(String lineText,String word){
	  Pattern pattern=Pattern.compile(word,Pattern.CASE_INSENSITIVE);
	  Matcher matcher=pattern.matcher(lineText);
	  return matcher.find();
	}
	
	public static String orderLimit(String sql){
	    String regex="";  
	   
	    if(isContains(sql , "order\\s+by")){
	    	// 包括order by，有分组字段
	    	regex="(order\\s+by)(.+)($)";   
	    	return "order by" + getMatchedString(regex,sql);
	    }else{
	    	if(isContains(sql , "limit\\s")){
	    		regex="(limit\\s)(.+)($)";
	    		return "limit " + getMatchedString(regex,sql);
	    	}else{
	    		// 不包括GroupBy则分组字段无从谈起，返回即可
		    	return null;
	    	}
	    }
	}
	
	/**
	 * 从文本text中找到regex首次匹配的字符串，不区分大小写
	 * @param regex： 正则表达式
	 * @param text：欲查找的字符串
	 * @return regex首次匹配的字符串，如未匹配返回空
	 */
	private static String getMatchedString(String regex,String text){
	  Pattern pattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
	   
	    Matcher matcher=pattern.matcher(text);

	    while(matcher.find()){
	      return matcher.group(2);
	    }
	   
	    return null;
	}
	
	
	private DownSql getTableInfoBySql(String sql){
		DownSql downSql = getSrcTableNameByStandardSql(sql);
		
		return getTableInfoByTableName(downSql);
	}
	
	private DownSql getTableInfoByTableName(DownSql downSql){
		if(downSql != null){
			BeanProxy bp = getProxy(downSql.tableName);
			if(bp != null){
				downSql.tableInfo = bp.tableInfo;
			}else{
				downSql = null;
			}
		}
		return downSql;
	}
	
	
	/***
	 * 获取分表代理后的sql
	 * @return
	 */
	public DownSql getAfterProxySql(String srcSql , QueryDataType queryDataType , Object[] params){
		String standardSql = getStandardSql(srcSql);
		DownSql downSql = getTableInfoBySql(standardSql);
		
		if(downSql != null){//同步方式
			if(downSql.tableInfo.updateWay() == UpdateWay.SYNC){
				downSql.params = params;
				tableNameDeal(downSql);
			}else if(downSql.tableInfo.updateWay() == UpdateWay.ASYNC){
				downSql.params = params;
				if(queryDataType == QueryDataType.DOWN){
					tableNameDeal(downSql);
				}else{
					downSql.dealedSql = downSql.stardardSql;
				}
			}
		}
		return downSql;
	}
	public class DownSql{
		public DownSql(String stardardSql , String tableName , CrudeWay way) {
			this.stardardSql = stardardSql;
			this.tableName = tableName;
			this.way = way;
		}
		public String tableName;
		public String dealedSql;
		public TableInfo tableInfo;
		public String stardardSql;
		public Object[] params;
		public CrudeWay way;
		public String[] tables;
	}
	
	static public class SrcTableAndTargetDatabase{
		public String srcDatabase;
		public String targetDatabase;
		
		public boolean isSame(){
			if(srcDatabase.equals(targetDatabase)){
				return true;
			}
			return false;
		}
	}
}


