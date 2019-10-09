package com.world.controller.admin.user.level;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.world.model.dao.level.IntegralRuleDao;
import com.world.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.RightMarginRecord;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.jifen.JifenDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.wallet.WalletDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.level.Jifen;
import com.world.model.entity.level.JifenType;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.User;
import com.world.util.DigitalUtil;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/user/level/", des = "积分管理")
public class Index extends AdminAction {

	JifenDao jifenDao = new JifenDao();
	UserDao userDao= new UserDao();
	PayUserDao payUserDao  = new PayUserDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		@SuppressWarnings("unchecked")
		EnumSet<JifenType> jifentypes = EnumUtils.getAll(JifenType.class);
		setAttr("jifentypes", jifentypes);
		
		ajax();
		
	}
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		//查询条件
		int currentPage = intParam("page");
		String tab = param("tab");
		String type = param("type");
		String userId = param("userId");
		String userName = param("userName");
		Timestamp timeS = dateParam("timeS");
		Timestamp timeE = dateParam("timeE");
		Query<Jifen> query = jifenDao.getQuery();
		List<Object> params = new ArrayList<Object>();
		
		query.setSql("select * from jifen");
		query.setCls(Jifen.class);
		if(StringUtils.isBlank(tab)){
			tab = "all";
		}
		setAttr("tab", tab);
		if(tab.equals("in")){
			query.append("ioType=0");
		}else if(tab.equals("out")){
			query.append("ioType=1");
		}else if (tab.equals("vip")) {
		/*	listVipRecord();
			SetViewerPath("/admins/user/level/vip6.jsp");
			return;*/
		}
		
		
		
		if(StringUtils.isNotBlank(userId)){
			query.append("userId = '"+userId+"'");
//			params.add(userId);
		}
		if(StringUtils.isNotBlank(userName)){
			query.append("userName like ?");
			params.add("%" + userName + "%");
		}
		if(StringUtils.isNotBlank(type)){
			query.append("type = ?");
			params.add(CommonUtil.stringToInt(type, -1));
		}
		if(timeS!=null){
			query.append("addTime >=?");
			params.add(timeS);
		}
		if(timeE!=null){
			query.append("addTime <=?");
			params.add(TimeUtil.getTodayLast(timeE));
		}
		
		query.setParams(params.toArray());
		int total = query.count();
		query.append(" ORDER BY id desc");
		if(total > 0){
			//分页查询
			List<Jifen> lists = jifenDao.findPage(currentPage, PAGE_SIZE);
			IntegralRuleDao integralRuleDao = new IntegralRuleDao();
			Map<String,String> ruleMap = integralRuleDao.getRuleMap();
			for(Jifen jifen : lists){
				String typeShow = ruleMap.get(jifen.getType() + "");
				jifen.setTypeShowNew(typeShow);
			}
			
			request.setAttribute("dataList", lists);
		}
		
		setPaging(total, currentPage);
	}	

	private void listVipRecord() {
		String userId = param("userId");
		String userName = param("userName");
		String userIdSql = "";
		String userNameSql = "";
		
		List<Object> params = new ArrayList<Object>();
		
		if(StringUtils.isNotBlank(userId)){
			userIdSql = " and userId = ?";
			params.add(userId);
		}
		if(StringUtils.isNotBlank(userName)){
			userNameSql =" and userName like ?";
			params.add("%" + userName + "%");
		}
		
		String sql = " SELECT pay_user.User_Id, " +
				" pay_user.Pay_Real_Name, " +
				" consume_details.Date, " +
				" pay_user.superVipTime, " +
				" consume_details.Description " +
				" FROM consume_details " +
				" INNER JOIN pay_user ON consume_details.User_Id = pay_user.User_Id " +
				" WHERE consume_details.Description = '购买升级vip6' and pay_user.superVipTime>now()  " + userIdSql + userNameSql +
				" ORDER BY consume_details.Date DESC " ;

	log.info(sql);
		List query = Data.Query(sql, params.toArray());
		setAttr("dataList", query);
		
		
	}
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		
	}

	@Page(Viewer = XML)
	public void doAoru() {
		try {
			String userName = param("userName");
			double jifen = doubleParam("jifen");
			String desc = param("desc");
			int addOrDel = intParam("addOrDel");
			User user = userDao.getByField("userName", userName);
			//PayUserBean payUser  = payUserDao.getById(Integer.valueOf(user.getId()));
			double addJifen = 0;//变更的积分数量
			
			List<OneSql> sqls = new ArrayList<OneSql>();
			
			if(addOrDel==1 && user.getTotalJifen()<jifen){
				WriteError("用户的积分不够扣除");
			}
			
			Jifen jifenObj = new Jifen();
			jifenObj.setUserId(user.getId());
			jifenObj.setUserName(user.getUserName());
			jifenObj.setJifen(BigDecimal.valueOf(jifen));
			jifenObj.setMemo(desc);
			jifenObj.setType(JifenType.admOper.getKey());
			jifenObj.setIoType(addOrDel);
			jifenObj.setStatus(0);
			jifenObj.setAddTime(TimeUtil.getNow());
			
			sqls.add(jifenDao.getTransInsertSql(jifenObj));
			if(addOrDel==0){
				addJifen = jifen;
				//sqls.add(new OneSql("update pay_user set totalJifen=totalJifen+? where user_Id=?",1, new Object[]{jifen,user.getId()}));
			}else{
				addJifen = -jifen;
				//sqls.add(new OneSql("update pay_user set totalJifen=totalJifen-? where  totalJifen>=? and user_Id=? ",1, new Object[]{jifen,jifen,user.getId()}));
			}
			
			if(Data.doTrans(sqls)){
				userDao.updateUserJifen(user.getId(), addJifen);
				WriteRight("操作成功");
				return;
			}else{
				WriteError("操作失败,请联系技术人员");
				return;
			}


		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
		WriteError("未知错误导致添加失败！");
	}
	
}

