import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalDetail from './modal/modalDetail'
import { toThousands } from '../../../utils'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, DatePicker, Tabs, Pagination, Select, Modal } from 'antd'
import { pageLimit, tableScroll } from '../../../utils/index'
import HedgeMarketList from '../../common/select/hedgeMarketList'
import {CommonHedgeResults,JudgeHedgeResults} from '../../common/select/commonHedgeResults'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class HedgeRecordBalance extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            id: '',
            entrustMarket: '',
            entrustStatus: '',
            strFromTime: '',
            strToTime: '',
            entrustUserId: '',
            entrustId: '',
            transRecordId: '',
            entrustType: ""

        }
        this.state = {
            showHide: true,
            time: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            visible: false,
            title: '',
            modalHtml: '',
            tableList: [],
            pageTotal: 0,
            limitBtn: [],
            height: 0,
            tableScroll: {
                tableId: 'HDRDBE',
                x_panelId: 'HDRDBEX',
                defaultHeight: 500,
                height: 0,
            },
            ...this.defaultState,
            hedgeResults:{}
        }
        this.clickHide = this.clickHide.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.showDetail = this.showDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount() {
        JudgeHedgeResults().then(({hedgeResults}) => {
            this.setState({
                hedgeResults,
                limitBtn: pageLimit('balance', this.props.permissList)
            },() =>this.requestTable())
            
        })
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
    requestTable(currIndex, currSize) {
        const { pageIndex, pageSize, id, entrustMarket, entrustStatus, entrustType, strFromTime, strToTime, entrustUserId, entrustId, transRecordId } = this.state
        axios.get(DOMAIN_VIP + '/brush/balance/entrusts', {
            params: {
                id, entrustMarket, entrustStatus, entrustType, strFromTime, strToTime, entrustUserId, entrustId, transrecordId: transRecordId,
                pageIndex: currIndex || pageIndex,
                pageSize: currSize || pageSize
            }
        }).then(res => {
            const result = res.data;
            if (result.code == 0) {
                if (result.data.list) {
                    this.setState({
                        tableList: result.data.list,
                        pageTotal: result.data.totalCount
                    })
                } else {
                    this.setState({
                        pageTotal: result.data.totalCount
                    })
                }

            }
        })
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            strFromTime: dateString[0] ? dateString[0] : '',
            strToTime: dateString[1] ? dateString[1] : '',
            time: date
        })
    }
    //明细
    showDetail(id) {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>关闭</Button>
        ]
        this.setState({
            visible: true,
            title: '对冲记录明细',
            modalHtml: <ModalDetail id={id} />

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
    //弹窗隐藏
    handleCancel() {
        this.setState({
            visible: false,
        });
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
    onResetState() {
        this.setState({
            ...this.defaultState,
            time: [],
        })
    }
    handleTypeChange = entrustType => { this.setState({ entrustType }) }
    selectMarket = (entrustMarket) => {
        this.setState({ entrustMarket })
    }
    selectStatus = (entrustStatus) => {
        this.setState({ entrustStatus })
    }
    render() {
        const { showHide, entrustMarket, transRecordId, time, pageIndex, pageSize, visible, title, modalHtml, tableList, pageTotal,
            limitBtn, entrustType, entrustUserId, id, entrustId, entrustStatus,hedgeResults } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 量化交易对账 > 对冲记录对账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HedgeMarketList market={entrustMarket} col='3' title='盘口市场' handleChange={this.selectMarket} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <CommonHedgeResults status={entrustStatus} col='3' title='保值状态' handleChange={this.selectStatus} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={entrustType} style={{ width: SELECTWIDTH }} onChange={this.handleTypeChange} >
                                                <Option value=''>请选择</Option>
                                                <Option value='1'>买入</Option>
                                                <Option value='0'>卖出</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">来源ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="id" value={id} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托用户ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="entrustUserId" value={entrustUserId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="entrustId" value={entrustId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交记录ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="transRecordId" value={transRecordId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />
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
                                <div id={this.state.tableScroll.tableId} style={{ height: `${this.state.tableList.length ? this.state.tableScroll.defaultHeight + this.state.height + 'px' : ''}` }} className="table-responsive-xyAuto table-responsive-fixed">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">来源ID</th>
                                                <th className="column-title">保值状态</th>
                                                <th className="column-title">交易方向</th>
                                                <th className="column-title">用户委托ID</th>
                                                <th className="column-title">交易市场</th>
                                                <th className="column-title">用户成交数量</th>
                                                <th className="column-title">用户成交价格</th>
                                                <th className="column-title">用户ID</th>
                                                <th className="column-title">用户成交ID</th>
                                                <th className="column-title">用户成交时间</th>
                                                <th className="column-title">用户手续费</th>
                                                <th className="column-title">来源方式</th>
                                                <th className="column-title">保值成交价格</th>
                                                <th className="column-title">保值数量</th>
                                                <th className="column-title">保值剩余数量</th>
                                                <th className="column-title">利润</th>
                                                <th className="column-title">对冲ID</th>
                                                <th className="column-title">保值对冲状态</th>
                                                <th className="column-title">保值平台</th>
                                                <th className="column-title">保值次数</th>
                                                <th className="column-title">保值委托ID</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ?
                                                    tableList.map((item, index) => {
                                                        return (

                                                            <tr key={index}>
                                                                <td>{(pageIndex - 1) * pageSize + index + 1}</td>
                                                                <td>{item.id}</td>
                                                                <td>{hedgeResults[item.entrustStatus]}</td>
                                                                <td>{item.entrustType > 0 ? '买' : '卖'}</td>

                                                                <td>{item.entrustId}</td>
                                                                <td>{item.entrustMarket}</td>
                                                                <td>{item.entrustNum}</td>
                                                                <td className='moneyGreen'>{toThousands(item.entrustPrice, true)}</td>
                                                                <td>{item.entrustUserId}</td>
                                                                <td>{item.transrecordId}</td>
                                                                <td>{moment(item.transrecordTime).format(TIMEFORMAT)}</td>
                                                                <td className='moneyGreen'>{toThousands(item.entrustFee, true)}</td>

                                                                <td>{item.sourceType > 0 ? '合并生成' : '推送'}</td>
                                                                <td className='moneyGreen'>{toThousands(item.hedgePrice, true)}</td>

                                                                <td>{item.hedgeExecutedNumber}</td>
                                                                <td>{item.hedgeRemainingNumber}</td>
                                                                <td className='moneyGreen' style={item.hedgeProfit > 0 ? { color: "#FF3333" } : {}}>{toThousands(item.hedgeProfit, true)}</td>
                                                                <td>{item.hedgeOrderId}</td>
                                                                <td>{hedgeResults[item.hedgeStatus]}</td>
                                                                <td>{item.hedgePlatform}</td>
                                                                <td>{
                                                                    item.hedgeOrderCount > 1 ?
                                                                        (limitBtn.indexOf('entrustDetails') > -1 ? <a href="javascript:void(0)" onClick={() => this.showDetail(item.id)}>成交明细</a> : '')
                                                                        : item.hedgeOrderId}</td>
                                                                <td>{item.hedgeEnturstId}</td>
                                                            </tr>
                                                        )
                                                    }) :
                                                    <tr className="no-record"><td colSpan="22">暂无数据</td></tr>
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
                                            showTotal={total => `总共 ${total} 条`}
                                            onChange={this.changPageNum}
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
                <Modal
                    visible={visible}
                    title={title}
                    width="1200px"
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}
                </Modal>
            </div>
        )

    }
}