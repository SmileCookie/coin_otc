package com.world.netty.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.util.MethodUtil;

public class ClientAsynCallBack {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3562151611327531972L;
	private static Logger log = Logger.getLogger(ClientAsynCallBack.class);
	
	private final static ConcurrentHashMap<String, Object> targetCacheMap       = new ConcurrentHashMap<String, Object>();
	private final static ConcurrentHashMap<String, Method> methodCacheMap       = new ConcurrentHashMap<String, Method>();
	    
	private static ExecutorService executorService = Executors.newFixedThreadPool(5);

	public static void execute(final Object tagerObject,final String method,final Object... params){
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long s1 = System.currentTimeMillis();
				call(tagerObject,method,params);
				long s2 = System.currentTimeMillis();
				log.info(((Class) tagerObject).getSimpleName()+"."+method+" excute耗时:"+(s2-s1));
				
			}
		});
		
	}
	
	
	 private static boolean call(Object tagerObject, String method,Object... params ){
		 try{
		        if (tagerObject == null){
		         //   throw new RuntimeException("target object is null");
		        	log.error("target object is null");
		        	return false;
		        }
		        boolean cache = true;
		        Object target = null;
		        String classKey = ((Class) tagerObject).getSimpleName();
	            if (cache) {
	                target = targetCacheMap.get(classKey);
	                if (target == null) {
	                    target = newObject((Class) tagerObject);
	                    targetCacheMap.put(classKey, target);
	                }
	            } else {
	                target = newObject((Class) tagerObject);
	            }
		        
		        Class clazz = target.getClass();
		        
		        //获取方法，先从缓存里面获取。如取不到再通过反射找到方法对象，存入缓存
		        String methodKey = MethodUtil.getClassMethodKey(clazz, params, method);
		        Method targetMethod = methodCacheMap.get(methodKey);
		        if (targetMethod == null) {
		            targetMethod = MethodUtil.getTargetMethod(clazz, params, method);
		            if (targetMethod != null) {
		                methodCacheMap.put(methodKey, targetMethod);
		            }
		        }

		        
		       // Method  targetMethod = MethodUtil.getTargetMethod(clazz, params, method);
		        if (targetMethod == null) {
		            //throw new IllegalArgumentException("target method is null" + clazz);
		        	log.error(method+": target method is not find in " + clazz);
		        	return false;
		        }
		        Object result = targetMethod.invoke(clazz, params);
		 }catch(Exception e){
			 log.error(e.toString(), e);
			 return false;
		 }
	      return true;

	  }
	 
	/**
	 * 根据类对象构造器创建实例对象返回 
	 * @param clzss
	 * @return Object
	 * @author zhanglinbo 20160624
	 */
	private static Object newObject(Class clzss) {
	        try {
	            Constructor constructor = clzss.getConstructor();
	            if (constructor == null) {
	                throw new IllegalArgumentException("target not have default constructor function");
	            }
	            // Instance target object
	            return clzss.newInstance();
	        } catch (Exception e) {
	            log.error(e.toString(), e);
	            return null;
	        }
	    }

}
