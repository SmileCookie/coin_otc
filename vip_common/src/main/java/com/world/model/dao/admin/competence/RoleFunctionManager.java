package com.world.model.dao.admin.competence;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.world.model.entity.admin.competence.MenuViewFunction;
import com.world.util.WebUtil;
/****
 * 角色  - 功能管理类
 * 给定角色ID可以获取到当前角色的功能列表
 * @author Administrator
 *
 */
public class RoleFunctionManager {
	
	static Logger log = Logger.getLogger(RoleFunctionManager.class.getName());
	private static Map<String , Map<Object , MenuViewFunction>> roleFunctions = new LinkedHashMap<String, Map<Object,MenuViewFunction>>();
	
	public static Map<String , Map<Object , MenuViewFunction>> getRoleFunctions(){
		return roleFunctions;
	}
	
	public synchronized static void refreshFunctionsByRoleId(String roleId){
		roleFunctions.put(roleId, null);
		roleFunctions.put(roleId, getFromDb(roleId));
		
		log.info("当前roleFunctions大小:" + WebUtil.objectSize(roleFunctions));
	}
	
	public synchronized static Map<Object , MenuViewFunction> getFromDb(String roleId){
		MenuViewFunctionDao mvfDao = new MenuViewFunctionDao();
		Map<Object , MenuViewFunction> mvfs = mvfDao.getMapByField("url", mvfDao.getQuery().filter("roleId =", roleId));
		return mvfs;
	}
	/****
	 * 获取当前角色拥有的所有功能后 ， 缓存到虚拟机中
	 * 考虑到集群的需要，可以将此数据保存到集群环境的memcached中
	 * @param roleId  角色ID
	 * @return 当前角色拥有的所有功能
	 */
	public static Map<Object , MenuViewFunction> getFunctionsByRoleId(String roleId){
		Map<Object , MenuViewFunction> fss = roleFunctions.get(roleId);
		if(fss == null){
			refreshFunctionsByRoleId(roleId);//重新加载
		}
		return roleFunctions.get(roleId);
	}
	/*****
	 * 
	 * @param url
	 * @param roleId
	 * @return
	 */
	public static MenuViewFunction getFunction(String url , String roleId){
		Map<Object , MenuViewFunction> frs = getFunctionsByRoleId(roleId);
		return frs.get(url);
	}
}
