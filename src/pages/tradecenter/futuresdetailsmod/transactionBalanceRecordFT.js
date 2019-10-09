import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Modal } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,PAGRSIZE_OPTIONS,PAGESIZE_50,TIMEFORMAT_ss,TIMEFORMAT, PAGRSIZE_OPTIONS20} from '../../../conf'
import { toThousands } from '../../../utils'
const { RangePicker } = DatePicker
const Option = Select.Option

export default class TransactionBalanceRecordFT extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            tableList:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            time:[],
            userid:'',
            orderid:'',
            userName:'',
            createtimeS:'',
            createtimeE:'',
        }
    }
    componentDidMount(){
        this.requestTable()
    }
    componentWillUnmount(){

    }
    clickHide = () => {
        this.setState({
            showHide:!this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    resetState = () => {
        this.setState({
            time:[],
            userid:'',
            orderid:'',
            userName:'',
            createtimeS:'',
            createtimeE:'',
        })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name] : value
        })
    }
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        // console.log(date, dateString);
        this.setState({
            createtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            createtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            time:date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize,userName,userid,createtimeE,createtimeS,orderid, } = this.state
        axios.post(DOMAIN_VIP+'/transReset/list',qs.stringify({
            userName,userid,createtimeE,createtimeS,orderid,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    onChangePageNum = (pageIndex,pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    render(){
        const { showHide,tableList,pageIndex,pageSize,pageTotal,time,userName,userid,orderid, } = this.state
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 财务中心 > 期货流水明细 > 成交结余记录
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='userid' value={userid} onChange={this.handleInputChange}/>
                                            {/* <b className="icon-fuzzy">%</b> */}
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='' value={''} onChange={this.handleInputChange}/>
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='orderid' value={orderid} onChange={this.handleInputChange}/>
                                            {/* <b className="icon-fuzzy">%</b> */}
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">处理状态:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={''} onChange={this.handleSelectDealStatus}>
                                                <Option key='0' value={0}>强平增加</Option>
                                                <Option key='1' value={1}>穿仓减少</Option>
                                                <Option key='2' value={2}>收益转出</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='userName' value={userName} onChange={this.handleInputChange}/>
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">结余时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onChangeCheckTime}
                                                value={time} />
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
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">用户编号</th>
                                                <th className="column-title">委托ID</th>
                                                <th className="column-title">结余金额</th>
                                                <th className="column-title">结余时间</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.userid}</td>
                                                            <td>{item.orderid}</td>
                                                            <td className='moneyGreen'>{toThousands(item.suplusamount,true)}</td>
                                                            <td>{item.createtime?moment(item.createtime).format(TIMEFORMAT):'--'}</td>
                                                        </tr>
                                                    )
                                                }):<tr className="no-record"><td colSpan="7">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                    <div className="pagation-box">
                                        {
                                            pageTotal > 0 && <Pagination 
                                                size="small"
                                                current={pageIndex}
                                                pageSize={pageSize} 
                                                total={pageTotal}
                                                showTotal={total => `总共${total}条`}
                                                onChange={this.onChangePageNum}
                                                onShowSizeChange={this.onShowSizeChange}
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE}
                                                showQuickJumper
                                                showSizeChanger/>
                                        }
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}