/**
 * @author oliver
 * @description  API访问数据
 */

import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, TIMEFORMAT_ss, DAYFORMAT, } from 'Conf'
import { Tabs, Button, Modal, Select, Popover, Checkbox, message, DatePicker } from 'antd'
import { arrayTimeToStr_ymd, dateToFormat_ymd, toThousands, ckd, arrayTimeToStr, isArray } from 'Utils';
import MarketList from '../../common/select/marketrequests'
import { RPicker } from '../../../components/date'


@Decorator()
export default class APIAccessData extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            time: [moment(moment().subtract(6, 'days').format(DAYFORMAT) + '00:00:00', TIMEFORMAT_ss), moment(moment().format(DAYFORMAT) + '23:59:59', TIMEFORMAT_ss)],
            market: 'btc_usdt',
            uri: '',
            ip: '',
        }

    }
    async componentDidMount() {
        console.log(moment(moment().subtract(6, 'days').format(DAYFORMAT) + '00:00:00', TIMEFORMAT_ss))

        this.requestTable()

    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time } = this.state

        const params = Object.keys(this.defaultState).reduce((res, key) => {
            void 0 !== this.state[key] && !isArray(this.state[key]) && (res[key] = this.state[key])
            return res
        }, {
                beginTime: arrayTimeToStr(time),
                endTime: arrayTimeToStr(time, 1),
                page: currIndex || pageIndex,
                limit: currSize || pageSize
            })
        const result = await this.request({ url: '/apiStat/getMarketVisit', }, params)
        this.setState({
            dataSource: ckd(result),
            pageTotal: TE(rs.result).totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,
        })

    }
    createColumns = () => {
        const { limitBtn, pageSize, pageIndex, } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '请求方IP', dataIndex: 'ip', },
            { title: '请求路径', dataIndex: 'name', },
            { title: '描述', dataIndex: 'message' },
            { title: '请求次数', dataIndex: 'market_visit_time' },
            { title: '盘口市场', dataIndex: 'market', },
            // { title: '日期', dataIndex: 'dateS', render: (t, r) => <span>{dateToFormat_ymd(r.dateS)} 至 {dateToFormat_ymd(r.dateE)}</span> },
        ]
    }

    render() {
        const { showHide, pageTotal, pageIndex, pageSize, dataSource, time, market, uri, ip } = this.state
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
                                    <MarketList underLine paymod market={market} title='盘口市场' handleChange={v => this.onSelectChoose(v, 'market')} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">请求路径:</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="uri" value={uri} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">请求IP:</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="ip" value={ip} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <RPicker time={time} title='时间' key='time' onChangeCheckTime={this.onChangeCheckTime} />
                                <div className="col-md-12 col-sm-12 col-xs-12 ">
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
                                <div className="table-responsive" >

                                    <CommonTable
                                        dataSource={dataSource}
                                        columns={this.createColumns()}
                                        requestTable={this.requestTable}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSize: pageSize,
                                                current: pageIndex
                                            }
                                        }
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