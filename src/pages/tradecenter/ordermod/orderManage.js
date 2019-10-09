import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { toThousands, tableScroll, dateToFormat,saveToSS,getFromSS,removeFromSS,splitArr,getType,isFunc } from '../../../utils'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, PAGRSIZE_OPTIONS20, TIMEFORMAT_ss } from '../../../conf'
import { Button, Tabs, Pagination, Select, Modal, DatePicker, message } from 'antd'
import { SeOp } from '../../../components/select/asyncSelect'
import UserInfoDetail from '../../systemcenter/usermod/userInfoDetail'
const TabPane = Tabs.TabPane;
const { RangePicker } = DatePicker
const Option = Select.Option;
const Big = require('big.js')

const _funds = {
    10: 'USDT',
    2: 'BTC',
    51: 'VDS'
}

export default class OrderManage extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            recordNo: '',
            userName: '',
            userId: '',
            status: '',
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: 0,
            tableList: [],
            visible: false,
            modalHtml: '',
            title: '',
            width: '',
            height: 0,
            orderNo: '',
            tableScroll: {
                tableId: 'ODRMANE',
                x_panelId: 'ODRMANEX',
                defaultHeight: 500,
            },
            time: [],
            fundstype: '',
            tk: '1,2,6',
            detailUser: null,
            pageTabs:true
        }
        this.clickHide = this.clickHide.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleChangeStatus = this.handleChangeStatus.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.jumpOther = this.jumpOther.bind(this)
        this.getHeight = this.getHeight.bind(this)

    }
    componentDidMount() {
        this.setState({
            appActiveKey: this.props.appActiveKey,
        })
        this.sendListen()
        // tableScroll(`#${this.state.tableScroll.tableId}`, 'add', `#${this.state.tableScroll.x_panelId}`, this.getHeight)
    }
    componentWillUnmount() {
        // tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    componentWillReceiveProps(nextProps) {
        if (nextProps.appActiveKey == this.state.appActiveKey) {
            this.sendListen()
        }
        
    }
    sendListen = async () => {

        let _key = splitArr(getFromSS() || '',0,'-')
        let _value = splitArr(getFromSS() || '',1,'-')

        let funObj = {
            orderNo: () => this.setState({orderNo:_value,recordNo:''}),
            recordNo: () => this.setState({recordNo:_value,orderNo:''})
        }
        
        funObj[_key] && isFunc(funObj[_key]) && await funObj[_key]()
        
        await this.requestTable()
        removeFromSS()
       
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

    jumpOther(json, recordNo, _key) {
        this.props._this.add(json)
        saveToSS(_key + '-' + recordNo)
        // this.props._this.setAppObj({ ...json, orderNo: recordNo })
    }
    inquireBtn() {
        this.setState({
            pageIndex: PAGEINDEX
        }, () => this.requestTable())
    }
    handleCancel() {
        this.setState({
            visible: false
        })
    }
    handleChangeStatus(val) {
        this.setState({
            status: val
        })
    }

    //点击收起
    clickHide() {
        let { showHide, xheight, pageSize } = this.state;
        this.setState({
            showHide: !showHide,
        })
    }
    requestTable(currIndex, currSize) {
        const { recordNo, userId, userName, status, pageIndex, pageSize, tk, time, fundstype, orderNo } = this.state
        axios.post(DOMAIN_VIP + '/orderform/list', qs.stringify({
            recordNo, userId, statustr: tk,
            fundstype, orderNo,
            createtimeS: time[0] ? moment(time[0]).format(TIMEFORMAT_ss) : '',
            createtimeE: time[1] ? moment(time[1]).format(TIMEFORMAT_ss) : '',
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    tableList: result.data.list,
                    pageTotal: result.data.totalCount
                })
            } else {
                message.error(result.msg)
            }
        })
    }
    onResetState() {
        this.setState({
            recordNo: '',
            userName: '',
            userId: '',
            status: '',
            orderNo: '',
            time: []
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
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            time: date
        })
    }
    selectFunds = (v) => this.setState({ fundstype: v })
    tcb = async tk => {
        await this.setState({ tk })
        this.requestTable()
    }
    toMark = (id) => {
        Modal.confirm({
            title: '你确定要标记吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk: () => {
                axios.post(DOMAIN_VIP + '/orderform/update', qs.stringify({ id }))
                    .then(res => {
                        const result = res.data;
                        if (result.code == 0) {
                            message.success(result.msg)
                            this.requestTable()
                        } else {
                            message.error(result.msg)
                        }
                    })
            },
            onCancel() {
                console.log('Cancel');
            },
        })
    }
    toIssue = (detailUser = null) => this.setState({ pageTabs: !this.state.pageTabs, detailUser, })
    render() {
        Big.RM = 0;
        let breakurl = [{
            key: 700100010052,
            name: "广告管理",
            url: "/tradecenter/advertmod/advertManage"
        }, {
            key: 700100010056,
            name: "申诉管理",
            url: "/tradecenter/appealmod/appealManage"
        }];
        const { showHide, recordNo, userId, userName, status, pageIndex, pageSize, pageTotal, tableList, visible, title, modalHtml, width, time, fundstype, orderNo, tk,pageTabs } = this.state
        return (
            <div className="right-con">
                {
                    pageTabs ?
                
                <div>
                    <div className="page-title">
                        当前位置：数据中心 > OTC交易中心 > 订单管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                    </div>
                    <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            {showHide && <div className="x_panel">
                                <div className="x_content">

                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">订单编号：</label>
                                            <div className="col-sm-8 ">
                                                <input type="text" className="form-control" name="recordNo" value={recordNo} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">广告编号：</label>
                                            <div className="col-sm-8 ">
                                                <input type="text" className="form-control" name="orderNo" value={orderNo} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">用户编号：</label>
                                            <div className="col-sm-8 ">
                                                <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                    {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名称：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div> */}
                                    <SeOp title='货币类型' value={fundstype} onSelectChoose={v => this.selectFunds(v)} ops={_funds} />
                                    {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">订单状态：</label>
                                        <div className="col-sm-9">
                                            <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.handleChangeStatus} >
                                                <Option value=''>请选择</Option>
                                                <Option value='1'>等待付款</Option>
                                                <Option value='2'>已付款</Option>
                                                <Option value='3'>交易完成</Option>
                                                <Option value='4'>交易取消</Option>
                                                <Option value='5'>异常订单</Option>
                                                <Option value='6'>申诉中</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div> */}
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">日期范围:</label>
                                            <div className="col-sm-8">
                                                <RangePicker
                                                    showTime={{
                                                        defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                    }}
                                                    format={TIMEFORMAT_ss}
                                                    onChange={this.onChangeCheckTime}
                                                    value={time} />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-6 col-sm-6 col-xs-6 right">
                                        <div className="right">
                                            <Button type="primary" onClick={() => this.inquireBtn()}>查询</Button>
                                            <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            }
                            <div className="x_panel">
                                <div className="x_content">
                                    <Tabs onChange={this.tcb} defaultActiveKey='1,2,6' activeKey={this.state.tk}>
                                        <TabPane tab='进行中订单' key='1,2,6'></TabPane>
                                        <TabPane tab='异常订单' key='5'></TabPane>
                                        <TabPane tab='历史订单' key='3,4'></TabPane>
                                    </Tabs>
                                    <div className="table-responsive-fixed">
                                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                            <thead>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title must_153px">订单编号</th>
                                                    <th className="column-title">创建时间</th>
                                                    <th className={`"column-title " ${(tk == '5' || tk == '3,4') ? '' : 'hide'}`}>{tk == 5 ? '异常时间' : "完成时间"}</th>
                                                    <th className="column-title">货币类型</th>
                                                    <th className="column-title">订单状态</th>
                                                    <th className="column-title">交易类型</th>
                                                    <th className="column-title">买方ID</th>
                                                    <th className={`"column-title " ${tk == 5 ? '' : 'hide'}`}>买方信息</th>
                                                    <th className="column-title">卖方ID</th>
                                                    <th className={`"column-title " ${tk == 5 ? '' : 'hide'}`}>卖方信息</th>
                                                    <th className="column-title">交易数量</th>
                                                    <th className="column-title">手续费</th>
                                                    <th className={`"column-title " ${tk == 5 ? 'hide' : ''}`}>单价(CNY)</th>
                                                    <th className="column-title">交易金额(CNY)</th>
                                                    <th className="column-title">广告编号</th>
                                                    <th className="column-title">{tk == 5 ? '操作' : '申诉编号'}</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {
                                                    tableList.length > 0 ?
                                                        tableList.map((item, index) => {
                                                            return (
                                                                <tr key={item.id}>
                                                                    <td>{(pageIndex - 1) * pageSize + index + 1}</td>
                                                                    <td>{item.recordNo}</td>
                                                                    <td>{dateToFormat(item.coinTime)}</td>
                                                                    <td className={(tk == '5' || tk == '3,4') ? '' : 'hide'}>{dateToFormat(tk == 5 ? item.errorTime : item.status == 4 ? item.cancelTime : item.completeTime)}</td>
                                                                    <td>{item.coinTypeName}</td>
                                                                    <td>{item.statusName}</td>
                                                                    <td>{item.typeName}</td>
                                                                    <td><a href="javascript:void(0)" onClick={() => this.toIssue({userId:item.buyUserId})}>{item.buyUserId}</a></td>
                                                                    <td className={tk == 5 ? '' : 'hide'}>{item.buyUserInfo}</td>
                                                                    <td><a href="javascript:void(0)" onClick={() => this.toIssue({userId:item.sellUserId})}>{item.sellUserId}</a></td>
                                                                    <td className={tk == 5 ? '' : 'hide'}>{item.sellUserInfo}</td>
                                                                    <td>{item.coinNumber}</td>
                                                                    <td className='moneyGreen'>{toThousands(item.userId == item.buyUserId ? item.buyUserFee : item.sellUserFee)}</td>
                                                                    <td className={`moneyGreen ${tk == 5 ? 'hide' : ''}`}>{item.coinPrice ? item.coinPrice : ''}</td>
                                                                    {/* toThousands(new Big(item.coinPrice).toFixed()) */}
                                                                    <td className='moneyGreen'>{item.coinPrice ? toThousands(item.sumAmount) : ''}</td>
                                                                    <td><a href="javascript:void(0)" onClick={() => { this.jumpOther(breakurl[0], item.orderNo,'orderNo') }}>{item.orderNo}</a></td>
                                                                    <td>{
                                                                        tk == '5' ?
                                                                            <a href="javascript:void(0)" onClick={() => { this.toMark(item.id) }}>标记</a>
                                                                            :
                                                                            <a href="javascript:void(0)" onClick={() => { this.jumpOther(breakurl[1], item.recordNo,'recordNo') }}>{item.complainId}</a>}
                                                                    </td>
                                                                </tr>
                                                            )
                                                        })
                                                        : <tr className="no-record"><td colSpan="19">暂无数据</td></tr>
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
                                                showSizeChanger
                                                showQuickJumper
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE}
                                            />
                                        }
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
                 :
                 <UserInfoDetail  permissList= {this.props.permissList}  toIssue={this.toIssue}  user={this.state.detailUser} />}
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 60 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}
                </Modal>
            </div>
        )

    }
}