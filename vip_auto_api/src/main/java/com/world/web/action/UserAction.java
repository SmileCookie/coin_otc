package com.world.web.action;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.api.VipResponse;
import com.api.user.UserManager;
import com.world.common.SystemCode;
import com.world.lang.exception.NoUserLogException;
import com.world.model.entity.user.UserCommon;
import com.world.web.UrlViewCode;

import net.sf.json.JSONObject;

/****
*   
* 项目名称：jua  
* 类名称：UserAction  
* 类描述：  
* 创建人：yangchunhe  
* 创建时间：2012-5-15 下午08:39:21  
* 修改人：yangchunhe  
* 修改时间：2012-5-15 下午08:39:21  
* 修改备注：  
* @version   
*
*/
public class UserAction extends BaseAction implements UserCommon{
	
	//------------------app的接口版本判断-------------- start
	private String[] versions = new String[]{"api", "api11"};	//可以使用的版本，根据包名
	
	/**
	 * 检查版本是否开放
	 * @return
	 */
	public boolean checkVersionOpen(){
		try {
			String packageName = this.getClass().getPackage().getName();
			String nowVersion = packageName.split("\\.")[packageName.split("\\.").length-1];
//			System.out.println("当前版本=" + nowVersion);
			
			boolean isOpen = false;
			for(String version : versions){
				if(version.equals(nowVersion) ){
					isOpen = true;
				}
			}
			
			//如果versions这个常量中，不包含当前版本，就提示版本已经关闭
			if(nowVersion.startsWith("api") && !isOpen){
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public String BaseInit(ServletContext _context, HttpServletRequest _request, HttpServletResponse _response , UrlViewCode uvc) {
		String result = super.BaseInit(_context, _request, _response, uvc);
//		System.out.println("ApiAction继承成功");
		if(	!checkVersionOpen() ){
			json(SystemCode.code_1009.getValue(), false, "{\"wrongVersion\" : true}");
			return "device-false";
		}
		return result;
	}
 
	/****
	 * 获取当前用户ID ， 无登陆时默认不返回历史页面
	 * @return
	 */
	
	protected String userId(boolean isHistory){
		return userId(isHistory , false);
	}
	
	/*****
	 * 获取当前用户ID
	 * @param isRidirect 是否返回历史页面  对于xml的请求isHistory=false
	 * @param end  是否结束方法，方法是否继续执行
	 * @param isIframe 是否处于iframe模式
	 * @return
	 */
	protected String userId(boolean isHistory , boolean end , boolean isIframe) {
		String uid = "";
//		boolean isLogin=Status.Check(request);
//		if(isLogin){

//		}
		
		String userId = String.valueOf(userId());
		if(userId==null||"0".equals(userId.trim())){
			//不作处理
		}else{
			uid = userId;
		}
		
		if(uid.length() <= 0 && end){
			if(isHistory){
				if(isIframe){//frame 模式
					System.out.println("xx:"+request.getHeader("Referer"));
					request.setAttribute("referer", request.getHeader("Referer"));
		    	    SetViewerPath("/users/loginIframe.jsp");
		    	    return "";
				}else{
					System.out.println("xx:"+request.getHeader("Referer"));
					request.setAttribute("referer", request.getHeader("Referer"));
					String urlLs=request.getRequestURI().toLowerCase();
					
					if(!urlLs.startsWith("/user/login")){
	        			 try {
							response.sendRedirect("/user/login");
						} catch (IOException e) {
							e.printStackTrace();
						}
	        		}
					
		    	    return "";
				}
			}else{
				Write(NO_LOGIN,false,"");
				ToXml();
			}
			throw new NoUserLogException(NO_LOGIN);
		}
		return uid;
	}
	
	protected String userId(boolean isHistory , boolean end) {
		return userId(isHistory, end, false);
	}
	
	//下单时判断是否启用安全密码
	protected boolean useSafePwd(String userId){
		VipResponse response = null;
		try {
			response = new UserManager().isUseSafePwd(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(response != null){
			JSONObject jo = JSONObject.fromObject(response.getMsg());
			if(jo != null){
				if(jo.getBoolean("isSuc")){
					return true;
				}else{
					Response.append(response.getMsg());
					return false;
				}
			}
		}
		Response.append("{\"des\" : \"内部错误\" , \"isSuc\" : "+false+"  , \"datas\" : {}}");
		return false;
	}
	
	/**
	 * 提交时判断安全密码
	 * @param pass
	 * @param userId
	 * @return
	 */
	protected boolean safePwd(String pass , String userId){
		VipResponse response = null;
		try {
			response = new UserManager().validateSafePwd(userId, pass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(response != null){
			JSONObject jo = JSONObject.fromObject(response.getMsg());
			if(jo != null){
				if(jo.getBoolean("isSuc")){
					return true;
				}else{
					Response.append(response.getMsg());
					return false;
				}
			}
		}
		Response.append("{\"des\" : \"内部错误\" , \"isSuc\" : "+false+"  , \"datas\" : {}}");
		return false;
	}
	
    /**
     * 输出API返回信息
     * @param errorCode 
     * @param message
     */
     public void WriteMsg(int errorCode,String des, boolean isSuc, String datas)
     {
         des = L(des);
	  	if(StringUtils.isEmpty(datas)){
	  		datas = "{}";
	  	} 
	   	StringBuffer buffer = new StringBuffer();
	   				 buffer.append("{");
	   				 buffer.append("\"code\":");
	   				 buffer.append(errorCode).append(",");
	   				 buffer.append("\"message\":");
	   				 buffer.append("{\"des\": \""+des+"\", \"isSuc\": "+String.valueOf(isSuc)+", \"datas\": "+datas+"}");
	   				 buffer.append("}");
	    Response.append(buffer.toString());
	 }
     
	 /**
      * 输出API返回信息
      * @param errorCode 
      * @param message
      */
      public void WriteMsg(int errorCode,String message)
      {
      	message = L(message);
    	StringBuffer buffer = new StringBuffer();
    				 buffer.append("{");
    				 buffer.append("\"code\":");
    				 buffer.append(errorCode).append(",");
    				 buffer.append("\"message\":");
    				 buffer.append("\"").append(message).append("\"");
    				 buffer.append("}");
      	Response.append(buffer.toString());
      }
      
      /**
       * 输出API返回信息
       * @param errorCode 
       * @param message
       */
       public void WriteMsg2(int errorCode,String message,String id)
       {
       	message = L(message);
     	StringBuffer buffer = new StringBuffer();
     				 buffer.append("{");
     				 buffer.append("\"code\":");
     				 buffer.append(errorCode).append(",");
     				 buffer.append("\"message\":");
     				 buffer.append("\"").append(L(message)).append("\",");
     				 buffer.append("\"id\":");
     				 buffer.append("\"").append(id).append("\"");
     				 buffer.append("}");
       	Response.append(buffer.toString());
       }
       
       /**
        * 输出API返回信息
        * @param errorCode 
        * @param message
        */
        public void WriteMsg3(int errorCode,String des, boolean isSuc, String datas)
        {
        	des = L(des);
     		if(StringUtils.isEmpty(datas)){
     			datas = "{}";
     		}
      		StringBuffer buffer = new StringBuffer();
		 	buffer.append("{");
		 	buffer.append("\"code\":");
		 	buffer.append(errorCode).append(",");
		 	buffer.append("\"message\":");
		 	buffer.append("{\"des\": \""+des+"\", \"isSuc\": "+String.valueOf(isSuc)+", \"datas\": "+datas+"}");
		 	buffer.append("}");
        	Response.append(buffer.toString());
        }
}
