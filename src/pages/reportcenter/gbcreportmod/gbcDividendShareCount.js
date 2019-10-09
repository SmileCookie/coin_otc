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

export default class GbcDividendShareCount extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            from:'',
            userId:'',
            userName:'',
            amountS:'',
            amountE:'',
            timeStart:"",
            timeEnd:"",
            time:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            tableList:[],
            amountSum:'0',
            burninfoSum:'0',
            totalShareCount:'0',
            height:0,
            tableScroll:{
                tableId:'GBCDVIDSAECT',
                x_panelId:'GBCDVIDSAECTX',
                defaultHeight:500,
                height:0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangetime = this.onChangetime.bind(this)
        this.onResetState = this.onResetState.bind(this)
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
        const { userId,userName,from,amountS,amountE,timeStart,timeEnd,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+'/gbcreportmod/gbcDividendShareCount/list',qs.stringify({
            userId,userName,from,amountS,amountE,timeStart,timeEnd,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }   
        })
        axios.post(DOMAIN_VIP+'/gbcreportmod/gbcDividendShareCount/sum',qs.stringify({
            userId,userName,from,amountS,amountE,timeStart,timeEnd
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let amountSum = result.data.amountSum?toThousands(result.data.amountSum):'--'
                let burninfoSum = result.data.burninfoSum?toThousands(result.data.burninfoSum):'--'
                let totalShareCount = result.data.totalShareCount?toThousands(result.data.totalShareCount):'--'
                  this.setState({
                    amountSum,
                    burninfoSum,
                    totalShareCount
                })
                
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
    onChangetime(date,dateString){
        this.setState({
            timeStart:moment(dateString[0]).format('X'),
            timeEnd:moment(dateString[1]).format('X'),
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
    onResetState(){
        this.setState({
            amountS:'',
            amountE:'',
            time:[],
            timecfg:[],
            status:'',   
            timeStart:'',
            timeEnd:'',
            userId:'',
            userName:'',
            from:'',
        })
    }
    render(){
            const {showHide,amountS,amountE,totalShareCount,time,userId,userName,from,amountSum,burninfoSum,pageIndex,pageSize,pageTotal,tableList}=this.state
            return(
                <div className="right-con">
                    <div className="page-title">
                    当前位置：报表中心>GBC报表>分红股份统计
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
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
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户燃烧地址：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="from" value={from} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">燃烧金额：</label>
                                        <div className="col-sm-8 ">
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="amountS" value={amountS} onChange={this.handleInputChange} /></div>
                                         <div className="left line34">-</div>
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="amountE" value={amountE} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">燃烧时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                        showTime={{
                                            defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                          }}
                                        format="YYYY-MM-DD HH:mm:ss"
                                        placeholder={['Start Time', 'End Time']}
                                       onChange={this.onChangetime }
                                       value={time}
                                       />
                                        </div>
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
                                                <th colSpan="8" className="column-title text-left">燃烧金额：{amountSum}，股份：{burninfoSum}，分红股份总数：{totalShareCount}</th>
                                                </tr>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">用户编号</th>
                                                    <th className="column-title">用户名</th>
                                                    <th className="column-title min_300px">交易编号</th>  
                                                    <th className="column-title">燃烧金额</th>    
                                                    <th className="column-title">生成股份</th>
                                                    <th className="column-title">燃烧时间</th> 
                                                    <th className="column-title min_300px">用户燃烧地址</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.userId}</td>
                                                            <td>{item.userName}</td>
                                                            <td>{item.txid}</td>
                                                            <td>{toThousands(item.amout,true)}</td>
                                                            <td>{toThousands(item.value,true)}</td>
                                                            <td>{item.time?moment(Number(item.time+'000')).format(TIMEFORMAT):''}</td>
                                                            <td>{item.from}</td>
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