import { post, get } from 'nets';
import axios from 'axios'
import moment from 'moment';
import confs from 'conf';
import { USERID, PAGESIZE, DOMAIN_VIP, DOMAIN_TRANS } from 'conf';
import { isArray, optPop, getCoinNum } from '../../utils';

// const BigNumber = require('big.js');

// 获取mock的默认数据
const { defaultData } = confs;

const orderLists = {
    list: [{
        buyUserFee: defaultData, //买方手续费
        buyUserName: defaultData, //买方姓名
        coinNumber: 0, //交易数量
        coinPrice: 0, //币种单价
        coinTime: 0, //交易时间
        type: 0,
        adType: null,
        id: defaultData,
        market: "--/--", //交易市场
        orderId: defaultData, //广告ID
        recordNo: defaultData, //订单编号
        sellUserFee: defaultData, //卖方手续费率
        sellUserName: defaultData, //卖方姓名
        sellerCardName: defaultData, //卖方真实姓名
        buyerCardName: defaultData, //买方真实姓名
        statusName: defaultData, //交易状态
        buyUserId: defaultData, //买方用户ID
        sellUserId: defaultData, //卖方用户ID
        coinPriceTotal: 0, //交易金额
        timeDiff: 0,
        status: '',
        paymentTypes: '',
    }],
    totalCount: 0,
    totalPage: 0,
    currPage: 0,
};

const TodayData = {
    "buyCount": defaultData,   //累计购买总额
    "sellCount": defaultData,  //累计出售总额
    "total_btc": defaultData,  //折算成多少比特币
    "total_legal_tender": defaultData,  //折算为多少CNY
    "total_usdt": 0,
    "finishOrder": defaultData,   //已成交订单数
};

const PayTypeData = {
    alipayName: defaultData, // 支付宝姓名
    qrcodeUrl: defaultData, // 支付宝二维码图片
    alipayNumber: defaultData, // 支付宝号
    bankName: defaultData, // 银行姓名
    bankNumber: defaultData, // 银行账号
    bankOpeningBank: defaultData, // 开户行
    bankOpeningBranch: defaultData, // 开户支行
};

async function getOrederList(page = 1, status, _this, isLoading) {
    // 获取数据
    let obj = {
        limit: PAGESIZE,//每页条数
        page,//页码
        userId: USERID,
        status: status,//交易状态
        type: '2',//0-买；1-卖 2-全部
        recordNo: '',//订单号，模糊查询
        market: '',//市场 例如 USDT/CNY
        startCoinTime: '',
        endCoinTime: '',
    };
    //loading
    if (isLoading) {
        _this.setState({ loading: true });
    }
    let result = {};
    if (status === '1,2') {
        result = await post('/web/v1/workbench/query', obj);
    } else {
        result = await post('/web/v1/order/query', obj);
    }
    let coinData = await get('/web/common/market/coins');
    if (coinData.code == '200') {
        coinData = coinData.data.marks;
    }
    //loading 
    if (isLoading) {
        _this.setState({ loading: false });
    }
    result = result.data;
    let rtData = orderLists;

    // 验证mock中对应的实例是否都存在。
    if (isArray(result.list) && result.list.length > 0) {
        // 开始遍历mapper
        let list = [];
        let orderListdata = orderLists.list[0];
        result.list.map(item => {
            //截取市场名字
            item.market = item.market.slice(0, -4);
            let _obj = Object.assign({}, orderListdata);
            Object.keys(_obj).map(v => {
                void 0 !== item[v] && ({ [v]: _obj[v] } = item);
            });
            list.push(_obj);
        });
        list.map((item) => {
            if (item.timeDiff > 0) {
                item.timeDiffDesc = resetTimeDiff(item.timeDiff);
            }
            item.dealType = USERID == item.buyUserId ? 'buy' : 'sell'; //buy买币订单&sell卖币订单
            // 根据状态以及类型判断操作按钮
            if (item.status == '1' || item.status == '2') {
                if (item.dealType == 'sell') { // 出售
                    item.operateName = '确认并释放';
                    if (item.statusName == '待放币') {
                        item.canotOperate = true;
                    }
                } else { // 购买
                    item.operateName = '标记已付款';
                    if (item.statusName == '待付款') {
                        item.canotOperate = true;
                    }
                }
            }
            let market = item['market'];
            let _obj = getCoinNum(coinData, market);
            item.marketL = _obj.marketL;
        });
        rtData = {
            list,
            totalCount: result.totalCount,
            totalPage: result.totalPage,
            currPage: result.currPage,
            pageSize: result.pageSize,
        }

    } else {
        rtData = {
            list: [],
            totalCount: result.totalCount,
            totalPage: result.totalPage,
            currPage: result.currPage,
            pageSize: result.pageSize,
        }
    }
    // 无论如何都将返回供体数据。
    return Promise.resolve(rtData);
}

