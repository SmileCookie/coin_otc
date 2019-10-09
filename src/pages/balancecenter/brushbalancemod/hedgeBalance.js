import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Select, message } from 'antd'
import { toThousands } from '../../../utils'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class HedgeBalance extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            market:"",
            fromTime:"",
            marketsList:[<Option key='0' value=''>请选择</Option>],
            toTime:"",
            time:[],
            tableList:[],
            startTime:'',
            endTime:''
        }
        this.clickHide = this.clickHide.bind(this)
        this.requestMarket = this.requestMarket.bind(this)
        this.handleMarketChange = this.handleMarketChange.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onResetState = this.onResetState.bind(this)
    }
    componentDidMount(){
        this.requestMarket()
        this.requestTable()
    }
     //请求市场
     requestMarket(){
        axios.get(DOMAIN_VIP+'/brush/common/markets').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.marketsList;
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                    accountTypeArr.push(<Option key={i+1} value={result.data[i]}>{result.data[i].toUpperCase()}</Option>)
                 }
                 this.setState({
                    marketsList:accountTypeArr
                 }) 
            }
        })
    }
    requestTable(){
        const {market,fromTime,toTime} = this.state
        axios.get(DOMAIN_VIP+'/brush/balance/summary', { params: {
            market,fromTime,toTime
        }}).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data,
                    startTime:fromTime,
                    endTime:toTime,
                })
            }else{
                message.error(result.msg)
            }   
        })
    }
    //市场select
    handleMarketChange(value){
        this.setState({
            market:value
        })
    }
      //点击收起
      clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    onResetState(){
        this.setState({
            market:"",
            fromTime:"",
            toTime:"",
            time:[],
        })
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            fromTime:dateString[0],
            toTime:dateString[1],
            time:date
        })
    }
    /**
     * 
     * @author oliver
     * @param {any} 
     * @returns --
     * @description 数据不对，暂时显示--，后续注释掉即可
     */
    toUnderLine = v => '--'
    render(){
        const {showHide,market,fromTime,marketsList,toTime,time,tableList,startTime,endTime}=this.state
        const { toUnderLine} = this
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 量化交易对账 > 保值对账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div className="x_panel">
                            <div className="x_content">
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">盘口市场：</label>
                                        <div className="col-sm-9">
                                        <Select value={market}  style={{ width: SELECTWIDTH }} onChange={this.handleMarketChange} >
                                            {marketsList}    
                                        </Select>
                                            
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对账区间：</label>
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
                               
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
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
                                    <div className="table-responsive">
                                        <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                                            <thead>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">盘口市场</th>
                                                    <th className="column-title">成交笔数</th>
                                                    <th className="column-title">成交金额</th>
                                                    <th className="column-title">对冲成功笔数</th> 
                                                    <th className="column-title">对冲成功金额</th> 
                                                    <th className="column-title">未对冲笔数</th>                   
                                                    <th className="column-title">未对冲金额</th> 
                                                    {/* <th className="column-title">亏损</th>  */}
                                                    <th className="column-title">利润</th> 
                                                    <th className="column-title">对账区间</th> 
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {
                                                    tableList.length>0?
                                                    tableList.map((item,index)=>{
                                                        return (
                                                             <tr key={index}>
                                                                <td>{index+1}</td>
                                                                <td>{item.entrustMarket}</td>
                                                                <td>{toUnderLine(item.totalCount)}</td>
                                                                <td>{toUnderLine(toThousands(item.totalAmoun,true))}</td>
                                                                <td>{toUnderLine(item.hedgeFinishCount)}</td>
                                                                <td>{toUnderLine(toThousands(item.hedgeFinishAmount,true))}</td>
                                                                <td>{toUnderLine(item.hedgeUnFinishCount)}</td>
                                                                <td>{toUnderLine(toThousands(item.hedgeUnFinishAmount,true))}</td>
                                                                {/* <td>{toThousands(item.lossAmount,true)}</td> */}
                                                                <td>{toUnderLine(toThousands(item.profitAmount,true))}</td>
                                                                <td>{toUnderLine(startTime)}至{toUnderLine(endTime)}</td>
                                                            </tr>
                                                        )
                                                    }):
                                                    <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                                }
                                            </tbody>
                                        </table>
                                    </div>
                                    
                                </div>
                            </div>

                    </div>
                </div>
            </div>
        )
        
    }
}