import axios from '../../../utils/fetch'
import moment, { isDate } from 'moment'
import ModalDetail from './modal/modalDetail'
import GoogleCode from '../../common/modal/googleCode'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, PAGRSIZE_OPTIONS20, TIMEFORMAT_ss } from '../../../conf'
import { Button, DatePicker, Tabs, Pagination, Modal, Select, message, Table } from 'antd'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
import { toThousands, pageLimit, tableScroll } from '../../../utils'
import HedgeMarketList from '../../common/select/hedgeMarketList'
import { CommonHedgeResults, JudgeHedgeResults } from '../../common/select/commonHedgeResults'
import IdsModal from './modal/idsModal'
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const Column = Table.Column

export default class HedgingTradeRecord extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            entrustFromId: '',
            entrustPlatform: '',
            entrustMarket: '',
            entrustType: '',
            entrustStatus: '',
            placeOrderType: '',
            strFromTime: '',
            strToTime: '',

        }
        this.state = {
            time: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            visible: false,
            title: '',
            modalHtml: '',
            tableList: [],
            pageTotal: 0,
            platformsList: [<Option key='0' value=''>请选择</Option>],
            showHide: true,
            googleCode: '',
            modalWidth: '',
            limitBtn: [],
            googVisibal: false,
            id: '',
            type: '',
            check: '',
            height: 0,
            ids: '',
            selectedRowKeys: [],//选中项的 key 数组 
            selectedRows: [],//选中项的 item 数组
            remainingAmount: '',
            tabKey: 1,
            ...this.defaultState,
            urls: {
                1: '/brush/hedge/order/list',
                2: '/brush/hedge/order/toList/reFailTab',
                3: '/brush/hedge/order/toList/failMergeTab'
            },
            disabled: {
                mergePlace: false,
                reCancel: false,
                reSync: false,
                resetStatus: false
            },
            sourceIds: [],
            hedgeResults: {}

        }
        this.requestList = this.requestList.bind(this)
        this.handlePlatformChange = this.handlePlatformChange.bind(this)
        this.handleMarketChange = this.handleMarketChange.bind(this)
        this.handleTypeChange = this.handleTypeChange.bind(this)
        this.handleStatusChange = this.handleStatusChange.bind(this)
        this.handleOrderChange = this.handleOrderChange.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.cancelLations = this.cancelLations.bind(this)
        this.closePosition = this.closePosition.bind(this)
        this.showDetail = this.showDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.onCancelLations = this.onCancelLations.bind(this)
        this.onClosePosition = this.onClosePosition.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }

    componentDidMount() {
        this.requestList()
        JudgeHedgeResults().then(({ hedgeResults }) => {
            this.setState({
                hedgeResults,
                limitBtn: pageLimit('hedge', this.props.permissList)
            }, () => this.requestTable())

        })

    }
    componentWillReceiveProps() {
    }
    componentWillUnmount() {

    }

    //弹窗 ok 
    handleOk() {
        this.setState({ loading: true });
        setTimeout(() => {
            this.setState({
                loading: false,
                visible: false
            });
        }, 3000);
    }
    //弹窗显示
    showModal() {
        this.setState({
            visible: true,
        });
    }
    //select 变化时
    handlePlatformChange(value) {
        this.setState({
            entrustPlatform: value
        })
    }
    handleMarketChange(value) {
        this.setState({
            entrustMarket: value
        })
    }
    //查询按钮
    inquireBtn() {
        this.setState({
            pageIndex: PAGEINDEX
        }, () => this.requestTable())

    }
    //select变化时
    handleTypeChange(value) {
        this.setState({
            entrustType: value
        })
    }
    //select变化时
    handleStatusChange(value) {
        this.setState({
            entrustStatus: value
        })
    }
    handleOrderChange(value) {
        this.setState({
            placeOrderType: value
        })
    }
    callback = (tabKey) => {
        this.setState({
            tabKey,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            selectedRowKeys: [],//选中项的 key 数组 
            selectedRows: [],//选中项的 item 数组
        }, () => this.requestTable())
    }
    //点击收起
    clickHide() {
        let { showHide, } = this.state;

        this.setState({
            showHide: !showHide,
        })
    }
    //弹窗隐藏
    handleCancel() {
        this.setState({
            visible: false,
        });
    }
    //明细
    showDetail(item, index) {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>关闭</Button>
        ]
        this.setState({
            visible: true,
            title: '委托记录成交明细',
            modalWidth: '1200px',
            modalHtml: <ModalDetail item={item} index={index} />

        })
    }
    //请求平台
    requestList() {
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
    requestTable(currIndex, currSize, type) {
        const { entrustPlatform, entrustMarket, entrustType, entrustStatus, placeOrderType, strFromTime, strToTime, pageIndex, pageSize, pageTotal, tabKey, urls, entrustFromId } = this.state
        axios.get(DOMAIN_VIP + urls[tabKey], {
            params: {
                entrustPlatform, entrustMarket, entrustType, entrustStatus,
                // placeOrderType,
                strFromTime, strToTime,
                entrustFromId,
                pageIndex: currIndex || pageIndex,
                pageSize: currSize || pageSize
            }
        }).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableList = result.data.list || [];
                for (let i = 0; i < tableList.length; i++) {
                    tableList[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1 || i + 1;
                    tableList[i].key = tableList[i].id || i
                }
                this.setState({
                    tableList: tableList,
                    pageTotal: result.data.totalCount,
                    sourceIds: result.data.sourceIds || [],
                    disabled: Object.assign({}, this.state.disabled, type ? { [type]: false } : {})
                    // disabled: {
                    //     mergePlace: false,
                    //     reCancel: false,
                    //     reSync: false
                    // }
                })
            } else {
                message.warning(result.msg)
            }
        })
    }


    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page,
            pageSize,
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
    onChangeCheckTime(date, dateString) {
        this.setState({
            strFromTime: dateString[0] ? dateString[0] : '',
            strToTime: dateString[1] ? dateString[1] : '',
            time: date
        })
    }
    //google 验证弹窗
    modalGoogleCode(googleItem, type) {
        this.setState({
            googVisibal: true,
            googleItem,
            type,
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

    //google 按钮
    modalGoogleCodeBtn(value) {
        const { googleItem, type } = this.state
        const { googleCode } = value
        axios.post(DOMAIN_VIP + "/common/checkGoogleCode", qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                message.success(result.msg)
                if (type == "1") {
                    this.setState({
                        googVisibal: false
                    }, () => this.onCancelLations(googleItem))

                } else if (type == "2") {
                    this.setState({
                        googVisibal: false
                    }, () => this.onClosePosition(googleItem))
                } else if (type == 'reOrder') {
                    this.setState({
                        googVisibal: false
                    }, () => this.reOrderBtn(googleItem))
                } else if (type == 'ids') {
                    this.setState({
                        googVisibal: false
                    }, () => this.mergeOrderBtn(googleItem))
                }

            } else {
                message.warning(result.msg)
            }
        })
    }
    //撤单
    onCancelLations(id) {
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP + "/brush/hedge/order/cancel", qs.stringify({
                id
            })).then(res => {
                const result = res.data
                if (result.code == 0) {
                    message.success(result.msg);
                    this.requestTable()
                } else {
                    message.error(result.msg);
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
        console.log('OK');
    }
    //撤单弹窗
    cancelLations(id, type) {
        let self = this
        Modal.confirm({
            title: `确定要撤单吗？`,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(id, type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //平仓
    onClosePosition(id) {
        axios.post(DOMAIN_VIP + "/brush/hedge/order/closePosition", qs.stringify({
            id
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                message.success(result.msg);
                this.requestTable()
            } else {
                message.error(result.msg);
            }
        })
    }
    //平仓弹窗
    closePosition(id, type) {
        let self = this
        Modal.confirm({
            title: `确定要平仓吗？`,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(id, type)
            },
            onCancel() {
                console.log('Cancel');
            }

        })
    }
    //重新下单
    reOrder = (item, type) => {

        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.modalGoogleCode(item, type)}>
                保存
            </Button>,
        ]
        this.setState({
            ids: '',
            modalHtml: <IdsModal handleInputChange={this.handleInputChange} item={item} />,
            title: '重新下单',
            modalWidth: '700px',
            visible: true,
            remainingAmount: item.remainingAmount || ''

        })
        // let self = this
        // Modal.confirm({
        //     title: `确定要重新下单吗？`,
        //     okText: '确定',
        //     okType: 'more',
        //     cancelText: '取消',
        //     onOk() {
        //         self.modalGoogleCode(id, type)
        //     },
        //     onCancel() {
        //         console.log('Cancel');
        //     }

        // })
    }
    reOrderBtn = (item) => {
        const { remainingAmount } = this.state
        axios.post(DOMAIN_VIP + "/brush/hedge/order/rePlace", qs.stringify({
            id: item.id,
            num: remainingAmount
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                message.success(result.msg);

                this.setState({
                    visible: false
                }, () => this.requestTable())
            } else {
                message.error(result.msg);
            }
        })
    }
    mergeOrder = (type, title) => {
        const { selectedRows } = this.state
        if ((type == 'mergePlace' || type == 'resetStatus') && !selectedRows.length) {
            message.warning('至少选择一项！')
            return false
        }
        let self = this
        Modal.confirm({
            title: title,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(type, 'ids')
            },
            onCancel() {
                console.log('Cancel');
            }

        })
    }
    mergeOrderBtn = (type) => {
        const { selectedRows } = this.state
        const getIds = (_k, arr = selectedRows) => arr.map(v => v[_k] || '')
        let _ids = {
            mergePlace: () => getIds('sourceIds'),
            resetStatus: () => getIds('id')
        }
        let ids = []
        if(Object.keys(_ids).includes(type)){
           ids = _ids[type]() || []
        }
        this.setState({
            disabled: Object.assign({}, this.state.disabled, { [type]: true })
        })

        axios.get(DOMAIN_VIP + `/brush/hedge/order/${type}`, Object.keys(_ids).includes(type) ? { params: { ids: ids } } : '').then(res => {
            const result = res.data
            if (result.code == 0) {
                message.success(result.msg);
                this.setState({
                    visible: false,
                    selectedRowKeys: [],//选中项的 key 数组 
                    selectedRows: [],//选中项的 item 数组
                }, () => this.requestTable(null, null, type))
            } else {
                this.setState({
                    disabled: Object.assign({}, this.state.disabled, { [type]: false })
                })
                message.error(result.msg);
            }
        })
    }
    onSelectChangeTable = (selectedRowKeys, selectedRows) => {
        this.setState({
            selectedRowKeys,
            selectedRows,
        });
    }
    //重置
    onResetState() {
        this.setState({
            ...this.defaultState,
            time: []
        })
    }
    handleCreate() {
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            form.resetFields();
            this.modalGoogleCodeBtn(values)
        });
    }
    saveFormRef(formRef) {
        this.formRef = formRef;
    }
    //谷歌弹窗关闭
    onhandleCancel() {
        this.setState({
            googVisibal: false
        })
    }
    getCheckboxProps = v => {
        const { tabKey } = this.state
        let _limt = [
            3,//交易失败
            4,//第三方平台取消
        ]

        if (tabKey == 1) return {
            disabled: !_limt.includes(v.entrustStatus),
        }
        return {
            disabled:false
        }
    }
    render() {
        const { showHide, entrustPlatform, modalWidth, platformsList, visible, title, modalHtml, entrustMarket, entrustType, entrustStatus, placeOrderType, strFromTime, strToTime, pageIndex, pageSize, pageTotal, time, tableList, limitBtn,
            selectedRowKeys, selectedRows, tabKey, entrustFromId, disabled, hedgeResults } = this.state
        const rowSelection = {
            selectedRowKeys,
            selectedRows,
            onChange: this.onSelectChangeTable,
            getCheckboxProps: this.getCheckboxProps,
            // onSelect:this.onSelect,
            fixed: true
        };
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 保值管理 > 对冲交易记录
                        <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保值平台：</label>
                                        <div className="col-sm-9">
                                            <Select value={entrustPlatform} style={{ width: SELECTWIDTH }} onChange={this.handlePlatformChange} >
                                                {platformsList}
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HedgeMarketList market={entrustMarket} col='3' title='交易市场' handleChange={this.handleMarketChange} />
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
                                    <CommonHedgeResults status={entrustStatus} col='3' title='保值状态' handleChange={this.handleStatusChange} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">来源ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="entrustFromId" value={entrustFromId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">下单类型：</label>
                                            <div className="col-sm-8">
                                            <Select value={placeOrderType} style={{ width: SELECTWIDTH }} onChange={this.handleOrderChange} >
                                                    <Option value=''>请选择</Option>
                                                    <Option value='0'>非平仓下单</Option>
                                                    <Option value='1'>平仓下单</Option>
                                            </Select>
                                            </div>
                                        </div>
                                    </div> */}
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
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对冲ID：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="entrustFromId" value={entrustFromId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.inquireBtn()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {tabKey == 1 && <Button type="primary" disabled={disabled['resetStatus']} onClick={() => this.mergeOrder('resetStatus', '确认要批量处理这些订单吗？')}>保值成功状态标记</Button>}
                                        {tabKey == 3 && <Button type="primary" disabled={disabled['mergePlace']} onClick={() => this.mergeOrder('mergePlace', '确认要批量合并下单吗？')}>批量合并下单</Button>}
                                        <Button type="primary" disabled={disabled['reCancel']} onClick={() => this.mergeOrder('reCancel', '确认要批量手动下单吗？')}>批量手动撤单</Button>
                                        <Button type="primary" disabled={disabled['reSync']} onClick={() => this.mergeOrder('reSync', '确认要批量手动同步吗？')}>批量手动同步</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Tabs onChange={this.callback}>
                                        <TabPane key={1} tab='记录查询'></TabPane>
                                        <TabPane key={2} tab='手工操作失败数据'></TabPane>
                                        <TabPane key={3} tab='下单失败合并统计'></TabPane>
                                    </Tabs>
                                    {tabKey == 3 ?
                                        <Table
                                            dataSource={tableList}
                                            bordered
                                            // scroll={{x:2500}}
                                            rowSelection={rowSelection}
                                            pagination={false}
                                            locale={{ emptyText: '暂无数据' }}
                                        >
                                            <Column title='序号' dataIndex='index' />
                                            <Column title='交易市场' dataIndex='entrustMarket' key='entrustMarket' />
                                            <Column title='委托数量' dataIndex='entrustNum' key='entrustNum' />
                                            <Column title='交易方向' dataIndex='entrustType' key='entrustType' render={text => text == "0" ? "卖" : "买"} />
                                            <Column title='成交价格' className='moneyGreen' dataIndex='entrustPrice' key='entrustPrice' render={text => toThousands(text, true)} />
                                            <Column title='生成时间' dataIndex='addTime' key='addTime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : ''} />
                                            <Column title='来源方式' dataIndex='sourceType' render={text => text == "0" ? "推送" : "合并生成"} />
                                            <Column title='对冲ID列表' dataIndex='sourceIds' />
                                        </Table>
                                        :
                                        <Table
                                            dataSource={tableList}
                                            bordered
                                            scroll={{ x: 2500 }}
                                            rowSelection={rowSelection}
                                            pagination={{
                                                size: "small",
                                                pageSize: pageSize,
                                                current: pageIndex,
                                                total: pageTotal,
                                                onChange: this.changPageNum,
                                                showTotal: total => `总共 ${total} 条`,
                                                onShowSizeChange: this.onShowSizeChange,
                                                pageSizeOptions: PAGRSIZE_OPTIONS20,
                                                defaultPageSize: PAGESIZE,
                                                showSizeChanger: true,
                                                showQuickJumper: true
                                            }}
                                            locale={{ emptyText: '暂无数据' }}
                                        >
                                            <Column title='序号' dataIndex='index' />
                                            <Column title='对冲ID' dataIndex='id' key='id' />
                                            <Column title='委托ID' dataIndex='entrustId' key='entrustId' />
                                            <Column title='来源ID' dataIndex='entrustFromId' key='entrustFromId' />
                                            <Column title='保值状态' dataIndex='entrustStatus' key='entrustStatus' render={text => hedgeResults[text]} />
                                            <Column title='交易方向' dataIndex='entrustType' key='entrustType' render={text => text == "0" ? "卖" : "买"} />
                                            {/* <Column title='下单账号' dataIndex='entrustUserName' key='entrustUserName'  /> */}
                                            <Column title='交易市场' dataIndex='entrustMarket' key='entrustMarket' />
                                            <Column title='保值平台' dataIndex='entrustPlatform' key='entrustPlatform' />
                                            <Column title='委托数量' dataIndex='entrustNum' key='entrustNum' />
                                            <Column title='成交价格' className='moneyGreen' dataIndex='entrustPrice' key='entrustPrice' render={text => toThousands(text, true)} />
                                            <Column title='成交数量' dataIndex='executedAmount' key='executedAmount' />
                                            <Column title='用户成交价格' className='moneyGreen' dataIndex='originPrice' key='originPrice' render={text => toThousands(text, true)} />
                                            <Column title='利润' className='moneyGreen' dataIndex='profit' key='profit' render={text => toThousands(text, true)} />
                                            <Column title='剩余数量' dataIndex='remainingAmount' key='remainingAmount' />
                                            {/* <Column title='来源方式' dataIndex='sourceType' render={text => text == "0" ? "推送" : "合并生成"} />
                                        <Column title='来源ID列表' dataIndex='sourceIds' /> */}
                                            <Column title='处理次数' dataIndex='dealTimes' />
                                            <Column title='操作用户' dataIndex='operator' />
                                            <Column title='添加时间' dataIndex='addTime' key='addTime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : ''} />
                                            <Column title='保值下单时间' dataIndex='hedgeTime' key='hedgeTime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : ''} />
                                            <Column title='成交时间' dataIndex='strDealTime' key='strDealTime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : ''} />
                                            <Column title='备注' dataIndex='remarks' />
                                            {/* <Column title='操作' dataIndex='op' key='3' render={(text, record) => {
                                                return <span>
                                                    {
                                                      limitBtn.indexOf('orderCancel')>-1?(record.entrustStatus == "1"&&<a href="javascript:void(0)" onClick={() => this.cancelLations(record.id,"1")}>撤单</a>):''}
                                                     { limitBtn.indexOf('orderClosePosition')>-1?(record.entrustStatus == "5"&&<a href="javascript:void(0)" onClick={() => this.closePosition(record.id,"2")}>平仓</a> ):''}
                                                    {record.entrustStatus == "3" && <a href="javascript:void(0)" onClick={() => this.reOrder(record, "reOrder")}>重新下单</a>}
                                                </span>
                                            }} /> */}
                                            {/* <Column title='平仓明细' dataIndex='originId' key='originId' render={(text, record) => text > 0 ? <a href="javascript:void(0)" onClick={() => this.showDetail(record, record.index)}>平仓明细</a> : ""} /> */}
                                        </Table>}
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={modalWidth}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange={this.handleInputChange}
                    mid='HTR'
                    visible={this.state.googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }

}
