package com.world.model.entity.admin;

import java.sql.Timestamp;

import com.file.PathUtil;
import com.file.config.FileConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.admin.role.AdminRole;

@Entity(value = "admin_user" , noClassnameStored = true)
public class AdminUser extends StrBaseLongIdEntity {
	public AdminUser() {
		super(null);
	}
	
	public AdminUser(Datastore ds) {
		super(ds);
	}

	private static final long serialVersionUID = -8791435182844009949L;
  
    private String admName;      
    private String admUName;      
    private String admPassword;      
    private int admRoleId; 
    private String admRoleName = "";
    private String admDes;      
    private String admPhoto;      
    private int admSex;      
    private int admPartId;      
    private int loginTime;      
    private Timestamp lastLoginTime;      
    private String lastLoginIp;      
    private int isLocked;      
    private String baseBorld;
    private String email;//邮箱
    private String telphone;//电话
    private Timestamp createTime;
    private Timestamp updateTime;
    private int createId;
    private int updateId;
    
    private int customers;
    
	private String secret;//只要保存值就不再改变
	
	private int variablesCustomer;
	
	private String secretKey;
    
    public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public int getVariablesCustomer() {
		return variablesCustomer;
	}

	public void setVariablesCustomer(int variablesCustomer) {
		this.variablesCustomer = variablesCustomer;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	private AdminRole ar;//
    
 // /获取要显示的头像
	public String getShowPhotos(String prefix) {
		if (admPhoto != null && admPhoto.trim().length() > 0) {
			return PathUtil.getPathByFileName(admPhoto, prefix);
		} else {
			return "";
		}
	}

	public String getDefaultPhoto() {
		return getShowPhotos("88x88");
	}
    
    public String getEmail() {
    	if(email == null || email.length() <= 0){
    		email = "-";
    	}
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelphone() {
		if(telphone == null || telphone.length() <= 0){
			telphone = "-";
    	}
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public int getCustomers() {
		return customers;
	}

	public void setCustomers(int customers) {
		this.customers = customers;
	}

	public String getStatus(){
    	if(isLocked == 0){
    		return "可用";
    	}else{
    		return "不可用";
    	}
    }

     public AdminRole getAr() {
		return ar;
	}

	public void setAr(AdminRole ar) {
		this.ar = ar;
	}

	public String getAdmRoleName() {
		return admRoleName;
	}

	public void setAdmRoleName(String admRoleName) {
		this.admRoleName = admRoleName;
	}

	public void setAdmName(String admName){
        this.admName= admName;
     } 

     public String getAdmName(){
        return this.admName;
     }   

     public void setAdmUName(String admUName){
        this.admUName= admUName;
     } 

     public String getAdmUName(){
        return this.admUName;
     }   

     public void setAdmPassword(String admPassword){
        this.admPassword= admPassword;
     } 
 
     public String getAdmPassword(){
        return this.admPassword;
     }   

     public void setAdmRoleId(int admRoleId){
        this.admRoleId= admRoleId;
     } 

     public int getAdmRoleId(){
        return this.admRoleId;
     }   

     public void setAdmDes(String admDes){
        this.admDes= admDes;
     } 

     public String getAdmDes(){
        return this.admDes;
     }   

     public void setAdmPhoto(String admPhoto){
        this.admPhoto= admPhoto;
     } 

     public String getAdmPhoto(){
        return this.admPhoto;
     }   

     public void setAdmSex(int admSex){
        this.admSex= admSex;
     } 

     public int getAdmSex(){
        return this.admSex;
     }   

     public void setAdmPartId(int admPartId){
        this.admPartId= admPartId;
     } 

     public int getAdmPartId(){
        return this.admPartId;
     }   

     public void setLoginTime(int loginTime){
        this.loginTime= loginTime;
     } 

     public int getLoginTime(){
        return this.loginTime;
     }   

     public void setLastLoginTime(Timestamp lastLoginTime){
        this.lastLoginTime= lastLoginTime;
     } 

     public Timestamp getLastLoginTime(){
        return this.lastLoginTime;
     }   

     public void setLastLoginIp(String lastLoginIp){
        this.lastLoginIp= lastLoginIp;
     } 

     public String getLastLoginIp(){
        return this.lastLoginIp;
     }   

     public void setIsLocked(int isLocked){
        this.isLocked= isLocked;
     } 

     public int getIsLocked(){
        return this.isLocked;
     }   

     public void setBaseBorld(String baseBorld){
        this.baseBorld= baseBorld;
     } 

     public String getBaseBorld(){
        return this.baseBorld;
     }   

     public void setAdmId(String admId){
        this.myId= admId;
     } 

     public String getAdmId(){
        return this.myId;
     }

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public int getCreateId() {
		return createId;
	}

	public void setCreateId(int createId) {
		this.createId = createId;
	}

	public int getUpdateId() {
		return updateId;
	}

	public void setUpdateId(int updateId) {
		this.updateId = updateId;
	}   
 }   
