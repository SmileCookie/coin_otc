package com.world.model.dao.user.relation;

import java.util.EnumSet;
import java.util.List;

import com.world.model.entity.admin.competence.MenuViewFunction;
import com.world.model.entity.user.relation.UserRelation;
import com.world.model.entity.user.relation.UserRelationFunction;
import com.world.model.entity.user.relation.UserRelationModule;

public class UserRelactionCommonDao {
	
	public static boolean getUserCompetence(MenuViewFunction mvf, String userId, String url){
		UserRelationDao urdao = new UserRelationDao();
		UserRelationRoleDao urrdao = new UserRelationRoleDao();
		UserRelationModuleDao urmdao = new UserRelationModuleDao();
		UserRelationFunctionDao urfdao = new UserRelationFunctionDao();
		//父用户，如果角色表里存在此用户创建的角色
		if(urrdao.getCount(userId) > 0){
			mvf.setRoleId("-1");
			mvf.setName(userId);
			return true;
		//如果是子用户
		}else{
			//查询用户关系表
			UserRelation ur = urdao.findBySubUser(userId);
			//普通用户
			if(ur == null){
				//不处理
				mvf.setRoleId("-1");
				mvf.setName(userId);
				return true;
			}else{
				//功能
				List<UserRelationFunction> functions = urfdao.findByRole(ur.getRoleId());
				int[] array1 = new int[functions.size()];
				int i = 0;
				boolean isTrue = false;
				EnumSet<PermissionEnum> all = PermissionEnum.getAll(PermissionEnum.class);
				for (PermissionEnum e : all) {
					String furl = e.getUrl().toLowerCase();
					if(url.equals(furl) || url.equals(furl + "/")){
						isTrue = true;
						break;
					}
				}
				boolean flag = false;
				for (UserRelationFunction func : functions) {
					array1[i] = func.getFunction();
					PermissionEnum m = PermissionEnum.getEnumByKey(func.getFunction(), PermissionEnum.class);
					if(m != null && isTrue){
						String furl = m.getUrl().toLowerCase();
						if(url.equals(furl) || url.equals(furl + "/")){
							flag = true;
						}
					}
					i++;
				}
				mvf.setFunctions(array1);
				//没有访问权限
				if(!flag && isTrue) return false;
				
				//模块
				List<UserRelationModule> modules = urmdao.findByRole(ur.getRoleId());
				mvf.setRoleId(ur.getRoleId());
				mvf.setName(ur.getUserId());
				String[] array = new String[modules.size()];
				i = 0;
				for (UserRelationModule module : modules) {
					array[i] = module.getModule();
					i++;
				}
				mvf.setPlateDataIds(array);
				return true;
			}
		}
	}
	
}
