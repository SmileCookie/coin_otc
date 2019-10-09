import Decorator from 'DTPath'
import CommonTable from 'CTable'
import GoogleCode from 'GCPath'
import { Button, Modal, Radio, message, } from 'antd'
import { dateToFormat, toThousands } from 'Utils'
import { SeOp, MSeOp } from '../../../components/select/asyncSelect'
import ModalViewDetail from './moadl/modalViewDetail'
import ModalManuallyModify from './moadl/modalManuallyModify'
// import Zhengmian from '../../../assets/images/zhengmian.png'
// import Fanmian from '../../../assets/images/fanmian.png'
// import Shouchi from '../../../assets/images/shouchi.png'
const RadioGroup = Radio.Group;
//用户资料
let _userMsg = {
    userId: '用户编号',
    userName: '账户昵称',
    ip: 'IP地址',
    authstatusName: '实名认证',
    storeStatus: 'XXXX',// XXXX标题  => 商家认证 || 商家取消，取后端typeName
    authFailTimes: '认证失败次数',
}
//实名认证
let _verified = {
    realName: '真实姓名',
    countryName: '认证国家',
    cardTypeName: '证件类型',
    cardId: '证件号码',
    startDate: '证件有效期',
    endDate: '证件有效期'

}
//商家认证
let _MerCer = {
    userQualificationName: '用户资质',
    bankAccountNumber: '银行卡',
    ailiPayAccount: '支付宝',
    typeName: '申请类型',
}


