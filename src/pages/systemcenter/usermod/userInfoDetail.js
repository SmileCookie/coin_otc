import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, } from 'Conf'
import { Button, Modal, Tabs, message } from 'antd'
import ModalAddAndDeleteTag from './moadl/modalAddAndDeleteTag'
import { toThousands, dateToFormat, userLimit, mapGet } from 'Utils'
const { TabPane } = Tabs;
let _user = {
    id: '用户编号',
    nickname: '用户昵称',
    email: '用户邮箱',
    customerTypeName: '账户类型',
    registerTime: '注册时间',
    vipRate: '用户等级',
    recommendName: '推荐人',
    userQualificationName: '用户资质',
    mobile: '用户手机',
    safePwdS: '资金密码',
    googleOpen: '谷歌验证',
    realname: '实名信息',//cardName => realname
    bank: '银行卡',
    alipay: '支付宝',
    warningUserName: '敏感用户',
    freezeName: '冻结标识',
    customerOperation: '用户限制',
}
const objToArr = o => Object.keys(o).map(v => {
    if (v == 'ip') {
        return { dataIndex: v, title: o[v], render: t => <a href={`http://www.ip138.com/ips138.asp?ip=${t}&action=2`} target="_blank">{t}</a> }
    }
    if (o[v].includes('时间')) {
        return { dataIndex: v, title: o[v], render: t => dateToFormat(t) }
    }
    return { dataIndex: v, title: o[v] }
})

/**
 * tab列表表头
 * @description {key:取得字段,value:表头文字}
 */
//认证记录
const _cerRecord = {
    applyTime: '记录时间',
    userId: '用户编号',
    ip: '申请时ip',
    applyType: '申请类型',
    auditTime: '审核时间',
    auditUserName: '审核人',
    statusName: '审核结果',
    reson: '原因说明',
}
//登录记录
const _loginRecord = {
    // index: '序号',
    date: '登录时间',
    userId: '用户编号',
    ip: 'IP',
    city: '城市',
    describe: '登录方式',
}
//资料变动
const _dataChangeRecord = {
    time: '记录时间',
    userId: '用户编号',
    ip: 'IP',
    authenTypeName: '变动项',
    des: '详细描述',
}
//积分变更
const _pointChangeRecord = {
    addtime: '记录时间',
    userid: '用户编号',
    typeShowNew: '积分类型',
    jifen: '积分',
    memo: '描述',
}
//tabs
const _TABS = new Map([
    ['认证记录', { thd: _cerRecord, url: '/storeAuth/queryAuthListAll' }],
    ['登录记录', { thd: _loginRecord, url: '/loginInfo/queryList' }],
    ['资料变动记录', { thd: _dataChangeRecord, url: '/authenLog/queryInfo' }],
    ['积分变更记录', { thd: _pointChangeRecord, url: '/integralBill/queryList' }],
    // ['风控拦截记录', { thd: {}, url: '' }],
    // ['个人黑名单', { thd: {}, url: '' }],
    // ['资金变动记录', { thd: {}, url: '' }],
    // ['监控审核记录', { thd: {}, url: '' }],
])

