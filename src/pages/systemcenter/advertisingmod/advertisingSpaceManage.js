import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,DEFAULTVALUE,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, Pagination,message,Modal,Table } from 'antd'
import ModalAdverSpace from './modal/modalAdverSpace'
import ModalAdverImgPre from './modal/modalAdverImgPre'
import ModalAdverImgEdit from './modal/modalAdverImgEdit'
import SelectBannerType from '../select/selectBannerType'
import GoogleCode from '../../common/modal/googleCode'
const confirm = Modal.confirm;
const {Column} = Table

export default class AdvertisingSpaceManage extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            tableList:[],
            id:'',
            bannerGroup:'',
            rebannerGroup:'',
            remark:'',
            modalHtml:'',
            title:'',
            visible:false,
            loading:false,
            tableSource:[],
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            selectedRowKeys:[],//选中项的 key 数组
            selectedRows:[],//选中项的 item 数组
            bannerUrlCN:'', 
            bannerUrlEN:'',
            bannerUrlHK:'',
            linkUrlCN:'',
            linkUrlEN:'',
            linkUrlHK:'',
            status:'0',
            selecteds:[],
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
        this.onAddEditAdverSpace = this.onAddEditAdverSpace.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        //this.onAddAdverPhoto = this.onAddAdverPhoto.bind(this)
        this.onAddEditAdverSpaceBtn = this.onAddEditAdverSpaceBtn.bind(this)
        this.onDelete = this.onDelete.bind(this)
        this.onDeleteBtn = this.onDeleteBtn.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.onSelectChangeTable = this.onSelectChangeTable.bind(this)
        this.onSelectPhotos = this.onSelectPhotos.bind(this)
        this.onSelectPhotosBtn = this.onSelectPhotosBtn.bind(this)
        this.onViewImg = this.onViewImg.bind(this)
        this.requstPreTable = this.requstPreTable.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.handleGoogleCancel = this.handleGoogleCancel.bind(this)
    }
    componentDidMount(){
        this.requstTable()
    }
    //请求列表
    requstTable(currIndex,currSize){
        const {pageIndex,pageSize,bannerGroup,pagination,tableSource,rebannerGroup} = this.state
        let self = this
        axios.get(DOMAIN_VIP+'/bannerGroup/queryList', {params: {
            bannerGroup:rebannerGroup,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        }}).then(res => {
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
        },()=>this.requstTable(pageIndex,pageSize))
    }
    //查询按钮
    clickInquireState(){
        this.requstTable()
    }
    //重置按钮
    onResetState(){
        this.setState({
            rebannerGroup:''
        })
    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible:false,
            loading:false,
            selecteds:[],
            selectedKeys:[],
        })
    }
    //banner组ID选择
    handleChange(val,option){
        //console.log(val,option)//option.props.children为选中的文字
        this.setState({
            rebannerGroup:option.props.children
        })
    }
    //table 多选框按钮选中时
    onSelectChangeTable(selectedRowKeys, selectedRows){
        this.setState({ selectedRowKeys,selectedRows });
        //console.log(selectedRowKeys,selectedRows)
    }
    //新增、修改广告位弹窗
    onAddEditAdverSpace(item,type){
        let mtitle = item ? '编辑广告位':'新增广告位';
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" onClick={()=>this.modalGoogleCode(item,type)} loading={this.state.loading} type="more">保存修改</Button>
        ]
        this.setState({
            visible:true,
            title:mtitle,
            width:'700px',
            modalHtml:<ModalAdverSpace item={item} handleInputChange={this.handleInputChange} />,
            bannerGroup:item.bannerGroup||'',
            status:item.status||'1',
            remark:item.remark||'',
        })
    }
    //新增、修改广告位弹窗 确定按钮
    onAddEditAdverSpaceBtn(item){
        const { status,remark,bannerGroup} = this.state
        if(!bannerGroup){
            message.warning('标题不能为空！');
            return false;
        }
        item ? 
        axios.post(DOMAIN_VIP+'/bannerGroup/update', qs.stringify({//修改
            id:item.id,
            status,remark,bannerGroup,
        })).then(res => {
            const result = res.data;
            if(result.code==0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    laoding:false
                })
                this.requstTable()
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        })
        :        
        axios.post(DOMAIN_VIP+'/bannerGroup/update', qs.stringify({//新增
            bannerGroup,
            status,remark
        })).then(res => {
            const result = res.data;
            if(result.code==0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    laoding:false
                })
                this.requstTable()
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        })
    }
    //选择图片回调
    onSelectPhotos(selectedKeys,selecteds){
        //console.log(selecteds)
        this.setState({
            selecteds,
            selectedKeys,
        })
    }
    //广告位图片编辑弹窗
    onPreviewImg(item,type){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" onClick={()=>this.modalGoogleCode(item,type)} loading={this.state.loading} type="more">保存修改</Button>    
        ]
        this.setState({
            visible:true,
            title:'广告图片',
            width:'900px',
            modalHtml:<ModalAdverImgEdit item={item}  onSelectPhotos={this.onSelectPhotos} />
        })
    }
    //确认选择图片按钮
    onSelectPhotosBtn(item){
        const {selecteds,selectedKeys} = this.state
        let selectKey
        if(selectedKeys){
            selectKey = selectedKeys.join(',').replace(/\s|\xA0/ig,"");
        }else{
            selectKey=''
        }       
        axios.post(DOMAIN_VIP+'/bannerGroup/saveRelation', qs.stringify({
            groupId:item.id,photoId:selectKey
        })).then(res => {
            const result = res.data;
            if(result.code==0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    laoding:false,
                })
                this.requstTable()
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        })
    }    
    //广告图片预览
    onViewImg(item){
        this.requstPreTable(item)
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>返回</Button>,
               
        ]
    }
    //广告图片预览
    requstPreTable(item){
        axios.get(DOMAIN_VIP+'/bannerGroup/queryBannerPhoto', {params:{
            pageIndex:1,
            pageSize:100000,
            groupId:item.id,
        }}).then(res => {
            const result = res.data;
            if(result.code==0){
                let tableSource = result.data;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = i+1;
                    tableSource[i].key = tableSource[i].id
                }
                this.footer = [
                    <Button key="back" onClick={this.handleCancel}>关闭</Button>,
                       
                ]
                this.setState({
                    visible:true,
                    title:'广告图片预览',
                    width:'600px',
                    modalHtml:<ModalAdverImgPre tableSourcePre={tableSource} />
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    
    //删除弹窗
    onDelete(item,type){
        let self = this;
        // const { selectedRowKeys } = this.state
        // let selects = selectedRowKeys.join(',').replace(/\s|\xA0/ig,"");//转换成string并去除空格
        Modal.confirm({
            title:'确定删除本项吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                self.modalGoogleCode(item,type)
            },
            onCancel() {
                console.log('Cancel');
            },
        })
    }
    //删除确定按钮
    onDeleteBtn(item){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/bannerGroup/delete', qs.stringify({
                id:item.id
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
                    this.onAddEditAdverSpaceBtn(googleSpace)
                }else if(googleType == 'del'){
                    this.onDeleteBtn(googleSpace)
                }else if(googleType == 'photoEdit'){
                    this.onSelectPhotosBtn(googleSpace)
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
        const { showHide,pageIndex,pageSize,pageTotal,tableList,visible,title,width,bannerGroup,tableSource,pagination,selectedRowKeys,selectedRows,rebannerGroup } = this.state
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChangeTable,
            fixed:true
        };
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 广告管理 > 广告位管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectBannerType findsType={rebannerGroup} label="Banner组" col='3' handleChange={this.handleChange}></SelectBannerType>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.clickInquireState} >查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {/* <Button type="primary" onClick={()=>this.onAddEditAdverSpace('','add')} >新增</Button>                                       */}
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table
                                         dataSource={tableSource} bordered pagination={pagination}>
                                        <Column title='序号' dataIndex='index' render={(text)=>(
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='标题' dataIndex='bannerGroup' key='bannerGroup' />
                                        <Column title='状态' dataIndex='status' key='status' render={(text,record) => (
                                            (()=>{
                                                return text == 0 ? <span>关闭</span>:<span>开启</span>
                                            })()
                                        )} />
                                        <Column title='备注' dataIndex='remark' key='remark' />
                                        <Column width='153px' title='操作' dataIndex='cp' render={(text,record) => (                                           
                                            (() => {
                                                return  <span>
                                                            <Button size="small" type="primary" onClick={()=>this.onAddEditAdverSpace(record,'edit')} >编辑</Button> 
                                                            {/* <Button size='small' type="primary" onClick={()=>this.onDelete(record,'del')}>删除</Button>  */}
                                                            <Button size="small" type="primary"  onClick={()=>this.onPreviewImg(record,'photoEdit')}>广告位图片编辑</Button>
                                                            <Button size="small" type="primary"  onClick={()=>this.requstPreTable(record)}>广告位图片预览</Button>                                                          
                                                        </span>
                                            })()
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
                    mid='ADTSM'
                    visible={this.state.googVisibal}
                    onCancel={this.handleGoogleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }
}