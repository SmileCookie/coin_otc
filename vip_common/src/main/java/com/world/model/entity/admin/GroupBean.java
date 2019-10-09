package com.world.model.entity.admin;
 
/**
 * 功能:数据容器bean,对应Admin_Role_Group
 * @author 凌晓
 */
public class GroupBean {

  /*****************************变量声明�?*****************************/
  
    //系统基础配置,默认配置GroupBean指向到insert数据库处理函�?
    private String dataName="Admin_Role_Group_Insert";
    private int rgRoleId;      
    private String rgName;      
    private int rgId;      
   
  /*****************************变量操作�?*****************************/
   /**
    * 功能:获取数据名称
    * @return 数据名称
    */
	public String getDataName(){
		return this.dataName;
	}
	/**
	* 功能:获取数据名称
	* @return 数据名称
	*/
	public void setDataName(String dataName){
		this.dataName=dataName;
	}
  /*****************基础属�?DataName操作函数定义结束*********************/ 
  
    /**
     *功能:设置属�?RgRoleId, 角色ID
     *返回:�?
     */
     public void setRgRoleId(int rgRoleId){
        this.rgRoleId= rgRoleId;
     } 
    /**
     *功能:获取属�?RgRoleId, 角色ID
     *返回:属�?RgRoleId
     */
     public int getRgRoleId(){
        return this.rgRoleId;
     }   
    
   /*****************属�?RgRoleId操作函数定义结束*********************/  
  
    /**
     *功能:设置属�?RgName, 组名�?
     *返回:�?
     */
     public void setRgName(String rgName){
        this.rgName= rgName;
     } 
    /**
     *功能:获取属�?RgName, 组名�?
     *返回:属�?RgName
     */
     public String getRgName(){
        return this.rgName;
     }   
    
   /*****************属�?RgName操作函数定义结束*********************/  
  
    /**
     *功能:设置属�?RgId, 角色组编�?
     *返回:�?
     */
     public void setRgId(int rgId){
        this.rgId= rgId;
     } 
    /**
     *功能:获取属�?RgId, 角色组编�?
     *返回:属�?RgId
     */
     public int getRgId(){
        return this.rgId;
     }   
    
   /*****************属�?RgId操作函数定义结束*********************/  
   
 }   
