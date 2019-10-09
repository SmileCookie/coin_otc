package com.dynamic.param;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.dynamic.param.data.DynamicParamsDao;
import com.dynamic.param.data.DynamicParamsEntity;
import com.world.cache.Cache;
import org.apache.log4j.Logger;

/*****
 * 动态参数类hash表保存处理    
 * 工厂类、用于产生动态参数类库、并可以返回需要的实体类、供具体业务调用
 * @author apple
 *
 */
public class DynamicParamsClassMapFactory {

	private final static Logger log = Logger.getLogger(DynamicParamsClassMapFactory.class.getName());
	
	///可动态加载配置类所在的路径
	private static String basePkg = "com.world.config.bean";
	private static HashMap<String, BaseDynamicParamsConfig> beanMaps = null;//配置类集合
	private static DynamicParamsDao dpDao = null;
	
	private static final String cacheDynamicParamsMapKey = "dynamic_params_map_key";
	
	/****
	 * 根据类获取内存中的对象数据
	 * @param c
	 * @return
	 */
	public static BaseDynamicParamsConfig getConfig(Class c){
		if(beanMaps == null){
			init();
		}
		
		if(beanMaps != null){
			///先从缓存服务器中取出保存的参数配置
			Map<Object , DynamicParamsEntity> storageMaps = (Map<Object, DynamicParamsEntity>) Cache.GetObj(cacheDynamicParamsMapKey);
			
			if(storageMaps != null){
				//检查java内存中的配置和缓存中的是否一致
				for (Map.Entry<String, BaseDynamicParamsConfig> e : beanMaps.entrySet()){
					BaseDynamicParamsConfig bdp = e.getValue();
					
					DynamicParamsEntity dpe = storageMaps.get(e.getKey());
					if(dpe != null && dpe.getConfig() != null){
						BaseDynamicParamsConfig bdpc = dpe.getConfig();
						if(dpe.getVersion() != bdp.getVersion()){//版本变化了
							bdpc.setVersion(dpe.getVersion());
							beanMaps.put(e.getKey(), bdpc);
						}
					}
				}
			}
			
		}
		
		return beanMaps.get(c.getName());
	}
	/***
	 * 同步数据到memcache
	 */
	public synchronized static void syncDataToCache(){
		if(beanMaps == null){
			init();
		}
		if(dpDao == null){
			dpDao = new DynamicParamsDao();
		}
		if(beanMaps != null){
			//保存的数据
			Map<Object , DynamicParamsEntity> storageMaps = dpDao.getMapByField("name", dpDao.getQuery());
			Cache.SetObj(cacheDynamicParamsMapKey , storageMaps);
			
			if(beanMaps != null){
				///先从缓存服务器中取出保存的参数配置
				//检查java内存中的配置和缓存中的是否一致
				for (Map.Entry<String, BaseDynamicParamsConfig> e : beanMaps.entrySet()){
					BaseDynamicParamsConfig bdp = e.getValue();
					
					DynamicParamsEntity dpe = storageMaps.get(e.getKey());
					if(dpe == null || dpe.getConfig() == null){
						initEntity(bdp);
					}
				}
				
			}
		}
	}
	/****
	 * 当数据库中还没有保存有此类信息时的初始化
	 * @param bdp
	 */
	public static void initEntity(BaseDynamicParamsConfig bdp){
		DynamicParamsEntity dpe = new DynamicParamsEntity(dpDao.getDatastore());
		dpe.setName(bdp.getClass().getName());
		dpe.setVersion(System.currentTimeMillis());
		dpe.setConfig(bdp);
		dpDao.save(dpe);
	}
	
	/****
	 * 首次使用初始化
	 */
	private synchronized static void init(){
		if(beanMaps == null){//未初始化
			beanMaps = new HashMap<String, BaseDynamicParamsConfig>();
			String basePath = basePkg.replace(".", "/");
			String src = DynamicParamsClassMapFactory.class.getClassLoader().getResource("").getPath();
			String baseDir = src+basePath;
			File f = new File(baseDir);
			readPck(f);
		}
	}
	
	/***
	 * 添加一个新的动态参数类
	 * @param bpc
	 */
	public static synchronized void add(BaseDynamicParamsConfig bpc){
		beanMaps.put(bpc.getClass().getName(), bpc);
	}
	
	private static void readPck(File f){
		File[] fs = f.listFiles();
		for(File cf : fs){
			if(cf.isDirectory()){
				readPck(cf);
			}else{
				String basePath = basePkg.replace(".", "/");//"com/world/config/bean";
				//
				String path = cf.getPath().replace("\\", "/");
				String classPath = path.substring(path.indexOf(basePath)).replace("/", ".").replace(".class", "");//
				
				Class cls = null;
				try {
					cls = Class.forName(classPath);
					Object obj;
					obj = cls.newInstance();
					
					if(obj instanceof BaseDynamicParamsConfig){
						add((BaseDynamicParamsConfig)obj);
					}
				} catch (ClassNotFoundException e1) {
					log.error(e1.toString(), e1);
				}catch (InstantiationException e) {
					log.error(e.toString(), e);
				} catch (IllegalAccessException e) {
					log.error(e.toString(), e);
				} catch (IllegalArgumentException e) {
					log.error(e.toString(), e);
				}
				
			}
		}
	}
}
