import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT,DAYFORMAT ,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Select,message } from 'antd'
import { tableScroll } from '../../../utils'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class UserPaymentTradeCount extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            beginTime:"",
            endTime:"",
            tableList:"",
            time:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            height:0,
            tableScroll:{
                tableId:'URPMETTAECT',
                x_panelId:'URPMETTAECTX',
                defaultHeight:500,
                height:0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        })
        this.requestTable(page,pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            beginTime:dateString[0]+" 00:00:00",
            endTime:dateString[1]+" 23:59:59",
            time:date
        })
       
    }
     //点击收起
     clickHide() {
        let { showHide,xheight,pageSize } = this.state;
            if(showHide&&pageSize>10){
                this.setState({
                    showHide: !showHide,
                    height:xheight,
                })
            }else{
                this.setState({
                    showHide: !showHide,
                    height:0
                })
            }
            // this.setState({
            //     showHide: !showHide,
            // })
    }
 
    requestTable(currIndex,currSize){
        const {beginTime,endTime,pageIndex,pageSize,pageTotal} = this.state
        axios.post(DOMAIN_VIP+'/userPaymentTradeCount/query',qs.stringify({
            beginTime,endTime,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
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
    onResetState(){
        this.setState({
            beginTime:"",
            endTime:"",
            time:[],
        })
    }
    render(){
        const {showHide,beginTime,endTime,tableList,time,pageIndex,pageSize,pageTotal}=this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 资金报表 > 用户充提交易统计
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">统计时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                        format="YYYY-MM-DD"
                                        placeholder={['Start Time', 'End Time']}
                                       onChange={this.onChangeCheckTime }
                                       value={time}
                                       />
                                        </div>
                                    </div>
                                </div>
                               
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                         }
                        <div className="x_panel">
                                <div className="x_content">
                                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto table-responsive-used" >

                                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                            <thead>
                                                <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">报表时间</th>
                                                <th className="column-title">充值用户总数</th>
                                                <th className="column-title">当日充值人数</th>
                                                <th className="column-title">当日首充人数</th>   
                                                <th className="column-title">当日提现人数</th>
                                                <th className="column-title">当日提空人数</th>
                                                <th className="column-title">提空且一周未登录用户人数</th> 
                                                <th className="column-title">当日用户交易人数</th>
                                                <th className="column-title">当日纯用户交易量（BTC市场）</th> 
                                                <th className="column-title">当日纯用户交易量（USDC市场）</th> 
                                                <th className="column-title">当日公司用户交易量（BTC市场）</th>
                                                <th className="column-title">当日公司用户交易量（USDC市场）</th>
                                                <th className="column-title">充值转化率</th>
                                                <th className="column-title">用户流失率</th>                   
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{moment(item.countDate).format(DAYFORMAT)}</td>
                                                            <td>{item.allDepositUserCount}</td>
                                                            <td>{item.depositUserCount}</td>
                                                            <td>{item.firstDepositCount}</td>
                                                            <td>{item.cashInCount}</td>
                                                            <td>{item.cashNullCount}</td>
                                                            <td>{item.cashNullNoLoginCount}</td>
                                                            <td>{item.transactionCount}</td>
                                                            <td>{item.userTransactionFeeBtc}</td>
                                                            <td>{item.userTransactionFeeUsdc}</td>
                                                            <td>{item.companyTransactionFeeBtc}</td>
                                                            <td>{item.companyTransactionFeeUsdc}</td>
                                                            <td>{item.rechargeConversionRate}</td>
                                                            <td>{item.churnRate}</td>
                                                        </tr>
                                                    )
                                                }):
                                                <tr className="no-record"><td colSpan="15">暂无数据</td></tr>
                                            }
                                        </tbody>
                                        </table>
                                    </div>
                                    <div className="pagation-box">
                                {
                                    pageTotal>0 && <Pagination
                                                size="small"
                                                current={pageIndex}
                                                total={pageTotal}
                                                onChange={this.changPageNum}
                                                showTotal={total => `总共 ${total} 条`}
                                                onShowSizeChange={this.onShowSizeChange}
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE}
                                                showSizeChanger
                                                showQuickJumper />
                                }
                                </div>
                                </div>
                            </div>

                    </div>
                </div>
            </div>
        )
        
    }
}