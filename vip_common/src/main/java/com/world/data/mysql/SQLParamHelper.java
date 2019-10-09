package com.world.data.mysql;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.world.data.mysql.config.BaseConfigBean;

/** 
 * SQLParamHelper SQL参数辅助器工具类 
 * @author CodingMouse 
 * @version 1.0.0.1 2009-3-26 
 */  
public class SQLParamHelper {  
  
	private static Logger log = Logger.getLogger(SQLParamHelper.class);
	/**
	 * 功能:根据配置的参数设定,相对安全,因为仅仅是设置所以无需返回
	 * @param param 参数
	 * @param ps statement
	 * @param configBean 配置项
	 * @return 返回值无
	 */
	public static void SetPrama(Object[] param, PreparedStatement ps,BaseConfigBean configBean)
	{
		try
		{
		 for(int i=0;i<configBean.dataFeilds.size();i++)
		 {
			FeildType ft=configBean.dataFeilds.get(i).feildType;
			String value="";
			if(param.length>i)
			{
				if(param[i]==null)//如果参数为空就用默认代替
					value=configBean.dataFeilds.get(i).defaultValue;
				else
				   value=param[i].toString();
			}
			else//如果index位数不对就是用默认的值代替
				value=configBean.dataFeilds.get(i).defaultValue;
			switch(ft)
			{
			case TInt:
				ps.setInt(i+1,Integer.parseInt(value));
				break;
			case TMEDIUMINT:
				ps.setInt(i+1,Integer.parseInt(value));
				break;
			case TFloat:
				ps.setFloat(i+1,Float.parseFloat(value));
				break;
			case TDOUBLE:
				ps.setDouble(i+1,Double.parseDouble(value));
				break;
			case TBIT:
				 if(param[i]==null||param[i].toString().toLowerCase()=="false"||param[i].toString().toLowerCase()=="0")
            		 ps.setInt(i + 1,0);
            	 else
            		 ps.setInt(i + 1,1);
				//ps.setBoolean(i+1,Boolean.parseBoolean(value));
				break;
			case TBigInt:
				ps.setLong(i+1,Long.parseLong(value));
				break;
			case TDateTime:
				//log.info("格式化:"+value+":");
				ps.setTimestamp(i+1,Timestamp.valueOf(value));
				break;
			case TDate:
				ps.setDate(i+1, Date.valueOf(value));
				break;
			case TTime:
				ps.setTime(i+1, Time.valueOf(value));
				break;
			default:
				ps.setString(i+1,value);
			 break;
			}
		 }
		}catch(Exception ex)
		{
			log.error(ex.toString(), ex);
		}
		//return ps;
	}
	/**
	 * 功能:获取需要执行的sql语句的合成参数
	 * @param param 参数
	 * @param ps statement 预处理过程
	 * @param configBean 配置项
	 * @return 返回值无
	 */
	public static String GetPrama(Object[] param, String progrom,BaseConfigBean configBean)
	{
	  try
	  {
		
		 StringBuilder sb=new StringBuilder();
		 int i=0;
		 String value="";
			sb.append(progrom+":");

		 for(;i<configBean.dataFeilds.size();i++)
		 {
	
			FeildType ft=configBean.dataFeilds.get(i).feildType;
			if(param.length>i)
			{
				if(param[i]==null)//如果参数为空就用默认代替
					value=configBean.dataFeilds.get(i).defaultValue;
				else
				   value=param[i].toString();
			}
			else//如果index位数不对就是用默认的值代替
				value=configBean.dataFeilds.get(i).defaultValue;
			value=value+",";
			
			switch(ft)
			{
			case TInt:
				sb.append(value);
				break;
			case TMEDIUMINT:
				sb.append(value);
				break;
			case TFloat:
				sb.append(value);
				break;
			case TDOUBLE:
				sb.append(value);
				break;
			case TBIT:
				sb.append(value);
				break;
			case TBigInt:
				sb.append(value);
				break;
			case TDateTime:
				//log.info("格式化:"+value+":");
				sb.append("'"+value+"'");
				break;
			case TDate:
				sb.append("'"+value+"'");
				break;
			case TTime:
				sb.append("'"+value+"'");
				break;
			default:
				sb.append("'"+value+"'");
			 break;
			}
		   }
		
			return sb.toString();
	}catch(Exception ex)
	{
		log.debug(ex.toString());
		log.error(ex.toString(), ex);
		return ex.toString(); 
	}
		//return ps;
	}
	/**
	 * 功能:将一个beans配置到一个PreparedStatement参数中,参数类型bean
	 * @param param 配置参数
	 * @param ps statement
	 * @param configBean 配置项
	 * @return 返回值无
	 */
	public static void SetPrama(Bean param, PreparedStatement ps,BaseConfigBean configBean)
	{
		try
		{
		 for(int i=1;i<=configBean.dataFeilds.size();i++)
		 {
			//setPrama(i,dataFeilds.get(i),ps,"d");
			
			DataFeild df=configBean.dataFeilds.get(i-1);
			FeildType ft=df.feildType;
                 // 取得第i列列名  
                 String setMethodName =df.feildName; 
                 // 通过命名规则处理第i列列名,取得 pojo 中对应字段的取值(setter)方法名  
                 setMethodName = "get"  
                    + setMethodName.substring(0, 1).toUpperCase()  
                    + setMethodName.substring(1);  

                 // 当前反射方法  
                 Method method = null;  
                 // 对应第i列的SQL数据类型人工映射到对应的Java数据类型，  
                 // 并反射执行该列的在 pojo 中对应属性的 setter 方法完成赋值  TDOUBLE,
                switch(ft)
     			{
                  case TInt:
                	 method = param.getClass().getMethod(setMethodName);  //获取方法
                	 if(method!=null)
                   	 {
                           Object obj= method.invoke(param);//执行方法
                           if(obj==null)
                        	   obj=df.defaultValue;
                           ps.setInt(i,Integer.parseInt(obj.toString()));
                   	 }
                     else
                       	 ps.setInt(i,Integer.parseInt(df.defaultValue));
     				 break;
                 case TMEDIUMINT:
                	 method = param.getClass().getMethod(setMethodName);  //获取方法
                	 if(method!=null)
                   	 {
                           Object obj= method.invoke(param);//执行方法
                           if(obj==null)
                        	   obj=df.defaultValue;
                           ps.setInt(i,Integer.parseInt(obj.toString()));
                   	 }
                     else
                       	 ps.setInt(i,Integer.parseInt(df.defaultValue));
     				break;
                 case TFloat:
                	 method = param.getClass().getMethod(setMethodName);  //获取方法
                	 if(method!=null)
                   	 {
                           Object obj= method.invoke(param);//执行方法
                           log.info(obj);
                           if(obj==null)
                        	   obj=df.defaultValue;
                           log.info(obj);
                           ps.setFloat(i,Float.parseFloat(obj.toString()));
                   	 }
                     else
                       	   ps.setFloat(i,Float.parseFloat(df.defaultValue));
                	 break;
                 case TBIT:
                	   method = param.getClass().getMethod(setMethodName);  //这里了先获bool的设置
                	   if(method!=null)
                     	 {
                             Object obj= method.invoke(param);//执行方法
                             if(obj==null)
                          	   obj=df.defaultValue;
                             ps.setBoolean(i,Boolean.parseBoolean(obj.toString()));
                     	 }
                       else
                         	 ps.setBoolean(i,Boolean.parseBoolean(df.defaultValue));
                	 break;
                 case TBigInt:
                	 method = param.getClass().getMethod(setMethodName);  //获取方法
                	 if(method!=null)
                 	 {
                         Object obj= method.invoke(param);//执行方法
                         if(obj==null)
                      	   obj=df.defaultValue;
                         ps.setLong(i,Long.parseLong(obj.toString()));
                 	 }
                     else
                     	 ps.setLong(i,Long.parseLong(df.defaultValue));
                	 break;
                 case TDateTime:
                	 method = param.getClass().getMethod(setMethodName);  //获取方法
                	 if(method!=null)
                 	 {
                         Object obj= method.invoke(param);//执行方法
                         if(obj==null)
                      	   obj=df.defaultValue;
                         ps.setTimestamp(i,Timestamp.valueOf(obj.toString()));
                 	 }
                     else
                     	 ps.setDate(i,Date.valueOf(df.defaultValue));
                	 break;
                 case TDate:
                	 method = param.getClass().getMethod(setMethodName);  //获取方法
                	 if(method!=null)
                 	 {
                         Object obj= method.invoke(param);//执行方法
                         if(obj==null)
                      	   obj=df.defaultValue;
                         ps.setDate(i,Date.valueOf(obj.toString()));
                 	 }
                     else
                     	 ps.setDate(i,Date.valueOf(df.defaultValue));
                	 break;
                 case TTimeStamp:
                	 method = param.getClass().getMethod(setMethodName);  //获取方法
                	 if(method!=null)
                 	 {
                         Object obj= method.invoke(param);//执行方法
                         if(obj==null)
                      	   obj=df.defaultValue;
                         ps.setTimestamp(i,Timestamp.valueOf(obj.toString()));
                 	 }
                     else
                     	 ps.setTimestamp(i,Timestamp.valueOf(df.defaultValue));
                	 break;
                 case TTime:
                	 method = param.getClass().getMethod(setMethodName);  //获取方法
                	 if(method!=null)
                 	 {
                         Object obj= method.invoke(param);//执行方法
                         if(obj==null)
                      	   obj=df.defaultValue;
                         ps.setTime(i,Time.valueOf(obj.toString()));
                 	 }
                     else
                     	 ps.setTime(i,Time.valueOf(df.defaultValue));
                	 break;
                 default: //默认的都是字符串
                	 method = param.getClass().getMethod(setMethodName);  //获取方法
                	 if(method!=null)
                 	 {
                         Object obj= method.invoke(param);//执行方法
                         if(obj==null)
                      	   obj=df.defaultValue;
                         ps.setString(i,obj.toString());
                 	 }
                     else
                     	 ps.setString(i,df.defaultValue);
                	 break;
     			}
		 }
		}catch(Exception ex)
		{
			log.error(ex.toString(), ex);
			//throw new Exception("Java程序中包含不支持的自动映射数据类型：" +ex.toString());
		}
		//return ps;
	}
    
