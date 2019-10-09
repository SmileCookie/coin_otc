package com.world.model.loan.worker;

import java.util.List;

import com.world.model.dao.task.Worker;
import com.world.model.loan.dao.RepayOfQiDao;
import com.world.model.loan.dao.RevenuedayDao;
import com.world.model.loan.entity.RepayOfQi;

/**
 * @author Administrator Day收益定时器，根据repayofqi字段dealstruts（0不处理,1未收集,2已收集）判断
 */
@SuppressWarnings("serial")
public class RevenuedayWork extends Worker {

	RevenuedayDao reveDao = new RevenuedayDao();
	RepayOfQiDao reDao = new RepayOfQiDao();

	public RevenuedayWork(String name, String des) {
		super(name, des);
	}

	@Override
	public void run() {
		revenue();
	}

	@SuppressWarnings("unchecked")
	public void revenue() {
		try {
			log.info("进入定时器RevenuedayWork，开始执行！");
			List<RepayOfQi> listrepay = reDao.find("SELECT * FROM repayofqi WHERE dealstatus=?", new Object[] { 1 }, RepayOfQi.class);
			for (RepayOfQi repay : listrepay) {
				log.info("未处理收集信息有：" + repay.getOutUserId());
				// 标识为1 时，进入revenudayDao。,并把投资收取利息的用户ID传过去
				if (repay.getDealStatus() == 1) {
					reveDao.isExist(repay.getOutUserId(), repay);// 进入RevenuedayDao执行
				}
			}
		} catch (Exception e) {
			log.error("定时器已发生错误！正在重新", e);
			return;
		}
		log.info("执行完毕，退出定时器RevenuedayWork！");
	}
}
