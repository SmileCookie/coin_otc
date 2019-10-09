package com.world.web;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.locks.Lock;

import com.world.config.GlobalConfig;
import com.world.system.tips.SysTipType;
import com.world.web.convention.annotation.ActionCache;
import com.world.web.proxy.Invoker;

public class ViewCode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5772488038226624605L;
	public String name;
	public Class<?> classType;
	public Method method;
	public Method basemethod;
	public String viewerPath = "";
	public ViewerPathType viewerPathType = ViewerPathType.DEFAULT;
	public ViewerType viewerType = ViewerType.DEFAULT;
	public Invoker methodInvoker;
	public Object obj;

	public int cacheTime = 0;

	public long count = 0L;

	public boolean compress = false;
	public String des = "";//功能（方法）描述
	public boolean plate = false;//标记当前函数 权限划分中是否有板块限制   譬如某角色只能管理产品中的某些分类
	public boolean saveLog = false;
	public boolean ipCheck = false;
	public List<SysTipType> sysTips = null;
	public Lock lock = null;
	public ActionCache actionCache = null;
	
	public boolean isPlate() {
		return plate;
	}
	////com.world.controller.admin.index 
	public String getPath(){
		return name.replace(GlobalConfig.basePckPath, "").replace(".", "/").replace("/index", "");
	}
	//类url路径
	public String getClassUrl(){
		return classType.getName().toLowerCase().replace(GlobalConfig.basePckPath, "").replace(".", "/").replace("/index", "");
	}

	public String getName() {
		return name;
	}

	public Class<?> getClassType() {
		return classType;
	}


	public Method getMethod() {
		return method;
	}

	public Method getBasemethod() {
		return basemethod;
	}


	public String getViewerPath() {
		return viewerPath;
	}

	public ViewerPathType getViewerPathType() {
		return viewerPathType;
	}


	public ViewerType getViewerType() {
		return viewerType;
	}

	public int getCacheTime() {
		return cacheTime;
	}

	public long getCount() {
		return count;
	}

	public boolean isCompress() {
		return compress;
	}
}