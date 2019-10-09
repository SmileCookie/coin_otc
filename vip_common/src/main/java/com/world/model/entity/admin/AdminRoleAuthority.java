package com.world.model.entity.admin;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@Entity(value = "admin_role_authority" , noClassnameStored = true)
public class AdminRoleAuthority extends StrBaseLongIdEntity{
	public AdminRoleAuthority(){
		super(null);
	}
	public AdminRoleAuthority(Datastore ds) {
		super(ds);
		// TODO Auto-generated constructor stub
	}
	/*****************************变量声明区******************************/
	  
    //系统基础配置,默认配置AuthorityBean指向到insert数据库处理函数
    private String autEnName;      
    private String autCnName;      
    private String autClassEnName;      
    private String autClassCnName;      
    private String autUrl;
    private int sysGroupId;
    private boolean isViewer;
    private boolean isTopViewer;
    private String photo;
    private boolean needPreUpdate;
    private int operationType;
    private int defaultLoginPer;
    private int parentUitId;
     

  /*****************基础属性DataName操作函数定义结束*********************/ 
  
    public int getSysGroupId() {
		return sysGroupId;
	}
	public void setSysGroupId(int sysGroupId) {
		this.sysGroupId = sysGroupId;
	}
	public boolean isViewer() {
		return isViewer;
	}
	public void setViewer(boolean isViewer) {
		this.isViewer = isViewer;
	}
	public boolean isTopViewer() {
		return isTopViewer;
	}
	public void setTopViewer(boolean isTopViewer) {
		this.isTopViewer = isTopViewer;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public boolean isNeedPreUpdate() {
		return needPreUpdate;
	}
	public void setNeedPreUpdate(boolean needPreUpdate) {
		this.needPreUpdate = needPreUpdate;
	}
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}
	public int getDefaultLoginPer() {
		return defaultLoginPer;
	}
	public void setDefaultLoginPer(int defaultLoginPer) {
		this.defaultLoginPer = defaultLoginPer;
	}
	public int getParentUitId() {
		return parentUitId;
	}
	public void setParentUitId(int parentUitId) {
		this.parentUitId = parentUitId;
	}
	/**
     *功能:设置属性AutEnName, 权限操作方法
     *返回:无
     */
     public void setAutEnName(String autEnName){
        this.autEnName= autEnName;
     } 
    /**
     *功能:获取属性AutEnName, 权限操作方法
     *返回:属性AutEnName
     */
     public String getAutEnName(){
        return this.autEnName;
     }   
    
   /*****************属性AutEnName操作函数定义结束*********************/  
  
    /**
     *功能:设置属性AutCnName, 权限操作名称
     *返回:无
     */
     public void setAutCnName(String autCnName){
        this.autCnName= autCnName;
     } 
    /**
     *功能:获取属性AutCnName, 权限操作名称
     *返回:属性AutCnName
     */
     public String getAutCnName(){
        return this.autCnName;
     }   
    
   /*****************属性AutCnName操作函数定义结束*********************/  
  
    /**
     *功能:设置属性AutClassEnName, 权限操作类
     *返回:无
     */
     public void setAutClassEnName(String autClassEnName){
        this.autClassEnName= autClassEnName;
     } 
    /**
     *功能:获取属性AutClassEnName, 权限操作类
     *返回:属性AutClassEnName
     */
     public String getAutClassEnName(){
        return this.autClassEnName;
     }   
    
   /*****************属性AutClassEnName操作函数定义结束*********************/  
  
    /**
     *功能:设置属性AutClassCnName, 权限描述
     *返回:无
     */
     public void setAutClassCnName(String autClassCnName){
        this.autClassCnName= autClassCnName;
     } 
    /**
     *功能:获取属性AutClassCnName, 权限描述
     *返回:属性AutClassCnName
     */
     public String getAutClassCnName(){
        return this.autClassCnName;
     }   
    
   /*****************属性AutClassCnName操作函数定义结束*********************/  
  
    /**
     *功能:设置属性AutUrl, 操作URL
     *返回:无
     */
     public void setAutUrl(String autUrl){
        this.autUrl= autUrl;
     } 
    /**
     *功能:获取属性AutUrl, 操作URL
     *返回:属性AutUrl
     */
     public String getAutUrl(){
        return this.autUrl;
     }   
    
   /*****************属性AutUrl操作函数定义结束*********************/  
  
    /**
     *功能:设置属性AutId, 权限编号
     *返回:无
     */
     public void setAutId(String autId){
        this.myId= autId;
     }  
    /**
     *功能:获取属性AutId, 权限编号
     *返回:属性AutId
     */
     public String getAutId(){
        return this.myId;
     }   
    
   /*****************属性AutId操作函数定义结束*********************/  
}
