import React from 'react'
import ReactDOM from 'react-dom'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import SelectAType from '../select/selectAType'
import { toThousands,tableScroll } from '../../../utils'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Select,message } from 'antd'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class WalletCapital extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            fundsType:"0",
            checkTimeStart:"",
            checkTimeEnd:"",
            tableList:[],
            time:[],
            blockHeightStart:'',
            blockHeightEnd:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            height:0,
            tableScroll:{
                tableId:'WALTCIPAL',
                x_panelId:'WALTCIPALXX',
                defaultHeight:500,
                height:0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
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
      //点击分页
      changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        })
        this.requestTable(page,pageSize)
    }
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
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
            checkTimeStart:dateString[0]+" 00:00:00",
            checkTimeEnd:dateString[1]+" 23:59:59",
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
    handleChangeType(value){
        this.setState({
            fundsType:value
        })
    }
    onResetState(){
        this.setState({
            fundsType:"0",
            checkTimeStart:"",
            checkTimeEnd:"",
            time:[],
            blockHeightStart:'',
            blockHeightEnd:'',
        })
    }
    requestTable(currIndex,currSize){
        const {fundsType,checkTimeStart,checkTimeEnd,blockHeightStart,blockHeightEnd,pageIndex,pageSize,pageTotal} = this.state
        axios.post(DOMAIN_VIP+'/centerCapitalExp/fundSummary',qs.stringify({
            fundsType,checkTimeStart,checkTimeEnd,blockHeightStart,blockHeightEnd,
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
    render(){
        const {showHide,fundsType,blockHeightStart,blockHeightEnd,tableList,time,pageIndex,pageSize,pageTotal} = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 资金报表 > 钱包收支表
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <FundsTypeList  fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">查询日期：</label>
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
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">区块高度：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control"  name="blockHeightStart" value={blockHeightStart} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control"  name="blockHeightEnd" value={blockHeightEnd} onChange={this.handleInputChange} />
                                            </div>
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
                                        <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto table-responsive-fixed" >
                                            <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                                <thead>
                                                    <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">资金类型</th>
                                                    <th className="column-title">冷存入</th>
                                                    <th className="column-title">冷转出</th>
                                                    <th className="column-title">冷手续费</th>   
                                                    <th className="column-title">冷余额</th>
                                                    <th className="column-title">热提存入</th>
                                                    <th className="column-title">热提转出</th> 
                                                    <th className="column-title">热提手续费</th>
                                                    <th className="column-title">热提余额</th> 
                                                    <th className="column-title">热充存入</th> 
                                                    <th className="column-title">热充转出</th>
                                                    <th className="column-title">热充手续费</th>
                                                    <th className="column-title">热充余额</th>                    
                                                    </tr>
                                                </thead>
                                                <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.fundTypeName}</td>
                                                            <td>{toThousands(item.colDeposit,true)}</td>
                                                            <td>{toThousands(item.colRollOut,true)}</td>
                                                            <td>{toThousands(item.colFee,true)}</td>
                                                            <td>{toThousands(item.colBalance,true)}</td>
                                                            <td>{toThousands(item.hotDeposit,true)}</td>
                                                            <td>{toThousands(item.hotRollOut,true)}</td>
                                                            <td>{toThousands(item.hotFee,true)}</td>
                                                            <td>{toThousands(item.hotBalance,true)}</td>
                                                            <td>{toThousands(item.inRecharge,true)}</td>
                                                            <td>{toThousands(item.outRecharge,true)}</td>
                                                            <td>{toThousands(item.feeRecharge,true)}</td>
                                                            <td>{toThousands(item.hotPayBalance,true)}</td>
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