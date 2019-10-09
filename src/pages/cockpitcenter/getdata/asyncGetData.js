import React, { Component } from 'react'
import axios from '../../../utils/fetch'
import cookie from 'js-cookie';
import { message } from 'antd'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,ALL_DATE } from '../../../conf'
import 'echarts/map/js/china';
import 'echarts/map/js/world';
import { toThousands, formatDateList,newTableColumns,getDate } from '../../../utils';
import contries from '../../../utils/countries';
import tradeTateTotal from '../../../assets/images/tradeTateTotal.png';
const barWidth = 30;
const { LAST_SEVEN_DAYS,LAST_MONTH } = ALL_DATE;
let defaulTime = getDate(LAST_SEVEN_DAYS);
let defaulMonthTime = getDate(LAST_MONTH);

import {
    TRADE_ENGINE,//交易终端
    TRADE_HANDICAP_DEAL,    //盘口成交统计
    TRADE_HANDICAP_DEAL_DSIT, //盘口成交分布统计
    TRADE_NEW_OLD_USER_EXCHANGE,    //新老客户交易构成
    TRADE_RANKING,  //平台排行统计
    TRADE_TATE_TOTAL,   //交易转化率统计
    TRADE_TREND,    //交易趋势
    TRADE_USER_EXCHANGE_MONEY,  //用户交易金额统计

    AMOUNT_DEPOSIT_MONEY,   //沉淀资金
    AMOUNT_EXCHANGE_MONEY_DIST, //交易所金额分布
    AMOUNT_HAND_FEE_CHANGE_TREND,   //手续费收取趋势
    AMOUNT_HAND_FEE_HANDICAP,   //手续费盘口统计
    AMOUNT_MONEY_FLOW_TREND,    //资金流动趋势
    AMOUNT_PLATFORM_MONEY_DIST, //平台账户资金分布
    AMOUNT_PRESERV_MONEY_DIST,  //保值资金分布
    AMOUNT_WALLET_CURRENCY_DIST,    //钱包货币分布
    AMOUNT_WALLET_MONEY_DIST,//钱包资金分布
    AMOUNT_USER_MONEY_DIST, //用户货币分布

    USER_ACTIVE_FORM, //活跃用户构成
    USER_NEWUSER_TOTAL, //新增用户统计'
    USER_INTERVIEW_FLOW_TREND, //访问流量趋势'
    USER_EXCHANGE_NUM_RANKING, //用户交易量排行'
    USER_CHINA_INTERVIEW, //国内用户访问分布'
    USER_GLOBAL_INTERVIEW, //全球用户访问分布'
    USER_GAIN_RANKING, //用户盈利排行'

    EMPLOYER_REGISTER, //'资方用户注册',
    EMPLOYER_EXCHAGNE_PROP, //'资方用户交易占比',
    EMPLOYER_EXCHANGE_NUM_TREND, //'资方用户交易量趋势',
    EMPLOYER_EXCHANGE_FREQ_TREND, //'资方用户交易频率趋势',
    EMPLOYER_WITHDRAW_PROP, //'资方用户提现占比统计',
    EMPLOYER_DEPOSIT_PROP, //'资方用户存入与抛售统计',
    EMPLOYER_TO_PLATFORM_USER, //'转为平台用户统计',
    EMPLOYER_TO_X_USER, //'转为X用户统计',
    EMPLOYER_HAND_FEE_DEVOTE, //'资方用户手续费贡献统计',

    tradeRateCols1,
    tradeHandicapCols,
    tradeRankCols,
    tradeRateCols2,
    tradeUserOldCols,
    interviewFlowCols,
    interviewWorldCols,
    tradeEngineCols
} from '../static/actionType'

import { PIE_CHARTS, LINE_CHARTS, FUNNEL_CHARTS, BAR_CHARTS, LIST_TABLE, MAP_CHARTS, } from '../static/static'

const defaltTooltip = {
    trigger: 'item',
    formatter: "{a} <br/>{b} : {c} ({d}%)"
}
const defaultTitle = {
    left: '5',
    top: '5',
}
const defaultTitleStyle = {
    fontSize: 14
}
const defaultLegendLine = {
    bottom: 10,
    left: 'center',
    icon: 'emptyCircle',
    itemWidth: 10
}
const defaultLegend = {
    // orient: 'vertical',
    // top: 'middle',
    bottom: 10,
    left: 'center',
    icon: 'emptyCircle',
    itemWidth: 10
}
const defaltSeries = {
    selectedMode: 'single',
    radius : '60%',
    selectedOffset: 10,
    animation: false,
    center: ['50%', '47%']
}
const defaltSeries_label = {
    show: false,
    position: 'outside',
    formatter: "{b} \n {c} ({d}%)"
}
const defaultColor = ['#54d7b7', '#02abe7', '#8989ed', '#ae7fe7', '#ff6e73', '#3dc0b6', '#fdbd66','#1183ac','#dc4d52']

//计算最大值
const calMax = arr => Math.ceil(Math.max(...arr) / 9.5) * 10;//不让最高的值超过最上面的刻度，让显示的刻度是整数 
//计算最小值
const calMin = arr => Math.floor(Math.min(...arr));

//请求页面layout接口
export const getPageLayout = (type,layouts) => {
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + "/setting/insertOrUpdate", qs.stringify({
            type: type,
            userid: cookie.get('userId'),
            content: JSON.stringify(layouts)
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success('保存成功')
                resolve(result)
            } else {
                reject(result.msg);
                message.warning(result.msg);
            }
        }).catch(err => {
            reject(err);
        });
    })
}

export const moneyTypeonSave = (radioCurrency, radioLegal) => {
    let id = cookie.get('userId');
    let str = radioCurrency + ','+ radioLegal;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP+"/setting/insertOrUpdate",qs.stringify({
            type: 5,
            userid:  id,
            content: str
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success('保存成功')
                resolve(result);
            }else{
                message.warning(result.msg);
            }
        }).catch(err => {
            reject(err);
        });
    })
};
export const moneyTypeCancel = () => {
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP+"/setting/queryList",qs.stringify({})).then(res => {
            const result = res.data;
            let bb = '';
            let fb = '';
            if(result.code == 0){
                let list = result.data;
                list.forEach(function (item) {
                    if(item.type == '5') {
                        let list = item.content.split(',');
                        if(list.length>1) {
                            bb = list[0];
                            fb = list[1];
                        } else {
                            bb = 'BTC';
                            fb = 'CNY';
                        }
                    }
                });
                let arr = [bb, fb];
                resolve(arr);
            }else{
                message.warning(result.msg);
            }
        }).catch(err => {
            reject(err);
        });
    })
};

