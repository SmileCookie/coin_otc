/**用户理财信息 */
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select, DatePicker } from 'antd'
import { PAY_STATE, INVEST_MATRIX, PAGRSIZE_OPTIONS20, PAGESIZE, PAGEINDEX, TIMEFORMAT_ss, SELECTWIDTH, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER } from '../../../conf'
import { toThousands, TE, mapGet, dateToFormat,arrayTimeToStr} from '../../../utils'
import { SeOp } from '../../../components/select/asyncSelect'
const { RangePicker } = DatePicker;
const { Option } = Select;

@Decorator()
export default class Userfinancialinfo extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            userid: '',//用户ID
            username: "",//用户名
            uservid: '',//用户VID
            invitationcode: '',//我的邀请码
            pinvitationcode: '',//推进人邀请码
            invitationusername: '',//  '邀请人用户名' ,
            matrixlevel: '',//  '投资矩阵' ,
            authPayFlag: '',//支付状态，0默认值，1已认证，2已支付，3复投中'
            investTimeStart: '',
            investTimeEnd: '',//投资时间区间
            investavergpriceS: '',
            investavergpriceE: '',  //协议价区间
            time: [],
        }
        this.state = {
            ...this.defaultState,
            peopleNum:'',//投资人数
        }
    }
    async componentDidMount() {
        // this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, userid, username,
            uservid, invitationcode, pinvitationcode, invitationusername, matrixlevel,
            investavergpriceS, investavergpriceE, authPayFlag, time } = this.state
        let params = {
            userid, username,
            uservid, invitationcode, pinvitationcode, invitationusername, matrixlevel,
            investavergpriceS, investavergpriceE, authPayFlag,
            investTimeStart: arrayTimeToStr(time,0),
            investTimeEnd: arrayTimeToStr(time,1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/userFinancialInfo/queryList', type: 'post' }, params)

        this.setState({
            dataSource: result.pageUtils.list || [],
            pageTotal: result.pageUtils.totalCount,
            peopleNum:result.peopleNum,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,
        })


    }
    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', className: 'wordLine',dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '用户编号', className: 'wordLine', dataIndex: 'userid' },
            { title: '用户名', className: 'wordLine', dataIndex: 'username' },
            { title: '用户VID', className: 'wordLine', dataIndex: 'uservid' },
            { title: '用户邀请码', className: 'wordLine', dataIndex: 'invitationcode' },
            { title: '推荐人邀请码', className: 'wordLine', dataIndex: 'pinvitationcode' },
            { title: '推荐人用户名', className: 'wordLine', dataIndex: 'invitationusername' },
            { title: '支付状态', className: 'wordLine', dataIndex: 'authpayflag', render: t => mapGet(PAY_STATE, t) },
            { title: '投资金额', className: 'wordLine', dataIndex: 'investAmount', },
            { title: '投资矩阵', className: 'wordLine', dataIndex: 'matrixlevel', render: t => mapGet(INVEST_MATRIX, t) },
            { title: 'VIP权重', className: 'wordLine', dataIndex: 'vipweight' },
            { title: '投资时间', className: 'wordLine', dataIndex: 'profittime', render: t => dateToFormat(t) },
            { title: '投资次数', className: 'wordLine', dataIndex: 'reintimes' },
            { title: '协议价', className: 'wordLine', dataIndex: 'investavergprice' },
            { title: '预期收益', className: 'wordLine', dataIndex: 'expectprofitusdt', },
        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal, userid, username, uservid, invitationcode, pinvitationcode,
            matrixlevel, authPayFlag, time, invitationusername, investavergpriceE, investavergpriceS,peopleNum } = this.state
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
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户VID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="uservid" value={uservid} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户邀请码：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="invitationcode" value={invitationcode} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">推荐人邀请码：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="pinvitationcode" value={pinvitationcode} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">推荐人用户名：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="invitationusername" value={invitationusername} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>

                                <SeOp title='投资矩阵' value={matrixlevel} ops={INVEST_MATRIX} onSelectChoose={v => this.onSelectChoose(v, 'matrixlevel')} pleaseC />
                                <SeOp title='支付状态' value={authPayFlag} ops={PAY_STATE} onSelectChoose={v => this.onSelectChoose(v, 'authPayFlag')} pleaseC />

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">协议价：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="investavergpriceS" value={investavergpriceS} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="investavergpriceE" value={investavergpriceE} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
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
                                    <div className='table-total'>
                                        投资人数：{peopleNum || 0}
                                    </div>
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