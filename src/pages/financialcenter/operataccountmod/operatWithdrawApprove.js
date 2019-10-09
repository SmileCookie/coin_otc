import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import cookie from 'js-cookie'
import { Button,Select,Pagination,message,DatePicker,Table,Modal } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import FundsTypeList from '../../common/select/fundsTypeList'
import ModalOperatWithdrawApprove from './modal/modalOperatWithdrawApprove'
import ModalPlatformWithdraw from './modal/modalPlatformWithdraw'
import FeeTypeList from '../../common/select/feeTypeList'
import FeeDirectionList from '../../common/select/feeDirectionList'
import GoogleCode from '../../common/modal/googleCode'
import { toThousands,pageLimit } from '../../../utils'
const Big = require('big.js')
const { RangePicker} = DatePicker
const {Column} = Table
const Option = Select.Option
const confirm = Modal.confirm

export default class OperatWithdrawApprove extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            visible:false,
            modalHtml:'',
            title:'',
            width:'',
            tableSource:[],
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共 ${total} 条`,
                size:'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            time:[],
            accname :'',
            fundstype:'0',
            sponsorname:'',
            downloadaddress:'',
            approvername:'',
            downloadamountS:'',
            downloadamountE:"",
            createtimeS :'',//发起时间
            createtimeE:'',
            status:'',
            feetype:'',
            costdirection:'',
            reviewername:'',
            checktimeS:'',//审核时间
            checktimeE:'',
            checktime:[],
            recordtimeS:'',//记账时间
            recordtimeE:'',
            recordtime:[],
            jointimeS:'',//入账时间
            jointimeE:'',
            jointime:[],


            googVisibal:false,
            check:'',
            googleSpace:{},

            userId:cookie.get('userId'),
            userName:cookie.get('userName'),
            mcostdirection:1,
            mdownloadamount:'',
            mdownloadaddress:'',
            refeetype:'',
            tmp:'',
            refundstype:'',
            refundstypeName:'',
            limitAvailableAmount:0,//提现金额的最大的值
            verifyVisible:false,//审核
            recordVisible:false,//记录
            reviewVisible:false,//复核
            mfundstype:'0',
            mfeetype:9,
            maccname:'',
            appVerifyKey:'700100010199',//审核页面
            appRecordKey:'700100010207',//记录页面
            appReviewKey:'700100010205',//复核页面
            limitBtn:[]

        }
    }
    componentDidMount(){        
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('feeAccountCheck', this.props.permissList)
        })
    }
    componentWillUnmount(){

    }
    clickHide = () => {
        this.setState({
            showHide:!this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    resetState = () => {
        this.setState({
            time:[],
            accname :'',
            fundstype:'0',
            sponsorname:'',
            downloadaddress:'',
            approvername:'',
            downloadamountS:'',
            downloadamountE:"",
            createtimeS :'',
            createtimeE:'',
            status:'',
            reviewername:'',
            checktimeS:'',//审核时间
            checktimeE:'',
            checktime:[],
            recordtimeS:'',//记账时间
            recordtimeE:'',
            recordtime:[],
            jointimeS:'',//入账时间
            jointimeE:'',
            jointime:[],
        })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name] : value
        })
    }
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    selectFundsType = v => {
        this.setState({
            fundstype:v
        })
    }
    selectStatus = v => {
        this.setState({
            status:v
        })
    }
    //编辑弹框 -费用方向
    selectFeeDirect = v => {
        this.setState({
            mcostdirection:v
        })
    }
    //新增其他-弹框资金类型
    mselectFundsType = (v,limitAvailableAmount,maccname) => {
        this.setState({
            mfundstype:v,
            limitAvailableAmount,
            maccname,
        })
    }
    //新增其他-弹框费用类型
    mselectFeeType = v => {
        this.setState({
            mfeetype:v
        })
    }
    //筛选条件-费用方向
    selectFeeDirection = v => {
        this.setState({
            costdirection:v
        })
    }
    //费用类型
    selectFeeType = v => {
        this.setState({
            feetype:v
        })
    }
    //发起时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            createtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            createtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            time:date
        })
    }
    //审核时间控件
    onCheckTime = (date, dateString) => {
        this.setState({
            checktimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            checktimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            checktime:date
        })
    }
    //记账时间控件
    onRecordTime = (date, dateString) => {
        this.setState({
            recordtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            recordtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            recordtime:date
        })
    }
    //入账时间控件
    onJoinTime = (date, dateString) => {
        this.setState({
            jointimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            jointimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            jointime:date
        })
    }
    //控制审核按钮
    // 700100010199  运营提现审核  
    // 700100010205  运营提现复核
    // 700100010207  运营提现记录
    requestVisible = () =>{
        axios.get(DOMAIN_VIP+"/sys/menu/getHomePageInfo").then(res => {
            const result = res.data
            if(result.code == 0){
                for(let i=0;i<result.menuIdList.length;i++){
                    if(result.menuIdList[i] == '700100010199'){
                        this.setState({
                            verifyVisible:true
                        })
                    }else if(result.menuIdList[i] == '700100010205'){
                        this.setState({
                            reviewVisible:true
                        })
                    }else if(result.menuIdList[i] == '700100010207'){
                        this.setState({
                            recordVisible:true
                        })
                    }
                }
            }
            console.log(this.state)
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize,pagination,accname,fundstype,sponsorname,downloadaddress,downloadamountS,downloadamountE,createtimeS,createtimeE,status,feetype,approvername,costdirection,reviewername,
            checktimeS,checktimeE,recordtimeS,recordtimeE,jointimeS,jointimeE } = this.state
        axios.post(DOMAIN_VIP+'/feeAccountCheck/list',qs.stringify({
            fundstype,sponsorname,downloadaddress,downloadamountS,downloadamountE,createtimeS,createtimeE,status,feetype,approvername,costdirection,
            reviewername,checktimeS,checktimeE,recordtimeS,recordtimeE,jointimeS,jointimeE,
            menuid:this.props.appActiveKey,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource:tableSource,
                    pagination,
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    onChangePageNum = (pageIndex,pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    //判断输入框
    judge = (isTrue) => {
        const {mcostdirection,mdownloadaddress,mdownloadamount,tmp,limitAvailableAmount} = this.state
        // console.log(limitAvailableAmount)
        if(!mcostdirection){
            message.warning('请选择费用方向！')
            return false
        }else if(!mdownloadamount||mdownloadamount==0){
            message.warning('提现金额不能为空！')
            return false
        }else if(isTrue ? false : Number(mdownloadamount)-limitAvailableAmount>0){
            message.warning('提现金额不可以大于可用金额！')
            return false
        }else if(!/^[0-9]+([.]{1}[0-9]+){0,1}$/ig.test(mdownloadamount)){
            message.warning('提现金额只能是数字！')
            return false
        }else if(!mdownloadaddress){
            message.warning('提现地址不能为空！')
            return false
        }else if(!tmp){
            message.warning('备注不能为空！')
            return false
        }else{
            return true
        }
    }
    //公共审核弹框
    commonCheckModal = (item, type,strType) => {
        let self = this, mtitle;
        if(type== 0){
            mtitle = '您确定要拒绝吗？'
        }else if(type == 1){
            mtitle = '您确定要同意吗？'
        }else if(type == 3){
            mtitle = '您确定要记账复核吗？'
        }else if(type ==4){
            mtitle = '您确定要拒绝吗？'
        }
        Modal.confirm({
            title: mtitle,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(item,type,strType)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //0：拒绝   1：同意   2：选择匹配   3：记账复核  按钮
    commonCheckBtn = (item,googletype) => {
        const {userId,userName} = this.state
        let url;
        switch(googletype){
            case 0:
                url = '/feeAccountCheck/check0';
            break;
            case 1:
                url = '/feeAccountCheck/check1';
            break;
            case 2:
                url = '/feeAccountCheck/check2';
            break;
            case 3:
                url = '/feeAccountCheck/check3';
            break;
            case 4:
                url = '/feeAccountCheck/check4';
            break;
            default:
            break;
        }
        axios.post(DOMAIN_VIP+`/feeAccountCheck/check${googletype}`,qs.stringify({
            checkId:item.id,choiceId:item.choiceid||'',operationType:googletype,
            approverid:userId,
            approvername:userName
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                // message.success(result.msg)
                if(result.state==2){
                    message.warning('提现金额不可以大于可用金额！')
                }
                this.requestTable()
            }else{
                message.warning(result.msg);
            }
        })
    }
    //弹框 记账选择  记账查看弹框
    onBookModal = (item,type,mtitle) => {
        // let mtitle = type==1?'运营账户提现记账':'记账查看'
        this.footer= [
            // <Button key="submit" type="more" onClick={this.handleCancel}>保存</Button>,
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        this.setState({
            visible:true,
            title:mtitle,
            width:'1200px',
            modalHtml:<ModalOperatWithdrawApprove {...this.state} requestTable={this.requestTable} handleCancel={this.handleCancel} item={item} type={type}/>
        })
    }
    //编辑弹框前要请求的接口
    onPrevEditModalDo = () => {
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP + '/feeAccountCheck/edit').then(res => {
                const result = res.data
                if(result.code == 0){
                    resolve()
                }else{
                    reject()
                    message.warning(result.msg)
                }
            })
        }).catch(err => console.log(err))
    }
    //编辑弹框
    onEditModal = (item,type) => {
        Big.RM = 0;
        this.onPrevEditModalDo().then(()=>{
            this.footer= [
                <Button key="submit" type="more" onClick={()=>this.onEditModalBtn(item,1,'insert')}>提交</Button>,
                <Button key="save" type="more" onClick={()=>this.onEditModalBtn(item,type,'insert')}>保存</Button>,
                <Button key="back" onClick={this.handleCancel}>取消</Button>
            ]
            axios.post(DOMAIN_VIP+'/feeAccountCheck/balance',qs.stringify({
                feetype:item.feetype,
                type:item.fundstype
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    // item.availableAmount = item.feetype == 2 ? result.operationData.currentamount - result.operationData.freezeamount : result.data.availableamount;//feetype ==2 是运营账户
                    switch(item.feetype){
                        case 2:
                            item.availableAmount = new Big(result.operationData.currentamount).minus(result.operationData.freezeamount);
                            break;
                        case 9:
                            item.availableAmount = result.operationData.currentamount;
                            break;
                        default:
                            item.availableAmount = result.data.availableamount;
                    }
                    this.setState({
                        visible:true,
                        title:"编辑",
                        width:'700px',
                        refundstype:item.fundstype||'',
                        modalHtml:<ModalPlatformWithdraw  editItem={item} item={item} selectFeeDirect={this.selectFeeDirect} userName={this.state.userName}  handleInputChange={this.handleInputChange} />,
                        mcostdirection:item.costdirection||1,
                        mdownloadamount:item.downloadamount||'',
                        mdownloadaddress:item.downloadaddress||'',
                        limitAvailableAmount:item.availableAmount||0,
                        refeetype:item.feetype||'',
                        tmp:item.tmp||'',
                        reaccname:'',
                        refundstypeName:item.fundstypename||''
                    })
                }else{
                    message.warning(result.msg);
                }
            })
        })       
    }
    //编辑&&提交 弹框按钮
    onEditModalBtn = (googleSpace,type,googleStrType) => {
        const { reaccname,id,mcostdirection,mdownloadaddress,mdownloadamount,tmp,refeetype,refundstype,userId,userName,refundstypeName,mfeetype,mfundstype,
        maccname } = this.state
        
        switch(googleStrType){            
            case 'insertSubmit':                 // --直接提交按钮
                // this.judgeAmount(googleSpace.feetype,googleSpace.id)
                let nurl = type == 3 ? '/feeAccountCheck/applyDelete' : '/feeAccountCheck/applySubmit'
                axios.post(DOMAIN_VIP+nurl,qs.stringify({
                    // costdirection:mcostdirection,downloadaddress:mdownloadaddress,
                    // downloadamount:mdownloadamount,tmp,feetype:refeetype,fundstype:refundstype,
                    // sponsorid:userId,sponsorname:userName,fundstypename:refundstypeName,
                    operationType:type,checkId:googleSpace.id
                })).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        // this.setState({
                        //     visible:false
                        // })
                        if(result.state==2){
                            message.warning('提现金额不可以大于可用金额！')
                        }
                        this.requestTable()
                    }else{
                        message.warning(result.msg);
                    }
                })
                break;
            case 'insert':        
                //编辑 弹框 弹框-提交&&保存按钮
                if(!this.judge()) return ; //judge返回false，则return false
                let url = type === 1 ? '/feeAccountCheck/applySubmit' : '/feeAccountCheck/applyUpdate'
                axios.post(DOMAIN_VIP+url,qs.stringify({              
                    costdirection:mcostdirection,downloadaddress:mdownloadaddress,
                    downloadamount:mdownloadamount,tmp,feetype:refeetype,fundstype:refundstype,
                    sponsorid:userId,sponsorname:userName,operationType:type,checkId:googleSpace.id,id:googleSpace.id
                })).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        this.setState({
                            visible:false
                        })
                        this.requestTable()
                    }else{
                        message.warning(result.msg);
                    }
                })
                break;
            case 'addOther':             //新增其他 弹框 弹框-提交&&保存按钮
                // console.log(googleSpace,type,googleStrType)
                let curl = type === 1 ? '/feeAccountCheck/applySubmit' : '/feeAccountCheck/applyInsert'
                if(mfundstype == '0'){
                    message.warning('资金类型不能为空！')
                    return false;
                }
                if(!this.judge(true)) return ; //judge返回false，则return false
                axios.post(DOMAIN_VIP+curl,qs.stringify({              
                    costdirection: mcostdirection, downloadaddress:mdownloadaddress,
                    downloadamount:mdownloadamount,tmp,feetype:mfeetype,fundstype:mfundstype,
                    sponsorid:userId,sponsorname:userName,operationType:type,accname:maccname
                })).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        this.setState({
                            visible:false
                        })
                        this.requestTable()
                    }else{
                        message.warning(result.msg);
                    }
                })
                break;
            default:
                break;
        }
    }
    //提交弹框
    onSubmitModal = (item,type,strType) => {
        let self = this, mtitle;
        if(type == 1){
            mtitle = '确定要提交吗？'
        }else if(type == 3){
            mtitle = '确定要删除吗？'
        }
        Modal.confirm({
            title: mtitle,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(item,type,strType)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    // //直接提交按钮 判断提现金额
    // judgeAmount = (feetype,accid) => {
    //     axios.post(DOMAIN_VIP+'/feeAccountCheck/balance',qs.stringify({              
    //         feetype,accid
    //     })).then(res => {
    //         const result = res.data;
    //         if(result.code == 0){

    //         }else{
    //             message.warning(result.msg);
    //         }
    //     })
    // }
    //google验证弹窗
    modalGoogleCode = (item,googletype,googleStrType) => {
        this.setState({
            googVisibal:true,
            googleSpace:item,
            googletype,
            googleStrType:googleStrType||''
        })
    }
    //google 按钮
    modalGoogleCodeBtn = value => {
        const { googleSpace,googletype,googleStrType } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                })
                // console.log(googletype)
                // console.log(googleStrType)
                switch(googleStrType){
                    case 'check':
                        this.commonCheckBtn(googleSpace,googletype)
                        break;
                    case 'insert':
                        this.onEditModalBtn(googleSpace,googletype,googleStrType)
                        break;
                    case 'insertSubmit':
                        this.onEditModalBtn(googleSpace,googletype,googleStrType)
                        break;
                    default:
                        break;                    
                }
                // if(googletype==0||googletype==1||googletype==3){

                // }else if(googletype==2){
                // }
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCreate = () => {
        const form = this.formRef.props.form;
        form.validateFields((err, valus) => {
            if(err){
                return;
            }
            form.resetFields();
            this.modalGoogleCodeBtn(valus)
        })
    }
    saveFormRef = formRef => {
        this.formRef = formRef
    }
    //google弹窗关闭
    onhandleCancel = () => {
        this.setState({
            googVisibal:false
        })
    }
    //新增其他
    addOther = () => {
        this.footer= [
            <Button key="submit" type="more" onClick={()=>this.onEditModalBtn('', 1, 'addOther')}>提交</Button>,
            <Button key="save" type="more" onClick={()=>this.onEditModalBtn('',0, 'addOther')}>保存</Button>,
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]

        axios.post(DOMAIN_VIP + '/feeAccountCheck/add').then(res => {
            const result = res.data
            if(result.code == 0){

                // axios.post(DOMAIN_VIP+'/feeAccountCheck/balance',qs.stringify({            
                //     feetype:2,
                // })).then(res => {
                //     const result = res.data;
                //     if(result.code == 0){
                        // item.availableAmount = item.feetype == 2 ? result.operationData.currentamount - result.operationData.freezeamount : result.data.availableamount;//feetype ==2 是运营账户
                        this.setState({
                            visible:true,
                            title:"新增",
                            width:'700px',
                            // refundstype:item.fundstype||'',
                            modalHtml:<ModalPlatformWithdraw mfeetype={this.state.mfeetype} selectFeeDirect={this.selectFeeDirect} userName={this.state.userName} mselectFeeType={this.mselectFeeType}  handleInputChange={this.handleInputChange} mselectFundsType={this.mselectFundsType} isHidden={true} />,
                            mcostdirection:1,
                            mdownloadamount:'',
                            mdownloadaddress:'',
                            limitAvailableAmount:0,
                            // refeetype:item.feetype||'',
                            tmp:'',
                            mfundstype:'0',
                            mfeetype:9,
                            maccname:'',
                            // refundstypeName:item.fundstypename||''
                        })
            //         }else{
            //             message.warning(result.msg);
            //         }
            //     })
            }else{
                message.warning(result.msg)
            }
        })
    }
    render(){
        const { showHide,tableSource,pagination,visible,modalHtml,width,title,time,accname,fundstype,sponsorname,downloadaddress,downloadamountS,downloadamountE,status,feetype,approvername,check,googVisibal,
        // verifyVisible,recordVisible,reviewVisible,
        costdirection,reviewername,appVerifyKey,appRecordKey,appReviewKey,checktime,jointime,recordtime,limitBtn } = this.state
        // verifyVisible:false,//审核
        // recordVisible:false,//记录
        // reviewVisible:false,//复核
        let appActiveKey = this.props.appActiveKey;
        let currentPlace = '',scroll_X = {}, tableCol = 0;
        let verifyVisible = false,recordVisible = false, reviewVisible = false;
        switch(appActiveKey){
            case appVerifyKey:
                currentPlace = '运营提现审核';
                scroll_X.x = 1730;
                tableCol = 14;
                verifyVisible = true;
                break;
            case appRecordKey:
                currentPlace = '运营提现记录';
                document.body.clientWidth > 1600 ? scroll_X: scroll_X.x = 1550;
                tableCol = 13;
                recordVisible =  true;
                break;
            case appReviewKey:
                currentPlace = '运营提现复核';
                scroll_X.x = 1730;
                tableCol = 14;
                reviewVisible = true;
                break;
            default:
                currentPlace = '运营提现审核'; 
                verifyVisible = true;         
                break;
        }
        return(
            <div className="right-con" style={{ marginTop: 0 }}>
                <div className='page-title' style={{position: 'relative',paddingTop: '0px',top:0}}>
                    当前位置：财务管理 > 内部提现管理 > {currentPlace}
                    <i style={{ right: '40px', top: '-13px' }} className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <FundsTypeList title='资金类型' fundsType={fundstype} handleChange={this.selectFundsType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FeeTypeList title='费用类型' feeType={feetype} handleChange={this.selectFeeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FeeDirectionList title='费用方向' col={3} feeDirection={costdirection} handleChange={this.selectFeeDirection} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发起人:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='sponsorname' className="form-control" value={sponsorname} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">审核人:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='approvername' className="form-control" value={approvername} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">复核人:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='reviewername' className="form-control" value={reviewername} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">提现地址:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='downloadaddress' className="form-control" value={downloadaddress} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">提现金额:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='downloadamountS' value={downloadamountS} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='downloadamountE' value={downloadamountE} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">状态:</label>
                                        <div className="col-sm-8">
                                        <Select value={status} style={{ width:SELECTWIDTH }} onChange={this.selectStatus} >
                                            <Option key='' value={''}>请选择</Option>
                                            <Option key='0' value={0}>默认</Option>
                                            <Option key='1' value={1}>已保存</Option>
                                            <Option key='2' value={2}>已提交</Option>
                                            <Option key='3' value={3}>审批中</Option>
                                            <Option key='4' value={4}>同意</Option>
                                            <Option key='5' value={5}>拒绝</Option>
                                            <Option key='6' value={6}>未记账</Option>
                                            <Option key='7' value={7}>已记账</Option>
                                            <Option key='8' value={8}>已入账</Option>
                                        </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发起时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker 
                                                showTime={{
                                                    defaultValue:[moment('00:00:00','HH:mm,ss'),moment('23:59:59','HH:mm,ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onChangeCheckTime}
                                                value={time}/>
                                        </div>
                                    </div>
                                </div>
                                {appActiveKey==appVerifyKey&&<div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">审核时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker 
                                                showTime={{
                                                    defaultValue:[moment('00:00:00','HH:mm,ss'),moment('23:59:59','HH:mm,ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onCheckTime}
                                                value={checktime}/>
                                        </div>
                                    </div>
                                </div>}
                                {
                                    appActiveKey==appRecordKey&&<div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">记账时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker 
                                                showTime={{
                                                    defaultValue:[moment('00:00:00','HH:mm,ss'),moment('23:59:59','HH:mm,ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onRecordTime}
                                                value={recordtime}/>
                                        </div>
                                    </div>
                                </div>
                                }
                                {appActiveKey==appReviewKey&&<div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">入账时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker 
                                                showTime={{
                                                    defaultValue:[moment('00:00:00','HH:mm,ss'),moment('23:59:59','HH:mm,ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onJoinTime}
                                                value={jointime}/>
                                        </div>
                                    </div>
                                </div>}
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        {recordVisible&&limitBtn.includes("add")&&<Button type="primary" onClick={this.addOther}>新增其他</Button>}
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className={`table-responsive table-rap-last table-rap-10 table-rap-${tableCol}`}>
                                    <Table dataSource={tableSource} bordered pagination={pagination} locale={{emptyText:'暂无数据'}} scroll={{x:2100}}>
                                        <Column title='序号' dataIndex='index' key='index'  />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename'  />
                                        <Column title='费用类型' dataIndex='feetype' key='feetype' render={(text)=>{
                                            switch(text){
                                                case 1:
                                                    return '平台手续费'
                                                case 2:
                                                    return '平台运营'
                                                case 3:
                                                    return '保险基金'
                                                case 9:
                                                    return '平台其他'
                                                default:
                                                    return '--'
                                            }
                                        }} />
                                        <Column title='账户名称' dataIndex='accname' key='accname' />
                                        <Column title='费用方向' dataIndex='costdirection' key='costdirection' render={(text)=>{
                                            switch(text){
                                                case 1:
                                                    return '冷到其他'
                                                case 2:
                                                    return '热充到其他'
                                                case 3:
                                                    return '热提到其他'
                                                default:
                                                    return '--'
                                            }
                                        }} />
                                        <Column className='moneyGreen' title='提现金额' dataIndex='downloadamount' key='downloadamount' render={(text)=>toThousands(text,true)} />
                                        <Column title='提现发起人' dataIndex='sponsorname' key='sponsorname' />
                                        <Column title='提现审批人' dataIndex='approvername' key='approvername' />
                                        <Column title='提现复核人' dataIndex='reviewername' key='reviewername' />
                                        <Column className='min_153px wid150' title='提现地址' dataIndex='downloadaddress' key='downloadaddress' />
                                        <Column title='状态' dataIndex='status' key='status' render={(text)=>{
                                            switch(text){
                                                case 0:
                                                    return '默认';
                                                case 1:
                                                    return '已保存';
                                                 case 2:
                                                    return '已提交';
                                                case 3:
                                                    return '待审核' ;
                                                case 4:
                                                    return '同意';
                                                case 5:
                                                    return '拒绝';
                                                case 6:
                                                    return '未记账';
                                                case 7:
                                                    return '待复核';
                                                case 8:
                                                    return '已入账';
                                                default:
                                                    return '--';
                                            }
                                        }}/>
                                        {appActiveKey==appRecordKey?
                                        <Column title='记账时间'  width='160px' dataIndex='recordtime' key='appRecordKey' render={(text)=>text?moment(text).format(TIMEFORMAT_ss):'--'} />
                                        :
                                        // <Column title='发起时间'  width='160px' dataIndex='createtime' key='createtime' render={(text)=>text?moment(text).format(TIMEFORMAT_ss):'--'} />
                                        ''
                                        }
                                        <Column title={appActiveKey==appRecordKey?'提现时间':'发起时间'}  width='160px' dataIndex='createtime' key='createtime' render={(text)=>text?moment(text).format(TIMEFORMAT_ss):'--'} />
                                        {appActiveKey==appVerifyKey&&<Column title='审核时间'  width='160px' dataIndex='checktime' key='appVerifyKey' render={(text)=>text?moment(text).format(TIMEFORMAT_ss):'--'} />}
                                        {appActiveKey==appReviewKey&&<Column title='入账时间'  width='160px' dataIndex='jointime' key='appReviewKey' render={(text)=>text?moment(text).format(TIMEFORMAT_ss):'--'} />}
                                        <Column title='操作'  className='min_153px wid150' dataIndex='status' key='action' render={(text,record)=>{
                                            switch(text){
                                                case 0:
                                                    return '';
                                                case 1:
                                                    return (<span>
                                                            {limitBtn.includes('edit')?<a className="mar10" onClick={()=>this.onEditModal(record, 2, 'insert')} href='javascript:void(0);' >编辑</a>:''}
                                                            {limitBtn.includes("applySubmit")&&<a className="mar10" href='javascript:void(0);' onClick={()=>this.onSubmitModal(record, 1, 'insertSubmit')}>提交</a>}
                                                            {limitBtn.includes("applyDelete")&&<a href='javascript:void(0);' onClick={()=>this.onSubmitModal(record, 3, 'insertSubmit')}>删除</a>}
                                                        </span>);
                                                 case 2:
                                                    return '';
                                                case 3:
                                                    return  (<span>
                                                            {recordVisible?'': limitBtn.includes("check1")&&<a className="mar10" href='javascript:void(0);' onClick={()=>this.commonCheckModal(record,1,'check')} >同意</a>}
                                                            {recordVisible?'': limitBtn.includes("check0")&&<a href='javascript:void(0);' onClick={()=>this.commonCheckModal(record,0,'check')} >拒绝</a>}
                                                        </span>);
                                                case 4:
                                                    return '';
                                                case 5:
                                                    return '';
                                                case 6:
                                                    return  (<span>
                                                            {limitBtn.includes("choice")&&<a className="mar10" onClick={()=>this.onBookModal(record,1,'运营账户提现记账')} href='javascript:void(0);' >记账选择</a>}
                                                        </span>);
                                                case 7:
                                                    return (
                                                        <span>
                                                            {limitBtn.includes("view")&&<a className="mar10" onClick={()=>this.onBookModal(record,2,'记账查看')} href='javascript:void(0);' >记账查看</a>}
                                                            {recordVisible?'': limitBtn.includes("check3")&&<a className="mar10" href='javascript:void(0);' onClick={()=>this.commonCheckModal(record, 3, 'check')} >记账复核</a>}
                                                            {recordVisible?'': limitBtn.includes("check4")&&<a href='javascript:void(0);' onClick={()=>this.commonCheckModal(record, 4, 'check')} >复核拒绝</a>}
                                                        </span>) ;
                                                case 8:
                                                    return (<span>{limitBtn.includes("view")&&<a className="mar10" onClick={()=>this.onBookModal(record,2,'入账信息')} href='javascript:void(0);' >入账信息</a>}</span>);
                                                default:
                                                    return '--';
                                            }
                                        }} />
                                        <Column className='min_153px wid150' title='备注' dataIndex='tmp' key='tmp' />
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    width={width}
                    title={title}
                    onOk={this.handleOk}
                    footer={this.footer}
                    onCancel={this.handleCancel}
                >
                    {modalHtml}
                </Modal>
                <GoogleCode 
                wrappedComponentRef={this.saveFormRef}
                // check={check}
                handleInputChange={this.handleInputChange}
                mid='OTWA'
                visible={googVisibal}
                onCancel={this.onhandleCancel}
                onCreate={this.handleCreate}/>
            </div>
        )
    }
}