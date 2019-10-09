//保值下单数量异常明细--弹窗
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select, DatePicker } from 'antd'
import MarketRequests from '../../common/select/marketrequests'
import { DOMAIN_VIP, PAGEINDEX, SHOW_TIME_DEFAULT, TIMEFORMAT_ss, TRADE_TYPE, SHOW_TYPE, DAYFORMAT, SELECTWIDTH } from '../../../conf'
import { toThousands, mapGet, arrayTimeToStr, dateToFormat_ymd, dateToFormat, ckd, isObj } from 'Utils';
import { SeOp } from '../../../components/select/asyncSelect'
import ExternalNetworkComDetails from './externalNetworkComDetails'
import { exportExcel } from 'xlsx-oc'
import axios from '../../../utils/fetch'
const { RangePicker } = DatePicker
const { Option } = Select;

@Decorator()
export default class AbnormalDetails extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            entrustmarket: '',//交易市场
            tradingid: '',//成交编号
            userid: '',//用户编号
            types: '',//交易类型
            entrustFromId: '',//来源ID
            state: '',//状态：0 异常 1 正常
            addtimeS: '',//成交时间开始
            addtimeE: '',//成交时间结束
            time: []

        }
        this.state = {
            ...this.defaultState,
            width: '',
            modalHtml: '',
            title: '',
            visible: false,
        }
    }
    componentDidMount() {
        const { entrustmarket, entrusttype, savetime } = this.props.item
        this.setState({
            entrustmarket, types: entrusttype,
            time: [moment(this.timeChange(savetime)), moment(this.timeChange(savetime) + 24 * 3600 * 1000 - 1000)]
        }, () => {
            this.requestTable(1)
        })

    }

    componentWillReceiveProps(nextprops) {
        const { entrustmarket, entrusttype, savetime } = nextprops.item
        this.setState({
            entrustmarket, types: entrusttype,
            time: [moment(this.timeChange(savetime)), moment(this.timeChange(savetime) + 24 * 3600 * 1000 - 1000)]
        }, () => {
            this.requestTable(1)
        })

    }


    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time, entrustmarket, tradingid, userid, types, entrustFromId, state, } = this.state

        const params = {
            entrustmarket, tradingid, userid, types, entrustFromId, state,
            addtimeS: time.length ? new Date(moment(time[0]).format(TIMEFORMAT_ss)).getTime() : '',
            addtimeE: time.length ? Number(new Date(moment(time[1]).format(TIMEFORMAT_ss)).getTime() + 999) : '',
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex

        }
        const result = await this.request({ url: '/coinQtHedgingnumbers/list', type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }

    // 时间转换
    timeChange = (t) => {
        const gettime = Number(new Date(t).getTime());
        const hours = Number(new Date(t).getHours()) * 60 * 1000 * 60;
        const minutes = Number(new Date(t).getMinutes()) * 60 * 1000;
        const seconds = Number(new Date(t).getSeconds()) * 1000;
        const time = Number(gettime) - Number(hours) - Number(minutes) - Number(seconds)
        return time
    }

    getDetails = (item) => {
        this.setState({
            visible: true,
            title: "外网委托详情",
            width: '1200px',
            modalHtml: <ExternalNetworkComDetails item={item} />,
        })

    }
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }

    // 导出
    exportDefaultExcel = () => {
        this.setState({ loading: true });
        const { time, entrustmarket, tradingid, userid, types, entrustFromId, state, } = this.state
        let params = {
            entrustmarket, tradingid, userid, types, entrustFromId, state,
            addtimeS: time.length ? new Date(moment(time[0]).format(TIMEFORMAT_ss)).getTime() : '',
            addtimeE: time.length ? Number(new Date(moment(time[1]).format(TIMEFORMAT_ss)).getTime() + 999) : '',
        }

        axios.post(DOMAIN_VIP + '/coinQtHedgingnumbers/download', qs.stringify(params)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let etData = result.data || [];
                if (etData.length) {
                    let str = '序号,交易市场,用户编号,成交编号,来源ID,用户成交数量,交易类型,保值成交数量,保值类型,保值剩余数量,成交时间,状态\n';

                    for (let i = 0; i < etData.length; i++) {
                        etData[i].index = i + 1
                        str += etData[i].index + '\t' + ',' + etData[i].entrustmarket + ',' + etData[i].userid + '\t' + ',' + etData[i].tradingid + '\t' + ',' + etData[i].entrustfromid + ',' + etData[i].numbers + ',' +
                            etData[i].typestr + ',' + etData[i].transactionnumbers + ',' + etData[i].hedgingtypestr + ',' + etData[i].hedgingnumbers + ',' + etData[i].addtimestr + '\t' + ',' + etData[i].statestr + '\n'
                    }
                    let blob = new Blob([str], { type: "text/plain;charset=utf-8" });
                    //解决中文乱码问题
                    blob = new Blob([String.fromCharCode(0xFEFF), blob], { type: blob.type });
                    let object_url = window.URL.createObjectURL(blob);
                    let link = document.createElement("a");
                    link.href = object_url;
                    link.download = "保值下单数量异常明细.csv";
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
        await this.request({ url: '/coinQtHedgingnumbers/reviewall', type: 'post', isP: true })
        this.requestTable()

    }

    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '交易市场', dataIndex: 'entrustmarket', },
            { title: '用户编号', dataIndex: 'userid' },
            { title: '成交编号', dataIndex: 'tradingid' },
            { title: '来源ID', dataIndex: 'entrustfromid' },
            { title: '用户成交数量', dataIndex: 'numbers' },
            { title: '交易类型', dataIndex: 'types', render: t => mapGet(TRADE_TYPE, t) },
            { title: '保值成交数量', dataIndex: 'transactionnumbers' },
            { title: '保值类型', dataIndex: 'hedgingtypes', render: t => mapGet(TRADE_TYPE, t) },
            { title: '保值剩余数量', dataIndex: 'hedgingnumbers' },
            { title: '成交时间', dataIndex: 'addtime', render: t => dateToFormat(t) },
            {
                title: '状态', dataIndex: 'state', render: (t, record) => {
                    return (
                        <span className={record.state ? '' : 'bac'}>
                            {mapGet(SHOW_TYPE, t)}
                        </span>
                    )
                }
            },
            {
                title: '外网委托', dataIndex: 'entrust', render: (text, record, index) => {
                    return (
                        <div>
                            <a href="javascript:;" onClick={() => this.getDetails(record)}>详情</a>
                        </div>
                    )
                }
            },
        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, title, visible, modalHtml, width, dataSource, pageTotal, entrustmarket, tradingid, userid, types, entrustFromId, state, time } = this.state
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
                                <SeOp title='交易类型' value={types} ops={TRADE_TYPE} onSelectChoose={(v) => this.onSelectChoose(v, 'types')} pleaseC={false} />
                                <SeOp title='状态' value={state} ops={SHOW_TYPE} onSelectChoose={(v) => this.onSelectChoose(v, 'state')} pleaseC />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange} />

                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="tradingid" value={tradingid} onChange={this.handleInputChange} />

                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">来源ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="entrustFromId" value={entrustFromId} onChange={this.handleInputChange} />

                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交时间</label>
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
                                <div className="col-md-12 col-sm-12 col-xs-12 ">
                                    <div className="col-md-8 col-sm-8 col-xs-8 right">
                                        <div className="right">
                                            <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                            <Button type="primary" onClick={this.resetState}>重置</Button>
                                            <Button type="primary" onClick={this.oneReview}>一键复查</Button>
                                            <Button type="primary" loading={this.state.loading} onClick={this.exportDefaultExcel}>导出</Button>
                                        </div>
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