//package com.world.model.dao.auto.worker;
//
//import com.world.data.mysql.Data;
//import com.world.data.mysql.OneSql;
//import com.world.model.dao.fee.FeeDao;
//import com.world.model.dao.pay.FundsDao;
//import com.world.model.dao.task.Worker;
//import com.world.model.dao.user.mem.UserCache;
//import com.world.model.entity.bill.BillType;
//import com.world.model.entity.financial.fee.Fee;
//import com.world.model.entity.usercap.dao.CommAttrDao;
//import com.world.model.entity.usercap.entity.CommAttrBean;
//import org.apache.log4j.Logger;
//
//import java.math.BigDecimal;
//import java.util.List;
//
///**
// * Created by suxinjie on 2017/7/12.
// *
// * 手续费汇总,将交易手续费的50%转移到固定用户的账户中(配置在comm_attr表中)
// *
// * 因为交易双向收费,会收取买方和卖方的手续费,因为两方则算成btc的量是一样的,
// * 所以只需要将fee表中的btc量统计出来,即使交易手续费的50%
// *
// */
//public class FeeSummaryWorker extends Worker {
//
//    public static Logger LOGGER = Logger.getLogger(FeeSummaryWorker.class.getName());
//
//    public FeeSummaryWorker(String name, String des) {
//        super(name, des);
//    }
//
//    private CommAttrDao commAttrDao = new CommAttrDao();
//    private FeeDao feeDao = new FeeDao();
//    private FundsDao fundsDao = new FundsDao();
//
//    private volatile boolean running = false;
//
//    @Override
//    public void run() {
//        //1. 获取comm_attr表中关于手续费汇总的配置信息
//        //2. 判断是否处于开启状态,如果没开启,直接结束任务
//        //3. 统计fee表中没有处理的(flag=0)btc手续费数量
//        //4. 将该部分手续费累加到用户财务信息中
//        //5. 添加一条财务流水到bill表
//        //6. 将转移成功的手续费标记为已处理(flag=1)
//        try {
//            if (running) {
//                LOGGER.info(super.getName() + "-上一个任务还没有执行完毕,等待下一个轮询");
//                return;
//            }
//
//            running = true;
//
//            CommAttrBean commAttrBean = commAttrDao.queryByAttrTypeAndParaCode(10000002, "01");
//            if (commAttrBean == null) {
//                LOGGER.info(super.getName() + "-没有配置回购用户或者回购用户开关未开启");
//
//                return;
//            }
//
//            //查询当前fee表最大的id,用于后续的update
//            Fee maxIdFee = feeDao.getMaxIdByTypeAndCurrency(1, "BTC");
//            if (maxIdFee == null || maxIdFee.getId() <= 0) {
//                LOGGER.info(super.getName() + "-没有需要转移的BTC交易手续费");
//                return;
//            }
//
//            //查询maxId下的未处理的btc手续费总和
//            Fee sumFee = feeDao.getSumFeeByMaxId(1, "BTC", maxIdFee.getId());
//            if (sumFee.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
//                LOGGER.info(super.getName() + "-手续费总额为0,无需处理");
//                return;
//            }
//
//            //处理用户财务信息,添加bill
//            List<OneSql> sqls = fundsDao.addMoney(sumFee.getAmount(), commAttrBean.getParaValue(), commAttrBean.getParaDesc(), BillType.transFeeToICO.getValue(), BillType.transFeeToICO.getKey(), 2, BigDecimal.ZERO, "0", true);
//            //修改fee操作状态
//            sqls.add(new OneSql("update fee set flag=1 where type=1 and currency='BTC' and flag=0 and id<=?", -2, new Object[]{maxIdFee.getId()}));
//
//            if (Data.doTrans(sqls)) {
//                UserCache.resetUserFundsFromDatabase(commAttrBean.getParaValue());
//                LOGGER.info(super.getName() + "-手续费转移给回购用户成功, 转移金额 : " + sumFee.getAmount());
//            } else {
//                LOGGER.error(super.getName() + "-手续费转移给回购用户失败, 失败金额 : " + sumFee.getAmount());
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(super.getName() + "-任务执行异常", e);
//        } finally {
//            running  = false;
//        }
//
//    }
//}
