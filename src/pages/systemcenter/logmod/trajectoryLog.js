import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import cookie from 'js-cookie'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,TIMEFORMAT,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Select,Button,DatePicker,Pagination,message } from 'antd'
import { tableScroll } from '../../../utils'
const { RangePicker } = DatePicker;
const Option = Select.Option;

export default class TrajectoryLog extends React.Component{

    constructor(props){
        super(props)
        this.state={
            tableList:[],
            showHide:true,
            pageTotal:0,
            creDateS:'',
            creDateE:'',
            username:'',
            operation:'',
            time:[],
            page:PAGEINDEX,
            limit:PAGESIZE,
            userId:cookie.get('userId'),
            height:0,
            tableScroll:{
                tableId:'tjtOYLG',
                x_panelId:'tjtOYLGX',
                defaultHeight:600,
            }
        }

        this.requestTable = this.requestTable.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
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
    requestTable(){
        const {page,limit,creDateS,creDateE,operation,username,userId} = this.state
        axios.get(DOMAIN_VIP+"/sys/log/list",{
           params:{
                page,limit,creDateS,creDateE,operation,username,userId
           } 
        }).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList:result.page.list,
                    pageTotal:result.page.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //查询按钮
    inquireBtn(){
        this.setState({
            page:PAGEINDEX
        },()=>this.requestTable())
    }

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            page
        },()=>this.requestTable())
        
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.setState({
            page:current,
            limit:size
        }, () => this.requestTable())
    }

    //时间改变
    onChangeTime(date, dateString) {
        this.setState({
            creDateS:dateString[0],
            creDateE:dateString[1],
            time:date
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    //重置状态
    onResetState(){
        this.setState({
            creDateS:'',
            creDateE:'',
            username:'',
            operation:'',
            time:[],
        })
    }

    render(){
        const { showHide,username,operation,page,limit,tableList,pageTotal,time } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>日志管理>轨迹日志
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                           
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">操作员：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="username" value={username}  onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">操作时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChangeTime} value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">日志内容：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="operation" value={operation}  onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                
                                <div className="col-md-6 col-sm-6 col-xs-6 right martop4">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">操作员</th>
                                                <th className="column-title">操作日期</th>
                                                <th className="column-title">操作内容</th>
                                                <th className="column-title">IP</th>                      
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return(
                                                        <tr key={index}>
                                                            <td>{(page-1)*limit+index+1}</td>
                                                            <td>{item.username}</td>                                     
                                                            <td>{moment(item.createDate).format(TIMEFORMAT)}</td>
                                                            <td>{item.operation}</td>
                                                            <td>{<a href={`http://www.ip138.com/ips138.asp?ip=${item.ip}&action=2`} target="_blank">{item.ip}</a>}</td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="15">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                    {
                                        pageTotal>0 && <Pagination
                                                    size="small"
                                                    current={page}
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


























