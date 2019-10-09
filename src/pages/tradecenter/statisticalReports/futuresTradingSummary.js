/**数据中心 》 统计报表 》 期货报表 》 期货交易汇总表  */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH,TIMEFORMAT,TIMEFORMAT_ss ,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button, DatePicker, Select, Table, Modal,message } from 'antd'
import HandicapMarket from '../../common/select/handicapMarketFT'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;

export default class FuturesTradingSummary extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: '50',
            pageTotal: 0,
 
            time: [],
            sortType: "",

            futuresid:'',
            filledamountsumS:'',
            filledamountsumE:'',
            openlongpositionsS:'',
            openlongpositionsE:'',
            openshortpositionsS:'',
            openshortpositionsE:'',
            holdlongpositionsS:'',
            holdlongpositionsE:'',
            holdshortpositionsS:'',
            holdshortpositionsE:'',
            reportdateStart:'',
            reportdateEnd:'',

            insurancechangesSum:'',
            insuranceretainedchangesSum:'',
            longblowingupSum:'',
            longunderweightSum:'',
            shortblowingupSum:'',
            shortunderweightSum:'',
            filledamountsumAllSum:'',
            holdlongpositionsSum:'',
            holdshortpositionsSum:'',
            openlongpositionsSum:'',
            openshortpositionsSum:'',
            transactionsnumberSum:'',


        }
        this.clickHide = this.clickHide.bind(this);
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.requestTable = this.requestTable.bind(this);
        this.handleChangeType = this.handleChangeType.bind(this);
        this.onResetState = this.onResetState.bind(this);
        this.changPageNum = this.changPageNum.bind(this);
        this.onShowSizeChange = this.onShowSizeChange.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.requestSort = this.requestSort.bind(this);
        this.handleChangeTable = this.handleChangeTable.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
    }
    componentDidMount() {
        this.requestTable()
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    handleChangeTable(pagination, filters, sorter) {

        console.log(sorter);
        // sorter.field
        // sorter.order
        // this.setState({
        //     sortType: sorter.order
        // }, () => this.requestTable());
    }
    handleInputChange(e) {
        const target = e.target;
        const value = target.value;
        const name = target.name
        let json = new Object();
        json[name] = value;
        this.setState(json);
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({            
            reportdateStart: dateString[0],
            reportdateEnd: dateString[1],
            time:date
        })
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestTable(page,pageSize))

    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requestTable(current,size))
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    handleChangeType(value) {
        this.setState({
            futuresid: value
        })
    }
    onResetState() {
        this.setState({
            futuresid:'',
            filledamountsumS:'',
            filledamountsumE:'',
            openlongpositionsS:'',
            openlongpositionsE:'',
            openshortpositionsS:'',
            openshortpositionsE:'',
            holdlongpositionsS:'',
            holdlongpositionsE:'',
            holdshortpositionsS:'',
            holdshortpositionsE:'',
            reportdateStart:'',
            reportdateEnd:'',
            time:[]
        })
    }
    requestSort(type) {
        this.setState({ sortType: type }, () => this.requestTable())
    }
    requestTable(currIndex, currSize) {
        const { pageIndex, pageSize, futuresid,filledamountsumS,filledamountsumE,openlongpositionsS,openlongpositionsE,openshortpositionsS,openshortpositionsE,holdlongpositionsS,holdlongpositionsE,
        holdshortpositionsS,holdshortpositionsE,reportdateStart,reportdateEnd} = this.state
        axios.post(DOMAIN_VIP + "/transactionfutures/list", qs.stringify({
            futuresid,filledamountsumS,filledamountsumE,openlongpositionsS,openlongpositionsE,openshortpositionsS,openshortpositionsE,holdlongpositionsS,holdlongpositionsE,
        holdshortpositionsS,holdshortpositionsE,reportdateStart,reportdateEnd,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }                
                this.setState({
                    tableList:tableSource,
                    pageTotal:result.data.totalCount
                })              
            }else{
                message.warning(result.msg)
            }
        })
        axios.post(DOMAIN_VIP + "/transactionfutures/sum", qs.stringify({
            futuresid,filledamountsumS,filledamountsumE,openlongpositionsS,openlongpositionsE,openshortpositionsS,openshortpositionsE,holdlongpositionsS,holdlongpositionsE,
        holdshortpositionsS,holdshortpositionsE,reportdateStart,reportdateEnd,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    insurancechangesSum:result.data.insurancechangesSum,
                    insuranceretainedchangesSum:result.data.insuranceretainedchangesSum,
                    longblowingupSum:result.data.longblowingupSum,
                    longunderweightSum:result.data.longunderweightSum,
                    shortblowingupSum:result.data.shortblowingupSum,
                    shortunderweightSum:result.data.shortunderweightSum,
                    filledamountsumAllSum:result.data.filledAmountSumAll,
                    holdlongpositionsSum:result.data.holdlongpositionsSum,
                    holdshortpositionsSum:result.data.holdshortpositionsSum,
                    openlongpositionsSum:result.data.openlongpositionsSum,
                    openshortpositionsSum:result.data.openshortpositionsSum,
                    transactionsnumberSum:result.data.transactionsnumberSum,
                })             
                             
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCancel() {
        this.setState({ visible: false })
    }
    footer() {

    }
    render() {
        Big.RM = 0;
        const { 
            showHide,tableList, pageIndex, pageTotal,time ,
            futuresid,
            filledamountsumS,
            filledamountsumE,
            openlongpositionsS,
            openlongpositionsE,
            openshortpositionsS,
            openshortpositionsE,
            holdlongpositionsS,
            holdlongpositionsE,
            holdshortpositionsS,
            holdshortpositionsE,
            filledamountsumAllSum,
            holdlongpositionsSum,
            holdshortpositionsSum,
            insurancechangesSum,
            insuranceretainedchangesSum,
            longblowingupSum,
            longunderweightSum,
            openlongpositionsSum,
            openshortpositionsSum,
            shortblowingupSum,
            shortunderweightSum,
            transactionsnumberSum,
        } = this.state

        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 期货报表 > 期货交易汇总表 
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={futuresid} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易总量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="filledamountsumS" value={filledamountsumS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="filledamountsumE" value={filledamountsumE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">开多仓量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="openlongpositionsS" value={openlongpositionsS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="openlongpositionsE" value={openlongpositionsE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">开空仓量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="openshortpositionsS" value={openshortpositionsS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="openshortpositionsE" value={openshortpositionsE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">持多仓量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="holdlongpositionsS" value={holdlongpositionsS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="holdlongpositionsE" value={holdlongpositionsE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">持空仓量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="holdshortpositionsS" value={holdshortpositionsS} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="holdshortpositionsE" value={holdshortpositionsE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">报表日期：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">                           
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table style={{margin:'0'}} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{textAlign:'left'}} colSpan="17" className="column-title">
                                                总交易量：{filledamountsumAllSum}，
                                                开多仓量：{openlongpositionsSum ? openlongpositionsSum : 0}，
                                                开空仓量：{openshortpositionsSum ? openshortpositionsSum : 0}，
                                                持多仓量：{holdlongpositionsSum ? holdlongpositionsSum : 0}，
                                                持空仓量：{holdshortpositionsSum ? holdshortpositionsSum : 0}，
                                                多手爆仓量：{longblowingupSum ? longblowingupSum : 0}，
                                                空手爆仓量：{shortblowingupSum ? shortblowingupSum : 0}，
                                                自动减多仓量：{longunderweightSum ? longunderweightSum : 0}，
                                                自动减空仓量：{shortunderweightSum ? shortunderweightSum : 0}，
                                                保险基金变动量：{insurancechangesSum ? insurancechangesSum : 0}，
                                                保险留存变动量：{insuranceretainedchangesSum ? insuranceretainedchangesSum : 0}，
                                                交易人数：{transactionsnumberSum ? transactionsnumberSum : 0}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table
                                        dataSource={tableList}
                                        // scroll={{ x: 1580 }}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{emptyText:'暂无数据'}}
                                        pagination={{
                                            size: "small", current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            pageSizeOptions:PAGRSIZE_OPTIONS20,
                                            defaultPageSize:PAGESIZE,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }}>
                                            
                                            <Column title= '序号'  dataIndex= 'index'  key= 'index'   />
                                            <Column title= '期货市场'  dataIndex= 'futuresid'  key= 'futuresid'  />
                                            <Column title= '总交易量'  dataIndex= 'filledamountsum'  key= 'filledamountsum'  sorter= "true"  className= "moneyGreen"   />
                                            <Column title= '开多仓量'  dataIndex= 'openlongpositions'  key= 'openlongpositions'  sorter= "true"  className= "moneyGreen"  />
                                            <Column title= '开空仓量'  dataIndex= 'openshortpositions'  key= 'openshortpositions'  sorter= "true"  className= "moneyGreen"  />
                                            <Column title= '持多仓量'  dataIndex= 'holdlongpositions'  key= 'holdlongpositions'  sorter= "true"  className= "moneyGreen"  />
                                            <Column title= '持空仓量'  dataIndex= 'holdshortpositions'  key= 'holdshortpositions'  sorter= "true"  className= "moneyGreen"  />
                                            <Column title= '多手爆仓量'  dataIndex= 'longblowingup'  key= 'longblowingup'  className= "moneyGreen"  />
                                            <Column title= '空手爆仓量'  dataIndex= 'shortblowingup'  key= 'shortblowingup'  className= "moneyGreen"  />
                                            <Column title= '自动减多仓量'  dataIndex= 'longunderweight'  key= 'longunderweight'  className= "moneyGreen"  />
                                            <Column title= '自动减空仓量'  dataIndex= 'shortunderweight'  key= 'shortunderweight'  className= "moneyGreen"  />
                                            <Column title= '保险基金变动量'  dataIndex= 'insurancechanges'  key= 'insurancechanges'  className= "moneyGreen" />
                                            <Column title= '保险留存变动量'  dataIndex= 'insuranceretainedchanges'  key= 'insuranceretainedchanges'  className= "moneyGreen" />
                                            <Column title= '交易人数'  dataIndex= 'transactionsnumber'  key= 'transactionsnumber' />
                                            <Column title= '报表日期'  dataIndex= 'reportdate'  key= 'reportdate'  render={(text)=>{ return text?moment(text).format(TIMEFORMAT):'--'}} />
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