
import React from 'react'
import axios from '../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalGoogle from '../systemcenter/verifymod/modal/modalGoogle'
import ModalAuthen from '../systemcenter/verifymod/modal/modalAuthen'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT } from '../../conf'
import { DatePicker,Select,Modal, Button,Pagination,message } from 'antd'
const Option = Select.Option;

export default class Home extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            visible:false,
            modalHtml:'',   
            startValue: '',
            todoname:'',
            todonodename:'',
            username:'',
            todostarttimeS:'',
            isOvertime:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            time:null,
            tableList:[],
            pageTotal:0,
            auditMemo:'',
            auditStatus:'',
            id:'',
            tableViewList:[],
            totalCoin:0,
            totalWithdraw:0,
            status:'',
            lodalHtml:'',
            lotitle:'',
            lovisible:false,
            lowidth:'600px',
            defaultFlg:1,
            novisible:false,
            nowidth:'',
            certificationNum:'',
            reason:'',
            reasonName:'',
            reasonList:'',
            taskVisible:false,
            noteVisible:false,
            checkResult:'',
            checkImg:'',
            qiniu_host:"https://o4we6sxpt.qnssl.com/"

        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.handleChangeTodo = this.handleChangeTodo.bind(this)
        this.handleChangeTodoName = this.handleChangeTodoName.bind(this)
        this.handleChangeOverTime = this.handleChangeOverTime.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onModalRadio = this.onModalRadio.bind(this)
        this.openModalGoogle = this.openModalGoogle.bind(this)
        this.openModalPerson = this.openModalPerson.bind(this)
        this.openModalPhone = this.openModalPhone.bind(this)
        this.requestViewTable = this.requestViewTable.bind(this)
        this.setReason = this.setReason.bind(this)
        this.onChangeRadio = this.onChangeRadio.bind(this)
        this.onChangeReason = this.onChangeReason.bind(this)
        this.lohandleCancel = this.lohandleCancel.bind(this)
        this.lockProve = this.lockProve.bind(this)
        this.addBlacklist= this.addBlacklist.bind(this)
        this.memoOk = this.memoOk.bind(this)
        this.memoCancel = this.memoCancel.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.setImage = this.setImage.bind(this)
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
     //获取子组件的审核不通过原因
     setReason(item){
        let reasonll = [];
        for(var i =0; i <item.length; i++){
            console.log(item[i].reason)
            switch(item[i].reason){
                case '8':
                    reasonll.push("图像经过处理")
                    break;
                case '9':
                    reasonll.push('图像不清晰')
                    break;
                case '10':
                    reasonll.push('证件图像类型不符')
                    break;
                case '11':
                    reasonll.push('平台仅支持满16周岁的用户进行交易')
                    break;
                default:
                    break;
            }
        }
        console.log(reasonll)
        this.setState({
            reasonList:reasonll.join(',')
        })
    }
    //原因
    onChangeReason(val){
        let reasonNum
        switch (val) {
            case '8':
                reasonNum = "图像经过处理"
                break;
            case '9':
                reasonNum = '图像不清晰'
                break;
            case '10':
                reasonNum = '证件图像类型不符'
                break;
            case '11':
                reasonNum = '平台仅支持满16周岁的用户进行交易'
                break;
            default:
                break;
            }
        this.setState({
            reason:val,
            reasonName:reasonNum
        })
    }
    componentDidMount(){
        this.requestTable()
        this.requestViewTable()
        this.requestVisible()
    }
    //查询按钮
    inquireBtn(){
        this.setState({
            page:PAGEINDEX,
            defaultFlg:2
        },()=>this.requestTable())
    }
    //代办 、 提示 显示隐藏
    requestVisible(){
        axios.get(DOMAIN_VIP+"/sys/menu/getHomePageInfo").then(res => {
            const result = res.data
            if(result.code == 0){
                for(let i=0;i<result.menuIdList.length;i++){
                    if(result.menuIdList[i] == '700100010002'){
                        this.setState({
                            taskVisible:true
                        })
                    }else if(result.menuIdList[i] == '700100010003'){
                        this.setState({
                            noteVisible:true
                        })
                    }
                }
            }
        })
    }

    //待办任务 table
    requestTable(currIndex,currSize){
        const { todoname,todonodename,username,startValue,isOvertime,pageIndex,pageSize,defaultFlg } = this.state
        axios.post(DOMAIN_VIP+'/agencyTask/queryList',qs.stringify({
            todoname,
            todonodename,
            username,
            todostarttimeS:startValue,
            isOvertime,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize,
            defaultFlg
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount,
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    //总览 table
    requestViewTable(){ 
        axios.post(DOMAIN_VIP+'/withdraw/query', qs.stringify({
            fundType:'0'
        })).then(res => {
            const result = res.data;
            let totalWithdraw = 0;
            for(let i=0;i<result.data&&result.data.length;i++){
                totalWithdraw+=result.data[i].waitCount
            }
            if(result.code == 0){
                this.setState({
                    tableViewList:result.data,
                    totalWithdraw,
                    totalCoin:result.data.length
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //改变 onChangeRadio
    onChangeRadio(val){
        this.setState({
            status:val
        })
    }

    //时间控件
    onChangeTime(date, dateString) {
        // console.log(date, dateString);
        this.setState({
            time:date,
            startValue:dateString
        })
    }

    //select 事项
    handleChangeTodo(value){
        this.setState({
            todoname:value
        })
    }
    //select 事项节点
    handleChangeTodoName(value){
        this.setState({
            todonodename:value
        })
    }
    //select 是否超市
    handleChangeOverTime(value){
        this.setState({
            isOvertime:value
        })
    }

    //过去弹窗单选按钮
    onModalRadio(val){
        this.setState({
            auditStatus:val
        })
    }

    openModal(item){
        let self = this
        axios.post(DOMAIN_VIP+'/agencyTask/receive',qs.stringify({
            id:item.id,
            todoName:item.todoname,
            taskId:item.taskid
        })).then(res => {
            const result = res.data;
            if(result.code == 0&&result.isRecive){
                this.footer = [
                    <Button key="back" onClick={this.handleCancel}>取消</Button>,
                    <Button key="submit" type="more" loading={this.state.loading} onClick={item.url == 1?()=>this.openModalGoogle(item):item.url == 2?()=>this.openModalPhone(item):()=>this.openModalPerson(item)}>
                        保存修改
                    </Button>,
                ]
                let modalHtml = item.url == 3? <ModalAuthen item={result.data} tab={5} onChangeRadio={this.onChangeRadio} onChangeReason={this.onChangeReason} setReason={this.setReason} setImage={this.setImage}/>:<ModalGoogle item={result.data} tab="0" onModalRadio={this.onModalRadio} handleInputChange={this.handleInputChange}/>;
                let title = item.url == 3?"身份认证审核":item.url == 1?"Google认证审核":"手机认证审核";
                //  console.log(item.taskid)
                this.setState({
                    certificationNum:result.data.authTimes,
                    title,
                    modalHtml:modalHtml,
                    width:item.url == 3?"900px":"600px",
                    visible:true,
                    auditMemo:'',
                    auditStatus:'',
                    status:'1',
                    reason:'',
                    id:item.taskid
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //保存有效性检查返回图像
    setImage(src){
        const{qiniu_host}= this.state
        this.setState({
            checkResult:1,
            checkImg:qiniu_host + src, 
        },()=>{console.log(this.state.checkImg)})
    }

    //Google 审核 弹窗 按钮
    openModalGoogle(item){
        // console.log(item)
        const { auditMemo,auditStatus,id } = this.state
        axios.post(DOMAIN_VIP + '/googleVerify/pass',qs.stringify({
            id:item.taskid,
            memo:auditMemo,
            status:auditStatus
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                this.requestTable()
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //手机 审核  按钮
    openModalPhone(item){
        console.log(item)
        const { auditMemo,auditStatus,id } = this.state
        axios.post(DOMAIN_VIP + '/phoneVerify/pass',qs.stringify({
            id:item.taskid,
            memo:auditMemo,
            status:auditStatus
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                this.requestTable()
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //身份 审核 按钮
    openModalPerson(item){
        // console.log(item.taskid)
        const{certificationNum,status,reason,reasonList,checkImg,checkResult,reasonName} = this.state
        // console.log(certificationNum)
        axios.post(DOMAIN_VIP+"/authentication/pass",qs.stringify({
            id:item.taskid,
            status,
            reason,
            checkImg,
            checkResult
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                 if(certificationNum>=2&&status == 2){
                    this.footerr = [
                                <Button key='1' type="more" onClick={()=>this.lockProve(item)}>锁定认证72小时</Button>,
                                <Button key='2' type="more" onClick={()=>this.addBlacklist(item)}>移入黑名单</Button>,
                                <Button key="back" onClick={this.lohandleCancel}>取消</Button>
                            ]
                            this.setState({
                                lovisible:true,
                                lowidth:'400px',
                                lodalHtml:  <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="center">提示</div>
                                    <div className="col-md-12 col-sm-12 col-xs-12 lineHeight_34">
                                        第{certificationNum}次拒绝改用户的认证申请。
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12 lineHeight_34">
                                        拒绝原因分别为：{reasonList},{reasonName}。
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12 lineHeight_34">
                                        是否需要限制该用户的认证权限。
                                    </div>
                                </div>
                                
                            })
                 }else{
                     this.setState({
                        visible:false
                     })
                 }
                this.requestTable()
                message.success(result.msg)
            }else{
                
                message.warning(result.msg)
            }
        })
       
    }
    lohandleCancel(){
        this.setState({
            lovisible:false,
            lovisible:false,
            visible:false,
            lodalHtml:'',
            auditMemo:''
        })
    }
    //锁定认证
    lockProve(item){
        // console.log("锁定")
        // console.log(item.userid)
        axios.post(DOMAIN_VIP+"/authentication/lock",qs.stringify({
            id:item.taskid,
            state:'1',
            userId:item.userid
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    lovisible:false,
                    visible:false
                })
                this.requestTable()
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //移入黑名单原因
    addBlacklist(item){
        this.footern= [
            <Button key="back" onClick={this.memoCancel}>取消</Button>,
            <Button key='submit' type="more" onClick={()=>this.memoOk(item)}>确定</Button>
        ]
        this.setState({
            novisible:true,
            nowidth:'400px'
            
        })
    }
    //移入黑名单确定
        memoOk(item){
            // console.log(item)
        axios.post(DOMAIN_VIP+"/blacklist/insert",qs.stringify({
            userid:item.userid,
            userName:item.username,
            memo:this.state.memo
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    lovisible:false,
                    novisible:false,
                    visible:false
                })
                this.requestTable()
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //移入黑名单取消
    memoCancel(){
        // console.log("取消")
        this.setState({
            novisible:false,
            memo:'',
        })
        this.requestTable()
    }

    //重置按钮
    onResetState(){
        this.setState({
            startValue: '',
            todoname:'',
            todonodename:'',
            username:'',
            todostarttimeS:'',
            isOvertime:'',
            time:""
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
    //关闭修改备注弹窗
    handleCancel(){
        this.setState({
            visible:false
        },()=>this.requestTable())
    }

    render(){
        const { taskVisible,noteVisible,todoname,todonodename,username,startValue,isOvertime,novisible,nowidth,time,pageIndex,pageSize,visible,width,title,modalHtml,tableList,pageTotal,tableViewList,totalCoin,totalWithdraw, lodalHtml,lotitle,lovisible,lowidth} = this.state;
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：工作台 > 我的待办
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {taskVisible&&<div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">事项</th>
                                                <th className="column-title">事项节点</th>
                                                <th className="column-title">用户名（%）</th>
                                                <th className="column-title">任务发起时间</th>
                                                <th className="column-title">是否超时</th>
                                                <th className="column-title">操作</th>                                                
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>
                                                    <Select value={todoname} style={{width:'120px'}} onChange={this.handleChangeTodo}>
                                                        <Option value="">请选择</Option>
                                                        <Option value="1">Google审核</Option>
                                                        <Option value="2">手机审核</Option>
                                                        <Option value="3">身份认证</Option>
                                                    </Select>
                                                </td>
                                                <td>
                                                    <Select value={todonodename} style={{width:'120px'}} onChange={this.handleChangeTodoName}>
                                                        <Option value="">请选择</Option>
                                                        <Option value="1">更改Google</Option>
                                                        <Option value="2">更改手机</Option>
                                                        <Option value="3">认证审核</Option>
                                                    </Select>
                                                </td>
                                                <td>
                                                    <input type="text" name="username" value={username} className="form-control" onChange={this.handleInputChange} />
                                                </td>
                                                <td>
                                                    <DatePicker onChange={this.onChangeTime} value={time} />
                                                </td>
                                                <td>
                                                    <Select value={isOvertime} onChange={this.handleChangeOverTime}>
                                                        <Option value=''>请选择</Option>
                                                        <Option value="1">是</Option>
                                                        <Option value="2">否</Option>
                                                    </Select>
                                                </td>
                                                <td className='right' style={{float:'none'}}>
                                                    <Button type="primary" onClick={() => this.inquireBtn()}>查询</Button>
                                                    <Button type="primary" onClick={this.onResetState}  >重置</Button>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div> 
                     
                            <div className="x_title">
                                <h3>我的待办列表</h3>
                            </div>
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">事项</th>
                                                <th className="column-title">事项节点</th>
                                                <th className="column-title">单号</th>
                                                <th className="column-title">用户编号</th>
                                                <th className="column-title">事项发起时间</th>
                                                <th className="column-title">计划完成时间</th>
                                                <th className="column-title">操作</th>                                         
                                            </tr>
                                        </thead>                    
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return(
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.todoname}</td>
                                                            <td>{item.todonodename}</td>  
                                                            <td>{item.busid}</td>  
                                                            <td>{item.userid}</td>                                                  
                                                            <td>{moment(item.todostarttime).format(TIMEFORMAT)}</td>
                                                            <td>{moment(item.todoplancomptime).format(TIMEFORMAT)}</td>                                                            
                                                            <td>{item.todostate==1?<Button type="more" onClick={() => this.openModal(item)}>领办</Button>:
                                                                item.todostate ==2?<Button type="more" onClick={() => this.openModal(item)}>办理</Button>:"完成"}</td>
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
                                                showQuickJumper />
                                }
                                </div>
                            </div>
                        </div>}
                    </div>
                </div>

                <div className="row">
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        {noteVisible&&<div className="x_panel">
                            <div className="x_title">
                                <h3>提现提示，当前共计有<span className="tblue">{totalCoin}</span>个币种，<span className="tblue">{totalWithdraw}</span>笔提现。</h3>
                            </div>
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action">
                                        <thead className="headings">  
                                            <tr>
                                                <th className="column-title">序号</th>
                                                <th className="column-title">资金类型</th>
                                                <th className="column-title">待确认笔数</th>
                                                <th className="column-title">待确认金额</th>
                                                <th className="column-title">操作</th>        
                                            </tr>                
                                        </thead>
                                        <tbody>
                                            {
                                                tableViewList.length>0?
                                                tableViewList.map((item,index)=>{
                                                    return(
                                                        <tr key={index}>
                                                            <td>{index+1}</td>
                                                            <td>{item.fundTypeName}</td>
                                                            <td>{item.waitCount}</td>  
                                                            <td>{item.waitAmount}</td>  
                                                            <td><a href="javascript:void(0)" onClick={this.props.jumpCom}>提现审核</a></td>                                                                                                              
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                            </div>  
                        </div>}
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 50 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}            
                </Modal>
                <Modal
             visible={lovisible}
             width={lowidth}
             title={lotitle}
             maskClosable={false}
             footer={this.footerr}
             >
             {lodalHtml}            
         </Modal>
         <Modal
                    visible={novisible}
                    width={nowidth}
                    maskClosable={false}
                    footer={this.footern}
                    >
                     <div className="col-md-12 col-sm-12 col-xs-12">
                        <label className="col-sm-12 control-label">请输入移入原因：</label>
                        <div className="col-sm-12">
                            <input type="text" className="form-control"  name="memo" value={this.state.memo} onChange={this.handleInputChange} />
                        </div>
                        </div>           
                </Modal>
            </div>
             
        )
    }

}






























