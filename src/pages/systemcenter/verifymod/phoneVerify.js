import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalGoogle from './modal/modalGoogle'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Select,Modal,message } from 'antd'
import { pageLimit,tableScroll } from '../../../utils'
const { RangePicker } = DatePicker
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class UserCapital extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            showHide:true,
            userId:'',
            userName:'',
            verifyOldInfo:'',
            adminUserName:'',
            submitTimeS:'',
            submitTimeE:'',
            checkTimeS:'',
            checkTimeE:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tab:0,
            pageTotal:0,
            tableList:[],
            visible:false,
            title:'',
            modalHtml:'',
            auditStatus:'',
            auditMemo:'',
            time:[],
            checkTime:[],
            googleCode:'',
            limitBtn: [],
            googVisibal:false,
            googleSpace:'',
            type:'',
            height:0,
            tableScroll:{
                tableId:'PHEVIY',
                x_panelId:'PHEVIYX',
                defaultHeight:500,
            }
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.callbackTabs = this.callbackTabs.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onChangeApplyTime = this.onChangeApplyTime.bind(this)
        this.onAuditGoogle = this.onAuditGoogle.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onAuditGoogleBtn = this.onAuditGoogleBtn.bind(this)
        this.onModalRadio = this.onModalRadio.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.passGoogleCode = this.passGoogleCode.bind(this)
        this.requestGoogle = this.requestGoogle.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('phoneVerify', this.props.permissList)
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
        },() => this.requestTable())    
    }

    //table 列表
    requestTable(currIndex,currSize,currKey){
        const { userId,userName,verifyOldInfo,adminUserName,submitTimeS,submitTimeE,checkTimeS,checkTimeE,pageIndex,pageSize,tab } = this.state
        axios.post(DOMAIN_VIP+'/phoneVerify/queryList',qs.stringify({
            userId,userName,verifyOldInfo,adminUserName,submitTimeS,submitTimeE,checkTimeS,checkTimeE,
            tab:currKey||tab,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            console.log(result)
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

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        })
        this.requestTable(page,pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }

    //过去弹窗单选按钮
    onModalRadio(val){
        this.setState({
            auditStatus:val
        })
    }
 
    //冻结或者解冻
    passGoogleCode(id){
        this.setState({
            googVisibal:true,
            googleSpace:id,
        })
    }

    //冻结或者解冻按钮 谷歌验证
    requestGoogle(value){
        const { googleSpace } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                console.log(result)
                this.setState({
                    googVisibal:false
                },()=>this.onAuditGoogleBtn(googleSpace))
                
            }else{
                message.warning(result.msg)
            }
        })
    }

    //审核弹窗
    onAuditGoogle(id){
        const { tab } = this.state
        let url = tab ==0 ? "/phoneVerify/see":"/phoneVerify/onlySee";
        axios.post(DOMAIN_VIP+url,qs.stringify({
            id:id
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.footer=[
                    <Button key="back" onClick={this.handleCancel}>取消</Button>,
                    <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.passGoogleCode(id)}>
                        确认
                    </Button>,
                ]
                this.setState({
                    visible:true,
                    title:'我要审核',
                    modalHtml:<ModalGoogle item={result.Data} tab={this.state.tab} onModalRadio={this.onModalRadio} handleInputChange={this.handleInputChange} />
                })
            }else{
                message.warning(result.msg)
            }
        })

    }
    //审核弹窗按钮
    onAuditGoogleBtn(id){
        const { auditMemo,auditStatus } = this.state
        axios.post(DOMAIN_VIP + '/phoneVerify/pass',qs.stringify({
            id,
            memo:auditMemo,
            status:auditStatus
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false
                })
                this.requestTable()
                
            }else{
                message.warning(result.msg)
            }
        })
    }
    //select 变化时
    handleChange(value){
        this.setState({
            verifyOldInfo:value
        })
    }
    //重置状态
    onResetState(){
        this.setState({
            userId:'',
            userName:'',
            verifyOldInfo:'',
            adminUserName:'',
            submitTimeS:'',
            submitTimeE:'',
            checkTimeS:'',
            checkTimeE:'',
            time:[],
            checkTime:[]
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
 
    //时间选择框变化时   审核时间
    onChangeTime(date, dateString){
        this.setState({
            checkTimeS:dateString[0],
            checkTimeE:dateString[1],
            checkTime:date
        })
    }
    //apply申请时间
    onChangeApplyTime(date, dateString){
        this.setState({
            submitTimeS:dateString[0],
            submitTimeE:dateString[1],
            time:date
        })
    }
    //弹窗隐藏
    handleCancel(){
        console.log("handleCancel")
        this.setState({ 
            visible: false
        });
    }

    //tab 切换时
    callbackTabs(key){
        const { pageIndex,pageSize } = this.state
        this.setState({
            tab:key
        })
        this.requestTable(pageIndex,pageSize,key)
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
            // }
    }
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.requestGoogle(values)
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
        const { showHide,userId,userName,verifyOldInfo,adminUserName,submitTimeS,submitTimeE,checkTimeS,checkTimeE,pageIndex,pageSize,pageTotal,tableList,tab,visible,title,modalHtml,time,checkTime,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>用户管理>手机审核
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                       { showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
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
                                        <label className="col-sm-3 control-label">验证旧手机：</label>
                                        <div className="col-sm-8">
                                            <Select value={verifyOldInfo} style={{ width: SELECTWIDTH }} onChange={this.handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">是</Option>
                                                <Option value="0">否</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">审核人：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="adminUserName" value={adminUserName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">申请时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChangeApplyTime} value={time}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">审核时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChangeTime} value={checkTime}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>                                        
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <div className="x_panel">
                            <div className="x_content">
                                <Tabs defaultActiveKey="0" onChange={this.callbackTabs}>
                                    <TabPane tab="待审核" key="0"></TabPane>
                                    <TabPane tab="已通过" key="2"></TabPane>
                                    <TabPane tab="已拒绝" key="1"></TabPane>
                                    <TabPane tab="已撤销" key="3"></TabPane>                                    
                                </Tabs>
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">用户编号</th>
                                                <th className="column-title">申请时间</th>
                                                <th className="column-title">审核时间|撤销时间</th>
                                                <th className="column-title">审核人</th>
                                                <th className="column-title">申请操作</th>
                                                <th className="column-title">申请修改</th>
                                                <th className="column-title">修改后</th>  
                                                <th className="column-title">验证旧手机</th>
                                                <th className="column-title">状态</th>
                                                <th className="column-title">操作</th>                       
                                            </tr>
                                        </thead>
                                        <tbody>
                                        {
                                            tableList.length>0?
                                            tableList.map((item,index)=>{
                                                return (
                                                    <tr key={index}>
                                                        <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                        <td>{item.userId}</td>
                                                        <td>{moment(item.addTime).format(TIMEFORMAT)}</td>
                                                        <td>{tab!=0?moment(item.verifyTime).format(TIMEFORMAT):''}</td>
                                                        <td>{item.adminName}</td>
                                                        <td>{item.type==1?'更改手机':item.type==2?'更改Google':'挂失手机'}</td>
                                                        <td>{item.beforeInfoShow}</td>
                                                        <td>{item.infoShow}</td>
                                                        <td>{item.verifyOldInfo==1?'是':'否'}</td>
                                                        <td>{item.status==0?'待审核':item.status==1?'已拒绝':item.status==2?'已通过':'已撤销'}</td>
                                                        <td>
                                                            {
                                                                tab==0?
                                                                (limitBtn.indexOf('see')>-1?<a href="javascript:void(0)" onClick={() => this.onAuditGoogle(item.id)}>审核</a>:'')
                                                                :
                                                                (limitBtn.indexOf('onlySee')>-1?<a href="javascript:void(0)" onClick={() => this.onAuditGoogle(item.id)}>查看</a>:'')
                                                            }
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
                    width="600px"
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    { modalHtml }
                </Modal> 
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='PV'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}






























































