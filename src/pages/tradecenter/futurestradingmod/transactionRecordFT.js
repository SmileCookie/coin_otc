import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, Select, Pagination, message, DatePicker, Table } from "antd"
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { toThousands } from '../../../utils'
import HandicapMarket from '../../common/select/handicapMarketFT'
const { RangePicker } = DatePicker
const Option = Select.Option
const { Column } = Table

export default class TransactionRecordFT extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: 0,
            time: [],
            pagination: {
                showQuickJumper: true,
                showSizeChanger: true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            futuresid: '',
            side: '',
            shortuserid:"",
            longuserid:"",
            priceS: '',
            priceE: '',
            amountS: '',
            amountE: '',
            username: '',
            createtimeS: '',
            createtimeE: '',
            totalValue: "",                          
            totalAmount: "",                                  
            totalPrice: "",                                
            tableDataInterface: DOMAIN_VIP + "/transactionRecord/list",
            id:''
        }
    }
    componentDidMount() {
        this.requestTable()
    }
    componentWillUnmount() {

    }
    clickHide = () => {
        this.setState({
            showHide: !this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    resetState = () => {
        this.setState({
            time:"",
            side:"",
            futuresid:"",
            shortuserid:"",
            longuserid:"",
            priceS:"",
            priceE:"",
            amountS:"",
            amountE:"",
            username:"",
            createtimeS: '',
            createtimeE: '',
            id:'',
        })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        })
    }
    //市场
    handleSelectMarket = val => {
        this.setState({
            futuresid: val
        })
    }
    //
    handleMochikuraType = val => {
        this.setState({
            side:val
        })
    }
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const {
            summaryDataInterface,
            tableDataInterface,
            pageIndex,
            pageSize,
            side,
            futuresid,
            shortuserid,
            longuserid,
            priceS,
            priceE,
            amountS,
            amountE,
            username,
            createtimeS,
            createtimeE,
            id,
        } = this.state
        const parameter = {
            futuresid,
            shortuserid,
            longuserid,
            username,
            side,
            priceS,
            priceE,
            amountS,
            amountE,
            createtimeS,
            createtimeE,
            id,
            pageIndex:currentIndex|| pageIndex,
            pageSize:currentSize|| pageSize
        }
        axios.post(tableDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableList = result.data.list;
                tableList.map((item, index) => {
                    item.index = (result.data.currPage - 1) * result.data.pageSize + index + 1;
                    item.key = item.id;
                })               
                this.setState({
                    tableList,
                    pageSize: result.data.pageSize,
                    pageTotal: result.data.totalCount,
                    totalValue: result.totalValue,                       
                    totalAmount: result.totalAmount,                          
                    totalPrice: result.totalPrice,  
                })
            }
        })
    }
    //一键追踪
    tracking = (id) => {

    }
    //点击分页
    changPageNum = (page, pageSize)=>{
        this.setState({
            pageIndex: page
        }, () => this.requestTable(page,pageSize))

    }
    //分页的 pagesize 改变时
    onShowSizeChange = (current, size)=>{
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requestTable(current,size))
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            createtimeS:dateString[0]?moment(dateString[0]).format("x"):dateString[0],
            createtimeE:dateString[1]?moment(dateString[1]).format("x"):dateString[1],
            time: date
        })
    }
    sorter = (pagination, filters, sorter) => {
        // console.log(pagination, filters, sorter&&sorter.order&&sorter.order.slice(0,3)?sorter.order.slice(0,3):'')
        console.log(sorter)
    }
    render() {
        const {
            showHide,
            tableList,
            pageIndex,
            pageSize,
            pageTotal,
            time,
            side,
            futuresid,
            shortuserid,
            longuserid,
            priceS,
            priceE,
            amountS,
            amountE,
            username,
            totalValue,
            totalAmount,
            totalPrice,
            id,
        } = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 数据中心 > 期货交易中心 > 盘口管理 > 成交记录
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={futuresid} handleChange={this.handleSelectMarket} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="username" value={username} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="id" value={id} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">做空ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="shortuserid" value={shortuserid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">做多ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="longuserid" value={longuserid}  onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{ width: SELECTWIDTH }} value={side} onChange={this.handleMochikuraType}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='0' value={0}>做空</Option>
                                                <Option key='1' value={1}>做多</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交数量:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box" >
                                                <input type="text" className="form-control" name="amountS" value={amountS} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box" >
                                                <input type="text" className="form-control"  name="amountE" value={amountE} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交价格:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name="priceS" value={priceS} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name="priceE" value={priceE} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-5 col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交时间：</label>
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
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    成交数量：{toThousands(totalAmount,true)}&nbsp;&nbsp;
                                                    {/* 成交价格：{toThousands(totalPrice,true)}&nbsp;&nbsp; */}
                                                    成交价值： {totalValue}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table 
                                        dataSource={tableList}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{emptyText:'暂无数据'}}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            showSizeChanger: true,
                                            showQuickJumper: true,
                                            pageSizeOptions:PAGRSIZE_OPTIONS20,
                                            defaultPageSize:PAGESIZE
                                        }}>
                                            <Column title='序号' dataIndex= 'index' key= 'index'/>
                                            <Column title='期货市场' dataIndex='futuresid' key= 'futuresid'/>
                                            <Column title='成交编号' dataIndex= 'id' key= 'id'/>
                                            <Column title='成交价格' dataIndex= 'filledprice' key= 'filledprice' className= "moneyGreen" sorter= "true" render={(text)=>toThousands(text,true)}/>
                                            <Column title='成交数量' dataIndex= 'filledamount' key= 'filledamount'/>
                                            <Column title='成交价值' dataIndex= 'filledvalue' key= 'filledvalue' className= "moneyGreen" sorter= "true"/>
                                            <Column title='做多方用户ID' dataIndex= 'longuserid' key= 'longuserid'/>
                                            <Column title='做空方用户ID' dataIndex= 'shortuserid' key= 'shortuserid'/>
                                            <Column title='成交类型' dataIndex= 'side' key= 'side' render={(text)=>{
                                                return text==0?"做空":"做多"
                                            }}/>
                                            <Column title='成交时间' dataIndex= 'createtime' key= 'createtime' render={(text)=>{
                                                return moment(text).format(TIMEFORMAT_ss)
                                            }}/>
                                            <Column title='处理状态' dataIndex= 'status' key= 'status' render={(text)=>{
                                                return text==0?"初始":text==1?"处理中":"处理完成"
                                            }}/>
                                            <Column title='操作' dataIndex= 'action' key= 'action' render={(text)=>{
                                                return <a href="javascript:void(0);" onClick={()=>{this.tracking(text)}}>一键追踪</a>
                                            }}/>
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