	/** 
      * 自动将Java数据类型参数映射转换为SQL参数,不安全的方法 
      * @param param Java数据类型参数数组 
      * @param ps SQL命令执行对象 
      * @return 返回自动匹配添加好SQL参数的SQl命令执行对象 
      */  
     public static PreparedStatement JavaParam2SQLParam(Object[] param, PreparedStatement ps) {  
         // 使用Class.isInstance()方法判定指定的 Object   
         // 是否与此 Class 所表示的对象赋值兼容。  
         // 此方法是 Java 语言 instanceof 运算符的动态等效方法。  
         // 如果当前SQL命令包含参数则进行Java数据类型与SQL命令参数的人工映射  
         if (param != null && param.length > 0) {  
             try {  
                 for (int i = 0; i < param.length; i++) {  
                     // 将第i个参数的Java数据类型通过其对应的包装器类人工映射并设定SQL命令参数  
                     if (Boolean.class.isInstance(param[i])) {  
                    	 // 映射boolean类型  
                    	 if(param[i]==null||param[i].toString().toLowerCase()=="false"||param[i].toString().toLowerCase()=="0")
                    		 ps.setInt(i + 1,0);
                    	 else
                    		 ps.setInt(i + 1,1);
                         //ps.setBoolean(i + 1, Boolean.parseBoolean(param[i].toString()));  
                     } else if (Byte.class.isInstance(param[i])) {              // 映射byte类型  
                         ps.setByte(i + 1, Byte.parseByte(param[i].toString()));  
                     } else if (byte[].class.isInstance(param[i])) {  
                         ps.setBytes(i + 1, (param[i].toString()).getBytes());  // 映射byte[]类型  
                     } else if (Character.class.isInstance(param[i])  
                         || String.class.isInstance(param[i])) {                // 映射char和String类型  
                         ps.setString(i + 1, String.valueOf(param[i]));  
                     } else if (Short.class.isInstance(param[i])) {             // 映射short类型  
                         ps.setShort(i + 1, Short.parseShort(param[i].toString()));  
                     } else if (Integer.class.isInstance(param[i])) {           // 映射int类型  
                         ps.setInt(i + 1, Integer.parseInt(param[i].toString()));  
                     } else if (Long.class.isInstance(param[i])) {              // 映射long类型  
                         ps.setLong(i + 1, Long.parseLong(param[i].toString()));  
                     } else if (Float.class.isInstance(param[i])) {             // 映射float类型  
                         ps.setFloat(i + 1, Float.parseFloat(param[i].toString()));  
                     } else if (Double.class.isInstance(param[i])) {            // 映射double类型  
                         ps.setDouble(i + 1, Double.parseDouble(param[i].toString()));  
                     } else if (BigDecimal.class.isInstance(param[i])) {        // 映射BigDecimal类型  
                         ps.setBigDecimal(i + 1, new BigDecimal(param[i].toString()));  
                     } else if (Date.class.isInstance(param[i])) {              // 映射Date类型  
                         ps.setDate(i + 1, Date.valueOf(param[i].toString()));  
                     } else if (Time.class.isInstance(param[i])) {              // 映射Time类型  
                         ps.setTime(i + 1, Time.valueOf(param[i].toString()));  
                     } else if (Timestamp.class.isInstance(param[i])) {         // 映射Timestamp类型  
                         //ps.setTimestamp(i + 1, Timestamp.valueOf(param[i].toString())); 
                    	 ps.setString(i + 1, param[i].toString());
                     } else { 
                    	 //所有不认识的类型都当作int类型处理
                    	 if(param[i]!=null){
                    		 ps.setString(i + 1, param[i].toString());
                    	 }else{
                    		 ps.setString(i + 1, null);
                    	 }
                    	   
                        // throw new Exception("Java程序中包含不支持的自动映射数据类型：" + param[i].getClass().getName());  
                     }  
                 }  
             }  catch(SQLException ex) { 
             	log.error("Sql异常：" + ex.toString(), ex);
             } catch (Exception ex) {
             	log.error("Data请求异常：" + ex.toString(), ex);
             }     
         }  
           
         // 返回结果  
         return ps;  
           
     }  
     
