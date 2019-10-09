package com.world.data.mysql.config;

import java.util.ArrayList;

import com.world.data.mysql.Bean;
import com.world.data.mysql.DataFeild;

/**
 * 功能:这实际上是一个测试用的bean,因为他跟BaseConfigBean功能一样
 * @author 凌晓
 */
public class BaseBean extends Bean{
	private String dataName="progrom_ListById";
	private int id;
	private String mingzi;
	private String opsCode;
	private String opsShuxing;
	private String opsDemo;
	private int quanxian;
	private int datatype;
	private int contenttype;
	private String beizhu;
	public ArrayList<DataFeild> dataFeilds;

	//用于请求的两个参数
	private int pageSize;
	private int pageIndex;
	
	public int getPageSize()
	{
		return this.pageSize;
	}
	public void setPageSize(int pageSize){
		this.pageSize=pageSize;
	}
	public int getPageIndex()
	{
		return this.pageIndex;
	}
	public void setPageIndex(int pageIndex){
		this.pageIndex=pageIndex;
	}
	//被使用了多少次
	private int calledTimes=0;
	/**
	 * 功能:获取数据名称
	 * @return 数据名称
	 */
	public String getDataName(){
		return this.dataName;
	}
	/**
	 * 功能:这个链接是否需要用主链接链接
	 * @return
	 */
	public boolean isMain()
	{
		//clazz.getClasses();
		boolean rtn=false;
		if(contenttype==0)
			rtn=true;
		return rtn;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		sb.append("mingzi:"+mingzi+"\n");
		sb.append("opsCode:"+opsCode+"\n");
		for(int i=0;i<dataFeilds.size();i++)
		{
			DataFeild df=dataFeilds.get(i);
			sb.append("--feildName:"+df.feildName+"\n");
			sb.append("--length:"+df.length+"\n");
			sb.append("--feildType:"+df.feildType+"\n");
			sb.append("--defaultValue:"+df.defaultValue+"\n\n");
		}
		return sb.toString();
	}

	/**
	 * 功能:返回一个指定项的配置长度,如果不存在就返回0
	 * @param name
	 */
	private int getInLength(String name)
	{
		if(name.indexOf('(')<=0)
			return 0;
		else
		{
			try{
			name=name.substring(name.indexOf('('));
			name=name.substring(1,name.indexOf(')'));
			return Integer.parseInt(name);
			}catch(Exception ex)
			{
				return 0;
			}
			
		}
	}

	/**
	   * 增加一个字段
	   * @param dataFeild
	   */
	  public void addFeild(DataFeild dataFeild)
	  {
		  dataFeilds.add(dataFeild);
	  }
	  
	public void setId(int id){
		this.id=id;
	}
	public int getId(){
		return this.id;
	}
	
	public void setMingzi(String mingzi){
		this.mingzi=mingzi;
	}
	public String getMingzi(){
		return this.mingzi;
	}
	
	
	public void setOpsCode(String opsCode){
		this.opsCode=opsCode;
	}
	public String getOpsCode(){
		return this.opsCode;
	}
	
	public void setOpsShuxing(String opsShuxing){
		this.opsShuxing=opsShuxing;
	}
	public String getOpsShuxing(){
		return this.opsShuxing;
	}
	
	public void setOpsDemo(String opsDemo){
		this.opsDemo=opsDemo;
	}
	public String getOpsDemo(){
		return this.opsDemo;
	}
	
	public void setBeizhu(String beizhu){
		this.beizhu=beizhu;
	}
	public String getBeizhu(){
		return this.beizhu;
	}
	
	
	public void setQuanxian(int quanxian){
		this.quanxian=quanxian;
	}
	public int getQuanxian(){
		return this.quanxian;
	}
	
	public void setDatatype(int datatype){
		this.datatype=datatype;
	}
	public int getDatatype(){
		return this.datatype;
	}
	
	public void setContenttype(int contenttype){
		this.contenttype=contenttype;
	}
	public int getContenttype(){
		return this.contenttype;
	}



}
