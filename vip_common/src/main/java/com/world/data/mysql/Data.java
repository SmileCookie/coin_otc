package com.world.data.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.Response;
import com.world.config.GlobalConfig;
import com.world.data.big.MysqlDownTable.DownSql;
import com.world.data.big.table.DownTableManager;
import com.world.util.ClassUtil;
import com.world.util.callback.CallbackMethod;

/**
 * 功能:数据服务主类
 * 
 * @author Administrator
 * 
 */
public class Data {
	public static Logger log = Logger.getLogger(Data.class);
	static boolean isDebug = true;

	/**
	 * 功能:根据sql或者预设置程序执行返回一个xml,如果是sql,将会执行默认的sql
	 * 
	 * @param progrom
	 *            sql或者程序
	 * @param param
	 *            参数集合
	 * @return String 返回xml数据的字符串形式,不包含xml头
	 */
	public static String GetXml(String progrom, Object[] param) {
		String rtn = null;

		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;

		try {
			// 当成一个可以执行的sql来执行
			conn = connection.GetConnection(connection.groupName_static, false);
			statement = conn.prepareStatement(progrom);
			// 设置参数
			SQLParamHelper.JavaParam2SQLParam(param, statement);

			// 执行查询
			resultset = statement.executeQuery();
			rtn = SQLParamHelper.parseResultsetToXml(resultset);

			resultset.close();
			statement.close();
			conn.close();
			conn = null;
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			// log.info("鑾峰彇瀵硅薄鍒楄〃鍙戠敓閿欒!");
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
			}
		}

