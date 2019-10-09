package com.world.model.dao.user;

import com.Lan;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.world.data.mongo.MongoDao;
import com.world.model.dao.account.EncryptionPhoto;
import com.world.model.entity.user.User;
import com.world.util.UserUtil;
import com.world.web.Pages;
import com.world.web.action.Action;
import com.yc.entity.SysGroups;
import com.yc.entity.msg.Msg;
import com.yc.util.MsgUtil;

import java.sql.Timestamp;
import java.util.Random;

public class EmailDao extends MongoDao<User, String> implements Action{
	
	private static final long serialVersionUID = 1L;
	UserDao userDao = new UserDao();
	
	public int sendEmail(String ip, String userId, String userName, String title, String cont, String email){
		SysGroups sg = SysGroups.vip;
		Msg m=new Msg();
		m.setSysId(sg.getId());
		m.setSendIp(ip);
		m.setUserId(userId);
		m.setUserName(userName);
//		m.setTitle(L(sg.getValue()) + " " + L(title));
		m.setTitle(title);
		m.setCont(cont);
		m.setReceiveEmail(email);
		m.setSendUserName("admin");
		return MsgUtil.sendEmail(m);
	}

	//注册时发送的邮件内容
	public String getRegEmailHtml(String userId, String language, String email, String emailCode,Pages p){
		p.lan = language;//未设置语言的选择当前页码语言
		p.setAttr("emailCode", emailCode);
		p.setAttr("lan", p.lan);
		p.setAttr("email", email);
		p.setAttr("userId", userId);
		return p.newJsp("/cn/templet/email_reg.jsp");
	}

	//忘记密码时发送的邮件内容
	public String getForgetRegEmailHtml(String userId, String language, String email, String emailCode,Pages p){
		p.lan = language;//未设置语言的选择当前页码语言
		p.setAttr("emailCode", emailCode);
		p.setAttr("lan", p.lan);
		p.setAttr("email", email);
		p.setAttr("userId", userId);
		return p.newJsp("/cn/templet/email_forget_pw.jsp");
	}

	//身份认证时发送的邮件内容
	public String getAuthFailEmailHtml(User user, String language, String email, Pages p,String reason){
		p.lan = language;//未设置语言的选择当前页码语言
		p.setAttr("lan", p.lan);
		p.setAttr("email", email);
		p.setAttr("user", user);
		p.setAttr("reason", reason);
		p.setAttr("fromUrl","/authen");
		return p.newJsp("/cn/templet/email_realfail_auth.jsp");
	}


	
	//添加认证地址发送的邮件内容
	public String getAddAddrEmailHtml(User user, String language, String email, String auth, String type, Pages p){
		p.lan = language;//未设置语言的选择当前页码语言
		p.setAttr("lan", p.lan);
		p.setAttr("auth", auth);
		p.setAttr("email", email);
		p.setAttr("user", user);
		p.setAttr("type", type);
		return p.newJsp("/cn/templet/email_add_address.jsp");
	}
	
	//邮箱认证时发送的邮件内容
	public String getEmailHtml(User user, String emailCode,Pages p){
//		p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
		p.setAttr("curUser", user);
		p.setAttr("emailCode", emailCode);
		p.setAttr("lan", p.lan);
		return p.newJsp("/cn/templet/email_auth.jsp");
	}
	
	//管理员重置密码发送邮件
	public String getPwdEmailHtml(User user, String emailCode, String type, Pages p){
//		p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
		p.setAttr("curUser", user);
		p.setAttr("emailCode", emailCode);
		p.setAttr("type", type);
		p.setAttr("lan", p.lan);
		return p.newJsp("/cn/templet/email_pwd.jsp");
	}
	
	//用户接收短信验证码时同时发送邮件
	public String getCodeEmailHtml(User user, String emailCode, Pages p){
//		p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
		p.setAttr("curUser", user);
		p.setAttr("emailCode", emailCode);
		p.setAttr("lan", p.lan);
		return p.newJsp("/cn/templet/email_code.jsp");
	}

