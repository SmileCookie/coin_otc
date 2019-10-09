package com.world.model.usercap.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.world.model.financial.entity.BillFinancial;
import org.apache.log4j.Logger;



/**
 * <p>标题: 用户资金监控池</p>
 * <p>描述: 储需要监控的用户资金信息</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class UserCapPool {
	/* 同步存储，存放需要扫描核对的用户id */
	private static Vector<BillFinancial> userBillPool = new Vector<BillFinancial>();
	
	/*存放每个资金类型的检查正确和异常的数量*/
	public static Map<String, Integer> mapCheckResult = new HashMap<String, Integer>();
	
	/*等待间隔*/
	private static final int CALCULATE_WAIT_TIME = 500;
	/*记录总数*/
	public static int count = 0;
	

	private static Logger log = Logger.getLogger(UserCapPool.class.getName());

	public UserCapPool() {

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
	public synchronized static BillFinancial getUserBill() {
		if (userBillPool.isEmpty()) {
			try {
				userBillPool.wait(CALCULATE_WAIT_TIME);
				return null;
			} catch (InterruptedException ex) {
				log.error(ex.toString(), ex);
				return null;
			}
		} else {
			return userBillPool.remove(0);
		}
	}
	
	/**
	 * 异常时重置userBillPool
	 */
	public static void resetUserBillPool() {
		userBillPool = new Vector<BillFinancial>();
	}

	/**
	 * 添加用户ID<br>
	 * 一、线程加锁，防止多线程同时访问该方法<br>
	 * 二、将待计算用户ID添加到池中<br>
	 * @param
	 */
	public synchronized static void addUserBill(BillFinancial BillFinancial) {
		userBillPool.add(BillFinancial);
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
		return userBillPool.contains(capUserId);

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
		return userBillPool.isEmpty();
	}




	public static void setNull(){
		userBillPool.clear();
	}

}