//请求echarts图表接口
export const chartsLoading = async (params) => {
    return judgeCharts(params)
}
const judgeCharts = (params) => {
    const { title: chartsTile, isShowTitle } = params
    switch (chartsTile) {
        case TRADE_ENGINE:
            return _tradeEngine(params)
        case TRADE_HANDICAP_DEAL:
            return _tradeHandicapDeal(params)
        case TRADE_HANDICAP_DEAL_DSIT:
            return _tradeHandicapDealDsit(params)
        case TRADE_NEW_OLD_USER_EXCHANGE:
            return _tradeNewOldUserExchange(params)
        case TRADE_RANKING:
            return _tradeRanking(params)
        case TRADE_TATE_TOTAL:
            return _tradeTateTotal(params)
        case TRADE_TREND:
            return _tradeTrend(params)
        case TRADE_USER_EXCHANGE_MONEY:
            return _tradeUserExchangeMoney(params)

        case AMOUNT_EXCHANGE_MONEY_DIST:
            return _amountExchangeMoneyDist(params)
        case AMOUNT_HAND_FEE_CHANGE_TREND:
            return _amountHeadFeeChangeTrend(params)
        case AMOUNT_HAND_FEE_HANDICAP:
            return _amountHeadFeeHandicap(params)
        case AMOUNT_MONEY_FLOW_TREND:
            return _amountMoneyFlowTrend(params)
        case AMOUNT_PLATFORM_MONEY_DIST:
            return _amountPlatformMoneyDist(params)
        case AMOUNT_PRESERV_MONEY_DIST:
            return _amountPerservMoneyDist(params)
        case AMOUNT_WALLET_CURRENCY_DIST:
            return _amountWalletCurrencyDist(params)
        case AMOUNT_WALLET_MONEY_DIST:
            return _amountWalletMoneyDist(params)
        case AMOUNT_USER_MONEY_DIST:
            return _amountUserMoneyDist(params)
        case AMOUNT_DEPOSIT_MONEY:
            return _amountDepositMoney(params)

        case USER_ACTIVE_FORM:
            return _userActiveForm(params)
        case USER_NEWUSER_TOTAL:
            return _userNewUserTotal(params)
        case USER_INTERVIEW_FLOW_TREND:
            return _userInterviewFlowTrend(params)
        case USER_EXCHANGE_NUM_RANKING:
            return _userExchangeNumRanking(params)
        case USER_CHINA_INTERVIEW:
            return _userChinaInterview(params)
        case USER_GLOBAL_INTERVIEW:
            return _userGlobalInterview(params)
        case USER_GAIN_RANKING:
            return _userGainRanking(params)
        case EMPLOYER_REGISTER:
            return _employerRegister(params);
        case EMPLOYER_EXCHAGNE_PROP:
            return _employerExchagneProp(params);
        case EMPLOYER_EXCHANGE_NUM_TREND:
            return _employerExchangeNumTrend(params);
        case EMPLOYER_EXCHANGE_FREQ_TREND:
            return _employerExchangeFreqTrend(params);
        case EMPLOYER_WITHDRAW_PROP:
            return _employerWithdrawProp(params);
        case EMPLOYER_DEPOSIT_PROP:
            return _employerDepositProp(params);
        case EMPLOYER_TO_PLATFORM_USER:
            return _employerToPlatformUser(params);
        case EMPLOYER_TO_X_USER:
            return _employerToxUser(params);
        case EMPLOYER_HAND_FEE_DEVOTE:
            return _employerHandFeeDevote(params);
        default:
            break
    }
}



const _tradeEngine = (args) => {
    const { title: chartsTile, isShowTitle, } = args
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jyTradeClient/list', qs.stringify({ createtime: args.startTime || defaulMonthTime.startTime, entrustmarket: args.scopeType || '',currencyType: args.radioCurrency }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let xList = result.data.map((item) => ({
                        name:item.clienttype,
                        value:item.usercoin
                    }));
                    let dw = '';
                    if(args.scopeType && args.scopeType != '') {
                        dw = args.scopeType.split('_')[0];
                    } else {
                        dw = args.radioCurrency;
                    }
                    let list = [{index: 1, type: 0, title: '交易币量'}];
                    let columns = newTableColumns(tradeEngineCols,list,dw,args.radioLegal);
                    let startTime =  moment(args.startTime || defaulMonthTime.startTime).format('YYYY-MM');
                    let str1 = startTime + '成交量  ' + result.totalamount + '单';
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div></div>,
                        tableOption: {
                            columns: columns,
                            tableData: result.data.map((item,index) => {item.id = item.id || index;return item}),
                        },
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                                // subtext: 'ECharts 示例副标题',
                            },
                            color: defaultColor,
                            tooltip: {
                                ...defaltTooltip
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '成交量',
                                type: "pie",
                                ...defaltSeries,
                                center: ['50%', '42%'],
                                label: {
                                    ...defaltSeries_label
                                },
                                data: xList,
                            }]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据',tableOption: {columns: columns, tableData: result.data}})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject()
                }
            })
    })
}

const _tradeHandicapDeal = (args) => {
    const { title: chartsTile, isShowTitle } = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jyEntrustMarket/list', qs.stringify({ createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let xList = result.data.entrustmarkets || [];
                    let seriesData = result.data.tradeamounts || [];
                    let startTime =  moment(args.startTime || defaulTime.startTime).format('YYYY-MM-DD');
                    let endTime = moment(args.endTime || defaulTime.endTime).format('YYYY-MM-DD');
                    let str1 = startTime + '/' + endTime+ '成交量  ' + result.data.tradeamount + '单';
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                trigger: 'axis',
                                axisPointer: {
                                    type: 'cross',
                                    crossStyle: {
                                        color: '#999'
                                    }
                                }
                            },
                            xAxis: [{
                                type: 'category',
                                name: '',
                                axisLabel: {
                                    showMaxLabel: true
                                },
                                data: xList,
                            }],
                            yAxis: [{
                                name: '成交单数'
                            }],
                            series: [
                                {
                                    name: '成交单数',
                                    label: {
                                        show: false,
                                    },
                                    type: 'bar',
                                    barWidth: barWidth,
                                    data: seriesData
                                },
                            ]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg})
                    reject()
                }
            })
    });
};

