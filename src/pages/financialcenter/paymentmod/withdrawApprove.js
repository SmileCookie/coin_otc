/**财务管理 》 提现审核 */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import SelectAType from '../select/selectAType'
import FundsTypeList from '../../common/select/fundsTypeList'
import ModalWithdraw from './modal/modalWithdraw'
import ModalSure from './modal/modalSure'
import GoogleCode from '../../common/modal/googleCode'
import ModalCustomerOpe from './../../systemcenter/usermod/moadl/modalCustomerOpe'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, TIMEFORMAT, SELECTWIDTH, URLS } from '../../../conf'
import { Button, Tabs, Select, DatePicker, Table, Modal, message } from 'antd'
import { pageLimit, toThousands } from '../../../utils'
const { COMMON_QUERYATTRUSDTE } = URLS
const TabPane = Tabs.TabPane
const Option = Select.Option
const { Column, ColumnGroup } = Table;
const { RangePicker } = DatePicker
const BigNumber = require('big.js')

export default class WithdrawApprove extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            fundType: '0',
            auditType: '2',
            tableViewList: [],
            PageViewTotal: 0,

            userid: '',
            username: '',
            commandid: '',
            moneyMin: '',
            moneyMax: '',
            confirmStartDate: '',
            confirmEndDate: '',
            startTime: '',
            endTime: '',
            customerOperation: '',
            remark: '',
            status: 1,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,

            selectedRowKeys: [],
            selectedRows: [],
            tableSource: [],
            pagination: {
                showSizeChanger: true,
                showQuickJumper: true
            },
            tab: '1',
            visible: false,
            configTime: [],
            submitTime: [],
            sureBtn: true,
            limitBtn: [],
            check: '',
            googVisibal: false,
            item: {},
            type: '',
            hasAmount: '',

        }

        this.callbackTabs = this.callbackTabs.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestViewTable = this.requestViewTable.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.handleChangeAudit = this.handleChangeAudit.bind(this)
        this.handleChangeCommandid = this.handleChangeCommandid.bind(this)
        this.onChangeSubTime = this.onChangeSubTime.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onSelectChange = this.onSelectChange.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.callbackAuditTabs = this.callbackAuditTabs.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.requestAuditTable = this.requestAuditTable.bind(this)
        this.onConfirmCancel = this.onConfirmCancel.bind(this)
        this.onChangeTable = this.onChangeTable.bind(this)
        this.countChecked = this.countChecked.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.playCoin = this.playCoin.bind(this)
        this.jumpPaper = this.jumpPaper.bind(this)
        this.handleChangeOperation = this.handleChangeOperation.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.setSureBtn = this.setSureBtn.bind(this)
        this.seeCustomerOpe = this.seeCustomerOpe.bind(this)
        this.onConfirmCancelBtn = this.onConfirmCancelBtn.bind(this)

        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }

    componentDidMount() {
        console.log(this.props)
        this.requestViewTable()
        this.requestAuditTable()
        this.setState({
            limitBtn: pageLimit('withdraw', this.props.permissList)
        }, () => console.log(this.state.limitBtn))
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestAuditTable(page, pageSize))

    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.requestAuditTable(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    //提现总览 资金类型
    handleChangeType(val) {
        this.setState({
            fundType: val,
        })
    }
    //提现审核 资金类型
    handleChangeAudit(val) {
        this.setState({
            auditType: val,
        })
    }

    //tabs 返回
    callbackTabs(key) {
        this.setState({
            tab: key,
            selectedRowKeys: [],
            selectedRows: [],
        })
    }
    //提现审核tabs 返回
    callbackAuditTabs(key) {
        const { pageIndex, pageSize } = this.state
        this.setState({
            status: key,
            selectedRowKeys: [],
            selectedRows: [],
        })
        this.requestAuditTable(PAGEINDEX, pageSize, key);
    }
    //关闭修改备注弹窗
    handleCancel() {
        this.setState({
            visible: false
        })
    }
    //提现审核页面
    jumpPaper(ftp) {
        this.setState({
            tab: '2',
            auditType: String(ftp)
        }, () => this.inquireBtn())
    }

    //总览 table
    requestViewTable() {
        const { fundType } = this.state
        axios.post(DOMAIN_VIP + '/withdraw/query', qs.stringify({
            fundType: fundType
        })).then(res => {
            const result = res.data;

            if (result.code == 0) {
                this.setState({
                    tableViewList: result.data
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    //提现审核列表 查询按钮
    inquireBtn() {
        this.setState({
            pageIndex: PAGEINDEX
        }, () => this.requestAuditTable())

    }
    //提现审核列表
    requestAuditTable(cueeIndex, currSize, auditKey) {
        const { auditType, userid, username, commandid, moneyMin, moneyMax, customerOperation, confirmStartDate, confirmEndDate, startTime, endTime, remark, status, pageIndex, pageSize, pagination } = this.state
        const self = this;
        axios.post(DOMAIN_VIP + '/withdraw/withdrawVerify', qs.stringify({
            fundstype: auditType,
            userid: userid,
            username: username,
            commandid: commandid,
            moneyMin: moneyMin,
            moneyMax: moneyMax,
            confirmStartDate: confirmStartDate,
            confirmEndDate: confirmEndDate,
            startTime: startTime,
            endTime: endTime,
            remark: remark,
            type: auditKey || status,
            pageIndex: cueeIndex || pageIndex,
            pageSize: currSize || pageSize,
            customerOperation
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                BigNumber.RM = 0;
                let tableSource = result.data.list;
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id;
                    tableSource[i].realfee = tableSource[i].realfee;
                    tableSource[i].managename = tableSource[i].managename;
                    tableSource[i].submittime = moment(tableSource[i].submittime).format(TIMEFORMAT)
                    tableSource[i].managetime = moment(tableSource[i].managetime).format(TIMEFORMAT)
                    tableSource[i].amount = new BigNumber(tableSource[i].amount).toFixed()
                    tableSource[i].afterAmount = new BigNumber(tableSource[i].afterAmount).toFixed()

                }
                pagination.total = result.data.totalCount
                pagination.onChange = self.changPageNum
                pagination.onShowSizeChange = self.onShowSizeChange
                this.setState({
                    tableSource: tableSource,
                    pagination
                })
            } else {
                message.warning(result.msg)
            }
        })
    }

    //	打币类型
    handleChangeCommandid(value) {
        this.setState({
            commandid: value
        })
    }
    //资金状态
    handleChangeOperation(val) {
        this.setState({
            customerOperation: val
        })
    }
    //确认时间
    onChangeTime(date, dateString) {
        this.setState({
            confirmStartDate: dateString[0],
            confirmEndDate: dateString[1],
            configTime: date
        })
    }
    //提交时间
    onChangeSubTime(date, dateString) {
        this.setState({
            startTime: dateString[0],
            endTime: dateString[1],
            submitTime: date
        })
    }

    //table 多选框
    onSelectChange(selectedRowKeys) {
        this.setState({ selectedRowKeys });
    }

    //多选框按钮选中时
    onChangeTable(selectedRowKeys, selectedRows) {
        const { status } = this.state
        // const selectedRowKeys = selectedRows.map(v => v.id)
        /**
         * @author oliver
         * @description 当勾选资金状态为异常时 禁止确认打币 customerOperation:03正常
         */

        const _keys = selectedRows.filter(v => v.customerOperation != '03')
        if (status == '1' && _keys.length) {
            Modal.warning({
                title: '勾选的用户资金异常，请处理后再确认！',
                okText: '确认'
            })
            return false
        }
        this.setState({ selectedRowKeys, selectedRows });
    }
    // onSelectAllTable = (selected, selectedRows, changeRows) => {
    //     const {status} = this.state

    //     let newselectedRows = selectedRows.filter(v => v.customerOperation == '03')
    //     const _keys = selectedRows.filter(v => v.customerOperation != '03')
    //     let selectedRowKeys = newselectedRows.map(v => v.id)
    //     if(status == '1' && _keys.length){
    //         Modal.warning({
    //             title:'用户资金异常，请处理后再确认！',
    //             okText:'确认'
    //         })
    //         return false 
    //     }        
    //     this.setState({selectedRowKeys,selectedRows:newselectedRows})

    // }

    //重置按钮
    onResetState() {
        this.setState({
            fundType: '0',
            auditType: '2',
            userid: '',
            username: '',
            commandid: '',
            moneyMin: '',
            moneyMax: '',
            confirmStartDate: '',
            confirmEndDate: '',
            startTime: '',
            endTime: '',
            customerOperation: '',
            remark: '',
            configTime: [],
            submitTime: []
        })
    }
    //google 验证弹窗
    modalGoogleCode(item, type) {
        this.setState({
            googVisibal: true,
            item,
            type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value) {
        const { item, type } = this.state
        const { googleCode } = value
        axios.post(DOMAIN_VIP + "/common/checkGoogleCode", qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                if (type == "cancel") {
                    this.setState({
                        googVisibal: false,
                        visible: false
                    }, () => this.onConfirmCancelBtn(item, type))
                } else if (type == "sure") {
                    this.setState({
                        googVisibal: false,
                        visible: false
                    }, () => this.palySurecoinBtn(item))

                } else if (type == "playCoin") {
                    this.setState({
                        googVisibal: false,
                        visible: false
                    }, () => this.playCoin(item))
                } else if (type == 'confirmReset' || type == 'sendingReset') {
                    this.setState({
                        googVisibal: false,
                        visible: false
                    }, () => this.onResetBtn(item, type))
                }

            } else {
                message.warning(result.msg)
            }
        })
    }
    //取消打币
    onConfirmCancelBtn(item, type) {
        let url;
        let self = this;
        if (type == 'cancel') {
            url = '/withdraw/confirmCancel';
        }
        BigNumber.RM = 0;
        const fundsComm = new BigNumber(item.amount).minus(item.afterAmount).toFixed()
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP + url, qs.stringify({
                downloadid: item.downloadid,
                accountId: item.id,
                fundsType: item.fundstype,
                fundsTypeName: item.fundstypename,
                money: item.afterAmount,
                memo: item.remark,
                userId: item.userid,
                fundsComm: fundsComm,
                userName: item.userName
            })).then(res => {
                const result = res.data;
                if (result.code == 0) {
                    message.success(result.msg)
                    self.requestAuditTable()
                    resolve(result.msg)
                } else {
                    message.warning(result.msg)
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
    }
    //取消 huozhe 确认 打币 弹窗
    onConfirmCancel(item, type) {
        let title;
        let self = this;
        if (type == 'cancel') {
            title = `您确定要取消该${item.fundstypename}提现吗?`;

        }
        // BigNumber.RM = 0;
        // const fundsComm = new BigNumber(item.amount).minus(item.afterAmount).toFixed()
        Modal.confirm({
            title: title,
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(item, type)

            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    //校验选择、全部打币
    async countChecked(type, btnType) {
        let params;
        const { status, auditType, selectedRows } = this.state
        if (type == 'all') {
            params = ""
        } else {
            params = this.state.selectedRowKeys.join(',')
            if (!params.length) {
                message.warning("请选择一项");
                return false;
            }
        }
        this.ckRules(selectedRows[0], () => this.choosePayCoin({ btnType, params, status, auditType,type }))


    }
    //选择打币接口
    choosePayCoin = ({ btnType, params, status, auditType,type }) => {
        axios.post(DOMAIN_VIP + '/withdraw/choseCount', qs.stringify({
            ids: params,
            type: status,
            fundsType: auditType
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                btnType == 'swap' ? this.footer = [
                    <Button key="back" onClick={this.handleCancel}>取消</Button>,
                    <Button key="submit" type="more" loading={this.state.loading} onClick={this.handleCancel}>
                        确定
                                        </Button>,
                ] :
                    this.footer = [
                        <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" disabled={!result.flag} type="more" onClick={() => this.modalGoogleCode(params, 'playCoin')}>
                            确认打币
                                        </Button>,
                    ]
                this.setState({
                    visible: true,
                    title: btnType == 'swap' ? '统计金额' : type == 'all' ? '全部打币' : '选中打币',
                    width: '1100px',
                    modalHtml: <ModalWithdraw btnType={btnType} tableList={result.data} handleInputChange={this.handleInputChange} />
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    //防抖
    debounce = (func, wait) => (...rest) => {
        if (this.timeout) clearTimeout(this.timeout);
        this.timeout = setTimeout(() => {
            func(...rest)
        }, wait);
    }


    fetchCheck = ({ fundstype, fundstypename }) => new Promise((resolve, reject) => {
        axios.post(DOMAIN_VIP + "/withdraw/check", qs.stringify({
            fundsType: fundstype,
            fundsTypeName: fundstypename,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                console.log(result.obj)
                resolve(result.obj)
            } else {
                message.warning(result.msg)
                reject()
            }
        })
    })
    //跳转到系统字典
    toSysMap = () => {
        this.props._this.add && this.props._this.add({
            key: 100400030000,
            name: "系统字典",
            url: "/systemcenter/sysmod/sysDictionary"
        })
    }

    //打币校验规则
    ckRules = async (item, cb) => {
        const ckRule = await this.fetchCheck(item)
        const { status, rate } = ckRule

        //调用cb
        const cCb = func => (...rest) => func && func(...rest)
        let mct = {
            0:{
                content:`前端UC接口异常，市价获取失败，配置价格为${rate}，请确认是否以配置价格打币？`,
                okText: '确定打币',
                onOk:() => cCb(cb)({ rate, ...item })
            },
            3:{
                content:`前端UC接口异常，市价获取失败，请先配置价格后再切换至提现审核页面操作“确认打币”`,
                okText: '配置价格',
                onOk:() => this.toSysMap()
            },
            4:{
                content:`操作员按指引跳转至系统管理中配置价格，再切换至提现审核页面操作“确认打币”` ,
                okText: '配置价格',
                onOk:() => this.toSysMap()
            }
        }
        const mc = (status) => {
            Modal.confirm({
                title: `${item.fundstypename}提现打币`,
                ...mct[status],
                okType: 'danger',
                cancelText: '取消',
                onOk: () => {
                    // cCb(cb)({ rate, ...item })
                    mct[status].onOk()
                    // this.setSureBtn({ rate, ...item })
                },
                onCancel() {
                    console.log('Cancel');
                },
            })
        }

        switch (status) {
            case 0://uc接口异常
                // Modal.confirm({
                //     title: `${item.fundstypename}提现打币`,
                //     content: `前端UC接口异常，市价获取失败，配置价格为${rate}，请确认是否以配置价格打币？`,
                //     okText: '确定打币',
                //     okType: 'danger',
                //     cancelText: '取消',
                //     onOk: () => {
                //         cCb(cb)({ rate, ...item })
                //         // this.setSureBtn({ rate, ...item })
                //     },
                //     onCancel() {
                //         console.log('Cancel');
                //     },
                // })
                mc(status)
                break;
            case 1://uc接口成功
            case 2://uc接口返回为零
                cCb(cb)({ rate, ...item })
                break;
            case 3://请配置折算比例
                // Modal.confirm({
                //     title: `${item.fundstypename}提现打币`,
                //     content: `前端UC接口异常，市价获取失败，请先配置价格后再切换至提现审核页面操作“确认打币”`,
                //     okText: '配置价格',
                //     okType: 'danger',
                //     cancelText: '取消',
                //     onOk: () => {
                //         this.toSysMap()
                //         // this.setSureBtn({ rate, ...item })
                //     },
                //     onCancel() {
                //         console.log('Cancel');
                //     },
                // })
                // this.setSureBtn({ rate, ...item })
                mc(status)
                break;
            case 4://请配置折算比例
                // Modal.confirm({
                //     title: `${item.fundstypename}提现打币`,
                //     content: `操作员按指引跳转至系统管理中配置价格，再切换至提现审核页面操作“确认打币”                    `,
                //     okText: '配置价格',
                //     okType: 'danger',
                //     cancelText: '取消',
                //     onOk: () => {
                //         this.toSysMap()
                //         // this.setSureBtn({ rate, ...item })
                //     },
                //     onCancel() {
                //         console.log('Cancel');
                //     },
                // })
                // this.setSureBtn({ rate, ...item })
                mc(status)
                break;
            default:
                break;

        }
    }
    cksetSureBtn = async (item) => {
        this.ckRules(item, this.setSureBtn)
    }

    // //确认打币按钮是否置灰
    setSureBtn(item) {
        // /**
        //  * @author oliver
        //  * @description 资金状态为异常时 禁止确认打币 customerOperation:03正常
        //  */

        // if (item.customerOperation !== '03') {
        //     // Modal.warning({
        //     //     title: '用户资金异常，请处理后再确认！',
        //     //     okText: '确认'
        //     // })

        //     return false
        // }
        axios.post(DOMAIN_VIP + "/accountManage/query", qs.stringify({
            id: 0,
            fundType: item.fundstype,
            type: 3,
            pageIndex: 1,
            pageSize: 10,
            downloadaddr: item.toaddress
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                const sureBtn = Number(item.afterAmount) > Number(result.data.finanaccountList[0].amount) || result.data.errorType == 2;

                this.setState({
                    sureBtn,
                    hasAmount: result.data.finanaccountList[0].amount,
                }, () => this.palySurecoin(item, sureBtn, result.data.errorType))
            }
        })
    }

    //确认打币
    palySurecoin(item, sureBtn, errorType) {
        const { hasAmount } = this.state
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" disabled={sureBtn} loading={this.state.loading} onClick={() => this.modalGoogleCode(item, 'sure')}>
                确认打币
            </Button>,
        ]

        this.setState({
            visible: true,
            modalHtml: <ModalSure item={item} hasAmount={hasAmount} errorType={errorType} />,
            title: item.fundstypename + "提现打币",
            width: "700px"
        })

    }
    //确认打币按钮
    palySurecoinBtn(item) {
        const fundsComm = new BigNumber(item.amount).minus(item.afterAmount).toFixed()
        axios.post(DOMAIN_VIP + "/withdraw/confirmSuc", qs.stringify({
            downloadid: item.downloadid,
            accountId: item.id,
            fundsType: item.fundstype,
            fundsTypeName: item.fundstypename,
            money: item.afterAmount,
            memo: item.remark,
            userId: item.userid,
            fundsComm: fundsComm,
            userName: item.username,
            rate: item.rate
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                message.success(result.msg)
                this.requestAuditTable()
            } else {
                message.warning(result.msg)
            }
        })
    }

    //全部 或者 部分 打币
    async playCoin(item) {
        const { selectedRowKeys, auditType } = this.state
        const ckRule = await this.fetchCheck(item)
        let idsKey = selectedRowKeys.join(",")
        axios.post(DOMAIN_VIP + '/withdraw/confirmAll', qs.stringify({
            ids: idsKey,
            fundsType: auditType,
            rate: ckRule.rate
        })).then(res => {
            const result = res.data;
            let obj = result.data
            if (result.code == 0) {
                let objKey = Object.keys(obj)[0]
                objKey == 0 ? message.success(obj[objKey]) : message.warning(obj[objKey])
                this.setState({
                    visible: false,
                })
                this.onEmptyRowKeys(this.requestAuditTable)

            } else {
                message.warning(result.msg)
            }
        })
    }
    //清空多选框
    onEmptyRowKeys = (cb) => {
        this.setState({
            selectedRowKeys: [],
            selectedRows: [],
        }, () => cb && cb())
    }
    //查看客户操作类型
    seeCustomerOpe(id) {

        let item = {};
        let limitCause = '';
        axios.post(DOMAIN_VIP + '/userInfo/getLimitCauseById', qs.stringify({
            userId: id,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                limitCause = result.data
                item = { 'limitCause': limitCause }
                this.setState({
                    visible: true,
                    width: '600px',
                    title: '查看客户操作类型',
                    modalHtml: <ModalCustomerOpe item={item} onlySee={true} />

                })
            } else {
                message.warning(result.msg)
            }
        })

        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]

    }
    onReset = (item, type) => {
        let self = this
        let title = type == 'confirmReset' ? '您确定要重置吗，请先确认该笔交易是否已上链！' : '您确定要重置吗？'
        Modal.confirm({
            title: title,
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(item, type)

            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    onResetBtn = (item, type) => {
        let url = type == 'confirmReset' ? '/withdraw/confirmReset' : '/withdraw/sendingReset'
        axios.post(DOMAIN_VIP + url, qs.stringify({
            downloadId: item.downloadid,
            fundsType: item.fundstype
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.requestAuditTable()
                //    message.success(result.msg)
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

    render() {
        const { showHide, tab, fundType, auditType, status, userid, username, customerOperation, tableViewList, commandid, remark, moneyMin, moneyMax, selectedRowKeys, tableSource, pagination, pageIndex, pageSize, visible, title, width, configTime, submitTime, limitBtn } = this.state
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onChangeTable,
            // onSelectAll:this.onSelectAllTable
            fixed: 'left'
        };
        BigNumber.RM = 0;

        return (
            <div className="right-con">
                <div className="page-title">
                    {/*当前位置：财务中心 > 充提管理 > 提现审核*/}
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <Tabs activeKey={tab} onChange={this.callbackTabs}>
                            <TabPane tab="提现总览" key="1">
                                {showHide && <div className="x_panel">

                                    <div className="x_content">
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <FundsTypeList url={COMMON_QUERYATTRUSDTE} fundsType={fundType} col='3' handleChange={this.handleChangeType} />
                                        </div>
                                        <div className="col-md-4 col-sm-4 col-xs-4 right">
                                            <div className="right">
                                                <Button type="primary" onClick={this.requestViewTable}>查询</Button>
                                                <Button type="primary" onClick={this.onResetState}>重置</Button>
                                            </div>
                                        </div>
                                    </div>
                                </div>}
                                <div className="x_panel">

                                    <div className="x_content">
                                        <div className="table-responsive">
                                            <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                                <thead>
                                                    <tr className="headings">
                                                        <th className="column-title">序号</th>
                                                        <th className="column-title">资金类型</th>
                                                        <th className="column-title">待确认笔数</th>
                                                        <th className="column-title">待确认金额</th>
                                                        <th className="column-title">已确认</th>
                                                        <th className="column-title">已成功</th>
                                                        <th className="column-title">已失败</th>
                                                        <th className="column-title">已取消</th>
                                                        <th className="column-title">发送中</th>
                                                        <th className="column-title">操作</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {
                                                        tableViewList.length > 0 ?
                                                            tableViewList.map((item, index) => {
                                                                return (
                                                                    <tr key={index}>
                                                                        <td>{(pageIndex - 1) * pageSize + index + 1}</td>
                                                                        <td>{item.fundTypeName}</td>
                                                                        <td><a href="javascript:void(0)" onClick={() => this.jumpPaper(item.fundType)}>{item.waitCount}</a></td>
                                                                        <td>{item.waitAmount}</td>
                                                                        <td>{item.confirmCount}</td>
                                                                        <td>{item.successCount}</td>
                                                                        <td>{item.failCount}</td>
                                                                        <td>{item.cancelCount}</td>
                                                                        <td>{item.sendingCount}</td>
                                                                        <td>
                                                                            <a href="javascript:void(0)" onClick={() => this.jumpPaper(item.fundType)}>提现审核</a>
                                                                        </td>
                                                                    </tr>
                                                                )
                                                            })
                                                            : <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                                    }
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </TabPane>
                            <TabPane tab="提现审核" key="2">
                                {showHide && <div className="x_panel">
                                    <div className="x_content">
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <FundsTypeList url={COMMON_QUERYATTRUSDTE} fundsType={auditType} paymod col='3' handleChange={this.handleChangeAudit} />
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">用户编号：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">用户名：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="username" value={username} onChange={this.handleInputChange} />
                                                    <b className="icon-fuzzy">%</b>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">打币类型：</label>
                                                <div className="col-sm-8">
                                                    <Select value={commandid} style={{ width: SELECTWIDTH }} onChange={this.handleChangeCommandid}>
                                                        <Option value="">请选择</Option>
                                                        <Option value="0">自动</Option>
                                                        <Option value="1">人工</Option>
                                                    </Select>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">资金状态：</label>
                                                <div className="col-sm-8">
                                                    <Select value={customerOperation} style={{ width: SELECTWIDTH }} onChange={this.handleChangeOperation}>
                                                        <Option value="">请选择</Option>
                                                        <Option value="03">正常</Option>
                                                        <Option value="02">异常</Option>
                                                    </Select>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">提现金额：</label>
                                                <div className="col-sm-8">
                                                    <div className="left col-sm-5 sm-box">
                                                        <input type="text" className="form-control" name="moneyMin" value={moneyMin} onChange={this.handleInputChange} />
                                                    </div>
                                                    <div className="left line34">-</div>
                                                    <div className="left col-sm-5 sm-box">
                                                        <input type="text" className="form-control" name="moneyMax" value={moneyMax} onChange={this.handleInputChange} />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">确认时间：</label>
                                                <div className="col-sm-8">
                                                    <RangePicker onChange={this.onChangeTime} value={configTime} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">提交时间：</label>
                                                <div className="col-sm-8">
                                                    <RangePicker onChange={this.onChangeSubTime} value={submitTime} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-4 col-sm-4 col-xs-4 right martop4">
                                            <div className="right">
                                                <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                                <Button type="primary" onClick={this.onResetState}>重置</Button>
                                            </div>
                                        </div>
                                    </div>
                                </div>}
                                <div className="x_panel">

                                    <div className="x_content">
                                        <div className="btn-box">
                                            {~limitBtn.indexOf('choseCount') ? <Button type="more" onClick={() => this.countChecked('part', 'swap')}>统计选中金额</Button> : ''}
                                            {~limitBtn.indexOf('choseCount') ? <Button type="more" onClick={() => this.countChecked('all', 'swap')}>统计全部金额</Button> : ''}
                                            {~limitBtn.indexOf('confirmAll') ? (status == 1 && <Button type="more" disabled={auditType == 27 ? true : false} onClick={() => this.countChecked('all', 'playCoin')}>全部打币</Button>) : ''}
                                            {~limitBtn.indexOf('confirmAll') ? (status == 1 && <Button type="more" disabled={auditType == 27 ? true : false} onClick={() => this.countChecked('part', 'playCoin')}>选中打币</Button>) : ''}
                                        </div>
                                        <Tabs defaultActiveKey="1" onChange={this.callbackAuditTabs}>
                                            <TabPane tab="待确认" key="1"></TabPane>
                                            <TabPane tab="已确认" key="2"></TabPane>
                                            <TabPane tab="已成功" key="3"></TabPane>
                                            <TabPane tab="已失败" key="4"></TabPane>
                                            <TabPane tab="已取消" key="5"></TabPane>
                                            <TabPane tab="发送中" key="6"></TabPane>
                                        </Tabs>
                                        <div className=" table-responsive table-checkbox table-responsive-fixed">
                                            {
                                                status == 1 || status == 2 || status == 6 ?
                                                    <Table
                                                        dataSource={tableSource}
                                                        rowSelection={rowSelection}
                                                        bordered
                                                        pagination={pagination}
                                                        locale={{ emptyText: '暂无数据' }}
                                                        scroll={{ x: 1800 }}
                                                    >
                                                        <Column title="序号" dataIndex="index" render={(text, record) => (
                                                            <span>{text}</span>
                                                        )} />
                                                        <Column title="资金类型" dataIndex="fundstypename" />
                                                        <Column title="提现编号" dataIndex="downloadid" />
                                                        <Column title="用户编号" dataIndex="userid" />
                                                        <Column title="提现金额" className='moneyGreen' dataIndex="amount" render={t => toThousands(t, true)} />
                                                        <Column title="实际金额" className='moneyGreen' dataIndex="afterAmount" render={t => toThousands(t, true)} />
                                                        <Column title="基准手续费" className='moneyGreen' dataIndex="fees" render={t => toThousands(t, true)} />
                                                        <Column title="实际手续费" className='moneyGreen' dataIndex="putfees" render={t => toThousands(t, true)} />
                                                        <Column title="申请时间" dataIndex="submittime" />
                                                        {status == 2 && <Column title="审核时间" dataIndex="managetime" />}
                                                        <Column title="提现地址" dataIndex="toaddress" />
                                                        <Column title="用户限制" dataIndex="customerOperationName" render={(text, record, index) => (
                                                            (() => {
                                                                if (text == '异常') {
                                                                    return <span><a href="javascript:void(0)" className="mar10" onClick={() => this.seeCustomerOpe(tableSource[index].userid)}>{text}</a></span>
                                                                } else {
                                                                    return text
                                                                }
                                                            })()
                                                        )} />
                                                        <Column title="备注" dataIndex="remark" />
                                                        <Column title="操作" dataIndex="action" render={(text, record) => (
                                                            <span>
                                                                {status == 1 && ~limitBtn.indexOf('confirmCancel') ? <a href="javascript:void(0)" className="mar10" onClick={() => this.onConfirmCancel(record, 'cancel')}>取消</a> : ''}
                                                                {status == 1 && ~limitBtn.indexOf('confirmSuc') && record.customerOperation == '03' ? <a href="javascript:void(0)" className="mar10" onClick={() => this.debounce(this.cksetSureBtn, 500)(record, 'sure')}>确认</a> : '确认'}
                                                                {status == 2 && ~limitBtn.indexOf('confirmReset') ? <a href="javascript:void(0)" className="mar10" onClick={() => this.onReset(record, 'confirmReset')}>重置</a> : ''}
                                                                {status == 6 && ~limitBtn.indexOf('sendingReset') ? <a href="javascript:void(0)" className="mar10" onClick={() => this.onReset(record, 'sendingReset')}>重置</a> : ''}
                                                            </span>
                                                        )} />
                                                    </Table>
                                                    :
                                                    <Table dataSource={tableSource} rowSelection={rowSelection} bordered pagination={pagination} locale={{ emptyText: '暂无数据' }}>
                                                        <Column title="序号" dataIndex="index" key="index" />
                                                        <Column title="资金类型" dataIndex="fundstypename" key="fundstypename" />
                                                        <Column title="提现编号" dataIndex="downloadid" key="downloadid" />
                                                        <Column title="用户编号" dataIndex="userid" key="userid" />
                                                        <Column title="提现金额" className='moneyGreen' dataIndex="amount" key="amount" render={t => toThousands(t, true)} />
                                                        <Column title="实际金额" className='moneyGreen' dataIndex="afterAmount" key="afterAmount" render={t => toThousands(t, true)} />
                                                        <Column title="基准手续费" className='moneyGreen' dataIndex="fees" render={t => toThousands(t, true)} />
                                                        <Column title="实际手续费" className='moneyGreen' dataIndex="putfees" render={t => toThousands(t, true)} />
                                                        <Column title="申请时间" dataIndex="submittime" key="submittime" />
                                                        {status == 6 ? '' : <Column title="审核时间" dataIndex="managetime" key="managetime" />}
                                                        <Column title="审核人" dataIndex="managerid" key='managerid' render={(managerid, record) => managerid === 1 ? '用户发起' : record.managename} />
                                                        <Column title="提现地址" dataIndex="toaddress" key="toaddress" />
                                                        <Column title="用户限制" dataIndex="customerOperationName" key="customerOperationName" render={(text, record, index) => (
                                                            (() => {
                                                                if (text == '异常') {
                                                                    return <span><a href="javascript:void(0)" className="mar10" onClick={() => this.seeCustomerOpe(tableSource[index].userid)}>{text}</a></span>
                                                                } else {
                                                                    return text
                                                                }
                                                            })()
                                                        )} />
                                                        <Column title="备注" dataIndex="remark" />
                                                        <Column title="打币类型" dataIndex="auditorTypeName" key="auditorTypeName" />
                                                    </Table>

                                            }

                                        </div>
                                    </div>
                                </div>
                            </TabPane>
                        </Tabs>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {this.state.modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange={this.handleInputChange}
                    mid='WA'
                    visible={this.state.googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }

}






























































