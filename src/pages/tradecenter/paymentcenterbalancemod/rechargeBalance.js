import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, TIMEFORMAT_ss, PAGRSIZE_OPTIONS20, URLS } from '../../../conf'
import { Button, DatePicker, Tabs, Pagination, Select } from 'antd'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
import FundsTypeList from '../../common/select/fundsTypeList'
import { toThousands, tableScroll } from '../../../utils'
const { COMMON_QUERYATTRUSDTE } = URLS
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class RechargeBalance extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            bgDetailsId: "",
            fundType: "2",
            pmStatus: "0",
            balanceFlag: "",
            pmConfigTimeOfStart: "",
            pmConfigTimeOfEnd: "",
            time: [],
            pmBlockHeightOfStart: "",
            pmBlockHeightOfEnd: "",
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: 0,
            tableList: [],
            height: 0,
            tableScroll: {
                tableId: 'RHGEBLE',
                x_panelId: 'RHGEBLEX',
                defaultHeight: 500,
                height: 0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.handleStatusChange = this.handleStatusChange.bind(this)
        this.handleFlagChange = this.handleFlagChange.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.getHeight = this.getHeight.bind(this)

    }
    componentDidMount() {
        this.requestTable()
        tableScroll(`#${this.state.tableScroll.tableId}`, 'add', `#${this.state.tableScroll.x_panelId}`, this.getHeight)
    }
    componentWillUnmount() {
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight) {
        this.setState({
            xheight
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
    //查询 按钮
    inquireBtn() {
        this.setState({
            pageIndex: PAGEINDEX,
            current: PAGEINDEX
        }, () => this.requestTable())
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
    handleFlagChange(value) {
        this.setState({
            balanceFlag: value
        })
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            pmConfigTimeOfStart: dateString[0],
            pmConfigTimeOfEnd: dateString[1],
            time: date
        })

    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page,
            current: page
        })
        this.requestTable(page, pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.requestTable(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }
    handleChangeType(value) {
        this.setState({
            fundType: value
        })
    }
    handleStatusChange(value) {
        this.setState({
            pmStatus: value
        })
    }
    requestTable(currIndex, currSize) {
        const { bgDetailsId, fundType, pmStatus, balanceFlag, pmConfigTimeOfStart, pmConfigTimeOfEnd, pmBlockHeightOfStart, pmBlockHeightOfEnd, pageIndex, pageSize } = this.state
        axios.post(DOMAIN_VIP + '/rechargeBalance/queryOfRecharges', qs.stringify({
            bgDetailsId, fundType, pmStatus, balanceFlag, pmConfigTimeOfStart, pmConfigTimeOfEnd, pmBlockHeightOfStart, pmBlockHeightOfEnd,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    tableList: result.List.list,
                    pageTotal: result.List.totalCount
                })
            }
        })
    }
    onResetState() {
        this.setState({
            bgDetailsId: "",
            fundType: "2",
            pmStatus: "0",
            balanceFlag: "",
            time: [],
            pmBlockHeightOfStart: "",
            pmBlockHeightOfEnd: "",
            pmConfigTimeOfStart: '',
            pmConfigTimeOfEnd: '',
        }, () => {
            this.requestTable()
        })
    }
    render() {
        const { showHide, bgDetailsId, time, tableList, current, fundType, pmStatus, accountType, balanceFlag, pmBlockHeightOfStart, pmBlockHeightOfEnd, pageIndex, pageSize, pageTotal } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 支付中心对账 > 充值对账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>

                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">充值编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="bgDetailsId" value={bgDetailsId} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">(精确)</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList col='3' url={COMMON_QUERYATTRUSDTE} paymod="1" fundsType={fundType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">处理状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={pmStatus} style={{ width: SELECTWIDTH }} onChange={this.handleStatusChange} >
                                                <Option value='0'>请选择</Option>
                                                <Option value='2'>成功</Option>
                                                <Option value='1'>失败</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">是否一致：</label>
                                        <div className="col-sm-8">
                                            <Select value={balanceFlag} style={{ width: SELECTWIDTH }} onChange={this.handleFlagChange} >
                                                <Option value=''>请选择</Option>
                                                <Option value='0'>是</Option>
                                                <Option value='-1'>否</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">区块高度：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="pmBlockHeightOfStart" value={pmBlockHeightOfStart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="pmBlockHeightOfEnd" value={pmBlockHeightOfEnd} onChange={this.handleInputChange} /></div>
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
                                                format={TIMEFORMAT_ss}
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />
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
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId} style={{ height: `${this.state.tableList.length > 10 ? this.state.tableScroll.defaultHeight + this.state.height + 'px' : ''}` }} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title min_69px">序号</th>
                                                <th className="column-title">资金类型</th>
                                                <th className="column-title min_68px">充值编号</th>
                                                <th className="column-title wid300 min_153px">交易平台流水号</th>
                                                <th className="column-title">充值金额</th>
                                                <th className="column-title min_68px">状态</th>
                                                <th className="column-title">区块高度</th>
                                                <th className="column-title wid300 min_153px">支付中心流水号</th>
                                                <th className="column-title">充值金额</th>
                                                <th className="column-title min_68px">状态</th>
                                                <th className="column-title">区块高度</th>
                                                <th className="column-title">确认时间</th>
                                                <th className="column-title">是否一致</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ?
                                                    tableList.map((item, index) => {
                                                        return (

                                                            <tr key={index}>
                                                                <td>{(pageIndex - 1) * pageSize + index + 1}</td>
                                                                <td>{item.fundTypeName}</td>
                                                                <td>{item.bgId ? item.bgDetailsId : ''}</td>
                                                                <td>{item.bgId}</td>
                                                                <td className='moneyGreen'>{toThousands(item.bgTxAmount, true)}</td>
                                                                <td>{item.bgStatus == '2' ? "成功" : item.bgStatus == '1' ? "失败" : ""}</td>
                                                                <td>{item.bgId ? item.bgBlockHeight : ''}</td>
                                                                <td>{item.pmId}</td>
                                                                <td className='moneyGreen'>{toThousands(item.pmTxAmount, true)}</td>
                                                                <td>{item.pmStatus == '2' ? "成功" : item.pmStatus == '1' ? "失败" : ""}</td>
                                                                <td>{item.pmId ? item.pmBlockHeight : ''}</td>
                                                                <td>{item.pmConfigTime ? moment(item.pmConfigTime).format(TIMEFORMAT) : ''}</td>
                                                                <td>{(() => {
                                                                    switch (item.balanceFlag) {
                                                                        case 0:
                                                                            return '是'
                                                                            break;
                                                                        case -1:
                                                                            return '否'
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                                })()}</td>
                                                            </tr>
                                                        )
                                                    }) :
                                                    <tr className="no-record"><td colSpan="13">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                    {
                                        pageTotal > 0 && <Pagination
                                            current={pageIndex}
                                            size="small"
                                            current={pageIndex}
                                            total={pageTotal}
                                            onChange={this.changPageNum}
                                            showTotal={total => `总共 ${total} 条`}
                                            onShowSizeChange={this.onShowSizeChange}
                                            pageSizeOptions={PAGRSIZE_OPTIONS20}
                                            defaultPageSize={PAGESIZE}
                                            showSizeChanger
                                            showQuickJumper />
                                    }
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        )

    }
}