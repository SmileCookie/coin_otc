import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalModifyRole from './modal/modalModifyRole'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { pageLimit,tableScroll } from '../../../utils'
import { Input,Modal,Button,Pagination,message} from 'antd'

export default class MailManage extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            sendName:'',
            fromAddr:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            visible:false,
            modalHtml:'',
            addName:'',
            addAddr:'',
            mailServerHost:'',
            mailServerPort:'',
            emailUserName:'',
            emailPassword:'',
            title:'',
            width:'600px',
            tableList:'',
            status:'',
            limitBtn: [],
            loading:false,
            height:0,
            tableScroll:{
                tableId:'MLMAG',
                x_panelId:'MLMAGEX',
                defaultHeight:500,
            }
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.deleteRole = this.deleteRole.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modifyRole = this.modifyRole.bind(this)
        this.onChangeRatio = this.onChangeRatio.bind(this)
        this.modifyRoleBtn = this.modifyRoleBtn.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('SendEmailAccount', this.props.permissList)
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
    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
     //点击收起
     clickHide(){
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
    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())    
    }
    deleteRole(id){
        let self = this;
        Modal.confirm({
            title: '您确定要删除此条数据?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/SendEmailAccount/delete',qs.stringify({
                        id
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.requestTable()
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    requestTable(currIndex,currSize){
        const { sendName,fromAddr,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+'/SendEmailAccount/list',qs.stringify({
            sendName,fromAddr,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.page.list,
                    pageTotal:result.page.totalCount
                })
            }   
        })
    }
    modifyRole(item,type){
        const {id,sendName,fromAddr,mailServerHost,mailServerPort,emailUserName,emailPassword,status} = item
        this.footer=[
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modifyRoleBtn(id,type)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            title:'添加/编辑邮件帐户',
            modalHtml:<ModalModifyRole item={item} onChangeRatio={this.onChangeRatio} handleInputChange={this.handleInputChange}/>,
            addName:sendName||'',
            addAddr:fromAddr||'',
            mailServerHost:mailServerHost||'',
            mailServerPort:mailServerPort||'',
            emailUserName:emailUserName||'',
            emailPassword:emailPassword||'',
            status:status||1
        })
    }
    onChangeRatio(e){
        this.setState({
            status:e.target.value,
        })
        console.log(e.target.value)
    }
      //弹窗隐藏
      handleCancel(){
        this.setState({ 
            visible: false,
            loading:false
        });
    }
    onResetState(){
        this.setState({
            sendName:'',
            fromAddr:''
        })
    }
    modifyRoleBtn(id,type){
        const {addName,addAddr,mailServerHost,mailServerPort,emailUserName,emailPassword,status}= this.state
        let inorupStatus = type?2:1;
        let sendUrl = inorupStatus == 1?"/SendEmailAccount/insert":'/SendEmailAccount/update'
        if(addName== ''){
            message.error("请输入发送名称！")
        }else if(addAddr==''){
            message.error("请输入发送邮箱！")
        }else if(mailServerHost==''){
            message.error("请输入HOST！")
        }else if(mailServerPort==''){
            message.error("请输入端口！")
        }else if(emailUserName==''){
            message.error("请输入账号！")
        }else if(emailPassword==''){
            message.error("请输入密码！")
        }else{
            this.setState({
                loading:true
            })
            axios.post(DOMAIN_VIP + sendUrl,qs.stringify({
                id,sendName:addName,fromAddr:addAddr,mailServerHost,mailServerPort,emailUserName,emailPassword,inorupStatus,status
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    this.setState({
                        visible:false,
                        loading:false
                    })
                    this.requestTable()
                }else{
                    message.warning(result.msg)
                    this.setState({
                        loading:false
                    })
                }
            })  
        }  
    }

    render(){
        const {showHide,sendName,fromAddr,visible,modalHtml,title,width,tableList,pageIndex,pageSize,pageTotal,limitBtn }=this.state
            return(
                <div className="right-con">
                    <div className="page-title">
                    当前位置：系统中心>系统管理>邮箱管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-5 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发送名称：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="sendName" value={sendName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-5 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发送邮箱：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="fromAddr" value={fromAddr} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.inquireBtn()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={this.modifyRole}>新增</Button>:''}
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
                                                    <th className="column-title">编号</th>
                                                    <th className="column-title">发送名称</th>
                                                    <th className="column-title">发送邮箱</th>  
                                                    <th className="column-title">HOST/端口</th>    
                                                    <th className="column-title">账号/密码</th>
                                                    <th className="column-title">状态</th>
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
                                                            <td>{item.id}</td>
                                                            <td>{item.sendName}</td>
                                                            <td>{item.fromAddr}</td>
                                                            <td>{item.mailServerHost+'/'+item.mailServerPort}</td>
                                                            <td>{item.emailUserName+'/'+item.emailPassword}</td>
                                                            <td>{(() => {
                                                                switch (item.status) {
                                                                case 1:
                                                                    return "可用" 
                                                                    break;
                                                                case 2:
                                                                    return "不可用" 
                                                                    break;
                                                                default:
                                                                    return ""
                                                                    break; 
                                                                    }
                                                                })()
                                                            }</td>
                                                            <td>
                                                                {limitBtn.indexOf('delete')>-1?<a className="mar20" href="javascript:void(0)" onClick={() => this.deleteRole(item.id)}>删除</a>:''}
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)"onClick={() => this.modifyRole(item,"mod")}>修改</a>:''}
                                                            </td>
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
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    { modalHtml }
                </Modal>
            </div>
        )
    }
}