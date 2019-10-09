package com.world.data.mysql;

import java.lang.reflect.Method;
import java.sql.Time;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.world.data.database.DatabaseProp;
import org.apache.log4j.Logger;

/**
 * 功能:所有基础数据类的基础bean,其他集成至这里是为了增强性能和统一管理
 * @author 凌晓
 */
public class Bean extends DatabaseProp implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -291343338597051597L;

	protected static Logger log = Logger.getLogger(Bean.class.getName());

	/**
	 * 功能：空bean
	 */
	public Bean(){
		
	}
	/**
	 * 功能：自动填写表单的bean
	 */
     public Bean(HttpServletRequest request){
    	 setFormBean(request);
	}
	private String dataName="baseBean";//对应btc.Web.Data.btc.BaseConfigBean的name,所有继承的项输出.
	/**
	 * 功能:获取数据名称
	 * @return 数据名称
	 */
	public String getDataName(){
		return this.dataName;
	}
 
	
	private HttpServletRequest formBean;
	public HttpServletRequest getFormBean(){
		return formBean;
	}
	
	/**
	 * 功能:设置数据名称
	 * @param dataName 数据名称
	 */
	public void setDataName(String dataName){
		this.dataName=dataName;
	}
	
	/**
	 * 功能:通过继承的方法实现bean的这一功能
	 * @param request
	 * @param bean
	 */
	public  void setFormBean(HttpServletRequest request) {
		Object bean=this;
	    Class c = bean.getClass();
	    Method[] ms = c.getMethods();
	    for(int i=0; i<ms.length; i++) {
	        String name = ms[i].getName();
	        if(name.startsWith("set")) {
	          Class[] cc = ms[i].getParameterTypes();
	          if(cc.length==1) {
	            String type = cc[0].getName(); // parameter type
	            try {
	                // get property name:
	                String prop = Character.toLowerCase(name.charAt(3)) + name.substring(4);
	                // get parameter value:
	                String param =request.getParameter(prop);
	                if(param!=null && !param.equals("")) {
	                	param=param.trim();
	                  //ms.setAccessible(true);
	                  if(type.equals("java.lang.String")) {
	                    ms[i].invoke(bean, new Object[] {param});
	                  }
	                  else if(type.equals("int") || type.equals("java.lang.Integer")) {
	                    ms[i].invoke(bean, new Object[] {new Integer(param)});
	                  }
	                  else if(type.equals("long") || type.equals("java.lang.Long")) {
	                    ms[i].invoke(bean, new Object[] {new Long(param)});
	                  }
	                  else if(type.equals("boolean") || type.equals("java.lang.Boolean")) {
	                	  
	                	  if(param.equals("1"))
	                	  {
	                         ms[i].invoke(bean, new Object[] { true });
	                       //  log.info("类名称1"+type+":"+param);
	                	  }
	                      else
	                		  ms[i].invoke(bean, new Object[] { false });
	                  }
	                  else if(type.equals("float") || type.equals("java.lang.Float")) {
		                    ms[i].invoke(bean, new Object[] { Float.valueOf(param) });
		                  }
	                  else if(type.equals("double") || type.equals("java.lang.Double")) {
		                    ms[i].invoke(bean, new Object[] { Double.valueOf(param) });
		                  }
	                  else if(type.equals("java.sql.Date")) {
	                	  Date date=new java.sql.Date(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(param).getTime());
	                    if(date!=null)
	                        ms[i].invoke(bean, new Object[] {date});
	                  }
	                  else if(type.equals("java.sql.Timestamp")) {
	                	  Date date=new java.sql.Timestamp(new java.text.SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(param).getTime());
	                    if(date!=null)
	                        ms[i].invoke(bean, new Object[] {date});
	                  }
	                  else if(type.equals("java.sql.Time")) {
	                	  //Date date=new java.sql.Timestamp(new java.text.SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(param).getTime());
	                    Time time=new java.sql.Time(new java.text.SimpleDateFormat("kk:mm:ss").parse(param).getTime());
	                	  if(time!=null)
	                        ms[i].invoke(bean, new Object[] {time});
	                  }
	                  else
	                  {
	                	 // log.info("类名称"+type);
	                	  ms[i].invoke(bean, new Object[] { String.valueOf(param) });
	                  }
	                }
	            }
	            catch(Exception e) {
	                log.error(e.toString(), e);
	            }
	          }
	        }
	    }
	  }
}
