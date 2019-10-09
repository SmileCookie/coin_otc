package com.world.model.dao.auto;

import org.apache.commons.lang.time.DateUtils;

import com.world.model.backcapital.worker.BackCapitalUpdateWorker;
import com.world.model.backcapital.worker.BackCapitalWithdrawWorker;
import com.world.model.backcapital.worker.BackCapitalWorker;
import com.world.model.balaccount.job.autodownload.AutoDownloadWorker;
import com.world.model.balaccount.job.feeaccount.FeeAccountDealWork;
import com.world.model.balaccount.job.finaccdetailswork.FinAccDetaislWork;
import com.world.model.balaccount.job.finaccdownload.FinAccDownloadWork;
import com.world.model.balaccount.job.finaccwalbill.FinAccWalBillWork;
import com.world.model.balaccount.job.finaccwalbill.thread.WalletCheckThread;
import com.world.model.balaccount.job.finaccwalhotdownload.FinAccWalHotDownloadWork;
import com.world.model.balaccount.job.finaccwalletnetfee.FinAccWalletNetFeeWork;
import com.world.model.balaccount.job.report.UserOnlineWork;
import com.world.model.balaccount.job.wallettrans.WalletBalanceCrawlerWork;
import com.world.model.balaccount.job.wallettrans.WalletTransWork;
import com.world.model.chart.TradingVolumeWorker;
import com.world.model.dao.auto.worker.BuySellRatioWorker;
import com.world.model.dao.auto.worker.DealChargeWorker;
import com.world.model.dao.auto.worker.DealDownloadWorker;
import com.world.model.dao.auto.worker.GetRechargeAddressWorker;
import com.world.model.dao.auto.worker.GivingNumberWorker;
import com.world.model.dao.auto.worker.LuckyQualifyWorker;
import com.world.model.dao.auto.worker.NetAssetWorker;
import com.world.model.dao.auto.worker.PriceReminder;
import com.world.model.dao.auto.worker.RechargeTurnoverWorker;
import com.world.model.dao.auto.worker.SynCoinPriceWorker;
import com.world.model.dao.auto.worker.SynForeignPriceWorker;
import com.world.model.dao.auto.worker.SyncOuterNetMarketPriceWorker;
import com.world.model.dao.auto.worker.TickerDataWorker;
import com.world.model.dao.auto.worker.VipUpgradeWorker;
import com.world.model.dao.task.TaskFactory;
import com.world.model.financialproift.userfininfo.DynamicBonusDisWork;
import com.world.model.financialproift.userfininfo.DynamicBonusResetCalWork;
import com.world.model.financialproift.userfininfo.InviTotalNumWork;
import com.world.model.financialproift.userfininfo.ReturnUserCapitalPayWork;
import com.world.model.financialproift.userfininfo.ReturnUserCapitalProductWork;
import com.world.model.financialproift.userfininfo.ReturnUserOrderWork;
import com.world.model.financialproift.userfininfo.ReturnUserWarnMailWork;
import com.world.model.financialproift.userfininfo.SetPlatEcologySystemWork;
import com.world.model.financialproift.userfininfo.SetUserInvitaionNum;
import com.world.model.financialproift.userfininfo.SetVdsUsdtPriceWork;
import com.world.model.financialproift.userfininfo.SuperNodeRewardDetailWork;
import com.world.model.financialproift.userfininfo.SuperNodeRewardProductWork;
import com.world.model.financialproift.userfininfo.SuperNodeRewardWork;
import com.world.model.financialproift.userfininfo.VIPRewardFromDoubleThrowWork;
import com.world.model.financialproift.worker.EcoRewardAssignWork;
import com.world.model.financialproift.worker.EcoRewardBillWork;
import com.world.model.financialproift.worker.FinancialProiftBlockConfigWork;
import com.world.model.financialproift.worker.SuperNodeWork;
import com.world.model.quanttrade.worker.QuantHedgeWarnWorker;
import com.world.model.quanttrade.worker.QuantTradeWarnWorker;
import com.world.model.statisticalreport.MongoExtractWork;
import com.world.model.statisticalreport.MysqlExtractWork;
import com.world.model.statisticalreport.StatisticalReportWork;
import com.world.model.statisticalreport.WithDrawReviewWorker;
import com.world.model.usercap.UserCapMonitor;

