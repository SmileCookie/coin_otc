/**数据中心 》 统计报表 》 总账报表 》 资金变动表  */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH,TIMEFORMAT,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, DatePicker, Select, Table, Modal,message } from 'antd'
import { toThousands } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;

export default class StatementChangeOfCapital extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            accountType: [<Option key='0' value='0'>请选择</Option>, <Option key='1' value='1'>用户帐户</Option>, <Option key='2' value='2'>刷量帐户</Option>],
            accountTypeVal: "0",
            moneyType: [<Option key='0' value='0'>请选择</Option>],
            fundsType: "2",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: '20',
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
            sortType: "",
            totalMoney: "",
            withdrawalFeeMin: "",
            withdrawalFeeMax: "",
            futuresTradingFeeMin: "",
            futuresTradingFeeMax: "",
            currencyTradingFeeMin: "",
            currencyTradingFeeMax: "",
            insuranceFundMin: "",
            insuranceFundMax: "",
            walletBalance: "",
            futuresBalance: "",
            currencyBalance: "",
            otcBalance: "",
            topUp: "",
            withdrawDeposit: "",
            futuresFee: "",
            currencyFee: "",
            otcFee: "",
            withdrawDepositFee: "",
            unrealizedProfitAndLoss: "",
            insuranceFundReserve: "",
            otcfeesmin: "",
            otcfeesmax: "",
            retainedsum: "",
            otcFeessum:'',
            otcBalancesum:''
        }
        this.clickHide = this.clickHide.bind(this);
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.requestList = this.requestList.bind(this);
        this.requestTable = this.requestTable.bind(this);
        this.handleChangeType = this.handleChangeType.bind(this);
        this.onResetState = this.onResetState.bind(this);
        this.changPageNum = this.changPageNum.bind(this);
        this.onShowSizeChange = this.onShowSizeChange.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.requestSort = this.requestSort.bind(this);
        this.handleChangeTable = this.handleChangeTable.bind(this);
        this.handleAccountChangeType = this.handleAccountChangeType.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
    }
    componentDidMount() {
        this.requestTable()
        this.requestList()
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    handleChangeTable(pagination, filters, sorter) {

        console.log(sorter);
        // sorter.field
        // sorter.order
        // this.setState({
        //     sortType: sorter.order
        // }, () => this.requestTable());
    }
    handleInputChange(e) {
        const target = e.target;
        const value = target.value;
        const name = target.name
        let json = new Object();
        json[name] = value;
        this.setState(json);
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            begin: dateString[0],
            end: dateString[1],
            time: date
        })
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestTable(page,pageSize))

    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requestTable(current,size))
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
            fundsType: value
        })
    }
    handleAccountChangeType(value) {
        this.setState({
            accountTypeVal: value
        })
    }
    onResetState() {
        this.setState({
            accountTypeVal: "0",
            fundsType: "2",
            time: null,
            withdrawalFeeMin: "",
            withdrawalFeeMax: "",
            futuresTradingFeeMin: "",
            futuresTradingFeeMax: "",
            currencyTradingFeeMin: "",
            currencyTradingFeeMax: "",
            insuranceFundMin: "",
            insuranceFundMax: "",
            otcfeesmin: "",
            otcfeesmax: "",
            time:[],
            begin:'',
            end:''
        })
    }
    requestSort(type) {
        this.setState({ sortType: type }, () => this.requestTable())
    }
    requestList() {
        axios.get(DOMAIN_VIP + '/common/queryAttr').then(res => {
            const result = res.data;
            let moneyTypeArr = this.state.moneyType;
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    moneyTypeArr.push(<Option key={result.data[i].paracode} value={result.data[i].paracode}>{result.data[i].paravalue}</Option>)
                }
                this.setState({
                    moneyType: moneyTypeArr
                })
            }
        })
    }
    requestTable(currIndex, currSize) {
        const {
            fundsType,
            pageIndex,
            pageSize,
            sortType,
            withdrawalFeeMin,
            withdrawalFeeMax,
            futuresTradingFeeMin,
            futuresTradingFeeMax,
            currencyTradingFeeMin,
            currencyTradingFeeMax,
            insuranceFundMin,
            insuranceFundMax,
            accountTypeVal,
            otcfeesmin,
            otcfeesmax,
            begin,
            end,
        } = this.state;
        const parameter = {
            fundstype: fundsType,
            begintime: begin,
            endtime: end,
            sortType,
            walletfeesmin: withdrawalFeeMin,
            walletfeesmax: withdrawalFeeMax,
            // futuresfeesmin: futuresTradingFeeMin,
            // futuresfeesmax: futuresTradingFeeMax,
            feesmin: currencyTradingFeeMin,
            feesmax: currencyTradingFeeMax,
            // insurancemin: insuranceFundMin,
            // insurancemax: insuranceFundMax,
            // otcfeesmin,
            // otcfeesmax,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        };
        axios.post(DOMAIN_VIP + "/moneyChange/query", qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {

                // Big.RM = 0;
                // let tableList = result.data.list;

                // tableList.map((item, index) => {
                //     item.index = (result.data.currPage-1)*result.data.pageSize+index+1;
                //     item.key = item.id;
                //     item.checktime = moment(item.checktime).format('YYYY-MM-DD');
                // })
                let tableList = result.data.list;
                for(let i=0;i<tableList.length;i++){
                    tableList[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableList[i].key = tableList[i].id
                }
                this.setState({
                    tableList,
                    pageSize: result.data.pageSize,
                    pageTotal: result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
        axios.post(DOMAIN_VIP + "/moneyChange/sum", qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {

                Big.RM = 0;
                let tableList = result.data[0]&&result.data[0];
                tableList&&this.setState({
                    walletBalance: tableList.walletbalancesum,
                    futuresBalance: tableList.futuresbalancesum,
                    currencyBalance: tableList.balancesum,
                    otcBalance: tableList.otcbalancesum,
                    topUp: tableList.rechargesum,
                    withdrawDeposit: tableList.withdrawalsum,
                    futuresFee: tableList.futuresfeessum,
                    currencyFee: tableList.feessum,
                    otcFee: tableList.otcfeessum,
                    withdrawDepositFee: tableList.walletfeessum,
                    unrealizedProfitAndLoss: tableList.unrealizedsum,
                    insuranceFundReserve: tableList.insurancesum,
                    retainedsum: tableList.retainedsum,
                    otcBalancesum:tableList.otcBalancesum,
                    otcFeessum:tableList.otcFeessum,
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCancel() {
        this.setState({ visible: false })
    }
    footer() {

    }
    render() {
        Big.RM = 0;
        const { showHide, accountType, accountTypeVal, moneyType, retainedsum, fundsType, tableList, pageIndex, pageSize, pageTotal, walletBalance, futuresBalance, currencyBalance, otcBalance, topUp, withdrawDeposit, futuresFee, currencyFee, otcFee, withdrawDepositFee, unrealizedProfitAndLoss, insuranceFundReserve } = this.state
        const { time, withdrawalFeeMin, otcfeesmin, otcfeesmax, withdrawalFeeMax, futuresTradingFeeMin, futuresTradingFeeMax, currencyTradingFeeMin, currencyTradingFeeMax, insuranceFundMin, insuranceFundMax,
            otcFeessum,otcBalancesum } = this.state;
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 总账报表 > 资金变动表 
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList  fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">提现手续费：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="withdrawalFeeMin" value={withdrawalFeeMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="withdrawalFeeMax" value={withdrawalFeeMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label control-label-lg">期货手续费：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="futuresTradingFeeMin" value={futuresTradingFeeMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="futuresTradingFeeMax" value={futuresTradingFeeMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label control-label-lg">币币手续费：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="currencyTradingFeeMin" value={currencyTradingFeeMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="currencyTradingFeeMax" value={currencyTradingFeeMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label control-label-lg">OTC手续费：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="otcfeesmin" value={otcfeesmin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="otcfeesmax" value={otcfeesmax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保险基金：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="insuranceFundMin" value={insuranceFundMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="insuranceFundMax" value={insuranceFundMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">帐户类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={accountTypeVal} style={{ width: SELECTWIDTH }} onChange={this.handleAccountChangeType} >
                                                {accountType}
                                            </Select>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">报表日期：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table style={{margin:'0'}} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{textAlign:'left'}} colSpan="17" className="column-title">
                                                钱包余额：{toThousands(walletBalance,true)}，
                                                {/* 期货余额：{toThousands(futuresBalance,true)}， */}
                                                币币余额：{toThousands(currencyBalance,true)}，
                                                法币余额：{toThousands(otcBalancesum,true)}，
                                                充值：{toThousands(topUp,true)}，
                                                提现：{toThousands(withdrawDeposit,true)}，
                                                {/* 期货手续费：{toThousands(futuresFee,true)}， */}
                                                币币手续费：{ toThousands(currencyFee,true)}，
                                                法币交易手续费：{toThousands(otcFeessum,true)}，
                                                提现手续费：{toThousands(withdrawDepositFee,true)}，
                                                {/* 未实现盈亏：{toThousands(unrealizedProfitAndLoss,true)}，
                                                保险基金：{toThousands(insuranceFundReserve,true)}，
                                                保险留存：{toThousands(retainedsum,true)} */}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table
                                        dataSource={tableList}
                                        // scroll={{ x: 1970 }}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{emptyText:'暂无数据'}}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            showSizeChanger: true,
                                            showQuickJumper: true,
                                            pageSizeOptions:PAGRSIZE_OPTIONS20,
                                            defaultPageSize:PAGESIZE,
                                            
                                        }}>

                                        <Column title='序号' dataIndex='index' key='index'  />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                                        <Column title='用户钱包余额' dataIndex='walletbalance' key='walletbalance' className="moneyGreen" sorter="true"  render={(text)=>toThousands(text,true)} />
                                        {/* <Column title='期货帐户余额' dataIndex='futuresbalance' key='futuresbalance' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} /> */}
                                        <Column title='币币帐户余额' dataIndex='balance' key='balance' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='法币帐户余额' dataIndex='otcBalance' key='otcbalance' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='充值' dataIndex='recharge' key='recharge' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='提现' dataIndex='withdrawal' key='withdrawal' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        {/* <Column title='期货交易手续费' dataIndex='futuresfees' key='futuresfees' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} /> */}
                                        <Column title='币币交易手续费' dataIndex='fees' key='fees' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='法币交易手续费' dataIndex='otcFees' key='otcfees' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='提现手续费' dataIndex='walletfees' key='walletfees' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        {/* <Column title='未实现盈亏' dataIndex='unrealized' key='unrealized' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='保险基金' dataIndex='insurance' key='insurance' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='保险留存' dataIndex='字段待定' key='字段待定' className="moneyGreen" sorter="true" render={(text)=>'字段待定！！'} /> */}
                                        <Column title='报表日期' dataIndex='checktime' key='checktime' render={(text)=>text?moment(text).format(TIMEFORMAT):'--'} />
                                    </Table>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        )

    }
}