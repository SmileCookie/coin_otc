import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { message, Modal, Table, Button, DatePicker, Select } from 'antd'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, SELECTWIDTH, PAGRSIZE_OPTIONS20, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss, TIMEFORMAT, DAYFORMAT } from '../../../conf'
import MarketList from '../../common/select/marketrequests'
import { toThousands, ckd, isObj } from '../../../utils'
import { exportExcel } from 'xlsx-oc'
const { Column } = Table
const { RangePicker } = DatePicker;
const Option = Select.Option;

export default class BibiDealRecord extends React.Component {
    constructor(props) {
        super(props)
        this.defaultMarket = 'BTC_USDT'
        this.state = {
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            market: this.defaultMarket,
            userId: '',
            tableSource: [],
            time: [],
            startTime: '',
            endTime: '',
            days30: 0,
            types: '',
            userBuyId: '',
            userSellId: '',
            sumNumbers: '',
            sumTotalPrice: '',
            loading: false,
            isInquery: false,
            timerStarted: false
        }
    }
    componentDidMount() {
        let beforeTime = moment().subtract(6, 'days').format(DAYFORMAT)
        let nowTime = moment().format(DAYFORMAT)
        let defaultStartTime = moment(beforeTime + ' 00:00:00').format('x')
        let defaultEndTime = moment(nowTime + ' 23:59:59').format('x')
        let defaultTime = [moment(beforeTime + '00:00:00', TIMEFORMAT), moment(nowTime + '23:59:59', TIMEFORMAT)]
        let defaultDays30 = defaultEndTime - defaultStartTime
        this.setState({
            startTime: defaultStartTime,
            endTime: defaultEndTime,
            time: defaultTime,
            defaultStartTime,
            defaultEndTime,
            defaultTime,
            defaultDays30,
        }, () => {
            // this.createTimer()
        })

    }
    componentWillUnmount() {
        this.clearTimer()
    }
    createTimer = (time = 1000) => {
        this.timer = setInterval(() => {
            this.requestTable()
        }, time)
    }
    clearTimer = () => {
        if (this.timer) {
            console.log('clear')
            clearInterval(this.timer)
        }
    }
    isTrue = () => {
        const { userBuyId, userSellId, types, startTime, endTime, defaultStartTime, defaultEndTime } = this.state
        return (userBuyId || userSellId || types || startTime != defaultStartTime || endTime != defaultEndTime)


    }
    controlTimer = () => {
        const { days30, startTime, endTime } = this.state
        if (this.state.timerStarted) {
            this.clearTimer()
        } else {
            this.clearTimer()
            if (!this.limitTime30(days30, '请选择查询小于30天的时间段,再打开定时器！')) return
            if (!startTime) {
                message.warning('请选择查询小于30天的时间段,再打开定时器！')
                return false
            }
            this.createTimer()
        }
        this.setState({
            timerStarted: !this.state.timerStarted
        })

    }
    controlTimer = () => {
        const { days30, startTime, endTime } = this.state
        if (this.state.timerStarted) {
            this.clearTimer()
        } else {
            this.clearTimer()
            if (!this.limitTime30(days30, '请选择查询小于30天的时间段,再打开定时器！')) return
            if (!startTime) {
                message.warning('请选择查询小于30天的时间段,再打开定时器！')
                return false
            }
            this.createTimer()
        }
        this.setState({
            timerStarted: !this.state.timerStarted
        })

    }
    clickHide = () => {
        this.setState({
            showHide: !this.state.showHide
        })
    }
    inquiry = () => {
        let dtqy = false
        if (this.isTrue()) {
            dtqy = true
        }
        if (this.state.timerStarted) {
            message.warning('请关闭定时器，再手动查询')
            return false
        }

        this.setState({
            pageIndex: PAGEINDEX,
            isInquery: dtqy
        }, () => this.requestTable())
    }
    resetState = () => {
        const { defaultEndTime, defaultStartTime, defaultTime, defaultDays30 } = this.state
        this.setState({
            market: this.defaultMarket,
            userId: '',
            types: '',
            userBuyId: '',
            userSellId: '',
            startTime: defaultStartTime,
            endTime: defaultEndTime,
            time: defaultTime,
            days30: defaultDays30
        })
    }
    onChangePageNum = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    handleChange = (value) => {
        this.setState({ market: value })
    }

