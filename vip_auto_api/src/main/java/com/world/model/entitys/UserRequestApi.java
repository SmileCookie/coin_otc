package com.world.model.entitys;

import java.io.Serializable;
import java.util.Map;

public class UserRequestApi implements Serializable{

	private static final long serialVersionUID = -7310979648523512330L;

	public String userKey;//用户key

	public long last;//用户最后请求所在的毫秒  或者  hour  或者 秒
	
	public int times;//最后一秒请求次数
	
	public long unlockMiliSeconds;//解锁时间

	public long version = 0; // 版本，用于对比

	public Map<String, UserRequestApi> preMap;

	public void addVersion(){
		if (this.version == Long.MAX_VALUE)
			this.version = 1;
		else
			this.version += 1;
	}
}
