import React from 'react';
import axios from 'axios';
import { Link } from 'react-router';
import { DOMAIN_TRANS } from '../../../conf';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import ReactModal from '../../../components/popBox';
import {FETCH_ENTRUST_RECORD} from '../../../conf';
import {formatDate} from '../../../utils';
import SelectList from '../../../components/selectList'
import ScrollArea from 'react-scrollbar'
const BigNumber = require('big.js');
class LimitEntrust extends React.Component {
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
        this.scrollarea = React.createRef()
    }
    componentDidMount() {
       const {user} = this.props;
    }
    
    //取消委托弹窗
    moadlCancel(id,plantype){
        this.props.moadlCancel(id,plantype)
    }

    render() {
        let {limit,user} = this.props;
        return (  [
                <div className="trade-content-flex-tit" key="limit-title">
                    <table>
                        <thead>
                            <tr>
                                {/* <th width="15%"><FormattedMessage id="日期" /></th>
                                <th width="5%"><FormattedMessage id="类型" /></th>
                                <th width="11%"><FormattedMessage id="市场" /></th>
                                <th width="14%"><FormattedMessage id="委托价格" /></th>
                                <th width="14%"><FormattedMessage id="委托数量" /></th>
                                <th width="10%"><FormattedMessage id="总额" /></th>
                                <th width="14%"><FormattedMessage id="成交均价" /></th>
                                <th width="14%"><FormattedMessage id="已成交" /></th>
                                <th width="6%"></th> */}
                                <th width="14%"><FormattedMessage id="日期" /></th>
                                <th width="7%"><FormattedMessage id="类型" /></th>
                                <th width="12%" style={{textAlign:'center'}}><FormattedMessage id="市场" /></th>
                                <th width="12%"><FormattedMessage id="委托价格" /></th>
                                <th width="10%"><FormattedMessage id="委托数量" /></th>
                                <th width="12%"><FormattedMessage id="总额" /></th>
                                <th width="13%"><FormattedMessage id="成交均价" /></th>
                                <th width="11%"><FormattedMessage id="已成交" /></th>
                                <th width="9%"></th>
                            </tr>
                        </thead>
                    </table>
                </div>,
                <ScrollArea className="trade-scrollarea" key="limit-content" ref={this.scrollarea}>
                    <div className="table-box-cover" style={{minHeight:'100px'}}>
                {
                    user?(
                        limit.length==0?(
                            <div className="alert_under_table"> 
                                <i className="iconfont icon-tongchang-tishi norecord"></i>
                                <FormattedMessage id="no.limit.order"/>
                            </div>
                        ):(
                                <div className="table-box-cover" >
                                    <table>
                                        <tbody>
                                            {
                                                limit.map((record, index) => {
                                                    return (
                                                        // <tr key={record.entrustId}>
                                                        //     <td width="15%">{record.dateString}</td>
                                                        //     <td width="5%" className="label">
                                                        //         {   
                                                        //             record.tradeTypes==1?
                                                        //             <span className="green"><FormattedMessage id="买入" /></span>:
                                                        //             <span className="red"><FormattedMessage id="卖出" /></span>
                                                        //         }
                                                        //     </td>
                                                        //     <td width="11%">{record.fromMarket}</td>
                                                        //     <td width="14%">{record.unitPrice}</td>
                                                        //     <td width="14%">{record.numbers}</td>
                                                        //     <td width="14%">{record.completeTotalMoney}</td>
                                                        //     <td width="10%">{record.averagePrice}</td>
                                                        //     <td width="14%">{record.completeNumber}</td>
                                                        //     <td width="15%"><em onClick={()=>this.moadlCancel(record.entrustId,record.plantype)}><FormattedMessage id="撤销" /></em></td>
                                                        // </tr>
                                                         <tr key={record.entrustId}>
                                                            <td width="14%" >{record.dateString}</td>
                                                            <td width="7%" className="label">
                                                                {   
                                                                    record.tradeTypes==1?
                                                                    <span className="green"><FormattedMessage id="买入" /></span>:
                                                                    <span className="red"><FormattedMessage id="卖出" /></span>
                                                                }
                                                            </td>
                                                            <td width="12%" >{record.fromMarket}</td>
                                                            <td width="12%">{record.unitPrice}</td>
                                                            <td width="10%">{record.numbers}</td>
                                                            <td width="12%">{record.completeTotalMoney}</td>
                                                            <td width="13%">{record.averagePrice}</td>
                                                            <td width="11%">{record.completeNumber}</td>
                                                            <td width="9%"><em onClick={()=>this.moadlCancel(record.entrustId,record.plantype)}><FormattedMessage id="撤销" /></em></td>
                                                        </tr>
                                                    )
                                                })
                                            }
                                        </tbody>
                                    </table>
                                </div>
                        )
                    ):(
                        <div className="alert_under_table">
                            <i className="iconfont icon-tongchang-tishi norecord"></i> 
                            &nbsp;<FormattedMessage id="haveNotSign"/>  <Link to='/bw/login'><FormattedMessage id="Login" /></Link>  <FormattedMessage id="or"/>  <Link to='/bw/signup'><FormattedMessage id="OpenAccount" /></Link> <FormattedMessage id="andTryAgain" />
                        </div>
                    )
                }
                    
                </div>
                </ScrollArea>
            ]  
        )
    }
}

export default LimitEntrust;