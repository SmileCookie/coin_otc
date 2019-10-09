import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Modal } from 'antd'
import { toThousands,pageLimit,tableScroll } from '../../../utils'
import ModalcapitalSource from './modal/modalcapitalSource'
import ModalcapitalRecord from './modal/modalcapitalRecord'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;

export default class GbcRepoTrack extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            batchId:'',
            entrustId:'',
            entrustTimeStart:"",
            entrustTimeEnd:"",
            time:[],   
            completeTotalMoneyStart:'',
            completeTotalMoneyEnd:'',
            completeNumberStart:'',
            completeNumberEnd:'',        
            blockHeightStart:"",
            blockHeightEnd:"",
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            tableList:[],
            visible:false,
            title:'',
            width:'',
            modalHtml:'',
            limitBtn: [],
            completeNumberSum:'',
            height:0,
            tableScroll:{
                tableId:'GBCROTCKQ',
                x_panelId:'GBCROTCKQS',
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
        this.showDetail = this.showDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('gbcRepoTrack',this.props.permissList)
        })
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
        const { batchId,entrustId,entrustTimeStart,entrustTimeEnd,completeTotalMoneyStart,completeTotalMoneyEnd,completeNumberStart,completeNumberEnd,pageIndex,pageSize } = this.state
        axios.get(DOMAIN_VIP+'/gbcreportmod/gbcRepoTrack/list',{params: {
            batchId,entrustId,entrustTimeStart,entrustTimeEnd,completeTotalMoneyStart,completeTotalMoneyEnd,completeNumberStart,completeNumberEnd,
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
        axios.post(DOMAIN_VIP+'/gbcreportmod/gbcRepoTrack/sum',qs.stringify({
            batchId,entrustId,entrustTimeStart,entrustTimeEnd,completeTotalMoneyStart,completeTotalMoneyEnd,completeNumberStart,completeNumberEnd,
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    completeNumberSum:result.data[0].completeNumberSum
                })
            } 
        })
    }
        //明细
        showDetail(enId,type){
            let maxWidth = window.screen.width > 1500?"1700px":'1300px'
            let showHtml = type==1?<ModalcapitalSource enId={enId}/>:<ModalcapitalRecord enId={enId}/>
            let showTitle = type== 1?'回购资金来源明细':'回购成交记录明细'
            this.footer=[
                <Button key="back" onClick={this.handleCancel}>返回</Button>
            ]
            this.setState({
                visible:true,
                title:showTitle,
                width:'maxWidth',
                modalHtml:showHtml
            })
           
        }
         //弹窗隐藏
        handleCancel(){
            this.setState({ 
                visible: false
            });
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
    //充值开始时间
    onChangeTime(date, dateString){
        this.setState({
            entrustTimeStart:moment((dateString[0])).format('X')+'000',
            entrustTimeEnd:moment((dateString[1])).format('X')+'000',
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
    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())    
    }
    onResetState(){
        this.setState({
            batchId:'',
            entrustId:'',
            entrustTimeStart:"",
            entrustTimeEnd:"",
            time:[],   
            completeTotalMoneyStart:'',
            completeTotalMoneyEnd:'',
            completeNumberStart:'',
            completeNumberEnd:'',        
            blockHeightStart:"",
            blockHeightEnd:"",
        })
    }
    render(){
            const {completeNumberSum,showHide,tableList,batchId,entrustId,completeTotalMoneyStart,completeTotalMoneyEnd,completeNumberStart,completeNumberEnd,time,pageIndex,pageSize,pageTotal,visible,width,modalHtml,title,limitBtn }=this.state
            return(
                <div className="right-con">
                    <div className="page-title">
                    当前位置：报表中心>GBC报表>GBC回购跟踪
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                    <label className="col-sm-3 control-label">回购编号：</label>
                                    <div className="col-sm-8">
                                        <div className="left col-sm-7">
                                             <input type="text" className="form-control"  name="batchId" value={batchId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                    <label className="col-sm-3 control-label">委托编号：</label>
                                    <div className="col-sm-8">
                                        <div className="left col-sm-7">
                                             <input type="text" className="form-control"  name="entrustId" value={entrustId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托金额：</label>
                                        <div className="col-sm-8 ">
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="completeTotalMoneyStart" value={completeTotalMoneyStart} onChange={this.handleInputChange} /></div>
                                         <div className="left line34">-</div>
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="completeTotalMoneyEnd" value={completeTotalMoneyEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托数量：</label>
                                        <div className="col-sm-8 ">
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="completeNumberStart" value={completeNumberStart} onChange={this.handleInputChange} /></div>
                                         <div className="left line34">-</div>
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="completeNumberEnd" value={completeNumberEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托时间：</label>
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
                                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                            <thead>
                                            <tr className="headings">
                                                <th colSpan="7" className="column-title text-left">回购总数量{completeNumberSum}</th>
                                                </tr>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">回购批次号</th>
                                                    <th className="column-title">委托编号</th>
                                                    <th className="column-title">委托金额</th>  
                                                    <th className="column-title">委托数量</th>    
                                                    <th className="column-title">委托时间</th>
                                                    <th className="column-title">操作</th> 
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.batchStringId}</td>
                                                            <td>{item.entrustid}</td>
                                                            <td>{item.completetotalmoney?toThousands(item.completetotalmoney):''}</td>
                                                            <td>{item.completenumber}</td>
                                                            <td>{item.entrusttime?moment(item.entrusttime).format(TIMEFORMAT):''}</td>
                                                            <td>
                                                                {limitBtn.indexOf('moneylist')>-1?<a className="mar20" href="javascript:void(0)" onClick={()=>this.showDetail(item.batchStringId,1)}>回购资金来源</a>:''}
                                                                {limitBtn.indexOf('deallist')>-1?<a href="javascript:void(0)" onClick={()=>this.showDetail(item.entrustid,2)}>回购成交记录</a>:''}
                                                            </td>
                                                        </tr>
                                                    )
                                                }):
                                                <tr className="no-record"><td colSpan="7">暂无数据</td></tr>
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
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml}            
                </Modal>
            </div>
        )
        
    }
}