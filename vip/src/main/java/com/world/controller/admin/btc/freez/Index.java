package com.world.controller.admin.btc.freez;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.pay.FreezDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.pay.FreezType;
import com.world.model.entity.pay.FreezeBean;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.User;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@FunctionAction(jspPath = "/admins/btc/freez/" , des = "冻结管理")
public class Index extends AdminAction{
	UserDao uDao = new UserDao();
	PayUserDao payDao = new PayUserDao();
	FreezDao freezDao = new FreezDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		//查询条件
		int currentPage = intParam("page");
		String currentTab = param("tab");
		Timestamp startTime = dateParam("startTime");
		Timestamp endTime = dateParam("endTime");
		String userName = param("userName");
		String memo = param("memo");
		
		freezDao.setCoint(coint);
		Query<FreezeBean> query = freezDao.getQuery();
		query.setSql("select * from "+freezDao.getTableName());
		query.setCls(FreezeBean.class);
		if(currentTab.length()>0){
			if(currentTab.equals("manager")){
				query.append(" AND Type=7 and statu=0");
			}else if(currentTab.equals("system")){
				query.append(" AND Type<>7 and statu=0");
			}else if(currentTab.equals("all")){
				
			}else{
				query.append(" AND Type<>7 and statu=0");
				currentTab="system";
			}
		}else{
			query.append(" AND Type<>7 and statu=0");
			currentTab="system";
		}
		request.setAttribute("currentTab", currentTab);
		request.setAttribute("userName", userName);
		
		if(startTime!=null){
			
			query.append(" and freezeTime>=cast('"+startTime+"' as datetime)");
		}
		if(endTime!=null){
			query.append(" and freezeTime<=cast('"+endTime+"' as datetime)");
		}
		
		if(userName.trim().length()>0){
			Pattern pattern = Pattern.compile("^.*"  + userName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
			List<User> users = uDao.find(uDao.getQuery().filter("userName", pattern)).asList();
			String userIds="";
			if(users.size()>0){
				for(User b : users){
					userIds+=","+b.getId();
				}
			}
			if(userIds.length()>0){
				userIds=userIds.substring(1);
				query.append("UserId in("+userIds+")");
			}
		}
		if(memo.trim().length()>0){
			query.append(" and reMark like '%"+memo+"%'");
		}
		
		int total = query.count();
		
		if(total>0){
			//分页查询
			query.append("order by freezeTime desc");
			
			List<FreezeBean> inOuts=query.getPageList(currentPage, PAGE_SIZE);
			
			List<String> userIds = new ArrayList<String>();
			for(FreezeBean btb : inOuts){
				userIds.add(btb.getUserId()+"");
			}
			
			if(userIds.size()>0){
				Map<Object,User> userMaps = uDao.getMapByField("id", uDao.getQuery().filter("_id in", userIds));
				
				for(Bean b : inOuts){
					FreezeBean btb=(FreezeBean) b;
					btb.setUser(userMaps.get(btb.getUserId()+""));
				}
			}
			setAttr("dataList", inOuts);
			setPaging(total, currentPage);
		}
	}
	@Page(Viewer=DEFAULT_AJAX)
	public void ajax(){
		index();
	}	
	
	@Page(Viewer="/admins/btc/freez/freez.jsp")
	public void freez(){
		String userId = param(0);
		User user = uDao.get(userId);
		if(user != null)
			setAttr("userName", user.getUserName());
		
		PayUserBean payUser = payDao.getById(Integer.parseInt(userId), coint.getFundsType());
		if (payUser!=null)
			setAttr("total", payUser.getBalance());
	}
	
