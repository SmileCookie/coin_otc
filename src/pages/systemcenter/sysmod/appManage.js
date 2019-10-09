
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalModify from './modal/modalModify'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT, SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import { Select,Modal,Pagination,Button,message } from 'antd'
import { pageLimit } from '../../../utils'
const Option = Select.Option

export default class AppManage extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            tableList:[],
            pageTotal:0,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            name:'',
            type:'',
            googleCode:'',
            Mtype:'',
            limitBtn: [],
            check:'',
            googVisibal:false,
            googleSpace:'',
            type:'',
            loading:false,
            redealType:'',
            dealType:''
        }   

        this.requestTable = this.requestTable.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.modalAppGoogle = this.modalAppGoogle.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modalAppGoogleBtn = this.modalAppGoogleBtn.bind(this)
        this.modalAppkey = this.modalAppkey.bind(this)
        this.modalModify = this.modalModify.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
        this.modalModifyBtn = this.modalModifyBtn.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.handleChangeUpdate = this.handleChangeUpdate.bind(this)
        this.modalAppGoogleBtn = this.modalAppGoogleBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.business_handleChange = this.business_handleChange.bind(this)
        this.handleChange_business = this.handleChange_business.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('app', this.props.permissList)
        })
    }

    requestTable(){
        const { type,name,pageIndex,pageSize,dealType } = this.state
        axios.post(DOMAIN_VIP+"/app/AppInfo",qs.stringify({
            type,name,pageIndex,pageSize,dealType
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.list.list,
                    pageTotal:result.list.totalCount
                })
            }
        })
    }
    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },()=>this.requestTable())
    }
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTable())
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.setState({
            page:current,
            limit:size
        }, () => this.requestTable())
    }
    //select change
    handleChange(value) {
        this.setState({
            type:value,
        })
    }
    //select change
    handleChange_business(value) {
        this.setState({
            dealType:value,
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
    //重置按钮
    onResetState(){
        this.setState({
            name:'',
            type:'',
            redealType:'',
            dealType:''
        })
    }
    //关闭修改备注弹窗
    handleCancel(){
        this.setState({
            visible:false,
            loading:false
        })
    }
    //Appkey 按钮
    modalAppGoogle(id){
        this.setState({
            googVisibal:true,
            googleSpace:id,
        })
    }
    //Appkey google 按钮
    modalAppGoogleBtn(value){
        const { googleSpace } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/app/checkCode",qs.stringify({
            code:googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0&&result.status == 1){
                this.setState({
                    googVisibal:false
                })
                this.modalAppkeyBtn(googleSpace)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //真正的 Appkey 按钮
    modalAppkeyBtn(id){
        let self = this
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+"/app/editItemKey",qs.stringify({
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
    }
    //真正的 Appkey 弹窗 
    modalAppkey(id){
        let self = this;
        Modal.confirm({
            title: "此操作会更改APP所有版本授权，请谨慎操作，如有疑问，请咨询移动开发组成员，确定要changeAppKey吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                self.modalAppGoogle(id)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //弹窗里面的 select value
    handleChangeType(val){
        //console.log(val)
        this.setState({
            Mtype:val
        })
    }
    //弹窗里面的 enforceUpdate
    handleChangeUpdate(val){
        this.setState({
            enforceUpdate:val
        })
    }
    //弹窗里面的   交易类型 
    business_handleChange(value) {
        this.setState({
            redealType:value
        })
    }

    //修改按钮
    modalModify(item){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalModifyBtn(item.id)}>
                确定
            </Button>
        ]
        let newItem = item?item:{};
        //console.log(newItem.name)
        this.setState({
            visible:true,
            width:"1000px",
            title:"添加/编辑",
            modalHtml:<ModalModify item={item} business_handleChange={this.business_handleChange} handleChangeUpdate={this.handleChangeUpdate} handleChangeType={this.handleChangeType}  handleInputChange={this.handleInputChange}/>,
            Mtype:newItem.type||'android',
            Mname:newItem.name||'',
            enforceUpdate:newItem.enforceUpdate||false,
            cnName:newItem.cnName||'',
            enName:newItem.enName||'',
            hkName:newItem.hkName||'',
            jpName:newItem.jpName||'',
            krName:newItem.krName||'',
            size:newItem.size||'',
            url:newItem.url||'',
            released:newItem.released||false,
            cnRemark:newItem.cnRemark||'',
            enRemark:newItem.enRemark||'',
            hkRemark:newItem.hkRemark||'',
            jpRemark:newItem.jpRemark||'',
            krRemark:newItem.krRemark||'',
            num:newItem.num||'',
            redealType:newItem.dealType||'',
        })
    }
    //修改确定按钮
    modalModifyBtn(id){
        this.setState({
            loading:true
        })
        const { Mtype,Mname,enforceUpdate,cnName,enName,hkName,num,size,url,released,cnRemark,enRemark,hkRemark,redealType,jpName,jpRemark,krName,krRemark, } = this.state
        axios.post(DOMAIN_VIP+"/app/editItemAppInfo",qs.stringify({
            type:Mtype,
            name:Mname,
            id,enforceUpdate,cnName,enName,hkName,num,size,url,released,cnRemark,enRemark,hkRemark,dealType:redealType,
            jpName,jpRemark,krName,krRemark,
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    type:'',
                    name:'',
                    loading:false
                },() => this.requestTable())
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        })
    }
    //删除
    deleteItem(id){
        let self = this;
        Modal.confirm({
            title: "确定删除本项吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+"/app/deleteItemAppInfo",qs.stringify({
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
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.modalAppGoogleBtn(values)
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
        const { tableList,pageTotal,name,pageIndex,type,width,title,visible,modalHtml,limitBtn,dealType } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 系统管理 > 客户端管理
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">

                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-4 control-label">客户端类型：</label>
                                    <div className="col-sm-8">
                                        <Select value={type} style={{width:SELECTWIDTH}}  onChange={this.handleChange}>
                                            <Option value="">请选择</Option>
                                            <Option value="1">Android</Option>
                                            <Option value="2">IOS</Option>                                            
                                        </Select>
                                    </div>
                                </div>
                            </div>
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-4 control-label">交易类型：</label>
                                    <div className="col-sm-8">
                                        <Select name="dealType" value={dealType} style={{width:SELECTWIDTH}}  onChange={this.handleChange_business}>
                                            <Option value="">请选择</Option>
                                            <Option value="0">币币</Option>
                                            <Option value="1">OTC</Option>                                            
                                        </Select>
                                    </div>
                                </div>
                            </div>
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-4 control-label">版本名称：</label>
                                    <div className="col-sm-8">
                                        <input type="text" className="form-control"  name="name" value={name}  onChange={this.handleInputChange} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-4 col-sm-4 col-xs-4 right martop4">
                                <div className="right">
                                    <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                    <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    {limitBtn.indexOf('editItemAppInfo')>-1?<Button type="primary" onClick={() => this.modalModify({})}>添加</Button>:''}                                    
                                </div>
                            </div>
                        </div>

                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">客户端类型</th>
                                                <th className="column-title">是否强制更新</th>
                                                <th className="column-title">版本名称</th>
                                                <th className="column-title">版本号</th>
                                                <th className="column-title min_214px">下载链接</th>
                                                <th className="column-title">更新时间</th>                   
                                                <th className="column-title">操作</th>                                                               
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length?
                                                tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.type}</td>
                                                            <td>{item.enforceUpdate?"是":"否"}</td>
                                                            <td>{item.cnName}</td>
                                                            <td>{item.num}</td>
                                                            <td><a href={item.url} target="_blank">{item.url}</a></td>
                                                            <td>{item.datetime?moment(item.datetime).format(TIMEFORMAT):'--'}</td>
                                                            <td>
                                                                {limitBtn.indexOf('checkCode')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.modalAppkey(item.id)}>APPKey</a>:''}
                                                                {limitBtn.indexOf('editItemAppInfo')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.modalModify(item)}>修改</a>:''}
                                                                {limitBtn.indexOf('deleteItemAppInfo')>-1?<a href="javascript:void(0)" onClick={() => this.deleteItem(item.id)}>删除</a>:''}                                                               
                                                            </td>
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
                    style={{ top: 60 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    maskClosable={false}

                >
                    {this.state.modalHtml}            
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='AM'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}








































































