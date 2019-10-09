import React, { Component } from 'react'
import qs from 'qs'
import moment from 'moment';
import axios from 'axios'
import { TIMEFORMAT_ss,DOMAIN_VIP } from '../../../conf'
import {message} from 'antd'
import { personalBlow,platformBlow} from './data/index'
import { personalBlowUrls,platformBlowUrls} from './data/information'
import { jumpItem } from '../../common/pageurl'

const titles = ['','频繁对倒账户熔断','频繁挂撤单账户熔断','关联账户熔断','人工熔断']
export default class PlatformMeltingOverview extends Component {
    constructor(props) {
        super(props)
        this.state = {
            personalBlow,//平台熔断
            platformBlow,//个人熔断
            platformAll:0,
            platformList:[],
            appActiveKey:'',
        }

    }
    componentDidMount(){       
        this.requestTable()
        this.setState({
            appActiveKey: this.props.appActiveKey
        })
    }
    componentWillReceiveProps(nextProps) {
        if (nextProps.appActiveKey == this.state.appActiveKey) {
            this.requestTable()
        }
    }
    openChild = content => {        
        this.props._this.add({
            name:"个人熔断",
            url:"/riskmanagement/fusemod/personalBlow"
        })
             
    }
    requestTable = () => {
        // let platformList = []
        axios.get(DOMAIN_VIP + '/circuitCount/count').then((res) =>{
            const result = res.data;
            if(result.code == 0){
                let _allNum = 0
                result.data.map((item,index) =>{
                    _allNum = _allNum + Number(item.allStop) + Number(item.downloadStop) +  Number(item.entrustStop)
                })
                let _data = result.data.sort((a,b) =>{
                    if(a.fuseType > b.fuseType){
                        return 1
                    }
                    if(a.fuseType < b.fuseType){
                        return -1
                    }else{
                        return 0
                    }

                })
                this.setState({
                     platformList:_data,
                     platformAll:_allNum
                })
            }
        })
    }
    //循环获取dom
    getElementsFirst = (elements,borderNum = '') => {
        return elements.map((item, index) => {
            let _type = item.fuseType;
            let _num = item.allStop + item.downloadStop + item.entrustStop
            return (
                <div className='col-mg-2 col-lg-3 col-md-3 col-sm-2 col-xs-4 mouseOnSecond'
                    style={{ marginTop: '10px', paddingLeft: 0, paddingRight: 5 }}
                    key={index}
                >  
                    <div>
                        <div className={`right-border${borderNum}`} onClick={()=>this.openChild(item)}>
                            <div className='warning_children_deal rowRight'>{titles[_type]}</div>
                            {/* <div className='warning_children_time rowRight'>最近发生：{item.frequency?moment(item.frequency).format(TIMEFORMAT_ss):'--'}</div> */}
                            <div className='warning_children_time rowRight'>最近发生：实时</div>
                            <div className='warning_children_box rowRight'>
                                <i className={_num > 0 ? 'isWar' : "isShow"}>{_num}</i>
                                <span>个</span>
                            </div>
                            <div className='warning_children_box rowRight'>
                                <span>全部暂停账户：</span>
                                <i className={item.allStop > 0 ? 'isWar font14' : "isShow font14"}>{item.allStop}</i>
                                <span>个</span>
                            </div>
                            <div className='warning_children_box rowRight'>
                                <span>提现暂停账户：</span>
                                <i className={item.downloadStop > 0 ? 'isWar font14' : "isShow font14"}>{item.downloadStop}</i>
                                <span>个</span>
                            </div>
                            <div className='warning_children_box rowRight'>
                                <span>交易暂停账户：</span>
                                <i className={item.entrustStop > 0 ? 'isWar font14' : "isShow font14"}>{item.entrustStop}</i>
                                <span>个</span>
                            </div>

                        </div>
                    </div>

                </div>
            )
        })
    }
    //计算预警总数
    computeSum = list => {
        return list.map(item => item.sum).reduce((prev,curr) => prev+curr)
    }
    render() {
        // let sum = personalBlow.map(item => item.sum).reduce((prev,curr) => prev+curr)//异常账户-今日预警总数
        // let keepSum = keepValue.map(item => item.sum).reduce((prev,curr) => prev+curr)//保值异常-今日预警总数
        const {personalBlow,platformBlow,platformList,platformAll} = this.state
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
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 风控管理 > 风控管理总览 > 平台熔断总览
                </div>
                <div className='clearfix'></div>
                <div className='row'>
                    <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12' style={Dstyle.Dheight} >
                        <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                            <div className='pandect_warning '>
                                <div className='warning_deal'>个人熔断</div>
                                <div className='warning_sum' style={{ marginLeft: '140px' }}>今日预警总数：
                                            <i className='warning-i'>{platformAll}</i>
                                </div>
                            </div>
                            {this.getElementsFirst(platformList)}
                        </div>
                    </div>
                    {/* <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12' style={Dstyle.Dheight2} >
                        <div className='col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 edage'>
                            <div className='pandect_warning '>
                                <div className='warning_deal'>平台熔断</div>
                                <div className='warning_sum' style={{ marginLeft: '140px' }}>今日预警总数：
                                            <i className='warning-i'>{this.computeSum(platformBlow)}</i>
                                </div>
                            </div>
                            {this.getElementsFirst(platformBlow,2)}
                        </div>
                    </div> */}
                </div>
            </div>
        )
    }
}