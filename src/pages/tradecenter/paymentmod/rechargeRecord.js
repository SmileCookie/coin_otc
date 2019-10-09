import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalRechargeRecord from './modal/modalRechargeRecord'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, TIMEFORMAT, SELECTWIDTH, URLS } from '../../../conf'
import { exportExcel } from 'xlsx-oc'
import { ckd, isArray, isObj, arrayTimeToStr } from "../../../utils";
import FundsTypeList from '../../common/select/fundsTypeList'
import { Input, Modal, DatePicker, Select, Button, Pagination, message } from 'antd'
const { COMMON_QUERYATTRUSDTE } = URLS
const { RangePicker } = DatePicker;
const Option = Select.Option;

export default class RechargeRecord extends React.Component {

    constructor(props) {
        super(props)
        this.defaultState = {
            fundstype: '0',
            toaddr: '',
            userid: '',
            username: '',
            status: '',
            rechargeMin: '',
            rechargeMax: '',
            beginTime: '',
            endTime: '',
            configStartTime: '',
            configEndTime: '',
            time: [],//充值时间
            configTime: [],//确认时间
            billTime: [],//记账时间
        }
        this.state = {
            showHide: true,
            isreLoad: false,
            height: 0,
            tableScroll: {
                tableId: 'RHRGERCD',
                x_panelId: 'RHRGERCDX',
                defaultHeight: 500,
            },
            ...this.defaultState,
        }
        this.handleChangeType = this.handleChangeType.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onChangeConfig = this.onChangeConfig.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.queryClickBtn = this.queryClickBtn.bind(this)
        this.handleChangegroup = this.handleChangegroup.bind(this)
        this.clickHide = this.clickHide.bind(this)
    }
    componentDidMount() {
        var height = document.querySelector(`#${this.state.tableScroll.x_panelId}`).offsetHeight
        this.setState({
            xheight: height
        })
    }
    //资金类型 select
    handleChangeType(val) {
        this.setState({
            fundstype: val
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
    //充值开始时间
    onChangeTime(date, dateString) {
        this.setState({
            beginTime: dateString[0],
            endTime: dateString[1],
            time: date
        })
    }
    //确认时间
    onChangeConfig(date, dateString) {
        this.setState({
            configStartTime: dateString[0],
            configEndTime: dateString[1],
            configTime: date
        })
    }

    //重置状态
    onResetState() {
        this.setState({
            ...this.defaultState
        })
    }

    //查询按钮
    queryClickBtn(val) {
        this.setState({
            isreLoad: val
        })
    }
    handleChangegroup(val) {
        this.setState({
            status: val
        })
    }
    //点击收起
    clickHide() {
        let { showHide, xheight, pageSize } = this.state;
        if (showHide && pageSize > 10) {
            this.setState({
                showHide: !showHide,
                height: xheight,
            })
        } else {
            this.setState({
                showHide: !showHide,
                height: 0
            })
        }
        // this.setState({
        //     showHide: !showHide,
        // })
    }
    onChangeCheckTime = (v, k) => {
        this.setState({ [k]: v })
    }
    limitTime30 = (days30, msg = '时间区间请选择30天以内的数据导出！') => {
        let limit = 30 * 24 * 60 * 60 * 1000;
        if (days30 - limit > 0) {
            message.warning(msg);
            return false
        }
        return true
    }
    exportDefaultExcel = () => {
        const { time, configTime, billTime } = this.state

        //如果时间为空或者区间大于30则返回false
        // let _lt = [{ k: '充值', t: time }, { k: '确认', t: configTime }, { k: '记账', t: billTime }]
        // for (let i = 0; i < _lt.length; i++) {
        //     const { k, t } = _lt[i];
        //     let day30 = Number(moment(t[1]).format('x')) - Number(moment(t[0]).format('x'))
        //     if (!t.length) {
        //         message.warning(`请选择${k}时间区间，区间为30天以内！！！`)
        //         return false
        //     }
        //     if (!this.limitTime30(day30, k + '时间区间请选择30天以内的数据导出！')) {
        //         return false
        //     }
        // }

        const params = Object.keys(this.defaultState).reduce((res, key) => {
            void 0 !== this.state[key] && !isArray(this.state[key]) && (res[key] = this.state[key])
            return res
        }, {
            billStartTime: arrayTimeToStr(billTime),
            billEndTime: arrayTimeToStr(billTime, 1)
        })
        // let _headers = [
        //     { k: 'index', v: '' },
        //     { k: 'fundstypeName', v: '资金类型' },
        //     { k: 'detailsid', v: '充值编号' },
        //     { k: 'userid', v: '用户编号' },
        //     { k: 'amount', v: '充值金额' },
        //     { k: 'showStatu', v: '状态' },
        //     { k: 'sendtimeExport', v: '充值时间' },
        //     { k: 'configtimeExport', v: '确认时间' },
        //     { k: 'billtimeExport', v: '记账时间' },
        //     { k: 'toaddr', v: '充值地址' },
        //     { k: 'confirmtimes', v: '备注' },
        // ]
        this.setState({ loading: true })
        axios.post(DOMAIN_VIP + '/recharge/export', qs.stringify(params)).then(res => {
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

                    // exportExcel(_headers, etData, '充值记录.xlsx')
                    let str = '序号,资金类型,充值编号,用户编号,充值金额,状态,充值时间,确认时间,记账时间,充值地址,备注\n';

                    for (let i = 0; i < etData.length; i++) {
                        etData[i].index = i+1
                        isObj(etData[i]) && Object.keys(etData[i]).forEach(v => {

                            if (etData[i][v] == null) {
                                etData[i][v] = ''
                            }
                        })
                        if (etData[i].confirmtimes != '') {
                            etData[i].confirmtimes = '确认次数：' + etData[i].confirmtimes + '次'
                        }

                        etData[etData.length - 1].index = '汇总'
                        str += etData[i].index + '\t' + ',' + etData[i].fundstypeName + '\t' + ',' + etData[i].detailsid + '\t' + ',' + etData[i].userid + '\t' + ',' + etData[i].amount + '\t' + ',' + etData[i].showStatu + '\t' + ',' +
                            etData[i].sendtimeExport + '\t' + ',' + etData[i].configtimeExport + '\t' + ',' + etData[i].billtimeExport + '\t' + ',' + etData[i].toaddr + '\t' + ',' + etData[i].confirmtimes + '\t' + '\n'
                    }

                   
                    let blob = new Blob([str], { type: "text/plain;charset=utf-8" });
                    //解决中文乱码问题
                    blob = new Blob([String.fromCharCode(0xFEFF), blob], { type: blob.type });
                    let object_url = window.URL.createObjectURL(blob);
                    let link = document.createElement("a");
                    link.href = object_url;
                    link.download = "充值记录.csv";
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
        const { showHide, fundstype, toaddr, userid, username, status, pageIndex, pageSize, tableList, pageTotal, time, configTime, rechargeMin, rechargeMax, billTime } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 充提管理 > 充值记录
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div id={this.state.tableScroll.x_panelId} className="x_panel">

                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList url={COMMON_QUERYATTRUSDTE} fundsType={fundstype} handleChange={this.handleChangeType}></FundsTypeList>
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
                                        <label className="col-sm-3 control-label">充值金额区间：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="rechargeMin" value={rechargeMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="rechargeMax" value={rechargeMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">处理状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.handleChangegroup}>
                                                <Option value="">请选择</Option>
                                                <Option value="0">确认中</Option>
                                                <Option value="2">成功</Option>
                                                <Option value="1">失败</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">充值地址：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="toaddr" value={toaddr} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">充值时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeTime}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">确认时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeConfig}
                                                value={configTime}
                                            />
                                        </div>
                                    </div>
                                </div>

                                {/**
                                *2019/08/06增加记账时间
                                */}
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
                                                onChange={(t) => this.onChangeCheckTime(t, 'billTime')}
                                                value={billTime}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={() => this.queryClickBtn(true)}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        <Button type="primary" loading={this.state.loading} onClick={this.exportDefaultExcel}>导出</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <ModalRechargeRecord {...this.state} queryClickBtn={this.queryClickBtn} />

                    </div>
                </div>
            </div>
        )
    }
}





















































