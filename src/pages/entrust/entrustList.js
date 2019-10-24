import React from 'react'
import axios from 'axios'
import cookie from 'js-cookie';
import { Link ,browserHistory} from 'react-router';
import EntrustModal from '../../components/entrustBox';
import ExportModal from '../../components/export';
import { connect } from 'react-redux'
import SelectHistory from '../../components/selectHistory'
import { FormattedMessage, injectIntl, FormattedTime, FormattedDate } from 'react-intl';
import { COOKIE_LAN,PAGEINDEX,PAGESIZEFIVE,LOGINVIEWPORT,COOKIE_UID,COIN_KEEP_POINT,PAGESIZETHIRTY,DOMAIN_TRANS } from '../../conf';
import Pages from '../../components/pages';
import ScrollArea from 'react-scrollbar'
import { formatDate ,TradeFormatDate} from '../../utils';
import { fetchMarketList,fetchEntrustList,setEntrustTransCoin } from '../../redux/modules/entrust'
import FileSaver from 'file-saver';
import Select from 'react-select';
import { fetchOrderHistory } from '../../redux/modules/orderhistory'
import {formatURL} from '../../utils/index'
const BigNumber = require('big.js')
import '../../assets/css/trust.css'
import '../../assets/css/table.less'
import '../trade/entrustrecord/entrustrecord.css'
import { doSaveConditionList } from '../../redux/modules/entrustcd'


const dot = (color = '#ccc') => ({
    alignItems: 'center',
    display: 'flex',
  
    ':before': {
      backgroundColor: color,
      borderRadius: 10,
      content: ' ',
      display: 'block',
      marginRight: 8,
      height: 10,
      width: 10,
    },
  });
 
//   export default {
//     name: 'Index',
//     data () {
//   return {
//     fullscreenLoading: false, // 加载中
//     imFile: '', // 导入文件el
//     outFile: '',  // 导出文件el
//     errorDialog: false, // 错误信息弹窗
//     errorMsg: '', // 错误信息内容
//   }
  
