/**市场访问统计*/
import Decorator from 'DTPath'
import CommonTable from 'CTable'
import {TIMEFORMAT, SHOW_TIME_DEFAULT ,PAGEINDEX} from 'Conf'
import { Button, Select, message, DatePicker } from 'antd'
import { arrayTimeToStr, ckd,dateToFormat_ymd } from 'Utils';
import MarketList from '../../common/select/marketrequests'
const { RangePicker, } = DatePicker;

@Decorator()
export default class MarketStatistics extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            time: [],
            market: '',
            ip: '',
            uri: '',
            beginTime: '',
            endTime: '',

        }
        this.state = {
            ...this.defaultState,

        }

    }
    async componentDidMount() {

       await this.requestTable()

    }
    requestTable = async (currIndex, currSize) => {
        const { pageSize,pageIndex,time, market, ip, uri } = this.state
        let params = {
            market, ip, uri,
            beginTime: arrayTimeToStr(time),
            endTime: arrayTimeToStr(time, 1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize,
        }
        const result = await this.request({ url: '/apiStat/getMarketVisit',}, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize,

        })

    }
    createColumns = (pageIndex, pageSize) => {
       
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '请求方IP', dataIndex: 'ip', },
            { title: '请求路径', dataIndex: 'name', },
            { title: '描述', dataIndex: 'message' },
            { title: '请求次数', dataIndex: 'market_visit_time' },
            { title: '盘口市场', dataIndex: 'market', },
            { title: '真实地址', dataIndex: 'real_address', },
            { title: '统计日期', dataIndex: 'stat_date',render:t=>dateToFormat_ymd(t) },
        ]
    }

    render() {
        const { showHide, pageTotal, pageIndex, pageSize, dataSource, ip,time,uri, market} = this.state
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
                                    <MarketList underLine  market={market} title='盘口市场' handleChange={v => this.onSelectChoose(v, 'market')} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">请求路径：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="uri" value={uri} onChange={this.handleInputChange} />

                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">请求IP：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="ip" value={ip} onChange={this.handleInputChange} />

                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">日期：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: SHOW_TIME_DEFAULT
                                                }}
                                                format={TIMEFORMAT}
                                                onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>
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