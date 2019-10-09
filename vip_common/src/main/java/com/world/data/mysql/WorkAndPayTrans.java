package com.world.data.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;


import com.world.data.mysql.config.BaseConfig;
import com.world.data.mysql.config.BaseConfigBean;



public class WorkAndPayTrans {
	static Logger log = Logger.getLogger(WorkAndPayTrans.class);
	static final String mainName="default";
	/****
	 * 
	 * work DB  pay DB事务执行
	 * @param workSqls
	 * @param paySqls
	 * @return
	 */
	public static boolean doTransWorkAndPay(String[] workSqls,String[] paySqls){
		Connection workConn = null;
		Connection payConn = null;
		Statement workStat = null;
		Statement payState = null;
		boolean ret=false;
		try {
			// 因为是事务,所以默认是在主数据库上执行,第二个参数为true   sql语句非空获取连接
			if(workSqls!=null&&workSqls.length>0){
				workConn = connection.GetConnection(mainName, true);
				workStat = workConn.createStatement();
				workConn.setAutoCommit(false);
				for(String s : workSqls){
					if(s!=null)
					workStat.executeUpdate(s);
				}
			}
				
			if(paySqls!=null&&paySqls.length>0){
				payConn=connection.GetBtcConnection();
				payState=payConn.createStatement();
				payConn.setAutoCommit(false);
				for(String s : paySqls){
					if(s!=null)
					payState.executeUpdate(s);
				}
			}
			
			if(workConn!=null){
				workConn.commit();
				workStat.close();
			}
			if(payConn!=null){
				payConn.commit();
				payState.close();
			}
			ret=true;
		} catch(SQLException ex){
			try {
				if (workConn != null) {
					workConn.rollback();
					workConn.setAutoCommit(true);
					log.warn("以语句" + workSqls[0] + "开头的一个事务执行失败");
					workConn.close();
					workConn = null;
					log.error("以语句" + workSqls[0] + "开头的一个事务执行失败", ex);
				}
				if (payConn != null) {
					payConn.rollback();
					payConn.setAutoCommit(true);
					log.warn("以语句" + paySqls[0] + "开头的一个事务执行失败");
					payConn.close();
					payConn = null;
				}
			} catch (Exception e) {
				log.error("以语句" + workSqls[0]
						+ "开头的一个事务执行失败,并且事务没有得到回滚,可能发生了数据不同步", e);
			}
		} catch (Exception ex) {
			log.warn(ex.toString());
			try {
				if (workConn != null) {
					workConn.rollback();
					workConn.setAutoCommit(true);
					log.warn("以语句" + workSqls[0] + "开头的一个事务执行失败");
					workConn.close();
					workConn = null;
					log.error("以语句" + workSqls[0] + "开头的一个事务执行失败", ex);
				}
				if (payConn != null) {
					payConn.rollback();
					payConn.setAutoCommit(true);
					log.warn("以语句" + paySqls[0] + "开头的一个事务执行失败");
					payConn.close();
					payConn = null;
				}
			} catch (Exception e) {
				log.error("以语句" + workSqls[0]
						+ "开头的一个事务执行失败,并且事务没有得到回滚,可能发生了数据不同步", e);
			}
		} finally {
			try {
				if (workStat != null) {
					workStat.close();
					workStat = null;
				}
				if (workConn != null) {
					workConn.setAutoCommit(true);
					workConn.close();
					workConn = null;
				}
				if (payState != null) {
					payState.close();
					payState = null;
				}
				if (payConn != null) {
					payConn.setAutoCommit(true);
					payConn.close();
					payConn = null;
				}
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}

		return ret;
		
	}
	/*****
	 * 处理带有条件的事物语句  传递参数List数组
	 * @param workSqls
	 * @param paySqls
	 * @return
	 */
	public static boolean doTransWorkAndPayWithCondList(List<OneSql> workSqls,List<OneSql> paySqls){
		int wSize=workSqls!=null?workSqls.size():0;
		int pSize=paySqls!=null?paySqls.size():0;
		OneSql[] wss=new OneSql[wSize];
		OneSql[] pss=new OneSql[pSize];
		for(int i=0;i<wSize;i++){
			wss[i]=workSqls.get(i);
		}
		for(int i=0;i<pSize;i++){
			pss[i]=paySqls.get(i);
		}
		return doTransWorkAndPayWithCondArray(wss, pss);
	}
	
	/*****
	 * 处理带有条件的事物语句  传递参数List数组
	 * @param workSqls
	 * @param paySqls
	 * @return
	 */
	public static boolean doTransWorkAndPayWithCondList(List<OneSql> workSqls){
		int wSize=workSqls!=null?workSqls.size():0;
	
		OneSql[] wss=new OneSql[wSize];
		
		for(int i=0;i<wSize;i++){
			wss[i]=workSqls.get(i);
		}
	
		return doTransWorkAndPayWithCondArray(wss);
	}
	
	
	
	/*********
	 * 处理带有条件的事物语句  可以包含数据库work  pay 中的sql语句  非预处理
	 * 严格比较影响的行数
	 * 
	 * @param workSqls
	 * @param paySqls
	 * @return
	 */
	public static boolean doTransWorkAndPayWithCondArray(OneSql[] workSqls,OneSql[] paySqls){
		Connection workConn = null;
		Connection payConn = null;
		Statement workStat = null;
		Statement payState = null;
		boolean ret=false;
		try {
			// 因为是事务,所以默认是在主数据库上执行,第二个参数为true
			if(workSqls!=null&&workSqls.length>0){
				workConn = connection.GetConnection(mainName, true);
				workStat = workConn.createStatement();
				workConn.setAutoCommit(false);
				for(OneSql s : workSqls){
					if(s!=null&&s.getSql()!=null){
						int efRows = workStat.executeUpdate(s.getSql());
						if(!s.ifCanNext(efRows)){//是否与断定次数相等
							log.debug(s.getSql() + "执行结果与预期不一致！将执行回滚");
							rollDoubleTrans(workStat, workConn, payState, payConn, workSqls, paySqls);
							return false;
						}
					}
				}
			}
			if(paySqls!=null&&paySqls.length>0){
				payConn=connection.GetConnection();
				payState=payConn.createStatement();
				payConn.setAutoCommit(false);
				for(OneSql s : paySqls){
					if(s!=null&&s.getSql()!=null){
						int efRows = payState.executeUpdate(s.getSql());
						if(!s.ifCanNext(efRows)){//是否与断定次数相等
							log.debug(s.getSql() + "执行结果与预期不一致！将执行回滚");
							rollDoubleTrans(workStat, workConn, payState, payConn, workSqls, paySqls);
							return false;
						}
					}
				}
			}
			if(workConn!=null){
				workConn.commit();
				workStat.close();
			}
			if(payState!=null){
				payConn.commit();
				payState.close();
			}
			ret=true;
		} catch(SQLException ex){
			rollDoubleTrans(workStat, workConn, payState, payConn, workSqls, paySqls);
			log.error(ex.toString() + "执行报sql错误！", ex);
		} catch (Exception ex) {
			rollDoubleTrans(workStat, workConn, payState, payConn, workSqls, paySqls);
			log.error(ex.toString() + "执行报ex错!", ex);
		} finally {
			try {
				if (workStat != null) {
					workStat.close();
					workStat = null;
				}
				if (workConn != null&&!workConn.isClosed()) {
					workConn.setAutoCommit(true);
					workConn.close();
					workConn = null;
				}
				if (payState != null) {
					payState.close();
					payState = null;
				}
				if (payConn != null&&!payConn.isClosed()) {
					payConn.setAutoCommit(true);
					payConn.close();
					payConn = null;
				}
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}
		return ret;
	}
	
	
	/*********
	 * 处理带有条件的事物语句  可以包含数据库work中的sql语句  非预处理
	 * 严格比较影响的行数
	 * 
	 * @param workSqls
	 * @return
	 */
	public static boolean doTransWorkAndPayWithCondArray(OneSql[] workSqls){
		Connection workConn = null;
	
		Statement workStat = null;
		
		boolean ret=false;
		try {
			// 因为是事务,所以默认是在主数据库上执行,第二个参数为true
			if(workSqls!=null&&workSqls.length>0){
				workConn = connection.GetConnection(mainName, true);
				workStat = workConn.createStatement();
				workConn.setAutoCommit(false);
				for(OneSql s : workSqls){
					if(s!=null&&s.getSql()!=null){
						int efRows = workStat.executeUpdate(s.getSql());
						if(!s.ifCanNext(efRows)){//是否与断定次数相等
							log.debug(s.getSql() + "执行结果与预期不一致！将执行回滚");
							rollDoubleTrans(workStat, workConn, workSqls);
							return false;
						}
					}
				}
			}
			
			if(workConn!=null){
				workConn.commit();
				workStat.close();
			}
		
			ret=true;
		} catch(SQLException ex){
			rollDoubleTrans(workStat, workConn, workSqls);
			log.error(ex.toString() + "执行报sql错误！", ex);
		} catch (Exception ex) {
			rollDoubleTrans(workStat, workConn, workSqls);
			log.error(ex.toString() + "执行报ex错!", ex);
		} finally {
			try {
				if (workStat != null) {
					workStat.close();
					workStat = null;
				}
				if (workConn != null&&!workConn.isClosed()) {
					workConn.setAutoCommit(true);
					workConn.close();
					workConn = null;
				}
			
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}
		return ret;
	}
	
	
	/****
	 * 回滚双事务
	 * @param workStat
	 * @param workConn
	 * @param payState
	 * @param payConn
	 */
	private static void rollDoubleTrans(Statement workStat,Connection workConn,Statement payState,Connection payConn,OneSql[] workSqls,OneSql[] paySqls){
		String sql="";
		try {
			if (workConn != null) {
				workConn.rollback();
				workConn.setAutoCommit(true);
				sql+="来源： work数据库，";
				for(OneSql os :  workSqls){
					sql+=","+os.getSql();
				}
				log.warn("以语句" + sql + "开头的一个事务执行失败,事务回滚");
				workConn.close();
				workConn = null;
			}
			if (payConn != null) {
				payConn.rollback();
				payConn.setAutoCommit(true);
				sql+="来源： pay数据库，";
				for(OneSql os :  paySqls){
					sql+=os.getSql()+",";
				}
				log.warn("以语句" + sql + "开头的一个事务执行失败,事务回滚");
				payConn.close();
				payConn = null;
			}
		} catch (Exception e) {
			log.error("以语句" + workSqls[0].getSql()
					+ "开头的一个事务执行失败,并且事务没有得到回滚,可能发生了数据不同步", e);
		}
	}
	/****
	 * 回滚双事务
	 * @param workStat
	 * @param workConn
	 * @param payState
	 * @param payConn
	 */
	private static void rollDoubleTrans(Statement workStat,Connection workConn,OneSql[] workSqls){
		String sql="";
		try {
			if (workConn != null) {
				workConn.rollback();
				workConn.setAutoCommit(true);
				sql+="来源： work数据库，";
				for(OneSql os :  workSqls){
					sql+=","+os.getSql();
				}
				log.warn("以语句" + sql + "开头的一个事务执行失败,事务回滚");
				workConn.close();
				workConn = null;
			}
			
		} catch (Exception e) {
			log.error("以语句" + workSqls[0].getSql()
					+ "开头的一个事务执行失败,并且事务没有得到回滚,可能发生了数据不同步", e);
		}
	}
	
	public static boolean doTransWorkAndPayWithCondList(List<OneSql> workSqls,List<OneSql> paySqls,Boolean isPre){
		return doTransWorkAndPayWithCondList(workSqls, paySqls, isPre, "ltc");
	}
	
	/*****
	 * 处理带有条件的事物语句  传递参数List数组
	 * @param workSqls
	 * @param paySqls
	 * @param isPre 是否支持预处理
	 * @return
	 */
	public static boolean doTransWorkAndPayWithCondList(List<OneSql> workSqls,List<OneSql> paySqls,Boolean isPre , String data){
		int wSize=workSqls!=null?workSqls.size():0;
		int pSize=paySqls!=null?paySqls.size():0;
		OneSql[] wss=new OneSql[wSize];
		OneSql[] pss=new OneSql[pSize];
		for(int i=0;i<wSize;i++){
			wss[i]=workSqls.get(i);
		}
		for(int i=0;i<pSize;i++){
			pss[i]=paySqls.get(i);
		}
		return doTransWorkAndPayWithCondArray(wss, pss,isPre,data);
	}
	
	/*********
	 * 处理带有条件的事物语句  可以包含数据库work  pay 中的sql语句  
	 * 预处理
	 * 严格比较影响的行数  错误时抛出异常 并进行事务回滚
	 * 
	 * @param workSqls
	 * @param paySqls
	 * @param isPre  是否支持预处理
	 * @return
	 */
	public static boolean doTransWorkAndPayWithCondArray(OneSql[] workSqls,OneSql[] paySqls,Boolean isPre,String data){
		Connection workConn = null;
		Connection payConn = null;
		PreparedStatement workStat = null;
		PreparedStatement payState = null;
		boolean ret=false;
		BaseConfigBean progromBase=null;
		try {
			// 因为是事务,所以默认是在主数据库上执行,第二个参数为true
			if(workSqls!=null&&workSqls.length>0){
				workConn = connection.GetConnection();
				workConn.setAutoCommit(false);
				for(OneSql s : workSqls){
					if(s!=null&&s.getSql()!=null){
						progromBase=BaseConfig.getProgrom(s.getSql());
						if(progromBase!=null){
					    	   if(progromBase.getDatatype()==0)
					    		   workStat=workConn.prepareStatement(progromBase.getOpsCode());
						       else
						    	   workStat=workConn.prepareCall(progromBase.getOpsCode());
						       //设置参数
						       SQLParamHelper.SetPrama(s.getPrams(), workStat, progromBase);
					     }else{
					    	workStat=workConn.prepareStatement(s.getSql());//初始化statemaent
					        //设置参数
					        SQLParamHelper.JavaParam2SQLParam(s.getPrams(), workStat);
					     }
						int efRows = workStat.executeUpdate();
						if(!s.ifCanNext(efRows)){//是否与断定次数相等
							log.error("以sql语句（wrok）：" + s.getSql() + "的执行与预期行数不一，预期影响：" + s.getEffectRows() + "行，实际影响：" + efRows + "行,导致事务回滚！");
							rollDoubleTrans(workStat, workConn, payState, payConn, workSqls, paySqls);
							return false;
						}
					}
				}
			}
			if(paySqls!=null&&paySqls.length>0){
				if(data.equals("btc")){
					payConn=connection.GetBtcConnection();
				}
				if(payConn == null){
					rollDoubleTrans(workStat, workConn, payState, payConn, workSqls, paySqls);
					return false;
				}
				payConn.setAutoCommit(false);
				for(OneSql s : paySqls){
					if(s!=null&&s.getSql()!=null){
						progromBase=BaseConfig.getProgrom(s.getSql());
						if(progromBase!=null){
					    	   if(progromBase.getDatatype()==0)
					    		   payState=payConn.prepareStatement(progromBase.getOpsCode());
						       else
						    	   payState=payConn.prepareCall(progromBase.getOpsCode());
						       //设置参数
						       SQLParamHelper.SetPrama(s.getPrams(), payState, progromBase);
					     }else{
					    	 payState=payConn.prepareStatement(s.getSql());//初始化statemaent
					        //设置参数
					        SQLParamHelper.JavaParam2SQLParam(s.getPrams(), payState);
					     }
						int efRows = payState.executeUpdate();
						if(!s.ifCanNext(efRows)){//是否与断定次数相等
							log.error("-----以sql语句（pay）：" + s.getSql() + "的执行与预期行数不一，预期影响：" + s.getEffectRows() + "行，实际影响：" + efRows + "行,导致事务回滚！-----");
							rollDoubleTrans(workStat, workConn, payState, payConn, workSqls, paySqls);
							return false;
						}
					}
				}
			}
			if(workConn!=null){
				workConn.commit();
				workStat.close();
			}
			if(payState!=null){
				payConn.commit();
				payState.close();
			}
			ret=true;
		} catch(SQLException ex){
			rollDoubleTrans(workStat, workConn, payState, payConn, workSqls, paySqls);
			log.error(ex.toString(), ex);
		} catch (Exception ex) {
			log.warn(ex.toString());
			rollDoubleTrans(workStat, workConn, payState, payConn, workSqls, paySqls);
			log.error(ex.toString(), ex);
		} finally {
			try {
				if (workStat != null) {
					workStat.close();
					workStat = null;
				}
				if (workConn != null&&!workConn.isClosed()) {
					workConn.setAutoCommit(true);
					workConn.close();
					workConn = null;
				}
				if (payState != null) {
					payState.close();
					payState = null;
				}
				if (payConn != null&&!payConn.isClosed()) {
					payConn.setAutoCommit(true);
					payConn.close();
					payConn = null;
				}
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}
		return ret;
	}
}
