package com.world.model.dao.account;


import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.world.config.GlobalConfig;
import com.world.data.mysql.Data;
import com.world.model.dao.admin.role.AdminPermissionsDao;
import com.world.model.dao.admin.role.AdminRoleAuthorityDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.AdminPermissions;
import com.world.model.entity.admin.AdminRoleAuthority;
import com.world.model.entity.admin.AdminUser;
import com.world.util.date.TimeUtil;
import com.world.util.string.MD5;

public class AdminLogin {
   
    static Logger log =Logger.getLogger(AdminLogin.class);
    
    AdminUserDao auDao = new AdminUserDao();
    AdminRoleAuthorityDao araDao = new AdminRoleAuthorityDao();
    AdminPermissionsDao apDao = new AdminPermissionsDao();
    
	//获取客户端的真实ip地址
	   public static String getRemortIP(HttpServletRequest request) {
		   if (request.getHeader("x-forwarded-for") == null) {
		   return request.getRemoteAddr();
		   }
		   return request.getHeader("x-forwarded-for");
	  } 
	   /**
	    * 返回一个指定长度的随机字符串
	    * @param length
	    * @return
	    */
	  public static String GetRadomStr(){
		  String[]   str={ "a", "B", "c", "D", "e", "f", "G", "h", "i", "J", "k", "L", "m", "n", "o", "P", "q","r", "s", "T", "u", "v", "w", "X", "y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "!", "@", "#", "$", "%"};
		  Random   r=new   Random();
		  int length=r.nextInt(6);//先取出一个长度不固定的,小于6的,太长了也没用
		  if(length<=0)
			  length=1;
		  String ls="";
		  for(int j=0;j<length;j++){
		//  for(int   i=0;i <str.length;i++){
		      int   a=r.nextInt(str.length);
		      ls+=str[a];
		      // log.info(str[a]);
	   //   }
		  }
		  return ls;
	 }
	  public static void DoWrite(HttpServletResponse response,String message){
		  try{
			  response.getWriter().write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ROOT><HEAD><title>登录失败</title></HEAD><BODY><State>false</State><Des>登录失败</Des><MainData>"+message+"</MainData></BODY></ROOT>");
		  }catch(Exception ex){
					
				}
		  }

	   /**
	    * 功能:进行真正的登录,注意,如果现实视图被转向到jsp的其他需要二次执行的视图,本功能将不正正确应用,因为cookie无法正确
	    *      被输出就截断转换了.
	    * @param userName 用户名
	    * @param passWord 密码
	    * @param keepMnute 保持状态的分钟数
	    * @param safeLogin 是否需要安全登录,安全登录会保持ip规则进cookie里面,所以即使cookie被盗也不会发生问题
	    *                   以后这个变量还可以用来强制下线.
	    */
	   public  Boolean DoLogin(String userName,String passWord,HttpServletRequest request,HttpServletResponse response,String ip)
	   {
		   int keepMinute=60*60*24*1;//默认一天 
		   log.info("收到验证登录的请求:" + userName);
		  try{
		    if(userName.length()<2||passWord.length()<2){
		    	log.error("收到一个过短的用户名或者密码登录请求,登录失败!");
		        return false;
		    }
		    passWord=MD5.toMD5(userName+passWord);
		    
		  //获取第一条数据的list格式返回
		  //Object user = Data.GetOne("select a.AdmId,a.AdmName,a.AdmUName,a.AdmRoleId,a.AdmDes,r.RoleName,a.isLocked,a.AdmPassword,a.LastLoginTime,a.LoginTime,r.RoleName from Admin_User a,Admin_Role r where a.AdmName=? and a.AdmRoleId=r.RoleId", new Object[]{userName});
		
		  AdminUser user = auDao.getByField("admName", userName);
		  //and a.AdmPassword=?
		  if(user!=null) {
			   String userId = user.getAdmId();
			   if(!user.getAdmPassword().equals(passWord))
			   {//如果密码不正确
				   int errorTime = user.getLoginTime();
				   if(errorTime<5){
					   //错误次数不到5次,更新一次然后返回
					   DoWrite(response,"登陆失败,您还有"+(5-errorTime)+"次机会,之后账户就会被锁定1个小时.");
					   auDao.update(auDao.getQuery().filter("_id =", userId), auDao.getUpdateOperations().inc("loginTime").set("lastLoginTime", TimeUtil.getNow()));
					   return false;
				   } else {
					   if(user.getIsLocked() == 0){   
						   //还没被锁定,主动锁一下
						   DoWrite(response,"超过登陆次数限定,您的账户已经被锁定.");
						   auDao.update(auDao.getQuery().filter("_id =", userId), auDao.getUpdateOperations().set("isLocked" , 1).set("lastLoginTime", TimeUtil.getNow()));
						   
//						   Data.Update("Update Admin_User SET isLocked=1,LastLoginTime=? WHERE AdmId=?",new Object[]{new java.sql.Timestamp(System.currentTimeMillis()),userId});
						   return false;
					   } 
					   else
					   {
						   //已经被锁定了,并且密码错误
						   //仅仅是密码不正确
						   long nowTime=System.currentTimeMillis();
					
						   //log.info("上次登陆时间"+(nowTime-da.getTime())/60000);
						    if((nowTime - user.getLastLoginTime().getTime())/60000>60)
						    {
						    	log.info("过期解锁" + (nowTime - user.getLastLoginTime().getTime()) / 60000);
						    	 //超过1个小时进行解锁
						    	
						    	 auDao.update(auDao.getQuery().filter("_id =", userId), auDao.getUpdateOperations().set("loginTime", 0).set("isLocked" , 0).set("lastLoginTime", TimeUtil.getNow()));
//						    	 Data.Update("Update Admin_User SET LoginTime=0,isLocked=0,LastLoginTime=?  WHERE AdmId=?",new Object[]{new java.sql.Timestamp(System.currentTimeMillis()),userId}); 
						    }//接完锁还要等
					   }
				   }
				      DoWrite(response,"登陆失败,您的账号已经被锁定,请超过1个小时再试,或者直接联系高级管理员为您解锁.");
				       return false;
			   }
			   //密码正确了
			   if(user.getIsLocked() > 0)
			   {   //但是现在还处于锁定中
				   long nowTime=System.currentTimeMillis();
				  
				   //log.info("上次登陆时间"+(nowTime-da.getTime())/60000);
				    if((nowTime-user.getLastLoginTime().getTime())/60000>10)
				    {
				    	auDao.update(auDao.getQuery().filter("_id =", userId), auDao.getUpdateOperations().set("loginTime", 0).set("isLocked" , 0).set("lastLoginTime", TimeUtil.getNow()));
				    	 //超过1个小时进行解锁
//				    	 Data.Update("Update Admin_User SET LoginTime=0,isLocked=0,LastLoginTime=?  WHERE AdmId=?",new Object[]{new java.sql.Timestamp(System.currentTimeMillis()),userId}); 
				    }
				    else
				    {
				    	   DoWrite(response,"登陆失败,您的账号已经被锁定,请超过1个小时再试,或者直接联系高级管理员为您解锁.");
						   return false;
				    }
				 //  log.error("后台用户用户被锁定了");
			   }
			   else
			   {
				   auDao.update(auDao.getQuery().filter("_id =", userId), auDao.getUpdateOperations().set("loginTime", 0).set("lastLoginTime", TimeUtil.getNow()));
//				   Data.Update("Update Admin_User SET LoginTime=0,LastLoginTime=?  WHERE AdmId=?",new Object[]{new java.sql.Timestamp(System.currentTimeMillis()),userId}); 
					  
			   }
			 //  String loginStatus="True";//明文
			   //String ip=getRemortIP(request);
			   long nowTime = System.currentTimeMillis();
			  
			   auDao.update(auDao.getQuery().filter("_id =", userId), auDao.getUpdateOperations().set("lastLoginIp", ip).set("lastLoginTime", TimeUtil.getNow()));
//			   int count=Data.Update("Update Admin_User set LastLoginTime=?,LastLoginIp=?  WHERE AdmId=?",new Object[]{ts,ip,userId});
			   //设置二级域名

			
			  // response.addCookie(css);
			   log.info("用户'" + user.getAdmName() + "'登陆成功");
			   return true;
		   }else{
			   DoWrite(response,"登陆失败,请仔细检查您输入的用户名或密码是否正确.");
			   return false;
		   }
		  }catch(Exception ex)
		  {
			  log.error(ex.toString(), ex);
			  return false;
		  }
	   }

	   /**
	    * 功能:检查当前请求的用户是否处于正常的登录状态,还是模拟登录
	    * @param request 当前请求
	    * @return
	    */
	   public static Boolean Check(HttpServletRequest request){
		  return false;
	 }
	  /**
	   * 功能:深度验证用户当前页面权限
	   * @param path     路径
	   * @param userId   用户id
	   * @param roleId   角色id
	   * @return 如果验证通过将返回null;否则返回相应的字符串
	   */ 
	   public  PreBean DeepCheck(String path,int userId,int roleId){
		   //先查找出这个页面的权限设置AutId,IsViewer,IsTopViewer,needPreUpdate,OperationType,defaultLoginPer
//		   Object liO=Data.GetOne("SELECT AutId,IsTopViewer,OperationType,defaultLoginPer FROM admin_role_authority WHERE AutUrl=?",new Object[]{path});
		   
		   AdminRoleAuthority ara = araDao.getByField("autUrl", path);
		   if(ara != null){
			   //用户权限
			  
			   PreBean pb=new PreBean(); 
			   //功能id
			   String authId = ara.getAutId();  
//			   Object obj=li.get(1);
//			   //是否需要跟版块挂钩的
 			   boolean needBord = ara.isViewer();//Netpet.Web.Tools.SystemFunction.ToBool(li.get(1).toString());

			   //先获取用户和组的权限,这里是将用户和组所属的权限全部选择出来PermiId, authorityId,PermiUserId,PermiType,TypeId,OperationType
			   
// 			   List list=Data.Query("SELECT PermiId, authorityId,PermiUserId,TypeId	FROM admin_permissions  WHERE (PermiUserId="+roleId+") AND (authorityId=?)", new Object[]{authId});  
			  
 			  List<AdminPermissions> list = apDao.getQuery().filter("permiUserId", roleId).filter("authorityId", authId).asList();
 			   
			  Integer[] userBorld=null;
			   //支持的浏览方式 默认登录用户权限，-2 所有人可用(这个暂时不考虑使用,暂不开放),-1 登录后台可用 0 需设置才可用
			   int defaultLoginPer = ara.getDefaultLoginPer();  
			   Integer[] num;
			   if(defaultLoginPer==-1)//说明登陆就可以用了,那么就不限定
			   {
				   num=new Integer[1];//这里用两个变量来就可以实现动态定义了.
				   num[0]=Integer.parseInt("-1");//所有
				   pb.SetPri(num);
				   //这里直接返回就好了,这里有个问题,对于那些需要直接访问,但是有按钮功能限制的页面无能为力了
				  return pb;//如果直接可以访问的那么当然内部元素都不控制
			   }
			   else
			   {   //默认为需要设置才能访问  
				  //对于主版块的设置 
				  if(list.size()>0){
					   //说明有权限了,可以使用了
				  if(needBord){
					  AdminUser liu = auDao.getById(userId);//Data.GetOne("SELECT BaseBorld FROM admin_User WHERE AdmId=?",new Object[]{userId});
					  if(liu!=null)
					  {//如果用户板块设置过权限，那么读取进来
						  String ids=liu.getBaseBorld();
						  if(ids.trim().length()>0){
							  String[] iids=ids.split(",");
							  userBorld=new Integer[iids.length];
							  for(int u=0;u<iids.length;u++){
								  userBorld[u]=Integer.parseInt(iids[u]);
							  }
						  }
					  }
					   int a=list.size();//需要多少行
					   if(userBorld!=null)
					      num=new Integer[a+userBorld.length];//这里用两个变量来就可以实现动态定义了.
					   else
						   num=new Integer[a];//这里用两个变量来就可以实现动态定义了.
					   for(int i=0;i<list.size();i++){
						   //循环赋值
						   num[i]=0;//Integer.parseInt(((List)list.get(i)).get(3).toString());//这里实现动态赋值了
					   }
					   if(userBorld!=null){//用户板块也添加进入
						   for(int i=0;i<userBorld.length;i++){
							   //循环赋值
							   num[i+list.size()]=userBorld[i];//这里实现动态赋值了
						   }
					   }
					   //返回这个权限列
					   pb.SetPri(num);
					 }else
					 {
						 num=new Integer[1];//因为存在权限了,那么就肯定是可以的了
						 num[0]=Integer.parseInt("-1");
						 pb.SetPri(num);
						 //返回一个一行的二维数组
					  }
				   return pb;
				   }
				   else
				   {
					   //说明用户不具备这个页面的使用权
					   log.info("不具备当前页面的权限,直接返回");
					   return null;
				   }
			   }
		   }
		   return null;//说明没有权限访问,直接返回
	   }
	   
}
