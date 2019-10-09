package com.world.model.dao.wallet;

import java.math.BigDecimal;
import java.util.List;

import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;

public class WalletDetailsDao extends DataDaoSupport{

	public OneSql getInsertSql(int walletId , int ioType , BigDecimal amount , BigDecimal fee, String des){
		return new OneSql("insert into WalletDetails(walletId,ioType,amount,fee,walletAmount,des,addDate) " +
				"select ?,?,?,?,btcs,?,now() from btcwallet where walletId=?", -1, 
				new Object[]{walletId , ioType , amount , fee, des,walletId});
	}
	
	public void injectInsertSql(int walletId , int ioType , BigDecimal amount , BigDecimal fee, String des , List<OneSql> sqls){
		sqls.add(getInsertSql(walletId , ioType , amount , fee, des));
	}
}
