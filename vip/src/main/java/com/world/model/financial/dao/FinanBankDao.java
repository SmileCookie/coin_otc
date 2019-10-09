package com.world.model.financial.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.financial.entity.FinanBank;

/**
 * 银行数据dao
 * @author Administrator
 *
 */
public class FinanBankDao extends DataDaoSupport{
	
	public FinanBank get(int id){
		return (FinanBank)super.get("SELECT * FROM finanbank WHERE id = ? and isDel = ?", new Object[]{id, false}, FinanBank.class);
	}

	public int delById(int id){
		return super.delete("UPDATE finanbalance SET isDel = ? WHERE id = ?", new Object[]{true, id});
	}
	
	public int save(FinanBank bank){
		
		int count = super.save("INSERT INTO FinanBank (name, memo, tag, cftTag, epayTag, yeepayTag, img, withdrawLimit, withdrawBank, isDel) VALUES (?,?,?,?,?,?,?,?,?,?)", new Object[]{
				bank.getName(), bank.getMemo(), bank.getTag(), bank.getCftTag(), bank.getEpayTag(), bank.getYeepayTag(), bank.getImg(), bank.getWithdrawLimit(), bank.getWithdrawBank(), 0
		});
		return count;
	}
	
	public OneSql saveSql(FinanBank bank){
		return new OneSql("INSERT INTO FinanBank (name, memo, tag, cftTag, epayTag, yeepayTag, img, withdrawLimit, withdrawBank, isDel) VALUES (?,?,?,?,?,?,?,?,?,?)", 1, new Object[]{
				bank.getName(), bank.getMemo(), bank.getTag(), bank.getCftTag(), bank.getEpayTag(), bank.getYeepayTag(), bank.getImg(), bank.getWithdrawLimit(), bank.getWithdrawBank(), 0
		});
	}
	
}
