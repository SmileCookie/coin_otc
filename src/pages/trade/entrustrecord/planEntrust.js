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
import LimitEntrust from './limitEntrust'
import ScrollArea from 'react-scrollbar'
const BigNumber = require('big.js');
class PlanEntrust extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
          
        }

    }
    componentDidMount() {
      
    }
    componentWillUnmount() {
       
    }
    //取消委托弹窗
    moadlCancel(id,plantype){
        this.props.moadlCancel(id,plantype)
    }
    

    render() {
            let {user,plan} =  this.props;
            console.log(plan)
        return (
           [
                <div className="trade-content-flex-tit" key="limit-title"> 
                    <table>
                        <thead>
                            {/* <tr>
                                <th width="17%"><FormattedMessage id="日期" /></th>
                                <th width="5%"><FormattedMessage id="类型" /></th>
                                <th><FormattedMessage id="市场" /></th>
                                <th width="16%"><FormattedMessage id="触发价格" /></th>
                                <th width="16%"><FormattedMessage id="委托价格" /></th>
                                <th width="16%"><FormattedMessage id="委托数量" /></th>
                                <th width="16%"><FormattedMessage id="总额" /></th>
                                <th width="14%"><em style={{display:'none'}}>[<FormattedMessage id="CancelAll"/>]</em></th>
                            </tr> */}
                            <tr>
                                <th width="14%"><FormattedMessage id="日期" /></th>
                                <th width="7%"><FormattedMessage id="类型" /></th>
                                <th width="12%" style={{textAlign:'center'}}><FormattedMessage id="市场" /></th>
                                <th width="13%"><FormattedMessage id="触发价格" /></th>
                                <th width="13%"><FormattedMessage id="委托价格" /></th>
                                <th width="14%"><FormattedMessage id="委托数量" /></th>
                                <th width="14%"><FormattedMessage id="总额" /></th>
                                <th ><em style={{display:'none'}}>[<FormattedMessage id="CancelAll"/>]</em></th>
                            </tr>
                        </thead>
                    </table>
                </div>,
                <ScrollArea className="trade-scrollarea" key="limit-content">
                    <div className="table-box-cover" style={{minHeight:'100px'}}>
                        {
                            user?(
                                plan.length==0?(
                                    <div className="alert_under_table">
                                        <i className="iconfont icon-tongchang-tishi norecord"></i>
                                        <FormattedMessage id="no.plan.order"/>
                                    </div>
                                ):(
                                        <div className="table-box-cover" style={{minHeight:'100px'}}>
                                            <table>
                                                <tbody>
                                                {
                                                    plan.map((record, index) => {
                                                        return (
                                                            // <tr key={record.entrustId}>
                                                            //     <td width="17%">{record.dateString}</td>
                                                            //     <td width="5%" className="label">
                                                            //         {
                                                            //             record.tradeTypes==1?
                                                            //                 <span className="green"><FormattedMessage id="买入" /></span>:
                                                            //                 <span className="red"><FormattedMessage id="卖出" />
                                                            //                             </span>
                                                            //         }
                                                            //     </td>
                                                            //     <td>{record.fromMarket}</td>
                                                            //     <td width="16%">{!isNaN(record.triggerPrice)?record.triggerPrice:record.triggerPriceProfit}</td>
                                                            //     <td width="16%">{!isNaN(record.unitPrice)?record.unitPrice:record.unitPriceProfit}</td>
                                                            //     <td width="16%">{!isNaN(record.numbers)?record.numbers:record.stopAmount}</td>
                                                            //     <td width="16%">{record.planTotalMoney}</td>
                                                            //     <td width="14%">
                                                            //         <em onClick={() => this.moadlCancel(record.entrustId,record.plantype)}>
                                                            //             <FormattedMessage id="撤销" />
                                                            //         </em>
                                                            //     </td>
                                                            // </tr>
                                                            <tr key={record.entrustId}>
                                                                <td width="14%" >{record.dateString}</td>
                                                                <td width="7%" className="label">
                                                                    {
                                                                        record.tradeTypes==1?
                                                                            <span className="green"><FormattedMessage id="买入" /></span>:
                                                                            <span className="red"><FormattedMessage id="卖出" />
                                                                                        </span>
                                                                    }
                                                                </td>
                                                                <td width="12%">{record.fromMarket}</td>
                                                                <td width="13%">{!isNaN(record.triggerPrice)?record.triggerPrice:record.triggerPriceProfit}</td>
                                                                <td width="13%">{!isNaN(record.unitPrice)?record.unitPrice:record.unitPriceProfit}</td>
                                                                <td width="14%">{!isNaN(record.numbers)?record.numbers:record.stopAmount}</td>
                                                                <td width="14%">{record.planTotalMoney}</td>
                                                                <td>
                                                                    <em onClick={() => this.moadlCancel(record.entrustId,record.plantype)}>
                                                                        <FormattedMessage id="撤销" />
                                                                    </em>
                                                                </td>
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

export default PlanEntrust;