     /** 
      * 自动将Java数据类型参数映射转换为SQL参数,不安全的方法 
      * @param param Java数据类型参数数组 
      * @param ps SQL命令执行对象 
      * @return 返回自动匹配添加好SQL参数的SQl命令执行对象 
      */  
     public static String GetPrama(Object[] param, String progrom) {  
         // 使用Class.isInstance()方法判定指定的 Object   
         // 是否与此 Class 所表示的对象赋值兼容。  
         // 此方法是 Java 语言 instanceof 运算符的动态等效方法。  
         // 如果当前SQL命令包含参数则进行Java数据类型与SQL命令参数的人工映射  
    	 String s[]=progrom.split("\\?");
    	 StringBuilder sb=new StringBuilder();
    	 int i =0;
         if (param != null && param.length > 0) {  
             try {  
            	 
                 for (i=0; i < param.length; i++) {  
                	 sb.append(s[i]);
                     // 将第i个参数的Java数据类型通过其对应的包装器类人工映射并设定SQL命令参数  
                     if (Boolean.class.isInstance(param[i])) { 
                    	 sb.append(param[i].toString());// 映射boolean类型  
                     } else if (Byte.class.isInstance(param[i])) {              // 映射byte类型  
                    	 sb.append(param[i].toString());
                     } else if (byte[].class.isInstance(param[i])) {  
                    	 sb.append(param[i].toString());  // 映射byte[]类型  
                     } else if (Character.class.isInstance(param[i])  
                         || String.class.isInstance(param[i])) {                // 映射char和String类型  
                    	 sb.append("'"+param[i].toString()+"'"); 
                     } else if (Short.class.isInstance(param[i])) {             // 映射short类型  
                    	 sb.append(param[i].toString());
                     } else if (Integer.class.isInstance(param[i])) {           // 映射int类型  
                    	 sb.append(param[i].toString()); 
                     } else if (Long.class.isInstance(param[i])) {              // 映射long类型  
                    	 sb.append(param[i].toString());
                     } else if (Float.class.isInstance(param[i])) {             // 映射float类型  
                    	 sb.append(param[i].toString());
                     } else if (Double.class.isInstance(param[i])) {            // 映射double类型  
                    	 sb.append(param[i].toString());  
                     } else if (BigDecimal.class.isInstance(param[i])) {        // 映射BigDecimal类型  
                    	 sb.append("'"+param[i].toString()+"'");
                     } else if (Date.class.isInstance(param[i])) {              // 映射Date类型  
                    	 sb.append("'"+param[i].toString()+"'");
                     } else if (Time.class.isInstance(param[i])) {              // 映射Time类型  
                    	 sb.append("'"+param[i].toString()+"'"); 
                     } else if (Timestamp.class.isInstance(param[i])) {         // 映射Timestamp类型  
                         //ps.setTimestamp(i + 1, Timestamp.valueOf(param[i].toString())); 
                    	 sb.append("'"+param[i].toString()+"'");
                     } else {  
                    	 if(param[i]!=null)
                    	     sb.append("'"+param[i].toString()+"'"); 
                     }  
                 }  
             }catch (Exception ex) {
             	log.error("Data请求异常：" + ex.toString(), ex);
             } 
             if(i>0&&i<=param.length&&i<s.length)
          	   sb.append(s[i]);
           } 
           else
        	   sb.append(progrom);
           
         // 返回结果  
         return sb.toString();  
           
     }  
     /** 
      * 自动将Javabean参数映射转换为SQL参数 
      * @param param Java数据类型参数数组 
      * @param ps SQL命令执行对象 
      * @return 返回自动匹配添加好SQL参数的SQl命令执行对象 
      */  
     public static PreparedStatement JavaBean2SQLParam(Object[] param, PreparedStatement ps) {  
          
         // 使用Class.isInstance()方法判定指定的 Object   
         // 是否与此 Class 所表示的对象赋值兼容。  
         // 此方法是 Java 语言 instanceof 运算符的动态等效方法。  
         // 如果当前SQL命令包含参数则进行Java数据类型与SQL命令参数的人工映射  
         if (param != null && param.length > 0) {  
             try {  
                 for (int i = 0; i < param.length; i++) {  
                     // 将第i个参数的Java数据类型通过其对应的包装器类人工映射并设定SQL命令参数  
                     if (Boolean.class.isInstance(param[i])) {                  // 映射boolean类型  
                         ps.setBoolean(i + 1, Boolean.parseBoolean(param[i].toString()));  
                     } else if (Byte.class.isInstance(param[i])) {              // 映射byte类型  
                         ps.setByte(i + 1, Byte.parseByte(param[i].toString()));  
                     } else if (byte[].class.isInstance(param[i])) {  
                         ps.setBytes(i + 1, (param[i].toString()).getBytes());  // 映射byte[]类型  
                     } else if (Character.class.isInstance(param[i])  
                         || String.class.isInstance(param[i])) {                // 映射char和String类型  
                         ps.setString(i + 1, String.valueOf(param[i]));  
                     } else if (Short.class.isInstance(param[i])) {             // 映射short类型  
                         ps.setShort(i + 1, Short.parseShort(param[i].toString()));  
                     } else if (Integer.class.isInstance(param[i])) {           // 映射int类型  
                         ps.setInt(i + 1, Integer.parseInt(param[i].toString()));  
                     } else if (Long.class.isInstance(param[i])) {              // 映射long类型  
                         ps.setLong(i + 1, Long.parseLong(param[i].toString()));  
                     } else if (Float.class.isInstance(param[i])) {             // 映射float类型  
                         ps.setFloat(i + 1, Float.parseFloat(param[i].toString()));  
                     } else if (Double.class.isInstance(param[i])) {            // 映射double类型  
                         ps.setDouble(i + 1, Double.parseDouble(param[i].toString()));  
                     } else if (BigDecimal.class.isInstance(param[i])) {        // 映射BigDecimal类型  
                         ps.setBigDecimal(i + 1, new BigDecimal(param[i].toString()));  
                     } else if (Date.class.isInstance(param[i])) {              // 映射Date类型  
                         ps.setDate(i + 1, Date.valueOf(param[i].toString()));  
                     } else if (Time.class.isInstance(param[i])) {              // 映射Time类型  
                         ps.setTime(i + 1, Time.valueOf(param[i].toString()));  
                     } else if (Timestamp.class.isInstance(param[i])) {         // 映射Timestamp类型  
                         ps.setTimestamp(i + 1, Timestamp.valueOf(param[i].toString()));  
                     } else {  
                         throw new Exception("Java程序中包含不支持的自动映射数据类型：" + param[i].getClass().getName());  
                     }  
                 }  
             }  catch(SQLException ex) {  
             	log.error("Sql异常：" + ex.toString(), ex);
             } catch (Exception ex) { 
             	log.error("Data请求异常：" + ex.toString(), ex);
             } 
               
         }  
           
         // 返回结果  
         return ps;  
           
     }  
      
