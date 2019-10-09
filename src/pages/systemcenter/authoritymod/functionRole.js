

import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalTree from './modal/modalTree'
import ModalFrom from './modal/modalFrom'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,TIMEFORMAT,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Select,Modal,Button,Table,Pagination,message } from 'antd'
import { pageLimit,tableScroll } from '../../../utils/index'
const Option = Select.Option;
import GoogleCode from '../../common/modal/googleCode'

export default class FunctionRole extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            roleName:'',
            page:PAGEINDEX,
            limit:PAGESIZE,
            pageTotal:0,
            tableList:[],
            visible:false,
            loading: false,
            modalHtml:'',
            title:'',
            userID:'',
            userName:'',
            remarks:'',
            width:'',
            googleCode:'',
            checkGoogle:'',
            limitBtn: [],
            limitBtns: [],
            check:'',
            googVisibal:false,
            item:'',
            type:'',
            height:0,
            tableScroll:{
                tableId:'FUNTNREd',
                x_panelId:'FUNTNREXd',
                defaultHeight:500,
            }
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.showModal = this.showModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.setPurviewBtn = this.setPurviewBtn.bind(this)
        this.addFunRole = this.addFunRole.bind(this)
        this.deleteRole = this.deleteRole.bind(this)
        this.modifyRole = this.modifyRole.bind(this)
        this.addFunRoleBtn = this.addFunRoleBtn.bind(this)
        this.modifyRoleBtn = this.modifyRoleBtn.bind(this)
        this.chooseTreeKey = this.chooseTreeKey.bind(this)
        this.setPurviewSure = this.setPurviewSure.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.deleteRoleItem = this.deleteRoleItem.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('role', this.props.permissList),
            limitBtns: pageLimit('menu', this.props.permissList)
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
    
    //弹窗 ok 
    handleOk(){
        this.setState({ loading: true });
        setTimeout(() => {
          this.setState({ 
              loading: false, 
              visible: false 
          });
        }, 3000);
    }
    //弹窗显示
    showModal(){
        this.setState({
          visible: true,
        });
    }
    //弹窗隐藏
    handleCancel(){
        this.setState({ 
            visible: false,
            userID:'',
            userName:'',
            remarks:'',
            modalHtml:''
        });
    }
    //设置权限
    setPurviewBtn(item,type){
        let heiTitle = type == 'set'?"设置权限":"查看权限"
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            type?<Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(0,3,'check')}>
                保存修改
            </Button>:'',
        ]
        this.setState({
            visible: true,
            modalHtml:<ModalTree checkable={true} type={type} chooseTreeKey={this.chooseTreeKey} item={item} />,
            title:heiTitle,
            width:'1200px',
            userID:item.roleId,
            userName:item.roleName,
            remarks:item.remark
          });
    }
    //选择带回 tree key
    chooseTreeKey(checkedKeys){
        this.setState({
            menuIdList:checkedKeys
        })
    }
    //设置权限 按钮
    setPurviewSure(){
        const { userID,userName,remarks,menuIdList } = this.state
        axios.post(DOMAIN_VIP+"/sys/role/update",{
            roleId:userID,
            roleName:userName,
            remark:remarks,
            menuIdList:menuIdList
        }).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false
                },() => this.requestTable())
            }else{
                message.warning(result.msg)
            }
        })
    }

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            page:page
        },()=>this.requestTable(page,pageSize))
        
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.setState({
            page:current,
            limit:size
        },()=> this.requestTable(current,size))
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
 
    //点击查询
    inquireBtn(){
        this.setState({
            page:PAGEINDEX
        },() => this.requestTable())
        
    }
    //请求角色列表
    requestTable(currIndex,currSize){
        const { roleName,page,limit } = this.state
        axios.get(DOMAIN_VIP+'/sys/role/list',{
            params:{
              page,
              limit,
              roleName
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
    
    //重置按钮
    onResetState(){
         this.setState({
            roleName:''
         })
    }
    //新增角色
    addFunRole(){
        const { userID,userName,remarks } = this.state
        this.footer=[
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(0,1,'check')}>
                确认
            </Button>,
        ];
        this.setState({
            visible:true,
            modalHtml:<ModalFrom 
                        userName={userName} 
                        remarks={remarks}
                        handleInputChange={this.handleInputChange}  />,
            title:'新增角色',
            width:'600px'
        })
    }
    //新增角色 确认按钮
    addFunRoleBtn(){
        const { userID,userName,remarks } = this.state
        if(!userName){
            message.warning('角色名称不能为空!')
            return false
        }
        axios.post(DOMAIN_VIP + '/sys/role/save',{
            roleName:userName,
            remark:remarks,
            menuIdList: []
        }).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false,
                    userID:'',
                    userName:'',
                    remarks:''
                })
                this.requestTable();
                message.success(result.msg);
            }else{
                message.warning(result.msg)
            }
        })
    }
    //删除角色Item
    deleteRoleItem(roleId){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/sys/role/delete',
                [roleId]
            ).then(res => {
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
    //删除角色
    deleteRole(roleId,type){
        let self = this;
        Modal.confirm({
            title: '您确定要删除此角色?',
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
    //修改角色信息
    modifyRole(item){
        this.footer=[
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item,3,'check')}>
                保存修改
            </Button>,
        ];
        this.setState({
            visible:true,
            modalHtml:<ModalFrom
                        userName={item.roleName}
                        remarks={item.remark}
                        handleInputChange={this.handleInputChange} />,
            userID:item.roleId,
            userName:item.roleName,
            remarks:item.remark,
            title:'修改角色',
            width:'600px'
        })
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
        const { item,type,check} = this.state
        const {googleCode,checkGoogle} = value
      let url =  check ?"/common/checkTwoGoogleCode":"/common/checkGoogleCode"
        axios.post(DOMAIN_VIP+url,qs.stringify({
            googleCode,checkGoogle
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(type == 1){
                    this.setState({
                        googVisibal:false
                    },() => this.addFunRoleBtn())
                }else if(type == 2){
                    this.setState({
                        googVisibal:false
                    },() => this.deleteRoleItem(item))
                }else if(type == 3){
                        this.setState({
                            googVisibal:false
                        },() => this.setPurviewSure())
                }else{
                    this.setState({
                        googVisibal:false
                    },() => this.modifyRoleBtn(item))
                }
                
            }else{
                message.warning(result.msg)
                this.setState({
                    userName:'',
                    remarks:''
                })
            }
        })
    }
    //修改角色信息 确认按钮
    modifyRoleBtn(item){
        const { userID,userName,remarks } = this.state
        axios.post(DOMAIN_VIP + '/sys/role/update',{
            roleId:item.roleId,
            roleName:userName,
            remark:remarks,
            createUserId: item.createUserId,
            createTime: item.createTime,
            menuIdList: item.menuIdList
        }).then(res => {
             const result = res.data;
             if(result.code == 0){
                this.setState({
                    visible:false
                })
                this.requestTable();
                message.success(result.msg);
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
        const { showHide,roleName,page,limit,tableList,pageTotal,modalHtml,visible,loading,title,width,limitBtn,limitBtns } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>权限管理>角色权限
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">角色名称：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="roleName" value={roleName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {limitBtn.indexOf('save')>-1? <Button type="primary" onClick={this.addFunRole}>新增</Button>:''}
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
                                                <th className="column-title">角色编号</th>
                                                <th className="column-title">角色名称</th>
                                                <th className="column-title">创建时间</th>
                                                <th className="column-title">角色描述</th>
                                                <th className="column-title">操作</th>
                                                <th className="column-title">权限</th>                                              
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0? 
                                                tableList.map((item,index)=>{
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(page-1)*limit+index+1}</td>
                                                            <td>{item.roleId}</td>
                                                            <td>{item.roleName}</td>
                                                            <td>{moment(item.createTime).format(TIMEFORMAT)}</td>
                                                            <td>{item.remark}</td>
                                                            <td>
                                                                {limitBtn.indexOf('delete')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.deleteRole(item.roleId,2)}>删除</a>:''}
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" onClick={() => this.modifyRole(item)}>修改</a>:''}                                                                
                                                            </td>
                                                            <td>
                                                                {limitBtns.indexOf('setProperty')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.setPurviewBtn(item,'set')}>设置权限</a>:''}
                                                                {limitBtns.indexOf('selectProperty')>-1?<a href="javascript:void(0)" onClick={() => this.setPurviewBtn(item)}>查看权限</a>:''}
                                                            </td>
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
                 mid='FR'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}



























































