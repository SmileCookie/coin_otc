/**
 * model
 * @description 所有的核心数据放在此处处理，控制层只拿处理好的数据。
 * 如果在运算中出现了任何异常直接使用mock数据，视图在渲染如果没有此字段直接用--。
 */
import {post, get} from 'nets';
import {DATA_TIME_FORMAT} from 'conf'
// 获取模型对应的mock
import {MockPage, MockListObj} from 'mock/advertisement/advertiseDetails';
import moment from 'moment';

// 工具类 不采用路径简写的形式，因为需要工具看定义
import {isArray, optPop} from '../../utils';
// 获取详情数据
async function getAdvertiseInfo(_this,id) {
    // 获取数据
    _this.setState({
        loading: true
    });
    let res = await post('/web/ad/getInfo', {id: id, check: true});
    if(res.code != 200){
        _this.setState({
            errorPage:true
        })
    }
    let result = res.data;
    let rtData = MockPage;
    if (result) {
        // 验证mock中对应的实例是否都存在。
        Object.keys(rtData).map(v=>{  void 0 !== result[v] && ({[v]:rtData[v]}=result)});
        // 获取币种类型名
        if(result.market) {
            rtData.coinTypeName = result.market.split('/')[0];
        }
        if (result.reason && result.reason.indexOf('可交易数量已小于设置的最小限额') > -1) {
            rtData.typeOperation = 1
        }
        // 格式化交易时间
        // if(rtData.orderTime && rtData.orderTime != '--') {
        //     rtData.orderTime = moment(rtData.orderTime).format(DATA_TIME_FORMAT);
        // }
        if(isArray(result.otrList ) && result.otrList.length > 0){
            // 开始遍历mapper
            let rt  =[];
            result.otrList.forEach(item => {
                // 时间格式化
                // if(item.coinTime && item.coinTime != '--') {
                //     item.coinTime = moment(item.coinTime).format(DATA_TIME_FORMAT);
                // }
                let MockObj = JSON.parse(JSON.stringify(MockListObj));
                Object.keys(MockObj).map(v=>{  void 0 !== item[v] && ({[v]:MockObj[v]}=item)  });
                MockObj.tradeTotal = MockObj.coinNumber * MockObj.coinPrice;
                rt.push(MockObj);
            });
            rtData.otrList = rt;
        }
    }else {
        rtData = res.msg
    }
    // 无论如何都将返回供体数据。
    _this.setState({
        loading: false
    });
    return Promise.resolve(rtData);
}
// 下架
async function downFramAd(id) {
    // 获取数据
    let result = await post('/web/ad/downFramAd', {id: id});
    optPop(() => {}, result.msg);
    return Promise.resolve(result);
}
// 上架
async function upFramAd(id) {
    // 获取数据
    let result = await post('/web/ad/upFramAd', {id: id});
    optPop(() => {}, result.msg);
    return Promise.resolve(result);
}
// 隐藏
async function hideAd(id) {
    // 获取数据
    let result = await post('/web/ad/hideAd', {id: id});
    optPop(() => {}, result.msg);
    return Promise.resolve(result);
}
export { getAdvertiseInfo, downFramAd, upFramAd, hideAd };
