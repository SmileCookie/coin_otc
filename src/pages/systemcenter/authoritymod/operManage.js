import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import ModalManage from './modal/modalManage'
import ModalRole from './modal/modalRole'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Select,Modal,Button,Table,Pagination,Radio,message } from 'antd'
import GoogleCode from '../../common/modal/googleCode'
import { pageLimit,tableScroll } from '../../../utils/index'
const Option = Select.Option;
const RadioGroup = Radio.Group;

export default class OperManage extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            qroleId:'0',
            qstatus:'',
            qlock:'',
            quserId:'',
            quserName:'',
            qrealName:'',
            sexValue:1,
            statusValue:1,
            page:PAGEINDEX,
            limit:PAGESIZE,
            tableList:[],
            pageTotal:0,
            visible:false,
            title:'',
            modalHtml:'',
            roleIdList:[],
            limitBtn: [],
            checkGoogle:'',
            ngoogleCode:"",
            check:'',
            googVisibal:false,
            item:'',
            type:'',
            height:0,
            tableScroll:{
                tableId:'OEMAGE',
                x_panelId:'OEMAGEX',
                defaultHeight:500,
            }
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetParams = this.onResetParams.bind(this)
        this.handleChangeroleId = this.handleChangeroleId.bind(this)
        this.handleChangeStatus = this.handleChangeStatus.bind(this)
        this.handleChangeGoogle = this.handleChangeGoogle.bind(this)
        this.onAddModal = this.onAddModal.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onAddModalBtn = this.onAddModalBtn.bind(this)
        this.onSelectroleIds = this.onSelectroleIds.bind(this)
        this.deleteManage = this.deleteManage.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.onResetPwd = this.onResetPwd.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.resetGoogle = this.resetGoogle.bind(this)
        this.choosePerBtn = this.choosePerBtn.bind(this)
        this.handleChangePer = this.handleChangePer.bind(this)
        this.deleteManageItem = this.deleteManageItem.bind(this)
        this.resetPwd = this.resetPwd.bind(this)
        this.onResetGoogle = this.onResetGoogle.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('user', this.props.permissList)
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
    handleInputChange(event,check) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
 
    //重置
    onResetParams(){
        this.setState({
            qroleId:'0',
            qstatus:'0',
            qlock:'',
            quserId:'',
            quserName:'',
            qrealName:'',
            qstatus:'',
        })
    }
    //角色 select
    handleChangeroleId(value){
       this.setState({
            qroleId:value
       })
    }
    //账户状态 select
    handleChangeStatus(value){
        this.setState({
            qstatus:value
        })
    }
    //
    handleChangeGoogle(value){
        this.setState({
            qlock:value
        })
    }
    //角色选择带回
    onSelectroleIds(keyRows){   
        this.setState({
            roleIdList:keyRows
        })
    }
    //新增
    onAddModal(userId){
        let titleTop =  userId?'修改操作员':'新增操作员'
        axios.post(DOMAIN_VIP+"/common/getUserUpdOrAddInfo",qs.stringify({
            userId:userId||''
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.footer = [
                    <Button key="back" onClick={this.handleCancel}>取消</Button>,
                    <Button key="submit" type="more" onClick={() => this.onAddModalBtn(userId)}>
                        确认
                    </Button>
                ];
                this.setState({
                    visible:true,
                    width:'900px',
                    title:titleTop,
                    modalHtml:<ModalManage 
                                item={result.data} 
                                userId={userId} 
                                onSelectroleIds={this.onSelectroleIds}
                                handleInputChange={this.handleInputChange}
                                choosePerBtn={this.choosePerBtn}
                                handleChangePer={this.handleChangePer} />
                })
                if(userId){
                    const {secret} = result.data
                    const {userId,username,realname,email,mobile,status,roleIdList,salt,createTime,lock,dataPermission,deptId,deptName} = result.data.sysUser                    
                    this.setState({
                        userId,username,realname,email,mobile,secret,status,roleIdList,salt,createTime,lock,dataPermission,deptId,deptName
                    })
                }else{
                    const {userId,secret} = result.data
                    this.setState({
                        userId,
                        secret,
                        roleIdList:[],
                        username:'',
                        password:'',
                        email:'',
                        mobile:'',
                        status:'',
                        dataPermission:'',
                        deptId:'',
                        deptName:'',
                        checkGoogleCode:'',
                        realName:''
                    })
                }
            }else{
                message.warning(result.msg)
            }
        })
    }
    //新增修改 弹窗按钮
    onAddModalBtn(type){
        let url = type ? "/sys/user/update":"/sys/user/save";
        const {userId,username,checkGoogleCode,password,email,mobile,secret,googleCode,adminGoogleCode,status,roleIdList,salt,createTime,lock,realname,dataPermission,deptId,deptName} = this.state        
        if(!adminGoogleCode){
            message.warning("请输入管理员Google！")
            return false;
        }
        if(!username){
            message.warning("登录名不能为空！")
            return false;
        }
        if(!type&&!password){
            message.warning('密码不能为空！')
            return false
        }
        if(!type&&!googleCode){
            message.warning('google验证码不能为空！')
            return false
        }
        axios.post(DOMAIN_VIP+url,{
            userId,
            username,
            password,
            email,
            mobile,
            secret,
            googleCode,
            adminGoogleCode,
            status,
            roleIdList,
            salt,
            createTime,
            lock,
            realname,
            dataPermission,
            deptId,
            checkGoogleCode,
        }).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                this.requestTable()
                message.success(result.msg)
            }else{
                message.warning(<span dangerouslySetInnerHTML={{__html: result.msg}} />)
            }
        })
    }
     //弹窗隐藏
     handleCancel(){
        this.setState({ visible: false });
    }
    //查询按钮
    inquireBtn(){
        this.setState({
            page:PAGEINDEX
        },() => this.requestTable())
    }
    //重置密码
    resetPwd(userId){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+"/sys/user/resetPassword",qs.stringify({
                userId
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    resolve(result.msg)
                }else{
                    message.warning(result.msg)
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
    }
    //重置密码弹框
    onResetPwd(userId,type){
        let self = this
        Modal.confirm({
            title: "你确定要重置密码吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                self.modalGoogleCode(userId, type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    //操作员列表信息
    requestTable(currIndex,currSize){
        const { page,limit,quserName,quserId,qrealName,qroleId,qstatus,qlock } = this.state
        axios.get(DOMAIN_VIP+'/sys/user/list',{
            params:{
                page:currIndex||page,
                limit:currSize||limit,
                userName:quserName,
                userId:quserId,
                realName:qrealName,
                roleId:qroleId,
                status:qstatus,
                lock:qlock
            }
        }).then(res => {
            const result = res.data;
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

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            page
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
    //删除管理员Item
    deleteManageItem(roleId){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/sys/user/delete', 
                [roleId]
            ).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg);
                    this.requestTable();
                    resolve(result.msg)
                }else{
                    message.warning(result.msg)
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
    }
    //删除管理员信息弹窗
    deleteManage(roleId,type){
        let self = this;
        Modal.confirm({
            title: '您确定要删除此管理员?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk(){
                self.modalGoogleCode(roleId, type,'check')
            },
            onCancel() {
                console.log('Cancel');
            },
        });
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
            // }
    }
    //google 弹窗添加 回撤 事件
    modalGoogleCodekeyPress(e){
        const { visible } = this.state
        if(visible){
            if(e.keyCode == 13){
                if(this.input&&this.input.value){
                    this.googleBtn.props.onClick()
                }
            }
        }
    }
    //google 验证弹窗
    modalGoogleCode(item,type,check){
        this.setState({
            googVisibal:true,
            item,
            type,
            check,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const {item,type,check } = this.state
        const { googleCode,checkGoogle } = value
        let url =  check ?"/common/checkTwoGoogleCode":"/common/checkGoogleCode"
        axios.post(DOMAIN_VIP+url,qs.stringify({
            googleCode,checkGoogle
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(type == 'del'){
                    this.setState({
                        googVisibal:false
                    },() => this.deleteManageItem(item))
                    
                }else if(type == 'pwd'){
                    this.setState({
                        googVisibal:false
                    },()=>this.resetPwd(item))
                    
                }else if(type == 'google'){
                    this.setState({
                        googVisibal:false
                    },()=>this.onResetGoogle(item))
                }
            }else{
                message.warning(result.msg)
            }
        })
    }
    //解锁谷歌
    onResetGoogle(id){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/userInfo/clearGoogle',qs.stringify({
                userId:id
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    this.requestTable()
                    resolve(result.msg)
                }else{
                    message.warning(result.msg)
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
    }
    //解锁谷歌弹窗
    resetGoogle(id,type){
        let self = this;
        Modal.confirm({
            title: '确定要解锁该管理员吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                self.modalGoogleCode(id, type);
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //部门
    choosePerBtn(deptName,deptId){
        this.setState({
            deptName,
            deptId
        })
    }
    //select 数据权限
    handleChangePer(val){
        this.setState({
            dataPermission:val
        })
    }
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.modalGoogleCodeBtn(values)
        });
      }
      saveFormRef(formRef){
        this.formRef = formRef;
      }
        //谷歌弹窗关闭
    onhandleCancel(){
        this.setState({
            googVisibal: false 
        })
    }

    render(){
        const { showHide,qroleId,qstatus,qlock,quserName,qrealName,quserId,tableList,pageTotal,page,limit,visible,title,width,modalHtml,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 权限管理 > 操作员管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">操作员编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" value={quserId} name="quserId" onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">登录名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" value={quserName} name="quserName" onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">操作员真实姓名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" value={qrealName} name="qrealName" onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <ModalRole roleId={qroleId}  handleChange={this.handleChangeroleId} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">账户状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={qstatus} style={{ width: SELECTWIDTH }} onChange={this.handleChangeStatus}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">正常</Option>
                                                <Option value="0">禁用</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">谷歌状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={qlock} style={{ width: SELECTWIDTH }} onChange={this.handleChangeGoogle}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">正常</Option>
                                                <Option value="2">已锁定</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetParams}>重置</Button>
                                        {limitBtn.indexOf('save')>-1?<Button type="primary" onClick={()=>this.onAddModal()}>新增</Button>:''}
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
                                                <th className="column-title">操作员编号</th>
                                                <th className="column-title">登录名</th>
                                                <th className="column-title">真实姓名</th>
                                                <th className="column-title">邮箱</th>
                                                <th className="column-title">电话</th>
                                                <th className="column-title min_116px">角色</th>
                                                <th className="column-title">账户状态</th>
                                                <th className="column-title">谷歌状态</th>
                                                <th className="column-title min_153px">操作</th>                                                                                              
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(page-1)*limit+index+1}</td>
                                                            <td>{item.userId}</td>
                                                            <td>{item.username}</td>
                                                            <td>{item.realname}</td>
                                                            <td>{item.email}</td>
                                                            <td>{item.mobile}</td>
                                                            <td>{item.roleNames}</td>
                                                            <td>{item.status==1?"正常":"禁用"}</td>
                                                            <td>{item.lock==1?'正常':item.lock==2?'已锁定':''}</td>
                                                            <td>
                                                                {limitBtn.indexOf('delete')>-1?<a className="mar20" href="javascript:void(0)" onClick={() => this.deleteManage(item.userId,'del')}>删除</a>:''}
                                                                {limitBtn.indexOf('update')>-1?<a className="mar20" href="javascript:void(0)" onClick={() => this.onAddModal(item.userId)}>修改</a>:''}
                                                                {limitBtn.indexOf('resetPassword')>-1?<a className="mar20" href="javascript:void(0)" onClick={() => this.onResetPwd(item.userId,'pwd')}>重置密码</a>:''}
                                                                {limitBtn.indexOf('lock')>-1?<a href="javascript:void(0)" onClick={() => this.resetGoogle(item.userId,'google')}>解锁谷歌锁定</a>:''}                                                                
                                                            </td>
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

                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml}
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='OM'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}




















































