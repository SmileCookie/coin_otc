package com.world.model.financialproift.userfininfo.thread;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.world.data.mysql.Data;
import com.world.model.entity.financialproift.UserFinancialInfo;

public class ReturnUserCapitalPayThread extends Thread {
	/*sql语句*/
	private String sql = "";
	private static Logger log = Logger.getLogger(ReturnUserCapitalPayThread.class.getName());
	private CountDownLatch countDownLatch;
	private String invitationUserName;
	private String batchNo;
	
	public ReturnUserCapitalPayThread (CountDownLatch countDownLatch, String invitationUserName, String batchNo) {
		this.countDownLatch = countDownLatch;
		this.invitationUserName = invitationUserName;
		this.batchNo = batchNo;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			
		} catch (Exception e) {
			log.info("理财报警REWARDERROR:【回本用户顺序列表生成】", e);
		} finally {
    		countDownLatch.countDown();
    	}
	}
}
