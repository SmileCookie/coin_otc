import axios from '../../../utils/fetch'
import { PAGEINDEX, PAGESIZE, SELECTWIDTH, PAGRSIZE_OPTIONS20, TIME_PLACEHOLDER, TIMEFORMAT_ss, SHOW_TIME_DEFAULT } from '../../../conf'
import { Button, DatePicker, Tabs, Modal, Select, message, Table, } from 'antd'
import { toThousands, pageLimit, islessBodyWidth } from '../../../utils'
import HedgeMarketList from '../../common/select/hedgeMarketList'
import { CommonHedgeResults, JudgeHedgeResults } from '../../common/select/commonHedgeResults'
import CommonTable from '../../common/table/commonTable'
import Decorator from '../../decorator'
const { MonthPicker, RangePicker, } = DatePicker;
const Option = Select.Option;

@Decorator()
export default class RecordHedgedRecharge extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            coinType: '',
            cashType: '',
            platform: '',
            accountNumber: '',
            time: [],
        }
        this.state = {
            // pageIndex: PAGEINDEX,
            // pageSize: PAGESIZE,
            // showHide: true,
            // tableSource: [],
            // pageTotal: 0,
            ...this.defaultState,
            hedgeResults: {}
        }
    }
    async componentDidMount() {
        const { hedgeResults } = await JudgeHedgeResults()
        this.setState({ hedgeResults }, () => this.requestTable())

    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, coinType, cashType, platform, accountNumber, time } = this.state
        let params = {
            coinType, cashType, platform, accountNumber,
            cashTimeS: time.length ? moment(time[0]).format('x') : '',
            cashTimeE: time.length ? moment(time[1]).format('x') : '',
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/qtCashValueRecord/list ', type: 'post' }, params)
        this.setState({
            tableSource,
            pageTotal: result.data.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    exportToExcel = () => {

    }
    createColumns = (pageIndex, pageSize) => {
        const { hedgeResults } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '平台', dataIndex: 'id' },
            { title: '账号', dataIndex: 'accountNumber', render: text => text == 0 ? '操盘' : '保值' },
            { title: '币种', dataIndex: 'coinType' },
            { title: '类型', dataIndex: 'cashType', render: text => text == 0 ? '提现' : '充值' },
            { title: '时间', dataIndex: 'cashTime', render: text => text ? moment(text).format(TIMEFORMAT_ss) : '' },
            { title: '数量', dataIndex: 'cashValueNumber' },
            { title: '余额', className: 'moneyGreen', dataIndex: 'balance', render: text => toThousands(text, true) },
        ]
    }
    render() {
        const { showHide, time, pageIndex, pageSize, pageTotal, tableSource, cashType, accountNumber } = this.state
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
                                    <HedgeMarketList market={''} col='3' title='币种' handleChange={(v) => this.onSelectChoose(v, 'coinType')} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={cashType} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'cashType')} >
                                                <Option value=''>全部</Option>
                                                <Option value='0'>提现</Option>
                                                <Option value='1'>充值</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">账号：</label>
                                        <div className="col-sm-9">
                                            <Select value={accountNumber} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'accountNumber')} >
                                                <Option value=''>全部</Option>
                                                <Option value='0'>操盘</Option>
                                                <Option value='1'>保值</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <CommonHedgeResults status={''} col='3' title='平台' handleChange={(v) => this.onSelectChoose(v, 'platform')} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: SHOW_TIME_DEFAULT
                                                }}
                                                format={TIMEFORMAT_ss}
                                                placeholder={TIME_PLACEHOLDER}
                                                onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        <Button type="primary" onClick={this.exportToExcel}>导出</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <CommonTable
                                        dataSource={tableSource}
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
                    </div>
                </div>
            </div>
        )
    }
}