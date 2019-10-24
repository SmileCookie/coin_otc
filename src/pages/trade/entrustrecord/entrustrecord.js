import React from 'react';
import axios from 'axios';
import { Link } from 'react-router';
import { DOMAIN_TRANS,ERRORCONFIG} from '../../../conf';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import ReactModal from '../../../components/popBox';
import {FETCH_ENTRUST_RECORD} from '../../../conf';
import {formatDate, optPop, trade_pop,TradeFormatDate} from '../../../utils';
import SelectList from '../../../components/selectList'
import LimitEntrust from './limitEntrust'
import PlanEntrust from './planEntrust'
import HistoryEntrust from './historyEntrust'
import ScrollArea from 'react-scrollbar'
import {reqPic} from '../../../redux/modules/position';
import {connect} from 'react-redux';
const BigNumber = require('big.js');
class Entrustrecord extends React.Component {
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
            tabIndex:0,
            dataError:false
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
        this.tabUrl = ['/bw/entrust/current?type=0','/bw/entrust/current?type=1','/bw/entrust/list']

        this.changeHideTrade = this.changeHideTrade.bind(this)
        this.changeTabIndex = this.changeTabIndex.bind(this)
    }
    componentDidMount() {
        const {user} = this.props;
        if(user){
            this.interval = setInterval(()=>{
                const { currentMarket } = this.props;
                this.props.fetchEntrustRecord(currentMarket);
                if(this.state.timeType==0){
                    this.fetchOrderHistory24HInfo();
                }
            },FETCH_ENTRUST_RECORD)
        }
    }
    componentWillUnmount() {
        clearInterval(this.interval)
    }
    componentWillReceiveProps(nextPops){
        if(nextPops.currentMarket!=this.props.currentMarket){
            this.setState({
                includeCancel:false,
                timeType:0,
                type:-1,
            })
        }
    }

    formatFundsDetail(result, flg){
        const {marketsConf,currentMarket} = this.props;
        let Lister = [];
        let i = 0;
        let coinNameArry,coinName,markerName;
        if(currentMarket){
            coinNameArry = currentMarket.split("_");
            coinName = coinNameArry[0].toUpperCase();
            markerName = coinNameArry[1].toUpperCase();
            let exchangeBixDian = 0;
            try{
                exchangeBixDian = marketsConf[currentMarket].exchangeBixDian
            } catch(e){

            }
            let numberBixDian = marketsConf[currentMarket].numberBixDian;
            BigNumber.RM = 0;
            for (let key in result) {
                Lister[i] = {};
                Lister[i].entrustId = result[key][0];
                let completeTotalMoney = result[key][4];
                let numbers = result[key][2];
                let completeNumber = result[key][3];
                let unitPrice = result[key][1];
                let triggerPrice = 0;
                if (result[key].length >= 13) {
                    triggerPrice = result[key][9];
                }
                let triggerPriceProfit = 0;
                if (result[key].length >= 13) {
                    triggerPriceProfit = result[key][11];
                }
                let unitPriceProfit = 0;
                if (result[key].length >= 13) {
                    unitPriceProfit = result[key][12];
                }
                let totalMoney = 0;
                if (result[key].length >= 14) {
                    totalMoney = result[key][13];
                }
                let formalEntrustId = "";
                if (result[key].length >= 15) {
                    formalEntrustId = result[key][14];
                }
                let plantype = "false";
                if (result[key].length >= 16) {
                    plantype = result[key][15];
                }
                Lister[i].fromMarket = result[key][(result[key].length - 1)].replace('_','/').toUpperCase();
                Lister[i].tradeTypes = result[key][5];
                Lister[i].unitPrice = unitPrice==0?"--":new BigNumber(unitPrice).toFixed(exchangeBixDian);
                Lister[i].averagePrice = completeNumber > 0 ? new BigNumber(completeTotalMoney).div(completeNumber).toFixed(exchangeBixDian) : "--";
                Lister[i].numbers = numbers==0?'--':new BigNumber(numbers).toFixed(numberBixDian);
                Lister[i].completeNumber = new BigNumber(completeNumber).toFixed(numberBixDian);
                Lister[i].completeTotalMoney = completeTotalMoney > 0 ? new BigNumber(completeTotalMoney).toFixed(exchangeBixDian):'--';
                Lister[i].submitTime = <FormattedTime value = {new Date(result[key][6])} />;
                Lister[i].submitDate = <FormattedDate value = {new Date(result[key][6])} />;
                Lister[i].dateString = TradeFormatDate(new Date(result[key][6]));
                Lister[i].triggerPrice = triggerPrice==0?"--":new BigNumber(triggerPrice).toFixed(exchangeBixDian);
                Lister[i].triggerPriceProfit = new BigNumber(triggerPriceProfit).toFixed(exchangeBixDian);
                Lister[i].unitPriceProfit = new BigNumber(unitPriceProfit).toFixed(exchangeBixDian);
                Lister[i].totalMoney = new BigNumber(totalMoney).toFixed(exchangeBixDian);
                if (unitPrice == 0) {
                    Lister[i].stopAmount = numbers == 0 ? new BigNumber(totalMoney).div(unitPriceProfit).toFixed(numberBixDian) : new BigNumber(numbers).toFixed(numberBixDian);
                } else {
                    Lister[i].stopAmount = numbers == 0 ? new BigNumber(totalMoney).div(unitPrice).toFixed(numberBixDian) : new BigNumber(numbers).toFixed(numberBixDian);
                }
                Lister[i].formalEntrustId = formalEntrustId;
                // Lister[i].numbers = new BigNumber(result[key][1]).toFixed(exchangeBixDian);

                // Lister[i].unitPrice = new BigNumber(result[key][1]).toFixed(exchangeBixDian);
                // Lister[i].tradeTypes = result[key][5];

                Lister[i].plantype = plantype;
                if(totalMoney!=0){
                    Lister[i].planTotalMoney=new BigNumber(totalMoney).toFixed(exchangeBixDian);
                }else{
                    if(!isNaN(Lister[i].unitPrice) && !isNaN(Lister[i].numbers)){
                        Lister[i].planTotalMoney=new BigNumber(Lister[i].unitPrice).times(Lister[i].numbers).toFixed(exchangeBixDian);
                    }
                    if(isNaN(Lister[i].unitPrice) && !isNaN(Lister[i].numbers)){
                        Lister[i].planTotalMoney=new BigNumber(Lister[i].unitPriceProfit).times(Lister[i].numbers).toFixed(exchangeBixDian);
                    } 
                    if(!isNaN(Lister[i].unitPrice) && isNaN(Lister[i].numbers)){
                        Lister[i].planTotalMoney=new BigNumber(Lister[i].unitPrice).times(Lister[i].stopAmount).toFixed(exchangeBixDian);
                    }
                    if(isNaN(Lister[i].unitPrice) && isNaN(Lister[i].numbers)){
                        Lister[i].planTotalMoney=new BigNumber(Lister[i].unitPriceProfit).times(Lister[i].stopAmount).toFixed(exchangeBixDian);
                    }
                }
                
                i++
            }
        }
        if(flg){
            const setPic = Lister.map(item=>item.unitPrice);
            this.props.setPk(setPic);
        }

        return Lister 
    }
    handlerCancelEntrust(entrustid,plantype) {
        const { currentMarket } = this.props;
        this.props.cancelEntrust(currentMarket,entrustid,plantype,
            res => {
                let result = res.data;
                if (result.datas == 200) {
                    this.modal.closeModal();
                    this.props.fetchEntrustRecord(currentMarket)
                    // this.props.notifSend(result.des);
                    //optPop(()=>{}, result.des);
                    trade_pop({
                        msg: result.des,
                    })
                }else{
                    //optPop(()=>{}, result.des, undefined, true);
                    trade_pop({
                        msg: result.des,
                        style: 1,
                    })
                    //this.props.notifSend(result.des,"warning");
                }
            }
        );
    }

    //Mstr
    changeStateMstr(str){
        this.setState({Mstr:str})
    }
    
    //取消委托弹窗
    moadlCancel(id,plantype){
        let str = <div className="modal-btn">
                <p><FormattedMessage id="确定取消当前委托？" /></p>
                <div className="modal-foot">
                    <a className="btn ml10" onClick={() => this.modal.closeModal()}><FormattedMessage id="cancel" /></a>
                    <a className="btn ml10" onClick={() => this.handlerCancelEntrust(id,plantype)}><FormattedMessage id="sure" /></a>
                </div>
            </div>;
         this.changeStateMstr(str)
         this.modal.openModal();
    }
    //取消所有限价委托弹窗
    moadlCancelAllStop(){
        let str = <div className="modal-btn">
                <p><FormattedMessage id="sureCancelAllOrders"/></p>
                <div className="modal-foot">
                    <a className="btn ml10" onClick={() => this.modal.closeModal()}><FormattedMessage id="cancel" /></a>
                    <a className="btn ml10" onClick={() => this.handlerCancelAllStop()}><FormattedMessage id="sure" /></a>
                </div>
            </div>;
        this.changeStateMstr(str)
        this.modal.openModal();
    }
    handlerCancelAllStop(){
        const { currentMarket } = this.props;
        this.props.cancelAllStop(currentMarket,
            res => {
                let result = res.data;
                if (result.isSuc) {
                    this.modal.closeModal();
                    this.props.fetchEntrustRecord(currentMarket);
                    //optPop(()=>{}, props.intl.formatMessage({id:"cancelsucces"}));
                    trade_pop({
                        msg: props.intl.formatMessage({id:"cancelsucces"}),
                    })
                } else {
                    //optPop(()=>{}, result.des);
                    trade_pop({
                        msg: result.des,
                    })
                    // this.props.notifSend(result.des,"warning");
                }
            }
        );
    }
    //历史委托ID详情
    formatMoadlDetail(result){
        const {marketsConf,currentMarket} = this.props;
        let Lister = [];
        let i = 0;
        if(currentMarket){
            let exchangeBixDian = 0;
            try{
                exchangeBixDian = marketsConf[currentMarket].exchangeBixDian
            } catch(e){
                
            }
            let numberBixDian = marketsConf[currentMarket].numberBixDian;
            BigNumber.RM = 0;
            for (let key in result) {
                let numberArry = new BigNumber(result[key][3]).toFixed(numberBixDian).toString().split(".");
                Lister[i] = {};
                Lister[i].id = result[key][0];
                Lister[i].numberA = numberArry[0];
                Lister[i].numberB = numberArry[1];
                Lister[i].submitTime = <FormattedTime value = {new Date(result[key][5])} />;
                Lister[i].submitDate = <FormattedDate value = {new Date(result[key][5])} />;
                Lister[i].dateString = formatDate(new Date(result[key][5]));
                Lister[i].price = new BigNumber(result[key][1]).toFixed(exchangeBixDian);
                Lister[i].total = new BigNumber(result[key][2]).toFixed(exchangeBixDian);
                i++
            }
        }
        return Lister;
    }

    //历史委托ID详情弹窗
    moadlDetail(id,tradeTypes,numbers,completeNumber,averagePrice,completeTotalMoney){
        const { currentMarket } = this.props;
        let coinNameArry,coinName,markerName;
        if(currentMarket){
            coinNameArry = currentMarket.split("_");
            coinName = coinNameArry[0].toUpperCase();
            markerName = coinNameArry[1].toUpperCase();
        }
        axios.get(DOMAIN_TRANS + "/Record/GetDetails-" + currentMarket + "-" + id + "?jsoncallback=",{withCredentials:true})
        .then(res => {
            let data = eval(res['data'])[0];
            let record = this.formatMoadlDetail(data.record);
            let str =   <div className="bk-moadlDetail  bk-moadlDetail-big">
                            <div className="head ">
                                <h3><FormattedMessage id="委托详情" /></h3>
                                {tradeTypes==1?
                                    <em className="bk-entrust-info-type bk-entrust-info-type-buy"><FormattedMessage id="买入" /></em>
                                    :
                                    <em className="bk-entrust-info-type bk-entrust-info-type-sell"><FormattedMessage id="卖出" /></em>
                                }
                                <a className="right iconfont icon-guanbi-moren" href="javascript:void(0);" onClick={() => this.modal.closeModal()}></a>
                            </div>
                            <div className="bk-entrust">
                                <div className="bk-entrust-info">
                                    <ul className="bk-entrust-info-head">
                                        <li>
                                            <FormattedMessage id="成交均价" />:
                                            <font>{averagePrice} {markerName}</font>
                                        </li>
                                        <li>
                                            <FormattedMessage id="委托数量/成交数量" />:
                                            <font>{numbers}/{completeNumber} {coinName}</font>
                                        </li>
                                        <li>
                                            <FormattedMessage id="成交额" />:
                                            <font>{completeTotalMoney.replace(/[A-Z]/g,"")} {markerName}</font>
                                        </li>
                                    </ul>
                                </div>
                                <div className="tradeList">
                                    <table >
                                        <thead>
                                            <tr>
                                                <th><FormattedMessage id="成交时间" /></th>
                                                <th><FormattedMessage id="成交数量" />({coinName})</th>
                                                <th><FormattedMessage id="成交价格" />({markerName})</th>
                                                <th><FormattedMessage id="成交额" />({markerName})</th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <div className="table-body-box">
                                    <ScrollArea className="trade-scrollarea">
                                        <table>
                                            <tbody>
                                                {
                                                    record.map((record, index)=>{
                                                        return(
                                                            <tr key={record.id}>
                                                                <td>{record.dateString}</td>
                                                                <td>{record.numberA}{record.numberB !== undefined ? '.'+record.numberB : ''}</td>
                                                                <td>{record.price}</td>
                                                                <td>{record.total}</td>
                                                            </tr>
                                                        )
                                                    })
                                                }
                                            </tbody>
                                        </table>
                                    </ScrollArea>
                                    </div>
                                </div>
                            </div>
                            <div className="modal-foot">
                                <a className="btn ml10" onClick={() => this.modal.closeModal()}><FormattedMessage id="sure" /></a>
                            </div>
                        </div>;
            this.changeStateMstr(str)
            this.modal.openModal();
        });
    }

    formatHistoryDetail(result){
        // console.log(result)
        const {marketsConf,currentMarket} = this.props;
        let record = [];
        let coinNameArry,coinName,markerName;
        // console.log(result);
        if(currentMarket&&result){
            coinNameArry = currentMarket.split("_");
            coinName = coinNameArry[0].toUpperCase();
            markerName = coinNameArry[1].toUpperCase();
            let exchangeBixDian = 0;
            try{
                exchangeBixDian = marketsConf[currentMarket].exchangeBixDian
            }catch(e){
                
            }
            let numberBixDian = marketsConf[currentMarket].numberBixDian;
            BigNumber.RM = 0;
            
            for(let i=0;i<result.length;i++){
                record[i] = {};
                record[i].date = TradeFormatDate(new Date(result[i].date));
                // record[i].typeRes = result[i].type == 1? "买入" : "卖出";
                record[i].tradeTypes = result[i].type;
                record[i].price = new BigNumber(result[i].price).toFixed(exchangeBixDian);
                record[i].amount = new BigNumber(result[i].amount).toFixed(numberBixDian);
                record[i].averagePrice = result[i].completeNumber >0 ?new BigNumber(result[i].completeTotalMoney).div(new BigNumber(result[i].completeNumber)).toFixed(exchangeBixDian): "--";
                record[i].completeNumber = result[i].completeNumber>0?new BigNumber(result[i].completeNumber).toFixed(numberBixDian):"--";
                record[i].completeTotalMoney = result[i].completeTotalMoney>0?new BigNumber(result[i].completeTotalMoney).toFixed(exchangeBixDian):"--";
                record[i].statusRes = result[i].status ==1? <FormattedMessage id="CanceledEntrust"/> : <FormattedMessage id="CompletedEntrust"/>;
                record[i].status = result[i].status;
                record[i].entrustId = result[i].entrustId;
                record[i].market = result[i].market? result[i].market.replace('_','/').toUpperCase():'';
                
            }
        }
        //  console.log(record)
        //  console.log(record)
        return record;
    }

    currentPageClick (value){
        this.setState({
            pageIndex:value
        }, () =>{ 
            if(this.state.timeType==0){
                this.fetchOrderHistory24HInfo();
            }
            else if(this.state.timeType==1){
                this.fetchOrderHistoryInfo();
            }
        })
    }

    updateType(value){
        this.setState({
            type:value,
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

    setTimeType(value){
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

    resatOrderHistoryConf(){
        this.setState({
            pageIndex:1,
            includeCancel:false,
            timeType:0,
            type:-1,
        }, () =>{ 
            this.fetchOrderHistory24HInfo();
        })
    }

    fetchOrderHistoryInfo(){
        const { currentMarket } = this.props;
        let showCancel = this.state.includeCancel?1:0;
        this.props.fetchOrderHistory(currentMarket,showCancel,this.state.timeType,this.state.type,this.state.pageIndex,this.state.pageSize); 
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
    changeHideTrade(){
        this.setState({
            hideTrade:!this.state.hideTrade
        })
    }
    //tab 切换时 改变 index
    changeTabIndex(val){
        console.log(val);
        this.setState({
            tabIndex:val
        })
    }

    addParameterToURL = (url) => {
        const { currentMarket } = this.props
        let _param = url.indexOf("?") > 0?"&":"?";
        return  `${url}${_param}market=${currentMarket}`
    }
    componentDidCatch(err,infor){
        if(window.ERRORCONFIG){
            this.setState({
                dataError:true
            })
            // console.log(err,infor)
        }else{
            // console.log(err,infor)
        }
       
    }

    render() {
        //throw new Error(2222)
        if(this.state.dataError){
            return  <div className="iconfont icon-jiazai new-loading"></div>
        }
        const { user,currentMarket, entrustrecord,orderhistory,orderhistory24H} = this.props;
        const { timeType,showData,hideTrade,includeCancel,tabIndex ,dataError} = this.state
        let coinNameArry,coinName,markerName;
        if(currentMarket){
            coinNameArry = currentMarket.split("_");
            coinName = coinNameArry[0].toUpperCase();
            markerName = coinNameArry[1].toUpperCase();
        }
        let limit = this.formatFundsDetail(entrustrecord.limit.data, 1);
        let plan = this.formatFundsDetail(entrustrecord.plan.data);
        
        let history = this.formatHistoryDetail(orderhistory.datas.records);
        let history24H = this.formatHistoryDetail(orderhistory24H.datas.records)
        return (
            <React.Fragment>
            <div className="entrust-record">
                <Tabs   onSelect={this.changeTabIndex}>
                    <div className="trade-item-title trade-item-title-entrust">
                        <div className="trade-item-title-right">
                            <TabList className="clearfix">
                                <Tab><FormattedMessage id="限价委托" /></Tab>
                                <Tab><FormattedMessage id="计划委托" /></Tab>
                                <Tab onClick={()=>{this.resatOrderHistoryConf()}}><FormattedMessage id="历史委托-交易"/></Tab>
                            </TabList>
                            <Link className="look-over" to={this.addParameterToURL(this.tabUrl[tabIndex])}>
                                <FormattedMessage id="查看更多" />
                            </Link>
                        </div>
                    </div>
                    <div className="trade-content record-content trade-content-flex">
                        <TabPanel>
                            <LimitEntrust limit={limit} user={user}  moadlCancel={this.moadlCancel.bind(this)}></LimitEntrust>
                        </TabPanel>
                        <TabPanel>
                            <PlanEntrust plan={plan} user={user} moadlCancel={this.moadlCancel.bind(this)}></PlanEntrust>
                            {/* <div className="trade-content-flex-tit"> 
                                <table>
                                    <thead>
                                        <tr>
                                            <th width="17%"><FormattedMessage id="日期" /></th>
                                            <th width="5%"><FormattedMessage id="类型" /></th>
                                            <th width="16%"><FormattedMessage id="触发价格" /></th>
                                            <th width="16%"><FormattedMessage id="委托价格" /></th>
                                            <th width="16%"><FormattedMessage id="委托数量" /></th>
                                            <th width="16%"><FormattedMessage id="总额" /></th>
                                            <th width="14%"><em style={{display:'none'}} onClick={this.moadlCancelAllStop.bind(this)}>[<FormattedMessage id="CancelAll"/>]</em></th>
                                        </tr>
                                    </thead>
                                </table>
                            </div>
                            {
                                user?(
                                    plan.length==0?(
                                        <div className="alert_under_table">
                                            <i className="iconfont icon-tongchang-tishi norecord"></i>
                                            <FormattedMessage id="no.plan.order"/>
                                        </div>
                                    ):(
                                        <ScrollArea className="trade-scrollarea">
                                            <div className="table-box-cover">
                                                <table>
                                                    <tbody>
                                                        {
                                                            plan.map((record, index) => {
                                                                return (
                                                                    <tr key={record.entrustId}>
                                                                        <td width="17%">{record.dateString}</td>
                                                                        <td width="5%" className="label">
                                                                        {
                                                                            record.tradeTypes==1?
                                                                                <span className="green"><FormattedMessage id="买入" /></span>:
                                                                                <span className="red"><FormattedMessage id="卖出" />
                                                                            </span>
                                                                        }
                                                                        </td>
                                                                        <td width="16%">{!isNaN(record.triggerPrice)?record.triggerPrice:record.triggerPriceProfit}</td>
                                                                        <td width="16%">{!isNaN(record.unitPrice)?record.unitPrice:record.unitPriceProfit}</td>
                                                                        <td width="16%">{!isNaN(record.numbers)?record.numbers:record.stopAmount}</td>
                                                                        <td width="16%">{record.planTotalMoney}</td>
                                                                        <td width="14%">
                                                                            <em onClick={this.moadlCancel.bind(this, record.entrustId,record.plantype)}>
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
                                        </ScrollArea>
                                    )
                                ):(
                                    <div className="alert_under_table">
                                        <i className="iconfont icon-tongchang-tishi norecord"></i> 
                                        &nbsp;<FormattedMessage id="haveNotSign"/>  <Link to='/bw/login'><FormattedMessage id="Login" /></Link>  <FormattedMessage id="or"/>  <Link to='/bw/signup'><FormattedMessage id="OpenAccount" /></Link> <FormattedMessage id="andTryAgain" />
                                    </div>
                                )
                            } */}
                        </TabPanel>
                        <TabPanel>
                            {/* <HistoryEntrust user={user} history={history} history24H={history24H} timeType={timeType} includeCancel={includeCancel} updateType={this.updateType.bind(this)} setTimeType ={this.setTimeType.bind(this)} setIncludeCancel={this.setIncludeCancel.bind(this)} moadlDetail={this.moadlDetail.bind(this)}></HistoryEntrust> */}
                            <div className="select-conf-set">
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
                                    Cb={this.updateType.bind(this)}
                                    options={this.typeOptions}
                                />
                                <div className="checkbox-includeCancel" onClick={()=>{this.setIncludeCancel()}}>
                                    <div className={`checkbox-div checkbox-hidden ${!includeCancel?"bg-white":""}`}>
                                        {!includeCancel&&<i className="iconfont icon-xuanze-yixuan"></i>}
                                    </div>
                                    <FormattedMessage id="隐藏已取消"/>
                                </div>
                            </div>
                            <div className="table-box-cover-tit">
                                <table>
                                    <thead>
                                        <tr>
                                            <th width="14%"><FormattedMessage id="日期" /></th>
                                            <th width="5%"><FormattedMessage id="类型" /></th>
                                            <th width="12%" style={{textAlign:'center'}}><FormattedMessage id="市场" /></th>
                                            <th width="10%"><FormattedMessage id="委托价格" /></th>
                                            <th width="11%"><FormattedMessage id="委托数量" /></th>
                                            <th width="10%"><FormattedMessage id="成交均价" /></th>
                                            <th width="11%"><FormattedMessage id="已成交" /></th>
                                            <th width="11%"><FormattedMessage id="总额" /></th>
                                            <th width="8%"><FormattedMessage id="状态" /></th>
                                            <th ><FormattedMessage id="操作" /></th>
                                        </tr>
                                    </thead>
                                </table>
                            </div>
                            
                            {
                                user?(
                                    // entrustrecord.history.isFetching?(
                                    //     <div>Loading...</div>
                                    // ):(
                                        this.state.timeType==1?(
                                            history.length==0?(
                                                <div className="alert_under_table" style={{top:'90px'}}>
                                                    <i className="iconfont icon-tongchang-tishi norecord"></i>
                                                    <FormattedMessage id="no.order.record"/>
                                                </div>
                                            ):(
                                                <ScrollArea className="trade-scrollarea">
                                                    <div className="table-box-cover table-box-cover-sm">
                                                        <table>
                                                            <tbody>
                                                                {
                                                                    history.map((record, index) => {
                                                                        return (
                                                                            <tr key={record.entrustId}>
                                                                                <td width="14%">{record.date}</td>
                                                                                <td width="5%" className=" label">
                                                                                    {
                                                                                        record.tradeTypes==1?
                                                                                        <span className="green"><FormattedMessage id="买入" /></span>:
                                                                                        <span className="red"><FormattedMessage id="卖出" /></span>
                                                                                    }
                                                                                </td>
                                                                                <td width="12%">{record.market}</td>
                                                                                <td width="10%">{record.price}</td>
                                                                                <td width="11%">{record.amount}</td>
                                                                                <td width="10%">{record.averagePrice}</td>
                                                                                <td width="11%">{record.completeNumber}</td>
                                                                                <td width="11%">{record.completeTotalMoney}</td>
                                                                                <td width="8%">{record.statusRes}</td>
                                                                                <td>{record.status==2?<em onClick={this.moadlDetail.bind(this,record.entrustId,record.tradeTypes,record.amount,record.completeNumber,record.averagePrice,record.completeTotalMoney)}><FormattedMessage id="详细"/></em>:""}</td>
                                                                            </tr>
                                                                        )
                                                                    })
                                                                }
                                                                
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </ScrollArea>
                                              )
                                            )
                                            :
                                            (
                                                history24H.length==0?(
                                                    <div style={ {position:'relative', flex: '1 0 0',marginTop:'-22px'}}>
                                                        <div className="alert_under_table">
                                                            <i className="iconfont icon-tongchang-tishi norecord"></i>
                                                            <FormattedMessage id="no.order.record"/>
                                                        </div>
                                                    </div>
                                                ):(
                                                    <ScrollArea  className="trade-scrollarea">
                                                        <div className="table-box-cover table-box-cover-sm">
                                                            <table>
                                                                <tbody>
                                                                    {
                                                                        history24H.map((record, index) => {
                                                                            return (
                                                                                <tr key={record.entrustId}>
                                                                                    <td width="14%">{record.date ? record.date.split(" ")[1] : '-'}</td>
                                                                                    <td width="5%"className="label">
                                                                                        {
                                                                                            record.tradeTypes==1?
                                                                                            <span className="green"><FormattedMessage id="买入" /></span>:
                                                                                            <span className="red"><FormattedMessage id="卖出" /></span>
                                                                                        }
                                                                                    </td>
                                                                                    <td width="12%">{record.market}</td>
                                                                                    <td width="10%">{record.price}</td>
                                                                                    <td width="11%">{record.amount}</td>
                                                                                    <td width="10%">{record.averagePrice}</td>
                                                                                    <td width="11%">{record.completeNumber}</td>
                                                                                    <td width="11%">{record.completeTotalMoney}</td>
                                                                                    <td width="8%">{record.statusRes}</td>
                                                                                    <td >{record.status==2?<em onClick={this.moadlDetail.bind(this,record.entrustId,record.tradeTypes,record.amount,record.completeNumber,record.averagePrice,record.completeTotalMoney)}><FormattedMessage id="详细"/></em>:""}</td>
                                                                                </tr>
                                                                            )
                                                                        })
                                                                    }
                                                                    
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </ScrollArea>
                                                )
                                            )
                                        
                                    // )
                                ):(
                                    <div style={ {minHeight:'50px', position:'relative', flex: '1 0 0',marginTop:'-22px'}}>
                                    <div className="alert_under_table">
                                        <i className="iconfont icon-tongchang-tishi norecord"></i> 
                                        &nbsp;
                                        <FormattedMessage id="haveNotSign"/>  <Link to='/bw/login'><FormattedMessage id="Login" /></Link>  <FormattedMessage id="or"/>  <Link href='/bw/signup'><FormattedMessage id="OpenAccount" /></Link> <FormattedMessage id="andTryAgain" />
                                    </div>
                                    </div>
                                )
                            }
                        </TabPanel>
                    </div>
                </Tabs>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </ReactModal>
                
            </div>
            <div className="zw"></div>
            </React.Fragment>
        )
    }
}

export default connect(null,(dispatch)=>{
    return {
        setPk(data){
            dispatch(reqPic(data));
        }
    }
})(Entrustrecord);