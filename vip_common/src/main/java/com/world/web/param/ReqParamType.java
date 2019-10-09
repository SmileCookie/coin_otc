package com.world.web.param;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/****
*   
 */
public enum ReqParamType {

	INT,LONG,DOUBLE,STRING,TIMESTAMP,BOOLEAN,FLOAT;

	private static Logger log = Logger.getLogger(ReqParamType.class.getName());
	
	public Object getVal(String reqParam){
		Object o = null;
		try {
			if(this.equals(INT)){
				o = Integer.parseInt(reqParam);
			}else if(this.equals(LONG)){
				o = Long.parseLong(reqParam);
			}else if(this.equals(DOUBLE)){
				o = Double.parseDouble(reqParam);
			}else if(this.equals(TIMESTAMP)){
				String pattern = "yyyy-MM-dd HH:mm:ss";
				String dat = reqParam;
				String[] dats = dat.split(" ");
				if(dats.length == 1){
					pattern = "yyyy-MM-dd";
				}else if(dats.length == 2){
					String[] hms = dats[1].split(":");
					if(hms.length == 1){
						pattern = "yyyy-MM-dd HH";
					}else if(hms.length == 2){
						pattern = "yyyy-MM-dd HH:mm";
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				try {
					o = new Timestamp(sdf.parse(reqParam).getTime());
				} catch (ParseException e) {
					log.error(e.toString(), e);
				}
			}else if(this.equals(BOOLEAN)){
				if(reqParam.equals("1") || reqParam.equals("true")){
					o = true;
				}else{
					o = false;
				}
			}else if(this.equals(STRING)){
				o = reqParam;
				if(reqParam.equals("0")){
					o = "";
				}
			}else if(this.equals(FLOAT)){
				o = Float.parseFloat(reqParam);
			}else {
				o = reqParam;
			}
		} catch (NumberFormatException e) {
			//log.error(e.toString(), e);
			
			throw new NumberFormatException("当前参数值：["+reqParam+"]与需求的传递类型不匹配，转换失败。");
		}
		return o;
	}
}
