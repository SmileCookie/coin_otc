package com.world.data.mongo;

import java.util.HashMap;
import java.util.Map;

import com.world.config.GlobalConfig;
import com.world.model.entity.coin.CoinProps;

public class CointMongoUtil {
	private static Map<String, MorphiaMongo> morphias = new HashMap<String, MorphiaMongo>();
	
	public static MorphiaMongo getMorphia(CoinProps coint){
		if(coint == null){
			throw new RuntimeException("库信息为空，无法初始化");
		}
		String tag = coint.getDatabaseKey();
		MorphiaMongo mm = morphias.get(tag);
		String dbName = GlobalConfig.getValue(tag + "Db");
		if(mm == null){
			mm = MorphiaMongoUtil.getMorphiaMongo(GlobalConfig.getValue(tag + "Db") ,
					GlobalConfig.getValue(tag + "DbIp") , GlobalConfig.getValue(tag + "UserName") , GlobalConfig.getValue(tag + "Pwd"));
			morphias.put(dbName, mm);
		}
		return mm;
	}
}
