import React from 'react';
import { Link,browserHistory } from 'react-router';
import { FormattedMessage, injectIntl } from 'react-intl';
import { COIN_KEEP_POINT,FETCH_ACCOUNT_INTERVAL } from '../../../conf'
const BigNumber = require('big.js')

export default class Bkassets extends React.Component{
    
    constructor(props){
        super(props);
        this.state = {
            coinList : null,
            clickNum:0,
            sortName:''
        }
        this.formatFundsDetail = this.formatFundsDetail.bind(this);
        this.jumpWithdrawLink = this.jumpWithdrawLink.bind(this)
        this.touchSortReacrd = this.touchSortReacrd.bind(this)
        this.sortRecord = this.sortRecord.bind(this)
        this.setCoinList = this.setCoinList.bind(this)
        this.filterRecord = this.filterRecord.bind(this)
    }

    componentDidMount(){
        this.props.fetchManageInfo(this.setCoinList);
        // this.interval = setInterval(()=>{
        //     this.props.fetchManageInfo();
        // },FETCH_ACCOUNT_INTERVAL)
        window.addEventListener('keypress',(e) => {
            if(e.target.name == 'filterVal'){
                this.setCoinList()
            }
        })
    }
    componentWillUnmount(){
        //   clearInterval(this.interval)
        window.removeEventListener('keypress',(e) => {
            if(e.target.name == 'filterVal'){
                this.setCoinList()
            }
        })
    }

    componentWillReceiveProps(nextProps) {
        if(nextProps.coinList.isloaded) {
            let coinList = this.formatFundsDetail(nextProps.coinList.data);
            this.setState({
                coinList: coinList
            })
        }
    }

    //格式化数据
    setCoinList(){
        let coinList = this.formatFundsDetail(this.props.coinList.data);
        this.setState({
            coinList: coinList
        })
    }

    //跳转
    jumpWithdrawLink(coin){
        if(coin !== "ABCDE" && coin !== "usdt" && coin !== "btg"){
            this.props.jumpWithdraw(coin);
            browserHistory.push("/bw/manage/account/download")
        }
    }

    //格式化数据
    formatFundsDetail(result){
        BigNumber.RM = 0;
        let record = [];
        try{
            let i = 0;
            let exchangeRate = this.props.moneyrate[this.props.moneylogo.name]
            if(result){
                for(let key in result){
                    var funds = result[key];
                    var balance =  funds.balance;
                    var freeze = funds.freeze;
                    var fundsType = funds.fundsType;
                    var unitTag = funds.unitTag;
                    var propTag = funds.propTag;
                    var total = funds.total;
                    var coinFullName = funds.coinFullNameEn;
                    var canCharge = funds.canCharge;
                    var canWithdraw = funds.canWithdraw;
                    var eventFreez = funds.eventFreez;
                    var usdExchange = funds.usdExchange!="--"&&exchangeRate?funds.usdExchange*total*exchangeRate:0;
                    
                    record[i] = {};
                    record[i].propTag = propTag;
                    record[i].coinFullName = coinFullName;
                    record[i].stag = propTag.toLowerCase();
                    record[i].balance6 = new BigNumber(balance).toFixed(8) //可用余额
                    record[i].freeze6 = new BigNumber(freeze).toFixed(8);  //冻结资金
                    record[i].total6 = new BigNumber(total).toFixed(8);   //总额
                    record[i].canCharge =canCharge
                    record[i].canWithdraw =canWithdraw
                    record[i].valuation = new BigNumber(usdExchange).toFixed(2);
                    record[i].eventFreez = eventFreez;
                    
                    i++;
                }
            }
            record = this.filterRecord(record)
            record = this.sortRecord(record)
        } catch(e){
            
        }
        return record;
    }
    //点击 th 时间
    touchSortReacrd(name){
        let { clickNum,sortName } = this.state
        let newClickNum,
            targetName = name
        if(targetName == sortName || !sortName){
            newClickNum = ++clickNum
            if(newClickNum > 2){
                newClickNum = 0
            }
        }else{
            newClickNum = 1
        }
        this.setState({
            sortName:targetName,
            clickNum:newClickNum
        },() => this.setCoinList())
    }
    //点击 th 排序
    sortRecord(data){
        const { clickNum,sortName } = this.state 
        let sortData = [];
        let clickSortName = sortName;
        let sortABC = function(a,b){
            return a[clickSortName] < b[clickSortName]?-1:1;
        }
        let sortNumber2 = function(a,b){
            return a[clickSortName] - b[clickSortName];
        }
        if(clickSortName&&clickNum!==0){
            if(clickNum==1){
                if(sortName == 'propTag' || sortName == 'coinFullName'){
                    sortData = data.sort(sortABC)
                }else{
                    sortData = data.sort(sortNumber2)
                }
            }else if(clickNum==2){
                if(sortName == 'propTag' || sortName == 'coinFullName'){
                    sortData = data.sort(sortABC).reverse();
                }else{
                    sortData = data.sort(sortNumber2).reverse()
                }
            }
            return sortData
        }else{
            return data;
        }
    }

