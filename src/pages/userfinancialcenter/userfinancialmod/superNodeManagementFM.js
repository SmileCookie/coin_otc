/**超级节点管理 */
import Decorator from 'DTPath'
import CommonTable from 'CTable'
import FundsTypeList from '../../common/select/fundsTypeList'
import { Button, } from 'antd'
import {PAGEINDEX, NODE_TYPE, IS_SHOW, NODE_STATE } from 'Conf'
import { toThousands, TE, ckd, dateToFormat,mapGet } from 'Utils'
import { SeOp } from '../../../components/select/asyncSelect'
import ModalSuperNode from './modal/modalSuperNode'
const Big = require('big.js')


@Decorator({lb: 'supernode'})
export default class SuperNodeManagementFM extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            // snodeseq: '',//节点编号
            snodename: '',//名称
            snodeaddr: '',//地址
            // snoderemark: '',//备注
            snodestate: '',//状态
            // snodepayamountS: '', //节点转出余额
            // snodepayamountE: '',
            snodebalanceS: '',//节点余额
            snodebalanceE: '',
            snodetype: '',//节点类型
            snodebeltype: '',//归属类型
            snodeshowflag: '',//显示标志
        }
        this.state = {
            ...this.defaultState
        }
    }
    async componentDidMount() {

    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, } = this.state
        const params = Object.keys(this.defaultState).reduce((res, key) => {
            void 0 !== this.state[key] && (res[key] = this.state[key])
            return res
        }, {
                pageIndex: currIndex || pageIndex,
                pageSize: currSize || pageSize
            })
        const result = await this.request({ url: '/supernode/list', type: 'post' }, params)
        this.setState({
            dataSource: ckd(result.list),
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    modify = ({snodename,snodeaddr,snodetype,snodebeltype,snodeshowflag,snodestate,id}) => {
        this.setState({
            cpMV:true,
            cpMT:'修改',
            cpMW:'1000px',
            cpMlh:<ModalSuperNode item={{snodename,snodeaddr,snodetype,snodebeltype,snodeshowflag,snodestate,id}} requestTable={this.requestTable} cpMCancel={this.cpMCancel} />
        })
    }
    createColumns = (pageIndex, pageSize) => {
        const {limitBtn} = this.state
        Big.RM = 0;
        return [
            { title: '序号', className: 'wordLine',dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            // { title: '节点编号', className: 'wordLine',dataIndex: 'snodeseq' },
            { title: '节点名称', className: 'wordLine',dataIndex: 'snodename' },
            { title: '节点地址', className: 'wordLine',dataIndex: 'snodeaddr' },
            { title: '节点余额', className: 'wordLine',dataIndex: 'snodebalance', className: 'moneyGreen', render: text => toThousands(text, true) },
            // { title: '节点转出余额', className: 'wordLine',dataIndex: 'snodepayamount', className: 'moneyGreen', render: text => toThousands(text, true) },
            // { title: '节点备注', className: 'wordLine',dataIndex: 'snoderemark' },
            { title: '最近产出', className: 'wordLine',dataIndex: 'lateminingamount' },
            { title: '产出时间', className: 'wordLine',dataIndex: 'lateminingtime', render: t => dateToFormat(t) },
            { title: '节点类型', className: 'wordLine',dataIndex: 'snodetype', render: t => mapGet(NODE_TYPE,t) },
            { title: '归属类型', className: 'wordLine',dataIndex: 'snodebeltype', render: t => mapGet(NODE_TYPE,t) },
            { title: '显示标志', className: 'wordLine',dataIndex: 'snodeshowflag', render: t => mapGet(IS_SHOW,t) },
            { title: '节点状态', className: 'wordLine',dataIndex: 'snodestate', render: t => mapGet(NODE_STATE,t) },
            { title: '操作',  className: 'wordLine',dataIndex: 'balance', render: (t, r) => limitBtn.includes('update') && <a href='javascript:void(0);' onClick={() => this.modify(r)}>修改</a> },
        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal, snodeseq, snodename, snodeaddr, snoderemark, snodestate,
            snodebalanceS, snodebalanceE, snodepayamountS, snodepayamountE, snodetype, snodebeltype, snodeshowflag,limitBtn } = this.state
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
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">节点编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="snodeseq" value={snodeseq} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">节点名称：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="snodename" value={snodename} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">节点地址：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="snodeaddr" value={snodeaddr} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">节点备注：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="snoderemark" value={snoderemark} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div> */}
                                <SeOp title='节点状态' value={snodestate} ops={NODE_STATE} onSelectChoose={v => this.onSelectChoose(v, 'snodestate')} pleaseC />
                                <SeOp title='节点类型' value={snodetype} ops={NODE_TYPE} onSelectChoose={v => this.onSelectChoose(v, 'snodetype')} pleaseC />
                                <SeOp title='归属类型' value={snodebeltype} ops={NODE_TYPE} onSelectChoose={v => this.onSelectChoose(v, 'snodebeltype')} pleaseC />
                                <SeOp title='显示标志' value={snodeshowflag} ops={IS_SHOW} onSelectChoose={v => this.onSelectChoose(v, 'snodeshowflag')} pleaseC />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">节点余额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最小值' className="form-control" name="snodebalanceS" value={snodebalanceS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" placeholder='最大值' className="form-control" name="snodebalanceE" value={snodebalanceE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">节点转出余额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="snodepayamountS" value={snodepayamountS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="snodepayamountE" value={snodepayamountE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div> */}
                                
                                <div className="col-md-12 col-sm-12 col-xs-12 right">
                                    <div className="right">
                                        {limitBtn.includes('list') && <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>}
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
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
                                    // scroll={this.islessBodyWidth() ? { x: 1800 } : {}}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}