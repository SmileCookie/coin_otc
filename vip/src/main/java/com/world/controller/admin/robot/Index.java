package com.world.controller.admin.robot;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.robot.RobotDao;
import com.world.model.dao.robot.RobotHandler;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.robot.RobotConfig;
import com.world.model.entity.user.User;
import com.world.timer.ExecutorServicePool;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import me.chanjar.weixin.common.util.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.regex.Pattern;

	@FunctionAction(jspPath = "/admins/robot/", des = "自动委托管理")
	public class Index extends AdminAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6488318080492871455L;
		
		RobotDao robotDao =new RobotDao();
		static Logger logger = Logger.getLogger(Index.class.getName());

		@Page(Viewer = DEFAULT_INDEX, Cache = 300)
		public void index() {
			try {
				String tab = param("tab");
				int pageSize = intParam("pageSize");
				int pageIndex = 1;
				String title = param("title");
				String account = param("account");
				
				if (tab.length() == 0)
					tab = "etc_btc";

				Query<RobotConfig> q = robotDao.getQuery(RobotConfig.class);
				q.filter("currency", tab);
				if(StringUtils.isNotBlank(title)){
					Pattern pattern = Pattern.compile("^" + title + ".*$", Pattern.CASE_INSENSITIVE);
					q.filter("title", pattern);
				}
				if(StringUtils.isNotBlank(account)){
					Pattern pattern = Pattern.compile("^" + account + ".*$", Pattern.CASE_INSENSITIVE);
					q.filter("account", pattern);
				}
				
				
				long count = robotDao.count(q);
				if (count > 0) {
					List<RobotConfig> dataList = robotDao.findPage(q, pageIndex, pageSize);
					setAttr("dataList", dataList);
				}
				
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
                    robotDao.deleteByQuery(robotDao.getQuery().filter("_id", id));
                    Write("删除成功", true, "");
                    //robotDao.reload();//重新刷新数据
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
					RobotConfig s = robotDao.get(id);
					setAttr("robotConfig", s);
				}
			} catch (Exception ex) {
				log.error("内部异常", ex);
			}
		}

		/**
		 * 新增或修改机器人信息
		 */
		@Page(Viewer = ".xml")
		public void doAoru() {
			try {
				String id = param("id");//id
				String title = param("title");//标题
				String account = request.getParameter("account");//账户
				String currency = request.getParameter("currency");//币种
				double lowPrice = doubleParam("lowPrice");//委托区间最低价格
				double highPrice = doubleParam("highPrice");//委托区间最高价格
				double minAmount = doubleParam("minAmount");//委托区间最小量
				double maxAmount = doubleParam("maxAmount");//委托区间最大量
				long freq = longParam("freq");
			
				int res = 0;
				
				UserDao userDao = new UserDao();
				User user = userDao.getByField("userName", account);
				if(user==null){
					WriteError(L("操作失败，该账户不存在！"));
					return;
				}
				
				RobotConfig e  = robotDao.getByaccount(account);
				Datastore ds = robotDao.getDatastore();
				if (id.length() > 0 && e.getId()==id) {
					Query<RobotConfig> query = ds.find(RobotConfig.class, "_id", id);

					UpdateOperations<RobotConfig> operate = ds.createUpdateOperations(RobotConfig.class).set("title", title)// 标题
							.set("account", account)// 内容
							.set("currency", currency)// 摘要
							.set("lowPrice", lowPrice)//
							.set("highPrice", highPrice)//
							.set("minAmount", minAmount)//
							.set("maxAmount", maxAmount)
							.set("freq", freq)
							.set("opUserName", super.adminName());

					UpdateResults<RobotConfig> ur = ds.update(query, operate);
					if (!ur.getHadError()) {
						res = 2;
					}
				} else {
					
					if(e==null){
						e = new RobotConfig(ds, title, currency,account, lowPrice, highPrice, minAmount, maxAmount, freq);
						e.setCreateTime(TimeUtil.getNow());
						e.setOpUserName(super.adminName());
						if (robotDao.save(e) != null) {
							res = 2;
						}
					}else{
						WriteError(L("操作失败，该账号不能重复添加配置机器人！"));
						return;
					}
					
					
				}
				if (res > 0) {
					//robotDao.reload();//重新刷新数据
					WriteRight(L("操作成功"));
					return;
				}else{
					WriteError(L("操作失败"));
					return;
				}
			} catch (Exception ex) {
				log.error("内部异常", ex);
				WriteError(L("操作失败"));
			}
		}

		@Page(Viewer = ".xml")
		public void start(){
			try{
				String id = param("id");//id
				int res = 0;//处理结果状态 >0为成功
				RobotConfig robotConfig = robotDao.getById(id);
				if(robotConfig!=null){
					robotConfig.setStatus(1);//设置为启动
					if (robotDao.save(robotConfig) != null) {
						//robotDao.reload();//重新刷新数据
						RobotHandler  handler = new RobotHandler(robotConfig);
						ExecutorServicePool.getExecutorService().execute(handler);
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
			}catch (Exception ex) {
				log.error(ex.toString(), ex);
				WriteError(L("操作失败"));
			}
		}
		
		
		@Page(Viewer = ".xml")
		public void stop(){
			try{
				String id = param("id");//id
				int res = 0;//处理结果状态 >0为成功
				RobotConfig robotConfig = robotDao.getById(id);
				if(robotConfig!=null){
					robotConfig.setStatus(0);//设置为启动
					if (robotDao.save(robotConfig) != null) {
						//robotDao.reload();//重新刷新数据
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
			}catch (Exception ex) {
				log.error("内部异常", ex);
				WriteError(L("操作失败"));
			}
		}
	
}