@Decorator()
export default class ViewMerchantCer extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            reason: '0'
        }
        this.state = {
            visible: false,
            title: '',
            width: '',
            modalHtml: '',
            userMsg: Object.assign({}, _userMsg),   //  用户信息
            verified: Object.assign({}, _verified), //实名认证
            _denialReason: {//拒绝原因      没有用了
                1: '请选择拒绝原因',
                2: '图像不清晰'
            },
            MerCer: Object.assign({}, _MerCer), //商家认证
            userItem: {},
            ...this.defaultState,
            _reasons: {},
            storeStatus: 0,
            curStoreFreez: 0,//当前保证金余额,
            _disableStore: false, //商家认证按钮是否置灰
        }
        this.goofn = () => new Map([
            ['default', v => this.onSubmit(v)],
        ])
    }
    async componentDidMount() {
        this.getUser()
    }
    getUser = async (userId = '') => {
        const { detailUser: { id } } = this.state
        const rsu = await this.request({ url: '/storeAuth/find', type: 'post' }, { id })
        const { storeStatusName, authstatus, authCardReson, loadImg, frontalImg, backImg, authstatusName, storeStatus, storeReson,
            type, curStoreFreez, cardTypeName } = rsu;

        // await this.request({ url: '/common/getRefuseReasonList', type: 'post' })
        //商家拒绝原因
        let _reasons = await this.request({ url: '/common/getStoreApplyRefuseReason', type: 'post' })
        //商家取消原因
        let _cancleReasons = await this.request({ url: '/common/getStoreCancelRefuseReason', type: 'post' })
        let _rea = {
            1: _reasons,
            2: _cancleReasons
        }
        //商家认证||商家取消 状态
        let _storeStatus = {
            0:'待审核',
            1:'通过',
            2:'拒绝'
        }
        let userMsg = Object.keys(_userMsg).reduce((res, k) => {
            _userMsg.storeStatus = rsu.typeName || 'XXXX'
            if(k === 'storeStatus') {
                res[k] = _storeStatus[rsu[k]] || '--';
                return res
            }
            res[k] = rsu[k] || '--'
            return res
        }, {})
        let verified = Object.keys(_verified).reduce((res, k) => {
            res[k] = rsu[k] || '--'
            return res
        }, {})
        let MerCer = Object.keys(_MerCer).reduce((res, k) => {
            res[k] = rsu[k] || '--'
            return res
        }, {})


        this.setState({
            userMsg,
            verified,
            MerCer,
            userItem: {
                storeStatusName, authstatus, authCardReson, loadImg, frontalImg, backImg, authstatusName, cardTypeName,
                type,//申请类型 1:入驻申请，2;取消申请
            },
            dataSource: rsu.list || [],
            _reasons: _rea[type],
            rsu,
            storeStatus,
            _disableStore: storeStatus,//
            reason: storeReson || '0',
            curStoreFreez,
        })
    }
    //手动修改
    manuallyModify = () => {
        this.setState({
            title: '修改用户资料',
            width: '700px',
            modalHtml: <ModalManuallyModify detailUser={this.state.detailUser} handleCancel={this.handleCancel} getUser={this.getUser} rsu={this.state.rsu} />,
            visible: true
        })
    }
    //有效性检查
    validityCheck = async () => {
        const { cardId, realName, } = this.state.rsu
        let params = {
            cardNo: cardId, realName
        }
        const result = await this.request({ url: '/common/checkAvaliable', isP: true }, params)
        this.setState({
            validityCheckMsg: result.description || ''
        })

    }
    //商家认证提交
    onSubmit = async () => {
        const { detailUser: { userId }, userItem, reason, storeStatus, rsu: { storeId } } = this.state
        if (!storeStatus) {
            message.warning('请选择审核判定！')
            return
        }
        if (storeStatus == 2 && reason == '0') {
            message.warning('请选择拒绝原因！')
            return false
        }

        await this.request({ url: '/storeAuth/submit', type: 'post', isP: true }, {
            userId, status: storeStatus, reason, id: storeId
        })

        this.getUser()
        await this.setState({ ...this.defaultState })

        // this.props.toIssue()

    }
    viewDetail = () => {
        const { detailUser: { id, userId }, } = this.state
        this.setState({
            title: '认证失败详情',
            width: '700px',
            modalHtml: <ModalViewDetail userId={userId} />,
            visible: true
        })
    }
    createColumns = (pageIndex, pageSize) => {


        return [
            // { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '申请时间', dataIndex: 'sendTime', render: t => dateToFormat(t) },
            { title: '申请类型', dataIndex: 'typeName' },
            // { title: '法币账户USDT余额', dataIndex: 'userId' },
            { title: '保证金余额USDT', dataIndex: 'storeFreez', render: t => toThousands(t, true) },
            { title: '审核时间', dataIndex: 'auditTime', render: t => dateToFormat(t) },
            { title: '审核人', dataIndex: 'auditUserName', },
            { title: '审核结果', dataIndex: 'statusName' },
            { title: '备注', dataIndex: 'storeReson', },
        ]
    }
    handleCancel = () => this.setState({ visible: false })

    /**
     * @description 判断审核判定后面的文案是否显示
     * @returns {Boolean}
     * @param {Number} 审核状态 //状态;0待审核 不置灰，1:通过 2:拒绝
     * 
     */
    auditJudge = storeStatusName => !!storeStatusName 
    render() {
        const { detailUser: { id, userId }, userMsg, verified, _denialReason, MerCer, dataSource, pageIndex, pageSize, pageTotal, userItem: { authstatus, authCardReson, storeStatusName, authstatusName, loadImg, frontalImg, backImg, cardTypeName, type }, reason, storeStatus, curStoreFreez, _disableStore } = this.state

        return (
            <div className="x_panel">
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12" style={{ borderBottom: '1px solid #E6E9ED', marginBottom: '10px' }}>
                        <p className='left'>用户资料</p><Button style={{ float: 'right' }} type="primary" onClick={() => { this.props.toIssue() }}>返回上一级</Button>
                    </div>
                    <div className="col-md-12 col-sm-12 col-xs-12" style={{ borderBottom: '1px solid #E6E9ED', marginBottom: '20px' }}>
                        {
                            Object.keys(userMsg).map(k =>
                                <div className="col-md-4 col-sm-6 col-xs-6" key={k}>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">{_userMsg[k]}：</label>
                                        <div className="col-sm-8 lineHeight_34">
                                            {userMsg[k]}{k == 'authFailTimes' && <a style={{ marginLeft: '10px' }} href='javascript:void(0);' onClick={this.viewDetail}>查看详情</a>}
                                        </div>
                                    </div>
                                </div>)
                        }
                    </div>
                    <div className="col-md-12 col-sm-12 col-xs-12" style={{ borderBottom: '1px solid #E6E9ED', marginBottom: '20px' }}>
                        <div className="col-md-12 col-sm-12 col-xs-12" >
                            <p className='left'>实名认证：</p>
                            <a className='right' href='javascript:void(0);' onClick={this.manuallyModify}>手动修改</a>
                        </div>
                        <div className="clearfix"></div>
                        <div className="col-md-5 col-sm-5 col-xs-5">
                            {
                                Object.keys(verified).map(k => {
                                    if (k == 'startDate') return
                                    return <div className="col-md-12 col-sm-12 col-xs-12" key={k}>
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">{_verified[k]}：</label>
                                            <div className="col-sm-8 lineHeight_34">
                                                {k == 'endDate' ? `${verified['startDate']} 至 ${verified[k]}` : verified[k]}{k == 'cardId' && <a style={{ marginLeft: '10px' }} href='javascript:void(0);' onClick={this.validityCheck}>有效性检查</a>}
                                                {k == 'cardId' && <p style={{ color: '#ff6600' }}>{this.state.validityCheckMsg}</p>}
                                            </div>
                                        </div>
                                    </div>
                                })
                            }
                            <div className="col-md-12 col-sm-12 col-xs-12" >
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">审核判定：</label>
                                    <div className="col-sm-8 lineHeight_34">
                                        <RadioGroup disabled={true} value={authstatus}>
                                            <Radio value={6}>通过</Radio>
                                            <br></br>
                                            <Radio value={7}>拒绝:</Radio>
                                        </RadioGroup>
                                        {authCardReson}
                                        {/* <MSeop title='' value={''} onSelectChoose={v => this.onSelectChoose(v)} ops={_denialReason} pleaseC={false} /> */}
                                    </div>
                                </div>
                            </div>
                            <Button disabled={true} type="primary">{authstatusName}</Button>
                        </div>
                        <div className="col-md-7 col-sm-7 col-xs-7">
                            <div className="col-md-4 col-sm-4 col-xs-4">
                                <div style={{ textIndent: '54px' }}>{cardTypeName}正面照片</div>
                                <div className='h300w270'>
                                    <img className='img_wh100' src={frontalImg} alt='图片' />
                                </div>
                            </div>
                            {verified.cardTypeName == '身份证' && <div className="col-md-4 col-sm-4 col-xs-4">
                                <div style={{ textIndent: '54px' }}>{cardTypeName}反面照片</div>
                                <div className='h300w270'>
                                    <img className='img_wh100' src={backImg} alt='图片' />
                                </div>
                            </div>}
                            <div className="col-md-4 col-sm-4 col-xs-4">
                                <div style={{ textIndent: '54px' }}>{cardTypeName}手持照片</div>
                                <div className='h300w270'>
                                    <img className='img_wh100' src={loadImg} alt='图片' />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="col-md-12 col-sm-12 col-xs-12" >
                            <p>商家认证：</p>
                        </div>
                        <div className="col-md-5 col-sm-5 col-xs-5">
                            {
                                Object.keys(MerCer).map(k =>
                                    <div className="col-md-12 col-sm-12 col-xs-12" key={k}>
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">{_MerCer[k]}：</label>
                                            <div className="col-sm-8 lineHeight_34">
                                                {MerCer[k]}
                                            </div>
                                        </div>
                                    </div>)
                            }
                            <div className="col-md-12 col-sm-12 col-xs-12" >
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">审核判定：</label>
                                    <div className="col-sm-9 lineHeight_34" style={{ position: 'relative' }}>
                                        <RadioGroup style={{ width: '100%' }} disabled={this.auditJudge(_disableStore)} name='storeStatus' onChange={this.handleInputChange} value={storeStatus}>
                                            <div ><Radio value={1}>通过</Radio> {!this.auditJudge(_disableStore) && <p style={{ display: 'inline-block', }}>：将用户资质将变更为{`${type == 1 ? '“商家用户”' : '“实名用户”'}`}</p>} </div>
                                            <div><Radio value={2}>拒绝</Radio>{storeStatus == 2 && !this.auditJudge(_disableStore) ? <div style={{ display: 'inline-block', }}>：<MSeOp width={250} value={reason} onSelectChoose={v => this.onSelectChoose(v, 'reason')} ops={this.state._reasons} defaultValue='0' pleaseC={true} disabled={this.auditJudge(_disableStore)} pCK={'0'} /> </div> : ''}</div>
                                        </RadioGroup>

                                    </div>
                                </div>
                            </div>
                            <Button type="primary" disabled={this.auditJudge(_disableStore)} onClick={() => this.modalGoogleCode()}>{storeStatusName}</Button>
                        </div>
                        <div className="col-md-7 col-sm-7 col-xs-7">
                            <div>
                                <p className='left'>账户编号：{userId}，当前保证金余额：{toThousands(curStoreFreez, true)} USDT，认证记录如下：</p>
                                <a href='javascript:void(0);' className='right' onClick={() => this.props.jumpPage({ name: "法币流水明细", url: "/tradecenter/billingdetailsmod/billDetailOTC", key: 700100010078 })}>保证金扣减详情</a>
                            </div>
                            <div className="table-responsive">
                                <CommonTable
                                    dataSource={dataSource}
                                    // pagination={
                                    //     {
                                    //         total: pageTotal,
                                    //         pageSize: pageSize,
                                    //         current: pageIndex

                                    //     }
                                    // }
                                    columns={this.createColumns(pageIndex, pageSize)}
                                    requestTable={this.requestTable}
                                    scroll={{ x: 1800 }}
                                />
                                注：请注意两次认证时间间隔不能低于指定日期
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={this.state.visible}
                    title={this.state.title}
                    width={this.state.width}
                    style={{ top: 50 }}
                    onCancel={this.handleCancel}
                    footer={null}
                >
                    {this.state.modalHtml}
                </Modal>
            </div>
        )
    }
}