	///冻结用户资金
	@Page(Viewer=".xml")
	public void doFreez(){
		if(!codeCorrect(XML)){
			return;
		}
		String strUserName=request.getParameter("userName");
		BigDecimal freezMoney=decimalParam("money");
		String reason = param("reason");
		if(reason.length()<=0){
			reason=FreezType.glyFreez.getValue();
		}
		User curUser=null;
		try {
			curUser = uDao.getUserByColumn(strUserName, "userName");
			if(curUser!=null){
				String userName=curUser.getUserName();
				int userId=Integer.parseInt(curUser.getId());
				PayUserBean user = payDao.getById(Integer.parseInt(curUser.getId()), coint.getFundsType());
				
				if(user.getBalance().compareTo(freezMoney) < 0){
					Write("用户["+userName+"]的"+coint.getPropTag()+"余额"+user.getBalance().doubleValue()+"，无法冻结"+freezMoney+"个！",false,"");
					return;
				}
				freezDao.setCoint(coint);
				FreezeBean freez = new FreezeBean();
				freez.setUserId(userId+"");
				freez.setUserName(userName);
				freez.setReMark(reason);
				freez.setType(FreezType.glyFreez.getKey()); //比特币冻结类型
				freez.setBtcNumber(freezMoney);
				long newFreezId = freezDao.getId();
				freez.setFreezeId(newFreezId);
				freez.setFreezeTime(now());
			
				List<OneSql> paySqls = new ArrayList<OneSql>();
				freezDao.freez(paySqls, freez);
				
				if(Data.doTrans(paySqls)){
					UserCache.resetUserFunds(curUser.getId());//即时刷新用户资产
					try {
						//插入一条管理员日志信息
						DailyType type = DailyType.btcFreez;
						new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "管理员冻结", userName, freezMoney, reason, 0, coint.getPropTag()), String.valueOf(adminId()), ip(), now());
					} catch (Exception e) {
						log.error("添加管理员日志失败", e);
					}
					
					Write("成功冻结用户["+userName+"]的"+coint.getPropTag()+""+freezMoney+"个！",true,"");
					return;
				}
			}else{
				Write("当前用户不存在，冻结"+coint.getPropTag()+"失败!",false,"");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			Write("程序出错",false,"");
		}
		
	}	
	
	
	///解冻用户资金
	@Page(Viewer=".xml")
	public void unFreez(){
		if(!codeCorrect(XML)){
			return;
		}
		String strFreezId=param("fid");
		String freezId=strFreezId;
		if(freezId.length() <= 0){
			Write("当前冻结不存在",false,"");
			return;
		}
		
		User curUser=null;
		try {
			freezDao.setCoint(coint);
			FreezeBean bfb = (FreezeBean)Data.GetOne("select * from "+freezDao.getTableName()+" where freezeId=? and Type=? and statu=? and connectedId=?", 
					new Object[]{freezId,FreezType.glyFreez.getKey(),0,0}, FreezeBean.class);
			
			if(bfb!=null){
				int userId=Integer.parseInt(bfb.getUserId());
				curUser = uDao.getById(userId+"");
				String userName=curUser.getUserName();
				
				PayUserBean user = payDao.getById(Integer.parseInt(curUser.getId()), coint.getFundsType());
				BigDecimal unfreezAmount=bfb.getFreezeBanlance();
				
				List<OneSql> paySqls=new ArrayList<OneSql>();
				if(unfreezAmount.compareTo(BigDecimal.ZERO) > 0 && unfreezAmount.compareTo(user.getFreez()) <= 0){
					//管理员解冻的类型
					long newFreezId = freezDao.getId();
					//解冻并扣除的语句
					FreezeBean fbean = new FreezeBean(bfb.getUserId(), userName, "解冻"+coint.getPropCnName(), FreezType.glyUnFreez.getKey(), unfreezAmount, newFreezId, bfb.getFreezeId());
					
					freezDao.unFreezSqls(paySqls, fbean, null, false);
					
					//所以关于资金变动的调用统一方法
//					paySqls = FundsConvertUtil.getInstance().assetChanges(
//								CurrencyType.BTC, MergeType.UNFREEZE_NOT_DEDUCT, null,
//								userId, unfreezAmount, adminId(), 0, "解冻管理员冻结比特币", "", "", false,0,FreezType.glyUnFreez.getKey());
//					
				}else{
					Write("当前用户没有可解冻的管理员冻结资金!",false,"");
					return;
				}
		 		
				if(Data.doTrans(paySqls)){
					UserCache.resetUserFunds(curUser.getId());//即时刷新用户资产
					try {
						//插入一条管理员日志信息
						DailyType type = DailyType.btcUnFreez;
						new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "管理员解冻", userName, unfreezAmount, freezId, coint.getPropTag()), String.valueOf(adminId()), ip(), now());
					} catch (Exception e) {
						log.error("添加管理员日志失败", e);
					}
					
					Write("成功解冻用户["+userName+"]的资金"+unfreezAmount+"元！",true,"");
					return;
				}
			}else{ 
				Write("当前冻结不存在!",false,"");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			Write("程序出错",false,"");
		}
		
	}
	
	@Page(Viewer="/admins/btc/freez/upfreez.jsp")
	public void handsUnFreezPage(){
		String userId = param(0);
		User user = uDao.get(userId);
		if(user != null)
			setAttr("userName", user.getUserName());
		
		PayUserBean payUser = payDao.getById(Integer.parseInt(userId), coint.getFundsType());
		if (payUser!=null)
			setAttr("total", payUser.getFreez());
//		else
//			setAttr("total", "");
		//获取记录的鉴定结果Id
		  //List<Bean> reasons=Data.Query(workName, "select * from Evaluate_Reason where Type=?" , new Object[]{21},EvaluateReason.class);
		  //request.setAttribute("reasons", reasons);
	}	
	
	///手动解冻用户资金   此方法用于处理系统出错时
	@Page(Viewer=".xml")
	public void handsUnFreez(){
		if(!codeCorrect(XML)){
			return;
		}
		String strUserName=request.getParameter("userName");
		BigDecimal freezMoney=decimalParam("money");
		String des = request.getParameter("reason");
		if(des.length()<=0){
			des=FreezType.glyFreez.getValue();
		}
		User curUser=null;
		try {
			curUser = uDao.getUserByColumn(strUserName, "userName");
			if(curUser!=null){
				String userName=curUser.getUserName();
				int userId=Integer.parseInt(curUser.getId());
				
				PayUserBean user = payDao.getById(Integer.parseInt(curUser.getId()), coint.getFundsType());
				
				if(user.getFreez().compareTo(freezMoney) < 0){
					Write("用户["+userName+"]的冻结"+coint.getPropTag()+"余额"+user.getFreez().doubleValue()+"，无法解冻"+freezMoney+""+coint.getPropTag()+"！",false,"");
					return;
				}
				List<OneSql> paySqls=new ArrayList<OneSql>();
				
				freezDao.setCoint(coint);
				long newFreezId = freezDao.getId();
				//解冻并扣除的语句
				FreezeBean fbean = new FreezeBean(curUser.getId(), userName, "解冻管理员冻结"+coint.getPropCnName(), FreezType.glyUnFreez.getKey(), freezMoney, newFreezId, 0);
				
				freezDao.setDatabase(coint.getDatabasesName());
				freezDao.unFreezSqls(paySqls, fbean, null, false);

				//所以关于资金变动的调用统一方法
//				paySqls = FundsConvertUtil.getInstance().assetChanges(
//							CurrencyType.BTC, MergeType.UNFREEZE_NOT_DEDUCT, null,
//							userId, freezMoney, adminId(), 0, des, "", "", false, 0, FreezType.glyUnFreez.getKey());
				
				if(Data.doTrans(paySqls)){
					UserCache.resetUserFunds(curUser.getId());//即时刷新用户资产
					try {
						//插入一条管理员日志信息
						DailyType type = DailyType.btcUnFreez;
						new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "管理员手动解冻", userName, freezMoney, des, coint.getPropCnName()), String.valueOf(adminId()), ip(), now());
					} catch (Exception e) {
						log.error("添加管理员日志失败", e);
					}
					
					Write("成功解冻用户["+userName+"]的"+coint.getPropTag()+""+freezMoney+"个！",true,"");
					return;
				}
			}else{
				Write("当前用户不存在，解冻"+coint.getPropTag()+"失败!",false,"");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			Write("程序出错",false,"");
		}
		
	}
}
