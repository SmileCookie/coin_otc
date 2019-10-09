package com.world.util.string;

import java.util.ArrayList;
import java.util.List;

/*********************************
 * 分页操作类
 * @author Administrator
 *
 */
public class PageTool{
	private Integer pageNo=1;//当前页码
	private Integer totalpage = 1;//总页数
	private int pageSize = 10;//每页显示条数
	private Integer currentPage = 1;//当前页码
	private Integer totalRecord=0;//总记录数
	private int pagecode = 10;//
	
	public PageTool() {
		super();
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getFirstResult() {
		return (this.currentPage-1)*this.pageSize;
	}
	
	public int getPagecode() {
		return pagecode;
	}

	public void setPagecode(int pagecode) {
		this.pagecode = pagecode;
	}

	
	public PageTool(int pageSize, int currentPage) {
		this.pageSize = pageSize;
		this.currentPage = currentPage;
	}
	
	public long getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(Integer totalrecord) {
		this.totalRecord = totalrecord;
		setTotalpage(this.totalRecord%this.pageSize==0? this.totalRecord/this.pageSize : this.totalRecord/this.pageSize+1);
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getTotalpage() {
		return totalpage;
	}
	public void setTotalpage(Integer totalpage) {
		this.totalpage = totalpage;
	}


	public int getPageSize() {
		return pageSize;
	}


	public void setCurrentPage(Integer currentpage) {
		this.currentPage = currentpage;
	}
	/*
	 * 获取当前可显示的页码的字符串操作html
	 * 用于ajax分页中的可显示页码显示
	 * 例如：
	 * ...2 3 4 5 6 7...
	 * 
	 */
	public String getShowPage(){
		//显示页码
		String pageShow="";
		BranchPage bp = new BranchPage(getCurrentPage(),3,getTotalpage());
		String show=bp.handlePage();
		String current=bp.handlePage().split("<")[1].split(">")[0];
		int splitSize=show.split("<"+current+">").length;
		if(splitSize>=1){
			String before=show.split("<"+current+">")[0];
			before=before.substring(1,before.length());
			List listBefore=new ArrayList();
			for(int i=0;i<before.split(",").length;i++){
				listBefore.add(before.split(",")[i]);
			}
			for(int i=0;i<listBefore.size();i++){
				String ss=(String) listBefore.get(i);
				if(ss.contains("...")){
					pageShow+="<![CDATA[<a>"+ss+"</a>]]>";
				}else{
					if(!ss.equals(getCurrentPage().toString())){
						pageShow+="<![CDATA[<a onclick='getNotes("+ss+")' style='cursor:pointer;' class='figure'>"+ss+"</a>]]>";
					}
				} 
			}
		}
		pageShow+="<![CDATA[<a style='color:red;'  class='figure'>"+getCurrentPage()+"</a>]]>";
		if(splitSize>=2){
			List listAfter=new ArrayList();
			String after=show.split("<"+current+">")[1];
			after=after.substring(1,after.length());
			for(int i=0;i<after.split(",").length;i++){
				listAfter.add(after.split(",")[i]);
			}
			for(int i=0;i<listAfter.size();i++){
				String la=(String) listAfter.get(i);
				if(la.contains("...")){
					pageShow+="<![CDATA[<a>"+la+"</a>]]>";
				}else{
					if(!la.equals(getCurrentPage().toString())){
						pageShow+="<![CDATA[<a onclick='getNotes("+la+")' style='cursor:pointer;' class='figure'>"+la+"</a>]]>";
					}
				} 
			}
		}
		return pageShow;
	}
}
