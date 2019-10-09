package com.world.controller.admin.level;

import com.google.code.morphia.query.Query;
import com.world.model.dao.level.UserVipLevelDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.level.UserVipLevel;
import com.world.model.entity.level.VipRate;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import me.chanjar.weixin.common.util.StringUtils;

import java.util.List;

@FunctionAction(jspPath = "/admins/level/", des = "用户积分等级")
public class Index extends AdminAction {

	UserVipLevelDao userVipLevelDao = new UserVipLevelDao();
	
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		try {
			
			int pageSize = intParam("pageSize");
			int pageIndex = 1;

			String strVipRate= request.getParameter("vipRate");//等级
			int jifen = intParam("jifen");//所需积分
			int vipRate= -1;
			if(StringUtils.isNotBlank(strVipRate)){
				vipRate = Integer.parseInt(strVipRate);
			}
			Query<UserVipLevel> q = userVipLevelDao.getQuery(UserVipLevel.class);
			if(vipRate >=0){
				q.filter("vipRate", vipRate);
			}
			if(jifen >=0){
				q.filter("jifen >= ", jifen);
			}
			
			q.order("vipRate");

			long count = userVipLevelDao.count(q);
			if (count > 0) {
				List<UserVipLevel> dataList = userVipLevelDao.findPage(q, pageIndex, pageSize);
				setAttr("dataList", dataList);
			}
			setAttr("vipRatesType", EnumUtils.getAll(VipRate.class));//等级类型
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
		
	}

	
	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	@Page(Viewer = XML)
	public void doDel() {
		String id = param("id");
		if (id.length() > 0) {
			boolean res = true;
			if (res) {// .filter("faBuZhe", adminName)
                userVipLevelDao.deleteByQuery(userVipLevelDao.getQuery().filter("_id", id));
                Write("删除成功", true, "");
                return;
			}
		}
		Write("未知错误导致删除失败！", false, "");
	}

	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			String id = param("id");
			if (id.length() > 0) {
				UserVipLevel userVipLevel = userVipLevelDao.get(id);
				setAttr("userVipLevel", userVipLevel);
			}
			setAttr("vipRatesType", EnumUtils.getAll(VipRate.class));//等级类型
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doAoru() {
		try {
			
			String id = param("id");//id
			int vipRate = intParam("vipRate");//等级
			int jifen = intParam("jifen");//对应积分
			double discount = doubleParam("discount");//折扣费率
			String memo = param("memo");//备注
			
			UserVipLevel userVipLevel = new UserVipLevel(userVipLevelDao.getDatastore());
			userVipLevel.setVipRate(vipRate);
			userVipLevel.setJifen(jifen);
			userVipLevel.setDiscount(discount);
			userVipLevel.setMemo(memo);
			int res = 0;
			if (id.length() > 0) {
				userVipLevel.setMyId(id);
				boolean flag = userVipLevelDao.updateUserVipLevel(userVipLevel);
				if(flag){
					res = 2;
				}
			} else {
				String nId  = userVipLevelDao.addUserVipLevel(userVipLevel);
				if (nId != null) {
					res = 2;
				}
			}
			if (res > 0) {
				WriteRight(L("操作成功"));
				return;
			}else{
				WriteError(L("操作失败"));
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			WriteError(L("操作失败"));
			return;
		}
	}

}

