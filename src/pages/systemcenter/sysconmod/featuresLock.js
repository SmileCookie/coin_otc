import React from 'react'
import ReactDOM from 'react-dom'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Select,Modal,Button,Table,Pagination,message,DatePicker } from 'antd'
import { toThousands,pageLimit } from '../../../utils'
import GoogleCode from '../../common/modal/googleCode'
import {tableScroll} from '../../../utils/index'
import Item from 'antd/lib/list/Item';
const confirm = Modal.confirm;


export default class FeaturesLock extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            visible:false,            
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            userid:'',
            modalHtml:'',
            loading:false,
            width:'',
            height:0,
            tableScroll:{
                tableId:'FETRSLK',
                x_panelId:'FETRSLKX',
                defaultHeight:500,
            },
            googVisibal:false,
            googleCode:'',
            googleSpace:'',
            googleType:'',
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
        this.onLock = this.onLock.bind(this)
        this.onLockBtn = this.onLockBtn.bind(this)

        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.handleGoogleCancel = this.handleGoogleCancel.bind(this)

    }
    componentDidMount(){
        this.requestTable()
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
    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        })
        this.requestTable(PAGEINDEX,PAGESIZE)
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

    //重置状态
    onResetState(){
        this.setState({
           userid:''
        })
    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible: false,
            loading:false
        })
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
    //输入时 input 设置到 state
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    } 
    //table 请求
    requestTable(currentIndex,currentSize){
        const {pageIndex,pageSize,userid} = this.state
        axios.post(DOMAIN_VIP+"/deblocking/query",qs.stringify({
            userid,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code ==0 ){
                this.setState({
                    tableList:result.page,
                    // pageTotal:result.data.totalCount
                })
            }else if(result.code == 500){
                message.warning(result.msg)
                this.setState({
                    tableList:[]
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    onLock(item,type){
        let self = this;
        Modal.confirm({
            title: "确定解锁本项吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                self.modalGoogleCode(item,type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
        // const {userid} = this.state
        // axios.post(DOMAIN_VIP+"/deblocking/clearlock",qs.stringify({
        //     userId:userid,key:item.keyName
        // })).then(res => {
        //     const result = res.data;
        //     if(result.code ==0 ){
        //         message.success(result.msg)
        //         this.requestTable()
        //     }else{
        //         message.warning(result.msg)
        //     }
        // })
    }
    onLockBtn(item){
        const {userid} = this.state
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+"/deblocking/clearlock",qs.stringify({
                userId:userid,key:item.keyName
            })).then(res => {
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
                if(googleType=='unlock'){
                    this.onLockBtn(googleSpace)
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
        const {tableList,showHide,userid,pageIndex,pageSize,pageTotal,visible,width} = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 系统管理 > 功能锁
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">                           
                            <div className="x_content">                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户ID：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="userid" value={userid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-12 col-sm-12 col-xs-12 marTop">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">    
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto" >
                                    <table  className="table table-striped jambo_table bulk_action table-linehei table_scroll ">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">key</th>
                                                <th className="column-title">功能</th>
                                                <th className="column-title">上限次数</th>
                                                <th className="column-title">状态</th>
                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {    
                                                tableList.length>0?tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index} >
                                                            {/* <td>{(pageIndex-1)*pageSize+index+1}</td> */}
                                                            <td>{index+1}</td>
                                                            <td>{item.keyName}</td>
                                                            <td>{item.remark}</td>
                                                            <td>{item.limitForbid}</td>
                                                            <td>{item.lock==='false'?'未锁定':'已锁定'}</td>
                                                            <td>
                                                                {item.lock==='false'?
                                                                    '解锁'
                                                                    :<a href="javascript:void(0)" onClick={()=>this.onLock(item,'unlock')}>解锁</a>
                                                                }                       
                                                            </td>                                           
                                                        </tr>
                                                    )
                                                }):<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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
                                                onChange={this.changPageNum}
                                                showTotal={total => `总共 ${total} 条`}
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
                    title={this.state.title}
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
                    mid='TFS'
                    visible={this.state.googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }

}