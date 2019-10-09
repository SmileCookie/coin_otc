package com.world.web.action;

import com.file.config.FileConfig;

/***
 * 
*   
* 类名称：Action  
* 类描述：  定义action的接口
* 创建人：Administrator  
* 创建时间：2012-5-15 下午09:27:58  
* 修改备注：  
* @version   
*
 */
public interface Action{

	String DEFAULT_ENCODE = "UTF-8";
	String DEFAULT_INDEX = "list.jsp";
	String DEFAULT_AJAX = "ajax.jsp";
	String DEFAULT_AORU = "aoru.jsp";
	
	String INDEX = "index.jsp";
	
	String JSP = ".jsp";
	String XML = ".xml";
	String JSON = ".json";
	
	String[] INDEX_METHOD = new String[]{"index" , "查看"};//管理
	String[] AJAX_METHOD = new String[]{"ajax" , "ajax"};//ajax加载
	String[] AORU_METHOD = new String[]{"aoru" , "添加/修改视图"};//添加修改视图
	String[] DOAORU_METHOD = new String[]{"doAoru" , "添加/修改"};//添加/修改功能
	String[] DODEL_METHOD = new String[]{"doDel" , "删除"};//删除
	
	String WEB_NAME = "Btcwinex";
	
	String MAIN_DOMAIN = FileConfig.getValue("main");
	String STATIC_DOMAIN = FileConfig.getValue("static");
	String VIP_DOMAIN = FileConfig.getValue("vip");
	String P2P_DOMAIN = FileConfig.getValue("p2p");
	String TRANS_DOMAIN = FileConfig.getValue("trans");
	String MOBILE_DOMAIN = FileConfig.getValue("mobile");
	
	String CONTENT_TEXT_XML = "text/xml;charset=UTF-8";
	String CONTENT_TEXT_JAVASCRIPT = "text/javascript;charset=utf-8";
	String CONTENT_TEXT_HTML = "text/html;charset=utf-8";
}
