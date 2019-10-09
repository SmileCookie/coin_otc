package com.world.model.financialproift.userfininfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.model.entity.financialproift.UserFinancialInfo;
import com.world.model.financial.entity.BillFinancial;
import com.world.model.financialproift.userfininfo.thread.ReturnUserOrderThread;
import com.world.util.date.TimeUtil;

import me.chanjar.weixin.common.util.StringUtils;

public class ReturnUserOrderWork extends Worker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*查询SQL*/
	private String sql = "";
	
	/*此轮定时任务结束标识*/
    private static boolean workFlag = true;
    /*展示批量耗时的标识*/
    private boolean showInfoFlag = false;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;
	
	public ReturnUserOrderWork(String name, String des) {
		super(name, des);
	}
	
	@SuppressWarnings("unused")
	@Override
	public void run() {	
		/*记录核算开始时间*/
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        /*现在时间获取*/
        if (intLogInfoFlag == 10) {
        	log.info("理财报警REWARDINFO:【回本用户顺序列表生成】开始");
        }
        //任务是否可执行
      	boolean taskflag = false;
      	showInfoFlag = false;
		
        try {
			/**
			 * 取得类名称，作为任务名称
			 */
			sql = "select * from fin_task where taskName = 'ReturnUserOrderWork' and taskTime <= NOW() and taskFlag = 1 ";
			log.info("HierarchyWork sql = " + sql);
			financialTask = (FinancialTask) Data.GetOne("vip_financial",sql, null,FinancialTask.class);
			if (null == financialTask) {
				if (intLogInfoFlag == 10) {
					log.info("理财报警REWARDINFO:【回本用户顺序列表生成】没有需要执行的任务");
				}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【回本用户顺序列表生成】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
		} catch (Exception e1) {
			log.info("理财报警REWARDERROR:【回本用户顺序列表生成】任务配置初始化失败", e1);
			return;
		} finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
        if (workFlag && taskflag) {
        	workFlag = false;
        	try {
    			Date calStart = financialTask.getCallStartDate();
    			Date calEnd = financialTask.getCallEndDate();
    			log.info("calStart = " + calStart + ", calEnd = " + calEnd);
    			/*批次设置*/
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String curDate = sdf.format(new Date());
                String distType = "8";
            	String batchNo = curDate + distType;
            	/*删除临时表数据*/
            	sql = "truncate table fin_userreturnorderinfotmp";
            	log.info("sql = " + sql);
            	int intTruncate = Data.Insert("vip_financial", sql, null);
    			if (-1 == intTruncate) {
    				log.info("理财报警REWARDERROR:【回本用户顺序列表生成】truncate fin_userreturnorderinfotmp异常");
    				return;
    			}
            	
    			/**
    			 * 1、前置查找1周内进行动态和静态提取的用户
    			 * (type in (5110, 5120, 5130) and fundsType = 10) or 
    			 */
    			sql = "select distinct userId, userName from bill_financial fa where FROM_UNIXTIME(createTime / 1000) >= '" + calStart + "' "
    				+ "and FROM_UNIXTIME(createTime / 1000) < '" + calEnd + "' "
    				+ "and ((type = 5371 and fundsType = 51)) ";
    			log.info("sql = " + sql);
    			List<Bean> listWeekNoDrawUsers = (List<Bean>) Data.Query("vip_main", sql, null, BillFinancial.class);
    			log.info("listWeekNoDrawUsers.size = " + listWeekNoDrawUsers.size());
    			/*定义循环接收变量，注意此处下面的程序会继续沿用此变量*/
    			BillFinancial billFinancial;
    			/*提取过收益的用户ID和用户名*/
    			String userId = "";
    			String userName = "";
    			Map<String, String> mapWeekNoDrawUsers = new HashMap<String, String>();
    			if (null != listWeekNoDrawUsers) {
    				for (int i = 0; i < listWeekNoDrawUsers.size(); i++) {
    					billFinancial = (BillFinancial) listWeekNoDrawUsers.get(i);
    					userId = billFinancial.getUserId() + "";
    					userName = billFinancial.getUserName();
    					log.info("userId = " + userId + ", userName = " + userName);
    					mapWeekNoDrawUsers.put(userId, userName);
    				}
    			}
    			log.info("mapWeekNoDrawUsers = " + mapWeekNoDrawUsers.size());
    			
    			/**
    			 * 2、获取筛选直推人员大于3个的VIP 
    			 * 2-1、未复投过的：authPayFlag = 2，resetProfitTime is null
    			 * 2-2、复投过的：authPayFlag = 2, resetProfitTime is not null
    			 */
        		sql = "select invitationUserName, count(*) cnt from fin_userfinancialinfo "
        			+ "where authPayFlag != 1 and profitTime > '2019-07-01' and profitTime <= '" + calEnd + "' "
        			+ "group by invitationUserName having count(*) >= 3";
    			log.info("sql = " + sql);
//    			UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
    			List<Bean> listUserFinancialInfo = (List<Bean>) Data.Query("vip_financial", sql, null, UserFinancialInfo.class);
    			log.info("listUserFinancialInfo.size = " + listUserFinancialInfo.size());
    			/**
    			 * 定义循环变量
    			 */
    			/*推荐人用户名*/
    			String invitationUserName = "";
    			UserFinancialInfo userFinancialInfo;
    			if (null != listUserFinancialInfo) {
    				ExecutorService executorReturnUserOrder = Executors.newFixedThreadPool(1);
    				CountDownLatch countDownLatch = new CountDownLatch(listUserFinancialInfo.size());
    				/*listUserFinancialInfo.size()*/
    				for (int i = 0; i < listUserFinancialInfo.size(); i++) {
    					userFinancialInfo = (UserFinancialInfo) listUserFinancialInfo.get(i);
    					if (StringUtils.isEmpty(userFinancialInfo.getInvitationUserName()) ) {
    						countDownLatch.countDown();
    						continue;
    					} else {
    						invitationUserName = userFinancialInfo.getInvitationUserName();
    					}
    					/*剔除提取过收益的用户*/
    					if (mapWeekNoDrawUsers.containsValue(invitationUserName)) {
    						countDownLatch.countDown();
    						continue;
     					}
    					ReturnUserOrderThread returnUserOrderThread = new ReturnUserOrderThread(countDownLatch, 
    							invitationUserName, batchNo);
    					executorReturnUserOrder.execute(returnUserOrderThread);
    				}
    				countDownLatch.await();
    				showInfoFlag = true;
    				/*关闭线程池*/
    				executorReturnUserOrder.shutdown();
    			} else {
    				log.info("理财报警REWARDTASK:【回本用户顺序列表生成】没有满足条件的用户");
    				return;
    			}
    			
    			/**
    			 * 3、数据迁移
    			 * 主库迁入历史
    			 * 备份迁入主库
    			 */
    			/**
            	 * 开启事务
            	 */
            	List<OneSql> sqls = new ArrayList<>();
    			TransactionObject txObj = new TransactionObject();
    			/*先清空原来的表，序号从新开始*/
    			sql = "truncate table fin_userreturnorderinfo";
            	log.info("sql = " + sql);
            	sqls.add(new OneSql(sql, -2, null, "vip_financial"));
            	
            	/*将tmp数据按顺序迁移到主表*/
            	sql = "insert into fin_userreturnorderinfo (userId, userName, profitTime, resetProfitTime, "
            		+ "expectProfitUsdt, staticProfitSumUsdt, returnType, batchNo, seqNo, dealFlag, createTime, investAmount, investAvergPrice) "
            		+ "select userId, userName, profitTime, resetProfitTime, "
            		+ "expectProfitUsdt, staticProfitSumUsdt, returnType, batchNo, seqNo, dealFlag, createTime, investAmount, investAvergPrice "
            		+ "from fin_userreturnorderinfotmp where batchNo = '" + batchNo + "' "
            		+ "order by returnType asc, resetProfitTime asc";
            	log.info("sql = " + sql);
            	sqls.add(new OneSql(sql, -2, null, "vip_financial"));
            	
            	/*更新序号*/
            	sql = "update fin_userreturnorderinfo set seqNo = id ";
            	log.info("sql = " + sql);
            	sqls.add(new OneSql(sql, -2, null, "vip_financial"));
            	
            	/*删除all表此期次的数据*/
            	sql = "delete from fin_userreturnorderinfoall where batchNo = '" + batchNo + "' ";
            	log.info("sql = " + sql);
            	sqls.add(new OneSql(sql, -2, null, "vip_financial"));
            	
            	/*将主表数据按顺序迁移到all表*/
            	sql = "insert into fin_userreturnorderinfoall (userId, userName, profitTime, resetProfitTime, "
            		+ "expectProfitUsdt, staticProfitSumUsdt, returnType, batchNo, seqNo, dealFlag, createTime, investAmount, investAvergPrice) "
            		+ "select userId, userName, profitTime, resetProfitTime, "
            		+ "expectProfitUsdt, staticProfitSumUsdt, returnType, batchNo, seqNo, dealFlag, createTime, investAmount, investAvergPrice "
            		+ "from fin_userreturnorderinfo where batchNo = '" + batchNo + "' "
            		+ "order by returnType asc, resetProfitTime asc";
            	log.info("sql = " + sql);
            	sqls.add(new OneSql(sql, -2, null, "vip_financial"));
            	
            	/*修改定时任务下一轮执行时间*/
    			SimpleDateFormat sdfNext = new SimpleDateFormat("yyyy-MM-dd");
    			String curDateNext = sdfNext.format(new Date());
    			/*获取7天后的时间*/
            	String nextDistStartDate = sdfNext.format(TimeUtil.getBeforeTime(7));
            	/*更新 taskTime 时间，即下一轮开启时间*/
            	String taskTime = nextDistStartDate + " 04:00:00";
            	/*askFlag, callStartDate, callEndDate*/
            	String nextCallStartDate = curDateNext + " 04:00:00";
            	String nextCallEndDate = nextDistStartDate + " 04:00:00";
            	
            	sql = "update fin_task set taskFlag = 1, handleTime = now(), taskTime = '" + taskTime + "', "
    				+ "callStartDate = '" + nextCallStartDate + "', callEndDate = '" + nextCallEndDate + "' "
    				+ "where taskName = 'ReturnUserOrderWork' ";
            	log.info("sql = " + sql);
            	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            	
            	txObj.excuteUpdateList(sqls);
                if (txObj.commit()) {
                	long endTime = System.currentTimeMillis();
            		if (showInfoFlag) {
            			log.info("理财报警REWARDTASK:【回本用户顺序列表生成】结束!!!【核算耗时：{" + (endTime - startTime) + "}】");
            		}
                } else {
                	log.info("理财报警REWARDERROR:【回本用户顺序列表生成】最后迁移异常");
                }
    		} catch (Exception e) {
    			// 记录任务日志(失败)
				updateTask(financialTask, startTime, 1);
    			log.info("理财报警REWARDERROR:【回本用户顺序列表生成】", e);
    		} finally {
            	workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDTASK:【回本用户顺序列表生成】上一轮分配任务尚未结束，本轮不需要进行");
        }
	}
	
	private void updateTask(FinancialTask financialTask, long startTime, int taskFlag) {
		TransactionObject txObj = new TransactionObject();
		List<OneSql> sqls = new ArrayList<>();
		String msg = "";
		if (taskFlag ==0) {
			msg = "动态奖金分配明细生成成功";
		}else {
			msg = "动态奖金分配明细生成失败";
		}
		/* 更新任务执行状态 , taskFlag = 0 */
		sql = "update fin_task set handleTime=now(),nowStep = 1, taskFlag = 0, taskError=" + taskFlag
				+ " where taskName='" + financialTask.getTaskName() + "' ";
		log.info("NewUserDetailWork sql = " + sql);
		sqls.add(new OneSql(sql, 1, null, "vip_financial"));
		/* 插入任务执行日志 */
		sql = "insert into fin_task_logs (taskName,taskType,taskTime,taskIndex,sumStep,nowStep,taskStartTime,taskEndTime,taskResult,resultInfo) "
				+ "values('" + financialTask.getTaskName() + "'," + financialTask.getTaskType() + ",'"
				+ financialTask.getTaskTime() + "'," + financialTask.getTaskIndex() + "," + financialTask.getSumStep()
				+ "," + financialTask.getSumStep() + ",from_unixtime(" + startTime + "),now()," + taskFlag + ",'"
				+ msg + "')";
		log.info("NewUserDetailWork sql = " + sql);
		sqls.add(new OneSql(sql, 1, null, "vip_financial"));
		txObj.excuteUpdateList(sqls);

		if (txObj.commit()) {
			log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新成功");
		} else {
			log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新失败");
		}
	}
	
	public static void main(String[] args) {
		ReturnUserOrderWork returnUserOrderWork = new ReturnUserOrderWork("", "");
		returnUserOrderWork.run();
	}

}
