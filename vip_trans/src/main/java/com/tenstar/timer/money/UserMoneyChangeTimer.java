//package com.tenstar.timer.money;
//
//import com.world.model.daos.world.FundsUserDao;
//import com.world.util.callback.AsynMethodFactory;
//import org.apache.log4j.Logger;
//
//import java.util.Iterator;
//import java.util.TimerTask;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
///**
// * 功能：利用队列容器保存最新需要更新资产变动的用户ID。
// * 每500毫秒中从队列获取50个用户进行处理，利用异步线程处理50个用户的资产变动.
// *
// * @author zhanglinbo 20160813
// */
//public class UserMoneyChangeTimer extends TimerTask {
//    public static Logger log = Logger.getLogger(UserMoneyChangeTimer.class);
//
//    //定义线程安全队列存放需要处理的用户资金变动，采用先进先出的顺序处理。确保用户大批量购买时同一时间不重复多次执行
//    public static java.util.concurrent.ConcurrentLinkedQueue<Object[]> userMoneyChangeQueue = new ConcurrentLinkedQueue<Object[]>();
//
//    /**
//     * 线程从队列里面获取前50个进行资金异步处理，控制性能。
//     */
//    @Override
//    public void run() {
//        //定时处理用户资产变动
//        try {
//            int num = 0;
//            //异步任务处理器未初始化，直接退出
//            if (AsynMethodFactory.anycService == null) {
//                return;
//            }
//
//            while (!userMoneyChangeQueue.isEmpty()) {
//                //线程池已堆满。退出等待下一次循环再处理
//                if (AsynMethodFactory.getBalance() >= 300) {
//                    return;
//                }
//                Object[] obj = userMoneyChangeQueue.poll();
//                AsynMethodFactory.addWork(FundsUserDao.class, "updateFundsByChange", obj);
//                num++;
//                if (num >= 50) {
//                    return;
//                }
//            }
//        } catch (Exception e) {
//            log.error(e.toString(), e);
//        }
//    }
//
//    /**
//     * 用户资金变动通知添加到处理队列。
//     * 添加进行重复过滤判断，用户ID和变动类型一致则不添加，减少用户资产处理重复执行，影响性能。
//     *
//     * @param arg 对象数据 用户ID
//     * @author zhanglinbo 20160819
//     */
//    public static void add(Object[] arg) {
//        Iterator<Object[]> iter = userMoneyChangeQueue.iterator();
//        boolean flag = false;
//        while (iter.hasNext()) {
//            Object[] obj = iter.next();
//            if (obj[0].equals(arg[0])) {
//                flag = true;
//                break;
//            }
//        }
//        if (!flag) {
//            userMoneyChangeQueue.offer(arg);
//        }
//    }
//
//}
