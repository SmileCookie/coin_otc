import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,DEFAULTVALUE } from '../../../conf'
import { Button, Pagination,message,Modal,Table } from 'antd'
import modalAdverPhotoOTC from './modal/modalAdverPhotoOTC'
import GoogleCode from '../../common/modal/googleCode'
import ModalAdverPhotoOTC from './modal/modalAdverPhotoOTC';
const confirm = Modal.confirm;
const { Column } = Table

export default class AdvertisingPhotoManageOTC extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            tableList:[],
            modalHtml:'',
            title:'',
            visible:false,
            loading:false,
            status:0,
            groupId:'',
            tableSource:[],
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
            },
            selectedRowKeys:[],//选中项的 key 数组
            selectedRows:[],
            bannerUrlCN:'', 
            bannerUrlEN:'',
            bannerUrlHK:'',
            linkUrlCN:'',
            linkUrlEN:'',
            linkUrlHK:'',
            rebannerName:'',
            googleCode:'',
            googleSpace:'',
            googleType:''
            
        }
        this.requstTable = this.requstTable.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.clickInquireState = this.clickInquireState.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onAddEditAdverPhoto = this.onAddEditAdverPhoto.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.onSelectChangeTable = this.onSelectChangeTable.bind(this)
        this.onDelete = this.onDelete.bind(this)
        this.handleChangeBannerUrl = this.handleChangeBannerUrl.bind(this)
        this.onDeleteBtn = this.onDeleteBtn.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.handleGoogleCancel = this.handleGoogleCancel.bind(this)
    }
    componentDidMount(){
        this.requstTable()
    }
    //输入时 input 设置到 state
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]:value
        })
    }
    //点击收起
    clickHide(){
        const {showHide} = this.state
        this.setState({
            showHide:!showHide
        })
    }
    //点击分页
    changPageNum(pageIndex,pageSize){        
        this.setState({
            pageIndex,
            pageSize,
        },()=>this.requstTable(pageIndex,pageSize))
    }
    //分页pageSize改变时
    onShowSizeChange(current,size){
        this.setState({
            pageIndex:current,
            pageSize:size
        },()=>this.requstTable(current,size))
    }
    //banner组ID选择
    handleChange(val){
        this.setState({
            groupId:val
        })
    }
    //查询按钮
    clickInquireState(){
        this.requstTable()
    }
    //重置按钮
    onResetState(){
        this.setState({
            //groupId:''
            rebannerName:''
        })
    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible:false,
            loading:false
        })
    }
    //多选框按钮选中时
    onSelectChangeTable(selectedRowKeys, selectedRows){
        this.setState({ selectedRowKeys,selectedRows });
        console.log(selectedRowKeys,selectedRows)
    }
    //banner图片链接设置
    handleChangeBannerUrl(url,type){
        switch(type){
            case 'CN':
                this.setState({
                    bannerUrlCN:url
                },()=>console.log(this.state.bannerUrlCN,type))
                break;
            case 'EN':
                this.setState({
                    bannerUrlEN:url
                },()=>console.log(this.state.bannerUrlEN,type))
                break;
            case 'HK':
                this.setState({
                    bannerUrlHK:url
                },()=>console.log(this.state.bannerUrlHK,type))
                break;
            default:
                break;
        }
    }
    //新增、修改广告图片管理弹窗
    onAddEditAdverPhoto(item,type){      
        console.log(item)
        let mtitle = item ? '修改广告图片' : '新增广告图片'
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={()=>this.modalGoogleCode(item,type)}>保存修改</Button>
        ]
        this.setState({
            visible:true,
            title:mtitle,
            width:'700px',
            modalHtml:<ModalAdverPhotoOTC item={item} handleChangeBannerUrl={this.handleChangeBannerUrl} handleInputChange={this.handleInputChange} />,
            bannerUrlCN:item.bannerUrl||'',
            bannerName:item.bannerName||'',
            //bannerUrlEN:item.bannerUrl||'',
            //bannerUrlHK:item.bannerUrl||'',
            linkUrlCN:item.linkUrl||'',
            //linkUrlEN:item.linkUrl||'',
            //linkUrlHK:item.linkUrl||'',
            status:item.status||0
        })
        
    }
    //修改广告图片管理弹窗
    onEditAdverPhotoBtn(item){      
         
        const {bannerUrlCN, bannerUrlEN,bannerUrlHK,linkUrlCN,linkUrlEN,linkUrlHK,status,bannerName} = this.state
        if(!bannerName){
            message.warning('图片名称不能为空！')
            return false;
        }
        if(!bannerUrlCN){
            message.warning('请上传一张图片')
            return false;
        }
        if(!linkUrlCN){
            message.warning('跳转链接不能为空！')
            return false;
        }
        item ?
        axios.post(DOMAIN_VIP+'/otcBannerPhoto/update', qs.stringify({//修改
            id:item.id,
            status,bannerName,
            bannerUrl:bannerUrlCN,
            linkUrl:linkUrlCN,
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false
                })
                this.requstTable()
            }else{
                message.warning(result.msg)
            }
        })
        :
        axios.post(DOMAIN_VIP+'/otcBannerPhoto/insert', qs.stringify({//新增
            status,bannerName,
            bannerUrl:bannerUrlCN,
            linkUrl:linkUrlCN,
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    bannerUrlCN:''
                })
                this.requstTable()
            }else{
                message.warning(result.msg)
            }
        })

    }
    //请求列表
    requstTable(currIndex,currSize){
        const {pageIndex,pageSize,groupId,pagination,rebannerName} = this.state
        let self = this
        axios.post(DOMAIN_VIP+'/otcBannerPhoto/query', qs.stringify({
            bannerName:rebannerName,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize,
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = self.changPageNum;
                pagination.onShowSizeChange = self.onShowSizeChange
                self.setState({
                    tableSource:tableSource,
                    pagination,
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    //删除弹窗
    onDelete(id,type){
        let self = this;
        // const { selectedRowKeys } = this.state
        // let selects = selectedRowKeys.join(',').replace(/\s|\xA0/ig,"");//转换成string并去除空格
        Modal.confirm({
            title:'确定删除本项吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                self.modalGoogleCode(id,type)
            },
            onCancel() {
                console.log('Cancel');
            },
        })
    }
    onDeleteBtn(id){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/otcBannerPhoto/delete', qs.stringify({
                id
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    this.requstTable()
                    resolve(result.msg)
                }else{
                    message.warning(result.msg)
                }
            }).then(error => reject(error))
        }).catch(() => console.log('Oops errors!'))
    }
    //google弹窗
    modalGoogleCode(item,type){
        this.setState({
            googVisibal:true,
            googleSpace:item,
            googleType:type
        })
    }
    //google 按钮
    modalGoogleCodeBtn(values){
        const {googleSpace,googleType} = this.state
        const {googleCode} = values
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                })
                if(googleType=='edit'||googleType=='add'){
                    this.onEditAdverPhotoBtn(googleSpace)
                }else if(googleType == 'del'){
                    this.onDeleteBtn(googleSpace)
                }
            }else{
                message.warning(result.msg)
            }
        })
    }
    //google 校验并获取一组输入框的值
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err,values) => {
            if(err) return;
            //重置输入框的值
            form.resetFields();
            this.modalGoogleCodeBtn(values)
        })
    }
    saveFormRef(formRef){
        this.formRef = formRef
    }
    //google 弹窗关闭
    handleGoogleCancel(){
        this.setState({
            googVisibal:false
        })
    }
    render(){
        const { showHide,pageIndex,pageSize,pageTotal,tableList,visible,title,width,groupId,tableSource,pagination,selectedRowKeys,rebannerName } = this.state
        // const rowSelection = {
        //     selectedRowKeys,
        //     onChange: this.onSelectChangeTable,
        //     fixed:true
        // };
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 广告管理 > OTC广告图片管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">图片名称：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="rebannerName" value={rebannerName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.clickInquireState} >查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        <Button type="primary" onClick={()=>this.onAddEditAdverPhoto('','add')} >新增</Button>
                                        
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">                                                                
                                <div className="table-responsive ">                                   
                                    <Table /*rowSelection={rowSelection}*/ dataSource={tableSource} bordered pagination={pagination} locale={{emptyText:'暂无数据'}} >
                                        <Column title='序号' dataIndex='index' render={(text)=>(
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='图片名称' dataIndex='bannerName' key='bannerName'  />
                                        <Column width='200px' title='banner图链接' dataIndex='bannerUrl' key='bannerUrl' render={(text,record) => (
                                            <a href={record.bannerUrl} target='_blank'>{record.bannerUrl}</a>
                                        )} />
                                        <Column width='200px' title='跳转链接' dataIndex='linkUrl' key='linkUrl' render={(text,record) => 
                                            <a href={record.linkUrl} target='_blank'>{record.linkUrl}</a>
                                        } />
                                        <Column title='状态' dataIndex='status' render={(text,record) => (
                                            (() => {
                                                return text == 0 ? <span>关闭</span>:<span>开启</span>
                                            })()
                                        )} />
                                        <Column title='操作' dataIndex='op' key='op' render={(text,record) => (
                                            (<span>
                                                <Button size="small" type="primary" onClick={()=>this.onAddEditAdverPhoto(record,'edit')}>修改</Button>
                                                <Button size="small" type="primary" onClick={()=>this.onDelete(record.id,'del')}>删除</Button>
                                            </span>)
                                        )} />
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>                        
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width={width}
                >
                    {this.state.modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange = {this.handleInputChange}
                    mid='ADSPM'
                    visible={this.state.googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }
}