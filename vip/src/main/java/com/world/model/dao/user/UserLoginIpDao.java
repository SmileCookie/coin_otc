package com.world.model.dao.user;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.api.config.ApiConfig;
import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserLoginIp;
import com.world.util.ip.IpUtil;

@SuppressWarnings("serial")
public class UserLoginIpDao extends MongoDao<UserLoginIp, String>{

	public boolean add(String loginName, String userId, String userName, String newIp, int terminal, String version){
		List<UserLoginIp> lists = getIps(userId, 1, 50);
		String removeId = "";
		if(lists.size() >= 50){
			removeId = lists.get(lists.size() - 1).getId();
			this.delById(removeId);
		}
		String loginTerminal = StringUtils.isBlank(version)?"网页":version;

		UserLoginIp uli = new UserLoginIp(this.getDatastore());
		uli.setUserId(userId);
		uli.setDate(now());
		uli.setIp(newIp);
		uli.setTerminal(terminal);

		uli.setDescribe(loginName+"："+LanFormat("登录账号【%%】",userName)+"，"+LanFormat("登录方式【%%】",loginTerminal));
		try {
			String key = ApiConfig.getValue("IP_ANALYSIS");
			String city = IpUtil.getCityByIpApi(newIp,key);
			if(newIp.equals("127.0.0.1")){
				city = "本机";
			}

			if(city != null){
				uli.setCity(L(city));
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		this.save(uli);
		return true;
	}

	public UserLoginIp getLastIp(String userId){
		UserLoginIp lastIp = findOne(getQuery().filter("userId", userId).order("-date"));
		return lastIp;
	}

	public List<UserLoginIp> getIps(String userId, int pageIndex, int pageSize){
		List<UserLoginIp> lists = this.findPage(this.getQuery().filter("userId =", userId).order("-date"), pageIndex, pageSize);
		return lists;
	}

	public List<UserLoginIp> getIps(String userId){
		return getIps(userId, 1, 10);
	}
	public List<UserLoginIp> getAllIps(String userId){
		List<UserLoginIp> lists = getListByField("userId", userId);
		return lists;
	}
	/**
	 * 判断是否需要手机验证码
	 * @param user
	 * @param newIp
	 * @return
	 */
	public boolean needCheckMobile(User user, String newIp){
		String userId = user.getId();
		UserLoginIp lastIp = (UserLoginIp)this.findOne(getQuery().filter("userId =", userId).order("-date"));
		String city = "";
		try {
			city = IpUtil.getCity(newIp);
		} catch (Exception e) {
			log.error(e.toString(), e);
			city = lastIp.getCity();
		}
		if(newIp.equals("127.0.0.1")){
			city = "本机";
		}

		/*if(StringUtils.isNotEmpty(city)){
			List<UserLoginIp> list = (List<UserLoginIp>)this.find(getQuery().filter("userId", userId).filter("city", city).filter("checked", 1)).asList();
			if(list.size() > 0){//
				return false;
			}
		}
		if(lastIp != null){
			if(StringUtils.isNotEmpty(lastIp.getCity()) && !lastIp.getCity().equals(city)){
				return true;
			}
		}

		return false;*/

		if(StringUtils.isNotEmpty(city)){
			if("IP地址库文件错误".equals(city) && !newIp.contains("192.168.")){
				return true;
			}

			List<UserLoginIp> list = (List<UserLoginIp>)this.find(getQuery().filter("userId", userId).filter("city", city).filter("checked", 1)).asList();
			if(list.size() > 0){
				return false;
			}
		}
		if(lastIp != null){
			if(StringUtils.isNotBlank(lastIp.getCity()) && lastIp.getCity().equals(city)){
				return false;
			}
		}

		return true;
	}

	public boolean checkTrustIp(String userId, String newIp){
		boolean flag = false;
		UserLoginIp lastIp = (UserLoginIp)this.findOne(getQuery().filter("userId =", userId).order("-date"));
		String city = "";
		try {
			city = IpUtil.getCity(newIp);
		} catch (Exception e) {
			log.error(e.toString(), e);
			city = lastIp.getCity();
		}
		if(newIp.equals("127.0.0.1")){
			city = "本机";
		}
		if(StringUtils.isNotEmpty(city)){
			List<UserLoginIp> list = (List<UserLoginIp>)this.find(getQuery().filter("userId", userId).filter("city", city).filter("checked", 1)).asList();
			if(list.size() > 0){//
				flag = true;
			}
		}
		return flag;
	}

	public UpdateResults<UserLoginIp> updateChecked(String userId, String ip){
		Datastore ds = super.getDatastore();
		Query<UserLoginIp> q = ds.find(UserLoginIp.class, "userId", userId).filter("ip", ip).field("checked").notEqual(1);
		UpdateOperations<UserLoginIp> ops = ds.createUpdateOperations(UserLoginIp.class);
		ops.set("checked", 1);
		return update(q, ops);
	}

	public void setLoginCache(String sessionId, String userName, String newIp, HttpServletResponse response){
		Cache.SetObj(sessionId+"_"+newIp, userName, 60*20);
	}

	public Object getLoginCache(String sessionId, String newIp){
		return Cache.GetObj(sessionId + "_" + newIp);
	}

	public boolean clearLoginCache(String sessionId, String newIp, HttpServletResponse response){
		return Cache.Delete(sessionId + "_" + newIp);
	}

	UserDao userDao = new UserDao();
	public boolean sendMobile(User user, String newIp){
		String userId = user.getId();
		if(user.getUserContact().isCouldPost()){
			if(StringUtils.isNotEmpty(user.getUserContact().getSafeMobile())){
				log.info("用户"+user.getUserName()+"异地登录，发送短信验证码");

				return true;
			}
		}else{
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String city = IpUtil.getCity("183.32.190.142");
		log.info(city);
	}

}
