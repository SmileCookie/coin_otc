package com.world.model;

import java.util.List;

import org.apache.log4j.Logger;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;

/**
 * 取得省市区集合
 * @author Administrator
 *
 */
public class ProvinceCityCountryUtil {
	static Logger log = Logger.getLogger(ProvinceCityCountryUtil.class.getName());
	/**
	 * 获取省集合
	 * @return ProvinceList
	 */
	public static List getProvince(){
		try{
		String sql_province="SELECT DISTINCT ProvinceName,ProvinceId FROM ProvinceCityCountry WHERE  ProvinceParentId=0  order by ProvinceId";
		List list_province=Data.Query(sql_province,new Object[]{});
		return list_province;
		}catch (Exception e) {
			// TODO: handle exception
			log.info(e.toString());
			return null;
		}
	}
	/**
	 * 获取省XML
	 * @return province
	 */
	public static String getProvinceToXML(){
		try{
			String sql_province="SELECT DISTINCT ProvinceName,ProvinceId FROM ProvinceCityCountry WHERE  ProvinceParentId=?  order by ProvinceId";
			return Data.GetXml(sql_province,new Object[]{});
		}catch (Exception e) {
			log.info(e.toString());
			return null;
		}
	}
	
	/**
	 * 根据 省ID获取城市集合
	 * @param provinceId 省ID
	 * @return 城市集合
	 */
	public static List getCityListByProvinceId(int provinceId){
		try{

			String sql_city="SELECT DISTINCT CityName,CityId FROM ProvinceCityCountry WHERE  CityParentId=? order by CityId";
			List list_city=Data.Query(sql_city,new Object[]{provinceId});
			return list_city;
		}catch (Exception e) {
			log.info(e.toString());
			return null;
		}
	}
	
	/**
	 * 根据 省ID获取城市XML
	 * @param provinceId 省ID
	 * @return 城市集合
	 */
	public static String getCityByProvinceIdToXML(int provinceId){
		String sql_city="SELECT DISTINCT CityName,CityId FROM ProvinceCityCountry WHERE  CityParentId=?  ORDER BY CityId";
		return Data.GetXml(sql_city,new Object[]{provinceId});
	}
	
	/**
	 * 根据市ID获取区、县集合
	 * @param cityId
	 * @return
	 */
	public static List getCountryListByCityId(int cityId){
		String sql_country="SELECT CountryName,CountryId FROM ProvinceCityCountry WHERE CountryParentId=? ORDER BY CountryId";
		List list_country=Data.Query(sql_country,new Object[]{cityId});
		return list_country;
	}
	
	/**
	 * 根据市ID获取区、县xml
	 * @param cityId
	 * @return
	 */
	public static String getCountryByCityIdToXML(int cityId){
		String sql_country="SELECT CountryName,CountryId FROM ProvinceCityCountry WHERE  CountryParentId=? ORDER BY CountryId";
		return Data.GetXml(sql_country,new Object[]{cityId});
	}
	/**
	 * 根据市 ID，区ID。获取
	 * @param cityId 市ID
	 * @param countryId 区ID
	 * @return
	 */
	public static List getProvinceCityCountryInfoById(int cityId,int countryId){
		String sql="SELECT ProvinceId,CityId,CountryId FROM ProvinceCityCountry WHERE CityId=? AND CountryId=?";
		List list=Data.Query(sql,new Object[]{cityId,countryId});
		return list;
	}
	
	/**
	 *  获取省市区名称
	 * @param cityId 市Id
	 * @param countryId 省Id
	 * @return 省市区名称
	 */
	public static List getAddressById(int cityId,int countryId){
		String sql="SELECT ProvinceName,CityName,CountryName FROM ProvinceCityCountry WHERE CityId=? AND CountryId=?";
		return (List)Data.GetOne(sql,new Object[]{cityId,countryId});
	}
//	
	
	/**
	 * 根据市 ID，省ID。获取
	 * @param 市ID
	 * @param 省ID
	 * @return
	 */
	public static List getProvinceCityInfoById(int cityId,int provinceId){
		String sql="SELECT ProvinceId,CityId,CountryId FROM ProvinceCityCountry WHERE ProvinceId=? and CityId=?";
		List list=Data.Query(sql,new Object[]{provinceId,cityId});
		return list;
	}
			
	public static int getIdByProp(String prop,String id,String val){
		String sql="SELECT "+prop+" FROM ProvinceCityCountry WHERE "+id+"=?";
		List list=(List)Data.GetOne(sql,new Object[]{val});
		return list!=null?(Integer)list.get(0):0;
	}
	
	/**
	 * 根据市 ID，省ID。获取
	 * @param 省名称
	 * @param 市名称
	 * @return
	 */
	public static List getProvinceCityAddressById(int cityId,int provinceId){
		String sql="SELECT p.ProvinceName,c.cityName FROM (SELECT DISTINCT ProvinceName,ProvinceId FROM ProvinceCityCountry WHERE ProvinceId=?)p JOIN (SELECT DISTINCT cityName,ProvinceId " +
				"FROM  ProvinceCityCountry WHERE  CityId=? AND ProvinceId=?) c ON p.ProvinceId=c.ProvinceId";
		return (List)Data.GetOne(sql,new Object[]{provinceId,cityId,provinceId});
		
	}
	
	public static String getJsonForPc(){
		StringBuilder jsonB=new StringBuilder();
		jsonB.append("{");
		List<Bean> list=Data.Query("default","select * from ProvinceCityCountry", new Object[]{},ProvinceCityCountryBean.class);
		int index=0;
		for(Bean b : list){
			ProvinceCityCountryBean pcb=(ProvinceCityCountryBean) b;
			if(index>0){
				jsonB.append(",");
			}
			//省
			if(pcb.getCityId()==0){
				jsonB.append("\""+pcb.getPCCId()+"\""+" : ").append("[\""+pcb.getProvinceName()+"\""+","+"\""+0+"\",\""+pcb.getProvinceId()+"\"]");
			}else{//市
				jsonB.append("\""+pcb.getPCCId()+"\""+" : "+"[\""+pcb.getCityName()+"\",\""+pcb.getProvinceId()+"\",\""+pcb.getCityId()+"\"]");
			}
			index++;
		}
		jsonB.append("}");
		return jsonB.toString();
		
	}
	
	public static void main(String[] args) {
		  String s = "中国 ";
		  try {
		   byte[] b = s.getBytes();
		   String str = " ";
		   for (int i = 0; i < b.length; i++) {
		    Integer I = new Integer(b[i]);
		    String strTmp = I.toHexString(b[i]);
		    if (strTmp.length() > 2)
		     strTmp = strTmp.substring(strTmp.length() - 2);
		    str = str + strTmp;
		   }
		   log.info(str.toUpperCase());
		  } catch (Exception e) {
		   log.error(e.toString(), e);
		  }
	}

}
