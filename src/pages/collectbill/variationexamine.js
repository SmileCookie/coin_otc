import React, { Component } from 'react'
import axios from '../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { rechargeUrls, platformUrls,walletUrls,paymentUrls} from './data/information'
import { jumpItem } from '../common/pageurl'
import { recharge, payment, platform, wallet } from './data/index'
import { message, } from 'antd'
import { DOMAIN_VIP, TIMEFORMAT_ss, } from '../../conf'
// import GoogleCode from '../../common/modal/googleCode'

class Globalpandect extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isWarning: false,
            hover: false,
            isLoding: 'ok',
            recharge: recharge,
            platform: platform,
            payment: payment,
            wallet: wallet,
            appActiveKey:'',
            appMenuListAll:[]
        }
    }
    //请求数据
    requestTable = () => {
        let { recharge, platform, wallet, payment } = this.state
         new Promise((resolve, reject) => {
            //充提异常账户
            rechargeUrls.map((item, index) => {
                if(item.port){
                    axios.post(DOMAIN_VIP + `${item.port}/overview`).then(res => {
                        const result = res.data;
                        // console.log(1)
                        if (result.code == 0) {
                            // recharge[index].num = result.data.countsum;
                            recharge[index].frequency = result.data.recentlytime;
                            recharge[index].sum = result.data.countsum || 0;
                            if(item.unreadcount){
                                recharge[index].unreadcount = result.data.unreadcount || 0;
                            }
                            this.setState({
                                recharge
                            })                                       
                        } else {
                            message.warning(item.content + result.msg);
                            console.error(item.content+item.port+'/overview')
                        }
                    })
                }else{
                    console.log(item.content)
                }
            })
            //资金异常
            platformUrls.map((item, index) => {
                if(item.port){
                    axios.post(DOMAIN_VIP + `${item.port}/overview`).then(res => {//
                        const result = res.data;
                        if (result.code == 0) {
                            
                            platform[index].frequency = result.data.recentlytime;
                            platform[index].sum = result.data.countsum;
                            if(item.content == '虚拟资金异常'){
                                platform[index].num = result.data.countsum;
                            }else{
                                platform[index].num = result.data.unreadcount || 0;
                            }
                            this.setState({
                                platform
                            })
                        } else {
                            message.warning(item.content + result.msg);
                            console.error(item.content+item.port+'/overview')
                        }
                    })
                }else{
                    console.log(item.content)
                }
            })
            // //钱包异常
            // walletUrls.map((item, index) => {
            //     if(item.port){

            //         axios.post(DOMAIN_VIP + `${item.port}/overview`).then(res => {
            //             const result = res.data;
            //             // console.log(3)
            //             if (result.code == 0) {
            //                 wallet[index].num = result.data.unreadcount;
            //                 wallet[index].frequency = result.data.recentlytime;
            //                 wallet[index].sum = result.data.countsum;
            //                 this.setState({
            //                     wallet
            //                 })
            //             } else {
            //                 message.warning(item.content + result.msg);
                                // console.error(item.content+item.port+'/overview')
            //             }
            //         })
            //     }else{
            //         console.log(item.content)
            //     }
            // })
            //支付中心对账异常
            paymentUrls.map((item, index) => {
                if(item.port){
                    axios.post(DOMAIN_VIP + `${item.port}/overview`).then(res => {
                        const result = res.data;
                        // console.log(3)
                        if (result.code == 0) {
                            payment[index].num = result.data.unreadcount ;
                            payment[index].frequency = result.data.recentlytime ;
                            payment[index].sum = result.data.unreadcount;
                            this.setState({
                                payment
                            })
                        } else {
                            message.warning(item.content + result.msg);
                            console.error(item.content+item.port+'/overview')
                        }
                    })
                }else{
                    console.log(item.content)
                }
            })
            // let obj = {
            //     recharge,
            //     platform,
            //     wallet,
            //     payment
            // }
            resolve()

        }).catch(err=>console.log(err))
        // .then((obj) => {
        //     console.log(this.state)
        //     const { recharge, platform, wallet,payment } = obj;
        //     this.setState({
        //         recharge,
        //         platform,
        //         wallet,
        //         payment
        //     },()=>{
                
        //     })
        // })
    }
    openChild(content) {
        const { appMenuListAll } = this.state;
        appMenuListAll.forEach((item)=>{
            if(item.url == content.url){
                console.log(item)
                console.log(content)
                this.props._this.add(item)
            }
        })
        
        // jumpItem.map((item, index) => {
        //     if (item.id === content.id) {
        //         if (item.url) {
        //             this.props._this.add(item)
        //         } else {
        //             return
        //         }
        //     }
        // })
    }
    //   componentWillMount(){

    //     //   this.getElementsFour()

    //     }     
    componentDidMount() {
        this.requestTable()
        // console.log(this.props)
        this.setState({
            appActiveKey:this.props.appActiveKey,
            appMenuListAll:this.props.TmenuListAll
        })
        // this.requestTable()
    }
    componentWillReceiveProps(nextProps) {
        // console.log(this.state.appActiveKey ,'++++++',nextProps.appActiveKey)
        if( nextProps.appActiveKey == this.state.appActiveKey ){
            this.requestTable()
        }
    }

    render() {
        // const {time,pageSize,showHide,tableSource,pagination,googVisibal,check} = this.state
        const { recharge, platform, payment, wallet, } = this.state
        let paymentSum = payment.map(item => item.sum).reduce((prev, curr) => prev + curr)//支付中心异常-今日预警总数
        let platformSum = platform.map(item => {
            // if(item.id==24){
            //     return item.sum
            // }
            return item.sum
        }).reduce((prev, curr) => prev + curr)//资金异常对账-今日预警总数
        let rechargeSum = recharge.map(item => item.sum).reduce((prev, curr) => prev + curr)// 充提异常账户-今日预警总数
        let walletSum = wallet.map(item => item.sum).reduce((prev, curr) => prev + curr)// 钱包异常-今日预警总数
        let Dstyle = {
            Dheight: {
                minHeight: 300,
                minWidth: 500,
                borderWidth: 1,
                borderColor: '#E7E9ED',
                borderStyle: 'solid',
                backgroundColor: '#FFFFFF',
                marginBottom: 10,
                height: "auto",
                overflow: "hidden",
                _overflow: "visible",
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
            <div className='right-con' style={{ minWidth: '1200px' }}>
                {/*<div className="page-title">*/}
                    {/*当前位置：风控管理 > 风控管理总览 > 总账户变动总览*/}
                {/*</div>*/}
                {/* <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i> */}
                <div className='clearfix'></div>
                <div className='row'>
                    <div className='col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6 ' style={{ minWidth: '500px' }}>
                        <div style={Dstyle.Dheight} style={{ padding: '0 10px', marginTop: '0px' }} className='right-con pandect-style downward '>
                            <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                                <div className='pandect_warning ' >
                                    <div className='warning_deal'>充提异常账户</div>
                                    <div className='warning_sum' style={{ marginLeft: '140px' }}>今日预警总数(实时)：
                                        <i className='warning-i'>{rechargeSum}</i>
                                    </div>
                                </div>
                                <div>
                                    {this.getElementsFour(recharge)}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className='col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6' style={Dstyle.Dheight} >
                        {/* <div className = 'right-con'> */}
                        <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                            <div className='pandect_warning '>
                                <div className='warning_deal'>资金异常</div>
                                <div className='warning_sum' style={{ marginLeft: '140px' }}>今日预警总数：
                                            <i className='warning-i'>{platformSum}</i>
                                </div>
                            </div>
                            {this.getElementsFirst(platform)}
                             {this.getElementsFill(2)}   
                        </div>
                        {/* </div> */}
                    </div>
                    <div className='clearfix'></div>
                    <div className='col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6 ' style={{ minWidth: '5 00px'}}>
                        <div className='right-con pandect-style downward' style={Dstyle.Dheight} style={{ padding: '0 10px', marginTop: '0px' }}>
                            <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                                <div>
                                    <div className='pandect_warning '>
                                        <div className='warning_deal'>支付中心对账异常</div>
                                        <div className='warning_sum' style={{ marginLeft: '190px' }}>今日预警总数(实时)：
                                            <i className='warning-i'>{paymentSum}</i>
                                        </div>
                                    </div>
                                    {this.getElementsSecond(payment)}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className='col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6 ' style={Dstyle.Dheight} style={{opacity:0}}>
                        <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                            <div className='pandect_warning '>
                                <div className='warning_deal'>钱包异常</div>
                                <div className='warning_sum' style={{ marginLeft:"190px" }}>今日预警总数：
                                    <i className='warning-i'>{walletSum}</i>
                                </div>
                            </div>
                            {/* {this.getElementsThree(wallet)} */}
                            {this.getElementsFill(4)}
                        </div>
                    </div>
                </div>
            </div>
        )
    }
    // 循环获取dom
    getElementsFirst = (elements) => {
        return elements.map((item, index) => {
            let timeList = item.frequency
            let time = moment(timeList).format(TIMEFORMAT_ss)
            return (
                <div className='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6 mouseOnSecond'
                    style={{ marginTop: '10px', paddingLeft: 0 }}
                    key={index}

                >
                    <div>
                        <div className='right-border' onClick={this.openChild.bind(this, item)} >
                            <div className='warning_children_deal rowRight'>{item.title}</div>
                            <div className='warning_children_time rowRight'>最近发生：{timeList == null  ? '--' : time}</div>
                            <div className='warning_children_box rowRight'>
                                {/* <i ref='isShow' className={item.title == '虚拟资金异常'?
                                    item.sum > 0 ? 'isWar' : "isShow"
                                :
                                    item.num > 0 ? 'isWar' : "isShow"}
                                >
                                {item.title == '虚拟资金异常' ? item.sum : item.num}</i> */}
                                <i ref='isShow' className={
                                    item.num > 0 ? 'isWar' : "isShow"}
                                >
                                {item.num}</i>
                                <span>个</span>
                            </div>
                        </div>
                    </div>

                </div>
            )
        })
    }
    getElementsSecond = (keepValue) => {

        return keepValue.map((item, index) => {
            let timeList = item.frequency
            let time = moment(timeList).format(TIMEFORMAT_ss)
            //  console.log(time)
            return (
                <div className='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6 mouseThree'
                    style={{ marginTop: '10px', paddingLeft: 0, }}
                    key={index}

                >
                    <div className='right-border2' onClick={this.openChild.bind(this, item)} style={{}}>
                        <div className='warning_children_deal rowRight'>{item.title}</div>
                        <div className='warning_children_time rowRight'>最近发生：{timeList ? time : '--'}</div>
                        <div className='warning_children_box rowRight'>
                            <i ref='isShow' className={item.num > 0 ? 'isWar' : "isShow"}>{item.num}</i>
                            <span>个</span>
                        </div>
                    </div>
                </div>
            )
        })
    }
    getElementsThree = (wallet) => {
        return wallet.map((item, index) => {
            let timeList = item.frequency
            let time = moment(timeList).format(TIMEFORMAT_ss)
            return (
                <div className='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6 mouseFour'
                    style={{ marginTop: '10px', paddingLeft: 0, }}
                    key={index}
                >
                    <div className='right-border1 ' onClick={this.openChild.bind(this, item)}  >
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
    getElementsFour = (recharge) => {
        return recharge.map((item, index) => {
            let timeList = item.frequency
            let time = moment(timeList).format(TIMEFORMAT_ss)
            return (
                <div className='col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6'
                    style={{ paddingLeft: 0, }}
                    key={item.id}
                >
                    <div className='bourse-deal mouseOnFirst' style={{ paddingLeft: '5px' }} onClick={this.openChild.bind(this, item)}>
                        <div className='warning_children_deal rowRight'>{item.title}</div>
                        <div className='warning_children_time rowRight'>最近发生：{timeList == null && item.sum == 0 ? '--' : time}</div>
                        <div className='warning_children_box rowRight'>
                            <i ref='isShow' className={
                                item.id == 39 || item.id == 34 || item.id == 36 ? 
                                    item.unreadcount > 0 ? 'isWar' : "isShow" 
                                : 
                                    item.sum > 0 ? 'isWar' : "isShow"}
                                >
                                {[39, 34, 36].includes(item.id) ? item.unreadcount : item.sum}
                            </i>
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