    //输入时 input 设置到 state
    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    time_onChange = (date, dateString) => {
        // console.log(date,dateString,+moment(dateString[0]).format('x'))
        let days30 = Number(moment(dateString[1]).format('x')) - Number(moment(dateString[0]).format('x'));
        if (this.state.timerStarted && !this.limitTime30(days30, '请关闭定时器，再选择查询大于30天的数据！')) return
        let startTime = dateString[0] && moment(dateString[0]).format('x')
        let endTime = dateString[1] && moment(dateString[1]).format('x')
        // console.log(startTime, endTime)
        this.setState({
            startTime: startTime,
            endTime,
            time: date,
            days30
        })

    }
    limitTime30 = (days30, msg = '时间区间请选择30天以内！') => {
        let limit = 30 * 24 * 60 * 60 * 1000;
        if (days30 - limit > 0) {
            message.warning(msg);
            return false
        }
        return true
    }
    requestTable = (currIndex, currSize) => {
        const { market, userId, startTime, endTime, days30, userBuyId, userSellId, types, pageIndex, pageSize } = this.state
        if (!this.limitTime30(days30)) return false
        // console.log('定时器')
        this.clearTimer()
        // this.setState({ loading: true })
        axios.post(DOMAIN_VIP + '/dealRecord/getC2cTransRecord', qs.stringify({
            market: market.toUpperCase(), userBuyId, userSellId, types, beginTimeStamp: startTime, endTimeStamp: Number(endTime) + 999,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.transrecordList.list || [];
                for (let i = 0; i < tableSource.length; i++) {
                    let marketarr = []
                    tableSource[i].index = (result.data.transrecordList.currPage - 1) * result.data.transrecordList.pageSize + i + 1;
                    // tableSource[i].index = i + 1
                    tableSource[i].key = tableSource[i].transrecordid
                    // let types = tableSource[i].types > 0 ? 1 : 0
                    tableSource[i].unitprice = toThousands(tableSource[i].unitprice)
                    // tableSource[i].feesbuy = toThousands(tableSource[i].feesbuy) 
                    // tableSource[i].feessell = toThousands(tableSource[i].feessell) 
                    tableSource[i].fee = tableSource[i].types > 0 ? toThousands(tableSource[i].feesbuy) : toThousands(tableSource[i].feessell)

                    tableSource[i].numbers = toThousands(tableSource[i].numbers)
                    tableSource[i].totalprice = toThousands(tableSource[i].totalprice)
                }

                this.setState({
                    tableSource: tableSource,
                    pageTotal: result.data.transrecordList.totalCount,
                    sumNumbers: toThousands(result.data.sumNumbers) || '',
                    sumTotalPrice: toThousands(result.data.sumTotalPrice) || '',
                    //loading: false
                }, () => {
                    // if(this.state.isInquery && this.isTrue()){
                    //     this.clearTimer()
                    // }else{
                    this.clearTimer()
                    if (this.state.timerStarted) {
                        this.createTimer()
                    }
                    // }
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    selectTypes = types => {
        this.setState({ types })
    }
    setTypes = types => {
        switch (Number(types)) {
            case -1:
                return {
                    str: '取消',
                    classname: '',
                    types: 1
                }
            case 0:
                return {
                    str: '卖',
                    classname: 'font_green',
                    types
                }
            case 1:
                return {
                    str: '买',
                    classname: 'font_red',
                    types
                }
            default:
                break
        }
    }
    exportDefaultExcel = () => {
        const { market, userId, startTime, endTime, days30, userBuyId, userSellId, types, pageIndex, pageSize } = this.state
        // let _headers = [
        //     { k: 'index', v: '' },
        // { k: 'typesName', v: '方向' },
        // { k: 'marketShow', v: '交易市场' },
        // { k: 'useridbuy', v: '买方编号' },
        // { k: 'useridsell', v: '卖方编号' },
        // { k: 'unitprice', v: '成交价' },
        // { k: 'numbers', v: '成交量' },
        // { k: 'totalprice', v: '成交金额' },
        // { k: 'feesbuy', v: '手续费' },
        // { k: 'timesShow', v: '成交日期' },
        // ]
        this.setState({ loading: true })
        axios.post(DOMAIN_VIP + '/dealRecord/exportC2cTransRecord', qs.stringify({
            market: market.toUpperCase(), userBuyId, userSellId, types, beginTimeStamp: startTime, endTimeStamp: Number(endTime) + 999,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let etData = ckd(result.data);
                if (etData.length) {
                    // exportExcel(_headers, etData, '币币成交记录.xlsx')
                    let str = '序号,方向,交易市场,买方编号,卖方编号,成交价,成交量,成交金额,手续费,成交日期\n';
                    for (let i = 0; i < etData.length; i++) {

                        etData[i].index = i+1
                        isObj(etData[i]) && Object.keys(etData[i]).forEach(v => {

                            if (etData[i][v] == null) {
                                etData[i][v] = ''
                            }
                        })
                        const type= etData[i].typesName=='买'? etData[i].feesbuy :etData[i].feessell
                        etData[etData.length - 1].index = '汇总'
                        str += etData[i].index + '\t' + ',' + etData[i].typesName + ',' + etData[i].marketShow + ',' + etData[i].useridbuy + ',' + etData[i].useridsell + ',' + etData[i].unitprice + ',' +
                            etData[i].numbers + ',' + etData[i].totalprice + ',' + type+ '\t' + ',' + etData[i].timesShow + '\t' + '\n'

                    }
                    
                    let blob = new Blob([str], { type: "text/plain;charset=utf-8" });
                    //解决中文乱码问题
                    blob = new Blob([String.fromCharCode(0xFEFF), blob], { type: blob.type });
                    let object_url = window.URL.createObjectURL(blob);
                    let link = document.createElement("a");
                    link.href = object_url;
                    link.download = "币币成交记录.csv";
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
        const { showHide, tableSource, pageIndex, pageSize, pageTotal, market, userId, time, types, userBuyId, userSellId, sumNumbers, sumTotalPrice, loading } = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置：数据中心 > 盘口管理 > 币币成交记录
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <MarketList market={market} underLine={true} col='3' handleChange={this.handleChange}></MarketList>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">买方编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userBuyId" value={userBuyId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">卖方编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userSellId" value={userSellId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">方向：</label>
                                        <div className="col-sm-9">
                                            <Select value={types}
                                                style={{ width: SELECTWIDTH }}
                                                onChange={this.selectTypes}>
                                                <Option value=''>全部</Option>
                                                <Option value='1'>买方用户</Option>
                                                <Option value='0'>卖方用户</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                onChange={this.time_onChange}
                                                value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                                    <div >
                                        <div className="form-group right">
                                            <Button type="primary" onClick={this.inquiry}>查询</Button>
                                            <Button type="primary" onClick={this.resetState}>重置</Button>
                                            <Button type="primary" loading={this.state.loading} onClick={this.exportDefaultExcel}>导出</Button>
                                            <Button type="primary" onClick={this.controlTimer}>{this.state.timerStarted ? '关闭定时器' : '启动定时器'}</Button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    成交数量：{sumNumbers} &nbsp;&nbsp;&nbsp;
                                                    成交总金额：{sumTotalPrice} &nbsp;&nbsp;&nbsp;
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table
                                        dataSource={tableSource}
                                        bordered
                                        // loading={this.state.loading}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.onChangePageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            pageSizeOptions: PAGRSIZE_OPTIONS20,
                                            defaultPageSize: PAGESIZE,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }}
                                        locale={{ emptyText: '暂无数据' }}
                                    >
                                        <Column title='序号' dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )} />
                                        <Column title='方向' dataIndex='types' key='' render={text => {
                                            return <div className={this.setTypes(text).classname}>{this.setTypes(text).str}</div>
                                        }} />
                                        <Column title='交易市场' dataIndex='marketShow' key='' />
                                        <Column title='买方编号' dataIndex='useridbuy' key='' />
                                        <Column title='卖方编号' dataIndex='useridsell' key='' />
                                        <Column className='moneyGreen' title='成交价' dataIndex='unitprice' key='' />
                                        <Column title='成交量' dataIndex='numbers' key='' />
                                        <Column className='moneyGreen' title='成交金额' dataIndex='totalprice' key='' />
                                        <Column className='moneyGreen' title='手续费' dataIndex='fee' key='cc' />
                                        <Column title='成交日期' dataIndex='timesShow' key='' />
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

