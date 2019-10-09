package com.world.model.loan.worker;

import com.world.model.dao.task.Worker;
import com.world.model.loan.dao.LoanRecordDao;

public class ForceRepayWorker extends Worker{
	private static final long serialVersionUID = 1L;
	public ForceRepayWorker(String name,String des){
		this.name = name;
		this.des = des;
	}
	LoanRecordDao loanRecordDao = new LoanRecordDao();
	@Override
	public void run() {
		super.run();
		log.info("##强制还款##为需要自动还款的的记录还款。");
		//每日计息
		loanRecordDao.autoRepay(null);
	}
}
