import axios from '../../../utils/fetch'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, PAGRSIZE_OPTIONS20, PAGRSIZE_OPTIONS, TIMEFORMAT_ss, PAGESIZE_50 } from '../../../conf'
import { Button, DatePicker, Tabs, Modal, Select, message, Table, } from 'antd'
import { toThousands, pageLimit, islessBodyWidth } from '../../../utils'
import HedgeMarketList from '../../common/select/hedgeMarketList'
import { CommonHedgeResults, JudgeHedgeResults } from '../../common/select/commonHedgeResults'
import CommonTable from '../../common/table/commonTable'
const { MonthPicker, RangePicker, } = DatePicker;
const Option = Select.Option;

export default class HedgeUserTransRecordMange extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            id: '',
            entrustMarket: '',
            entrustStatus: '',
            strFromTime: '',
            strToTime: '',
            entrustUserId: '',
            entrustId: '',
            transrecordId: '',
            entrustType: ""

        }
        this.state = {
            time: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            showHide: true,
            tableList: [],
            pageTotal: 0,
            ...this.defaultState,
            hedgeResults: {}
        }
    }
    async componentDidMount() {
        const { hedgeResults } = await JudgeHedgeResults()
        this.setState({ hedgeResults }, () => this.requestTable())

    }
    clickHide = () => {
        this.setState({
            showHide: !this.state.showHide
        })
    }
    resetState = () => {
        this.setState({
            ...this.defaultState,
            time: []
        })
    }
    selectMarket = (entrustMarket) => {
        this.setState({ entrustMarket })
    }
    selectStatus = (entrustStatus) => {
        this.setState({ entrustStatus })
    }
    handleTypeChange = entrustType => {
        this.setState({
            entrustType
        })
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            strFromTime: dateString[0] ? moment(dateString[0]).format('x') : '',
            strToTime: dateString[1] ? moment(dateString[1]).format('x') : '',
            time: date
        })
    }
    requestTable = (currIndex, currSize) => {
        const { id, entrustMarket, entrustStatus, entrustType, strFromTime, strToTime, entrustUserId, entrustId, transrecordId, pageIndex, pageSize } = this.state
        axios.get(DOMAIN_VIP + '/brush/hedge/order/fromList', {
            params: {
                id, entrustMarket, entrustStatus, entrustType, strFromTime, strToTime, entrustUserId, entrustId, transrecordId,
                pageIndex: currIndex || pageIndex,
                pageSize: currSize || pageSize
            }
        }).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableList = result.data.list || [];
                this.setState({
                    tableList,
                    pageTotal: result.data.totalCount,
                    pageSize: currSize || pageSize,
                    pageIndex: currIndex || pageIndex
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    //输入时 input 设置到 satte
    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    createColumns = (pageIndex, pageSize) => {
        const { hedgeResults } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '来源ID', dataIndex: 'id' },
            { title: '交易市场', dataIndex: 'entrustMarket' },
            { title: '保值状态', dataIndex: 'entrustStatus', render: text => hedgeResults[text] },
            { title: '交易方向', dataIndex: 'entrustType', render: text => text == 0 ? '卖' : '买' },
            { title: '委托ID', dataIndex: 'entrustId' },
            { title: '委托价格', className: 'moneyGreen', dataIndex: 'entrustPrice', render: text => toThousands(text, true) },
            { title: '委托数量', dataIndex: 'entrustNum', },
            { title: '委托用户', dataIndex: 'entrustUserId' },
            { title: '成交ID', dataIndex: 'transrecordId' },
            { title: '手续费', className: 'moneyGreen', dataIndex: 'entrustFee', render: text => toThousands(text, true) },
            { title: '来源方式', dataIndex: 'sourceType', render: text => text == "0" ? "推送" : "合并生成" },
            { title: '对冲ID列表', dataIndex: 'sourceIds' },
            { title: '来源单ID列表', dataIndex: 'sourceFromIds' },
            { title: '添加时间', dataIndex: 'addTime', render: text => text ? moment(text).format(TIMEFORMAT_ss) : '' },
            { title: '成交时间', dataIndex: 'transrecordTime', render: text => text ? moment(text).format(TIMEFORMAT_ss) : '' }
        ]
    }
    render() {
        const { showHide, time, pageIndex, pageSize, pageTotal, tableList, id, entrustMarket, entrustId, entrustStatus, entrustUserId, entrustType,
            transrecordId } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HedgeMarketList market={entrustMarket} col='3' title='交易市场' handleChange={this.selectMarket} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={entrustType} style={{ width: SELECTWIDTH }} onChange={this.handleTypeChange} >
                                                <Option value=''>请选择</Option>
                                                <Option value='1'>买入</Option>
                                                <Option value='0'>卖出</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <CommonHedgeResults status={entrustStatus} col='3' title='保值状态' handleChange={this.selectStatus} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">来源ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="id" value={id} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托用户ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="entrustUserId" value={entrustUserId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="entrustId" value={entrustId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="transrecordId" value={transrecordId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交时间：</label>
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
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <CommonTable
                                        dataSource={tableList}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSize: pageSize,
                                                current: pageIndex

                                            }
                                        }
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}
                                        scroll={islessBodyWidth() ? { x: 1800 } : {}}
                                    />
                                </div>
                            </div>
                        </div>
                        {/* <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table
                                        dataSource={tableList}
                                        bordered
                                        scroll={{ x: 2000 }}
                                        // rowSelection={rowSelection}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.onChangPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            pageSizeOptions: PAGRSIZE_OPTIONS20,
                                            defaultPageSize: PAGESIZE,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }}
                                        locale={{ emptyText: '暂无数据' }}
                                    >
                                        <Column title='序号' dataIndex='index' />
                                        <Column title='来源ID' dataIndex='id' />
                                        <Column title='交易市场' dataIndex='entrustMarket' />
                                        <Column title='保值状态' dataIndex='entrustStatus' />
                                        <Column title='交易方向' dataIndex='entrustType' render={text => text == 0 ? '卖' : '买'} />
                                        <Column title='委托ID' dataIndex='entrustId' />
                                        <Column title='委托价格' dataIndex='entrustPrice' render={text => toThousands(text, true)} />
                                        <Column title='委托数量' dataIndex='entrustNum' />
                                        <Column title='委托用户' dataIndex='entrustUserId' />
                                        <Column title='成交ID' dataIndex='transrecordId' />
                                        <Column title='手续费' dataIndex='entrustFee' render={text => toThousands(text, true)} />
                                        <Column title='来源方式' dataIndex='sourceType' render={text => text == "0" ? "推送" : "合并生成"} />
                                        <Column title='来源ID列表' dataIndex='sourceIds' />
                                        <Column title='添加时间' dataIndex='addTime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : ''} />
                                        <Column title='成交时间' dataIndex='transrecordTime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : ''} />
                                    </Table>
                                </div>

                            </div>
                        </div> */}
                    </div>
                </div>
            </div>
        )
    }
}