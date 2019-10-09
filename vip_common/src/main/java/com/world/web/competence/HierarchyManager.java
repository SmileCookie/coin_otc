package com.world.web.competence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HierarchyManager {

	private static GroupHierarchy lastHierachy = null;//最外层次
	private static String rootPath = "/admin";
	
	private synchronized static void init(){//初始化
		if(lastHierachy == null){
			lastHierachy = new GroupHierarchy();
			lastHierachy.setGroup(FunctionManager.getGroups().get(rootPath));
			initHierarchys(lastHierachy);
		}
	}
	/****
	 * 当前层次初始化
	 * @param gh
	 */
	private static void initHierarchys(GroupHierarchy gh){
		Set<String> urls = FunctionManager.getGroups().keySet();///urls组
		List<GroupHierarchy> sonHierarchys = new ArrayList<GroupHierarchy>();//子层次
		
		for(String s : urls){
			if(s.substring(0, s.lastIndexOf("/")).equals(gh.getGroup().getUrl())){
				FunctionGroup fg = FunctionManager.getGroups().get(s);
				///当前子
				GroupHierarchy sgh = new GroupHierarchy();
				sgh.setDes(fg.getDes());
				sgh.setGroup(fg);
				sonHierarchys.add(sgh);
				
				///递归其子层次
				initHierarchys(sgh);
			}
		}
		gh.setSonHierarchys(sonHierarchys);
	}
	
	public static GroupHierarchy getLastHierachy(){
		if(lastHierachy == null) init();
			return lastHierachy;
	}
	
}
