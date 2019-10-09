package com.world.data.mysql.transaction;

import com.world.data.mysql.Bean;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.SQLParamHelper;
import com.world.data.mysql.connection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/***
 * 2016-6-25  日创建  
 * 处理事务模型
 * 此事物模型应用与中间业务层需要操作回滚的业务
 * 提交或回滚事物也可以在业务层控制
 *
 */
public class TransactionObject {
	public static Logger log = Logger.getLogger(TransactionObject.class);
	
	public TransactionObject(){
		
	}
	
	public TransactionObject(String[] databases) {
		super();
		this.databases = databases;
		start();
	}
	
	private int status = 1;//1正常   0 已回滚

	private String[] databases;//连接的数据库

	private Map<String, Connection> connects;//已建立的连接
	
	/***
	 * 执行一条查询语句
	 * @param s
	 */
	public void excuteUpdate(OneSql s){
		excuteSql(s, null);
	}
	
	/***
	 * 执行多条sql
	 * @param sqls
	 */
	public void excuteUpdateList(List<OneSql> sqls){
		for(OneSql s : sqls){
			excuteSql(s, null);
		}
	}
	
	public <T extends Bean> T excuteQueryT(OneSql s, Class<T> cls){
		return excuteSql(s, cls);
	}
	
	public List<Object> excuteQuery(OneSql s){
		if(status == 0){//前面的事物语句已回滚
			return null;
		}
		
		addDatabase(s.getDatabase());
		
		Connection conn = null;
		PreparedStatement workStat = null;
		
		List<Object> rtn = new ArrayList<Object>();
		if (s != null && s.getSql() != null) {
			try {
				conn = connects.get(s.getDatabase());
				
				if(conn == null){
					rollback(s, workStat);
					return null;
				}
				workStat = conn.prepareStatement(s.getSql());
				
				SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);
				
				ResultSet resultset = null;

				try {
					// 执行查询
					resultset = workStat.executeQuery();
					rtn = SQLParamHelper.parseDataEntityBeans(resultset);
					resultset.close();
					workStat.close();
					conn = null;
					
					if(rtn.size() >= 1){
						return (List<Object>)rtn.get(0);
					}
				} catch (Exception ex) {
					log.error("error! sql:" + s.getSql(), ex);
					log.info("sql:" + s.getSql());
				} finally {
					try {
						if (resultset != null) {
							resultset.close();
							resultset = null;
						}
						if (workStat != null) {
							workStat.close();
							workStat = null;
						}
					} catch (Exception ex) {
						log.error(ex.toString(), ex);
					}
				}
			} catch (SQLException e1) {
				log.error(e1.toString(), e1);
				rollback(s, workStat);
			}
		}else{
			rollback(s, workStat);
		}
		
