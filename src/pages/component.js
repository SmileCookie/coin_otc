import React from 'react'
import Loadable from 'react-loadable'
import Loading from './loading'



const InputPage = Loadable({
    loader: () => import('./tradecenter/marketmod/inputPage'),
    loading() {
        return <Loading />
    }
});


const Home = Loadable({
    loader: () => import('./home'),
    loading() {
        return <Loading />
    }
});
const OperTask = Loadable({
    loader: () => import('./deskcenter/opertaskmod/operTask'),
    loading() {
        return <Loading />
    }
});
//财务中心
const Finance = Loadable({
    loader: () => import('./financialcenter/financialmod/finance'),
    loading() {
        return <Loading />
    }
});
const AccountManage = Loadable({//账户管理
    loader: () => import('./financialcenter/financialmod/accountManage'),
    loading() {
        return <Loading />
    }
});
const SettleMent = Loadable({//每日结算
    loader: () => import('./financialcenter/financialmod/settleMent'),
    loading() {
        return <Loading />
    }
});
const CapitalUse = Loadable({//收支用途
    loader: () => import('./financialcenter/financialmod/capitalUse'),
    loading() {
        return <Loading />
    }
});
const TransferSettlement = Loadable({//划转结算
    loader: () => import('./financialcenter/financialmod/transferSettlement'),
    loading() {
        return <Loading />
    }
});

// const UserCapitalOTC = Loadable({//用户资金-OTC
//     loader: () => import('./tradecenter/userfundsmod/userCapitalOTC'),
//     loading() {
//         return <Loading />
//     }
// });
const UserCapitalWallet = Loadable({//用户资金-钱包
    loader: () => import('./tradecenter/moneycenter/userCapitalWallet'),
    loading() {
        return <Loading />
    }
});
// const UserCapital = Loadable({//用户资金-币币
//     loader: () => import('./tradecenter/userfundsmod/userCapital'),
//     loading() {
//         return <Loading />
//     }
// });
// const UserCapitalSummary = Loadable({//用户资金汇总
//     loader: () => import('./tradecenter/userfundsmod/userCapitalSummary'),
//     loading() {
//         return <Loading />
//     }
// });


const BillDetailOTC = Loadable({//账单明细-OTC
    loader: () => import('./tradecenter/billingdetailsmod/billDetailOTC'),
    loading() {
        return <Loading />
    }
});
const BillDetailWallet = Loadable({//账单明细-钱包
    loader: () => import('./tradecenter/billingdetailsmod/billDetailWallet'),
    loading() {
        return <Loading />
    }
});
const BillDetail = Loadable({//账单明细-币币
    loader: () => import('./tradecenter/billingdetailsmod/billDetail'),
    loading() {
        return <Loading />
    }
});

const CapitalCount = Loadable({//资金统计-币币
    loader: () => import('./financialcenter/fundstatistics/capitalCount'),
    loading() {
        return <Loading />
    }
});
const CapitalCountOTC = Loadable({//资金统计-法币
    loader: () => import('./financialcenter/fundstatistics/capitalCountOTC'),
    loading() {
        return <Loading />
    }
});
const CapitalCountWallet = Loadable({//资金统计-钱包
    loader: () => import('./financialcenter/fundstatistics/capitalCountWallet'),
    loading() {
        return <Loading />
    }
});

const FeeProfit = Loadable({//币币收益
    loader: () => import('./tradecenter/statisticalReports/feeProfit'),
    loading() {
        return <Loading />
    }
});

const RechargeRecord = Loadable({//充值记录
    loader: () => import('./tradecenter/paymentmod/rechargeRecord'),
    loading() {
        return <Loading />
    }
});
const RechargeAddress = Loadable({//充值地址
    loader: () => import('./tradecenter/paymentmod/rechargeAddress'),
    loading() {
        return <Loading />
    }
});
const WithdrawApprove = Loadable({
    loader: () => import('./financialcenter/paymentmod/withdrawApprove'),
    loading() {
        return <Loading />
    }
});

// const RechargeAudit = Loadable({//充值审核
//     loader: () => import('./financialcenter/paymentmod/rechargeAudit'),
//     loading() {
//         return <Loading />
//     }
// });



const WithdrawAddress = Loadable({//提现地址
    loader: () => import('./tradecenter/paymentmod/withdrawAddress'),
    loading() {
        return <Loading />
    }
});
const WithdrawRecord = Loadable({//提现查询
    loader: () => import('./tradecenter/paymentmod/withdrawRecord'),
    loading() {
        return <Loading />
    }
});

const FunctionManage = Loadable({
    loader: () => import('./systemcenter/authoritymod/functionManage'),
    loading() {
        return <Loading />
    }
});
const FunctionRole = Loadable({
    loader: () => import('./systemcenter/authoritymod/functionRole'),
    loading() {
        return <Loading />
    }
});
const OperManage = Loadable({
    loader: () => import('./systemcenter/authoritymod/operManage'),
    loading() {
        return <Loading />
    }
});

const UserInfo = Loadable({
    loader: () => import('./systemcenter/usermod/userInfo'),
    loading() {
        return <Loading />
    }
});
const LoginInfo = Loadable({
    loader: () => import('./systemcenter/usermod/loginInfo'),
    loading() {
        return <Loading />
    }
});
const IdentificationInfo = Loadable({
    loader: () => import('./systemcenter/usermod/identificationInfo'),
    loading() {
        return <Loading />
    }
});
const UserDataManage = Loadable({//用户资料管理
    loader: () => import('./systemcenter/usermod/userDataManage'),
    loading() {
        return <Loading />
    }
});
const BlackList = Loadable({
    loader: () => import('./systemcenter/usermod/blackList'),
    loading() {
        return <Loading />
    }
});

const IntegralBill = Loadable({
    loader: () => import('./systemcenter/vipmod/integralBill'),
    loading() {
        return <Loading />
    }
});
const IntegralRule = Loadable({
    loader: () => import('./systemcenter/vipmod/integralRule'),
    loading() {
        return <Loading />
    }
});
const IntegralVipRule = Loadable({
    loader: () => import('./systemcenter/vipmod/integralVipRule'),
    loading() {
        return <Loading />
    }
});
const UserVip = Loadable({
    loader: () => import('./systemcenter/vipmod/userVip'),
    loading() {
        return <Loading />
    }
});

const IdentityAuthentication = Loadable({//实名认证
    loader: () => import('./systemcenter/verifymod/identityAuthentication'),
    loading() {
        return <Loading />
    }
});
const GoogleVerify = Loadable({
    loader: () => import('./systemcenter/verifymod/googleVerify'),
    loading() {
        return <Loading />
    }
});
const PhoneVerify = Loadable({
    loader: () => import('./systemcenter/verifymod/phoneVerify'),
    loading() {
        return <Loading />
    }
});
const CloseSecondVerify = Loadable({//关闭二次验证
    loader: () => import('./systemcenter/verifymod/closeSecondVerify'),
    loading() {
        return <Loading />
    }
});


const EntrustRecord = Loadable({
    loader: () => import('./tradecenter/marketmod/entrustRecord'),
    loading() {
        return <Loading />
    }
});
const PlanEntrustRecord = Loadable({
    loader: () => import('./tradecenter/marketmod/palnEntrustRecord'),
    loading() {
        return <Loading />
    }
});

const MessageQuery = Loadable({
    loader: () => import('./systemcenter/message/messageQuery'),
    loading() {
        return <Loading />
    }
});
const MessageSend = Loadable({
    loader: () => import('./systemcenter/message/messageSend'),
    loading() {
        return <Loading />
    }
});
const MouldSend = Loadable({
    loader: () => import('./systemcenter/message/mouldSend'),
    loading() {
        return <Loading />
    }
});
const MessageRuleMould = Loadable({
    loader: () => import('./systemcenter/message/messageRuleMould'),
    loading() {
        return <Loading />
    }
});

