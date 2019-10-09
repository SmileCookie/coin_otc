import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { toThousands } from '../../../utils'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, DAYFORMAT, PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, DatePicker, Tabs, Pagination, Select, Table, message } from 'antd'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class CenterCapitalSum extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            accountType: [<Option key='0' value='0'>请选择</Option>],
            fundsType: "0",
            startTime: "",
            endTime: "",
            tableList: [],
            time: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: 0,
            columns: [{
                title: '序号',
                dataIndex: 'index',
                key: 'index',
            }, {
                title: '报表时间',
                dataIndex: 'countDate',
                key: 'countDate',
            }, {
                title: '资金类型', dataIndex: 'fundsTypeName', key: 'fundsTypeName'
            }, {
                title: '充值金额',className:'moneyGreen', dataIndex: 'amountDeposit', key: 'amountDeposit'
            }, {
                title: '公司充值',className:'moneyGreen', dataIndex: 'companyDeposit', key: 'companyDeposit'
            }, {
                title: '用户充值',className:'moneyGreen', dataIndex: 'userDeposit', key: 'userDeposit'
            }, {
                title: '提现金额',className:'moneyGreen', dataIndex: 'amountCashIn', key: 'amountCashIn'
            }, {
                title: '公司提现',className:'moneyGreen', dataIndex: 'companyCashIn', key: 'companyCashIn'
            }, {
                title: '用户提现',className:'moneyGreen', dataIndex: 'userCashIn', key: 'userCashIn'
            }, {
                title: '用户账面充值、提现差额',className:'moneyGreen', dataIndex: 'companyFuns', key: 'companyFuns'
            }, {
                title: '公司币币留存资金',className:'moneyGreen', dataIndex: 'companyRetainedFee', key: 'companyRetainedFee'
            }, {
                title: '用户币币留存资金',className:'moneyGreen', dataIndex: 'userRetainedFee', key: 'userRetainedFee'
            },{
                title: '公司钱包留存资金',className:'moneyGreen', dataIndex: 'companyRetainedFeeWallet', key: 'companyRetainedFeeWallet'
            }, {
                title: '用户钱包留存资金',className:'moneyGreen', dataIndex: 'userRetainedFeeWallet', key: 'userRetainedFeeWallet'
            },{
                title: '公司法币留存资金',className:'moneyGreen', dataIndex: 'companyRetainedFeeOtc', key: 'companyRetainedFeeOtc'
            }, {
                title: '用户法币留存资金',className:'moneyGreen', dataIndex: 'userRetainedFeeOtc', key: 'userRetainedFeeOtc'
            },{
                title: '公司留存资金总额',className:'moneyGreen', dataIndex: 'companyRetainedFeeSum', key: 'companyRetainedFeeSum'
            }, {
                title: '用户留存资金总额',className:'moneyGreen', dataIndex: 'userRetainedFeeSum', key: 'userRetainedFeeSum'
            }, {
                title: '交易手续费',className:'moneyGreen', dataIndex: 'transactionFee', key: 'transactionFee'
            }, {
                title: '用户交易手续费',className:'moneyGreen', dataIndex: 'transactionFeeUser', key: 'transactionFeeUser'
            }, {
                title: '公司交易手续费',className:'moneyGreen', dataIndex: 'transactionFeeCompany', key: 'transactionFeeCompany'
            }, {
                title: '提现手续费',className:'moneyGreen', dataIndex: 'cashInFee', key: 'cashInFee'
            }, {
                title: '手续费合计',className:'moneyGreen', dataIndex: 'numFee', key: 'numFee'
            }, {
                title: 'GBC回购与用户成交GBC', dataIndex: 'backCapitalUserGbc', key: 'backCapitalUserGbc'
            }, {
                title: 'GBC回购与公司成交GBC', dataIndex: 'backCapitalCompanyGbc', key: 'backCapitalCompanyGbc'
            }, {
                title: 'GBC回购与用户成交USDT', dataIndex: 'backCapitalUserUsdt', key: 'backCapitalUserUsdc'
            }, {
                title: 'GBC回购与公司成交USDT', dataIndex: 'backCapitalCompanyUsdt', key: 'backCapitalCompanyUsdc'
            }],
        }
        this.clickHide = this.clickHide.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    componentDidMount() {
        this.requestTable()
    }
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //查询按钮
    queryClickBtn(val) {
        this.setState({
            pageIndex: PAGEINDEX
        }, () => this.requestTable())
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestTable())

    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requestTable())
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            startTime: dateString[0] ? dateString[0] + " 00:00:00" : '',
            endTime: dateString[1] ? dateString[1] + " 23:59:59" : '',
            time: date
        })

    }
    handleChangeType(value) {
        this.setState({
            fundsType: value
        })
    }
    requestTable(currIndex, currSize) {
        const { fundsType, startTime, endTime, pageIndex, pageSize, pageTotal } = this.state
        axios.post(DOMAIN_VIP + '/centerCapitalSum/query', qs.stringify({
            startTime, endTime, fundsType,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            Big.RM = 0;
            const result = res.data;
            if (result.code == 0) {
                result.data.list.map((item, index) => {
                    for (let key in item) {
                        if (typeof (item[key]) == Number) {
                            item[key] = new Big(item[key]).toFixed()
                        }
                    }
                    item.countDate = moment(item.countDate).format(DAYFORMAT)
                    item.amountDeposit = toThousands(new Big(item.userDeposit).plus(item.companyDeposit), true)
                    console.log(item.amountDeposit)
                    item.amountCashIn = toThousands(new Big(item.userCashIn).plus(item.companyCashIn), true)
                    item.companyFuns = toThousands(new Big(item.userDeposit).minus(item.userCashIn), true)
                    item.numFee = toThousands(new Big(item.transactionFee).plus(item.cashInFee), true)
                    if (currIndex && currSize) {
                        item.index = (currIndex - 1) * currSize + index + 1
                    } else {
                        item.index = (pageIndex - 1) * pageSize + index + 1
                    }

                })
                result.data.list.map((item, index) => {
                    for (let key in item) {
                        if (typeof (item[key]) == Number) {
                            item[key] = toThousands(item[key])
                        }
                    }
                })
                let tableList = result.data.list
                this.setState({
                    tableList,
                    pageTotal: result.data.totalCount
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }

    onResetState() {
        this.setState({
            fundsType: "0",
            startTime: "",
            endTime: "",
            time: [],
        })
    }
    render() {
        const { showHide, paraList, fundsType, data, columns, startTime, endTime, tableList, time, pageIndex, pageSize, pageTotal } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 资金报表 > 平台资金累计
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">统计时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                format="YYYY-MM-DD"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.queryClickBtn()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table columns={columns} bordered locale={{ emptyText: '暂无数据' }} dataSource={tableList} rowKey='index' scroll={{ x: '260%' }} pagination={{
                                        size: "small", current: pageIndex,
                                        total: pageTotal,
                                        onChange: this.changPageNum,
                                        showTotal: total => `总共 ${total} 条`,
                                        onShowSizeChange: this.onShowSizeChange,
                                        pageSizeOptions: PAGRSIZE_OPTIONS20,
                                        defaultPageSize: PAGESIZE,
                                        showSizeChanger: true,
                                        showQuickJumper: true
                                    }}></Table>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        )

    }
}