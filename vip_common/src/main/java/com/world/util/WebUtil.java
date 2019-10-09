package com.world.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.world.util.mem.MemoryCounter;

public class WebUtil {
	static Logger log =Logger.getLogger(WebUtil.class);
    /**
     * 添加cookie
     * @param response
     * @param name cookie的名称
     * @param value cookie的值
     * @param maxAge cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0,cookie将随浏览器关闭而清除)
     * @param scope cookie的作用域
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge,String scope) { 
    	if(scope==null||"".equals(scope)){
    		scope="/";
    	}
    	try {
			value=URLEncoder.encode(value,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(scope);
        if (maxAge>0) cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
    /**
     * 获取cookie的值
     * @param request
     * @param name cookie的名称
     * @return
     */
    public static String getCookieByName(HttpServletRequest request, String name) {
    	Map<String, Cookie> cookieMap = WebUtil.readCookieMap(request);
        if(cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);
            return cookie.getValue();
        }else{
            return null;
        }
    }
    
    public static Cookie getCookie(HttpServletRequest request, String name) {
    	Map<String, Cookie> cookieMap = WebUtil.readCookieMap(request);
        if(cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);
            return cookie;
        }else{
            return null;
        }
    }
    
    protected static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (int i = 0; i < cookies.length; i++) {
                cookieMap.put(cookies[i].getName(), cookies[i]);
            }
        }
        return cookieMap;
    }
    
	  /****
   * 鑾峰彇鏄剧ずphoto
   */
  public static String getShowPhoto(String photos,int userId,int index){
  	if(photos!=null&&photos.length()>0){
  		if(index > 0){
  			return getFolder(userId)+photos.split(" ")[0].replace(".", "-"+index+".");
  		}else{
  			return getFolder(userId)+photos.split(" ")[0];
  		}
  	}else{
  		return "";
  	}
  }
   

  /****
   * 鑾峰彇鏄剧ずphoto
   */
	public static String getFolder(int userId) {
		String uid = String.valueOf(userId);
		String pre = "";
		String end = "";
		if (userId < 1000) {
			pre = uid;
			end = uid;
		} else {
			pre = uid.substring(0, 4);
			end = uid.substring(4, uid.length());
		}
		return "/img/" + end + "/";
	}
    public static String HtmltoText(String inputString) {
        String htmlStr = inputString; //含html标签的字符串
        String textStr ="";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;          
        java.util.regex.Pattern p_ba;
        java.util.regex.Matcher m_ba;
        
        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
            String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
            String patternStr = "\\s+";
            
            p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); //过滤script标签

            p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); //过滤style标签
         
            p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); //过滤html标签
