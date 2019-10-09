import React, { Component } from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, DatePicker, Tabs, Pagination, Select, message, Table, Modal } from 'antd'
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT, PAGRSIZE_OPTIONS20, DAYFORMAT } from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import MarketRequests from '../../common/select/marketrequests'
import { toThousands, pageLimit } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList';
import PlatformModal from '../components/platform'
import SelectStateList from '../../common/select/selectStateList'
//保值记录异常
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { Column } = Table
class PrewarningValue extends Component {
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
            title: '',
            tableSource: [],
            time: [],
            entrustmarket: '',
            userid: '',
            hedgingnumbersS: '',
            hedgingnumbersE: '',
            addtimeS: '',
            addtimeE: '',
            solveValue: '0',
            RowKeys: [],
            limitBtn: [],
        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChangeState = this.handleChangeState.bind(this)
        this.selectFundsType = this.selectFundsType.bind(this)
        this.handleChangeSolve = this.handleChangeSolve.bind(this)
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render() {
        const { time, pageSize, showHide, tableSource, pagination, googVisibal, pageIndex, check, entrustmarket, modalHtml, visible, width, userid, hedgingnumbersS, hedgingnumbersE, title, solveValue, RowKeys, limitBtn } = this.state
        let types = ['卖出', '买入']
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 期货交易业务 > 保值下单数量异常
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <MarketRequests market={entrustmarket} underLine={true} handleChange={this.handleChangeSelect} col='3' />
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户ID：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange} width={SELECTWIDTH} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保值差额：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="hedgingnumbersS" value={hedgingnumbersS} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="hedgingnumbersE" value={hedgingnumbersE} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <SelectStateList value={solveValue} handleChange={this.handleChangeSolve} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交时间</label>
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
                                        <Button type="primary" style={{ background: '#ff4d4f', borderColor: '#ff4d4f' }} onClick={this.checkData}>一键核查</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        {limitBtn.indexOf('updateall') > -1 && <Button type="primary" disabled={!RowKeys.length} onClick={this.batchMarking}>批量标记</Button>}
                                    </div>
                                </div>

                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table dataSource={tableSource}
                                        bordered pagination={{ ...pagination, current: pageIndex }} locale={{ emptyText: '暂无数据' }}
                                        onChange={this.sorter}
                                        rowSelection={{
                                            selectedRowKeys: RowKeys,
                                            onChange: this.onSelectChange,
                                        }}
                                    >
                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='交易市场' dataIndex='entrustmarket' key='entrustmarket' />
                                        <Column title='用户ID' dataIndex='userid' key='userid' />
                                        <Column title='交易ID' dataIndex='tradingid' key='tradingid' />
                                        <Column sorter title='成交数量' dataIndex='numbers' key='numbers' />
                                        <Column title='交易类型' dataIndex='types' key='types' render={text => types[text]} />
                                        <Column title='保值成交数量' dataIndex='transactionnumbers' key='transactionnumbers' />
                                        <Column title='保值交易类型' dataIndex='hedgingtypes' key='hedgingtypes' render={text => types[text]} />
                                        <Column title='保值差额数量' dataIndex='hedgingnumbers' key='hedgingnumbers' />
                                        <Column title='解决状态' dataIndex='state' key='state' render={text => text == 1 ? '已解决' : '未解决'} />
                                        <Column title='成交时间' dataIndex='addtime' key='addtime' render={(parameter) => {
                                            return parameter ? moment(parameter).format(TIMEFORMAT_ss) : '--'
                                        }} />
                                        <Column title='操作' dataIndex='open' key='open' render={(parameter, record) =>
                                            <span>
                                                {record.state ? '' : limitBtn.indexOf('update') > -1 ? <a className='mar10' href='javascript:void(0);;' onClick={(e) => { e.preventDefault(); this.requestState(record.id) }}>标记</a> : ''}
                                                <a href='javascript:void(0);;' onClick={(e) => { e.preventDefault(); this.openModal(record.entrustfromid) }}>查看保值平台</a>
                                            </span>
                                        } />
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
                    title={title}
                    footer={null}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
    componentDidMount() {
        this.requestTable();
        this.setState({
            limitBtn: pageLimit('coinQtHedgingnumbers', this.props.permissList)
        });
    }
    componentDidUpdate() {

    }
    onChangeCheckTime = (date, dateString) => {
        // console.log(date,dateString)
        this.setState({
            addtimeS: dateString[0] ? moment(dateString[0]).format('x') : '',
            addtimeE: dateString[1] ? moment(dateString[1]).format('x') : '',
            time: date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex, pageSize, types, pagination, userid, entrustmarket, hedgingnumbersS, hedgingnumbersE, addtimeS, addtimeE, solveValue } = this.state
        axios.post(DOMAIN_VIP + '/coinQtHedgingnumbers/list', qs.stringify({
            userid, entrustmarket, hedgingnumbersS, hedgingnumbersE, addtimeS, addtimeE, state: solveValue,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize,
        })).then(res => {
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
    }
    requestState(id) {
        let self = this;
        Modal.confirm({
            title: '你确定要标记吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + '/coinQtHedgingnumbers/update', qs.stringify({ id })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.requestTable()
                    }
                })
            },
            onCancel() {
                console.log('Cancel')
            }
        })
    }
    handleChangeSolve(value) {
        this.setState({
            solveValue: value
        })
    }
    //交易市场下拉菜单
    handleChangeSelect = value => {
        this.setState({
            entrustmarket: value,
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
    //查询按钮
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => this.requestTable())
    }

    // 一键核查
    checkData = () => {
        axios.post(DOMAIN_VIP + '/coinQtHedgingnumbers/reviewall').then(res => {
            const result = res.data;
            //console.log(result)
            if (result.code == 0) {
               message.success(result.msg)
               this.requestTable()
            } else {
                message.warning(result.msg);
            }
        })
    }

    //重置按钮
    resetState = () => {
        this.setState(() => ({
            time: [],
            entrustmarket: '',
            userid: '',
            hedgingnumbersS: '',
            hedgingnumbersE: '',
            addtimeS: '',
            addtimeE: '',
            solveValue: ''
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
    openModal(entrustFromId) {
        const { pageIndex, pageSize } = this.state
        axios.post(DOMAIN_VIP + '/coinQtHedgingnumbers/findOne', qs.stringify({ entrustFromId, pageIndex, pageSize })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.list
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    title: '查看保值平台',
                    visible: true,
                    width: '800px',
                    pageSize: result.data.pageSize,
                    pageTotal: result.data.totalCount,
                    modalHtml: <PlatformModal {...this.state} tableSource={tableSource} />
                })
            }
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
            item,
            googletype: type
        })
    }
    //google按钮
    modalGoogleCodeBtn = (value) => {
        const { item, googletype } = this.state
        const { googleCode } = value
        axios.post(DOMAIN_VIP + "/common/checkGoogleCode", qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if (result.code == 0) {

                this.setState({
                    googVisibal: false
                })

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
    onSelectChange = (selectedRowKeys) => {
        this.setState({
            RowKeys: selectedRowKeys
        });
    };
    batchMarking = () => {
        let self = this;
        Modal.confirm({
            title: '你确定要批量标记选中的数据吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + '/coinQtHedgingnumbers/updateall', qs.stringify({ idlist: self.state.RowKeys.toString() })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.setState({
                            RowKeys: []
                        }, () => {
                            self.requestTable();
                        });
                    }
                })
            },
            onCancel() {
            }
        })
    }
}
export default PrewarningValue
