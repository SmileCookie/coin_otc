package com.world.util.ip.impl;


/** 
 * 
 * @category 用来封装ip相关信息，目前只有两个字段，ip所在的国家和地区
 */

public class IPLocation {
	private final static String PRO_NAME = "省";
	private final static String CITY_NAME = "市";
	private String country;
	private String area;
	
	public IPLocation() {
	    country = area = "";
	}
	
	public IPLocation getCopy() {
	    IPLocation ret = new IPLocation();
	    ret.country = country;
	    ret.area = area;
	    return ret;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
                //如果为局域网，纯真IP地址库的地区会显示CZ88.NET,这里把它去掉
		if(area.trim().equals("CZ88.NET")){
			this.area="本机或本网络";
		}else{
			this.area = area;
		}
	}
	
	public String getProvince(){
		if(country != null && country.length() > 0){
			int proIndex = country.indexOf(PRO_NAME);
			int citIndex = country.indexOf(CITY_NAME);
			if(proIndex > -1){//存在
				return country.substring(0 , proIndex);
			}else{
				if(citIndex > -1){//存在
					return country.substring(0 , citIndex);
				}else{
					return null;
				}
			}
		}
		return null;
	}
	
	public String getCity(){
		if(country != null && country.length() > 0){
			int proIndex = country.indexOf(PRO_NAME);
			int citIndex = country.indexOf(CITY_NAME);
			
			if(proIndex > -1){//存在
				if(citIndex > -1){//存在
					return country.substring(proIndex+1 , citIndex);
				}else{
					return null;
				}
			}else{
				if(citIndex > -1){//存在
					return country.substring(0 , citIndex);
				}else{
					return null;
				}
			}
		}
		return null;
	}
}


