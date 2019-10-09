package com.world.util.string;

import com.Lan;



public class Pager
{
	public Pager(String lan) {
		this.lan = lan;
	}

	private int pagesize=5;   //每页显示数
	private int start=1;	  //开始条数
	private int end=1;		  //结束条数
	private int sum;		  //总数
	private int curpage=1;    //当前页
	private int pages=1;	  //总页数
	private String urlPre="";   //换url的前半部分
	private String urlAfter="";   //换url的后半部分
	private String cleanUrl=""; //也就是第一页的url地址,一般是不带参数的
	
	private String lan;

	public String getUrlPre() {
		return urlPre;
	}
	public void setUrlPre(String urlPre) {
		this.urlPre = urlPre;
	}
	public String getUrlAfter() {
		return urlAfter;
	}
	public void setUrlAfter(String urlAfter) {
		this.urlAfter = urlAfter;
	}
	public String getCleanUrl() {
		return cleanUrl;
	}
	public void setCleanUrl(String cleanUrl) {
		this.cleanUrl = cleanUrl;
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
	
	public int getCurpage() {
		return curpage;
	}
	public void setCurpage(int curpages) {
		if(curpages<1)curpage=1;
		if(curpages>this.getPages())curpage=this.getPages();
		this.curpage = curpages;
		//log.info(curpages);
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
	 * @param _urlPre  url的前面部分
	 * @param _urlAfter url的后面部分
	 * @param _cleanUrl 干净的url
     * @param _pagesize 页面大小
	 * @param _curpage 当前页码
	 * @param _setSum 总数量
	 * @return 返回最终的url结果
	 */
	public String GetPaper(String _urlPre,String _urlAfter,String _cleanUrl,int _pagesize,int _curpage,int _setSum)
	{
		return GetPaper(_urlPre,_urlAfter,_cleanUrl,_pagesize,_curpage,_setSum,2,true,false); 
	}
	/**
	 * 直接函数的形式调用这个pager组件,直接返回完成的结果
	 * @param _urlPre  url的前面部分
	 * @param _urlAfter url的后面部分
	 * @param _cleanUrl 干净的url
	 * @param _pagesize 页面大小
	 * @param _curpage 当前页码
	 * @param _setSum 总数量
	 * @param _showItems 紧靠被选项两边的显示个数
	 * @param _isShowSearch 是否显示搜索部分
	 * @param _isShowSide 是否显示两边的两个导航
	 * @return 返回最终的url结果
	 */
	public String GetPaper(String _urlPre,String _urlAfter,String _cleanUrl,int _pagesize,int _curpage,int _setSum,int _showItems,boolean _isShowSearch,boolean _isShowSide)
	{
		this.setUrlPre(_urlPre);
		this.setUrlAfter(_urlAfter);
		this.setCleanUrl(_cleanUrl);
		//this.setPages(_pagesize);
		this.setPagesize(_pagesize);
		this.setCurpage(_curpage);
		this.setSum(_setSum);
		this.setShowSide(_isShowSide);
		this.setShowItems(_showItems);
		this.setShowSearch(_isShowSearch);
		return GetPaper(); 
	}
	public String GetPaperOld()
	{
		String urlstart=getUrlPre();
		String urlend=getUrlAfter();
		//初始化一个字符串缓冲区
		StringBuffer bar=new StringBuffer();
		//显示页码
		//bar.append("<div class=\"page_nav\">");
		bar.append("<span class=\"page_number\">"+curpage+"/"+getPages()+"Page</span>");
		/**
		 * 按钮
		 */
		//如果是第一页,定制按钮
		if(curpage == 1){
			bar.append("<a forbid=\"true\" shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button  blue_button_enables\" abid=\"s1121\"><span><i>"+Lan.Language(lan, "第一页")+"<u>"+Lan.Language(lan, "第一页")+"</u></i></span></a> <a forbid=\"true\" shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button  blue_button_enables\" abid=\"s1121\"><span><i>"+Lan.Language(lan, "上一页")+"<u>"+Lan.Language(lan, "上一页")+"</u></i></span></a>");
			  // .append("<a class='Pager Current'>1</a>");
		}else{
			bar.append(" <a href=\""+urlstart+"1"+urlend+"\"  shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button\" abid=\"s1121\"><span><i>"+Lan.Language(lan, "第一页")+"<u>"+Lan.Language(lan, "第一页")+"</u></i></span></a><a href=\""+urlstart+(curpage-1)+urlend+"\"  shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button\" abid=\"s1121\"><span><i>"+Lan.Language(lan, "上一页")+"<u>"+Lan.Language(lan, "上一页")+"</u></i></span></a>");
			   //.append("<a href='"+getCleanUrl()+"'>1</a>");
		}
		//log.info(curpage+"d"+showItems);
		/*
		 * 前面部分
		 */ 
		if(curpage>(showItems+1))
		{
			//log.info("d");
			bar.append("<span class=\"ellipsis\">...</span>");
		   for(int i=(curpage-showItems);i<curpage;i++){
			 bar.append("<a href=\""+urlstart+i+urlend+"\" class=\"num\" >"+i+"</a>");
		  }
		}
		else
		{
			 for(int i=1;i<curpage;i++){
				 bar.append("<a href=\""+urlstart+i+urlend+"\" class=\"num\" >"+i+"</a>");
			  }
		}
		//中间部分
		bar.append("<a class='num now'>"+curpage+"</a>");
		//后面部分
		if(getPages()>(curpage+showItems+1))
		{
		  for(int i=(curpage+1);i<(curpage+showItems+1);i++){
		  	bar.append("<a href=\""+urlstart+i+urlend+"\" class=\"num\" >"+i+"</a>");
		  }
		  bar.append("<span class=\"ellipsis\">...</span>");
		}
		else
		{
			for(int i=(curpage+1);i<(getPages()+1);i++){
			  	bar.append("<a href=\""+urlstart+i+urlend+"\" class=\"num\" >"+i+"</a>");
			  }
		}
		/**
		 * 最后一页
		 */
		//如果是最后一页
		if(curpage == pages){
			bar.append("<a forbid=\"true\" shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button  blue_button_enables\" abid=\"s1122\"><span><i>"+Lan.Language(lan, "下一页")+"<u>"+Lan.Language(lan, "下一页")+"</u></i></span></a>");
		}else{
			bar.append("<a href=\""+urlstart+(curpage+1)+urlend+"\"  shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button\" abid=\"s1122\"><span><i>"+Lan.Language(lan, "下一页")+"<u>"+Lan.Language(lan, "下一页")+"</u></i></span></a>");
		}
		//bar.append("</div>");
		//如果现实搜索 
		if(isShowSearch) 
			bar.append("<div class=\"go_page\"><span class=\"pageText\">To</span><span class=\"input\"><input type=\"text\" maxSize=\""+getPages()+"\" mytitle=\"请输入一个1至"+getPages()+"之间的页码,然后回车或者点击跳转进行跳转\"  pattern=\"num()\" errmsg=\"请检查您输入的是否为一个1至"+getPages()+"之间的页码 ！\" size=2 id=\"PagerInput\"/></span><span class=\"pageText\">Page</span></div><a shadow=\"true\"  stylename=\"yellow_button\" class=\"AButton yellow_button\" abid=\"s1222\" id=\"JumpButton\"><span><i>Jump<u>Jump</u></i></span></a> ");
		return bar.toString(); 
	}
	
	public String GetPaper()
	{
		String urlstart=getUrlPre();
//		try{
//		urlstart=java.net.URLEncoder.encode(urlstart,"UTF-8");
//		}catch(Exception ex){}
		String urlend=getUrlAfter();
		//urlend=java.net.URLEncoder.encode(urlend);
		try{
		urlend=java.net.URLEncoder.encode(urlend,"UTF-8");
		}catch(Exception ex){}
		//初始化一个字符串缓冲区
		StringBuffer bar=new StringBuffer();
		//显示页码
	//	bar.append("<span class=\"stat\"> C: "+curpage+" OF "+getPages()+" PAGES</span>");
		/**
		 * 按钮
		 */
		//如果是第一页,定制按钮
		if(curpage == 1){
			bar.append("<span class=\"Pbtn first\">"+Lan.Language(lan, "第一页")+"</span><span class=\"Pbtn pre\"><i>&lt;</i> "+Lan.Language(lan, "上一页")+"</span>");
		}else{
			bar.append("<a class=\"Pbtn first\" onclick=\"newUrl(event,this)\"  href=\""+urlstart+"1"+urlend+"\">"+Lan.Language(lan, "第一页")+"</a>").append("<a class=\"Pbtn pre\" onclick=\"newUrl(event,this)\" href=\""+urlstart+(curpage-1)+urlend+"\"><i>&lt;</i> "+Lan.Language(lan, "上一页")+"</a>");
		}
		/*
		 * 前面部分
		 */ 
		if(curpage>(showItems+1))
		{//如果前面是满的
			
			int start=curpage-showItems;
			if((curpage+showItems)>getPages())
			{
				start=getPages()-2*showItems;
				if(start<=0)
					start=1;
			}
			else
				start=curpage-showItems;
			if(start>1)
			  bar.append("<span class=\"ellipsis\">...</span>");
			
		    for(int i=start;i<curpage;i++){
			 bar.append("<a href=\""+urlstart+i+urlend+"\"  onclick=\"newUrl(event,this)\"  class=\"num\" >"+i+"</a>");
		    }
		}
		else
		{
			 for(int i=1;i<curpage;i++){
				 bar.append("<a href=\""+urlstart+i+urlend+"\"  onclick=\"newUrl(event,this)\"  class=\"num\"  >"+i+"</a>");
			  }
		}
		//中间部分
		bar.append("<a class='num current'  onclick=\"newUrl(event,this)\" >"+curpage+"</a>");
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
		       	bar.append("<a href=\""+urlstart+i+urlend+"\"  onclick=\"newUrl(event,this)\"   class=\"num\" >"+i+"</a>");
		       }
			if(getPages()>(2*showItems+1))
		      bar.append("<span class=\"ellipsis\">...</span>");
		}
		else
		{
			for(int i=(curpage+1);i<(getPages()+1);i++){
			  	bar.append("<a  href=\""+urlstart+i+urlend+"\"  onclick=\"newUrl(event,this)\"  class=\"num\" >"+i+"</a>");
			  }
		}
		/**
		 * 最后一页
		 */
		//如果是最后一页
		if(curpage == pages){
			bar.append("<span class=\"Pbtn next\">"+Lan.Language(lan, "下一页")+" <i>&gt;</i></span>");
		}else{
			bar.append("<a class=\"Pbtn next\"  onclick=\"newUrl(event,this)\"   href=\""+urlstart+(curpage+1)+urlend+"\">"+Lan.Language(lan, "下一页")+" <i>&gt;</i></a>");
		}
		//bar.append("</div>");
		//如果现实搜索 
		if(isShowSearch) 
			bar.append("<div class=\"go_page\"><input type=\"text\" position=\"s\"　onkeydown=\"checkPageInput(event,"+getPages()+")\" id=\"PagerInput\" size=2 maxSize=\""+getPages()+"\" mytitle=\""+Lan.LanguageFormat(lan, "最多%%页", new String[]{String.valueOf(getPages())})+"\" TitlePosition=\"Left\"  pattern=\"num()\" errmsg=\""+Lan.LanguageFormat(lan, "页码太大，最多%%", new String[]{String.valueOf(getPages())})+"\"  value=\""+curpage+"\" /><a href=\"javascript:jumpPage("+getPages()+")\" id=\"JumpButton\" class=\"Pbtn jump\">"+Lan.Language(lan, "跳转")+"</a></div>");
		return bar.toString();
	}
	/**
	 * 直接函数的形式调用这个pager组件,直接返回完成的结果,简化参数默认配置版本
	 * @param _urlPre  url的前面部分
	 * @param _urlAfter url的后面部分
	 * @param _cleanUrl 干净的url
     * @param _pagesize 页面大小
	 * @param _curpage 当前页码
	 * @param _setSum 总数量
	 * @return 返回最终的url结果
	 */
	public String GetPaperSimple(String _urlPre,String _urlAfter,String _cleanUrl,int _pagesize,int _curpage,int _setSum)
	{
		return GetPaperSimple(_urlPre,_urlAfter,_cleanUrl,_pagesize,_curpage,_setSum,2,true,false); 
	}
	/**
	 * 直接函数的形式调用这个pager组件,直接返回完成的结果
	 * @param _urlPre  url的前面部分
	 * @param _urlAfter url的后面部分
	 * @param _cleanUrl 干净的url
	 * @param _pagesize 页面大小
	 * @param _curpage 当前页码
	 * @param _setSum 总数量
	 * @param _showItems 紧靠被选项两边的显示个数
	 * @param _isShowSearch 是否显示搜索部分
	 * @param _isShowSide 是否显示两边的两个导航
	 * @return 返回最终的url结果
	 */
	public String GetPaperSimple(String _urlPre,String _urlAfter,String _cleanUrl,int _pagesize,int _curpage,int _setSum,int _showItems,boolean _isShowSearch,boolean _isShowSide)
	{
		this.setUrlPre(_urlPre);
		this.setUrlAfter(_urlAfter);
		this.setCleanUrl(_cleanUrl);
		//this.setPages(_pagesize);
		this.setPagesize(_pagesize);
		this.setCurpage(_curpage);
		this.setSum(_setSum);
		this.setShowSide(_isShowSide);
		this.setShowItems(_showItems);
		this.setShowSearch(_isShowSearch);
		return GetPaperSimper(); 
	}
	public String GetPaperSimper()
	{
		String urlstart=getUrlPre();
		String urlend=getUrlAfter();
		//初始化一个字符串缓冲区
		StringBuffer bar=new StringBuffer();
		if(getPages()<2)
			return "";
		//显示页码
		//bar.append("<div class=\"page_nav\">");
		bar.append("<span class=\"num\">"+curpage+"/"+getPages()+"页</span>");
		/**
		 * 按钮
		 */ 
		//如果是第一页,定制按钮
		if(curpage == 1){
			bar.append(" <a forbid=\"true\" shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button  blue_button_enables\" abid=\"s1111\"><span><i>上一页<u>上一页</u></i></span></a>&nbsp;" );//+
		}else{
			bar.append(" <a href=\""+urlstart+(curpage-1)+urlend+"\"  shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button\" abid=\"s1111\"><span><i>上一页<u>上一页</u></i></span></a>&nbsp;" );//+
		}
		/**
		 * 最后一页　
		 */
		//如果是最后一页
		if(curpage == pages){
			bar.append("<a forbid=\"true\" shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button  blue_button_enables\" abid=\"s1112\"><span><i>下一页<u>下一页</u></i></span></a>");
		}else{
			bar.append("<a href=\""+urlstart+(curpage+1)+urlend+"\"  shadow=\"true\" stylename=\"blue_button\" class=\"AButton blue_button\" abid=\"s1112\"><span><i>下一页<u>下一页</u></i></span></a>");
		}
		return bar.toString(); 
	}
} 