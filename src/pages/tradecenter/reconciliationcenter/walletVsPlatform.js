/**区块钱包对账vs交易平台对账 */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, DOMAIN_VIP, SELECTWIDTH, DAYFORMAT, PAGRSIZE_OPTIONS20, PAGESIZE, TIMEFORMAT } from '../../../conf'
import { toThousands } from '../../../utils'
import { Button, DatePicker, Select, Table, message } from 'antd'
import FundsTypeList from '../../common/select/fundsTypeList'
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;

export default class WalletVsPlatform extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            fundsType: "0",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: '50',
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
            tableDataInterface: DOMAIN_VIP + '/walletReconVS/query',
            defaultBegin: '',
            defaultEnd: '',
            defaultTime: null
        }
        this.clickHide = this.clickHide.bind(this);
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.requestTable = this.requestTable.bind(this);
        this.handleChangeType = this.handleChangeType.bind(this);
        this.onResetState = this.onResetState.bind(this);
        this.changPageNum = this.changPageNum.bind(this);
        this.onShowSizeChange = this.onShowSizeChange.bind(this);
    }
    componentDidMount() {

        let time = moment().subtract(1, 'days').format(DAYFORMAT)
        let t = moment().subtract(1, 'days')
        // console.log(t)
        // console.log(time)
        this.setState({
            begin: time + ' 00:00:00',
            end: time + ' 23:59:59',
            // time:[moment(time+'00:00:00', TIMEFORMAT), moment(time+'23:59:59', TIMEFORMAT)],
            time: t,
            defaultBegin: time + ' 00:00:00',
            defaultEnd: time + ' 23:59:59',
            defaultTime: t
        }, () => this.requestTable())
    }
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => this.requestTable())
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        console.log(dateString)
        this.setState({
            begin: dateString ? dateString + " 00:00:00" : '',
            end: dateString ? dateString + " 23:59:59" : '',
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
    onResetState() {
        const { defaultBegin, defaultEnd, defaultTime } = this.state
        this.setState({
            fundsType: "0",
            begin: defaultBegin,
            end: defaultEnd,
            time: defaultTime,
        })
    }
    requestTable(currIndex, currSize) {
        const { fundsType, begin, end, pageIndex, pageSize, tableDataInterface } = this.state
        axios.post(tableDataInterface, qs.stringify({
            begin,
            end,
            fundsType,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                // console.log(result)
                Big.RM = 0;
                let demandData = {
                    "balanceResp": {
                        fundsType: null,//资金类型
                        // fundsTypeName :null,//资金类型名称
                        userRecharge: null,//钱包用户充值
                        userWithdraw: null,//钱包用户提现
                        hotRechargeHappenedAmount: null, //热充余额
                        hotToColdFee: null,//网络费1
                        hotToCold: null,//热充到冷
                        coldHappenedAmount: null,//冷钱包余额
                        coldToOtherHappenedAmount: null,//其他1
                        coldToOtherFee: null,//网络费2
                        coldToHotFee: null,//网络费3
                        coldToHot: null,//冷到热提
                        hotWithdrawHappenedAmount: null,//热提余额
                        withdrawFee: null,//网络费4
                        otherToColdHappenedAmount: null,//其他3
                        otherToHotHappenedAmount: null,//其他4
                        hotWithdrawToOtherHappenedAmount: null, //其他2
                        hotWithdrawToOtherFee: null,//网络费5
                        hotWithdrawToColdHappenedAmount: null,//热提到冷余额
                        hotWithdrawToColdFee: null, //网络费6
                        coldToHotContract: null, //冷到热提合约消耗
                        userWithdrawContract: null,//提现合约消耗
                        coldToOtherContract: null,//冷到其他合约消耗
                        hotWithdrawToOtherContract: null,//热提到其他合约消耗
                        blockHeight: null, //区块高度
                        checkTime: null,//对账日期
                        coldToHotFeeM: null, //冷到热提
                        coldToOtherFeeM: null,//冷到其他
                        contractB: null,              //B 消耗金额
                        contractX: "0",              //X 消耗金额
                        feeAmount: 0,           //手续费提现A
                        downloadAmount: 0,           //运营提现B
                        rechargeAmount: 0,             //运营充值  C 
                        ortherrechge:0,     ///钱包其他充值
                        ortherdownload:0,       //钱包其他提现

                    },
                    "generalledger": {
                        "reportdate": null,  //日期
                        recharge: null,//改--用户充值
                        withdraw: null,//改--用户提现  
                        balanceVs: 0,  // 交易平台金额 JA  
                        fundstypeName: null,//资金类型名称      
                        internaladjustmentpositive: 0,   //内部调账正
                        internaladjustmentnegative: 0,       //内部调账负
                        externaladjustmentpositive: 0,       //外部调账正
                        externaladjustmentnegative: 0        //外部调账负       
                    }
                }

                let tableList = [];
                if (result.data.length > 0) {
                    result.data.map((item, index) => {

                        let json = new Object();
                        for (let key in demandData.balanceResp) {
                            if (key == "hotToColdFee") {
                                //网络费=网络费1+网络费2+网络费3+网络费4+网络费5
                                json.coldToHotExtractFee = new Big(item.balanceResp.hotToColdFee)
                                    .plus(item.balanceResp.coldToOtherFee)
                                    .plus(item.balanceResp.coldToHotFee)
                                    .plus(item.balanceResp.withdrawFee)
                                    .plus(item.balanceResp.hotWithdrawToOtherFee)
                                    .plus(item.balanceResp.hotWithdrawToColdFee);
                                continue
                            }

                            if (key == "coldToOtherFee" || key == "coldToHotFee" || key == "hotWithdrawToOtherFee" || key == "hotWithdrawToColdFee") {
                                continue
                            }
                            json[key] = item.balanceResp[key]

                        }
                        for (let key in demandData.generalledger) {
                            if (key == "reportdate") {
                                json.reportdate = moment(item.generalledger[key]).format('YYYY-MM-DD');
                                continue
                            }
                            json[key] = item.generalledger[key]
                        }


                        //其他发生额 = C-A-B
                        json.allOtherMoney = new Big(item.balanceResp.rechargeAmount)
                            .minus(item.balanceResp.feeAmount)
                            .minus(item.balanceResp.downloadAmount)
                        // .minus(item.balanceResp.otherToHotHappenedAmount)

                        // json.allOtherMoney = new Big(item.balanceResp.coldToOtherHappenedAmount)
                        // .plus(item.balanceResp.hotWithdrawToOtherHappenedAmount)
                        // .minus(item.balanceResp.otherToColdHappenedAmount)
                        // // .minus(item.balanceResp.otherToHotHappenedAmount)
                        // console.log(json.allOtherMoney)
                        // console.log(item.generalledger.balanceVs)

                        //钱包金额(QA) = 钱包用户充值 - 钱包用户提现 + 钱包其他充值 - 钱包其他提现
                        json.userWalletMoney = new Big(item.balanceResp.userRecharge)
                            .minus(item.balanceResp.userWithdraw)
                            .plus(item.balanceResp.ortherrechge)
                            .minus(item.balanceResp.ortherdownload)
                            // .plus(json.allOtherMoney)

                        //交易平台金额JA + 其他发生额(C-A-B)
                        json.balanceVsAll = new Big(item.generalledger.balanceVs)
                            .plus(json.allOtherMoney)

                        //对账差额 = (交易平台金额)JA - (钱包余额)QA 
                        json.reconciliationBalance = new Big(json.balanceVsAll)
                            .minus(json.userWalletMoney)

                            //交易平台差额（不调账）P1
                            // debugger
                        json.platFormRecharge = new Big(item.generalledger.recharge)
                        .minus(item.generalledger.withdraw)
                        .plus(item.generalledger.withdrawfee)

                        //钱包差额Q1
                        json.userWalletRecharge = new Big(item.balanceResp.userRecharge)
                        .minus(item.balanceResp.userWithdraw)
                        //对账差额P2-Q1
                        json.p2_q1 = new Big(item.generalledger.balanceVs)
                        .minus(json.userWalletRecharge)


                        //平台VS区块钱包对账 = 用户充值-用户提现-运营充值-热充钱包余额-冷热钱包流转支付网络费-用户提现实际支付网络费
                        // json.platformBalanceReconciliation = new Big(json.transactionPlatformAmount?json.transactionPlatformAmount:0)
                        //     .minus(new Big(json.hotWithdrawHappenedAmount?json.hotWithdrawHappenedAmount:0)
                        //         .plus(json.coldHappenedAmount?json.coldHappenedAmount:0)
                        //         .plus(json.hotRechargeHappenedAmount?json.hotRechargeHappenedAmount:0)
                        //         .plus(json.coldToHotExtractFee?json.coldToHotExtractFee:0)
                        //         .plus(json.withdrawFee?json.withdrawFee:0));
                        //     //热充到冷网络费+冷到其他网络费+冷到热提网络费+热提到用户网络费+热提到冷网络费+热提到其他网络费
                        // json.coldHotBalanceFee = new Big(json.hotToColdFee?json.hotToColdFee:0)
                        //         .plus(json.coldToOtherFee?json.coldToOtherFee:0)
                        //         .plus(json.coldToHotFee?json.coldToHotFee:0)
                        //         .plus(json.withdrawFee?json.withdrawFee:0)
                        //         .plus(json.hotWithdrawToColdFee?json.hotWithdrawToColdFee:0)
                        //         .plus(json.hotWithdrawToOtherFee?json.hotWithdrawToOtherFee:0);
                        //索引
                        json.index = index + 1;
                        tableList.push(json);
                    })
                }
                this.setState({
                    tableList,
                    pageTotal: result.pageTotal
                })
            } else {
                message.warn(result.msg)
            }
        })
    }
    render() {
        Big.RM = 0;
        const { showHide, accountType, fundsType, tableList, pageSize, time, pageIndex, pageTotal } = this.state
        // console.log(tableList)
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 钱包对账 > 区块钱包对账vs交易平台对账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList title='资金类型' fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对账日期：</label>
                                        <div className="col-sm-8">
                                            <DatePicker
                                                showTime={{
                                                    defaultValue: [moment('2015/01/01', DAYFORMAT), moment('2015/01/01', DAYFORMAT)]
                                                }}
                                                format={DAYFORMAT}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>

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

                                    <Table
                                        dataSource={tableList}
                                        bordered={true}
                                        rowKey="index"
                                        scroll={{ x: 3000 }}
                                        locale={{ emptyText: '暂无数据' }}
                                        pagination={{
                                            size: "small",
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            showSizeChanger: true,
                                            showQuickJumper: true,
                                            pageSizeOptions: PAGRSIZE_OPTIONS20,
                                            defaultPageSize: PAGESIZE
                                        }}>

                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='资金类型' dataIndex='fundstypeName' key='fundstypeName' />
                                        {/* //(充值-提现+手续费+内正-内负+外正-外负) */}
                                        <Column title={<React.Fragment>对账差额<br />(P2-Q1)</React.Fragment>} dataIndex='p2_q1' key='p2_q1' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>交易平台差额P2<br /></React.Fragment>} dataIndex='balanceVs' key='balanceVs' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>交易平台差额P1<br />(充值-提现+手续费)</React.Fragment>} dataIndex='platFormRecharge' key='platFormRecharge' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>钱包差额Q1<br />(充值-提现)</React.Fragment>} dataIndex='userWalletRecharge' key='userWalletRecharge' className="moneyGreen" render={(text) => toThousands(text, true)} />

                                        {/* <Column title={<React.Fragment>对账差额<br />(JA-QA)</React.Fragment>} dataIndex='reconciliationBalance' key='reconciliationBalance' className="moneyGreen" render={(text) => toThousands(text, true)} /> */}
                                        <Column title={<React.Fragment>交易平台金额JA<br />(充值-提现+C-A-B)</React.Fragment>} dataIndex='balanceVsAll' key='balanceVsAll' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>运营提现<br />(A)</React.Fragment>} dataIndex='downloadAmount' key='downloadAmount' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>手续费帐户提现<br />(B)</React.Fragment>} dataIndex='feeAmount' key='feeAmount' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>运营充值<br />(C)</React.Fragment>} dataIndex='rechargeAmount' key='rechargeAmount' className="moneyGreen" render={(text) => toThousands(text, true)} />

                                        <Column title={<React.Fragment>钱包金额<br />(QA=1+3-2-4)</React.Fragment>} dataIndex='userWalletMoney' key='userWalletMoney' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>钱包用户充值<br />(1)</React.Fragment>} dataIndex='userRecharge' key='userRecharge' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>钱包用户提现<br />(2)</React.Fragment>} dataIndex='userWithdraw' key='userWithdraw' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        {/* <Column title={<React.Fragment>热充余额<br />(3)</React.Fragment>} dataIndex='hotRechargeHappenedAmount' key='hotRechargeHappenedAmount' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>冷余额<br />(4)</React.Fragment>} dataIndex='coldHappenedAmount' key='coldHappenedAmount' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>热提余额<br />(5)</React.Fragment>} dataIndex='hotWithdrawHappenedAmount' key='hotWithdrawHappenedAmount' className="moneyGreen" render={(text) => toThousands(text, true)} /> */}

                                        <Column title={<React.Fragment>钱包其他充值<br />(3)</React.Fragment>} dataIndex='ortherrechge' key='ortherrechge' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>钱包其他提现<br />(4)</React.Fragment>} dataIndex='ortherdownload' key='ortherdownload' className="moneyGreen" render={(text) => toThousands(text, true)} />

                                        {/* <Column title={<React.Fragment>网络费<br />(5)</React.Fragment>} dataIndex='coldToHotExtractFee' key='coldToHotExtractFee' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>其他发生额<br />(7=C-A-B)</React.Fragment>} dataIndex='allOtherMoney' key='allOtherMoney' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>X|B消耗金额<br />(不参与对账)</React.Fragment>} dataIndex='X|B' key='X|B' className="moneyGreen" render={(text, record) => {
                                            return <span>{toThousands(record.contractX, true)} | {toThousands(record.contractB, true)}</span>
                                        }} /> */}
                                        <Column title={<React.Fragment>内部调账正</React.Fragment>} dataIndex='internaladjustmentpositive' key='internaladjustmentpositive' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>内部调账负</React.Fragment>} dataIndex='internaladjustmentnegative' key='internaladjustmentnegative' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>外部调账正</React.Fragment>} dataIndex='externaladjustmentpositive' key='externaladjustmentpositive' className="moneyGreen" render={(text) => toThousands(text, true)} />
                                        <Column title={<React.Fragment>外部调账负</React.Fragment>} dataIndex='externaladjustmentnegative' key='externaladjustmentnegative' className="moneyGreen" render={(text) => toThousands(text, true)} />



                                        {/* <Column title={<React.Fragment>热提钱包余额<br />(J)</React.Fragment>} dataIndex='hotWithdrawHappenedAmount' key='hotWithdrawHappenedAmount' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>冷钱包余额<br />(K)</React.Fragment>} dataIndex='coldHappenedAmount' key='coldHappenedAmount' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>热冲钱包余额<br />(L)</React.Fragment>} dataIndex='hotRechargeHappenedAmount' key='hotRechargeHappenedAmount' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>冷热钱包支付网络费<br />(M)</React.Fragment>} dataIndex='coldHotBalanceFee' key='coldHotBalanceFee' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='对账日期' dataIndex='reportdate' key='reportdate' />
                                        <Column title="状态" dataIndex="state" key="sss" render={(state) => state == 0 ? '正常' : '异常'} /> */}

                                    </Table>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        )

    }
}