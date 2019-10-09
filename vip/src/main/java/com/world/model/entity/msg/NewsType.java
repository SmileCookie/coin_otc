package com.world.model.entity.msg;

import java.util.EnumSet;
import java.util.Iterator;

import com.world.model.entity.EnumUtils;
import com.world.model.entity.SysEnum;

public enum NewsType implements SysEnum{
	
	notice(1, "公告"),
	news(2, "新闻")
	;
	private NewsType(int key, String value) {
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
	
	public static NewsType getByValue(String value){
		EnumSet<NewsType> all = EnumUtils.getAll(NewsType.class);
		Iterator<NewsType> it = all.iterator();
		while (it.hasNext()) {
			NewsType newsType = (NewsType) it.next();
			if(newsType.name().equals(value)){
				return newsType;
			}
		}
		return notice;
	}

}
