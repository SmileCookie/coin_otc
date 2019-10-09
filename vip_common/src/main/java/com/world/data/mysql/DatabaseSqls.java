package com.world.data.mysql;

import java.util.ArrayList;
import java.util.List;



/******
 * 包装数据库操作语句  现有数据库 work  pay
 * 
 * @author Administrator
 *
 */
public class DatabaseSqls {
	public DatabaseSqls(){
		this.workSqls = new ArrayList<OneSql>();
		this.paySqls = new ArrayList<OneSql>();
	}
	public DatabaseSqls(List<OneSql> workSqls, List<OneSql> paySqls) {
		this.workSqls = workSqls;
		this.paySqls = paySqls;
	}

	private List<OneSql> workSqls;
	private List<OneSql> paySqls;
	
	public List<OneSql> getWorkSqls() {
		return workSqls;
	}
	public void setWorkSqls(List<OneSql> workSqls) {
		this.workSqls = workSqls;
	}
	public List<OneSql> getPaySqls() {
		return paySqls;
	}
	public void setPaySqls(List<OneSql> paySqls) {
		this.paySqls = paySqls;
	}
	
	public boolean doTrans(){
		return WorkAndPayTrans.doTransWorkAndPayWithCondList(workSqls, paySqls , true);
	}
	
	public boolean doBtcTrans(){
		return WorkAndPayTrans.doTransWorkAndPayWithCondList(workSqls, paySqls , true , "btc");
	}
	
}
