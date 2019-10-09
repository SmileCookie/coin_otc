//虚拟资金异常
import React, { Component } from 'react'
import axios from '../../../utils/fetch';
import qs from 'qs';
import moment from 'moment'
import FundsTypeList from '../../common/select/fundsTypeList'
import { Table, message, DatePicker, Button, Select, Modal } from 'antd'
import { PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, PAGRSIZE_OPTIONS20, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT } from '../../../conf'
import { pageLimit } from '../../../utils'
const Column = Table.Column
const Option = Select.Option;
const { RangePicker } = DatePicker


export default class SmallPayManagement extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            tableSource: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            time: [],
            fundstype: '0',
            selectedRowKeys:[],//选中项的 key 数组 
            selectedRows:[],//选中项的 item 数组
            limitBtn:[]
        }
    }
    componentDidMount() {
        this.requestTable()
        this.setState({
            limitBtn:pageLimit('smallPayManagement', this.props.permissList)
        },()=>console.log(this.state.limitBtn))
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
            fundstype: '0'
        })
    }

    selectFundsType = v => {
        this.setState({
            fundstype: v
        })
    }
    //input 信息改变
    handleChangeInput = (e) => {
        const target = e.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //查询按钮
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => this.requestTable())
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex, pageSize, fundstype } = this.state
        axios.post(DOMAIN_VIP + '/smallPayManagement/list', qs.stringify({
            fundstype,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })).then(res => {
            // console.log(res)
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.list;
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id;
                }
                this.setState({
                    tableSource: tableSource,
                    pageTotal: result.data.totalCount
                }, () => console.log(this.state.tableSource))
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
    //table 多选框按钮选中时
    onSelectChangeTable = (selectedRowKeys, selectedRows) => {
        console.log('selectedRowKeys',selectedRowKeys)
        console.log('selectedRows',selectedRows)
        this.setState({ 
            selectedRowKeys,
            selectedRows,
        });
    }
    // getCheckboxProps = record => {
    //     console.log(record)
    //     return {
    //         disabled:true,
    //         name:record.name
    //     }
    // }
    //打开
    onSwitch = (type) => {
        let self = this, title = type === 1 ? '打开' : '关闭'
        Modal.confirm({
            title: `您确定要${title}吗`,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                // self.modalGoogleCode(item, type);
                self.onSwitchBtn(type)
                
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    
    onSwitchBtn = (type) => {
        const {selectedRowKeys} = this.state
        if(!selectedRowKeys.length){
            message.warning('请至少选择一条数据！')
            return false
        }
        let ids = selectedRowKeys.join(',').trim()
        axios.post(DOMAIN_VIP + '/smallPayManagement/update', qs.stringify({
            ids,
            download:type
        })).then(res => {
            // console.log(res)
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    selectedRowKeys:[],
                    selectedRows:[]
                },()=>this.requestTable())
                
            } else {
                message.warning(result.msg);
            }
        })
    }
    render() {
        const { showHide, tableSource, pageIndex, pageSize, pageTotal, fundstype,selectedRowKeys,selectedRows,limitBtn } = this.state
        let download = ['关','开']
        const rowSelection = {
            selectedRowKeys,
            selectedRows,
            onChange: this.onSelectChangeTable,
            // getCheckboxProps:this.getCheckboxProps,
            fixed:true
          };
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 监控中心 > 小额自动打币 > 小额打币开关
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList fundsType={fundstype} handleChange={this.selectFundsType} />
                                </div>
                                <div className="col-md-8 col-sm-6 col-xs-8 right">
                                    <div className="right">
                                        {limitBtn.includes('list')&&<Button type="primary" onClick={() => this.clickInquireState()}>查询</Button>}
                                        <Button type="primary" onClick={() => this.resetState()}>重置</Button>
                                        {limitBtn.includes('update')&&<Button type="primary" onClick={() => this.onSwitch(1)}>打开</Button>}
                                        {limitBtn.includes('update')&&<Button type="primary" onClick={() => this.onSwitch(0)}>关闭</Button>}
                                    </div>
                                </div>

                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table
                                        dataSource={tableSource}
                                        rowSelection={rowSelection}
                                        bordered
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.onChangePageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            pageSizeOptions: PAGRSIZE_OPTIONS20,
                                            defaultPageSize: PAGESIZE,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }}
                                        locale={{ emptyText: '暂无数据' }}
                                    >
                                        <Column title='序号' dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )} />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                                        <Column title='状态' dataIndex='download' key='download' render={t=>download[t]} />
                                        {/* <Column title='操作' dataIndex='op' key='op' render={(text, record) =>
                                            record.download == 0 ? 
                                            <a href="javascript:void(0)" onClick={() => this.onStart('start',record)}>打开</a>
                                            : <a href="javascript:void(0)" onClick={() => this.onClose('close',record)}>关闭</a>
                                        } /> */}
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}