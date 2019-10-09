package com.world.system.tips;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.world.config.GlobalConfig;
import com.world.system.model.TipsMsg;
import com.world.system.model.TipsMsgDao;
import com.world.util.date.TimeUtil;
import com.world.web.Pages;
import com.world.web.ViewCode;
import com.world.web.ViewerType;
import org.apache.log4j.Logger;

public class SysTipsManager {

	private static Logger log = Logger.getLogger(SysTipsManager.class.getName());

	private static Map<Method , List<SysTipType>> conns = null;
	
	private SysTipType[] types;
	
	private static SysTipsManager sysTipsManager;
	
	static TipsMsgDao dao = null;
	
	public synchronized static SysTipsManager getSysTipsManager(){
		if(sysTipsManager == null){
			sysTipsManager = new SysTipsManager();
		}
		return sysTipsManager;
	}
	
	private SysTipsManager(){
	}
	
	/***
	 * 初始化方法提示关系
	 */
	public void initTypes(){
		synchronized (SysTipsManager.class) {
			if(conns != null){
				return;
			}
			
			try {
				Class<?> clazz = Class.forName(GlobalConfig.sysMsgTipsClass);
		        if(clazz.isEnum()){  
		            Object[] enumObjs = clazz.getEnumConstants();  
		            
		            if(enumObjs.length > 0){
		            	Object eobj = enumObjs[0];
		            	try {
							Method method = clazz.getMethod("values", new Class[]{});
							
							types = (SysTipType[]) method.invoke(eobj, new Object[]{});
						} catch (NoSuchMethodException e) {
							log.error(e.toString(), e);
						} catch (SecurityException e) {
							log.error(e.toString(), e);
						} catch (IllegalArgumentException e) {
							log.error(e.toString(), e);
						} catch (InvocationTargetException e) {
							log.error(e.toString(), e);
						}
		            	
		            }
		             
		        } 
			} catch (ClassNotFoundException e1) {
				log.error(e1.toString(), e1);
			} catch (IllegalAccessException e) {
				log.error(e.toString(), e);
			}
			
			conns = new HashMap<Method , List<SysTipType>>();
			if(types != null && types.length > 0){
				for(SysTipType type : types){
					Method[] mts = null;
					try {
						mts = type.getMethods();
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
					if(mts != null && mts.length > 0){
						for(Method m : mts){
							List<SysTipType> cstts = conns.get(m);
							if(cstts == null){
								cstts = new ArrayList<SysTipType>();
							}
							cstts.add(type);
							conns.put(m, cstts);
						}
					}
				}
			}
		}
		
	}
	/***
	 * 获取当前方法包含的提示
	 * @param method
	 * @return
	 */
	public List<SysTipType> getSysTipTypeByMethod(Method method){
		if(conns == null){
			initTypes();
		}
		return conns.get(method);
	}
	
	
	public void send(SysTipType sysTipType){
		if(dao == null){
			dao = new TipsMsgDao();
		}
		TipsMsg msg = new TipsMsg(dao.getDatastore());
		msg.setContent(sysTipType.getCont());
		msg.setStatus(0);
		msg.setSeeTimes(0);
		msg.setAddTime(TimeUtil.getNow());
		dao.save(msg);
		//log.info(sysTipType.getCont() + "提示消息发送");
	}
	/***
	 * 多类型提示
	 * @param sysTipTypes
	 */
	public void sends(Pages p){
		ViewCode vc = p.urlViewCode.viewCode;
		String responseStr = p.getResponseStr();
		if(vc.viewerType == ViewerType.XML){
			if(!responseStr.contains("<State>true</State>")){
				return;
			}
		}else if (vc.viewerType == ViewerType.JSON){
			//"isSuc" : false
			if(!responseStr.contains("\"isSuc\" : true")){
				return;
			}
		}
		List<SysTipType> sysTipTypes = vc.sysTips;
		for(SysTipType stt : sysTipTypes){
			send(stt);
		}
		p = null;
	}
}