     /** 
      * 自动将SQL数据类型参数映射转换为Java数据类型 
      * @param <T> 泛型类型参数 
      * @param rsmd 关于 ResultSet 对象中列的类型和属性信息的 ResultSetMetaData 对象 
      * @param pojoClass 利用Java反射机制查找对应 pojo 的Class 
      * @param rs 结果集对象 
      * @return 泛型 pojo 实例 
      */  
     @SuppressWarnings("unchecked")  // 禁用对此方法的类型安全检查  
    public static <T> T SQLParam2JavaParam(ResultSetMetaData rsmd, Class<?> pojoClass, ResultSet rs) {  
            
         // 定义泛型 pojo 实例  
         T pojo = null;  
           
         try {  
               
             // 生成单个泛型 pojo 实例  
             pojo = (T)pojoClass.newInstance();  
               
             // 遍历数据集的每一列，通过共同遵守的Pascal命名规则反射查找并执行对应   
             // pojo 类的赋值(getter)方法以实现结果集到pojo泛型集合的自动映射  
             for (int i = 1; i <= rsmd.getColumnCount(); i++) {  
      
                 // 取得第i列列名  
                 String setMethodName = rsmd.getColumnName(i);  
                 // 通过命名规则处理第i列列名,取得 pojo 中对应字段的取值(setter)方法名  
                 setMethodName = "set"  
                    + setMethodName.substring(0, 1).toUpperCase()  
                    + setMethodName.substring(1);  
                 // 取得第i列的数据类型  
                 int dbType = rsmd.getColumnType(i);  
                 // 当前反射方法  
                 Method method = null;  
                 // 对应第i列的SQL数据类型人工映射到对应的Java数据类型，  
                 // 并反射执行该列的在 pojo 中对应属性的 setter 方法完成赋值  
                 if (dbType == Types.TINYINT) {  
                     method = pojoClass.getMethod(setMethodName, byte.class);  
                     method.invoke(pojo, rs.getByte(i));  
                 } else if (dbType == Types.SMALLINT) {  
                     method = pojoClass.getMethod(setMethodName, short.class);  
                     method.invoke(pojo, rs.getShort(i));  
                 } else if (dbType == Types.INTEGER) {  
                     method = pojoClass.getMethod(setMethodName, int.class);  
                     method.invoke(pojo, rs.getInt(i));  
                 } else if (dbType == Types.BIGINT) {  
                     method = pojoClass.getMethod(setMethodName, long.class);  
                     method.invoke(pojo, rs.getLong(i));  
                 } else if (dbType == Types.FLOAT  
                         || dbType == Types.REAL) {  
                     method = pojoClass.getMethod(setMethodName, float.class);  
                     method.invoke(pojo, rs.getFloat(i));  
                 } else if (dbType == Types.DOUBLE) {  
                     method = pojoClass.getMethod(setMethodName, double.class);  
                     method.invoke(pojo, rs.getDouble(i));  
                 } else if (dbType == Types.DECIMAL  
                         || dbType == Types.NUMERIC) {  
                     method = pojoClass.getMethod(setMethodName, BigDecimal.class);  
                     method.invoke(pojo, rs.getBigDecimal(i));  
                 } else if (dbType == Types.BIT) {  
                     method = pojoClass.getMethod(setMethodName, boolean.class);  
                     method.invoke(pojo, rs.getBoolean(i));  
                 } else if (dbType == Types.CHAR  
                         || dbType == Types.VARCHAR  
                         || dbType == Types.LONGVARCHAR  
                         || dbType == Types.CLOB) {  
                     method = pojoClass.getMethod(setMethodName, String.class);  
                     method.invoke(pojo, rs.getString(i));  
                 } else if (dbType == Types.DATE) {       // 继承于 java.util.Date 类  
                     method = pojoClass.getMethod(setMethodName, Date.class);  
                     method.invoke(pojo, rs.getDate(i));  
                 } else if (dbType == Types.TIME) {       // 继承于 java.util.Date 类  
                     method = pojoClass.getMethod(setMethodName, Time.class);  
                     method.invoke(pojo, rs.getTime(i));  
                 } else if (dbType == Types.TIMESTAMP) {  // 继承于 java.util.Date 类  
                     method = pojoClass.getMethod(setMethodName, Timestamp.class);  
                     method.invoke(pojo, rs.getTimestamp(i));  
                 } else if (dbType == Types.BINARY  
                         || dbType == Types.VARBINARY  
                         || dbType == Types.LONGVARBINARY  
                         || dbType == Types.BLOB) {  
                     method = pojoClass.getMethod(setMethodName, byte[].class);  
                     method.invoke(pojo, rs.getBytes(i));  
                 } else {  
                     throw new Exception("数据库中包含不支持的自动映射数据类型：" + dbType);  
                 }  
             }  
         } catch (InstantiationException ex) {  
             log.error("异常信息：参数错误，指定的类对象无法被 Class 类中的 newInstance 方法实例化！\r\n" + ex.toString(), ex);
         } catch (NoSuchMethodException ex) {  
             log.error("异常信息：参数错误，无法找到某一特定的方法！\r\n" + ex.toString(), ex);
         } catch (IllegalAccessException ex) {  
             log.error("异常信息：参数错误，对象定义无法访问，无法反射性地创建一个实例！\r\n" + ex.toString(), ex);
         } catch (InvocationTargetException ex) {  
             log.error("异常信息：参数错误，由调用方法或构造方法所抛出异常的经过检查的异常！\r\n" + ex.toString(), ex);
         } catch (SecurityException ex) {  
             log.error("异常信息：参数错误，安全管理器检测到安全侵犯！\r\n" + ex.toString(), ex);
         } catch (IllegalArgumentException ex) {  
             log.error("异常信息：参数错误，向方法传递了一个不合法或不正确的参数！\r\n" + ex.toString(), ex);
         } catch (SQLException ex) {  
             log.error("异常信息：参数错误，获取数据库连接对象错误！\r\n" + ex.toString(), ex);
         } catch (Exception ex) {  
             log.error("异常信息：程序兼容问题！\r\n" + ex.toString(), ex);
         }  
               
         // 返回结果  
         return pojo;  
     }  
    /**
 	* 功能:把resultSet转化成xml
    * @param rs resultset 从数据库返回
 	* @return
 	*/
 	public static String parseResultsetToXml(ResultSet rs )
 	{
 		 StringBuilder sb=new StringBuilder();
 		 try{
 	      ResultSetMetaData rsmd = rs.getMetaData();
 	      int colCount = rsmd.getColumnCount();
 	      while (rs.next()) {
 	       sb.append("<DataRow>");
 	       
 	        for (int ii = 1; ii <= colCount; ii++) {
 	           String columnName = rsmd.getColumnName(ii);
 	           sb.append("<"+columnName+">");
 	           sb.append(rs.getString(ii));
 	           sb.append("</"+columnName+">");
 	        }
 	        sb.append("</DataRow>");
 	      }
 		 }catch(Exception ex)
 		 {
 			 log.error(ex.toString(), ex);
 		 }
 		 return sb.toString();
 	}
 	/**
 	 * 功能:实现结果集到实体对象/值对象/持久化对象转换
 	 * @param rsResult 数据库集
 	 * @param strEntity 类名称(字符串)
 	 * @return object数组
 	 * @throws Exception
 	 */
 	public static Object[] parseDataEntityBeans(ResultSet rsResult,String strEntity) throws Exception{
 	
 		//注册实体,strEntity指定的实体类名称字符串
 		Class classEntity=Class.forName(strEntity);
 		return ParseEntityBean(rsResult,classEntity);
 	}
 	/**
 	 * 功能:返回数组形式的result转化对象
 	 * @param rsResult
 	 * @param classEntity
 	 * @return
 	 * @throws Exception
 	 */
 	public static Object[] ParseEntityBean(ResultSet rsResult,Class classEntity) throws Exception{
 		//以数组方式返回
 		java.util.List listResult=parseDataEntityBeans(rsResult,classEntity);
 		Object objResultArray=Array.newInstance(classEntity, listResult.size());
 		listResult.toArray((Object[]) objResultArray);
 		return (Object[]) objResultArray;
 	}
 	/**
 	 * 功能:返回list形式的result转化对象
 	 * @param rsResult 数据库集
 	 * @param strEntity 类名称(字符串)
 	 * @return object数组
 	 * @throws Exception
 	 */
 	public static <T extends Bean> List<T> parseDataEntityBeans(ResultSet rsResult,Class<T> classEntity) throws Exception{
 		DataTableEntity  dataTable=null;
 		List<T> listResult=new ArrayList<T>();
 		//获取实体中定义的方法
 		HashMap hmMethods=new HashMap();
 		for(int i=0;i<classEntity.getDeclaredMethods().length;i++)
 		{
 			MethodEntity methodEntity=new MethodEntity();
 			//方法的名称
 			String methodName=classEntity.getDeclaredMethods()[i].getName();
 			String methodKey=methodName.toUpperCase();
 			//方法的参数
 			Class[] paramTypes=classEntity.getDeclaredMethods()[i].getParameterTypes();
 			methodEntity.setMethodName(methodName);
 			methodEntity.setMethodParamTypes(paramTypes);
 			//处理方法重载
 			if(hmMethods.containsKey(methodKey)){
 				methodEntity.setRepeatMethodNum(methodEntity.getRepeatMethodNum()+1);
 				methodEntity.setRepeatMethodsParamTypes(paramTypes);
 			}
 			else
 				hmMethods.put(methodKey,methodEntity);
 		}
 		//处理reault结构体信息
 		if(rsResult!=null){
 			ResultSetMetaData rsMetaData=rsResult.getMetaData();
 			int columnCount=rsMetaData.getColumnCount();
 			dataTable=new DataTableEntity(columnCount);
 			//获取字段名称,类型
 			for(int i=0;i<columnCount;i++){
 				String label = rsMetaData.getColumnLabel(i+1);
 				String columnName = label != null ? label : rsMetaData.getColumnName(i+1);
 				int columnType=rsMetaData.getColumnType(i+1);
 				dataTable.setColumnNames(columnName,i);
 				dataTable.setColumnType(columnType,i);
 			}
 		}
 		//处理resultset数据信息
 		while(rsResult.next()){
 			//调用方法,根据字段名在hsMethods中查找对应的set方法
 			Object objResult=ParseObjectFromResultSet(rsResult,dataTable,classEntity,hmMethods);
 			listResult.add((T)objResult);
 		}
 		return listResult;
 		
 	}
 	/**
 	 * 功能:返回list形式的result转化对象,每行也是一个list
 	 * @param rsResult 数据库集
 	 * @param strEntity 类名称(字符串)
 	 * @return object数组
 	 * @throws Exception
 	 */
 	public static List<Object> parseDataEntityBeans(ResultSet rs) throws Exception{

 		List<Object> listResult=new ArrayList<Object>();
 		 ResultSetMetaData meta = rs.getMetaData();
         int m_Cols = meta.getColumnCount();
       
         while (rs.next()) {
             ArrayList<Object> list = new ArrayList<Object>();
             for (int i = 1; i <= m_Cols; i++) {
            	 int t=meta.getColumnType(i);
            	 if(Types.TINYINT==t){
            		 Object obj = rs.getObject(i);
            		// System.out.print("TINYINTTINYINTTINYINTTINYINT======="+obj);
            		 if(obj==null||Integer.parseInt(obj.toString())==0)
                        list.add(Boolean.FALSE);
            		 else
            			list.add(Boolean.TRUE);
            	 }else{
            	    Object obj = rs.getObject(i);
                    list.add(obj);
            	 }
             }
             listResult.add(list);
         }
        
 		return listResult;
 		
 	}
 	
