package com.world.web.action;

import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.entity.P2pUser;

/**
 * 手机短信验证码过滤Action
 * 1分钟重发
 * 10分钟内有效
 * 错误三次锁定2小时
 * @author Administrator
 *
 */
public class LoanUserAction extends UserAction{
	private static final long serialVersionUID = 1L;
	public P2pUser p2pUser = null;
	private P2pUserDao p2pUserDao = new P2pUserDao();

	/**
	 * 初始化用户借贷资产信息
	 */
	protected void initLoanUser(){
		initLoginUser();
		if(p2pUser == null){
			p2pUser = p2pUserDao.getById(loginUser.getId(), loginUser.getUserName());
			p2pUserDao.initLoanUser(p2pUser);
			p2pUserDao.initLoanWalletUser(p2pUser);
			setAttr("p2pUser", p2pUser);
		}
	}
	
}
