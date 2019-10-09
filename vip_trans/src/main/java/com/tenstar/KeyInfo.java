package com.tenstar;

import java.util.List;

import com.world.data.mysql.Data;
import org.apache.log4j.Logger;

public class KeyInfo {
	private final static Logger log = Logger.getLogger(KeyInfo.class);

	private long keyMax;
	private long Keymin;
	private long nextKey;
	private long poolSize;
	private String keyName;
	private String db;

	public KeyInfo(long poolSize, String keyName,String db) {
		this.poolSize = poolSize;
		this.keyName = keyName;
		this.db = db;
		loadFromDB(db);
	}

	public long getKeyMax() {
		return keyMax;
	}

	public long getKeymin() {
		return Keymin;
	}

	private synchronized void loadFromDB(String db) {
		List c = (List) Data.GetOne(db,"select * from autoids where names=?", new Object[] { keyName.trim().toLowerCase() });
		long keyFromDB = 0;
		if(c == null || c.size() < 3 || c.get(2) == null){
			Data.Insert(db,"insert into autoids(names,numbers,steps,marks) values(?,?,?,?)", new Object[]{keyName, 0, 1, keyName});
		}else{
			keyFromDB = Long.parseLong(c.get(2).toString());
		}
		
		keyFromDB = keyFromDB + poolSize;
		Data.Update(db,"update autoids set numbers=numbers+? where names=?", new Object[] {poolSize,keyName});
		
		keyMax = keyFromDB;
		Keymin = keyFromDB - poolSize + 1;
		nextKey = Keymin;
	}

	public long getNextKey(String db) {

		if (nextKey > keyMax) {
			loadFromDB(db);
			log.info("get keyvalue from db");

		}
		return nextKey++;
	}

}
