/** 外网委托详情*/
import Decorator from 'DTPath'
import CommonTable from 'CTable'
import {PAGEINDEX, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER, TIMEFORMAT_ss, TRADE_TYPE } from 'Conf'
import { Button, DatePicker } from 'antd'
import { SeOp } from '../../../components/select/asyncSelect'
import PlatformsList from '../../common/select/platformsList'
import { islessBodyWidth, ckd, dateToFormat, arrayTimeToStr,TE, isArray, toThousands, mapGet } from '../../../utils'
const { RangePicker } = DatePicker
@Decorator()
export default class ExternalNetworkComDetails extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            entrustPlatform: '',//保值平台
            types: '',//交易类型
            entrustId: '',//委托ID
            time: [],//成交时间
        }
    }
    componentDidMount() {
        const { entrustPlatform, entrustId} = this.props.item
        this.setState({
            entrustPlatform,entrustId
        }, () => {
            this.requestTable(1)
        })

    }
    componentWillReceiveProps(nextprops) {
        const { entrustPlatform, entrustId} = nextprops.item
        this.setState({
            entrustPlatform,entrustId
        }, () => {
            this.requestTable(1)
        })

    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time } = this.state
        const params = Object.keys(this.defaultState).reduce((res, key) => {
            let _v = this.state[key];
            void 0 !== _v && !isArray(_v) && (res[key] = _v);
            return res
        }, {
            addtimeS: arrayTimeToStr(time),
            addtimeE: arrayTimeToStr(time, 1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })
        const result = await this.request({ url: '/qtEntrustPlatform/list', type: 'post' }, params)
        this.setState({
            dataSource: ckd(TE(result.list)),
            pageTotal: TE(result.totalCount),
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,

        })
    }
    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', className: 'wordLine', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '保值平台', className: 'wordLine', dataIndex: 'entrustPlatform' },
            { title: '保值平台委托ID', className: 'wordLine', dataIndex: 'entrustId' },
            { title: '成交数量', className: 'wordLine', dataIndex: 'entrustNum' },
            { title: '成交价格', className: 'wordLine moneyGreen', dataIndex: 'entrustPrice', render: t => toThousands(t, true) },
            { title: '交易类型', className: 'wordLine', dataIndex: 'entrustType', render: t => mapGet(TRADE_TYPE, t) },
            { title: '成交时间', className: 'wordLine', dataIndex: 'hedgeTime', render: t => dateToFormat(t) },
        ]
    }
    
    render() {
        const { showHide, pageIndex, pageSize, pageTotal, dataSource, time, entrustPlatform, types, entrustId } = this.state
        return (
            <div className="right-con">
                
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <PlatformsList title='保值平台' platform={entrustPlatform} handleChange={v => this.onSelectChoose(v, 'entrustPlatform')} />
                                </div>
                                <SeOp title='交易类型' value={types} ops={TRADE_TYPE} onSelectChoose={v => this.onSelectChoose(v, 'types')} pleaseC={false} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保值平台委托ID:</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="entrustId" value={entrustId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
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
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
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
                                    // scroll={islessBodyWidth() ? { x: 1800 } : {}}
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
