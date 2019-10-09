package com.world.util.page;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.world.util.language.LanguageTag;


/**
 * 功能：分页通用类
 * @author 
 */
public class Paging implements Serializable{
	private final static String PAGE_KEY = "page";
	
	private int pagesize=5;   //每页显示数
	private int start=1;	  //开始条数
	private int end=1;		  //结束条数 
	private int sum;		  //总数
	private int curpage=1;    //当前页
	private int pages=1;	  //总页数
	private String url="";   //换url的前半部分
	private HttpServletRequest request;
	private LanguageTag lan;//语种
	
	private PageTag pt;

	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getShowItems() {
		return showItems;
	}
	public void setShowItems(int showItems) {
		this.showItems = showItems;
	}
	
	public boolean isShowSearch() {
		return isShowSearch;
	}
	public void setShowSearch(boolean isShowSearch) {
		this.isShowSearch = isShowSearch;
	}
	public boolean isShowSide() {
		return isShowSide;
	}
	public void setShowSide(boolean isShowSide) {
		this.isShowSide = isShowSide;
	}

	private int showItems=3;  //居中显示的两边显示最多多少项
//	private int sideShowItems=2;  //两边显示的数量,在省略号最前边显示的数量
	private boolean isShowSearch=true; //要不要显示查询跳转功能
	private boolean isShowSide=true; //要不要显示两边省略号外部分
	private String params;
	
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public int getCurpage() {
		return curpage;
	}
	public void setCurpage(int curpages) {
		if(curpages<1)curpage=1;
		if(curpages>this.getPages())curpage=this.getPages();
		this.curpage = curpages;
	}
	public int getEnd() {
		end=this.getStart()+pagesize;
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getPages() {
		if(sum%pagesize==0){
			pages=sum/pagesize;
		}else{
			pages=sum/pagesize+1;
		}
		return pages;
	}
	public void setPages(int pages) {
		this.pages = pages;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public int getStart() {
		start=(this.getCurpage()-1)*pagesize+1;
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	/**
	 * 直接函数的形式调用这个pager组件,直接返回完成的结果,简化参数默认配置版本
	 * @param url 基url
     * @param _pagesize 页面大小
	 * @param _curpage 当前页码
	 * @param _setSum 总数量
	 * @return 返回最终的url结果
	 */
	public String getPaper(int _pagesize,int _curpage,int _setSum , HttpServletRequest request,LanguageTag lan){
		return getPaper(_pagesize,_curpage,_setSum,2,true,false,request,lan); 
	}
	
	public String getPaper(int _pagesize,int _curpage,int _setSum , HttpServletRequest request,LanguageTag lan,String params){
		this.params = params;
		return getPaper(_pagesize,_curpage,_setSum,2,true,false,request,lan); 
	}
	/**
	 * 直接函数的形式调用这个pager组件,直接返回完成的结果
	 * @param _cleanUrl 干净的url
	 * @param _pagesize 页面大小
	 * @param _curpage 当前页码
	 * @param _setSum 总数量
	 * @param _showItems 紧靠被选项两边的显示个数
	 * @param _isShowSearch 是否显示搜索部分
	 * @param _isShowSide 是否显示两边的两个导航
	 * @return 返回最终的url结果
	 */
	public String getPaper(int _pagesize,int _curpage,int _setSum,int _showItems,boolean _isShowSearch,boolean _isShowSide,HttpServletRequest request,LanguageTag lan)
	{
		//this.setPages(_pagesize);
		this.setPagesize(_pagesize);
		this.setCurpage(_curpage);
		this.setSum(_setSum);
		this.setShowSide(_isShowSide);
		this.setShowItems(_showItems);
		this.setShowSearch(_isShowSearch);
		this.setRequest(request);
		this.lan = lan;
		this.pt = lan.getPt();
		
		return getPaper(); 
	}
	
	/*****  2011  4-14    分页按钮  修改
	 *  <div class="pagination">
        <span class="stat">共100项 1/10页</span>
        <span class="Pbtn first"></span>
        <span class="Pbtn pre"></span>
        <a class="Pbtn first" href="#"></a>
        <a class="Pbtn pre" href="#"></a>
        <a class="num current" href="#">1</a>
        <a class="num" href="#">2</a>
        <a class="num" href="#">3</a>
        <span class="ellipsis">...</span>
        <a class="Pbtn next" href="#"></a>
        <div class="go_page">
        <span>到第</span>
        <input type="text" name="textfield2" id="textfield2" />
        <span>页</span>
        <a href="#" class="Pbtn jump"></a>
        </div>
        </div>

	 */
	public String getPaper(){
		String curUri = request.getRequestURI(),//  /pay/inout/ajax
			   baseUrl = getBaseUrl(curUri),
		       ajaxUrl = baseUrl + "/ajax";
		if(curUri.indexOf("/ajaxList")>=0){
			ajaxUrl = baseUrl + "/ajaxList";
		}
		
		if(params != null){
			ajaxUrl = ajaxUrl.replace("/" + params, "");
			ajaxUrl += "/" + params;
		}
		
		String curUrl = "",
			   curUrlAjax = "";
		Map rm = new HashMap(request.getParameterMap());
		
		rm.put(PAGE_KEY, 1);
		String firstUrl = baseUrl+"?"+toStringMap(rm);//第一页
		String firstUrlAjax = ajaxUrl+"?"+toStringMap(rm);//ajax第一页
		
		rm.put(PAGE_KEY, curpage-1);//
		String preUrl = baseUrl+"?"+toStringMap(rm);//"+pt.getShangYiYe()+"
		String preUrlAjax = ajaxUrl+"?"+toStringMap(rm);//"+pt.getShangYiYe()+"
		
		rm.put(PAGE_KEY, curpage+1);//
		String nextUrl = baseUrl+"?"+toStringMap(rm);//"+pt.getXiaYiYe()+"
		String nextUrlAjax = ajaxUrl+"?"+toStringMap(rm);//"+pt.getXiaYiYe()+"
		
		
		if(getPages() <= 1){
			return "";
		}
		
		//初始化一个字符串缓冲区
		StringBuffer bar=new StringBuffer();
		bar.append("<span class=\"page_num\">"+pt.getGong()+" "+sum+" "+pt.getXiang()+"</span>");
		
		//显示页码
		bar.append("<span class=\"stat\">"+curpage+"/"+getPages()+""+pt.getYe()+"</span>");
		/**
		 * 按钮
		 */
		//如果是第一页,定制按钮
		if(curpage == 1){
			bar.append("<span class=\"Pbtn first\">"+pt.getDiYiYe()+"</span><span class=\"Pbtn pre\"><i>&lt;</i> "+pt.getShangYiYe()+"</span>");
		}else{
			bar.append("<a class=\"Pbtn first\" onclick=\"vip.list.ajaxPage({url:'"+firstUrlAjax+"'} , event)\"  href=\""+firstUrl+"\">"+pt.getDiYiYe()+"</a>").append("<a class=\"Pbtn pre\" onclick=\"vip.list.ajaxPage({url:'"+preUrlAjax+"'} , event)\"  href=\""+preUrl+"\"><i>&lt;</i> "+pt.getShangYiYe()+"</a>");
		}
		/*
		 * 前面部分
		 */ 
		if(curpage>(showItems+1)){//如果前面是满的
			int start=curpage-showItems;
			if((curpage+showItems)>getPages()){
				start=getPages()-2*showItems;
				if(start<=0)
					start=1;
			}
			else
				start=curpage-showItems;
			if(start>1)
			  bar.append("<span class=\"ellipsis\">...</span>");
			
		    for(int i=start;i<curpage;i++){
		       rm.put(PAGE_KEY, i);
			   curUrl = baseUrl+"?"+toStringMap(rm);//当前页
			   curUrlAjax = ajaxUrl+"?"+toStringMap(rm);//当前页 ajax
			   bar.append("<a href=\""+curUrl+"\" onclick=\"vip.list.ajaxPage({url:'"+curUrlAjax+"'} , event)\"  class=\"num\" >"+i+"</a>");
		    }
		}else{
			 for(int i=1;i<curpage;i++){
				 rm.put(PAGE_KEY, i);
				 curUrl = baseUrl+"?"+toStringMap(rm);//当前页
				 curUrlAjax = ajaxUrl+"?"+toStringMap(rm);//当前页 ajax
				 bar.append("<a href=\""+curUrl+"\" class=\"num\" onclick=\"vip.list.ajaxPage({url:'"+curUrlAjax+"'} , event)\"  >"+i+"</a>");
			  }
		}
		//中间部分
		bar.append("<a class='num current' onclick=\"newUrl(event,this)\" >"+curpage+"</a>");
		//后面部分
	
		if(getPages()>(curpage+showItems))
		{
			int end=showItems;
			if(curpage<=showItems)//刚开始，左边本身没有显示完全
			{
				if(getPages()>(2*showItems+1))
					end=2*showItems+2;
				else
					end=getPages()+1;
			}
			else
				end=curpage+showItems+1;
			for(int i=(curpage+1);i<end;i++){
				rm.put(PAGE_KEY, i);
				curUrl = baseUrl+"?"+toStringMap(rm);//当前页
				curUrlAjax = ajaxUrl+"?"+toStringMap(rm);//当前页 ajax
		       	bar.append("<a href=\""+curUrl+"\" onclick=\"vip.list.ajaxPage({url:'"+curUrlAjax+"'} , event)\"  class=\"num\" >"+i+"</a>");
		    }
			if(getPages()>(2*showItems+1))
		      bar.append("<span class=\"ellipsis\">...</span>");
		}
		else
		{
			for(int i=(curpage+1);i<(getPages()+1);i++){
				rm.put(PAGE_KEY, i);
				curUrl = baseUrl+"?"+toStringMap(rm);//当前页
				curUrlAjax = ajaxUrl+"?"+toStringMap(rm);//当前页 ajax
			  	bar.append("<a onclick=\"vip.list.ajaxPage({url:'"+curUrlAjax+"'} , event)\"  href=\""+curUrl+"\" class=\"num\" >"+i+"</a>");
			  }
		}
		/**
		 * 最后一页
		 */
		//如果是最后一页
		if(curpage == pages){
			bar.append("<span class=\"Pbtn next\">"+pt.getXiaYiYe()+"<i>&gt;</i></span>");
		}else{
			bar.append("<a class=\"Pbtn next\"  onclick=\"vip.list.ajaxPage({url:'"+nextUrlAjax+"'} , event)\" href=\""+nextUrl+"\">"+pt.getXiaYiYe()+" <i>&gt;</i></a>");
		}
		//如果现实搜索 
		if(isShowSearch)
			bar.append("<div class=\"go_page\"><input type=\"text\" position=\"s\"　onkeydown=\"checkPageInput(event,"+getPages()+")\" id=\"PagerInput\" size=2 maxSize=\""+getPages()+"\" mytitle=\""+pt.getjMyTitle()+""+getPages()+""+pt.getYe()+"。\" TitlePosition=\"Left\"  pattern=\"num()\" errmsg=\""+pt.getjErrormsg()+""+getPages()+"！\"  value=\""+curpage+"\"/><a onclick=\"javascript:vip.list.jumpPage("+getPages()+")\" href=\"javascript:;\" id=\"JumpButton\" class=\"Pbtn jump\">"+pt.getTiaoZhuan()+"</a></div>");

		return bar.toString();
	}
	/*****
	 * url 形如：/pay/inout/ajax    /pay/inout
	 * @param url
	 * @return
	 */
	private String getBaseUrl(String url){
		String nurl = "";
		if(url.indexOf("/ajaxList") >= 0){
			nurl = url.replace("/ajaxList", "");
		}else{
			nurl = url.replace("/ajax", "");
		}
		return nurl;
	}
	
	private String toStringMap(Map m){
		StringBuilder sbl = new StringBuilder();
		for(Iterator<Entry> i = m.entrySet().iterator(); i.hasNext();){
			Entry e = i.next();
			Object o = e.getValue();
			String v = "";
			if(o == null){
				v = "";
			}else if(o instanceof String[]) {
				String[] s = (String[]) o;
				if(s.length > 0){
					v = s[0];
				}
			}else{
				v=o.toString();
			}
			sbl.append("&").append(e.getKey()).append("=").append(v);
		}
		String s = sbl.toString();
		if(s.length()>0){
			return s.substring(1);
		}
		return "";
	}
}