@Decorator({ lb: 'userInfo' })
export default class UserInfoDetail extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            visible: false,
            title: '',
            width: '',
            modalHtml: '',
            arr: [],
            tk: "认证记录",
            userInfo: Object.assign({}, _user),
            userTagsList: [],
            systemUuserTagsList: [],
        }
    }
    async componentDidMount() {
        await this.setState({
            id: this.props.user.userId
        })
        await this.getUserInfo()
        this.requestTableRecord()
    }
    getUserInfo = async () => {
        const { id } = this.state
        const rs = await this.request({ url: '/userInfo/queryUser', type: 'post' }, {
            userId: id,
            userType: this.props.pUserType || ''
        })
        const result = rs.list[0] || {}

        let _judgeRs = {
            registerTime: v => dateToFormat(v),
            googleOpen: v => v ? '开启' : '关闭',
            customerOperation: v => userLimit(v) || '--',

        }
        //用户信息
        let _rs = Object.keys(_user).reduce((res, key) => {

            if (Object.keys(_judgeRs).includes(key)) {
                res[key] = _judgeRs[key](result[key])
                return res
            }
            //不是Number时，不存在显示--，为数字时直接取值
            res[key] = /^\d+$/.test(result[key]) ? result[key] : result[key] || '--';

            return res
        }, {})

        this.setState({
            userInfo: _rs,
            userTagsList: result.userTags ? result.userTags.split(',') : [],
            systemUuserTagsList: result.systemUuserTags ? result.systemUuserTags.split(',') : [],
        })
    }
    //各记录数据
    requestTableRecord = async (currIndex, currSize) => {
        const { pageIndex, pageSize, id, tk } = this.state
        let params = {
            userId: id,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const url = mapGet(_TABS, tk).url;

        if (!url) {
            message.warning(tk + '没有接口')
            this.setState({ dataSource: [], pageTotal: 0 })
            return
        }
        const result = await this.request({ url, type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    tcb = async tk => {
        await this.setState({ tk })
        this.requestTableRecord(PAGEINDEX)
    }
    //删除标签
    onDeleteTag = (id, tagName) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onDeleteTagBtn(id, tagName)}>
                保存修改
                </Button>,
        ]
        this.setState({
            width: '600px',
            title: '删标签',
            visible: true,
            modalHtml: <ModalAddAndDeleteTag memo={''} type={true} selected={[]} onChange={this.handleInputChange} />
        })
    }
    onDeleteTagBtn = async (id, tagName) => {
        const { tagMemo } = this.state;
        if (!tagMemo) {
            message.warning('备注不能为空');
            return;
        }
        await this.request({ url: '/userInfo/delUserTag', type: 'post' }, {
            userId: id,
            userTagName: tagName,
            tagRemark: tagMemo
        })
        this.handleCancel()
        this.getUserInfo()
    };
    //增加标签
    onAddTag = (arr, id) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onAddTagBtn(id)}>
                保存修改
                </Button>,
        ];
        this.setState({
            width: '600px',
            title: '贴标签',
            visible: true,
            tagsSelect: arr,
            tagMemo: '',
            modalHtml: <ModalAddAndDeleteTag memo={''} type={false} selected={arr} onCheckBoxChange={this.onCheckBoxChange} />
        })
    };
    onCheckBoxChange = (arr) => {
        this.setState({
            tagsSelect: arr
        })
    };
    onAddTagBtn = async (id) => {
        const { tagsSelect } = this.state;
        if (tagsSelect.length == 0) {
            message.warning("请选择一个标签");
            return;
        }
        await this.request({ url: '/userInfo/saveUserTag', type: 'post', isp: true }, {
            userId: id,
            userTagName: tagsSelect.toString()
        })
        this.handleCancel()
        this.getUserInfo()
    };
    createColum = () => { }
    handleCancel = () => this.setState({ visible: false })
    render() {
        const { dataSource, pageIndex, pageSize, pageTotal, tk, id, userTagsList, systemUuserTagsList, limitBtn, userInfo } = this.state
        let _index = [{ title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 }]
        return (
            <div className="x_panel">
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12" style={{ borderBottom: '1px solid #E6E9ED', marginBottom: '10px' }}>
                        <p className='left'>用户详细信息</p><Button style={{ float: 'right' }} type="primary" onClick={() => { this.props.toIssue(null,this.props.user.number) }}>返回上一级</Button>
                    </div>
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel" style={{ border: '1px dashed #E6E9ED' }}>
                            {Object.keys(userInfo).map((_key) => (
                                <div className="col-md-4 col-sm-6 col-xs-6" key={_key}>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">{_user[_key]}：</label>
                                        <div className="col-sm-8 lineHeight_34">
                                            {userInfo[_key]}
                                        </div>
                                    </div>
                                </div>
                            ))}
                            <div className="col-md-4 col-sm-6 col-xs-6">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">用户标签：</label>
                                    <div className="col-sm-8 lineHeight_34">
                                        {
                                            systemUuserTagsList.map((tag, i) => {
                                                return (<span key={i} className='userinfo-tag' style={{ padding: '0px 5px' }}>{tag}</span>)
                                            })
                                        }
                                        {
                                            userTagsList.map((tag, i) => {
                                                return (limitBtn.includes("delUserTag") && <span key={i} className='userinfo-tag'>{tag}<a href="javascript:void(0);" onClick={() => this.onDeleteTag(id, tag)}>X</a></span>)
                                            })
                                        }
                                        {limitBtn.includes("saveUserTag") && <a href="javascript:void(0);" className="userinfo-add" onClick={() => this.onAddTag(userTagsList, id)}>+标签</a>}
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="table-responsive" >
                            <Tabs onChange={this.tcb} activeKey={tk}>
                                {
                                    [..._TABS.keys()].map(v => <TabPane tab={v} key={v}></TabPane>)
                                }
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
                                columns={objToArr(mapGet(_TABS, tk).thd || {})}
                                requestTable={this.requestTableRecord}

                            // scroll={{ x: 2500 }}
                            />
                        </div>
                    </div>
                </div>
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