const _tradeHandicapDealDsit = (args) => {
    const { title: chartsTile, isShowTitle } = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jyDistributedMarket/list', qs.stringify({ createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime, currencyType: args.radioCurrency }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    result.data = result.data.map(function (item, index) {
                        item.id = index;
                        return item;
                    });
                    let xList = result.data.map(function (item) {
                        let obj = {};
                        obj.value = item.tradeamount;
                        obj.name = item.entrustmarket;
                        return obj;
                    });
                    let list = [{index: 3, type: 0, title: '交易币量'}];
                    let columns = newTableColumns(tradeHandicapCols,list,args.radioCurrency,args.radioLegal);
                    let startTime =  moment(args.startTime || defaulTime.startTime).format('YYYY-MM-DD');
                    let endTime = moment(args.endTime || defaulTime.endTime).format('YYYY-MM-DD');
                    let str1 = startTime + '/' + endTime+ '成交量  ' + result.tradeamount + '单';
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        tableOption: {
                            columns: columns,
                            tableData: result.data,
                        },
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                                // subtext: 'ECharts 示例副标题',
                            },
                            color: defaultColor,
                            tooltip: {
                                ...defaltTooltip
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '成交量',
                                type: "pie",
                                ...defaltSeries,
                                label: {
                                    ...defaltSeries_label
                                },
                                data: xList,
                            }]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据',tableOption: {columns: [], tableData: []}})
                } else {
                    resolve({loading:false,noData:chartsTile + result.msg});
                    reject()
                }
            })
    });
}

const _tradeNewOldUserExchange = (args) => {
    const { title: chartsTile, isShowTitle } = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jyUserTrade/list', qs.stringify({ createtime: args.startTime || defaulMonthTime.startTime,entrustmarket: args.scopeType || '',currencyType: args.radioCurrency }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    result.data = result.data.map(function (item,index) {
                        item.id = index;
                        return item;
                    });
                    let xList = result.data.map(function (item) {
                        let obj = {};
                        obj.value = item.usercoin;
                        obj.name = item.usertype == 0? '老用户': '新用户';
                        return obj;
                    });
                    let dw = '';
                    if(args.scopeType && args.scopeType != '') {
                        dw = args.scopeType.split('_')[0];
                    } else {
                        dw = args.radioCurrency;
                    }
                    let list = [{index: 1, type: 0, title: '交易币量'}];
                    let columns = newTableColumns(tradeUserOldCols,list,dw,args.radioLegal);
                    let startTime =  moment(args.startTime || defaulMonthTime.startTime).format('YYYY-MM');
                    let str1 = startTime + '总交易量  ' + result.totalamount + dw;
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        tableOption: {
                            columns: columns,
                            tableData: result.data,
                        },
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                ...defaltTooltip
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '交易量',
                                type: "pie",
                                ...defaltSeries,
                                label: {
                                    ...defaltSeries_label
                                },
                                data: xList,
                            }]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据',tableOption:{columns:[],tableData:[]}});
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject()
                }
            })
    });
};

const _tradeRanking = (args) => {
    const { title: chartsTile, isShowTitle,scopeType } = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jyRanking/list', qs.stringify({}))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let seriesData = result.data || [];
                    let option = {
                        loading: false,
                        tableOption: {
                            columns: tradeRankCols,
                            tableData: seriesData,
                        }
                    };
                    seriesData.length > 0 ? resolve(option) : resolve({loading:false,tableOption: {columns: [],tableData: []}})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })
    });
};

const _tradeTateTotal = (args) => {
    const { title: chartsTile, isShowTitle,scopeType } = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jyConversionRate/list', qs.stringify({ createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime, entrustmarket: scopeType || '',currencyType: args.radioCurrency, FCurrencyType: args.radioLegal }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let seriesData = result.data || [];
                    result.data = result.data.map(function (item, index) {
                        item.id = index;
                        return item;
                    });
                    let dw = '';
                    if(args.scopeType && args.scopeType != '') {
                        dw = args.scopeType.split('_')[0];
                    } else {
                        dw = args.radioCurrency;
                    }
                    let list = [{index: 3, type: 0, title: '委托币量'},{index: 4, type: 1, title: '委托金额'}];
                    let list2 = [{index: 3, type: 0, title: '成交币量'},{index: 4, type: 1, title: '成交金额'}];
                    let column1 = newTableColumns(tradeRateCols1,list, dw,args.radioLegal);
                    let column2 = newTableColumns(tradeRateCols2,list2, dw,args.radioLegal);
                    let option = {
                        loading: false,
                        noData:false,
                        tableOption: {
                            columns: column1,
                            tableData: result.data,
                            columns2: column2,
                        },
                        isImage: tradeTateTotal,
                        persentData: seriesData
                    };
                    seriesData.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据',tableOption: {columns: [],tableData: [],columns2: []}})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })
    });
}

const _tradeTrend = (args) => {
    const { title: chartsTile, isShowTitle, timeType} = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jyTradeCurrent/list', qs.stringify({ createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime, timeType: args.timeType || 1,entrustmarket: args.scopeType || '', currencyType: args.radioCurrency, FCurrencyType: args.radioLegal }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let xList = formatDateList(result.data.longtimes, timeType);
                    let amountList = result.data.amountList || [];
                    let coinList = result.data.coinList || [];
                    let startTime =  moment(args.startTime || defaulTime.startTime).format('YYYY-MM-DD');
                    let endTime = moment(args.endTime || defaulTime.endTime).format('YYYY-MM-DD');
                    let dw = '';
                    if(args.scopeType && args.scopeType != '') {
                        dw = args.scopeType.split('_')[0];
                    } else {
                        dw = args.radioCurrency;
                    }
                    let str1 = startTime + '/' + endTime+ '交易额合计  ' + result.amount + args.radioLegal;
                    let str2 = startTime + '/' + endTime+ '交易币量合计  ' + result.coin + dw;
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}<br/>{str2}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                trigger: 'axis',
                                axisPointer: {
                                    type: 'cross',
                                    crossStyle: {
                                        color: '#999'
                                    }
                                }
                            },
                            legend: {
                                top: 50,
                                icon: 'line',
                                itemWidth: 10,
                            },
                            grid: {
                                top: 120,
                                bottom: 40,
                                left:'100',
                                right:'90',
                            },
                            xAxis: [{
                                type: 'category',
                                axisLabel: {
                                    showMaxLabel: true
                                },
                                data: xList,
                            }],
                            yAxis: [{
                                name: '交易额' + args.radioLegal,
                            },{
                                name: '交易币量' + dw,
                                splitLine:{
                                    show:false
                                },
                            }],
                            series: [
                                {
                                    name: '交易额',
                                    type: 'line',
                                    data: amountList
                                },
                                {
                                    name: '交易币量',
                                    type: 'line',
                                    yAxisIndex: 1,
                                    data: coinList
                                },
                            ]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })
    });
};

