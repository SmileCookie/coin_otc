import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, SELECTWIDTH, TIME_PLACEHOLDER, SHOW_TIME_DEFAULT, TIMEFORMAT_ss } from 'Conf'
import { Tabs, Button, Modal, Select, DatePicker } from 'antd'
import { SeOp } from '../../../components/select/asyncSelect'
import { toThousands, dateToFormat, arrayTimeToStr } from 'Utils'
import ViewMerchantCer from './viewMerchantCer'
import UserInfoDetail from './userInfoDetail'
const { TabPane } = Tabs;
const { RangePicker } = DatePicker
const Option = Select.Option
const _mch = {
    1: '商家认证',
    2: '商家取消'
}
@Decorator()
export default class MerchantCertification extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            userId: '',
            userName: '',
            realName: '',
            auditUserName: '',
            type: '1',
            time: [],
            AuditTime: []
        }
        this.state = {
            ...this.defaultState,
            tk: '0',
            pageTabs: 1,
            detailUser: null,

        }
    }
    componentDidMount() {
        this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, userId, userName, auditUserName, realName, type, tk, time, AuditTime
        } = this.state

        let params = Object.assign({
            userId, userName, auditUserName, realName, type,
            status: tk,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }, tk == '0' ? {
            startSendTime: arrayTimeToStr(time),
            endSendTime: arrayTimeToStr(time, 1),
        } : {
                    startAuditTime: arrayTimeToStr(AuditTime),
                    endSAuditTime: arrayTimeToStr(AuditTime, 1),
                })
        const result = await this.request({ url: '/storeAuth/query', type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    createColumns = (pageIndex, pageSize) => {
        let statuss = ['待审核', '通过', '拒绝']
        const { tk } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '申请时间', dataIndex: 'sendTime', render: t => dateToFormat(t) },
            { title: '审核人', className: `${tk == '0' ? 'hide' : ''}`, dataIndex: 'auditUserName', },
            { title: '审核时间', className: `${tk == '0' ? 'hide' : ''}`, dataIndex: 'auditTime', render: t => dateToFormat(t) },
            { title: '用户编号', dataIndex: 'userId', render: (t, r) => <a href="javascript:void(0)" className="mar10" onClick={() => this.toIssue(3, r)}>{t}</a> },
            { title: '账户名称', dataIndex: 'userName' },
            { title: '真实姓名', dataIndex: 'realName', },
            { title: '申请类型', dataIndex: 'type', render: t => _mch[t] || '--' },
            // { title: '所在地区', dataIndex: 'areaInfo', },
            { title: '状态', dataIndex: 'status', render: t => statuss[t] || '--' },
            {
                title: '操作', render: (t, r) => <a href="javascript:void(0)" className="mar10" onClick={() => this.toIssue(2, { id: r.id, userId: r.userId })}>查看</a>
            },
        ]
    }
    tcb = async tk => {
        await this.setState({ tk, })
        this.requestTable()
    }
    toIssue = (pageTabs = 1, detailUser = null) => this.setState({ pageTabs: pageTabs, detailUser, })
    render() {
        const { showHide, pageTotal, pageIndex, pageSize, dataSource, tk, userId, userName, auditUserName, time, type, realName, pageTabs, detailUser, AuditTime } = this.state
        return (
            <div className="right-con">
                {(() => {
                    switch (pageTabs) {
                        case 1:
                            return <div>
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
                                                        <label className="col-sm-3 control-label">用户名：</label>
                                                        <div className="col-sm-8 ">
                                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                                            <b className="icon-fuzzy">%</b>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                                    <div className="form-group">
                                                        <label className="col-sm-3 control-label">真实姓名：</label>
                                                        <div className="col-sm-8 ">
                                                            <input type="text" className="form-control" name="realName" value={realName} onChange={this.handleInputChange} />
                                                            <b className="icon-fuzzy">%</b>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                                    <div className="form-group">
                                                        <label className="col-sm-3 control-label">审核人：</label>
                                                        <div className="col-sm-8 ">
                                                            <input type="text" className="form-control" name="auditUserName" value={auditUserName} onChange={this.handleInputChange} />
                                                            <b className="icon-fuzzy">%</b>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                                    <div className="form-group">
                                                        <label className="col-sm-3 control-label">用户编号：</label>
                                                        <div className="col-sm-8 ">
                                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                                        </div>
                                                    </div>
                                                </div>
                                                <SeOp title='认证类型' value={type} onSelectChoose={v => this.onSelectChoose(v, 'type')} ops={_mch} pleaseC={false} />
                                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                                    <div className="form-group">
                                                        <label className="col-sm-3 control-label">申请时间：</label>
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
                                                {tk != '0' && <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                                    <div className="form-group">
                                                        <label className="col-sm-3 control-label">审核时间</label>
                                                        <div className="col-sm-8">
                                                            <RangePicker
                                                                showTime={{
                                                                    defaultValue: SHOW_TIME_DEFAULT
                                                                }}
                                                                format={TIMEFORMAT_ss}
                                                                placeholder={TIME_PLACEHOLDER}
                                                                onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'AuditTime')}
                                                                value={AuditTime}
                                                            />
                                                        </div>
                                                    </div>
                                                </div>}
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
                                                    <Tabs onChange={this.tcb} defaultActiveKey='0' activeKey={tk}>
                                                        <TabPane tab='待审核' key={'0'}></TabPane>
                                                        <TabPane tab='已通过' key={'1'}></TabPane>
                                                        <TabPane tab='已拒绝' key={'2'}></TabPane>
                                                    </Tabs>
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
                        case 2:
                            //商家审核详情
                            return <ViewMerchantCer jumpPage={this.props._this.add} detailUser={detailUser} toIssue={this.toIssue} />
                        case 3:
                            //用户信息详情
                            return <UserInfoDetail permissList={this.props.permissList} toIssue={this.toIssue} user={this.state.detailUser} />
                        default:
                            break
                    }
                })()}
            </div>
        )
    }
}