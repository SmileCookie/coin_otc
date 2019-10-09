/**vip分红查询 */
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select, DatePicker } from 'antd'
import {DOMAIN_VIP, DISTRIBUTE_STATE, PAGRSIZE_OPTIONS20, PAGESIZE, PAGEINDEX, TIMEFORMAT_ss, SHOW_TIME_DEFAULT, SELECTWIDTH } from '../../../conf'
import { toThousands, TE, mapGet, dateToFormat ,arrayTimeToStr} from '../../../utils'
import { SeOp } from '../../../components/select/asyncSelect'
import axios from '../../../utils/fetch'
const { Option } = Select;
const { RangePicker } = DatePicker;

@Decorator()
export default class DividendQuery extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            userid: '',//  '用户编号' ,
            username: '',//    用户名' ,
            parentid: '',//分配批次' ,
            flag: '',//分配状态（字段没给）（0：未分配，1：已分配）
            profitAmountS: '',//VDS分配数量区间
            profitAmountE: '',
            usdtAmountS: '',//USDT分配数量开始
            usdtAmountE: '',//USDT分配数量结束
            usdtPriceS: '', //分配价格区间 ,
            usdtPriceE: '',
            createtimeS: '',
            createtimeE: '',//分配时间区间
            time: []
        }
        this.state = {
            ...this.defaultState,
            profitAmountCount:'',//vds总数
            usdtAmountCount:'',//USDT总数
        }
    }
    // async componentDidMount() {

    // }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, userid, username, parentid, flag, profitAmountS, profitAmountE, usdtAmountS, usdtAmountE,
            time, usdtPriceS, usdtPriceE } = this.state
        let params = {
            userid, username, parentid, flag, profitAmountS, profitAmountE, usdtAmountS, usdtAmountE,
            time, usdtPriceS, usdtPriceE,
            createtimeS: arrayTimeToStr(time,0),
            createtimeE:arrayTimeToStr(time,1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        
        const result = await this.request({ url: '/finProfitSupernodeDetail/list', type: 'post' }, params)
        this.setState({
            dataSource: result.pageUtils.list || [],
            pageTotal: result.pageUtils.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,
            profitAmountCount:result.profitAmountCount,
            usdtAmountCount:result.usdtAmountCount

        })
        

    }
    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', className: 'wordLine',dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '用户编号', className: 'wordLine',dataIndex: 'profituserid', },
            { title: '用户名', className: 'wordLine',dataIndex: 'profitusername' },
            { title: 'VDS数量', className: 'wordLine',dataIndex: 'profitamount' },
            { title: '分配价格', className: 'wordLine',dataIndex: 'usdtprice' },
            { title: 'USDT分配数量', className: 'wordLine',dataIndex: 'usdtamount' },
            { title: '分配总笔数', className: 'wordLine',dataIndex: 'supernodeprofitcount' },
            { title: '分配总金额', className: 'wordLine',dataIndex: 'supernodepfofitamount' },
            { title: '分配总权重', className: 'wordLine',dataIndex: 'supernodeprofitvipweight' },
            { title: '分配时间', className: 'wordLine',dataIndex: 'createtime', render: t => dateToFormat(t.time) },
            { title: '分配批次', className: 'wordLine',dataIndex: 'parentid' },
            { title: '分配状态', className: 'wordLine',dataIndex: 'flag', render: t => mapGet(DISTRIBUTE_STATE, t) },

        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal, userid, username, parentid, flag, profitAmountS, profitAmountE, usdtAmountS, usdtAmountE,
            time, usdtPriceS, usdtPriceE,profitAmountCount,usdtAmountCount } = this.state
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
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange} />

                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="username" value={username} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">分配批次：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="parentid" value={parentid} onChange={this.handleInputChange} />

                                        </div>
                                    </div>
                                </div>
                                <SeOp title='分配状态' value={flag} ops={DISTRIBUTE_STATE} onSelectChoose={v => this.onSelectChoose(v, 'flag')} pleaseC />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">VDS分配数量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="profitAmountS" value={profitAmountS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="profitAmountE" value={profitAmountE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">分配价格：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="usdtPriceS" value={usdtPriceS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="usdtPriceE" value={usdtPriceE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">USDT分配数量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="usdtAmountS" value={usdtAmountS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="usdtAmountE" value={usdtAmountE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">分配时间：</label>
                                        <div className="col-sm-8 ">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: SHOW_TIME_DEFAULT
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')}
                                                placeholder={['Start Time', 'End Time']}
                                                value={time} />

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
                                                    VDS总数量：{profitAmountCount || 0} ，&nbsp;&nbsp;&nbsp;
                                                    USDT总数量：{usdtAmountCount || 0} ，&nbsp;&nbsp;&nbsp;
                                                    总记录数：{pageTotal}
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