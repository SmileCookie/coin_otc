import React from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router';
import { DOMAIN_TRANS } from '../../../conf';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import { fetchEntrustRecord, cancelEntrust,batchCancelEntrust,cancelAllStop } from '../../../redux/modules/entrustrecord';
import {fetchOrderHistory} from '../../../redux/modules/orderhistory';
import {fetchOrderHistory24H} from '../../../redux/modules/orderhistory24H';
import { fetchAssetsDetail } from '../../../redux/modules/assets'
import ReactModal from '../../../components/popBox';
import {FETCH_ENTRUST_RECORD} from '../../../conf';
import {formatDate} from '../../../utils';
import SelectList from '../../../components/selectList'
import ScrollArea from 'react-scrollbar'
const BigNumber = require('big.js');
class HistoryEntrust extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            Mstr:"",
            pageSizeLimit:1,
            isloadingLimit:false,
            showLimit:true,
            pageSizeStop:1,
            isloadingStop:false,
            showStop:true,
            pageSize24H:1,
            isloading24H:false,
            show24H:true,
            pageSize:30,
            pageIndex:1,
            includeCancel:false,
            timeType:0,
            type:-1,
            hideTrade:false,
            tabIndex:0
        }
        this.typeOptions=[
            {
                val:-1,
                key:<FormattedMessage id="不限"/>
            },
            {
                val:1,
                key:<FormattedMessage id="买入"/>
            },
            {
                val:0,
                key:<FormattedMessage id="卖出"/>
            }
        ]
    }
    componentDidMount() {
       
    }
    componentWillUnmount() {
        
    }
    
    //历史委托ID详情弹窗
    moadlDetail(id,tradeTypes,numbers,completeNumber,averagePrice,completeTotalMoney){
       
       this.props.moadlDetail(id,tradeTypes,numbers,completeNumber,averagePrice,completeTotalMoney)
    }

    updateType(value){
      this.props.updateType(value);
    }

    setIncludeCancel(){
        this.setState({
            includeCancel:!this.state.includeCancel,
            pageIndex:1
        }, () =>{
            if(this.state.timeType==0){
                this.fetchOrderHistory24HInfo();
            }
            else if(this.state.timeType==1){
                this.fetchOrderHistoryInfo();
            }
        })
    }

    //时间选择
    setTimeType(value){
       //this.props.setTimeType(value)
       this.setState({
            timeType:value,
            pageIndex:1
        }, () =>{ 
            if(value==0){
                this.fetchOrderHistory24HInfo();
            }
            else if(value==1){
                this.fetchOrderHistoryInfo();
            }
        })
    }

    fetchOrderHistory24HInfo(){
        const { currentMarket } = this.props;
        let showCancel = this.state.includeCancel?1:0;
        this.props.fetchOrderHistory24H(currentMarket,showCancel,this.state.timeType,this.state.type,1,30,()=>{
            this.setState({
                isloading24H:false
            })
        }); 
    }

    fetchOrderHistoryInfo(){
        const { currentMarket } = this.props;
        let showCancel = this.state.includeCancel?1:0;
        this.props.fetchOrderHistory(currentMarket,showCancel,this.state.timeType,this.state.type,this.state.pageIndex,this.state.pageSize); 
    }

    render() {
         const {user,history,history24H} = this.props;
         const {includeCancel,timeType} = this.state;
        return (
            [
                <div className="select-conf-set" key="trade-title">
                        <label htmlFor="24H" className={timeType==0?'active':''} onClick={()=>{this.setTimeType(0)}}>
                            <div className="user-defined">
                                <span className="circle"></span>
                            </div>
                            <FormattedMessage id="24小时"/>
                        </label>
                        <label htmlFor="History" className={timeType==1?'active':''} onClick={()=>{this.setTimeType(1)}}>
                            <div className="user-defined">
                                <span className="circle"></span>
                            </div>
                            <FormattedMessage id="历史"/>
                        </label>
                        <SelectList
                            class="select-entrust"
                            Cb={ this.updateType.bind(this)}
                            options={this.typeOptions}
                        />
                        <div className="checkbox-includeCancel" onClick={()=>{this.setIncludeCancel()}}>
                            <div className={`checkbox-div checkbox-hidden ${!includeCancel?"bg-white":""}`}>
                                {!includeCancel&&<i className="iconfont icon-xuanze-yixuan"></i>}
                            </div>
                            <FormattedMessage id="隐藏已取消"/>
                        </div>
                </div>,
                <div className="table-box-cover-tit" key="trade-content">
                    <table>
                        <thead>
                            <tr>
                                <th width="15%"><FormattedMessage id="日期" /></th>
                                <th width="5%"><FormattedMessage id="类型" /></th>
                                <th width="12%"><FormattedMessage id="委托价格" /></th>
                                <th width="12%"><FormattedMessage id="委托数量" /></th>
                                <th width="12%"><FormattedMessage id="成交均价" /></th>
                                <th width="12%"><FormattedMessage id="已成交" /></th>
                                <th width="13%"><FormattedMessage id="总额" /></th>
                                <th width="11%"><FormattedMessage id="状态" /></th>
                                <th width="8%"><FormattedMessage id="操作" /></th>
                            </tr>
                        </thead>
                    </table>
                </div>,
                <ScrollArea className="trade-scrollarea">
                    <div className="table-box-cover" style={{minHeight:'100px'}}>
                            {
                                user?(
                                    // entrustrecord.history.isFetching?(
                                    //     <div>Loading...</div>
                                    // ):(
                                        this.state.timeType==1?(
                                            history.length==0?( 
                                                <div className="alert_under_table">
                                                    <i className="iconfont icon-tongchang-tishi norecord"></i>
                                                    <FormattedMessage id="no.order.record"/>
                                                </div>
                                            ):(
                                                    <div className="table-box-cover table-box-cover-sm">
                                                        <table>
                                                            <tbody>
                                                                {
                                                                    history.map((record, index) => {
                                                                        return (
                                                                            <tr key={record.entrustId}>
                                                                                <td width="15%">{record.date}</td>
                                                                                <td width="5%" className=" label">
                                                                                    {
                                                                                        record.tradeTypes==1?
                                                                                        <span className="green"><FormattedMessage id="买入" /></span>:
                                                                                        <span className="red"><FormattedMessage id="卖出" /></span>
                                                                                    }
                                                                                </td>
                                                                                <td width="12%">{record.price}</td>
                                                                                <td width="12%">{record.amount}</td>
                                                                                <td width="12%">{record.averagePrice}</td>
                                                                                <td width="12%">{record.completeNumber}</td>
                                                                                <td width="13%">{record.completeTotalMoney}</td>
                                                                                <td width="11%">{record.statusRes}</td>
                                                                                <td width="8%">{record.status==2?<em onClick={() => this.moadlDetail(record.entrustId,record.tradeTypes,record.amount,record.completeNumber,record.averagePrice,record.completeTotalMoney)}><FormattedMessage id="详细"/></em>:""}</td>
                                                                            </tr>
                                                                        )
                                                                    })
                                                                }
                                                                
                                                            </tbody>
                                                        </table>
                                                    </div>
                                              )
                                            )
                                            :
                                            (
                                                history24H.length==0?(
                                                    <div className="alert_under_table" style={{minHeight:'100px'}}>
                                                        <i className="iconfont icon-tongchang-tishi norecord"></i>
                                                        <FormattedMessage id="no.order.record"/>
                                                    </div>
                                                ):(
                                                        <div className="table-box-cover table-box-cover-sm">
                                                            <table>
                                                                <tbody>
                                                                    {
                                                                        history24H.map((record, index) => {
                                                                            return (
                                                                                <tr key={record.entrustId}>
                                                                                    <td width="15%">{record.date}</td>
                                                                                    <td width="5%" className="label">
                                                                                        {
                                                                                            record.tradeTypes==1?
                                                                                            <span className="green"><FormattedMessage id="买入" /></span>:
                                                                                            <span className="red"><FormattedMessage id="卖出" /></span>
                                                                                        }
                                                                                    </td>
                                                                                    <td width="12%">{record.price}</td>
                                                                                    <td width="12%">{record.amount}</td>
                                                                                    <td width="12%">{record.averagePrice}</td>
                                                                                    <td width="12%">{record.completeNumber}</td>
                                                                                    <td width="13%">{record.completeTotalMoney}</td>
                                                                                    <td width="11%">{record.statusRes}</td>
                                                                                    <td width="8%">{record.status==2?<em onClick={this.moadlDetail.bind(this,record.entrustId,record.tradeTypes,record.amount,record.completeNumber,record.averagePrice,record.completeTotalMoney)}><FormattedMessage id="详细"/></em>:""}</td>
                                                                                </tr>
                                                                            )
                                                                        })
                                                                    }
                                                                    
                                                                </tbody>
                                                            </table>
                                                        </div>

                                                )
                                            )
                                        
                                    // )
                                ):(
                                    <div className="alert_under_table" style={{minHeight:'100px'}}>
                                        <i className="iconfont icon-tongchang-tishi norecord"></i> 
                                        &nbsp;
                                        <FormattedMessage id="haveNotSign"/>  <Link to='/bw/login'><FormattedMessage id="Login" /></Link>  <FormattedMessage id="or"/>  <Link href='/bw/signup'><FormattedMessage id="OpenAccount" /></Link> <FormattedMessage id="andTryAgain" />
                                    </div>
                                )
                            }
                    </div>
                </ScrollArea>

            ]

        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        user: state.session.user,
        currentMarket: state.marketinfo.currentMarket,
        entrustrecord: state.entrustrecord,
        orderhistory:state.orderHistpry,
        orderhistory24H:state.orderhistory24H,
        marketsConf: state.marketsConf.marketsConfData
    };
}