    //用户发送邮件指定具体业务
    public String getCodeEmailHtmlByInfo(User user, String emailInfo, String emailCode, Pages p){
	    //使用页面上的语言，防止由于用户切换语言导致邮件语言不一致
//        p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
        p.setAttr("curUser", user);
        p.setAttr("emailCode", emailCode);
        p.setAttr("emailInfo", emailInfo);
        p.setAttr("lan", p.lan);
        return p.newJsp("/cn/templet/email_code_info.jsp");
    }
	/*start by xwz 20181211 登录发邮件*/
	//用户登录成功后发送邮件
	public String getLoginEmailHtmlByInfo(User user, String ip, String time, Pages p){
		//未设置语言的选择当前页码语言
//		p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;
		p.setAttr("curUser", user);
		p.setAttr("ip", ip);
		p.setAttr("time", time);
		p.setAttr("lan", p.lan);
		return p.newJsp("/cn/templet/email_login.jsp");
	}
	/*end*/

    //用户发送邮件输入错误超限
    public String getWrongLimitEmailHtml(User user, String emailInfo, Pages p){
//        p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
        p.setAttr("curUser", user);
        p.setAttr("emailInfo", emailInfo);
        p.setAttr("lan", p.lan);
        return p.newJsp("/cn/templet/email_wrong_limit.jsp");
    }

	//系统发送邮件模板
	public String getSysEmailHtml(User user, String content, Pages p){
//		p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
		p.setAttr("curUser", user);
		p.setAttr("content", content);
		p.setAttr("lan", p.lan);
		return p.newJsp("/cn/templet/email_sys.jsp");
	}

	//
	public String getOutUserJudgeEmailHtml(String lan,String userName,String content){
		StringBuffer cont = new StringBuffer();
//		cont.append("<div style='padding: 0px 20px; height: 50px; text-align: right; line-height: 40px; overflow: hidden;'></div>");
		cont.append("<div style='padding: 2px 20px 30px;'>");
		cont.append("	<p>" + Lan.LanguageFormat(lan,"%%,您好！",userName) + "<br/></p>");
		cont.append("	<p>" + content + "</p>");
		cont.append("	<p><br/><font color='#CCCCCC'>" + Lan.Language(lan,"此为自动发送邮件，请勿直接回复！").concat(Lan.Language(lan,"如您有任何疑问，请发送邮件到XXXXX。")));
		cont.append("	</font></p>");
		cont.append("</div>");
		return cont.toString();
	}

