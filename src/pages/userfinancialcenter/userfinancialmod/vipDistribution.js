/**vip&新人分配记录 */
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select, DatePicker } from 'antd'
import { PAGRSIZE_OPTIONS20, PAGESIZE, PAGEINDEX, TIMEFORMAT_ss, SELECTWIDTH } from '../../../conf'
import { toThousands, TE, mapGet, dateToFormat, arrayTimeToStr } from '../../../utils'
import ModalVip from './modal/modalVip'
const { Option } = Select;
const { RangePicker } = DatePicker;

@Decorator()
export default class VipDistribution extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            usdtPriceStart: '',//分配价格最小值
            usdtPriceEnd: '',//分配价格最大值
            distendtime: '',//分配结束时间
            diststarttime: '', // 分配开始时间
            diststatus: '',// 分配状态
            time: [],
            distbalstart: '',//分配金额开始
            distbalend: '',//分配金额结束
        }
        this.state = {
            ...this.defaultState,
            distflag: '',  // 是否可执行
            distnum: '',//  分配人数
            disttype: '', // 分配类型
            supernodeprofitcount: '',  //分配笔数
            distbal: "", //分配金额
            distbaloriginal: '',// 分配原始金额
            seqno: "", // 分配批次
            disttime: '',  // 分配时间
            updatetime: '',//结算时间
            usdtprice: '',//分配价格
            title: '',
            visible: false,
            width: '',
            modalHtml: '',
        }
    }
    async componentDidMount() {
        //await this.requestTable()
    }

    handleCancel = () => {
        this.setState({
            visible: false
        })
    }

    requestTable = async (currIndex, currSize) => {
        const { time, pageIndex, pageSize, diststatus, usdtPriceStart, usdtPriceEnd, distbalstart, distbalend } = this.state
        let params = {
            diststatus, usdtPriceStart, usdtPriceEnd, distbalstart, distbalend,
            distStartTime: arrayTimeToStr(time, 0),
            distEndTime: arrayTimeToStr(time, 1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/finUserRewardStatus/list', type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }

    // 新增/修改弹窗
    showModal = (item) => {
        let maxWidth = window.screen.width > 1500 ? "880px" : '660px'
        let titleName = '修改数据'
        this.setState({
            title: titleName,
            visible: true,
            width: maxWidth,
            modalHtml: <ModalVip item={item} handleCancel={this.handleCancel} requestTable={this.requestTable} />
        });
    };


    createColumns = (pageIndex, pageSize) => {
        let states = ['未分配', '已分配']
        let flagstates = ['不可执行', '可执行']
        let typestate = { 5: 'VIP分红', 7: '新人加成' }
        return [
            { title: '序号', className: 'wordLine', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '分配批次', className: 'wordLine', dataIndex: 'seqno', },
            { title: '分配时间', className: 'wordLine', dataIndex: 'disttime', render: t => t ? moment(t).format(TIMEFORMAT_ss) : '--' },
            { title: '分配笔数', className: 'wordLine', dataIndex: 'supernodeprofitcount' },
            { title: '分配类型', className: 'wordLine', dataIndex: 'disttype', render: t => typestate[t] },
            { title: '分配金额', className: 'wordLine', dataIndex: 'distbal' },
            { title: '分配原始金额', className: 'wordLine', dataIndex: 'distbaloriginal' },
            { title: 'distStatus', className: 'wordLine', dataIndex: 'diststatus', render: t => t },
            { title: 'distFlag', className: 'wordLine', dataIndex: 'distflag', render: t => t },
            { title: '分配开始时间', className: 'wordLine', dataIndex: 'diststarttime', render: t => t ? moment(t).format(TIMEFORMAT_ss) : '--' },
            { title: '分配结束时间', className: 'wordLine', dataIndex: 'distendtime', render: t => t ? moment(t).format(TIMEFORMAT_ss) : '--' },
            { title: '分配人数', className: 'wordLine', dataIndex: 'distnum' },
            { title: '分配价格', className: 'wordLine', dataIndex: 'usdtprice' },
            { title: '结算时间', className: 'wordLine', dataIndex: 'updatetime', render: t => t ? moment(t).format(TIMEFORMAT_ss) : '--' },
            // {
            //     title: '操作', className: 'wordLine',dataIndex: 'op', render: (text, record, index) => {
            //         return (
            //             <div>
            //                 <a href="javascript:;" onClick={() => this.showModal(record)} >修改</a>

            //             </div>
            //         )
            //     }
            // },
        ]
    }





    render() {
        const { usdtPriceStart, usdtPriceEnd, title, distbalend, distbalstart, visible, width, modalHtml, showHide, pageIndex, pageSize, dataSource, pageTotal, time, seqno, diststatus, distflag, disttype } = this.state
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
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">分配状态：</label>
                                        <div className="col-sm-9">
                                            <Select value={diststatus} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'diststatus')} >
                                                <Option value=''>请选择</Option>
                                                <Option value='0'>未分配</Option>
                                                <Option value='1'>已分配</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">分配价格：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" placeholder='最小值' className="form-control" name="usdtPriceStart" value={usdtPriceStart} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" placeholder='最大值' className="form-control" name="usdtPriceEnd" value={usdtPriceEnd} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">分配金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最小值' className="form-control" name="distbalstart" value={distbalstart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最大值' className="form-control" name="distbalend" value={distbalend} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">分配时间：</label>
                                        <div className="col-sm-8 ">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')}
                                                placeholder={['Start Time', 'End Time']}
                                                value={time} />

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
                                    // scroll={this.islessBodyWidth() ? { x: 1800 } : {}}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>





                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 60 }}
                    footer={null}
                    onCancel={this.handleCancel}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}