package com.world.model.entity.msg;

import com.world.model.entity.EnumUtils;
import com.world.model.entity.SysEnum;

import java.util.EnumSet;
import java.util.Iterator;

public enum NoticeType implements SysEnum{

	newCoin(1, "新币上线"),
	sysMaintenance(2, "系统维护"),
	newActivity(3, "最新活动"),
	platformDynamics(4, "平台动态")
	;
	private NoticeType(int key, String value) {
		this.key = key;
		this.value = value;
	}

	private int key;
	private String value;
	
	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	public static NoticeType getByValue(String value){
		EnumSet<NoticeType> all = EnumUtils.getAll(NoticeType.class);
		Iterator<NoticeType> it = all.iterator();
		while (it.hasNext()) {
			NoticeType newsType = (NoticeType) it.next();
			if(newsType.name().equals(value)){
				return newsType;
			}
		}
		return null;
	}

}