const MarketTrade = Loadable({
    loader: () => import('./tradecenter/marketmod/marketTrade'),
    loading() {
        return <Loading />
    }
});
const DealRecord = Loadable({
    loader: () => import('./tradecenter/marketmod/dealRecord'),
    loading() {
        return <Loading />
    }
});
const MarketMergeTrade = Loadable({
    loader: () => import('./tradecenter/marketmod/marketMergeTrade'),
    loading() {
        return <Loading />
    }
});
const BatchEntrust = Loadable({
    loader: () => import('./tradecenter/marketmod/batchEntrust'),
    loading() {
        return <Loading />
    }
});
const BatchWithdrawal = Loadable({
    loader: () => import('./tradecenter/marketmod/batchWithdrawal'),
    loading() {
        return <Loading />
    }
});

const VoteManage = Loadable({
    loader: () => import('./systemcenter/activitymod/voteManage'),
    loading() {
        return <Loading />
    }
});
const DrawManage = Loadable({
    loader: () => import('./systemcenter/activitymod/drawManage'),
    loading() {
        return <Loading />
    }
});
const DistributeVote = Loadable({
    loader: () => import('./systemcenter/activitymod/distributeVote'),
    loading() {
        return <Loading />
    }
});

const NewsManage = Loadable({
    loader: () => import('./systemcenter/sysmod/newsManage'),
    loading() {
        return <Loading />
    }
});
const TextConfig = Loadable({//textConfig 文案配置
    loader: () => import('./systemcenter/sysmod/textConfig'),
    loading() {
        return <Loading />
    }
});
const NewNewsManage = Loadable({//new新闻管理
    loader: () => import('./systemcenter/sysmod/newNewsManage'),
    loading() {
        return <Loading />
    }
});
const SysNotice = Loadable({
    loader: () => import('./systemcenter/sysmod/sysNotice'),
    loading() {
        return <Loading />
    }
});
const AppManage = Loadable({
    loader: () => import('./systemcenter/sysmod/appManage'),
    loading() {
        return <Loading />
    }
});

const SysDictionary = Loadable({
    loader: () => import('./systemcenter/sysmod/sysDictionary'),
    loading() {
        return <Loading />
    }
});
const DeptManage = Loadable({
    loader: () => import('./systemcenter/sysmod/deptManage'),
    loading() {
        return <Loading />
    }
});
const MailManage = Loadable({
    loader: () => import('./systemcenter/sysmod/mailManage'),
    loading() {
        return <Loading />
    }
});

const Webconfig = Loadable({ //网站底部配置
    loader: () => import('./systemcenter/sysmod/webconfig'),
    loading() {
        return <Loading />
    }
});



const BrushParameter = Loadable({
    loader: () => import('./tradecenter/brushmod/brushParameter'),
    loading() {
        return <Loading />
    }
});
const BrushTask = Loadable({
    loader: () => import('./tradecenter/brushmod/brushTask'),
    loading() {
        return <Loading />
    }
});

const BrushParameterManage = Loadable({
    loader: () => import('./tradecenter/brushtrademod/brushParameterManage'),
    loading() {
        return <Loading />
    }
});
const HangingOrderParameterManage = Loadable({
    loader: () => import('./tradecenter/brushtrademod/hangingOrderParameterManage'),
    loading() {
        return <Loading />
    }
});//挂撤单配置
const SelfSaleParameterManage = Loadable({
    loader: () => import('./tradecenter/brushtrademod/oldselfSaleParameterManage'),
    loading() {
        return <Loading />
    }
});//自成交配置
const OldSelfSaleParameterManage = Loadable({
    loader: () => import('./tradecenter/brushtrademod/selfSaleParameterManage'),
    loading() {
        return <Loading />
    }
});//旧的自成交配置
const BrushTaskManage = Loadable({
    loader: () => import('./tradecenter/brushtrademod/brushTaskManage'),
    loading() {
        return <Loading />
    }
});//量化
const HangingOrderTaskManage = Loadable({
    loader: () => import('./tradecenter/brushtrademod/hangingOrderTaskManage'),
    loading() {
        return <Loading />
    }
});//挂撤单
const SelfSaleTaskManage = Loadable({
    loader: () => import('./tradecenter/brushtrademod/selfSaleTaskManage'),
    loading() {
        return <Loading />
    }
});//自成交
const OldSelfSaleTaskManage = Loadable({
    loader: () => import('./tradecenter/brushtrademod/oldselfSaleTaskManage'),
    loading() {
        return <Loading />
    }
});//旧的自成交

const HedgingTradeRecord = Loadable({
    loader: () => import('./tradecenter/hedgemod/hedgingTradeRecord'),
    loading() {
        return <Loading />
    }
});
const HedgeAccountFinancial = Loadable({
    loader: () => import('./tradecenter/hedgemod/hedgeAccountFinancial'),
    loading() {
        return <Loading />
    }
});
const HedgeAccountState = Loadable({
    loader: () => import('./tradecenter/hedgemod/hedgeAccountState'),
    loading() {
        return <Loading />
    }
});

const HedgeContractTransactionRecord = Loadable({//对冲合约交易记录
    loader: () => import('./tradecenter/futureshedgemod/hedgeContractTransactionRecord'),
    loading() {
        return <Loading />
    }
});



//对账中心
const RechargeBalance = Loadable({//充值对账
    loader: () => import('./tradecenter/paymentcenterbalancemod/rechargeBalance'),
    loading() {
        return <Loading />
    }
});
const RechargeBalanceRisk = Loadable({//充值对账（风控）
    loader: () => import('./tradecenter/paymentcenterbalancemod/rechargeBalanceRisk'),
    loading() {
        return <Loading />
    }
});
const WalletBalance = Loadable({//钱包每日对账
    loader: () => import('./tradecenter/paymentcenterbalancemod/walletBalance'),
    loading() {
        return <Loading />
    }
});
const WalletBalanceRisk = Loadable({//钱包每日对账（风控）
    loader: () => import('./tradecenter/paymentcenterbalancemod/walletBalanceRisk'),
    loading() {
        return <Loading />
    }
});
const WalletBill = Loadable({//钱包流水
    loader: () => import('./tradecenter/paymentcenterbalancemod/walletBill'),
    loading() {
        return <Loading />
    }
});
const WithdrawBalance = Loadable({//提现对账
    loader: () => import('./tradecenter/paymentcenterbalancemod/withdrawBalance'),
    loading() {
        return <Loading />
    }
});
const WithdrawBalanceRisk = Loadable({//提现对账(风控)
    loader: () => import('./tradecenter/paymentcenterbalancemod/withdrawBalanceRisk'),
    loading() {
        return <Loading />
    }
});
const WalletBillDetail = Loadable({//钱包流水明细
    loader: () => import('./tradecenter/paymentcenterbalancemod/walletBillDetail'),
    loading() {
        return <Loading />
    }
});
const WalletxFee = Loadable({//X钱包对账
    loader: () => import('./tradecenter/paymentcenterbalancemod/walletxFee'),
    loading() {
        return <Loading />
    }
});

const WalletTradePlatformLedger = Loadable({
    loader: () => import('./balancecenter/walletbalancemod/walletTradePlatformLedger'),
    loading() {
        return <Loading />
    }
});

const HedgeBalance = Loadable({
    loader: () => import('./balancecenter/brushbalancemod/hedgeBalance'),
    loading() {
        return <Loading />
    }
});
const HedgeRecordBalance = Loadable({
    loader: () => import('./balancecenter/brushbalancemod/hedgeRecordBalance'),
    loading() {
        return <Loading />
    }
});



