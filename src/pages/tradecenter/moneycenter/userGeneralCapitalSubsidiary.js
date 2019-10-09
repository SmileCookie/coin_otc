/**数据中心 》 资金中心 》 用户资金 》 用户总资金明细  */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH, PAGESIZE_50, PAGRSIZE_OPTIONS } from '../../../conf'
import { Button, DatePicker, Select, Table, Modal, message } from 'antd'
import { toThousands } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;

export default class UserGeneralCapitalSubsidiary extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            accountType: [<Option key='0' value='0'>请选择</Option>, <Option key='1' value='1'>用户帐户</Option>, <Option key='2' value='2'>刷量帐户</Option>],
            accountTypeVal: "0",
            fundsType: "2",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE_50,
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
            userId: "",
            userName: "",
            sortType: "",
            money: "",
            tradingFrozen: "",
            advertisingFrozen: "",
            totalMoney: "",
            modalHtml: "",
            visible: false,
            walletAccount: "",
            contractAccount: "",
            currencyAccount: "",
            otcAccount: "",
            financialUserSum: '',//理财sum
        },
            this.clickHide = this.clickHide.bind(this);
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.requestTable = this.requestTable.bind(this);
        this.handleChangeType = this.handleChangeType.bind(this);
        this.onResetState = this.onResetState.bind(this);
        this.changPageNum = this.changPageNum.bind(this);
        this.onShowSizeChange = this.onShowSizeChange.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.requestSort = this.requestSort.bind(this);
        this.handleChangeTable = this.handleChangeTable.bind(this);
        this.handleAccountChangeType = this.handleAccountChangeType.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.openFreezeDetails = this.openFreezeDetails.bind(this);
    }
    componentDidMount() {
        // this.requestTable()

    }
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => this.requestTable())
    }
    handleChangeTable(pagination, filters, sorter) {

        console.log(sorter);
        // sorter.field
        // sorter.order
        // this.setState({
        //     sortType: sorter.order
        // }, () => this.requestTable());
    }
    handleInputChange(e) {
        const target = e.target;
        const value = target.value;
        const name = target.name
        let json = new Object();
        json[name] = value;
        this.setState(json);
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            begin: dateString[0],
            end: dateString[1],
            time: date
        })
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestTable(page, pageSize))

    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requestTable(current, size))
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    handleChangeType(value) {
        this.setState({
            fundsType: value
        })
    }
    handleAccountChangeType(value) {
        this.setState({
            accountTypeVal: value
        })
    }
    openFreezeDetails() {
        this.setState({ visible: true })
    }
    onResetState() {
        this.setState({
            accountTypeVal: "0",
            fundsType: "2",
            time: null,
            userId: "",
            userName: "",
        })
    }
    requestSort(type) {
        this.setState({ sortType: type }, () => this.requestTable())
    }
    requestTable(currIndex, currSize) {
        const {
            fundsType,
            userId,
            accountTypeVal,
            userName,
            sortType,
            pageIndex,
            pageSize
        } = this.state
        axios.post(DOMAIN_VIP + "/fundUserCapitalDetail/query", qs.stringify({
            fundType: fundsType,
            accountType: accountTypeVal,
            userId,
            userName,
            sortType,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                if (!result.data.list) {
                    message.warning(result.data)
                    return false
                }
                Big.RM = 0;
                let tableList = result.data.list;
                tableList.map((item, index) => {
                    item.index = (result.data.currPage - 1) * result.data.pageSize + index + 1;
                    item.key = index;
                    item.currencyFreeze = item.puFreezSum; //币币冻结
                    item.withdrawalFreeze = item.puwFreezSum; //钱包冻结 
                    item.contractFreeze = new Big(item.pufFreezSum).plus(item.pufPositionFreezSum); //期货冻结
                    item.otcFreeze = new Big(item.puoFrozenTradeSum).plus(item.puoFrozenFeeSum).plus(item.puoFrozenWithDrawSum); // otc冻结

                    item.freezeSecurity = new Big(item.currencyFreeze)
                        .plus(item.withdrawalFreeze)
                        .plus(item.contractFreeze)
                        .plus(item.otcFreeze);    //冻结资金

                    item.currencyAccount = new Big(item.puBalanceSum).plus(item.currencyFreeze);//币币账户
                    item.walletAccount = new Big(item.puwBalanceSum).plus(item.withdrawalFreeze);//钱包账户
                    item.contractAccount = new Big(item.pufBalanceSum).plus(item.contractFreeze);//期货账户
                    item.otcAccount = new Big(item.puoBalanceSum).plus(item.otcFreeze);//otc账户

                    //可用资金
                    item.availableBalance = new Big(item.pufBalanceSum)
                        .plus(item.puBalanceSum)
                        .plus(item.puoBalanceSum)
                        .plus(item.puwBalanceSum)
                        .plus(item.financialUserSum);

                    //总金额
                    item.totalAmount = new Big(item.currencyAccount)
                        .plus(item.walletAccount)
                        .plus(item.contractAccount)
                        .plus(item.otcAccount)
                        .plus(item.financialUserSum);//理财
                        
                })

                let modalHtml = <div className="table-responsive">
                    <Table
                        dataSource={tableList}
                        bordered={true}
                        pagination={false}
                    >
                        <Column title='序号' dataIndex='index' key='index' />
                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                        <Column title='OTC冻结' dataIndex='otcFreeze' key='otcFreeze' className="moneyGreen" render={t => toThousands(t) }  />
                        <Column title='币币冻结' dataIndex='currencyFreeze' key='currencyFreeze' className="moneyGreen" render={t => toThousands(t) }  />
                        <Column title='期货冻结' dataIndex='contractFreeze' key='contractFreeze' className="moneyGreen" render={t => toThousands(t) }  />
                        <Column title='提现冻结' dataIndex='withdrawalFreeze' key='withdrawalFreeze' className="moneyGreen" render={t => toThousands(t) }  />
                    </Table>
                </div>
                this.setState({
                    tableList,
                    pageSize: result.data.pageSize,
                    pageTotal: result.data.totalCount,
                    modalHtml: modalHtml,
                    money: tableList[0] && tableList[0].availableBalance,
                    freezeSecurity: tableList[0] && tableList[0].freezeSecurity,
                    totalMoney: tableList[0] && tableList[0].totalAmount,
                    walletAccount: tableList[0] && tableList[0].walletAccount,
                    contractAccount: tableList[0] && tableList[0].contractAccount,
                    currencyAccount: tableList[0] && tableList[0].currencyAccount,
                    otcAccount: tableList[0] && tableList[0].otcAccount,
                    financialUserSum: tableList[0] && tableList[0].financialUserSum,
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    handleCancel() {
        this.setState({ visible: false })
    }
    footer() {

    }
    render() {
        Big.RM = 0;
        const { showHide, money, totalMoney, freezeSecurity, accountTypeVal, moneyType, modalHtml, visible, fundsType, tableList, pageIndex, pageSize, pageTotal,
            walletAccount, contractAccount, currencyAccount, otcAccount, financialUserSum } = this.state
        const { userId, userName } = this.state;
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 用户资金 > 用户总资金明细
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
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList paymod={true} fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">//待定
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">帐户类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={accountTypeVal} style={{ width: SELECTWIDTH }} onChange={this.handleAccountChangeType} >
                                                {accountType}
                                            </Select>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <div className='table-total'>
                                        总资金：{toThousands(totalMoney, true)}， &nbsp;&nbsp;&nbsp;
                                        钱包账户：{toThousands(walletAccount, true)}， &nbsp;&nbsp;&nbsp;
                                        币币帐户：{toThousands(currencyAccount, true)}，&nbsp;&nbsp;&nbsp;
                                        OTC帐户：{toThousands(otcAccount, true)}，&nbsp;&nbsp;&nbsp;
                                        理财帐户：{toThousands(financialUserSum, true)}，&nbsp;&nbsp;&nbsp;
                                        合约账户：{toThousands(contractAccount, true)}，&nbsp;&nbsp;&nbsp;
                                        可用资金：{toThousands(money, true)}，&nbsp;&nbsp;&nbsp;
                                        冻结资金：{toThousands(freezeSecurity, true)}
                                    </div>
                                    <Table
                                        dataSource={tableList}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{ emptyText: '暂无数据' }}
                                        pagination={{
                                            hideOnSinglePage: true,
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            showSizeChanger: true,
                                            showQuickJumper: true,
                                            pageSizeOptions: PAGRSIZE_OPTIONS,
                                            defaultPageSize: PAGESIZE_50
                                        }}
                                    >
                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                                        <Column title='用户编号' dataIndex='userId' key='userId' />
                                        <Column title='总金额' dataIndex='totalAmount' key='totalAmount' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title='钱包账户' dataIndex='walletAccount' key='walletAccount' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title='币币帐户' dataIndex='currencyAccount' key='currencyAccount' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title='OTC帐户' dataIndex='otcAccount' key='otcAccount' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title='理财帐户' dataIndex='financialUserSum' key='financialUserSum' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title='合约帐户' dataIndex='contractAccount' key='contractAccount' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title='可用资金' dataIndex='availableBalance' key='availableBalance' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title='冻结资金' dataIndex='freezeSecurity' key='freezeSecurity' className="moneyGreen" render={(text) => {
                                            return <a href="javascript:void(0);" onClick={this.openFreezeDetails}>{toThousands(text, true)}</a>
                                        }} />
                                    </Table>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
                <Modal
                    visible={visible}
                    title="冻结资金"
                    width="1200px"
                    style={{ top: 60 }}
                    onCancel={this.handleCancel}
                    footer={[
                        <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" type="more" onClick={this.handleCancel}>确认</Button>,
                    ]}
                >
                    {modalHtml}
                </Modal>
            </div>
        )

    }
}