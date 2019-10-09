import moment from 'moment'

//域名、url相关
const LOCATION = window.location;
const PROTOCOL = LOCATION["protocol"];
export const DOMAIN_BASE = document.domain.split('.').slice(-2).join('.');
export const DOMAIN_VIP = PROTOCOL + "//" + LOCATION["host"];
export const DOMAIN_TRANS = PROTOCOL + "//" + "t." + DOMAIN_BASE;
export const DOMAIN_COOKIE = "." + DOMAIN_BASE;

//顶部导航宽度
export const navWid = 60;

//分页首页
export const PAGEINDEX = 1;
//分页默认条数
export const PAGESIZE = 20;
//分页默认条数
export const PAGESIZE_10 = 20;
//盘口交易默认分页条数
export const PAGESIZE_200 = 50;
//盘口交易默认分页条数
export const PAGESIZE_50 = 50;
//盘口交易默认分页条数切换
export const PAGRSIZE_OPTIONS = ["50", "100", "200", "500"];
//分页默认条数 20
export const PAGESIZE_20 = 20;
//分页默认条数 20 分页条数切换
export const PAGRSIZE_OPTIONS20 = ["20", "50", "100", "200"];
export const DEFAULT_OPTIONS = ['10', '20', '30', '40']

//默认初始值 0
export const DEFAULTVALUE = 0;

//时间格式
export const DAYFORMAT = 'YYYY-MM-DD'
export const TIMEFORMAT = 'YYYY-MM-DD HH:mm:ss'
export const TIMEFORMAT_DAYS = 'H:mm:ss'
export const HOURSFORMAT = 'YYYY-MM-DD HH'
export const MINUTFORMAT = 'YYYY-MM-DD HH:mm'
export const MONTHFORMAT = 'YYYY-MM'
//日期选择器使用的时间格式
export const TIMEFORMAT_ss = 'YYYY-MM-DD HH:mm:ss'
export const TIMEFORMAT_DAYS_ss = 'HH:mm:ss'



//时间placeholder
export const TIME_PLACEHOLDER = ['Start Time', 'End Time']

//充币 XXX
export const MODALCAPITALCHARGE = 1
//扣币 XXX
export const MODALCAPITALDEDUCT = 2
//冻结可用资金
export const MODALCAPITALFREEZE = 3
//解冻可用资金
export const MODALCAPITALUNFREEZE = 4
//select 宽度
export const SELECTWIDTH = 216

//小数点位数
export const NUMBERPOINT = 6;

