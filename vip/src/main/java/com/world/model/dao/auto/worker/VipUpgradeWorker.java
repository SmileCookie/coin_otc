package com.world.model.dao.auto.worker;

import com.google.code.morphia.query.UpdateResults;
import com.world.cache.Cache;
import com.world.model.dao.jifen.JifenDao;
import com.world.model.dao.level.UserVipLevelDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.task.Worker;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.user.User;
import com.world.web.sso.SSOLoginManager;

import java.math.BigDecimal;
import java.util.List;

/**
 * vip的升降级(定时器)
 * vip1~9,当分数达到下一等级即更新等级
 */
@SuppressWarnings("serial")
public class VipUpgradeWorker extends Worker {
	private static final long Day = 24*60*60*1000;
	private static final long days = 2;

	UserDao userDao = new UserDao();
	PayUserDao payUserDao = new PayUserDao();
	JifenDao jifenDao = new JifenDao();
	UserVipLevelDao userVipLevelDao = new UserVipLevelDao();
	public VipUpgradeWorker(String name, String des) {
		super(name, des);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
        try {
            super.run();
            log.info("vip的升降级(定时器)开始");
            /**
             * vip1-9,当分数达到下一等级即更新等级
             */
            long count = userDao.count(userDao.getQuery().filter("totalJifen > ", 0));

            int pageSize = 1000;
            long totalIndex = (count % pageSize == 0 ? count / pageSize : count / pageSize + 1);

            for (int i = 1; i <= totalIndex; i++) {
                upgrade(i, pageSize);
            }

            if(updateCount+errCount>0){
                log.info("\n*********vip升级程序处理结果,成功:" + updateCount + ", 失败" + errCount);
            }else{
                log.info("\n*********vip升级程序处理结果:没有需要升级的用户");
            }

            updateCount = 0;
            errCount = 0;
        } catch (Exception e) {
            log.error("vip的升降级(定时器)异常", e);
        }

    }

	Integer updateCount = 0;
	Integer errCount = 0;

	private void upgrade(int index, int pageSize) {
        try {
            List<User>  users = userDao.findPage(userDao.getQuery().filter("totalJifen > ", 0).order("_id"), index, pageSize);
            for (User user : users) {
                try {
                    BigDecimal totalJifen = BigDecimal.valueOf(user.getTotalJifen());
//                    VipRate oldVip = (VipRate)EnumUtils.getEnumByKey(user.getVipRate(), VipRate.class);
                    int oldVip = user.getVipRate();
                    if(totalJifen.compareTo(BigDecimal.ZERO)<0){
                        totalJifen = BigDecimal.ZERO;
                        return;
                    }
                    BigDecimal feeDiscount;

                    int newVip = userVipLevelDao.getVipRateByJiFenUpd(totalJifen);

//			if (oldVip != VipRate.vip9) {
//				if (newVip != oldVip) {
//					// 需要upgrade
//					feeDiscount = userVipLevelDao.getDiscountByVipRate(newVip.getId());
//
//					user.setVipRate(newVip.getId());
//					UpdateResults<User> ur = userDao.updateUserVipRate(user);
//					if (ur.getUpdatedCount() > 0) {
//						// 更新内存里,cookie里的vip等级
//						SSOLoginManager.updateVip(user.getId() + "", newVip.getId());
//						//缓存用户的手续费折扣率  为计算手续费准备
//						Cache.SetObj("user_vip_fee_discount_"+user.getId(), feeDiscount);
//						updateCount++;
//					}else{
//						errCount++;
//					}
//				}
//			}

                    //modify by xwz 20170918，清空memcached后重新更新缓存
                    if (newVip != oldVip || null == Cache.GetObj("user_vip_fee_discount_"+user.getId())) {
                        feeDiscount = userVipLevelDao.getDiscountByVipRate(newVip);
                        user.setVipRate(newVip);
                        UpdateResults<User> ur = userDao.updateUserVipRate(user);
                        if (ur.getUpdatedCount() > 0) {
                            // 更新内存里,cookie里的vip等级
                            SSOLoginManager.updateVip(user.getId() + "", newVip);
                            //缓存用户的手续费折扣率  为计算手续费准备
                            Cache.SetObj("user_vip_fee_discount_"+user.getId(), feeDiscount);
                            updateCount++;
                        }else{
                            errCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("vip的升降级(定时器)异常，userId:"+user.getId() , e);
                }
            }
        } catch (Exception e) {
            log.error("vip的升降级(定时器)异常", e);
        }

    }
}
