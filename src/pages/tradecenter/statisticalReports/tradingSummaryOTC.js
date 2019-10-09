import { PAGEINDEX, PAGESIZE, SELECTWIDTH, PAGRSIZE_OPTIONS20, TIME_PLACEHOLDER, TIMEFORMAT_ss, SHOW_TIME_DEFAULT } from 'Conf'
import { Button, DatePicker, Tabs, Modal, Select, message, Table, } from 'antd'
import { toThousands, pageLimit, dateToFormat } from 'Utils'
import { SeOp } from '../../../components/select/asyncSelect'
import CommonTable from 'CTable'
import Decorator from 'DTPath'
const { MonthPicker, RangePicker, } = DatePicker;
const Option = Select.Option;

const _funds = {
    10: 'USDT',
    2: 'BTC',
    51: 'VDS'
}
@Decorator()
export default class TradingSummaryOTC extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            fundsType: '',
            buyAmountStart: '',
            buyAmountEnd: '',
            sellAmountStart: '',
            sellAmountEnd: '',
            userFeeStart: '',
            userFeeEnd: '',
            userNumStart: '',
            userNumEnd: '',
            time: [],
        }
        this.state = {
            ...this.defaultState,
        }
    }
    async componentDidMount() {

    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time, fundsType, buyAmountStart, buyAmountEnd, sellAmountStart, sellAmountEnd
            , userFeeStart, userFeeEnd, userNumStart, userNumEnd, } = this.state
        let params = {
            fundsType, buyAmountStart, buyAmountEnd, sellAmountStart, sellAmountEnd
            , userFeeStart, userFeeEnd, userNumStart, userNumEnd,
            tradingStrStart: time.length ? moment(time[0]).format(TIMEFORMAT_ss) : '',
            tradingStrEnd: time.length ? moment(time[1]).format(TIMEFORMAT_ss) : '',
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/otcsummary/list', type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    exportToExcel = () => {

    }
    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '报表日期', dataIndex: 'trading', render: t => dateToFormat(t) },
            { title: '资金类型', dataIndex: 'fundstypeName' },
            { title: '上架广告数', dataIndex: 'ordernums' },
            { title: '订单成交数-买', dataIndex: 'buyrecords', },
            { title: '订单成交金额-买', dataIndex: 'buyamounts', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '订单成交数-卖', dataIndex: 'sellrecords' },
            { title: '订单成交金额-卖', className: 'moneyGreen', dataIndex: 'sellamounts', render: text => toThousands(text, true) },
            { title: '申诉订单数', dataIndex: 'complains' },
            { title: '交易用户数', dataIndex: 'usernums' },
            { title: '累计成交币量', dataIndex: 'coinnums', },
            { title: '累计手续费', className: 'moneyGreen', dataIndex: 'userfees', render: text => toThousands(text, true) },
        ]
    }
    render() {
        const { showHide, time, pageIndex, pageSize, pageTotal, dataSource, fundsType, buyAmountStart, buyAmountEnd, sellAmountStart, sellAmountEnd
            , userFeeStart, userFeeEnd, userNumStart, userNumEnd, } = this.state
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
                                <SeOp title='资金类型' value={fundsType} onSelectChoose={v => this.onSelectChoose(v, 'fundsType')} ops={_funds} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易金额买：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最小值' className="form-control" name="buyAmountStart" value={buyAmountStart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最大值' className="form-control" name="buyAmountEnd" value={buyAmountEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易金额卖：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最小值' className="form-control" name="sellAmountStart" value={sellAmountStart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最大值' className="form-control" name="sellAmountEnd" value={sellAmountEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">广告手续费：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最小值' className="form-control" name="userFeeStart" value={userFeeStart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最大值' className="form-control" name="userFeeEnd" value={userFeeEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易人数：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最小值' className="form-control" name="userNumStart" value={userNumStart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最大值' className="form-control" name="userNumEnd" value={userNumEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">报表日期：</label>
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
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="9" className="column-title">
                                                    累计成交金额：{''},&nbsp;&nbsp;&nbsp;&nbsp;
                                                    累计成交笔数：{''}，&nbsp;&nbsp;&nbsp;&nbsp;
                                                    累计广告手续费：{}，&nbsp;&nbsp;&nbsp;&nbsp;
                                                    累计交易人数：{}，&nbsp;&nbsp;&nbsp;&nbsp;
                                                    累计申诉订单数：{}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <CommonTable
                                        dataSource={dataSource}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSize: pageSize,
                                                current: pageIndex

                                            }
                                        }
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}
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