const mapDispatchToProps = (dispatch) => {
    return {
        fetchEntrustRecord: (market) => {
            let lastTime = +new Date;
            dispatch(fetchEntrustRecord(market,1, 3, lastTime, 30, 1));
            dispatch(fetchEntrustRecord(market,2, -1, lastTime, 30, 1));
            // dispatch(fetchEntrustRecord(market,1, 2, lastTime, 0, 1));
        },
        fetchLimitRecord: (market,pageSize,fun) => {
            let lastTime = +new Date;
            dispatch(fetchEntrustRecord(market,1, 3, lastTime, pageSize, 1,fun));
        },
        fetchStopRecord: (market,pageSize,fun) => {
            let lastTime = +new Date;
            dispatch(fetchEntrustRecord(market,2, -1, lastTime, pageSize, 1,fun));
        },
        cancelEntrust:(market,entrustid,plantype,fun) => {
            console.log(entrustid)
            dispatch(cancelEntrust(market,entrustid,plantype))
                .then(fun)
        },
        batchCancelEntrust:(market,plantype,types,minPrice,maxPrice,fun)=>{
            dispatch(batchCancelEntrust(market,plantype,types,minPrice,maxPrice))
                .then(fun)
        },
        fetchOrderHistory:(market,includeCancel,timeType,type,pageNum,pageSize)=>{
            let lastTime = +new Date;
            dispatch(fetchOrderHistory(market,includeCancel,timeType,type,pageNum,pageSize,lastTime))
        },
        fetchOrderHistory24H:(market,includeCancel,timeType,type,pageNum,pageSize,fun)=>{
            let lastTime = +new Date;
            dispatch(fetchOrderHistory24H(market,includeCancel,timeType,type,pageNum,pageSize,lastTime,fun))
        },
        cancelAllStop:(market,fun)=>{
            dispatch(cancelAllStop(market)).then(fun)
        },
        fetchAssetsDetail:() => {
            dispatch(fetchAssetsDetail())
        },
        notifSend:(inf,kind)=>{
            dispatch(notifClear());
            dispatch(notifSend({
                    message: inf,
                    kind: kind||'info',
                    dismissAfter: DISMISS_TIME
                })
            );
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(HistoryEntrust));