// export const recieveMarketsinfo = (markets) => {
//     let marketlist = {
//         USDT: {},
//         BTC: {},
//         FAV: {}
//     }
//     let localeCookie = cookie.get(COOKIE_MARKETS_FAV);
//     if (!localeCookie || localeCookie == undefined) {
//         localeCookie = "";
//     }
//     let marketsFavArr = localeCookie.split("-");
//     let rateBtc = markets.btc_usdt_hotdata_Bitcoin[0];
//     Object.keys(markets).map(
//         (key, index) => {
//             let keyP = key.replace(/ /g, "+");
//             let coin = key.split("_");
//             let marketName = coin[1].toUpperCase();
//             marketlist[marketName][keyP] = markets[key];
//             marketlist[marketName][keyP][10] = false;
//             if (key == "ABCDE_btc_hotdata_ABCDE") {
//                 marketlist[marketName][keyP][11] = false;
//             } else {
//                 marketlist[marketName][keyP][11] = true;
//             }
//             if (!isNaN(markets[key][9])) {
//                 if (marketName == "USDT") {
//                     marketlist[marketName][keyP][12] = new BigNumber(markets[key][9]).toFixed(6);
//                 } else if (marketName == "BTC") {
//                     marketlist[marketName][keyP][12] = new BigNumber(markets[key][9]).times(rateBtc).toFixed(6);
//                 }
//             }
//             marketsFavArr.map(
//                 (k, i) => {
//                     if (k == keyP) {
//                         marketlist.FAV[keyP] = markets[key];
//                         marketlist[marketName][keyP][10] = true;
//                     }
//                 }
//             )
//         }
//     )
//     return marketlist
// }

