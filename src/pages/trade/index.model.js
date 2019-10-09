import { post, get } from 'nets';
import { DATA_TIME_FORMAT } from 'conf'
// 获取模型对应的mock
import { MockPage, MockListObj, coinInfo } from 'mock/trade';
import { isArray, optPop } from '../../utils';

// 获取列表数据
async function getTableList(_this, orderType, payType, currency, legalCurrency = 'CNY', pageIndex, pageSize, noLoading) {
    // 获取数据
    if (!noLoading) {
        _this.setState({ loading: true });
    }
    let market = currency ? `${currency}/${legalCurrency}` : 'BTC/CNY';
    let obj = {
        paymentType: payType || '', // 支付方式
        orderType: orderType || '1', // 订单类型 0 购买 1 出售
        market: market,
        limit: pageSize || 10,
        page: pageIndex || 1,
        orderStatus: 0,
    };
    // 将筛选值存入 localStorage
    const { localStorage } = window;
    let trStorage = JSON.parse(localStorage.getItem('tradeStorage')) || {};
    trStorage = {
        ...trStorage,
        orderType: orderType || '1', // 订单类型
        payType: payType || '', // 支付方式
        currency, // 币种
        limit: pageSize || 10,
        page: pageIndex || 1,
        orderStatus: 0,
    };
    localStorage.setItem('tradeStorage', JSON.stringify(trStorage));

    let result = await post('/web/v1/trade/query', obj);
    result = result.data;
    let rtData = MockPage;
    // 验证mock中对应的实例是否都存在。
    Object.keys(rtData).map(v => {
        void 0 !== result[v] && ({ [v]: rtData[v] } = result);
    });
    if (isArray(result.list) && result.list.length > 0) {
        // 开始遍历mapper
        let list = [];
        result.list.forEach(item => {
            let MockObj = JSON.parse(JSON.stringify(MockListObj));
            Object.keys(MockObj).map(v => {
                void 0 !== item[v] && ({ [v]: MockObj[v] } = item);
            });
            list.push(MockObj);
        });
        rtData = {
            list,
            totalCount: result.totalCount,
            totalPage: result.totalPage,
            currPage: result.currPage,
        }
    }
    // 无论如何都将返回供体数据。
    if (!noLoading) {
        _this.setState({ loading: false });
    }
    return Promise.resolve(rtData);
}

// 获取币种类型接口
async function getFindCoinName() {
    // 获取数据
    let result = await get('/web/common/getCoinTypeList');
    result = result.data;
    let rtData = [];
    if (isArray(result) && result.length > 0) {
        // 开始遍历mapper
        result.forEach(item => {
            let coinObj = JSON.parse(JSON.stringify(coinInfo));
            // 每一项都走mock实例
            Object.keys(coinObj).map(v => {
                void 0 !== item[v] && ({ [v]: coinObj[v] } = item);
            });
            coinObj.coinName = coinObj.coinName.split('/')[0];
            rtData.push(coinObj);
        });
    }
    // 无论如何都将返回供体数据。
    return Promise.resolve(rtData);
}

//点击购买和出售的时候的校验
async function getAdCheckInfo(id, userId) {
    let result = await post('/web/common/getAdCheckInfo', { id, userId });
    let flag = true;
    if (result.msg && !result.data) {
        flag = false;
        optPop(() => {
        }, result.msg);
    } else if (result.data) {
        flag = result.data;
    }
    return Promise.resolve(flag);
}

async function FirstEquivalence(userId) {

    let result = await get('/web/common/updateFirstVisit', { uid: userId });
    let flag = true;
    if (result.msg && !result.data) {
        flag = false;
    } else if (result.data) {
        flag = result.data;
    }
}

// 初始化数据
export const init = async (_this) => {
    const currencyList = await getFindCoinName();
    // 如果缓存中存在币种，则从缓存中获取，否则使用列表中的第一个
    const { localStorage } = window;
    const trStorage = JSON.parse(localStorage.getItem('tradeStorage')) || {};
    const orderType = trStorage.orderType || '';
    const payType = trStorage.payType || '';
    const currency = trStorage.currency || currencyList[0].coinName;

    const tradeList = await getTableList(_this, orderType, payType, currency);
    const obj = {
        tradeList,
        currencyList,
    };
    return Promise.resolve(obj);
};

export { getTableList, getAdCheckInfo, FirstEquivalence };
