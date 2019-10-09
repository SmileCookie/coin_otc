package com.world.web.action;

import com.world.web.Pages;

public class ApiAction extends Pages{
	private String[] versions = new String[]{"v1"};	//可以使用的版本，根据包名
	
	public boolean checkVersion(){
		try {
			String packageName = this.getClass().getPackage().getName();
			String nowVersion = packageName.split("\\.")[packageName.split("\\.").length-1];
//			log.info("当前版本=" + nowVersion);
			
			boolean isOpen = false;
			for(String version : versions){
				if(version.equals(nowVersion) ){
					isOpen = true;
				}
			}
			
			//如果versions这个常量中，不包含当前版本，就提示版本已经关闭
			if(!isOpen){
				response.setContentType("text/javascript");
				response.getWriter().write("{\"result\":false,\"message\":\"当前版本已经关闭\"}");
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
		return true;
	}
}
