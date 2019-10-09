package com.world.model.financial.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.financial.entity.FinanRemit;

public class FinanRemitDao extends DataDaoSupport<FinanRemit>{
	
	public OneSql saveSql(BigDecimal inAmount, BigDecimal outAmount, BigDecimal inTotalAmount, BigDecimal outTotalAmount, String adminId, Timestamp createTime, int status,
			Timestamp startTime, Timestamp endTime){
		return new OneSql("INSERT INTO financeremit (inAmount, outAmount, inTotalAmount, outTotalAmount, adminId, addTime, status, startTime, endTime) VALUES (?,?,?,?,?,?,?,?,?)", 1, new Object[]{
				inAmount, outAmount, inTotalAmount, outTotalAmount, adminId, createTime, status, startTime, endTime
		});
	}

	public OneSql updateSql(BigDecimal inAmount, BigDecimal outAmount, int status, Timestamp startTime, Timestamp endTime, int id){
		return new OneSql("UPDATE financeremit SET inAmount = ?, outAmount = ?, status = ?, startTime = ?, endTime = ? WHERE id = ? AND isDel = 0", 1, new Object[]{
				inAmount, outAmount, status, startTime, endTime, id
		});
	}
	
	public FinanRemit getLast(){
		return (FinanRemit)Data.GetOne("SELECT * FROM financeremit WHERE status = 1 AND isDel = 0 ORDER BY addTime DESC LIMIT 0, 1", new Object[]{}, FinanRemit.class);
	}
	
	public FinanRemit get(int id){
		FinanRemit remit = (FinanRemit) super.get("SELECT * FROM financeRemit WHERE id = ? AND isDel = 0", new Object[]{id}, FinanRemit.class);
		return remit;
	}
	
	public OneSql updateStatusSql(int id, int status){
		return new OneSql("UPDATE financeremit SET status = ? WHERE id = ? AND isDel = 0", 1, new Object[]{status, id});
	}
}
