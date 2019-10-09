import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import {message,Input,Modal,Button,Select,Pagination } from 'antd'
import ModalAddBank from './modal/modalAddBank'
import { pageLimit,tableScroll} from '../../../utils'
const Option = Select.Option

export default class BankManage extends React.Component{
    constructor(props){
        super(props)
        this.state = {
           tableList:[],
           visible:false,
           title:'',
           width:'',
           modalHtml:'',
           status:'',
           value:'',
           type:'',
           showHide:true,
           seq: '',
           state: '',
           name: '',
           pageIndex: PAGEINDEX,
           pageSize: PAGESIZE,
           pageTotal:0,
           reseq: '',
           restate: '',
           rename: '',
           loading:false,
           item:'',
           limitBtn:[],
           height:0,
            tableScroll:{
                tableId:'BKMAG',
                x_panelId:'BKMAGX',
                defaultHeight:500,
            }
        }
        this.requestTable = this.requestTable.bind(this)
        this.changeDetail = this.changeDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onAuditInfoBtn = this.onAuditInfoBtn.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleChangeState = this.handleChangeState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        //this.getPushAddress = this.getPushAddress.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('otcBank',this.props.permissList)
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
    requestTable(currentIndex,currentSize){
        const { rename,restate,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+'/otcBank/query',qs.stringify({
            name:rename,state:restate,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
         })).then(res => {
            const result = res.data;
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
    //地址推送
    // getPushAddress(){
    //     axios.get(DOMAIN_VIP + "/common/push").then(res => {
    //         const result = res.data
    //         if(result.code == 0){
    //             message.success(result.msg)
    //         }else{
    //             message.warning(result.msg)
    //         }
    //     })
    // }

    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
    }
    //点击分页
    changPageNum(page,pageSize){
        this.requestTable(page,pageSize)
        this.setState({
            pageIndex:page,
            pageSize:pageSize
        })
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
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
    //状态
    handleChangeState(val){
        this.setState({
            restate: val
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
    changeDetail(item){
       let mtitle= item ?'修改信息':'新增'
       console.log(item)
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading}  onClick={() => this.onAuditInfoBtn(item,'ss')}>
                保存修改
            </Button>,
        ]
        this.setState({
            item:item,
            visible:true,
            title:mtitle,
            width:'600px',
            modalHtml:<ModalAddBank item={item} handleInputChange ={this.handleInputChange}/>,
            name:item.name||'',
            state:item.state||0,
            seq:item.seq||'', 
        })
    }
    //重置
    onResetState(){
        this.setState({
            rename:'',
            restate:''
        })
    }
    onAuditInfoBtn(item,m){
        console.log(item,m)
        const {name,seq,state} = this.state
        if(!name){
            message.warning('银行名称不能为空！')
        }else if(!/^[\u4e00-\u9fa5]+$/gi.test(name)){
            message.warning('银行名称请输入汉字！')
        }else if(!seq){
            message.warning('排序不能为空！')
        }else if(!/^[1-9]\d*$|^0$/g.test(seq)){
            message.warning('排序请输入数字！')
        }else{
            this.setState({loading:true})
            item.id?
            axios.post(DOMAIN_VIP+'/otcBank/update',qs.stringify({
                id:item.id,name,state,seq
                })).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        message.success(result.msg)
                        this.setState(
                            {
                                visible:false,
                                loading:false
                            }
                        )
                        this.requestTable()
                    }else{
                        this.setState({
                            loading:false
                        })
                        message.warning(result.msg)
                    }
                }):axios.post(DOMAIN_VIP+'/otcBank/insert',qs.stringify({
                    seq,name,state
                })).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        message.success(result.msg)
                        this.setState(
                            {
                                visible:false,
                                loading:false
                            }
                        )
                        this.requestTable()
                    }else{
                        this.setState({
                            loading:false
                        })
                        message.warning(result.msg)
                    }
                })
            }
        
    }
    handleCancel(){
        this.setState({
            visible:false,
            loading:false,
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
                    axios.post(DOMAIN_VIP+"/otcBank/delete",qs.stringify({
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
    
    render(){
        const {tableList,visible,title,width,modalHtml,loading,showHide,rename,restate,pageTotal,pageIndex,pageSize,limitBtn} = this.state
        return(
            <div className="right-con">
            <div className="page-title">
                当前位置：系统中心 > 系统管理 > 银行管理
                <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
            </div>
            <div className="clearfix"></div>
            <div className="row">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                        
                        <div className="x_content">
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">银行名称：</label>
                                    <div className="col-sm-8">
                                        <input type="text" className="form-control"  name="rename" value={rename} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">状态：</label>
                                    <div className="col-sm-8">
                                        {/* <input type="text" className="form-control"  name="statusName" value={statusName} onChange={this.handleInputChange} /> */}
                                        <Select value={restate} style={{ width: SELECTWIDTH }} onChange={this.handleChangeState}>
                                            <Option value=''>请选择</Option>
                                            <Option value={0}>正常</Option>
                                            <Option value={1}>删除</Option>
                                        </Select>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-6 col-sm-6 col-xs-6 right">
                            <div className="right">
                                <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                <Button type="primary" onClick={this.onResetState}>重置</Button>
                                {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={this.changeDetail}>新增</Button>:''}
                                {/* <Button type="primary" onClick={()=>this.getPushAddress()}>地址推送</Button>  */}
                            </div>
                        </div>
                    <div>
                </div>
            </div>}
            
             
            <div className="x_panel">                 
                <div className="x_content">
                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll table-more">
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">银行名称</th>
                                    {/* <th className="column-title wid500">字典值</th> */}
                                    <th className="column-title">排序</th>
                                    <th className="column-title">状态</th>
                                    <th className="column-title">操作</th>                   
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length>0?
                                    tableList.map((item,index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{index+1}</td>
                                                <td>{item.name}</td>
                                                {/* <td>{item.value}</td> */}
                                                <td>{item.seq}</td>
                                                <td>{item.statusName}</td>
                                                <td>
                                                    {limitBtn.indexOf('update')>-1?<a className="mar20" onClick ={()=>this.changeDetail(item)}>修改</a>:''}
                                                    {limitBtn.indexOf('delete')>-1?<a onClick ={()=>this.deleteItem(item.id)}>删除</a>:''}
                                                </td>
                                            </tr>
                                        )
                                    })
                                    :<tr className="no-record"><td colSpan="20">暂无数据</td></tr>
                                }
                            </tbody>
                        </table>
                    </div>
                    <div className="pagation-box">
                        {pageTotal>0&&
                            <Pagination 
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
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    style={{marginTop:'-80px'}}
                    >
                    {modalHtml}            
                </Modal>
            </div>
            </div>
            </div>
            </div>
        )
    }
}