/**数据中心 》 充提管理 》 提现查询  */
import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, TIMEFORMAT, SELECTWIDTH ,PAGRSIZE_OPTIONS20,TIMEFORMAT_ss } from '../../../../conf'
import { Input, Modal, DatePicker, Select, Button, Pagination, message } from 'antd'
import { toThousands, tableScroll } from '../../../../utils'

export default class ModalWithdrawRecord extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            tableList: [],
            pageTotal: 0,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            height: 0,
            tableScroll: {
                tableId: 'ModalWithdrawRecord',
                x_panelId: 'ModalWithdrawRecordX',
                defaultHeight: 500,
            },
            amountSum:null,
            amountNowSum:null,
            realFeeSum:'',
            moneyTypeInterface: DOMAIN_VIP + '/withdrawRecord/withdrawVerify',
            tableDataInterface: DOMAIN_VIP + '/withdrawRecord/sum'
        }

        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    // componentDidMount(){
    //     const { pageIndex,pageSize } = this.state
    //     this.requestTable(pageIndex,pageSize,this.props.id)
    // }
    componentDidMount() {

        const { pageIndex, pageSize } = this.state
        this.requestTable(pageIndex, pageSize, this.props.id)
        tableScroll(`#${this.state.tableScroll.tableId}`, 'add', `#${this.state.tableScroll.x_panelId}`)
    }
    componentWillUnmount() {
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight) {
        this.setState({
            xheight
        })
    }

    componentWillReceiveProps(nextProps) {
        const { pageIndex,pageSize} = this.state
        if (!nextProps.showHide && this.state.pageSize > 10) {
            this.setState({
                height: nextProps.xheight
            })
        } else {
            this.setState({
                height: 0,
            })
        }
        if (nextProps.isreLoad) {
            // this.setState({
            //     pageIndex: PAGEINDEX,
            //     pageSize: PAGESIZE
            // })
            this.requestTable(PAGEINDEX, pageSize, nextProps.id)
            this.props.queryClickBtn && this.props.queryClickBtn(false)
        }
    }

    requestTable(currIndex, currSize, currId) {
        let {
            userid,
            username,
            type,
            customerOperation,
            endTime,
            startTime,
            confirmEndDate,
            confirmStartDate,
            moneyMax,
            moneyMin,
            commandid,
            fundstype,
            billTime,
            blockTime,
        } = this.props
        const {
            pageIndex,
            pageSize,
            moneyTypeInterface,
            tableDataInterface
        } = this.state;
        if(!Array.isArray(billTime)) billTime = [];
        if(!Array.isArray(blockTime)) blockTime = []
        let billStartTime =  billTime.length ? moment(billTime[0]).format(TIMEFORMAT_ss) : '',
        billEndTime  =  billTime.length ? moment(billTime[1]).format(TIMEFORMAT_ss) : '';
        let blockStartTime =  blockTime.length ? moment(blockTime[0]).format(TIMEFORMAT_ss) : '',
        blockEndTime  =  blockTime.length ? moment(blockTime[1]).format(TIMEFORMAT_ss) : '';
        const parameter = {
            username, type, customerOperation, endTime, startTime, confirmEndDate, confirmStartDate, moneyMax, moneyMin, commandid, fundstype,
            billStartTime,billEndTime,blockStartTime,blockEndTime,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize,
            userid: currId || userid
        }
        axios.post(moneyTypeInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    tableList: result.data.list,
                    pageTotal: result.data.totalCount
                })
            } else {
                message.warning(result.msg)
            }
        })
        axios.post(tableDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                // console.log(result)
                this.setState({
                    amountSum:result.data.list[0].amountSum,
                    amountNowSum:result.data.list[0].amountNowSum,
                    realFeeSum:result.data.list[0].realFeeSum
                })
            }
        })
        
    }

    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page,
            pageSize: pageSize
        })
        this.requestTable(page, pageSize, this.props.id)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.requestTable(current, size, this.props.id)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }



    render() {
        const { pageSize, pageIndex, pageTotal, tableList, amountSum, amountNowSum,realFeeSum } = this.state
        return (
            <div className="x_panel">

                <div className="x_content">
                    <div id={this.state.tableScroll.tableId} style={{ height: `${this.state.tableList.length > 10 ? this.state.tableScroll.defaultHeight + this.state.height + 'px' : ''}` }} className="table-responsive-xyAuto table-responsive-fixed">
                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                            {this.props.display ? '' :<thead>
                                <tr className='headings'>
                                    <th colSpan='15' style={{textAlign:'left'}} className="column-title">
                                        提现金额:{toThousands(amountSum,true)} ，
                                        实际金额:{toThousands(amountNowSum,true)}，
                                        提现手续费：{toThousands(realFeeSum,true)}
                                    </th>
                                </tr>
                            </thead>}
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">资金类型</th>
                                    <th className="column-title">提现编号</th>
                                    <th className="column-title">用户编号</th>
                                    <th className="column-title">提现金额</th>
                                    <th className="column-title">实际金额</th>
                                    <th className="column-title">申请时间</th>
                                    <th className="column-title">审核时间</th>
                                    <th className="column-title">到账时间</th>
                                    <th className="column-title">记账时间</th>
                                    <th className="column-title min_153px">提现地址</th>
                                    <th className="column-title">资金状态</th>
                                    <th className="column-title">提现手续费</th>
                                    <th className="column-title">打币类型</th>
                                    <th className="column-title">打币状态</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length > 0 ?
                                        tableList.map((item, index) => {
                                            return (
                                                <tr key={index}>
                                                    <td>{(pageIndex - 1) * pageSize + index + 1}</td>
                                                    <td>{item.fundstypename}</td>
                                                    <td>{item.downloadid}</td>
                                                    <td>{item.userid}</td>
                                                    <td>{toThousands(item.amount, true)}</td>
                                                    <td>{toThousands(item.afterAmount, true)}</td>
                                                    <td>{item.submittime?moment(item.submittime).format(TIMEFORMAT):'--'}</td>
                                                    <td>{item.managetime ? moment(item.managetime).format(TIMEFORMAT) : '--'}</td>
                                                    <td>{item.configtime ? moment(item.configtime).format(TIMEFORMAT) : '--'}</td>
                                                    <td>{item.billTime ? moment(item.billTime).format(TIMEFORMAT) : '--'}</td>
                                                    <td><a href={(() => {
                                                        let strLast = item.txid == 0 ? '' : item.txid
                                                        return item.url + strLast
                                                    })()}>{item.toaddress}</a></td>
                                                    <td>{item.customerOperationName}</td>
                                                    <td>{item.fees}</td>
                                                    <td>{item.auditorTypeName}</td>
                                                    <td>{item.typeName}</td>
                                                </tr>
                                            )
                                        })
                                        : <tr className="no-record"><td colSpan="20">暂无数据</td></tr>
                                }
                            </tbody>
                        </table>
                    </div>
                    <div className="pagation-box">
                        {pageTotal > 0 &&
                            <Pagination
                                size="small"
                                current={pageIndex}
                                total={pageTotal}
                                showTotal={total => `总共 ${total} 条`}
                                onChange={this.changPageNum}
                                onShowSizeChange={this.onShowSizeChange}
                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                defaultPageSize={PAGESIZE}
                                showSizeChanger
                                showQuickJumper
                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                defaultPageSize={PAGESIZE}
                                 />
                        }
                    </div>
                </div>
            </div>
        )

    }

}


























