package com.world.util.request;

import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapSort {

	private final static Logger log = Logger.getLogger(MapSort.class);

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("Kb", "K");
		map.put("Wc", "W");
		map.put("Ab", "A");
		map.put("ad", "a");
		map.put("Fe", "N");
		map.put("Bf", "B");
		map.put("Cg", "C");
		map.put("Zh", "Z");

		Map<String, String> resultMap = sortMapByKey(map);	//按Key进行排序，根据首字母hashcode

		for (Map.Entry<String, String> entry : resultMap.entrySet()) {
			log.info(entry.getKey() + " " + entry.getValue());
		}
		
	}
	
	/**
	 * 使用 Map按key首字母hashcode进行排序
	 * @param map
	 * @return
	 */
	public static Map<String, String> sortMapByKey(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		
		Map<String, String> sortMap = new TreeMap<String, String>(
				new MapKeyComparator());

		sortMap.putAll(map);

		return sortMap;
	}
}

class MapKeyComparator implements Comparator<String>{

	public int compare(String str1, String str2) {
		
		return str1.compareTo(str2);
	}
}