import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT, HOURSFORMAT, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss, NUMBERPOINT, PAGESIZE_50, PAGRSIZE_OPTIONS, SELECTWIDTH, TIMEFORMAT_DAYS } from '../../../conf'
import { Button, Pagination, Select, DatePicker, Tabs, message } from 'antd'
import { toThousands, tableScroll, ckd, TE, isObj, isArray } from '../../../utils'
import MarketList from '../select/marketList'
import moment from 'moment'
import { exportExcel } from 'xlsx-oc'
import SelectUserTypeList from '../../common/select/selectUserTypeList'
const Big = require('big.js')
const Option = Select.Option;
const { RangePicker } = DatePicker;
const TabPane = Tabs.TabPane;

export default class DealRecord extends React.Component {

    constructor(props) {
        super(props)
        this.defaultState = {
            market: 'etc_btc',
            userId: '',
            userName: '',
            minPrice: '',
            maxPrice: '',
            numbersMin: '',
            numbersMax: '',
            type: '',
            status: '',
            endTime: '',
            beginTime: '',
            maxTitleCount: '',
            minTitleCount: '',
            transRecordId: '',
            entrustidsell: '',
            entrustidbuy: '',
            time: [],
            accountType: '2',
            queryType: 0,
        }
        this.state = {
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE_50,
            pageTotal: DEFAULTVALUE,
            tableList: [],
            tableListNow: [],
            tableListAgo: [],
            pageTotalNow: 0,
            pageTotalAgo: 0,
            height: 0,
            numbersSumNow: 0,
            totalpriceSumNow: 0,
            numbersSumAgo: 0,
            totalpriceSumAgo: 0,
            ...this.defaultState,
            loading: false
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.resetState = this.resetState.bind(this)
        this.clickInquireState = this.clickInquireState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)         //分页
        this.onShowSizeChange = this.onShowSizeChange.bind(this)   //分页
        this.clickHide = this.clickHide.bind(this);
        this.resetGetAllRecord = this.resetGetAllRecord.bind(this)
        this.handleChange = this.handleChange.bind(this)            //市场
        this.type_handleChange = this.type_handleChange.bind(this)  //委托类型
        this.status_handleChange = this.status_handleChange.bind(this)
        this.time_onChange = this.time_onChange.bind(this)
        this.time_onOk = this.time_onOk.bind(this)
        this.callbackTabs = this.callbackTabs.bind(this)
    }
    componentDidMount() {

    }
    componentWillReceiveProps() {

    }
    componentWillUnmount() {

    }
    //点击分页
    changPageNum(page, pageSize) {
        this.resetGetAllRecord(page, pageSize)
        this.setState({
            pageIndex: page,
            pageSize: pageSize
        })
    }
    //tabs 回调
    callbackTabs(key) {
        const { pageIndex, pageSize } = this.state
        this.setState({
            queryType: key,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE_50
        })
        // this.requestTable(pageIndex,pageSize,this.props.curId,key)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.resetGetAllRecord(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }

    //市场选择框
    handleChange(value) {
        this.setState({
            market: value
        })
    }
    //委托类型选择框
    type_handleChange(value) {
        this.setState({
            type: value
        })
    }
    //处理状态选择框
    status_handleChange(value) {
        this.setState({
            status: value
        })
    }
    //时间选择框
    time_onChange(value, dateString) {
        this.setState({
            beginTime: dateString[0],
            endTime: dateString[1],
            time: value
        })
    }
    time_onOk(value) {
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    createParams = (obj = {}) => Object.keys(this.defaultState).reduce((res, key) => {
        void 0 !== this.state[key] && !isArray(this.state[key]) && (res[key] = this.state[key])
        return res
    }, obj)
    resetGetAllRecord(currentIndex, currentSize) {
        const { pageIndex, pageSize, queryType } = this.state;
        const params = this.createParams({
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })
        axios.post(DOMAIN_VIP + "/dealRecord/getAllRecord", qs.stringify(params)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                if (queryType == 0) {
                    this.setState({
                        tableListNow: result.dealRecord.list,
                        pageTotalNow: result.dealRecord.totalCount,
                        numbersSumNow: result.dealRecord.list[0] && result.dealRecord.list[0].numbersSum,
                        totalpriceSumNow: result.dealRecord.list[0] && result.dealRecord.list[0].totalpriceSum
                    })
                } else {
                    this.setState({
                        tableListAgo: result.dealRecord.list,
                        pageTotalAgo: result.dealRecord.totalCount,
                        numbersSumAgo: result.dealRecord.list[0] && result.dealRecord.list[0].numbersSum,
                        totalpriceSumAgo: result.dealRecord.list[0] && result.dealRecord.list[0].totalpriceSum
                    })
                }

            } else {
                message.warning(result.code)
            }
        })

    }

