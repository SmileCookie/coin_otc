import React from 'react'
import axios from 'axios'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP, PAGESIZE, PAGEINDEX, TIMEFORMAT } from '../../../../conf'
import { Table, Button, message, Tabs } from 'antd'
const { Column, ColumnGroup } = Table;
const { TabPane } = Tabs

export default class ModalSettleInfo extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            selectedRowKeys: [],
            selectedRows: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pagination: {
                showSizeChanger: true,
                showQuickJumper: true
            },
            tableSource: [],
            totalAccount: '?',
            afterAmount: '?',
            type: '',
            id: '',
            operaType: '',
            fundType: '',
            tk: '10',//tab key
            detailsusdteid: 0,//usdte的id
        }

        this.requestTable = this.requestTable.bind(this)
        this.onChange = this.onChange.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.countChargeTotal = this.countChargeTotal.bind(this)
        this.countWithdrawTotal = this.countWithdrawTotal.bind(this)
    }
    componentDidMount() {
        const { type, id, operaType, detailsId, fundType, detailsusdteid } = this.props
        this.setState({
            type,
            id,
            operaType,
            detailsId,
            fundType,
            detailsusdteid
        }, () => this.requestTable())

    }
    componentWillReceiveProps(nextProps) {
        const { type, id, operaType, detailsId, fundType, detailsusdteid } = nextProps
        this.setState({
            type,
            id,
            operaType,
            detailsId,
            fundType,
            detailsusdteid
        }, () => this.requestTable())

    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestTable(page, pageSize))
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.requestTable(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }
    /**
     * @author oliver
     * @function 判断资金类型和detailsId
     */
    usdteDetaisID = () => {
        const { detailsId, detailsusdteid, tk, fundType } = this.state
        return {
            fundType: fundType == 10 ? tk * 1 : fundType, //资金类型为10时，usdt：10，usdte：102,。否则直接传fundsType
            detailsId: tk == 102 ? detailsusdteid : detailsId,//tab 为usdte时候传detailsusdteid 否则 detailsId
        }

    }
    requestTable(currIndex, currSize, type) {
        let self = this;
        const { pagination, pageIndex, pageSize, fundType, detailsId, tk, detailsusdteid } = this.state

        let url = this.props.operaType == 3 ? '/settlement/giveDownload' : '/settlement/rechargeDetails'
        axios.post(DOMAIN_VIP + url, qs.stringify({
            ...this.usdteDetaisID(),
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })).then(res => {
            const result = res.data;
            let tableSource = result.data.list;
            for (let i = 0; i < tableSource.length; i++) {
                tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                tableSource[i].key = tableSource[i].id;

                tableSource[i].sendtime ? tableSource[i].sendtime = moment(tableSource[i].sendtime).format(TIMEFORMAT) : "";
                tableSource[i].configtime = moment(tableSource[i].configtime).format(TIMEFORMAT);
                tableSource[i].submittime ? tableSource[i].submittime = moment(tableSource[i].submittime).format(TIMEFORMAT) : "";
                tableSource[i].remark = <span dangerouslySetInnerHTML={{ __html: tableSource[i].remark }} />
            }

            pagination.total = result.data.totalCount
            pagination.onChange = self.changPageNum
            pagination.onShowSizeChange = self.onShowSizeChange
            this.setState({
                tableSource,
                pagination
            })
        })
    }

    onChange(selectedRowKeys, selectedRows) {
        this.setState({
            selectedRowKeys,
            selectedRows
        })
    }

    //充值统计选中
    countChargeTotal(type) {
        const { fundType, detailsId, selectedRowKeys } = this.state
        const ids = type == 'all' ? '' : this.state.selectedRowKeys.join();
        if (type != 'all' && !selectedRowKeys.length) {
            message.warning("至少选择一项！")
            return false;
        }
        axios.post(DOMAIN_VIP + '/settlement/chooseDetailsCount', qs.stringify({
            ...this.usdteDetaisID(),
            ids
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    totalAccount: result.amount
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    //提现统计选中
    countWithdrawTotal(type) {
        const { fundType, detailsId, selectedRowKeys } = this.state
        const ids = type == 'all' ? '' : this.state.selectedRowKeys.join();
        if (type != 'all' && !selectedRowKeys.length) {
            message.warning("至少选择一项！")
            return false;
        }
        axios.post(DOMAIN_VIP + '/settlement/chooseDownloadCount', qs.stringify({
            ...this.usdteDetaisID(),
            ids
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    totalAccount: result.amount,
                    afterAmount: result.afterAmount
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    //只有资金类型为USDT的时候才会显示tab，USDT = 10
    tcb = async tk => {
        await this.setState({
            tk,
            selectedRowKeys: [],
            selectedRows: [],
        })
        this.requestTable()
    }
    render() {
        const { tableSource, pagination, selectedRowKeys, selectedRows, totalAccount, operaType, afterAmount, fundType, tk } = this.state
        const rowSelection = {
            selectedRowKeys,
            selectedRows,
            onChange: this.onChange
        };
        return (
            <div>
                {fundType == 10 && <Tabs onChange={this.tcb} activeKey={tk}>
                    <TabPane tab='USDT' key='10'></TabPane>
                    <TabPane tab='USDTE' key='102'></TabPane>
                </Tabs>}
                {
                    operaType != 3 ?
                        <div className="settle-box">
                            <div className="table-responsive martop50">
                                <Table dataSource={tableSource} rowSelection={rowSelection} bordered pagination={pagination} locale={{ emptyText: '暂无数据' }}>
                                    <Column title="序号" dataIndex="index" key="index" />
                                    <Column title="资金类型" dataIndex="fundstypeName" key="fundstypeName" />
                                    <Column title="充值编号" dataIndex="id" key="id" />
                                    <Column title="用户编号" dataIndex="userid" key="userid" />
                                    <Column title="充值金额" dataIndex="amount" key="amount" />
                                    <Column title="状态" dataIndex="showStatu" key="showStatu" />
                                    <Column title="充值时间" dataIndex="sendtime" key="sendtime" />
                                    <Column title="确认时间" dataIndex="configtime" key="configtime" />
                                    <Column title="充值地址" dataIndex="toaddr" key="toaddr" />
                                    <Column title="备注" dataIndex="remark" key="remark" />
                                </Table>
                            </div>
                            <div className="settle-btn col-md-12 col-sm-12 col-xs-12 right">
                                <Button className="right mar20" type="more" onClick={() => this.countChargeTotal('all')}>统计全部金额</Button>
                                <Button className="right mar20" type="more" onClick={() => this.countChargeTotal()}>统计选中金额</Button>
                                <p className="total-count right line34 mar20">总金额：{totalAccount}</p>
                            </div>
                        </div> :
                        <div className="settle-box">
                            <div className="table-responsive martop50">
                                <Table dataSource={tableSource} rowSelection={rowSelection} bordered pagination={pagination} locale={{ emptyText: '暂无数据' }}>
                                    <Column title="序号" dataIndex="index" key="index" />
                                    <Column title="资金类型" dataIndex="fundstypename" key="fundstypename" />
                                    <Column title="提现编号" dataIndex="id" key="id" />
                                    <Column title="用户编号" dataIndex="userid" key="userid" />
                                    <Column title="提现金额" dataIndex="amount" key="amount" />
                                    <Column title="实际金额" dataIndex="afterAmount" key="afterAmount" />
                                    <Column title="申请时间" dataIndex="submittime" key="submittime" />
                                    <Column title="提现时间" dataIndex="configtime" key="configtime" />
                                    <Column title="提现地址" dataIndex="toaddress" key="toaddress" />
                                    <Column title="备注" dataIndex="remark" key="remark" />
                                    <Column title="资金状态" dataIndex="customerOperationName" key="customerOperationName" />
                                    <Column title="状态" dataIndex="typeName" key="typeName" />
                                    <Column title="打币类型" dataIndex="auditorTypeName" key="auditorTypeName" />
                                </Table>
                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12 right settle-btn">
                                <Button className="right mar20" type="more" onClick={() => this.countWithdrawTotal('all')} >统计全部金额</Button>
                                <Button className="right mar20" type="more" onClick={() => this.countWithdrawTotal()}>统计选中金额</Button>
                                <p className="total-count right line34 mar20">实际金额：{totalAccount}</p>
                                <p className="total-count right line34 mar20">提现金额：{afterAmount}</p>
                            </div>
                        </div>
                }
            </div>
        )
    }

}










































