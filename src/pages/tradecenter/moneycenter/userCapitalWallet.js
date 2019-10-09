/**数据中心 》 资金中心 》 用户资金 》 钱包用户资金  */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, MODALCAPITALCHARGE, MODALCAPITALDEDUCT, MODALCAPITALFREEZE, MODALCAPITALUNFREEZE,PAGRSIZE_OPTIONS20,URLS } from '../../../conf'
import FundsTypeList from '../../common/select/fundsTypeList'
import ModalCapital from './modal/modalCapital'
import ModalTransfer from './modal/modaltransfer'
import GoogleCode from '../../common/modal/googleCode'
import { toThousands, pageLimit, tableScroll } from '../../../utils'
import { AsyncSelect } from '../../../components/select/asyncSelect'

import { DatePicker, Select, Modal, Button, Tabs,Table, Pagination, message } from 'antd'
const BigNumber = require('big.js')
const { COMMON_GETUSERTYPE } = URLS
export default class UserCapitalWallet extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            fundType: '2',
            userid: '',
            userName: '',
            moneyMin: '',
            moneyMax: '',
            freezMoneyMin: '',
            freezMoneyMax: '',
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            tableList: [],
            pageTotal: 0,
            modalHtml: '',
            visible: false,
            title: '',
            strMoney: '',
            memo: '',
            width: '',
            googleCode: '',
            checkGoogle: '',
            totalMoneyMin: '',
            totalMoneyMax: '',
            limitBtn: [],
            check: '',
            googVisibal: false,
            item: {},
            type: '',
            totalMoney:"" ,
            puwbalancesum:"" ,
            puwfreezsum:"",
            accountid:'',
            money:'',
            tableDataInterface: DOMAIN_VIP + "/walletUserCapital/query",
            summaryDataInterface: DOMAIN_VIP + "/walletUserCapital/sum",
            accountType:'0',//用户类型
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.coinCharge = this.coinCharge.bind(this)
        this.deduct = this.deduct.bind(this)
        this.coinFreez = this.coinFreez.bind(this)
        this.UncoinFreez = this.UncoinFreez.bind(this)
        this.Transfer = this.Transfer.bind(this)
        this.coinChargeModal = this.coinChargeModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.coinDeductModal = this.coinDeductModal.bind(this)
        this.coinFreezeModal = this.coinFreezeModal.bind(this)
        this.coinUnfreezeModal = this.coinUnfreezeModal.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.coinTransferModal = this.coinTransferModal.bind(this)
        this.handleSelectChange = this.handleSelectChange.bind(this)
    }

    componentDidMount() {
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('walletUserCapital', this.props.permissList)
        })
    }
    componentWillUnmount() {

    }

    //输入时 input 设置到 satte
    handleInputChange(event, check) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //弹框select
    handleSelectChange(val) {
        this.setState({
            accountid:val
        })
    }

    //资金类型 select
    handleChangeType(val) {
        this.setState({
            fundType: val
        })
    }

    //查询 按钮
    inquireBtn() {
        this.setState({
            pageIndex: PAGEINDEX
        }, () => this.requestTable())
    }

    //table 列表请求
    requestTable(currIndex, currSize) {
        const { tableDataInterface,summaryDataInterface,fundType, userid, userName, moneyMin, moneyMax, freezMoneyMin, freezMoneyMax, pageIndex, pageSize, totalMoneyMin, totalMoneyMax,accountType } = this.state
        const parameter = {
            fundType: fundType,
            accountType,
            userId: userid,
            userName: userName,
            moneyMin: moneyMin,
            moneyMax: moneyMax,
            freezMoneyMin: freezMoneyMin,
            freezMoneyMax: freezMoneyMax,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize,
            totalMoneyMin, totalMoneyMax
        }
        
        axios.post(tableDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                BigNumber.RM = 0;
                let dataKey = {
                    "balance": null, //可用金额
                    "freez": null, //冻结金额
                    "userid": null, //用户编号
                    "fundstypename": null, //资金类型
                    'withdrawfreeze':null, //提现冻结,
                    'accountTypeName':null,//用户类型
                }

                let tableList = [];
                let limitBtn = pageLimit('walletUserCapital', this.props.permissList);
                
                result.data.list.map((item, index) => {
                    let json = new Object();
                    
                    json.key = item.id;
                    for (let key in dataKey) {
                        json.index = (result.data.currPage - 1) * result.data.pageSize + index + 1;
                        json[key] = item[key];
                    }
                    json.totalAmount = new BigNumber(json.balance).plus(json.freez);
                    let actions = <React.Fragment>
                        {limitBtn.indexOf('doCharge') > -1?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinChargeModal(item, 'doCharge')}>充{item.fundstypename}</a>:""}
                        {limitBtn.indexOf('doDeduction') > -1 ? <a className="mar10" href="javascript:void(0)" onClick={() => this.coinDeductModal(item, 'doDeduction')}>扣{item.fundstypename}</a> : ''}
                        {<a className="mar10" href="javascript:void(0)" onClick={(e) => {e.preventDefault; this.coinTransferModal(item,'tansfer')}}>划转资金</a> }
                        {limitBtn.indexOf('doFreez') > -1 ? (item.balance > 0 && <a className="mar10" href="javascript:void(0)" onClick={() => this.coinFreezeModal(item, 'doFreez')}>冻结可用资金</a>) : ''}
                        {limitBtn.indexOf('unFreez') > -1 ? (item.freez > 0 && <a className="mar10" href="javascript:void(0)" onClick={() => this.coinUnfreezeModal(item, 'unFreez')}>解冻冻结资金</a>) : ''}
                    </React.Fragment>;
                    json.action = actions;
                    tableList.push(json);
                })
                this.setState({
                    tableList,
                    pageSize: result.data.pageSize,
                    pageTotal: result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
        axios.post(summaryDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    puwbalancesum:result.data[0]&&result.data[0].puwbalancesum,
                    puwfreezsum:result.data[0]&&result.data[0].puwfreezsum,
                    totalMoney:result.data[0]&&result.data[0].allsum
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.requestTable(page, pageSize)
        this.setState({
            pageIndex: page,
            pageSize: pageSize
        })
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.requestTable(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }
    //重置按钮
    onResetState() {
        this.setState({
            fundType: '2',
            userid: '',
            userName: '',
            moneyMin: '',
            moneyMax: '',
            freezMoneyMin: '',
            freezMoneyMax: '',
            totalMoneyMin: '',
            totalMoneyMax: '',
            accountType:'0'
        })
    }
    //关闭弹窗
    handleCancel() {
        this.setState({
            visible: false,
            strMoney:'',
            money:''
        })
    }
    //谷歌弹窗关闭
    onhandleCancel() {
        this.setState({
            googVisibal: false
        })
    }

    // //google 验证弹窗
    modalGoogleCode(item, type, check) {
        const {strMoney, money} = this.state
        switch(type){
            case 'doCharge':
                if (!strMoney) {
                    message.warning("请输入您要充值的数量")
                    return false;
                }
                break;
            case 'doDeduction':
                if (!strMoney) {
                    message.warning("请输入您要扣除的数量")
                    return false;
                }
                break;
            case 'tansfer':
                if (!money) {
                    message.warning("请输入您要划转的数量")
                    return false;
                }
                break;
            case 'doFreez':
                if (!strMoney) {
                    message.warning("请输入您要冻结的数量")
                    return false;
                }
                break;
            case 'unFreez':
                if (!strMoney) {
                    message.warning("请输入您要解冻的数量")
                    return false;
                }
                break;
            default:
                break;

        }
        this.setState({
            googVisibal: true,
            item,
            type,
            check,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value) {
        const { item, type, check } = this.state
        const { googleCode, checkGoogle } = value
        let url = check ? "/common/checkTwoGoogleCode" : "/common/checkGoogleCode"
        axios.post(DOMAIN_VIP + url, qs.stringify({
            googleCode, checkGoogle
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                if (type == "doCharge") {
                    this.coinCharge(item)
                    console.log(item)
                } else if (type == "doDeduction") {
                    this.deduct(item)
                } else if (type == "doFreez") {
                    this.coinFreez(item)
                } else if (type == "unFreez") {
                    this.UncoinFreez(item)
                } else if (type == "tansfer") {
                    this.Transfer(item)
                }
                this.setState({
                    googVisibal: false
                })

            } else {
                message.warning(result.msg)
            }
        })
    }
    //充币
    coinChargeModal(item, type) {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item, type, 'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible: true,
            title: '系统充值',
            width: '700px',
            modalHtml: <ModalCapital item={item} type={MODALCAPITALCHARGE} handleInputChange={this.handleInputChange} />
        })
    }

    //充币 按钮
    coinCharge(item) {
        let self = this;
        const { fundstype, userid, username, fundstypename } = item
        const { strMoney, memo, fee } = this.state
        
        axios.post(DOMAIN_VIP + '/walletUserCapital/doCharge', qs.stringify({
            fundType: fundstype,
            userId: userid,
            userName: username,
            fundTypeName: fundstypename,
            strMoney: strMoney,
            memo: memo,
            fee: fee
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                self.setState({
                    visible: false,
                    strMoney: '',
                    memo: '',
                    fee: ''
                })
                message.success(result.msg)
                this.requestTable()
            } else {
                message.warning(result.msg)
            }
        })
    }
    //扣币弹窗
    coinDeductModal(item, type) {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item, type, 'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible: true,
            width: '700px',
            title: '系统扣除',
            modalHtml: <ModalCapital item={item} type={MODALCAPITALDEDUCT} handleInputChange={this.handleInputChange} />
        })
    }
    //扣币
    deduct(item) {
        const { fundstype, userid, username, fundstypename } = item
        const { strMoney, memo } = this.state
        if (!strMoney) {
            message.warning("请输入您要扣除的数量")
            return false;
        }
        axios.post(DOMAIN_VIP + '/walletUserCapital/doDeduction', qs.stringify({
            fundType: fundstype,
            userId: userid,
            userName: username,
            fundTypeName: fundstypename,
            strMoney: strMoney,
            memo: memo
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success(result.msg)
                this.setState({
                    visible: false,
                    strMoney: '',
                    memo: ''
                })
                this.requestTable()
            } else {
                message.warning(result.msg)
            }
        })
    }
    //冻结弹窗
    coinFreezeModal(item, type) {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item, type, 'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible: true,
            width: '700px',
            title: '系统冻结',
            modalHtml: <ModalCapital item={item} type={MODALCAPITALFREEZE} handleInputChange={this.handleInputChange} />
        })
    }
    //冻结
    coinFreez(item) {
        const { fundstype, userid, username, fundstypename } = item
        const { strMoney, memo } = this.state

        axios.post(DOMAIN_VIP + '/walletUserCapital/doFreez', qs.stringify({
            userName: username,
            userId: userid,
            fundTypeName: fundstypename,
            fundType: fundstype,
            freezMoney: strMoney,
            memo: memo
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success(result.msg)
                this.setState({
                    visible: false,
                    strMoney: '',
                    memo: ''
                })
                this.requestTable()
            } else {
                message.warning(result.msg)
            }
        })
    }
    //解冻弹窗
    coinUnfreezeModal(item, type) {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item, type, 'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible: true,
            width: '700px',
            title: '系统解冻',
            modalHtml: <ModalCapital item={item} type={MODALCAPITALUNFREEZE} handleInputChange={this.handleInputChange} />
        })
    }
    //解冻
    UncoinFreez(item) {
        const { fundstype, userid, username, fundstypename } = item
        const { strMoney, memo } = this.state
        
        axios.post(DOMAIN_VIP + '/walletUserCapital/unFreez', qs.stringify({
            fundType: fundstype,
            userId: userid,
            userName: username,
            fundTypeName: fundstypename,
            freezMoney: strMoney,
            memo: memo
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success(result.msg)
                this.setState({
                    visible: false,
                    strMoney: '',
                    memo: ''
                })
                this.requestTable()
            } else {
                message.warning(result.msg)
            }
        })
    }
    //划转资金弹窗
    coinTransferModal(item,type){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item, type,'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible: true,
            width: '900px',
            title: '划转资金',
            modalHtml: <ModalTransfer handleInputChange={this.handleInputChange} from='我的钱包' handleSelectChange = {this.handleSelectChange} item={item}/>
        })
    }
    //划转资金
    Transfer(item) {
        const { fundstype, userid,  } = item
        const {money,accountid} = this.state
        let self = this
        console.log(accountid)
        axios.post(DOMAIN_VIP + '/walletUserCapital/transfer', qs.stringify({
            fundsType: fundstype,
            from:'1',
            to: accountid,
            userId: userid,
            amount: money,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success(result.msg)
                this.setState({
                    visible: false,
                    money: '',
                    accountid: ''
                })
                this.requestTable()
                console.log(this.state)
            } else {
                message.warning(result.msg)
            }
        })
    }
    //点击收起
    clickHide() {
        let { showHide, xheight, pageSize } = this.state;
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
    //用户类型
    selectUserType = (v,k) => {
        this.setState({[k]: v})
    }
    render() {
        const { googVisibal,limitBtn, totalMoney ,puwbalancesum ,puwfreezsum , width, showHide, fundType, pageTotal, tableList, pageIndex, pageSize, totalMoneyMin, totalMoneyMax, title, modalHtml, visible, userid, userName, moneyMin, moneyMax, freezMoneyMin, freezMoneyMax,accountType  } = this.state
        let columns = [
            { title: '序号', dataIndex: 'index', key: 'index' },
            { title: '资金类型', dataIndex: 'fundstypename', key: 'fundstypename' },
            { title: '用户编号', dataIndex: 'userid', key: 'userid' },
            {title: '用户类型', dataIndex:'accountTypeName', key: 'accountTypeName'},
            { title: '总金额', className:'moneyGreen', dataIndex: 'totalAmount', key: 'totalAmount', sorter: true,render:(text)=>toThousands(text,true) },
            { title: '可用余额', className:'moneyGreen', dataIndex: 'balance', key: 'balance', sorter: true,render:(text)=>toThousands(text,true) },
            { title: '冻结金额', className:'moneyGreen', dataIndex: 'freez', key: 'freez', sorter: true,render:(text)=>toThousands(text,true) },
            { title: '提现冻结', className:'moneyGreen', dataIndex: 'withdrawfreeze', key: 'withdrawfreeze', sorter: true,render:(text)=>toThousands(text,true) },
            { title: '操作', dataIndex: 'action', key: 'action' },
        ]
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 用户资金 > 钱包用户资金
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList title='资金类型' fundsType={fundType} col='3' handleChange={this.handleChangeType}></FundsTypeList>
                                </div>
                                <AsyncSelect title='用户类型' paymod url={COMMON_GETUSERTYPE} value={accountType} onSelectChoose={v => this.selectUserType(v,'accountType')} />
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
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">总金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="totalMoneyMin" value={totalMoneyMin} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="totalMoneyMax" value={totalMoneyMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">可用金额：</label>
                                        <div className="col-sm-8 ">
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
                                        <label className="col-sm-3 control-label">冻结金额：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="freezMoneyMin" value={freezMoneyMin} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="freezMoneyMax" value={freezMoneyMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-3 col-sm-3 col-xs-3 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                            <div  className="table-responsive">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    总资金：{toThousands(totalMoney,true)}， &nbsp;&nbsp;&nbsp;
                                                    可用资金：{toThousands(puwbalancesum,true)}， &nbsp;&nbsp;&nbsp;
                                                    冻结资金：{toThousands(puwfreezsum,true)}，&nbsp;&nbsp;&nbsp;
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table columns={columns}
                                        dataSource={tableList}
                                        bordered={true}
                                        locale={{emptyText:'暂无数据'}}
                                        onChange={this.handleChangeTable}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            showSizeChanger: true,
                                            showQuickJumper: true,
                                            pageSizeOptions:PAGRSIZE_OPTIONS20,
                                        }} />
                                </div>
                                
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width={width}
                >
                    {modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange={this.handleInputChange}
                    mid='UCW'
                    visible={this.state.googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }
}






























































