import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, PAGRSIZE_OPTIONS20 } from '../../../../conf'
import SelectAType from '../../select/selectAType'
import { Button, Pagination, Modal } from 'antd'
import { toThousands, tableScroll,pageLimit } from '../../../../utils'
import DeductOfDep from './deductOfDep'
const BigNumber = require('big.js')

export default class ModalCapitalList extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            tableList: [],
            pageTotal: 0,
            currId: '',
            height: 0,
            tableScroll: {
                tableId: 'MDALCITLotc',
                x_panelId: 'MDALCITLotcXX',
                defaultHeight: 500,
            },
            visible: false,
            title: '',
            width: '',
            modalHtml: '',
            puobalancesum:'' ,//可用余额
            puofrozenfeesum:'',// 广告冻结
            puofrozentradesum:'',//交易冻结
            puofrozenwithdrawsum:'',//提现冻结
            storefreezsum:'',//保证金冻结余额
            allsum:'',//总资金
            limitBtn:[]
        }
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount() {
        const { pageIndex, pageSize } = this.state
        this.setState({
            currId: this.props.id,
            limitBtn: pageLimit('otcCapitalCount', this.props.permissList)
        }, () => this.requestTable())
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
            this.setState({
                pageIndex: PAGEINDEX,
                // pageSize:PAGESIZE,
                currId: nextProps.id
            }, () => this.requestTable())
            this.props.queryClickBtn ? this.props.queryClickBtn(false) : "";
        }
    }

    //table List
    requestTable(currIndex, currSize) {
        BigNumber.RM = 0;
        const { fundsType, userId, minBalance, maxBalance, userName, borrowMin, borrowMax, freezMoneyMax, freezMoneyMin,
            frozenWithdrawMin, frozenWithdrawMax, lendingMin, lendingMax, entrustMin, entrustMax, totalMoneyMin, totalMoneyMax, frozenFeeMax, frozenFeeMin,
            frozenTradeMax, frozenTradeMin, } = this.props

        const { pageIndex, pageSize, currId } = this.state
        axios.post(DOMAIN_VIP + '/otcCapitalCount/query', qs.stringify({
            coinTypeId: fundsType, minBalance, maxBalance, userName, borrowMin, borrowMax, freezMoneyMax, freezMoneyMin,
            frozenWithdrawMin, frozenWithdrawMax, lendingMin, lendingMax, entrustMin, entrustMax, totalMoneyMin, totalMoneyMax, frozenFeeMax, frozenFeeMin,
            frozenTradeMax, frozenTradeMin,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize,
            userId: currId || userId
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {

                this.setState({
                    tableList: result.data.list.map((item,index) => {
                        item.newbalance = new BigNumber(item.balance).plus(item.frozenfee).plus(item.frozenwithdraw).plus(item.frozentrade).plus(item.storefreez).toFixed()
                        return item
                    }),
                    pageTotal: result.data.totalCount
                })
            } else {
                message.warning(result.msg)
            }
        })
        axios.post(DOMAIN_VIP + '/otcCapitalCount/sum', qs.stringify({
            coinTypeId: fundsType, minBalance, maxBalance, userName, borrowMin, borrowMax, freezMoneyMax, freezMoneyMin,
            frozenWithdrawMin, frozenWithdrawMax, lendingMin, lendingMax, entrustMin, entrustMax, totalMoneyMin, totalMoneyMax, frozenFeeMax, frozenFeeMin,
            frozenTradeMax, frozenTradeMin,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize,
            userId: currId || userId
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    storefreezsum: result.data[0] ? result.data[0].storefreezsum : null,
                    puobalancesum: result.data[0] ? result.data[0].puobalancesum : null,
                    puofrozenfeesum: result.data[0] ? result.data[0].puofrozenfeesum : null,
                    puofrozentradesum: result.data[0] ? result.data[0].puofrozentradesum : null,
                    puofrozenwithdrawsum: result.data[0] ? result.data[0].puofrozenwithdrawsum : null,
                    allsum: result.data[0] ? result.data[0].allsum : null,
                  
                })


            } else {
                message.warning(result.msg)
            }
        })
    }

    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page,
            pageSize: pageSize
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
    deductOfDep = v => {
        this.setState({
            title: '扣减保证金',
            visible: true,
            modalHtml: <DeductOfDep item={v} handleCancel={this.handleCancel} requestTable= {this.requestTable} />,
            width: '600px'
        })
    }
    handleCancel = () => this.setState({ visible: false })
    render() {
        const { pageIndex, pageSize, pageTotal, tableList,limitBtn } = this.state
        return (
            <div className="x_panel">
                <div className="x_content">
                    <div id={this.state.tableScroll.tableId} style={{ height: `${this.state.tableList.length > 10 ? this.state.tableScroll.defaultHeight + this.state.height + 'px' : ''}` }} className="table-responsive-yAuto">
                       
                       
                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                            <thead>
                                <tr className="headings">
                                    <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                    总资金:{toThousands(this.state.allsum)}&nbsp;&nbsp;&nbsp;
                                    交易冻结:{toThousands(this.state.puofrozentradesum)}&nbsp;&nbsp;&nbsp;
                                    可用余额:{toThousands(this.state.puobalancesum)}&nbsp;&nbsp;&nbsp;
                                    广告冻结:{toThousands(this.state.puofrozenfeesum)}&nbsp;&nbsp;&nbsp;
                                    提现冻结:{toThousands(this.state.puofrozenwithdrawsum)}&nbsp;&nbsp;&nbsp;
                                    保证金冻结余额:{toThousands(this.state.storefreezsum)}
                                    </th>
                                    
                                   
                                   
                                </tr>
                                
                            </thead>
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">资金类型</th>
                                    <th className="column-title">用户编号</th>
                                    <th className="column-title">总金额</th>
                                    <th className="column-title">可用金额</th>
                                    {/* <th className="column-title hide">融资融币借入金额</th> */}
                                    <th className="column-title">冻结广告费金额</th>
                                    <th className="column-title">提现冻结金额</th>
                                    {/* <th className="column-title hide">放贷冻结金额</th> */}
                                    <th className="column-title">交易冻结金额</th>
                                    <th className="column-title">保证金冻结余额</th>
                                    <th className="column-title">操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length > 0 ?
                                        tableList.map((item, index) => {
                                            BigNumber.RM = 0;
                                            return (
                                                <tr key={index}>
                                                    <td>{(pageIndex - 1) * pageSize + index + 1}</td>
                                                    <td>{item.coinTypeName}</td>
                                                    <td>{item.userid}</td>
                                                    <td>{toThousands(item.newbalance)}</td>
                                                    <td>{toThousands(item.balance)}</td>
                                                    {/* <td className="hide">{item.insucces}</td> */}
                                                    <td>{toThousands(item.frozenfee)}</td>
                                                    <td>{toThousands(item.frozenwithdraw)}</td>
                                                    {/* <td className="hide">{toThousands(item.outwait)}</td> */}
                                                    {/* <td>{toThousands(new BigNumber(item.freez).minus(item.withdrawfreeze).minus(item.outwait).toFixed())}</td> */}
                                                    <td>{toThousands(item.frozentrade)}</td>
                                                    <td>{toThousands(item.storefreez)}</td>
                                                    <td>{item.storefreez ? limitBtn.includes('subtract')&&<a href='javascript:void(0)' onClick={() => this.deductOfDep(item)}>扣减保证金</a>:""}</td>
                                                </tr>
                                            )
                                        })
                                        : <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                }
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className="pagation-box">
                    {
                        pageTotal > 0 && <Pagination
                            size="small"
                            current={pageIndex}
                            total={pageTotal}
                            showTotal={total => `总共 ${total} 条`}
                            onChange={this.changPageNum}
                            onShowSizeChange={this.onShowSizeChange}
                            showSizeChanger
                            showQuickJumper
                            pageSizeOptions={PAGRSIZE_OPTIONS20}
                            defaultPageSize={PAGESIZE}
                        />
                    }
                </div>
                <Modal
                    visible={this.state.visible}
                    title={this.state.title}
                    width={this.state.width}
                    style={{ top: 50 }}
                    onCancel={this.handleCancel}
                    footer={null}
                >
                    {this.state.modalHtml}
                </Modal>
            </div>
        )
    }

}








