const _tradeUserExchangeMoney = (args) => {
    const { title: chartsTile, isShowTitle } = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jyUserMoney/list', qs.stringify({ createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime,entrustmarket: args.scopeType || '' }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let xList = result.data.intervals || [];
                    let seriesData = result.data.numbers || [];
                    let option = {
                        loading: false,
                        noData:false,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                trigger: 'axis',
                                axisPointer: {
                                    type: 'cross',
                                    crossStyle: {
                                        color: '#999'
                                    }
                                }
                            },
                            xAxis: [{
                                type: 'category',
                                name: '(USD)',
                                axisLabel: {
                                    showMaxLabel: true
                                },
                                data: xList,
                            }],
                            yAxis: [{
                                name: '人数'
                            }],
                            series: [
                                {
                                    name: '人数',
                                    label: {
                                        show: false,
                                    },
                                    type: 'bar',
                                    barWidth: barWidth,
                                    data: seriesData
                                },
                            ]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg})
                    reject()
                }
            })
    });
}

const _amountDepositMoney = (args) => {
    const { title: chartsTile, isShowTitle, } = args
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                    },
                    color: defaultColor,
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            crossStyle: {
                                color: '#999'
                            }
                        }
                    },
                    legend: {
                        ...defaultLegend,
                        data: ['买入', '卖出']
                    },
                    xAxis: [{
                        type: 'category',
                        axisLabel: {
                            showMaxLabel: true
                        },
                        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
                    }],
                    yAxis: [{}],
                    series: [
                        {
                            name: '买入',
                            stack: '总量',
                            label: {
                                show: true,
                            },
                            type: 'bar',
                            data: [5, 20, 36, 10, 10, 30, 50, 20, 36, 10, 15, 20]
                        },
                        {
                            name: '卖出',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                show: true,
                            },
                            data: [8, 20, 30, 15, 10, 20, 5, 20, 34, 14, 10, 20]
                        },
                        {
                            name: '折现',
                            type: 'line',
                            data: [8, 20, 30, 15, 10, 20, 5, 20, 34, 14, 10, 20]
                        },
                    ]
                }
            }
            resolve(option)
        }, 5000)
    })
}

const _amountExchangeMoneyDist = (args) => {
    const { title: chartsTile, isShowTitle} = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jeAmountDistribution/queryList', qs.stringify({currencyType: args.radioCurrency}))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let seriesData = result.data.seriesData || [];
                    let _isBrushLess0 = false;
                    let newSeriesData = seriesData.map((item) => {
                        if(item.value < 0){
                            _isBrushLess0 = true;
                            item.value = Math.abs(Number(item.value))
                        }
                        return item
                    })
                    let str1 = '总计  ' + result.data.totalNum + args.radioCurrency;
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                trigger: 'item',
                                formatter:({ seriesName, percent, data: {name, value } }) => `<span style="display:inline-block;margin-right:5px;border-radius:10px;padding:10px;">
                                    ${seriesName}<br />${name}: ${name == '保值刷量' && _isBrushLess0 ? '-' + value : value} (${percent}%)
                                    </span>`,
                                backgroundColor: 'rgba(50,50,50,0.7)',
                                padding: 0,
                                textStyle: {
                                    color: '#FFFFFF',
                                },
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '交易所金额',
                                type: "pie",
                                ...defaltSeries,
                                label: {
                                    ...defaltSeries_label
                                },
                                data: newSeriesData,
                            }]
                        }
                    };
                    seriesData.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })


    });
};

const _amountHeadFeeChangeTrend = (args) => {
    const { title: chartsTile, isShowTitle,timeType,scopeType } = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jeFees/queryListOne', qs.stringify({ createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime, timeType: timeType || 1, market: scopeType || '', currencyType: args.radioCurrency }))
            .then(res => {
                const result = res.data;
                let xList = formatDateList(result.data.longtimes, timeType);
                let longnumber = result.data.longnumber || [];
                let startTime =  moment(args.startTime || defaulTime.startTime).format('YYYY-MM-DD');
                let endTime = moment(args.endTime || defaulTime.endTime).format('YYYY-MM-DD');
                let str1 = startTime + '/' + endTime+ '总计  ' + result.data.feesSum + args.radioCurrency;
                if (result.code == 0) {
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                trigger: 'axis'
                            },
                            // legend: {
                            //     top: 35,
                            //     icon: 'line',
                            //     itemWidth: 10,
                            // },
                            grid: {
                                top: 120,
                                bottom: 40
                            },
                            xAxis: {
                                name: '',
                                type: 'category',
                                boundaryGap: false,
                                data: xList
                            },
                            yAxis: [
                                {
                                    name: '币量('+ args.radioCurrency + ')',
                                    type: 'value',
                                }
                            ],
                            series: [
                                {
                                    name: '币量',
                                    type: 'line',
                                    data: longnumber
                                }
                            ]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })
    });
}

const _amountHeadFeeHandicap = (args) => {
    const { title: chartsTile, isShowTitle} = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jeFees/queryList', qs.stringify({ createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime,currencyType: args.radioCurrency}))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let seriesData = result.data.seriesData || [];
                    let startTime =  moment(args.startTime || defaulTime.startTime).format('YYYY-MM-DD');
                    let endTime = moment(args.endTime || defaulTime.endTime).format('YYYY-MM-DD');
                    let str1 = startTime + '/' + endTime+ '总计  ' + result.data.totalNum + args.radioCurrency;
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                ...defaltTooltip
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '',
                                type: "pie",
                                ...defaltSeries,
                                label: {
                                    ...defaltSeries_label
                                },
                                data: seriesData,
                            }]
                        }
                    };
                    seriesData.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })
    });
}

