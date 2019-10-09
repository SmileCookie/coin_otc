import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, SELECTWIDTH, TIMEFORMAT, USERLIMIT, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER, TIMEFORMAT_ss } from 'Conf'
import { SeOp } from '../../../components/select/asyncSelect'
import { Tabs, Button, Modal, Select, Popover, Checkbox, message, DatePicker,Tag } from 'antd'
import ModalMemo from './moadl/modalMemo'
import UserType from './moadl/userType'
import ModalSafeManage from './moadl/modalSafeManage'
import ModalCustomerOpe from './moadl/modalCustomerOpe'
import UserInfoDetail from './userInfoDetail'
import { arrayTimeToStr, dateToFormat, userLimit,getRandomColor } from '../../../utils';
const { TabPane } = Tabs;
const CheckboxGroup = Checkbox.Group
const Option = Select.Option
const { RangePicker, } = DatePicker;

const _customerType = {
    '0': '请选择',
    '01': '用户账户',
    '04': 'VDS刷量账户',
    '05': '测试账户',
    // '06':'其他用户',
    '07': '刷量账户',
    '09': '公司账户',
}
const _customerOperation = {
    '0': '请选择',
    1: '正常',
    2: '受限'
}
const _freez = {
    '0': '请选择',
    1: '正常',
    2: '冻结'
}

const Content = ({ value, popChange, allPopList }) => {
    return <div style={{ minWidth: '120px', maxHeight: '350px', overflow: 'auto', background: '#fff' }} className='side-hidden-print'>
        <CheckboxGroup value={value} onChange={popChange}>
            {[...allPopList].map(v => <div key={v}><Checkbox value={v}>{v}</Checkbox><br /></div>)}
        </CheckboxGroup>
    </div>
}

