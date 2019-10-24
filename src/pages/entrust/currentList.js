import React from 'react'
import cookie from 'js-cookie';
import { Link,browserHistory} from 'react-router';
import ReactModal from '../../components/popBox';
import { connect } from 'react-redux'
import { FormattedMessage, injectIntl, FormattedTime, FormattedDate } from 'react-intl';
import { PAGEINDEX,PAGESIZEFIVE,LOGINVIEWPORT,COIN_KEEP_POINT,PAGESIZETHIRTY,COOKIE_UID,DOMAIN_TRANS,COOKIE_LAN } from '../../conf';
import Pages from '../../components/pages';
import SelectHistory from '../../components/selectHistory'
import FileSaver from 'file-saver';
import ExportModal from '../../components/export';
import { formatDate ,optPop,TradeFormatDate} from '../../utils';
import { fetchMarketList,fetchEntrustList,setEntrustTransCoin } from '../../redux/modules/entrust'
import {cancelEntrust} from '../../redux/modules/entrustrecord'
import { fetchCurrentHistory } from '../../redux/modules/currenthistory'
import Select from 'react-select';
import {formatURL} from '../../utils/index'
const BigNumber = require('big.js')
import '../../assets/css/trust.css'
import { doSaveConditionCurrent } from '../../redux/modules/entrustcd'

class CurrentList extends React.Component{
    constructor(props){
        super(props)
        const {marketVal = 'btc', legVal = 'USDT', type = '-1', tab = 0} = this.props.entrustcd.current;

        this.state = {
            marketVal:marketVal,
            marketOption:[],
            legVal:legVal,
            type:''+type,
            includeCancel:true,
            timeType:0,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZETHIRTY,//PAGESIZETHIRTY
            tableList:[],
            // totalCount:0,
            tab:''+tab,
        }
        this.conditionCurrent = this.props.entrustcd.current;
        //console.log(this.conditionCurrent, '@@@@----=====>', this.state);
        this.setMarketCoin = this.setMarketCoin.bind(this)
        // this.handleChangeTime = this.handleChangeTime.bind(this)
        this.formatHistoryDetail = this.formatHistoryDetail.bind(this)
        this.moadlDetail = this.moadlDetail.bind(this)
        this.formatMoadlDetail = this.formatMoadlDetail.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.currentPageClick = this.currentPageClick.bind(this)
        this.getCode = this.getCode.bind(this)
        this.getCodeLeg = this.getCodeLeg.bind(this)
        this.getCodeType = this.getCodeType.bind(this)
        this.getChangeType = this.getChangeType.bind(this)
        this.handleConfirmCancel = this.handleConfirmCancel.bind(this)
    }

    componentDidMount(){
        
        const curMarket = this.props.location.query.market?this.props.location.query.market:"btc_usdt";
        this.setState({
            tab:this.props.location.query.type||(this.props.entrustcd.current.tab || 0),
            marketVal:curMarket.split("_")[0].toUpperCase(),
            legVal:curMarket.split("_")[1].toUpperCase(),
        }, () => {
            new Promise((resolve, reject) => {
                if(!this.props.coin.isloaded){
                    this.props.fetchMarketList(() => {
                        resolve()
                    })
                }else{
                    resolve()
                }
            }).then((value) => {
                const { timeType,includeCancel,type,pageIndex,pageSize,tab } = this.state
                const numIncludeCancel = includeCancel?0:1
                this.setMarketCoin()
                this.props.fetchCurrentHistory(tab,curMarket,numIncludeCancel,timeType,type,pageIndex,pageSize)
            })
        })
    }
    //遍历市场币种 option
    setMarketCoin(cb){
        const { legVal,marketVal } = this.state
        let marketOption = [];
        const coinList = this.props.coin.data[legVal.toUpperCase()];
        const thisVal = coinList.includes(marketVal.toUpperCase())?marketVal:coinList[0];
        if(!coinList.includes(marketVal.toUpperCase())){
            this.resetUrl(coinList[0],0)
        }
        for(let i=0;i < coinList.length; i++){
            let item = {}
            item.val = coinList[i]
            item.key = coinList[i]
            marketOption.push(item)
        }
        this.setState({
            marketOption,
            marketVal:thisVal
        },cb)
    }
    
