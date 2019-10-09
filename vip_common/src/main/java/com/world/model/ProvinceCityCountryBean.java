package com.world.model;
import java.math.BigDecimal;
import java.util.List;

import com.world.data.mysql.Bean;
import com.world.util.WebUtil;

/**
 * 功能:数据容器bean,对应ProvinceCityCountry
 * @author 凌晓
 */
public class ProvinceCityCountryBean extends Bean{

	private static final long serialVersionUID = 1L;

/*****************************变量声明区******************************/
  
	public static final BigDecimal commonFees = new BigDecimal("0.05");
	
    //系统基础配置,默认配置ProvinceCityCountryBean指向到insert数据库处理函数
    private String dataName="ProvinceCityCountry_Insert";
    private int provinceId;      
    private String provinceName;      
    private int provinceParentId;      
    private int cityId;      
    private String cityName;      
    private int cityParentId;      
    private int countryId;      
    private String countryName;      
    private int countryParentId;
    private int pCCId;
    
    private List<ProvinceCityCountryBean> sonList;
    
    private BigDecimal withdrawRate;//提现税率
    private String withdrawRateShow;//显示提现税率
    private int userId;
    private String userName;
    private int connectedId;
    
    public int getConnectedId() {
		return connectedId;
	}
	public void setConnectedId(int connectedId) {
		this.connectedId = connectedId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getWithdrawRateShow() {
		withdrawRate=WebUtil.saveTow(withdrawRate);
    	if(!withdrawRate.equals(commonFees)){
    		withdrawRateShow = "<font color='red'>"+withdrawRate.multiply(new BigDecimal("100"))+"%</font>";
    	}else{
    		withdrawRateShow = "<font color='green'>"+withdrawRate.multiply(new BigDecimal("100"))+"%</font>";
    	}
		return withdrawRateShow;
	}
	public void setWithdrawRateShow(String withdrawRateShow) {
		this.withdrawRateShow = withdrawRateShow;
	}
	public BigDecimal getWithdrawRate() {
		withdrawRate=WebUtil.saveTow(withdrawRate);
		return withdrawRate;
	}
	public void setWithdrawRate(BigDecimal withdrawRate) {
		this.withdrawRate = withdrawRate;
	}
	public List<ProvinceCityCountryBean> getSonList() {
		return sonList;
	}
	public void setSonList(List<ProvinceCityCountryBean> sonList) {
		this.sonList = sonList;
	}
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
     *功能:设置属性ProvinceId, 省ID 
     *返回:无
     */
     public void setProvinceId(int provinceId){
        this.provinceId= provinceId;
     } 
    /**
     *功能:获取属性ProvinceId, 省ID 
     *返回:属性ProvinceId
     */
     public int getProvinceId(){
        return this.provinceId;
     }   
    
   /*****************属性ProvinceId操作函数定义结束*********************/  
  
    /**
     *功能:设置属性ProvinceName, 省名称
     *返回:无
     */
     public void setProvinceName(String provinceName){
        this.provinceName= provinceName;
     } 
    /**
     *功能:获取属性ProvinceName, 省名称
     *返回:属性ProvinceName
     */
     public String getProvinceName(){
        return this.provinceName;
     }   
    
   /*****************属性ProvinceName操作函数定义结束*********************/  
  
    /**
     *功能:设置属性ProvinceParentId, 省父节点
     *返回:无
     */
     public void setProvinceParentId(int provinceParentId){
        this.provinceParentId= provinceParentId;
     } 
    /**
     *功能:获取属性ProvinceParentId, 省父节点
     *返回:属性ProvinceParentId
     */
     public int getProvinceParentId(){
        return this.provinceParentId;
     }   
    
   /*****************属性ProvinceParentId操作函数定义结束*********************/  
  
    /**
     *功能:设置属性CityId, 市 ID
     *返回:无
     */
     public void setCityId(int cityId){
        this.cityId= cityId;
     } 
    /**
     *功能:获取属性CityId, 市 ID
     *返回:属性CityId
     */
     public int getCityId(){
        return this.cityId;
     }   
    
   /*****************属性CityId操作函数定义结束*********************/  
  
    /**
     *功能:设置属性CityName, 市名称
     *返回:无
     */
     public void setCityName(String cityName){
        this.cityName= cityName;
     } 
    /**
     *功能:获取属性CityName, 市名称
     *返回:属性CityName
     */
     public String getCityName(){
        return this.cityName;
     }   
    
   /*****************属性CityName操作函数定义结束*********************/  
  
    /**
     *功能:设置属性CityParentId, 所属省
     *返回:无
     */
     public void setCityParentId(int cityParentId){
        this.cityParentId= cityParentId;
     } 
    /**
     *功能:获取属性CityParentId, 所属省
     *返回:属性CityParentId
     */
     public int getCityParentId(){
        return this.cityParentId;
     }   
    
   /*****************属性CityParentId操作函数定义结束*********************/  
  
    /**
     *功能:设置属性CountryId, 区ID
     *返回:无
     */
     public void setCountryId(int countryId){
        this.countryId= countryId;
     } 
    /**
     *功能:获取属性CountryId, 区ID
     *返回:属性CountryId
     */
     public int getCountryId(){
        return this.countryId;
     }   
    
   /*****************属性CountryId操作函数定义结束*********************/  
  
    /**
     *功能:设置属性CountryName, 区名称
     *返回:无
     */
     public void setCountryName(String countryName){
        this.countryName= countryName;
     } 
    /**
     *功能:获取属性CountryName, 区名称
     *返回:属性CountryName
     */
     public String getCountryName(){
        return this.countryName;
     }   
    
   /*****************属性CountryName操作函数定义结束*********************/  
  
    /**
     *功能:设置属性CountryParentId, 所属市
     *返回:无
     */
     public void setCountryParentId(int countryParentId){
        this.countryParentId= countryParentId;
     } 
    /**
     *功能:获取属性CountryParentId, 所属市
     *返回:属性CountryParentId
     */
     public int getCountryParentId(){
        return this.countryParentId;
     }   
    
   /*****************属性CountryParentId操作函数定义结束*********************/  
  
    /**
     *功能:设置属性PCCId, 省市区表ID
     *返回:无
     */
     public void setPCCId(int pCCId){
        this.pCCId= pCCId;
     } 
    /**
     *功能:获取属性PCCId, 省市区表ID
     *返回:属性PCCId
     */
     public int getPCCId(){
        return this.pCCId;
     }   
    
   /*****************属性PCCId操作函数定义结束*********************/  
   
 }   
