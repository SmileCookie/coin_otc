import axios from '../../../utils/fetch'
import { PAGEINDEX, PAGESIZE, SELECTWIDTH, PAGRSIZE_OPTIONS20, TIME_PLACEHOLDER, TIMEFORMAT_ss, SHOW_TIME_DEFAULT, DAYFORMAT, DOMAIN_VIP} from 'Conf'
import { Button, DatePicker, Tabs, Modal, Select, message, Table, } from 'antd'
import { toThousands, pageLimit, islessBodyWidth } from 'Utils'
import { CommonHedgeResults, JudgeHedgeResults } from '../../common/select/commonHedgeResults'
import CommonTable from 'CTable'
import Decorator from 'DTPath'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, } = DatePicker;
const Option = Select.Option;

@Decorator()
export default class LegalCoinCurrencyAccountOTC extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            time: [],
            fundsType: '0',
        }
        this.state = {
            ...this.defaultState,
            hedgeResults: {}
        }
        this.handleChangeType = this.handleChangeType.bind(this)
    }
    componentDidMount() {
        this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time, fundsType } = this.state
        let params = {
            reportdateStart: time.length ? moment(time[0]).format('YYYY-MM-DD HH:mm:ss') : '', 
            reportdateEnd: time.length ? moment(time[1]).format('YYYY-MM-DD HH:mm:ss') : '', 
            fundstype: fundsType,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/billReconciliationOtc/list', type: 'post' }, params)
        this.setState({
            dataSource: result.list,
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    handleChangeType(value) {
        this.setState({
            fundsType: value
        })
    }
    updateStatus = id => {
        let self = this;
        Modal.confirm({
            title:'你确定要改变状态吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                let params = {id}
                self.request({ url: '/billReconciliationOtc/update', type: 'post' }, params).then(res => {
                    self.requestTable()
                })
            },
            onCancel(){
                console.log('Cancel')
            } 
        })
    }
    createColumns = (pageIndex, pageSize) => {
        const { hedgeResults } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '资金类型', dataIndex: 'fundstypeName' },
            { title: <React.Fragment>平台对账<br/>(E-F)-(G+H)</React.Fragment>, dataIndex: 'difference', className: "moneyGreen", render: (text)=>toThousands(text,true) },
            { title: <React.Fragment>用户转入<br/>(E)</React.Fragment>, dataIndex: 'shiftto', className: "moneyGreen", render: (text)=>toThousands(text,true) },
            { title: <React.Fragment>用户转出<br/>(F)</React.Fragment>, dataIndex: 'rollout', className: "moneyGreen", render: (text) => toThousands(text,true) },
            { title: <React.Fragment>OTC账户余额<br/>(G)</React.Fragment>, dataIndex: 'balance', className: "moneyGreen", render: (text)=>toThousands(text,true) },
            { title: <React.Fragment>交易广告费<br/>(H)</React.Fragment>, dataIndex: 'transactionfee', className: "moneyGreen", render: (text)=>toThousands(text,true) },
            { title: '对账日期', dataIndex: 'reportdate', render: text => text ? moment(text).format(DAYFORMAT) : '' },
            { title: '状态', dataIndex: 'state', render: (state,record) => state == 0 ? '正常' : <a href='javascript:void(0);' onClick={()=>this.updateStatus(record.id)} >异常</a> },
        ]
    }
    render() {
        const { showHide, time, pageIndex, pageSize, pageTotal, dataSource, fundsType } = this.state
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
                                    <FundsTypeList  fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对账日期：</label>
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
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
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