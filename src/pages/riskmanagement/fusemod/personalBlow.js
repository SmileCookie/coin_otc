import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, Select, message, DatePicker, Table, Modal } from "antd"
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, PAGRSIZE_OPTIONS20 } from '../../../conf'
import { toThousands } from '../../../utils'
import GoogleCode from '../../common/modal/googleCode'
import BlowRemarksModal from './modal/blowRemarksModal'
import SwitchModal from './modal/switchModal'
import AssociatMemoModal from './modal/associatMemoModal'
const { RangePicker } = DatePicker
const { Column } = Table
const Option = Select.Option;

export default class PersonalBlow extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            time: [],
            visible: false,
            googVisibal: false,
            check: 'check',
            googleSpace: {},
            googleType: '',
            modalHtml: '',
            title: '',
            width: '',
            tableSource: [],
            userId: '',
            fusetype: '',
            userStatus: '',
            fuseTimeStart: '',
            fuseTimeEnd: '',
            customerOperation: '',

            modifyTextArea: '',
            appActiveKey:''
        }
    }
    componentDidMount() {
        this.requestTable()
        this.setState({
            appActiveKey:this.props.appActiveKey,
        })
    }
    componentWillReceiveProps(nextProps) {
        // console.log(this.state.appActiveKey ,'++++++',nextProps.appActiveKey)
        if( nextProps.appActiveKey == this.state.appActiveKey ){
            // console.log(111)
            this.requestTable()
        }
    }
    componentWillUnmount() {

    }
    clickHide = () => {
        this.setState({
            showHide: !this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => this.requestTable())
    }
    resetState = () => {
        this.setState({
            time: [],
            userId: '',
            fusetype: '',
            userStatus: '',
            fuseTimeStart: '',
            fuseTimeEnd: ''
        })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        })
    }
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex, pageSize, userId, fusetype, userStatus, fuseTimeStart, fuseTimeEnd, } = this.state
        axios.post(DOMAIN_VIP + '/coinOwnFuse/queryList', qs.stringify({
            userid: userId, fusetype, userStatus, fuseTimeStart, fuseTimeEnd,
            // pageIndex: currentIndex || pageIndex,
            // pageSize: currentSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data;
                for (let i = 0; i < tableSource.length; i++) {
                    // tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].index = i + 1
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    tableSource: tableSource,
                    pageTotal: result.data.totalCount
                })
            } else {
                message.warning(result.msg);
            }
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
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            fuseTimeStart: dateString[0] ? moment(dateString[0]).format('x') : '',
            fuseTimeEnd: dateString[1] ? moment(dateString[1]).format('x') : '',
            time: date
        })
    }
    //google验证弹窗
    modalGoogleCode = (googleType, googleSpace) => {
        this.setState({
            googVisibal: true,
            googleSpace,
            googleType,
        })
    }
    //google 按钮
    modalGoogleCodeBtn = value => {
        let { googleSpace, googleType,customerOperation } = this.state
        const { googleCode } = value
        axios.post(DOMAIN_VIP + "/common/checkGoogleCode", qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                switch (googleType) {
                    case 'all':
                        if(googleSpace.customerOperation.indexOf('01,02') != -1) {
                            customerOperation = '';
                        } else {
                            customerOperation = '01,02';
                        }
                        this.setState({
                            customerOperation
                        },()=> {
                            this.switchBtn(googleSpace,3);
                        });
                        break;
                    case 'exchange':
                        if(googleSpace.customerOperation.indexOf('01') != -1) {
                            customerOperation = '';
                        } else {
                            customerOperation = '01';
                        }
                        this.setState({
                            customerOperation
                        },()=> {
                            this.switchBtn(googleSpace,2);
                        });
                        break;
                    case 'withdraw':
                        if(googleSpace.customerOperation.indexOf('02') != -1) {
                            customerOperation = '';
                        } else {
                            customerOperation = '02';
                        }
                        this.setState({
                            customerOperation
                        },()=> {
                            this.switchBtn(googleSpace, 1);
                        });
                        // this.onWithdraw(googleSpace)
                        break;
                    case 'fabi':
                        if(googleSpace.customerOperation.indexOf('04') != -1) {
                            customerOperation = '';
                        } else {
                            customerOperation = '04';
                        }
                        this.setState({
                            customerOperation
                        },()=> {
                            this.switchBtn(googleSpace, 4);
                        });
                        // this.onWithdraw(googleSpace)
                        break;
                    default:
                        break;
                }
                this.setState({
                    googVisibal: false
                })

            } else {
                message.warning(result.msg)
            }
        })
    }
    onView = (item) => {
        console.log(item)
        console.log(this.props)
        //1-频繁对倒，2-频繁挂撤单，3-关联账户
        switch (item.fusetype) {
            case 1:
                this.props._this.add({
                    name: '频繁对倒账户',
                    key: 700100010212,
                    url: '/riskmanagement/checkaccounts/checkaccounts'
                })
                break;
            case 2:
                this.props._this.add({
                    name: '频繁挂单撤单',
                    key: 700100010214,
                    url: '/riskmanagement/accountmonitoring/accountmonitoring'
                })
                break;
            case 3:
                this.props._this.add({
                    name: '关联账户',
                    key: 700100010218,
                    url: '/riskmanagement/connectedaccout/connectedaccout'
                })
                break;
            default:
                break;
        }
    }
    onWithdraw = item => {
        axios.post(DOMAIN_VIP + '', qs.stringify({

        })).then(res => {
            const result = res.data;
            if (result.code == 0) {

            } else {
                message.warning(result.msg)
            }
        })
    }
    onExchange = item => {

    }
    onAll = item => {

    }
    onRemarksModal = (e, item) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onRemarksBtn(item)}>
                确定
            </Button>,
        ]

        this.setState({
            visible: true,
            title: e.target.innerHTML,
            width: '700px',
            modifyTextArea: item.memo || '',
            modalHtml: <BlowRemarksModal memo={item.memo} handleInputChange={this.handleInputChange} />
        })

    }
    onRemarksBtn = (item) => {
        const { modifyTextArea } = this.state
        axios.post(DOMAIN_VIP + '/coinOwnFuse/saveMemoById', qs.stringify({
            userId: item.userid,
            id: item.id,
            userName: item.userName,
            memo: modifyTextArea
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    visible: false
                })
                this.requestTable();
                message.success(result.msg)
            } else {
                message.warning(result.msg)
            }
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
    //google弹窗关闭
    onhandleCancel = () => {
        this.setState({
            googVisibal: false
        })
    }
    onConfirm = (e, googleType, googleSpace) => {
        let title = e.target.innerHTML;
        let self = this;
        // this.footer = [
        //     <Button key="back" onClick={this.handleCancel}>取消</Button>,
        //     <Button key="submit" type="more" onClick={() => this.modalGoogleCode(googleType, googleSpace)}>
        //         确定
        //     </Button>,
        // ]
        // this.setState({
        //     visible: true,
        //     title: '权限开关',
        //     width: '700px',
        //     modifyTextArea: '',
        //     modalHtml: <SwitchModal item={googleSpace} onChange={this.onChangeCheckbox} onlySee={false} />
        // })
        Modal.confirm({
            title: '您确定要' + title + '功能吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(googleType, googleSpace)
            },
            onCancel() {
                console.log('Cancel');
            },
        })
    };
    switchBtn = (item,type) => {
        const { customerOperation } = this.state;
        axios.post(DOMAIN_VIP + '/coinOwnFuse/updCustomerOperation', qs.stringify({
            customerOperation, userId: item.userid,id:item.id,
            typeName: item.fusetypeName, typeStatus: type
        })).then(res => {
            const result = res.data;
            this.requestTable()
            this.setState({
                visible: false
            });
            if (result.code == 0) {
                message.success(result.msg);
            } else {
                message.warning(result.msg);
            }
        })
    };
    //checkbox 
    onChangeCheckbox = (checkedValues) => {
        console.log('checked = ', checkedValues);
        console.log(checkedValues.join(','))
        this.setState({
            customerOperation: checkedValues.join(',')
        })
    };
    onViewAssociatMemo = (userid) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>关闭</Button>,
        ]
        this.setState({
            visible: true,
            title: '关联属性',
            width: '700px',
            modifyTextArea: '',
            modalHtml: <AssociatMemoModal userid={userid} />
        })
    };
    handleFusetype = fusetype => {
        this.setState({
            fusetype
        })
    };
    handleUserStatus = userStatus => {
        this.setState({ userStatus })
    };
    render() {
        const { showHide, tableSource, pageTotal, pageIndex, pageSize, time, width, modalHtml, visible, googVisibal, title, userId, fusetype, userStatus, } = this.state
        let userStates = ['--', '暂停提现、暂停交易', '提现开启、暂停交易', '暂停提现、交易开启', '全部开启']
        let fuseStates = ['--', '频繁对倒', '频繁挂撤单', '关联账户']
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 风控管理 > 平台熔断 > 个人熔断
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">熔断类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={fusetype} style={{ width: SELECTWIDTH }} onChange={this.handleFusetype} >
                                                <Option value={''}>请选择</Option>
                                                <Option value={1}>频繁对倒</Option>
                                                <Option value={2}>频繁挂撤单</Option>
                                                <Option value={3}>关联账户</Option>
                                                <Option value={4}>人工熔断</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={userStatus} style={{ width: SELECTWIDTH }} onChange={this.handleUserStatus} >
                                                <Option value={''}>请选择</Option>
                                                <Option value={1}>暂停提现、暂停交易</Option>
                                                <Option value={2}>提现开启、暂停交易</Option>
                                                <Option value={3}>暂停提现、交易开启</Option>
                                                <Option value={4}>全部开启</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">熔断时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm,ss'), moment('23:59:59', 'HH:mm,ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onChangeCheckTime}
                                                value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        {/* <a href='javascript:void(0);' onClick={(e) => this.onRemarksModal(e)} >备注</a> */}
                                        {/* <a href='javascript:void(0);' className="mar10" onClick={(e) => this.onConfirm(e, 'withdraw', )} >{1 ? '开启提现' : '暂停提现'}</a> */}
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table dataSource={tableSource}
                                        bordered
                                        rowKey="id"
                                        pagination={{
                                            showQuickJumper: true,
                                            showSizeChanger: true,
                                            showTotal: total => `总共${total}条`,
                                            size: 'small',
                                            total: pageTotal,
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            pageSizeOptions: PAGRSIZE_OPTIONS20,
                                            defaultPageSize: PAGESIZE,
                                            onShowSizeChange: this.onShowSizeChange,
                                            onChange: this.onChangePageNum
                                        }}
                                        scroll={{x:1800}}
                                        locale={{ emptyText: '暂无数据' }}>
                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='用户编号' dataIndex='userid' key='userid' />
                                        <Column title='熔断类型' dataIndex='fusetypeName' key='fusetypeName' />
                                        <Column title='用户状态' dataIndex='customerOperationName' key='customerOperationName' />
                                        <Column title='触发规则' dataIndex='triggerRule' key='triggerRule' />
                                        <Column title='熔断时间' dataIndex='fusetime' key='fusetime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : '--'} />
                                        <Column title='操作时间' dataIndex='opertime' key='opertime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : '--'} />
                                        <Column title='操作' dataIndex='op' key='op' render={(text, record) => (
                                            <span>
                                                {record.fusetype != 4 && <a href='javascript:void(0);' className="mar10" onClick={() => this.onView(record)} >查看</a>}
                                                {/*<a href='javascript:void(0);' className="mar10" onClick={(e) => this.onConfirm(e, 'withdraw', record)} >授权开关</a>*/}
                                                <a href='javascript:void(0);' className="mar10" onClick={(e) => this.onConfirm(e, 'withdraw', record)} >{record.customerOperation.indexOf("02") != -1 ? '开启提现' : '暂停提现'}</a>
                                                <a href='javascript:void(0);' className="mar10" onClick={(e) => this.onConfirm(e, 'exchange', record)} >{record.customerOperation.indexOf("01") != -1 ? '开启币币交易' : '暂停币币交易'}</a>
                                                {/* <a href='javascript:void(0);' className="mar10" onClick={(e) => this.onConfirm(e, 'fabi', record)} >{record.customerOperation.indexOf("04") != -1 ? '开启法币交易' : '暂停法币交易'}</a> */}
                                                <a href='javascript:void(0);' className="mar10" onClick={(e) => this.onConfirm(e, 'all', record)} >{record.customerOperation.indexOf("01,02") != -1 ? '全部开启' : record.customerOperation == ''?'全部暂停':''}</a>
                                                <a href='javascript:void(0);' onClick={(e) => this.onRemarksModal(e, record)} >备注</a>
                                            </span>
                                        )} />
                                        <Column title='备注' dataIndex='memo' key='memo' />
                                        <Column title='关联属性' dataIndex='cc' key='cc' render={(text, record) => record.fusetype == 3 && <a href='javascript:void(0);' onClick={() => this.onViewAssociatMemo(record.userid)} >查看</a>} />
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    width={width}
                    title={title}
                    onOk={this.handleOk}
                    footer={this.footer}
                    onCancel={this.handleCancel}
                >
                    {modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange={this.handleInputChange}
                    mid={new Date()}
                    visible={googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate} />
            </div>
        )
    }
}