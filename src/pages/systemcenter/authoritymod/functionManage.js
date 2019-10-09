
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import ModalModify from './modal/modalModify'
import ModalFtree from './modal/modalFtree'
import { DOMAIN_VIP,PAGESIZE,PAGEINDEX, DOMAIN_BASE,SELECTWIDTH } from '../../../conf'
import { Select,Modal,Button,Table,Pagination,message } from 'antd'
import { pageLimit,tableScroll } from "../../../utils/index"
const Option = Select.Option;
import GoogleCode from '../../common/modal/googleCode'

export default class FunctionManage extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            funType:'',
            tableList:[],
            visible:false,
            title:'',
            width:'',
            modifyHtml:'',
            loading:false,
            modalHtml:'',
            menuFind:1,
            menuName:'',
            menuOrder:'',
            parentMenuName:'',
            menuUrl:'',
            menuSign:'',
            parentMenuName:'',
            funParentName:'',
            limitBtn: [],
            check:'',
            googVisibal:false,
            item:'',
            type:'',
            height:0,
            tableScroll:{
                tableId:'FUNSTONMAG',
                x_panelId:'FUNSTONMAGX',
                defaultHeight:600,
            },
            funMenuName:''
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestTble = this.requestTble.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.showModal = this.showModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modifyManage = this.modifyManage.bind(this)
        this.addManage = this.addManage.bind(this)
        this.changeRadio = this.changeRadio.bind(this)
        this.onModifyFunManage = this.onModifyFunManage.bind(this)
        this.deleteManage = this.deleteManage.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.moadifyFunTree = this.moadifyFunTree.bind(this)
        this.addManageBtn = this.addManageBtn.bind(this)
        this.setTreeMenuid = this.setTreeMenuid.bind(this)
        this.setParentId = this.setParentId.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.deleteManageItem = this.deleteManageItem.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTble()
        // console.log(pageLimit('menu', this.props.permissList))
        this.setState({
            limitBtn: pageLimit('menu', this.props.permissList)
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
        this.setState({ visible: false });
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
  
    handleChangeType(value){
        this.setState({
            funType:value
        })
    }
    //table 列表
    requestTble(){
        const { funType,funMenuName,funParentName } = this.state
        axios.get(DOMAIN_VIP+'/sys/menu/list',{
            params:{
                name:funMenuName,
                parentName:funParentName,
                type:funType
            }
        }).then(res=>{
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.menuList
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTble())
    }

    //选择带回上级功能名称 ID
    setParentId(id){
        this.setState({
            parentId:id
        })
    }

    //新增模块
    addManage(){
        const { menuFind,menuName,menuOrder,menuSign,menuUrl,parentMenuName } = this.state
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(0,1)}>
                确认
            </Button> 
        ]

        this.setState({
            visible:true,
            modalHtml:<ModalModify 
                        menuFind = ''
                        menuName = ''
                        menuOrder = ''
                        menuSign = ''
                        menuUrl = ''
                        parentMenuName = ''
                        icon = ''
                        parentId = ''
                        handleInputChange={this.handleInputChange} 
                        changeRadio={this.changeRadio} 
                        setParentId={this.setParentId}
                        />,
            title:'新增功能',
            width:'650px',
            menuFind:'',
            menuName:'',
            menuOrder:'',
            menuSign:'',
            menuUrl:'',
            parentMenuName:'',
            icon:''
        })
    }

    //新增模块 按钮
    addManageBtn(){
        const { menuFind,menuName,menuOrder,menuSign,menuUrl,parentId,parentMenuName,menuId,icon } = this.state 
        if(menuFind === ''){
            message.warning("功能类型不能为空!")
            return false
        }
        axios.post(DOMAIN_VIP+"/sys/menu/save",qs.stringify({
            menuId:menuId,
            name: menuName,
            parentId: parentId,
            url: menuUrl.trim(),
            perms: menuSign,
            type: menuFind,
            icon: icon,
            orderNum: menuOrder
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false
                })
                this.requestTble()
            }else{
                message.warning(result.msg)//菜单不能为空
            }
        })
    }

    //单选按钮值 改变时
    changeRadio(val){
        this.setState({
            menuFind:val,
        },()=>console.log(this.state.menuFind));
    }
    //删除Item
    deleteManageItem(menuId){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/sys/menu/delete', qs.stringify({
                menuId
            })).then(r => {
                const result = r.data;
                if(result.code == 0){
                    message.success(result.msg);
                    this.requestTble()
                }else{
                    message.warning(result.msg)
                }
            }).then(err => {
                reject(err)
            })
        }).catch(() => console.log('Oops errors!'))
    }
    //删除弹窗
    deleteManage(menuId){
        let self = this
        Modal.confirm({
            title: '您确定要删除此功能?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(menuId, 3)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //修改功能弹窗 
    modifyManage(item){
        console.log(1)
        this.footer = [ <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item.menuId,2)}>
                            保存修改
                        </Button> ];
        this.setState({
            visible:true,
            modalHtml:<ModalModify 
                        menuFind = {item.type}
                        menuName = {item.name}
                        menuOrder = {item.orderNum}
                        menuSign = {item.perms}
                        menuUrl = {item.url}
                        parentMenuName = {item.parentName}
                        icon={item.icon}
                        handleInputChange={this.handleInputChange} 
                        changeRadio={this.changeRadio}  
                        setParentId={this.setParentId}
                        />,
            title:'修改功能',
            width:'650px',
            menuFind:item.type,
            menuName:item.name,
            menuOrder:item.orderNum,
            menuSign:item.perms,
            menuUrl:item.url,
            parentMenuName:item.parentName,
            icon:item.icon,
            parentId:item.parentId
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
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(type == 1){
                    this.setState({
                        googVisibal:false
                    },() => this.addManageBtn())
                }else if(type == 2){
                    this.setState({
                        googVisibal:false
                    },() => this.onModifyFunManage(item))
                }else{
                    this.setState({
                        googVisibal:false
                    },() => this.deleteManageItem(item))
                }
            }else{
                message.warning(result.msg)
            }
        })
    }
    //修改功能按钮 item
    onModifyFunManage(id){
        const { menuFind,menuName,menuOrder,menuSign,menuUrl,parentMenuName,parentId,menuId,icon } = this.state 
        axios.post(DOMAIN_VIP + '/sys/menu/update',qs.stringify({
            menuId: id||menuId,
            name: menuName,
            parentId: parentId,
            url: menuUrl.trim(),
            perms: menuSign,
            type: menuFind,
            icon: icon,
            orderNum: menuOrder
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false,
                    loading:false
                })
                this.requestTble()
                message.success(result.msg)
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
    //设置 menuID
    setTreeMenuid(id){
        this.setState({
            menuId:id
        })
    }
    //功能 Tree 
    moadifyFunTree(){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>返回</Button>,
            // <Button key="submit" type="more"  onClick={() => this.onModifyFunManage()}>
            //     确定
            // </Button>
        ]
        this.setState({
            visible:true,
            modalHtml:<ModalFtree 
                        funManage 
                        treeData={this.state.tableList} 
                        handleInputChange={this.handleInputChange} 
                        setParentId={this.setParentId}
                        />,
            width:"1000px",
            title:"功能 Tree"
        })
    }
    //充值状态
    onResetState(){
        this.setState({
            funParentName:'',
            funMenuName:'',
            funType:''
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
        const { showHide,funType,tableList,modifyHtml,visible,title,width,loading,modalHtml,funMenuName,funParentName,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 权限管理 > 功能管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">功能名称：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="funMenuName" value={funMenuName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">上级功能名称：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="funParentName" value={funParentName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">功能类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={funType} style={{ width: SELECTWIDTH }} onChange={this.handleChangeType}>
                                                <Option value="">请选择</Option>
                                                <Option value="0">目录项</Option>
                                                <Option value="1">菜单项</Option>
                                                <Option value="2">按钮项</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {limitBtn.indexOf('save')>-1?<Button type="primary" onClick={this.addManage}>新增</Button>:''}
                                        <Button type="primary" onClick={this.moadifyFunTree}>功能tree</Button>  
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
                                                <th className="column-title min_69px">序号</th>
                                                <th className="column-title">功能编号</th>
                                                <th className="column-title">功能名称</th>
                                                <th className="column-title">显示顺序</th>
                                                <th className="column-title">功能类型</th>
                                                <th className="column-title hide">上级功能编号</th>
                                                <th className="column-title">上级功能名称</th>
                                                <th className="column-title min_153px">功能URL(授权)</th>
                                                <th className="column-title min_153px">功能描述</th>
                                                <th className="column-title">操作</th>                                                
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        <tr key={index}>
                                                            <td>{index+1}</td>
                                                            <td>{item.menuId}</td>
                                                            <td>{item.name}</td>
                                                            <td>{item.orderNum}</td>
                                                            <td>{item.type==0?'目录':item.type==1?'菜单':'按钮'}</td>
                                                            <td className="hide">{item.parentId}</td>
                                                            <td>{item.parentName}</td>
                                                            <td>{item.url}</td>
                                                            <td>{item.perms}</td>
                                                            <td>
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" className="mar10" onClick={() => this.modifyManage(item)}>修改</a>:''}
                                                                {limitBtn.indexOf('delete')>-1?<a href="javascript:void(0)" className="mar10" onClick={() => this.deleteManage(item.menuId)}>删除</a>:''}                                                               
                                                            </td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
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
                 mid='FM'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}




































