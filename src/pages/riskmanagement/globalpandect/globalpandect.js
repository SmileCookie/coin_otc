import React, { Component } from 'react'
import data from './data'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import * as defaultData from './data'
import * as warningToKeep from './data/information'
import { Button, DatePicker, Tabs, Pagination, Select, message, Table, Modal } from 'antd'
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT } from '../../../conf'
// import GoogleCode from '../../common/modal/googleCode'
import { jumpItem } from '../../common/pageurl'


// const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// // const TabPane = Tabs.TabPane;
// const Option = Select.Option;
// const {Column} = Table
class Globalpandect extends Component {
    constructor(props) {
        super(props);
        this.newOpen = this.newOpen.bind(this);
        this.state = {
            isWarning:false,
            hover:false,
            isLoding:'ok',
            elements:defaultData.elments,
            keepValue:defaultData.keepValue,
                market:defaultData.market,
                html:[],
            unreadcount_Tradingalert:0,
            unreadcount_TradingalertTime:0,
            unreadcount_TradingalertTimeSum:0,
            appMenuListAll:[]

        }
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        return new Promise((resolve, reject) => {
            let list = this.state.elements
            let { keepValue, market, } = this.state
            const { keepWarning, valueToWarn, marketValue } = warningToKeep
            keepWarning.map((item, index) => {
                axios.post(DOMAIN_VIP + `${item.port}/overview`).then(res => {//频繁对倒
                    const result = res.data;
                    // console.log(1)
                    if (result.code == 0) {
                        list[index].num = result.data.unreadcount;
                        list[index].frequency = result.data.recentlytime;
                        list[index].sum = result.data.countsum;
                    } else {
                        message.warning(item.content + result.msg);
                    }
                })
            })
            // //保值异常预警
            valueToWarn.map((item, index) => {
                axios.post(DOMAIN_VIP + `${item.port}/overview`).then(res => {//频繁对倒
                    const result = res.data;
                    // console.log(2)
                    if (result.code == 0) {
                        keepValue[index].num = result.data.unreadcount;
                        keepValue[index].frequency = result.data.recentlytime;
                        keepValue[index].sum = result.data.countsum;
                    } else {
                        message.warning(item.content + result.msg);
                    }
                })
            })
            // //行情异常
            // marketValue.map((item, index) => {
            //     axios.post(DOMAIN_VIP + `${item.port}/overview`).then(res => {//频繁对倒
            //         const result = res.data;
            //         // console.log(3)
            //         if (result.code == 0) {
            //             market[index].num = result.data.unreadcount;
            //             market[index].frequency = result.data.recentlytime;
            //             market[index].sum = result.data.countsum;
            //         } else {
            //             message.warning(item.content + result.msg);
            //         }
            //     })
            // })

            axios.post(DOMAIN_VIP + '/coinQtBelowwarning/overview').then(res => {//资金低于预警值
                const result = res.data;
                if (result.code == 0) {
                    keepValue[2].num = result.data.unreadcount;
                    keepValue[2].frequency = result.data.recentlytime;
                    keepValue[2].sum = result.data.countsum;
                    this.forceUpdate()
                } else {
                    message.warning('资金低于预警值' + result.msg);
                }
            })
            axios.post(DOMAIN_VIP + '/coinQtHedgingnumbers/overview').then(res => {//保值下单数量异常
                const result = res.data;
                if (result.code == 0) {
                    keepValue[3].num = result.data.unreadcount;
                    keepValue[3].frequency = result.data.recentlytime;
                    keepValue[3].sum = result.data.countsum;
                    this.forceUpdate()
                } else {
                    message.warning('保值记录异常' + result.msg);
                }
            })
            //行情异常预警
            axios.post(DOMAIN_VIP + '/coinQtForfailure/overview ').then(res => {//外网行情获取失败预警
                const result = res.data;
                if (result.code == 0) {
                    market[0].num = result.data.unreadcount;
                    market[0].frequency = result.data.recentlytime;
                    market[0].sum = result.data.countsum;
                    this.forceUpdate()
                } else {
                    message.warning('外网行情获取失败预警' + result.msg);
                }
            })
            axios.post(DOMAIN_VIP + '/coinQtMarketdeparture/overview').then(res => {//行情偏离
                const result = res.data;
                if (result.code == 0) {
                    market[1].num = result.data.unreadcount;
                    market[1].frequency = result.data.recentlytime;
                    market[1].sum = result.data.countsum;
                    this.forceUpdate()
                } else {
                    message.warning('行情偏离' + result.msg);
                }
            })
            axios.post(DOMAIN_VIP + '/coinQtAmountlowwarning/overview').then(res => {//刷量账号资金低于预警
                const result = res.data;
                if (result.code == 0) {
                    market[2].num = result.data.unreadcount;
                    market[2].frequency = result.data.recentlytime;
                    market[2].sum = result.data.countsum;
                    this.forceUpdate()
                } else {
                    message.warning('刷量账号资金低于预警' + result.msg);
                }
            })
            axios.post(DOMAIN_VIP + '/coinQtDishlowwarning/overview').then(res => {//盘口深度低于预警值预警
                const result = res.data;
                if (result.code == 0) {
                    market[3].num = result.data.unreadcount;
                    market[3].frequency = result.data.recentlytime;
                    market[3].sum = result.data.countsum;
                    this.forceUpdate()
                } else {
                    message.warning('盘口深度低于预警值预警' + result.msg);
                }
            })
            axios.post(DOMAIN_VIP + '/coinQtStopwarning/overview').then(res => {//量化程序停止报警
                const result = res.data;
                if (result.code == 0) {
                    market[4].num = result.data.unreadcount;
                    market[4].frequency = result.data.recentlytime;
                    market[4].sum = result.data.countsum;
                    this.forceUpdate()
                } else {
                    message.warning('量化程序停止报警' + result.msg);
                }
            })
            axios.post(DOMAIN_VIP + '/coinQtAccounted/overview').then(res => {//用户成交占数量资金比
                const result = res.data;
                if (result.code == 0) {
                    market[5].num = result.data.unreadcount || 0;
                    market[5].frequency = result.data.recentlytime || null;
                    market[5].sum = result.data.countsum;
                    this.forceUpdate()
                    // info.num = 1;
                    // list[6].frequency = 2;
                    // list[6].sum =3;

                    // this.forceUpdate()
                    // console.log(list[0])
                    // this.setState({
                    //     elements:
                    // })
                    // resolve();

                    // console.log(keep)
                } else {
                    message.warning('用户成交占数量资金比' + result.msg);
                }
            })
            axios.post(DOMAIN_VIP + '/qttransrecord/overview').then(res => {//  交易预警
                const result = res.data;
                if (result.code == 0) {
                    this.setState({
                        unreadcount_Tradingalert: result.data.unreadcount,
                        unreadcount_TradingalertTime: result.data.recentlytime,
                        unreadcount_TradingalertTimeSum: result.data.countsum,
                    })
                    // resolve();
                } else {
                    message.warning('交易预警' + result.msg);
                }
            })
            resolve(list)
        }).then((list) => {
            this.setState({
                elements: list,

            })
        })

    }


