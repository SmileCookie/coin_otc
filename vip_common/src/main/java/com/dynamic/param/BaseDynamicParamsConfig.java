package com.dynamic.param;

import java.io.Serializable;

import com.google.code.morphia.annotations.Embedded;
@Embedded
public class BaseDynamicParamsConfig implements Serializable{

	private static final long serialVersionUID = -6965894814577063056L;
	
	private long version = 0;//当前的版本号以当前的服务器时间戳记录，0时表示还未从数据库中同步过当前实体记录、只是纯粹的新建一个配置类而已

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

}
