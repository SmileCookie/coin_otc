/**量化程序停止报警 */
import React, { Component } from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, DatePicker, Tabs, Pagination, Select, message, Table, Modal } from 'antd'
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT, PAGRSIZE_OPTIONS20, DAYFORMAT, MINUTFORMAT } from '../../../conf'
import MarketRequests from '../../common/select/marketrequests'
import { toThousands } from '../../../utils'

// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { Column } = Table
class QuantizateProgramStopAlarm extends Component {
    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            isreLoad: false,
            showHide: true,
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
            entrustmarket: '',
            state: '0',
            callMessage: '',
            selectedRowKeys: [], // 复选框
            loading: false,
        }
        this.clickHide = this.clickHide.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.start = this.start.bind(this)
        this.onSelectChange = this.onSelectChange.bind(this)
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render() {
        const { selectedRowKeys, loading, time, pageSize, showHide, tableSource, pagination, pageIndex, entrustmarket, state, callMessage } = this.state
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
        };
        const hasSelected = selectedRowKeys.length > 0;
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 保值异常 > 量化程序停止报警
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <MarketRequests market={entrustmarket} handleChange={this.handleChangeSelect} col='3' />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={state} onChange={this.handleChange} style={{ width: SELECTWIDTH }}>
                                                <Option value=''>全部</Option>
                                                <Option value='0'>失败</Option>
                                                <Option value='1'>正常</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">报警信息：</label>
                                        <div className="col-sm-8">
                                            <Select value={callMessage} onChange={this.handleMessageChange} style={{ width: SELECTWIDTH }}>
                                                <Option value=''>请选择</Option>
                                                <Option value='0'>委托停止</Option>
                                                <Option value='1'>成交停止</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        <Button type="primary" onClick={this.start} disabled={!hasSelected} loading={loading}>
                                            批量标记
                                        </Button>
                                    </div>
                                </div>

                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table rowSelection={rowSelection} dataSource={tableSource} bordered pagination={{ ...pagination, current: pageIndex }} locale={{ emptyText: '暂无数据' }}
                                        onChange={this.sorter}
                                    >
                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='交易市场' dataIndex='entrustmarket' key='entrustmarket' render={(parameter) => {

                                            return this._plusFlg(parameter)
                                        }} />
                                        <Column title='报警信息' dataIndex='types' key='types' render={(parameter) => parameter == 0 ? <div>成交停止</div> : ''} />
                                        <Column title='状态' dataIndex='state' key='state' render={(parameter) => parameter == 0 ? '失败' : (parameter == 1 ? '正常' : '')} />
                                        <Column title='行情停止时间（分）' dataIndex='stoptime' key='stoptime' render={(stoptime, obj) => obj.state == 0 ? '' : parseInt(stoptime / 60) + '时' + stoptime % 60 + '分'} />
                                        <Column title='报警时间' dataIndex='savetime' key='savetime' render={(parameter) => {
                                            return parameter ? moment(parameter).format(TIMEFORMAT) : '--'
                                        }} />
                                        <Column title='操作' dataIndex='op' key='opp' render={(text, record) => <a href="javascript:void(0)" onClick={() => this.toMark(record.id)}>标记</a>} />,

                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

            </div>
        )
    }
    componentDidMount() {
        this.requestTable()
    }
    componentDidUpdate() {

    }
    toMark = id => {
        let self = this
        Modal.confirm({
            title: `确定要标记吗？`,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + "/coinQtStopwarning/update", qs.stringify({
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
        axios.post(DOMAIN_VIP + '/coinQtStopwarning/viewBatch', qs.stringify({
            ids: selectedRowKeys.join(',').trim()
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                setTimeout(() => {
                    this.setState({
                        selectedRowKeys: [],
                        loading: false,
                    });
                }, 300);
                this.requestTable();
                message.success('批量标记成功')
            } else {
                message.error(result.msg);
            }
        })

    };
    onSelectChange = selectedRowKeys => {
        this.setState({ selectedRowKeys });
    };


    //请求数据
    requestTable = (currentIndex, currentSize) => {

        const { pageIndex, pageSize, entrustmarket, state, pagination, callMessage } = this.state
        axios.post(DOMAIN_VIP + '/coinQtStopwarning/list', qs.stringify({
            entrustmarket, state,
            types: callMessage,
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
    //交易市场下拉菜单
    handleChangeSelect = value => {
        this.setState({
            entrustmarket: value,
        })
    }
    //状态选择
    handleChange = value => {
        this.setState({
            state: value
        })
    }
    // 报警状态的选择
    handleMessageChange = value => {
        this.setState({
            callMessage: value
        })
    }
    insert_flg(str, flg, sn) {
        var newstr = "";
        for (var i = 0; i < str.length; i += sn) {
            var tmp = str.substring(i, i + sn);
            newstr += tmp + flg + str.substring(i + sn, str.length);
            if (newstr.length >= 1) {
                return newstr
            }
        }
    }
    _plusFlg = str => {
        let newStr = '';
        if (/BTC$/.test(str)) {
            newStr = str.replace('BTC', '_BTC')
        }
        if (/USDT$/.test(str)) {
            newStr = str.replace('USDT', '_USDT')
        }
        return newStr

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
            entrustmarket: '',
            state: '0',
            callMessage: ''
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

}
export default QuantizateProgramStopAlarm