public class AutoFactory extends TaskFactory {

    public static void start() {


        startAll();
        work(new NetAssetWorker("NetAssetWorker", "净资产折合积分定时器"), 10 * 60 * 1000);    // 10分钟一次
        work(new VipUpgradeWorker("VipUpgradeWorker", "VIP等级更新器"), 1 * 60 * 1000);    //1分钟一次
        work(new GivingNumberWorker("GivingNumberWorker", "成交积分处理定时器"), 30 * 1000); // 30s一次
        // 价格提醒推送
        work(new PriceReminder("PriceReminder", "价格提醒推送"), 60 * 1000); // 1分钟一次
        //获取本项目没有的市场价格
        work(new TickerDataWorker("TickerDataWorker", "获取行情定时器", true), 60 * 1000);
        // 从商户平台获取新地址xx
        //work(new GetMerchantsAddressWorker("GetMerchantsAddressWorker","从商户平台获取新地址"), 10 * 60 * 1000);//10分钟一次

        // 获取新充值地址BTC\ltc\etc地址  renfei
        work(new GetRechargeAddressWorker("GetRechargeAddressWorker", "从gaia-交易中心获取充值地址"), 1 * 60 * 1000);//1分钟一次
        //同步充值记录，获取充值地址，RPC打币
        //work(new RpcChargeWithdrawWorker("RpcChargeWithdrawWorker", "RPC调用处理充值提币", true), 2 * 60 * 1000);
        //  充值确认口水 获取充值记录信息 renfei
        work(new RechargeTurnoverWorker("RechargeTurnoverWorker", "从gaia-交易中心获取充值记录信息", true), 2 * DateUtils.MILLIS_PER_MINUTE);
        //根据充值记录给用记充值 到账
        work(new DealChargeWorker("DealChargeWorker", "充值财务到账定时器"), DateUtils.MILLIS_PER_MINUTE);// fixme 这个定时器可以不需要处理,主要是对status=200的充值记录进行到账操作(操作财务)  renfei

        //RPC打币完成后，处理提币记录，解冻资产
        work(new DealDownloadWorker("DealDownloadWorker", "下载定时器"), 60000);

        //用户资金监控，10分钟一次
        work(new UserCapMonitor("UserCapMonitor", "用户资金监控"), 10 * 60 * 1000);
        //同步中国人民银行外汇价格,从bitfinex获取比特币对美元价格并缓存
        work(new SynCoinPriceWorker("SynCoinPriceWorker", "从外网获取货币价格"), 2 * 60 * 1000); // 2分钟执行一次
        work(new SyncOuterNetMarketPriceWorker("SyncOuterNetMarketPriceWorker", "同步外网市场价格"), 5 * 60 * 1000); // 1分钟执行一次
        work(new BuySellRatioWorker("BuySellRatioWorker", "统计每个市场的买卖占比"), 2 * 60 * 1000); // 2分钟执行一次
        //计算提现手续费
        work(new FeeAccountDealWork("FeeAccountDealWork", "计算提现手续费定时任务"), 2 * 60 * 1000); // 30min执行一次
        /**
         * 交易平台同步支付中心的对账数据
         */
        /*充值记录对账同步,30分钟一次*/
        work(new FinAccDetaislWork("FinAccDetaislWork", "充值记录对账同步"), 5 * 60 * 1000);
        /*提现记录对账同步,30分钟一次*/
        work(new FinAccDownloadWork("FinAccDownloadWork", "提现记录对账同步"), 5 * 60 * 1000);

        /*提现热钱包余额查询,1分钟一次*/
        work(new FinAccWalHotDownloadWork("FinAccWalHotDownloadWork", "提现热钱包余额查询"), 1 * 60 * 1000);
        /*网络费记录对账同步,1分钟一次*/
        work(new FinAccWalletNetFeeWork("FinAccWalletNetFeeWork", "网络费记录对账同步"), 5 * 60 * 1000);
        /*钱包流水同步查询,1分钟一次 线上生产环境未启用 FinAccWalBillWork=false */
        work(new FinAccWalBillWork("FinAccWalBillWork", "钱包流水同步查询"), 5 * 60 * 1000);
        //回购功能定时任务
        work(new BackCapitalWorker("BackCapitalWorker", "回购功能定时任务"), 1000); //每秒钟执行一次
        //回购记录更新定时任务
        work(new BackCapitalUpdateWorker("BackCapitalUpdateWorker", "回购记录更新定时任务"), 3 * 1000); //3秒钟执行一次
        //回购提现定时任务
        work(new BackCapitalWithdrawWorker("BackCapitalWithdrawWorker", "回购提现定时任务"), 1000); //每秒钟执行一次

        work(new WithDrawReviewWorker("WithDrawReviewWorker", "回购提现定时任务"), 5 * 60 * 1000);//每10分钟执行一次

        //量化交易预警定时任务
        work(new QuantTradeWarnWorker("QuantTradeWarnWorker", "量化交易预警定时任务"), 2 * 1000); //2秒钟执行一次，保证保值速度

        //统计平台24小时成交量定时任务
        work(new TradingVolumeWorker("TradingVolumeWorker", "统计平台24小时成交量定时任务"), 1000); //每秒钟执行一次

        //同步法币汇率到缓存定时任务
        work(new SynForeignPriceWorker("SynForeignPriceWorker", "同步法币汇率到缓存定时任务"), 30*DateUtils.MILLIS_PER_MINUTE); //30m执行一次

        //mysql数据库抽取
        work(new MysqlExtractWork("MysqlExtractWork", "mysql数据抽取"), 60 * 60 * 1000);
        //mongo数据抽取
        work(new MongoExtractWork("MongoExtractWork", "mongos数据抽取"), 60 * 60 * 1000);
        //bill 表数据计算
        work(new StatisticalReportWork("StatisticalReportWork", "bill数据计算"), 70 * 60 * 1000);

        //小额自动打币定时任务
        work(new AutoDownloadWorker("AutoDownloadWorker", "小额自动打币定时任务"), 10 * 60 * 1000); //30分钟执行一次

        work(new LuckyQualifyWorker("LuckyQualifyWorker", "同步抽奖资格数据"), 2 * 60 * 1000); //5分钟执行一次

        //start by chendi
        work(new WalletCheckThread("WalletCheckThread", "钱包流水同步查询"), 30 * 60 * 1000);
        //end

        /*支付中心钱包流水同步 && 钱包资金自检 线上生产环境启用 WalletTransWork=true*/
        work(new WalletTransWork("WalletTransWork", "【新对账】钱包流水同步查询"), 5 * 60 * 1000);


        //===============================首页报表定时任务============================
//        work(new PlatformFundsWork("PlatformFundsWork", "平台资金报表定时任务计算"), 2 * 60 * 1000);
//        work(new EntrustmentDistributionWork("EntrustmentDistributionWork", "委托分布报表定时任务计算"), 2 * 60 * 1000);
//        work(new UserDistributionWork("UserDistributionWork", "用户分布报表定时任务计算"), 2 * 60 * 1000);
//        work(new TransactionVolumeWork("TransactionVolumeWork", "用户分布报表定时任务计算"), 2 * 60 * 1000);

        work(new UserOnlineWork("UserOnlineWork", "用户在线报表定时任务计算"), 10 * 1000);

        //=========保值对账=====
        work(new QuantHedgeWarnWorker("QuantHedgeWarnWorker", "保值对账"), 30 * 60 * 1000);

        // 钱包余额爬虫
        work(new WalletBalanceCrawlerWork("WalletTransWork", "【新对账】钱包流水同步查询"), 5 * 60 * 1000);
        
        //理财邀请统计10分钟统计1次
        work(new InviTotalNumWork("InviTotalNumWork", "理财邀请统计"), 10 * 60 * 1000);

        //理财-同步当前区块和分红区块到REDIS（1分钟一次）--create by yolanda
        work(new FinancialProiftBlockConfigWork("FinancialProiftBlockConfigWork", "同步当前区块和分红区块"), 1 * 60 * 1000);

        //理财-同步超级节点信息到REDIS（10分钟一次）--create by yolanda
        work(new SuperNodeWork("SuperNodeWork", "同步超级节点信息"), 10 * 60 * 1000);

        /*理财设置VDSUSDT价格*/
        work(new SetVdsUsdtPriceWork("SetVdsUsdtPriceWork", "理财设置VDSUSDT价格定时任务"), 1 * 1000);
        
        /*理财设置生态回馈奖金、超级节点、用户奖励状态、t_bonus*/
        work(new SetPlatEcologySystemWork("SetPlatEcologySystemWork", "理财资金收益设置定时任务"), 30 * 1000);
        
        /**
         * 理财开跑顺序
         * 
         */
        
        
        /**
         * 新人加成
         * NewUserRewardProductWork
         * NewUserDetailWork
         * NewUserDistWork
         */
//        work(new NewUserRewardProductWork("NewUserRewardProductWork", "新人加成-区块奖励记录生成(1周区块)-生成-用distTime"), 10 * 60 * 1000);
//        work(new NewUserDetailWork("NewUserDetailWork", "新人加成奖励分配记录生成"), 10 * 60 * 1000);
//        work(new NewUserDistWork("NewUserDistWork", "新人加成奖励分配记录结算"), 10 * 60 * 1000);
        /**
         * VIP分红奖励
         * SuperNodeRewardProductWork
         * SuperNodeRewardDetailWork
         * SuperNodeRewardWork
         */
        work(new SuperNodeRewardProductWork("SuperNodeRewardProductWork", "VIP分红-区块奖励记录生成(50区块)-生成"), 30 * 60 * 1000);
        work(new SuperNodeRewardDetailWork("SuperNodeRewardDetailWork", "VIP分红奖励分配记录生成"), 10 * 60 * 1000);
        work(new SuperNodeRewardWork("SuperNodeRewardWork", "VIP分红奖励分配记录结算"), 11 * 60 * 1000);
        /**
         * VIP补发达到50000
         * VIPRewardFromDoubleThrowWork 
         */
        work(new VIPRewardFromDoubleThrowWork("VIPRewardFromDoubleThrowWork", "VIP分红奖励记录生成(从复投释放)-生成"), 30 * 60 * 1000);
        
        /**
         * 扫描层级人数
         * SetUserInvitaionNum
         */
        work(new SetUserInvitaionNum("SetUserInvitaionNum", "扫描层级人数"), 1 * 60 * 1000);
        
        /**
         * 动态奖金(建点,指导,晋升)分配-跑的时候开(cal分配区间)
         * DynamicBonusDisWork
         */
        work(new DynamicBonusResetCalWork("DynamicBonusResetCalWork", "动态奖金(建点,指导,晋升)重新计算"), 10 * 60 * 1000);
        work(new DynamicBonusDisWork("DynamicBonusDisWork", "动态奖金(建点,指导,晋升)结算"), 10 * 60 * 1000);
        
        /**
         * 理财-生态参与回馈-分配定时任务（10分钟一次）
         * 理财-生态参与回馈-结算定时任务（10分钟一次）
         */
        work(new EcoRewardAssignWork("EcoRewardAssignWork", "生态参与回馈分配定时任务"), 20 * 60 * 1000);
        work(new EcoRewardBillWork("EcoRewardBillWork", "生态参与回馈结算定时任务"), 10 * 60 * 1000);
        
        /**
         * 回本加成
         * ReturnUserOrderWork				回本用户顺序列表生成-cal
         * ReturnUserCapitalProductWork		回本加成-区块奖励记录生成(1周区块)-生成-更新16,17,18时间
         * ReturnUserCapitalPayWork			回本用户奖励支付
         * ReturnUserWarnMailWork			回本未复投用户邮件提醒-3天
         */
        work(new ReturnUserOrderWork("ReturnUserOrderWork", "回本用户顺序列表生成-cal"), 10 * 60 * 1000);
        work(new ReturnUserCapitalProductWork("ReturnUserCapitalProductWork", "回本加成-区块奖励记录生成(1周区块)-生成-更新16,17,18时间"), 10 * 60 * 1000);
        work(new ReturnUserCapitalPayWork("ReturnUserCapitalPayWork", "回本用户奖励支付"), 10 * 60 * 1000);
        work(new ReturnUserWarnMailWork("ReturnUserWarnMailWork", "回本未复投用户邮件提醒-3天"), 24 * 60 * 60 * 1000);
    }
    
    public static void main(String[] args) {
        WalletTransWork dealDownloadWorker = new WalletTransWork("WalletTransWork","冷钱包余额查询");
        dealDownloadWorker.run();
    }
}
