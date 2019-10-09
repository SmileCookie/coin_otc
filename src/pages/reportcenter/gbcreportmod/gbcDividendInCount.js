import React from 'react';
import ReactDOM from 'react-dom';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Modal,Select} from 'antd'
import { toThousands,tableScroll } from '../../../utils'
import ModalcapitalSource from './modal/modalcapitalSource'
import ModalcapitalRecord from './modal/modalcapitalRecord'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class GbcDividendInCount extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            amountStart:'',
            amountEnd:'',
            startSubmittime:"",
            endSubmittime:"",
            timesub:[],
            startConfigtime:'',
            endConfigtime:'',
            timecfg:[],
            status:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            tableList:[],
            amountSum:'0',
            amountNowSum:'0',
            height:0,
            tableScroll:{
                tableId:'gbcDVIDNCNT',
                x_panelId:'gbcDVIDNCNTX',
                defaultHeight:500,
                height:0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTimeSub = this.onChangeTimeSub.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.onChangeTimeCfg = this.onChangeTimeCfg.bind(this)
        this.handleChangeStatus = this.handleChangeStatus.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillReceiveProps(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
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
    requestTable(currIndex,currSize){
        const { status,amountStart,amountEnd,startSubmittime,endSubmittime,startConfigtime,endConfigtime,pageIndex,pageSize } = this.state
        axios.get(DOMAIN_VIP+'/gbcreportmod/gbcDividendInCount/list',{params: {
            status,amountStart,amountEnd,startSubmittime,endSubmittime,startConfigtime,endConfigtime,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        }}).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }   
        })
        axios.get(DOMAIN_VIP+'/gbcreportmod/gbcDividendInCount/sum',{params: {
            status,amountStart,amountEnd,startSubmittime,endSubmittime,startConfigtime,endConfigtime,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        }}).then(res => {
            const result = res.data;
            if(result.code == 0){
                if(result.data[0].amountSum&&result.data[0].amountNowSum){
                  this.setState({
                    amountSum:toThousands(result.data[0].amountSum),
                    amountNowSum:toThousands(result.data[0].amountNowSum)
                })  
                }else{
                    this.setState({
                        amountSum:'0',
                        amountNowSum:'0'
                    })  
                }
                
            }   
        })
    }
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())    
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
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    handleChangeStatus(value){
        this.setState({
            status:value
        })
    }
    //充值开始时间
    onChangeTimeSub(date,dateString){
        this.setState({
            startSubmittime:dateString[0],
            endSubmittime:dateString[1],
            timesub:date
        })
    }
    onChangeTimeCfg(date,dateString){
        this.setState({
            startConfigtime:dateString[0],
            endConfigtime:dateString[1],
            timecfg:date
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
    onResetState(){
        this.setState({
            amountStart:'',
            amountEnd:'',
            timesub:[],
            timecfg:[],
            status:'',   
            startSubmittime:'',
            endSubmittime:'',
            startConfigtime:'',       
            endConfigtime:"",
        })
    }
    render(){
            const {showHide,amountStart,amountEnd,timesub,timecfg,status,amountSum,amountNowSum,pageIndex,pageSize,pageTotal,tableList}=this.state
            return(
                <div className="right-con">
                    <div className="page-title">
                    当前位置：报表中心>GBC报表>分红转入统计
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">提现金额：</label>
                                        <div className="col-sm-8 ">
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="amountStart" value={amountStart} onChange={this.handleInputChange} /></div>
                                         <div className="left line34">-</div>
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="amountEnd" value={amountEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">确认时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                        showTime={{
                                            defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                          }}
                                        format="YYYY-MM-DD HH:mm:ss"
                                        placeholder={['Start Time', 'End Time']}
                                       onChange={this.onChangeTimeSub }
                                       value={timesub}
                                       />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">提交时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                        showTime={{
                                            defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                          }}
                                        format="YYYY-MM-DD HH:mm:ss"
                                        placeholder={['Start Time', 'End Time']}
                                       onChange={this.onChangeTimeCfg }
                                       value={timecfg}
                                       />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                    <label className="col-sm-3 control-label">打币状态：</label>
                                    <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.handleChangeStatus}>
                                        <Option value="">请选择</Option>
                                        <Option value="0">提交</Option>
                                        <Option value="1">失败</Option>
                                        <Option value="2">成功</Option>
                                        <Option value="3">取消</Option>
                                    </Select>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.inquireBtn()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                         }
                        <div className="x_panel">
                                <div className="x_content">
                                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                            <thead>
                                                <tr className="headings">
                                                <th colSpan="10" className="column-title text-left">转入金额：{amountSum}，实际转入金额：{amountNowSum}</th>
                                                </tr>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">提现编号</th>
                                                    <th className="column-title">GBC转入账号</th>
                                                    <th className="column-title">转入金额</th>  
                                                    <th className="column-title">实际转入金额</th>    
                                                    <th className="column-title">申请时间</th>
                                                    <th className="column-title">审核时间</th> 
                                                    <th className="column-title">到账时间</th> 
                                                    <th className="column-title min_153px">提现地址</th> 
                                                    <th className="column-title">打币状态</th> 
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.downloadid}</td>
                                                            <td>{item.userid}</td>
                                                            <td>{toThousands(item.amount,true)}</td>
                                                            <td>{toThousands(item.amountNow,true)}</td>
                                                            <td>{item.submittime?moment(item.submittime).format('YYYY-MM-DD'):''}</td>
                                                            <td>{item.managetime?moment(item.managetime).format('YYYY-MM-DD'):''}</td>
                                                            <td>{item.configtime?moment(item.configtime).format('YYYY-MM-DD'):''}</td>
                                                            <td>{item.toaddress}</td>
                                                            <td>{(()=>{ 
                                                                switch (item.status) {
                                                                        case 0:
                                                                            return'提交'
                                                                            break;
                                                                        case 1:
                                                                            return'失败'
                                                                            break;
                                                                        case 2:
                                                                            return'成功'
                                                                            break;
                                                                        case 3:
                                                                            return'取消'
                                                                            break;
                                                                        default:
                                                                            break;
                                                                        }
                                                                    })()}</td>
                                                        </tr>
                                                    )
                                                }):
                                                <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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