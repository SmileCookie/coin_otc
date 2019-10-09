//保值下单数量异常汇总
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select, DatePicker } from 'antd'
import MarketRequests from '../../common/select/marketrequests'
import { DOMAIN_VIP, PAGEINDEX, SHOW_TIME_DEFAULT, TIMEFORMAT_ss, SELECTWIDTH, TRADE_TYPE } from '../../../conf'
import { mapGet, arrayTimeToStr, dateToFormat_ymd, dateToFormat, ckd, isObj } from '../../../utils'
import { SeOp } from '../../../components/select/asyncSelect'
import { exportExcel } from 'xlsx-oc'
import axios from '../../../utils/fetch'
import moment from 'moment'
import AbnormalDetails from './abnormalDetails'

const { Option } = Select;
const { RangePicker } = DatePicker;

@Decorator()
export default class AbnormalSummary extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            entrustmarket: '',//交易市场
            entrusttype: '',
            state: '',
            savetimeS: '',
            savetimeE: '',
            time: []
        }
        this.state = {
            ...this.defaultState,
            width: '',
            modalHtml: '',
            title: '',
            visible: false,
            isabled: false,

        }
    }


    async componentDidMount() {
        await this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time, entrustmarket, entrusttype, state, } = this.state
        let params = {
            entrustmarket, entrusttype, state,
            savetimeS: time.length ? new Date(moment(time[0]).format(TIMEFORMAT_ss)).getTime() : '',
            savetimeE: time.length ? Number(new Date(moment(time[1]).format(TIMEFORMAT_ss)).getTime() + 999) : '',
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize,
        }
        const result = await this.request({ url: '/qtHedgingnumbersSum/query', type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    // 导出
    exportDefaultExcel = () => {
        this.setState({ loading: true });
        const { time, entrustmarket, entrusttype, state, } = this.state
        let params = {
            entrustmarket, entrusttype, state,
            savetimeS: time.length ? new Date(moment(time[0]).format(TIMEFORMAT_ss)).getTime() : '',
            savetimeE: time.length ? Number(new Date(moment(time[1]).format(TIMEFORMAT_ss)).getTime() + 999) : '',
        }

        axios.post(DOMAIN_VIP + '/qtHedgingnumbersSum/exportlist', qs.stringify(params)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let etData = result.data || [];
                if (etData.length) {
                    let str = '序号,交易市场,交易类型,用户成交总量,保值下单总量,保值成交总量,保值剩余总量,利润,日期,状态\n';

                    for (let i = 0; i < etData.length; i++) {
                        etData[i].index=i+1
                        str += etData[i].index+'\t'+','+etData[i].entrustmarket + ',' + etData[i].entrusttypeName + ',' + etData[i].usernum + ',' + etData[i].hedgingnum + ',' + etData[i].hedgingsuccessnum + ',' +
                            etData[i].hedgingsurplusnum + ',' + etData[i].profitsum + ',' + etData[i].savetimeDate +'\t'+ ',' + etData[i].stateName + '\n'
                    }
                    let blob = new Blob([str], { type: "text/plain;charset=utf-8" });
                    //解决中文乱码问题
                    blob = new Blob([String.fromCharCode(0xFEFF), blob], { type: blob.type });
                    let object_url = window.URL.createObjectURL(blob);
                    let link = document.createElement("a");
                    link.href = object_url;
                    link.download = "保值下单数量异常汇总.csv";
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);

                    this.setState({ loading: false });

                } else {
                    message.warning('没有数据，无法导出！')
                }

            } else {
                message.error(result.msg)
            }

        })

    }
    // 一键复查
    oneReview = async () => {
        await this.request({ url: '/qtHedgingnumbersSum/reviewall', type: 'post', isP: true })
        this.requestTable()

    }
    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '交易市场', dataIndex: 'entrustmarket', },
            { title: '交易类型', dataIndex: 'entrusttypeName', },
            { title: '用户成交总量', dataIndex: 'usernum' },
            { title: '保值下单总量', dataIndex: 'hedgingnum' },
            { title: '保值成交总量', dataIndex: 'hedgingsuccessnum' },
            {
                title: '保值剩余总量', dataIndex: 'hedgingsurplusnum', render: (text, record, index) => {
                    return (
                        <div>
                            <a href="javascript:;" style={{ display: 'block' }} onClick={() => this.showModal(record)}>{record.hedgingsurplusnum}</a>
                        </div>
                    )
                }
            },
            { title: '利润', dataIndex: 'profitsum' },
            { title: '日期', dataIndex: 'savetime', render: t => dateToFormat_ymd(t) },
            {
                title: '状态', dataIndex: 'stateName', render: (text, record) => {
                    return (
                        <span className={record.state ? '' : 'bac'}>
                            {text}
                        </span>
                    )
                }
            },
        ]
    }

    showModal = (item) => {
        this.setState({
            visible: true,
            title: "保值下单数量异常明细",
            width: '1200px',
            modalHtml: <AbnormalDetails item={item} />,
        })
    }

    handleCancel = () => {
        this.setState({
            visible: false
        })
    }



    render() {
        const { isabled, showHide, pageIndex, pageSize, title, visible, modalHtml, width, dataSource, pageTotal, time, entrustmarket, state, entrusttype } = this.state
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
                                    <MarketRequests market={entrustmarket} underLine={true} handleChange={(v) => this.onSelectChoose(v, 'entrustmarket')} col='3' />
                                </div>
                                <SeOp title='交易类型' value={entrusttype} ops={TRADE_TYPE} onSelectChoose={(v) => this.onSelectChoose(v, 'entrusttype')} pleaseC={false} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">状态：</label>
                                        <div className="col-sm-8 ">
                                            <Select value={state} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'state')}>
                                                <Option value="">请选择</Option>
                                                <Option value={1}>正常</Option>
                                                <Option value={0}>异常</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">日期：</label>
                                        <div className="col-sm-8 ">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: SHOW_TIME_DEFAULT
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={(v, d) => this.onChangeCheckTime(v, d, 'time')}
                                                placeholder={['Start Time', 'End Time']}
                                                value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        <Button type="primary" onClick={this.oneReview}>一键复查</Button>
                                        <Button type="primary" loading={this.state.loading} onClick={this.exportDefaultExcel}>导出</Button>
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
                                    // scroll={this.islessBodyWidth() ? { x: 1800 } : {}}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    title={title}
                    visible={visible}
                    width={width}
                    style={{ top: 50 }}
                    onCancel={this.handleCancel}
                    footer={null}
                >
                    {modalHtml}
                </Modal>

            </div>
        )
    }
}