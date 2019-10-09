import { post, get } from 'nets';
import { DATA_TIME_FORMAT } from 'conf';
// 获取模型对应的mock
import { DetailsMoc, CheckInfo, CheckMoc } from 'mock/trade/buySell';
// 工具类 不采用路径简写的形式，因为需要工具看定义
import { optPop } from '../../utils';

// 获取详情数据
async function getAdvertiseInfo(id) {
    // 获取数据
    let result = await post('/web/ad/getInfo', { id: id, check: false });
    result = result.data;
    let rtData = DetailsMoc;
    // 验证mock中对应的实例是否都存在。
    Object.keys(rtData).map(v => {
        void 0 !== result[v] && ({ [v]: rtData[v] } = result);
    });
    // 获取币种类型名
    if (result.market) {
        rtData.coinTypeName = result.market.split('/')[0];
    }
    return Promise.resolve(rtData);
}

async function getAdCheckInfo(id, userId) {
    // 获取数据
    let result = await post('/web/common/getAdCheckInfo', { id, userId });
    result = result.data;
    let rtData = CheckInfo;
    // 验证mock中对应的实例是否都存在。
    Object.keys(rtData).map(v => {
        void 0 !== result[v] && ({ [v]: rtData[v] } = result);
    });
    if (result.configInfo) {
        let obj = CheckMoc;
        Object.keys(obj).map(v => {
            void 0 !== result.configInfo[v] && ({ [v]: obj[v] } = result.configInfo);
        });
        rtData.configInfo = obj;
    }
    return Promise.resolve(rtData);
}

async function saveOrder(_this, advertiseInfo, coinNumber, adUserId, coinPriceTotal) {
    let { id, market, coinPrice } = advertiseInfo;
    let type = advertiseInfo.orderType == 0 ? 1 : 0;
    // 获取数据
    let result = await post('/web/v1/trade/saveRecord', {
        orderId: id,
        market,
        coinPrice,
        coinNumber,
        type,
        adUserId,
        coinPriceTotal
    });
    if (result.code == 200) {
        // let id = result.data && result.data.recordId || '--';
        // window.location.href = `/otc/orderDetail/${id}`
    } else {
        optPop(() => {
            getAdvertiseInfo(id).then(res => {
                _this.setState({ tradeInfo: res });
            });
        }, result.msg);
    }
    return Promise.resolve(result);
}

async function getTips(type) {
    type = type == 1 ? 'buyTips' : 'sellTips';
    let result = await post('/web/otcIntroduction/getMsg', { type });
    if (result.data) {
        result = (result.data[0] && result.data[0].descript) || '--';
    } else {
        result = '--';
    }
    return Promise.resolve(result);
}

// 初始化数据
export const init = async (_this, id) => {
    _this.setState({ loading: true });
    let advertiseInfo = await getAdvertiseInfo(id);
    let checkInfo = await getAdCheckInfo(id, advertiseInfo.userId);
    let tips = await getTips(advertiseInfo.orderType);
    let obj = {};
    obj.advertiseInfo = advertiseInfo;
    obj.checkInfo = checkInfo;
    obj.tips = tips;
    _this.setState({ loading: false });
    return Promise.resolve(obj);
};

export { getAdvertiseInfo, getAdCheckInfo, saveOrder };
