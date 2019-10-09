package com.world.model.dao.wallet;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.CointTable;
import com.world.model.entity.pay.WalletBean;

@SuppressWarnings("serial")
public class WalletDao extends DataDaoSupport<WalletBean>{
	public String getTableName(){
		return coint.getStag()+CointTable.wallet;
	}
}
