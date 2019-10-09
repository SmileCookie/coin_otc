import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import {pageLimit,tableScroll} from '../../../utils/index'
import ModaltimingTask from './modal/modaltimingTask'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,Tabs,Pagination,Modal,message } from 'antd'
const TabPane = Tabs.TabPane;

export default class SmallPayTask extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            tableList:'',
            modalHtml:'',
            visible:false,
            title:'',
            id:'',
            jobName:'',
            jobStartTime:'',
            jobEndTime:'',
            jobStatus:'',
            jobInterval:'',
            limitBtn:[],
            loading:false,
            height:0,
            tableScroll:{
                tableId:'SPTK',
                x_panelId:'SPTKx',
                defaultHeight:500,
                height:0,
            }
        }
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.changeRadio = this.changeRadio.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.ModalchengeTime = this.ModalchengeTime.bind(this)
        this.onChengeTime = this.onChengeTime.bind(this)
        this.modifyRoleBtn = this.modifyRoleBtn.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState ({
            limitBtn : pageLimit('smallPayTask',this.props.permissList)
        })
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`)
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }

         //修改
         showDetail(item){
            const {accountType} = this.state
           this.footer=[
               <Button key="back" onClick={this.handleCancel}>取消</Button>,
               <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modifyRoleBtn(item)}>
                   保存修改
               </Button>,
           ]
           this.setState({
               visible:true,
               title:'修改小额打币定时任务',
               modalHtml:<ModaltimingTask item={item}
                changeRadio={this.changeRadio}
                handleInputChange = {this.handleInputChange}
                onChengeTime = {this.onChengeTime}
                ModalchengeTime = {this.ModalchengeTime}
                />,
                id:item.id,
                jobName:item.jobName,
                jobStartTime:item.jobStartTime?item.jobStartTime:'',
                jobEndTime:item.jobEndTime?item.jobEndTime:'',
                jobStatus:item.jobStatus,
                jobInterval:item.jobInterval
           })
       }
       modifyRoleBtn(item){
           this.setState({
               loading:true
           })
           const {id,jobName,jobStartTime,jobEndTime,jobStatus,jobInterval} = this.state
           axios.post(DOMAIN_VIP+"/smallPayTask/editSmallPayTaskInfo",qs.stringify({
            id,jobName,jobStartTime,jobEndTime,jobStatus,jobInterval
           })).then(res => {
               const result = res.data
               if(result.code == 0){
                   message.success(result.msg);
                   this.requestTable();
                   this.setState({
                       visible:false,
                       loading:false
                    })
               }else{
                   message.error(result.msg); 
                   this.setState({
                       loading:false
                   })
               }
           })
       }
       //输入时 input 设置到 sate
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //时间
    ModalchengeTime(dateString){
        this.setState({
            jobEndTime:dateString 
        })
       
    }
    onChengeTime(dateString){
        this.setState({
            jobStartTime:dateString
        })
        
    }
       //单选按钮
       changeRadio(value){
        this.setState({
            jobStatus: value,
        });
    }
       //弹窗隐藏
       handleCancel(){
           this.setState({ 
               visible: false,
               loading:true
           });
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
    requestTable(currIndex,currSize){
        const {pageIndex,pageSize} = this.state;
        axios.post(DOMAIN_VIP+'/smallPayTask/getSmallPayTaskInfo',qs.stringify({
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
        const {tableList,pageIndex,pageSize,pageTotal,visible,title,modalHtml,limitBtn}=this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：监控中心 > 小额自动打币 > 小额打币定时任务
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                                <div className="x_content">
                                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':'auto'}`}} className="table-responsive-yAuto">
                                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                            <thead>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">作业名称</th>
                                                    <th className="column-title">开始执行时间</th>
                                                    <th className="column-title">结束执行时间</th> 
                                                    <th className="column-title">执行间隔(秒)</th>
                                                    <th className="column-title">状态</th> 
                                                    <th className="column-title">创建时间</th>  
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
                                                            <td>{item.jobName}</td>
                                                            <td>{item.jobStartTime?item.jobStartTime:''}</td>
                                                            <td>{item.jobEndTime?item.jobEndTime:''}</td>
                                                            <td>{item.jobInterval}</td>
                                                            <td>{item.statusDes}</td>
                                                            <td>{moment(item.managetime).format(TIMEFORMAT)}</td>
                                                            <td>{limitBtn.indexOf('editSmallPayTask')>-1?<a href="javascript:void(0)" onClick={() => this.showDetail(item)}>修改</a>:''}</td>
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
                <Modal
                    visible={visible}
                    title={title}
                    width="700px"
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml}            
                </Modal>
            </div>
        )
        
    }
}