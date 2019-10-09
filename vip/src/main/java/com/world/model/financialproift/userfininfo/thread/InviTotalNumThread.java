package com.world.model.financialproift.userfininfo.thread;

import java.util.List;
import org.apache.log4j.Logger;
import com.redis.RedisUtil;
import com.world.data.mysql.Data;
import com.world.model.financialproift.userfininfo.pool.InviTotalNumPool;
public class InviTotalNumThread extends Thread {
	/*推荐码进行统计*/
	private String userId = "";
	private String invitationCode = "";
	/*sql语句*/
	private String sql = "";
	private static Logger log = Logger.getLogger(InviTotalNumThread.class.getName());
	
	public InviTotalNumThread (String userId, String invitationCode) {
		this.userId = userId;
		this.invitationCode = invitationCode;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			/*邀请统计SQL*/
			String invitationTotalNum = "0";
			sql = "select count(*) cnt from ( select t1.invitationCode, "
				+ "if(find_in_set(pInvitationCode, @pids) > 0, @pids := concat(@pids, ',', invitationCode), 1) as ischild "
				+ "from (select pInvitationCode, invitationCode from fin_userfinancialinfo t where t.authPayFlag = 2 ) t1, "
				+ "(select @pids := '" + invitationCode + "') t2 ) t3 where ischild != 1";
			log.info("InviTotalNumWork sql = " + sql);
			List<Integer> listInvitationCode = (List<Integer>) Data.GetOne("vip_financial", sql, null);
			log.info("listInvitationCode.get(0) = " + listInvitationCode.get(0));
			if (null != listInvitationCode) {
				invitationTotalNum = listInvitationCode.get(0) + "";
			}
			if (!"0".equals(invitationTotalNum)) {
				RedisUtil.set("financial_inviTotalNum_" + userId, invitationTotalNum, 0);
				log.info("financial_inviTotalNum_" + userId + " = " + RedisUtil.get("financial_inviTotalNum_" + userId));
				sql = "update fin_userfinancialinfo set invitationTotalNum = " + invitationTotalNum + ", modifyTime = now() where userId = " + userId;
				log.info("InviTotalNumWork sql = " + sql);
				Data.Update("vip_financial", sql, null);
				int intInvitationTotalNum = 0;
				try {
					intInvitationTotalNum = Integer.parseInt(invitationTotalNum);
				} catch (Exception e) {
					intInvitationTotalNum = 0;
				}
				if (intInvitationTotalNum >= 1000) {
					log.error("理财报警ERROR:邀请统计有达到超过1000的用户 = " + intInvitationTotalNum);
				}
			}
		} catch (Exception e) {
			log.error("理财报警ERROR:InviTotalNumThread", e);
		} finally {
			/*减少线程池中的统计个数*/
			InviTotalNumPool.subCount();
//			log.error("理财报警:InviTotalNumThread...finally");
		}
	}
}
