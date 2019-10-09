/**用户投资信息 */
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select, DatePicker } from 'antd'
import { PROCESS_STATE, INVSET_TYPE, INVEST_MATRIX, PAGRSIZE_OPTIONS20, PAGESIZE, PAGEINDEX, TIMEFORMAT_ss, SELECTWIDTH, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER } from '../../../conf'
import { toThousands, TE, mapGet, dateToFormat, arrayTimeToStr } from '../../../utils'
import { SeOp } from '../../../components/select/asyncSelect'
const { RangePicker } = DatePicker;
const { Option } = Select;

@Decorator()
export default class Proinvestment extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            userid: '', //用户编号，
            username: '', //用户名，
            matrixlevel: '',//投资金额对应的矩阵
            investamountS: '',
            investamountE: '', //投资金额区间
            vdsusdtpriceS: '',
            vdsusdtpriceE: '',//协议价区间
            investtimeS: '',
            investTimeE: '',//时间区间
            investproperiodS: '',
            investproperiodE: '',//投资次数区间
            doublethrowflag: '',//   '投资类型，0首投，1增投，2自动复投, 3手动复投' ,
            ecologysystemdealflag: '',//   'VDS生态回馈处理标记,0是默认,1是已处理' ,
            leaderbonusdealflag: '',//  '全球领袖奖励处理标记,0是默认,1是已处理' ,
            time: [],

        }
        this.state = {
            ...this.defaultState,
            investamountTotal: '',//总投资金额
        }
    }
    async componentDidMount() {
        // this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time, userid, username, matrixlevel, investamountS, investamountE,
            investproperiodS, investproperiodE, vdsusdtpriceS, vdsusdtpriceE,
            doublethrowflag, ecologysystemdealflag, leaderbonusdealflag } = this.state
        let params = {
            userid, username, matrixlevel, investamountS, investamountE,
            investproperiodS, investproperiodE, vdsusdtpriceS, vdsusdtpriceE,
            doublethrowflag, ecologysystemdealflag,
            leaderbonusdealflag,
            investtimeS: arrayTimeToStr(time, 0),
            investtimeE: arrayTimeToStr(time, 1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }

        const result = await this.request({ url: '/productinvest/list', type: 'post' }, params)
        this.setState({
            dataSource: result.pageUtils.list || [],
            pageTotal: result.pageUtils.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,
            investamountTotal: result.investamountTotal,

        })


    }


    createColumns = (pageIndex, pageSize) => {
      
        return [
            { title: '序号', className: 'wordLine', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '用户编号', className: 'wordLine', dataIndex: 'userid' },
            { title: '用户名', className: 'wordLine', dataIndex: 'username' },
            { title: '投资类型', className: 'wordLine', dataIndex: 'doublethrowflag', render: t => mapGet(INVSET_TYPE, t) },
            { title: '投资金额', className: 'wordLine', dataIndex: 'investamount', },
            { title: '投资矩阵', className: 'wordLine', dataIndex: 'matrixlevel', render: t =>  mapGet(INVEST_MATRIX, t)},
            { title: 'VIP权重', className: 'wordLine', dataIndex: 'vipweight' },
            { title: '投资时间', className: 'wordLine', dataIndex: 'investtime', render: t => dateToFormat(t) },
            { title: '投资次数', className: 'wordLine', dataIndex: 'investproperiod' },
            { title: '协议价', className: 'wordLine', dataIndex: 'vdsusdtprice' },
            { title: '预期收益', className: 'wordLine', dataIndex: 'expectprofitusdt' },
            { title: '生态状态', className: 'wordLine', dataIndex: 'ecologysystemdealflag', render: t => mapGet(PROCESS_STATE, t) },
            { title: '领袖状态', className: 'wordLine', dataIndex: 'leaderbonusdealflag', render: t => mapGet(PROCESS_STATE, t) },
        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal, time, userid, username, matrixlevel, investamountS, investamountE, investproperiodS, investproperiodE, vdsusdtpriceS, vdsusdtpriceE,
            doublethrowflag, ecologysystemdealflag, leaderbonusdealflag, investamountTotal } = this.state
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
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="username" value={username} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>

                                <SeOp title='投资矩阵' value={matrixlevel} ops={INVEST_MATRIX} onSelectChoose={v => this.onSelectChoose(v, 'matrixlevel')} pleaseC />


                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">投资金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="investamountS" value={investamountS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="investamountE" value={investamountE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">投资次数：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="investproperiodS" value={investproperiodS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="investproperiodE" value={investproperiodE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">协议价：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="vdsusdtpriceS" value={vdsusdtpriceS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="vdsusdtpriceE" value={vdsusdtpriceE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <SeOp title='投资类型' value={doublethrowflag} ops={INVSET_TYPE} onSelectChoose={v => this.onSelectChoose(v, 'doublethrowflag')} pleaseC />
                                <SeOp title='生态处理状态' value={ecologysystemdealflag} ops={PROCESS_STATE} onSelectChoose={v => this.onSelectChoose(v, 'ecologysystemdealflag')} pleaseC />
                                <SeOp title='领袖处理状态' value={leaderbonusdealflag} ops={PROCESS_STATE} onSelectChoose={v => this.onSelectChoose(v, 'leaderbonusdealflag')} pleaseC />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">投资时间：</label>
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
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    投资总金额:{investamountTotal || 0} ，&nbsp;&nbsp;&nbsp;
                                                    总记录数：{pageTotal}
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
                                    // scroll={this.islessBodyWidth() ? { x: 1800 } : {}}
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