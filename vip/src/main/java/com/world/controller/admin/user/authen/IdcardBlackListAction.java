package com.world.controller.admin.user.authen;

import org.apache.commons.lang.StringUtils;

import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.authen.IdcardBlackListDao;
import com.world.model.entity.user.authen.IdcardBlackList;
import com.world.util.CommonUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/user/authen/idcardBlackList/" , des = "证件号码黑名单管理")
public class IdcardBlackListAction extends AdminAction{

	IdcardBlackListDao dao = new IdcardBlackListDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		// 获取参数
		int pageNo = intParam("page");
		int pageSize = 20;
		long total = dao.search4Back(param("cardNo"), pageSize, pageNo, this);
		setPaging((int) total, pageNo, pageSize);
	}
	
	@Page(Viewer=DEFAULT_AJAX)
	public void ajax(){
		index();
	}
	
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			String id = param("id");
			if(StringUtils.isNotBlank(id)){
				IdcardBlackList idcardBlackList = dao.getById(id);
				request.setAttribute("entity", idcardBlackList);
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer = XML)
	public void doAoru() {
		try {
			String id = param("id");
			String cardNo = CommonUtil.replaceBlank(param("cardNo"));
			String remark = CommonUtil.replaceBlank(param("remark"));
			if(StringUtils.isNotBlank(cardNo)) {
				if (StringUtils.isBlank(id)) {
					dao.save(cardNo, remark);
				} else {
					dao.update(dao.getQuery().filter("_id =", id),
							dao.getUpdateOperations()
									.set("cardNo", cardNo)
									.set("remark", param("remark"))
									.set("createTime", now())
					);
				}

				WriteRight("保存成功");
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
		WriteError("保存失败");
	}
	
	@Page(Viewer = XML)
	public void doDel() {
		String id = param("id");
		if (StringUtils.isNotBlank(id)) {
			IdcardBlackList idcardBlackList = dao.getById(id);
            dao.deleteByQuery(dao.getQuery().filter("myId", id));
            AuthenticationDao authenticationDao = new AuthenticationDao();
            try {
                authenticationDao.update(authenticationDao.getQuery().filter("cardId =", idcardBlackList.getCardNo()),
                        authenticationDao.getUpdateOperations()
                                .set("isCardIdBlackList", false)
                );
            }catch (Exception ex){
                log.error(ex.toString(), ex);
            }

            Write("删除成功", true, "");
            return;
		}
		Write("未知错误导致删除失败！", false, "");
	}

	@Page(Viewer = "/admins/user/authen/idcardBlackList/add.jsp", des = "打开加入黑名单页面")
	public void addBlackList() {
		setAttr("id", param("id"));
		setAttr("cardNo", param("cardNo"));
	}

	@Page(Viewer = XML, des = "加入黑名单")
	public void doAddBlackList() {
		try {
			String cardNo = CommonUtil.replaceBlank(param("cardNo"));
			String remark = CommonUtil.replaceBlank(param("remark"));
			if(StringUtils.isNotBlank(cardNo)) {
				boolean isIn = dao.isBlackList(cardNo);
				if(isIn){
					WriteError("已经是黑名单了，不需要重复设置。");
				}else{
					dao.save(cardNo, remark);
					WriteRight("加入黑名单成功");
				}

				String id = param("id");
				AuthenticationDao authenticationDao = new AuthenticationDao();
				authenticationDao.update(authenticationDao.getQuery().filter("_id =", id),
						authenticationDao.getUpdateOperations()
								.set("isCardIdBlackList", true)
				);
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
		WriteError("加入黑名单失败");
	}
}
