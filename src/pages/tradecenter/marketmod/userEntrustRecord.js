import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, PAGRSIZE_OPTIONS20, TIMEFORMAT_ss,DEFAULTVALUE, DAYFORMAT } from '../../../conf'
import { Button, DatePicker, Tabs, Pagination, Select, Table, message } from 'antd'
import { toThousands } from '../../../utils'
import MarketList from '../../common/select/marketrequests'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { Column } = Table

export default class UserEntrustRecord extends React.Component {
    constructor(props) {
        super(props)
        this.default = {
            time: [],
            beginTime: '',
            endTime: '',
            market: '',
            userId: '',
        }
        this.state = {
            tableName: 1,
            showHide: true,
            tableSource: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            ...this.default,
            numbers: 0,
            completenumber: 0,
            completetotalmoney: 0,
            totalmoney: 0,
            loading: false
        }
    }
    componentDidMount() {
        this.setBeforeDays(1)
        // this.requestTable()

    }
    componentWillReceiveProps() {

    }
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => this.requestTable())
    }
    resetState = () => {
        const { tableName, defaultEndTime, defaultStartTime, defaultTime} = this.state
        // const {  } = this.setBeforeDays(1)
        if (tableName == 2) {
            this.setState({
                beginTime:defaultStartTime,
                endTime:defaultEndTime,
                time:defaultTime
            })
        } else {
            this.setState({
                ...this.default
            })
        }

    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        })
    }
    handleTabChange = tabKey => {
        // const { defaultEndTime, defaultStartTime, defaultTime } = this.setBeforeDays(1)
        let { beginTime, endTime, time,defaultEndTime, defaultStartTime, defaultTime } = this.state
        if (tabKey == 2 && !beginTime && !endTime) {
            beginTime = defaultStartTime;
            endTime = defaultEndTime;
            time = defaultTime
        }
        this.setState({
            tableName: tabKey,
            pageIndex: PAGEINDEX,
            beginTime,
            endTime, time
        }, () => this.requestTable())
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            beginTime: dateString[0] && moment(dateString[0]).format('x'),
            endTime: dateString[1] && moment(dateString[1]).format('x'),
            time: date
        })
       
    }
    setBeforeDays = (days) => {
        let beforeTime = moment().subtract(days, 'days').format(DAYFORMAT)
        let nowTime = moment().format(DAYFORMAT)
        let defaultStartTime = moment(beforeTime + ' 00:00:00').format('x')
        let defaultEndTime = moment(nowTime + ' 23:59:59').format('x')
        let defaultTime = [moment(beforeTime + '00:00:00', TIMEFORMAT), moment(nowTime + '23:59:59', TIMEFORMAT)]
        this.setState({
            defaultStartTime,
            defaultEndTime,
            defaultTime
        }) 

    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex, pageSize, userId, market, beginTime, endTime, tableName } = this.state
        this.setState({ loading: true })
        axios.post(DOMAIN_VIP + '/entrustRecord/queryUserEntrust', qs.stringify({
            userId, market, beginTime, endTime:Number(endTime)+999, tableName,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.page.list || [];
                for (let i = 0, length = tableSource.length; i < length; i++) {
                    tableSource[i].index = (result.data.page.currPage - 1) * result.data.page.pageSize + i + 1;
                    tableSource[i].key = i
                }
                let { numbers, completenumber, completetotalmoney, totalmoney, } = result.data
                this.setState({
                    tableSource,
                    pageTotal: result.data.page.totalCount,
                    numbers: toThousands(numbers),
                    completenumber: toThousands(completenumber),
                    completetotalmoney: toThousands(completetotalmoney),
                    totalmoney: toThousands(totalmoney),
                    loading: false
                })

            } else {
                message.warning(result.msg);
            }
        })
    }
    onChangePageNum = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    handleChange = (market) => {
        this.setState({ market })
    }
    render() {
        const { showHide, pageIndex, pageSize, pageTotal, time, tableSource, userId, market, numbers, completenumber, completetotalmoney, totalmoney } = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置：数据中心 > 盘口管理 > 用户委托记录
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <MarketList market={market} underLine={true} col='3' handleChange={this.handleChange}></MarketList>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onChangeCheckTime}
                                                value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Tabs onChange={this.handleTabChange} size='small'>
                                        <TabPane tab='当日委托' key={1}></TabPane>
                                        <TabPane tab='历史委托' key={2}></TabPane>
                                    </Tabs>
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    委托数量:{numbers} &nbsp;&nbsp;&nbsp;
                                                    委托总金额：{totalmoney} &nbsp;&nbsp;&nbsp;
                                                    已成交数量：{completenumber} &nbsp;&nbsp;&nbsp;
                                                    已成交总金额：{completetotalmoney} &nbsp;&nbsp;&nbsp;
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table
                                        dataSource={tableSource}
                                        bordered
                                        loading={this.state.loading}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.onChangePageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            pageSizeOptions: PAGRSIZE_OPTIONS20,
                                            defaultPageSize: PAGESIZE,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }}
                                        locale={{ emptyText: '暂无数据' }}
                                    >
                                        <Column title='序号' dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )} />
                                        <Column title='委托编号' dataIndex='entrustid' />
                                        <Column title='交易市场' dataIndex='marketShow' />
                                        <Column title='账户类型' dataIndex='userType' />
                                        <Column title='委托用户编号' dataIndex='userid' />
                                        <Column title='委托类型' dataIndex='typesShow' />
                                        <Column className='moneyGreen' title='委托单价' dataIndex='unitprice' />
                                        <Column title='委托数量' dataIndex='numbersShow' />
                                        <Column className='moneyGreen' title='委托总金额' dataIndex='totalmoneyShow' />
                                        <Column className='moneyGreen' title='已成交数量' dataIndex='completenumbershow' />
                                        <Column title='已成交金额' dataIndex='completetotalmoney' />
                                        <Column title='处理状态' dataIndex='statusName' />
                                        <Column title='委托来源' dataIndex='webName' />
                                        <Column title='委托时间' dataIndex='submittimeShow' />
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