const _amountMoneyFlowTrend = (args) => {
    const { title: chartsTile, isShowTitle,timeType } = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jeCashFlow/queryList', qs.stringify({ createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime, timeType: timeType || 1,fundstype: args.fundsType || '2' }))
            .then(res => {
                const result = res.data;
                let xList = formatDateList(result.data.longtimes, timeType);
                let rechargeList = result.data.rechargeList || []; //充值
                let downloadList = result.data.downloadList || []; //提现
                let startTime =  moment(args.startTime || defaulTime.startTime).format('YYYY-MM-DD');
                let endTime = moment(args.endTime || defaulTime.endTime).format('YYYY-MM-DD');
                let fundsList = JSON.parse(sessionStorage.getItem('fundsTypeList'));
                let dw = '';
                //总览的时候不一定能取到fundsList
                if(args.fundsType) {
                    for(let i = 0; i < fundsList.length; i++) {
                        if (fundsList[i].paracode == args.fundsType) {
                            dw = fundsList[i].paravalue;
                            break;
                        }
                    }
                } else {
                    dw = 'BTC';
                }
                let str1 = startTime + '/' + endTime+ '充值总计  ' + result.data.rechargeSum + dw;
                let str2 = startTime + '/' + endTime+ '提现总计  ' + result.data.downloadSum + dw;
                if (result.code == 0) {
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}<br/>{str2}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                trigger: 'axis'
                            },
                            legend: {
                                top: 50,
                                icon: 'line',
                                itemWidth: 10,
                            },
                            grid: {
                                top: 120,
                                bottom: 40
                            },
                            xAxis: {
                                name: '',
                                type: 'category',
                                boundaryGap: false,
                                data: xList
                            },
                            yAxis: [
                                {
                                    name: dw,
                                    type: 'value',
                                }
                            ],
                            series: [
                                {
                                    name: '充值',
                                    type: 'line',
                                    data: rechargeList
                                },
                                {
                                    name: '提现',
                                    type: 'line',
                                    data: downloadList
                                }
                            ]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })
    });
}

const _amountPlatformMoneyDist = (args) => {
    const { title: chartsTile, isShowTitle} = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jePlatforMmoney/queryList', qs.stringify({currencyType: args.radioCurrency}))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let seriesData = result.data.seriesData || [];
                    let str1 = '总计  ' + result.data.totalNum + args.radioCurrency;
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                ...defaltTooltip
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '平台账户资金',
                                type: "pie",
                                ...defaltSeries,
                                label: {
                                    ...defaltSeries_label
                                },
                                data: seriesData,
                            }]
                        }
                    };
                    seriesData.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })


    });
}

const _amountPerservMoneyDist = (args) => {
    const { title: chartsTile, isShowTitle} = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jeHedgeAccountBalance/queryList', qs.stringify({currencyType: args.radioCurrency}))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let seriesData = result.data.seriesData || [];
                    let str1 = '总计  ' + result.data.totalNum + args.radioCurrency;
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                ...defaltTooltip
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '保值资金',
                                type: "pie",
                                ...defaltSeries,
                                label: {
                                    ...defaltSeries_label
                                },
                                data: seriesData,
                            }]
                        }
                    };
                    seriesData.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })


    });
}

const _amountWalletCurrencyDist = (args) => {
    const { title: chartsTile, isShowTitle} = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jeWalletAmount/queryCoinList', qs.stringify({currencyType: args.radioCurrency}))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let seriesData = result.data.seriesData || [];
                    let str1 = '总计  ' + result.data.totalNum + args.radioCurrency;
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                ...defaltTooltip
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '钱包货币',
                                type: "pie",
                                ...defaltSeries,
                                label: {
                                    ...defaltSeries_label
                                },
                                data: seriesData,
                            }]
                        }
                    };
                    seriesData.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })
    });
}

const _amountWalletMoneyDist = (args) => {
    const { title: chartsTile, isShowTitle} = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jeWalletAmount/queryList', qs.stringify({currencyType: args.radioCurrency}))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let seriesData = result.data.seriesData || [];
                    let str1 = '总计  ' + result.data.totalNum + args.radioCurrency;
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                ...defaltTooltip
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '钱包资金',
                                type: "pie",
                                ...defaltSeries,
                                label: {
                                    ...defaltSeries_label
                                },
                                data: seriesData,
                            }]
                        }
                    };
                    seriesData.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })
    });
}

const _amountUserMoneyDist = (args) => {
    const { title: chartsTile, isShowTitle} = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jeUserCurrency/queryList', qs.stringify({currencyType: args.radioCurrency}))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let seriesData = result.data.seriesData || [];
                    let str1 = '总计  ' + result.data.totalNum + args.radioCurrency;
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                ...defaltTooltip
                            },
                            legend: {
                                ...defaultLegend,
                            },
                            series: [{
                                name: '用户货币',
                                type: "pie",
                                ...defaltSeries,
                                label: {
                                    ...defaltSeries_label
                                },
                                data: seriesData,
                            }]
                        }
                    };
                    seriesData.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })


    });
}