async function getTopall() {
    let topall = {};
    // 获取topall 数据
    let res = await axios.get(DOMAIN_TRANS + '/line/topall?jsoncallback=');
    try {
        topall = eval(res['data'])[0];
    } catch (e) {
        console.log(e);
    }
    // let res = {
    //     data: ([{"eth_btc_hotdata_Ethereum":[0.031521000,0.031517,0.031525,0.0,0.0,0.0,0.031521000,[[]],0.0,0.0],"btc_usdt_hotdata_Bitcoin":[8621.740000000,8619.29,8651.78,8648.82,8621.74,0.3932,8648.820000000,[[1565229360000,8640.550000000],[1565229420000,8622.010000000],[1565231040000,8621.740000000]],-0.31,3396.61],"ltc_usdt_hotdata_Litecoin":[163.970000000,161.73,163.97,163.97,163.97,2.0,163.990000000,[[1565143020000,163.970000000]],-0.01,327.94],"ltc_btc_hotdata_Litecoin":[0.013124000,0.013124,0.013131,0.0,0.0,0.0,0.013124000,[[]],0.0,0.0],"eth_usdt_hotdata_Ethereum":[268.810000000,268.82,268.92,0.0,0.0,0.0,268.820000000,[[]],0.0,0.0],"eos_btc_hotdata_EOS":[0,0,0,0,0,0,0,[[]],0,0],"etc_btc_hotdata_Ethereum Classic":[0,0,0,0,0,0,0,[[]],0,0],"etc_usdt_hotdata_Ethereum Classic":[0,0,0,0,0,0,0,[[]],0,0],"eos_usdt_hotdata_EOS":[23.438100000,23.9608,30.0,0.0,0.0,0.0,23.030300000,[[]],1.77,0.0],"dash_btc_hotdata_DASH":[0.019243000,0.019242,0.019243,0.0,0.0,0.0,0.019243000,[[]],0.0,0.0],"qtum_usdt_hotdata_Qtum":[3.063000000,2.979,3.078,0.0,0.0,0.0,3.074000000,[[]],-0.35,0.0],"snt_usdt_hotdata_Status":[0.026772000,0.026734,0.026816,0.0,0.0,0.0,0.026772000,[[]],0.0,0.0],"link_btc_hotdata_ChainLink":[0.000136860,0.00012819,0.00013687,0.0,0.0,0.0,0.000136850,[[]],0.0,0.0],"omg_btc_hotdata_OmiseGO":[0,0,0,0,0,0,0,[[]],0,0],"elf_usdt_hotdata_aelf":[0.241500000,0.2414,0.2415,0.0,0.0,0.0,0.241500000,[[]],0.0,0.0],"zrx_btc_hotdata_0x":[0,0.00028761,0.00028924,0.0,0.0,0.0,0,[[]],0.0,0.0],"knc_usdt_hotdata_KyberNetwork":[0.378600000,0.3785,0.3786,0.0,0.0,0.0,0.378600000,[[]],0.0,0.0],"vds_usdt_hotdata_Vollar":[4.134400000,3.6877,4.1463,4.1344,3.6877,214.2,3.797100000,[[1565228640000,3.687700000],[1565229540000,4.134400000]],8.88,842.3963],"vds_btc_hotdata_Vollar":[0.000414700,0.00028901,0.00041573,0.0,0.0,0.0,0.000414700,[[]],0.0,0.0]}])
    // };
    // try{
    //     topall = eval(res['data'])[0];
    // }catch(e){
    //     topall = [];
    // }
    return Promise.resolve(topall);
}

function getTopallData(marketFlag, topall) {
    let rtData = {};
    let flag = marketFlag.toLowerCase().split('/').join('_');
    let topallData = [];
    for (let item in topall) {
        if (item.indexOf(flag) > -1) {
            topallData = topall[item];
        }
    }
    rtData.lastPrice = topallData[0];
    rtData.high = topallData[3];
    rtData.low = topallData[4];
    rtData.volume = topallData[9];
    return rtData;
}

function returnCoinTypeList(coinType, topall) {
    let coinTypeList = [];
    coinType.map((item) => {
        for (let item2 in topall) {
            let item2Arr = item2.split('_');
            if (item2Arr[0] == item.toLowerCase()) {
                coinTypeList.push({
                    val: coinTypeList.length.toString(),
                    key: (`${item2Arr[0]}/${item2Arr[1]}`).toUpperCase(),
                })
            }
        }
    });
    return coinTypeList;
}

async function getExchangeRate() {
    let exchangeRate = await axios.get(DOMAIN_TRANS + '/getExchangeRate');
    // let exchangeRate ={
    //     data:{"des" : "success" , "isSuc" : true  , "datas" : {"exchangeRateBTC":{"AUD":12657.39786,"EUR":7644.41717,"GBP":7048.43273,"USD":8621.74,"CNY":60093.5278},"exchangeRateUSD":{"AUD":1.46808,"BTC":0.00012,"EUR":0.88664,"GBP":0.81752,"USD":1,"CNY":6.97}}}
    // };
    try {
        exchangeRate = eval(exchangeRate).data.datas;
    } catch (e) {
        exchangeRate = {};
    }
    return Promise.resolve(exchangeRate);
}

async function getMarketCoins(_this) {
    let res = _this.props.coinData;
    let rtData = {};
    res.map((item) => {
        rtData[item.name.split('/')[0]] = item.coinBixDian;
    });
    return Promise.resolve(rtData);
}

