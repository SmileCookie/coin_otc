package com.world.web.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.api.VipResponse;
import com.api.user.UserManager;
import com.world.lang.exception.NoUserLogException;
import com.world.model.entity.user.UserCommon;
import com.world.web.sso.SessionUser;

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
					log.info("xx:"+request.getHeader("Referer"));
					request.setAttribute("referer", request.getHeader("Referer"));
		    	    SetViewerPath("/users/loginIframe.jsp");
		    	    return "";
				}else{
					log.info("xx:"+request.getHeader("Referer"));
					request.setAttribute("referer", request.getHeader("Referer"));
					String urlLs=request.getRequestURI().toLowerCase();
					
					if(!urlLs.startsWith("/user/login")){
	        			 try {
							response.sendRedirect("/user/login");
						} catch (IOException e) {
							log.error(e.toString(), e);
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
	protected boolean isUserSafe(String userId){
		VipResponse response = null;
		try {
			response = UserManager.getInstance().isUseSafePwd(userId);
			JSONObject json = JSONObject.fromObject(response.getMsg());
			if(response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					return true;
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return false;
	}
	//下单时判断是否启用安全密码
	protected boolean useSafePwd(String userId){
		VipResponse response = null;
		try {
			response = UserManager.getInstance().isUseSafePwd(userId);
			JSONObject json = JSONObject.fromObject(response.getMsg());
			if(response.taskIsFinish()){
				if(json.getBoolean("isSuc")){
					json(JSONObject.fromObject(json.getString("datas")).getString("useSafePwd").toString(), true, "");
					//Response.append("{\"des\" : \""+JSONObject.fromObject(json.getString("datas")).getString("useSafePwd").toString()+"\" , \"isSuc\" : "+true+"  , \"datas\" : {}}");
					return true;
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		json("内部错误", false, "",true);
		return false;
	}
	
	/**
	 * 提交时判断安全密码
	 * @param pass
	 * @param userId
	 * @return
	 */
	protected boolean safePwd(String pass , String userId, String market, int type){
		VipResponse response = null;
		try {
			Map<String , String> params = new HashMap<String , String>();
			params.put("userId", userId);
			params.put("safePwd", pass);
			params.put("market", market);
			params.put("type", String.valueOf(type));
			
			response = new UserManager().validateSafePwd(params);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		if(response != null){
			JSONObject jo = JSONObject.fromObject(response.getMsg());
			if(jo != null){
				if(jo.getBoolean("isSuc")){
					return true;
				}else{
					//Response.append(response.getMsg());
					json(jo.getString("des"), false, jo.getString("datas"),true);
					return false;
				}
			}
		}
		json("内部错误", false, "",true);
		return false;
	}
	/**
	 * 提交时判断安全密码
	 * @param pass
	 * @param userId
	 * @return
	 */
	protected boolean safePwdCheck(String pass , String userId, String market, int type){
		VipResponse response = null;
		try {
			Map<String , String> params = new HashMap<String , String>();
			params.put("userId", userId);
			params.put("safePwd", pass);
			params.put("market", market);
			params.put("type", String.valueOf(type));

			response = new UserManager().verifySafePwd(params);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		if(response != null){
			JSONObject jo = JSONObject.fromObject(response.getMsg());
			if(jo != null){
				if(jo.getBoolean("isSuc")){
					return true;
				}else{
					//Response.append(response.getMsg());
					json(jo.getString("des"), false, jo.getString("data"),true);
					return false;
				}
			}
		}
		json("内部错误", false, "",true);
		return false;
	}
	
    /**
     * 输出API返回信息
     * @param errorCode 
     * @param message
     */
     public void WriteMsg(int errorCode,String des, boolean isSuc, String datas)
     {
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
     
	protected int adminId() {
		
		try {
			SessionUser user = session.getAdmin(this);
			if(user != null){
				return Integer.parseInt(user.getUid());
			}
		} catch (NumberFormatException e) {
			log.error(e.toString(), e);
		}
		return 0;
	}
}
