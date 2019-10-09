import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE, TIMEFORMAT_ss,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button,Table,message,Modal,Icon } from 'antd'
import ModalScheduled from './modal/modalScheduled'
const { Column } = Table

export default class ScheduledTask extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            visible:false,
            title:'',
            width:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableSource:[],
            modalHtml:'',
            beanName:'',
            rebeanName:'',
            jobId:0,
            methodName:'',
            params:'',
            cronExpression:'',
            status:0,
            remark:'',
            creatTime:'',
            selectedRowKeys:[],//选中项的 key 数组
            selectedRows:[],
            pagination:{
                showSizeChanger:true,//是否可以改变 pageSize
                showQuickJumper:true,//是否可以快速跳转至某页
                showTotal:total=>`总共 ${total} 条`,
                size:'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            }

        }

        this.showHide = this.showHide.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTable = this.onChangeTable.bind(this)
        this.onSelectChange = this.onSelectChange.bind(this)
        this.show_click = this.show_click.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onAddModalBtn = this.onAddModalBtn.bind(this)
        this.onAddModal = this.onAddModal.bind(this)
        this.onDelete = this.onDelete.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onPause = this.onPause.bind(this)
        this.onRestore = this.onRestore.bind(this)
        this.onStart = this.onStart.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
    }

    componentDidMount(){
        this.requestTable()
    }
    requestTable(currentIndex,currentSize){
        const {pageIndex,pageSize,rebeanName,pagination} = this.state
        let self = this
        axios.get(DOMAIN_VIP+'/schedule/list', { params:{
            page:currentIndex||pageIndex,
            limit:currentSize||pageSize,
            beanName:rebeanName
        }}).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.page.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.page.currPage-1)*result.page.pageSize+i+1;
                    tableSource[i].key = tableSource[i].jobId;
                }
                pagination.total = result.page.totalCount;
                pagination.onChange = self.changPageNum
                pagination.onShowSizeChange = self.onShowSizeChange
                this.setState({
                    tableSource:tableSource,
                    pagination
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //table 多选框
    onSelectChange (selectedRowKeys){
        //console.log(selectedRowKeys)
        this.setState({ selectedRowKeys });
    }

    //多选框按钮选中时
    onChangeTable(selectedRowKeys, selectedRows){
        this.setState({ selectedRowKeys,selectedRows });
        //console.log(selectedRowKeys,selectedRows)
    }
    // 查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
        
    }
    //点击收起
    showHide(){
        const { showHide } = this.state
        this.setState({
            showHide: !showHide
        })
    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTable(page,pageSize))
    }
    //分页pageSize改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }
    //修改状态
    show_click(index,ids) {
        this.props.showHideClick(index, ids);
    }
    //输入时 input 设置到 state
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'check' ? target.check : target.value;
        const name = target.name
        this.setState({
            [name]: value
        })
    }
    
    //新增、修改弹窗
    onAddModal(item,type){
        
        let mtitle = type==='edit'?'修改信息':'新增';
        if(!item&&type==='edit'){
            message.warning('请选择一项');
            return false
        }
        const { jobId,beanName,methodName,params,cronExpression,status,remark,createTime } = item
        this.footer=[
            <Button key='back' onClick={this.handleCancel}>取消</Button>,
            <Button key='submit' type='more' onClick={()=>this.onAddModalBtn(item,type)}>确认修改</Button>
        ]
        this.setState({
            title:mtitle,
            visible:true,
            width:'600px',
            modalHtml:<ModalScheduled item={item} handleInputChange={this.handleInputChange}/>,
            jobId:jobId||0,
            beanName:beanName||'',
            methodName:methodName||'',
            params:params||'',
            cronExpression:cronExpression||'',
            status:status||0,
            remark:remark||'',
            createTime:createTime||''
        })
    }
    //新增、修改按钮
    onAddModalBtn(item,type){
        const {jobId,beanName,methodName,params,cronExpression,status,remark,createTime} = this.state
        if(!beanName||!methodName||!cronExpression){
            message.warning('必填项不能为空！')
            return false
        }
        let url = type==='edit'?'/schedule/update':'/schedule/save';
        type ==='edit'?
        axios.post(DOMAIN_VIP+url,qs.stringify({
            jobId:item.jobId,beanName,methodName,params,cronExpression,status,remark,
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    selectedRowKeys:[]
                },()=>this.requestTable())
            }else{
                message.warning(result.msg)
            }
        })
        :axios.post(DOMAIN_VIP+url,qs.stringify({
            beanName,methodName,params,cronExpression,status,remark,
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false
                },()=>this.requestTable())
            }else{
                message.warning(result.msg)
            }
        })
    }
    //删除按钮
    onDelete(){
        const {selectedRowKeys} = this.state
        let selects = selectedRowKeys.join(',').replace(/\s|\xA0/ig,"");
        if(!selects.length){
            message.warning('请选择一项')
            return false
        }
        let self = this
        Modal.confirm({
            title:'您确定要删除这些数据吗?',
            okText:'确定',
            okType:'danger',
            okCancel:'取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/schedule/delete',qs.stringify({
                        jobIds:selects
                    })).then(res => {
                        const result = res.data
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
                }).catch(()=>{console.log('Oops errors!')})
            },
            onCancel(){
                console.log('Cancel')
            }
        })
    }
    //暂停按钮
    onPause(){
        const {selectedRowKeys} = this.state
        let selects = selectedRowKeys.join(',').replace(/\s|\xA0/ig,"");
        if(!selects.length){
            message.warning('请选择一项')
            return false
        }
        axios.post(DOMAIN_VIP+'/schedule/pause',qs.stringify({
            jobIds:selects
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    selectedRowKeys:[]
                })
                this.requestTable()
            }else{
                message.warning(result.msg)
            }
        })
    }
    //恢复按钮
    onRestore(){
        const {selectedRowKeys} = this.state
        let selects = selectedRowKeys.join(',').replace(/\s|\xA0/ig,"");
        if(!selects.length){
            message.warning('请选择一项')
            return false
        }
        axios.post(DOMAIN_VIP+'/schedule/resume',qs.stringify({
            jobIds:selects
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    selectedRowKeys:[]
                })
                this.requestTable()
            }else{
                message.warning(result.msg)
            }
        })
    }
    //立即执行按钮
    onStart(){
        const {selectedRowKeys} = this.state
        let selects = this.state.selectedRowKeys.join(',').replace(/\s|\xA0/ig,"");
        // console.log(selects)
        if(!selects.length){
            message.warning('请选择一项')
            return false
        }
        axios.post(DOMAIN_VIP+'/schedule/run',qs.stringify({
            jobIds:selects
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    selectedRowKeys:[]
                })
                this.requestTable()
            }else{
                message.warning(result.msg)
            }
        })
    }
    render(){
        const { showHide,title,width,rebeanName,tableSource,selectedRowKeys,pagination,modalHtml,visible,selectedRows,pageIndex } = this.state
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onChangeTable,
            fixed:true
          };
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>系统管理>定时任务管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-3 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">bean名称：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="rebeanName" value={rebeanName} onChange={this.handleInputChange} />
                                            {/* <b className="icon-fuzzy">%</b> */}
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-12 col-sm-12 col-xs-12 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquireBtn} >查询</Button>
                                        {/* <Button type="primary" >重置</Button> */}
                                        <Button type="primary" onClick={()=>this.onAddModal({},'add')}>新增</Button>
                                        <Button type="primary" onClick={()=>this.onAddModal(selectedRows[0],'edit')}>修改</Button>
                                        <Button type="primary" onClick={()=>this.onDelete()}>删除</Button>
                                        <Button type="primary" onClick={()=>this.onPause()}>暂停</Button>
                                        <Button type="primary" onClick={()=>this.onRestore()}>恢复</Button>
                                        <Button type="primary" onClick={()=>this.onStart()}>立即执行</Button>
                                        <Button type="more" onClick={()=>this.show_click(1)}>日志列表</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive table-checkbox">
                                    <Table dataSource={tableSource} rowSelection={rowSelection} bordered locale={{emptyText:'暂无数据'}} pagination={{...pagination,current:pageIndex}}>
                                        {/* <Column title='' dataIndex='index' /> */}
                                        <Column title='序号' dataIndex='index' key='index' render={(text,record)=>(
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='任务ID' dataIndex='jobId' key='jobId' />
                                        <Column title='bean名称' dataIndex='beanName' key='beanName' />
                                        <Column title='方法名称' dataIndex='methodName'key='methodName' />
                                        <Column title='参数' dataIndex='params' key='params' />
                                        <Column title='cron表达式' dataIndex='cronExpression' key='cronExpression' />
                                        <Column title='备注' dataIndex='remark' key='remark' />
                                        <Column title='状态' dataIndex='status' render={(text,record) => (
                                            (() => {
                                                return text == 0 ? <span>正常</span>:<span>暂停</span>
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
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {this.state.modalHtml}            
                </Modal> 
            </div>
        )
    }
}