//报表中心
const CenterCapitalDaily = Loadable({
    loader: () => import('./reportcenter/capitalreportmod/centerCapitalDaily'),
    loading() {
        return <Loading />
    }
});
const WalletCapital = Loadable({
    loader: () => import('./reportcenter/capitalreportmod/walletCapital'),
    loading() {
        return <Loading />
    }
});
const CenterCapitalSum = Loadable({
    loader: () => import('./reportcenter/capitalreportmod/centerCapitalSum'),
    loading() {
        return <Loading />
    }
});
const UserBaseOperCount = Loadable({
    loader: () => import('./reportcenter/userreportmod/userBaseOperCount'),
    loading() {
        return <Loading />
    }
});
const UserPaymentTradeCount = Loadable({
    loader: () => import('./reportcenter/userreportmod/userPaymentTradeCount'),
    loading() {
        return <Loading />
    }
});
const UserPositionCount = Loadable({
    loader: () => import('./reportcenter/userreportmod/userPositionCount'),
    loading() {
        return <Loading />
    }
});
const Mars = Loadable({
    loader: () => import('./reportcenter/financialmod/mars'),
    loading() {
        return <Loading />
    }
});
const CapitalAccount = Loadable({
    loader: () => import('./reportcenter/capitalreportmod/capitalAccount'),
    loading() {
        return <Loading />
    }
});
const GbcRepoTrack = Loadable({
    loader: () => import('./reportcenter/gbcreportmod/gbcRepoTrack'),
    loading() {
        return <Loading />
    }
});
const GbcRepoCount = Loadable({
    loader: () => import('./reportcenter/gbcreportmod/gbcRepoCount'),
    loading() {
        return <Loading />
    }
});
const GbcDividendInCount = Loadable({
    loader: () => import('./reportcenter/gbcreportmod/gbcDividendInCount'),
    loading() {
        return <Loading />
    }
});
const GbcInJackpotCount = Loadable({
    loader: () => import('./reportcenter/gbcreportmod/gbcInJackpotCount'),
    loading() {
        return <Loading />
    }
});
const GbcDividendCount = Loadable({
    loader: () => import('./reportcenter/gbcreportmod/gbcDividendCount'),
    loading() {
        return <Loading />
    }
});
const GbcDividendShareCount = Loadable({
    loader: () => import('./reportcenter/gbcreportmod/gbcDividendShareCount'),
    loading() {
        return <Loading />
    }
});



//监控中心
const SmallAutoPayRecord = Loadable({
    loader: () => import('./monitorcenter/smallautopaymod/smallAutoPayRecord'),
    loading() {
        return <Loading />
    }
});
const SmallPayTask = Loadable({
    loader: () => import('./monitorcenter/smallautopaymod/smallPayTask'),
    loading() {
        return <Loading />
    }
});
const OperLog = Loadable({
    loader: () => import('./systemcenter/logmod/operLog'),
    loading() {
        return <Loading />
    }
});
const TrajectoryLog = Loadable({
    loader: () => import('./systemcenter/logmod/trajectoryLog'),
    loading() {
        return <Loading />
    }
});

const UserCapitalMonitor = Loadable({
    loader: () => import('./monitorcenter/capitalmonitormod/userCapitalMonitor'),
    loading() {
        return <Loading />
    }
});
const BackCapital = Loadable({
    loader: () => import('./monitorcenter/backcapitalmod/backCapital'),
    loading() {
        return <Loading />
    }
});
const BackCapitalCoords = Loadable({
    loader: () => import('./monitorcenter/backcapitalmod/backCapitalCoords'),
    loading() {
        return <Loading />
    }
});

const CurrencyIntroduction = Loadable({//币种介绍
    loader: () => import('./systemcenter/activitymod/currencyIntroduction'),
    loading() {
        return <Loading />
    }
});
// 合作伙伴
const FriendLink = Loadable({
    loader: () => import('./systemcenter/friendmod/friendlink'),
    loading() {
        return <Loading />
    }
});
const GeneralConfig = Loadable({
    loader: () => import('./systemcenter/sysconmod/generalConfig'),
    loading() {
        return <Loading />
    }
});//通用配置
const MarketConfigSS = Loadable({
    loader: () => import('./systemcenter/sysconmod/marketConfigSS'),
    loading() {
        return <Loading />
    }
});//市场配置_之前版本备份
const MarketConfig = Loadable({
    loader: () => import('./systemcenter/sysconmod/marketConfig'),
    loading() {
        return <Loading />
    }
});//市场配置
const CloudStorage = Loadable({
    loader: () => import('./systemcenter/sysconmod/cloudStorage'),
    loading() {
        return <Loading />
    }
});//云存储配置
const MarketList = Loadable({
    loader: () => import('./systemcenter/sysconmod/marketList'),
    loading() {
        return <Loading />
    }
});//市场列表
const BankManage = Loadable({
    loader: () => import('./systemcenter/sysconmod/bankManage'),
    loading() {
        return <Loading />
    }
});//银行管理
const OnlineDeploy = Loadable({
    loader: () => import('./systemcenter/sysmod/onlineDeploy'),
    loading() {
        return <Loading />
    }
});//在线人数配置
const APIparamsConfig = Loadable({
    loader: () => import('./systemcenter/sysmod/APIparamsConfig'),
    loading() {
        return <Loading />
    }
});//API开关配置
const FeaturesLock = Loadable({//功能锁
    loader: () => import('./systemcenter/sysconmod/featuresLock'),
    loading() {
        return <Loading />
    }
});
const OtcPaymentType = Loadable({
    loader: () => import('./systemcenter/sysconmod/otcPaymentType'),
    loading() {
        return <Loading />
    }
});//支付方式
const AdvertisingSpaceManage = Loadable({//广告位管理
    loader: () => import('./systemcenter/advertisingmod/advertisingSpaceManage'),
    loading() {
        return <Loading />
    }
});
const AdvertisingPhotoManage = Loadable({//广告位图片
    loader: () => import('./systemcenter/advertisingmod/advertisingPhotoManage'),
    loading() {
        return <Loading />
    }
});
const AdvertisingPhotoManageOTC = Loadable({//OTC广告位图片
    loader: () => import('./systemcenter/advertisingmod/advertisingPhotoManageOTC'),
    loading() {
        return <Loading />
    }
});
const CurrencyManageOTC = Loadable({//币种管理-OTC
    loader: () => import('./systemcenter/sysconmod/currencyManageOTC'),
    loading() {
        return <Loading />
    }
});
const ScheduledManage = Loadable({//定时任务管理
    loader: () => import('./systemcenter/sysmod/scheduledManage'),
    loading() {
        return <Loading />
    }
});

const AdvertManage = Loadable({//广告管理
    loader: () => import('./tradecenter/advertmod/advertManage'),
    loading() {
        return <Loading />
    }
});
const AppealManage = Loadable({//申诉管理
    loader: () => import('./tradecenter/appealmod/appealManage'),
    loading() {
        return <Loading />
    }
});

const OrderManage = Loadable({//订单管理
    loader: () => import('./tradecenter/ordermod/orderManage'),
    loading() {
        return <Loading />
    }
});
/*数据中心*/

//期货账单流水明细
const CapitalChangeRecordFT = Loadable({//资金变动记录
    loader: () => import('./tradecenter/futuresdetailsmod/capitalChangeRecordFT'),
    loading() {
        return <Loading />
    }
});
const LeverAdjustRecordFT = Loadable({//杠杆调整记录
    loader: () => import('./tradecenter/futuresdetailsmod/leverAdjustRecordFT'),
    loading() {
        return <Loading />
    }
});
const MarginCallRecordFT = Loadable({//追加保证金记录
    loader: () => import('./tradecenter/futuresdetailsmod/marginCallRecordFT'),
    loading() {
        return <Loading />
    }
});
const TransactionBalanceRecordFT = Loadable({//成交结余记录
    loader: () => import('./tradecenter/futuresdetailsmod/transactionBalanceRecordFT'),
    loading() {
        return <Loading />
    }
});

