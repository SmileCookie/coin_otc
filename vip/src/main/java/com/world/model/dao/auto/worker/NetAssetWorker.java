package com.world.model.dao.auto.worker;

import com.world.model.dao.jifen.JifenDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.task.Worker;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.level.Jifen;
import com.world.model.entity.level.JifenType;
import com.world.model.entity.user.User;
import com.world.model.service.BrushAccountService;
import com.world.util.date.TimeUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 净资产积分结算定时器, 每天0点后结算一次
 */
@SuppressWarnings("serial")
public class NetAssetWorker extends Worker {
	
	UserDao userDao = new UserDao();
	PayUserDao payUserDao = new PayUserDao();
	JifenDao jifenDao = new JifenDao();
    BrushAccountService brushAccountService = new BrushAccountService();
	
//	上次更新时间默认为当天,即明天凌晨执行汇总; 如果要改为前天:TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(1));
	private Timestamp LastUpdateTime =TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));
//	private Timestamp LastUpdateTime =TimeUtil.getTodayFirst();
	
	boolean isDebug = false;
	
	public NetAssetWorker(String name, String des) {
		super(name, des);
	}

	@Override
	public void run() {
		
		try{
			super.run();
			//时间控制
			Timestamp today  = TimeUtil.getTodayFirst();
			if(today.compareTo(LastUpdateTime)>0 ){
				long beginTime = TimeUtil.getNow().getTime();
				long count = userDao.count(userDao.getQuery());
				for (int i = 1; i <= count/100+1; i++) {
					doMethod(i,100);//每次处理100个用户
					if(isDebug){
						break;
					}
				}
				
				long endTime = TimeUtil.getNow().getTime();
				log.info("\n ===============净资产积分结算定时器执行结果, 用户总数: " + count + ",实际处理用户数:" + handledUserCount + ", 处理时间: " + (endTime-beginTime) + "ms" );
				
				LastUpdateTime = TimeUtil.getTodayFirst();
				handledUserCount = 0;
			}
		}catch(Exception e){
			log.error(e.toString(), e);
		}
		
	}

	int handledUserCount = 0;
	private void doMethod(int index, int pageSize) {
		try {
			List<User> users;
			if(isDebug){
				users = userDao.findPage(userDao.getQuery().filter("userName <>", null).order("-lastLoginTime"), 0, 10);
			}else{
				users = userDao.findPage(userDao.getQuery().filter("userName <>", null), index, pageSize);
			}
			
			for (User user : users) {
                //刷量账号不累计积分 add by buxianguan 20180321
                boolean isBrushAccount = brushAccountService.isBrushAccount(user.getId());
                if (isBrushAccount) {
                    continue;
                }

				Jifen jifenObj = jifenDao.findByType(JifenType.netAsset, user.getId());
				
				//计算实际的净资产
				BigDecimal btcNetAssest = UserCache.getUserBTCAssest(user.getId());
				
				if(jifenObj==null && btcNetAssest.compareTo(BigDecimal.ZERO)>0){
					jifenDao.updateJifen(JifenType.netAsset, user.getId(), btcNetAssest);
					handledUserCount++;
				}else{
					if(isDebug){
						if(jifenObj!=null){
							log.info("用户资产: 用户ID:" +  user.getId() + ", 用户名:" + user.getRealName()+ ", 已添加!");
						}
						if(btcNetAssest.compareTo(BigDecimal.ZERO)<=0){
							log.info("用户资产: 用户ID:" + user.getId() + ", 用户名:" + user.getRealName() + ", 无资产!");
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

}
