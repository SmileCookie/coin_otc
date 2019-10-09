package com.world.model.financialproift.worker;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.world.model.dao.financialproift.FinancialSuperNodeDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.SuperNode;
import com.world.model.financialproift.thread.SuperNodeThread;

/**
 * @author yeqing
 */
public class SuperNodeWork extends Worker {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(SuperNodeWork.class);

    FinancialSuperNodeDao financialSuperNodeDao = new FinancialSuperNodeDao();
    /*此轮定时任务结束标识*/
    private static boolean workFlag = true;

    public SuperNodeWork(String name, String des) {
        super(name, des);
    }
    
    @Override
    public void run() {
        /*记录核算开始时间*/
        long startTime = System.currentTimeMillis();
        /*现在时间获取*/
        log.info("理财报警AINFO:【同步超级节点信息】开始,startTime={}", new Date());
        try {
        	if (workFlag) {
            	workFlag = false;
            	try {
            		/**
            		 * 
            		 * 查询SQL	fin_supernode where sNodeState = 1
            		 * redis 	存放 sum(MiningAmount) fin_supernode_mining_detail	sNodeState = 1
            		 * 
            		 * 页面查询 	fin_supernode
            		 */
                    List<SuperNode> superNodeList = financialSuperNodeDao.selectAvialableNode();
                    /*线程池*/
                    ExecutorService executorSuperNode = Executors.newFixedThreadPool(10);
                    if (superNodeList != null && superNodeList.size() > 0) {
                    	CountDownLatch countDownLatch = new CountDownLatch(superNodeList.size());
                        for (int i = 0; i < superNodeList.size(); i++) {
                            SuperNode superNode = superNodeList.get(i);
                            SuperNodeThread superNodeThread = new SuperNodeThread(superNode, countDownLatch);
                            executorSuperNode.execute(superNodeThread);
                        }
                        countDownLatch.await();
                    }
                    executorSuperNode.shutdown();
                  //所有超级节点的余额都更新完成后，再将节点数量和节点累加总额更新到Redis
                    financialSuperNodeDao.updateSumNodeInfoToRedis();
                    long endTime = System.currentTimeMillis();
                    log.info("理财报警INFO:【同步超级节点信息】结束!!!【核算耗时：{}】", (endTime - startTime));
                } catch (Exception e) {
                	log.info("理财报警ERROR:SuperNodeWork", e);
                } finally {
                	workFlag = true;
                }
            } else {
            	log.info("理财报警WARN：超级节点收益统计，上一轮尚未结束!");
            }
        } catch (Exception e) {
        	log.info("理财报警ERROR：超级节点收益统计错误 SuperNodeWork ", e);
        } finally {
        	workFlag = true;
        }
        
    }


    public static void main(String[] args) throws InterruptedException {

        /**
         * 后续
         * 1、在main.properties里配置定时任务开关
         * task0 SuperNodeWork=true
         * task1 SuperNodeWork=false
         * 2、在messi\vip_conf\src\main\resources\mysql.json 配置vip_main的连接
         */
        SuperNodeWork superNodeWork = new SuperNodeWork("SuperNodeWork", "【同步超级节点信息】");
        superNodeWork.run();
//        SuperNodeWork s2 = new SuperNodeWork("SuperNodeWork", "【同步超级节点信息】");
//        s2.run();
//        s2.run();
//    	TestFinProfit tfp = new TestFinProfit();
//		tfp.start();
//		try {
//			Thread.sleep(1000 * 10L);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		TestFinProfit tfp2 = new TestFinProfit();
//		tfp2.start();
    	
    }

}
