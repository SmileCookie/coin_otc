package com.world.controller.u;

import com.world.web.action.BaseAction;

public class Index extends BaseAction {

//	@Page(Viewer = "/cn/u/index.jsp" , Cache = 60)
	public void index() { 
		// log.info("==========首页================url：" + request.getRequestURI()+",IP:" + WebUtil.getIp(request));
		// 这里测试多语言 设置本地域名 en.btcbt.com us.btcbt.com www.btcbt.com
		// 指向127.0.0.1,然后分别访问 jsp会自动切换到cn目录，语言包会被重新加载
		try {
		//	String test=L("发送邮箱验证");
			
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

}