    newOpen(orderNo) {
        axios.post(DOMAIN_VIP + '/qttransrecord/view', qs.stringify({//  交易预警
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                // this.requestTable()
            } else {
                message.warning(result.msg);
            }
        })
        let breakurl = {
            key: 700100010224,
            id: 16,
            name: "交易预警",
            url: "/riskmanagement/tradealertment/tradealertment"
        }


        this.props._this.add(breakurl)
        // this.props.changeSendNum(breakurl.name,orderNo)  
    }

    openChild(content) {
        const { appMenuListAll } = this.state;
        appMenuListAll.forEach((item)=>{
            if(item.url == content.url){
                console.log(content)
                this.props._this.add(item)
            }
        })
        // const title = content.id
        // jumpItem.map((item, index) => {
        //     if (item.id === title) {
        //         this.props._this.add(item)
        //         // this.props.changeSendNum(item.name)
        //     }
        // })
    }
    componentDidMount() {
        this.requestTable()
        this.setState({
            appActiveKey: this.props.appActiveKey,
            appMenuListAll:this.props.TmenuListAll
        })
    }
    componentWillReceiveProps(nextProps) {
        // console.log(this.state.appActiveKey, '++++++', nextProps.appActiveKey)
        if (nextProps.appActiveKey == this.state.appActiveKey) {
            this.requestTable()
        }
    }
    // shouldComponentUpdate(newprops,newstate){
    //     if(newprops == this.props && newstate== this.state ){
    //         return false
    //     }else{
    //         return true
    //     }

