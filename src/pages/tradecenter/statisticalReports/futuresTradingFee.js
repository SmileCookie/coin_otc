/**数据中心 》 统计报表 》 期货报表 》 手续费明细  */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH,TIMEFORMAT,TIMEFORMAT_ss ,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button, DatePicker, Select, Table, Modal,message } from 'antd'
import {toThousands} from '../../../utils'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
import HandicapMarket from '../../common/select/handicapMarketFT'
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;

export default class FuturesTradingFee extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            dealType: [
                <Option key='0' value=''>请选择</Option>,
                <Option key='1' value='1'>做空获取</Option>,
                <Option key='2' value='2'>做空支付</Option>,
                <Option key='3' value='3'>做多获取</Option>,
                <Option key='4' value='4'>做多支付</Option>,
                // <Option key='5' value='5'>空开支付</Option>,
                // <Option key='6' value='6'>空开获得</Option>,
                // <Option key='7' value='7'>空平支付</Option>,
                // <Option key='8' value='8'>空平获得</Option>
            ],
            pageIndex: PAGEINDEX,
            pageSize: 50,
            pageTotal: 0,
            time:[],

            userId:'',
            futuresId:'',
            tradeId:'',
            typeAdd:'',
            feemin:'',
            feemax:'',
            createTimeMin:'',
            createTimeMax:'',
            tradepricesum:'',
            tradeamountsum:'',
            amountsum:'',
            feeratiosum:'',
            feesum:''
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
            createTimeMin: moment(dateString[0]).format('x'),
            createTimeMax: moment(dateString[1]).format('x'),
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
            futuresId: value
        })
    }
    handleAccountChangeType(value) {
        this.setState({
            typeAdd: value
        })
    }
    onResetState() {
        this.setState({
            time: null,
            userId:'',
            futuresId:'',
            tradeId:'',
            typeAdd:'',
            feemin:'',
            feemax:'',
            createTimeMin:'',
            createTimeMax:''
        })
    }
    requestSort(type) {
        this.setState({ sortType: type }, () => this.requestTable())
    }
    requestTable(currIndex, currSize) {
        const { userId,futuresId,tradeId,typeAdd,feemax,feemin,pageIndex,pageSize,createTimeMin,createTimeMax } = this.state;
        
        const parameter = {
            userId,
            futuresId,
            tradeId,
            typeAdd,
            feemax,
            feemin,
            createTimeMin,
            createTimeMax,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        axios.post(DOMAIN_VIP + "/tradingRecord/query", qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = i+1;
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
        axios.post(DOMAIN_VIP + "/tradingRecord/sum", qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    tradepricesum:result.data.tradepricesum,
                    tradeamountsum:result.data.tradeamountsum,
                    amountsum:result.data.amountsum,
                    feeratiosum:result.data.feeratiosum,
                    feesum:result.data.feesum,
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
        const { showHide,dealType, tableList, pageIndex, pageSize, pageTotal,userId, time, currencyTradingFeeMin, currencyTradingFeeMax, insuranceFundMin, insuranceFundMax,futuresId,tradeId,typeAdd,feemax,feemin,tradeamountsum,tradepricesum,amountsum,feeratiosum,feesum } = this.state;

        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 期货报表 > 手续费明细 
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={futuresId} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="tradeId" value={tradeId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={typeAdd} style={{ width: SELECTWIDTH }} onChange={this.handleAccountChangeType} >
                                                {dealType}
                                            </Select>

                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易手续费：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="feemin" value={feemin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="feemax" value={feemax} onChange={this.handleInputChange} /></div>
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
                                                成交价格：{ toThousands(tradepricesum,true) }，
                                                成交数量：{ toThousands(tradeamountsum,true) }，
                                                成交金额：{ toThousands(amountsum,true) }，
                                                {/* 交易手续费率：{ toThousands(feeratiosum,true) }， */}
                                                交易手续费：{ toThousands(feesum,true) }
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table
                                        dataSource={tableList}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{emptyText:'暂无数据'}}
                                        // scroll={pageSize > 10 ? { y: 500 } : {}}
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

                                        <Column title='序号' dataIndex='index' key='index' render={(text)=>text} />
                                        <Column title='期货市场' dataIndex='futuresid' key='futuresid' />
                                        <Column title='用户编号' dataIndex='userid' key='userid' />
                                        <Column title='成交编号' dataIndex='tradeid' key='tradeid' />
                                        <Column title='交易类型' dataIndex='typeadd' key='typeadd' />
                                        <Column title='成交价格' dataIndex='tradeprice' key='tradeprice' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='成交数量' dataIndex='tradeamount' key='tradeamount' className="moneyGreen" />
                                        <Column title='成交价值' dataIndex='amount' key='amount' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='交易手续费率' dataIndex='feeratio' key='feeratio' />
                                        <Column title='交易手续费' dataIndex='fee' key='fee' className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='报表日期' dataIndex='createtime' key='createtime' render={(text)=>moment(text).format(TIMEFORMAT)}/>
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