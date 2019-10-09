package com.world.model.loan.dao;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.loan.entity.CheckInfo;

public class CheckInfoDao extends DataDaoSupport<CheckInfo> {

	private static final long serialVersionUID = 1L;

	// 一天限制次数
	private final static int LIMIT_TIMES = 3;
	// 发送间隔时间
	private final static long INTERVAL_TIME = 2 * 60 * 60 * 1000; // 2小时
	private final static long INTERVAL_TIME_8_HRS = 8 * 60 * 60 * 1000; // 8小时

	// private final static long INTERVAL_TIME = 5*60*1000; //2小时

	private UserDao userDao = new UserDao();
	
	/**
	 * 增加一条记录
	 * 
	 * @param checkInfo
	 * @param smsContent
	 *            如果不能发送短信，可以设置为"".
	 * @throws Exception
	 */
	public boolean addCheckInfo(CheckInfo checkInfo, String smsContent) throws Exception {
		// 判断是否需要发短信，获取当天0点为开始时间，每间隔两小时才能发短信，24小时内只能发送3次.
		Long[] today0HAnd24H = getToday0HAnd24H();
		Timestamp today0H = new Timestamp(today0HAnd24H[0].longValue());
		Timestamp today24H = new Timestamp(today0HAnd24H[1].longValue());

		// 如果系统提示需要发送短信，进行检测是否能发送短信
		if (checkInfo.getSendSms() == 1) {
			boolean isSendSms = false;
			Query q = super.getQuery();
			// 获取当前用户当天24小时内已发送的短信的信息来判断
			q.setSql("select * from checkinfo where (addTime between ? and ?) and userId=? and sendSms=? order by id desc").setParams(new Object[] { today0H, today24H, checkInfo.getUserId(), 1 }).setCls(CheckInfo.class);

			List<CheckInfo> list = super.find();

			if (list.size() < LIMIT_TIMES && list.size() > 0) {
				// 最新的一条记录
				CheckInfo info = (CheckInfo) list.get(0);
				// 第2次发送，判断是否在两小时后
				if (list.size() == 2 && System.currentTimeMillis() - info.getAddTime().getTime() > INTERVAL_TIME) {
					isSendSms = true;
				}
				// 第1次发送，判断是否8小时候
				if (list.size() == 1 && System.currentTimeMillis() - info.getAddTime().getTime() > INTERVAL_TIME_8_HRS) {
					isSendSms = true;
				}
			}
			// 不存在sendsms=1的记录，可以发送短信
			if (list.size() == 0) {
				isSendSms = true;
			}
			if (isSendSms) {
				// 通过api调用发送信息接口
				int rel = sendMsg(checkInfo.getUserId(), smsContent);
				if (rel != 1) {
					// 本次发送不成功，把发送短信状态设为0
					checkInfo.setSendSms(0);
				}
			} else {
				// 把发送短信状态设为0
				checkInfo.setSendSms(0);
			}

			if (isSendSms && checkInfo.getSendSms() == 1) {
				List<OneSql> sqls = new ArrayList<OneSql>();
				sqls.add(super.getTransInsertSql(checkInfo));
				return Data.doTrans(sqls);
			}

		}
		return true;
	}

	/**
	 * 获取当前0时与24时毫秒数
	 * 
	 * @return
	 */
	public Long[] getToday0HAnd24H() {
		try {
			// 当天日期
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			// 当天0时
			String date0H = date + " 00:00:01";
			String date24H = date + " 23:59:59";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d1 = format.parse(date0H);
			Date d2 = format.parse(date24H);
			return new Long[] { d1.getTime(), d2.getTime() };
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		return null;
	}
	
	private int sendMsg(String userId, String content){
		MobileDao mDao = new MobileDao();
		User user = userDao.get(userId);
		UserContact uc = user.getUserContact();
		if(StringUtils.isNotEmpty(uc.getSafeMobile())){
			boolean due = mDao.sendSms(user, "", "借贷提醒", content, uc.getSafeMobile());
			if(due){
				return 1;
			}else{
				return 2;
			}
		}else{
			return 0;//未认证
		}
	} 
}