//时间格式
export const SHOW_TIME_DEFAULT = [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')];
//otc资金类型
export const OTC_FUNDSTYPE = {
    10: 'USDT',
    2: 'BTC',
    51: 'VDS'
}
//用户限制-用户信息
export const USERLIMIT = {
    '01': '币币交易异常',
    '02': '提现异常',
    '03': '正常',
    '04': '法币交易异常',
    '05': '期货交易异常',
    '01,02': '提现|币币交易异常',
    '01,04': '币币、法币交易异常',
    '01,05': '币币、期货交易异常',
    '02,04': '提现|法币交易异常',
    '02,05': '提现|期货交易异常',
    '04,05': '法币、期货交易异常',
    '01,02,04': '提现|币币、法币交易异常',
    '01,02,05': '提现|币币、期货交易异常',
    '02,04,05': '提现|法币、期货交易异常',
    '01,02,04,05': '提现|币币、法币、期货交易异常',
    'default': '--'
}

// let obj = {
//     '01': '币币',
//     '02': '提现',
//     '04': '法币',
//     '05': '期货',
// }
// function combined(data, index = 0, group = []){
//     var need_apply = new Array();
//     need_apply.push(data[index]);
//     for(var i = 0; i < group.length; i++) {
//       need_apply.push(group[i] + data[index]);
//     }
//     group.push.apply(group, need_apply);
//     if(index + 1 >= data.length) return group;
//     else return combined(data, index + 1, group);
// }
// let keys = Object.keys(obj)
// console.log(combined(keys))
// function ccc(arr = []){
//     let limt = {
//         '03': '正常',
//         'default': '--'
//     }
//     let keyA = arr.map(v => {
//         return v.replace(/0/g, ',0').slice(1)
//     })
//     keyA.forEach(v => {

//     })
//     console.log(keyA)
// }
// ccc(combined(keys))
export const ALL_DATE = {
    YESTERDAY: { scope: 'day', prev: 1, curr: 1, state: '昨天' },
    TODAY: { scope: 'day', state: '今天' },
    LAST_WEEK: { scope: 'week', prev: 1, curr: 1, state: '上周' },
    THIS_WEEK: { scope: 'week', state: '本周' },
    LAST_MONTH: { scope: 'month', prev: 1, curr: 1, state: '上个月' },
    THIS_MONTH: { scope: 'month', prev: 0, curr: 0, state: '本月' },
    LAST_YEAR: { scope: 'year', prev: 1, curr: 1, state: '去年' },
    THIS_YEAR: { scope: 'year', state: '本年' },
    LAST_SEVEN_DAYS: { scope: 'day', prev: 6, curr: 0, state: '7天' },
    LAST_THIRTH_DAYS: { scope: 'day', prev: 29, curr: 0, state: '30天' },
    GOING_ONLINE: { scope: 'day', prev: 0, curr: 0, beforeNow: '2019-03-20', state: '上线' }
}

/** 公共接口 */
export const URLS = {
    COMMON_QUERYATTRUSDTE: '/common/queryAttrUsdte',   //提现审核，提现查询，充值查询 ----资金类型
    COMMON_GETUSERTYPE: '/common/getUserType',           //币币、法币、钱包用户资金页面，----用户类型
}





/**         理财下拉框 */

//投资矩阵
export const INVEST_MATRIX = new Map([
    [1, '1级矩阵2'],
    [2, '2级矩阵8'],
    [3, '3级矩阵18'],
    [4, '4级矩阵38'],
    [5, '5级矩阵88'],
    [6, '6级矩阵188'],
])

// 支付状态
export const PAY_STATE = new Map([
    [1, '已保存'],
    [2, '已支付'],
    [3, '复投中']
])

//状态
export const NODE_STATE = new Map([
    [1, '停用'],
    [2, '正常']
])

//处理状态
export const PROCESS_STATE = new Map([
    [0, '未处理'],
    [1, '已处理']
])

//节点类型
export const NODE_TYPE = new Map([
    [1, '初创'],
    [2, '固定'],
    [3, '动态']
])


// 显示标志
export const IS_SHOW = new Map([
    [1, '展示'],
    [0, '不展示']
])


export const INVSET_TYPE = new Map([
    [0, '首投'],
    [1, '增投'],
    [2, '自动复投'],
    [3, '手动复投']

])

//分配状态
export const DISTRIBUTE_STATE = new Map([
    [0, '未分配'],
    [1, '已分配']
])

//开启、关闭
export const ON_OFF = new Map([
    [0, '关闭'],
    [1, '开启']
])

//状态
export const SHOW_TYPE = new Map([
    [0, '异常'],
    [1, '正常']
])
/** 保值异常> 保值下单数量异常明细>外网委托详情 --交易类型 */

export const TRADE_TYPE = new Map([
    ['','全部'],
    [1,'买入'],
    [0,'卖出']
])

/** x钱包对账--- 资金类型（代币） */

export const X_WALLET_FUNDSTYPE = new Map([
    [6,'ETH'],
    [24,'OMG'],
    [29,'ELF'],
    [31,'LRC'],
    [33,'SNT'],
    [36,'MANA'],
    [37,'ZRX'],
    [46,'LINK'],
    [47,'KNC'],
    [48,'MCO'],
    [50,'DGD'],
    [102,'USDTE'],

])


// 用户信息userinfo中，用户标签检索类型
export const USER_TAGS = new Map([
    [0,'个人熔断'],
    [1,'风险用户'],

])