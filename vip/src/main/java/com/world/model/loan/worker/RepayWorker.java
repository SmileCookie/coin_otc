package com.world.model.loan.worker;

import java.util.Map;
import java.util.Map.Entry;

import com.world.data.database.DatabasesUtil;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.model.loan.dao.LoanRecordDao;

/****
 * 还息定时器，每天记录利息，还款时发息
 * @author apple
 *
 */
public class RepayWorker extends Worker{
	private static final long serialVersionUID = 1L;
	public RepayWorker(String name,String des){
		this.name = name;
		this.des = des;
	}
	LoanRecordDao loanRecordDao = new LoanRecordDao();
	@Override
	public void run() {
		super.run();
		//每日计息  
		loanRecordDao.repayInterest2016(1 , 10);
	}

	
}
