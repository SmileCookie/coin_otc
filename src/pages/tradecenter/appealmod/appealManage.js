import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import CommonTable from '../../common/table/commonTable'
import { pageLimit, tableScroll, saveToSS, getFromSS, removeFromSS ,splitArr} from '../../../utils'
import ModalAppealdetail from './modal/modalAppealdetail'
import ModalTip from './modal/modalTip'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, PAGRSIZE_OPTIONS20, TIMEFORMAT_ss } from '../../../conf'
import { Button, Pagination, Select, Modal, message, Tabs, DatePicker } from 'antd'
const Option = Select.Option;
const { RangePicker } = DatePicker
const TabPane = Tabs.TabPane;

export default class AppealManage extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            recordNo: '',
            tabHeader: 0,
            status: '',
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: 0,
            tableList: [],
            visible: false,
            modalHtml: '',
            title: '',
            width: '',
            limitBtn: [],
            height: 0,
            tableScroll: {
                tableId: 'APALMANE',
                x_panelId: 'APALMANEX',
                defaultHeight: 500,
            },
            tk: '0',
            time: []

        }
        this.clickHide = this.clickHide.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleChangeStatus = this.handleChangeStatus.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.showDetail = this.showDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.onConfirmCancel = this.onConfirmCancel.bind(this)
        this.newOpen = this.newOpen.bind(this)
        this.sendListen = this.sendListen.bind(this)
        this.getHeight = this.getHeight.bind(this)
        this.onAccept = this.onAccept.bind(this)

    }
    componentDidMount() {
        this.sendListen()
        this.setState({
            limitBtn: pageLimit('otcComplain', this.props.permissList),
            appActiveKey: this.props.appActiveKey,
        })
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
    componentWillReceiveProps(nextProps) {
        if (nextProps.appActiveKey == this.state.appActiveKey) {
            this.sendListen()
        }
    }
    sendListen = async () => {
        let _key = splitArr(getFromSS() || '',0,'-')
        let _value = splitArr(getFromSS() || '',1,'-')
        await this.setState({
            [_key]:_value
        })
        await this.requestTable()
        // setTimeout(() => { removeFromSS()},0)
        removeFromSS()
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
    showDetail(item, id, complainId) {
        const { limitBtn } = this.state
        let maxWidth = window.screen.width > 1500 ? "1400px" : '1000px'
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        this.setState({
            visible: true,
            title: "申诉详情",
            width: maxWidth,
            modalHtml: <ModalAppealdetail id={id} update={this.requestTable} handleCancel={this.handleCancel} item={item} complainId={complainId} limitBtn={limitBtn} newOpen={this.newOpen} />
        });
    }



    newOpen(orderNo,_key,url) {
        this.setState({
            visible: false
        })
        saveToSS(_key + '-' + orderNo)
        this.props._this.add(url)

    }
    //提醒用户
    onConfirmCancel(item) {
        const { limitBtn } = this.state
        let maxWidth = window.screen.width > 1500 ? "600px" : '400px';
        this.footer = null
        this.setState({
            visible: true,
            title: "提醒用户",
            width: maxWidth,
            modalHtml: <ModalTip item={item} handleCancel={this.handleCancel} />
        })
    }
    onSubmitData = (e) => {
        this.ModalTip.onSubmit();
    }

    //受理申诉
    onAccept(id) {
        let self = this;
        Modal.confirm({
            title: '您确定要受理该申诉吗',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                return new Promise((resolve, reject) => {
                    axios.get(DOMAIN_VIP + '/otcComplain/dealWith', {
                        params: {
                            id
                        }
                    }).then(res => {
                        const result = res.data;
                        if (result.code == 0) {
                            message.success(result.msg)
                            self.requestTable()
                            self.setState({
                                visible: false
                            })
                            resolve(result.msg)
                        } else {
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
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
    requestTable(currIndex, currSize) {
        const { recordNo, status, pageIndex, pageSize, tk, time } = this.state
        axios.post(DOMAIN_VIP + '/otcComplain/list', qs.stringify({
            recordNo, status: tk,
            createtimeS: time[0] ? moment(time[0]).format(TIMEFORMAT_ss) : '',
            createtimeE: time[1] ? moment(time[1]).format(TIMEFORMAT_ss) : '',
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    tableList: result.data.list,
                    pageTotal: result.data.totalCount,
                    pageSize: currSize || pageSize,
                    pageIndex: currIndex || pageIndex

                })

            } else {
                message.error(result.msg)
            }
        })
    }





    onResetState() {
        this.setState({
            recordNo: '',
            status: '',
            time: [],
            recordNo: '',
            createtimeS: '',
            createtimeE: ''
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
    tcb = async tk => {
        await this.setState({
            tk,
            tabHeader: tk
        })
        this.requestTable()


    }
    tipUser = item => {

    }

    createColumns = (pageIndex, pageSize) => {
        const { tabHeader,limitBtn } = this.state;
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '申诉编号', dataIndex: 'id', render: (text, record, index) => record.id },
            { title: '申诉发起时间', className: tabHeader == 0 ? '' : 'hide', dataIndex: 'addTime', render: (text, record, index) => { return moment(text).format(TIMEFORMAT) } },
            { title: '申诉受理时间', className: tabHeader == 1 ? '' : 'hide', dataIndex: 'complainTime', render: text => moment(text).format(TIMEFORMAT) },
            { title: '审核时间', className: tabHeader == 2 ? '' : 'hide', dataIndex: 'checkTime', render: text => moment(text).format(TIMEFORMAT) },
            { title: '订单编号', dataIndex: 'recordNo',render:t => <a href="javascript:void(0)" onClick={() => this.newOpen(t,'recordNo', {name: "订单管理", url: "/tradecenter/ordermod/orderManage", key: 700100010062})}>{t}</a> },
            { title: '交易金额', dataIndex: 'sumAmount' },
            { title: '买方', dataIndex: 'buyStatus' },
            { title: '卖方', dataIndex: 'sellStatus' },
            { title: '申诉等待时长', className: tabHeader == 0 ? '' : 'hide', dataIndex: 'timeLong' },
            { title: '申诉状态', dataIndex: 'statusName' },
            { title: '判定结果', className: tabHeader == 2 ? '' : 'hide', dataIndex: 'checkResultName' },
            { title: '判定人', className: tabHeader == 2 ? '' : 'hide', dataIndex: 'checkUserName' },
            {
                title: '操作', dataIndex: 'option', render: (text, record) => (

                    <div>
                        {record.status == '3' ? (limitBtn.indexOf('dealWith') > -1 ? <a href="javascript:void(0)" className='mar20' onClick={() => this.onAccept(record.id)}>受理</a> : '') : ''}
                        {record.status == '1' || record.status == '0' || record.status == '3' ? <a href="javascript:void(0)" className='mar20' onClick={() => this.onConfirmCancel(record)}>提醒</a> : ''}
                        {limitBtn.indexOf('complainList') > -1 ? <a href="javascript:void(0)" onClick={() => this.showDetail(record, record.recordNo, record.id)}>{record.status == '0' || record.status == '3' ? '查看' : record.status == '1' ? '审核' : '查看详情'}</a> : ''}
                    </div>

                ),

            }
        ]
    }





    render() {
        const { showHide, recordNo, pageIndex, pageSize, pageTotal, tableList, visible, title, modalHtml, width,  time } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > OTC交易中心 > 申诉管理 > 申诉管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
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
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">订单编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="recordNo" value={recordNo} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>

                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">申诉状态：</label>
                                        <div className="col-sm-9">
                                        <Select value={status}  style={{ width: SELECTWIDTH }} onChange={this.handleChangeStatus} >
                                            <Option value=''>请选择</Option>
                                            <Option value='1'>已受理</Option>
                                            <Option value='0'>未受理</Option>
                                            <Option value='2'>已解决</Option>
                                        </Select>
                                        </div>
                                    </div>
                                </div> */}

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
                                <Tabs onChange={this.tcb} defaultActiveKey='0' activeKey={this.state.tk}>
                                    <TabPane tab='未受理' key='0'></TabPane>
                                    <TabPane tab='已受理' key='1'></TabPane>
                                    <TabPane tab='已解决' key='2'></TabPane>
                                </Tabs>
                                <div className="table-responsive">
                                    <CommonTable
                                        dataSource={tableList}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSize: pageSize,
                                                current: pageIndex

                                            }
                                        }
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}

                                    />
                                </div>
                            </div>

                        </div>

                    </div>
                </div>

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