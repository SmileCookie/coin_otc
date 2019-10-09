package com.world.data.mysql.config;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.world.data.mysql.DataFeild;
import com.world.data.mysql.FeildType;

/**
 * 功能:数据库应用程序的配置选项
 * @author 凌晓
 *
 */
public class BaseConfigBean implements java.io.Serializable{

 static Logger logger=Logger.getLogger(BaseConfigBean.class);

	/**
	 * 功能:程序名称
	 */
	private int id;
	private String mingzi;
	private String opsCode;
	private String opsShuxing;
	private String opsGroup="default";
	private int quanxian;
	private int datatype;
	private int contenttype;
	private String beizhu;
	public ArrayList<DataFeild> dataFeilds;

	//被使用了多少次
	private int calledTimes=0;
	
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
	 * 功能:解析这个配置成可用的选项
	 */
	public void ParseThis()
	{
//		log.info(mingzi);
		//opsDemo=opsDemo.replace("\n", "").trim();
		opsShuxing=opsShuxing.replace("\n", "").trim();
		dataFeilds=new ArrayList<DataFeild>();
		if(datatype==1)
		{//如果是存储过程重组opsCode
			StringBuilder sb=new StringBuilder();
			sb.append("{CALL ");
			sb.append(mingzi+"(");
			String[] items=opsShuxing.split(";");
			//String[] values=opsDemo.split(";");
			for(int i=0;i<items.length;i++)
			{
				//如果为空就终止循环
				if(items[i].trim().length()==0)
					break;
				if(items[i].split("\\: ").length<3)
				{
					logger.error("如下数据库Item未能正确解析:"+items[i]);
					continue;
				}
//				log.info(items[i]);
				DataFeild df=new DataFeild();
				df.feildName=items[i].split("\\: ")[1].trim();
				df.feildType=getFeildType(items[i].split("\\: ")[0].trim());
				df.idAuto=false;
				df.isKey=false;
				df.isNull=false;

				try{
				df.length=getFeildLength(items[i].split("\\: ")[0].trim());
				try{
				df.defaultValue=items[i].split("\\: ")[2].trim();
				}
				catch(Exception ex)
				{
					logger.error(mingzi+" 的 "+df.feildName+" 项参数个数不对");
				}
				//log.info(items[i]+":::"+df.defaultValue);
				
				df.comment=getFeildComment(items[i]);
				}
				catch(Exception ex){
					logger.error("Id为"+Integer.toString(id)+" ,名字叫做'"+mingzi+"'的加载配置项出错,请认真检查progrom下面的这项");
				}
				dataFeilds.add(df);
				//统计参数格式
				if(i==0)
					sb.append("?");
				else
					sb.append(",?");
			}
			
			sb.append(")}");
			opsCode=sb.toString();
		}
		else
		{

			String[] items=opsShuxing.split(";");

			for(int i=0;i<items.length;i++)
			{
				//如果为空就终止循环
				if(items[i].trim().length()==0)
					break;
				DataFeild df=new DataFeild();
				df.feildName=items[i].split("\\:")[1].trim();
				df.feildType=getFeildType(items[i].split("\\:")[0].trim());
				df.idAuto=false;
				df.isKey=false;
				df.isNull=false;
				df.defaultValue=items[i].split("\\:")[2].trim();
				df.length=getFeildLength(items[i].split("\\:")[0].trim());
				dataFeilds.add(df);

			}

		}
	}
	private String getFeildComment(String item){
		if(item.split("\\:").length>=4)
			return item.split("\\:")[3];
		else
			return "输入正确的"+item.split("\\:")[1].trim();
	}
	