const _userActiveForm = (args) => {
    const { title: chartsTile, isShowTitle, timeType} = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/yhuser/query', qs.stringify({ browsetimeS: args.startTime || defaulTime.startTime, browsetimeE: args.endTime || defaulTime.endTime, timeType: args.timeType || 1 }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let xList = formatDateList(result.data.longtimes, timeType);
                    let newList = result.data.longnewusers || [];
                    let oldList = result.data.longoldusers || [];
                    let persentList = result.data.proportion || [];
                    let startTime =  moment(args.startTime || defaulTime.startTime).format('YYYY-MM-DD');
                    let endTime = moment(args.endTime || defaulTime.endTime).format('YYYY-MM-DD');
                    let str1 = startTime + '/' + endTime+ '新用户总计  ' + result.data.newuser + '人次';
                    let str2 = startTime + '/' + endTime+ '老用户总计  ' + result.data.olduser + '人次';
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                trigger: 'axis',
                                axisPointer: {
                                    type: 'cross',
                                    crossStyle: {
                                        color: '#999'
                                    }
                                }
                            },
                            legend: {
                                ...defaultLegend,
                                data: ['新用户','老用户','新用户占比']
                            },
                            xAxis: [{
                                type: 'category',
                                axisLabel: {
                                    showMaxLabel: true
                                },
                                data: xList,
                            }],
                            yAxis: [{},{
                                splitLine:{
                                    show:false
                                },
                                axisLabel: {
                                    type: "value",
                                    name: '',
                                    formatter: function (params) {
                                        return ((params) * 10000/100) + '%';
                                    }
                                }
                            }],
                            series: [
                                {
                                    name: '老用户',
                                    stack: '总数',
                                    label: {
                                        show: true,
                                    },
                                    type: 'bar',
                                    barWidth: barWidth,
                                    data: oldList
                                },
                                {
                                    name: '新用户',
                                    type: 'bar',
                                    stack: '总数',
                                    label: {
                                        show: true,
                                    },
                                    barWidth: barWidth,
                                    data: newList
                                },
                                {
                                    name: '新用户占比',
                                    type: 'line',
                                    yAxisIndex: 1,
                                    data: persentList
                                },
                            ]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })


    })
}
const _userNewUserTotal = (args) => {
    const { title: chartsTile, isShowTitle, timeType } = args
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/yhnewuser/query', qs.stringify({ registertimeS: args.startTime || defaulTime.startTime, registertimeE: args.endTime || defaulTime.endTime, timeType: timeType || 1 }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let xList = formatDateList(result.data.longtimes, timeType);
                    let longusers = result.data.longusers || [];
                    let startTime =  moment(args.startTime || defaulTime.startTime).format('YYYY-MM-DD');
                    let endTime = moment(args.endTime || defaulTime.endTime).format('YYYY-MM-DD');
                    let str1 = startTime + '/' + endTime+ '总计  ' + result.data.num + '人';
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                trigger: 'axis'
                            },
                            legend: {
                                top: 35,
                                icon: 'line',
                                itemWidth: 10,
                                data: ['新增用户']
                            },
                            grid: {
                                top: 120,
                                bottom: 40
                            },
                            xAxis: {
                                name: '统计日期',
                                type: 'category',
                                boundaryGap: false,
                                data: xList
                            },
                            yAxis: [
                                {
                                    name: '人数/人',
                                    type: 'value',
                                    // max: 500
                                    minInterval:1
                                },
                            ],
                            series: [
                                {
                                    name: '新增用户',
                                    type: 'line',
                                    // stack: '总量',
                                    data: longusers
                                }
                            ]
                        }
                    }
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg})
                    reject()
                }
            })
    })
}
const _userInterviewFlowTrend = (args) => {
    const { title: chartsTile, isShowTitle,timeType } = args;
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/jyConversionRateCookie/query', qs.stringify({ browsetimeS: args.startTime || defaulTime.startTime, browsetimeE: args.endTime || defaulTime.endTime, timeType: timeType || 1 }))
            .then(res => {
                const result = res.data;
                let xList = formatDateList(result.data.longtimes, timeType);
                let pvList = result.data.longcount || [];
                let uvList = result.data.longusers || [];
                let startTime =  moment(args.startTime || defaulTime.startTime).format('YYYY-MM-DD');
                let endTime = moment(args.endTime || defaulTime.endTime).format('YYYY-MM-DD');
                let str1 = startTime + '/' + endTime+ 'PV总计  ' + result.data.browseNumAll + '次';
                let str2 = startTime + '/' + endTime+ 'UV总计  ' + result.data.numAll + '次';
                if (result.code == 0) {
                    let option = {
                        loading: false,
                        noData:false,
                        totalTitle: <div>{str1}<br/>{str2}</div>,
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                            },
                            color: defaultColor,
                            tooltip: {
                                trigger: 'axis'
                            },
                            legend: {
                                top: 50,
                                icon: 'line',
                                itemWidth: 10,
                                data: ['PV', 'UV']
                            },
                            grid: {
                                top: 120,
                                bottom: 40
                            },
                            xAxis: {
                                name: '',
                                type: 'category',
                                boundaryGap: false,
                                data: xList
                            },
                            yAxis: [
                                {
                                    name: '次数',
                                    type: 'value',
                                    minInterval: 1,
                                    // max: 500
                                }
                            ],
                            series: [
                                {
                                    name: 'PV',
                                    type: 'line',
                                    data: pvList
                                },
                                {
                                    name: 'UV',
                                    type: 'line',
                                    data: uvList
                                }
                            ]
                        }
                    };
                    xList.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据'})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            })
    });
}
const _userExchangeNumRanking = (args) => {
    const { title: chartsTile, isShowTitle, } = args
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/yhtransactionvolume/query', qs.stringify({createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime,currencyType:args.radioCurrency,FCurrencyType:args.radioLegal}))
        .then(res => {
            const result = res.data;
            if(result.code == 0){
                let list = result.data || []
                let option = {
                    tableOption:{
                        columns: tradeRankCols,
                        tableData: list,
                        loading: false
                    }               
                }
                resolve(option)
            }else{
                reject()
            }
        })       
    })
}
const _userChinaInterview = (args) => {
    const { title: chartsTile, isShowTitle, } = args
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/yhAccessDistribution/query', qs.stringify({ areatype: 2, createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime }))
            .then(res => {
                const result = res.data
                if (result.code == 0) {
                    let yData = [];
                    let barData = [];
                    let data = result.data || []
                    data.forEach((item, index) => {
                        item.name = item.area;
                        item.value = item.proportion;
                        item.key = index;
                        item.id = index;

                    })
                    for (let i = 0; i < data.length; i++) {
                        barData.push(data[i]);
                        yData.push(i + data[i].name);
                    }
                    let top10 = data.slice(0,10)
                    let option = {
                        loading: false,
                        showTable: true,
                        tableOption: {
                            columns: interviewFlowCols,
                            tableData: top10,
                        },
                        option: {
                            title: [{
                                text: isShowTitle ? chartsTile : '',
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                                // right: 180,
                                // top: 100
                            }],
                            tooltip: {
                                show: true,
                                trigger: 'item',
                                formatter: function (params) {
                                    if (!params.data) return
                                    let str = `<div style=min-width:150px;>
                                        <div style=padding-bottom:5px;padding-top:5px;padding-left:10px;background-color:#e9e7e7;> ${params.name || ''}</div>
                                        <div style=padding-bottom:5px;padding-top:5px;padding-left:10px;padding-right:10px><span style=display:inline-block;width:8px;height:8px;border-radius:50%;background-color:#3385ff></span> 浏览量 <span style=float:right;> ${params.data['num']}</span></div>
                                        <div style=padding-bottom:5px;padding-left:10px;padding-right:10px><span style=display:inline-block;width:8px;height:8px;border-radius:50%;background-color:#77e490></span> 占比 <span style=float:right;> ${params.data['value']}%</span></div>
                                        </div>`
                                    return str
                                },
                                backgroundColor: 'rgba(255,255,255,1)',
                                padding: 0,
                                textStyle: {
                                    color: '#333',
                                    fontSize: 12,
                                },
                            },
                            visualMap: {
                                type: 'continuous',
                                orient: 'horizontal',
                                itemWidth: 10,
                                itemHeight: 80,
                                text: ['高', '低'],
                                showLabel: true,
                                seriesIndex: [0],
                                min: 0,
                                max: 100,
                                inRange: {
                                    color: ['#d3dfef', '#90bcfa', '#3385ff']
                                },
                                textStyle: {
                                    color: '#7B93A7'
                                },
                                bottom: 30,
                                left: 'left',
                            },
                            geo: {
                                roam: true,
                                map: 'china',
                                label: {
                                    emphasis: {
                                        show: false
                                    }
                                },
                                itemStyle: {
                                    emphasis: {
                                        areaColor: '#fff464'
                                    }
                                }
                            },
                            series: [{
                                name: 'mapSer',
                                type: 'map',
                                roam: false,
                                geoIndex: 0,
                                label: {
                                    show: false,
                                },
                                data: data
                            }]
                        }
                    }
                    data.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据',tableOption: {columns: [],tableData: [] }})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject()
                }

            })
    })
}
const _userGlobalInterview = (args) => {
    const { title: chartsTile, isShowTitle, } = args
    return new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + '/yhAccessDistribution/query', qs.stringify({ areatype: 1, createtimeS: args.startTime || defaulTime.startTime, createtimeE: args.endTime || defaulTime.endTime }))
            .then(res => {
                const result = res.data;
                if (result.code == 0) {
                    let data = result.data || [];
                    let obj = {};
                    contries.forEach(function (item) {
                        obj[item.cname] = item.name;
                    });
                    data.forEach((item, index) => {
                        item.name = obj[item.area];
                        item.noMap = item.area;
                        item.value = item.proportion;
                        item.key = index;
                        item.id = index;
                    });
                    let top10 = data.slice(0,10)
                    let option = {
                        loading: false,
                        showTable: true,
                        tableOption: {
                            columns: interviewWorldCols,
                            tableData: top10,
                        },
                        option: {
                            title: {
                                text: isShowTitle ? chartsTile : '',
                                ...defaultTitle,
                                textStyle: {
                                    ...defaultTitleStyle
                                }
                                // subtext: 'ECharts 示例副标题',
                            },
                            tooltip: {
                                show: true,
                                trigger: 'item',
                                formatter: function (params) {
                                    if (!params.data) return;
                                    let str = '<div style=min-width:150px;>'
                                    str = str + '<div style=padding-bottom:5px;padding-top:5px;padding-left:10px;background-color:#e9e7e7;>' + params.data['noMap'] + '</div>'
                                    str = str + '<div style=padding-bottom:5px;padding-top:5px;padding-left:10px;padding-right:10px><span style=display:inline-block;width:8px;height:8px;border-radius:50%;background-color:#3385ff></span>' + ' 浏览量' + '<span style=float:right;>' + params.data['num'] + '</span></div>'
                                    str = str + '<div style=padding-bottom:5px;padding-left:10px;padding-right:10px><span style=display:inline-block;width:8px;height:8px;border-radius:50%;background-color:#77e490></span>' + ' 占比' + '<span style=float:right;>' + params.data['value'] + '%</span></div>'
                                    str = str + '</div>'
                                    return str
                                },
                                backgroundColor: 'rgba(255,255,255,1)',
                                padding: 0,
                                textStyle: {
                                    color: '#333',
                                    fontSize: 12,
                                },
                            },
                            visualMap: {
                                type: 'continuous',
                                orient: 'horizontal',
                                itemWidth: 10,
                                itemHeight: 80,
                                text: ['高', '低'],
                                showLabel: true,
                                seriesIndex: [0],
                                min: 0,
                                max: 100,
                                inRange: {
                                    color: ['#d3dfef', '#90bcfa', '#3385ff']
                                },
                                textStyle: {
                                    color: '#7B93A7'
                                },
                                bottom: 30,
                                left: 'left',
                            },
                            series: [
                                {
                                    name: 'World Population (2010)',
                                    type: 'map',
                                    mapType: 'world',
                                    roam: true,
                                    itemStyle: {
                                        emphasis: { label: { show: false } }
                                    },
                                    data: data
                                }
                            ]
                        }
                    }
                    data.length > 0 ? resolve(option) : resolve({loading:false,noData:chartsTile + '暂无数据',tableOption: {columns: [],tableData: [] }})
                } else {
                    resolve({loading:false,noData:result.msg});
                    reject();
                }
            });
    })
}
const _userGainRanking = (args) => {
    const { title: chartsTile, isShowTitle, } = args
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                        // subtext: 'ECharts 示例副标题',
                    },
                    color: defaultColor,
                    tooltip: {
                        ...defaltTooltip
                    },
                    legend: {
                        ...defaultLegend,
                        data: ['直接', '邮件', '联盟', '视频', '搜索']
                    },
                    series: [{
                        name: '销量',
                        type: "pie",
                        ...defaltSeries,
                        label: {
                            ...defaltSeries_label
                        },
                        data: [
                            { value: 3335, name: '直接' },
                            { value: 310, name: '邮件' },
                            { value: 234, name: '联盟' },
                            { value: 135, name: '视频' },
                            { value: 1548, name: '搜索' }
                        ],
                    }]
                }
            }
            resolve(option)
        }, 5000)
    })
}

