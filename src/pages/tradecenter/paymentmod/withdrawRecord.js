import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalWithdrawRecord from './modal/modalWithdrawRecord'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, TIMEFORMAT, SELECTWIDTH, URLS } from '../../../conf'
import FundsTypeList from '../../common/select/fundsTypeList'
import { Input, Modal, DatePicker, Select, Button, Pagination } from 'antd'
import { tableScroll, ckd, isObj } from '../../../utils'
const { COMMON_QUERYATTRUSDTE } = URLS
const { RangePicker } = DatePicker;
const Option = Select.Option;

export default class WithdrawRecord extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            userid: '',
            username: '',
            type: '0',
            customerOperation: '',
            endTime: '',
            startTime: '',
            confirmEndDate: '',
            confirmStartDate: '',
            moneyMax: '',
            moneyMin: '',
            commandid: '',
            fundstype: '0',
            configTime: [],
            submitTime: [],
            billTime: [],//记账时间
            blockTime: [],//到账时间
            isreLoad: false,
            tableScroll: {
                tableId: 'WHDWRCOD',
                x_panelId: 'WHDWRCODX',
                defaultHeight: 500,
                height: 0,
            }
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleCommandid = this.handleCommandid.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onChangeSubTime = this.onChangeSubTime.bind(this)
        this.handleOperation = this.handleOperation.bind(this)
        this.handleType = this.handleType.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.queryClickBtn = this.queryClickBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
    }
    componentDidMount() {
        var height = document.querySelector(`#${this.state.tableScroll.x_panelId}`).offsetHeight
        this.setState({
            xheight: height
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
    //资金类型
    handleChangeType(val) {
        this.setState({
            fundstype: val
        })
    }

    //打币类型
    handleCommandid(value) {
        this.setState({
            commandid: value
        })
    }
    //资金状态
    handleOperation(val) {
        this.setState({
            customerOperation: val
        })
    }
    //打币状态
    handleType(val) {
        this.setState({
            type: val
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
    //重置状态
    onResetState() {
        this.setState({
            userid: '',
            username: '',
            type: '0',
            customerOperation: '',
            endTime: '',
            startTime: '',
            confirmEndDate: '',
            confirmStartDate: '',
            moneyMax: '',
            moneyMin: '',
            commandid: '',
            fundstype: '0',
            configTime: [],
            submitTime: [],
            billTime: [],//记账时间
            blockTime: [],//到账时间
        })
    }
    //查询按钮
    queryClickBtn(val) {
        this.setState({
            isreLoad: val
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide,
        })
    }
    onChangeCheckTime = (v, k) => {
        this.setState({ [k]: v })
    }


    exportDefaultExcel = () => {
        const { fundstype, customerOperation, userid, username, commandid, type, moneyMin, moneyMax, configTime, submitTime, blockTime, billTime } = this.state
        this.setState({ loading: true })
        const params = {
            fundstype, customerOperation, userid, username, commandid, type, moneyMin, moneyMax,
            confirmStartDate: configTime.length ? moment(configTime[0]).format(TIMEFORMAT_ss) : '',
            confirmEndDate: configTime.length ? moment(configTime[1]).format(TIMEFORMAT_ss) : '',
            endTime: submitTime.length ? moment(submitTime[0]).format(TIMEFORMAT_ss) : '',
            startTime: submitTime.length ? moment(submitTime[1]).format(TIMEFORMAT_ss) : '',
            blockStartTime: blockTime.length ? moment(blockTime[0]).format(TIMEFORMAT_ss) : '',
            blockEndTime: blockTime.length ? moment(blockTime[1]).format(TIMEFORMAT_ss) : '',
            billStartTime: billTime.length ? moment(billTime[0]).format(TIMEFORMAT_ss) : '',
            billEndTime: billTime.length ? moment(billTime[1]).format(TIMEFORMAT_ss) : '',
        }
        axios.post(DOMAIN_VIP + '/withdrawRecord/export', qs.stringify(params)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let etData = ckd(result.data);
                if (etData.length) {
                    // let sumObj = etData[etData.length - 1]
                    // //有汇总的处理一下
                    // isObj(sumObj) && Object.keys(sumObj).forEach(v => {
                    //     sumObj[v] === null && (sumObj[v] = '')
                    // })
                    // sumObj.index = '汇总'

                    // exportExcel(_headers, etData, '成交记录.xlsx')
                    let str = '序号,资金类型,提现编号,用户编号,提现金额,实际金额,申请时间,审核时间,到账时间,记账时间,提现地址,资金状态,提现手续费,打币类型,打币状态\n';
                    for (let i = 0; i < etData.length; i++) {
                        etData[i].index = i+1
                        isObj(etData[i]) && Object.keys(etData[i]).forEach(v => {

                            if (etData[i][v] == null) {
                                etData[i][v] = ''
                            }
                        })
                        etData[etData.length - 1].index = '汇总'
                        str += etData[i].index + '\t' + ',' + etData[i].fundstypename + '\t' + ',' + etData[i].downloadid + '\t' + ',' + etData[i].userid + '\t' + ',' + etData[i].amount + '\t' + ',' + etData[i].afterAmount + '\t' + ',' +
                            etData[i].submittimeExport + '\t' + ',' + etData[i].managetimeExport + '\t' + ',' + etData[i].configtimeExport + '\t' + ',' + etData[i].billTimeExport + '\t' + ','
                            + etData[i].toaddress + '\t' + ',' + etData[i].customerOperationName + '\t' + ',' + etData[i].fees + '\t' + ',' + etData[i].auditorTypeName + '\t' + ',' + etData[i].typeName + '\n'
                    }


                    let blob = new Blob([str], { type: "text/plain;charset=utf-8" });
                    //解决中文乱码问题
                    blob = new Blob([String.fromCharCode(0xFEFF), blob], { type: blob.type });
                    let object_url = window.URL.createObjectURL(blob);
                    let link = document.createElement("a");
                    link.href = object_url;
                    link.download = "提现查询.csv";
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);


                    this.setState({ loading: false })
                } else {
                    message.warning('没有数据，无法导出！')
                }

            } else {
                message.error(result.msg)
            }

        })
    }

    render() {
        const { showHide, userid, username, type, customerOperation, endTime, startTime, configTime, submitTime, confirmEndDate, confirmStartDate, moneyMax, moneyMin, commandid, fundstype, pageIndex, pageSize, pageTotal, modalHtml, tableList, billTime, blockTime } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 充提管理 > 提现查询
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList url={COMMON_QUERYATTRUSDTE} fundsType={fundstype} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">资金状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={customerOperation} style={{ width: SELECTWIDTH }} onChange={this.handleOperation}>
                                                <Option value="">请选择</Option>
                                                <Option value="03">正常</Option>
                                                <Option value="02">异常</Option>
                                            </Select>
                                        </div>
                                    </div>
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
                                            <Select value={commandid} style={{ width: SELECTWIDTH }} onChange={this.handleCommandid}>
                                                <Option value="">请选择</Option>
                                                <Option value="0">人工</Option>
                                                <Option value="1">自动</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">打币状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={type} style={{ width: SELECTWIDTH }} onChange={this.handleType}>
                                                <Option value="0">请选择</Option>
                                                <Option value="1">待确认</Option>
                                                <Option value="2">已确认</Option>
                                                <Option value="3">已成功</Option>
                                                <Option value="4">失败</Option>
                                                <Option value="5">取消</Option>
                                                <Option value="6">进行中</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">提现金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMin" value={moneyMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMax" value={moneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">审核时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeTime}
                                                value={configTime}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">申请时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeSubTime}
                                                value={submitTime}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">到账时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={v => this.onChangeCheckTime(v, 'blockTime')}
                                                value={blockTime}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">记账时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={v => this.onChangeCheckTime(v, 'billTime')}
                                                value={billTime}
                                            />
                                        </div>
                                    </div>
                                </div>


                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={() => this.queryClickBtn(true)}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        <Button type="primary" loading={this.state.loading} onClick={this.exportDefaultExcel}>导出</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <ModalWithdrawRecord {...this.state} queryClickBtn={this.queryClickBtn} />

                    </div>
                </div>
            </div>
        )
    }
}









































