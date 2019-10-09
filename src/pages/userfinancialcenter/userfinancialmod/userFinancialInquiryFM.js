/**用户理财查询 */
import Decorator from '../../decorator'
import FundsTypeList from '../../common/select/fundsTypeList'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select, DatePicker } from 'antd'
import { PAGRSIZE_OPTIONS20, PAGESIZE, PAGEINDEX, TIMEFORMAT_ss, SELECTWIDTH, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER } from '../../../conf'
import { toThousands } from '../../../utils'
const { RangePicker } = DatePicker;
const { Option } = Select;
const Big = require('big.js')

@Decorator()
export default class UserFinancialInquiryFM extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            userid: '',
            username: "",
            uservid: '',
            usertype: '',
            invitationcode: '',
            pinvitationcode: '',
            invitationtotalnumS: '',
            invitationtotalnumE: '',
            investAmountStart: '',
            investAmountEnd: '',
            time: [],
            authPayFlag: ''
        }
        this.state = {
            ...this.defaultState,
            proTotalUser: '',
            proTotalAmount: ''
        }
    }
    async componentDidMount() {
        this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, userid, username, uservid, usertype, invitationcode,
            pinvitationcode, invitationtotalnumS, invitationtotalnumE, investAmountStart, investAmountEnd, time,
            authPayFlag } = this.state
        let params = {
            userid, username, uservid, usertype, invitationcode,
            pinvitationcode, invitationtotalnumS, invitationtotalnumE,
            investAmountStart, investAmountEnd, authPayFlag,
            investTimeStart: time.length ? moment(time[0]).format(TIMEFORMAT_ss) : '',
            investTimeEnd: time.length ? moment(time[1]).format(TIMEFORMAT_ss) : '',
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/userFinancialInfo/list', type: 'post' }, params)
        this.setState({
            dataSource: result.page.list || [],
            pageTotal: result.page.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,
            proTotalUser: result.prod && result.prod.proTotalUser,
            proTotalAmount: result.prod && result.prod.proTotalAmount
        })

    }
    createColumns = (pageIndex, pageSize) => {
        let states = ['停用', '正常',]
        let authPayFlag = {
            1: '已认证',
            2: '已支付',
            default: '--'
        }
        Big.RM = 0;
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '用户编号', dataIndex: 'userid' },
            { title: '用户名', dataIndex: 'username' },
            { title: '用户类型', dataIndex: 'usertypename' },
            { title: '用户VID', dataIndex: 'uservid' },
            { title: '用户邀请码', dataIndex: 'invitationcode' },
            { title: '支付状态', dataIndex: 'authpayflag', render: t => authPayFlag[t] || authPayFlag.default },
            { title: '投资金额', className: 'moneyGreen', dataIndex: 'investAmount', render: t => toThousands(t, true) },
            { title: '投资时间', dataIndex: 'investTime', render: t => t ? moment(t).format(TIMEFORMAT_ss) : '--' },
            { title: '推荐人邀请码', dataIndex: 'pinvitationcode' },
            { title: '邀请人数', dataIndex: 'invitationtotalnum', },
        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal, userid, username, uservid, usertype, pinvitationcode, invitationcode, invitationtotalnumS,
            invitationtotalnumE, proTotalAmount, proTotalUser, time, investAmountStart, investAmountEnd, authPayFlag } = this.state
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
                                        <label className="col-sm-3 control-label">用户类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={usertype} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'usertype')} >
                                                <Option value="">请选择</Option>
                                                <Option value="04">公司账户-融资融币</Option>
                                                <Option value="05">公司账户-测试用户</Option>
                                                <Option value="07">公司账户-量化交易</Option>
                                                <Option value="01">普通用户</Option>
                                                <Option value="06">其他用户</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">支付状态：</label>
                                        <div className="col-sm-9">
                                            <Select value={authPayFlag} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'authPayFlag')} >
                                                <Option value="">请选择</Option>
                                                <Option value="1">已认证</Option>
                                                <Option value="2">已支付</Option>
                                            </Select>
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
                                        <label className="col-sm-3 control-label">邀请人数：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="invitationtotalnumS" value={invitationtotalnumS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="invitationtotalnumE" value={invitationtotalnumE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">投资金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最小值' className="form-control" name="investAmountStart" value={investAmountStart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最大值' className="form-control" name="investAmountEnd" value={investAmountEnd} onChange={this.handleInputChange} /></div>
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
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="9" className="column-title">
                                                    投资总人数：{proTotalUser || 0},&nbsp;&nbsp;&nbsp;&nbsp;
                                                    投资总金额：{toThousands(proTotalAmount, true)}
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