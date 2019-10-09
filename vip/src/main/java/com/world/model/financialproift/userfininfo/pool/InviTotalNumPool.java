package com.world.model.financialproift.userfininfo.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.financialproift.UserFinancialInfo;


/**
 * <p>标题: 推进统计监控池</p>
 * <p>描述: 储需要统计的用户信息</p>
 */
public class InviTotalNumPool {
	/* 同步存储，存放需要扫描核对的用户id */
	private static Vector<UserFinancialInfo> inviTotalNumPool = new Vector<UserFinancialInfo>();
	
	/*存放每个资金类型的检查正确和异常的数量*/
	public static Map<String, Integer> mapCheckResult = new HashMap<String, Integer>();
	
	/*等待间隔*/
	private static final int CALCULATE_WAIT_TIME = 500;
	/*记录总数*/
	public static int count = 0;
	

	private static Logger log = Logger.getLogger(InviTotalNumPool.class.getName());

	public InviTotalNumPool() {

	}
	
	/**
	 * 初始化每个币种的核对的正常和异常用户数
	 * @param fundsType
	 */
	public synchronized static void initMapCheckResult(int fundsType) {
		log.info("初始化正常用户数，异常用户数...");
		/*初始化正常用户数，异常用户数*/
		/*初始化赋值0*/
		/*加入资金类型计数*/
		mapCheckResult.put(fundsType + "cor", 0);
		mapCheckResult.put(fundsType + "err", 0);
	}
	
	/**
	 * 
	 * @param fundsType
	 */
	public synchronized static void updateCorrectUserNum(int fundsType) {
		log.info("updateCorrectUserNum");
		mapCheckResult.put(fundsType + "cor", mapCheckResult.get(fundsType + "cor") + 1);
	}
	
	/**
	 * 
	 * @param fundsType
	 */
	public synchronized static void updateErrorUuserNum(int fundsType) {
		log.info("updateErrorUuserNum");
		mapCheckResult.put(fundsType + "err", mapCheckResult.get(fundsType + "err") + 1);
	}

	/**
	 * 取出池中的用户<br>
	 * 一、线程加锁，防止多线程同时访问该方法<br>
	 * 二、取出池中待计算的用户<br>
	 * 三、若池为空，返回null<br>
	 * 
	 * @return Stirng 待计算用户ID
	 */
	public synchronized static UserFinancialInfo getUserFinancialInfo() {
		if (inviTotalNumPool.isEmpty()) {
			try {
				inviTotalNumPool.wait(CALCULATE_WAIT_TIME);
				return null;
			} catch (InterruptedException ex) {
				log.error(ex.toString(), ex);
				return null;
			}
		} else {
			return inviTotalNumPool.remove(0);
		}
	}
	
	/**
	 * 异常时重置userBillPool
	 */
	public static void resetInviTotalNumPool() {
		inviTotalNumPool = new Vector<UserFinancialInfo>();
	}

	/**
	 * 添加用户ID<br>
	 * 一、线程加锁，防止多线程同时访问该方法<br>
	 * 二、将待计算用户ID添加到池中<br>
	 * @param capUserId
	 */
	public synchronized static void addUserFinancialInfo(UserFinancialInfo userFinancialInfo) {
		inviTotalNumPool.add(userFinancialInfo);
		count = count + 1;
	}

	/**
	 * 判断文件池中是否存在该对象<br>
	 * 一、调用Vector中的contains方法判断是否存在该对象<br>
	 * 二、如存在，返回true,否则返回false<br>
	 * 
	 * @param files
	 *            待计算文件对象
	 * @return true|false
	 */
	public static boolean contains(String capUserId) {
		return inviTotalNumPool.contains(capUserId);

	}
	
	/**
	 * 
	 */
	public static synchronized void subCount() {
		count = count - 1;
	}
	

	/**
	 * 取得池的深度
	 * 
	 * @return 池深度
	 */
	public static int size() {
		return count;
	}

	/**
	 * 判断池是否为空
	 * 
	 * @return true|false
	 */
	public static boolean isEmpty() {
		return inviTotalNumPool.isEmpty();
	}
}
