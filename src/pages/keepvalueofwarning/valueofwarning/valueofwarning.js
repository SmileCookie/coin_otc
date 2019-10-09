import React, { Component } from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, DatePicker, Tabs, Pagination, Select, message, Table, Modal, Badge } from 'antd'
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT, PAGRSIZE_OPTIONS20 } from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import MarketRequests from '../../common/select/marketrequests'
import { toThousands } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList';
import DetailModal from '../components/detailmodals'
import { relative } from 'path';
import { Z_BLOCK } from 'zlib';
// import Decorator from '../../../decorator'
//保值异常
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { Column } = Table


class ValueOfWarning extends Component {
    constructor(props) {
        super(props);
        this.state = {
            googVisibal: false,
            visible: false,
            isreLoad: false,
            showHide: true,
            modalHtml: '',
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            width: '',
            pagination: {
                showQuickJumper: true,
                showSizeChanger: true,
                showTotal: total => `总共${total}条`,
                size: 'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions: PAGRSIZE_OPTIONS20,
                defaultPageSize: PAGESIZE
            },

            tableSource: [],
            market: '',
            type: '',
            Stime: '',
            Etime: '',
            time: [],
            tabKey: '1',
            unfilledSum: 0,//未成交总数
            failureSum: 0,//失败总数
            selectedRowKeys: [], // 复选框
            loading: false,

        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChangeState = this.handleChangeState.bind(this)
        this.selectFundsType = this.selectFundsType.bind(this)
        this.handleChangeSolve = this.handleChangeSolve.bind(this)
        this.handleTypeChange = this.handleTypeChange.bind(this)
        this.start = this.start.bind(this)
        this.onSelectChange = this.onSelectChange.bind(this)

        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render() {

        const { loading, selectedRowKeys, time, pageSize, showHide, tableSource, pagination, googVisibal, pageIndex, check, modalHtml, visible, width, market, tabKey, type, unfilledSum, failureSum, } = this.state
        let entrustTypes = ['卖出', '买入'];
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
        };
        const hasSelected = selectedRowKeys.length > 0;

        // let entrustStatus = ['--','交易中','交易成功','','第三方平台取消','量化后台取消']
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 保值异常 > 保值异常
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <MarketRequests market={market} underLine={true} handleChange={this.handleChangeSelect} col='3' />
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={type} style={{ width: SELECTWIDTH }} onChange={this.handleTypeChange} >
                                                <Option value=''>请选择</Option>
                                                <Option value='1'>买入</Option>
                                                <Option value='0'>卖出</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保值状态：</label>
                                        <div className="col-sm-8">
                                                <Select value={solveValue} 
                                                style={{width:SELECTWIDTH}}
                                                onChange={this.handleChangeSolve}
                                                >
                                                    <Option value=''>请选择</Option>
                                                    <Option value = '1'>正常</Option>
                                                    <Option value = '2'>异常</Option>
                                                </Select>
                                        </div>
                                    </div> 
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">下单时间</label>
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
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>

                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive" style={{ positon: 'relative' }}>
                                    <Tabs onChange={this.handleTabChange} size='small'>
                                        <TabPane tab={<Badge count={unfilledSum} overflowCount={99} offset={[18, 0]} showZero>未成交</Badge>} key="1"></TabPane>
                                        <TabPane tab={<Badge count={failureSum} overflowCount={99} offset={[18, 0]} showZero>下单失败</Badge>} key="2"></TabPane>
                                        <TabPane tab="交易成功" key="3"></TabPane>

                                    </Tabs>
                                    <div style={{ marginBottom: 16, position: 'absolute', right: 10, top: 0 }}>
                                        <Button type="primary" style={tabKey == '2' ? { display: 'block' } : { display: 'none' }} onClick={this.start} disabled={!hasSelected} loading={loading}>
                                            批量标记
                                            </Button>
                                    </div>
                                    <Table rowSelection={tabKey == '1' || tabKey == '3' ? null : rowSelection} dataSource={tableSource} bordered pagination={{ ...pagination, current: pageIndex }} locale={{ emptyText: '暂无数据' }}
                                        onChange={this.sorter}
                                    >
                                        {tabKey == '1' || tabKey == '3' ?
                                            [<Column title='序号' dataIndex='index' key='index' />,
                                            <Column title='保值交易ID' dataIndex='entrustId' key='entrustId' />,
                                            <Column title='三方保值平台' dataIndex='entrustPlatform' key='entrustPlatform' />,
                                            <Column title='交易市场' dataIndex='entrustMarket' key='entrustMarket' />,
                                            <Column title='保值交易类型' dataIndex='entrustType' key='entrustType' render={text => entrustTypes[text]} />,
                                            <Column title='保值委托价格' className='moneyGreen' dataIndex='entrustPrice' key='entrustPrice' render={text => toThousands(text, true)} />,
                                            <Column title='保值委托数量' dataIndex='entrustNum' key='entrustNum' />,
                                            <Column title='保值成交数量' dataIndex='executedAmount' key='executedAmount' />,
                                            <Column title='保值剩余数量' dataIndex='remainingAmount' key='remainingAmount' />,
                                            <Column title='保值下单时间' dataIndex='addTime' key='addTime' render={parameter => parameter ? moment(parameter).format(TIMEFORMAT_ss) : '--'} />,
                                            <Column title='保值状态' dataIndex='entrustStatusStr' key='entrustStatusStr' />,
                                            tabKey == '1' && <Column title='操作' dataIndex='op' key='op' render={(text, record) => {
                                                switch (record.entrustStatus) {
                                                    case 1:
                                                        return <a href="javascript:void(0)" onClick={() => this.cancelLations(record.id, "1")}>撤单</a>
                                                    case 5:
                                                        return <a href="javascript:void(0)" onClick={() => this.closePosition(record.id, "2")}>平仓</a>
                                                    default:
                                                        break;
                                                }
                                            }} />]
                                            :
                                            [<Column title='序号' dataIndex='index' key='index' />,
                                            <Column title='交易市场' dataIndex='entrustMarket' key='entrustMarket' />,
                                            <Column title='三方平台' dataIndex='entrustPlatform' key='entrustPlatform' />,
                                            <Column title='交易ID' dataIndex='entrustFromId' key='entrustFromId' />,
                                            <Column title='交易类型' dataIndex='entrustType' key='entrustType' render={text => entrustTypes[text]} />,
                                            <Column title='保值委托价格' className='moneyGreen' dataIndex='entrustPrice' key='entrustPrice' render={text => toThousands(text, true)} />,
                                            <Column title='保值委托数量' dataIndex='entrustNumbers' key='entrustNumbers' />,
                                            <Column title='下单失败原因' dataIndex='errorMessage' key='errorMessage' />,
                                            <Column title='下单失败时间' dataIndex='dateTime' key='dateTime' render={parameter => parameter ? moment(parameter).format(TIMEFORMAT_ss) : '--'} />,
                                            <Column title='操作' dataIndex='op' key='opp' render={(text, record) => <a href="javascript:void(0)" onClick={() => this.failMark(record.id)}>标记</a>} />,
                                            ]
                                        }
                                    </Table>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={check}
                    handleInputChange={this.handleInputChange}
                    mid='CAE'
                    visible={googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate} />
                <Modal
                    visible={visible}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={null}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
    componentDidMount() {
        this.requestTable()
        this.requestPrompt('1', { Etime: '', Stime: '', market: '', type: '' })
        this.requestPrompt('2', { Etime: '', Stime: '', market: '', type: '' })
    }
    componentDidUpdate() {

    }
    //失败标记
    failMark = id => {
        let self = this
        Modal.confirm({
            title: `确定要标记吗？`,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + "/coinQtHedgingabnormal/update", qs.stringify({
                    id
                })).then(res => {
                    const result = res.data
                    if (result.code == 0) {
                        self.requestTable()
                    } else {
                        message.error(result.msg);
                    }
                })
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    // 批量标记
    start = () => {
        this.setState({ loading: true });
        const { selectedRowKeys } = this.state;
        axios.post(DOMAIN_VIP + '/coinQtHedgingabnormal/updateBatch', qs.stringify({
            ids:selectedRowKeys.join(',').trim()
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                setTimeout(() => {
                    this.setState({
                        selectedRowKeys: [],
                        loading: false,
                    });
                }, 300);
                this.requestTable()
                message.success('批量标记成功')
            } else {
                message.error(result.msg);
            }
        })

    };

    onSelectChange = selectedRowKeys => {
        this.setState({ selectedRowKeys });
    };





    //撤单
    onCancelLations = (id) => {
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
    cancelLations = (id, type) => {
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
    onClosePosition = (id) => {
        axios.post(DOMAIN_VIP + "/brush/hedge/order/closePosition", qs.stringify({
            id
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                message.success(result.msg);
                self.requestTable()
            } else {
                message.error(result.msg);
            }
        })
    }
    //平仓弹窗
    closePosition = (id, type) => {
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
    onChangeCheckTime = (date, dateString) => {
        // console.log(date,dateString)
        this.setState({
            Stime: dateString[0],
            Etime: dateString[1],
            time: date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex, pageSize, pagination, market, type, Etime, Stime, tabKey } = this.state
        let url;
        let payload = {
            market, type,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize,
        }
        switch (tabKey) {
            case '1':
                payload = Object.assign({}, payload, { Stime, Etime });
                url = '/coinQtHedgingabnormal/list';
                break;
            case '2':
                payload = Object.assign({}, payload, {
                    LStime: Stime ? moment(Stime).format('x') : '',
                    LEtime: Etime ? moment(Etime).format('x') : '',
                });
                url = '/coinQtHedgingabnormal/query';
                break;
            case '3':
                payload = Object.assign({}, payload, { Stime, Etime });
                url = '/coinQtHedgingabnormal/successful';
                break;
            default:
                break
        }
        axios.post(DOMAIN_VIP + url, qs.stringify(payload)).then(res => {
            const result = res.data;
            // console.log(result)
            if (result.code == 0) {
                let tableSource = result.data.list;
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource: tableSource,
                    pagination,
                })
            } else {
                message.warning(result.msg);
            }
        })
        this.requestPrompt(tabKey, payload)
    }
    requestPrompt = (type, payload) => {
        let url = ''
        let newPayload = Object.assign({}, payload)
        newPayload.pageIndex && delete newPayload.pageIndex;
        newPayload.pageSize && delete newPayload.pageSize;
        switch (type) {
            case '1':
                url = '/coinQtHedgingabnormal/sum';
                break;
            case '2':
                url = '/coinQtHedgingabnormal/listSum';
                break;
            default:
                return false;
        }
        axios.post(DOMAIN_VIP + url, qs.stringify(newPayload)).then(res => {
            const result = res.data;
            // console.log(result)
            if (result.code == 0) {
                if (type == 1) {
                    this.setState({
                        unfilledSum: result.data.sum || 0
                    })
                } else {
                    this.setState({
                        failureSum: result.data.listSum || 0
                    })
                }
            } else {
                message.warning(result.msg);
            }
        })
    }
    // requestState(parameter,el){
    //     let id = parameter,self = this;
    //     Modal.confirm({
    //         title:'你确定要标记吗？',
    //         okText:'确定',
    //         okType:'more',
    //         cancelText:'取消',
    //         onOk(){
    //             axios.post(DOMAIN_VIP+'/frequent/update',qs.stringify({id,state:el})).then(res => {
    //                 const result = res.data;
    //                 if(result.code == 0){
    //                     self.requestTable()
    //                 } 
    //             })
    //         },
    //         onCancel(){
    //             console.log('Cancel')
    //         } 
    //     })
    // }
    handleChangeSolve(value) {
        this.setState({
            solveValue: value
        })
    }
    handleChangeSelect = value => {
        this.setState({
            market: value,
        })
    }
    handleTypeChange(value) {
        this.setState({
            type: value
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
    //切换面板后的动作
    handleTabChange = key => {
        this.setState({
            tabKey: key,
            pageIndex: PAGEINDEX
        }, () => {
            this.requestTable();
            // this.requestPrompt(key)
        })
    }
    //查询按钮
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => this.requestTable())
    }
    //重置按钮
    resetState = () => {
        this.setState(() => ({
            market: '',
            type: '',
            timeS: '',
            tiemE: '',
            time: []
        }), () => {
            this.requestTable()
        })

    }
    //资金类型
    selectFundsType = v => {
        this.setState({
            fundstype: v
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;

        this.setState({
            showHide: !showHide,
        })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value,
        })
    }

    handleChange(value) {
        this.setState({
            types: value
        })
    }
    handleChangeState(value) {
        this.setState({
            state: value
        })
    }
    handleCreate = () => {
        const form = this.formRef.props.form;
        form.validateFields((err, valus) => {
            if (err) {
                return;
            }
            form.resetFields();
            this.modalGoogleCodeBtn(valus)
        })
    }
    saveFormRef = formRef => {
        this.formRef = formRef
    }
    //打开弹框
    openModal() {
        this.setState({
            visible: true,
            width: '1400px',
            modalHtml: <DetailModal {...this.state} />
        })
    }
    //弹框关闭
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }
    //google 验证弹窗
    modalGoogleCode = (item, type) => {

        this.setState({
            googVisibal: true,
            googleSpace: item,
            googletype: type
        })
    }
    //google 按钮
    modalGoogleCodeBtn(value) {
        const { googletype, googleSpace } = this.state
        const { googleCode } = value
        axios.post(DOMAIN_VIP + "/common/checkGoogleCode", qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                if (googletype == "1") {
                    this.setState({
                        googVisibal: false
                    }, () => this.onCancelLations(googleSpace))

                } else if (googletype == "2") {
                    this.setState({
                        googVisibal: false
                    }, () => this.onClosePosition(googleSpace))
                }
            } else {
                message.warning(result.msg)
            }
        })
    }
    commonCheckModal = (item, type) => {
        let self = this, mtitle;
        if (item === 1) {
            mtitle = '您确定要限制该帐户体现功能吗？'
        } else if (item === 2) {
            mtitle = '您确定要冻结该帐户吗？'
        } else if (item === 3) {
            mtitle = '您确定要冻结该帐户吗？'
        }
        Modal.confirm({
            title: mtitle,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(item, type);

            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //google弹窗关闭
    onhandleCancel = () => {
        this.setState({
            googVisibal: false
        })
    }
    sorter = (pagination, filters, sorter) => {
        console.log(sorter)
    }

}
export default ValueOfWarning