const webpack = require('webpack');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common,{
    devServer: {
        inline: true,
        historyApiFallback: true,
        host: '0.0.0.0',
        port: 3000,
        proxy: [
            /**
             *              ip                          定时任务
             * 凯里         192.168.3.18:8999
             * 卜宪冠       192.168.3.19:8080
             * 建芳         192.168.3.23:8999           192.168.3.23:8088
             * 刘冰         192.168.3.241:8999
             * 文图         192.168.3.9:8999
             * 张首道       192.168.3.28:8999
             * 庆先         192.168.3.47:8999          192.168.3.47:8999
             * 祝树国       192.168.3.50:8999          192.168.3.240:8088
             * 纪海洋       192.168.3.184:8998
             * 叶青         192.168.3.229:8999
             * 远志         192.168.3.87:8999
             * 管恩松       192.168.3.168:8999
             * 沈乃鑫       192.168.3.177:8999
             *              192.168.137.37
             *  
             * 线上
             * http://admin.bitstaging.com/
             * http://admin.common.com/
             * http://admin.gbccoin.com/
             *
             */

            {//定时任务
                context: ['/schedule','/scheduleLog',],
                target:"http://192.168.3.87:8088",
                secure: false,
                changeOrigin: true
            },
            {
                context: ['/finProfitSupernodeDetail','/finUserRewardStatus','/finTask',"/apiConfig","/onlineNumConfig","/gbcDividendCount","/capitalCount", "/withdraw","/recharge","/authenLog","/centerCapitalExp","/withdrawRecord", "/withdrawRecord", "/settlement", "/marketTrade","/marketMergeTrade","/entrustRecord","/planEntrustRecord","/centerCapitalDaily","/centerCapitalSum","/userPaymentTradeCount","/userBaseOperCount","/userPositionCount","/userPositionCount","/mars","/smallAutoPayRecord","/smallPayTask","/feeProfit","/capitalUse", "/userCapital","/accountManage",'/blacklist',"/batchEntrust", "/news",'/brush',"/capitalaccount","/authentication","/billDetail","/SysDictionary","/app","/gbcreportmod","/backcapital",'/walletBill','/walletBillDetail','/walletBalance',"/msg","/integralBill","/integralRule","/integralVipRule","/googleVerify","/phoneVerify","/messageSend","/msgTemplateRule","/operInfo","/agencyTask",'/rechargeAddress','/withdrawAddress',"/capitalMonitor","/SendEmailAccount","/voteManage","/drawManage","/userInfo","/sys","/loginInfo","/common","/usermod",
                '/walletRecon','/otcConfig','/otcBank','/otcPaymentType','/otcBank','/otcPaymentType','/advertisement','/otcComplain','/orderform','/otcBill','/otcUserCapital','/walletUserCapital','/otcSms','/coin','/otcCapitalCount','/walletCapitalCount','/friendUrl','/bannerPhoto','/bannerGroup','/sysmod','/doubleCheck','/fundTransferLog','/otcRePush','/otcBannerPhoto','/deblocking','/otcCointype','/billFutures','/leverageRecord','/transReset','/positionDetails',"/common","/generalledger","/checkreconciliation",'/transactionfutures',"/returnsSummary","/transactionAll","/moneyChange","/futures","/fundUserCapitalDetail","/entrustmentDetails","/transactionRecord","/billReconciliation","/futuresreconciliation","/gbcDividendCount","/capitalCount", "/withdraw","/recharge","/authenLog","/centerCapitalExp","/withdrawRecord", "/withdrawRecord", "/settlement", "/marketTrade","/marketMergeTrade","/entrustRecord","/planEntrustRecord","/centerCapitalDaily","/centerCapitalSum","/userPaymentTradeCount","/userBaseOperCount",
                "/userPositionCount","/userPositionCount","/mars","/smallAutoPayRecord","/smallPayTask","/feeProfit","/capitalUse", "/userCapital","/accountManage",'/blacklist',"/batchEntrust", "/news",'/brush',"/capitalaccount","/authentication","/billDetail","/SysDictionary","/app","/gbcreportmod","/backcapital",'/walletBill','/walletBillDetail','/walletBalance',"/msg","/integralBill","/integralRule","/integralVipRule","/googleVerify","/phoneVerify","/messageSend","/msgTemplateRule","/operInfo","/agencyTask",'/rechargeAddress','/withdrawAddress',"/dealRecord","/capitalMonitor","/SendEmailAccount","/voteManage","/drawManage","/userInfo","/sys","/loginInfo","/usermod",'/walletRecon','/otcConfig','/otcBank','/otcPaymentType','/otcConfig','/otcBank','/otcPaymentType','/advertisement','/otcComplain','/orderform','/otcBill','/otcUserCapital','/otcSms','/coin','/otcCapitalCount','/walletCapitalCount','/friendUrl','/bannerPhoto','/bannerGroup','/sysmod','/doubleCheck','/fundTransferLog','/otcRePush','/otcBannerPhoto','/deblocking',
                '/otcCointype',"/walletaccount","/walletRecon","/withdraw","/otc","/userFutures",'/tradingRecord','/positionChangeRecord','/handicapTrading','/fundsRate','/insuranceFund','/feeAccountDetails','/feeAccountCheck','/coinLargeOrder','/coinLargeAccount','/coinReverseaccount','/coinInfoupdate',"/frequent","/qttransrecord","/linkedaccount","/coinQtStopwarning",'/coinQtBelowwarning',' /coinQtAccounted','/coinQtStopwarning','/coinQtBelowwarning','/coinQtAccounted','/coinQtMarketdeparture','/coinQtDishlowwarning','/coinQtForfailure','/coinQtHedgingabnormal','/coinQtHedgingnumbers','/coinQtRecordabnormal','/coinQtAmountlowwarning','/chargeManagement','/recommendCoin','/accountManage','/coinQtMarket',"/coinChangeDayrecharge","/coinChangeLargerecharge","/coinChangeCxception","/coinChangeBalance","/rechargeBalanceWindcontrol","/withdrawBalanceWindcontrol","/coinChangeInvented","/coinChangeDaywithdrawal","/coinChangeLargewithdrawal",'/circuitCount',"/extend",
                '/yhAccessDistribution','/yhuser','/yhnewuser','/jyConversionRateCookie', '/setting','/jeAmountDistribution','/jePlatforMmoney','/jeWalletAmount','/jeUserCurrency','/jeHedgeAccountBalance','/jeFees','/jyConversionRate','/jyEntrustMarket','/jyUserMoney','/jyDistributedMarket','/jyUserTrade','/jyTradeClient','/jeCashFlow','/jyTradeCurrent','/jyRanking','/yhtransactionvolume','/jyConversionRateCookie','/yhnewuser','/brushamount',"/smallPayManagement",'/brushamount','/introductionManagement','/qtTransactionRecord','/qtCashValueRecord','/userFinancial','/billReconciliationFinancial', '/financialBill','/supernode','/userFinancialInfo','/proFitblockConfig','/storeAuth','/userConfig','/marketConfig','/otcsummary','/rwAnalysis','/tradeAnalysis','/webBottom','/qtHedgingnumbersSum','/qtEntrustPlatform','/apiStat'],
                target:"http://192.168.3.87:8999",
                secure: false,
                changeOrigin: true
            },
        ]
    },
    plugins: [
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': "'development'"
        })
    ],
    devtool: 'cheap-module-eval-source-map'
});










































