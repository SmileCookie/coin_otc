import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { toThousands,tableScroll } from '../../../utils'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Select } from 'antd'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class SmallAutoPayRecord extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            submitTimeOfStart:"",
            fundsType:'0',
            submitTimeOfEnd:"",
            time:[],  
            checkTime:[],          
            amountOfStart:"",
            amountOfEnd:"",
            manageTimeOfStart:"",
            manageTimeOfEnd:"",
            status:'',
            batchId:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            tableList:'',
            height:0,
            tableScroll:{
                tableId:'SAPRD',
                x_panelId:'SAPayRDx',
                defaultHeight:500,
                height:0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleTypeChange = this.handleTypeChange.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.onChangeSubmitTime = this.onChangeSubmitTime.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        console.log(window.screen.width)
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
    handleChangeType(value) {
        this.setState({
            fundsType:value
        })
    }
    handleTypeChange(value){
        this.setState({
            status:value
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
    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },()=>this.requestTable())
    }
      //时间控件
      onChangeCheckTime(date, dateString) {
        this.setState({
            submitTimeOfStart:dateString[0],
            submitTimeOfEnd:dateString[1],
            time:date
        })
    }
    onChangeSubmitTime(date,dateString){
        this.setState({
            manageTimeOfStart:dateString[0],
            manageTimeOfEnd:dateString[1],
            checkTime:date
        })
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;
    this.setState({
        [name]: value
    });
}
    requestTable(currIndex,currSize){
        const { fundsType,submitTimeOfStart,submitTimeOfEnd,amountOfStart,amountOfEnd,manageTimeOfStart,manageTimeOfEnd,status,batchId, pageIndex,pageSize} = this.state;
        axios.post(DOMAIN_VIP+'/smallAutoPayRecord/getAutoDownloadInfo',qs.stringify({
            fundsType,amountOfStart,amountOfEnd,manageTimeOfStart,manageTimeOfEnd,status,batchId,submitTimeOfStart,submitTimeOfEnd,
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
            fundsType:'', 
            checkTime:[],
            time:[],
            amountOfStart:'',
            amountOfEnd:'',
            manageTimeOfStart:'',
            manageTimeOfEnd:'',
            status:'',
            batchId:'',
            submitTimeOfStart:"",
            submitTimeOfEnd:"",
        })
    }
    render(){
        const {showHide,fundsType,tableList,checkTime,time,amountOfStart,amountOfEnd,manageTimeOfStart,manageTimeOfEnd,status,batchId, pageIndex,pageSize,pageTotal}=this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：监控中心 > 小额自动打币 > 小额自动打币记录
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList title='资金类型' fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">打币状态：</label>
                                        <div className="col-sm-9">
                                        <Select value={status}  style={{ width: SELECTWIDTH }} onChange={this.handleTypeChange} >
                                                 <Option  value=''>请选择</Option>
                                                <Option  value='0'>提交</Option>
                                                <Option  value='1'>失败</Option>
                                                <Option  value='2'>成功</Option>
                                                <Option  value='3'>取消</Option>
                                        </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">批次号：</label>
                                        <div className="col-sm-8">
                                             <input type="text" className="form-control"  name="batchId" value={batchId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">打币金额：</label>
                                        <div className="col-sm-8 ">
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="amountOfStart" value={amountOfStart} onChange={this.handleInputChange} /></div>
                                         <div className="left line34">-</div>
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="amountOfEnd" value={amountOfEnd} onChange={this.handleInputChange} /></div>
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
                                       onChange={this.onChangeCheckTime }
                                       value={time}
                                       />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">打币时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                        showTime={{
                                            defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                          }}
                                        format="YYYY-MM-DD HH:mm:ss"
                                        placeholder={['Start Time', 'End Time']}
                                       onChange={this.onChangeSubmitTime }
                                       value={checkTime}
                                       />
                                        </div>
                                    </div>
                                </div>
                                
                               
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                         }
                        <div className="x_panel">
                                <div className="x_content">
                                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                            <thead>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">批次号</th>
                                                    <th className="column-title">资金类型</th>
                                                    <th className="column-title">打币金额</th> 
                                                    <th className="column-title">提交时间</th>
                                                    <th className="column-title">打币时间</th> 
                                                    <th className="column-title">打币状态</th>  
                                                    <th className="column-title min_153px">唯一标识</th>            
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.batchid}</td>
                                                            <td>{item.fundsTypeName}</td>
                                                            <td>{item.amount?toThousands(item.amount):''}</td>
                                                            <td>{item.submittime?moment(item.submittime).format(TIMEFORMAT):''}</td>
                                                            <td>{item.managetime?moment(item.managetime).format(TIMEFORMAT):''}</td>
                                                            <td>{(() => {
                                                                switch (item.status) {
                                                                case 0:
                                                                    return "提交" 
                                                                    break;
                                                                case 1:
                                                                    return "失败" 
                                                                    break;
                                                                case 2:
                                                                    return "成功" 
                                                                    break;
                                                                case 3:
                                                                    return "取消" 
                                                                    break;
                                                                case 4:
                                                                    return "打币失败" 
                                                                    break;
                                                                case 5:
                                                                    return "正在打币中" 
                                                                    break;
                                                                case 6:
                                                                    return "打币成功" 
                                                                    break;
                                                                default:
                                                                    return ""
                                                                    break; 
                                                                    }
                                                                })()
                                                            }
                                                            </td>
                                                            <td>{item.uuid}</td>
                                                        </tr>
                                                    )
                                                }):
                                                <tr className="no-record"><td colSpan="8">暂无数据</td></tr>
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