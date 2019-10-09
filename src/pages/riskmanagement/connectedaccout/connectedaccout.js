import React, { Component } from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, DatePicker, Tabs, Pagination, Select, message, Table, Modal } from 'antd'
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT, PAGRSIZE_OPTIONS20 } from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import StateSelect from '../modal/stateselect'
import { pageLimit } from '../../../utils'


const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { Column } = Table
class Connectedaccout extends Component {
    constructor(props) {
        super(props);
        this.state = {
            googVisibal: false,
            visible: false,
            isreLoad: false,
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            show: '查看',
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
            moneyMin: '',
            moneyMax: '',
            time: [],
            starttime: "",
            endtime: "",
            ip: '',
            userid: '',
            type: '',
            address: '',
            state: '0',
            RowKeys: [],
            limitBtn:[]
        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleChangeSelect = this.handleChangeSelect.bind(this)
        this.handleChangeState = this.handleChangeState.bind(this)
        // this.handleChangeType = this.handleChangeType.bind(this);
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    componentDidMount() {
        this.setState ({
            limitBtn : pageLimit('linkedaccount',this.props.permissList)
        })
        this.requestTable()
    }
    componentDidUpdate() {

    }

    //请求数据
    requestTable = (currentIndex, currentSize) => {

        const { pageIndex, pageSize, pagination, type, ip, address, userid, starttime, endtime, state } = this.state
        axios.post(DOMAIN_VIP + '/linkedaccount/list', qs.stringify({
            type, ip, address, userid, starttime, endtime, state,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize,

        })).then(res => {
            const result = res.data;
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
    //下拉菜单请求数据
    requestState(parameter, el) {
        let id = parameter, self = this;
        Modal.confirm({
            title: '你确定要标记吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + '/linkedaccount/update', qs.stringify({ id, state: el })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.requestTable()
                    }else{
                        message.warning(result.msg)
                    }
                })
            },
            onCancel() {
                console.log('Cancel')
            }
        })
    }

    handleChangeState(value) {
        this.setState({
            state: value
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
    //重置按钮
    resetState = () => {

        this.setState({
            tableSource: [],
            moneyMin: '',
            moneyMax: '',
            time: [],
            starttime: "",
            endtime: "",
            ip: '',
            userid: '',
            type: '',
            address: '',
            state: '0'
        }, () => { this.requestTable() })

    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    handleChangeSelect(value) {
        this.setState({
            type: value
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide,
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
    onChangeCheckTime(date, dateString) {
        this.setState({
            starttime: dateString[0] ? moment(dateString[0]).format('x') : '',
            endtime: dateString[1] ? moment(dateString[1]).format('x') : '',
            time: date
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

                this.requestState()
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
            mtitle = '您确定要冻结该帐户吗？'

        }
        if (item === 2) {
            mtitle = '您确定要限制该帐户提现功能吗？'

        }
        if (item === 3) {
            mtitle = '您确定要冻结该帐户吗'

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
    };
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
                axios.post(DOMAIN_VIP + '/linkedaccount/updateall', qs.stringify({idlist:self.state.RowKeys.toString()})).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.setState({
                            RowKeys: []
                        }, () => {
                            self.requestTable();
                        });
                    }else{
                        message.warning(result.msg)
                    }
                })
            },
            onCancel() {
            }
        })
    }
    render() {
        const { time, pageSize, showHide, tableSource, pagination, googVisibal, check, type, ip, address, userid, pageIndex, RowKeys,limitBtn } = this.state
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 币币交易业务 > 关联账户
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">关联类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={type} style={{ width: 200 }} onChange={this.handleChangeSelect}>
                                                <Option value=''>请选择</Option>
                                                <Option value='1'>IP一致</Option>
                                                <Option value='2'>充值一致</Option>
                                                <Option value='3'>提现一致</Option>
                                                {/* <Option value='4'>密码一致</Option> */}
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">IP地址：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='ip' value={ip} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>


                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">充提地址：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='address' value={address} onChange={this.handleInputChange} /><b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">关联用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='userid' value={userid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <StateSelect value={this.state.state} changeState={(val) => this.handleChangeState(val)} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">报警时间</label>
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
                                        {limitBtn.indexOf('updateall') > -1 &&<Button type="primary" disabled={!RowKeys.length} onClick={this.batchMarking}>批量标记</Button>}
                                    </div>
                                </div>

                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table dataSource={tableSource} bordered pagination={{ ...pagination, current: pageIndex }} locale={{ emptyText: '暂无数据' }}
                                        onChange={this.sorter}
                                        rowSelection={{
                                            selectedRowKeys: RowKeys,
                                            onChange: this.onSelectChange,
                                        }}
                                    >
                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='关联用户编号' dataIndex='userid' key='userid' />
                                        <Column title='关联类型' dataIndex='typename' key='typename' />
                                        <Column title='IP地址' dataIndex='ip' key='ip' />
                                        <Column title='充值地址' dataIndex='rechargeaddress' key='rechargeaddress' />
                                        <Column title='提现地址' dataIndex='withdrawaladdress' key='withdrawaladdress' />
                                        <Column title='报警时间' dataIndex='occurtime' key='occurtime' render={(parameter) => {
                                            return parameter ? moment(parameter).format(TIMEFORMAT_ss) : '--'
                                        }} />
                                        <Column title='状态' dataIndex='state' key='state' render={text => text == 1 ? '已解决' : '未解决'} />
                                        <Column title='详情' dataIndex='id' key='id' render={(parameter, record) => {
                                            return (
                                                <div>
                                                    {limitBtn.indexOf('update') > -1 && record.state == 0 ? <a href='javascript:void(0);' onClick={() => this.requestState(parameter, 1)}>标记</a> : ''}
                                                </div>
                                            )
                                        }} />
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
            </div>
        )
    }
}
export default Connectedaccout
