import React from 'react'
import axios from 'axios'
import { connect } from 'react-redux'
import cookie from 'js-cookie';
import { Link,browserHistory } from 'react-router';
import { FormattedMessage, injectIntl, FormattedTime, FormattedDate } from 'react-intl';
import { COOKIE_LAN,PAGEINDEX,PAGESIZEFIVE,LOGINVIEWPORT,COIN_KEEP_POINT,COOKIE_UID,PAGESIZETHIRTY,DOMAIN_TRANS } from '../../conf';
import Pages from '../../components/pages';
import ExportModal from '../../components/export';
import SelectHistory from '../../components/selectHistory'
import { formatDate,TradeFormatDate } from '../../utils';
import FileSaver from 'file-saver';
import Select from 'react-select';
import { fetchMarketList,fetchEntrustList,setEntrustListCoin } from '../../redux/modules/entrust'
const BigNumber = require('big.js')
import '../../assets/css/trust.css'
import {formatURL} from '../../utils/index'
import { doSaveConditionTransrecord } from '../../redux/modules/entrustcd'

class TransRecord extends React.Component{
    constructor(props){
        super(props)
        const { marketVal = 'btc', legVal = 'USDT', type = '-1', timeType = 0 } = this.props.entrustcd.transrecord
        this.state = {
            marketVal: marketVal,
            marketOption:[],
            legVal: legVal,
            type: type,
            includeCancel:1,
            timeType: timeType,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZETHIRTY,
            tableList:[],
            totalCount:0,
        }

        // this.handleChangeMarket = this.handleChangeMarket.bind(this)
        // this.handleChangeLeg = this.handleChangeLeg.bind(this)
        this.setMarketCoin = this.setMarketCoin.bind(this)
        // this.handleChangeType = this.handleChangeType.bind(this)
        this.handleChangeTime = this.handleChangeTime.bind(this)
        this.formatHistoryDetail = this.formatHistoryDetail.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.currentPageClick = this.currentPageClick.bind(this)
        this.fomartexportData = this.fomartexportData.bind(this)
        this.getCode = this.getCode.bind(this)
        this.getCodeLeg = this.getCodeLeg.bind(this)
        this.getCodeType = this.getCodeType.bind(this)

        this.conditionTransrecord = this.props.entrustcd.transrecord
    }

    setCd(key, data){
        this.conditionTransrecord[key] = data;
        this.props.doSaveConditionTransrecord(this.conditionTransrecord);
    }

