import React, { Component } from 'react'
import axios from '../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, DatePicker, Tabs, Pagination, Select, message, Table, Modal } from 'antd'
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT, PAGRSIZE_OPTIONS20, DAYFORMAT } from '../../conf'
import GoogleCode from '../common/modal/googleCode'
import PlatformModal from './modal/platform'
//用户查询
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Option = Select.Option;
// const TabPane = Tabs.TabPane;
const { Column } = Table
class Userquery extends Component {
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
            modalSource: [],
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
            userId: '',
            userName: '',
            email: '',
            customerType: '0',
            addtimeS: '',
            addtimeE: '',

        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render() {
        const { time, pageSize, userId, userName, email, customerType, showHide, tableSource, pagination, googVisibal, pageIndex, check, entrustmarket, modalHtml, visible, width, hedgingnumbersS, hedgingnumbersE, title, solveValue } = this.state
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：市场管理 > 用户查询 > 用户查询
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} width={SELECTWIDTH} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} width={SELECTWIDTH} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">邮箱账号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="email" value={email} onChange={this.handleInputChange} width={SELECTWIDTH} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={customerType} style={{ width: SELECTWIDTH }} onChange={this.handleChangeSelect}>
                                                <Option value="0">请选择</Option>
                                                <Option value="04">公司账户-融资融币</Option>
                                                <Option value="05">公司账户-测试用户</Option>
                                                <Option value="07">公司账户-量化交易</Option>
                                                <Option value="01">普通用户</Option>
                                                <Option value="06">其他用户</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">注册时间</label>
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
                                <div className="table-responsive">
                                    <Table dataSource={tableSource} bordered pagination={{ ...pagination, current: pageIndex }} locale={{ emptyText: '暂无数据' }}
                                        onChange={this.sorter}
                                    >
                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='用户名' dataIndex='userName' key='userName' />
                                        <Column title='用户编号' dataIndex='id' key='id' />
                                        <Column title='绑定手机号' dataIndex='userContact.safeMobile' key='userContact.safeMobile' />
                                        <Column title='邮箱' dataIndex='email' key='email' />
                                        <Column title='用户类型' dataIndex='customerTypeName' key='customerTypeName' />
                                        <Column title='用户等级' dataIndex='vipRate' key='vipRate' />
                                        <Column title='最近登录时间' dataIndex='lastLoginTime' key='lastLoginTime' render={(parameter) => {
                                            return parameter ? moment(parameter).format(TIMEFORMAT_ss) : '--'
                                        }} />
                                        <Column title='登录IP' dataIndex='loginIp' key='loginIp' render={(parameter, record) => {
                                            return (
                                                <span><a href='javascript:void(0);' onClick={() => { this.openModal(record.id) }}>{parameter}</a></span>
                                            )
                                        }} />
                                        <Column title='注册时间' dataIndex='registerTime' key='registerTime' render={(parameter) => {
                                            return parameter ? moment(parameter).format(TIMEFORMAT_ss) : '--'
                                        }} />
                                        <Column title='语言' dataIndex='languageName' key='languageName' />
                                        <Column title='客户端' dataIndex='' key='' />
                                        <Column title='版本' dataIndex='' key='' />
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
        this.requestTable()
    }
    onChangeCheckTime = (date, dateString) => {
        // console.log(date,dateString)
        this.setState({
            addtimeS: dateString[0],
            addtimeE: dateString[1],
            time: date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize, parameter) => {
        const { pageIndex, pageSize, pagination, userId, userName, addtimeE, addtimeS, email, customerType } = this.state
        axios.post(DOMAIN_VIP + '/userInfo/queryUserList', qs.stringify({
            userId, userName, stime: addtimeS, etime: addtimeE, email, customerType,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize,

        })).then(res => {
            const result = res.data;
            // console.log(result)
            if (result.code == 0) {
                let tableSource = result.page.list;
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.page.currPage - 1) * result.page.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.page.totalCount;
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


    //用户类型下拉菜单
    handleChangeSelect = value => {
        this.setState({
            customerType: value,
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
        this.setState(() => ({
            userId: '',
            userName: '',
            email: '',
            customerType: '0',
            addtimeS: '',
            addtimeE: '',
            time: []

        }), () => {
            this.requestTable()
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
    openModal(parameter) {
        const { pageIndex, pageSize, } = this.state

        this.setState(() => ({
            visible: true,
            title: '登录历史',
            width: '1200px',
            modalHtml: <PlatformModal  userId={parameter}  />

        }))



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
export default Userquery