async function initData(_this) {

    let manageUser = await axios.get(DOMAIN_VIP + '/manage/user');
    let authentication = await axios.get(DOMAIN_VIP + '/manage/auth/authenticationJson');
    //  获取保证金列表
    let storeList = await get('/web/v1/store/storeList');
    let todayData = await getTodayData(_this);
    // let manageUser = {
    //     data: {
    //         "des":"ok",
    //         "isSuc":true,
    //         "datas":{
    //             "vipRate":0,
    //             "reason":"",
    //             "hasMobileCheckBox":true,
    //             "color":"#3E85A2",
    //             "isGoogleOpen":false,
    //             "mobilec":"",
    //             "twoAuth":false,
    //             "hasSafe":true,
    //             "isLock":false,
    //             "loginIp":"192.168.3.109",
    //             "nickname":"张三",
    //             "email":"l008@qq.com",
    //             "emailSatus":2,
    //             "isSmsOpen":false,
    //             "previousLogin":1564372631328,
    //             "authStatus":6,
    //             "mobile":"+86 13455960985",
    //             "guideFlg":false,
    //             "userName":"l008@qq.com",
    //             "isBlack":false,
    //             "mobileStatus":2,
    //             "googleAuth":1,
    //             "userSafeLevel":"2",

    //             "storeLevel": "", //用户商家资质级别  31， 32 ， 33 ， 34
    //         }
    //     }
    // }
    // let authentication = {
    //     data: {
    //         "des":"ok",
    //         "isSuc":true,
    //         "datas":{
    //             "isLock":false,
    //             "reason":"",
    //             "isBlack":false,
    //             "authStatus":6,
    //             "storeStatus": "", //  商家状态-1:可认证， 状态;0待审核，1:通过 2:拒绝
    //             "storeType": "", //认证类型：申请类型 1:入驻申请，2;取消申请
    //             "storeReason": "", //不通过原因
    //         }
    //     }
    // }

    let rtData = {};
    if (manageUser.data.isSuc) {
        rtData.authStatus = manageUser.data.datas.authStatus;
        rtData.storeLevel = manageUser.data.datas.storeLevel;
    }
    if (authentication.data.isSuc) {
        rtData.storeStatus = authentication.data.datas.storeStatus; //  //  商家状态-1:可认证， 状态;0待审核，1:通过 2:拒绝
        rtData.storeType = authentication.data.datas.storeType;// //认证类型：申请类型 1:入驻申请，2;取消申请 (storeStatus 为-1 该字段无)
        rtData.storeReason = authentication.data.datas.storeReason; // "storeReason": "", //不通过原因 (storeStatus 为-1 该字段无)
    }
    if (rtData.storeType && rtData.storeType == 2 && rtData.storeStatus == 1) { // 如果通过取消认证申请，则还可以再认证
        rtData.storeStatus = -1;
    }
    if (rtData.storeType && rtData.storeType == 2 && (rtData.storeStatus == 2 || rtData.storeStatus == 0)) { // 如果通过拒绝取消认证申请，则还是商家，进入工作台
        rtData.storeStatus = 1;
    }

    rtData.storeList = storeList.data || {};
    rtData.balance = storeList.balance;

    rtData.todayData = todayData;
    return Promise.resolve(rtData);
}

// 扣除保证金认证
async function getAuthen(safePwd, storeLevel) {
    // 获取数据
    let result = await post('/web/v1/store/apply', { safePwd, storeLevel });
    if (result.code == 200) {
        result = true;
    } else {
        optPop(() => {
        }, result.msg);
        result = false;
    }
    return Promise.resolve(result);
}

// 获取今日数据
async function getTodayData(_this, isLoading) {
    //loading
    if (isLoading) {
        _this.setState({ loading: true });
    }
    let result = await post('/web/v1/workbench/queryCount', {});
    //loading 
    if (isLoading) {
        _this.setState({ loading: false });
    }
    result = result.data;
    let rtData = TodayData;
    // 验证mock中对应的实例是否都存在。
    Object.keys(rtData).map(v => {
        void 0 !== result[v] && ({ [v]: rtData[v] } = result)
    });
    return Promise.resolve(rtData);
}

