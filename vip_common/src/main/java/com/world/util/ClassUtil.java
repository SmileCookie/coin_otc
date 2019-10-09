package com.world.util;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class ClassUtil {

	private static Logger log = Logger.getLogger(ClassUtil.class.getName());

	public static Object getValByField(Class entityClazz , String field , Object o){
		Object result = null;
		Method[] mss = entityClazz.getMethods();
    	for(Method m : mss){
    		if(m.getName().equalsIgnoreCase("get"+field)){
    			try {
					result = m.invoke(o, new Object[]{});
					break;
				} catch (IllegalArgumentException e) {
					log.error(e.toString(), e);
				} catch (IllegalAccessException e) {
					log.error(e.toString(), e);
				} catch (InvocationTargetException e) {
					log.error(e.toString(), e);
				}
    		}
    	}
    	return result;
	}
	
	public static Object setValByField(Class entityClazz , String field , Object o , Object newVal){
		Object result = null;
		Method[] mss = entityClazz.getMethods();
    	for(Method m : mss){
    		if(m.getName().equalsIgnoreCase("set"+field)){
    			try {
					result = m.invoke(o, new Object[]{newVal});
					break;
				} catch (IllegalArgumentException e) {
					log.error(e.toString(), e);
				} catch (IllegalAccessException e) {
					log.error(e.toString(), e);
				} catch (InvocationTargetException e) {
					log.error(e.toString(), e);
				}
    		}
    	}
    	return result;
	}
	
	public static Object invokeByClassAndMethod(Class<?> entityClazz , String method , Object[] params){
		Object result = null;
		try {
			Object o = entityClazz.newInstance();
			Method[] mss = entityClazz.getMethods();
	    	for(Method m : mss){
	    		if(m.getName().equalsIgnoreCase(method)){
	    			try {
						result = m.invoke(o, params);
					} catch (IllegalArgumentException e) {
						log.error(e.toString(), e);
					} catch (IllegalAccessException e) {
						log.error(e.toString(), e);
					} catch (InvocationTargetException e) {
						log.error(e.toString(), e);
					}
	    		}
	    	}
		} catch (InstantiationException e1) {
			log.error(e1.toString(), e1);
		} catch (IllegalAccessException e1) {
			log.error(e1.toString(), e1);
		}
    	return result;
	}
	
	public static BigDecimal getDecimalValByField(Class entityClazz , String field , Object o){
		return getBigDecimal(getValByField(entityClazz, field, o));
	}
	public static BigDecimal getBigDecimal(Object param) {
		BigDecimal bd = null;
		if (param instanceof Integer) {
			int value = ((Integer) param).intValue();
			bd = new BigDecimal(value);
		} else if (param instanceof Double) {
			double d = ((Double) param).doubleValue();
			bd = new BigDecimal(d);
		} else if (param instanceof Float) {
			float f = ((Float) param).floatValue();
			bd = new BigDecimal(f);
		} else if (param instanceof Long) {
			long l = ((Long) param).longValue();
			bd = new BigDecimal(l);
		}

		return bd;
	}
	
	public static void printEntity(Object obj, Class clzz){
		try {
			Method[] methods = clzz.getMethods();
			for(int i=0; i<methods.length; i++){
				Method method = methods[i];
				if(method.getName().startsWith("get") && method.getParameterTypes().length==0){
					log.info("变量名：" + method.getName() + "\t值：" + method.invoke(obj, null));
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
}