	private int getFeildLength(String item){
		String name=item.split("\\(")[0].trim().toUpperCase();
		if(name.equals("VARCHAR".toUpperCase()))
		{
			int rtn=getInLength(item);
			return rtn==0?50:rtn;
		}
		else if(name.equals("CHAR".toUpperCase()))
		{
			int rtn=getInLength(item);
			return rtn==0?50:rtn;
		}
		else if(name.equals("TEXT".toUpperCase()))
		{
			int rtn=getInLength(item);
			return rtn==0?60*1024:rtn;//最大值60k
		}
		
		else if(name.equals("INT".toUpperCase()))
			return 11;//最长字符串为-2 147 483 648
		else if(name.equals("FLOAT".toUpperCase()))
			return 39;
		else if(name.equals("DOUBLE".toUpperCase()))
			return 309;
		else if(name.equals("BIT".toUpperCase()))
		{
			int rtn=getInLength(item);
			return rtn==0?1:rtn;//最大值60k
		}
		else if(name.equals("BIGINT".toUpperCase()))
			return 20;
		
		else if(name.equals("DateTime".toUpperCase()))
			return 18;
		else if(name.equals("Date".toUpperCase()))
			return 11;
		else if(name.equals("TimeStamp".toUpperCase()))
			return 14;
		else if(name.equals("Time".toUpperCase()))
			return 9;
		else
		    return 0;
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
	 * 将配置对象转化成enum对象
	 * @param item
	 * @return
	 */
	private FeildType getFeildType(String item)
	{
		String name=item.split("\\(")[0].trim().toUpperCase();
		if(name.equals("VARCHAR".toUpperCase()))
			return FeildType.TVarchar;
		else if(name.equals("CHAR".toUpperCase()))
			return FeildType.TCharacter;
		else if(name.equals("TEXT".toUpperCase()))
			return FeildType.TText;
		else if(name.equals("MEDIUMINT".toUpperCase()))
			return FeildType.TMEDIUMINT;
		else if(name.equals("INT".toUpperCase()))
			return FeildType.TInt;
		else if(name.equals("FLOAT".toUpperCase()))
			return FeildType.TFloat;
		else if(name.equals("DOUBLE".toUpperCase()))
			return FeildType.TDOUBLE;
		else if(name.equals("BIT".toUpperCase()))
			return FeildType.TBIT;
		else if(name.equals("BIGINT".toUpperCase()))
			return FeildType.TBigInt;
		
		else if(name.equals("DateTime".toUpperCase()))
			return FeildType.TDateTime;
		else if(name.equals("Date".toUpperCase()))
			return FeildType.TDate;
		else if(name.equals("TimeStamp".toUpperCase()))
			return FeildType.TTimeStamp;
		else if(name.equals("Time".toUpperCase()))
			return FeildType.TTime;
		else
		    return null;
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
	
	public void setOpsGroup(String opsGroup){
		this.opsGroup=opsGroup;
	}
	public String getOpsGroup(){
		return this.opsGroup;
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


	
	
	 /** 
     * 功能:静态的自动将Java数据类型参数映射转换为SQL参数 
     * 区分:该函数是静态函数,参数直接转化成ps,不进行校对,如果发生错误会产生sql错误,根据给定参数返回prama,存在安全隐患,应该由系统调用
     * @param param Java数据类型参数数组 
     * @param ps SQL命令执行对象 
     * @return 返回自动匹配添加好SQL参数的SQl命令执行对象 
     */  
    public static PreparedStatement GetStatement(Object[] param, PreparedStatement ps) {  
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
            } catch(SQLException ex) {  
            	logger.error("Sql异常："+ex.toString(), ex);
            } catch (Exception ex) { 
            	logger.error("Data请求异常："+ex.toString(), ex);
            }  
              
        }  
          
        // 返回结果  
        return ps;  
          
    }  
	/**
	 * 功能:设置具体一项
	 * @param index
	 * @param dataFeilds
	 * @param ps
	 */
	private void setPrama(int index,DataFeild dataFeilds,PreparedStatement ps,String values){
//		switch(dataFeilds.feildType)
//		{
//		case TInt:
//			ps.setInt(index,Integer.parseInt(values));
//			break;
//		case TFloat:
//			ps.setFloat(index,Float.parseFloat(values));
//			break;
//		case TDateTime:
//			ps.setTimestamp(index,new java.sql.Timestamp(new java.text.SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(values).getTime()));
//			break;
//			break;
//		default:
//			ps.setString(index,values);
//		break;
//		}
	}
}
