/**
 * 
 * 老的用户信息
 */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { PAGESIZE,PAGEINDEX,DOMAIN_VIP,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button,Tabs,Select,Pagination,Modal,message,Input } from 'antd'
import GoogleCode from '../../common/modal/googleCode'
import moment from 'moment'
import ModalMemo from './moadl/modalMemo'
import ModalAddAndDeleteTag from './moadl/modalAddAndDeleteTag'
import ModalSafe from './moadl/moadlSafe'
import ModalSafeManage from './moadl/modalSafeManage'
import ModalCustomerOpe from './moadl/modalCustomerOpe'
import UserType from './moadl/userType'
import { pageLimit,tableScroll } from '../../../utils'
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { TextArea } = Input;


export default class UserInfo extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            userId:'',
            userName:'',
            loginIp:'',
            recommendName:'',
            customerType:'0',
            customerOperation:'0',
            freez:'0',
            memo:'',
            tabKey:'real',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            visible:false,
            modifyTextArea:'',
            modifyUserId:'',
            modifyUserName:'',
            loading:false,
            modalHtml:'',
            googleCode:'',
            limitBtn: [],
            checkedValues:[],
            checkGoogle:'',
            googVisibal:false,
            googleSpace:'',
            type:'',
            height:0,
            tableScroll:{
                tableId:'USIO',
                x_panelId:'USIOX',
                defaultHeight:500,
            },
            tagsSelect: [],// 标签的选择
            tagMemo: ''//删除标签的备注
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestActivatedTable = this.requestActivatedTable.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.callbackTabsKey = this.callbackTabsKey.bind(this)
        this.handleCustomerOperation = this.handleCustomerOperation.bind(this)
        this.handleCustomerType = this.handleCustomerType.bind(this)
        this.handleChangeFreez = this.handleChangeFreez.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onModifyMemoSave = this.onModifyMemoSave.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onClearIP = this.onClearIP.bind(this)
        this.onClearIpBtn = this.onClearIpBtn.bind(this)
        this.onSafeManage = this.onSafeManage.bind(this)
        this.modalCustomerType = this.modalCustomerType.bind(this)
        this.handleModalCustomer = this.handleModalCustomer.bind(this)
        this.modalCustomerBtn = this.modalCustomerBtn.bind(this)
        this.modalCustomerOpe = this.modalCustomerOpe.bind(this)
        this.onChangeCheckbox = this.onChangeCheckbox.bind(this)
        this.freezeUserBtn = this.freezeUserBtn.bind(this)
        this.freezeUser = this.freezeUser.bind(this)
        this.userSafeInfo = this.userSafeInfo.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.freezeUserGoogle = this.freezeUserGoogle.bind(this)
        
        this.revertBtn = this.revertBtn.bind(this)
        this.deleteTableList = this.deleteTableList.bind(this)
        this.isOkey = this.isOkey.bind(this)
        //this.deleteTableListItem = this.deleteTableListItem(this)
        this.freezeUserModal = this.freezeUserModal.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestActivatedTable()
        this.setState({
            limitBtn: pageLimit('userInfo', this.props.permissList)
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

    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        })
        this.requestActivatedTable(this.state.tabKey,PAGEINDEX)
    }
    //table 请求
    requestActivatedTable(key,currIndex,currSize){
        const { userId,userName,loginIp,recommendName,customerType,customerOperation,freez,memo,tabKey,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+'/userInfo/queryUser',qs.stringify({
            userId:userId,
            userName:userName,
            loginIp:loginIp,
            recommendName:recommendName,
            customerType:customerType,
            customerOperation:customerOperation,
            freez:freez,
            memo:memo,
            tab:key||tabKey,
            userType:'1',
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            console.log(result)
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

    //重置查询条件
    onResetState(){
        this.setState({
            userId:'',
            userName:'',
            loginIp:'',
            recommendName:'',
            customerType:'0',
            customerOperation:'0',
            freez:'0',
            memo:'',
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event,check){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
   
    //select 用户类型
    handleCustomerType(val){
        this.setState({
            customerType:val
        })
    }

    //select 用户操作类型
    handleCustomerOperation(val){
        this.setState({
            customerOperation:val
        })
    }

    //select 冻结标志
    handleChangeFreez(val){
        this.setState({
            freez:val
        })
    }

    //tabs 返回 key
    callbackTabsKey(key){
        this.setState({
            tabKey:key
        })
        this.requestActivatedTable(key)
    }
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestActivatedTable(this.state.tabKey,page,pageSize))
        
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestActivatedTable(this.state.tabKey,current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }

    //删除tableItem
    deleteTableListItem(id){
        axios.post(DOMAIN_VIP+'/userInfo/updUserById',qs.stringify({
                userId:id
            })).then(res => {
                const result = res.data
                if(result.code == 0){
                    message.success(result.msg)
                    this.requestActivatedTable()
                }else{
                    message.warning(result.msg);
                } 
            })
    }
    //删除 tableItem弹框
    deleteTableList(id,type,check){
        let self = this;
        Modal.confirm({
            title:"确定要删除此条记录",
            okType: 'danger',
            onOk() {
                self.freezeUser(id,type,check)
            },
            onCancel() {
                console.log('Cancel');
            },
        })
    }

    //显示备注
    showModal(id,name,memo){
        let modalHtml =  <ModalMemo memo={memo}  onChange={this.handleInputChange}  />
        this.footer =[
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={this.onModifyMemoSave}>
              确认
            </Button>,
          ];
        this.setState({
            visible: true,
            modalHtml:modalHtml,
            title:'新增备注',
            modifyUserId:id,
            modifyUserName:name,
            modifyTextArea:memo,
            width:"600px"
        })
        
    }
    //添加备注
    onModifyMemoSave(){
        const { modifyUserId,modifyUserName,modifyTextArea } = this.state;
        axios.post(DOMAIN_VIP+'/userInfo/saveMemoById',qs.stringify({
            userId:modifyUserId,
            userName:modifyUserName,
            memo:modifyTextArea
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                this.requestActivatedTable();
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //关闭修改备注弹窗
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //清理 IP
    onClearIP(){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.freezeUser(0,'clearIP')}>
                确认
            </Button>,
        ]
        if(this.inputclear){
            this.inputclear.value = ""
        }
        let modalHtml = <div className="col-md-12 col-sm-12 col-xs-12"> 
                            <div className="form-group">
                                <label className="col-sm-3 control-label">清理的IP：<i>*</i></label>
                                <div className="col-sm-8">
                                    <input type="text" className="form-control" ref={(inp) => this.inputclear=inp} name="clearIP" onChange={this.handleInputChange} />
                                </div>
                            </div>
                        </div>;
        this.setState({
            width: '600px',
            visible:true,
            modalHtml:modalHtml,
            title:'清理 IP'
        })
    }
    //清理 IP 按钮
    onClearIpBtn(){
        const { clearIP } = this.state
        if(!clearIP){
            message.warning("请输入要清理的 IP ！");
            return;
        }
        axios.post(DOMAIN_VIP+'/userInfo/clearIP',qs.stringify({
            ip:this.state.clearIP
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    visible:false,
                    clearIP:''
                },()=>this.requestActivatedTable())
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //安全管理
    onSafeManage(item){
        const { tabKey } = this.state
        axios.post(DOMAIN_VIP+"/userInfo/safeMange",qs.stringify({
            userId:item.id,
            tab:tabKey
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.footer=[
                    <Button key="back" onClick={this.handleCancel}>取消</Button>,
                    <Button key="submit" type="more" loading={this.state.loading} onClick={this.handleCancel}>
                        确认
                    </Button>,
                ]
                this.setState({
                    visible:true,
                    modalHtml:<ModalSafeManage item={result.Data} />,
                    title:`用户${item.userName}安全管理`,
                    width:"1000px"
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //用户类型弹窗 select
    handleModalCustomer(val){
        this.setState({
            modalCustomer:val
        })
    }
    //用户类型弹窗
    modalCustomerType(type,id,check){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.freezeUser(id,'custype',check)}>
                确认
            </Button>,
        ]
        this.forceUpdate()
        this.setState({
            visible:true,
            modalCustomer:type,
            width:'600px',
            modalHtml:<UserType handleModalCustomer={this.handleModalCustomer} modalCustomer={type} />,
            title:'修改客户类型'
        })
    }
    //按钮 用户类型弹窗
    modalCustomerBtn(id){
        axios.post(DOMAIN_VIP+'/userInfo/modifyCustomerType',qs.stringify({
            userId:id,
            customerType:this.state.modalCustomer
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                message.success(result.msg)
                this.requestActivatedTable()
            }else{
                message.warning(result.msg)
            }
        })
    }

    //修改客户操作类型
    modalCustomerOpe(item,check){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.freezeUser(item,'cusope',check)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            width:'600px',
            title:' 修改客户操作类型',
            // defaultValue:item.customerOperation||[],
            modalHtml:<ModalCustomerOpe item={item} onChange={this.onChangeCheckbox} onlySee={false}/>

        })
    }
    //checkbox 
    onChangeCheckbox(checkedValues) {
        if(checkedValues.target && checkedValues.target.name == 'modifyTextArea') {
            this.setState({
                modifyTextArea:checkedValues.target.value,
            })
        } else {
            this.setState({
                checkedValues:checkedValues,
            })
        }
    }
    //修改客户操作类型 button
    modalCustomerOpeBtn(item){
        let { checkedValues,modifyTextArea } = this.state;
        console.log(checkedValues)
        if(checkedValues.length == 0){
            checkedValues.push('03')
        }else if(checkedValues.length > 1){
            checkedValues = checkedValues.filter((currentValue,index,arr) => {
                return currentValue !== '03'
            })
        }
        axios.post(DOMAIN_VIP+'/userInfo/modifyCustomerOperation',qs.stringify({
            userId:item.id,
            customerOperation:checkedValues.join(),
            operationMark: modifyTextArea
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                message.success(result.msg)
                this.requestActivatedTable()
            }else{
                message.warning(result.msg)
                
            }
        })
    }
    //冻结或者解冻
    freezeUser(id,type,check){
        this.setState({
            googVisibal:true,
            googleSpace:id,
            type,
            check,
        })
    }

    //冻结或者解冻按钮 谷歌验证
    freezeUserGoogle(value){
        const { googleSpace,type,check } = this.state
        const {googleCode,checkGoogle} = value
        let url =  check ?"/common/checkTwoGoogleCode":"/common/checkGoogleCode"
        axios.post(DOMAIN_VIP+url,qs.stringify({
            googleCode,checkGoogle
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(type == 'del'){
                    this.setState({
                        googVisibal:false
                    },()=>this.deleteTableListItem(googleSpace))
                }else if(type == 'rev'){
                    this.setState({
                        googVisibal:false
                    },() => this.revertBtnItem(googleSpace))
                }else if(type == 'clearIP'){
                    this.setState({
                        googVisibal:false
                    },()=>this.onClearIpBtn())
                }else if(type == 'custype'){
                    this.setState({
                        googVisibal:false
                    },()=>this.modalCustomerBtn(googleSpace))
                }else if(type == 'cusope'){
                    this.setState({
                        googVisibal:false
                    },()=>this.modalCustomerOpeBtn(googleSpace))
                }else if(type == 'sensitiveUser'){
                    this.setState({googVisibal:false},()=>this.sensitiveUserBtn(googleSpace))
                }else{
                    this.setState({
                        googVisibal:false
                    },() => this.isOkey(googleSpace,type))
                }
            }else{
                message.warning(result.msg)
            }
        })
    }
    //还原 Item
    revertBtnItem(id){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/userInfo/restoreUserById',qs.stringify({
                userId:id
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    this.requestActivatedTable()
                    resolve(result.msg)
                }else{
                    message.warning(result.msg)
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
    }
    //还原按钮
    revertBtn(id,type,check){
        let self = this;
        Modal.confirm({
            title:'还原用户',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                self.freezeUser(id,type,check)
            },
            onCancel() {
                console.log('Cancel');
            },
        });

    }
    //冻结或者解冻按钮
    freezeUserBtn(id,type){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/userInfo/freez',qs.stringify({
                userId:id
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    this.requestActivatedTable()
                    resolve(result.msg)
                }else{
                    message.warning(result.msg)
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
    }
    //冻结或者解冻按钮弹窗
    freezeUserModal(id,type,check){
        let self = this;
        let title = type?"解冻用户":"冻结用户"
        Modal.confirm({
            title:title,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                self.freezeUser(id,type,check)
            },
            onCancel() {
                console.log('Cancel');
            },
        });

    }
    isOkey(id,type){
        let self = this;
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/userInfo/freez',qs.stringify({
                userId:id
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    self.requestActivatedTable()
                    resolve(result.msg)
                }else{
                    message.warning(result.msg)
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
    }
    //用户看板
    userSafeInfo(item){
        axios.post(DOMAIN_VIP+"/userInfo/infoShow",qs.stringify({
            userId:item.userId,
            userName:item.userName
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.footer = [
                    <Button key="back" onClick={this.handleCancel}>取消</Button>,
                    <Button key="submit" type="more" loading={this.state.loading} onClick={this.handleCancel}>
                        确定
                    </Button>,
                ]
                this.setState({
                    visible:true,
                    title:`用户【${item.userName}】360度信息看板`,
                    width:"1400px",
                    modalHtml:<ModalSafe id={item.id}/>
                })
            }else{
                message.warning(result.msg)
            }
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
    //删除标签
    onDeleteTag = (id, tagName) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.onDeleteTagBtn(id, tagName)}>
                保存修改
            </Button>,
        ]
        this.setState({
            width: '600px',
            title:'删标签',
            visible:true,
            modalHtml:<ModalAddAndDeleteTag memo={''} type={true} selected={[]}  onChange={this.handleInputChange}  />
        })
    }
    onDeleteTagBtn = (id, tagName) => {
        const {tagMemo} = this.state;
        if(!tagMemo){
            message.warning('备注不能为空');
            return;
        }
        axios.post(DOMAIN_VIP+'/userInfo/delUserTag',qs.stringify({
            userId: id,
            userTagName: tagName,
            tagRemark: tagMemo
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible: false,
                    tagMemo: ''
                },()=> {
                    this.requestActivatedTable();
                });
            }else {
                message.warning(result.msg);
            }
        })
    };
    //增加标签
    onAddTag = (arr, id) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.onAddTagBtn(id)}>
                保存修改
            </Button>,
        ];
        this.setState({
            width: '600px',
            title:'贴标签',
            visible:true,
            tagsSelect: arr,
            modalHtml:<ModalAddAndDeleteTag memo={''} type={false} selected={arr} onCheckBoxChange={this.onCheckBoxChange}  />
        })
    };
    onCheckBoxChange=(arr)=> {
        this.setState({
            tagsSelect: arr
        })
    };
    onAddTagBtn = (id) => {
        const {tagsSelect} = this.state;
        if(tagsSelect.length == 0) {
            message.warning("请选择一个标签");
            return;
        }
        axios.post(DOMAIN_VIP+'/userInfo/saveUserTag',qs.stringify({
            userId: id,
            userTagName: tagsSelect.toString()
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible: false
                },()=> {
                    this.requestActivatedTable();
                });
            }else {
                message.warning(result.msg);
            }
        })
    };
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.freezeUserGoogle(values)
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
    sensitiveUser = item => {
        console.log(item)
        let self = this
        let title = item.warningUser == 1 ? '设置为普通用户吗？': '设置为敏感用户吗？'
        Modal.confirm({
            title:title,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                self.freezeUser(item,'sensitiveUser','check')
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    sensitiveUserBtn = item => {
        axios.post(DOMAIN_VIP + '/userInfo/setWarningUser',qs.stringify({userId:item.id,warningUser:item.warningUser}))
        .then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({visible:false},()=>this.requestActivatedTable())
            }else{
                message.warning(result.msg)
            }
        })
    }
    render(){
        const { showHide,customerType,tabKey,customerOperation,freez,userId,userName,loginIp,recommendName,memo,tableList,pageTotal,visible,title,pageIndex,pageSize,width,limitBtn,modalCustomer } = this.state

        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 用户管理 > 用户信息
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div> 
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">登录IP：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="loginIp" value={loginIp} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div> 
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">推荐人：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="recommendName" value={recommendName} onChange={this.handleInputChange} />
                                        </div> 
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={customerType} style={{ width: SELECTWIDTH }} onChange={this.handleCustomerType}>
                                                <Option value="0">请选择</Option>
                                                <Option value="04">VDS刷量账户</Option>
                                                <Option value="05">测试账户</Option>
                                                <Option value="07">刷量账户</Option>
                                                <Option value="01">用户账户</Option>
                                                {/* <Option value="06">其他用户</Option>                                                 */}
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">受限类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={customerOperation} style={{ width: SELECTWIDTH }} onChange={this.handleCustomerOperation}>
                                                <Option value="0">请选择</Option>
                                                <Option value="1">正常</Option>
                                                <Option value="2">受限</Option>
                                            </Select>
                                        </div> 
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">冻结标志：</label>
                                        <div className="col-sm-8">
                                            <Select value={freez} style={{ width: SELECTWIDTH }} onChange={this.handleChangeFreez}>
                                                <Option value="0">请选择</Option>
                                                <Option value="1">正常</Option>
                                                <Option value="2">冻结</Option>
                                            </Select>
                                        </div> 
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">备注：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="memo" value={memo} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div> 
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {limitBtn.indexOf('clearIP')>-1?<Button type="primary" onClick={this.onClearIP}>清理IP</Button>:''}
                                    </div>
                                </div>
                            </div>
                        </div>  } 
                        <div className="x_panel">
                            <div className="x_content">
                                <Tabs onChange={this.callbackTabsKey}>
                                    <TabPane tab="已激活" key="real"></TabPane>
                                    <TabPane tab="未激活" key="noreg"></TabPane>
                                    <TabPane tab="已删除" key="del"></TabPane>
                                </Tabs>
                                <div id={this.state.tableScroll.tableId}  style={{height:'calc(100vh - 205px)'}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title min_96px">用户名</th>
                                                <th className="column-title">手机</th>
                                                <th className="column-title min_96px">邮箱</th>
                                                <th className="column-title">用户类型</th>
                                                <th className="column-title">受限类型</th>
                                                <th className="column-title">推荐人</th>
                                                <th className="column-title">冻结标志</th>
                                                <th className="column-title">敏感用户</th>
                                                <th className="column-title wid300 min_153px">备注</th>
                                                <th className="column-title">操作</th>                                              
                                            </tr>
                                        </thead>
                                        <tbody>
                                        {
                                            tableList.length>0?tableList.map((item,index) => {
                                                let con = '';
                                                if(item.customerOperation && (item.customerOperation.indexOf('1')>-1||item.customerOperation.indexOf('4')>-1||item.customerOperation.indexOf('5')>-1)&&item.customerOperation.indexOf('2')>-1) {
                                                    con = '提现|交易异常';
                                                }
                                                else if(item.customerOperation && item.customerOperation.indexOf('2')>-1) {
                                                    con = '提现异常';
                                                }else if(item.customerOperation && (item.customerOperation.indexOf('1')>-1||item.customerOperation.indexOf('4')>-1||item.customerOperation.indexOf('5')>-1)) {
                                                    con = '交易异常';
                                                } else{
                                                    con = '正常';
                                                }
                                                    let systemUuserTagsList =item.systemUuserTags && item.systemUuserTags != ''&& item.systemUuserTags.split(',') || [];
                                                    let userTagsList =item.userTags && item.userTags != ''&& item.userTags.split(',') || [];
                                                    return (
                                                        [<tr key={item.id}>
                                                            <td colSpan='11'>
                                                                <span className="pad-sapce">用户编号：{item.id}</span>
                                                                <span className="pad-sapce">注册时间：{moment(item.registerTime).format(TIMEFORMAT)}</span>
                                                                <span className="pad-sapce">最后登录时间：{moment(item.lastLoginTime).format(TIMEFORMAT)}</span>
                                                                <span className="pad-sapce">登录 IP：<a href={`http://www.ip138.com/ips138.asp?ip=${item.loginIp}&action=2`} target="_blank">{item.loginIp}</a></span>
                                                                {
                                                                    systemUuserTagsList.map((tag,i)=>{
                                                                        return (<span key={i} className='userinfo-tag' style={{ padding: '0px 5px' }}>{tag}</span>)
                                                                    })
                                                                }
                                                                {
                                                                    userTagsList.map((tag, i)=>{
                                                                        return ( <span key={i} className='userinfo-tag'>{tag}<a href="javascript:void(0);" onClick={() => this.onDeleteTag(item.id, tag)}>X</a></span>)
                                                                    })
                                                                }
                                                                <a href="javascript:void(0);" className="userinfo-add" onClick={() => this.onAddTag(userTagsList, item.id)}>+标签</a>
                                                            </td>
                                                        </tr>,
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                        <td>{limitBtn.indexOf('infoShow')>-1?<a href="javascript:void(0)" onClick={() => this.userSafeInfo(item)}>{item.userName}</a>:item.userName}</td>
                                                            <td>{item.mobile}</td>
                                                            <td>{item.email}</td>
                                                            <td>{limitBtn.indexOf('modifyCustomerType')>-1?<a href="javascript:void(0)" onClick={()=>this.modalCustomerType(item.customerType,item.id,'check')}>{item.customerTypeName}</a>:item.customerTypeName}</td>
                                                            <td>{limitBtn.indexOf('modifyCustomerOperation')>-1?<a href="javascript:void(0)" onClick={()=>this.modalCustomerOpe(item,'check')}>{con}</a>:con}</td>
                                                            <td>{item.recommendName}</td>
                                                            <td>{limitBtn.indexOf('freez')>-1?<a href="javascript:void(0)" onClick={()=>this.freezeUserModal(item.id,item.freez,'check')}>{item.freezeName}</a>:item.freezeName}</td>
                                                            <td><a href="javascript:void(0)" onClick={() => this.sensitiveUser(item)}>{item.warningUser == 1 ? '是' : '否'}</a></td>
                                                            <td>{item.memo}</td>
                                                            <td>
                                                                {tabKey!='del'?limitBtn.indexOf('updUserById')>-1?<a className="mar10" href="javascript:void(0);" onClick={()=>this.deleteTableList(item.id,'del','check')}>删除</a>:'':limitBtn.indexOf('restoreUserById')>-1?<a className="mar10" href="javascript:void(0);" onClick={()=>this.revertBtn(item.id,'rev','check')}>还原</a>:''}
                                                                {limitBtn.indexOf('saveMemoById')>-1?<a className="mar10" href="javascript:void(0);" onClick={() => this.showModal(item.id,item.userName,item.memo)}>备注</a>:''}
                                                                {limitBtn.indexOf('safeMange')>-1?<a className="mar10" href="javascript:void(0);" onClick={() => this.onSafeManage(item)}>安全管理</a>:''}
                                                            </td>                                                                              
                                                        </tr>]
                                                    )
                                            })
                                            :<tr className="no-record"><td colSpan="11">暂无数据</td></tr>
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
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 50 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {this.state.modalHtml}            
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='UI'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
                
            </div>
        )
    }
}




