    //搜索框 过滤
    filterRecord(data){
        var newData = data;
        if(this.props.filterVal){
            nameArr = data.filter(function(element, index, array){
                return element.propTag.indexOf(this.props.filterVal) !== -1
            })
            newData = nameArr;
        }
        if(this.props.isHideZerobalance){
            newData = newData.filter(function(element, index, array){
                return element.total6 > 0;
            })
        }
        return newData;
    }

    render(){
        const { sortName,clickNum } = this.state
        return (
                <div className="data-sheets">
                    <table width="100%" className="norm-table">
                        <thead id="tableSort">
                            <tr>
                                <th onClick={() => this.touchSortReacrd("propTag")}><span className="more-box">币种<i className={sortName=="propTag"&&clickNum==1?"show":sortName=="propTag"&&clickNum==2?"show more":""}></i></span></th>
                                <th onClick={() => this.touchSortReacrd("coinFullName")} className="text-center"><span className="more-box">全称<i></i></span></th>
                                <th onClick={() => this.touchSortReacrd("total")} className="text-right"><span className="more-box">总额<i></i></span></th>
                                <th onClick={() => this.touchSortReacrd("balance")} className="text-right"><span className="more-box">可用余额<i></i></span></th>
                                <th onClick={() => this.touchSortReacrd("freeze")} className="text-right"><span className="more-box">冻结资金<i></i></span></th>
                                <th onClick={() => this.touchSortReacrd("valuation")} className="text-right"><span className="more-box"><b id="valName"></b>估值<i></i></span></th>
                                <th className="text-center">操作</th>
                            </tr>
                        </thead>
                        <tbody id="fundsDetail">
                            {
                                this.state.coinList&&this.state.coinList.map((item,index)=> {
                                    return (
                                        <tr key={index}>
                                            <td>
                                                <i className={`money-coin-sm ${item.stag}`}></i>
                                                <span className="ftbold">{item.propTag}</span>
                                            </td>
                                            <td className="text-center">{item.coinFullName}</td>
                                            <td className="text-right">{item.total6}</td>
                                            <td className="text-right">{item.balance6}</td>
                                            <td className="text-right item_1_3">
                                                {item.propTag == 'ABCDE' && item.eventFreez > 0?
                                                    <strong className="hover_text">
                                                        <div className="text_divcon">
                                                            <div className="text_div tag ${lan}">活动冻结资金，需完成<a href="${vip_domain}/manage/auth/authentication">身份认证超链接</a>后方可解冻</div>
                                                        </div>
                                                    </strong>:''}
                                                {item.freeze6}
                                            </td>
                                            <td className="text-right">
                                                {
                                                    item.valuation != 0?item.valuation:"--"
                                                } 
                                            </td>
                                            <td className="text-center">
                                                {
                                                    item.canCharge?
                                                        <a href="${vip_domain}/manage/account/charge?coint=item.propTag" className="assets-link" target="_blank">充值</a>:
                                                        <a href="javascript:void(0)" className="assets-link color-gray curDefault">暂停</a>
                                                }
                                                {
                                                    item.canWithdraw?<a href="${vip_domain}/manage/account/download?coint=item.propTag" className="assets-link" target="_blank">提现</a>:
                                                    <a href="javascript:void(0)" className="assets-link color-gray curDefault">暂停</a>
                                                }
                                            </td>
                                        </tr>
                                    )
                                })
                            }
                        </tbody>
                    </table>
                </div>
            )
    }
}









