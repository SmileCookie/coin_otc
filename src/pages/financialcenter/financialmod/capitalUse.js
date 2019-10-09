import React from 'react'
import ReactDOM from 'react-dom'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Select,Modal,Button,Table,Pagination,message } from 'antd'
import moment from 'moment'
import {tableScroll} from '../../../utils'
const Option = Select.Option;



export default class CapitalUse extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            useTypeId:'',
            tableList:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            height:0,
            tableScroll:{
                tableId:'CAITLU',
                x_panelId:'CAITLUX',
                defaultHeight:500,
            }
        }

        this.requestTable = this.requestTable.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleChangeSurver = this.handleChangeSurver.bind(this)
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

    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },()=>this.requestTable())
    }

    requestTable(){
        const { useTypeId,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+"/capitalUse/query",qs.stringify({
            useTypeId,pageIndex,pageSize
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
            pageIndex:page,
            pageSize:pageSize
        },()=>this.requestTable(page,pageSize))
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.setState({
            pageIndex:current,
            pageSize:size
        },()=>this.requestTable(current,size))
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
    //重置状态
    onResetState(){
        this.setState({
            useTypeId:''
        })
    }
    handleChangeSurver(val){
        this.setState({
            useTypeId:val
        })
    }
    render(){
        const { showHide,tableList,useTypeId,pageIndex,pageSize,pageTotal } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 账务管理 > 收支用途
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">账户类型：</label>
                                            <div className="col-sm-8">
                                                <Select value={useTypeId} style={{ width: SELECTWIDTH }} onChange={this.handleChangeSurver}>
                                                    <Option value="">请选择</Option>                                                    
                                                    <Option value="1">用户充值</Option>
                                                    <Option value="2">用户提现</Option>
                                                    <Option value="3">其他(网络费等)</Option>
                                                    <Option value="4">内部账户流转(储备账户)</Option>                                                    
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
                            </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">账户名称</th>
                                                <th className="column-title">收支分类</th>
                                                <th className="column-title">是否周转</th>
                                                <th className="column-title">支出类型</th> 
                                                <th className="column-title">备注</th>                                                                                           
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.name}</td>
                                                            <td>{item.isName}</td>
                                                            <td>{item.turnroundName}</td>
                                                            <td>{item.typeName}</td>
                                                            <td>{item.memo}</td>                                                           
                                                        </tr>
                                                    )    
                                                })  
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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
                                                showQuickJumper
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE}
                                                 />
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
