		return null;
	}
	
	private <T extends Bean> T excuteSql(OneSql s, Class<T> cls){
		if(status == 0){//前面的事物语句已回滚
			return null;
		}
		
		addDatabase(s.getDatabase());
		
		Connection conn = null;
		PreparedStatement workStat = null;
		
		List<T> rtn = new ArrayList<T>();
		if (s != null && s.getSql() != null) {
			try {
				conn = connects.get(s.getDatabase());
				
				if(conn == null){
					rollback(s, workStat);
					return null;
				}
				workStat = conn.prepareStatement(s.getSql());
				
				SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);
				
				if(s.getSql().toLowerCase().trim().startsWith("select ")){
					ResultSet resultset = null;

					try {
						// 执行查询
						resultset = workStat.executeQuery();
						rtn = SQLParamHelper.parseDataEntityBeans(resultset, cls);
						resultset.close();
						workStat.close();
						conn = null;
						
						if(rtn.size() >= 1){
							return rtn.get(0);
						}
					} catch (Exception ex) {
						log.error("sql:" + s.getSql(), ex);
					} finally {
						try {
							if (resultset != null) {
								resultset.close();
								resultset = null;
							}
							if (workStat != null) {
								workStat.close();
								workStat = null;
							}
						} catch (Exception ex) {
							log.error(ex.toString(), ex);
						}
					}
				}else{
					String ss = "";
					int efRows = -3;
					try {
						efRows = workStat.executeUpdate();
					} catch (Exception e) {
                        if (null != s.getPrams()) {
                            for (int i = 0; i < s.getPrams().length; i++) {
                                ss += ":" + s.getPrams()[i];
                            }
                        }
                        log.error("以sql语句（"+s.getDatabase()+"）：" + s.getSql()
								+ ",参数：" + ss + "的执行出错了，导致事务回滚！", e);
						rollback(s, workStat);
					}
					if (status != 0 && !s.ifCanNext(efRows)) {// 是否与断定次数相等
						
						if (s.getPrams() != null)
							for (int i = 0; i < s.getPrams().length; i++) {
								ss += ":" + s.getPrams()[i];
							}
						log.error(s.getDatabase() + ",以sql语句（"+s.getDatabase()+"）：" + s.getSql()
								+ ",参数：" + ss + "的执行与预期行数不一，预期影响："
								+ s.getEffectRows() + "行，实际影响："
								+ efRows + "行,导致事务回滚！");
						rollback(s, workStat);
					}
				}
			} catch (SQLException e1) {
				log.error(e1.toString(), e1);
				rollback(s, workStat);
			}
		}else{
			rollback(s, workStat);
		}
		
		return null;
	}
	
	public void rollback(OneSql os){
		rollback(os, null);
	}
	
	public void rollback(String error){
		rollback(null, null, error);
	}
	
	private void rollback(OneSql os, PreparedStatement workStat){
		rollback(os, workStat, null);
	}
	
	/***
	 * 回滚当前事物模型中的所有连接
	 * @param os
	 * @param workStat
	 * @param error
	 */
	private void rollback(OneSql os, PreparedStatement workStat, String error){
		status = 0;
		String sql = "";
		try {
			if(connects.size() > 0){
				for(Entry<String, Connection> entry : connects.entrySet()){
					try {
						Connection workConn = entry.getValue();
						workConn.rollback();
						workConn.setAutoCommit(true);
						if(os != null){
							sql += "来源： work数据库，";
							sql += "," + os.getSql();
							log.error("以语句" + sql + "开头的一个事务执行失败,事务回滚");
						}else{
							log.error("database:" + entry.getKey() + "事物出错了," + error != null ? error : "no infos!");
						}
						workConn.close();
						workConn = null;
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}
			}
			
		} catch (Exception e) {
			if(os != null){
				log.error("以语句" + os.getSql()
						+ "开头的一个事务执行失败,并且事务没有得到回滚,可能发生了数据不同步", e);
			}else{
				log.error("database:事物出错了", e);
			}
		}finally{
			try {
				if(workStat != null && !workStat.isClosed()){
					workStat.close();
				}
			} catch (SQLException e) {
				log.error(e.toString(), e);
			}
		}
	}
	
	/****
	 * 启动事物
	 */
	private void start(){
		for(String database : databases){
			addDatabase(database);
		}
	}
	
	
	private synchronized void addDatabase(String database){
		
		if(connects == null){
			connects = new HashMap<String, Connection>();
		}
		
		Connection conn = connects.get(database);
		if(conn == null){
			conn = connection.GetConnection(database, true);
			try {
				if(conn != null){
					conn.setAutoCommit(false);
					connects.put(database, conn);
				}
			} catch (SQLException e) {
				log.error(e.toString(), e);
			}
		}
	}

    public List<Object> excuteQuery2(OneSql s){
        if(status == 0){//前面的事物语句已回滚
            return null;
        }

        addDatabase(s.getDatabase());

        Connection conn = null;
        Statement workStat = null;

        List<Object> rtn = new ArrayList<Object>();
        if (s != null && s.getSql() != null) {
            try {
                conn = connects.get(s.getDatabase());

                if(conn == null){
                    rollback(s, workStat, null);
                    return null;
                }
                workStat = conn.createStatement();

                ResultSet resultset = null;
                try {
                    // 执行查询
                    resultset = workStat.executeQuery(s.getSql());
                    rtn = SQLParamHelper.parseDataEntityBeans(resultset);
                    resultset.close();
                    workStat.close();
                    conn = null;

                    if(rtn.size() >= 1){
                        return (List<Object>)rtn.get(0);
                    }
                } catch (Exception ex) {
                    log.error("error! sql:" + s.getSql(), ex);
                } finally {
                    try {
                        if (resultset != null) {
                            resultset.close();
                            resultset = null;
                        }
                        if (workStat != null) {
                            workStat.close();
                            workStat = null;
                        }
                    } catch (Exception ex) {
                        log.error(ex.toString(), ex);
                    }
                }
            } catch (SQLException e1) {
                log.error(e1.toString(), e1);
                rollback(s, workStat, null);
            }
        }else{
            rollback(s, workStat, null);
        }

        return null;
    }


    public void excuteUpdate2(OneSql s){
        excuteSql2(s, null);
    }

    public void excuteUpdateList2(List<OneSql> sqls){
        for(OneSql s : sqls){
            excuteSql2(s, null);
        }
    }

    private <T extends Bean> T excuteSql2(OneSql s, Class<T> cls){
        if(status == 0){//前面的事物语句已回滚
            return null;
        }

        if(null == s){
            return null;
        }

        addDatabase(s.getDatabase());

        Connection conn = null;
        Statement workStat = null;

        List<T> rtn = new ArrayList<T>();
        if (s.getSql() != null) {
            try {
                conn = connects.get(s.getDatabase());

                if(conn == null){
                    rollback(s, null, null);
                    return null;
                }
                workStat = conn.createStatement();

                if(s.getSql().toLowerCase().trim().startsWith("select ")){
                    ResultSet resultset = null;

                    try {
                        // 执行查询
                        resultset = workStat.executeQuery(s.getSql());
                        rtn = SQLParamHelper.parseDataEntityBeans(resultset, cls);
                        resultset.close();
                        workStat.close();
                        conn = null;

                        if(rtn.size() >= 1){
                            return rtn.get(0);
                        }
                    } catch (Exception ex) {
                        log.error("sql:" + s.getSql(), ex);
                    } finally {
                        try {
                            if (resultset != null) {
                                resultset.close();
                                resultset = null;
                            }
                            if (workStat != null) {
                                workStat.close();
                                workStat = null;
                            }
                        } catch (Exception ex) {
                            log.error(ex.toString(), ex);
                        }
                    }
                }else{
                    String ss = "";
                    int efRows = -3;
                    try {
                        efRows = workStat.executeUpdate(s.getSql());
                    } catch (Exception e) {
                        if (null != s.getPrams()) {
                            for (int i = 0; i < s.getPrams().length; i++) {
                                ss += ":" + s.getPrams()[i];
                            }
                        }
                        log.error("以sql语句（"+s.getDatabase()+"）：" + s.getSql()
                                + ",参数：" + ss + "的执行出错了，导致事务回滚！", e);
                        rollback(s, workStat, null);
                    }
                    if (status != 0 && !s.ifCanNext(efRows)) {// 是否与断定次数相等

                        if (s.getPrams() != null){
                            for (int i = 0; i < s.getPrams().length; i++) {
                                ss += ":" + s.getPrams()[i];
                            }
                        }
                        log.error(s.getDatabase() + ",以sql语句（"+s.getDatabase()+"）：" + s.getSql()
                                + ",参数：" + ss + "的执行与预期行数不一，预期影响："
                                + s.getEffectRows() + "行，实际影响："
                                + efRows + "行,导致事务回滚！");
                        rollback(s, workStat, null);
                    }
                }
            } catch (SQLException e1) {
                log.error(e1.toString(), e1);
                rollback(s, workStat, null);
            }
        }else{
            rollback(s, workStat, null);
        }

        return null;
    }

    private void rollback(OneSql os, Statement workStat, String error){
        status = 0;
        String sql = "";
        try {
            if(connects.size() > 0){
                for(Entry<String, Connection> entry : connects.entrySet()){
                    try {
                        Connection workConn = entry.getValue();
                        workConn.rollback();
                        workConn.setAutoCommit(true);
                        if(os != null){
                            sql += "来源： work数据库，";
                            sql += "," + os.getSql();
                            log.error("以语句" + sql + "开头的一个事务执行失败,事务回滚");
                        }else{
                            log.error("database:" + entry.getKey() + "事物出错了," + error != null ? error : "no infos!");
                        }
                        workConn.close();
                        workConn = null;
                    } catch (Exception e) {
                        log.error(e.toString(), e);
                    }
                }
            }

        } catch (Exception e) {
            if(os != null){
                log.error("以语句" + os.getSql()
                        + "开头的一个事务执行失败,并且事务没有得到回滚,可能发生了数据不同步", e);
            }else{
                log.error("database:事物出错了", e);
            }
        }finally{
            try {
                if(workStat != null && !workStat.isClosed()){
                    workStat.close();
                }
            } catch (SQLException e) {
                log.error(e.toString(), e);
            }
        }
    }

	/***
	 * 提交结束事物
	 * return true 事务成功   false 事务失败
	 */
	public boolean commit(){
        try {
            if(status == 0){//前面的事物语句已回滚
                log.error("事物已回滚，无需提交");


                if(connects.size() > 0){
                    for(Entry<String, Connection> entry : connects.entrySet()){
                        Connection connect = entry.getValue();
                        try {
                            if(connect != null && !connect.isClosed()){
                                connect.close();
                            }
                        } catch (SQLException e) {
                            log.error(e.toString(), e);
                        }finally{
                            try {
                                connect.close();
                            } catch (SQLException e) {
                                log.error(e.toString(), e);
                            }
                        }
                    }
                }
                return false;
            }

            if(connects.size() > 0){
                for(Entry<String, Connection> entry : connects.entrySet()){
                    Connection connect = entry.getValue();
                    try {
                        connect.commit();
                    } catch (SQLException e) {
                        log.error(e.toString(), e);
                        rollback(null, null);
                        break;
                    } catch (Exception e) {
                        log.error(e.toString(), e);
                        rollback(null, null);
                        break;
                    }finally{
                        try {
                            connect.setAutoCommit(true);
                            connect.close();
                        } catch (SQLException e) {
                            log.error(e.toString(), e);
                        }
                    }
                }

                if(status == 1){
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("commit error" ,e);
        }

        return false;
	}
	
	
}
