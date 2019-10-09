import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,NUMBERPOINT,SELECTWIDTH} from '../../../conf'
import { Button,DatePicker,Select } from 'antd'
const Big = require('big.js')
const Option = Select.Option;

export default class WalletTradePlatformLedger extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            accountType:[<Option key='0' value='0'>请选择</Option>],
            fundsType:"0",
            begin:"",
            end:"",
            tableList:"",
            time:null,
            pageIndex:PAGEINDEX,
            pageSize:'50',
            pageTotal:0
        }
        this.clickHide = this.clickHide.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.requestList = this.requestList.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.onResetState = this.onResetState.bind(this)
    }
    
    componentDidMount(){
        this.requestTable()
        this.requestList()
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            begin:dateString+" 00:00:00",
            end:dateString+" 23:59:59",
            time:date
        })
       
    }
     //点击收起
     clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    handleChangeType(value) {
        this.setState({
            fundsType:value
        })
    }
    onResetState(){
        this.setState({
            fundsType:"0",
            begin:"",
            end:"",
            time:null,
        })
    }
    requestList(){
        axios.get(DOMAIN_VIP+'/common/queryAttr').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.accountType;
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                    accountTypeArr.push(<Option key={result.data[i].paracode} value={result.data[i].paracode}>{result.data[i].paravalue}</Option>)
                 }
                 this.setState({
                    accountType:accountTypeArr
                 }) 
            }
        })
    }
    requestTable(currIndex,currSize){
        const {fundsType,begin,end,pageIndex,pageSize,pageTotal} = this.state
        axios.post(DOMAIN_VIP+'/walletRecon/query',qs.stringify({
            begin,end,fundsType,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data,
                    pageTotal:result.data.totalCount
                })
            }   
        })
    }
    render(){
        Big.RM = 0;
        const {showHide,accountType,paraList,fundsType,tableList,time,pageIndex,pageSize,pageTotal} = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 钱包对账 > 钱包VS交易平台总账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div className="x_panel">
                            <div className="x_content">
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">资金类型：</label>
                                        <div className="col-sm-9">
                                        <Select value={fundsType}  style={{ width: SELECTWIDTH }} onChange={this.handleChangeType} >
                                        {accountType}     
                                        </Select>
                                            
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对账日期：</label>
                                        <div className="col-sm-8">
                                        <DatePicker 
                                        format="YYYY-MM-DD"
                                       onChange={this.onChangeCheckTime }
                                        value={time}
                                       />
                                        </div>
                                    </div>
                                </div>
                               
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                                <div className="col-md-12 col-sm-12 col-xs-12" style ={{color:"#3333CC"}}>
                                钱包金额（QA = 1-2=3+4+5+6+7）
                                </div>
                            </div>
                        </div>
                         }
                        <div className="x_panel">
                                <div className="x_content">
                                    <div className="table-responsive min_tableFixe">
                                        <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                                            <thead>
                                                <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title min_68px">资金类型</th>
                                                <th className="column-title">对账差额（JA-QA）</th>
                                                <th className="column-title min_68px">内部调帐差额</th>
                                                <th className="column-title">交易平台金额（JA）</th>
                                                <th className="column-title min_68px">外部调账差额</th> 
                                                <th className="column-title">钱包金额（QA=1-2）</th> 
                                                <th className="column-title">用户充值（1）</th> 
                                                <th className="column-title">用户提现（2）</th> 
                                                <th className="column-title">热冲发生额|余额（3）</th> 
                                                <th className="column-title">冷钱包发生额|余额（4）</th> 
                                                <th className="column-title">热提发生额|余额（5）</th> 
                                                <th className="column-title">网络费汇总（6）</th>  
                                                <th className="column-title">其他发生额（7）</th> 
                                                <th className="column-title">合约消耗汇总</th>               
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    const regexp=/(?:\.0*|(\.\d+?)0+)$/
                                                    let externaldiff = new Big(item.reconciliation.externaladjustmentpositive).minus(new Big(item.reconciliation.externaladjustmentnegative)).toFixed()
                                                    let internalad = new Big(item.reconciliation.internaladjustmentpositive).minus(new Big(item.reconciliation.internaladjustmentnegative)).toFixed()
                                                   //qa
                                                    let userRecharge = new Big(item.balanceResp.userRecharge)
                                                    let userWithdraw = new Big(item.balanceResp.userWithdraw)
                                                   //ja 
                                                   let transaction =  new Big(item.reconciliation.transactionPlatformAmount)
                                                    let chae =  transaction.minus(userRecharge).plus(userWithdraw).toFixed()
                                                    let hotToColdFee = new Big(item.balanceResp.hotToColdFee).plus(item.balanceResp.coldToOtherFee).plus(item.balanceResp.coldToHotFee).plus(item.balanceResp.withdrawFee).plus(item.balanceResp.hotWithdrawToOtherFee).plus(item.balanceResp.hotWithdrawToColdFee).toFixed()
                                                    let coldToOther = new Big(item.balanceResp.coldToOtherHappenedAmount).plus(item.balanceResp.hotWithdrawToOtherHappenedAmount).plus(item.balanceResp.otherToColdHappenedAmount).plus(item.balanceResp.otherToHotHappenedAmount).toFixed()
                                                    // let coldToOther = new Big(item.balanceResp.coldToOtherHappenedAmount).plus(item.balanceResp.hotWithdrawToOtherHappenedAmount).plus(item.balanceResp.otherToColdHappenedAmount).plus(item.balanceResp.otherToHotHappenedAmount).toFixed()
                                                    let contractAmount = new Big(item.balanceResp.coldToOtherHappenedAmount).plus(item.balanceResp.hotWithdrawToOtherHappenedAmount).minus(item.balanceResp.otherToColdHappenedAmount).minus(item.balanceResp.otherToHotHappenedAmount).toFixed()
                                                    return (
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.reconciliation.fundstypename}</td>
                                                            <td>{chae.replace(regexp,'$1')}</td>
                                                            <td>{internalad.replace(regexp,'$1')}</td>
                                                            <td>{transaction.toFixed().replace(regexp,'$1')}</td>
                                                            <td>{externaldiff.replace(regexp,'$1')}</td>
                                                            <td>{userRecharge.minus(userWithdraw).toFixed().replace(regexp,'$1')}</td>
                                                            <td>{userRecharge.toFixed().replace(regexp,'$1')}</td>
                                                            <td>{userWithdraw.toFixed().replace(regexp,'$1')}</td>
                                                            <td>{item.balanceResp.hotRechargeHappenedAmount.replace(regexp,'$1')}</td>
                                                            <td>{item.balanceResp.coldHappenedAmount.replace(regexp,'$1')}</td>
                                                            <td>{item.balanceResp.hotWithdrawHappenedAmount.replace(regexp,'$1')}</td>
                                                            <td>{hotToColdFee.replace(regexp,'$1')}</td>
                                                            <td>{coldToOther.replace(regexp,'$1')}</td>
                                                            <td>{contractAmount.replace(regexp,'$1')}</td>
                                                        </tr>
                                                    )
                                                }):
                                                <tr className="no-record"><td colSpan="14">暂无数据</td></tr>
                                            }
                                        </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>

                    </div>
                </div>
            </div>
        )
        
    }
}