const _employerRegister = (args) => {
    const { title: chartsTile, isShowTitle, } = args;
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                    },
                    color: defaultColor,
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        top: 35,
                        icon: 'line',
                        itemWidth: 10,
                    },
                    grid: {
                        top: 120,
                        bottom: 40
                    },
                    xAxis: {
                        name: '',
                        type: 'category',
                        boundaryGap: false,
                        data: ['3月7日', '2月28日', '3月1日', '3月2日', '3月3日', '3月4日', '3月5日'],
                    },
                    yAxis: [
                        {
                            name: '人数',
                            type: 'value',
                        }
                    ],
                    series: [
                        {
                            name: '人数',
                            type: 'line',
                            data: [100, 200, 500, 300, 200, 300, 600]
                        }
                    ]
                }
            }
            resolve(option)
        }, 5000)
    });
};
const _employerExchagneProp = (args) => {
    const { title: chartsTile, isShowTitle, } = args;
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                        // subtext: 'ECharts 示例副标题',
                    },
                    color: defaultColor,
                    tooltip: {
                        ...defaltTooltip
                    },
                    legend: {
                        ...defaultLegend,
                    },
                    series: [{
                        name: '',
                        type: "pie",
                        ...defaltSeries,
                        label: {
                            ...defaltSeries_label
                        },
                        data: [
                            { value: 200, name: '普通用户' },
                            { value: 300, name: 'X项目用户' },
                        ],
                    }]
                }
            }
            resolve(option)
        }, 5000)
    })
};
const _employerExchangeNumTrend = (args) => {
    const { title: chartsTile, isShowTitle, } = args;
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                    },
                    color: defaultColor,
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        top: 35,
                        icon: 'line',
                        itemWidth: 10,
                    },
                    grid: {
                        top: 120,
                        bottom: 40
                    },
                    xAxis: {
                        name: '',
                        type: 'category',
                        boundaryGap: false,
                        data: ['3月7日', '2月28日', '3月1日', '3月2日', '3月3日', '3月4日', '3月5日'],
                    },
                    yAxis: [
                        {
                            name: '交易量(USD)',
                            type: 'value',
                        }
                    ],
                    series: [
                        {
                            name: '普通用户',
                            type: 'line',
                            data: [100, 200, 500, 300, 200, 300, 600]
                        },
                        {
                            name: 'X项目用户',
                            type: 'line',
                            data: [100, 300, 200, 500, 100, 400, 200]
                        }
                    ]
                }
            }
            resolve(option)
        }, 5000)
    });
};
const _employerExchangeFreqTrend = (args) => {
    const { title: chartsTile, isShowTitle, } = args;
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                    },
                    color: defaultColor,
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        top: 35,
                        icon: 'line',
                        itemWidth: 10,
                    },
                    grid: {
                        top: 120,
                        bottom: 40
                    },
                    xAxis: {
                        name: '',
                        type: 'category',
                        boundaryGap: false,
                        data: ['3月7日', '2月28日', '3月1日', '3月2日', '3月3日', '3月4日', '3月5日'],
                    },
                    yAxis: [
                        {
                            name: '单数',
                            type: 'value',
                        }
                    ],
                    series: [
                        {
                            name: '普通用户',
                            type: 'line',
                            data: [100, 230, 550, 200, 500, 300, 600]
                        },
                        {
                            name: 'X项目用户',
                            type: 'line',
                            data: [100, 200, 110, 500, 100, 400, 200]
                        }
                    ]
                }
            }
            resolve(option)
        }, 5000)
    });
};
const _employerWithdrawProp = (args) => {
    const { title: chartsTile, isShowTitle, } = args;
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                        // subtext: 'ECharts 示例副标题',
                    },
                    color: defaultColor,
                    tooltip: {
                        ...defaltTooltip
                    },
                    legend: {
                        ...defaultLegend,
                    },
                    series: [{
                        name: '',
                        type: "pie",
                        ...defaltSeries,
                        label: {
                            ...defaltSeries_label
                        },
                        data: [
                            { value: 200, name: '存入X币' },
                            { value: 300, name: '提现非X币' },
                        ],
                    }]
                }
            }
            resolve(option)
        }, 5000)
    })
};
const _employerDepositProp = (args) => {
    const { title: chartsTile, isShowTitle, } = args;
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                    },
                    color: defaultColor,
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        top: 35,
                        icon: 'line',
                        itemWidth: 10,
                    },
                    grid: {
                        top: 120,
                        bottom: 40
                    },
                    xAxis: {
                        name: '',
                        type: 'category',
                        boundaryGap: false,
                        data: ['3月7日', '2月28日', '3月1日', '3月2日', '3月3日', '3月4日', '3月5日'],
                    },
                    yAxis: [
                        {
                            name: '币量(USD)',
                            type: 'value',
                        }
                    ],
                    series: [
                        {
                            name: '存入',
                            type: 'line',
                            data: [100, 230, 550, 200, 500, 300, 600]
                        },
                        {
                            name: '抛售',
                            type: 'line',
                            data: [100, 200, 110, 500, 100, 400, 200]
                        }
                    ]
                }
            }
            resolve(option)
        }, 5000)
    });
};