    componentDidMount(){
        const curMarket = this.props.location.query.market?this.props.location.query.market:"btc_usdt";
        this.setState({
            tab:this.props.location.query.type||0,
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
                const { timeType,includeCancel,legVal,type,pageIndex,pageSize } = this.state
                this.setMarketCoin()
                this.props.fetchOrderHistory(curMarket,timeType,type,pageIndex)
            })
        })
            
    }

    //遍历市场币种 option
    setMarketCoin(cb){
        const { legVal,marketVal } = this.state
        let marketOption = [];
        const coinList = this.props.coin.data[legVal.toUpperCase()]
        const thisVal = coinList.includes(marketVal.toUpperCase())?marketVal:coinList[0]
        if(!coinList.includes(marketVal.toUpperCase())){
            this.resetUrl(coinList[0],0)
        }
        for(let i=0;i < coinList.length; i++ ){
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
        const { marketVal,timeType,includeCancel,legVal,type,pageIndex,pageSize } = this.state
        const { records,count } = this.props.orderHistpry.data
        const user = cookie.get(COOKIE_UID)
        const zlogin = cookie.get('zloginStatus')
        if(count > pageSize&&user&&zlogin!=4&&values !== 'page'){
            this.refs.pages.resetPage();
        }
        const market = (marketVal+'_'+legVal).toLowerCase()
        this.props.setEntrustListCoin(market)
        this.props.fetchOrderHistory(market,timeType,type,pageIndex)
    }

    //币种 select
    // handleChangeMarket(selectedOption){
    //     this.setState({
    //         marketVal:selectedOption.value,
    //         pageIndex:PAGEINDEX
    //     },() => this.requestTable())
    // }
    fomartexportData(data){
        let chartName = '';
        let languaged = cookie.get(COOKIE_LAN);
        let localData =TradeFormatDate(new Date(), this.dateFormat).replace(/[\|\,|\:|\_|\ ]/g,'').replace(/\//g,'');
        let sdata = []
        if(data !==''){
            data.forEach((item,index)=>{
                    let ddata = []
                    let tradeTypesname = '';
                    let statusname = '';
                    if(languaged == 'cn'){
                        tradeTypesname = item['type'] ==1?'买入':'卖出'
                        chartName =localData +  '成交记录'
                    }else if (languaged == 'en'){
                        tradeTypesname = item['type'] ==1?'Buy':'Sell'
                        chartName = localData + 'trade history'
                    }else if(languaged == 'hk'){
                        tradeTypesname = item['type'] ==1?'買入':'賣出'
                        chartName =  localData + '成交記錄'
                    }else if(languaged == 'jp'){
                        tradeTypesname = item['type'] ==1?'購入':'販売'
                        chartName =  localData + '成交記錄'
                    }
                    else if(languaged == 'kr'){
                        tradeTypesname = item['type'] ==1?'매수':'매도'
                        chartName =  localData + '거래 내역'
                    }
                    else{
                        tradeTypesname = item['type'] ==1?'Buy':'Sell'
                        chartName = localData + 'trade history'
                    }
                    ddata.push(tradeTypesname)
                    ddata.push(item['date'])
                    ddata.push(item['price'])
                    ddata.push(item['outAmount'])
                    ddata.push(item['intAmount'])
                    ddata.push(item['fees'])
                    sdata.push(ddata)
            })
        }
        let str =  '';
        switch(languaged) {
            case 'cn':
                str = '交易,时间,成交价,成交金额,成交量,手续费';
                 break;
            case 'en':
                 str = 'Exchange,Time,Price,Total Price,Amount,Fee';
                break;
            case 'hk':
                 str = '交易,時間,成交價,成交金額,成交量,手續費';
                break;
            case 'jp':
                str = '取引,時間,成交價,成約価格,成約数,手数料';
               break;
            case 'kr':
               str = '거래,시간,거래가격,거래금액,거래량,수수료';
              break;    
            default:
                 str = '交易,时间,成交价,成交金额,成交量,手续费';
        }
        sdata.forEach((item,index)=>{
            str += '\n'+item+','
        })
        // Excel打开后中文乱码添加如下字符串解决
        let exportContent = "\uFEFF";
        let blob = new Blob([exportContent + str], {
            type: "text/plain;charset=utf-8"
        });
        FileSaver.saveAs(blob, `${chartName}.csv`);
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
            this.setMarketCoin(() => this.requestTable())
        })
    }
    getCodeType(item = {}){
        this.setCd('type', item.val);
        this.setState({
            type: item.val,
            pageIndex:PAGEINDEX
        },() => this.requestTable());
    }
    //时间 Tab
    handleChangeTime(id){
        this.setCd('timeType', id);

        this.setState({
            timeType:id,
            pageIndex:PAGEINDEX
        },() => this.requestTable())
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
        const { marketVal,legVal } = this.state
        let currentMarket = (marketVal+"_"+legVal).toLowerCase()
        let record = [];
        let coinNameArry,coinName,markerName;
        if(currentMarket&&result){
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
            for(var i=0;i<result.length;i++){
                record[i] = {};
                record[i].date = TradeFormatDate(new Date(result[i].date));
                record[i].type = result[i].type;
                record[i].price = new BigNumber(result[i].price).toFixed(exchangeBixDian)+" "+ markerName;
                record[i].outAmount = result[i].type == 1? new BigNumber(result[i].outAmount).toFixed(exchangeBixDian)+" "+markerName:new BigNumber(result[i].outAmount).toFixed(exchangeBixDian)+" "+markerName;
                record[i].intAmount = result[i].type == 1? new BigNumber(result[i].intAmount).toFixed(numberBixDian)+" "+coinName:new BigNumber(result[i].intAmount).toFixed(numberBixDian)+" "+coinName;
                record[i].fees = result[i].type == 1? new BigNumber(result[i].feesBuy).toFixed(8)+" "+coinName:new BigNumber(result[i].feesSell).toFixed(8)+" "+markerName;
            }
        }
        return record;
    }
   

    render(){
        const user = cookie.get(COOKIE_UID)
        const zlogin = cookie.get('zloginStatus')
        const { marketVal,marketOption,legVal,type,includeCancel,timeType,pageIndex,pageSize } = this.state
        const { records,count } = this.props.orderHistpry.data
        const { isloaded,data } = this.props.coin
        const tableList = isloaded&&data[legVal.toUpperCase()].includes(marketVal.toUpperCase())&&this.formatHistoryDetail(records)
        return(
            <div className="mainer-phase2 select-new bk_entrust">
                <div className="container2">
                    <div className="bbyh-weituoHeader">
                        <h2 className="entrust-title unChooseTitle">
                            <Link to={`${formatURL('/entrust/current')}?market=${marketVal.toLowerCase()}_${legVal.toLowerCase()}`}>
                                <FormattedMessage id="当前委托_w" />
                            </Link>
                        </h2>
                        <h2 className="entrust-title unChooseTitle">
                            <Link to={`${formatURL('/entrust/list')}?market=${marketVal.toLowerCase()}_${legVal.toLowerCase()}`}>
                                <FormattedMessage id="ORDER-HISTORY" />
                            </Link>
                        </h2>
                        <h2 className="entrust-title chooseTitle"><FormattedMessage id="TRADE-HISTORY" /></h2>
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
                        <h5 className="left padl10"><FormattedMessage id="交易" />：</h5>
                            <div className="record-head entrust-selcet">
                                <SelectHistory
                                        defaultValue = {type}
                                        options = {[
                                            { val:"-1", key: <FormattedMessage id="不限" /> },
                                            { val:"1" , key: <FormattedMessage id="buyit" /> },
                                            { val:"0" , key: <FormattedMessage id="sellit" /> },                                    
                                        ]}
                                        Cb={this.getCodeType}
                                />
                            </div>
                        </div>

                        <div className="entrust-time left">
                            <h5 className="padl40 padl10"><FormattedMessage id="时间_w" /></h5>
                            <ul className="tab-time">
                            <li>
                                 
                                 <label className={timeType==0?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'}  onClick={() => this.handleChangeTime(0)}></label>
                                 <span> <FormattedMessage id="24H" /></span>
                            </li>   
                            <li>                              
                                    <label className={timeType==1?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(1)}></label>
                                    <span> <FormattedMessage id="History" /></span>
                            </li>  
                            </ul>
                        </div>
                       {tableList.length > 0&&user&&zlogin!=4? <ExportModal boxname={<FormattedMessage id="导出历史成交记录" />} fomartexportData={()=>this.fomartexportData(tableList)}></ExportModal>: <div className="export-dis right"><FormattedMessage id="导出历史成交记录" /></div>}
                    </div>
                    <div className="entrust-con bk-entrust">
                        <table className="table-entrust ">
                            <thead>
                                <tr>
                                    <th><FormattedMessage id="交易" /></th>
                                    <th className="text-left"><FormattedMessage id="时间"/></th>
                                    <th className="text-right"><FormattedMessage id="entrust.text6" /></th>
                                    <th className="text-right"><FormattedMessage id="成交金额" /></th>
                                    <th className="text-right"><FormattedMessage id="成交量" /></th>
                                    <th className="text-right"><FormattedMessage id="手续费" /></th>
                                    <th className="borright"></th>
                                </tr>
                            </thead>
                            {user && zlogin!=4 ?<tbody id="historyEntrustList">
                                {
                                    tableList.length > 0? tableList.map((item,index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{item.type==1?<span className="green"><FormattedMessage id="buyit" /></span>:<span className="red"><FormattedMessage id="sellit" /></span>}</td>
                                                <td className="text-left">{item.date}</td>
                                              
                                                <td className="text-right">{item.price}</td>
                                                <td className="text-right">{item.outAmount}</td>
                                                <td className="text-right">{item.intAmount}</td> 
                                                <td className="text-right">{item.fees}</td> 
                                                <td></td> 
                                            </tr>
                                        )
                                    }):(
                                        <tr className="nodata">
                                            <td className="billDetail_no_list" colSpan="15">
                                                <p className="entrust-norecord"> 
                                                    <svg className="icon" aria-hidden="true">
                                                        <use xlinkHref="#icon-tongchang-tishi"></use>
                                                    </svg>
                                                    <FormattedMessage id="当前没有成交记录数据"/>
                                                </p>
                                            </td>
                                        </tr>
                                    )

                                }
                            </tbody>
                        :(
                                    <p className="alert_under_table user_login top200">
                                        <i className="iconfont icon-tongchang-tishi norecord"></i> 
                                        &nbsp;<FormattedMessage id="haveNotSign"/>  <Link to='/bw/login'><FormattedMessage id="Login" /></Link>  <FormattedMessage id="or"/>  <Link to='/bw/signup'><FormattedMessage id="OpenAccount" /></Link> <FormattedMessage id="andTryAgain" />
                                    </p>
                                )}
                                </table>
                    </div>
                    <div className="historyEntrustList-page tablist" style={{display:count > pageSize&&user&&zlogin!=4?'block':'none'}}>
                        <Pages 
                            pageIndex={pageIndex} 
                            pagesize={pageSize} 
                            total={count}
                            ref="pages"
                            currentPageClick = { this.currentPageClick }
                        />
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state,ownProps) => {
    return {
        entrustcd: state.entrustcd,
        coin:state.entrust.coin,
        orderHistpry:state.entrust.transRecord,
        marketsConf: state.marketsConf.marketsConfData,
        listCoin:state.entrust.listCoin
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        doSaveConditionTransrecord:(data) => {
            dispatch(doSaveConditionTransrecord(data))
        },
        fetchMarketList:(cb) => {
            dispatch(fetchMarketList()).then(cb)
        },
        fetchOrderHistory:(market,timeType,type,pageNum)=>{
            let lastTime = +new Date;
            dispatch(fetchEntrustList(market,timeType,type,pageNum,lastTime))
        },
        setEntrustListCoin:(params) => {
            dispatch(setEntrustListCoin(params))
        }
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(TransRecord)



















































