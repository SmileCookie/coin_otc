/**
 * @author oliver
 * @description 用户与刷量卖出分析
 */

import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, SELECTWIDTH, TIMEFORMAT, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER, DAYFORMAT } from 'Conf'
import { Tabs, Button, Modal, Select, Popover, Checkbox, message, DatePicker } from 'antd'
import { arrayTimeToStr_ymd,dateToFormat_ymd,toThousands,ckd } from 'Utils';
import MarketList from '../../common/select/marketrequests'

const { RangePicker, } = DatePicker;


@Decorator()
export default class UserAndBrushSalesAnalysis extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            time:[moment(),moment()],
            market:'btc_usdt'
        }
        this.state = {
            ...this.defaultState,

        }

    }
    async componentDidMount() {

        this.requestTable()

    }
    requestTable = async (currIndex, currSize) => {
        const {time,market } = this.state
        let params = {
            market,
            dateS: arrayTimeToStr_ymd(time),
            dateE: arrayTimeToStr_ymd(time, 1),
        }
        const result = await this.request({ url: '/tradeAnalysis/brushSell', type: 'post' }, params)
        this.setState({
            dataSource: ckd(result),
        })

    }
    createColumns = () => {
        const { limitBtn, pageSize, pageIndex } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '市场', dataIndex: 'market',  },
            { title: '用户卖出数量', dataIndex: 'coinNum',  },
            { title: '用户卖出总额', dataIndex: 'totalAmount',render: t => toThousands(t,true)  },
            { title: '用户卖出均价', dataIndex: 'avgPrice', render: t => toThousands(t,true) },
            { title: '交易人数（卖出）', dataIndex: 'userNum' },
            { title: '日期', dataIndex: 'dateS',render:(t ,r) => <span>{dateToFormat_ymd(r.dateS)} 至 {dateToFormat_ymd(r.dateE)}</span> },
        ]
    }

    render() {
        const { showHide, pageTotal, pageIndex, pageSize, dataSource, time,market } = this.state
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
                                    <MarketList underLine paymod market={market} title='市场' handleChange={v => this.onSelectChoose(v,'market')} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">日期：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: SHOW_TIME_DEFAULT
                                                }}
                                                format={DAYFORMAT}
                                                placeholder={TIME_PLACEHOLDER}
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
                                        columns={this.createColumns()}
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