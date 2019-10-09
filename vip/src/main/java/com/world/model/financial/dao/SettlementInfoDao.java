package com.world.model.financial.dao;

import com.world.data.mongo.MongoDao;
import com.world.model.financial.entity.SettlementInfo;

public class SettlementInfoDao extends MongoDao<SettlementInfo, String>{

	public SettlementInfo getLast(int coinType) {
		SettlementInfo sinfo = super.findOne(getQuery()
				.filter("coinType", coinType)
				.order("-_id")
				.limit(1));
		return sinfo;
	}

}
