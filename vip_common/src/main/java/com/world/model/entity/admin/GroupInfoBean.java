package com.world.model.entity.admin;

/**
 * 功能:数据容器bean,对应Admin_Role_GroupInfo
 * @author 凌晓
 */
public class GroupInfoBean {

  /*****************************变量声明区******************************/
  
    //系统基础配置,默认配置GroupInfoBean指向到insert数据库处理函数
    private String dataName="Admin_Role_GroupInfo_Insert";
    private int rgiGroupId;      
    private int rgAutId;      
    private int rgiId;      
     
  /*****************************变量操作区******************************/
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
  /*****************基础属性DataName操作函数定义结束*********************/ 
  
    /**
     *功能:设置属性RgiGroupId, 组编号
     *返回:无
     */
     public void setRgiGroupId(int rgiGroupId){
        this.rgiGroupId= rgiGroupId;
     } 
    /**
     *功能:获取属性RgiGroupId, 组编号
     *返回:属性RgiGroupId
     */
     public int getRgiGroupId(){
        return this.rgiGroupId;
     }   
    
   /*****************属性RgiGroupId操作函数定义结束*********************/  
  
    /**
     *功能:设置属性RgAutId, 权限编号
     *返回:无
     */
     public void setRgAutId(int rgAutId){
        this.rgAutId= rgAutId;
     } 
    /**
     *功能:获取属性RgAutId, 权限编号
     *返回:属性RgAutId
     */
     public int getRgAutId(){
        return this.rgAutId;
     }   
    
   /*****************属性RgAutId操作函数定义结束*********************/  
  
    /**
     *功能:设置属性RgiId, 角色分组编号
     *返回:无
     */
     public void setRgiId(int rgiId){
        this.rgiId= rgiId;
     } 
    /**
     *功能:获取属性RgiId, 角色分组编号
     *返回:属性RgiId
     */
     public int getRgiId(){
        return this.rgiId;
     }   
    
   /*****************属性RgiId操作函数定义结束*********************/  
   
 }   
