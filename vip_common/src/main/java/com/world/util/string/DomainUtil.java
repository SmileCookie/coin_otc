package com.world.util.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.world.config.GlobalConfig;

public class DomainUtil {
	
	/***
	 * @param url
	 * @return
	 */
	public static String getHostName(String url){
		return getHostName(url, GlobalConfig.baseDomain);
	}
	
	/***
	 * @param url
	 * @param baseDomain
	 * @return
	 */
	public static String getHostName(String url , String baseDomain){
		String res = "";
		Pattern p = Pattern.compile("//(.*)[.]"+baseDomain, Pattern.CASE_INSENSITIVE);;
		Matcher matcher = p.matcher(url);
		while(matcher.find()){
			res = matcher.group(1);  
		}
		return res;
	}
	/***
	 * @param url
	 * @return
	 */
	public static String getDomain(String url){
		Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)",Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(url);
		matcher.find();
		return matcher.group(); 
	}
}
