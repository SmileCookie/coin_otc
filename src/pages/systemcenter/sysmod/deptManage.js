import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,PAGRSIZE_OPTIONS20 } from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import { Button,message,Modal,Pagination } from 'antd'
import ModalDepModify from './modal/modalDepModify'
import {pageLimit,tableScroll} from '../../../utils'
import ModalDepTree from './modal/modalDepTree'

export default class DeptManage extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            tableList:[],
            deptName:'',
            redeptName:'',
            page:PAGEINDEX,
            limit:PAGESIZE,
            pageTotal:0,
            modalHtml:'',
            visible:false,
            width:'',
            title:'',
            deptNo:'',
            deptLeaderName:'',
            parentDeptName:'',
            deptId:'',
            deptLeaderId:'',
            createUserId:'',
            remark:'',
            parentId:'',
            googVisibal:false,
            item:'',
            type:'',
            check:'',
            parendeptId:'',
            limitBtn:[],
            height:0,
            tableScroll:{
                tableId:'DPMAGE',
                x_panelId:'DPMAGEX',
                defaultHeight:500,
            }
        }

        this.clickHide = this.clickHide.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.onAddModal = this.onAddModal.bind(this)
        this.onAddModalBtn = this.onAddModalBtn.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onDelete = this.onDelete.bind(this)
        this.onModalDepTree = this.onModalDepTree.bind(this)
        this.setParentId = this.setParentId.bind(this)
        this.setDeptLeaderId = this.setDeptLeaderId.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.setdeptId = this.setdeptId.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('sysDept',this.props.permissList)
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
    //列表查询
    requestTable(){
        const { page,limit,redeptName } = this.state
        axios.post(DOMAIN_VIP+"/sys/sysDept/queryList",qs.stringify({
            page:page,limit:limit,deptName:redeptName
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

    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },()=>this.requestTable())
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
            page
        })
        this.requestTable(page,pageSize,this.props.id)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size,this.props.id)
        this.setState({
            page:current,
            limit:size
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
    //重置状态
    onResetState(){
        this.setState({
            redeptName:''
        })
    }
    //弹框隐藏
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //设置 parentId
    setParentId(id,name,checkdeptNo,checkdeptId,checkdeptLeaderName,checkparentDeptName){
        this.setState({
            parentId:id,
            deptName:name,
            deptNo:checkdeptNo,
            deptId:checkdeptNo,
            deptLeaderName:checkdeptLeaderName,
            parentDeptName:checkparentDeptName
        })
        console.log(id,name,checkdeptNo,checkdeptId,checkdeptLeaderName,checkparentDeptName)
    }
    setdeptId(id,name,checkdeptNo,checkdeptId){
        this.setState({
            parentDeptName:name,
            parendeptId:checkdeptId
        })
        console.log(id,name,checkdeptNo,checkdeptId)
    }
    //设置部门负责人 deptLeaderId
    setDeptLeaderId(id,name){
        this.setState({
            deptLeaderId:id,
            deptLeaderName:name,
        })
    }
    //新增、修改弹窗
    onAddModal(item,type){
       
        const { deptNo,deptName,deptId,parendeptId,deptLeaderName,parentDeptName,parentId,createUserId,deptLeaderId } = item
        let mtitle = type === 'add' ? '新增部门' : '修改部门'
        this.footer=[
            <Button key='back' onClick={this.handleCancel}>取消</Button>,
            <Button key='submit' type='more' onClick={()=>this.modalGoogleCode(item,type)}>确定</Button>
        ],
        this.setState({
            width:'600px',
            visible:true,
            title:mtitle,
            modalHtml:<ModalDepModify setdeptId={this.setdeptId} setDeptLeaderId={this.setDeptLeaderId} setParentId={this.setParentId} item={item} handleInputChange={this.handleInputChange}/>,
            deptNo:deptNo||'',
            deptId:deptId||'',
            deptName:deptName||'',
            deptLeaderName:deptLeaderName||'',
            deptLeaderId:deptLeaderId||'',
            parentDeptName:parentDeptName||'',
            parentId:parentId||'',
            createUserId:createUserId||''            
        })
    }
    //新增 、修改按钮、部门Tree确定按钮
    onAddModalBtn(item,type){
        const { deptId,deptNo,deptName,deptLeaderName, parendeptId,deptLeaderId,parentDeptName,createUserId,remark,parentId} = this.state
        console.log(deptNo)
        let dep = type == 'tree'?deptId:item.deptId
        type === 'add'?
        axios.post(DOMAIN_VIP+'/sys/sysDept/save',qs.stringify({
            deptId,deptNo,deptName,deptLeaderName, deptLeaderId,parentDeptName,createUserId,remark,parentId:parendeptId
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                },()=>this.requestTable())
            }else{
                message.warning(result.msg)
            }
        })
        :axios.post(DOMAIN_VIP+'/sys/sysDept/update',qs.stringify({
            deptId:dep,deptNo,deptName,deptLeaderName, deptLeaderId,parentDeptName,createUserId,remark,parentId:parendeptId
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                },()=>this.requestTable())
            }else{
                message.warning(result.msg)
            }
        })
    }
    //删除按钮
    onDelete(id){
        let self = this
        Modal.confirm({
            title:'确定要删除吗？',
            okText:'确定',
            onCancel:'取消',
            okType:'danger',
            onOk(){
                self.modalGoogleCode(id,'del')
            },
            onCancel(){
                console.log('Cancel')
            }
        })
    }
    onDeleteBtn(id){
        axios.post(DOMAIN_VIP+'/sys/sysDept/delete', qs.stringify({
            deptId:id
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.requestTable()
                resolve(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //部门Tree弹窗
    onModalDepTree(){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>返回</Button>,
            // <Button key="submit" type="more"  onClick={() => this.modalGoogleCode({},'tree')}>
            //     确定
            // </Button>
        ]
        this.setState({
            visible:true,
            title:'部门Tree',
            width:'1000px',
            modalHtml:<ModalDepTree
                        setdeptId = {this.setdeptId}
                        setDeptLeaderId={this.setDeptLeaderId}
                        setParentId={this.setParentId}
                        deptManage 
                        treeData={this.state.tableList}
                        handleInputChange={this.handleInputChange}
                    />,
            deptName:'',
            deptNo:'',
            deptLeaderName:'',
            parentDeptName:''
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
        const { item,type} = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(type == 'add'||type == 'edit'||type == 'tree'){
                    this.setState({
                        googVisibal:false
                    },()=>this.onAddModalBtn(item,type))
                }else{
                    this.setState({
                        googVisibal:false
                    },()=>this.onDeleteBtn(item))
                }
               
            }else{
                message.warning(result.msg)
            }
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
        const { showHide,tableList,page,limit,pageTotal,modalHtml,visible,title,width,redeptName,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 系统管理 > 部门管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-2 control-label">部门名称：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="redeptName" value={redeptName} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {limitBtn.indexOf('save')>-1?<Button type="primary" onClick={()=>this.onAddModal({},'add')}>新增</Button>:''}
                                        {limitBtn.indexOf('update')>-1? <Button type="primary" onClick={this.onModalDepTree}>部门Tree</Button>:''}
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
                                                <th className="column-title">部门编号</th>
                                                <th className="column-title">部门名称</th>
                                                <th className="column-title">部门负责人</th>
                                                <th className="column-title">上级部门名称</th> 
                                                <th className="column-title">操作</th>                                                                                           
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(page-1)*limit+index+1}</td>
                                                            <td>{item.deptNo}</td>
                                                            <td>{item.deptName}</td>
                                                            <td>{item.deptLeaderName}</td>
                                                            <td>{item.parentDeptName}</td>
                                                            <td>{
                                                            limitBtn.indexOf('delete')>-1?<a href="javascript:void(0)" className="mar10" onClick={()=>this.onDelete(item.deptId)}>删除</a>:''}
                                                           { limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" className="mar10" onClick={()=>this.onAddModal(item,'edit')}>修改</a>:'' }                                                            
                                                            
                                                            </td>                                                            
                                                        </tr>
                                                    )
                                                })
                                                : <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                    {pageTotal>0&&
                                        <Pagination 
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
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml}            
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='DMA'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>

        )
    }

}









































