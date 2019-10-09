import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,TIMEFORMAT } from '../../../conf'
import moment from 'moment'
import { Select,Modal,Button,Table,Pagination,Radio,DatePicker,message } from 'antd'
import { pageLimit} from '../../../utils'
import {tableScroll} from '../../../utils/index'
const { RangePicker } = DatePicker;
const Option = Select.Option

export default class OperTask extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            todoname:'',
            todonodename:'',
            busid:'',
            username:'',
            todostarttimeS:'',
            todostarttimeE:'',
            isOvertime:'',
            planopername:'',
            realopername:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            todostate:'',
            height:0,
            tableScroll:{
                tableId:'OPERTASK',
                x_panelId:'OPERTASKx',
                defaultHeight:500,
                height:0,
            }
        }
        
        this.onChangeTime = this.onChangeTime.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.changeTodoName = this.changeTodoName.bind(this)
        this.changeNodeName = this.changeNodeName.bind(this)
        this.changeOvertime = this.changeOvertime.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.changeTodoState = this.changeTodoState.bind(this)
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
    //查询按钮
    inquireBtn(){
        this.setState({
            page:PAGEINDEX
        },()=>this.requestTable())
    }

    //列表请求
    requestTable(currIndex,currSize){
        const { todoname,todonodename,busid,username,todostarttimeS,todostarttimeE,isOvertime,planopername,realopername,pageIndex,pageSize,todostate } = this.state
        axios.post(DOMAIN_VIP+"/agencyTask/queryTaskList",qs.stringify({
            todoname,todonodename,busid,username,todostarttimeS,todostarttimeE,isOvertime,planopername,realopername,todostate,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data
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

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTable(page,pageSize))
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    //任务发起时间
    onChangeTime(date, dateString){
        this.setState({
            todostarttimeS:dateString[0],
            todostarttimeE:dateString[1],
            time:date
        })
    }
    //事项 Select
    changeTodoName(val){
        this.setState({
            todoname:val
        })
    }
    //事项节点 select
    changeNodeName(val){
        this.setState({
            todonodename:val
        })
    }
    //时间超时 Select
    changeOvertime(val){
        this.setState({
            isOvertime:val
        })
    }
    //selet 事项状态
    changeTodoState(val){
        this.setState({
            todostate:val
        })
    }
    //状态重置
    onResetState(){
        this.setState({
            todoname:'',
            todonodename:'',
            busid:'',
            username:'',
            todostarttimeS:'',
            todostarttimeE:'',
            isOvertime:'',
            planopername:'',
            realopername:'',
            time:[],
            todostate:''
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
        // let { showHide } = this.state;
        // this.setState({
        //     showHide: !showHide
        // })
    }

    render(){
        const {showHide,time,planopername,realopername,todostate,todoname,todonodename,busid,username,initiatorname,todostarttimeS,todonodestarttimeS,isOvertime,pageIndex,pageSize,pageTotal,tableList} = this.state 
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：工作台 > 任务管理 > 任务查询
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>                    
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">事项：</label>
                                        <div className="col-sm-8">
                                            <Select value={todoname} style={{ width: SELECTWIDTH }} onChange={this.changeTodoName}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">Google审核</Option>
                                                <Option value="2">手机审核</Option>
                                                <Option value="3">身份认证</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">事项节点：</label>
                                        <div className="col-sm-8">
                                            <Select value={todonodename} style={{ width: SELECTWIDTH }} onChange={this.changeNodeName}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">修改Google</Option>
                                                <Option value="2">修改手机</Option>
                                                <Option value="3">认证审核</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" value={username} name="username" onChange={this.handleInputChange} /><b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">领办人：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" value={planopername} name="planopername" onChange={this.handleInputChange} /><b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">实际办理人：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" value={realopername} name="realopername" onChange={this.handleInputChange} /><b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">是否超时：</label>
                                        <div className="col-sm-8">
                                            <Select value={isOvertime} style={{ width: SELECTWIDTH }} onChange={this.changeOvertime}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">是</Option>
                                                <Option value="2">否</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">任务发起时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker onChange={this.onChangeTime} value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">事项状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={todostate} style={{ width: SELECTWIDTH }} onChange={this.changeTodoState}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">待办</Option>
                                                <Option value="2">办理中</Option>
                                                <Option value="3">办理完成</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>                                        
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">事项</th>
                                                <th className="column-title">事项节点</th>
                                                <th className="column-title">单号</th>
                                                <th className="column-title">用户编号</th>
                                                <th className="column-title">领办人</th>
                                                <th className="column-title">实际办理人</th>
                                                <th className="column-title">事项发起时间</th>
                                                <th className="column-title">实际完成时间</th>
                                                <th className="column-title">任务状态</th>                                                                                           
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.todoname}</td>
                                                            <td>{item.todonodename}</td>
                                                            <td>{item.busid}</td>
                                                            <td>{item.userid}</td>
                                                            <td>{item.planopername}</td>
                                                            <td>{item.realopername}</td>
                                                            <td>{moment(item.todostarttime).format(TIMEFORMAT)}</td>
                                                            <td>{moment(item.todorealcomptime).format(TIMEFORMAT)}</td>
                                                            <td>{item.todostate==1?'待办':item.todostate==2?'办理中':'办理完成'}</td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="13">暂无数据</td></tr>
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
                                                showTotal={total => `总共 ${total} 条`}
                                                onChange={this.changPageNum}
                                                onShowSizeChange={this.onShowSizeChange}
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



























