import React from 'react';
import axios from 'axios'
import { FormattedMessage, injectIntl } from 'react-intl';
import { connect } from 'react-redux'
import { Link, browserHistory } from 'react-router';
import { optPop } from '../../../utils';
import EntrustModal from '../../../components/entrustBox';
import { fetchManageInfo, fetchManageRecord, getAssetsTotal } from '../../../redux/modules/account'
import { fetchWalletInfo } from '../../../redux/modules/wallet'
import { setCoinListCurrentCoin } from '../../../redux/modules/deposit'
import { jumpWithdraw } from '../../../redux/modules/withdraw'
import Form from '../../../decorator/form';
import Transfer from '../transfer'
import { mobileFontSize, isMobile, cutDigits } from '../../../utils'
import { COIN_KEEP_POINT, FETCH_ACCOUNT_INTERVAL, DOMAIN_VIP,ERRORCONFIG } from '../../../conf'
const BigNumber = require('big.js')
import '../../../assets/css/check.less'
import './currency.less'
import {formatURL} from '../../../utils/index'
// 保险的录入弹窗
import InsuranceForm from '../../../components/insurance';

const tableHeader = [
    {
        name: <FormattedMessage id="币种" />,
        sortName: 'propTag'
    },
    {
        name: <FormattedMessage id="全称" />,
        sortName: 'coinFullName'
    }, {
        name: <FormattedMessage id="总额" />,
        sortName: 'total6'
    }, {
        name: <FormattedMessage id="可用余额" />,
        sortName: 'balance6'
    }, {
        name: <FormattedMessage id="冻结资金" />,
        sortName: 'freeze6'
    }, {
        name: <FormattedMessage id="估值" />,
        sortName: 'valuation'
    }, {
        name: <FormattedMessage id="操作" />,
        sortName: ''
    }
]
@Form
class Balance extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            filterVal: '',
            isHideZerobalance: false,
            clickNum: 0,
            sortName: '',
            coinList: [],
            availableDownload: 0,
            downloadLimit: 0,
            btnStus: 0,
            bordeBlue: false,
            Mstr: '',
            fromselectCode: 1,
            selectItem: {},
            toselectCode: 1,
            totalMoney:0,
            inputEvent:false,
            dataError:false,
            opend: 0,
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.formatFundsDetail = this.formatFundsDetail.bind(this)
        this.touchSortReacrd = this.touchSortReacrd.bind(this)
        this.sortRecord = this.sortRecord.bind(this)
        this.setCoinList = this.setCoinList.bind(this)
        this.filterRecord = this.filterRecord.bind(this)
        this.clearFilterVal = this.clearFilterVal.bind(this)
        this.changeFocus = this.changeFocus.bind(this)
        this.changeBlur = this.changeBlur.bind(this)
        this.setZerobalance = this.setZerobalance.bind(this)
        this.detailRefresh = this.detailRefresh.bind(this)
        this.checkoutNums = this.checkoutNums.bind(this)

        this.showBx = this.showBx.bind(this)
    }
    showBx(){
        this.setState({
            opend: ++this.state.opend,
        })
    }

    componentWillReceiveProps(nextProps){
        const { coinList, moneylogo , moneyrate } = nextProps
        const exchangeRate = moneyrate?moneyrate[moneylogo.name]:1;
        if(coinList.isloaded&&!coinList.isloading&&coinList.data){
            let totalMoney = 0;
            for(let k in coinList.data){
                var usdExchange = coinList.data[k].usdExchange != "--" && exchangeRate ? coinList.data[k].usdExchange * coinList.data[k].total * exchangeRate : 0;
                totalMoney = new BigNumber(usdExchange).plus(totalMoney).toFixed(2);
            }
            this.setState({
                totalMoney
            })
        }
    }

    componentDidMount() {

        this.props.fetchManageInfo();
        this.interval = setInterval(() => {
            this.props.fetchManageInfo();
        }, FETCH_ACCOUNT_INTERVAL)
        window.addEventListener('keypress', (e) => {
            if (e.target.name == 'filterVal') {
                this.setCoinList()
            }
        })
        axios.get(DOMAIN_VIP + "/manage/account/indexJson").then(res => {
            const result = res.data
            this.setState({
                availableDownload: result.datas.availableDownload,
                downloadLimit: result.datas.downloadLimit,
                authResult: result.datas.authResult
            })
        })
        // console.log(this.props.coinList)
        //this.baseFormatFundsDetail(this.props.coinList.data)
    }
    componentWillUnmount() {
        clearInterval(this.interval)
        window.removeEventListener('keypress', (e) => {
            if (e.target.name == 'filterVal') {
                this.setCoinList()
            }
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value,
        },() => this.setCoinList());      
        if (name == 'filterVal' && value) {
            this.setState(preState => {
                if (preState.btnStus != 1) {
                    return { btnStus: 1}
                }
            })
        } else {
            this.setState(preState => {
                if (preState.btnStus != 0) {
                    return { btnStus: 0}
                }
            })
        }
    }
    setZerobalance() {
        this.setState({
            isHideZerobalance: !this.state.isHideZerobalance
        })
    }

    //格式化数据
    setCoinList() {
        let coinList = this.formatFundsDetail(this.props.coinList.data);
        this.setState({
            coinList: coinList
        })
    }

    //格式化数据
    formatFundsDetail(result) {
        //console.log(result)
        BigNumber.RM = 0;
        let record = [];
        try{
            let i = 0;
            let exchangeRate = this.props.moneyrate[this.props.moneylogo.name]
            if (result) {
                for (let key in result) {
                    //throw new Error("参数必须是数字")
                    var funds = result[key];
                    var balance = funds.balance;
                    var freeze = funds.freeze;
                    var fundsType = funds.fundsType;
                    var unitTag = funds.unitTag;
                    var propTag = funds.propTag;
                    var total = funds.total;
                    var coinFullName = funds.coinFullNameEn;
                    var canCharge = funds.canCharge;
                    var canWithdraw = funds.canWithdraw;
                    var eventFreez = funds.eventFreez;
                    var usdExchange = funds.usdExchange != "--" && exchangeRate ? funds.usdExchange * total * exchangeRate : 0;
                    var imgUrl = funds.imgUrl;
                    var display =  funds.display

                    record[i] = {};
                    record[i].propTag = propTag;
                    record[i].coinFullName = coinFullName;
                    record[i].stag = propTag.toLowerCase();
                    record[i].balance6 = new BigNumber(balance).toFixed(8) //可用余额
                    record[i].freeze6 = new BigNumber(freeze).toFixed(8);  //冻结资金
                    record[i].total6 = new BigNumber(total).toFixed(8);   //总额
                    record[i].canCharge = canCharge
                    record[i].canWithdraw = canWithdraw
                    record[i].valuation = new BigNumber(usdExchange).toFixed(2);
                    record[i].eventFreez = eventFreez;
                    record[i].imgUrl = imgUrl;
                    record[i].display = display;
                    i++;
                }
            }
            record = this.filterRecord(record)
            record = this.sortRecord(record)
        } catch(e){

        }
        return record;
    }
    //点击 th 事件
    touchSortReacrd(name) {
        let { clickNum, sortName} = this.state
        // console.log(this.formatFundsDetail(this.props.coinList.data))
        // console.log(name)
        let isSort = this.checkoutNums(name);
        if(isSort){
            return
        }
        let newClickNum,
            targetName = name
        if (targetName == sortName || !sortName) {
            newClickNum = ++clickNum
            if (newClickNum > 2) {
                newClickNum = 0
            }
        } else {
            newClickNum = 1
        }
        this.setState({
            sortName: targetName,
            clickNum: newClickNum
        })
    }
    //检测数据是否为空
    checkoutNums(name){
        let reg = /total6|balance6|freeze6|valuation/
        let isCheckName = reg.test(name);
        let data = this.formatFundsDetail(this.props.coinList.data);
        console.log(data)
        
       if(isCheckName){
           //let _isNum;
            for(let i=0;i<data.length;i++){
               
                if(parseFloat(data[i][name])){
                     return false
                }
            }
            return true
       }else{
           return false
       }
    }
    //点击 th 排序
    sortRecord(data) {
        const { clickNum, sortName } = this.state
        let sortData = [];
        let clickSortName = sortName;
        let sortABC = function (a, b) {
            return a[clickSortName].toLowerCase() < b[clickSortName].toLowerCase() ? -1 : 1;
        }
        let sortNumber2 = function (a, b) {
            return a[clickSortName] - b[clickSortName];
        }
        if (clickSortName && clickNum !== 0) {
            if (clickNum == 2) {
                if (sortName == 'propTag' || sortName == 'coinFullName') {
                    sortData = data.sort(sortABC)
                } else {
                    sortData = data.sort(sortNumber2)
                }
            } else if (clickNum == 1) {
                if (sortName == 'propTag' || sortName == 'coinFullName') {
                    sortData = data.sort(sortABC).reverse();
                } else {
                    sortData = data.sort(sortNumber2).reverse()
                }
            }
            return sortData
        } else {
            return data;
        }
    }
    //搜索框边框
    changeFocus() {
        this.setState({
            bordeBlue: true
        })
    }
    changeBlur() {
        this.setState({
            bordeBlue: false
        })
    }

    //搜索框 过滤
    filterRecord(data) {
        var newData = data;
        const { filterVal, isHideZerobalance } = this.state

        if (filterVal) {
            let nameArr = data.filter(function (element, index, array) {
                let newfilterVal = filterVal.toUpperCase()
                return element.propTag.indexOf(newfilterVal) !== -1
            })
            let coinArr = data.filter(function (element, index, array) {
                let newfilterVal = filterVal.toLowerCase()
                return (element.coinFullName).toLowerCase().indexOf(newfilterVal) !== -1
            })
            let aaadate = Object.assign(nameArr, coinArr);
            let bbbdate = aaadate.filter(function (element, index, array) {
                return array.indexOf(element) == index
            })
            newData = bbbdate
        }
        if (isHideZerobalance) {
            newData = newData.filter(function (element, index, array) {
                return element.total6 > 0;
            })
        }
        return newData;
    }

    clearFilterVal() {
        this.setState({
            filterVal: '',
            btnStus: 0
        })
    }
    //划转详情
    moadlDetail(fundsType) {
        this.detailRefresh(fundsType)
        this.modal.openModal();
    }

    detailRefresh(fundsType) {
        this.setState({ Mstr: <Transfer closeModal={this.modal.closeModal} fromtype={2} totype={1} fundsType={fundsType} /> })
    }
    componentDidCatch(err,infor){
        if(window.ERRORCONFIG){
            this.setState({
                dataError:true
            })
            console.log(err,infor)
        }else{
            console.log(err,infor)
        }
    }

    render() {
        if(this.state.dataError){
            return (
                <div className="bk-assets">
                    <div className="iconfont icon-jiazai new-loading" ></div>
                </div>)
        }         
        const { formatMessage } = this.intl
        const { bordeBlue,totalMoney, filterVal, isHideZerobalance, sortName, clickNum, availableDownload, downloadLimit, authResult, btnStus, opend, } = this.state
        const { assets, money } = this.props
        let coinList = this.props.coinList.isloaded && this.formatFundsDetail(this.props.coinList.data)
        let totalAll = 0, rate = 1, totalBtc = 0;
        //console.log(coinList)
        //if (assets.total) {
        if(assets.total && totalMoney){
            //totalAll = assets.total.total_legal_tender;
            totalAll = totalMoney;
            totalBtc = assets.total.total_btc;
        }
        if (money.rate.exchangeRateUSD) {
            rate = money.rate.exchangeRateUSD[money.locale.name.toUpperCase()];
        }
        //console.log('okokokoookkokokkokookkook-============',coinList)
        return (
            <div className="bk-assets">
               <div className="cont-row">
               
                    <div className="bbyh-moneyHeader">
                        <h2 className="assets-title chooseTitle" onClick={() =>browserHistory.push(formatURL('/manage/account/currency'))}><FormattedMessage id="币币账户"/></h2>
                        <h2 className="assets-title unChooseTitle" onClick={() =>browserHistory.push(formatURL('/manage/account/fcurrency'))}><FormattedMessage id="法币账户"/></h2>
                        {true && <h2 className="assets-title unChooseTitle" onClick={() =>browserHistory.push(formatURL('/manage/account/lcurrency'))}><FormattedMessage id="理财账户"/></h2>}
                    </div>

                    <div className="assets-detail">
                    <div className="assets-detail-right">
                        <div className="assets-detail-value">
                            <b style={{"lineHeight":"20px"}}><FormattedMessage id="balance.text3" /></b>
                            <div className="val-con" style={{"lineHeight":"20px"}}>
                                <i id="cerrencyUnit">{this.props.moneylogo.logo}</i> <span id="cerrencyTotal">{totalAll}</span> / <em id="totalBtc">{totalBtc ? new BigNumber(totalBtc).toFixed(8) : '0.00000000'}</em> BTC
                            </div>
                            <div style={{lineHeight:'18px', display:'none'}} className="assets-detail-perfunds">
                                    {authResult == 0 && <Link to="/bw/mg/authenOne"><FormattedMessage id="balance.text4" /></Link>}

                                    <p><b><FormattedMessage id="balance.text5" /></b>{downloadLimit} BTC</p>
                                    <p className="mar10">
                                        <b><FormattedMessage id="balance.text6" /></b><span id="availableDownload">{availableDownload}</span> BTC
                                </p>
                            </div>
                        </div>

                    </div>
                    <div className="assets-detail-left left">
                        <div className={bordeBlue ? "input-box borde-blue" : 'input-box'}>
                            <input type="text" name="filterVal" value={filterVal} placeholder={formatMessage({ id: "币种搜索" })} onChange={this.handleInputChange} onFocus={this.changeFocus} onBlur={this.changeBlur} />
                            <button onClick={btnStus == 1 ? this.clearFilterVal : null} className={btnStus == 0 ? "iconfont icon-search-bizhong" : "iconfont icon-shanchu-moren"}></button>
                        </div>
                        <label htmlFor="hideCoin">
                            <div className={`${isHideZerobalance ? "bg-white" : ""} checkboxitem`}>
                                {/* <input type="checkbox" name="isHideZerobalance"  onChange={this.handleInputChange} />
                                    <i className="iconfont icon-xuanze-yixuan"></i> */}
                                <i className={isHideZerobalance ? "iconfont icon-xuanze-yixuan" : "iconfont icon-xuanze-weixuan "} onClick={this.setZerobalance}></i>
                            </div>
                            <FormattedMessage id="balance.text2" />
                        </label>

                    </div>
                    <div className="history_list_right">
                        <Link to="/bw/manage/account/fbchargeDownHistory?type=0"><div className="history_list"><FormattedMessage id="历史记录" /></div></Link>
                    </div>
                </div>
                <div className="data-sheets">
                    <table width="100%" className="norm-table table-account">
                        <thead id="tableSort">
                            <tr>
                                {
                                    tableHeader.map((item, index) => {
                                        return <th key={index} onClick={() => item.sortName && this.touchSortReacrd(item.sortName)}>
                                            <span className="more-box">
                                                {item.sortName == 'valuation' && <FormattedMessage id={this.props.moneylogo.name} />}
                                                {item.name}
                                                {item.sortName && <i className={sortName == item.sortName && clickNum == 1 ? "show" : sortName == item.sortName && clickNum == 2 ? "show more" : ""}></i>}
                                            </span>
                                        </th>
                                    })
                                }
                            </tr>
                        </thead>
                        <tbody id="fundsDetail" className="bgtbody bbyh-clist">
                            {   
                            
                                coinList.length > 0 && coinList.map((item, index) => {
                                    return (
                                        item.display?
                                        <tr key={item.stag} >
                                             <td className="ft">
                                                {/* <i className={`money-coin-sm ${item.stag}`}></i> */}
                                                {<span className="icx" style={{backgroundImage: "url("  + item.imgUrl + ")"}}></span>}
                                                <span>{item.propTag}</span>
                                            </td>
                                            <td className="text-center">{item.coinFullName}</td>
                                            <td className="text-right">{item.total6}</td>
                                            <td className="text-right">{item.balance6}</td>
                                            <td className="text-right item_1_3">
                                                {item.propTag == 'ABCDE' && item.eventFreez > 0 ?
                                                    <strong className="hover_text">
                                                        <div className="text_divcon">
                                                            <div className={"text_div tag " + this.props.lang}><FormattedMessage id="活动冻结资金，需完成" /><Link to="/bw/manage/auth/authentication"><FormattedMessage id="身份认证" /></Link><FormattedMessage id="后方可解冻" /></div>
                                                        </div>
                                                    </strong> : ''}
                                                {item.freeze6}
                                            </td>
                                            <td className="text-right sm">
                                                {item.valuation != 0 ? item.valuation : "--"}
                                            </td>
                                            <td className="text-center">
                                                {/*
                                                    item.canCharge ?
                                                        <Link to={`/bw/manage/account/charge?coint=${item.propTag}`} onClick={() => this.props.setCoinListCurrentCoin(item.propTag)} className="assets-link" target="_blank"><FormattedMessage id="充值" /></Link> :
                                                        <a href="javascript:void(0)" className="assets-link color-gray curDefault"><FormattedMessage id="暂停" /></a>
                                                */}
                                                {/*
                                                    item.canWithdraw ? <Link to={`/bw/manage/account/download?coint=${item.propTag}`} onClick={() => this.props.jumpWithdraw(item.propTag)} className="assets-link" target="_blank"><FormattedMessage id="提现" /></Link> :
                                                        <a href="javascript:void(0)" className="assets-link color-gray curDefault"><FormattedMessage id="暂停" /></a>
                                                */}
                                                <Link onClick={this.moadlDetail.bind(this, item.propTag)} className="assets-link" target="_blank">{formatMessage({ id: "划转" })}</Link>
                                                {
                                                    item.propTag.includes("VDS") ? 
                                                        <Link onClick={this.showBx} className="assets-link">{formatMessage({ id: "投保" })}</Link>
                                                    : null
                                                }
                                            </td>
                                        </tr>
                                        :
                                        null
                                    )
                                })
                            }
                        </tbody>
                    </table>
                </div>
                <EntrustModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </EntrustModal>
                <InsuranceForm open={opend} />
            </div>
        </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        coinList: state.account.detail,
        record: state.account.record,
        total: state.assets.total,
        lang: state.language.locale,
        moneylogo: state.money.locale,
        moneyrate: state.money.rate.exchangeRateUSD,
        assets: state.assets,
        money: state.money
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchManageInfo: (cb) => {
            dispatch(fetchManageInfo()).then(cb)
        },
        fetchManageRecord: () => {
            dispatch(fetchManageRecord())
        },
        jumpWithdraw: (type) => {
            dispatch(jumpWithdraw(type))
        },
        setCoinListCurrentCoin: (coin) => {
            dispatch(setCoinListCurrentCoin(coin))
        }
    };
};


export default connect(mapStateToProps, mapDispatchToProps)(Balance);