//            htmlStr = htmlStr.replaceAll(" ", "&nbsp;");//替换空格

            textStr = htmlStr;

        }catch(Exception e) {
                    log.error("Html2Text: " + e.toString(), e);
        }          
        return textStr;//返回文本字符串
     }
    
    /**
     * 去除html代码
     * @param inputString 字符串
     * @param isBlank 是否过滤空格
     * @return
     */
    public static String HtmltoText(String inputString,boolean isBlank) {
        String htmlStr = inputString; //含html标签的字符串
        String textStr ="";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        //java.util.regex.Pattern p_html;
        //java.util.regex.Matcher m_html;          
        java.util.regex.Pattern p_ba;
        java.util.regex.Matcher m_ba;
        
        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
            //String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
            String patternStr = "\\s+";
            
            p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); //过滤script标签

            p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); //过滤style标签
         
            //p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
            //m_html = p_html.matcher(htmlStr);
            //htmlStr = m_html.replaceAll(""); //过滤html标签
            htmlStr = htmlStr.replaceAll(" ", "&nbsp;");//替换空格
            htmlStr = htmlStr.replaceAll("\n", "<br/>");//过滤换行
            htmlStr = htmlStr.replaceAll("\r", "");//过滤enter键
            textStr = htmlStr;
            if(isBlank){
            	p_ba = Pattern.compile(patternStr,Pattern.CASE_INSENSITIVE);
                m_ba = p_ba.matcher(htmlStr);
                htmlStr = m_ba.replaceAll(""); //过滤空格
                
            }
        }catch(Exception e) {
                    log.error("Html2Text: " + e.toString(), e);
        }          
        return textStr;//返回文本字符串
     }
    
    public static String HtmltoText2(String inputString,boolean isBlank) {
        String htmlStr = inputString; //含html标签的字符串
        String textStr ="";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;          
        java.util.regex.Pattern p_ba;
        java.util.regex.Matcher m_ba;
        
        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
            String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
            String patternStr = "\\s+";
            
            p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); //过滤script标签

            p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); //过滤style标签
         
            p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); //过滤html标签
            
            textStr = htmlStr;
            if(isBlank){
            	p_ba = Pattern.compile(patternStr,Pattern.CASE_INSENSITIVE);
                m_ba = p_ba.matcher(htmlStr);
                htmlStr = m_ba.replaceAll(""); //过滤空格
                
            }
        }catch(Exception e) {
                    log.error("Html2Text: " + e.toString(), e);
        }          
        return textStr;//返回文本字符串
     }
    
    public static String delScript(String inputString) {
        String htmlStr = inputString; //含html标签的字符串
        String textStr ="";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        
        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
            
            p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll("《script》").replaceAll(" on", " 0n"); //过滤script标签

            textStr = htmlStr;

        }catch(Exception e) { 
                    log.error("delScript: " + e.toString(), e);
        }          
        return textStr.replace("'", "‘").replace("\"", "”").replace("{", "｛").replace("}", "｝").replace("select", "se").replace("<", "").replace(">", "").replace("update", "").replace("<", "").replace(">", "").replace("delete", "").replace("___", ".").replace("__", "-");//返回文本字符串
     }
    
  //效验
    public static boolean sqlValidate(String str) {
        String str2 = str.toLowerCase();//统一转为小写
        String badStr = "'|and|exec|execute|insert|select|delete|update|count|drop|*|%|chr|mid|master|truncate|" +
                "char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|and|exec|execute|insert|create|drop|" +
                "table|from|grant|use|group_concat|column_name|" +
                "information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|*|" +
                "chr|mid|master|truncate|char|declare|or|;|-|--|+|,|like|//|/|%|#";//过滤掉的sql关键字，可以手动添加
        String[] badStrs = badStr.split("\\|");
        for (int i = 0; i < badStrs.length; i++) {
            if (str2.indexOf(badStrs[i]) >= 0) {
                return true;
            }
        }
        return false;
    }
    
    public static String ShowInTextArea(String cont){
    	cont = cont.replaceAll("&nbsp;", " ");//替换空格
    	cont = cont.replaceAll("<br/>", "\n");//过滤换行
    	return cont;
    }
    
    /*****
     * List<String> 到  数组 的装换
     * @param list
     * @return
     */
    public static String[] getStrArrayByList(List<String> list){
    	if(list!=null&&list.size()>0){
    		int length=list.size();
    		String[] res=new String[length];
    		for(int i=0;i<length;i++){
    			res[i]=list.get(i);
    		}
    		return res;
    	}else{
    		return null;
    	}
    }
    
    public static List<String> getListByArray(String[] strs){
    	List<String> arryList=new ArrayList<String>();
    	for(String s : strs){
    		arryList.add(s);
    	}
    	return arryList;
    }
    
    /*****
     * 更具分隔符和原字符串返回一个整形数组
     * @param ids  要分割的字符串
     * @param regx 分割符号
     * @return
     */
    public static Integer[] getIdsByStrIds(String ids,String regx){
    	String[] strIds=ids.split(regx);
    	Integer[] iids=new Integer[strIds.length];
    	for(int i=0;i<strIds.length;i++){
    		iids[i]=Integer.parseInt(strIds[i]);
    	}
    	return iids;
    }
    
    public static Long[] getLongIdsByStrIds(String ids,String regx){
    	String[] strIds=ids.split(regx);
    	Long[] iids=new Long[strIds.length];
    	for(int i=0;i<strIds.length;i++){
    		iids[i]=Long.parseLong(strIds[i]);
    	}
    	return iids;
    }
    
    public static String getIdStrByArray(Integer[] ids){
    	String strIds="";
    	for(int i : ids){
    		strIds+=","+i;
    	}
    	if(strIds.length()>0){
    		strIds=strIds.substring(1);
    	}
    	return strIds;
    }
   
   
    /******
     * 深层拷贝
     * @param src
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> List<T> deepcopy(List<T> src) throws IOException, ClassNotFoundException{  
 	   
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();  
   
        ObjectOutputStream out = new ObjectOutputStream(byteout);  
   
        out.writeObject(src);  
   
      
   
        ByteArrayInputStream bytein = new ByteArrayInputStream(byteout.toByteArray());  
   
        ObjectInputStream in =new ObjectInputStream(bytein);  
   
        List<T> dest = (List<T>)in.readObject();  
   
        return dest;  
   
    }  
    
    public static Object deepCopyObj(Object o) {  
  	   
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();  
        Object dest = null;
        ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(byteout);
			out.writeObject(o);  
			   
	        ByteArrayInputStream bytein = new ByteArrayInputStream(byteout.toByteArray());  
	   
	        ObjectInputStream in =new ObjectInputStream(bytein);  
	   
	        dest = (Object)in.readObject(); 
		} catch (IOException e) {
			log.error(e.toString(), e);
		} catch (ClassNotFoundException e) {
			log.error(e.toString(), e);
		}  
   
        return dest;  
   
    }
    

	
	/****
	 * 返回一个指定范围内的随机整数  返回数值范围 1 - range  [可等于1 或 range]
	 * @param range 随机范围
	 * @return 
	 */
	public static int getOneRandom(int range){
		double a = Math.random()*range;
		a = Math.ceil(a);//返回最小的double值 该值大于或等于参数，并且等于某个整数
		int randomNum = new Double(a).intValue();
		return randomNum;
	}
	/****
	 * 保留两位有效数字
	 */
	public static BigDecimal saveTow(BigDecimal money){
		money = money.setScale(2, RoundingMode.DOWN);
	   	return money;
	}
	/****
	 * 保留两位有效数字
	 */
	public static double saveTow(double money){
		BigDecimal m = BigDecimal.valueOf(money);
		m = m.setScale(2, RoundingMode.DOWN);
	   	return m.doubleValue();
	}
	
	public static String showTow(BigDecimal money){
		return money.setScale(2, RoundingMode.DOWN).toPlainString();
	}
	
	public static String saveTowShow(BigDecimal money){
		money = money.setScale(2, RoundingMode.DOWN);
		return money.toPlainString();
	}
	
	
	public static BigDecimal saveFour(BigDecimal money){
		money = money.setScale(4, RoundingMode.DOWN);
	   	return money;
	}
	public static double saveFour(double money){
		BigDecimal m = BigDecimal.valueOf(money);
		m = m.setScale(4, RoundingMode.DOWN);
	   	return m.doubleValue();
	}
	public static String showFour(BigDecimal money){
		return money.setScale(4, RoundingMode.DOWN).toPlainString();
	}
	public static String saveFourShow(BigDecimal money){
		money = money.setScale(4, RoundingMode.DOWN);
		return money.toPlainString();
	}
	public static String saveFourShow(double money){
		BigDecimal m = BigDecimal.valueOf(money);
		m = m.setScale(4, RoundingMode.DOWN);
		return m.toPlainString();
	}
	
	
	/*****
	 * 获取收取手续费的数据
	 * @param money
	 * @return
	 * 
	 * new  java.math.BigDecimal(abc).setScale(2,java.math.BigDecimal.ROUND_HALF_UP).doubleValue());  
	 */
	public static BigDecimal getAfterFees(BigDecimal money , BigDecimal fee){
		 money=money.multiply(BigDecimal.ONE.subtract(fee)) ;
	   	 return saveTow(money);
	}

	/****
	 * 流-字符串
	 * @param is
	 * @return
	 */
	public static String inputStream2String(InputStream is){
		   BufferedReader in = new BufferedReader(new InputStreamReader(is));
		   StringBuffer buffer = new StringBuffer();
		   String line = "";
		   try {
			while ((line = in.readLine()) != null){
			     buffer.append(line).append("\n");
		   }
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
		return buffer.toString();
   }
	
	//获取ip的方法
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP"); // 获取代理ip
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr(); // 获取真实ip
		}

		if(ip.indexOf(",") > 0){
			for(int i = 0; i < ip.split(",").length; i ++){
				ip = ip.split(",")[i];
				if(!"".equals(ip) && !"unknown".equalsIgnoreCase(ip))
					break;
			}
		}
		
		return ip;
	}
	
	public static String getShortIp(String ip) {
		String shortIp = "";
		String[] ips = ip.split("[.]");
		if(ips.length==4){
			//ips[1]="*";
			ips[2]="*";
			shortIp=ips[0]+"."+ips[1]+"."+ips[2]+"."+ips[3];
		}else{
			shortIp=ip;
		}
		return shortIp;
	}
	
	public static String transHtmByXml(String htm){
		if(!htm.contains("<![CDATA[")){
			return "<![CDATA["+htm+"]]>";
		}else{
			return htm;
		}
	}
	
	private static final MemoryCounter mc = new MemoryCounter();
	/**
	 * 计算一个对象所占字节数
	 * 
	 * @param o对象
	 *            ，该对象必须继承Serializable接口即可序列化
	 * @return
	 * @throws IOException
	 */
	public static long objectSize(final Object o) {
		if (o == null) {
			return 0;
		}
		return mc.estimate(o);
	}
	
	public static int getPages(int total, int pageSize) {
		int pages = 0;
		if(total%pageSize==0){
			pages=total/pageSize;
		}else{
			pages=total/pageSize+1;
		}
		return pages;
	}
	
	/**
	 * 委托成功返回结果_+单号   取结果时分隔0 
	 * @param rtn
	 * @return
	 */
	public static String splitEntrust(String rtn){
		if(rtn != null){
			if(rtn.indexOf("_") > 0){
				rtn = rtn.split("_")[0];
			}
		}else{
			rtn = "";
		}
		return rtn;
	}
	
	public static boolean isSameDomain(HttpServletRequest request){
		String referer = request.getHeader("referer");
		if(referer.startsWith("https")){
			referer = referer.replaceAll("https://(.+?)/.*", "$1");
		}else{
			referer = referer.replaceAll("http://(.+?)/.*", "$1");
		}
		return  referer.equals(request.getServerName());
	}
	

	/****
	 * 去除url中的某些参数
	 * @param url
	 * @param params
	 * @return
	 */
	public static String removeParams(String url, String... params) {
	    String reg = null;
	    StringBuffer ps = new StringBuffer();
	    ps.append("(");
		for(int i = 0; i < params.length; i++) {
			ps.append(params[i]).append("|");
		}
		ps.deleteCharAt(ps.length() - 1);
		ps.append(")");
		reg = "(?<=[\\?&])" + ps.toString() + "=[^&]*&?";
	    url = url.replaceAll(reg, "");
	    url = url.replaceAll("(\\?|&+)$", "");
	    return url;
	}

	/***
	 * 掐头去尾*显示
	 * @param hideStr
	 * @return
	 */
	public static String hideHTShow(String hideStr){
		int length = hideStr.length();
		if(length ==2){
			String first = hideStr.substring(0 , 1);
			return first + "*";
		}else if(length > 2){
			String first = hideStr.substring(0 , 1);
			String last = hideStr.substring(length - 1 ,length);
			return first + "**" + last;
		}else{
			return hideStr + "*";
		}
	}
	
	/***
	 * 掐头去尾*显示   全位数显示
	 * @param hideStr
	 * @return
	 */
	public static String hideHTShowAll(String hideStr){
		int length = hideStr.length();
		if(length ==2){
			String first = hideStr.substring(0 , 1);
			return first + "*";
		}else if(length > 2){
			String first = hideStr.substring(0 , 1);
			String last = hideStr.substring(length - 1 ,length);
			String xing = "";
			for(int i=0;i<(length-2); i++){
				xing += "*";
			}
			return first + xing + last;
		}else{
			return hideStr + "*";
		}
	}
	
	/***
	 * 去尾*显示
	 * @param hideStr
	 * @return
	 */
	public static String hideTShow(String hideStr){
		int length = hideStr.length();
		if(length >= 1){
			String first = hideStr.substring(0 , 1);
			return first + "**";
		}else{
			return "**";
		}
	}
	

}
