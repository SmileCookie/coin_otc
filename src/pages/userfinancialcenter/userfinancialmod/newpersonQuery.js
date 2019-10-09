/**新人加成查询 */
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select, DatePicker } from 'antd'
import { PAGRSIZE_OPTIONS20,DISTRIBUTE_STATE, PAGESIZE, PAGEINDEX, TIMEFORMAT_ss, SELECTWIDTH, SHOW_TIME_DEFAULT } from '../../../conf'
import { toThousands, TE, mapGet, dateToFormat,arrayTimeToStr } from '../../../utils'
import { SeOp } from '../../../components/select/asyncSelect'
const { Option } = Select;
const { RangePicker } = DatePicker;

@Decorator()
export default class NewpersonQuery extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            profituserid: '',//用户编号' ,
            profitusernameLk: '',//用户名' ,
            parentid: '',//分配批次'
            flag: '',//分配状态
            profitamountLt: '',
            profitamountGt: '',// VDS分配数量区间
            usdtamountLt: '',
            usdtamountGt: '',// USDT分配数量区间
            usdtpriceLt: '',
            usdtpriceGt: '',//分配价格区间
            newvipweekuserLt: '',
            newvipweekuserGt: '',//分配总人数区间
            newvipweekamountLt: '',
            newvipweekamountGt: '',//分配总金额区间
            createtimeLt: '',
            createtimeGt: '',//分配时间区间
            time: []
        }
        this.state = {
            ...this.defaultState,
            profitamountall:'',
            usdtamountall:''
        }
    }
    // async componentDidMount() {

    // }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time, profituserid, profitusernameLk, parentid, flag, profitamountLt, profitamountGt, usdtamountLt,
            usdtamountGt, usdtpriceLt, usdtpriceGt, newvipweekuserLt, newvipweekuserGt, newvipweekamountLt, newvipweekamountGt, } = this.state
        let params = {
            time, profituserid, profitusernameLk, parentid, flag, profitamountLt, profitamountGt, usdtamountLt,
            usdtamountGt, usdtpriceLt, usdtpriceGt, newvipweekuserLt, newvipweekuserGt, newvipweekamountLt, newvipweekamountGt,
            createtimeLt: arrayTimeToStr(time,0),
            createtimeGt: arrayTimeToStr(time,1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/finNewVip/list', type: 'post' }, params)
        this.setState({
            dataSource: result.result.list || [],
            pageTotal: result.result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,
            profitamountall:result.sums.profitamountall,
            usdtamountall:result.sums.usdtamountall,
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
            { title: '分配总金额', className: 'wordLine',dataIndex: 'newvipweekamount' },
            { title: '分配时间', className: 'wordLine',dataIndex: 'createtime', render: t => dateToFormat(t.time) },
            { title: '分配批次', className: 'wordLine',dataIndex: 'parentid' },
            { title: '新人开始时间', className: 'wordLine',dataIndex: 'diststarttime', render: t => dateToFormat(t.time) },
            { title: '新人结束时间', className: 'wordLine',dataIndex: 'distendtime', render: t => dateToFormat(t.time) },
            { title: '分配状态', className: 'wordLine',dataIndex: 'flag', render: t => mapGet(DISTRIBUTE_STATE, t) },

        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal,time, profituserid, profitusernameLk, parentid, flag, profitamountLt, profitamountGt, usdtamountLt,
            usdtamountGt, usdtpriceLt, usdtpriceGt, newvipweekuserLt, newvipweekuserGt, newvipweekamountLt, newvipweekamountGt, profitamountall,usdtamountall } = this.state
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
                                            <input type="text" className="form-control" name="profituserid" value={profituserid} onChange={this.handleInputChange} />

                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="profitusernameLk" value={profitusernameLk} onChange={this.handleInputChange} />
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
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="profitamountLt" value={profitamountLt} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="profitamountGt" value={profitamountGt} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">分配价格：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="usdtpriceLt" value={usdtpriceLt} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="usdtpriceGt" value={usdtpriceGt} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">分配总人数：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="newvipweekuserLt" value={newvipweekuserLt} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="newvipweekuserGt" value={newvipweekuserGt} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">分配总金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="newvipweekamountLt" value={newvipweekamountLt} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="newvipweekamountGt" value={newvipweekamountGt} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">USDT分配数量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="usdtamountLt" value={usdtamountLt} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="usdtamountGt" value={usdtamountGt} onChange={this.handleInputChange} /></div>
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
                                                    VDS总数量：{profitamountall || 0} ，&nbsp;&nbsp;&nbsp;
                                                    USDT总数量：{usdtamountall || 0} ，&nbsp;&nbsp;&nbsp;
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