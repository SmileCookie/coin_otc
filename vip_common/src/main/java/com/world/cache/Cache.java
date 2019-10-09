package com.world.cache;

import com.world.config.MemoryConfig;
import net.rubyeye.xmemcached.GetsResponse;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

public class Cache {

	private final static Logger log = Logger.getLogger(Cache.class.getName());

	public static boolean Set(String key, String value, int time) {
		try {
			MemoryConfig.getMem().set(key, time, value);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			return false;
		}
		return Boolean.valueOf(true);
	}

	public static boolean expire(String key,int time){
		try {
			MemoryConfig.getMem().touch(key,time);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			return false;
		}
		return Boolean.valueOf(true);
	}


	public static Boolean Delete(String key) {
		try {
			MemoryConfig.getMem().delete(key);
		} catch (Exception ex) {
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public static Boolean Set(String key, String value) {
		try {
			MemoryConfig.getMem().set(key, 0, value);
		} catch (Exception ex) {
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public static Boolean SetObj(String key, Object value, int time) {
		try {
			MemoryConfig.getMem().set(key, time, value);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}
	
	public static Boolean SetObjNoReply(String key, Object value, int time) {
		try {
			MemoryConfig.getMem().setWithNoReply(key, time, value);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}


	public static Boolean SetObj(String key, Object value) {
		try {
			MemoryConfig.getMem().set(key, 0, value);
		} catch (Exception ex) {
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}
	
	public static String Get(String key) {
		try {
			return (String) MemoryConfig.getMem().get(key);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		return null;
	}
	
	public static <E> Map<String,GetsResponse<E>> Get(Collection<String> keyCollections) {
		try {
			return MemoryConfig.getMem().gets(keyCollections);
			
//			return MemoryConfig.getMem().gets(keyCollections);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		return null;
	}
	
	
	public static <E> Map<String,GetsResponse<E>> gets(Collection<String> keyCollections) {
		try {
			return MemoryConfig.getMem().get(keyCollections);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		return null;
	}
	
	public static long incr(String key, long delta){
		try {
			return MemoryConfig.getMem().incr(key , delta);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		return 0;
	}
	
	public static long incr(String key, long delta , long initValue){
		try {
			return MemoryConfig.getMem().incr(key , delta , initValue);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		return 0;
	}

	public static Object GetObj(String key) {
		// log.info("get:" + key + ":");
		try {
			return MemoryConfig.getMem().get(key);
		} catch (Exception ex) {
		}
        return null;
	}
	
	
	public static <E> E T(String key) {
		try {
			return MemoryConfig.getMem().get(key);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		return null;
	}
	
	/**
	 * Prepend value to key's data item in memcached.This method doesn't wait for reply.
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean prepend(String key, Object value){
		try {
			return MemoryConfig.getMem().prepend(key , value);
		} catch (Exception ex) {
		}
		return false;
	}

	/**
	 * append value to key's data item in memcached.This method doesn't wait for reply.
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean append(String key, Object value){
		try {
			return MemoryConfig.getMem().append(key , value);
		} catch (Exception ex) {
		}
		return false;
	}
}