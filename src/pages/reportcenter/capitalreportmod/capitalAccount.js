import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import SelectAType from '../../financialcenter/select/selectAType'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,NUMBERPOINT,TIMEFORMAT_DAYS_ss,TIMEFORMAT,DAYFORMAT ,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,DatePicker,Pagination } from 'antd'
import { toThousands } from '../../../utils'
import moment from 'moment'
const Big = require('big.js')

export default class CapitalAccount extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            fundsType:'0',
            startValue: null,
            endValue: null,
            endOpen: false,
            tableList:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            begin:'',
            end:'',
            pageTotal:0
        }
        
        this.clickHide = this.clickHide.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.disabledStartDate = this.disabledStartDate.bind(this)
        this.disabledEndDate = this.disabledEndDate.bind(this)
        this.onChange = this.onChange.bind(this)
        this.onStartChange = this.onStartChange.bind(this)
        this.onEndChange = this.onEndChange.bind(this)
        this.handleStartOpenChange = this.handleStartOpenChange.bind(this)
        this.handleEndOpenChange = this.handleEndOpenChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.queryClickBtn = this.queryClickBtn.bind(this)
    }

    componentDidMount(){
        this.requestTable()
    }

    requestTable(){
        const { fundsType,startValue,begin,end,endValue,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+"/capitalaccount/query",qs.stringify({
            fundsType,
            begin,
            end,
            pageIndex,
            pageSize
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                console.log(result)
                this.setState({
                tableList:result.data.list,
                pageTotal:result.data.totalCount,
                })
            }
        })
    }

    //查询按钮
    queryClickBtn(val){
        this.setState({
            pageIndex:PAGEINDEX
        },()=>this.requestTable())
    }

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTable())
        
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        
        this.setState({
            pageIndex:current,
            pageSize:size
        },()=>this.requestTable())
    }
    
    //重置状态
    onResetState(){
        this.setState({
            fundsType:'0',
            startValue: null,
            endValue: null,
            endOpen: false,
            begin:'',
            end:'',
        })
    }

    //时间控件  开始
    disabledStartDate(startValue){
        const endValue = this.state.endValue;
        if (!startValue || !endValue) {
          return false;
        }
        return startValue.valueOf() > endValue.valueOf();
    }
    
    disabledEndDate(endValue){
        const startValue = this.state.startValue;
        if (!endValue || !startValue) {
          return false;
        }
        return endValue.valueOf() <= startValue.valueOf();
    }
    
    onChange(field, value){
        const name = field=="startValue"?"begin":"end"
        this.setState({
          [field]: value,
          [name]:value.format("YYYY-MM-DD HH:mm:ss")
        });
    }

    onStartChange(value){
        this.onChange('startValue', value);
    }
    
    onEndChange(value){
        this.onChange('endValue', value);
    }
    
    handleStartOpenChange(open){
        if (!open) {
          this.setState({ endOpen: true });
        }
    }
    
    handleEndOpenChange(open){
        this.setState({ endOpen: open });
    }
     //时间控件  结束

    //资金类型 select
    handleChangeType(val){
        this.setState({
            fundsType:val
        })
    }

    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }

    render(){
        Big.RM = 0;
        const { showHide,fundsType,startValue,endValue,pageTotal,endOpen,tableList,pageIndex,pageSize } = this.state
        return(
            <div className="right-con">
            <div className="page-title">
                当前位置：对账中心 > 钱包对账 > 交易平台资金总账
                <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
            </div>
            <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide &&<div className="x_panel">
                        <div className="x_content">
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <SelectAType col='3' findsType={fundsType} handleChange={this.handleChangeType}></SelectAType>
                            </div>

                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">开始时间：</label>
                                    <div className="col-sm-8">
                                        <DatePicker
                                            disabledDate={this.disabledStartDate}
                                            showTime={{
                                                defaultValue : moment('00:00:00', 'HH:mm:ss')
                                            }}
                                            format="YYYY-MM-DD HH:mm:ss"
                                            value={startValue}
                                            placeholder="Start"
                                            onChange={this.onStartChange}
                                            onOpenChange={this.handleStartOpenChange}
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">结束时间：</label>
                                    <div className="col-sm-8">
                                        <DatePicker
                                            
                                            disabledDate={this.disabledEndDate}
                                            showTime={{
                                                defaultValue : moment('23:59:59', 'HH:mm:ss')
                                            }}
                                            format="YYYY-MM-DD HH:mm:ss"
                                            value={endValue}
                                            placeholder="End"
                                            onChange={this.onEndChange}
                                            open={endOpen}
                                            onOpenChange={this.handleEndOpenChange}
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="col-md-4 col-sm-4 col-xs-4 right">
                                <div className="right">
                                    <Button type="primary" onClick={this.queryClickBtn}>查询</Button>
                                    <Button type="primary" onClick={this.onResetState}>重置</Button>                                    
                                </div>
                            </div>
                        </div>
                    </div>
                    }

                    <div className="x_panel">
                        <div className="x_content">
                            <div className="table-responsive-fixed">
                                <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                                    <thead>
                                        <tr className="headings">
                                            <th className="column-title">序号</th>
                                            <th className="column-title">报表时间</th>
                                            <th className="column-title">资金类型</th>
                                            <th className="column-title">交易平台金额(11+4+9+10-3+6-N+W)</th>
                                            <th className="column-title">外部调账差额(N)</th>   
                                            <th className="column-title">内部调账差额(N)</th>
                                            <th className="column-title">交易平台内部差额(12-11-9+N)</th>
                                            <th className="column-title">充值(1)</th> 
                                            <th className="column-title">提现(2)</th>
                                            <th className="column-title">系统充值(3)</th> 
                                            <th className="column-title">系统扣除(4)</th> 
                                            <th className="column-title">系统分发(5)</th>
                                            <th className="column-title">ICO兑换(6)</th>
                                            <th className="column-title">交易卖出(7)</th>
                                            <th className="column-title">交易买入(8)</th>
                                            <th className="column-title">交易手续费(9)</th>   
                                            <th className="column-title">提现手续费(10)</th>
                                            <th className="column-title">手续费转入回购账户</th> 
                                            <th className="column-title">回购失败退回</th> 
                                            <th className="column-title">抽奖赠送</th>  
                                            <th className="column-title">账面余额(10)</th>                  
                                            <th className="column-title">交易平台类型汇总余额(1-2+3-4+5-6)</th>                                   
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {
                                            tableList.length>0?
                                            tableList.map((item,index) => {
                                                return(
                                                    <tr key={index}>
                                                        <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                        <td>{moment(item.reportdate).format(DAYFORMAT)}</td>
                                                        <td>{item.fundstypename}</td>
                                                        <td>{item.platformAmount?toThousands(item.platformAmount):'0'}</td>
                                                        <td>{toThousands(new Big(item.outDifference).toFixed())}</td>
                                                        <td>{toThousands(new Big(item.insideDifference).toFixed())}</td>
                                                        <td>{item.platformInsideAmount?toThousands(item.platformInsideAmount):'0'}</td>
                                                        <td>{item.recharge?toThousands(item.recharge):'0'}</td>
                                                        <td>{item.withdraw?toThousands(item.withdraw):'0'}</td>
                                                        <td>{item.sysrecharge?toThousands(item.sysrecharge):'0'}</td>
                                                        <td>{item.sysdeduction?toThousands(item.sysdeduction):'0'}</td>
                                                        <td>{item.syssort?toThousands(item.syssort):'0'}</td>
                                                        <td>{item.icoexchange?toThousands(item.icoexchange):'0'}</td>
                                                        <td>{item.sell?toThousands(item.sell):'0'}</td>
                                                        <td>{item.buy?toThousands(item.buy):'0'}</td>
                                                        <td>{item.transactionfee?toThousands(item.transactionfee):'0'}</td>
                                                        <td>{item.withdrawfee?toThousands(item.withdrawfee):'0'}</td>
                                                        <td>{item.backCapital?toThousands(item.backCapital):'0'}</td>
                                                        <td>{item.bcakCapitalFail?toThousands(item.bcakCapitalFail):'0'}</td> 
                                                        <td>{item.luckDrawCapital?toThousands(item.luckDrawCapital):'0'}</td> 
                                                        <td>{item.bookbalance?toThousands(item.bookbalance):'0'}</td>   
                                                        <td>{item.platformSummaryAmount?toThousands(item.platformSummaryAmount):'0'}</td>                                                                                                                                                                    
                                                    </tr>
                                                )
                                            })
                                            : <tr className="no-record"><td colSpan="30">暂无数据</td></tr>
                                        }
                                    </tbody>
                                </table>
                            </div>
                            <div className="pagation-box">
                                {pageTotal>0&&
                                    <Pagination 
                                        size="small" 
                                        current={pageIndex}
                                        total={pageTotal}  
                                        showTotal={total => `总共 ${total} 条`}
                                        onChange={this.changPageNum}
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



































