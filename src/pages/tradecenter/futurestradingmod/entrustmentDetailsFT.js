import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Table } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,PAGRSIZE_OPTIONS20,PAGESIZE_20,TIMEFORMAT_ss} from '../../../conf'
import {toThousands} from '../../../utils'
import HandicapMarket from '../../common/select/handicapMarketFT'
const { RangePicker } = DatePicker
const Option = Select.Option
const { Column } = Table

export default class EntrustmentDetailsFT extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: 6,
            userid: "",
            futuresid: "BTC",
            time: [],
            pagination: {
                showQuickJumper: true,
                showSizeChanger: true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            userid:'',
            futuresid:'',
            side:'',
            status:'',
            priceS:'',
            priceE:'',
            amountS:'',
            amountE:'',
            username:'',
            createtimeS:'',
            createtimeE:'',
            type:'',
            action:'',
            id:'',

            tableDataInterface: DOMAIN_VIP + "/entrustmentDetails/list",
        }
    }
    componentDidMount() {
        this.requestTable()
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
            userid:'',
            futuresid:'',
            side:'',
            status:'',
            priceS:'',
            priceE:'',
            amountS:'',
            amountE:'',
            username:'',
            createtimeS:'',
            createtimeE:'',
            type:'',
            action:'',
            time: "",
            id:''
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
    //委托类型
    handleSelectDetegateType = val => {
        this.setState({
            side: val
        })
    }
    //委托状态
    handleSelectDetegateStatus = val => {
        this.setState({
            status: val
        })
    }
    //市场
    handleSelectMarket = val => {
        this.setState({
            futuresid: val
        })
    }
    //委托价格类型
    selectDetegatePrice = val => {
        this.setState({
            type: val
        })
    }
    //委托动作类型
    selectDetegateAction = val => {
        this.setState({
            action: val
        })
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            createtimeS:dateString[0]?moment(dateString[0]).format("x"):dateString[0],
            createtimeE:dateString[1]?moment(dateString[1]).format("x"):dateString[1],
            time: date
        })
    }
    requestTable(currIndex, currSize) {
        const {
            tableDataInterface,
            pageIndex,
            pageSize,
            side,
            userid,
            futuresid,
            status,
            priceS,
            priceE,
            amountS,
            amountE,
            username,
            createtimeS,
            createtimeE,
            type,
            action,
            id
        } = this.state
        const parameter = {
            userid,
            futuresid,
            status,
            priceS,
            side,
            priceE,
            amountS,
            amountE,
            username,
            createtimeS,
            createtimeE,
            type,
            action,
            pageIndex:currIndex|| pageIndex,
            pageSize:currSize|| pageSize,
            id,
        }
        axios.post(tableDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableList = result.data.list;
                tableList.map((item, index) => {
                    item.index = (result.data.currPage-1)*result.data.pageSize+index+1;
                    item.key = item.id;
                })
                this.setState({
                    tableList,
                    pageSize: result.data.pageSize,
                    pageTotal: result.data.totalCount,
                    totalValue: result.totalValue,
                    totalAmount: result.totalAmount,
                    totalCostmargin: result.totalCostmargin,
                })
            }
        })

    }
    //点击分页
    changPageNum=(page, pageSize)=>{
        this.setState({
            pageIndex: page
        })
        this.requestTable(page,pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange=(current, size)=>{
        this.setState({
            pageIndex: current,
            pageSize: size
        })
        this.requestTable(current,size)
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
            userid,
            futuresid,
            status,
            priceS,
            priceE,
            amountS,
            amountE,
            username,
            type,
            action,
            totalValue,
            totalAmount,
            totalCostmargin,id
        } = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 数据中心 > 期货交易中心 > 盘口管理 > 委托明细
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
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">委托编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="id" value={id} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托方向:</label>
                                        <div className="col-sm-8">
                                            <Select style={{ width: SELECTWIDTH }} value={side} onChange={this.handleSelectDetegateType}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='0' value={0}>做空</Option>
                                                <Option key='1' value={1}>做多</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托状态:</label>
                                        <div className="col-sm-8">
                                            <Select style={{ width: SELECTWIDTH }} value={status} onChange={this.handleSelectDetegateStatus}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='0' value={0}>初始</Option>
                                                <Option key='1' value={1}>取消</Option>
                                                <Option key='2' value={2}>完成</Option>
                                                <Option key='3' value={3}>部分成交</Option>
                                                <Option key='4' value={4}>部分取消</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托价格:</label>
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
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托数量:</label>
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
                                        <label className="col-sm-3 control-label">委托价格类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{ width: SELECTWIDTH }} value={type} onChange={this.selectDetegatePrice}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='0' value={0}>限价</Option>
                                                <Option key='1' value={1}>市价</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托动作类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{ width: SELECTWIDTH }} value={action} onChange={this.selectDetegateAction}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='-1' value={-1}>取消</Option>
                                                <Option key='1' value={1}>开仓</Option>
                                                <Option key='2' value={2}>平仓</Option>
                                                <Option key='3' value={3}>强平</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                style={{ width: SELECTWIDTH }}
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
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
                                <div className="table-responsive table-box">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    委托数量：{totalAmount} &nbsp;&nbsp;&nbsp;
                                                    委托价值：{toThousands(totalValue,true)} &nbsp;&nbsp;&nbsp;
                                                    保证金：{toThousands(totalCostmargin,true)}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table 
                                        dataSource={tableList}
                                        bordered={true}
                                        onChange={this.sorter}
                                        scroll={{x:tableList.length>0?1760:1680}}
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
                                            <Column title= '序号'  dataIndex= 'index'  key= 'index' fixed />
                                            <Column title= '期货市场'  dataIndex= 'futuresid'  key= 'futuresid' fixed  />
                                            <Column title= '用户编号'  dataIndex= 'userid'  key= 'userid' fixed />
                                            <Column title= '委托编号'  dataIndex= 'id'  key= 'id' />
                                            <Column title= '委托方向'  dataIndex= "side"  key= "side" render={(text)=>{
                                                        switch (text) {
                                                            case 0:
                                                                return "做空";
                                                            case 1:
                                                                return "做多"
                                                        }
                                            }} />
                                            <Column title= '委托价格类型'  dataIndex= 'type'  key= 'type' render={(text)=>{
                                                switch (text) {
                                                    case 0:
                                                        return "限价";
                                                    case 1:
                                                        return "市价"
                                                }
                                            }} />
                                            <Column title= '委托动作类型'  dataIndex= "action"  key= "action" render={(text)=>{
                                                        switch (text) {
                                                            case -1:
                                                                return  "取消";
                                                            case 1:
                                                                return "开仓"
                                                            case 2:
                                                                return "平仓"
                                                            case 3:
                                                                return "强平"
                                                        }
                                            }} />
                                            <Column title= '委托价格'  dataIndex= 'price'  key= 'price'  className= "moneyGreen"  sorter= "true" render={(text)=>toThousands(text,true)}  />
                                            <Column title= '委托数量'  dataIndex= "amount"  key= "amount"  />
                                            <Column title= '已成交数量'  dataIndex= "finishedamount"  key= "finishedamount"  />
                                            <Column title= '委托价值'  dataIndex= 'value'  key= 'value'  className= "moneyGreen"  sorter= "true"  />
                                            <Column title= '委托杠杆'  dataIndex= 'leverage'  key= 'leverage'  />
                                            {/* <Column title= '成本保证金'  dataIndex= 'costmargin'  key= 'costmargin'  className= "moneyGreen"  sorter= "true" render={(text)=>toThousands(text,true)}  /> */}
                                            <Column title= '开仓佣金'  dataIndex= 'openorderfee'  key= 'openorderfee'  className= "moneyGreen"  sorter= "true" render={(text)=>toThousands(text,true)}  />
                                            <Column title= '平仓佣金'  dataIndex= 'closeorderfee'  key= 'closeorderfee'  className= "moneyGreen"  sorter= "true" render={(text)=>toThousands(text,true)}  />
                                            <Column title= '开仓保证金'  dataIndex= 'costmargin'  key= 'costmargin'  className= "moneyGreen"  sorter= "true" render={(text)=>toThousands(text,true)}  />
                                            <Column title= '委托时间'  dataIndex= 'createtime'  key= 'createtime' render={(text)=>{
                                                return moment(text).format(TIMEFORMAT_ss)
                                            }} />
                                            <Column title= '委托状态'  dataIndex= "status"  key= "status" render={(text)=>{
                                                 switch (text) {
                                                        case 0:
                                                            return "初始";
                                                        case 1:
                                                            return  "取消"
                                                        case 2:
                                                            return  "完成"
                                                        case 3:
                                                            return  "部分成交"
                                                        case 4:
                                                            return  "部分取消"
                                                    }
                                            }} />
        
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