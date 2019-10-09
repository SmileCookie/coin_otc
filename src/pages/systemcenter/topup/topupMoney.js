import React, { Component } from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, DatePicker, Tabs, Pagination, Select, message, Table, Modal } from 'antd'
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT, PAGRSIZE_OPTIONS20, URLS } from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import FundsTypeList from '../../common/select/fundsTypeList';
const { COMMON_QUERYATTRUSDTE } = URLS
//资金低于预警值
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { Column } = Table
class TopupMoney extends Component {
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
            fundstype: '0',
            allOpration: false, // 标识是否是批量操作
        }
        this.selectFundsType = this.selectFundsType.bind(this)
        this.handleOperation = this.handleOperation.bind(this)
        this.clickHide = this.clickHide.bind(this)
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    /**
     * @param    {[type]}   urlType    [description]
     * @param    {[type]}   management [0: 充值， 1: 提现，2: 展示]
     * @param    {[type]}   operation  [0: 关闭， 1: 打开]
     */
    handleOperation = (urlType, management, operation) => {
        let params = { management, operation }
        let msgTag = management === 0 ? '充值' : management === 1 ? '提现' : '展示'
        let self = this;
        Modal.confirm({
            title: `你确定要${operation === 0 ? `关闭${msgTag}` : `打开${msgTag}`}？`,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCodeAllOperation(urlType, management, operation)
            },
            onCancel() {
                console.log('Cancel')
            }
        })
        
    }
    handleAllOperation() {
        const { urlType, management, operation } = this.state
        let params = { urlType, management, operation }
        axios.post(DOMAIN_VIP + "/chargeManagement/" + urlType, qs.stringify(params)).then(res => {
            console.log(res)
            const result = res.data
            if (result.code == 0) {
                this.requestTable()
            } else {
                message.warning(result.msg)
            }
        })
    }
    modalGoogleCodeAllOperation(urlType, management, operation) {
        this.setState({
            googVisibal: true,
            urlType, management, operation,
            allOpration: true,
        })
    }
    render() {
        const { time, pageSize,pageTotal, showHide, tableSource, pagination, googVisibal, fundstype, pageIndex, state, check, accountname, platform } = this.state
        let status = ['关', '开']
        // let displays = ['开','关']
        const tsl = {
            padding:'10px 16px'
        }
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：系统中心 > 系统管理 > 充值提现管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList title='资金类型' url={COMMON_QUERYATTRUSDTE} fundsType={fundstype} handleChange={this.selectFundsType} />
                                </div>
                                <div className="col-md-8 col-sm-8 col-xs-8 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        <Button type="primary" onClick={() => {this.handleOperation('updateAllCharge', 0, 0)}}>关闭充值</Button>
                                        <Button type="primary" onClick={() => {this.handleOperation('updateAllCharge', 0, 1)}}>打开充值</Button>
                                        <Button type="primary" onClick={() => {this.handleOperation('updateAllWithdraw', 1, 0)}}>关闭提现</Button>
                                        <Button type="primary" onClick={() => {this.handleOperation('updateAllWithdraw', 1, 1)}}>打开提现</Button>
                                        <Button type="primary" onClick={() => {this.handleOperation('updateAllDisplay', 2, 0)}}>关闭展示</Button>
                                        <Button type="primary" onClick={() => {this.handleOperation('updateAllDisplay', 2, 1)}}>打开展示</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive ">
                                    <Table dataSource={tableSource}
                                        bordered
                                        pagination={{ ...pagination, current: pageIndex }}
                                        locale={{ emptyText: '暂无数据' }}
                                        onChange={this.sorter}
                                        rowClassName={(record,index) => {
                                            // console.log(record);
                                            // console.log(index,record.recharge == 0,record.withdraw == 0)
                                            // return (record.recharge == 0 || record.withdraw == 0) ? 'color-red' : 'color-red'
                                        }}
                                    >
                                        <Column title={<div style={tsl}>序号</div>} className='pd0-mg0' dataIndex='index' key='index' render={(text,record) => {
                                            return <div style={tsl} className={this.isTrue(record.recharge,record.withdraw)}>{text}</div>
                                        }}/>
                                        <Column title={<div style={tsl}>资金类型</div>} className='pd0-mg0'  dataIndex='fundstypename' key='fundstypename' render={(text,record) => {
                                            return <div style={tsl} className={this.isTrue(record.recharge,record.withdraw)}>{record.fundstypename}</div>
                                        }} />
                                        <Column title={<div style={tsl}>充值</div>} className='pd0-mg0' dataIndex='recharge' key='recharge'  render={(text,record) => {
                                            return <div style={tsl} className={this.isTrue(record.recharge,record.withdraw)}>{status[text]}</div>
                                        }} />
                                        <Column title={<div style={tsl}>提现</div>} className='pd0-mg0' dataIndex='withdraw' key='withdraw'  render={(text,record) => {
                                            return <div style={tsl} className={this.isTrue(record.recharge,record.withdraw)}>{status[text]}</div>
                                        }} />
                                        <Column title={<div style={tsl}>展示</div>} className='pd0-mg0' dataIndex='display' key='display'  render={(text,record) => {
                                            return <div style={tsl} className={this.isTrue(record.recharge,record.withdraw)}>{status[text]}</div>
                                        }} />
                                        <Column title={<div style={tsl}>操作</div>} className='pd0-mg0' dataIndex='id' key='id' render={(id, obj) => {
                                            return (
                                                <div style={tsl} className={this.isTrue(obj.recharge,obj.withdraw)}>
                                                    {
                                                        obj.recharge == 0 ? <a href='javascript=(0);' onClick={(e) => { e.preventDefault(); this.requestState(id, 1, 0, obj) }} className="mar10">打开充值</a>
                                                            :
                                                            <a href='javascript=(0);' onClick={(e) => { e.preventDefault(); this.requestState(id, 0, 0, obj) }} className="mar10">关闭充值</a>
                                                    }
                                                    {
                                                        obj.withdraw == 0 ? <a className="mar10" href='javascript=(0);' onClick={(e) => { e.preventDefault(); this.requestState(id, 1, 1, obj) }} >打开提现</a>
                                                            :
                                                            <a className="mar10" href='javascript=(0);' onClick={(e) => { e.preventDefault(); this.requestState(id, 0, 1, obj) }} >关闭提现</a>
                                                    }
                                                    {
                                                        obj.display == 0 ? <a href='javascript=(0);' onClick={(e) => { e.preventDefault(); this.requestState(id, 1, 2, obj) }} className="mar10">打开展示</a>
                                                            :
                                                            <a href='javascript=(0);' onClick={(e) => { e.preventDefault(); this.requestState(id, 0, 2, obj) }} className="mar10">关闭展示</a>
                                                    }
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
                    mid='TOPMY'
                    visible={googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate} />
            </div>
        )
    }
    componentDidMount() {
        this.requestTable()
    }
    componentDidUpdate() {

    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {

        const { pageIndex, pageSize, pagination, fundstype } = this.state
        axios.post(DOMAIN_VIP + '/chargeManagement/list', qs.stringify({
            fundstype,
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
                // pagination.total = result.data.totalCount;
                // pagination.onChange = this.onChangePageNum;
                // pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource: tableSource,
                    // pagination,
                    pageTotal:result.data.totalCount
                })
            } else {
                message.warning(result.msg);
            }
        })
    }
    requestState(id,type, management, obj) {
        let msgTag = management === 0 ? '充值' : management === 1 ? '提现' : '展示'
        let self = this;
        Modal.confirm({
            title: `你确定要${type === 0 ? `关闭${msgTag}` : `打开${msgTag}`}？`,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(obj, type, management)
                // axios.post(DOMAIN_VIP+'/chargeManagement/update',qs.stringify({id,management,operation:type,fundstype:obj.fundstype})).then(res => {
                //     const result = res.data;
                //     if(result.code == 0){
                //         self.requestTable()
                //     } 
                // })
            },
            onCancel() {
                console.log('Cancel')
            }
        })
    }

    requestStateBtn = (obj, type, management) => {
        axios.post(DOMAIN_VIP + '/chargeManagement/update', qs.stringify({ id: obj.id, management, operation: type, fundstype: obj.fundstype })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.requestTable()
            }
        })
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value,
        })
    }
    isTrue = (a,b) => {
        return a == 0 || b == 0  ? 'color-red': '';
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
            fundstype: '0',
        }), () => {
            this.requestTable()
        })

    }
    onChangePageNum = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
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
    //google 验证弹窗
    modalGoogleCode = (item, type, management) => {
        this.setState({
            googVisibal: true,
            googleItem: item,
            googletype: type,
            management,
            allOpration: false,
        })
    }
    //google按钮
    modalGoogleCodeBtn = (value) => {
        const { googleItem, googletype, management, allOpration } = this.state
        const { googleCode } = value
        axios.post(DOMAIN_VIP + "/common/checkGoogleCode", qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                if (allOpration) {
                    this.handleAllOperation() 
                }else {
                    this.requestStateBtn(googleItem, googletype, management)
                }
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
export default TopupMoney