package com;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.world.config.GlobalConfig;
import com.world.model.dao.lan.LanStoreDao;
import com.world.model.entity.lan.LanStore;
import com.world.util.chinese.ZHConverter;
import com.world.util.path.PathUtil;
import com.world.web.language.VIpResourceBundle;

/**
 * 语言环境 
 * @author pc
 * 
 */
public class Lan { 
	private static Logger log=Logger.getLogger(Lan.class); 
	private static Locale locale = new Locale("zh", "CN","UTF-8"); 
	public static VIpResourceBundle messageCn =LoadLang("cn"); 
	public static VIpResourceBundle messageEn =LoadLang("en"); 
	public static VIpResourceBundle messageTw =LoadLang("tw");
	public static VIpResourceBundle messageJp =LoadLang("jp");
	public static VIpResourceBundle messageKr =LoadLang("kr");
	public static VIpResourceBundle messageHk;
	public static VIpResourceBundle messageJr;
	public static VIpResourceBundle messageDe;
	public static VIpResourceBundle messageFr;
	/**
     * 根据字符串语言类名称返回字符串
     * @param l 语言简称，比如 en cn
     * @return 语言缓存
     */
    public static VIpResourceBundle  getLanguage(String l){
    	if(l == null){
    		l = "cn";
    	}
   	    if(l.equals("en"))
  		   return messageEn;
    	else if(l.equals("cn"))
  		   return messageCn;
  	    else if(l.equals("hk"))
  		   return messageTw;
  	    else if(l.equals("Jr"))
  		   return messageJr;
  	    else if(l.equals("de"))
  		   return messageDe;
  	    else if(l.equals("fr"))
  		   return messageFr;
  	    else if(l.equals("tw"))
  	       return messageTw;
		else if(l.equals("jp"))
			return messageJp;
		else if(l.equals("kr"))
			return messageKr;
  	    else
  		   return messageEn;
 
   }
	
    //重新加载一个语言
	public static void setLanguageResource(String l){//这里不能赋值，不能引用比如:getLanguage(l)=LoadLang(l),所以重复
		if(l.equals("en"))
	  		   messageEn=LoadLang(l);
	    	else if(l.equals("cn"))
	  		   messageCn=LoadLang(l);
	  	    else if(l.equals("hk"))
	  		   messageHk=LoadLang(l);
	  	    else if(l.equals("Jr"))
	  		   messageJr=LoadLang(l);
	  	    else if(l.equals("de"))
	  		   messageDe=LoadLang(l);
	  	    else if(l.equals("fr"))
	  		   messageFr=LoadLang(l);
	  	    else if(l.equals("tw"))
	  	       messageTw=LoadLang(l);
			else if(l.equals("jp"))
				messageTw=LoadLang(l);
			else if(l.equals("kr"))
				messageTw=LoadLang(l);
	  	    else
	  		   messageEn=LoadLang(l);
		

	}
	
