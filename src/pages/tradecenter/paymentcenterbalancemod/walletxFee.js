import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, PAGRSIZE_OPTIONS20, URLS,X_WALLET_FUNDSTYPE } from '../../../conf'
import { Button, DatePicker, Tabs, Pagination, Select } from 'antd'
import { toThousands, tableScroll } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const { COMMON_QUERYATTRUSDTE } = URLS
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class WalletxFee extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            fundsType: '',
            checkTimeStart: "",
            checkTimeEnd: "",
            time: [],
            blockHeightStart: "",
            blockHeightEnd: "",
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: 0,
            tableList: [],
            height: 0,
            tableScroll: {
                tableId: 'WalletxFeeX',
                x_panelId: 'WalletxFeeXX',
                defaultHeight: 500,
                height: 0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount() {
        this.requestTable()
        tableScroll(`#${this.state.tableScroll.tableId}`, 'add', `#${this.state.tableScroll.x_panelId}`, this.getHeight)
    }
    componentWillReceiveProps() {
        tableScroll(`#${this.state.tableScroll.tableId}`)
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
    requestTable(currIndex, currSize) {
        const { checkTimeStart, fundsType, checkTimeEnd, time, blockHeightStart, blockHeightEnd, pageIndex, pageSize } = this.state
        axios.post(DOMAIN_VIP + '/walletBalance/queryXFee', qs.stringify({
            checkTimeStart, fundsType, checkTimeEnd, blockHeightStart, blockHeightEnd,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    tableList: result.data.list,
                    pageTotal: result.data.totalCount
                })
            }
        })
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
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
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    onResetState() {
        this.setState({
            fundsType: '',
            checkTimeEnd: '',
            checkTimeStart: '',
            blockHeightStart: '',
            blockHeightEnd: '',
            time: []
        })
    }
    //充值开始时间
    onChangeTime(date, dateString) {
        this.setState({
            checkTimeStart: dateString[0],
            checkTimeEnd: dateString[1],
            time: date
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
    //资金类型 select
    handleChangeType(val) {
        this.setState({
            fundsType: val
        })
    }
    render() {
        const { showHide, checkTimeStart, tableList, fundsType, checkTimeEnd, time, blockHeightStart, blockHeightEnd, pageIndex, pageSize, pageTotal } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 支付中心对账 > X钱包对账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className=" col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList url={COMMON_QUERYATTRUSDTE} col='3' paymod="1" fundsType={fundType} handleChange={this.handleChangeType}></FundsTypeList>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">资金类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={fundsType}
                                                style={{ width: SELECTWIDTH }}
                                                onChange={this.handleChangeType}
                                                filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                                showSearch>
                                                <Option value=''>请选择</Option>
                                                {
                                                    [...X_WALLET_FUNDSTYPE.keys()].map(v => <Option key={v} value={v}>{X_WALLET_FUNDSTYPE.get(v)}</Option>)
                                                }
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对账日期：</label>
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
                                        <label className="col-sm-3 control-label">区块高度：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="blockHeightStart" value={blockHeightStart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="blockHeightEnd" value={blockHeightEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId} style={{ height: `${this.state.tableList.length > 10 ? this.state.tableScroll.defaultHeight + this.state.height + 'px' : ''}` }} className="table-responsive-yAuto table-responsive-fixed">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">资金类型</th>
                                                <th className="column-title">其他到付费</th>
                                                <th className="column-title">其他到付费网络费</th>
                                                <th className="column-title">付费到热充发生额</th>
                                                <th className="column-title">付费到热充网络费</th>
                                                <th className="column-title">APPROVE函数手续费</th>
                                                <th className="column-title">transferFrom函数手续费</th>
                                                <th className="column-title">付费到其他发生额</th>
                                                <th className="column-title">付费到其他手续费</th>
                                                <th className="column-title">区块高度</th>
                                                <th className="column-title">对账日期</th>
                                                <th className="column-title">状态</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ?
                                                    tableList.map((item, index) => {
                                                        return (

                                                            <tr key={index}>
                                                                <td>{(pageIndex - 1) * pageSize + index + 1}</td>
                                                                <td>{item.fundsTypeName}</td>
                                                                <td>{toThousands(item.otherToFee, true)}</td>
                                                                <td>{toThousands(item.otherToFeeFee, true)}</td>
                                                                <td>{toThousands(item.feeToRecharge, true)}</td>
                                                                <td>{toThousands(item.feeToRechargeFee, true)}</td>
                                                                <td>{toThousands(item.approve, true)}</td>
                                                                <td>{toThousands(item.transferFrom, true)}</td>
                                                                <td>{toThousands(item.feeToOther, true)}</td>
                                                                <td>{toThousands(item.feeToOtherFee, true)}</td>
                                                                <td>{item.minHeight}到{item.maxHeight}</td>
                                                                <td>{item.minTime ? moment(item.minTime).format('YYYY-MM-DD') : ''}到{item.maxTime ? moment(item.minTime).format('YYYY-MM-DD') : ''}</td>
                                                                <td>异常/正常预留字段</td>
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