// 期货交易中心
const HandicapMergerTransactionFT = Loadable({//期货盘口合并交易
    loader: () => import('./tradecenter/futurestradingmod/handicapMergerTransactionFT'),
    loading() {
        return <Loading />
    }
});
const HandicapTradingFT = Loadable({//期货盘口交易
    loader: () => import('./tradecenter/futurestradingmod/handicapTradingFT'),
    loading() {
        return <Loading />
    }
});
const EntrustmentDetailsFT = Loadable({//期货委托明细
    loader: () => import('./tradecenter/futurestradingmod/entrustmentDetailsFT'),
    loading() {
        return <Loading />
    }
});
const MochikuraDetailsFT = Loadable({//期货持仓明细
    loader: () => import('./tradecenter/futurestradingmod/mochikuraDetailsFT'),
    loading() {
        return <Loading />
    }
});
const TransactionRecordFT = Loadable({//期货成交记录
    loader: () => import('./tradecenter/futurestradingmod/transactionRecordFT'),
    loading() {
        return <Loading />
    }
});
const CloseRecordFT = Loadable({//期货平仓记录
    loader: () => import('./tradecenter/futurestradingmod/closeRecordFT'),
    loading() {
        return <Loading />
    }
});
const StrongManageFT = Loadable({//期货强平管理
    loader: () => import('./tradecenter/futurestradingmod/strongManageFT'),
    loading() {
        return <Loading />
    }
});
const AutoLightenManageFT = Loadable({//期货自动减仓管理
    loader: () => import('./tradecenter/futurestradingmod/autoLightenManageFT'),
    loading() {
        return <Loading />
    }
});
const InsuranceFundsManageFT = Loadable({//期货保险基金管理
    loader: () => import('./tradecenter/futurestradingmod/insuranceFundsManageFT'),
    loading() {
        return <Loading />
    }
});
const FundsRateManageFT = Loadable({//期货资金费率管理
    loader: () => import('./tradecenter/futurestradingmod/fundsRateManageFT'),
    loading() {
        return <Loading />
    }
});
/** 对账中心 start */
const Futures = Loadable({//期货账户对账
    loader: () => import('./tradecenter/reconciliationcenter/futures'),
    loading() {
        return <Loading />
    }
});
const CoinCurrencyAccount = Loadable({//币币账户对账
    loader: () => import('./tradecenter/reconciliationcenter/coinCurrencyAccount'),
    loading() {
        return <Loading />
    }
});
const NewCoinCurrencyAccount = Loadable({//币币账户对账
    loader: () => import('./tradecenter/reconciliationcenter/newCoinCurrencyAccount'),
    loading() {
        return <Loading />
    }
});
const LegalCoinCurrencyAccountOTC = Loadable({//法币账户对账
    loader: () => import('./tradecenter/reconciliationcenter/legalCoinCurrencyAccountOTC'),
    loading() {
        return <Loading />
    }
});
const OtcCurrencyAccount = Loadable({//OTC账户对账
    loader: () => import('./tradecenter/reconciliationcenter/otcCurrencyAccount'),
    loading() {
        return <Loading />
    }
});
const TradingPlatformWallet = Loadable({//交易平台钱包对账
    loader: () => import('./tradecenter/reconciliationcenter/tradingPlatformWallet'),
    loading() {
        return <Loading />
    }
});
const PlatformReconciliation = Loadable({//交易平台对账
    loader: () => import('./tradecenter/reconciliationcenter/platformReconciliation'),
    loading() {
        return <Loading />
    }
});
// const Wallet = Loadable({//区块钱包对账
//     loader: () => import('./tradecenter/reconciliationcenter/wallet'),
//     loading() {
//       return <Loading />
//     }
// });
const WalletVsPlatform = Loadable({//区块钱包对账vs交易平台对账
    loader: () => import('./tradecenter/reconciliationcenter/walletVsPlatform'),
    loading() {
        return <Loading />
    }
});
/** 对账中心 end */
/** 统计报表 start */
const StatementChangeOfCapital = Loadable({//资金变动表
    loader: () => import('./tradecenter/statisticalReports/statementChangeOfCapital'),
    loading() {
        return <Loading />
    }
});
const IncomeSummary = Loadable({//收益汇总表
    loader: () => import('./tradecenter/statisticalReports/incomeSummary'),
    loading() {
        return <Loading />
    }
});
const CurrencyTradingSummary = Loadable({//币币交易汇总表
    loader: () => import('./tradecenter/statisticalReports/currencyTradingSummary'),
    loading() {
        return <Loading />
    }
});
const CurrencyTradingDetail = Loadable({//币币总账
    loader: () => import('./tradecenter/statisticalReports/currencyTradingDetail'),
    loading() {
        return <Loading />
    }
});
const FuturesTradingFee = Loadable({//期货手续费
    loader: () => import('./tradecenter/statisticalReports/futuresTradingFee'),
    loading() {
        return <Loading />
    }
});

const FuturesTradingSummary = Loadable({//期货交易汇总
    loader: () => import('./tradecenter/statisticalReports/futuresTradingSummary'),
    loading() {
        return <Loading />
    }
});
/** 统计报表 end */
/** 用户资金 start */
const CoinUserMoney = Loadable({//币币用户资金
    loader: () => import('./tradecenter/moneycenter/coinUserMoney'),
    loading() {
        return <Loading />
    }
});
const FundsTransfer = Loadable({//资金划转
    loader: () => import('./tradecenter/moneycenter/fundsTransfer'),
    loading() {
        return <Loading />
    }
});
const FuturesUserMoney = Loadable({//期货用户资金
    loader: () => import('./tradecenter/moneycenter/futuresUserMoney'),
    loading() {
        return <Loading />
    }
});
const OtcUserMoney = Loadable({//otc用户资金
    loader: () => import('./tradecenter/moneycenter/otcUserMoney'),
    loading() {
        return <Loading />
    }
});
const UserGeneralCapitalSubsidiary = Loadable({//用户总资金明细
    loader: () => import('./tradecenter/moneycenter/userGeneralCapitalSubsidiary'),
    loading() {
        return <Loading />
    }
});


const PlatformFeeWithdrawal = Loadable({//平台手续费提现明细
    loader: () => import('./financialcenter/operataccountmod/platformFeeWithdrawal'),
    loading() {
        return <Loading />
    }
});
const ConfirmAccountEntry = Loadable({//确认入账
    loader: () => import('./financialcenter/operataccountmod/confirmAccountEntry'),
    loading() {
        return <Loading />
    }
}); const OperatWithdrawApprove = Loadable({//运营提现审核
    loader: () => import('./financialcenter/operataccountmod/operatWithdrawApprove'),
    loading() {
        return <Loading />
    }
});
const OperatAccountWithdrawal = Loadable({//运营账户提现
    loader: () => import('./financialcenter/operataccountmod/operatAccountWithdrawal'),
    loading() {
        return <Loading />
    }
});
const Checkaccounts = Loadable({//频道对到账户
    loader: () => import('./riskmanagement/checkaccounts/checkaccounts'),
    loading() {
        return <Loading />
    }
});
const Accountmonitoring = Loadable({//频繁挂单撤单的账户
    loader: () => import('./riskmanagement/accountmonitoring/accountmonitoring'),
    loading() {
        return <Loading />
    }
});
const Cancelaccount = Loadable({//关键信息修改预警
    loader: () => import('./riskmanagement/cancelaccount/cancelaccount'),
    loading() {
        return <Loading />
    }
});
const Connectedaccout = Loadable({//关联账户
    loader: () => import('./riskmanagement/connectedaccout/connectedaccout'),
    loading() {
        return <Loading />
    }
});
const Frequentlyonput = Loadable({//大额账户监控
    loader: () => import('./riskmanagement/frequentlyonput/frequentlyonput'),
    loading() {
        return <Loading />
    }
});
const Keyinformationchange = Loadable({//大额挂单账户
    loader: () => import('./riskmanagement/keyinformationchange/keyinformationchange'),
    loading() {
        return <Loading />
    }
});
const Tradealertment = Loadable({//交易预警
    loader: () => import('./riskmanagement/tradealertment/tradealertment'),
    loading() {
        return <Loading />
    }
});
const Globalpandect = Loadable({//风控总览
    loader: () => import('./riskmanagement/globalpandect/globalpandect'),
    loading() {
        return <Loading />
    }
});

//////////////////////////////////////////////
const NumberofWarning = Loadable({//保值下单数量异常
    loader: () => import('./keepvalueofwarning/numberofwarning/numberofwarning'),
    loading() {
        return <Loading />
    }
});



const AbnormalSummary = Loadable({//保值下单数量异常汇总
    loader: () => import('./keepvalueofwarning/numberofwarning/abnormalSummary'),
    loading() {
        return <Loading />
    }
});
const MarketStatistics = Loadable({//市场访问统计
    loader: () => import('./keepvalueofwarning/numberofwarning/marketStatistics'),
    loading() {
        return <Loading />
    }
});

