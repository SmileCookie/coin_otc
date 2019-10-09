package com.world.model.usercap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.world.model.dao.task.Worker;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.model.entity.usercap.dao.UserCapMonitorDao;
import com.world.model.entity.usercap.entity.CommAttrBean;
import com.world.model.financial.dao.BillFinancialDao;
import com.world.model.financial.entity.BillFinancial;
import com.world.model.usercap.pool.UserCapPool;
import com.world.model.usercap.thread.UserCapDealThread;
import com.yc.entity.msg.Msg;
import com.yc.util.MsgUtil;

/**
 * <p>标题: 用户资金流水监控</p>
 * <p>描述: 用于监控用户资金流水</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class UserCapMonitor extends Worker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*sql语句*/
	private String sql = "";
	
	private static Logger log = Logger.getLogger(UserCapMonitor.class.getName());

	public UserCapMonitor(String name, String des) {
		super(name, des);
	}
	
	@Override
	public void run() {
		try {
			log.info("开始进行用户资金监控检查...");
			/*记录核算开始时间*/
			long startTime = System.currentTimeMillis();
			/*本次监控扫描的最大流水号，即扫描此流水号之前的所有未核对的记录*/
			/*获取当前时点bill表没有核对的流水的最大流水号,按照每个资金类型*/
			List<BillFinancial> listBills = giveCapMonBills();
			log.info("listBills.size = " + listBills.size());
			
			/*如果本次扫描没有执行完或者没有需要扫描核对的记录，该批次监控不执行*/
			if(UserCapPool.isEmpty() && UserCapPool.size() < 1 && null != listBills && listBills.size() > 0) {
				/*初始化记录个数*/
				UserCapPool.count = 0;
				
				/*添加每个资金类型需要核对的用户总数*/
				Map<Integer, Integer> mapFundsType = new HashMap<Integer, Integer>();
				/*存放资金类型的本次核对扫描的最大流水号*/
				Map<Integer, Long> mapCapLastBillId = new HashMap<Integer, Long>();
				/**
				 * 添加每个资金类型需要核对的用户总数
				 * 保存本资金类型的本次扫描核对的最大流水号
				 * 初始化正常用户数，异常用户数
				 */
				initFundsType2LastBillIdInfo(listBills, mapFundsType, mapCapLastBillId);
				log.info("mapFundsType = " + mapFundsType);
				
				/*将该批次的扫描信息存入表userCapMonitor*/
				/*记录检查的监控编号,更新扫描正常和失败用户数使用*/
				Map<Integer, String> mapUcmId = new HashMap<Integer, String>();
				saveUserCapMonitorInfos(mapFundsType, mapCapLastBillId, mapUcmId);
				
				/*创建一个可重用固定线程数的线程池*/
				ExecutorService userCapDealPool = Executors.newFixedThreadPool(60);
				/*循环执行线程进行扫描处理*/
				log.info("mapUcmId = " + mapUcmId);
				exeUserCapPool(mapCapLastBillId, mapUcmId, userCapDealPool);
				
				/*监控是否全部核对完成,更新资金类型对应的检查正确和异常的数量*/
				monCheckComplete(mapUcmId);
				
				/*关闭线程池*/
				userCapDealPool.shutdown();
				/*记录核算完成时间*/
				long endTime = System.currentTimeMillis();
				log.info("用户资金监控批次结束!!!【核算耗时：" + (endTime - startTime) + "】");
			} else {
				log.info("还剩余【" + UserCapPool.count + "个用户资金需要扫描,不启动新扫描任务!】");
			}
		} catch (Exception e) {
			log.error("【用户资金监控异常】", e);
		} finally {
			/*防止该批次没有正常结束*/
			UserCapPool.resetUserBillPool();
			log.info("【用户资金监控finally】");
		}
	}
	
	/**
	 * 获取当前时点bill表没有核对的流水的最大流水号,按照每个资金类型
	 * @return
	 */
	public List<BillFinancial> giveCapMonBills() throws Exception {
		/*获取当前时点bill表没有核对的流水的最大流水号*/
		BillFinancialDao bdDao = new BillFinancialDao();
		sql = " select fa.userId as userId, fa.fundsType as fundsType , fb.id as id from "
			+ "(select distinct userId, fundsType from bill_financial a order by fundsType) fa "
			+ "left join (select fundsType, max(id) id from bill_financial group by fundsType) fb on fa.fundsType = fb.fundsType where fa.userId = 1216832 and fa.fundstype = 51 ";
		log.info("sql = " + sql);
		/*保存到List中*/
		List<BillFinancial> listBills = null;
		bdDao.setDatabase("vip_main");
		//List<Bean> beanList = Data.Query("otc",sql,new Object[]{},BillFinancial.class);
		listBills = bdDao.find(sql, null, BillFinancial.class);
		
		return listBills;
	}
	
	/**
	 * 添加每个资金类型需要核对的用户总数
	 * 保存本资金类型的本次扫描核对的最大流水号
	 * 初始化正常用户数，异常用户数
	 * @param listBills
	 * @param mapFundsType
	 * @param mapCapLastBillId
	 */
	public void initFundsType2LastBillIdInfo(List<BillFinancial> listBills, Map<Integer, Integer> mapFundsType, Map<Integer, Long> mapCapLastBillId) {
		/*每个资金类型的本次核对扫描的最大流水号*/
		long capLastBillId = 0;
		/*资金类型*/
		int fundsType = 0;
		/*存放单条Bill记录的实体类*/
		BillFinancial billDetails;
		for (int i = 0; i< listBills.size(); i++) {
			billDetails = listBills.get(i);
			fundsType = billDetails.getFundsType();
//			log.info("fundsType = " + fundsType + ", userId = " + billDetails.getUserId() + ", =" + billDetails.getFundsType());
			/*存入用户资金监控池*/
			UserCapPool.addUserBill(billDetails);
			
			/*记录需要扫描的用户资金类型及其个数*/
			if (fundsType > 0 && mapFundsType.containsKey(fundsType)) {
				mapFundsType.put(fundsType, mapFundsType.get(fundsType) + 1);
			} else {
				/*添加资金类型的第一个个数*/
				mapFundsType.put(fundsType, 1);
				/*保存本资金类型的本次扫描核对的最大流水号*/
				capLastBillId = billDetails.getId();
				mapCapLastBillId.put(fundsType, capLastBillId);
				/*初始化正常用户数，异常用户数*/
				UserCapPool.initMapCheckResult(fundsType);
			}
		}
	}
	
	/**
	 * 将该批次的扫描信息存入表userCapMonitor
	 * @param mapFundsType
	 * @param mapCapLastBillId
	 */
	public void saveUserCapMonitorInfos(Map<Integer, Integer> mapFundsType, Map<Integer, Long> mapCapLastBillId, Map<Integer, String> mapUcmId) {
/*		Iterator<Entry<Integer, Integer>> iterFundsType = mapFundsType.entrySet().iterator();
		Entry<Integer, Integer> entryFundsType = null;
		int checkUserNum = 0;
		UserCapMonitorDao ucmDao = new UserCapMonitorDao();
		*//*新一轮的检查开始，先生成监控编号*//*
		String ucmId = "";
		*//*资金类型*//*
		int fundsType = 0;
		*//*每个资金类型的本次核对扫描的最大流水号*//*
		long capLastBillId = 0;
		while (iterFundsType.hasNext()) {
			entryFundsType = iterFundsType.next();
			fundsType = entryFundsType.getKey();
			checkUserNum = entryFundsType.getValue();
			
			*//*监控结束，监控数据保存到数据库中*//*
			ucmId = fundsType + "ucm" + System.currentTimeMillis();
			capLastBillId = mapCapLastBillId.get(fundsType);
			*//**
			 * 监控编号，监控时间，资金类型，检查用户总数，正常用户数，异常用户数，检查结果,0默认，1正常，2异常，处理备注，处理人编号,处理时间,最后一笔流水号bill
			 * capLastBillId 是某一个最后1笔资金类型的，其他的查找可<capLastBillId该资金类型的第一条即是
			 *//*
			sql = "insert into mon_userCapMonitor(ucmId, monTime, fundsType, checkUserNum, correctUserNum, errorUuserNum, "
				+ "checkResult, dealRemark, dealUserId, dealTime, billId) "
				+ "values ('" + ucmId + "', '" + TimeUtil.getNow() + "', " + fundsType + ", " + checkUserNum + ", "
				+ "'0', '0', '0', '', '0', null, " + capLastBillId + ") ";
			log.info("保存用户资金监控sql = " + sql);
			ucmDao.save(sql, null);
			*//*记录监控编号*//*
			mapUcmId.put(fundsType, ucmId);
		}*/
	}
	
	/**
	 * 循环执行线程进行扫描处理
	 * @param mapCapLastBillId
	 * @param mapUcmId
	 * @param userCapDealPool
	 */
	public void exeUserCapPool(Map<Integer, Long> mapCapLastBillId, Map<Integer, String> mapUcmId, ExecutorService userCapDealPool) {
		/*新一轮的检查开始，先生成监控编号*/
		String ucmId = "";
		/*资金类型*/
		int fundsType = 0;
		/*每个资金类型的本次核对扫描的最大流水号*/
		long capLastBillId = 0;
		/*循环执行线程进行扫描处理*/
		/*存放单条Bill记录的实体类*/
		BillFinancial billDetails;
		while (!UserCapPool.isEmpty()) {
			log.info("还剩余【" + UserCapPool.count + "】条用户资金需要扫描核对!");
			billDetails = UserCapPool.getUserBill();
			/*该资金类型的本次核对的最后一条流水号*/
			fundsType = billDetails.getFundsType();
			capLastBillId = mapCapLastBillId.get(fundsType);
			/*该资金类型的本次监控ID*/
			ucmId = mapUcmId.get(fundsType);
			/*开启线程*/
			UserCapDealThread userCapDealThread = new UserCapDealThread(billDetails, capLastBillId, ucmId);
			userCapDealPool.execute(userCapDealThread);

			/*try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				log.error(e.toString(), e);
			}*/
		}
	}
	
	/**
	 * 监控是否全部核对完成
	 * @param mapUcmId
	 */
	public void monCheckComplete(Map<Integer, String> mapUcmId) {
		/*新一轮的检查开始，先生成监控编号*/
		String ucmId = "";
		/*资金类型*/
		int fundsType = 0;
		/*数据库操作*/
		UserCapMonitorDao ucmDao = new UserCapMonitorDao();
		/*监控是否全部核对完成*/
		while (UserCapPool.size() >= 0) {
			log.info("UserCapPool.size() = " + UserCapPool.size());
			/*监控程序全部扫描完毕,更新每个资金类型的检查正确和异常的数量*/
			if(UserCapPool.size() == 0) {
				boolean msgFlag = false;
				Iterator<Entry<Integer, String>> iterMapUcmId = mapUcmId.entrySet().iterator();
				Entry<Integer, String> entryMapUcmId = null;
				int correctUserNum = 0, errorUuserNum = 0, checkResult = 1, sendErrorNum = 0; 
				while (iterMapUcmId.hasNext()) {
					entryMapUcmId = iterMapUcmId.next();
					
					/*获取资金类型和监控编号*/
					fundsType = entryMapUcmId.getKey();
					ucmId = entryMapUcmId.getValue();
					
					/*获取资金类型对应的检查正确和异常的数量*/
					correctUserNum = UserCapPool.mapCheckResult.get(fundsType + "cor");
					errorUuserNum = UserCapPool.mapCheckResult.get(fundsType + "err");
					if (errorUuserNum > 0) {
						/*检查结果异常*/
						checkResult = 2;
						/*发送邮件*/
						msgFlag = true;
						sendErrorNum = errorUuserNum;
					}
					
					/*更新表userCapMonitor*/
					sql = "update mon_userCapMonitor set correctUserNum = " + correctUserNum + ", errorUuserNum = " + errorUuserNum + ", "
						+ "checkResult = " + checkResult + " where ucmId = '" + ucmId + "' and fundsType = " + fundsType + "";
					log.info("sql = " + sql);
					ucmDao.update(sql, null);
				}
				/*有异常用户资金流水发送邮件，短信*/
				if(msgFlag) {
					/*发送邮件和短信报错监控信息*/
					sendErrorMsg(sendErrorNum);
				}
				break;
			}
			
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				log.error(e.toString(), e);
			}
		}
	}
	
	/**
	 * 用户资金监控报错，循环需要发送的邮件和手机信息,01邮件，02手机
	 * @param sendErrorNum
	 */
	public void sendErrorMsg(int sendErrorNum) {
		log.info("sendErrorMsgToEmail");
		/*数据库操作*/
		CommAttrDao caDao = new CommAttrDao();
		sql = "select * from comm_attr where attrType = '10000001' and attrState = 1 ";
		log.info("sql = " + sql);
		
		/*保存到List中*/
		List<CommAttrBean> listCommAttr = caDao.find(sql, null, CommAttrBean.class);
		CommAttrBean commAttrBean = new CommAttrBean();
		/*需要发送的邮箱地址*/
		String sendMailAddr = "", paraCode = "";
		for(int i = 0; i < listCommAttr.size(); i++) {
			commAttrBean = listCommAttr.get(i);
			sendMailAddr = commAttrBean.getParaValue();
			/*消息类型：01邮件，02手机*/
			paraCode = commAttrBean.getParaCode();
			log.info("10400001TASKZJJK【户资金监控】："+"用户资金监控报错!检测出有" + sendErrorNum + "个用户资金异常!");
			if("01".equals(paraCode)) {
				/*邮箱发送*/
				sendMail(sendMailAddr, sendErrorNum);
			} else {
				/*短信发送*/
				sendPhone(sendMailAddr, sendErrorNum);
			}
		}
	}
	
	/**
	 * 邮件发送
	 * @param sendMailAddr
	 * @param errorUserNum
	 */
	public void sendMail(String sendMailAddr, int errorUserNum) {
		Msg msg = new Msg();
		msg.setSysId(1);
		msg.setSendIp("127.0.0.1");
		msg.setUserId("0");
		msg.setUserName("0");
		msg.setTitle("用户资金监控");
		msg.setCont("用户资金监控报错!检测出有" + errorUserNum + "个用户资金异常!");
		msg.setReceiveEmail(sendMailAddr);
		msg.setSendUserName("0");
		MsgUtil.sendEmail(msg);
	}
	
	public void sendPhone(String sendPhoneNum, int errorUserNum) {
		Msg msg = new Msg();
		msg.setSysId(1);
		msg.setSendIp("127.0.0.1");
		msg.setUserId("0");
		msg.setUserName("0");
		msg.setTitle("用户资金监控");
		msg.setCont("用户资金监控报错!检测出有" + errorUserNum + "个用户资金异常!");
		msg.setReceivePhoneNumber("+86 " + sendPhoneNum);
		msg.setSendUserName("VIP");
		/*8是中文韩文日文等 ，3是英文*/
		msg.setCodec(8);
		MsgUtil.sendSms(msg);
	}
	
	public static void main(String[] args ) throws Exception {
		log.info("用户资金监控开始...");
		System.out.println("hello...");
		UserCapMonitor userCapMonitor = new UserCapMonitor("UserCapMonitor","用户资金监控");
		TestUserCapMonitor testUserCapMonitor = new TestUserCapMonitor(userCapMonitor);
		testUserCapMonitor.start();
		
//		
//		Thread.sleep(5000L);
//		/*监控间隔测试，10分钟没有执行完则再启动监控不需要执行*/
//		UserCapMonitor userCapMonitor2 = new UserCapMonitor();
//		TestUserCapMonitor testUserCapMonitor2 = new TestUserCapMonitor(userCapMonitor2);
//		testUserCapMonitor2.start();
		
		
//		Msg msg = new Msg();
//		msg.setSysId(1);
//		msg.setSendIp("127.0.0.1");
//		msg.setUserId("0");
//		msg.setUserName("0");
//		msg.setTitle("用户资金监控");
//		msg.setCont("用户资金监控报错!");
//		msg.setReceiveEmail("363545262@qq.com");
//		msg.setSendUserName("0");
//		MsgUtil.sendEmail(msg);
		
		
//		Msg m = new Msg();
//		m.setSysId(1);
//		m.setSendIp("127.0.0.1");
//		m.setUserId("0");
//		m.setUserName("0");
//		m.setTitle("用户资金监控");
//		m.setCont("用户资金监控报错!");
//		m.setReceivePhoneNumber("+86 " + "15269222008");
//		m.setSendUserName("VIP");
//		m.setCodec(8);////8是中文韩文日文等 ，3是英文
//		MsgUtil.sendSms(m);//
		

		
		log.info("用户资金监控结束...");
	}
}
