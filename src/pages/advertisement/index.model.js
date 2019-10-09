import { post, get } from 'nets';
import axios from 'axios'
import { DATA_TIME_FORMAT, DOMAIN_VIP } from 'conf'
// 获取模型对应的mock
import { MockPage, MockListObj, coinInfo } from 'mock/advertisement';

import down_cn from '../../assets/image/ad/down_cn.png';
import down_en from '../../assets/image/ad/down_en.png';
import down_jp from '../../assets/image/ad/down_jp.png';
import down_kr from '../../assets/image/ad/down_kr.png';
import hidden_cn from '../../assets/image/ad/hidden_cn.png';
import hidden_en from '../../assets/image/ad/hidden_en.png';
import hidden_jp from '../../assets/image/ad/hidden_jp.png';
import hidden_kr from '../../assets/image/ad/hidden_kr.png';

const img = {
    down_cn,
    down_en,
    down_jp,
    down_kr,
    hidden_cn,
    hidden_en,
    hidden_jp,
    hidden_kr,
};

// 工具类 不采用路径简写的形式，因为需要工具看定义
import { isArray, optPop } from '../../utils';

// 获取列表数据
async function getAdvertiseList(_this, orderNo, orderStatus, orderType, coinTypeId, pageIndex, pageSize) {
    // 获取数据
    _this.setState({
        loading: true
    });
    let obj = {
        orderNo: orderNo || '', // 订单编号
        orderStatus: orderStatus || '-1', // 广告状态
        orderType: orderType || '', // 订单类型
        coinTypeId: coinTypeId || '',
        limit: pageSize || 10, // 币种
        page: pageIndex || 1,
    };
    // 将筛选值存入 localStorage
    const { localStorage } = window;
    let adStorage = JSON.parse(localStorage.getItem('adStorage')) || {};

    adStorage = {
        ...adStorage,
        filterVal: orderNo || '', // 订单编号
        adStatus: orderStatus || '-1', // 广告状态
        tadeType: orderType || '', // 订单类型
        currency: coinTypeId || '',
        pageIndex: pageIndex || 1,
        pageSize: pageSize || 10
    };
    localStorage.setItem('adStorage', JSON.stringify(adStorage));
    let result = await post('/web/ad/query', obj);
    result = result.data;
    let rtData = MockPage;
    // 验证mock中对应的实例是否都存在。
    Object.keys(rtData).map(v => {
        void 0 !== result[v] && ({ [v]: rtData[v] } = result)
    });
    if (isArray(result.list) && result.list.length > 0) {
        // 开始遍历mapper
        let list = [];
        result.list.forEach(item => {
            // 时间格式化
            // if(item.orderTime && item.orderTime != '--') {
            //     item.orderTime = moment(item.orderTime).format(DATA_TIME_FORMAT);
            // }
            let MockObj = JSON.parse(JSON.stringify(MockListObj));
            Object.keys(MockObj).map(v => {
                void 0 !== item[v] && ({ [v]: MockObj[v] } = item)
            });
            if (MockObj.reason && MockObj.reason.indexOf('可交易数量已小于设置的最小限额') > -1) {
                MockObj.typeOperation = 1
            }
            list.push(MockObj);
        });
        rtData = {
            list,
            totalCount: result.totalCount,
            totalPage: result.totalPage,
            currPage: result.currPage
        };
    }
    // 无论如何都将返回供体数据。
    _this.setState({ loading: false });
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
                void 0 !== item[v] && ({ [v]: coinObj[v] } = item)
            });
            coinObj.coinName = coinObj.coinName.split('/')[0];
            rtData.push(coinObj);
        });
    }
    return Promise.resolve(rtData);
}

// 下架
async function downFramAd(id) {
    // 获取数据
    let result = await post('/web/ad/downFramAd', { id: id });
    optPop(() => {
    }, result.msg);
    return Promise.resolve(result);
}

// 上架
async function upFramAd(id) {
    // 获取数据
    let result = await post('/web/ad/upFramAd', { id: id });
    optPop(() => {
    }, result.msg);
    return Promise.resolve(result);
}

// 隐藏
async function hideAd(id) {
    // 获取数据
    let result = await post('/web/ad/hideAd', { id: id });
    optPop(() => {
    }, result.msg);
    return Promise.resolve(result);
}

function getAdvertiseFlag(type, lan) {
    return img[`${type}_${lan}`];
}

// 商家才能发广告
async function isBusiness() {
    // 获取数据
    let res = '';
    let result = await axios.get(DOMAIN_VIP + '/manage/auth/authenticationJson');
    let storeStatus = '';
    let storeType = 1;
    if (result.data.isSuc) {
        if (result.data && result.data.datas) {
            storeStatus = result.data.datas.storeStatus;  //  商家状态 -1:可认证， 状态;0待审核，1:通过 2:拒绝
            storeType = result.data.datas.storeType || 1; // //认证类型：申请类型 1:入驻申请，2;取消申请 (storeStatus 为-1 该字段无)
            // storeReason = result.data.datas.storeReason || '';
        }
        if (storeStatus == -1
            || (storeStatus == 2 && storeType == 1)
            || (storeStatus == 1 && storeType == 2)) { // 普通用户
            res = 'common';
        }
        if (storeStatus == 0 && storeType == 1) { // 申请待审核
            res = 'approving';
        }
        if ((storeStatus == 1 && storeType == 1)
            || (storeStatus == 0 && storeType == 2)
            || (storeStatus == 2 && storeType == 2)) { // 商家
            res = 'business';
        }
    } else {
        optPop(() => {
        }, result.msg);
    }
    return Promise.resolve(res);
}

export { getAdvertiseList, downFramAd, upFramAd, hideAd, getFindCoinName, getAdvertiseFlag, isBusiness };