	public static Hashtable fileLanguage=new Hashtable();
	private static Properties prop=new Properties();         
	public static VIpResourceBundle LoadLang(String name){
		InputStreamReader stream = null;
		try {
			log.info("当前加载语言包" + name);
			
			PathUtil util = new PathUtil();
			//String csPath = util.getWebClassesPath();
			stream = util.getConfigFileInputStreamReader("/L_" + name + ".txt");
			//new InputStreamReader(new FileInputStream(csPath + "L_" + name + ".txt"), "UTF-8");
			VIpResourceBundle Prrb = new VIpResourceBundle(stream);
			
//			try {
//				Map<String, Object> map = Prrb.getLookup();
//				if(map != null){
//					List<LanStore> lists = new LanStoreDao().all();
//					for (LanStore lan : lists) {
//						map.put(lan.getKey(), lan.getValue(name));
//					}
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				//log.error(e.toString(), e);
//			}
			
			return Prrb;
		 }catch(Exception ex){
			 log.error("文件：" + "/L_" + name + ".txt" + "不存在。", ex);
			 return null;
		 }finally{
			 if(stream != null){
				 try {
					stream.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			 }
		 }
	}
	/**
	 * 这是加载@的外部引用键值时候用到的
	 * @param lan 语言
	 * @param filename 文件名
	 * @return 没有经过格式化的字符串，如果需要会被缓存
	 */
	public static String GetFileString(String lan,String filename){
		PathUtil util = new PathUtil();
		try{
			if(!GlobalConfig.debugModel&&fileLanguage.containsKey(lan+filename))
				return fileLanguage.get(lan+filename).toString();
			else{
		         String rtn=util.FileToString(util.getWebRoot()+"temp/"+lan+"/"+filename);
		         if(!GlobalConfig.debugModel)
		             fileLanguage.put(lan+filename, rtn);
		         return rtn;
			}
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			return "";
		}
		
	}
	/**
	 * 这是加载@@的外部引用键值时候用到的,跟上面不同的是，他会获取defaultLanguage目录下面的language，用来对同一个版本的不同语言进行格式化
	 * @param lan 语言
	 * @param filename 文件名
	 * @return 没有经过格式化的字符串，如果需要会被缓存
	 */
	public static String GetFileStringSpecial(String lan,String filename){
		PathUtil util = new PathUtil();
		try{
			String realPath=GlobalConfig.defaultLanguage+"/"+lan+"/"+filename;
			//log.info("realPath:"+realPath);
			if(!GlobalConfig.debugModel&&fileLanguage.containsKey(realPath))
				return fileLanguage.get(realPath).toString();
			else{
		         String rtn=util.FileToString(util.getWebRoot()+"temp/"+realPath);
		       log.info("当前加载："+util.getWebRoot()+"temp/"+realPath);
		         if(!GlobalConfig.debugModel){
		             fileLanguage.put(realPath, rtn);
		         }
		       //  log.info(rtn);
		         return rtn;
			}
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			return "";
		}
		
	}
	 /**
	  * 最基本的获取一个职，如果以@开头说明是外部引用文件
	  * @param lan 语言
	  * @param key 键值
	  * @return 一个值，如果是外部引用，也是经过转化的最终的值
	  */
    public static String Language(String lan,String key) {
    		//log.info("key:"+key + ",lan:"+lan);
    		VIpResourceBundle crb = getLanguage(lan);
    		if(crb == null) {
    			setLanguageResource(lan);
    			crb = getLanguage(lan);
    		}
    		try{
    			String rtn=crb.getVString(key);
    			
    			if(lan != null && lan.equals("hk")){
    				rtn = ZHConverter.convert(rtn, ZHConverter.TRADITIONAL);
    			}
    			
    			if(rtn.indexOf("@@")==0){
    				//加载文件
    				//log.info("加载："+lan+":"+rtn);
    				return GetFileStringSpecial(lan,rtn.substring(2));
    			}
    			if(rtn.indexOf("@")==0){
    				//加载文件
    				return GetFileString(lan,rtn.substring(1));
    			}
    			else
    				return rtn;
    		}catch(Exception ex){
    			log.error(ex.toString(), ex);
    			return key;
    		}
    }
    public static String format(String f,Object[] param){
    	if(null != param){
			for(int i=0;i<param.length;i++){
				f=f.replaceFirst("%%", param[i].toString());
				f=f.replace("%"+(i+1)+"%", param[i].toString());//替换所有制定位置的参数
			}
		}
    	return f;

    }
    // 获取省份 
    public static String LanguageFormat(){
    	return null;
    }

    public static String LanguageFormat(String lan,String key,String[] param){
    	String rtn=Language(lan,key);
    
    	return format(rtn, param);
    }

    public static String LanguageFormat(String lan,String key,String p1){
    	return LanguageFormat(lan,key, new String[]{p1});
    }

    public static String LanguageFormat(String lan,String key,String p1,String p2){
    	return LanguageFormat(lan,key, new String[]{p1,p2});
    }
 
    public static String LanguageFormat(String lan,String key,String p1,String p2,String p3){
    	return LanguageFormat(lan,key, new String[]{p1,p2,p3});
    }
    public static String LanguageFormat(String lan,String key,String p1,String p2,String p3,String p4){
    	return LanguageFormat(lan,key, new String[]{p1,p2,p3,p4});
    }
    public static String LanguageFormat(String lan,String key,String p1,String p2,String p3,String p4,String p5){
    	return LanguageFormat(lan,key, new String[]{p1,p2,p3,p4,p5});
    }
    public static String LanguageFormat(String lan,String key,String p1,String p2,String p3,String p4,String p5,String p6){
    	return LanguageFormat(lan,key, new String[]{p1,p2,p3,p4,p5,p6});
    }
    public static String LanguageFormat(String lan,String key,String p1,String p2,String p3,String p4,String p5,String p6,String p7){
    	return LanguageFormat(lan,key, new String[]{p1,p2,p3,p4,p5,p6,p7});
    }
    public static String LanguageFormat(String lan,String key,String p1,String p2,String p3,String p4,String p5,String p6,String p7,String p8){
    	return LanguageFormat(lan,key, new String[]{p1,p2,p3,p4,p5,p6,p7,p8});
    }
    public static String LanguageFormat(String lan,String key,String p1,String p2,String p3,String p4,String p5,String p6,String p7,String p8,String p9){
    	return LanguageFormat(lan,key, new String[]{p1,p2,p3,p4,p5,p6,p7,p8,p9});
    }

    

	 static public void main(String[] args) {  
		 try{
	      //  log.info(l("xin"));
		 }catch(Exception ex){log.error(ex.toString(), ex);}
	      
	     
	 }
}