		return rtn;
	}

	/**
	 * 功能:根据sql或者预设置程序执行返回一个xml,如果是sql,将会执行默认的sql
	 * 
	 * @param progrom
	 *            sql或者程序
	 * @param param
	 *            参数集合
	 * @return String 返回xml数据的字符串形式,不包含xml头
	 */
	public static String GetXml(String connGroupName, String progrom,
			Object[] param) {
		return GetXml(progrom, param);
	}

	/**
	 * 功能:用默认的链接执行一个查询,第一行就是实际数据
	 * 
	 * @param connGroupName
	 *            链接名称
	 * @param progrom
	 *            程序名称或者sql语句
	 * @param param
	 *            参数数组
	 * @return List<List<Object>>
	 */
	public static List Query(String sql, Object[] param) {
		return Query(connection.groupName_static, sql, param);
	}

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
	public static List<Object> Query(String connGroupName, String progrom, Object[] param) {
		List<Object> rtn = new ArrayList<Object>();
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
//			log.info("[执行统计查询] 执行SQL: "+progrom);
			resultset = statement.executeQuery();
			rtn = SQLParamHelper.parseDataEntityBeans(resultset);
			resultset.close();
			statement.close();
			conn.close();
			conn = null;
		} catch (Exception ex) {
			log.error("groupName: " + connGroupName + ", sql:" + progrom, ex);
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
			}
		}

		return rtn;
	}

	/**
	 * 功能:执行一个查询,每行都是一个bean的对象,既可以是实际的程序也可以是sql程序
	 * 本查询是根据指定连接名称和程序以及参数来查询,返回结果是bean集合,如果progrom不存在系统配置就认为是一个sql直接语句
	 * 
	 * @param connGroupName
	 *            链接名称
	 * @param progrom
	 *            程序名称或者sql语句
	 * @param param
	 *            参数数组
	 * @param bean
	 *            名称
	 * @return List<bean>
	 */
	public static List<Bean> Query(String progrom, Object[] param, Class bean) {

		return Query(connection.groupName_static, progrom, param, bean);
	}

	/**
	 * 功能:使用默认的连接,携带表头部的查询
	 * 
	 * @param connGroupName
	 *            链接名称
	 * @param progrom
	 *            sql语句或者预设值程序
	 * @param param
	 *            object 参数列表
	 * @return List<List<Object>>
	 */
	public static List QueryWithName(String progrom, Object[] param) {
		return QueryWithName(connection.groupName_static, progrom, param);
	}

	/**
	 * 功能:携带表头部的查询
	 * 
	 * @param connGroupName
	 *            链接名称
	 * @param progrom
	 *            sql语句或者预设值程序
	 * @param param
	 *            object 参数列表
	 * @return List<List<Object>>
	 */
	public static List QueryWithName(String connGroupName, String progrom,
			Object[] param) {
		List rtn = new ArrayList();
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;

		try {
			conn = connection.GetConnection(connGroupName, false);

			if (isDebug) {
				log.debug(SQLParamHelper.GetPrama(param, progrom));
			}
			statement = conn.prepareStatement(progrom);
			// 设置参数
			SQLParamHelper.JavaParam2SQLParam(param, statement);

			// 执行查询
			resultset = statement.executeQuery();
			rtn = SQLParamHelper.parseDataEntityBeansNames(resultset);
			resultset.close();
			statement.close();
			conn.close();
			conn = null;
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
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
			}
		}

		return rtn;
	}

	/**
	 * 功能:用默认的链接,根据指定的连接执行一个程序或者语句,返回插入的行号或者0或者错误的时候返回-1
	 * 
	 * @param connGroupName
	 *            程序组名称
	 * @param progrom
	 *            预设置名称或者sql本身
	 * @param param
	 *            参数设置
	 * @return 插入的行号,如果没有自动增长列则会返回0,或者错误-1
	 */
	public static int Insert(String progrom, Object[] param) {
		return Insert(connection.groupName_static, progrom, param);
	}

	/**
	 * 功能:根据指定的连接执行一个程序或者语句,返回影响的行数
	 *
	 * @param connGroupName
	 *            程序组名称
	 * @param progrom
	 *            预设置名称或者sql本身
	 * @param param
	 *            参数设置
	 * @return 插入数量,如果插入成功返回1 没有成功则会返回0,或者错误-1
	 */
	public static int insertNoId(String connGroupName, String progrom, Object[] param) {
		int rtn = -1;
		Connection conn = null;
		PreparedStatement statement = null;

		try {

			if (isDebug) {
				log.debug(SQLParamHelper.GetPrama(param, progrom));
			}
			conn = connection.GetConnection(connGroupName, true);

			progrom = DownTableManager.getProxySql(progrom, null , param);

			statement = conn.prepareStatement(progrom);
			// 设置参数
			SQLParamHelper.JavaParam2SQLParam(param, statement);

			// 执行更新
			rtn = statement.executeUpdate();
			statement.close();
			conn.close();
			conn = null;
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		} finally {
			try {
				if (statement != null) {
					statement.close();
					statement = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception ex) {
			}
		}
		return rtn;
	}

	/**
	 * 功能:根据指定的连接执行一个程序或者语句,返回插入的行号或者0或者错误的时候返回-1
	 * 
	 * @param connGroupName
	 *            程序组名称
	 * @param progrom
	 *            预设置名称或者sql本身
	 * @param param
	 *            参数设置
	 * @return 插入的行号,如果没有自动增长列则会返回0,或者错误-1
	 */
	public static int Insert(String connGroupName, String progrom, Object[] param) {
		int rtn = -1;
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;

		try {

			if (isDebug) {
				log.debug(SQLParamHelper.GetPrama(param, progrom));
			}
			conn = connection.GetConnection(connGroupName, true);
			
			progrom = DownTableManager.getProxySql(progrom, null , param);
			
			statement = conn.prepareStatement(progrom);
			// 设置参数
			SQLParamHelper.JavaParam2SQLParam(param, statement);

			// 执行更新
			rtn = statement.executeUpdate();
			resultset = statement.executeQuery("SELECT LAST_INSERT_ID()");
			if (resultset.next()) {
				rtn = resultset.getInt(1);
			}
			resultset.close();
			statement.close();
			conn.close();
			conn = null;
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			// log.info("鑾峰彇瀵硅薄鍒楄〃鍙戠敓閿欒!");
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
			}
		}
		return rtn;
	}

    /**
     * 功能:根据指定的连接执行一个程序或者语句,返回插入的行号或者0或者错误的时候返回-1
     *
     * @param connGroupName
     *            程序组名称
     * @param progrom
     *            预设置名称或者sql本身
     * @param param
     *            参数设置
     * @return 插入的行号,如果没有自动增长列则会返回0,或者错误-1
     */
    public static int InsertWithoutLog(String connGroupName, String progrom, Object[] param) {
        int rtn = -1;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultset = null;

        try {

            if (isDebug) {
                log.debug(SQLParamHelper.GetPrama(param, progrom));
            }
            conn = connection.GetConnection(connGroupName, true);

            progrom = DownTableManager.getProxySql(progrom, null , param);

            statement = conn.prepareStatement(progrom);
            // 设置参数
            SQLParamHelper.JavaParam2SQLParam(param, statement);

            // 执行更新
            rtn = statement.executeUpdate();
            resultset = statement.executeQuery("SELECT LAST_INSERT_ID()");
            if (resultset.next()) {
                rtn = resultset.getInt(1);
            }
            resultset.close();
            statement.close();
            conn.close();
            conn = null;
        } catch (Exception ex) {
            log.info(ex.toString(), ex);
            // log.info("鑾峰彇瀵硅薄鍒楄〃鍙戠敓閿欒!");
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
            }
        }
        return rtn;
    }
	
	/***
	 * 如果单库存在批量插入  则对单库只允许有插入语句 ，否则执行出错
	 *
	 * @param sqls
	 */
	public static boolean doTransWithBatch(List<OneSql> workSqls) {
		boolean ret = false;
		PreparedStatement workStat = null;
		List<Connection> hasConns = null;
		try {
			// 因为是事务,所以默认是在主数据库上执行,第二个参数为true
			if (workSqls != null && workSqls.size() > 0) {
					hasConns = new ArrayList<Connection>();// 已有的连接
					Map<String, List<OneSql>> sqlsMap = new HashMap<String, List<OneSql>>();
					List<DatabaseSql> databaseSqls = new ArrayList<DatabaseSql>();
					
					for (OneSql s : workSqls) {
						List<OneSql> curSqls = sqlsMap.get(s.getDatabase());
						if (curSqls == null) {
							curSqls = new ArrayList<OneSql>();
							DatabaseSql ds = new DatabaseSql(s.getDatabase(), s.isBatch(), s.getSql() , curSqls);
							databaseSqls.add(ds);
						}
						String ns = DownTableManager.getProxySql(s.getSql(), null ,s.getPrams());
						s.setSql(ns);
						curSqls.add(s);
						sqlsMap.put(s.getDatabase(), curSqls);
					}
	
					for (DatabaseSql ds : databaseSqls) {
						Connection conn = connection.GetConnection(ds.getDatabase(), true);
						conn.setAutoCommit(false);
						List<OneSql> curConnSqls = ds.getSqls();
						hasConns.add(conn);
						
						if(ds.isBatch()){//批处理
//							long s1 = System.currentTimeMillis();
							workStat = conn.prepareStatement(ds.getBatchSql());
							
							for (OneSql s : curConnSqls) {
								if (s != null && s.getSql() != null) {
									//log.info("当前执行事务语句："+s.getSql() + "，database:" + key + "," + Thread.currentThread().getName());
									SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);
//									String param = "";
//									for(Object o : s.getPrams()){
//										param += "," + o;
//									}
//									log.info(param);
									workStat.addBatch();
								}
							}
							workStat.executeBatch();
							
//							long s2 = System.currentTimeMillis();
//							
//							log.info("插入耗时：" + (s2 - s1));
						}else{
//							long s1 = System.currentTimeMillis();
							
							for (OneSql s : curConnSqls) {
								if (s != null && s.getSql() != null) {
									//log.info("当前执行事务语句："+s.getSql() + "，database:" + key + "," + Thread.currentThread().getName());
									workStat = conn.prepareStatement(s.getSql());
									SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);
									int efRows = workStat.executeUpdate();
									if (!s.ifCanNext(efRows)) {// 是否与断定次数相等
										String ss = "";
										if (s.getPrams() != null)
											for (int i = 0; i < s.getPrams().length; i++) {
												ss += ":" + s.getPrams()[i];
											}
										log.error("以sql语句（wrok）：" + s.getSql()
												+ ",参数：" + ss + "的执行与预期行数不一，预期影响："
												+ s.getEffectRows() + "行，实际影响："
												+ efRows + "行,导致事务回滚！");
										rollDoubleTrans(workStat, hasConns, workSqls);
										return false;
									}
								}
							}
							
//							long s2 = System.currentTimeMillis();
//							
//							log.info("删除耗时：" + (s2 - s1));
						}
					}
				}
				for (Connection workConn : hasConns) {
					workConn.commit();
				}
				workStat.close();
				ret = true;
		} catch (SQLException ex) {
			// 当前出错的sql
			rollDoubleTrans(workStat, hasConns, workSqls);
			log.error(ex.toString(), ex);
		} catch (Exception ex) {
			rollDoubleTrans(workStat, hasConns, workSqls);
			log.error(ex.toString(), ex);
		} finally {
			try {
				if (workStat != null) {
					workStat.close();
					workStat = null;
				}
				closeAll(hasConns);
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}
		return ret;
	}

