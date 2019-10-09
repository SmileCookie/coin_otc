package com.world.model.dao.admin.user;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.world.model.dao.admin.competence.CompetenceMongoDao;
import com.world.model.entity.admin.AdminUser;

public class AdminUserDao extends CompetenceMongoDao<AdminUser, String>{
	
	public AdminUser getUserByKeyAndSecret(String key, String secret){
		return this.findOne(this.getQuery().filter("admName =", key).filter("secretKey =", secret));
	}
	
	public AdminUser getUserByKey(String key){
		return this.findOne(this.getQuery().filter("admName =", key));
	}

	public Map<String , AdminUser> getUserMapByIds(List<String> ids){
		Map<String , AdminUser> maps = new LinkedHashMap<String, AdminUser>();
		List<AdminUser> users = super.getListByIds(ids);
		if(users != null && users.size() > 0){
			for(AdminUser u : users){
				maps.put(u.getId(), u);
			}
		}
		return maps;
	}
	
	public AdminUser jsonToEntity(JSONObject obj){
  		try {
  			AdminUser aUser = new AdminUser();
  			aUser.setMyId(obj.getString("_id"));
  			aUser.setAdmName(obj.getString("admName"));
  			aUser.setAdmUName(obj.getString("admUName"));
  			aUser.setAdmPassword(obj.getString("admPass"));
  			aUser.setAdmDes(obj.getString("admDes"));
  			aUser.setAdmSex(obj.getInt("admSex"));
  			aUser.setAdmRoleId(obj.getInt("admRoleId"));
  			aUser.setAdmPartId(obj.getInt("admPartId"));
  			aUser.setEmail(obj.getString("email"));
  			aUser.setTelphone(obj.getString("telphone"));
  			aUser.setIsLocked(obj.getInt("isLocked"));
  			aUser.setSecret(obj.getString("secret"));
  			aUser.setCreateId(obj.getInt("createId"));
  			aUser.setLastLoginIp(obj.getString("lastLoginIp"));
  			
  			return aUser;
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
  		return null;
  	}

	public JSONObject entityToJSON(AdminUser aUser){
		try {
			JSONObject obj = new JSONObject();
			obj.put("_id", aUser.getAdmId());
			obj.put("admName" , aUser.getAdmName());
			obj.put("admUName" , aUser.getAdmUName());
			obj.put("admPass" , aUser.getAdmPassword());
			obj.put("admRoleId" , aUser.getAdmRoleId());
			obj.put("admDes" , aUser.getAdmDes());
			obj.put("isLocked" , aUser.getIsLocked());
			obj.put("admSex" , aUser.getAdmSex());
			obj.put("admPartId" , aUser.getAdmPartId());
			obj.put("email", aUser.getEmail());
			obj.put("telphone", aUser.getTelphone());
			obj.put("secret", aUser.getSecret());
			obj.put("createId", aUser.getCreateId());
			obj.put("lastLoginIp", aUser.getLastLoginIp());
			
			return obj;
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return null;
	}
}
