import React from 'react';
import {fetchIntegral } from '../../redux/modules/level'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../conf'
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import '../../assets/css/table.less';
import '../../assets/css/chargeList.less'
// import TradeCharge from './tradeCharge'
// import WithdrawalCharge from './withdrawalCharge'
import { withRouter,Link } from 'react-router'
// import Leve from './leve'
import Form from '../../decorator/form';
import axios from 'axios';
import qs from 'qs';
import { connect } from 'react-redux';
import { separator,formatDate} from '../../utils';
import thunk from '../../../node_modules/redux-thunk';
const BigNumber = require('big.js');

@Form
class Charge extends React.Component{
    constructor(props){
        super(props)

        this.state = {
           chooseId: 0
        }
        this.chooseIdEvent = this.chooseIdEvent.bind(this)
        this.ch = window.location.href;
    }
    componentDidMount(){
       
    }
    componentWillReceiveProps(){
        if(this.ch !== window.location.href){
            this.ch = window.location.href;
            //this.forceUpdate(); //刷新头部导航状态
        }
    }

    chooseIdEvent(chooseId){
       this.setState({
            chooseId
       })
    }
   
    render(){
       const {chooseId} = this.state;
       const { formatMessage } = this.intl;
       const ch = this.ch;
        return(
             <div className="mains">
                <div className="content">
                    <ul className="tabs clearfix">
                        <Link  to="/bw/chargeList/leve"><li  className={ch.includes("/leve") ? 'choose':''}  >{formatMessage({id: "账户等级"})}</li></Link>
                        <Link  to="/bw/chargeList/tradsCharge"><li  className={ch.includes("/tradsCharge") ? 'choose':''} >{formatMessage({id: "交易手续费"})}</li></Link>
                        <Link  to="/bw/chargeList/withdrawalCharge"><li  className={ch.includes("/withdrawalCharge") ? 'choose':''} >{formatMessage({id: "提现手续费"})}</li></Link>
                        {/* <li  className={chooseId == 0 ? 'choose':''} onClick={() => this.chooseIdEvent(0)} >{formatMessage({id: "账户等级"})}</li>
                        <li  className={chooseId == 1 ? 'choose':''} onClick={() => this.chooseIdEvent(1)}>{formatMessage({id: "交易手续费"})}</li>
                        <li  className={chooseId == 2 ? 'choose':''} onClick={() => this.chooseIdEvent(2)}>{formatMessage({id: "提现手续费"})}</li> */}
                    </ul>
                    <div className="tables">
                        {this.props.children}
                        {/* {
                            chooseId == 0? 
                                <Leve/>
                            : chooseId == 1? 
                                <TradeCharge/>
                            : 
                                <WithdrawalCharge/>
                        } */}
                       
                    </div>
                </div> 
             </div>
           
        )
    }
}
export default withRouter(Charge)