    // }

    render() {
        // const {time,pageSize,showHide,tableSource,pagination,googVisibal,check} = this.state
        const { keepValue, elements, market, isWarning, unreadcount_Tradingalert, unreadcount_TradingalertTime, unreadcount_TradingalertTimeSum, html } = this.state

        let sum = elements.map(item => item.sum).reduce((prev, curr) => prev + curr)//异常账户-今日预警总数
        let keepSum = keepValue.map(item => item.sum).reduce((prev, curr) => prev + curr)//保值异常-今日预警总数
        let marketSum = market.map(item => item.sum).reduce((prev, curr) => prev + curr)// 行情异常-今日预警总数


        let Dstyle = {
            Dheight: {
                minHeight: 300,
                borderWidth: 1,
                borderColor: '#E7E9ED',
                borderStyle: 'solid',
                backgroundColor: '#FFFFFF',
                marginBottom: 10,
                height: "auto",
                overflow: "hidden",
                _overflow: "visible",

                // boxSizing:"content-box"
            },
            Dheight2: {
                minHeight: 300,
                borderWidth: 1,
                borderColor: '#E7E9ED',
                borderStyle: 'solid',
                backgroundColor: '#FFFFFF',
                padding: 0,
                height: "auto",
                overflow: "hidden",
                _overflow: "visible",
            },


        }

        return (
            <div className='right-con'>
                {/*<div className="page-title">*/}
                    {/*当前位置：风控管理 > 币币交易业务 > 币币风控总览*/}
                {/*</div>*/}
                {/* <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i> */}
                <div className='clearfix'></div>
                <div className='row'>
                    <div className='col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6 '>
                        <div style={Dstyle.Dheight} style={{ padding: '0 10px',marginTop: '0px' }} className='right-con pandect-style downward '>
                            <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                                <div className='pandect_warning ' >
                                    <div className='warning_deal'>交易预警</div>
                                    <div className='warning_sum'>今日预警总数：
                                        <i className='warning-i'>{unreadcount_TradingalertTimeSum}</i>
                                    </div>
                                </div>
                                <div>
                                    <div className='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4 mouseOnFirst bourse-deal'
                                        onClick={this.newOpen}
                                    >
                                        <div className='warning_children_deal rowRight'>交易预警</div>
                                        <div className='warning_children_time rowRight'>最近发生：{unreadcount_Tradingalert === 0 && unreadcount_TradingalertTime == null ? '--' : moment(unreadcount_TradingalertTime).format(TIMEFORMAT_ss)}</div>
                                        <div className='warning_children_box rowRight'>
                                            <i className={unreadcount_Tradingalert > 0 ? 'warning-i' : 'isShow'}>{unreadcount_Tradingalert}</i>
                                            <span>笔</span>
                                        </div>
                                    </div>


                                    {this.getElementsFill(5)}
                                    {/* <div className ='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4 bourse-deal'
                                        style={{borderStyle:'solid',borderColor:'#2190D2',borderWidth:'1px',marginTop:'10px',borderRadius:'2px',opacity:0}}
                                       
                                        >
                                                <div className='warning_children_deal rowRight'>交易预警</div>
                                                <div className='warning_children_time rowRight'>最近发生：{unreadcount_Tradingalert === 0 && unreadcount_TradingalertTime == null? '--' : moment(unreadcount_TradingalertTime).format(TIMEFORMAT_ss)}</div>
                                                <div className='warning_children_box rowRight'>
                                                    <i className={unreadcount_Tradingalert > 0 ?'warning-i':'isShow'}>{unreadcount_Tradingalert}</i>
                                                    <span>笔</span>
                                                </div>
                                    </div> */}
                                    {/* <div className ='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4'
                                        style={{borderStyle:'solid',borderColor:'#2190D2',borderWidth:'1px',marginTop:'10px',borderRadius:'2px',opacity:0}}
                                        
                                        >
                                                <div className='warning_children_deal rowRight'>交易预警</div>
                                                <div className='warning_children_time rowRight'>最近发生：{unreadcount_Tradingalert === 0 && unreadcount_TradingalertTime == null? '--' : moment(unreadcount_TradingalertTime).format(TIMEFORMAT_ss)}</div>
                                                <div className='warning_children_box rowRight'>
                                                    <i className={unreadcount_Tradingalert > 0 ?'warning-i':'isShow'}>{unreadcount_Tradingalert}</i>
                                                    <span>笔</span>
                                                </div>
                                    </div>
                                    <div className ='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4'
                                        style={{borderStyle:'solid',borderColor:'#2190D2',borderWidth:'1px',marginTop:'10px',borderRadius:'2px',opacity:0}}
                                        
                                        >
                                                <div className='warning_children_deal rowRight'>交易预警</div>
                                                <div className='warning_children_time rowRight'>最近发生：{unreadcount_Tradingalert === 0 && unreadcount_TradingalertTime == null? '--' : moment(unreadcount_TradingalertTime).format(TIMEFORMAT_ss)}</div>
                                                <div className='warning_children_box rowRight'>
                                                    <i className={unreadcount_Tradingalert > 0 ?'warning-i':'isShow'}>{unreadcount_Tradingalert}</i>
                                                    <span>笔</span>
                                                </div>
                                    </div>
                                    <div className ='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4'
                                        style={{borderStyle:'solid',borderColor:'#2190D2',borderWidth:'1px',marginTop:'10px',borderRadius:'2px',opacity:0}}
                                        
                                        >
                                                <div className='warning_children_deal rowRight'>交易预警</div>
                                                <div className='warning_children_time rowRight'>最近发生：{unreadcount_Tradingalert === 0 && unreadcount_TradingalertTime == null? '--' : moment(unreadcount_TradingalertTime).format(TIMEFORMAT_ss)}</div>
                                                <div className='warning_children_box rowRight'>
                                                    <i className={unreadcount_Tradingalert > 0 ?'warning-i':'isShow'}>{unreadcount_Tradingalert}</i>
                                                    <span>笔</span>
                                                </div>
                                    </div> */}


                                </div>
                            </div>
                        </div>
                    </div>
                    <div className='col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6' style={Dstyle.Dheight} >
                        {/* <div className = 'right-con'> */}
                        <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                            <div className='pandect_warning '>
                                <div className='warning_deal'>异常账户预警</div>
                                <div className='warning_sum' style={{ marginLeft: '140px' }}>今日预警总数：
                                            <i className='warning-i'>{sum}</i>
                                </div>
                            </div>
                            {this.getElementsFirst(elements)}

                        </div>
                        {/* </div> */}
                    </div>
                    <div className='clearfix'></div>
                    <div className='col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6 ' >
                        <div className='right-con pandect-style downward' style={Dstyle.Dheight} style={{ padding: '0 10px', marginTop: '0px' }}>
                            <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                                <div>
                                    <div className='pandect_warning '>
                                        <div className='warning_deal'>保值异常预警</div>
                                        <div className='warning_sum' style={{ marginLeft: '140px' }}>今日预警总数：
                                                            <i className='warning-i'>{keepSum}</i>
                                        </div>
                                    </div>
                                    {this.getElementsSecond(keepValue)}
                                    <div className='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4'
                                        style={{ borderStyle: 'solid', borderColor: '#2190D2', borderWidth: '1px', marginTop: '10px', borderRadius: '2px', opacity: 0 }}

                                    >
                                        <div className='warning_children_deal rowRight'>交易预警</div>
                                        <div className='warning_children_time rowRight'>最近发生：{unreadcount_Tradingalert === 0 && unreadcount_TradingalertTime == null ? '--' : moment(unreadcount_TradingalertTime).format(TIMEFORMAT_ss)}</div>
                                        <div className='warning_children_box rowRight'>
                                            <i className={unreadcount_Tradingalert > 0 ? 'warning-i' : 'isShow'}>{unreadcount_Tradingalert}</i>
                                            <span>笔</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className='col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6 ' style={Dstyle.Dheight}>
                        {/* <div className = 'right-con'> */}
                        <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                            <div className='pandect_warning '>
                                <div className='warning_deal'>行情异常预警</div>
                                <div className='warning_sum' style={{ marginLeft: '140px' }}>今日预警总数：
                                        <i className='warning-i'>{marketSum}</i>
                                </div>
                            </div>
                            {this.getElementsThree(market)}
                        </div>
                    </div>
                    {/* </div> */}
                </div>
            </div>
        )
    }
    //循环获取dom
    getElementsFirst(elements) {
        return elements.map((item, index) => {
            let timeList = item.frequency
            let time = moment(timeList).format(TIMEFORMAT_ss)
            return (
                <div className='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4 mouseOnSecond'
                    style={{ marginTop: '10px', paddingLeft: 0, paddingRight: 5 }}
                    key={index}

                >
                    <div>
                        <div className='right-border' onClick={this.openChild.bind(this, item)} >
                            <div className='warning_children_deal rowRight'>{item.title}</div>
                            <div className='warning_children_time rowRight'>最近发生：{timeList == null && item.num == 0 ? '--' : time}</div>
                            <div className='warning_children_box rowRight'>
                                <i ref='isShow' className={item.num > 0 ? 'isWar' : "isShow"}>{item.num}</i>
                                <span>个</span>
                            </div>
                        </div>
                    </div>

                </div>
            )
        })
    }
    getElementsSecond(keepValue) {

        return keepValue.map((item, index) => {
            let timeList = item.frequency
            let time = moment(timeList).format(TIMEFORMAT_ss)
            return (
                <div className='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4 mouseThree'
                    style={{ marginTop: '10px', paddingLeft: 0, }}
                    key={index}

                >
                    <div className='right-border1' onClick={this.openChild.bind(this, item)} style={{}}>
                        <div className='warning_children_deal rowRight'>{item.title}</div>
                        <div className='warning_children_time rowRight'>最近发生：{timeList == null && item.num == 0 ? '--' : time}</div>
                        <div className='warning_children_box rowRight'>
                            <i ref='isShow' className={item.num > 0 ? 'isWar' : "isShow"}>{item.num}</i>
                            <span>个</span>
                        </div>
                    </div>
                </div>
            )
        })
    }
    getElementsThree(market) {
        return market.map((item, index) => {
            let timeList = item.frequency
            let time = moment(timeList).format(TIMEFORMAT_ss)
            return (
                <div className='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4 mouseThree'
                    style={{ marginTop: '10px', paddingLeft: 0, }}
                    key={index}
                >
                    <div className='right-border2 ' onClick={this.openChild.bind(this, item)}>
                        <div className='warning_children_deal rowRight'>{item.title}</div>
                        <div className='warning_children_time rowRight'>最近发生：{timeList == null && item.num == 0 ? '--' : time}</div>
                        <div className='warning_children_box rowRight'>
                            <i ref='isShow' className={item.num > 0 ? 'isWar' : "isShow"}>{item.num}</i>
                            <span>个</span>
                        </div>
                    </div>
                </div>
            )
        })
    }
    getElementsFill = num => {
        let html = []
        for(let i = 0;i < num ; i++){
            html.push(<div className ='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-4 bourse-deal'
            style={{opacity:0}}
           key={1113+i}
            >
                    <div className='warning_children_deal rowRight'>.</div>
                    <div className='warning_children_time rowRight'>.</div>
                    <div className='warning_children_box rowRight'>
                        <i>.</i>
                        <span>.</span>
                    </div>
        </div>)
        }
        return html
    }

}

export default Globalpandect