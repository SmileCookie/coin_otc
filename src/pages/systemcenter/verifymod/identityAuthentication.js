import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import CommonTable from 'CTable'
import ModalAuthen from './modal/modalAuthen'
import ModalAuthena from './modal/modalAuthena'
import GoogleCode from '../../common/modal/googleCode'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, TIMEFORMAT, PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, DatePicker, Tabs, Pagination, Modal, message } from 'antd'
import { pageLimit, dateToFormat } from '../../../utils'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane

export default class UserCapital extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            userId: '',
            userName: '',
            realName: '',
            submitTimeS: '',
            submitTimeE: '',
            checkTimeS: '',
            checkTimeE: '',
            adminUserName: '',
            tab: 0,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            dataSource: [],
            pageTotal: 0,
            modalHtml: '',
            visible: false,
            title: '',
            time: [],
            checkTime: [],
            status: 1,
            reason: '',
            reasonName: '',
            certificationNum: '',
            movisible: false,
            mowidth: '',
            momodalHtml: '',
            reasonList: [],
            motitle: '',
            nomodalHtml: '',
            novisible: false,
            nowidth: '',
            memo: '',
            lodalHtml: '',
            lotitle: '',
            lovisible: false,
            lowidth: '600px',
            checkResult: 0,
            checkImg: '',
            qiniu_host: "https://o4we6sxpt.qnssl.com/",
            limitBtn: [],
            googVisibal: false,
            googleSpace: '',
            type: '',
            check: '',

        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.callbackTabs = this.callbackTabs.bind(this)
        this.onChangeSubmitTime = this.onChangeSubmitTime.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.showModal = this.showModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onAuditInfo = this.onAuditInfo.bind(this)
        this.onAuditInfoBtn = this.onAuditInfoBtn.bind(this)
        this.onChangeRadio = this.onChangeRadio.bind(this)
        this.onChangeReason = this.onChangeReason.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.onAuditInfoto = this.onAuditInfoto.bind(this)
        this.mohandleCancel = this.mohandleCancel.bind(this)
        this.setReason = this.setReason.bind(this)
        this.lockProve = this.lockProve.bind(this)
        this.addBlacklist = this.addBlacklist.bind(this)
        this.memoOk = this.memoOk.bind(this)
        this.memoCancel = this.memoCancel.bind(this)
        this.lohandleCancel = this.lohandleCancel.bind(this)
        this.setImage = this.setImage.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.AuditInfoBtn = this.AuditInfoBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }

    componentDidMount() {
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('authentication', this.props.permissList)
        })
    }
    componentWillReceiveProps() {

    }
    //弹窗 ok 
    handleOk() {
        this.setState({ loading: true });
        setTimeout(() => {
            this.setState({
                loading: false,
                visible: false
            });
        }, 3000);
    }
    //弹窗显示
    showModal() {
        this.setState({
            visible: true,
        });
    }
    //弹窗隐藏
    handleCancel() {
        this.setState({
            visible: false,
            vipRate: '',
            jifen: '',
            discount: '',
            status: 1
        });
    }

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        })
    }

    //时间控件
    onChangeSubmitTime(date, dateString) {
        this.setState({
            submitTimeS: dateString[0],
            submitTimeE: dateString[1],
            time: date
        })
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            checkTimeS: dateString[0],
            checkTimeE: dateString[1],
            checkTime: date
        })
    }

    //tab 切换
    callbackTabs(key) {
        const { pageIndex, pageSize } = this.state
        this.setState({
            tab: key
        })
        this.requestTable(pageIndex, pageSize, key);
    }
    //查询按钮
    inquireBtn() {
        this.setState({
            pageIndex: PAGEINDEX
        }, () => this.requestTable())

    }

    requestTable(currIndex, currSize, currTab) {

        const { userId, userName, realName, submitTimeS, submitTimeE, checkTimeS, checkTimeE, adminUserName, pageIndex, pageSize, tab } = this.state;
        axios.post(DOMAIN_VIP + '/authentication/queryList', qs.stringify({
            userId, userName, realName, submitTimeS, submitTimeE, checkTimeS, checkTimeE, adminUserName,
            tab: currTab || tab,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    pageTotal: result.page.totalCount,
                    dataSource: result.page.list
                })
            } else {
                message.warning(result.msg)
            }
        })
    }

    //重置
    onResetState() {
        this.setState({
            userId: '',
            userName: '',
            realName: '',
            submitTimeS: '',
            submitTimeE: '',
            checkTimeS: '',
            checkTimeE: '',
            adminUserName: '',
            time: [],
            checkTime: []
        })
    }
    //改变 onChangeRadio
    onChangeRadio(val) {
        this.setState({
            status: val
        })
    }
    //原因
    onChangeReason(val) {
        let reasonNum
        switch (val) {
            case '8':
                reasonNum = "图像经过处理"
                break;
            case '9':
                reasonNum = '图像不清晰'
                break;
            case '10':
                reasonNum = '证件图像类型不符'
                break;
            case '11':
                reasonNum = '平台仅支持满16周岁的用户进行交易'
                break;
            default:
                break;
        }
        this.setState({
            reason: val,
            reasonName: reasonNum
        })
    }
    //审核按钮
    onAuditInfo(item) {
        // let url = item.status == 5? "/authentication/see":"/authentication/onlySee";
        axios.post(DOMAIN_VIP + "/authentication/see", qs.stringify({
            id: item.id
        })).then(res => {
            const reuslt = res.data
            if (reuslt.code == 0) {
                this.footer = [
                    <Button key="back" onClick={this.handleCancel}>取消</Button>,
                    <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.AuditInfoBtn(item)}>
                        保存修改
                    </Button>,
                ]
                this.setState({
                    certificationNum: reuslt.data.authTimes,
                    reason: '',
                    visible: true,
                    title: '审核信息',
                    width: '1200px',
                    status: 1,
                    modalHtml: <ModalAuthen tab={item.status} item={reuslt.data} onChangeRadio={this.onChangeRadio} onChangeReason={this.onChangeReason} setReason={this.setReason} setImage={this.setImage} {...item} />

                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    setImage(src) {
        // const{qiniu_host}= this.state
        this.setState({
            checkResult: 1,
            // checkImg:qiniu_host + src, 
            checked: src
        })
    }
    //查看历史记录
    onAuditInfoto(item) {
        axios.post(DOMAIN_VIP + "/authentication/onlySee", qs.stringify({
            id: item.id
        })).then(res => {
            const reuslt = res.data
            if (reuslt.code == 0) {
                this.footert = [
                    <Button key="back" onClick={this.mohandleCancel}>取消</Button>
                ]
                this.setState({
                    movisible: true,
                    motitle: '历史记录',
                    mowidth: '1000px',
                    momodalHtml: <ModalAuthena tab={7} item={reuslt.data} degree={1} />

                })
            }
        })
    }
    //获取子组件的审核不通过原因
    setReason(item) {
        let reasonll = [];
        for (var i = 0; i < item.length; i++) {
            switch (item[i].reason) {
                case '8':
                    reasonll.push("图像经过处理")
                    break;
                case '9':
                    reasonll.push('图像不清晰')
                    break;
                case '10':
                    reasonll.push('证件图像类型不符')
                    break;
                case '11':
                    reasonll.push('平台仅支持满16周岁的用户进行交易')
                    break;
                default:
                    break;
            }
        }
        this.setState({
            reasonList: reasonll.join(',')
        })
    }
    //弹窗2取消
    mohandleCancel() {
        this.setState({
            movisible: false,
            momodalHtml: '',
        })
    }
    //审核按钮 保存后显示google弹窗
    onAuditInfoBtn(item) {
        let self = this;
        this.modalGoogleCode(item)
    }
    //审核按钮 保存
    AuditInfoBtn(item) {
        const { certificationNum, status, reason, reasonList, checkResult, checkImg, reasonName } = this.state
        axios.post(DOMAIN_VIP + "/authentication/pass", qs.stringify({
            id: item.id,
            status,
            reason,
            checkResult,
            // checkImg
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                if (certificationNum >= 2 && status == 2) {
                    this.footerr = [
                        <Button key='1' type="more" onClick={() => this.lockProve(item)}>锁定认证72小时</Button>,
                        <Button key='2' type="more" onClick={() => this.addBlacklist(item)}>移入黑名单</Button>,
                        <Button key="back" onClick={this.lohandleCancel}>取消</Button>
                    ]
                    this.setState({
                        lovisible: true,
                        lowidth: '400px',
                        lodalHtml: <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="center">提示</div>
                            <div className="col-md-12 col-sm-12 col-xs-12 lineHeight_34">
                                第{certificationNum}次拒绝改用户的认证申请。
                                    </div>
                            <div className="col-md-12 col-sm-12 col-xs-12 lineHeight_34">
                                拒绝原因分别为：{reasonList},{reasonName}。
                                    </div>
                            <div className="col-md-12 col-sm-12 col-xs-12 lineHeight_34">
                                是否需要限制该用户的认证权限。
                                    </div>
                        </div>

                    })
                } else {
                    this.setState({
                        visible: false
                    })
                }
                this.requestTable()
                message.success(result.msg)
            } else {

                message.warning(result.msg)
            }
        })

    }


    lohandleCancel() {
        this.setState({
            lovisible: false,
            lodalHtml: ''
        })
    }
    //锁定认证
    lockProve(item) {
        axios.post(DOMAIN_VIP + "/authentication/lock", qs.stringify({
            id: item.id,
            state: '1',
            userId: item.userId
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                this.setState({
                    lovisible: false,
                    visible: false
                })
                this.requestTable()
                message.success(result.msg)
            } else {
                message.warning(result.msg)
            }
        })
    }
    //移入黑名单原因
    addBlacklist(item) {
        this.footern = [
            <Button key="back" onClick={this.memoCancel}>取消</Button>,
            <Button key='submit' type="more" onClick={() => this.memoOk(item)}>确定</Button>
        ]
        this.setState({
            novisible: true,
            nowidth: '400px'

        })
    }
    //移入黑名单确定
    memoOk(item) {
        axios.post(DOMAIN_VIP + "/blacklist/insert", qs.stringify({
            userid: item.userId,
            userName: item.userName,
            memo: this.state.memo
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                this.setState({
                    lovisible: false,
                    novisible: false,
                    visible: false
                })
                this.requestTable()
                message.success(result.msg)
            } else {
                message.warning(result.msg)
            }
        })
    }
    //移入黑名单取消
    memoCancel() {
        this.setState({
            novisible: false,
            memo: '',
        })
        this.requestTable()
    }


    //google 验证弹窗
    modalGoogleCode(item) {
        this.setState({
            googVisibal: true,
            googleSpace: item,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value) {
        const { googleSpace } = this.state
        const { googleCode } = value
        axios.post(DOMAIN_VIP + "/common/checkGoogleCode", qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                this.setState({
                    googVisibal: false
                }, () => this.AuditInfoBtn(googleSpace))
            } else {
                message.warning(result.msg)
            }
        })
    }
    //点击收起
    clickHide() {
        let { showHide, pageSize } = this.state;
        this.setState({
            showHide: !showHide,
        })
    }
    handleCreate() {
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            form.resetFields();
            this.modalGoogleCodeBtn(values)
        });
    }
    saveFormRef(formRef) {
        this.formRef = formRef;
    }
    //谷歌弹窗关闭
    onhandleCancel() {
        this.setState({
            googVisibal: false
        })
    }
    createColumns = (pageIndex, pageSize) => {
        const { limitBtn } = this.state
        return [
            { title: '序号', className: 'wordLine', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '用户编号', className: 'wordLine', dataIndex: 'userId' },
            { title: '真实姓名', dataIndex: 'realName' },
            { title: '申请时间', className: 'wordLine', dataIndex: 'submitTime', render: t => dateToFormat(t) },
            { title: '审核时间', className: 'wordLine', dataIndex: 'checkTime',  render: t => dateToFormat(t) },
            { title: '审核人', className: 'wordLine', dataIndex: 'adminUserName', },
            { title: '认证地区', className: 'wordLine', dataIndex: 'areaName' },
            { title: '证件类型', className: 'wordLine', dataIndex: 'cardTypeName' },
            { title: '身份证/护照', className: 'wordLine', dataIndex: 'areaName1',render:(t, r) => r.areaName },
            { title: '状态', className: 'wordLine', dataIndex: 'statusName', },
            {
                title: '操作', className: 'wordLine', dataIndex: 'balance', render: (t, r) => r.status == 5 ?
                    (limitBtn.indexOf('see') > -1 ? <a href="javascript:void(0)" onClick={() => this.onAuditInfo(r)}>审核</a> : '')
                    :
                    (limitBtn.indexOf('onlySee') > -1 ? <a href="javascript:void(0)" onClick={() => this.onAuditInfoto(r)}>查看</a> : '')
            },
        ]
    }
    render() {
        const { movisible, mowidth, momodalHtml, showHide, dataSource, pageTotal, pageIndex, pageSize, userId, userName, realName, submitTimeS, submitTimeE, checkTimeS, checkTimeE, adminUserName, modalHtml, visible, title, width, time, checkTime, motitle, nomodalHtml, novisible, nowidth, lodalHtml, lotitle, lovisible, lowidth, limitBtn } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 审核管理 > 实名认证
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
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">真实姓名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="realName" value={realName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">审核人：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="adminUserName" value={adminUserName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">申请时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChangeSubmitTime} value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">审核时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChangeCheckTime} value={checkTime} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <div className="x_panel">

                            <div className="x_content">
                                <Tabs defaultActiveKey="0" onChange={this.callbackTabs}>
                                    <TabPane tab="待审核" key="0"></TabPane>
                                    <TabPane tab="已通过" key="2"></TabPane>
                                    <TabPane tab="已拒绝" key="1"></TabPane>
                                </Tabs>
                                <div className="table-responsive-yAuto">
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
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    style={{ marginTop: '-80px' }}
                >
                    {modalHtml}
                </Modal>
                <Modal
                    visible={movisible}
                    width={mowidth}
                    title={motitle}
                    onCancel={this.mohandleCancel}
                    footer={this.footert}
                >
                    {momodalHtml}
                </Modal>
                <Modal
                    visible={novisible}
                    width={nowidth}
                    maskClosable={false}
                    footer={this.footern}
                >
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <label className="col-sm-12 control-label">请输入移入原因：</label>
                        <div className="col-sm-12">
                            <input type="text" className="form-control" name="memo" value={this.state.memo} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </Modal>
                <Modal
                    visible={lovisible}
                    width={lowidth}
                    title={lotitle}
                    maskClosable={false}
                    footer={this.footerr}
                >
                    {lodalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange={this.handleInputChange}
                    mid='IA'
                    visible={this.state.googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate}
                />

            </div>
        )
    }

}






























































