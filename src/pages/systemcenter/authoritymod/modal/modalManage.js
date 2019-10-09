import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import ModalTable from './modalTable'
import MoadlPer from './modalPer'
import { Select,Modal,Button,Table,Pagination,Radio,Input,message } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../../conf/index';
const Option = Select.Option;
const RadioGroup = Radio.Group;
const { TextArea } = Input;
import GoogleCode from '../../../common/modal/googleCode'

export default class ModalManage extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            username:'',
            password:'',
            email:'',
            mobile:'',
            secret:'',
            googleCode:'',
            status:'',
            roleIdList:'',
            visible:false,
            title:'',
            width:'',
            modalHtml:'',
            imgSrc:'',
            roleNames:'',
            realname:'',
            adminGoogleCode:'',
            password:'',
            dataPermission:'',
            deptId:'',
            deptName:'',
            checkGoogleCode:'',
            check:'',
            googVisibal:false,
            item:'',
            type:'',
            EWM:false,
            mdisabled:true
            
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.chooseRoles = this.chooseRoles.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.onSelectRoles = this.onSelectRoles.bind(this)
        this.choosePer = this.choosePer.bind(this)
        this.handleChangePer = this.handleChangePer.bind(this)
        this.choosePerBtn = this.choosePerBtn.bind(this)
        this.choosePerBtn = this.choosePerBtn.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.watchGoogleCode = this.watchGoogleCode.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.createEWM = this.createEWM.bind(this)
    }

    componentDidMount(){
        let paramsName = ''
        if(this.props.userId){
            const { username,realname,mobile,email,status,roleNames,roleIdList,deptName,deptId,dataPermission } = this.props.item.sysUser
            const { secret } = this.props.item
            this.setState({
                username,realname,mobile,email,status,roleNames,roleIdList,secret,deptName,deptId,dataPermission,
                adminGoogleCode:'',
                password:'',
                checkGoogleCode:'',                
            });
            paramsName = username;
        }else{
            const { secret } = this.props.item
            this.setState({
                secret,
                username:'',
                realname:'',
                mobile:'',
                email:'',
                status:'',
                roleNames:'',
                roleIdList:'',
                adminGoogleCode:'',
                password:'',
                dataPermission:'',
                deptId:'',
                deptName:'',
                checkGoogleCode:'',
                EWM:false,
                //mdisabled:this.props.disabled              
            })
        }
        // let url = this.props.item.url
        //     axios.post(DOMAIN_VIP+url,qs.stringify({
        //         userName:paramsName
        //     })).then(res => {
        //         const result = res.data;
        //         this.setState({
        //             imgSrc:result
        //         })
        //     })
    }

    componentWillReceiveProps(nextProps){
        let paramsName = ''        
        if(nextProps.userId){
            const { username,realname,mobile,email,status,roleNames,roleIdList,deptName,deptId,dataPermission } = nextProps.item.sysUser
            const { secret } = nextProps.item
            this.setState({
                username,realname,mobile,email,status,roleNames,roleIdList,secret,deptName,deptId,dataPermission,
                adminGoogleCode:'',
                password:'',
                googleCode:'',
                checkGoogleCode:''
                
            })
            paramsName = username
        }else{
            const { secret } = nextProps.item
            this.setState({
                secret,
                username:'',
                realname:'',
                mobile:'',
                email:'',
                status:'',
                roleNames:'',
                roleIdList:'',
                adminGoogleCode:'',
                password:'',
                dataPermission:'',
                deptId:'',
                deptName:'',
                googleCode:'',
                checkGoogleCode:'',
                EWM:false,
                //mdisabled:this.props.disabled 
                
            })
        }
    }
    //选择带回
    onSelectRoles(selectedRowKeys,selectedRows){
        console.log(selectedRowKeys)
        console.log(selectedRows)
        let roleNames = '';
        for(let i=0;i<selectedRows.length;i++){
            roleNames+=selectedRows[i].roleName+','
        }
        this.setState({
            roleIdList:selectedRowKeys,
            roleNames
        })
        this.props.onSelectroleIds(selectedRowKeys)
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        if(name == 'username'){
            this.setState({
                [name]: value,
                mdisabled:true,
                //EWM:false
            }); 
        }
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }

    //弹窗隐藏
    handleCancel(){
        this.setState({
            visible: false,
            selectedRowKeys:[],
            selectedRows:[]
        });
    }
    //确认带回
    handleOk(){
        this.setState({
            visible: false
        })
    }

    //选择角色
    chooseRoles(){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={this.handleOk}>
                确定
            </Button>,
        ]
        this.setState({
            visible:true,
            title:'角色列表',
            width:'700px',
            modalHtml:<ModalTable onSelectRoles={this.onSelectRoles} roleIdList={this.state.roleIdList} />
        })
    }

    //select 数据权限
    handleChangePer(val){
        this.setState({
            dataPermission:val
        })
        this.props.handleChangePer(val)
    }
    //所属部门选择带回
    choosePer(){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={this.handleCancel}>
                确定
            </Button>,
        ]
        this.setState({
            visible:true,
            title:"部门选择",
            width:"800px",
            modalHtml:<MoadlPer choosePerBtn={this.choosePerBtn} />
        })
    }
    //所属部门选择带回 按钮
    choosePerBtn(deptName,deptId){
        this.setState({
            deptName,
            deptId,
            visible:false
        })
        this.props.choosePerBtn(deptName,deptId)
    }
    //生成二维码
    createEWM(){
        let url = this.props.item.url
        //console.log(url)
        const {paramsName,username} = this.state
        axios.post(DOMAIN_VIP+url,qs.stringify({
            userName:username
        })).then(res => {
            const result = res.data;
            this.setState({
                imgSrc:result,
                mdisabled:false,
                EWM:true
            })
        })
    }
  
    //google 验证弹窗
    modalGoogleCode(item,type){
        this.setState({
            googVisibal:true,
            item,
            type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { item,type } = this.state
        const {googleCode } = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({googVisibal:false})
                this.watchGoogleCode()
            }else{
                message.warning(result.msg)
            }
        })
    }

    //查看按钮
    watchGoogleCode(){
        const { url} = this.props.item;
        //console.log(url)
        const { secret,imgSrc,username } = this.state
        axios.post(DOMAIN_VIP+url,qs.stringify({
            userName:username
        })).then(res => {
            const result = res.data;
            //console.log(result)
            this.setState({
                //imgSrc:result,
                visible:true,
                width:'400px',
                title:'查看google标识',
                modalHtml:<div className="col-md-12 col-sm-12 col-xs-12">
                        <input type="text" className="form-control" name="secret" value={secret||''} readOnly />
                        <img src={"data:image/png;base64,"+result} alt="tupian"/>
                      </div>

            })
        })
        
        
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={this.handleCancel}>
                确定
            </Button>,
        ]
        // this.setState({
            
        // })
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
        const { imgSrc,visible,width,title,modalHtml,checkGoogleCode,deptName,roleIdList,dataPermission,roleNames,username,realname,mobile,email,googleCode,adminGoogleCode,password,status,secret,mdisabled,EWM } = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">登录名:<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control" name="username" value={username||''} onChange={this.handleInputChange} readOnly={this.props.userId?true:false}/>
                        </div>
                    </div>               
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">真实姓名:</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control" name="realname" value={realname||''} onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">电话:</label>
                        <div className="col-sm-8">
                        
                            <input type="text" className="form-control" name="mobile" value={mobile||''} onChange={this.handleInputChange}/>
                        </div>
                    </div> 
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">邮箱:<i>*</i></label>
                        <div className="col-sm-8">
                        <input type="text" className="form-control" name="email" style={{display:'none'}}/>
                            <input type="text" className="form-control" name="email" value={email||''} onChange={this.handleInputChange}/>
                        </div>
                    </div> 
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">角色:</label>
                        <div className="col-sm-8 locabox">
                            <TextArea style={{width:'150px',height:'80px'}} value={roleNames||''} name="roles" />
                            <Button type="more" onClick={this.chooseRoles}>选择</Button>
                        </div>
                    </div> 
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">账户状态:</label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="status" value={status}>
                                <Radio value={1}>正常</Radio>
                                <Radio value={0}>注销</Radio>
                            </RadioGroup>
                        </div>
                    </div> 
                </div>
                <div className="form-group-box martop10">
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-4 control-label">Google标识:</label>
                            <div className="col-sm-8 line34">
                                {this.props.userId?
                                    <span>
                                        <a href="javascript:void(0)" onClick={this.modalGoogleCode}>查看</a>
                                        <input type="hidden" className="form-control" name="secret" value={secret||''} />
                                    </span>:
                                    <div>
                                        <input type="text" className="form-control" name="secret" value={secret||''} readOnly />
                                        {EWM?<img src={"data:image/png;base64,"+imgSrc} />:''}<br />
                                        {username&&mdisabled?<Button type="more" onClick={this.createEWM}  >生成二维码</Button>:<Button type="more" disabled >生成二维码</Button>}
                                    </div>
                                }
                            </div>
                        </div> 
                    </div>
                   {
                       !this.props.userId&&<div className="col-md-6 col-sm-6 col-xs-6">
                                                <div className="form-group">
                                                    <label className="col-sm-4 control-label">Google验证码:<i>*</i></label>
                                                    <div className="col-sm-8">
                                                        <input type="text" className="form-control" name="googleCode" value={googleCode} onChange={this.handleInputChange}/>
                                                    </div>
                                                </div> 
                                            </div>
                   }
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-4 control-label">管理员Google:<i>*</i></label>
                            <div className="col-sm-8">
                                <input type="text" className="form-control" name="adminGoogleCode" value={adminGoogleCode} onChange={this.handleInputChange}/>
                            </div>
                        </div> 
                    </div>
                    {roleIdList&&!roleIdList.includes(999)&&<div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-4 control-label">监管员Google:</label>
                            <div className="col-sm-8">
                            <input type="text" className="form-control" name="checkGoogleCode" style={{display:'none'}}/>
                                <input type="text" className="form-control" name="checkGoogleCode" value={checkGoogleCode} onChange={this.handleInputChange}/>
                            </div>
                        </div> 
                    </div>
                }
                    
                    {!this.props.userId&&<div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-4 control-label">密码:<i>*</i></label>
                            <div className="col-sm-8">
                            <input type="password" className="form-control" name="password" style={{display:'none'}}/>
                                <input type="password" className="form-control" name="password" value={password} onChange={this.handleInputChange}/>
                            </div>
                        </div> 
                    </div>}
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-4 control-label">所属部门:</label>
                            <div className="col-sm-8 locabox">
                                <input type="text" className="form-control" style={{width:'150px'}} value={deptName||''} name="deptName" readOnly/>
                                <Button type="more" onClick={this.choosePer}>选择</Button>
                            </div>
                        </div> 
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-4 control-label">数据权限:</label>
                            <div className="col-sm-8 locabox">
                                <Select value={dataPermission} style={{ width: SELECTWIDTH }} onChange={this.handleChangePer}>
                                    <Option value="">请选择</Option>
                                    <Option value={1}>全部</Option>
                                    <Option value={2}>部门</Option>
                                    <Option value={3}>本人</Option>                                    
                                </Select>
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
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='MMGE'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}










































