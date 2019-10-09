/**数据中心 》 统计报表 》 币币报表 》 币币总账表 */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH } from '../../../conf'
import { Button, DatePicker, Select, Table, Modal,message } from 'antd'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const Option = Select.Option;

export default class CurrencyTradingDetail extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            accountType: [<Option key='0' value='0'>请选择</Option>, <Option key='1' value='1'>用户帐户</Option>, <Option key='2' value='2'>刷量帐户</Option>],
            accountTypeVal: "0",
            moneyType: [<Option key='0' value='0'>请选择</Option>],
            fundsType: "0",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: '50',
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
            userId: "",
            userName: "",
            sortType: "",
            money: "",
            tradingFrozen: "",
            advertisingFrozen: "",
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
            fundsType: "0",
            time: null,
            userId: "",
            userName: "",
            withdrawalFeeMin: "",
            withdrawalFeeMax: "",
            futuresTradingFeeMin: "",
            futuresTradingFeeMax: "",
            currencyTradingFeeMin: "",
            currencyTradingFeeMax: "",
            insuranceFundMin: "",
            insuranceFundMax: "",
            begin: "",
            end: "",
            time:[]
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
            }else{
                message.warning(result.msg)
            }
        })
    }
    requestTable(currIndex, currSize) {
        const { fundsType, pageIndex, pageSize, userId, transactionNumber, transferAccounts, intoAccount, transferAmountStart, transferAmountEnd, userName, sortType } = this.state
        // withdrawalFeeMin, withdrawalFeeMax, futuresTradingFeeMin, futuresTradingFeeMax, currencyTradingFeeMin, currencyTradingFeeMax, insuranceFundMin, insuranceFundMax
        axios.post(DOMAIN_VIP + "/doubleCheck/query", qs.stringify({
            fundsType, userId, transactionNumber, transferAccounts, intoAccount, transferAmountStart, transferAmountEnd, userName, sortType,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {

                Big.RM = 0;
                result.data = [
                    {
                        fundstypeName:"BTC",
                        walletBalance: "13123213",
                        futuresBalance: "21321312321",
                        currencyBalance: "32132132132",
                        otcBalance: "132131231",
                        topUp: "23123123123",
                        withdrawDeposit: "21312312321",
                        futuresFee: "321321312",
                        currencyFee: "3213123213",
                        otcFee: "21321321321",
                        withdrawDepositFee: "3123123",
                        unrealizedProfitAndLoss: "213213213",
                        insuranceFundReserve: "2131231231231",
                        time:"1541994717"
                    },
                    {
                        fundstypeName:"GTC",
                        walletBalance: "13123213",
                        futuresBalance: "21321312321",
                        currencyBalance: "32132132132",
                        otcBalance: "132131231",
                        topUp: "23123123123",
                        withdrawDeposit: "21312312321",
                        futuresFee: "321321312",
                        currencyFee: "3213123213",
                        otcFee: "21321321321",
                        withdrawDepositFee: "3123123",
                        unrealizedProfitAndLoss: "213213213",
                        insuranceFundReserve: "2131231231231",
                        time:"1541994717"
                    }
                ]
                let demandData = {
                        fundstypeName:"",
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
                        time:""
                    }
                let tableList = [];
                if (result.data.length > 0) {
                    result.data.map((item, index) => {
                        let json = new Object();
                        json.key = index;
                        json.index = index + 1;
                        for (let key in demandData) {
                            demandData[key] = item[key]
                        }
                        for (let key in demandData) {
                            json[key] = item[key]
                            if(key == "time"){
                                json.time = moment(demandData[key]).format('YYYY-MM-DD');  
                                }
                        }
                        tableList.push(json);
                    });
                    this.setState({
                        tableList,
                        pageTotal: result.pageTotal,
                        walletBalance: result.walletBalance,
                        futuresBalance: result.futuresBalance,
                        currencyBalance: result.currencyBalance,
                        otcBalance: result.otcBalance,
                        topUp: result.topUp,
                        withdrawDeposit: result.withdrawDeposit,
                        futuresFee: result.futuresFee,
                        currencyFee: result.currencyFee,
                        otcFee: result.otcFee,
                        withdrawDepositFee: result.withdrawDepositFee,
                        unrealizedProfitAndLoss: result.unrealizedProfitAndLoss,
                        insuranceFundReserve: result.insuranceFundReserve,
                    })
                }
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
        const { showHide, moneyType, fundsType, tableList, pageIndex, pageSize, pageTotal, walletBalance, futuresBalance, currencyBalance, otcBalance, topUp, withdrawDeposit, futuresFee, currencyFee, otcFee, withdrawDepositFee, unrealizedProfitAndLoss, insuranceFundReserve } = this.state
        const { time, withdrawalFeeMin, withdrawalFeeMax, futuresTradingFeeMin, futuresTradingFeeMax, currencyTradingFeeMin, currencyTradingFeeMax, insuranceFundMin, insuranceFundMax } = this.state;
        let columns = [
            { title: '序号', dataIndex: 'index', key: 'index'  },
            { title: '资金类型', dataIndex: 'fundstypeName', key: 'fundstypeName'   },
            { title: '余额', dataIndex: 'walletBalance', key: 'walletBalance', sorter: true, className: "moneyGreen",  },
            { title: '转入', dataIndex: 'futuresBalance', key: 'futuresBalance', sorter: true, className: "moneyGreen", },
            { title: '转出', dataIndex: 'currencyBalance', key: 'currencyBalance', sorter: true, className: "moneyGreen", },
            { title: '交易佣金', dataIndex: 'withdrawDeposit', key: 'withdrawDeposit', className: "moneyGreen", },
            { title: '交易时间', dataIndex: 'time', key: 'time' },
        ];

        return (
            <div className="right-con">
                <div className="page-title">
                    {/* 此处待定 TODO */}
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">资金类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={fundsType} style={{ width: SELECTWIDTH }} onChange={this.handleChangeType} >
                                                {moneyType}
                                            </Select>

                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">余额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="withdrawalFeeMin" value={withdrawalFeeMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="withdrawalFeeMax" value={withdrawalFeeMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">转入金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="futuresTradingFeeMin" value={futuresTradingFeeMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="futuresTradingFeeMax" value={futuresTradingFeeMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">转出金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="currencyTradingFeeMin" value={currencyTradingFeeMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="currencyTradingFeeMax" value={currencyTradingFeeMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易佣金：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name="currencyTradingFeeMin" value={currencyTradingFeeMin} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name="currencyTradingFeeMax" value={currencyTradingFeeMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">时间筛选：</label>
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
                            <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 ant-tabs-nav" >
                                <div className="col-mg-2 col-lg-2 col-md-2 col-sm-2 col-xs-2">
                                    <div className="col-sm-12 center">转入</div>
                                    <div className="col-sm-12 ant-tabs-tab-active center">{futuresBalance ? futuresBalance : 0}</div>
                                </div>
                                <div className="col-mg-2 col-lg-2 col-md-2 col-sm-2 col-xs-2">
                                    <div className="col-sm-12 center">余额</div>
                                    <div className="col-sm-12 ant-tabs-tab-active center">{walletBalance ? walletBalance : 0}</div>
                                </div>
                                <div className="col-mg-2 col-lg-2 col-md-2 col-sm-2 col-xs-2">
                                    <div className="col-sm-12 center">转出</div>
                                    <div className="col-sm-12 ant-tabs-tab-active center">{currencyBalance ? currencyBalance : 0}</div>
                                </div>
                                <div className="col-mg-2 col-lg-2 col-md-2 col-sm-2 col-xs-2">
                                    <div className="col-sm-12 center">交易佣金</div>
                                    <div className="col-sm-12 ant-tabs-tab-active center">{otcBalance ? otcBalance : 0}</div>
                                </div>
                                <div className="col-mg-1 col-lg-1 col-md-1 col-sm-1 col-xs-1">
                                    <div className="col-sm-12 center">交易佣金</div>
                                    <div className="col-sm-12 ant-tabs-tab-active center">{topUp ? topUp : 0}</div>
                                </div>
                            </div>
                            
                            <div className="x_content">

                                <div className="table-responsive">

                                    <Table columns={columns}
                                        dataSource={tableList}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{emptyText:'暂无数据'}}
                                        pagination={{
                                            size: "small", current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }} />
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        )

    }
}