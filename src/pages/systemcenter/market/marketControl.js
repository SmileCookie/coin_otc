import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
// import ModalGoogle from './modal/modalGoogle'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,SELECTWIDTH,TIMEFORMAT_ss } from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Select,Modal,message } from 'antd'
import { pageLimit,tableScroll,showChooseType} from '../../../utils'
import ModalMarketControl from './modal/modalMarketControl'
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
            chooseIndex:0,
            keyWord:'',
            sureType:0,
            Beizu:''

        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.callbackTabs = this.callbackTabs.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onChangeApplyTime = this.onChangeApplyTime.bind(this)
        //this.onAuditGoogle = this.onAuditGoogle.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        //this.onAuditGoogleBtn = this.onAuditGoogleBtn.bind(this)
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
        this.chooseType = this.chooseType.bind(this)
    }

    componentDidMount(){
        let {  pageIndex,pageSize} = this.state;
        this.requestTable(pageIndex,pageSize)
        this.setState({
            // limitBtn: pageLimit('phoneVerify', this.props.permissList)
        })
    }
    componentWillUnmount(){

    }
    //查询按钮
    inquireBtn(){
        let {chooseIndex} = this.state;
        this.setState({
            sureType:chooseIndex,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE
        },() =>{this.requestTable(PAGEINDEX,PAGESIZE)})
             
    }

    //table 列表
    requestTable(currIndex,currSize,currKey){
        const { sureType,keyWord } = this.state
        axios.post(DOMAIN_VIP+'/extend/query',qs.stringify({
            type:sureType,
            keyWord,
            pageIndex:currIndex,
            pageSize:currSize
            

        })).then(res => {
            const result = res.data;
            console.log(result)
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
    // onAuditGoogle(id){
    //     const { tab } = this.state
    //     let url = tab ==0 ? "/phoneVerify/see":"/phoneVerify/onlySee";
    //     axios.post(DOMAIN_VIP+url,qs.stringify({
    //         id:id
    //     })).then(res => {
    //         const result = res.data
    //         if(result.code == 0){
    //             this.footer=[
    //                 <Button key="back" onClick={this.handleCancel}>取消</Button>,
    //                 <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.passGoogleCode(id)}>
    //                     确认
    //                 </Button>,
    //             ]
    //             this.setState({
    //                 visible:true,
    //                 title:'我要审核',
    //                 modalHtml:<ModalGoogle item={result.Data} tab={this.state.tab} onModalRadio={this.onModalRadio} handleInputChange={this.handleInputChange} />
    //             })
    //         }else{
    //             message.warning(result.msg)
    //         }
    //     })

    // }
    // //审核弹窗按钮
    // onAuditGoogleBtn(id){
    //     const { auditMemo,auditStatus } = this.state
    //     axios.post(DOMAIN_VIP + '/phoneVerify/pass',qs.stringify({
    //         id,
    //         memo:auditMemo,
    //         status:auditStatus
    //     })).then(res => {
    //         const result = res.data;
    //         if(result.code == 0){
    //             message.success(result.msg)
    //             this.setState({
    //                 visible:false
    //             })
    //             this.requestTable()
                
    //         }else{
    //             message.warning(result.msg)
    //         }
    //     })
    // }
    //select 变化时
    handleChange(value){
        this.setState({
            verifyOldInfo:value
        })
    }
    //重置状态
    onResetState(){
        this.setState({
            chooseIndex:0,
            keyWord:'',
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
        let { showHide,pageSize } = this.state;

            this.setState({
                showHide: !showHide,
            })
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

    chooseType(value){
        let {  pageIndex,pageSize} = this.state;
        this.setState({
            chooseIndex:value
        })
    }
    
    leadModal = item => {
        this.footer= [
            <Button key="save" type="more" onClick={()=>this.leadModalBtn(item)}>确认领办</Button>,
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        this.setState({
            visible:true,
            title:"领办",
            width:'700px',
            refundstype:item.fundstype||'',
            modalHtml:<ModalMarketControl item={item} sureType ={this.state.sureType} handleInputChange={this.handleInputChange} />,

        })
    }
    leadModalBtn = (item) => {
        let {pageIndex,pageSize,chooseIndex} = this.state;
        axios.post(DOMAIN_VIP+'/extend/receive',qs.stringify({
          id:item.id,
          type:chooseIndex,
          memo:this.state.Beizu

        })).then(res => {
            const result = res.data;
            // console.log(result)
            if(result.code == 0){
            //    console.log(result)
               this.requestTable(pageIndex,pageSize)
               this.handleCancel()
            }else{
                message.warning(result.msg)
            }
        })
        //console.log(this.state.Beizu)
        
    }
    render(){
        const { sureType,chooseIndex,showHide,userId,userName,verifyOldInfo,adminUserName,submitTimeS,submitTimeE,checkTimeS,checkTimeE,pageIndex,pageSize,pageTotal,tableList,tab,visible,title,modalHtml,time,checkTime,limitBtn,keyWord } = this.state
        let th_3, th_4, th_5;
        switch(sureType){
            case 0:
                th_3 = '媒体名称'
                th_4 = '联系人'
                th_5 = '电话'
            break;
            case 1:
                th_3 = '姓名'
                th_4 = '联系人'
                th_5 = '电话'
            break;
            case 3:
                th_3 = '钱包名称'
                th_4 = '官方链接'
                th_5 = '联系人'
            break;
            case 4:
                th_3 = '姓名'
                th_4 = '电话'
                th_5 = '微信'
            break;
        }
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>用户管理>手机审核
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                       { showHide&&<div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">关键词：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="keyWord" value={keyWord} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                    <label className="col-sm-3 control-label">类型：</label>
                                    <Select value={chooseIndex} style={{ width:216 }} onChange={this.chooseType}>
                                        <Option value={0}>媒体邀请</Option>
                                        <Option value={1}>名人邀请</Option>
                                        <Option value={3}>钱包名称</Option>
                                        <Option value={4}>兼职邀请</Option>
                                    </Select>
                                    {/* <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>                                        
                                    </div> */}
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>                                        
                                    </div>
                                </div>
                            </div>
                        </div>}
 
                        <div className="x_panel">
                            <div className="x_content">
                                {/* <Tabs defaultActiveKey="0" onChange={this.callbackTabs}>
                                    <TabPane tab="待审核" key="0"></TabPane>
                                    <TabPane tab="已通过" key="2"></TabPane>
                                    <TabPane tab="已拒绝" key="1"></TabPane>
                                    <TabPane tab="已撤销" key="3"></TabPane>                                    
                                </Tabs> */}
                                <div className="table-responsive">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">类型</th>
                                                <th className="column-title">{th_3}</th> 
                                                <th className="column-title">{th_4}</th> 
                                                <th className="column-title">{th_5}</th> 
                                                <th className="column-title">{sureType == 4?'钱包地址':'微信号'}</th> 
                                                {
                                                    sureType !== 4?
                                                    <th className="column-title">合作类型</th>
                                                    : null
                                                }
                                                <th className="column-title">申请时间</th>
                                                <th className="column-title">备注</th> 
                                                <th className="column-title">操作</th>                                      
                                            </tr>
                                        </thead>
                                        <tbody>
                                        {
                                            tableList.length>0?
                                            tableList.map((item,index)=>{
                                                
                                                let _cooperType;
                                                if(sureType !== 4){
                                                    _cooperType = showChooseType(item.cooperateType);
                                                }
                                                // console.log(_cooperType)
                                                
                                                return (
                                                    <tr key={index}>
                                                        <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                        <td>{(()=>{
                                                            switch(sureType){
                                                                case 0:
                                                                    return '媒体邀请'
                                                                case 1:
                                                                    return '名人邀请'
                                                                case 3:
                                                                    return '钱包名称'
                                                                case 4:
                                                                    return '兼职邀请'
                                                                default:
                                                                    return '返回错误'
                                                            }
                                                        })()}</td>
                                                        <td>{item.name || item.walletName}</td>
                                                        <td>{
                                                                (()=>{
                                                                    switch(sureType){
                                                                        case 0:
                                                                            return item.userName
                                                                        case 1:
                                                                            return item.userName
                                                                        case 3:
                                                                            return item.websitesLink
                                                                        case 4:
                                                                            return item.mobile
                                                                        default:
                                                                            return item.userName
                                                                    }
                                                                })()
                                                            }</td>
                                                        <td>{
                                                                (()=>{
                                                                    switch(sureType){
                                                                        case 0:
                                                                            return item.mobile
                                                                        case 1:
                                                                            return item.mobile
                                                                        case 3:
                                                                            return item.userName
                                                                        case 4:
                                                                            return item.wechat
                                                                        default:
                                                                            return item.mobile
                                                                    }
                                                                })()
                                                            }</td>
                                                        <td>{
                                                                (()=>{
                                                                    switch(sureType){
                                                                        case 0:
                                                                            return item.wechat
                                                                        case 1:
                                                                            return item.wechat
                                                                        case 3:
                                                                            return item.wechat
                                                                        case 4:
                                                                            return item.walletAddress
                                                                        default:
                                                                            return item.wechat
                                                                    }
                                                                })()
                                                            }</td>
                                                        {
                                                            sureType !== 4?
                                                            <td>{
                                                                _cooperType.map((name,index) =>{
                                                                        return (
                                                                            <span>
                                                                                <span>{name}</span><br/>
                                                                            </span>    
                                                                            
                                                                        )
                                                                    })
                                                                }
                                                            </td>
                                                            : null
                                                        }
                                                        <td>{item.createTime?moment(item.createTime).format(TIMEFORMAT_ss):'--'}</td>
                                                        <td>{item.memo}</td>
                                                        <td>
                                                            <Button type="primary" onClick={()=>this.leadModal(item)}>{item.status == 0?'领办':'已领办'}</Button>
                                                        </td>                                                           
                                                    </tr>
                                                )
                                                // :
                                                // (
                                                //     <tr key={index}>
                                                //         <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                //         <td>兼职邀请</td>
                                                //         <td>{item.name}</td>
                                                //         <td>{item.mobile}</td>
                                                //         <td>{item.wechat}</td>
                                                //         <td>{item.walletAddress}</td>
                                                //         <td>{item.createTime?moment(item.createTime).format(TIMEFORMAT_ss):'--'}</td>
                                                //         <td>{item.memo}</td>
                                                //         <td>
                                                //             <Button type="primary" onClick={()=>this.leadModal(item)}>{item.status == 0?'领办':'已领办'}</Button>
                                                //         </td>                                                           
                                                //     </tr>
                                                // )
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
                                                showQuickJumper />
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






























