	//找回资金密码的处理方法
	public String findSafePwd(User user , Pages p){
		setLan(user.getLanguage());
		long nowTime = System.currentTimeMillis();
		Timestamp nowDate = new Timestamp(nowTime);
		String miYao = EncryptionPhoto.encrypt(Long.toString(nowTime));
		log.info(VIP_DOMAIN+"/ac/safepwd_reset?userId="+user.getId()+"&code="+miYao+"&needlogin=true");
		try {
			Datastore ds = super.getDatastore();
			Query<User> q = ds.find(User.class, "_id", user.getId());  
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("userContact.safePwdMiYao", miYao);
			ops.set("userContact.lastMiYaoTime", nowDate);
			super.update(q, ops);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		p.setAttr("curUser", user);
		p.setAttr("miYao", miYao);
		p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
		p.setAttr("lan", p.lan);
		
		return p.newJsp("/cn/templet/find_safe_pwd.jsp");
	}
	
	//用户提币时发送邮件
	public String getCashSendEmailHtml(String userName, String content, Pages p){
		StringBuffer cont = new StringBuffer();
		cont.append("<h1 style='margin: 0px; padding: 0px 15px; height: 48px; overflow: hidden;'>");
		cont.append("	<a title='' href='" + MAIN_DOMAIN + "' target='_blank' swaped='true'>");
		cont.append("		<img style='border-width: 0px; margin: 0px; padding: 0px;' src='" + MAIN_DOMAIN + "/statics/img/logob.png' width='98' height='33' />");
		cont.append("	</a>");
		cont.append("</h1>");
		cont.append("<div style='padding: 0px 20px; height: 50px; text-align: right; line-height: 40px; overflow: hidden;'></div>");
		cont.append("<div style='padding: 2px 20px 30px;'>");
		cont.append("	<p>" + content + "</p>");
		cont.append("	<p><br/>" + L("此为自动发送邮件，请勿直接回复！如您有任何疑问，请点发送邮件到support@btcwinex.com。"));
		cont.append("	</p>");
		cont.append("</div>");
		return cont.toString();
	}
	
	//找回登录密码的处理方法
	public String findLoginPwd(User user , Pages p){
		setLan(user.getLanguage());
		String newPass = GetRadomStr().toLowerCase();
		String code = UserUtil.newSafeSecretMethod(user.getMyId(), newPass);
		String miYao = EncryptionPhoto.encrypt(Long.toString(System.currentTimeMillis()));
		log.info(VIP_DOMAIN+"/ac/password_usenew?userId="+user.getId()+"&code="+miYao);
		try {
			Datastore ds = super.getDatastore();
			Query<User> q = ds.find(User.class, "_id", user.getId());  
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("userContact.newPwd", code);
			ops.set("userContact.miyao", miYao);
			ops.set("userContact.newPwdTime", now());
			super.update(q, ops);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		p.setAttr("curUser", user);
		p.setAttr("newPass", newPass);
		p.setAttr("code", miYao);
		p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
		p.setAttr("lan", p.lan);

		log.info("新密码->>" + newPass);
		
		return p.newJsp("/cn/templet/find_login_pwd.jsp");
	}

	//xzhang 2017.08.17 新增方法充值及提币到账时发送邮件
	public String getAutoSendEmailHtml(String lan,String userName, String content){
		StringBuffer cont = new StringBuffer();
		cont.append("<div style='padding: 0px 20px; height: 50px; text-align: right; line-height: 40px; overflow: hidden;'></div>");
		cont.append("<div style='padding: 2px 20px 30px;'>");
		cont.append("	<p>" + content + "</p>");
		cont.append("	<p><br/>" + Lan.Language(lan,"此为自动发送邮件，请勿直接回复！如您有任何疑问，请点发送邮件到support@btcwinex.com。"));
		cont.append("	</p>");
		cont.append("</div>");
		return cont.toString();
	}

	public static String GetRadomStr() {
		/*String[] str = { "a", "B", "c", "D", "e", "f", "G", "h", "i", "J", "k", "L", "m", "n", "o", "P", "q", "r", "s", "T", "u", "v", "w", "X", "y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8"};
		Random r = new Random();
		int length = r.nextInt(10);// 先取出一个长度不固定的,小于6的,太长了也没用
		if (length < 6)
			length = 6;
		String ls = "";
		for (int j = 0; j < length; j++) {
			// for(int i=0;i <length;i++){
			int a = r.nextInt(str.length);
			ls += str[a];
			// log.info(str[a]);
			// }
		}
		return ls;*/
		 Random r = new Random();  
		int length = r.nextInt(20);// 
		if (length < 8)
			length = 8;
		
		   String val = "";  
	       
	          
	        //参数length，表示生成几位随机数  
	        for(int i = 0; i < length; i++) {  
	              
	            String charOrNum = r.nextInt(2) % 2 == 0 ? "char" : "num";  
	            //输出字母还是数字  
	            if( "char".equalsIgnoreCase(charOrNum) ) {  
	                //输出是大写字母还是小写字母  
	                int temp = r.nextInt(2) % 2 == 0 ? 65 : 97;  
	                val += (char)(r.nextInt(26) + temp);  
	            } else if( "num".equalsIgnoreCase(charOrNum) ) {  
	                val += String.valueOf(r.nextInt(10));  
	            }  
	        }  
	        return val;  
	}
	
	
	public static void main(String[] args) {
//		log.info(String.format("aaa%1$sbbb%2$sccc%3$s", new Object[]{"hhh" , "iii" , "jjj"}));
		
	}
}
