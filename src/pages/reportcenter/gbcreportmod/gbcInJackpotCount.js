import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination } from 'antd'
import { toThousands,tableScroll } from '../../../utils'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;

export default class GbcInJackpotCount extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            tableName:'1',
            dateStart:"",
            dateEnd:"",
            time:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            tableList:[],
            amountSum:'',
            height:0,
            tableScroll:{
                tableId:'gbCIJKTCT',
                x_panelId:'gbCIJKTCTX',
                defaultHeight:500,
                height:0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.callbackTabs = this.callbackTabs.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
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
    requestTable(currIndex,currSize){
        const { tableName,dateStart,dateEnd,pageIndex,pageSize } = this.state
        axios.get(DOMAIN_VIP+'/gbcreportmod/gbcInJackpotCount/list',{params: {
            tableName,dateStart,dateEnd,
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
        axios.get(DOMAIN_VIP+'/gbcreportmod/gbcInJackpotCount/sum',{params: {
            tableName,dateStart,dateEnd,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        }}).then(res => {
            const result = res.data;
            if(result.code == 0){
                if(result.data[0].amountSum){
                  this.setState({
                    amountSum:toThousands(result.data[0].amountSum),
                })  
                }else{
                    this.setState({
                        amountSum:'0',
                    })  
                }
                
            }   
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
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())    
    }
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //tabs 回调
    callbackTabs(key) {
        const { pageIndex , pageSize } = this.state
        this.setState({
            tableName:key,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE
        })
    }
    //充值开始时间
    onChangeTime(date, dateString){
        this.setState({
            dateStart:dateString[0],
            dateEnd:dateString[1],
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
            dateStart:'',
            dateEnd:'',
            time:[]
        })
    }
    render(){
            const {showHide,checkTimeStart,tableList,checkTimeEnd,time,amountSum,pageIndex,pageSize,pageTotal}=this.state
            return(
                <div className="right-con">
                    <div className="page-title">
                    当前位置：报表中心>GBC报表>转入奖池统计
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">回购时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                        showTime={{
                                            defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                          }}
                                        format="YYYY-MM-DD HH:mm:ss"
                                        placeholder={['Start Time', 'End Time']}
                                       onChange={this.onChangeTime }
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
                                    <Tabs onChange={this.callbackTabs}>
                                        <TabPane tab="最近3天" key="1"></TabPane>
                                        <TabPane tab="3天前" key="2"></TabPane>
                                    </Tabs>
                                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                            <thead>
                                                <tr className="headings">
                                                    <th colSpan="6" className="column-title text-left">转入数量：{amountSum}</th>
                                                </tr>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">奖池账号</th>
                                                    <th className="column-title">转入流水号</th>
                                                    <th className="column-title">转入时间</th>  
                                                    <th className="column-title">转入数量</th>    
                                                    <th className="column-title">奖池当前数量</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.userid}</td>
                                                            <td>{item.id}</td>
                                                            <td>{item.sendtime?moment(item.sendtime).format('YYYY-MM-DD'):''}</td>
                                                            <td>{item.amount}</td>
                                                            <td>{item.balance}</td>
                                                        </tr>
                                                    )
                                                }):
                                                <tr className="no-record"><td colSpan="6">暂无数据</td></tr>
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