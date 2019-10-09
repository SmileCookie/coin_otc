package com.world.model.entity;

import java.util.*;

public class EnumUtils {

	private static Map<String , EnumSet> dateEnums = new HashMap<String, EnumSet>();
	
	/*******
	 * 获取所有状态
	 * @return
	 */
	public static EnumSet getAll(Class c){
		if(dateEnums.get(c.getName()) == null){
			dateEnums.put(c.getName(), EnumSet.allOf(c));
		}
		return dateEnums.get(c.getName());
	}
	/*******
	 * 根据键值返回对应状态
	 * @param key
	 * @return
	 */
	public static SysEnum getEnumByKey(int key , Class c){
		EnumSet curEs = getAll(c);
		if(curEs != null){
			Iterator it = curEs.iterator();
			while(it.hasNext()){
				SysEnum stat=(SysEnum) it.next();
				if(stat.getKey()==key){
					return stat;
				}
			}
		}
		return null;
	}

	public static Map<Integer, String> enumToMap(SysEnum[] sysEnums){
		if(sysEnums == null && sysEnums.length < 1)
			return null;

		Map<Integer, String> map = new LinkedHashMap<Integer, String>(sysEnums.length);
		for(SysEnum entity: sysEnums){
			map.put(entity.getKey(), entity.getValue());
		}

		return map;
	}
}