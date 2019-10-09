
import React from 'react'
import ReactDOM from 'react-dom'
import axios from '../../../utils/fetch'
import cookie from 'js-cookie'
import qs from 'qs'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT, SELECTWIDTH, PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Select, Modal, Button, Table, Pagination, message, Spin } from 'antd'
import ModalSettle from './modal/modalSettle.js'
import ModalEdit from './modal/modalEdit.js'
// import ModalSettleInfo from './modal/modalSettleInfo'
import GoogleCode from '../../common/modal/googleCode'
import { toThousands, pageLimit, dateToFormat } from '../../../utils/index'
import SelectAType from '../select/selectAType'
import SelectChoice from '../select/selectChoice'
import ModalOneKeySettle from './modal/modalOneKeySettle'
import moment from 'moment'
import CommonTable from 'CTable'
const confirm = Modal.confirm;
import Overlay from '../../../components/overlays'
const Option = Select.Option;

export default class AccountManage extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            isShowModal: false,
            isShow: 1,
            modalHtml: '',
            title: '',
            findsType: '0',
            findsAccountType: '0',
            findsChoice: '0',
            dataSource: [],
            dayTag0: DEFAULTVALUE,
            dayTag24: DEFAULTVALUE,
            modifyAmount: '',
            modifyMemo: '',
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            visible: false,
            isloaded: false,
            memo: '',
            width: '',
            limitBtn: [],
            check: '',
            googVisibal: false,
            item: {},
            type: '',
            loading: false,
            height: 0,
            tableScroll: {
                tableId: 'TSFLTww',
                x_panelId: 'TSFLTxwww',
                defaultHeight: 500,
                height: 0,
            }
        }

        this.handleChangeType = this.handleChangeType.bind(this)
        this.handleChangeFind = this.handleChangeFind.bind(this)
        this.handleChangeChoice = this.handleChangeChoice.bind(this)
        this.goWorkMoadl = this.goWorkMoadl.bind(this)
        // this.offWorkModal = this.offWorkModal.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.modifyModal = this.modifyModal.bind(this)
        this.onResetSelect = this.onResetSelect.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onModifySave = this.onModifySave.bind(this)
        this.onDeleteTableItem = this.onDeleteTableItem.bind(this)
        this.onSettleSave = this.onSettleSave.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)

        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }

    componentDidMount() {
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('accountManage', this.props.permissList)
        })


    }
    componentWillReceiveProps() {


    }

    closeSpin = () => {
        this.setState(() => ({
            isShowModal: false
        }))
    }
    //查询 按钮
    inquireBtn() {
        this.setState({
            pageIndex: PAGEINDEX
        })
        this.requestTable(PAGEINDEX)
    }

    //请求 table 列表数据
    requestTable(currentIndex, currentSize) {
        const { findsType, findsChoice, findsAccountType, pageIndex, pageSize } = this.state;
        axios.post(DOMAIN_VIP + '/accountManage/query', qs.stringify({
            id: findsChoice,
            fundType: findsType,
            type: findsAccountType,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    dataSource: result.data.finanaccountList,
                    dayTag0: result.data.dayTag0,
                    dayTag24: result.data.dayTag24,
                    pageTotal: result.data.totalCount,
                    isloaded: true,
                    isShow: result.data.state,
                    pageIndex: currentIndex || pageIndex,
                    pageSize: currentSize || pageSize
                })
            } else {
                message.warning(result.msg)
            }
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


    handleChangeType(value) {
        this.setState({
            findsType: value
        })
    }

    handleChangeFind(value) {
        this.setState({
            findsAccountType: value
        })
    }

    handleChangeChoice(value) {
        this.setState({
            findsChoice: value
        })
    }
    //重置
    onResetSelect() {
        this.setState({
            findsType: '0',
            findsAccountType: '0',
            findsChoice: '0',
        })
    }
    //关闭弹窗
    handleCancel() {
        this.setState({
            visible: false,
            loading: false,
            isShowModal: false
        })
    }
    //结算弹窗
    goWorkMoadl(item, tab) {
        axios.post(DOMAIN_VIP + '/accountManage/settlement', qs.stringify({
            accountId: item.id,
            accType: item.type,
            fundTypeName: item.fundTypeName
        })).then(res => {
            const result = res.data;
            const { type, name, id, daytag, fundtype, fundTypeName } = item;
            //fundstype ==10(usdt)时，充值记录备注为 => 本次结算USDT最后一笔充值记录编号：3,USDTE最后一笔充值记录编号：31
            /**
             * @author oliver
             * @param {*} str 充值||提现
             * @param {*} usdtNum usdt的编号
             * @param {*} usdteNum usdte的编号
             * @returns [String]
             */
            const USDT_str = (str, usdtNum, usdteNum) => {
                return `本次结算USDT最后一笔${str}记录编号：${usdtNum},USDTE最后一笔${str}记录编号：${usdteNum}`
            }

            if (result.code == 0) {
                let textDeposit = `${fundtype == 10 ? USDT_str('充值', result.data.detailsBean.detailsid, result.data.detailsBean.detailsusdteid) : `本次结算最后一笔充值记录编号：${result.data.detailsBean.detailsid}。`}
${result.data.balanceFlag > 0 ? `本次结算账务情况：${result.data.finanbalance.amount}+${result.data.detailsBean.amount}=${result.data.finanaccount.amount}` : `本次结算账务情况：${result.data.finanbalance.amount}+${result.data.detailsBean.amount}≠${result.data.finanaccount.amount}`}`;
                let textWithdraw = `${fundtype == 10 ? USDT_str('提现', result.data.detailsBean.detailsid, result.data.detailsBean.detailsusdteid) : `本次结算最后一笔提现记录编号：${result.data.detailsBean.detailsid}。`}
${result.data.balanceFlag > 0 ? `本次结算账务情况：${result.data.finanbalance.pertotalamount}+${result.data.detailsBean.amount}=${result.data.finanaccount.curtotalamount}` : `本次结算账务情况：${result.data.finanbalance.pertotalamount}+${result.data.detailsBean.amount}≠${result.data.finanaccount.curtotalamount}`}`;

                let textArea = type == 1 ? textDeposit : textWithdraw;
                let modalHtml = <ModalSettle
                    item={item}
                    textArea={textArea}
                    result={res.data}
                    handleInputChange={this.handleInputChange}
                    watchDetail={this.watchDetail} />
                let settleItem = {
                    accountId: id,
                    fundType: fundtype,
                    fundTypeName: fundTypeName,
                    dayTag: daytag,
                    balanceFlag: result.data.balanceFlag,
                    pertotalamount: result.data.finanaccount.curtotalamount,
                    accType: type,
                    maxDetailsId: result.data.detailsBean.detailsid,
                    perAmount: result.data.finanaccount.amount,
                    detailsusdteid: result.data.detailsBean.detailsusdteid, //usdte id
                };
                this.footer = [
                    <Button key="back" onClick={this.handleCancel}>取消</Button>,
                    <Button key="submit" type="more" loading={this.state.loading} onClick={result.data.balanceFlag == '0' ? () => this.brforeSettle(settleItem, tab) : () => this.onSettleSave(settleItem, tab)}>
                        保存修改
                    </Button>,
                ];
                this.setState({
                    visible: true,
                    title: "结算账户：" + name,
                    modalHtml: modalHtml,
                    settleItem: settleItem,
                    memo: textArea,
                    width: "700px"
                })

            } else {
                message.warning(result.msg)
            }
        })
    }
    brforeSettle(item, tab) {
        let self = this
        confirm({
            title: '此笔金额有问题，确定结算?',
            onOk() { self.onSettleSave(item, tab) },
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onCancel() { },
        });
    }
    //结算确定按钮
    onSettleSave(item, tab) {
        this.setState({
            loading: true
        })
        const { accountId, balanceFlag, pertotalamount, accType, maxDetailsId, perAmount, fundType, fundTypeName, detailsusdteid } = item
        const { memo, dayTag0, dayTag24, isShow } = this.state
        let dayTagN = (item.accType == 1 || item.accType == 3) ? (item.dayTag < dayTag0 || item.dayTag == dayTag24 ? dayTag0 : dayTag24) : '';
        axios.post(DOMAIN_VIP + '/accountManage/doAoru', qs.stringify({
            accountId: accountId,
            fundType: fundType,
            fundTypeName: fundTypeName,
            memo: memo,
            dayTag: dayTagN,
            balanceFlag: balanceFlag,
            perTotalAmount: pertotalamount,
            accType: accType,
            maxDetailsId: maxDetailsId,
            perAmount: perAmount,
            tab,
            maxDetailsUsdtId: detailsusdteid,//usdte
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    visible: false,
                    loading: false
                })
                this.requestTable();
                message.success(result.msg)
            } else {
                message.warning(result.msg)
                this.setState({
                    loading: false
                })
            }
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
                this.setState({
                    googVisibal: false
                })
                if (type == 'save') {
                    this.onModifySave(item)
                } else if (type == 'onekey') {
                    this.oneKeyBtn(item)
                }

            } else {

                message.warning(result.msg)
            }
        })
    }

    //修改弹窗
    modifyModal(item) {
        let typeStr;
        switch (item.type) {
            case 1:
                typeStr = "充值账户";
                break;
            case 2:
                typeStr = "流转账户";
                break;
            case 3:
                typeStr = "提现账户";
                break;
            case 4:
                typeStr = "网络费";
                break;
        }
        item.typeStr = typeStr
        let modalHtml = <ModalEdit modifyItem={item} handleInputChange={this.handleInputChange} />
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item, 'save')}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible: true,
            title: '编辑账户信息',
            width: "700px",
            modalHtml: modalHtml,
            modifyAmount: item.amount,
            modifyMemo: item.memo
        })
    }
    //修改弹窗 保存按钮
    onModifySave(modifyItem) {
        const { modifyAmount, modifyMemo } = this.state
        const { id, amount, memo } = modifyItem
        if (amount === modifyAmount && modifyMemo === memo) {
            message.warning('未做任何修改!');
            return false;
        }
        if (modifyAmount === "") {
            message.warning('请输入要修改的余额');
            return false;
        }
        if (modifyMemo === "") {
            message.warning('请输入要修改的备注');
            return false;
        }
        axios.post(DOMAIN_VIP + '/accountManage/update', qs.stringify({
            id: id,
            fund: modifyAmount,
            memo: modifyMemo
        })).then((res) => {
            const result = res.data;
            if (result.code == 0) {
                message.success(result.msg);
                this.setState({
                    visible: false
                })

                this.requestTable();
            } else {
                message.warning(result.msg)
            }
        })
    }
    //删除 table item
    onDeleteTableItem(e, id) {
        e.preventDefault();
        let self = this;
        Modal.confirm({
            title: '你确定要删除此条记录吗?',
            okText: 'Yes',
            cancelText: 'No',
            onOk() {
                axios.post(DOMAIN_VIP + '/accountManage/delete', qs.stringify({
                    id: id
                })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.requestTable({
                            isWorkModalShow: false
                        });
                        message.success(result.msg)
                    } else {
                        message.warning(result.msg)
                    }
                })
            }
        });
    }

    //点击收起
    clickHide() {
        let { showHide, } = this.state;
        this.setState({
            showHide: !showHide
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
    requestOneKeyTable = (tab) => {
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP + '/accountManage/settlementAll', qs.stringify({
                tab
            })).then(res => {
                const result = res.data;
                let errorSum = 0, successSum = 0;
                if (result) {
                    result.map((item, index) => {
                        item.key = index;
                        item.index = index + 1;
                        item.newmemo = item.type == 1
                            ?
                            (`本次结算最后一笔充值记录编号：${item.detailsid}。
                            ${item.balanceFlag > 0
                                    ?
                                    `本次结算账务情况：${item.amountLast}+${item.amountRecharge}=${item.amount}`
                                    :
                                    `本次结算账务情况：${item.amountLast}+${item.amountRecharge}≠${item.amount}`}`)
                            :
                            (`本次结算最后一笔提现记录编号：${item.detailsid}。
                            ${item.balanceFlag > 0
                                    ?
                                    `本次结算账务情况：${item.pertotalamount}+${item.balanceDetails}=${item.curtotalamount}`
                                    :
                                    `本次结算账务情况：${item.pertotalamount}+${item.balanceDetails}≠${item.curtotalamount}`}`);
                        item.balanceFlag > 0 ? successSum++ : errorSum++;
                    }
                    )
                } else {
                    this.handleCancel()

                    return
                }
                let obj = {
                    list: result || [],
                    errorSum,
                    successSum,
                }
                resolve(obj)
            })
        }).catch(error => { console.log(error) })
    }
    oneKeySettleModal = (e, type) => {
        //加载中
        this.setState(() => ({
            isShowModal: true
        }))
        let title = e.target.value
        this.requestOneKeyTable(type).then(res => {
            this.footer = [
                <Button key="back" onClick={this.handleCancel}>取消结算</Button>,
                <Button key="submit" type="more" disabled={!res.successSum} loading={this.state.loading} onClick={() => this.modalGoogleCode({ tab: type }, 'onekey')}>确认结算</Button>,
            ]
            this.setState({
                title,
                visible: true,
                isShow: type,
                width: '1000px',
                isShowModal: false,
                loading: false,
                modalHtml: <ModalOneKeySettle item={res} />
            })
        })
    }
    oneKeyBtn = (item) => {
        axios.post(DOMAIN_VIP + '/accountManage/doAoruAKey', qs.stringify({
            // tab:item.tab,
            adminid: cookie.get('userId')
        }))
            .then(res => {
                const result = res.data;
                this.setState({
                    visible: false
                }, () => {
                    this.requestTable()
                    message.success(result.msg)
                })
            })
    }
    createColumns = (pageIndex, pageSize) => {
        const { limitBtn, dayTag0, dayTag24 } = this.state
        return [
            { title: '序号', className: 'wordLine', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '资金类型', className: 'wordLine', dataIndex: 'fundTypeName' },
            { title: '账户编号', className: 'wordLine', dataIndex: 'id' },
            { title: '账户名称', className: 'wordLine', dataIndex: 'name' },
            { title: '账户余额', className: 'wordLine moneyGreen', dataIndex: 'amount', },
            { title: '更新时间', className: 'wordLine', dataIndex: 'updatetime', render: t => dateToFormat(t) },
            { title: '备注', className: 'wordLine', dataIndex: 'memo', },
            {
                title: '结算', className: 'wordLine', dataIndex: 'snodebeltype', render: (t, r) => <span>
                    {
                        ((r.type == 1 || r.type == 3) && (limitBtn.indexOf('settlement') > -1)) ?
                            (r.daytag < dayTag0 || r.daytag == dayTag24 ? <a href='javascript:void(0)' className='table-btn-up' onClick={() => this.goWorkMoadl(r, 1)}>上班结算</a> : "")
                            : ""
                    }
                    {
                        ((r.type == 1 || r.type == 3) && (limitBtn.indexOf('settlement') > -1)) ?
                            (r.daytag == dayTag0 && r.daytag < dayTag24 ? <a href='javascript:void(0)' className='table-btn-down' onClick={() => this.goWorkMoadl(r, 0)}>下班结算</a> : "")
                            : ""
                    }
                </span>
            },
            {
                title: '操作', className: 'wordLine', dataIndex: 'snodeshowflag', render: (t, r) => <span>
                    {limitBtn.indexOf('update') > -1 ? <a href="javascript:void(0)" onClick={() => this.modifyModal(r)}>修改</a> : ''}
                    <a href="###" className="hide" onClick={(e) => this.onDeleteTableItem(e, r.id)}>删除</a>
                </span>
            },
        ]
    }
    render() {
        const { showHide, findsType, findsAccountType, findsChoice, dataSource, dayTag0, dayTag24, modifyItem, pageTotal, isloaded, visible, pageIndex, pageSize, width, limitBtn, isShow } = this.state
        return (

            <div className="right-con" >
                <div className="page-title" >
                    当前位置：财务中心 > 账务管理 > 充提结算
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>

                <div className="clearfix"></div>

                <div className="row">
                    <Spin spinning={this.state.isShowModal} style={{ position: 'static' }} >
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            {showHide && <div className="x_panel">

                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <SelectAType findsType={findsType} col='3' handleChange={this.handleChangeType}></SelectAType>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">账户类型：</label>
                                            <div className="col-sm-8">
                                                <Select value={findsAccountType} style={{ width: SELECTWIDTH }} onChange={this.handleChangeFind}>
                                                    <Option value="0">请选择</Option>
                                                    <Option value="1">充值账户</Option>
                                                    <Option value="3">提现账户</Option>
                                                    <Option value="4">网络费</Option>
                                                    <Option value="2">流转账户</Option>
                                                </Select>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <SelectChoice findsType={findsChoice} col='3' handleChange={this.handleChangeChoice}></SelectChoice>

                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12 marTop">
                                        <div className="right">
                                            <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                            <Button type="primary" onClick={this.onResetSelect}>重置</Button>
                                            <Button type="up" value='上班一键结算' onClick={(e) => this.oneKeySettleModal(e, 1)}>一键结算</Button>

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
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </Spin>
                </div>

                <Modal
                    visible={visible}
                    title={this.state.title}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width={width}
                >
                    {this.state.modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange={this.handleInputChange}
                    mid='AM'
                    visible={this.state.googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate}
                />

            </div>

        )
    }

}
























