const PrewarningValue = Loadable({//资金低于预警值
    loader: () => import('./keepvalueofwarning/prewarningvalue/prewarningvalue'),
    loading() {
        return <Loading />
    }
});
const RecordAbnormal = Loadable({//保值记录异常
    loader: () => import('./keepvalueofwarning/recordabnormal/recordabnormal'),
    loading() {
        return <Loading />
    }
});
const ValueofWarning = Loadable({//保值异常
    loader: () => import('./keepvalueofwarning/valueofwarning/valueofwarning'),
    loading() {
        return <Loading />
    }
});
const TopUptoMoney = Loadable({//充值提现管理
    loader: () => import('./systemcenter/topup/topupMoney'),
    loading() {
        return <Loading />
    }
});

const BrushAccountFunds = Loadable({//刷量账号资金低于预警
    loader: () => import('./keepvalueofwarning/quotesabnormalmonitormod/brushAccountFunds'),
    loading() {
        return <Loading />
    }
})
const ExtranetQuotesGet = Loadable({//外网行情获取失败预警
    loader: () => import('./keepvalueofwarning/quotesabnormalmonitormod/extranetQuotesGet'),
    loading() {
        return <Loading />
    }
})
const HandicapDepthBelowWarning = Loadable({//盘口深度低于预警值报警
    loader: () => import('./keepvalueofwarning/quotesabnormalmonitormod/handicapDepthBelowWarning'),
    loading() {
        return <Loading />
    }
});
const QuantizateProgramStopAlarm = Loadable({//量化程序停止报警
    loader: () => import('./keepvalueofwarning/quotesabnormalmonitormod/quantizateProgramStopAlarm'),
    loading() {
        return <Loading />
    }
});
const QuotesDeviate = Loadable({//行情偏离
    loader: () => import('./keepvalueofwarning/quotesabnormalmonitormod/quotesDeviate'),
    loading() {
        return <Loading />
    }
});
const UserTransForAmountFunds = Loadable({//用户成交量占比
    loader: () => import('./keepvalueofwarning/quotesabnormalmonitormod/userTransForAmountFunds'),
    loading() {
        return <Loading />
    }
});
const Recommend = Loadable({//推荐币管理
    loader: () => import('./systemcenter/recommend/recommends'),
    loading() {
        return <Loading />
    }
});
// const Warehousewear = Loadable({//穿仓单
//     loader: () => import('./futureswarning/tradealertment/warehousewear'),
//     loading() {
//         return <Loading />
//     }
// });
// const Entrustpupil = Loadable({//委托多空比
//     loader: () => import('./futureswarning/tradealertment/entrustpupil'),
//     loading() {
//         return <Loading />
//     }
// });
// const Makebargain = Loadable({//成交单异常
//     loader: () => import('./futureswarning/tradealertment/makebargain'),
//     loading() {
//         return <Loading />
//     }
// });
// const Positionpupli = Loadable({//持仓多空比
//     loader: () => import('./futureswarning/tradealertment/positionpupli'),
//     loading() {
//         return <Loading />
//     }
// });
// const Reducestock = Loadable({//减仓单
//     loader: () => import('./futureswarning/tradealertment/reducestock'),
//     loading() {
//         return <Loading />
//     }
// });
// const Strongflat = Loadable({//强平单
//     loader: () => import('./futureswarning/tradealertment/strongflat'),
//     loading() {
//         return <Loading />
//     }
// });

//总账风控总览
const Variationexamine = Loadable({//总账风控总览
    loader: () => import('./collectbill/variationexamine'),
    loading() {
        return <Loading />
    }
});
const CoinMarketReport = Loadable({//币币市场报表
    loader: () => import('./tradecenter/statisticalReports/coinMarketReport'),
    loading() {
        return <Loading />
    }
});
//市场管理
const Userquery = Loadable({//用户查询
    loader: () => import('./marketregulation/userquery'),
    loading() {
        return <Loading />
    }
})
const PlatformMeltingOverview = Loadable({//平台熔断总览
    loader: () => import('./riskmanagement/globalpandect/platformMeltingOverview'),
    loading() {
        return <Loading />
    }
})


// ---------wm20190303 add
//币币交易业务
const AccountInfor = Loadable({//盈利账户
    loader: () => import('./riskmanagement/ProfitAccount/accountInfor'),
    loading() {
        return <Loading />
    }
})

//充提异常账户
const WithdrawError = Loadable({// 提现异常
    loader: () => import('./riskmanagement/moneyUnusual/withdrawError'),
    loading() {
        return <Loading />
    }
})

const SingleDayWithdrawAll = Loadable({// 单日累计提现
    loader: () => import('./riskmanagement/moneyUnusual/singleDayWithdrawAll'),
    loading() {
        return <Loading />
    }
})
const SingleBigWithdraw = Loadable({// 单笔大额提现
    loader: () => import('./riskmanagement/moneyUnusual/singleBigWithdraw'),
    loading() {
        return <Loading />
    }
})

const SingleCionDayRecharge = Loadable({// 单币种日累计充值
    loader: () => import('./riskmanagement/moneyUnusual/singleCionDayRecharge'),
    loading() {
        return <Loading />
    }
})
const SingleBigRecharge = Loadable({// 单笔大额充值
    loader: () => import('./riskmanagement/moneyUnusual/singleBigRecharge'),
    loading() {
        return <Loading />
    }
})
const BalanceBack = Loadable({// 提现后账户余额为X
    loader: () => import('./riskmanagement/moneyUnusual/balanceBack'),
    loading() {
        return <Loading />
    }
})

const VirtualFundsError = Loadable({// 虚拟资金异常
    loader: () => import('./riskmanagement/capitalError/virtualFundsError'),
    loading() {
        return <Loading />
    }
})

// ---------wm20190303 add
const MarketControl = Loadable({//市场推广
    loader: () => import('./systemcenter/market/marketControl'),
    loading() {
        return <Loading />
    }
});
const PersonalBlow = Loadable({// 个人熔断
    loader: () => import('./riskmanagement/fusemod/personalBlow'),
    loading() {
        return <Loading />
    }
})
const SmallPayManagement = Loadable({// 小额打币币种管理
    loader: () => import('./monitorcenter/smallautopaymod/smallPayManagement'),
    loading() {
        return <Loading />
    }
})
const OrderSubtask = Loadable({// 子任务下单
    loader: () => import('./tradecenter/ordersubtaskmod/orderSubtask'),
    loading() {
        return <Loading />
    }
})
const BrushAmountMaintenance = Loadable({// 刷量账号虚拟资金设置
    loader: () => import('./tradecenter/ordersubtaskmod/brushAmountMaintenance'),
    loading() {
        return <Loading />
    }
})
const AllDealRecord = Loadable({// 总币币成交记录
    loader: () => import('./tradecenter/marketmod/allDealRecord'),
    loading() {
        return <Loading />
    }
})
const WalletBFee = Loadable({// b钱包对账
    loader: () => import('./tradecenter/paymentcenterbalancemod/walletBFee'),
    loading() {
        return <Loading />
    }
})
const BibiDealRecord = Loadable({// 币币成交记录
    loader: () => import('./tradecenter/marketmod/bibiDealRecord'),
    loading() {
        return <Loading />
    }
})
const UserEntrustRecord = Loadable({// 用户委托记录
    loader: () => import('./tradecenter/marketmod/userEntrustRecord'),
    loading() {
        return <Loading />
    }
})

const RechargeInstructions = Loadable({// 充值说明
    loader: () => import('./systemcenter/sysmod/rechargeInstructions'),
    loading() {
        return <Loading />
    }
})

const HedgeUserTransRecordMange = Loadable({// 用户成交记录管理
    loader: () => import('./tradecenter/hedgemod/hedgeUserTransRecordMange'),
    loading() {
        return <Loading />
    }
})
const CurrencyManagement = Loadable({// 币币账户管理
    loader: () => import('./tradecenter/hedgemod/currencyManagement'),
    loading() {
        return <Loading />
    }
})