    //查询
    clickInquireState() {
        this.resetGetAllRecord();
    }
    //点击收起
    clickHide() {
        let { showHide, pageSize } = this.state;

        this.setState({
            showHide: !showHide,
        })
    }
    //重置
    resetState() {
        this.setState({
            ...this.defaultState
        })
    }
    selectUser = user => {
        this.setState({
            accountType: user
        })
    }
    limitTime30 = (days30, msg = '时间区间请选择30天以内的数据导出！') => {
        let limit = 30 * 24 * 60 * 60 * 1000;
        if (days30 - limit > 0) {
            message.warning(msg);
            return false
        }
        return true
    }
    exportDefaultExcel = () => {
        const { time } = this.state
        // if (!time.length) {
        //     message.warning('请选择成交时间区间，区间为30天以内！！！')
        //     return false
        // } else {
        //     let day30 = Number(moment(time[1]).format('x')) - Number(moment(time[0]).format('x'))
        //     if (!this.limitTime30(day30)) return
        // }
        // let _headers = [
        //     { k: 'index', v: '' },
        //     { k: 'transrecordid', v: '成交编号' },
        //     { k: 'buyTypeName', v: '买家账户类型' },
        //     { k: 'useridbuy', v: '买家用户编号' },
        //     { k: 'entrustidbuy', v: '买单委托编号' },
        //     { k: 'sellTypeName', v: '卖家账户类型' },
        //     { k: 'useridsell', v: '卖家用户编号' },
        //     { k: 'entrustidsell', v: '卖单委托编号' },
        //     { k: 'unitprice', v: '成交单价' },
        //     { k: 'numbers', v: '成交数量' },
        //     { k: 'totalprice', v: '成交总金额' },
        //     { k: 'typeName', v: '委托类型' },
        //     { k: 'exportTimes', v: '成交时间' },
        //     { k: 'statusName', v: '状态' },
        // ]
        this.setState({ loading: true })
        axios.post(DOMAIN_VIP + '/dealRecord/exportDealRecord', qs.stringify(this.createParams())).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let etData = ckd(result.data);
                if (etData.length) {
                    // let sumObj = etData[etData.length - 1]
                    // //有汇总的处理一下
                    // isObj(sumObj) && Object.keys(sumObj).forEach(v => {
                    //     sumObj[v] === null && (sumObj[v] = '')
                    // })
                    // sumObj.index = '汇总'

                    // exportExcel(_headers, etData, '成交记录.xlsx')
                   
                    let str = '序号,成交编号,买家账户类型,买家用户编号,买单委托编号,卖家账户类型,卖家用户编号,卖单委托编号,成交单价,成交数量,成交总金额,委托类型,成交时间,状态\n';
                    
                    for (let i = 0; i < etData.length; i++) {
                        etData[i].index = i+1
                        isObj(etData[i]) && Object.keys(etData[i]).forEach(v => {

                            if (etData[i][v] == null) {
                                etData[i][v] = ''
                            }
                        })
                        etData[etData.length - 1].index = '汇总'
                        str += etData[i].index+'\t'+','+etData[i].transrecordid + '\t'+ ',' + etData[i].buyTypeName+ '\t' + ',' + etData[i].useridbuy+ '\t' + ',' + etData[i].entrustidbuy+ '\t' + ',' + etData[i].sellTypeName+ '\t' + ',' +
                            etData[i].useridsell+ '\t' + ',' + etData[i].entrustidsell+ '\t' + ',' + etData[i].unitprice + '\t' + ',' + etData[i].numbers + '\t' + ','+etData[i].totalprice+'\t' + ','+etData[i].typeName+'\t'+','+etData[i].exportTimes+'\t' + ','+etData[i].statusName+ '\n'
                    }
                    
                    let blob = new Blob([str], { type: "text/plain;charset=utf-8" });
                    //解决中文乱码问题
                    blob = new Blob([String.fromCharCode(0xFEFF), blob], { type: blob.type });
                    let object_url = window.URL.createObjectURL(blob);
                    let link = document.createElement("a");
                    link.href = object_url;
                    link.download = "成交记录.csv";
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);


                    this.setState({ loading: false })
                } else {
                    message.warning('没有数据，无法导出！')
                }

            } else {
                message.error(result.msg)
            }

        })
    }
    judgeAccoutType = (typeall, userid) => typeall.split(',').map(Number).includes(userid) ? '刷量账户' : '用户账户'
    render() {
        Big.RM = 0;
        const { showHide, market, userId, userName, moneyMin, moneyMax, numbersMin, numbersMax, type, status, endTime, beginTime,
            minPrice, maxPrice, minTitleCount, maxTitleCount, transRecordId, entrustidsell, entrustidbuy, pageIndex, pageSize,
            pageTotalAgo, pageTotalNow, tableListAgo, tableListNow, queryType, numbersSumAgo, numbersSumNow, totalpriceSumAgo, totalpriceSumNow, time, accountType } = this.state;
        let page_index = new Big(pageIndex);
        let page_size = new Big(pageSize);
        let ones = new Big(1);
        const now_page = page_index.times(page_size).minus(page_size).plus(ones);
        let tableList = queryType == 0 ? tableListNow : tableListAgo;
        let pageTotal = queryType == 0 ? pageTotalNow : pageTotalAgo;
        let numbersSum = queryType == 0 ? numbersSumNow : numbersSumAgo;
        let totalpriceSum = queryType == 0 ? totalpriceSumNow : totalpriceSumAgo;
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 >盘口管理 > 币币成交记录
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <MarketList market={market} col='3' handleChange={this.handleChange}></MarketList>
                                </div>
                                <SelectUserTypeList value={accountType} handleChange={this.selectUser} />
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} /><b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={type} style={{ width: SELECTWIDTH }} onChange={this.type_handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="0">卖出</Option>
                                                <Option value="1">买入</Option>
                                                {/* <Option value="-1">取消</Option> */}
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">处理状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.status_handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">交易取消</Option>
                                                <Option value="2">交易成功</Option>
                                                <Option value="3">未完全成交</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">卖单委托编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="entrustidsell" value={entrustidsell} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交单价：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="minPrice" value={minPrice} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="maxPrice" value={maxPrice} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交数量：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="numbersMin" value={numbersMin} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="numbersMax" value={numbersMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交总额：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="minTitleCount" value={minTitleCount} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="maxTitleCount" value={maxTitleCount} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="transRecordId" value={transRecordId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">买单委托编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="entrustidbuy" value={entrustidbuy} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onOk={this.time_onOk}
                                                onChange={this.time_onChange}
                                                value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.clickInquireState} >查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        <Button type="primary" loading={this.state.loading} onClick={this.exportDefaultExcel}>导出</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">
                                <Tabs onChange={this.callbackTabs}>
                                    <TabPane tab='当日成交记录' key="0"></TabPane>
                                    <TabPane tab='历史成交记录' key="1"></TabPane>
                                </Tabs>
                                <div className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan='14'>成交数量：{toThousands(numbersSum)},成交总金额：{toThousands(totalpriceSum)} </th>
                                            </tr>
                                        </thead>
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">成交编号</th>
                                                <th className="column-title">买家账户类型</th>
                                                <th className="column-title">买家用户编号</th>
                                                <th className="column-title">买单委托编号</th>
                                                <th className="column-title">卖家账户类型</th>
                                                <th className="column-title">卖家用户编号</th>
                                                <th className="column-title">卖单委托编号</th>
                                                <th className="column-title">成交单价</th>
                                                <th className="column-title">成交数量</th>
                                                <th className="column-title">成交总金额</th>
                                                <th className="column-title">委托类型</th>
                                                <th className="column-title">成交时间</th>
                                                <th className="column-title">状态</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ? tableList.map((item, index) => {

                                                    return (
                                                        <tr key={index}>
                                                            <td>{new Big(index).plus(now_page).toFixed()}</td>
                                                            <td>{item.transrecordid}</td>
                                                            <td>{this.judgeAccoutType(item.typeall, item.useridbuy)}</td>
                                                            <td>{item.useridbuy}</td>
                                                            <td>{item.entrustidbuy}</td>
                                                            <td>{this.judgeAccoutType(item.typeall, item.useridsell)}</td>
                                                            <td>{item.useridsell}</td>
                                                            <td>{item.entrustidsell}</td>
                                                            <td className='moneyGreen'>{item.unitprice ? toThousands(item.unitprice) : ''}</td>
                                                            <td>{item.numbers}</td>
                                                            <td className='moneyGreen'>{toThousands(new Big(item.totalprice).toFixed())}</td>
                                                            <td>{
                                                                // item.types >= 0 ? (
                                                                //     item.types > 0 ? '买单' : '卖单' 
                                                                // ):'取消'
                                                                item.types > 0 ? '买入' : '卖出'
                                                            }
                                                            </td>
                                                            <td>{item.times ? moment(item.times).format(TIMEFORMAT) : '--'}</td>
                                                            <td>
                                                                {
                                                                    (() => {
                                                                        switch (item.status) {
                                                                            case 0:
                                                                                return '未完全成交'
                                                                                break;
                                                                            case 1:
                                                                                return '取消'
                                                                                break;
                                                                            case 2:
                                                                                return '交易成功'
                                                                                break;
                                                                            default:
                                                                                return '未完全成交'
                                                                                break;
                                                                        }
                                                                    })()
                                                                }
                                                            </td>
                                                        </tr>
                                                    )
                                                }) : <tr className="no-record"><td colSpan="14">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                    {
                                        pageTotal > 0 && <Pagination
                                            size="small"
                                            current={pageIndex}
                                            total={pageTotal}
                                            onChange={this.changPageNum}
                                            showTotal={total => `总共 ${total} 条`}
                                            defaultPageSize={PAGESIZE_50}
                                            onShowSizeChange={this.onShowSizeChange}
                                            showSizeChanger
                                            pageSizeOptions={PAGRSIZE_OPTIONS}
                                            showQuickJumper />
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}





