 	/**
 	 * 功能:返回list形式的result转化对象,每行也是一个list,第一行是Names
 	 * @param rsResult 数据库集
 	 * @param strEntity 类名称(字符串)
 	 * @return object数组
 	 * @throws Exception
 	 */
 	public static List parseDataEntityBeansNames(ResultSet rs) throws Exception{

 		List listResult=new ArrayList();
 		 ResultSetMetaData meta = rs.getMetaData();
         int m_Cols = meta.getColumnCount();
         ArrayList name = new ArrayList();
         
         for (int i = 1; i <= m_Cols; i++) {
         	name.add(meta.getColumnName(i));
         }
         int m_Rows = 0;
         
         listResult.add(name);
         while (rs.next()) {
             m_Rows++;
             ArrayList list = new ArrayList();
             for (int i = 1; i <= m_Cols; i++) {
            	 int t=meta.getColumnType(i);
            	 if(Types.TINYINT==t){
            		 Object obj = rs.getObject(i);
            		 if(obj==null||Integer.parseInt(obj.toString())==0)
                        list.add(Boolean.FALSE);
            		 else
            			list.add(Boolean.TRUE);
            	 }else
            	 {
            	   Object obj = rs.getObject(i);
                   list.add(obj);
            	 }
             }
             listResult.add(list);
         }
        
 		return listResult;
 		
 	}
 	/**
 	 * 功能:从resultSet中解析出单行记录对象,存储到实体对象中
 	 * @param rs
 	 * @param dataTable
 	 * @param classEntity
 	 * @param hsMethods
 	 * @return
 	 * @throws Exception
 	 */
 	public static Object ParseObjectFromResultSet(
 	ResultSet rs,
 	DataTableEntity dataTable,
 	Class classEntity,
 	java.util.HashMap hsMethods) throws Exception{
 		Object objEntity=classEntity.newInstance();
 		Method method=null;
 		int nColumnCount=dataTable.getColumnCount();
 		String[] strColumnNames=dataTable.getColumnNames();
 		
 		 ResultSetMetaData meta = rs.getMetaData();
 		 
 		for(int i=0;i<nColumnCount;i++){
 			//获取字段值
 			Object objColumnValue=rs.getObject(strColumnNames[i]);
 			//HashMapz中的方法名key值
 			String strMethodKey=null;
 			//获取set方法名
 			if(strColumnNames[i]!=null){
 				strMethodKey=String.valueOf("SET"+strColumnNames[i].toUpperCase());
 			}
 			//值和方法都为空,这里方法名部位空即可,值可以为空的
 			if(strMethodKey!=null){
 			 if(hsMethods.get(strMethodKey)!=null)
 			 {
 				//判断字段的类型,方法名,参数类型
				try{
 					//log.info(strMethodKey);
 					MethodEntity methodEntity=(MethodEntity)hsMethods.get(strMethodKey);
 					String methodName=methodEntity.getMethodName();
 					int repeatMethodNum=methodEntity.getRepeatMethodNum();
 					Class[]  paramTypes=methodEntity.getMethodParamTypes();
 					method=classEntity.getMethod(methodName,paramTypes);
 					//如果重载方法数>1，则判断是否有java.lang.IllegalArgumentException异常,循环处理
 					 int t=dataTable.getColumnTyoe(i);//getColumnType(i);
 					try{
 						//设置参数,实体对象,实体对象方法参数
 						
 		            	 if(Types.TINYINT==t){
 		            		 Object obj = rs.getObject(i);
 		            		 if(obj==null||obj.toString()=="0")
 		            			method.invoke(objEntity, Boolean.FALSE);
 		            		 else
 		            			method.invoke(objEntity,Boolean.TRUE);
 		            	 }else
 		            	 {
 		            		method.invoke(objEntity, new Object[]{objColumnValue});
 		            	 }
 						
 					}catch(java.lang.IllegalArgumentException e){
 						//处理重载方法
 						for(int j=1;j<repeatMethodNum;j++){
 							try{
 								Class[] repeatParamTypes=methodEntity.getRepeatMethodParamTypes(j-1);
 								method=classEntity.getMethod(methodName,repeatParamTypes);
 								
 								 if(Types.TINYINT==t){
 		 		            		 Object obj = rs.getObject(i);
 		 		            		 if(obj==null||obj.toString()=="0")
 		 		            			method.invoke(objEntity, Boolean.FALSE);
 		 		            		 else
 		 		            			method.invoke(objEntity,Boolean.TRUE);
 		 		            	 }else
 		 		            	 {
 		 		            		method.invoke(objEntity,new Object[]{objColumnValue});
 		 		            	 }
 								break;
 							}catch(java.lang.IllegalArgumentException ex)
 							{
 							 continue;
 							}
 						}
 					}
 				}catch(NoSuchMethodException e){
 					//log.error(e.toString(), e);
 					throw new NoSuchMethodException();
 				}catch(Exception ex){
 					log.error("内部异常", ex);
 				}
 			  }
 			}
 		}
 		return objEntity;
 	}
 	/**
 	 * 功能:将result转化成object对象二维数组
 	 * @param rs
 	 * @return
 	 * @throws Exception
 	 */
    public static List getQueryResult(ResultSet rs) throws Exception {
        ResultSetMetaData meta = rs.getMetaData();
        int m_Cols = meta.getColumnCount();
        
        int m_Rows = 0;
       
        ArrayList rtn = new ArrayList();
       
        while (rs.next()) {
            m_Rows++;
            ArrayList list = new ArrayList();
            for (int i = 1; i <= m_Cols; i++) {
            	 int t=meta.getColumnType(i);
            	 if(Types.TINYINT==t){
            		 Object obj = rs.getObject(i);
            		 if(obj==null||Integer.parseInt(obj.toString())==0)
                        list.add(Boolean.FALSE);
            		 else
            			list.add(Boolean.TRUE);
            	 }else
            	 {
            	   Object obj = rs.getObject(i);
                   list.add(obj);
            	 }
            }
            rtn.add(list);
        }
        return rtn;
    }

    /**
 	 * 功能:将result转化成object对象二维数组
 	 * @param rs
 	 * @return
 	 * @throws Exception
 	 */
    public static List getQueryResultWithName(ResultSet rs) throws Exception {
        ResultSetMetaData meta = rs.getMetaData();
        int m_Cols = meta.getColumnCount();
        ArrayList name = new ArrayList();
       
        for (int i = 1; i <= m_Cols; i++) {
        	name.add(meta.getColumnName(i));
        }
        int m_Rows = 0;
        
        ArrayList rtn = new ArrayList();
        rtn.add(name);
        while (rs.next()) {
            m_Rows++;
            ArrayList list = new ArrayList();
            for (int i = 1; i <= m_Cols; i++) {
            	 int t=meta.getColumnType(i);
            	 if(Types.TINYINT==t){
            		 Object obj = rs.getObject(i);
            		 if(obj==null||Integer.parseInt(obj.toString())==0)
                        list.add(Boolean.FALSE);
            		 else
            			list.add(Boolean.TRUE);
            	 }else
            	 {
            	   Object obj = rs.getObject(i);
                   list.add(obj);
            	 }
               
            }
            rtn.add(list);
        }
        return rtn;
    }

}  