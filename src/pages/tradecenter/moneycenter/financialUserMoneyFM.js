//理财用户资金
import Decorator from 'DTPath'
import FundsTypeList from '../../common/select/fundsTypeList'
import CommonTable from 'CTable'
import { Button, message, Modal } from 'antd'
import { PAGRSIZE_OPTIONS20, PAGESIZE, PAGEINDEX } from '../../../conf'
import ModalTransfer from './modal/modaltransfer'
import { toThousands,isObj } from 'Utils'

@Decorator({ lb: 'walletUserCapital' })
export default class FinancialUserMoneyFM extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            userId: '',
            userName: '',
            fundsType: '51',
            totalMoneyMax: '',
            totalMoneyMin: '',
            moneyMin: '',
            moneyMax: '',
            freezMoneyMin: '',
            freezMoneyMax: ''
        }
        // this.goofn = {
        //     default:v => this.transferFunds(v)            
        // }
        this.tableTotal = {
            pufbalancesum: null, //可用
            freezallsum: null, //冻结保证金
            allsum: null,//总资金
        }
        this.state = {
            modalHtml: '',
            modalTitle: '',
            modalVisible: false,
            modalWidth: '',
            allsum: '',
            freezallsum: '',
            pufbalancesum: '',
            ...this.defaultState
        }
        this.goofn = () => new Map([
            ['default', v => this.transferFunds(v)],
        ])

    }
    componentDidMount() {
        this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, userId, userName, fundsType, moneyMin, moneyMax, freezMoneyMin, freezMoneyMax, totalMoneyMin, totalMoneyMax, } = this.state

        let params = Object.keys(this.defaultState).reduce((res, key) => {
            const o = this.state[key]
            void 0 !== o && (res[key] = o)
            return res
        }, {
                pageIndex:  this.state.pageIndex,
                pageSize: this.state.pageSize
            })

        const result = await this.request({ url: '/userFinancial/query', type: 'post' }, params)
        const resultTotal = await this.request({ url: '/userFinancial/sum', type: 'post' }, params)
        const res = resultTotal[0] || {};
        Object.keys(this.tableTotal).forEach(v => {
            void 0 !== res[v] && ({ [v]: this.tableTotal[v] } = res)
        })
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize,
            ...this.tableTotal
        })

    }
    createColumns = (pageIndex, pageSize) => {
        const { limitBtn } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '资金类型', dataIndex: 'fundstypename' },
            { title: '用户编号', dataIndex: 'userid', },
            { title: '总金额', dataIndex: 'balanceSum', className: 'moneyGreen', render: text => toThousands(text, true) },
            { title: '可用金额', dataIndex: 'balance', className: 'moneyGreen', render: text => toThousands(text, true) },
            { title: '冻结保证金', dataIndex: 'freezeSecurity', className: 'moneyGreen', render: text => toThousands(text, true) },
            {
                title: '操作', dataIndex: 'cc', render: (text, record) => {
                    return (
                        <div>
                            {limitBtn.indexOf('transfer') > -1 ? <a className="mar10" href="javascript:void(0)" onClick={() => this.coinTransferModal(record)}>划转资金</a> : ''}
                        </div>
                    )
                }
            },
        ]
    }
    handleCancel = () => {
        this.setState({
            modalVisible: false
        })
    }
    //划转资金弹窗
    coinTransferModal = (item, type) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.modalGoogleCode(item, type, 'check')}>
                确认
            </Button>,
        ]
        this.setState({
            modalVisible: true,
            modalWidth: '900px',
            modalTitle: '划转资金',
            modalHtml: <ModalTransfer handleInputChange={this.handleInputChange} from='理财账户' to='我的钱包' item={item} />
        })
    }
    transferFunds = async googleItem => {
        const { money } = this.state
        const { fundstype, userid } = googleItem
        await this.request({ url: '/walletUserCapital/transfer', type: 'post', isP: true }, {
            fundsType: fundstype,
            from: '5',
            to: '1',
            userId: userid,
            amount: money,
        })
        this.handleCancel()
        this.requestTable()
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal, modalHtml, modalTitle, modalVisible, modalWidth,
            userId, userName, fundsType, totalMoneyMin, totalMoneyMax, moneyMin, moneyMax, freezMoneyMin, freezMoneyMax, allsum, freezallsum, pufbalancesum, } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList fundsType={fundsType} handleChange={(v) => this.onSelectChoose(v, 'fundsType')} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">总金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="totalMoneyMin" value={totalMoneyMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="totalMoneyMax" value={totalMoneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">可用金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMin" value={moneyMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMax" value={moneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">冻结保证金：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="freezMoneyMin" value={freezMoneyMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="freezMoneyMax" value={freezMoneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">

                                <div className="table-responsive">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    总资金：{toThousands(allsum, true)} &nbsp;&nbsp;&nbsp;
                                                    冻结保证金：{toThousands(freezallsum, true)}  &nbsp;&nbsp;&nbsp;
                                                    可用金额：{toThousands(pufbalancesum, true)}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <CommonTable
                                        dataSource={dataSource}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSize: pageSize,
                                                current: pageIndex

                                            }
                                        }
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}
                                        scroll={this.islessBodyWidth() ? { x: 1800 } : {}}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={modalVisible}
                    title={modalTitle}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width={modalWidth}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}
