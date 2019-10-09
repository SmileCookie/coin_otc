package com.world.web.competence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.world.web.ViewCode;
import com.world.web.action.Action;
import com.world.web.cache.page.CachePage;

/****
 * 功能函数管理类
 * @author Administrator
 *
 */
public class FunctionManager implements Action{

	private static Map<String , FunctionGroup> groups = new LinkedHashMap<String, FunctionGroup>();///保存功能分组  每个类为一组
	public static Map<String , FunctionGroup> plateGroups = new LinkedHashMap<String, FunctionGroup>();//有板块区分的分组
	public static Map<String , CachePage> cachePages = new LinkedHashMap<String, CachePage>();//有板块区分的分组
	
	public static Map<String , Function> functions = new LinkedHashMap<String, Function>();
	
	public static Map<String , FunctionGroup> getGroups(){
		return groups;
	}
	/***
	 * 添加 ViewCode
	 * @param vc
	 */
	public static synchronized void putViewCode(ViewCode vc , String jspBasePkg , String des , boolean plate){
		if(vc.cacheTime > 0){
			CachePage cp = new CachePage();
			cp.setCacheTimes(vc.cacheTime);
			cp.setUrl(vc.getPath());
			cachePages.put(vc.getPath(), cp);
		}
		if (vc.name.indexOf("index") >= 0 || vc.name.indexOf("com.world.controller.admin") < 0)return;
		if(des == null || des.length() <= 0){
			des = vc.getMethod().getName();
		} 
		
		FunctionGroup fg = groups.get(vc.getClassUrl());
		List<Function> funcs = null;
		if(fg == null){
			fg = new FunctionGroup();
			fg.setClassType(vc.getClassType());
			fg.setDes(des);///包描述
			fg.setPlate(plate);
			funcs = new ArrayList<Function>();
		}else{
			funcs = fg.getFunctions();
		}
		Function f = new Function(vc.getPath(), vc, fg , getDes(vc.getMethod().getName() , vc.des));
		funcs.add(f);
		fg.setFunctions(funcs);
		if(fg.isPlate()){
			plateGroups.put(fg.getUrl() , fg);
		}
		
		functions.put(f.getUrl(), f);
		groups.put(fg.getUrl(), fg);
	}
	
	/****
	 * 获取功能描述
	 * @return
	 */
	public static String getDes(String name , String methodDes){
		if(methodDes.length() <= 0){
			if(name.equals(INDEX_METHOD[0])){//默认
				return INDEX_METHOD[1];
			}else if(name.equals(AJAX_METHOD[0])){
				return AJAX_METHOD[1];
			}else if(name.equals(AORU_METHOD[0])){
				return AORU_METHOD[1];
			}else if(name.equals(DOAORU_METHOD[0])){
				return DOAORU_METHOD[1];
			}else if(name.equals(DODEL_METHOD[0])){
				return DODEL_METHOD[1];
			}else{
				return name;
			}
		}else{
			return methodDes;
		}
	}
	
}