    //请求 难受的 列表
    requestTable(values){
        const { marketVal,timeType,includeCancel,legVal,type,pageIndex,pageSize,tab} = this.state
        const { totalCount,list } = this.props.currentHistory.datas
        const user = cookie.get(COOKIE_UID)
        const zlogin = cookie.get('zloginStatus')
        if(totalCount>pageSize&&user&&zlogin!=4&&values !== 'page'){
            this.refs.pages.resetPage();
        }
        const market = (marketVal+'_'+legVal).toLowerCase()
        const numIncludeCancel = includeCancel?0:1        
        this.props.setEntrustTransCoin(market) 
        this.props.fetchCurrentHistory(tab,market,numIncludeCancel,timeType,type,pageIndex,pageSize)
    }
    //重置URL
    resetUrl = (val,type) => {
        const { marketVal,legVal } = this.state
        const coinName = type == 0?val:marketVal;
        const marketName = type == 1?val:legVal;
        this.props.router.replace({
            pathname: this.props.location.pathname,
            query: {market:`${coinName.toLowerCase()}_${marketName.toLowerCase()}`}
        })
    }

    setCd(key, data){
        this.conditionCurrent[key] = data;
        this.props.doSaveConditionCurrent(this.conditionCurrent);
    }

    getCode(item = {}){
        this.setCd('marketVal', item.val);
        this.resetUrl(item.val,0)
        this.setState({
            marketVal: item.val,
            pageIndex:PAGEINDEX
        },() => this.requestTable());
    }
    getCodeLeg(item={}){
        this.setCd('legVal', item.val);
        const { marketVal } = this.state
        let newMarketVal = marketVal == item.val?this.props.coin.data[marketVal][0]:marketVal      
        this.setState({
                legVal: item.val,
                marketVal:newMarketVal,
                pageIndex:PAGEINDEX
            },() => {
                this.resetUrl(item.val,1);
                this.setMarketCoin(this.requestTable)
            })
    }
    fomartexportData(data){
        const {tab} = this.state
        let chartName = '';
        let languaged = cookie.get(COOKIE_LAN);
        let sdata = []
        let localData =this.props.formatDate(new Date(), this.dateFormat).replace(/[\|\,|\:|\_|\ ]/g,'').replace(/\//g,'');
        if(data !==''){
            data.forEach((item,index)=>{
                    let ddata = []
                    let tradeTypesname = '';
                    let statusname = '';
                    let tabName ='';
                    if(languaged == 'cn'){
                        tradeTypesname = item['types'] ==0?'买入':'卖出'
                        tabName = tab ==1?'计划':'限价'
                        chartName = localData+'成交记录'
                    }else if (languaged == 'en'){
                        tradeTypesname = item['types'] ==0?'Buy':'Sell'
                        tabName = tab ==1?'Stop-Limit':'Limit'
                        chartName = localData+'trade history'
                    }else if(languaged == 'hk'){
                        tradeTypesname = item['types'] ==0?'買入':'賣出'
                        tabName = tab ==1?'計劃':'限價'
                        chartName = localData+'成交記錄'
                    }else if (languaged == 'jp'){
                        tradeTypesname = item['types'] ==0?'Buy':'Sell'
                        tabName = tab ==1?'Stop-Limit':'Limit'
                        chartName = localData+'trade history'
                    }else if (languaged == 'kr'){
                        tradeTypesname = item['types'] ==0?'Buy':'Sell'
                        tabName = tab ==1?'Stop-Limit':'Limit'
                        chartName = localData+'trade history'
                    }else{
                        tradeTypesname = item['types'] ==0?'买入':'卖出'
                        tabName = tab ==1?'计划':'限价'
                        chartName = localData+'成交记录'
                    }
                    ddata.push(tradeTypesname)
                    ddata.push(tabName)
                    ddata.push(item['unitPrice'])
                    tab ==1?ddata.push(item['triggerPrice']):''
                    ddata.push(item['numbers'])
                    ddata.push(item['completeTotalMoney'])
                    ddata.push(item['date'])
                    sdata.push(ddata) 
            })
        }
        let str =  '';
        switch(languaged) {
            case 'cn':
                str = `交易,类型,委托价格,${tab ==1?'触发价格,':''}委托数量/已成交,成交金额,日期`;
                 break;
            case 'en':
                 str = `Exchange,Type,Order.Price,${tab ==1?'Stop Price,,':''}Amount / Filled ,Total,Date,Action`;
                break;
            case 'hk':
                 str = `交易,類型,委託價格,${tab ==1?'觸發價格,':''}委托數量/已成交,成交金額,日期`;
                break;
            default:
                 str = `交易,类型,委托价格,${tab ==1?'触发价格,':''},委托数量/已成交,成交金额,日期`;
        }
        sdata.forEach((item,index)=>{
            str += '\n'+item+','
        })
        // Excel打开后中文乱码添加如下字符串解决
        let exportContent = "\uFEFF";
        let blob = new Blob([exportContent + str], {
            type: "text/plain;charset=utf-8"
        });
        FileSaver.saveAs(blob, `${chartName}.xlsx`);
    }
    getCodeType(item = {}){
        
        this.setCd('type', item.val);
        this.setState({
            type: item.val,
            pageIndex:PAGEINDEX
        },() => this.requestTable());
    }
    getChangeType(item = {}){
        
        this.setCd('tab', item.val);
        this.setState({
            tab:item.val,
            pageIndex:PAGEINDEX
        },() => this.requestTable());
    }

    //分页
    currentPageClick (values){
        this.setState({
            pageIndex:values
        },() => this.requestTable('page'))
    }
    //格式化数据
    formatHistoryDetail(result){
        const {marketsConf} = this.props;
        const { marketVal,legVal,tab } = this.state
        let currentMarket =(marketVal+"_"+legVal).toLowerCase()
        let record = [];
        let coinNameArry,coinName,markerName;
        if(currentMarket&&result){
            coinNameArry = currentMarket.split("_");
            coinName = coinNameArry[0].toUpperCase();
            markerName = coinNameArry[1].toUpperCase();
            let exchangeBixDian = 0;
            try{
                exchangeBixDian = marketsConf[currentMarket].exchangeBixDian;
            } catch(e){
                
            }
            let numberBixDian = marketsConf[currentMarket].numberBixDian;
            BigNumber.RM = 0;
            // console.log(result)
            // console.log(tab)
            for(let i=0;i<result.length;i++){
                record[i] = {};
                record[i].date = TradeFormatDate(new Date(result[i].submitTime));
                record[i].unitPrice = result[i].unitPrice>0?new BigNumber(result[i].unitPrice).toFixed(exchangeBixDian)+ ' '+legVal:'--';
                // tab==1?record[i].triggerPrice==''&&record[i].triggerPrice!==0?'':record[i].triggerPrice = new BigNumber(result[i].triggerPrice).toFixed(exchangeBixDian):'';
                record[i].numbers = new BigNumber(result[i].numbers).toFixed(numberBixDian)+ ' '+marketVal;
                // record[i].averagePrice = result[i].completeNumber >0 ?new BigNumber(result[i].completeTotalMoney).div(new BigNumber(result[i].completeNumber)).toFixed(exchangeBixDian): "--";
                record[i].completeNumber = result[i].completeNumber>0?new BigNumber(result[i].completeNumber).toFixed(numberBixDian)+ ' '+marketVal:"--";
                record[i].completeTotalMoney = result[i].completeTotalMoney>0?new BigNumber(result[i].completeTotalMoney).toFixed(exchangeBixDian)+" "+legVal:"--";
                record[i].types = result[i].types;
                record[i].id = result[i].id;
                // console.log(result[i].triggerPrice)
                if(tab == 1){
                   record[i].triggerPrice = result[i].triggerPrice>0?new BigNumber(result[i].triggerPrice).toFixed(exchangeBixDian)+" "+legVal:'--';
                }
            }
        }
        return record;
    }
    //历史委托ID详情
    formatMoadlDetail(result){
        const {marketsConf} = this.props;
        const { marketVal,legVal} = this.state
        let currentMarket = (marketVal+"_"+legVal).toLowerCase()
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
    moadlDetail(id){
        const Mstr = <div className="modal-btn">
                        <p><FormattedMessage id="bbyh确定撤销当前委托？" /></p>
                        <div className="modal-foot">
                            <a onClick={this.modal.closeModal} className="btn"><FormattedMessage id="withdraw.text51" /></a>
                            <a className="btn ml10" onClick={() => this.handleConfirmCancel(id)}><FormattedMessage id="withdraw.text49" /></a>
                        </div>
                     </div>;
            this.setState({Mstr})
            this.modal.openModal();
    }
    handleConfirmCancel(id){
        const {marketVal,legVal,tab} = this.state
        const market = (marketVal+'_'+legVal).toLowerCase()
        let type = tab == 1?true:false
        this.props.cancelEntrust(market,id,type,res=>{
            let result = res.data;
            if (result.datas == 200) {
                this.modal.closeModal();
                this.requestTable();
                optPop(() => {}, result.des);
            }else{
                optPop(() => {}, result.des);
            }
        })
    }
    render(){
        //console.log(this.props.entrustcd, '===--->');
        const user = cookie.get(COOKIE_UID)
        const zlogin = cookie.get('zloginStatus')
        const { marketVal,marketOption,legVal,type,includeCancel,timeType,pageIndex,pageSize,tab } = this.state
        
        const { totalCount,list } = this.props.currentHistory.datas
        const { isloaded,data } = this.props.coin
        const tableList = isloaded&&data[legVal.toUpperCase()].includes(marketVal.toUpperCase())&&this.formatHistoryDetail(list)
        return(
            <div className="mainer-phase2 select-new bk_entrust">
                <div className="container2">
                    <div className="bbyh-weituoHeader">
                        <h2 className="entrust-title chooseTitle"><FormattedMessage id="当前委托_w" /></h2>
                        <h2 className="entrust-title unChooseTitle">
                            <Link to={`${formatURL('/entrust/list')}?market=${marketVal.toLowerCase()}_${legVal.toLowerCase()}`}>
                                <FormattedMessage id="ORDER-HISTORY" />
                            </Link>
                        </h2>
                        <h2 className="entrust-title unChooseTitle">
                            <Link to={`${formatURL('/entrust/transrecord')}?market=${marketVal.toLowerCase()}_${legVal.toLowerCase()}`}>
                                <FormattedMessage id="TRADE-HISTORY" />
                            </Link>
                        </h2>
                    </div>
                    <div className="entrust-head">
                        <div className="entrust-head-market left">
                            <h5 className="left padl10"><FormattedMessage id="entrust.text1" /></h5>
                            <div className="record-head entrust-selcet">
                                {/* <Select
                                value={marketVal}
                                clearable={false}
                                searchable={false}
                                onChange={this.getCode}
                                options={marketOption}
                                /> */}
                                 <SelectHistory 
                                        defaultValue = {marketVal}
                                        options={marketOption}
                                        class="sm left marginleft5"
                                        Cb={this.getCode}
                                    />
                            </div>
                            <h5 className="left pad10">/</h5>
                            <div className="record-head entrust-selcet">
                                <SelectHistory
                                    defaultValue = {legVal}
                                    options = {[
                                        { val: 'USDT', key: 'USDT' },
                                        { val: 'BTC', key: 'BTC' },
                                      ]}
                                    Cb={this.getCodeLeg}
                                />
                            </div>
                        </div>
                       
                        <div className="entrust-head-type left">
                            <h5 className="left padl10"><FormattedMessage id="entrust.text2" /></h5>
                            <div className="record-head entrust-selcet">
                                    <SelectHistory
                                        defaultValue = {tab}
                                        options = {[
                                            { val:"1", key: <FormattedMessage id="计划" /> },
                                            { val:"0" , key: <FormattedMessage id="限价" /> },                                 
                                        ]}
                                        Cb={this.getChangeType}
                                    />
                            </div>
                        </div>
                        <div className="entrust-head-type left">
                         <h5 className="left padl10"><FormattedMessage id="交易" />：</h5>
                           
                            <div className="record-head entrust-selcet">
                                    <SelectHistory
                                        defaultValue = {type}
                                        options = {[
                                            { val:"-1", key: <FormattedMessage id="不限" /> },
                                            { val:"1" , key: <FormattedMessage id="买入_w" /> },
                                            { val:"0" , key: <FormattedMessage id="卖出_w" /> },                                    
                                        ]}
                                        Cb={this.getCodeType}
                                    />
                            </div>
                        </div>

                       
                        {/* {tableList.length > 0? <ExportModal boxname={<FormattedMessage id="导出当前委托记录" />} fomartexportData={()=>this.fomartexportData(tableList)}></ExportModal>:null} */}
                    </div>
                    <div className="entrust-con bk-entrust">
                        <table className="table-entrust bk_table">
                            <thead>
                                <tr>
                                    <th width="6%"><FormattedMessage id="交易" /></th>
                                    <th width="8%" className="text-left"><FormattedMessage id="account.text11" /></th>
                                    <th width="11%" className="text-right"><FormattedMessage id="unitPrice" /></th>
                                    {tab == 1&&<th width="11%" className="text-right"><FormattedMessage id="触发价格"/></th>}
                                    <th width="15%" className="text-right">{tab == 1?<FormattedMessage id="entrustAmount" />:<FormattedMessage id="委托数量/已成交" />}</th>
                                    {tab !== 1&&<th width="14%" className="text-right"><FormattedMessage id="bbyh金额" /></th>}
                                    <th width="6%" className="text-left"></th>
                                    <th width="16%" className="text-left"><FormattedMessage id="account.text9" /></th>
                                    <th width="8%" className="borright text-center"><FormattedMessage id="Action" /></th>
                                </tr>
                            </thead>
                            {user&&zlogin!=4? <tbody id="historyEntrustList">
                                {
                                !this.props.currentHistory.isLoading&&this.props.currentHistory.isLoaded?tableList.length > 0? tableList.map((item,index) => {
                                        return (
                                            <tr key={item.id}>
                                             <td>{item.types==0?<span className="red"><FormattedMessage id="sellit" /></span>:<span className="green"><FormattedMessage id="buyit" /></span>}</td>
                                                <td className="text-left">{tab == 1?<FormattedMessage id="计划" />:<FormattedMessage id="限价" />}</td>
                                                <td className="text-right">{item.unitPrice}</td>
                                                {tab == 1&& <td className="text-right">{item.triggerPrice}</td>}
                                                <td className="text-right">{item.numbers}{tab == 1?'':' / '}{tab == 1?'':item.completeNumber}</td>
                                                {tab !== 1&&<td className="text-right">{item.completeTotalMoney}</td>}
                                                <td  className="text-center"></td>
                                                <td  className="text-left">{item.date}</td>
                                                <td className="text-center"><a href="javascript:void(0)" onClick={this.moadlDetail.bind(this,item.id,item.types)}> <FormattedMessage id="撤销"/></a></td>
                                            </tr>
                                        )
                                    }):(
                                        <tr className="nodata">
                                            <td className="billDetail_no_list" colSpan="15">
                                                <p className="entrust-norecord"> 
                                                    <svg className="icon" aria-hidden="true">
                                                        <use xlinkHref="#icon-tongchang-tishi"></use>
                                                    </svg>
                                                    <FormattedMessage id="当前没有委托记录数据"/>
                                                </p>
                                            </td>
                                        </tr>
                                    )
                                    :<tr style={{background:'none',marginTop:'150px'}} className="iconfont icon-jiazai new-loading"></tr>
                                   
                                }
                            </tbody>:(
                                    <p className="alert_under_table user_login top200">
                                        <i className="iconfont icon-tongchang-tishi norecord"></i> 
                                        &nbsp;<FormattedMessage id="haveNotSign"/>  <Link to='/bw/login'><FormattedMessage id="Login" /></Link>  <FormattedMessage id="or"/>  <Link to='/bw/signup'><FormattedMessage id="OpenAccount" /></Link> <FormattedMessage id="andTryAgain" />
                                    </p>
                                )}
                                </table>
                    </div>
                    {totalCount>pageSize&&user&&zlogin!=4&&<div className="historyEntrustList-page tablist">
                        <Pages 
                            pageIndex={pageIndex} 
                            pagesize={pageSize} 
                            total={totalCount}
                            ref="pages"
                            currentPageClick = { this.currentPageClick }
                        />
                    </div>}
                </div>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </ReactModal>
            </div>
        )
    }
}

const mapStateToProps = (state,ownProps) => {
    return {
        entrustcd: state.entrustcd,
        coin:state.entrust.coin,
        currentHistory:state.currentHistory,
        marketsConf: state.marketsConf.marketsConfData,
        listCoin:state.entrust.transCoin,
        lang:state.language.locale,
        currentMarket: state.marketinfo.currentMarket,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        doSaveConditionCurrent:(data) => {
            dispatch(doSaveConditionCurrent(data))
        },
        fetchMarketList:(cb) => {
            dispatch(fetchMarketList()).then(cb)
        },
        fetchCurrentHistory:(tab,market,includeCancel,timeType,type,pageNum,pageSize)=>{
            let lastTime = +new Date;
            dispatch(fetchCurrentHistory(tab,market,includeCancel,timeType,type,pageNum,pageSize,lastTime))
        },
        setEntrustTransCoin:(params) => {
            dispatch(setEntrustTransCoin(params))
        },
        cancelEntrust:(market,entrustid,plantype,fun) => {
            dispatch(cancelEntrust(market,entrustid,plantype))
                .then(fun)
        },
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(CurrentList)



























