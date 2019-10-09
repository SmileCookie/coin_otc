package com.world.data.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;



public class OrderAndQueoteTrans {
	static Logger log = Logger.getLogger(OrderAndQueoteTrans.class);
	static final String dataBaseName="default";
	/****
	 * 取消同意
	 * @param 
	 * @return
	 */
	public static boolean cancelAgree(String[] workSqls,String[] paySqls){
		Connection workConn = null;
		Connection payConn = null;
		Statement workStat = null;
		Statement payState = null;
		boolean ret=false;
		try {
			// 因为是事务,所以默认是在主数据库上执行,第二个参数为true
			workConn = connection.GetConnection("default", true);
			payConn=connection.GetConnection(dataBaseName, true);
			workStat = workConn.createStatement();
			payState=payConn.createStatement();
			workConn.setAutoCommit(false);
			for(String s : workSqls){
				workStat.executeUpdate(s);
			}
			payConn.setAutoCommit(false);
			for(String s : paySqls){
				payState.executeUpdate(s);
			}
			
			workConn.commit();
			payConn.commit();
			workStat.close();
			payState.close();
			ret=true;
		} catch (SQLException ex) {
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
			log.error(ex.toString(), ex);
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
}
