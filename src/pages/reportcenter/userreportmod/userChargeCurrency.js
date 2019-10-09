/**
 * @author Oliver
 * @description 用户充提币种
 */

import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, SELECTWIDTH, TIMEFORMAT, USERLIMIT, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER, DAYFORMAT } from 'Conf'
import { Tabs, Button, Modal, Select, Popover, Checkbox, message, DatePicker } from 'antd'
import { arrayTimeToStr_ymd,dateToFormat_ymd,ckd,toThousands } from 'Utils';
import FundsTypeList from '../../common/select/fundsTypeList'


const { RangePicker, } = DatePicker;


@Decorator()
export default class UserChargeCurrency extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            time:[moment(),moment()],
            fundsType:'0'
        }
        this.state = {
            ...this.defaultState,

        }

    }
    async componentDidMount() {

        this.requestTable()

    }
    requestTable = async (currIndex, currSize) => {
        const {time,fundsType } = this.state
        let params = {
            fundsType,
            dateS: arrayTimeToStr_ymd(time),
            dateE: arrayTimeToStr_ymd(time, 1),
        }
        const result = await this.request({ url: '/rwAnalysis/queryList', type: 'post' }, params)
        const balanceCount = await this.request({url: '/rwAnalysis/queryBalance',type:'post'},{fundsType})
        this.setState({
            dataSource: ckd(result),
            balanceCount: balanceCount[0].balanceCount,
        })

    }
    createColumns = () => {
        const { limitBtn, pageSize, pageIndex } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '币种', dataIndex: 'fundsTypeName',  },
            { title: '充值人数', dataIndex: 'rechargeAnalysisCount',  },
            { title: '提现人数', dataIndex: 'withdrawalAnalysisCount' },
            { title: '交易人数', dataIndex: 'tradeAnalysisCount' },
            { title: '日期', dataIndex: 'dateS',render:(t ,r) => <span>{dateToFormat_ymd(r.dateS)} 至 {dateToFormat_ymd(r.dateE)}</span> },
        ]
    }

    render() {
        const { showHide, pageTotal, pageIndex, pageSize, dataSource, time,fundsType,balanceCount } = this.state
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
                                <FundsTypeList title='币种' fundsType={fundsType} handleChange={v => this.onSelectChoose(v,'fundsType')} />
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
                                    <div className='table-total'>
                                        当前持币人数（≥100USD）：{toThousands(balanceCount)}
                                    </div>
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