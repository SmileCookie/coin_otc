export const recharge = [
        {
                title: '提现异常预警',
                id: 32,
                frequency: 0,
                num: 0,
                sum: 0,
                unreadcount:0,
                url:"/riskmanagement/moneyUnusual/withdrawError",
        }, {
                title: '单日累计提现额度报警',
                id: 33,
                frequency: 0,
                num: 0,
                sum: 0,
                unreadcount:0,
                url:'/riskmanagement/moneyUnusual/singleDayWithdrawAll',
        }, {

                title: '单笔大额提现报警',
                id: 34,
                frequency: 0,
                num: 0,
                sum: 0,
                unreadcount:0,
                url:'/riskmanagement/moneyUnusual/singleBigWithdraw'
        }, {
                title: '单币种日累计充值报警',
                id: 35,
                frequency: 0,
                num: 0,
                sum: 0,
                unreadcount:0,
                url:'/riskmanagement/moneyUnusual/singleCionDayRecharge'
        }, {
                title: '单笔大额充值报警',
                id: 36,
                frequency: 0,
                num: 0,
                sum: 0,
                unreadcount:0,
                url:'/riskmanagement/moneyUnusual/singleBigRecharge'
        }, 
        // {

        //         title: '提现总金额报警',
        //         id: 37,
        //         frequency: 0,
        //         num: 0,
        //         sum: 0,
        //         unreadcount:0,
        // }, 
        {

                title: '小额打币功能报警',
                id: 38,
                frequency: 0,
                num: 0,
                sum: 0,
                unreadcount:0,

        }, {

                title: '提现后账户余额为X报警',
                id: 39,
                frequency: 0,
                num: 0,
                sum: 0,
                unreadcount:0,
                url:'/riskmanagement/moneyUnusual/balanceBack'

        },

]

export const platform = [{
        title: '交易平台对账',
        id: 17,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/reconciliationcenter/platformReconciliation'
}, {
        title: '交易平台钱包对账',
        id: 18,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/reconciliationcenter/tradingPlatformWallet'
}, 
// {
//         title: '区块钱包对账',
//         id: 19,
//         frequency: 0,
//         num: 0,
//         sum: 0,
// unreadcount:0,
// }, 
{
        title: '区块钱包vs交易平台对账',
        id: 20,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/reconciliationcenter/walletVsPlatform'

}, 
// {
//         title: '期货账户对账',
//         id: 21,
//         frequency: 0,
//         num: 0,
//         sum: 0,
// unreadcount:0,
// url:'/tradecenter/reconciliationcenter/futures'
// }, 
{
        title: '币币账户对账',
        id: 22,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/reconciliationcenter/newCoinCurrencyAccount'
}, 
// {
//         title: 'OTC账户对账',
//         id: 23,
//         key: '',
//         frequency: 0,
//         num: 0,
//         sum: 0,
        // unreadcount:0,
//         url: ''
// }, 
{
        title: '虚拟资金异常',
        id: 24,
        key: '',
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/riskmanagement/capitalError/virtualFundsError'
},];

export const wallet = [{
        title: '热提钱包异常',
        id: 25,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/paymentmod/withdrawRecord'
}, {
        title: '热充钱包异常',
        id: 26,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/paymentcenterbalancemod/walletBalance'
}, {
        title: '冷钱包流水异常',
        id: 27,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/paymentcenterbalancemod/walletBill'
}];
export const payment = [{
        title: '充值对账',//风控
        id: 28,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/paymentcenterbalancemod/rechargeBalanceRisk'
}, {
        title: '提现对账',//风控
        id: 29,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/paymentcenterbalancemod/withdrawBalanceRisk'
}, 
// {
//         title: '钱包每日对账',
//         id: 30,
//         frequency: 0,
//         num: 0,
//         sum: 0,
//         unreadcount:0,
// url:'/tradecenter/paymentcenterbalancemod/walletBalanceRisk'
// }, 
{
        title: 'X钱包对账',
        id: 31,
        frequency: 0,
        num: 0,
        sum: 0,
        unreadcount:0,
        url:'/tradecenter/paymentcenterbalancemod/walletxFee'
},
];


