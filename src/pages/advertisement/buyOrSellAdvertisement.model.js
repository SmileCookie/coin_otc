/**
 * model
 * @description 所有的核心数据放在此处处理，控制层只拿处理好的数据。
 * 如果在运算中出现了任何异常直接使用mock数据，视图在渲染如果没有此字段直接用--。
 */
import { post, get } from 'nets';
import axios from 'axios';
import { DOMAIN_VIP } from '../../conf';
// 获取模型对应的mock
import { coinInfo, AdInitialize } from 'mock/advertisement/buyOrSellAdvertisement';

const BigNumber = require('big.js');
import { isArray, optPop } from "../../utils";

// 工具类 不采用路径简写的形式，因为需要工具看定义
async function publishAdvertisement(params) {
    // 获取数据
    let result = await post('/web/ad/saveAd', params);
    return Promise.resolve(result);
}

// 获取币种类型接口
async function getFindCoinName() {
    // 先校验是否是商家，不是商家，跳转广告列表
    let authentication = await axios.get(DOMAIN_VIP + '/manage/auth/authenticationJson');
    // let authentication = {
    //     data: {
    //         "des":"ok",
    //         "isSuc":true,
    //         "datas":{
    //             "storeStatus": -1, //  商家状态-1:可认证， 状态;0待审核，1:通过 2:拒绝
    //             "storeType": "", //认证类型：申请类型 1:入驻申请，2;取消申请
    //             "storeReason": "", //不通过原因
    //         }
    //     }
    // }
    if (authentication.data.isSuc) {  //  商家状态-1:可认证， 状态;0待审核，1:通过 2:拒绝
        let storeStatus = authentication.data.datas.storeStatus;  //  商家状态 -1:可认证， 状态;0待审核，1:通过 2:拒绝
        let storeType = authentication.data.datas.storeType || 1; // //认证类型：申请类型 1:入驻申请，2;取消申请 (storeStatus 为-1 该字段无)
        if ((storeType == 1 && storeStatus == 1) || (storeType == 2 && (storeStatus != 1))) {
            // 可以发布
        } else {
            window.location.href = '/otc/advertisement';
            return
        }
    }
    let result = await get('/web/common/getCoinTypeList');
    result = result.data;
    let rtData = [];
    if (isArray(result) && result.length > 0) {
        // 开始遍历mapper
        result.forEach(item => {
            let obj = {};
            let coinObj = JSON.parse(JSON.stringify(coinInfo));
            // 每一项都走mock实例
            Object.keys(coinObj).map(v => {
                void 0 !== item[v] && ({ [v]: coinObj[v] } = item)
            });
            //因为是下拉框转化一下
            obj.key = coinObj.coinName.split('/')[0];
            obj.val = coinObj.fundsType;
            rtData.push(obj);
        });
    }
    // 无论如何都将返回供体数据。
    return Promise.resolve(rtData);
}

async function getInitialize(market) {
    // 获取数据
    let result = await post('/web/common/getAdInitialize', { market });
    let rtData = {};
    if (result.code != 200) {
        optPop(() => {
        }, result.msg);
        rtData = AdInitialize
    } else {
        result = result.data;
        rtData = AdInitialize;
        // 验证mock中对应的实例是否都存在。
        Object.keys(rtData).map(v => {
            void 0 !== result[v] && ({ [v]: rtData[v] } = result)
        });
        // 汇率转化成字符串加上%
        rtData.buyFeeFull = new BigNumber(rtData.buyFee).times(100).toFixed() + '%';
        rtData.sellFeeFull = new BigNumber(rtData.sellFee).times(100).toFixed() + '%';
    }
    // 无论如何都将返回供体数据。
    return Promise.resolve(rtData);
}

async function getTips(type) {
    type = type == 1 ? 'adTips_sell' : 'adTips_buy';
    let result = await post('/web/otcIntroduction/getMsg', { type });
    if (result.data) {
        result = (result.data[0] && result.data[0].descript) || '--';
    } else {
        result = '--'
    }
    return Promise.resolve(result);
}

// 初始化数据
export const init = async (type) => {
    let market = "";
    let currencyList = await getFindCoinName();
    if (currencyList.length > 0) {
        market = `${currencyList[0].key}/CNY`;
    }
    let getAdInitialize = await getInitialize(market);
    let tips = await getTips(type);
    let obj = {};
    obj.currencyList = currencyList;
    obj.getAdInitialize = getAdInitialize;
    obj.tips = tips;
    return Promise.resolve(obj);
};

/**
 * @desc 获取可发布数量
 * @param params market 市场 coinTypeId 币种  每次选择币种需要调用
 */
async function getPublishNum(params) {
    let result = await post('/web/ad/queryNumber', params);
    return Promise.resolve(result);
}

export { publishAdvertisement, getInitialize, getTips, getPublishNum };
