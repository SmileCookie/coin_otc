package com.world.model.financialproift.userfininfo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.UserFinancialInfo;
import com.world.model.financialproift.userfininfo.pool.InviTotalNumPool;
import com.world.model.financialproift.userfininfo.thread.InviTotalNumThread;

public class InviTotalNumWork extends Worker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*查询SQL*/
	private String sql = "";
	public InviTotalNumWork(String name, String des) {
		super(name, des);
	}
	
	@Override
	public void run() {
		try {
			log.info("理财报警AINFO:开始统计理财邀请人数..." + InviTotalNumPool.count);
			long startTime = System.currentTimeMillis();
			/*查询已支付的用户 0默认值，1已认证(已保存)，2已支付*/
			sql = "select userId, invitationCode from fin_userfinancialinfo where authPayFlag = 2 and pInvitationCode != invitationCode";
			log.info("InviTotalNumWork sql = " + sql);
			/*保存到List中*/
//			Data.Query(connGroupName, progrom, param, bean);
//			listUserFinancialInfo = userFinancialInfoDao.find(sql, null, BillDetails.class);
			
			List<Bean> listUserFinancialInfo = (List<Bean>) Data.Query("vip_financial", sql, null, UserFinancialInfo.class);
			int totalUser = listUserFinancialInfo.size();
			/*推荐码进行统计*/
			String userId = "";
			String invitationCode = "";
			/*如果本次扫描没有执行完或者没有需要扫描核对的记录，该批次监控不执行*/
			log.info("InviTotalNumPool.count = " + InviTotalNumPool.count);
			if (InviTotalNumPool.count < 1 && null != listUserFinancialInfo && listUserFinancialInfo.size() > 0) {
				/*初始化记录个数*/
				InviTotalNumPool.count = 0;
				log.info("理财报警AINFO：需要进行理财邀请人数统计的用户数 = " + listUserFinancialInfo.size());
				
				/*先添加到总计算资源监控池*/
				for (int i = 0; i < listUserFinancialInfo.size(); i++) {
					InviTotalNumPool.addUserFinancialInfo((UserFinancialInfo) listUserFinancialInfo.get(i));
				}
				UserFinancialInfo userFinancialInfo;
				/*创建一个可重用固定线程数的线程池*/
				ExecutorService userCapDealPool = Executors.newFixedThreadPool(10);
				while (!InviTotalNumPool.isEmpty()) {
//					log.info("还剩余【" + InviTotalNumPool.count + "】条用户需要统计!");
					userFinancialInfo = InviTotalNumPool.getUserFinancialInfo();
					/*获取需要的参数*/
					userId = userFinancialInfo.getUserId() + "";
					invitationCode = userFinancialInfo.getInvitationCode();
					/*开启线程*/
					InviTotalNumThread inviTotalNumThread = new InviTotalNumThread(userId, invitationCode);
					userCapDealPool.execute(inviTotalNumThread);
				}
				
				while (InviTotalNumPool.count > 0) {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						log.info(e.toString(), e);
					}
				}
				/*关闭线程池*/
				userCapDealPool.shutdown();
				/*单独统计根节点*/
				/**
				 * update userfinancialinfo2 a inner join (
				 * select 1 userid, count(*) cnt from userfinancialinfo2 where authPayFlag = 2 and pInvitationCode != invitationCode) b 
				 * on a.userid = b.userid set invitationTotalNum = b.cnt;
				 */
				sql = "update fin_userfinancialinfo a inner join ( "
					+ "select 1206683 userid, count(*) cnt from fin_userfinancialinfo where authPayFlag = 2 and pInvitationCode != invitationCode) b  "
					+ "on a.userid = b.userid set invitationTotalNum = b.cnt";
				Data.Update("vip_financial", sql, null);
			} else {
				log.info("理财报警WARN：上一轮尚未结束，还有【" + InviTotalNumPool.count + "】个用户需要进行理财邀请人数统计！");
			}
			long endTime = System.currentTimeMillis();
			log.info("理财报警INFO:理财邀请人数【" + totalUser + "】统计总耗时：【" + (endTime - startTime) + "】");
		} catch (Exception e) {
			log.info("理财报警ERROR:InviTotalNumWork", e);
		} finally {
			/*防止该批次没有正常结束*/
			InviTotalNumPool.count = 0;
			InviTotalNumPool.resetInviTotalNumPool();
//			log.info("理财报警:InviTotalNumWork...finally");
		}
		
	}
	
	public static void main (String[] args) {
		
		InviTotalNumWork inviTotalNumWork = new InviTotalNumWork("", "");
		inviTotalNumWork.run();
		
//		TestFinProfit tfp = new TestFinProfit();
//		tfp.start();
//		try {
//			Thread.sleep(1000 * 5L);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		TestFinProfit tfp2 = new TestFinProfit();
//		tfp2.start();
		
		
//		TestFinProfit tfp2 = new TestFinProfit();
//		tfp2.start();
		
		
//		RedisUtil.set("financial_inviTotalNum_FFFA", "AF", 0);
//		System.out.println(RedisUtil.get("financial_inviTotalNum_FFFA"));
//		System.out.println(RedisUtil.get("financial_inviTotalNum__1"));
		
	}
	

}