const RecordHedgedTrading = Loadable({// 资币方保值，操盘记录
    loader: () => import('./tradecenter/capitalCurrencyStatement/recordHedgedTrading'),
    loading() {
        return <Loading />
    }
})
const CockpitOverview = Loadable({// 驾驶舱
    loader: () => import('./cockpitcenter/chartsmod/cockpitOverview'),
    loading() {
        return <Loading />
    }
})
const RecordHedgedRecharge = Loadable({// 资方币保值、操盘充提记录
    loader: () => import('./tradecenter/capitalCurrencyStatement/recordHedgedRecharge'),
    loading() {
        return <Loading />
    }
})
const TradeTotal = Loadable({// 驾驶舱-交易统计
    loader: () => import('./cockpitcenter/chartsmod/tradeTotal'),
    loading() {
        return <Loading />
    }
})
const MessageManagePlatform = Loadable({// 消息平台管理
    loader: () => import('./systemcenter/message/messageManagePlatform'),
    loading() {
        return <Loading />
    }
})
const AmountTotal = Loadable({// 驾驶舱-金额统计
    loader: () => import('./cockpitcenter/chartsmod/amountTotal'),
    loading() {
        return <Loading />
    }
})

const UserFlowTotal = Loadable({// 驾驶舱-流量统计
    loader: () => import('./cockpitcenter/chartsmod/userFlowTotal'),
    loading() {
        return <Loading />
    }
})
const ProjectTotal = Loadable({// 驾驶舱-项目统计
    loader: () => import('./cockpitcenter/chartsmod/projectTotal'),
    loading() {
        return <Loading />
    }
})
const FinancialAccountReconFM = Loadable({// 理财账户对账
    loader: () => import('./tradecenter/reconciliationcenter/financialAccountReconFM'),
    loading() {
        return <Loading />
    }
})
const FinancialUserMoneyFM = Loadable({// 理财用户资金
    loader: () => import('./tradecenter/moneycenter/financialUserMoneyFM'),
    loading() {
        return <Loading />
    }
})

const FinancialDetailFM = Loadable({// 理财流水
    loader: () => import('./tradecenter/billingdetailsmod/financialDetailFM'),
    loading() {
        return <Loading />
    }
});
const UserFinancialInquiryFM = Loadable({// 用户理财查询
    loader: () => import('./userfinancialcenter/userfinancialmod/userFinancialInquiryFM'),
    loading() {
        return <Loading />
    }
});
const SuperNodeManagementFM = Loadable({// 超级节点管理
    loader: () => import('./userfinancialcenter/userfinancialmod/superNodeManagementFM'),
    loading() {
        return <Loading />
    }
});
const DevidedBlockConfigFM = Loadable({// 分红区块配置
    loader: () => import('./userfinancialcenter/userfinancialmod/devidedBlockConfigFM'),
    loading() {
        return <Loading />
    }
});


const  Userfinancialinfo = Loadable({// 用户理财信息
    loader: () => import('./userfinancialcenter/userfinancialmod/userfinancialinfo'),
    loading() {
        return <Loading />
    }
});
const  Proinvestment = Loadable({// 用户投资信息
    loader: () => import('./userfinancialcenter/userfinancialmod/proinvestment'),
    loading() {
        return <Loading />
    }
});


const  DividendQuery = Loadable({// vip分红查询
    loader: () => import('./userfinancialcenter/userfinancialmod/dividendQuery'),
    loading() {
        return <Loading />
    }
});

const  EcoQuery = Loadable({// 生态回馈查询
    loader: () => import('./userfinancialcenter/userfinancialmod/ecoQuery'),
    loading() {
        return <Loading />
    }
});
const  NewpersonQuery = Loadable({// 新人加成查询
    loader: () => import('./userfinancialcenter/userfinancialmod/newpersonQuery'),
    loading() {
        return <Loading />
    }
});

const  FinancialTask = Loadable({// 理财TASK
    loader: () => import('./userfinancialcenter/userfinancialmod/financialTask'),
    loading() {
        return <Loading />
    }
});

const  VipDistribution = Loadable({// vip新人分配记录
    loader: () => import('./userfinancialcenter/userfinancialmod/vipDistribution'),
    loading() {
        return <Loading />
    }
});
const  NodeOutputQueryFM = Loadable({// 节点产出查询
    loader: () => import('./userfinancialcenter/userfinancialmod/nodeOutputQueryFM'),
    loading() {
        return <Loading />
    }
});

const  NodeCompute = Loadable({// 节点数量计算
    loader: () => import('./userfinancialcenter/userfinancialmod/nodeCompute'),
    loading() {
        return <Loading />
    }
});
const  VdsMarket = Loadable({// vds行情
    loader: () => import('./userfinancialcenter/userfinancialmod/vdsMarket'),
    loading() {
        return <Loading />
    }
});
const  MerchantCertification = Loadable({// OTC商家认证审核
    loader: () => import('./systemcenter/usermod/merchantCertification'),
    loading() {
        return <Loading />
    }
});
const TradingSummaryOTC = Loadable({// OTC交易汇总报表
    loader: () => import('./tradecenter/statisticalReports/tradingSummaryOTC'),
    loading() {
        return <Loading />
    }
});
/** 用户资金 end */
/**
Futures
CoinCurrencyAccount
TradingPlatformWallet
PlatformReconciliation
Wallet
WalletVsPlatform

StatementChangeOfCapital
IncomeSummary
CurrencyTradingSummary
FuturesTradingFee
FuturesTradingSummary

CoinUserMoney
FundsTransfer
FuturesUserMoney
OtcUserMoney
UserGeneralCapitalSubsidiary
 */
/**
 * @author oliver
 * @description 用户分析表
 * @start
 */
const UserChargeTotalAnalysisReport = Loadable({// 用户充提总分析报表
    loader: () => import('./reportcenter/userreportmod/userChargeTotalAnalysisReport'),
    loading() {
        return <Loading />
    }
});
const UserChargeCurrency = Loadable({// 用户充提币种
    loader: () => import('./reportcenter/userreportmod/userChargeCurrency'),
    loading() {
        return <Loading />
    }
});
const HandicapUserTransactions = Loadable({// 盘口用户交易
    loader: () => import('./reportcenter/userreportmod/handicapUserTransactions'),
    loading() {
        return <Loading />
    }
});
const UserAndBrushBuyAnalysis = Loadable({// 用户与刷量买入分析
    loader: () => import('./reportcenter/userreportmod/userAndBrushBuyAnalysis'),
    loading() {
        return <Loading />
    }
});
const UserAndBrushSalesAnalysis = Loadable({// 用户与刷量卖出分析
    loader: () => import('./reportcenter/userreportmod/userAndBrushSalesAnalysis'),
    loading() {
        return <Loading />
    }
});
const UserAndUserTransactionAnalysis = Loadable({// 用户与用户成交分析
    loader: () => import('./reportcenter/userreportmod/userAndUserTransactionAnalysis'),
    loading() {
        return <Loading />
    }
});

const Test = Loadable({// test
    loader: () => import('./test'),
    loading() {
        return <Loading />
    }
});
const APIAccessData = Loadable({// API访问数据
    loader: () => import('./reportcenter/userreportmod/APIAccessData'),
    loading() {
        return <Loading />
    }
});

/**********************************************************     end   */