//	public static int batchInsert(List<OneSql> sqls) {
//		int rtn = 1;
//		Connection conn = null;
//		PreparedStatement statement = null;
//
//		try {
//			conn = connection.GetConnection(connection.groupName_static, true);
//			// 关闭事务自动提交
//			conn.setAutoCommit(false);
//
//			for (OneSql os : sqls) {
//				if (statement == null) {
//					statement = conn.prepareStatement(os.getSql());
//				}
//				// 设置参数
//				SQLParamHelper.JavaParam2SQLParam(os.getPrams(), statement);
//				statement.addBatch();
//			}
//			// 执行批量更新
//			statement.executeBatch();
//			// 语句执行完毕，提交本事务
//			conn.commit();
//			conn.setAutoCommit(true);
//			statement.close();
//			conn.close();
//			conn = null;
//		} catch (Exception ex) {
//			log.error(ex.toString(), ex);
//			return -1;
//		} finally {
//			try {
//				if (statement != null) {
//					statement.close();
//					statement = null;
//				}
//				if (conn != null) {
//					conn.setAutoCommit(true);
//					conn.close();
//					conn = null;
//				}
//			} catch (Exception ex) {
//			}
//		}
//		return rtn;
//	}

	/**
	 * 功能:使用默认的链接,执行一个更新,可以是程序也就可以直接是一个sql语句
	 * 
	 * @param connGroupName
	 *            连接池名称
	 * @param progrom
	 *            程序
	 * @param param
	 *            参数
	 * @return 影响的行数
	 */
	public static int Update(String progrom, Object[] param) {
		return Update(connection.groupName_static, progrom, true , param);
	}
	
	public static int Update(String connGroupName, String progrom , Object[] param) {
		return Update(connGroupName, progrom, true, param);
	}

	/**
	 * 功能:执行一个更新,可以是程序也就可以直接是一个sql语句
	 * 
	 * @param connGroupName 连接池名称
	 * @param progrom 程序
	 * @param proxy 是否允许代理
	 * @param param 参数
	 * @return 影响的行数
	 */
	public static int Update(String connGroupName, String progrom, boolean proxy , Object[] param) {
		int rtn = -1;
		Connection conn = null;
		PreparedStatement statement = null;

		try {
			conn = connection.GetConnection(connGroupName, true);

			if (isDebug) {
				log.debug(SQLParamHelper.GetPrama(param, progrom));
			}
			if(proxy){
				progrom = DownTableManager.getProxySql(progrom, null , param);
			}
			statement = conn.prepareStatement(progrom);// 初始化statemaent
			// 设置参数
			SQLParamHelper.JavaParam2SQLParam(param, statement);

			// 执行更新
			rtn = statement.executeUpdate();
			statement.close();
			conn.close();
			conn = null;
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		} finally {
			try {
				if (statement != null) {
					statement.close();
					statement = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception ex) {
			}
		}

		return rtn;
	}

    public static boolean doWithoutTrans(String connGroupName, List<OneSql> workSqls) {
        long startTime = System.currentTimeMillis();
        boolean ret = false;
        PreparedStatement workStat = null;
        Connection conn = null;
        try {
            conn = connection.GetConnection(connGroupName, true);
            for (OneSql s : workSqls) {
                if (s != null && s.getSql() != null) {
                    long start = System.currentTimeMillis();

                    workStat = conn.prepareStatement(s.getSql());
                    SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);

                    String sqlLog = s.getSql().replaceAll("\\?", "%s");
                    sqlLog = String.format(sqlLog, s.getPrams());

                    workStat.executeUpdate();

                    log.info("[资金处理] sql 语句：" + sqlLog + ";耗时：" + (System.currentTimeMillis() - start) + "ms");
                }
            }
            ret = true;
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        } finally {
            try {
                if (workStat != null) {
                    workStat.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                log.error(ex.toString(), ex);
            }
            log.info("[资金处理] 数据库更新耗时：" + (System.currentTimeMillis() - startTime) + " ms");
        }
        return ret;
    }


    public static boolean doWithoutTransOne(String connGroupName, OneSql s) {
        long startTime = System.currentTimeMillis();
        boolean ret = false;
        PreparedStatement workStat = null;
        Connection conn = null;
        try {
            conn = connection.GetConnection(connGroupName, true);
            if (s != null && s.getSql() != null) {
                long start = System.currentTimeMillis();

                workStat = conn.prepareStatement(s.getSql());
                SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);

                String sqlLog = s.getSql().replaceAll("\\?", "%s");
                sqlLog = String.format(sqlLog, s.getPrams());

                workStat.executeUpdate();

                log.info("[资金处理] sql 语句：" + sqlLog + ";耗时：" + (System.currentTimeMillis() - start) + "ms");
            }
            ret = true;
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        } finally {
            try {
                if (workStat != null) {
                    workStat.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                log.error(ex.toString(), ex);
            }
            log.info("[资金处理] 数据库更新耗时：" + (System.currentTimeMillis() - startTime) + " ms");
        }
        return ret;
    }

    public static boolean doWithoutTrans1(String connGroupName, List<OneSql> workSqls) {
        long startTime = System.currentTimeMillis();
        boolean ret = false;
        PreparedStatement workStat = null;
        Connection conn = null;
        try {
            conn = connection.GetConnection(connGroupName, true);
            for (OneSql s : workSqls) {
                if (s != null && s.getSql() != null) {
                    workStat = conn.prepareStatement(s.getSql());
                    SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);

                    String sqlLog = s.getSql().replaceAll("\\?", "%s");
                    sqlLog = String.format(sqlLog, s.getPrams());
                    log.error("[撮合处理] sql 语句：" + sqlLog + ";");

                    workStat.executeUpdate();
                }
            }
            ret = true;
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        } finally {
            try {
                if (workStat != null) {
                    workStat.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                log.error(ex.toString(), ex);
            }
            log.info("[资金处理] 数据库更新耗时：" + (System.currentTimeMillis() - startTime) + " ms");
        }
        return ret;
    }

	/***
	 * 事务sql，包输出
	 * 
	 * @param workSqls
	 * @param tag 输入日志的前面标识
	 * @return
	 */
	public static boolean doTrans(List<OneSql> workSqls, String tag) {
		for(OneSql sql : workSqls){
			String logStr = tag+"sql:" + sql.getSql() + " 参数：";
			for(Object obj : sql.getPrams()){
				logStr += obj+",";
			}
			logStr += " " + sql.getDatabase();
//			log.info(logStr);
		}
		return doTrans(workSqls, 0);
	}

	/***
	 * 读取超时时间
	 * 
	 * @param workSqls
	 * @param tx
	 * @return
	 */
	public static boolean doTrans(List<OneSql> workSqls) {
		return doTrans(workSqls, 0);
	}

	public static boolean doTrans(List<OneSql> workSqls, long tx) {
		return doTrans(workSqls, null, null, null, null, tx, null);
	}

	public static boolean doTrans(List<OneSql> workSqls, long tx,
			CallbackMethod[] calls) {
		return doTrans(workSqls, null, null, null, null, tx, calls);
	}

	public static boolean doTransWithHttp(List<OneSql> workSqls, Class cls,
			String method, Object[] params) {
		return doTransWithHttp(workSqls, cls, method, params, null);
	}

	public static boolean doTransWithHttp(List<OneSql> workSqls, Class cls,
			String method, Object[] params, Response response) {
		return doTrans(workSqls, cls, method, params, response, 0, null);
	}

	/****
	 * 处理带有条件的事物语句 可以包含数据库work pay 中的sql语句 预处理 严格比较影响的行数 错误时抛出异常 并进行事务回滚
	 * 此事物可以将http逻辑事物化
	 * 
	 * @param workSqls
	 * @param cls
	 *            要执行api的实体类
	 * @param method
	 *            api实体类的方法名
	 * @param params
	 *            api实体类的方法需求的参数
	 * @return
	 */
	public static boolean doTrans(final List<OneSql> workSqls, final Class<?> cls,
			final String method, final Object[] params, Response response,
			long tx, final CallbackMethod[] calls) {
		boolean ret = false;
		PreparedStatement workStat = null;
		List<Connection> hasConns = null;
		try {
			// 因为是事务,所以默认是在主数据库上执行,第二个参数为true
			if (workSqls != null && workSqls.size() > 0) {
				hasConns = new ArrayList<Connection>();// 已有的连接
				Map<String, List<OneSql>> sqlsMap = new HashMap<String, List<OneSql>>();
				for (OneSql s : workSqls) {
					List<OneSql> curSqls = sqlsMap.get(s.getDatabase());
					if (curSqls == null) {
						curSqls = new ArrayList<OneSql>();
					}
					String ns = DownTableManager.getProxySql(s.getSql(), null ,s.getPrams());
					s.setSql(ns);
					curSqls.add(s);
					sqlsMap.put(s.getDatabase(), curSqls);
				}

				Set<String> keys = sqlsMap.keySet();

				for (String key : keys) {

					Connection conn = connection.GetConnection(key, true);
					conn.setAutoCommit(false);
					List<OneSql> curConnSqls = sqlsMap.get(key);
					hasConns.add(conn);
					for (OneSql s : curConnSqls) {
						if (s != null && s.getSql() != null) {
							//log.info("当前执行事务语句："+s.getSql() + "，database:" + key + "," + Thread.currentThread().getName());
							workStat = conn.prepareStatement(s.getSql());
							SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);

							String ss = "";
							
							int efRows = -3;
							try {
								efRows = workStat.executeUpdate();
							} catch (Exception e) {
								log.info("异常信息："+e);
								for (int i = 0; i < s.getPrams().length; i++) {
									ss += "," + s.getPrams()[i];
								}
								log.error("以sql语句（" + key + "）：" + s.getSql()
										+ ",参数：" + ss + "的执行出错了，导致事务回滚！", e);
								rollDoubleTrans(workStat, hasConns, workSqls);
								return false;
							}
							if (!s.ifCanNext(efRows)) {// 是否与断定次数相等
								
								if (s.getPrams() != null)
									for (int i = 0; i < s.getPrams().length; i++) {
										ss += ":" + s.getPrams()[i];
									}
								log.error(s.getDatabase() + ",以sql语句（" + s.getDatabase() + "）：" + s.getSql()
										+ ",参数：" + ss + "的执行与预期行数不一，预期影响："
										+ s.getEffectRows() + "行，实际影响："
										+ efRows + "行,导致事务回滚！");
								rollDoubleTrans(workStat, hasConns, workSqls);
								return false;
							}
						}
					}
				}
			}

			if (hasConns != null && hasConns.size() > 0) {
				if (cls != null) {
					Object result = null;
					try {
						long start = System.currentTimeMillis();
						result = ClassUtil.invokeByClassAndMethod(cls, method,
								params);
						long end = System.currentTimeMillis();
						log.info("http()方法：" + method + "耗时："
								+ (end - start));
					} catch (Exception e) {
						result = null;
						log.error(e.toString(), e);
					}
					if (result == null) {
						log.error("http返回为空值，事物回滚。");
						rollDoubleTrans(workStat, hasConns, workSqls);
						return false;
					} else {
						Response res = (Response) result;
						if (res.taskIsFinish()) {
							// ret = true;
						} else {
							try {
								if (response != null) {
									response.setMsg(res.getMsg());
									response.setCode(res.getCode());
								}
							} catch (Exception e) {
								log.error(e.toString(), e);
							}
							log.error("http错误，事物回滚。" + res.getMsg());
							rollDoubleTrans(workStat, hasConns, workSqls);
							return false;
						}
					}
				}

				try {
					if (calls != null && calls.length > 0) {// 其他普通方法回调
						for (CallbackMethod cm : calls) {
							ClassUtil.invokeByClassAndMethod(cm.getCls(),
									cm.getMethod(), cm.getParams());
						}
					}
				} catch (Exception e) {
					log.error(e.toString(), e);
				}

				// //判断相关请求是否超时
				if ((tx > 0) && (tx <= System.currentTimeMillis())) {
					log.error("请求已经超时...");
					rollDoubleTrans(workStat, hasConns, workSqls);
					return false;
				}
				for (Connection workConn : hasConns) {
					workConn.commit();
				}
				workStat.close();
			}
			ret = true;
		} catch (SQLException ex) {
			// 当前出错的sql
			rollDoubleTrans(workStat, hasConns, workSqls);
			log.error(ex.toString(), ex);
		} catch (Exception ex) {
			log.warn(ex.toString());
			rollDoubleTrans(workStat, hasConns, workSqls);
			log.error(ex.toString(), ex);
		} finally {
			try {
				if (workStat != null) {
					workStat.close();
					workStat = null;
				}
				closeAll(hasConns);
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}
		return ret;
	}

    public static boolean doTransStatement(final List<OneSql> workSqls) {
        boolean ret = false;
        Statement workStat = null;
        List<Connection> hasConns = null;
        try {
            // 因为是事务,所以默认是在主数据库上执行,第二个参数为true
            if (workSqls != null && workSqls.size() > 0) {
                hasConns = new ArrayList<Connection>();// 已有的连接
                Map<String, List<OneSql>> sqlsMap = new HashMap<String, List<OneSql>>();
                for (OneSql s : workSqls) {
                    List<OneSql> curSqls = sqlsMap.get(s.getDatabase());
                    if (curSqls == null) {
                        curSqls = new ArrayList<OneSql>();
                    }
                    curSqls.add(s);
                    sqlsMap.put(s.getDatabase(), curSqls);
                }

                Set<String> keys = sqlsMap.keySet();

                for (String key : keys) {
                    Connection conn = connection.GetConnection(key, true);
                    conn.setAutoCommit(false);
                    List<OneSql> curConnSqls = sqlsMap.get(key);
                    hasConns.add(conn);
                    for (OneSql s : curConnSqls) {
                        if (s != null && s.getSql() != null) {
                            workStat = conn.createStatement();
                            String ss = "";
                            int efRows = -3;
                            try {
                                efRows = workStat.executeUpdate(s.getSql());
                            } catch (Exception e) {
                                for (int i = 0; i < s.getPrams().length; i++) {
                                    ss += "," + s.getPrams()[i];
                                }
                                log.error("以sql语句（" + key + "）：" + s.getSql()
                                        + ",参数：" + ss + "的执行出错了，导致事务回滚！", e);
                                rollDoubleTrans(workStat, hasConns, workSqls);
                                return false;
                            }
                            if (!s.ifCanNext(efRows)) {// 是否与断定次数相等
                                if (s.getPrams() != null)
                                    for (int i = 0; i < s.getPrams().length; i++) {
                                        ss += ":" + s.getPrams()[i];
                                    }
                                log.error(s.getDatabase() + ",以sql语句（" + s.getDatabase() + "）：" + s.getSql()
                                        + ",参数：" + ss + "的执行与预期行数不一，预期影响："
                                        + s.getEffectRows() + "行，实际影响："
                                        + efRows + "行,导致事务回滚！");
                                rollDoubleTrans(workStat, hasConns, workSqls);
                                return false;
                            }
                        }
                    }
                }
            }

            if (hasConns != null && hasConns.size() > 0) {
                for (Connection workConn : hasConns) {
                    workConn.commit();
                }
                workStat.close();
            }
            ret = true;
        } catch (SQLException ex) {
            // 当前出错的sql
            rollDoubleTrans(workStat, hasConns, workSqls);
            log.error(ex.toString(), ex);
        } catch (Exception ex) {
            log.warn(ex.toString());
            rollDoubleTrans(workStat, hasConns, workSqls);
            log.error(ex.toString(), ex);
        } finally {
            try {
                if (workStat != null) {
                    workStat.close();
                    workStat = null;
                }
                closeAll(hasConns);
            } catch (Exception ex) {
                log.error(ex.toString(), ex);
            }
        }
        return ret;
    }

	public static void closeAll(List<Connection> hasConns) {
		if (hasConns != null && hasConns.size() > 0) {
			try {
				for (Connection workConn : hasConns) {
					if (workConn != null && !workConn.isClosed()) {
						workConn.setAutoCommit(true);
						workConn.close();
						workConn = null;
					}
				}
			} catch (SQLException e) {
				log.error(e.toString(), e);
			}
		}
	}

	/****
	 * 回滚双事务
	 * 
	 * @param workStat
	 * @param hasConns
	 *            已有的连接
	 * @param payConn
	 */
	private static void rollDoubleTrans(Statement workStat,
			List<Connection> hasConns, List<OneSql> workSqls) {
		String sql = "";
		try {
			if (hasConns != null && hasConns.size() > 0) {
				for (Connection workConn : hasConns) {
					workConn.rollback();
					workConn.setAutoCommit(true);
					sql += "来源： work数据库，";
					for (OneSql os : workSqls) {
						sql += "," + os.getSql();
					}
					log.error("以语句" + sql + "开头的一个事务执行失败,事务回滚");
					workConn.close();
					workConn = null;
				}
			}
		} catch (Exception e) {
			log.error("以语句" + workSqls.get(0).getSql()
					+ "开头的一个事务执行失败,并且事务没有得到回滚,可能发生了数据不同步", e);
		}
	}

	/**
	 * 功能:用默认的连接池执行删除数据
	 * 
	 * @param connGroupName
	 *            连接组 名称
	 * @param progrom
	 *            sql语句或者程序组名称
	 * @param param
	 *            object对象
	 * @return List<List<Object>>
	 */
	public static Boolean Delete(String progrom, Object[] param) {
		return Delete(connection.groupName_static, progrom, param);
	}

	/**
	 * 功能:删除数据
	 * 
	 * @param connGroupName
	 *            连接组 名称
	 * @param progrom
	 *            sql语句或者程序组名称
	 * @param param
	 *            object对象
	 * @return List<List<Object>>
	 */
	public static Boolean Delete(String connGroupName, String progrom,
			Object[] param) {
		boolean rtn = false;
		Connection conn = null;
		PreparedStatement statement = null;

		try {
			conn = connection.GetConnection(connGroupName, true);

			if (isDebug) {
				log.debug(SQLParamHelper.GetPrama(param, progrom));
			}
			progrom = DownTableManager.getProxySql(progrom, null , param);
			statement = conn.prepareStatement(progrom);
			// 设置参数
			SQLParamHelper.JavaParam2SQLParam(param, statement);

			// 执行
			rtn = statement.execute();
			statement.close();
			conn.close();
			conn = null;
			rtn = true;
			log.info("删除操作执行成功!" + rtn);
		} catch (Exception ex) {
			log.error("删除操作发生错误!" + ex.toString(), ex);
		} finally {
			try {
				if (statement != null) {
					statement.close();
					statement = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception ex) {
			}
		}
		return rtn;
	}

	/**
	 * 功能:获取一行object的list对象
	 * 
	 * @param progrom
	 *            sql语句或者progrom对象
	 * @param param
	 *            参数
	 * @return List<Object>
	 */
	public static Object GetOne(String progrom, Object[] param) {
		return GetOne(connection.groupName_static, progrom, param);
	}

	/**
	 * 功能:获取一行object的list对象
	 * 
	 * @param progrom
	 *            sql语句或者progrom对象
	 * @atabase 连接池
	 * @param param
	 *            参数
	 * @return List<Object>
	 */
	public static Object GetOne(String database, String progrom, Object[] param) {
		List obj = Query(database, progrom, param);
		if (obj != null) {
			if (obj.size() > 0)
				return obj.get(0);
			else
				return null;
		} else
			return null;
	}

	/**
	 * 功能:获取一个bean对象,他是由操作结果集合的第一条产生的
	 * 
	 * @param progrom
	 *            程序名称或者具体的sql语句
	 * @param param
	 *            对应的参数列表
	 * @param bean
	 *            bean类型
	 * @return 具体一个Bean
	 */
	public static Bean GetOne(String progrom, Object[] param, Class bean) {
		// log.debug("--------------->内存池长度"+Netpet.Web.Data.ConfigData.connectionGroup.length);

		return GetOne(connection.groupName_static, progrom, param, bean);
	}

	/**
	 * 功能:获取一个bean对象,他是由操作结果集合的第一条产生的
	 * 
	 * @param connGroupName
	 *            联结组 默认为default,由配置产生
	 * @param progrom
	 *            程序名称或者具体的sql语句
	 * @param param
	 *            对应的参数列表
	 * @param bean
	 *            bean类型
	 * @return 具体一个Bean
	 */
	public static Bean GetOne(String connGroupName, String progrom, Object[] param, Class bean) {
		return getOneT(connGroupName, progrom, param, bean , QueryDataType.DEFAULT);
	}
	
	public static Bean GetOne(String connGroupName, String progrom, Object[] param, Class bean , QueryDataType queryDataType) {
		return getOneT(connGroupName, progrom, param, bean , queryDataType);
	}
	
	public static <T extends Bean> T GetOneT(String progrom, Object[] param, Class<T> bean) {
		return getOneT(connection.groupName_static, progrom, param, bean ,QueryDataType.DEFAULT);
	}
	
	public static <T extends Bean> T GetOneT(String connGroupName,String progrom, Object[] param, Class<T> bean) {
		return getOneT(connGroupName, progrom, param, bean ,QueryDataType.DEFAULT);
	}

	public static <T extends Bean> T getOneT(String connGroupName, String progrom, Object[] param, Class<T> bean, QueryDataType queryDataType) {
		// log.debug("--------------->内存池长度"+Netpet.Web.Data.ConfigData.connectionGroup.length);

		List<T> obj = QueryT(connGroupName, progrom, param, bean , queryDataType);
		if (obj != null) {
			if (obj.size() > 0)
				return (T) obj.get(0);
			else
				return null;
		} else
			return null;
	}
	
	public static <T extends Bean> List<Bean> Query(String connGroupName,
			String progrom, Object[] param, Class<T> bean) {
		return (List<Bean>) QueryT(connGroupName, progrom, param, bean , QueryDataType.DEFAULT);
	}

	public static <T extends Bean> List<Bean> Query(String connGroupName,
			String progrom, Object[] param, Class<T> bean , QueryDataType queryDataType) {
		return (List<Bean>) QueryT(connGroupName, progrom, param, bean , queryDataType);
	}
	
	public static <T extends Bean> List<T> QueryT(String progrom, Object[] param, Class<T> bean) {
		return QueryT(connection.groupName_static, progrom, param, bean , QueryDataType.DEFAULT);
	}

	public static <T extends Bean> List<T> QueryT(String progrom, Object[] param, Class<T> bean, QueryDataType type) {
		return QueryT(connection.groupName_static, progrom, param, bean , type);
	}
	
	public static <T extends Bean> List<T> QueryT(String connGroupName,
			String progrom, Object[] param, Class<T> bean) {
		return QueryT(connGroupName, progrom, param, bean , QueryDataType.DEFAULT);
	}

	/**
	 * 功能:执行一个查询,每行都是一个bean的对象,既可以是实际的程序也可以是sql程序
	 * 本查询是根据指定连接名称和程序以及参数来查询,返回结果是bean集合,如果progrom不存在系统配置就认为是一个sql直接语句
	 * 
	 * @param connGroupName
	 *            链接名称
	 * @param progrom
	 *            程序名称或者sql语句
	 * @param param 参数数组
	 * @param bean 名称
	 * @return List<bean>
	 */
	public static <T extends Bean> List<T> QueryT(String connGroupName,String progrom, Object[] param, Class<T> bean , QueryDataType queryDataType) {

		List<T> rtn = new ArrayList<T>();
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;

		try {
			// 准备获取连接
			conn = connection.GetConnection(connGroupName, true);// ConnectionPool.GetConnection(connGroupName,false);

			if(GlobalConfig.openDownTable){//开启分表功能
				DownSql downSql = DownTableManager.getProxyDownSql(progrom , queryDataType , param);
				if(downSql != null){
					progrom = downSql.dealedSql;
					param = downSql.params;
				}
			}
			
			//log.info(SQLParamHelper.GetPrama(param, progrom));
			
			statement = conn.prepareStatement(progrom);// 初始化statemaent
			// 设置参数
			SQLParamHelper.JavaParam2SQLParam(param, statement);

			// 执行查询
			// log.debug("准备执行查询！");
			// log.info("执行查询:"+progrom);
			resultset = statement.executeQuery();
			// rtn=SQLParamHelper.parseDataEntityBeans(resultset);
			rtn = SQLParamHelper.parseDataEntityBeans(resultset, bean);
			resultset.close();
			statement.close();
			conn.close();
			conn = null;
		} catch (Exception ex) {
			log.error("error! groupName: " + connGroupName +", sql:" + progrom, ex);
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
	 * 功能:执行一些update等相关的操作
	 * 
	 * @param progrom
	 * @param param
	 * @return
	 */
	public static int Do(String progrom, Object[] param) {
		return Update(progrom, param);
	}

	/**
	 * 功能:在系统默认的数据库组上执行一个事务,参数会被包含进progrom里面去,所以需要自行做sql安全检查
	 * 
	 * @param dataBase
	 *            指定的数据库组名称
	 * @param progrom
	 *            程序组,多个程序的集合
	 * @return 是否事务执行成功
	 */
	public static Boolean DoTrans(String[] progrom) {
		return DoTrans(connection.groupName_static, progrom);
	}

	/**
	 * 功能:执行一个事务,参数会被包含进progrom里面去,所以需要自行做sql安全检查
	 * 
	 * @param dataBase
	 *            指定的数据库组名称
	 * @param progrom
	 *            程序组,多个程序的集合
	 * @return 是否事务执行成功
	 */
	public static Boolean DoTrans(String dataBase, String[] progrom) {
		Boolean rtn = false;
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try {

			// 因为是事务,所以默认是在主数据库上执行,第二个参数为true
			conn = connection.GetConnection(dataBase, true);
			statement = conn.createStatement();
			conn.setAutoCommit(false);
			for (int i = 0; i < progrom.length; i++) {
				statement.executeUpdate(progrom[i]);
			}
			conn.commit();
			statement.close();
			rtn = true;

		} catch (SQLException ex) {
			try {
				if (conn != null) {
					conn.rollback();
					conn.setAutoCommit(true);
					String ls = "";
					for (int i = 0; i < progrom.length; i++) {
						ls += progrom[i] + "\n";
					}
					log.error("事务执行失败，sql语句如下：" + ls, ex);
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
				log.error("以语句" + progrom[0] + "开头的一个事务执行失败,并且事务没有得到回滚,可能发生了数据不同步", e);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		} finally {
			try {
				if (statement != null) {
					statement.close();
					statement = null;
				}
				if (conn != null) {
					conn.setAutoCommit(true);
					conn.close();
					conn = null;
				}
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}

		return rtn;
	}
	
	
	/***
	 * 
	 * 支持多库多表批量处理
	 * @param sqls
	 */
	public static boolean doTransWithBatchMutilTable(List<OneSql> workSqls) {
		boolean ret = false;
		PreparedStatement workStat = null;
		List<Connection> hasConns = null;
		try {
			// 因为是事务,所以默认是在主数据库上执行,第二个参数为true
			if (workSqls != null && workSqls.size() > 0) {
					hasConns = new ArrayList<Connection>();// 已有的连接
					Map<String, List<OneSql>> sqlsMap = new HashMap<String, List<OneSql>>();
					List<DatabaseSql> databaseSqls = new ArrayList<DatabaseSql>();
					
					for (OneSql s : workSqls) {
						//不同库和不同语句为一个批次处理
						List<OneSql> curSqls = sqlsMap.get(s.getDatabase()+"_"+s.getSql());
						if (curSqls == null) {
							curSqls = new ArrayList<OneSql>();
							DatabaseSql ds = new DatabaseSql(s.getDatabase(), s.isBatch(), s.getSql() , curSqls);
							databaseSqls.add(ds);
						}
						
						String ns = DownTableManager.getProxySql(s.getSql(), null ,s.getPrams());
						s.setSql(ns);
						curSqls.add(s);
						sqlsMap.put(s.getDatabase()+"_"+s.getSql(), curSqls);
					}
	
					for (DatabaseSql ds : databaseSqls) {
						Connection conn = connection.GetConnection(ds.getDatabase(), true);
						conn.setAutoCommit(false);
						List<OneSql> curConnSqls = ds.getSqls();
						hasConns.add(conn);
						
						if(ds.isBatch()){//批处理
//							long s1 = System.currentTimeMillis();
							workStat = conn.prepareStatement(ds.getBatchSql());
							for (OneSql s : curConnSqls) {
								if (s != null && s.getSql() != null) {
									//log.info("当前执行事务语句："+s.getSql() + "，database:" + key + "," + Thread.currentThread().getName());
									SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);
//									String param = "";
//									for(Object o : s.getPrams()){
//										param += "," + o;
//									}
//									log.info(param);
									workStat.addBatch();
								}
							}
							workStat.executeBatch();
							
//							long s2 = System.currentTimeMillis();
//							
//							log.info("插入耗时：" + (s2 - s1));
						}else{
//							long s1 = System.currentTimeMillis();
							
							for (OneSql s : curConnSqls) {
								if (s != null && s.getSql() != null) {
									//log.info("当前执行事务语句："+s.getSql() + "，database:" + key + "," + Thread.currentThread().getName());
									workStat = conn.prepareStatement(s.getSql());
									SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);
									int efRows = workStat.executeUpdate();
									if (!s.ifCanNext(efRows)) {// 是否与断定次数相等
										String ss = "";
										if (s.getPrams() != null)
											for (int i = 0; i < s.getPrams().length; i++) {
												ss += ":" + s.getPrams()[i];
											}
										log.error("以sql语句（wrok）：" + s.getSql()
												+ ",参数：" + ss + "的执行与预期行数不一，预期影响："
												+ s.getEffectRows() + "行，实际影响："
												+ efRows + "行,导致事务回滚！");
										rollDoubleTrans(workStat, hasConns, workSqls);
										return false;
									}
								}
							}
							
//							long s2 = System.currentTimeMillis();
//							
//							log.info("删除耗时：" + (s2 - s1));
						}
					}
				}
				for (Connection workConn : hasConns) {
					workConn.commit();
				}
				workStat.close();
				ret = true;
		} catch (SQLException ex) {
			// 当前出错的sql
			rollDoubleTrans(workStat, hasConns, workSqls);
			log.error(ex.toString(), ex);
		} catch (Exception ex) {
			rollDoubleTrans(workStat, hasConns, workSqls);
			log.error(ex.toString(), ex);
		} finally {
			try {
				if (workStat != null) {
					workStat.close();
					workStat = null;
				}
				closeAll(hasConns);
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}
		return ret;
	}

    public static void main(String[] args) {
	    String s = "update entrust set status=?,completeNumber=completeNumber+?,completeTotalMoney=completeTotalMoney+? where entrustId=? and completeNumber+?<=numbers and status in(0,3)";
        String sqlLog = s.replaceAll("\\?", "%s");
        System.out.println(sqlLog);
    }
}
