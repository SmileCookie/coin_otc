import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, TIMEFORMAT_ss, PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, Tabs, Pagination, DatePicker, Select, message } from 'antd'
import { toThousands, tableScroll } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
const TabPane = Tabs.TabPane;
const { MonthPicker, RangePicker, } = DatePicker;
const Option = Select.Option;

export default class HedgeAccountFinancial extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            time: [],
            userName: '',
            accountType: [],
            tableList: [],
            tab: 0,
            totalPage: 0,
            pageTotal: 0,
            platformsList: [<Option key='0' value=''>请选择</Option>],
            platform: '',
            currency: '',
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: '20',
            height: 0,
            tableScroll: {
                tableId: 'HDEAOTFNL',
                x_panelId: 'HDEAOTFNLX',
                defaultHeight: 500,
                height: 0,
            }
        }
        this.requestList = this.requestList.bind(this)
        this.resetState = this.resetState.bind(this)
        this.requestPlatform = this.requestPlatform.bind(this)
        this.requestCurrency = this.requestCurrency.bind(this)
        this.platformDataChange = this.platformDataChange.bind(this)
        this.currencyDataChange = this.currencyDataChange.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.refreshTable = this.refreshTable.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.getHeight = this.getHeight.bind(this)
        this.callbackTabs = this.callbackTabs.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestNewTable = this.requestNewTable.bind(this)

    }
    componentDidMount() {
        this.requestList()
        this.requestPlatform()
        
        //this.requestCurrency()
        //this.requestTable()
        //tableScroll(`#${this.state.tableScroll.tableId}`, 'add', `#${this.state.tableScroll.x_panelId}`, this.getHeight)
    }
    componentWillReceiveProps() {
        //tableScroll(`#${this.state.tableScroll.tableId}`)
        //tableScroll(`#${this.state.tableScroll.tableId}`, 'add', `#${this.state.tableScroll.x_panelId}`, this.getHeight)
    }
    componentWillUnmount() {
        // tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight) {
        this.setState({
            xheight
        })
    }



    resetState = () => {
        this.setState({
            platform: '',
            currency: '',
            strToTime:'',
            strFromTime:'',
            time:[]
        })
    }
    handleChange(value) {
        this.setState({
            userName: value
        })
        this.requestTable(value)
    }

    platformDataChange(value) {
        this.setState({
            platform: value
        })

    }
    currencyDataChange(value) {
        this.setState({
            currency: value
        })

    }



    //请求平台
    requestPlatform() {
        axios.get(DOMAIN_VIP + '/brush/common/platforms').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.platformsList;
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    accountTypeArr.push(<Option key={i + 1} value={result.data[i]}>{result.data[i]}</Option>)
                }
                this.setState({
                    platformsList: accountTypeArr
                })
            } else {
                message.warning(result.msg)
            }
        })
    }

    //请求币种
    requestCurrency() {
        axios.get(DOMAIN_VIP + '/brush/common/hedgeResults').then(res => {
            const result = res.data;
            let accountTypeA = this.state.currencyList;
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    accountTypeA.push(<Option key={i + 1} value={result.data[i]}>{result.data[i]}</Option>)
                }
                this.setState({
                    currencyList: accountTypeA
                })
            } else {
                message.warning(result.msg)
            }
        })
    }


    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            strFromTime: dateString[0] ? moment(dateString[0]).format(TIMEFORMAT) : '',
            strToTime: dateString[1] ? moment(dateString[1]).format(TIMEFORMAT) : '',
            time: date
        })
    }
    requestList() {
        axios.get(DOMAIN_VIP + '/brush/hedge/account/userNames').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.accountType;
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    accountTypeArr.push(<Option key={i + 1} value={result.data[i]}>{result.data[i]}</Option>)
                }
                this.setState({
                    accountType: accountTypeArr,
                    userName: result.data[0]
                }, () => this.requestTable(result.data[0]))
            } else {
                message.warning(result.msg)
            }
        })
    }

    //tab 切换时
    callbackTabs(key) {
        const { userName } = this.state;
        this.setState({
            tab: key
        })
        if (key == 0) {
            this.requestTable(userName);
        } else {
            this.requestNewTable();
        }

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


    //输入时 input 设置到 satte
    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    //查询按钮
    inquireBtn() {

        this.setState({
            pageIndex: 1
        },()=>{

            this.requestNewTable()
        })
        

    }

    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        })
        this.requestNewTable(page, pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.requestNewTable(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }
    requestNewTable(current, size) {
        const { platform, currency, strFromTime, strToTime, pageIndex, pageSize } = this.state

        axios.get(DOMAIN_VIP + "/brush/hedge/account/financial/history/list", {
            params: {

                platform: platform || '',
                currency: currency || '',
                strFromTime: strFromTime || '',
                strToTime: strToTime || '',
                pageIndex: current || pageIndex,
                pageSize: size || pageSize


            }
        })
            .then(res => {
                const result = res.data
                if (result.code == 0) {
                    this.setState({
                        tableList: result.data.list,
                        pageTotal: result.data.totalCount,
                        pageSize: result.data.pageSize,
                        totalPage: result.data.totalPage

                    })
                } else {
                    message.warning(result.msgs)
                }
            })
    }
    requestTable(value) {
        const { userName } = this.state
        axios.get(DOMAIN_VIP + "/brush/hedge/account/financial/list", {
            params: {
                userName: value,
            }
        }).then(res => {
            const result = res.data
            if (result.code == 0) {

                this.setState({
                    tableList: result.data
                })
                // console.log(result)
            } else {
                message.warning(result.msgs)
            }


        })
    }
    refreshTable() {
        const { userName } = this.state
        axios.post(DOMAIN_VIP + "/brush/hedge/account/financial/refresh", qs.stringify({
            userName
        })).then(res => {
            const result = res.data
            console.log(result)
            if (result.code == 0) {
                this.setState({
                    tableList: result.data
                })
                message.success(result.msg);
            } else {
                message.success(result.msg);
            }
        })
    }
    render() {
        const { showHide, accountType, tab, userName, platformsList, tableList, platform, currency, time, pageTotal, pageIndex, pageSize } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 保值管理 > 对冲财务信息
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保值平台账户：</label>
                                        <div className="col-sm-9">
                                            <div className="col-sm-4">
                                                <Select value={userName} style={{ width: SELECTWIDTH }} onChange={this.handleChange} >
                                                    {accountType}
                                                </Select>
                                            </div>
                                            <div className="col-sm-3">
                                                <Button type="primary" onClick={() => this.refreshTable()}>刷新</Button>
                                            </div>
                                        </div>
                                        <p className="col-sm-12" style={{ lineHeight: "36px", color: "#990099" }}>(&nbsp;刷新该用户的账户余额，取当前时间，需要几秒钟的时间，请耐心等待!&nbsp;)</p>
                                    </div>

                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">平台：</label>
                                        <div className="col-sm-8 ">
                                            <Select value={platform} style={{ width: SELECTWIDTH }} onChange={this.platformDataChange} >
                                                {platformsList}
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <FundsTypeList title='币种' type={1} fundsType={currency} handleChange={this.currencyDataChange} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">日期：</label>
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
                                        <Button type="primary" onClick={() => this.inquireBtn()}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>

                            </div>
                        </div>
                        }





                        <div className="x_panel">
                            <div className="x_content">
                                <Tabs defaultActiveKey="0" onChange={this.callbackTabs}>
                                    <TabPane key='0' tab='实时数据'></TabPane>
                                    <TabPane key='1' tab='历史数据'></TabPane>
                                </Tabs>
                                <div className="table-responsive-yAuto">


                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">平台</th>
                                                <th className="column-title">账户名称</th>
                                                <th className="column-title">币种</th>
                                                <th className="column-title">可用金额</th>
                                                <th className="column-title">总金额</th>
                                                <th className="column-title">日期</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ?
                                                    tableList.map((item, index) => {
                                                        return (
                                                            <tr key={index}>
                                                                <td>{item.platform}</td>
                                                                <td>{item.userName}</td>
                                                                <td>{item.currency}</td>
                                                                <td>{item.available ? toThousands(item.available) : ''}</td>
                                                                <td>{item.amount ? toThousands(item.amount) : ''}</td>
                                                                <td>{item.addTime ? moment(item.addTime).format(TIMEFORMAT_ss) : '--'}</td>
                                                            </tr>
                                                        )
                                                    })
                                                    : <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>

                                {
                                    tab == 0 ?
                                        '' :
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
                                }

                            </div>
                        </div>

                    </div>
                </div>
            </div>
        )

    }
}