@Decorator({ lb: 'userInfo' })
export default class UserInfo extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            customerType: '0',
            userId: '',
            userName: '',
            loginIp: '',
            recommendName: '',
            freez: '0',
            customerOperation: '0',
            memo: '',
            loginTime: [],   //登录时间
            registerTime: [], //注册时间
            nickname: '',// 用户昵称

        }
        this.state = {
            ...this.defaultState,
            tk: '1',
            visible: false,
            title: '',
            width: '',
            _tags: [],
            tag: '',
            pageTabs: true,
            detailUser: null,
            popVisible: false,
            popList: [],//选中pop列
            allPopList: [],//全部的pop列
            tHeadList: [],//表头
            tableRowKeys: []
        }
        this.goofn = () => new Map([
            ['clearIP', v => this.onClearIpBtn(v)], //清理IP
            ['freezeOrNo', v => this.isOkey(v)],    //是否冻结账户
            ['custype', v => this.modalCustomerBtn(v)], //修改客户操作类型
            ['batchFreeze', () => this.batchFreezeBtn()],   //批量冻结
            ['cusope', v => this.modalCustomerOpeBtn(v)],   //修改账户类型
            ['sensitiveUser', v => this.sensitiveUserBtn(v)],    //普通|敏感用户
            ['userQualificat', v => this.userQualificatBtn(v)], //修改用户资质
        ])
    }
    async componentDidMount() {
        let result = await this.request({ url: '/common/tag', type: 'post' })
        let allPopList = this.createColumns().map(v => v.title)
        await this.setState({
            _tags: result,
            tHeadList: this.createColumns(),
            allPopList,
            popList: allPopList
        })
        this.requestTable()

    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, customerType, userId, userName, UserInfo, recommendName,
            customerOperation, freez, memo, tk, tag, loginTime, registerTime, nickname } = this.state
        let params = {
            customerType, userId, userName, UserInfo, recommendName,
            customerOperation, memo, tabName: tag,
            userType: tk,
            nickname,
            stime: arrayTimeToStr(registerTime),
            etime: arrayTimeToStr(registerTime, 1),
            slastLoginTime: arrayTimeToStr(loginTime),
            elastLoginTime: arrayTimeToStr(loginTime, 1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/userInfo/queryUser', type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,
        })

    }
    //修改账户类型
    modalCustomerOpe = (v, check) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.modalGoogleCode(v, 'cusope', check)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible: true,
            width: '600px',
            title: ' 修改客户操作类型',
            // defaultValue:item.customerOperation||[],
            modalHtml: <ModalCustomerOpe item={v} onChange={this.onChangeCheckbox} onlySee={false} />

        })
    }
    //checkbox 
    onChangeCheckbox = (checkedValues) => {
        if (checkedValues.target && checkedValues.target.name == 'modifyTextArea') {
            this.setState({
                modifyTextArea: checkedValues.target.value,
            })
        } else {
            this.setState({
                checkedValues: checkedValues,
            })
        }
    }
    //修改客户操作类型 button
    modalCustomerOpeBtn = async (item) => {
        let { checkedValues, modifyTextArea } = this.state;
        if (checkedValues.length == 0) {
            checkedValues.push('03')
        } else if (checkedValues.length > 1) {
            checkedValues = checkedValues.filter((currentValue, index, arr) => {
                return currentValue !== '03'
            })
        }
        await this.request({ url: '/userInfo/modifyCustomerOperation', type: 'post' }, {
            userId: item.id,
            customerOperation: checkedValues.join(),
            operationMark: modifyTextArea || ''
        })
        this.handleCancel()
        this.requestTable()
    }
    createColumns = () => {
        const { pageSize, pageIndex } = this.state
        const { ckAuth } = this
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '用户编号', dataIndex: 'id', render: (t, r) => <a href="javascript:void(0)" onClick={() => this.toIssue({ userId: r.id })}>{t}</a> },
            { title: '邮箱', dataIndex: 'email' },
            { title: '用户名', dataIndex: 'userName' },
            { title: '用户昵称', dataIndex: 'nickname' },
            { title: '手机号', dataIndex: 'mobile' },
            { title: '账户类型', dataIndex: 'customerTypeName', render: (t, r) => ckAuth('modifyCustomerType') ? <a href="javascript:void(0)" onClick={() => this.modalCustomerType(r, 'check')}>{t}</a> : t },
            { title: '用户资质', dataIndex: 'userQualificationName', render: (t, r) => r.userQualification == 3 ? <a href="javascript:void(0)" onClick={() => this.userQualificat(r)} >{t}</a> : t },
            { title: '用户等级', dataIndex: 'vipRate', },
            { title: '用户限制', dataIndex: 'customerOperation', render: (t, r) => ckAuth('modifyCustomerOperation') && <a href="javascript:void(0)" onClick={() => this.modalCustomerOpe(r, 'check')}>{userLimit(t)}</a> },
            { title: '敏感用户', dataIndex: 'warningUserName', render: (t, r) => <a href="javascript:void(0)" onClick={() => this.sensitiveUser(r)}>{t}</a> },
            { title: '最近一次登录IP', dataIndex: 'loginIp', },
            { title: '冻结标识', dataIndex: 'freezeName', render: (t, r) => ckAuth('freez') ? <a href="javascript:void(0)" onClick={() => this.freezeUserModal(r, 'check')}>{t}</a> : t },
            // { title: '用户标签', dataIndex: 'systemUuserTags', render: (t, r) => <span>{t && <Tag color='cyan'>{t}</Tag>} {r.userTags && r.userTags.split(',').map(v => <Tag key={v} color={getRandomColor()}>{v}</Tag>)}</span> },
            { title: '用户标签', dataIndex: 'systemUuserTags', render: (t, r) => <span>{t}{r.userTags}</span> },
            { title: '注册时间', dataIndex: 'registerTime', render: t => dateToFormat(t) },
            {
                title: '操作', dataIndex: 'op', render: (t, r) => <span>
                    {ckAuth('saveMemoById') && <a className="mar10" href="javascript:void(0);" onClick={() => this.showModal(r)}>备注</a>}
                    {ckAuth('safeMange') && <a className="mar10" href="javascript:void(0);" onClick={() => this.onSafeManage(r)}>安全管理</a>}
                </span>
            },
        ]
    }
    tcb = async tk => {
        await this.setState({ tk })
        this.requestTable(PAGEINDEX)
    }
    onSelectTableRows = (tableRowKeys, tableRows) => {
        this.setState({
            tableRowKeys,
            tableRows,
        })
    }
    //修改用户资质 
    /**
     * @author oliver
     * @description 只能将商家认证 更改为实名认证
     */
    userQualificat = ({ id, userQualificationName }) => {
        this.md({ title: `你确定要将${userQualificationName.split('-')[0] || '商家认证'}改为实名认证吗？`, }, () => this.modalGoogleCode(id, 'userQualificat'))
    }
    userQualificatBtn = async id => {
        await this.request({ url: '/userInfo/updateUserQualification', type: 'post', isP: true }, { userId: id })
        this.requestTable()
    }


    //清理 IP
    onClearIP = async () => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.modalGoogleCode(0, 'clearIP')}>
                确认
                </Button>,
        ]
        if (this.inputclear) {
            this.inputclear.value = ""
        }
        let modalHtml = <div className="col-md-12 col-sm-12 col-xs-12">
            <div className="form-group">
                <label className="col-sm-3 control-label">清理的IP：<i>*</i></label>
                <div className="col-sm-8">
                    <input type="text" className="form-control" ref={(inp) => this.inputclear = inp} placeholder='255.255.255.255' name="clearIP" onChange={this.handleInputChange} />
                </div>
            </div>
        </div>;
        this.setState({
            width: '600px',
            visible: true,
            modalHtml: modalHtml,
            title: '清理 IP',
            clearIP: ''
        })
    }
    //清理 IP 按钮
    onClearIpBtn = async () => {
        const { clearIP } = this.state
        if (!clearIP) {
            message.warning("请输入要清理的 IP ！");
            return;
        }
        await this.request({ url: '/userInfo/clearIP', type: 'post', isP: true }, { ip: clearIP })
        await this.setState({
            visible: false,
            clearIP: ''
        })
        this.requestTable()
    }
    //显示备注
    showModal = ({ id, userName, memo }) => {
        let modalHtml = <ModalMemo memo={memo} onChange={this.handleInputChange} />
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={this.onModifyMemoSave}>
                确认
                </Button>,
        ];
        this.setState({
            visible: true,
            modalHtml: modalHtml,
            title: '新增备注',
            modifyUserId: id,
            modifyUserName: userName,
            modifyTextArea: memo,
            width: "600px"
        })

    }
    //添加备注
    onModifyMemoSave = async () => {
        const { modifyUserId, modifyUserName, modifyTextArea } = this.state;
        await this.request({ url: '/userInfo/saveMemoById', type: 'post' }, {
            userId: modifyUserId,
            userName: modifyUserName,
            memo: modifyTextArea
        })
        this.handleCancel()
        this.requestTable()
    }
    //安全管理
    onSafeManage = async ({ id, userName }) => {
        const { tk } = this.state
        let result = await this.request({ url: '/userInfo/safeMange', type: 'post' }, {
            userId: id,
            userType: tk
        })
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={this.handleCancel}>确认</Button>,
        ]
        this.setState({
            visible: true,
            modalHtml: <ModalSafeManage item={result} />,
            title: `用户${userName}安全管理`,
            width: '65%'
        })

    }
    //用户类型弹窗
    modalCustomerType = ({ customerType, id }, check) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.modalGoogleCode(id, 'custype', check)}>
                确认
            </Button>,
        ]
        this.forceUpdate()
        this.setState({
            visible: true,
            modalCustomer: customerType,
            width: '600px',
            modalHtml: <UserType _customerType={_customerType} handleModalCustomer={v => this.onSelectChoose(v, 'modalCustomer')} modalCustomer={customerType} />,
            title: '修改客户类型'
        })
    }
    //按钮 用户类型弹窗
    modalCustomerBtn = async (id) => {
        await this.request({ url: '/userInfo/modifyCustomerType', type: 'post', isP: true }, {
            userId: id,
            customerType: this.state.modalCustomer
        })
        this.handleCancel()
        this.requestTable()
    }
    //冻结或者解冻按钮弹窗
    freezeUserModal = ({ id, freez }, check) => {
        let title = freez ? "解冻用户" : "冻结用户"
        this.md({ title }, () => this.modalGoogleCode(id, 'freezeOrNo', check))
    }
    isOkey = async (id) => {
        await this.request({ url: '/userInfo/freez', type: 'post', isP: true }, { userId: id })
        this.requestTable()
    }
    //批量冻结
    batchFreeze = () => {
        this.md({ title: '您确定要批量冻结吗？' }, () => this.modalGoogleCode(null, 'batchFreeze', 'check'))
    }
    batchFreezeBtn = async () => {
        await this.request({ url: '/userInfo/batchFreez', type: 'post', isP: true }, { userIdList: this.state.tableRowKeys.join().trim() })
        this.setState({ tableRowKeys: [], tableRows: [] })
        this.requestTable()

    }
    //敏感用户
    sensitiveUser = item => {
        let title = item.warningUser == 1 ? '设置为普通用户吗？' : '设置为敏感用户吗？'
        this.md({ title }, () => this.modalGoogleCode(item, 'sensitiveUser', 'check'))
    }
    sensitiveUserBtn = async ({ id, warningUser }) => {
        await this.request({ url: '/userInfo/setWarningUser', type: 'post', isP: true }, { userId: id, warningUser })
        this.requestTable()
    }
    //更新表头
    updateTheader = p => {
        const { allPopList } = this.state;

        let newarr = allPopList.filter(v => new Set(p).has(v));
        let thl = this.createColumns().filter(v => new Set(newarr).has(v.title))

        if (!thl.length) {
            message.warning('至少选择一列显示')
            return
        }

        this.setState({
            tHeadList: thl,
            popList: p
        })
    }
    //切换显示列
    handlePopVis = () => this.setState({ popVisible: !this.state.popVisible })
    handleCancel = () => this.setState({ visible: false })
    //用户详情切换
    toIssue = (detailUser = null) => this.setState({ pageTabs: !this.state.pageTabs, detailUser, },() => !detailUser && this.requestTable())
    render() {
        const { showHide, pageTotal, pageIndex, pageSize, dataSource, tk, pageTabs, customerType, userId, userName, UserInfo, recommendName,
            customerOperation, freez, memo, loginIp, _tags, tag, tableRowKeys, allPopList, loginTime, registerTime, nickname, popList, popVisible, tHeadList } = this.state
        const { ckAuth, clickHide, handleInputChange, onSelectChoose, onChangeCheckTime, requestTable, resetState, handlePopVis, onSelectTableRows, batchFreeze, onClearIP,tcb,updateTheader  } = this
        return (
            <div className="right-con">
                {pageTabs ?
                    <div>
                        <div className="page-title">
                            <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={clickHide}></i>
                        </div>
                        <div className="clearfix"></div>
                        <div className="row">
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {showHide && <div className="x_panel">
                                    <div className="x_content">
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">用户编号：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="userId" required value={userId} onChange={handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">用户名：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="userName" value={userName} onChange={handleInputChange} />
                                                    <b className="icon-fuzzy">%</b>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">登录IP：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="loginIp" value={loginIp} onChange={handleInputChange} />
                                                    <b className="icon-fuzzy">%</b>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">推荐人：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="recommendName" value={recommendName} onChange={handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                        <SeOp title='账户类型' value={customerType} onSelectChoose={v => this.onSelectChoose(v, 'customerType')} ops={_customerType} pleaseC={false} />
                                        <SeOp title='受限类型' value={customerOperation} onSelectChoose={v => this.onSelectChoose(v, 'customerOperation')} ops={_customerOperation} pleaseC={false} />
                                        {/* <SeOp title='用户标签' value={} onSelectChoose={v => this.onSelectChoose(v, 'customerOperation')} ops={USER_TAGS} pleaseC /> */}
                                        {/* <SeOp title='冻结标志' value={freez} onSelectChoose={v => this.onSelectChoose(v, 'freez')} ops={_freez} pleaseC={false} /> */}
                                        {/* <SeOp title='标签' value={tag} onSelectChoose={v => this.onSelectChoose(v, 'tag')} ops={{ ..._tags }} /> */}
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">备注：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="memo" value={memo} onChange={handleInputChange} />
                                                    <b className="icon-fuzzy">%</b>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">用户昵称：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="nickname" value={nickname} onChange={handleInputChange} />
                                                    <b className="icon-fuzzy">%</b>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">注册时间：</label>
                                                <div className="col-sm-8">
                                                    <RangePicker
                                                        showTime={{
                                                            defaultValue: SHOW_TIME_DEFAULT
                                                        }}
                                                        format={TIMEFORMAT_ss}
                                                        placeholder={TIME_PLACEHOLDER}
                                                        onChange={(date, dateString) => onChangeCheckTime(date, dateString, 'registerTime')}
                                                        value={registerTime}
                                                    />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">登录时间：</label>
                                                <div className="col-sm-8">
                                                    <RangePicker
                                                        showTime={{
                                                            defaultValue: SHOW_TIME_DEFAULT
                                                        }}
                                                        format={TIMEFORMAT_ss}
                                                        placeholder={TIME_PLACEHOLDER}
                                                        onChange={(date, dateString) => onChangeCheckTime(date, dateString, 'loginTime')}
                                                        value={loginTime}
                                                    />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-12 col-sm-12 col-xs-12 ">
                                            <div className="right">
                                                <Button type="primary" onClick={() => requestTable(PAGEINDEX)}>查询</Button>
                                                <Button type="primary" onClick={resetState}>重置</Button>
                                                {ckAuth('clearIP') && <Button type="primary" onClick={onClearIP}>清理IP</Button>}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                }
                                <div className="x_panel">
                                    <div className="x_content">
                                        <div className="table-responsive" >
                                            <Tabs onChange={tcb} defaultActiveKey='1' activeKey={tk}>
                                                <TabPane tab='正常用户' key={'1'}></TabPane>
                                                <TabPane tab='冻结用户' key={'2'}></TabPane>
                                                {/* <TabPane tab='风险用户' key={'3'}></TabPane> */}
                                            </Tabs>
                                            <div style={{ position: 'absolute', top: '10px', right: '0px' }}>
                                                {ckAuth('batchFreez') && <Button disabled={!tableRowKeys.length} type="primary" size='small' onClick={batchFreeze}>批量冻结</Button>}
                                                <Popover
                                                    content={<Content value={popList} allPopList={allPopList} popChange={updateTheader} />}
                                                    trigger="click"
                                                    visible={popVisible}
                                                    onVisibleChange={handlePopVis}
                                                    placement="rightTop"
                                                >
                                                    <Button type="primary" size='small'>切换显示列</Button>
                                                </Popover>
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
                                                columns={tHeadList}
                                                requestTable={requestTable}
                                                rowSelection={tk == 1 && {
                                                    onChange: (k, r) => onSelectTableRows(k, r)
                                                }}
                                                scroll={{ x: 2000 }}
                                            />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    :
                    <UserInfoDetail permissList={this.props.permissList} toIssue={this.toIssue} pUserType={tk} user={this.state.detailUser} />
                }
                <Modal
                    visible={this.state.visible}
                    title={this.state.title}
                    width={this.state.width}
                    style={{ top: 50 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {this.state.modalHtml}
                </Modal>
            </div>
        )
    }
}




















