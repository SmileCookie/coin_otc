package com.world.util.string;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.world.model.dao.user.KeywordDao;
import com.world.model.entity.user.Keyword;
import org.apache.log4j.Logger;

public class KeyWordFilter {
	private static Logger log = Logger.getLogger(KeyWordFilter.class.getName());
	private static Pattern pattern = null;
	private static boolean isInitpattern = false;
	private static String[] patternType = new String[]{"","","",""};

	public static void initPattern(int type) {
		StringBuffer patternBuf = new StringBuffer("");
		try {
			KeywordDao keyDao = new KeywordDao();
			List<Keyword> list = keyDao.find(keyDao.getQuery().filter("typeId", type)).asList();
			
			for(Keyword key : list){
				String v = key.getWord();
				patternBuf.append(v + "|");
			}
			
			if(patternBuf.length() > 0)
				patternBuf.deleteCharAt(patternBuf.length() - 1);
			
			pattern = Pattern.compile(patternBuf.toString(), 2);
		} catch (Exception ioEx) {
			log.error(ioEx.toString(), ioEx);
		}
	}
	
	public static void initPattern2(int type) {
		StringBuffer patternBuf = new StringBuffer("");
		try {
			KeywordDao keyDao = new KeywordDao();
			List<Keyword> list = keyDao.find(keyDao.getQuery().filter("typeId", type)).asList();
			
			for(Keyword key : list){
				String v = key.getWord();
				patternBuf.append(v + "|");
			}
			
			if(patternBuf.length() > 0){
				patternBuf.deleteCharAt(patternBuf.length() - 1);
			}
			
			patternType[type] = patternBuf.toString();
		} catch (Exception ioEx) {
			log.error(ioEx.toString(), ioEx);
		}
	}

	public static String doFilter(String str) {
		try {
			if (!isInitpattern) {
				initPattern(KeyWordType.nick.getKey());
				//isInitpattern = true;
			}
			Matcher m = pattern.matcher(str);
			str = m.replaceAll("*");
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return str;
	}

	/**
	 * 默认为用户名过滤
	 * @param str
	 * @return
	 */
	public static boolean hasFilter(String str) {
		return hasFilter(str, KeyWordType.nick.getKey());
	}
	
	/**
	 * @param str
	 * @param type   不同的类型关键字库不一样
	 * @return
	 */
	public static boolean hasFilter(String str, int type) {
		try {
//			if (!isInitpattern) {
//				initPattern2(type);
//				isInitpattern = true;
//			}
//			String pat[] = patternType[type].split("\\|");
//			for(int i = 0; i < pat.length; i ++){
//				if(str.indexOf(pat[i]) >= 0){
//					return true;
//				}
//			}
//			if(patternType[type].indexOf("|"+str+"|") > 0){
//				return true;
//			}
			KeywordDao keyDao = new KeywordDao();
			
			Pattern pattern = Pattern.compile("^.*"  + str+  ".*$" ,  Pattern.CASE_INSENSITIVE);
			Keyword key = keyDao.findOne(keyDao.getQuery().filter("word", pattern));
			if(key != null){
				return true;
			}
			
			
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return false;
	}
	/**
	 * @param str
	 * @param type   不同的类型关键字库不一样
	 * @return
	 */
	public static boolean hasFilter2(String str, int type) {
		try {
			if (!isInitpattern) {
				initPattern(type);
				isInitpattern = true;
			}
			Matcher m = pattern.matcher(str);
			if(pattern.pattern().equals(""))
				return false;
			return m.find();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return false;
	}
}