class EntrustList extends React.Component{
    constructor(props){
        super(props)

        const { marketVal = 'btc', legVal = 'USDT', type = '-1', includeCancel = true, timeType = 0 } = this.props.entrustcd.list

        this.state = {
            marketVal: marketVal,
            marketOption:[],
            legVal: legVal,
            type: type,
            includeCancel: includeCancel,
            timeType: timeType,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZETHIRTY,//PAGESIZETHIRTY
            detailIndex:PAGEINDEX,
            detailSize:PAGESIZEFIVE,
            tableList:[],
            totalCount:0,
            currentMarket:'',
            id:'',
            coinName:'',
            markerName:'',
            tradeTypes:'',
            averagePrice:'',
            completeTotalMoney:'',
            completeNumber:'',
            numbers:'',
            exportStr:'',
            exportData:[],
            selectItem: {},
            coinList : [],
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChangeTime = this.handleChangeTime.bind(this)
        this.formatHistoryDetail = this.formatHistoryDetail.bind(this)
        this.moadlDetail = this.moadlDetail.bind(this)
        this.formatMoadlDetail = this.formatMoadlDetail.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.currentPageClick = this.currentPageClick.bind(this)
        this.detailPageClick = this.detailPageClick.bind(this)
        this.detailRefresh = this.detailRefresh.bind(this)
        this.fomartexportData = this.fomartexportData.bind(this)
        this.getCode = this.getCode.bind(this)
        this.getCodeLeg = this.getCodeLeg.bind(this)
        this.getCodeType = this.getCodeType.bind(this)
        this.conditionList = this.props.entrustcd.list
    }

    setCd(key, data){
        this.conditionList[key] = data;
        this.props.doSaveConditionList(this.conditionList);
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
                const numIncludeCancel = includeCancel?0:1
                this.setMarketCoin()
                this.props.fetchOrderHistory(curMarket,numIncludeCancel,timeType,type,pageIndex,pageSize)
                
            })
        })
        
        // console.log(this.props.orderHistpry.datas)

        // 判断国际化
        let localeCookie = cookie.get(COOKIE_LAN);
        let exportStr = ''
        switch(localeCookie) {
            case 'cn':
                exportStr = '交易,委托价格,委托数量,成交均价,成交数量,成交金额,状态,日期';
                 break;
            case 'en':
                 exportStr = 'Exchange,Order.Price,Order.Amount,Avg.Price,Finished,Total Price,Status,Date';
                break;
            case 'hk':
                exportStr = '交易,委托價格,委托數量,成交均價,成交數量,成交金額,狀態,日期';
                break;
            case 'jp':
                exportStr = '取引,注文価格,注文数,成約平均価格,成約数,成約金額,ステータス,日付';
                break;
            case 'kr':
                exportStr = '거래,주문가격,주문수량,거래 평균가격,거래수량,거래액,상태,날짜';
                break;
            default:
                exportStr = '交易,委托价格,委托数量,成交均价,成交数量,成交金额,状态,日期';
        }
        this.setState({exportStr})
    }


    //遍历市场币种 option
    setMarketCoin(cb){
        const { legVal,marketVal } = this.state
        let marketOption = [];
        const coinList = this.props.coin.data[legVal.toUpperCase()]||[]
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
        const { count } = this.props.orderHistpry.datas
        const user = cookie.get(COOKIE_UID)
        const zlogin = cookie.get('zloginStatus')
        if(count>pageSize&&user&&zlogin!=4&&values !== 'page'){
            this.refs.pages.resetPage();
        }
        const market = (marketVal+'_'+legVal).toLowerCase()
        const numIncludeCancel = includeCancel?0:1  
        this.props.setEntrustTransCoin(market) 
        this.props.fetchOrderHistory(market,numIncludeCancel,timeType,type,pageIndex,pageSize)
        // console.log(this.formatHistoryDetail(this.props.orderHistpry.datas))
    }

    handleInputChange() {
        this.setCd('includeCancel', !this.state.includeCancel);
        this.setState({
            includeCancel:!this.state.includeCancel,
            pageIndex:PAGEINDEX
        },() => this.requestTable())
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
                this.resetUrl(item.val,1)
                this.setMarketCoin(()=>{this.requestTable()})
        })
    }
    getCodeType(item = {}){
        this.setCd('type', item.val);
        this.setState({
            type: item.val,
            pageIndex:PAGEINDEX
        },() => this.requestTable());
    }
    // hideCancel(){
       
    // }
    //市场 select
    // handleChangeLeg(selectedOption){
    //     this.setState({
    //         legVal:selectedOption.value,
    //         pageIndex:PAGEINDEX
    //     },() => this.setMarketCoin(this.props,() => this.requestTable()))
    // }
    //类型 select
    // handleChangeType(selectedOption){
    //     this.setState({
    //         type:selectedOption.value,
    //         pageIndex:PAGEINDEX
    //     },() => this.requestTable())
    // }
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
    detailPageClick(value){
        this.setState({
            detailIndex:value
        },() => this.detailRefresh())
    }
    //格式化数据
    formatHistoryDetail(result){
        const {marketsConf} = this.props;
        const { marketVal,legVal } = this.state
        let currentMarket =this.props.listCoin?this.props.listCoin:(marketVal+"_"+legVal).toLowerCase()
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
            } catch(e){

            }
            let numberBixDian = marketsConf[currentMarket].numberBixDian;
            BigNumber.RM = 0;
            
            for(let i=0;i<result.length;i++){
                record[i] = {};
                record[i].date = TradeFormatDate(new Date(result[i].date));
                // record[i].typeRes = result[i].type == 1? "买入" : "卖出";
                record[i].tradeTypes = result[i].type;
                record[i].price = new BigNumber(result[i].price).toFixed(exchangeBixDian) +' '+legVal;
                record[i].amount = new BigNumber(result[i].amount).toFixed(numberBixDian);
                record[i].averagePrice = result[i].completeNumber >0 ?new BigNumber(result[i].completeTotalMoney).div(new BigNumber(result[i].completeNumber)).toFixed(exchangeBixDian): "--";
                record[i].completeNumber = result[i].completeNumber>0?new BigNumber(result[i].completeNumber).toFixed(numberBixDian):"--";
                record[i].completeTotalMoney = result[i].completeTotalMoney>0?new BigNumber(result[i].completeTotalMoney).toFixed(exchangeBixDian)+" "+legVal:"--";
                record[i].statusRes = result[i].status ==1? <FormattedMessage id="CanceledEntrust"/> : <FormattedMessage id="CompletedEntrust"/>;
                record[i].status = result[i].status;
                record[i].entrustId = result[i].entrustId;
            }
        }
        return record;
    }
    fomartexportData(data){
        const {marketVal,legVal} = this.state
        let chartName = '';
        let languaged = cookie.get(COOKIE_LAN);
        let sdata = []
        let localData =TradeFormatDate(new Date(), this.dateFormat).replace(/[\|\,|\:|\_|\ ]/g,'').replace(/\//g,'');
        if(data !==''){
            data.forEach((item,index)=>{
                    let ddata = []
                    let tradeTypesname = '';
                    let statusname = '';
                    if(languaged == 'cn'){
                        tradeTypesname = item['tradeTypes'] ==1?'买入':'卖出'
                        statusname = item['status'] ==1?'已撤销':'已完成'
                        chartName = localData + '委托记录'
                    }else if (languaged == 'en'){
                        tradeTypesname = item['tradeTypes'] ==1?'Buy':'Sell'
                        statusname = item['status'] ==1?'Cancelled':'Filled'
                        chartName = localData + 'order history'
                    }else if(languaged == 'hk'){
                        tradeTypesname = item['tradeTypes'] ==1?'買入':'賣出'
                        statusname = item['status'] ==1?'已撤銷':'已完成'
                        chartName = localData + '委托記錄'
                    }else if (languaged == 'jp'){
                        tradeTypesname = item['tradeTypes'] ==1?'購入':'販売'
                        statusname = item['status'] ==1?'キャンセル済':'完成済'
                        chartName = localData + '注文記録'
                    }else if (languaged == 'kr'){
                        tradeTypesname = item['tradeTypes'] ==1?'매수':'매도'
                        statusname = item['status'] ==1?'취소':'완성'
                        chartName = localData + '주문 내역'
                    }
                    else{
                        tradeTypesname = item['tradeTypes'] ==1?'Buy':'Sell'
                        statusname = item['status'] ==1?'Cancelled':'Filled'
                        chartName = localData + 'order history'
                    }
                    ddata.push(tradeTypesname)
                    ddata.push(item['price'])
                    ddata.push(item['amount']=='--'?'--':item['amount']+marketVal)
                    ddata.push(item['averagePrice']=='--'?'--':item['averagePrice']+legVal)
                    ddata.push(item['completeNumber']=='--'?'--':item['completeNumber']+marketVal)
                    ddata.push(item['completeTotalMoney'])
                    ddata.push(statusname)
                    ddata.push(String(item['date']))
                    sdata.push(ddata)
               
            })
            this.setState({exportData:sdata})
          
            
        }
        let str =  this.state.exportStr;
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
    s2ab(s){ // 字符串转字符流
        if (typeof ArrayBuffer !== 'undefined') {
            var buf = new ArrayBuffer(s.length);
            var view = new Uint8Array(buf);
            for (var i = 0; i != s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
            return buf;
        } else {
            var buf = new Array(s.length);
            for (var i = 0; i != s.length; ++i) buf[i] = s.charCodeAt(i) & 0xFF;
            return buf;
        }
      }
    //历史委托ID详情
    formatMoadlDetail(result){
        const {marketsConf} = this.props;
        const { marketVal,legVal } = this.state
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
                Lister[i].dateString = TradeFormatDate(new Date(result[key][5]));
                Lister[i].price = new BigNumber(result[key][1]).toFixed(exchangeBixDian);
                Lister[i].total = new BigNumber(result[key][2]).toFixed(exchangeBixDian);
                i++
            }
        }
        return Lister;
    }

    //历史委托ID详情弹窗
    moadlDetail(id,tradeTypes,numbers,completeNumber,averagePrice,completeTotalMoney){
        const { marketVal,legVal } = this.state
        let currentMarket = (marketVal+"_"+legVal).toLowerCase()
        let coinNameArry,coinName,markerName;
        if(currentMarket){
            coinNameArry = currentMarket.split("_");
            coinName = coinNameArry[0].toUpperCase();
            markerName = coinNameArry[1].toUpperCase();
        }
        this.setState({currentMarket,id,coinName,markerName,tradeTypes,averagePrice,completeTotalMoney,completeNumber,numbers},()=>{
            this.detailRefresh(1);
        })
        
        
    }
    detailRefresh(flg){
        try{
        const {detailIndex,detailSize,currentMarket,id,coinName,markerName,tradeTypes,averagePrice,completeTotalMoney,completeNumber,numbers}=this.state
        // "/Record/getEntrustHistory?jsoncallback=&"+"market="+market+"&type="+type+"&includeCancel="+includeCancel+"&timeType="+timeType+"&pageNum="+pageNum+"&pageSize="+pageSize+"&_="+lastTime,{withCredentials:true})
        axios.get(DOMAIN_TRANS + "/Record/GetDetails-" + currentMarket + "-" +id + "?jsoncallback=&"+"page="+detailIndex,{withCredentials:true})
        .then(res => {
            let data = eval(res['data'])[0];
            let record = this.formatMoadlDetail(data.record);
            let count = data.count;
            let str =   <div className="bk-moadlDetail">
                            <div className="head ">
                                <h3> <FormattedMessage id="委托明细"/></h3>
                                <span className={tradeTypes==1?'bggreen ml10 tool_paik':'bgred ml10 tool_paik'}>{tradeTypes==1?<FormattedMessage id='buyit'/>:<FormattedMessage id='sellit'/>}</span>
                                <a className="right iconfont icon-guanbi-moren" onClick={() => this.modal.closeModal()}></a>
                            </div>
                            
                            <div className="bk-entrust">
                                <div className="bk-entrust-info">
                                    <ul >
                                        <li>
                                            {/* <h6></h6> */}
                                            <FormattedMessage id="averagePrice" />：
                                            <p>{averagePrice} {markerName}</p>
                                        </li>
                                        <li>
                                        <FormattedMessage id="委托数量/成交数量" />：
                                            <p>{numbers}/{completeNumber} （{coinName}）</p>
                                        </li>
                                        <li>
                                            <FormattedMessage id="completeTotalMoney" />：
                                            <p>{completeTotalMoney.replace(/[A-Z]/g,"")}{markerName}</p>
                                        </li>
                                    </ul>
                                </div>
                                <div className="tradeList tradeList_detail">
                                    <table >
                                        <thead>
                                            <tr>
                                                <th><FormattedMessage id="historyDetailDate" /></th>
                                                <th className="padding_lick"><FormattedMessage id="historyDetailEntrustAmount" /></th>
                                                <th className="padding_lick"><FormattedMessage id="completePrice" /></th>
                                                <th className="padding_lick"><FormattedMessage id="completeTotalMoney" /></th>
                                            </tr>
                                        </thead>
                                    </table>
                                       
                                        {/* <ScrollArea className="etrust_scroll" > */}

                                        <div className="etrust_lit" style={{maxHeight:'300px',overflowY:'auto'}}>
                                        <table> 
                                            <tbody>
                                                {
                                                    record.map((record, index)=>{
                                                        return(
                                                            <tr key={record.id} className="en-tr">
                                                                <td className="en-td">{record.dateString}</td>
                                                                <td className="en-td">
                                                                    {record.numberA}{record.numberB !== undefined ? '.'+record.numberB : ''} {coinName}
                                                                </td>
                                                                <td className="en-td">{record.price} {markerName}</td>
                                                                <td className="en-td">{record.total} {markerName}</td>
                                                            </tr>
                                                        )
                                                    })
                                                }
                                            </tbody>
                                            </table>
                                            </div>
                                        {/* </ScrollArea> */}
                                        
                                        {/* </div> */}
                                    {/* </div> */}
                                    
                                </div>
                                {/* {count>detailSize&&<div className="historyEntrustList-page tablist">
                                    <Pages 
                                        pageIndex={detailIndex} 
                                        pagesize={detailSize} 
                                        total={count}
                                        currentPageClick = { this.detailPageClick}
                                    />
                                </div>} */}
                            </div>
                            <div className="modal-foot">
                                <a className="btn ml10" onClick={() => this.modal.closeModal()}><FormattedMessage id="弹窗确定" /></a>
                            </div>
                             {/* <div className="btns_div">
                                <span className="btn close_alertBox" onClick={() => this.modal.closeModal()}><FormattedMessage  id="cancel" /></span>
                                <span className="btn submit" onClick={() => this.modal.closeModal()}>新增</span>
                             </div> */}
                        </div>;
            this.setState({Mstr:str}, () => {
                flg && this.modal.openModal();
            })
            
        });
        }catch(e){
            
        }
    }
    render(){
        
        const user = cookie.get(COOKIE_UID)
        const zlogin = cookie.get('zloginStatus')
        const {exportData,exportStr,marketVal,marketOption,legVal,type,includeCancel,timeType,pageIndex,pageSize,totalCount } = this.state
        const { count,records } = this.props.orderHistpry.datas
        const { isloaded,data } = this.props.coin
        const tableList = isloaded&&data[legVal.toUpperCase()].includes(marketVal.toUpperCase())&&this.formatHistoryDetail(records)
        return(
            <div className="mainer-phase2 select-new entrist-list bk_entrust">
                <div className="container2">
                    <div className="bbyh-weituoHeader">
                        <h2 className="entrust-title unChooseTitle">
                            <Link to={`${formatURL('/entrust/current')}?market=${marketVal.toLowerCase()}_${legVal.toLowerCase()}`}>
                                <FormattedMessage id="当前委托_w" />
                            </Link>
                        </h2>
                        <h2 className="entrust-title chooseTitle"><FormattedMessage id="ORDER-HISTORY" /></h2>
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
                        <div className="entrust-head-box left">
                            <div  className={`${includeCancel?"bg-white":""} checkboxitem`}>
                                {/* <input type="checkbox" name="includeCancel"  checked={includeCancel} /> */}
                                <i className={includeCancel?"iconfont icon-xuanze-yixuan":"iconfont icon-xuanze-weixuan "} onClick={this.handleInputChange}></i>
                                
                            </div>
                            <label htmlFor="undoTrade"><FormattedMessage id="entrust.text3" /></label>
                        </div>
                        
                        {tableList.length > 0&&user&&zlogin!=4?<ExportModal boxname={<FormattedMessage id="export.orderhistory" />} fomartexportData={()=>this.fomartexportData(tableList)}></ExportModal>: <div className="export-dis right"><FormattedMessage id="export.orderhistory" /></div>}
                    </div>
                    <div className="entrust-con bk-entrust bk_table">
                        <table className="table-entrust ">
                            <thead>
                                <tr>
                                    <th width="6%"><FormattedMessage id="EXCHANGE" /></th>
                                    <th width="11%" className="text-right"><FormattedMessage id="unitPrice" /></th>
                                   
                                    <th width="11%" className="text-right"><FormattedMessage id="entrustAmount" /></th>
                                    <th width="11%" className="text-right"><FormattedMessage id="averagePrice" /></th>
                                    <th width="11%" className="text-right"><FormattedMessage id="completeNumber" /></th>
                                    <th width="14%" className="text-right"><FormattedMessage id="成交金额" /></th>
                                    <th width="8%" className="text-center"><FormattedMessage id="Status" /></th>
                                    <th width="16%"  className="text-left"><FormattedMessage id="account.text9" /></th>
                                    <th width="8%" className="borright text-center"><FormattedMessage id="Action" /></th>
                                </tr>
                            </thead>
                           {user&&zlogin!=4?<tbody id="historyEntrustList">
                                {
                                !this.props.orderHistpry.isLoading&&this.props.orderHistpry.isLoaded?tableList.length > 0? tableList.map((item,index) => {
                                        return (
                                            <tr key={item.entrustId} className="border_left">
                                                <td  width="6%">{item.tradeTypes==1?<span className="green"><FormattedMessage id="buyit" /></span>:<span className="red"><FormattedMessage id="sellit" /></span>}</td>
                                                <td width="11%" className="text-right">{item.price}</td>
                                               
                                                <td width="11%" className="text-right">{item.amount} {item.amount=='--'?'':marketVal}</td>
                                                <td width="11%" className="text-right">{item.averagePrice} {item.averagePrice == '--'?'':legVal}</td>
                                                <td width="11%" className="text-right">{item.completeNumber} {item.completeNumber == '--'?'':marketVal}</td>
                                                <td width="14%" className="text-right">{item.completeTotalMoney}</td>
                                                <td width="8%" className="text-center" style={{padding:'6px 16px'}}> {item.statusRes}</td>
                                                <td width="16%" className="text-left">{item.date}</td>
                                                <td width="8%" className="text-center">{item.status==2?<a href="javascript:void(0)" onClick={this.moadlDetail.bind(this,item.entrustId,item.tradeTypes,item.amount,item.completeNumber,item.averagePrice,item.completeTotalMoney)}><FormattedMessage id="详细" /></a>:""}</td>
                                            </tr>
                                        )
                                    }):(
                                        <tr className="nodata border_left border_right border_bottom">
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
                            </tbody>
                        :(
                                    <p className="alert_under_table user_login top200">
                                        <i className="iconfont icon-tongchang-tishi norecord"></i> 
                                        &nbsp;<FormattedMessage id="haveNotSign"/>  <Link to='/bw/login'><FormattedMessage id="Login" /></Link>  <FormattedMessage id="or"/>  <Link to='/bw/signup'><FormattedMessage id="OpenAccount" /></Link> <FormattedMessage id="andTryAgain" />
                                    </p>
                                )}
                                </table>
                    </div>
                    {count>pageSize&&user&&zlogin!=4&&<div className="historyEntrustList-page tablist">
                        <Pages 
                            pageIndex={pageIndex} 
                            pagesize={pageSize} 
                            total={count}
                            ref="pages"
                            currentPageClick = { this.currentPageClick }
                        />
                    </div>}
                </div>
                <EntrustModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </EntrustModal>
            </div>
        )
    }
}

const mapStateToProps = (state,ownProps) => {
    return {
        entrustcd: state.entrustcd,
        coin:state.entrust.coin,
        orderHistpry:state.orderHistpry,
        marketsConf: state.marketsConf.marketsConfData,
        listCoin:state.entrust.transCoin,
        lang:state.language.locale
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        doSaveConditionList:(data) => {
            dispatch(doSaveConditionList(data))
        },
        fetchMarketList:(cb) => {
            dispatch(fetchMarketList()).then(cb)
        },
        fetchOrderHistory:(market,includeCancel,timeType,type,pageNum,pageSize)=>{
            let lastTime = +new Date;
            dispatch(fetchOrderHistory(market,includeCancel,timeType,type,pageNum,pageSize,lastTime))
        },
        setEntrustTransCoin:(params) => {
            dispatch(setEntrustTransCoin(params))
        }
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(EntrustList)



























