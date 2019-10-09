/**数据中心 》 统计报表 》 总账报表 》 收益汇总表  */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH,TIMEFORMAT_ss ,PAGRSIZE_OPTIONS20,DAYFORMAT} from '../../../conf'
import { Button, DatePicker, Select, Table, Modal,message } from 'antd'
import { toThousands } from '../../../utils'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
import FundsTypeList from '../../common/select/fundsTypeList'
const Big = require('big.js');
const Option = Select.Option;
const { Column } = Table;

export default class IncomeSummary extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            accountTypeVal: "0",
            fundsType: "0",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: '50',
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
            userId: "",
            userName: "",
            sortType: "",
            money: "",
            tradingFrozen: "",
            advertisingFrozen: "",
            totalMoney: "",
            walletfeesmin: "", //提现手续费
            walletfeesmax: "",
            futuresfeesmin: "",//期货手续费
            futuresfeesmax: "",
            feesmin: "",//币币手续费
            feesmax: "",
            otcfeesmin: "", //OTC手续费
            otcfeesmax: "",
            hedgingmin: "", //保值盈亏
            hedgingmax: "",
            quantitativemin: "", //量化盈亏
            quantitativemax: "",
            walletBalance: "",
            futuresBalance: "",
            currencyBalance: "",
            otcBalance: "",

            defaultBegin:'',
            defaultEnd:'',
            defaultTime:[]
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
        this.handleAccountChangeType = this.handleAccountChangeType.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
    }
    componentDidMount() {
        let time = moment().subtract(1,'days').format(DAYFORMAT)
        let t = moment().subtract(1,'days')
        console.log(t)
        console.log(time)
        this.setState({
            time:[moment(time+'00:00:00', TIMEFORMAT_ss), moment(time+'23:59:59', TIMEFORMAT_ss)],
            begin:time+' 00:00:00',
            end:time+' 23:59:59',
            defaultBegin:time+' 00:00:00',
            defaultEnd: time+' 23:59:59',
            defaultTime:[moment(time+'00:00:00', TIMEFORMAT_ss), moment(time+'23:59:59', TIMEFORMAT_ss)]
        },()=>this.requestTable())
        // this.setState({
        //     begin:time+' 00:00:00',
        //     end:time+' 23:59:59',
        //     // time:[moment(time+'00:00:00', TIMEFORMAT), moment(time+'23:59:59', TIMEFORMAT)],
        //     time:t,
        //     defaultBegin:time+' 00:00:00',
        //     defaultEnd: time+' 23:59:59',
        //     defaultTime:t
        // },()=>this.requestTable())
        
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
            begin: dateString[0],
            end: dateString[1],
            time: date
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
            fundsType: value
        })
    }
    handleAccountChangeType(value) {
        this.setState({
            accountTypeVal: value
        })
    }
    onResetState() {
        const { defaultBegin,defaultEnd,defaultTime } = this.state
        this.setState({
            fundsType: "0",
            time: null,
            userId: "",
            userName: "",
            walletfeesmin: "", //提现手续费
            walletfeesmax: "",
            futuresfeesmin: "",//期货手续费
            futuresfeesmax: "",
            feesmin: "",//币币手续费
            feesmax: "",
            otcfeesmin: "", //OTC手续费
            otcfeesmax: "",
            hedgingmin: "", //保值盈亏
            hedgingmax: "",
            quantitativemin: "", //量化盈亏
            quantitativemax: "",
            begin: defaultBegin,
            end: defaultEnd,
            time: defaultTime,
        })
    }
    requestSort(type) {
        this.setState({ sortType: type }, () => this.requestTable())
    }

    requestTable(currIndex, currSize) {
        const { fundsType, pageIndex, pageSize,
            walletfeesmin, //提现手续费
            walletfeesmax,
            futuresfeesmin,//期货手续费
            futuresfeesmax,
            feesmin,//币币手续费
            feesmax,
            otcfeesmin, //OTC手续费
            otcfeesmax,
            hedgingmin, //保值盈亏
            hedgingmax,
            quantitativemin, //量化盈亏
            quantitativemax,
            insurance, //保险留存
            begin ,
            end,
        } = this.state

            axios.post(DOMAIN_VIP + "/returnsSummary/query", qs.stringify({
                fundstype:fundsType,
                walletfeesmin, //提现手续费
                walletfeesmax,
                futuresfeesmin,//期货手续费
                futuresfeesmax,
                feesmin,//币币手续费
                feesmax,
                otcfeesmin, //OTC手续费
                otcfeesmax,
                hedgingmin, //保值盈亏
                hedgingmax,
                quantitativemin, //量化盈亏
                quantitativemax,
                insurance, //保险留存
                begintime:begin ,
                endtime:end,
                pageIndex: currIndex || pageIndex,
                pageSize: currSize || pageSize
            })).then(res => {
                const result = res.data;
                if (result.code == 0) {

                    Big.RM = 0;
                    let tableList = result.data.list;

                    tableList.map((item, index) => {
                        item.index = index + 1;
                        item.key = item.id;
                        item.checktime = moment(item.checktime).format('YYYY-MM-DD');
                        item.combined = new Big(item.fees?item.fees:0)
                        .plus(item.futuresfees?item.futuresfees:0)
                        .plus(item.brushvolume?item.brushvolume:0)
                        .plus(item.hedging?item.hedging:0)
                        .plus(item.insurance?item.insurance:0)
                        .plus(item.otcfees?item.otcfees:0)
                        .plus(item.quantitative?item.quantitative:0)
                        .plus(item.walletfees?item.walletfees:0);
                    })
                    this.setState({
                        tableList,
                        pageSize: result.data.pageSize,
                        pageTotal: result.data.totalCount
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
        const { showHide, fundsType, tableList, pageIndex, pageSize, pageTotal } = this.state
        const { time,
            walletfeesmin, //提现手续费
            walletfeesmax,
            futuresfeesmin,//期货手续费
            futuresfeesmax,
            feesmin,//币币手续费
            feesmax,
            otcfeesmin, //OTC手续费
            otcfeesmax,
            hedgingmin, //保值盈亏
            hedgingmax,
            quantitativemin, //量化盈亏
            quantitativemax,
         } = this.state;
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 总账报表 > 收益汇总表  
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                                    <FundsTypeList title='资金类型' fundsType={fundsType}  handleChange={this.handleChangeType}/>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label control-label-longest">提现手续费平台累计：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="walletfeesmin" value={walletfeesmin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="walletfeesmax" value={walletfeesmax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label control-label-longest">期货交易手续费累计：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="futuresfeesmin" value={futuresfeesmin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="futuresfeesmax" value={futuresfeesmax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label control-label-longest">币币交易手续费累计：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="feesmin" value={feesmin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="feesmax" value={feesmax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label control-label-longest">OTC交易手续费累计：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="otcfeesmin" value={otcfeesmin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="otcfeesmax" value={otcfeesmax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">量化盈亏：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="quantitativemin" value={quantitativemin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="quantitativemax" value={quantitativemax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保险留存：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="quantitativemin" value={quantitativemin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="quantitativemax" value={quantitativemax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保值盈亏：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="hedgingmin" value={hedgingmin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="hedgingmax" value={hedgingmax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div> */}
                                
                                <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
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
                                    <Table
                                        dataSource={tableList}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{emptyText:'暂无数据'}}
                                        // scroll={{ x: 1500 }}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            pageSizeOptions:PAGRSIZE_OPTIONS20,
                                            defaultPageSize:PAGESIZE,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }}>
                                        <Column title='序号' dataIndex='index' key='index'  />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                                        {/* <Column title='期货交易手续费累计' dataIndex='futuresfees' key='futuresfees' className="moneyGreen" render={(text)=>toThousands(text,true)} /> */}
                                        <Column title='币币交易手续费累计' dataIndex='fees' key='fees' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='法币广告费累计' dataIndex='otcfees' key='otcfees' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='提现手续费平台累计' dataIndex='walletfees' key='walletfees' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        {/* <Column title='保险留存' dataIndex='insurance' key='insurance' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='量化盈亏' dataIndex='quantitative' key='quantitative' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='保值盈亏' dataIndex='hedging' key='hedging' className="moneyGreen" render={(text)=>toThousands(text,true)} /> */}
                                        <Column title='合计' dataIndex='combined' key='combined' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='报表日期' dataIndex='checktime' key='checktime' />
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