const ComponentList = {
    'Home': <Home />,//首页
    'operTask': OperTask,//工作台 任务查询


    inputPage: InputPage,//新页面


    'finance': Finance,  //
    'accountManage': AccountManage, //账户管理
    'settleMent': SettleMent, //每日结算
    'capitalUse': CapitalUse,//收支用途
    'transferSettlement': TransferSettlement,//划转结算

    'billDetail': BillDetail, //账单明细-币币
    'billDetailOTC': BillDetailOTC, //账单明细-OTC
    'billDetailWallet': BillDetailWallet, //账单明细-钱包

    // 'userCapital':UserCapital,//用户资金-币币
    // 'userCapitalOTC':UserCapitalOTC,//用户资金-OTC
    'userCapitalWallet': UserCapitalWallet,//用户资金-钱包
    // 'userCapitalSummary':UserCapitalSummary,//用户资金汇总

    'capitalCount': CapitalCount,//币币资金统计
    'capitalCountOTC': CapitalCountOTC,//法币资金统计
    'capitalCountWallet': CapitalCountWallet,//钱包资金统计

    'feeProfit': FeeProfit,//手续费收益->币币收益

    'rechargeRecord': RechargeRecord,//充值记录
    'rechargeAddress': RechargeAddress,//充值地址
    'withdrawApprove': WithdrawApprove,//提现审核
    // 'rechargeAudit':RechargeAudit,//充值审核
    'withdrawAddress': WithdrawAddress,//提现地址
    'withdrawRecord': WithdrawRecord,//提现查询//提现查询

    'functionManage': FunctionManage,//功能管理
    'functionRole': FunctionRole,//角色权限
    'operManage': OperManage,//操作员管理

    'userInfo': UserInfo,//用户信息
    'loginInfo': LoginInfo,//登录信息
    'identificationInfo': IdentificationInfo,//认证信息
    'blackList': BlackList,//黑名单管理
    'mailManage': MailManage,//邮箱管理
    'webconfig':Webconfig,//网站底部配置

    
    'integralBill': IntegralBill,//积分流水
    'integralRule': IntegralRule,//积分规则
    'integralVipRule': IntegralVipRule,//积分等级规则
    'userVip': UserVip,//用户等级

    'identityAuthentication': IdentityAuthentication, //实名认证
    'googleVerify': GoogleVerify, //Google审核
    'phoneVerify': PhoneVerify, //手机审核
    'closeSecondVerify': CloseSecondVerify,//关闭二次验证

    'entrustRecord': EntrustRecord,//委托记录
    'planEntrustRecord': PlanEntrustRecord,//计划委托记录

    'operLog': OperLog, //操作日志
    'trajectoryLog': TrajectoryLog, //轨迹日志

    'messageQuery': MessageQuery, //消息查询
    'messageSend': MessageSend,   //消息发送
    'mouldSend': MouldSend,   //模版发送
    'messageRuleMould': MessageRuleMould,   //消息规则模版

    'marketTrade': MarketTrade,  //盘口交易
    'dealRecord': DealRecord,     //成交记录
    'marketMergeTrade': MarketMergeTrade, //盘口合并交易记录
    'batchEntrust': BatchEntrust, //批量挂单
    'batchWithdrawal': BatchWithdrawal, //批量撤单

    'voteManage': VoteManage,  //投票管理
    'drawManage': DrawManage,  //抽奖管理
    'distributeVote': DistributeVote, //投票发布
    'currencyIntroduction': CurrencyIntroduction,//币种介绍

    'newsManage': NewsManage, //新闻管理
    'newNewsManage': NewNewsManage,
    'appManage': AppManage,//客户端管理
    'sysDictionary': SysDictionary,//系统字典   
    'deptManage': DeptManage,//部门管理  
    'sysNotice': SysNotice,//系统公告       

    'brushParameter': BrushParameter,  //GBC参数管理
    'brushTask': BrushTask, //GBC任务管理

    'brushParameterManage': BrushParameterManage,  //量化参数管理
    'brushTaskManage': BrushTaskManage,  //量化任务管理  
    'hangingOrderTaskManage': HangingOrderTaskManage,//自成交任务
    'selfSaleTaskManage': SelfSaleTaskManage,//自成交任务
    'oldselfSaleTaskManage': OldSelfSaleTaskManage,//旧的自成交任务
    'hangingOrderParameterManage': HangingOrderParameterManage,//挂撤单配置
    'selfSaleParameterManage': SelfSaleParameterManage,//自成交配置
    'oldselfSaleParameterManage': OldSelfSaleParameterManage,//旧的自成交配置

    'hedgingTradeRecord': HedgingTradeRecord,//对冲交易记录
    'hedgeAccountFinancial': HedgeAccountFinancial,//报纸财务账户
    'hedgeAccountState': HedgeAccountState,//保值账户状态
    'hedgeBalance': HedgeBalance,//保值对账
    'hedgeRecordBalance': HedgeRecordBalance,//对冲记录对账

    'hedgeContractTransactionRecord': HedgeContractTransactionRecord,//对冲合约交易记录

    'rechargeBalance': RechargeBalance,//充值对账
    'rechargeBalanceRisk': RechargeBalanceRisk,//充值对账（风控）
    'walletBalance': WalletBalance,//钱包每日对账
    'walletBalanceRisk': WalletBalanceRisk,//钱包每日对账(风控)
    'walletBill': WalletBill,//钱包流水
    'walletBillDetail': WalletBillDetail,//钱包流水明细
    'withdrawBalance': WithdrawBalance,//提现对账
    'withdrawBalanceRisk': WithdrawBalanceRisk,//提现对账（风控）

    'walletTradePlatformLedger': WalletTradePlatformLedger,//钱包VS交易平台总账

    'centerCapitalDaily': CenterCapitalDaily,//平台资金日报
    'centerCapitalSum': CenterCapitalSum,//平台资金累计
    'capitalAccount': CapitalAccount,//交易平台资金总账
    'userBaseOperCount': UserBaseOperCount,//用户登陆注册统计
    'userPaymentTradeCount': UserPaymentTradeCount,//用户冲提交易统计
    'userPositionCount': UserPositionCount,//用户持仓统计
    'mars': Mars,//火星表
    'walletCapital': WalletCapital,//钱包收支表
    'gbcRepoTrack': GbcRepoTrack,//GBC回购跟踪
    'gbcRepoCount': GbcRepoCount,
    'gbcDividendInCount': GbcDividendInCount,//分红转入统计
    'gbcInJackpotCount': GbcInJackpotCount,
    'gbcDividendCount': GbcDividendCount,
    'gbcDividendShareCount': GbcDividendShareCount,

    'smallAutoPayRecord': SmallAutoPayRecord,//小额自动打币记录
    'smallPayTask': SmallPayTask,//小额打币定时任务

    'userCapitalMonitor': UserCapitalMonitor,//用户资金监控
    'backCapital': BackCapital,//GBC回购管理
    'backCapitalCoords': BackCapitalCoords,//私钥坐标管理
    'walletxFee': WalletxFee,//X钱包对账

    'marketConfig': MarketConfig,//市场配置
    'marketConfigSS': MarketConfigSS,//市场配置_之前版本备份
    'cloudStorage': CloudStorage,//云存储配置
    'generalConfig': GeneralConfig,//通用配置
    'marketList': MarketList,//市场列表
    'bankManage': BankManage,//银行管理
    'onlineDeploy': OnlineDeploy,//在线人数配置
    'APIparamsConfig': APIparamsConfig,//Api 开关配置
    'featuresLock': FeaturesLock,//功能锁
    'currencyManageOTC': CurrencyManageOTC,//币种管理-OTC
    'otcPaymentType': OtcPaymentType,//支付方式
    'scheduledManage': ScheduledManage,//定时任务管理
    'advertisingSpaceManage': AdvertisingSpaceManage,//广告位管理
    'advertisingPhotoManage': AdvertisingPhotoManage,//广告图片管理
    'advertisingPhotoManageOTC': AdvertisingPhotoManageOTC,//OTC广告图片管理

    'advertManage': AdvertManage,//广告管理
    'appealManage': AppealManage,//申诉管理
    'orderManage': OrderManage,//订单管理

    'friendLink': FriendLink,//合作链接

    'capitalChangeRecordFT': CapitalChangeRecordFT,//资金变动记录
    'leverAdjustRecordFT': LeverAdjustRecordFT,//杠杆调整记录
    'marginCallRecordFT': MarginCallRecordFT,//追加保证金记录
    'transactionBalanceRecordFT': TransactionBalanceRecordFT,// 成交结余记录

    'handicapMergerTransactionFT': HandicapMergerTransactionFT,//期货盘口合并交易
    'handicapTradingFT': HandicapTradingFT,//期货盘口交易
    'entrustmentDetailsFT': EntrustmentDetailsFT,//期货委托明细
    'mochikuraDetailsFT': MochikuraDetailsFT,//期货持仓明细
    'transactionRecordFT': TransactionRecordFT,//期货成交记录
    'closeRecordFT': CloseRecordFT,//期货平仓记录
    'strongManageFT': StrongManageFT,//期货强平管理
    'autoLightenManageFT': AutoLightenManageFT,//期货自动减仓管理
    'insuranceFundsManageFT': InsuranceFundsManageFT,//期货保险基金管理
    'fundsRateManageFT': FundsRateManageFT,//期货资金费率管理


    "futures": Futures,//期货对账
    "coinCurrencyAccount": CoinCurrencyAccount,//币币对账（研发）
    'newCoinCurrencyAccount': NewCoinCurrencyAccount,//币币对账
    "legalCoinCurrencyAccountOTC": LegalCoinCurrencyAccountOTC, // 法币对账
    'otcCurrencyAccount': OtcCurrencyAccount,//OTC账户对账
    "tradingPlatformWallet": TradingPlatformWallet,//交易平台钱包对账
    "platformReconciliation": PlatformReconciliation,//交易平台对账
    // "wallet":Wallet,//区块钱包对账
    "walletVsPlatform": WalletVsPlatform,//区块钱包vs交易平台对账
    "statementChangeOfCapital": StatementChangeOfCapital,//资金变动表
    "incomeSummary": IncomeSummary,//收益汇总表
    "currencyTradingSummary": CurrencyTradingSummary,//币币交易汇总表
    'currencyTradingDetail': CurrencyTradingDetail,//币币总账
    "futuresTradingFee": FuturesTradingFee,//期货手续费明细表
    "futuresTradingSummary": FuturesTradingSummary,//期货交易汇总表
    "coinUserMoney": CoinUserMoney,//币币用户资金
    "fundsTransfer": FundsTransfer,//资金划转
    "futuresUserMoney": FuturesUserMoney,//期货用户资金
    "otcUserMoney": OtcUserMoney,//otc用户资金
    "userGeneralCapitalSubsidiary": UserGeneralCapitalSubsidiary,//用户总资金明细

    'platformFeeWithdrawal': PlatformFeeWithdrawal,//平台手续费提现明细
    'confirmAccountEntry': ConfirmAccountEntry,//确认入账
    'operatWithdrawApprove': OperatWithdrawApprove,//运营提现审核
    'operatAccountWithdrawal': OperatAccountWithdrawal,//运营账户提现



    'checkaccounts': Checkaccounts,//频道对到账户
    'accountmonitoring': Accountmonitoring,//频繁挂单撤单的账户
    'cancelaccount': Cancelaccount,//关键信息修改预警
    'connectedaccout': Connectedaccout,//关联账户
    'frequentlyonput': Frequentlyonput,//大额账户监控
    'keyinformationchange': Keyinformationchange,//大额挂单账户
    'tradealertment': Tradealertment,//交易预警
    'globalpandect': Globalpandect,//风控总览



    'numberofwarning': NumberofWarning, //数量异常
    'valueofwarning': ValueofWarning,  //保值异常
    'recordabnormal': RecordAbnormal,  //保值记录异常
    'prewarningvalue': PrewarningValue, //资金低于预警值
    'abnormalSummary':AbnormalSummary,//保值下单数量异常汇总
    'marketStatistics':MarketStatistics,//市场访问统计


    'brushAccountFunds': BrushAccountFunds, //刷量账号资金低于预警
    'extranetQuotesGet': ExtranetQuotesGet,//外网行情获取失败预警
    'handicapDepthBelowWarning': HandicapDepthBelowWarning,//盘口深度低于预警值报警
    'quantizateProgramStopAlarm': QuantizateProgramStopAlarm,//量化程序停止报警
    'userTransForAmountFunds': UserTransForAmountFunds,//用户成交量占比
    'quotesDeviate': QuotesDeviate, //行情偏离

    'topupMoney': TopUptoMoney,//充值提现管理,
    'recommends': Recommend,//推荐币管理
    // 'entrustpupil': Entrustpupil,//委托多空比
    // 'makebargain': Makebargain,//成交单异常
    // 'positionpupli': Positionpupli,//持仓多空比
    // 'reducestock': Reducestock,//减仓单
    // 'strongflat': Strongflat,//强平单
    // 'warehousewear': Warehousewear,//穿仓单
    'variationexamine': Variationexamine,//总账风控总览


    'coinMarketReport': CoinMarketReport,//币币市场报表
    'userquery': Userquery,//市场管理
    'platformMeltingOverview': PlatformMeltingOverview,//平台熔断总览

    //wm 20190303 add
    "accountInfor": AccountInfor,//盈利账户
    "withdrawError": WithdrawError,//提现异常
    "singleDayWithdrawAll": SingleDayWithdrawAll,//单日累计提现
    "singleBigWithdraw": SingleBigWithdraw,// 单笔大额提现
    "singleCionDayRecharge": SingleCionDayRecharge,// 单币种日累计充值
    "singleBigRecharge": SingleBigRecharge,// 单笔大额充值
    "balanceBack": BalanceBack,// 提现后账户余额为X
    "virtualFundsError": VirtualFundsError, //虚拟资金异常

    "personalBlow": PersonalBlow,// 个人熔断

    'marketControl': MarketControl,//市场推广页

    'smallPayManagement': SmallPayManagement,//小额打币币种管理

    'orderSubtask': OrderSubtask,
    'brushAmountMaintenance': BrushAmountMaintenance,
    'allDealRecord': AllDealRecord,
    'walletBFee': WalletBFee,
    'bibiDealRecord': BibiDealRecord,
    'userEntrustRecord': UserEntrustRecord,
    'rechargeInstructions': RechargeInstructions, //充值说明

    'hedgeUserTransRecordMange': HedgeUserTransRecordMange,//用户成交记录管理
    'currencyManagement':CurrencyManagement,//币币账户管理
    'recordHedgedTrading': RecordHedgedTrading,// 资币方保值，操盘记录
    'recordHedgedRecharge': RecordHedgedRecharge,// 资方币保值、操盘充提记录

    'messageManagePlatform': MessageManagePlatform,
    'financialDetailFM': FinancialDetailFM, // 理财流水明细
    'financialUserMoneyFM': FinancialUserMoneyFM,
    'financialAccountReconFM': FinancialAccountReconFM,

    'userFinancialInquiryFM':UserFinancialInquiryFM,
    'superNodeManagementFM':SuperNodeManagementFM,
    'devidedBlockConfigFM':DevidedBlockConfigFM,
    'merchantCertification':MerchantCertification,
    'tradingSummaryOTC':TradingSummaryOTC,
    'vdsMarket':VdsMarket,//vds行情

    'cockpitOverview': CockpitOverview,  //
    'tradeTotal': TradeTotal,
    'amountTotal': AmountTotal,
    'userFlowTotal': UserFlowTotal,
    'projectTotal': ProjectTotal,
    'financialTask':FinancialTask,//定时任务管理
    'vipDistribution':VipDistribution,//vip新人分配记录
    'dividendQuery':DividendQuery,//vip分红查询
    'ecoQuery':EcoQuery,//生态回馈查询
    'newpersonQuery':NewpersonQuery,//新人加成查询
    'userfinancialinfo':Userfinancialinfo,//用户理财信息
    'proinvestment':Proinvestment,//用户投资信息
    'nodeOutputQueryFM':NodeOutputQueryFM,
    'nodeCompute':NodeCompute,//节点数量计算
    

    'userChargeTotalAnalysisReport': UserChargeTotalAnalysisReport,
    'userChargeCurrency': UserChargeCurrency,
    'handicapUserTransactions': HandicapUserTransactions,
    'userAndBrushBuyAnalysis': UserAndBrushBuyAnalysis,
    'userAndBrushSalesAnalysis': UserAndBrushSalesAnalysis,
    'userAndUserTransactionAnalysis': UserAndUserTransactionAnalysis,


    'textConfig':TextConfig,
    'test':Test,
    'APIAccessData':APIAccessData,
    'userDataManage':UserDataManage,//用户资料管理
}

export default ComponentList;