const _employerToPlatformUser = (args) => {
    const { title: chartsTile, isShowTitle, } = args;

    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                        // subtext: 'ECharts 示例副标题',
                    },
                    color: defaultColor,
                    tooltip: {
                        ...defaltTooltip
                    },
                    legend: {
                        ...defaultLegend,
                    },
                    series: [{
                        name: '',
                        type: "pie",
                        ...defaltSeries,
                        label: {
                            ...defaltSeries_label
                        },
                        data: [
                            { value: 200, name: '转为平台用户' },
                            { value: 300, name: '未转化的X用户' },
                        ],
                    }]
                }
            }
            resolve(option)
        }, 5000)
    })
};
const _employerToxUser = (args) => {
    const { title: chartsTile, isShowTitle, } = args;

    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                        // subtext: 'ECharts 示例副标题',
                    },
                    color: defaultColor,
                    tooltip: {
                        ...defaltTooltip
                    },
                    legend: {
                        ...defaultLegend,
                    },
                    series: [{
                        name: '',
                        type: "pie",
                        ...defaltSeries,
                        label: {
                            ...defaltSeries_label
                        },
                        data: [
                            { value: 200, name: '转为X用户' },
                            { value: 300, name: '未转化的普通用户' },
                        ],
                    }]
                }
            }
            resolve(option)
        }, 5000)
    })
};
const _employerHandFeeDevote = (args) => {
    const { title: chartsTile, isShowTitle, } = args;

    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let option = {
                loading: false,
                option: {
                    title: {
                        text: isShowTitle ? chartsTile : '',
                        ...defaultTitle,
                        textStyle: {
                            ...defaultTitleStyle
                        }
                    },
                    color: defaultColor,
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            crossStyle: {
                                color: '#999'
                            }
                        }
                    },
                    legend: {
                        ...defaultLegend,
                    },
                    xAxis: [{
                        type: 'category',
                        axisLabel: {
                            showMaxLabel: true
                        },
                        data: ['BTC/USDT', 'EOS/USDT', 'EOS/BTC', 'ETH/BTC', 'BTH/BTC', 'BCH/USDT', 'LTC/USDT', '其他'],
                    }],
                    yAxis: [
                        {
                            name: '金额（USD）',
                            type: 'value',
                        }
                    ],
                    series: [
                        {
                            name: '普通用户',
                            label: {
                                show: true,
                            },
                            type: 'bar',
                            data: [5, 20, 36, 10, 10, 30, 50, 20]
                        },
                        {
                            name: 'X项目用户',
                            type: 'bar',
                            label: {
                                show: true,
                            },
                            data: [8, 20, 30, 15, 10, 20, 5, 20]
                        }
                    ]
                }
            }
            resolve(option)
        }, 5000)
    })
}