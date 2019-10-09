package com.world.model.financialproift.userfininfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinUserRewardStatus;
import com.world.model.entity.financialproift.FinancialTask;

/**
 * @Author Ethan
 * @Date 2019-08-05 14:13
 * @Description 生成超级节点用户收益奖励明细
 **/

public class SuperNodeRewardDetailWork extends Worker {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sql = "";
    /**
     * 此轮定时任务结束标识
     */
    private static boolean workFlag = true;
    /*展示批量耗时的标识*/
    private boolean showInfoFlag = false;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;

    public SuperNodeRewardDetailWork(String name, String des) {
        super(name, des);
    }

    @SuppressWarnings("rawtypes")
	@Override
    public void run() {
    	if (intLogInfoFlag == 10) {
    		log.info("理财报警REWARDINFO:【超级节点-用户奖励收益明细-生成】开始");
    	}
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        boolean taskflag = false;
        showInfoFlag = false;
        try {
            /**
             * 取得类名称，作为任务名称
             * 超级节点-用户奖励收益明细-生成
             */
            sql = "select * from fin_task where taskName = 'SuperNodeRewardDetailWork' and taskTime <= NOW() and taskFlag = 1  ";
            log.info("SuperNodeRewardDetailWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
            if (null == financialTask) {
            	if (intLogInfoFlag == 10) {
            		log.info("理财报警REWARDINFO:【超级节点-用户奖励收益明细-生成】没有需要执行的任务");
            	}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【超级节点-用户奖励收益明细-生成】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
        } catch (Exception e1) {
            financialTask = new FinancialTask();
            financialTask.setTaskName("SuperNodeRewardDetailWork");
            log.info("理财报警REWARDERROR:【超级节点-用户奖励收益明细-生成】定时任务启动失败,失败原因:", e1);
        } finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
        if (workFlag && taskflag) {
            workFlag = false;
            try {
                log.info("超级节点-用户奖励收益明细-生成，定时任务开始...");
                //1.获取奖励记录
                sql = "select * from fin_user_reward_status where distType = 5 and distFlag = 1 and distStatus = 0 "
                	+ "and distTime < now() order by id asc limit 1";
                FinUserRewardStatus userRewardOne = (FinUserRewardStatus) Data.GetOne("vip_financial", sql, null, FinUserRewardStatus.class);
//                log.info("超级节点-用户奖励收益明细-生成信息:" + JSON.toJSONString(userRewardOne));
                if (userRewardOne != null) {
                    //2.获取udst价格   Cache.Get("vds_usdt_l_price")
                    String vdsUsdt = Cache.Get("vds_usdt_l_price");
                    BigDecimal bdVdsUsdt = BigDecimal.ZERO;
                    if (StringUtils.isEmpty(vdsUsdt)) {
                        log.info("理财报警REWARDERROR:Cache获取vdsUsdt价格异常 = " + vdsUsdt);
                        return;
                    } else {
                        try {
                            bdVdsUsdt = new BigDecimal(vdsUsdt);
                        } catch (Exception e) {
                            log.info("理财报警REWARDERROR:Cache获取vdsUsdt价格异常 = " + vdsUsdt);
                            return;
                        }
                        if (bdVdsUsdt.compareTo(BigDecimal.ZERO) <= 0) {
                            log.info("理财报警REWARDERROR:Cache获取vdsUsdt价格异常小于0 = " + vdsUsdt);
                            return;
                        }
                    }
                    //3.获取本次分配收益金额
                    BigDecimal profitAmount = userRewardOne.getDistBal();
                    BigDecimal usdtAmount = profitAmount.multiply(bdVdsUsdt).setScale(4, BigDecimal.ROUND_DOWN);
                    int superNodeProfitCount = userRewardOne.getSuperNodeProfitCount();
                    //4.获取满足条件的收益分配用户
                    sql = "select count(*) from fin_userfinancialinfo where authPayFlag = 2 and matrixLevel = 6 ";
                    List usersizeList = (List) Data.GetOne("vip_financial", sql, null);
                    int usersize = Integer.valueOf(String.valueOf(usersizeList.get(0)));
                    if (usersize > 0) {
                    	log.info("理财报警REWARDTASK:【超级节点-用户奖励收益明细-生成】定时任务可以继续执行,本次分配总人数 = " + usersize);
                        try {
                            TransactionObject trans = new TransactionObject();
                            List<OneSql> sqls = new ArrayList<>();
                            //批量插入收益分配明细记录
                            sql = "insert into fin_profit_supernode_detail "
                                + " ( profitUserId, profitUserName, profitType, profitAmount, usdtPrice, usdtAmount, superNodeProfitCount, "
                                + "superNodePfofitAmount, superNodeProfitVipWeight, createtime, parentid, flag ) "
                                + " select userid, username, 5, truncate((vipweight/b.sumweight) * " + profitAmount + ", 4) as profitAmount, "
                                + "" + bdVdsUsdt.toPlainString() + ", "
                                + " truncate((vipweight/b.sumweight) * " + usdtAmount + ",4) usdtAmount, " + superNodeProfitCount + ", "
                                + "" + profitAmount.toPlainString() + ", b.sumweight, now(), " + userRewardOne.getSeqNo() + ", 0 "
                                + " from fin_userfinancialinfo a "
                                + " inner join "
                                + " (select sum(vipweight) sumweight from fin_userfinancialinfo where authPayFlag = 2 "
                                + "and matrixLevel=6 ) b  "
                                + "where authPayFlag = 2 and matrixLevel = 6 ";
                            
                            log.info("SuperNodeRewardDetailWork sql = " + sql);
                            sqls.add(new OneSql(sql, usersize, null, "vip_financial"));
                            
                            /* 更新fin_supernode_profit 状态标识=1 usdt当时价格 收益用户数*/
                            sql = "update fin_user_reward_status set distStatus = 1, distFlag = 0, usdtPrice = " + bdVdsUsdt + ", "
                            	+ "distNum = " + usersize + ", updateTime = now() where id = " + userRewardOne.getId();
                            log.info("SuperNodeRewardDetailWork sql = " + sql);
                            sqls.add(new OneSql(sql, 1, null, "vip_financial"));

                            trans.excuteUpdateList(sqls);
                            //事务提交
                            if (trans.commit()) {
                                log.info("理财报警REWARDINFO: 任务【超级节点-用户奖励收益明细-生成】执行收益用户明细添加成功，超级节点分配记录状态更新成功");
                            } else {
                                log.info("理财报警REWARDERROR: 任务【超级节点-用户奖励收益明细-生成】执行收益用户明细添加失败，超级节点分配记录状态更新失败");
                            }
                            showInfoFlag = true;
                        } catch (Exception e) {
                            log.info("理财报警REWARDERROR:【超级节点-用户奖励收益明细-生成】插入奖励明细发生非受控异常,异常信息:{}", e);
                        }
                    } else {
                    	if (intLogInfoFlag == 10) {
                    		log.info("理财报警:【超级节点-用户奖励收益明细-生成】定时任务可以继续执行,本次没有需要分配的人员 sql = " + sql);
                    	}
                    	return;
                    }
                } else {
                	if (intLogInfoFlag == 10) {
                		log.info("理财报警REWARDINFO:【超级节点-用户奖励收益明细-生成】,本次没有需要执行的分配资金记录任务 = " + sql);
                	}
                	return;
                }
                if (intLogInfoFlag == 10) {
                	updateTask(financialTask, startTime, 0, 0);
                }
                long endTime = System.currentTimeMillis();
                if (showInfoFlag) {
                	log.info("理财报警REWARDTASK:【超级节点-用户奖励收益明细-生成】... 耗时：" + (endTime - startTime));
                }
            } catch (Exception e) {
                log.info("理财报警REWARDERROR:【超级节点-用户奖励收益明细-生成】发生不控异常,异常信息", e);
                updateTask(financialTask, startTime, 1, 1);
            } finally {
                workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDTASK:【超级节点-用户奖励收益明细-生成】,上一轮定时任务没有结束，本轮不需要执行");
        }

    }

    private void updateTask(FinancialTask financialTask, long startTime, int taskFlag, int taskError) {
        TransactionObject txObj = new TransactionObject();
        List<OneSql> sqls = new ArrayList<>();

        String taskInfo = "执行成功";
        if (taskFlag != 0) {
            taskInfo = "执行失败，请查看日志记录";
        }
        /* 更新任务执行状态 */
        sql = "update fin_task set handleTime = now(),nowStep = 1,taskError = " + taskError + ", taskFlag = " + taskFlag + " "
        	+ "where taskName = '" + financialTask.getTaskName() + "' ";
        log.info("SuperNodeRewardDetailWork sql = " + sql);
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        /* 插入任务执行日志 */
        sql = "insert into fin_task_logs (taskName,taskType,taskTime,taskIndex,sumStep,nowStep,taskStartTime,taskEndTime,taskResult,resultInfo) "
                + "values('" + financialTask.getTaskName() + "'," + financialTask.getTaskType() + ",'"
                + financialTask.getTaskTime() + "'," + financialTask.getTaskIndex() + "," + financialTask.getSumStep()
                + "," + financialTask.getSumStep() + ",from_unixtime(" + startTime + "),now()," + taskFlag + ",'"
                + taskInfo + "')";
        log.info("SuperNodeRewardDetailWork sql = " + sql);
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        txObj.excuteUpdateList(sqls);
        
        if (txObj.commit()) {
            log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新成功");
        } else {
            log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新失败");
        }
    }

    public static void main(String[] args) {
    	SuperNodeRewardDetailWork sndw = new SuperNodeRewardDetailWork("", "");
    	sndw.run();
//    	FinThreadTest ft = new FinThreadTest();
//    	ft.start();
//    	FinThreadTest ft2 = new FinThreadTest();
//    	ft2.start();
    }
}