// pending 订单列表操作时限
function resetTimeDiff(timeDiff) {
    if (timeDiff > 0) {
        return moment(timeDiff * 1000).format('mm:ss');
    } else {
        return '超时';
    }
}

// 是否商家认证锁定状态
async function getLockStatus() {
    // 获取数据
    let result = await get('/web/v1/store/lockStatus');
    if (result.code == 200) {
        result = false;
    } else {
        optPop(() => {
        }, result.msg);
        result = true;
    }
    return Promise.resolve(result);
}

// 点击立即申请判断保证金是否足够
async function isEnoughBail() {
    let res = await get('/web/v1/store/storeList');
    let balance = res.balance;
    let storeList = res.data || {};
    let result = true;
    if (Object.keys(storeList).length > 0) {
        let key = Object.keys(storeList)[0];
        result = (parseFloat(balance) >= parseFloat(storeList[key]));
    } else {
        result = false;
    }
    return Promise.resolve(result);
}

/**
 * @desc 获取支付信息详情
 * @param recordId 订单ID
 */
export const getPayTypeInfo = async recordId => {
    let res = await post('/web/v1/trade/querUserPayment', { recordId });
    let result = {};
    if (res && res.code === 200) {
        if (res.data && Array.isArray(res.data) && res.data.length > 0) {
            const arr = res.data;
            arr.forEach(item => {
                if (item.paymentType === 1) {
                    result = {
                        ...result,
                        alipayName: item.accountName,
                        qrcodeUrl: item.qrcodeUrl,
                        alipayNumber: item.accountNumber,
                    }
                }
                if (item.paymentType === 2) {
                    result = {
                        ...result,
                        bankName: item.accountName,
                        bankNumber: item.accountNumber,
                        bankOpeningBank: item.bankOpeningBank,
                        bankOpeningBranch: item.bankOpeningBranch,
                    }
                }
            });
            let mockData = JSON.parse(JSON.stringify(PayTypeData));
            // 验证mock中对应的实例是否都存在。
            Object.keys(mockData).map(v => {
                void 0 !== result[v] && ({ [v]: mockData[v] } = result)
            });
        }
    }
    return Promise.resolve(result);
};

export const getDelayData = async _this => {
    let result = {};
    let exchangeRate = await getExchangeRate();

    let topall = await getTopall();

    let coinType = await get('/web/v1/workbench/coinType'); // 获取OTC币种列表
    coinType = coinType.data || [];

    let coinTypeList = returnCoinTypeList(coinType, topall);

    let topallData = getTopallData(coinTypeList[0].key, topall);
    // 获取币种小数位数
    let coins = await getMarketCoins(_this);

    result = {
        ...result,
        coins,
        topall,
        topallData,
        exchangeRate,
        coinTypeList,
    };

    return Promise.resolve(result);
};

const linkUrl = {
    'cn': 'https://support.btcwinex.com/hc/zh-cn/articles/360031458571-%E5%B9%BF%E5%91%8A%E5%95%86%E5%8D%8F%E8%AE%AE',
    'en': 'https://support.btcwinex.com/hc/en-us/articles/360031458571-Advertising-Rules',
    'jp': 'https://support.btcwinex.com/hc/ja/articles/360031458571-%E5%BA%83%E5%91%8A%E4%B8%BB%E5%8D%94%E8%AD%B0',
    'kr': 'https://support.btcwinex.com/hc/ko/articles/360031458571-%EA%B4%91%EA%B3%A0-%EC%97%85%EC%B2%B4-%EA%B3%84%EC%95%BD',
};

export {
    getOrederList,
    initData,
    getAuthen,
    getTopall,
    getTopallData,
    getTodayData,
    resetTimeDiff,
    getLockStatus,
    linkUrl,
    isEnoughBail,
    getMarketCoins
};
