
/**用户成交占数量资金比 */
import React, { Component } from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, DatePicker, Tabs, Pagination, Select, message, Table, Modal, Badge } from 'antd'
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT, PAGRSIZE_OPTIONS20, DAYFORMAT } from '../../../conf'
import MarketRequests from '../../common/select/marketrequests'
import { toThousands,pageLimit } from '../../../utils'
import SelectStateList from '../../common/select/selectStateList'
const TabPane = Tabs.TabPane



const { MonthPicker, RangePicker, WeekPicker } = DatePicker;

// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { Column } = Table
class UserTransForAmountFunds extends Component {
    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            isreLoad: false,
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            width: '',
            pagination: {
                showQuickJumper: true,
                showSizeChanger: true,
                showTotal: total => `总共${total}条`,
                size: 'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions: PAGRSIZE_OPTIONS20,
                defaultPageSize: PAGESIZE
            },

            tableSource: [],
            entrustmarket: '',
            quantitativeid: '',
            createtimeS: '',
            createtimeE: '',
            tabKey: 0,
            status: '0',
            time: [],
            noReachedSum: 0,
            reachedSum: 0,
            RowKeys: [],
            limitBtn:[]
        }
        this.clickHide = this.clickHide.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)

        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render() {
        const { time, pageSize, showHide, tableSource, pagination, pageIndex, entrustmarket, quantitativeid, tabKey, status, noReachedSum, reachedSum, RowKeys,limitBtn } = this.state
        let statusArr = ['未解决', '已解决']
        let newObj = {}
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 保值异常 > 用户成交占刷量资金比
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <MarketRequests market={entrustmarket} handleChange={this.handleChangeSelect} col='3' />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">量化账户：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="quantitativeid" value={quantitativeid} onChange={this.handleInputChange} width={SELECTWIDTH} />
                                        </div>
                                    </div>
                                </div>
                                <SelectStateList handleChange={this.selectStatus} value={status} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">报警时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />

                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        {limitBtn.indexOf('updateall') > -1 && tabKey == 0 && <Button type="primary" disabled={!RowKeys.length} onClick={this.batchMarking}>批量标记</Button>}
                                    </div>
                                </div>

                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Tabs onChange={this.callback}>
                                        <TabPane tab={<Badge count={reachedSum} overflowCount={99} offset={[18, 0]} showZero>达到成交预警值</Badge>} key={0}></TabPane>
                                        <TabPane tab={<Badge count={noReachedSum} overflowCount={99} offset={[18, 0]} showZero>未达到成交预警值</Badge>} key={1}></TabPane>
                                    </Tabs>
                                    <Table dataSource={tableSource} bordered pagination={{ ...pagination, current: pageIndex }} locale={{ emptyText: '暂无数据' }}
                                        onChange={this.sorter}
                                        rowSelection={tabKey == 0 ? {
                                            selectedRowKeys: RowKeys,
                                            onChange: this.onSelectChange,
                                        } : null}
                                    >
                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='交易市场' dataIndex='entrustmarket' key='entrustmarket'

                                        />
                                        <Column title='量化账号' dataIndex='quantitativeid' key='quantitativeid' />
                                        <Column title='量化账号金额(BTC)' dataIndex='amount' key='amount' className='moneyGreen' render={text => toThousands(text, true)} />
                                        <Column title='所有用户与量化成交金额' dataIndex='allamount' key='allamount' className='moneyGreen' render={text => toThousands(text, true)} />
                                        <Column title='与量化成交人数' dataIndex='numbers' key='numbers' render={text => text == '0E-9' ? 0 : text} />
                                        <Column title='用户占量化账号资金比' dataIndex='accounted' key='accounted' render={text => `${Number(text).toFixed(2)}%`} />
                                        {tabKey == 0 && <Column title='解决状态' dataIndex='state' key='state' render={text => statusArr[text]} />}
                                        <Column title='报警时间' dataIndex='datetime' key='datetime' render={(parameter) => {
                                            return parameter ? moment(parameter).format(TIMEFORMAT) : '--'
                                        }} />
                                        <Column title='报表时间' dataIndex='reporttime' key='reporttime' render={(parameter) => {
                                            return parameter ? moment(parameter).format(DAYFORMAT) : '--'
                                        }} />
                                        {tabKey == 0 && <Column title='操作' dataIndex='op' key='op' render={(text, record) => {
                                            return limitBtn.indexOf('update') > -1 &&record.state == 0 ? <a href='javascript:void(0);' onClick={() => this.onMark(record.id)}>标记</a> : ''
                                        }} />}
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

            </div>
        )
    }
    componentDidMount() {
        this.setState ({
            limitBtn : pageLimit('coinQtAccounted',this.props.permissList)
        })
        this.requestTable()
        this.requestTableSum(0);
        this.requestTableSum(1)
    }
    componentDidUpdate() {

    }
    onChangeCheckTime = (date, dateString) => {
        // console.log(date,dateString)
        this.setState({
            createtimeS: dateString[0] ? moment(dateString[0]).format('x') : '',
            createtimeE: dateString[1] ? moment(dateString[1]).format('x') : '',
            time: date
        })
    }
    onMark = id => {
        let self = this;
        Modal.confirm({
            title: '你确定要标记吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + '/coinQtAccounted/update', qs.stringify({ id })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.requestTable()
                    }
                })
            },
            onCancel() {
                console.log('Cancel')
            }
        })
    }
    selectStatus = status => {
        this.setState({ status })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex, pageSize, types, pagination, quantitativeid, entrustmarket, createtimeS, createtimeE, status, tabKey } = this.state
        axios.post(DOMAIN_VIP + '/coinQtAccounted/list', qs.stringify({
            quantitativeid, entrustmarket, createtimeS, createtimeE,
            state: status, code: tabKey,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize,
        })).then(res => {
            const result = res.data;
            // console.log(result)
            if (result.code == 0) {
                let tableSource = result.data.list;
                let arr = []
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource: tableSource,
                    pagination,
                })
            } else {
                message.warning(result.msg);
            }
        })

    }
    requestTableSum = tab => {
        console.log(tab)
        const { pageIndex, pageSize, types, pagination, quantitativeid, entrustmarket, createtimeS, createtimeE, status, tabKey } = this.state
        axios.post(DOMAIN_VIP + '/coinQtAccounted/sum', qs.stringify({
            quantitativeid, entrustmarket, createtimeS, createtimeE,
            state: tab == 0 ? '0' : '',
            code: tab,
            pageIndex: pageIndex,
            pageSize: pageSize,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                if (tab == 0) {
                    this.setState({
                        reachedSum: result.data.sum
                    })
                } else if (tab == 1) {
                    this.setState({
                        noReachedSum: result.data.sum
                    })
                }
            } else {
                message.warning(result.msg);
            }
        })
    }
    //交易市场下拉菜单
    handleChangeSelect = value => {
        this.setState({
            entrustmarket: value,
        })
    }
    //输入框获取值
    handleInputChange = e => {
        const value = e.target.value
        const name = e.target.name
        this.setState({
            [name]: value
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
    callback = tabKey => {
        this.setState({
            tabKey,
            status: tabKey == 0 ? '0' : '',
            pageIndex: PAGEINDEX
        }, () => {
            this.requestTable();
            this.requestTableSum(tabKey)
        })
    }
    //查询按钮
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => {
            this.requestTable()
            this.requestTableSum(0);
            this.requestTableSum(1)
        })
    }
    //重置按钮
    resetState = () => {
        this.setState(() => ({
            entrustmarket: '',
            quantitativeid: '',
            createtimeS: '',
            createtimeE: '',
            status: this.state.tabKey == 0 ? '0' : '',
            time: []
        }), () => {
            this.requestTable()
            this.requestTableSum(0);
            this.requestTableSum(1)
        })

    }

    //点击收起
    clickHide() {
        let { showHide } = this.state;

        this.setState({
            showHide: !showHide,
        })
    }
    //去重
    arrayUnique2(arr, name) {
        let hash = {};
        return arr.reduce(function (item, next) {
            hash[next[name]] ? '' : hash[next[name]] = true && item.push(next);
            return item;
        }, []);
    }
    onSelectChange = (selectedRowKeys) => {
        this.setState({
            RowKeys: selectedRowKeys
        });
    };
    batchMarking = () => {
        let self = this;
        Modal.confirm({
            title: '你确定要批量标记选中的数据吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + '/coinQtAccounted/updateall', qs.stringify({ idlist: self.state.RowKeys.toString() })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.setState({
                            RowKeys: []
                        }, () => {
                            self.requestTable();
                            self.requestTableSum(0);
                            self.requestTableSum(1)
                        });
                    }
                })
            },
            onCancel() {
            }
        })
    }

}

export default UserTransForAmountFunds  
