package com.world.web.proxy;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.log4j.Logger;

import com.world.web.Pages;
import com.world.web.UrlViewCode;

public class ActionProxy {

	private static Logger log = Logger.getLogger(ActionProxy.class);

	public static Object doMethod(Object object, Object[] args,Invoker methodInvoker) throws Throwable {
		Pages obj = (Pages) object;
		obj.BaseInit((ServletContext)args[0], (HttpServletRequest)args[1], (HttpServletResponse)args[2], (UrlViewCode)args[3]);
//		Object result = 
//		obj.toJson();
		return null;
	}

}
