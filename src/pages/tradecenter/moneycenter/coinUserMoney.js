import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,MODALCAPITALCHARGE,MODALCAPITALDEDUCT,MODALCAPITALFREEZE,MODALCAPITALUNFREEZE,PAGRSIZE_OPTIONS,PAGESIZE_50,URLS } from '../../../conf'
import FundsTypeList from '../../common/select/fundsTypeList'
import ModalCapital from './modal/modalCapital'
import ModalTransfer from './modal/modaltransfer'
import GoogleCode from '../../common/modal/googleCode'
import { toThousands,pageLimit,tableScroll } from '../../../utils'
import SelectUserTypeList from '../../common/select/selectUserTypeList'
import { AsyncSelect } from '../../../components/select/asyncSelect'

import { DatePicker,Select,Modal, Button ,Tabs,Pagination,message,Table } from 'antd'
const { Column } = Table;
const Big = require('big.js')
const { COMMON_GETUSERTYPE } = URLS

export default class UserCapital extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            showHide:true,
            fundType:'2',
            userid:'',
            userName:'',
            moneyMin:'',
            moneyMax:'',
            freezMoneyMin:'',
            freezMoneyMax:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE_50,
            tableList:[],
            pageTotal:0,
            modalHtml:'',
            visible:false,
            title:'',
            strMoney:'',
            memo:'',
            width:'',
            googleCode:'',
            checkGoogle:'',
            totalMoneyMin:'',
            totalMoneyMax:'',
            limitBtn: [],
            check:'',
            googVisibal:false,
            item:{},
            type:'',
            pubalancesum:0,
            pufreezsum:0,
            totalMoney:0,
            strMoney:'',
            memo:'',
            accountid:'',
            accountType:'0',

        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.coinCharge = this.coinCharge.bind(this)
        this.deduct = this.deduct.bind(this)
        this.coinFreez = this.coinFreez.bind(this)
        this.UncoinFreez = this.UncoinFreez.bind(this)
        this.coinChargeModal = this.coinChargeModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.coinDeductModal = this.coinDeductModal.bind(this)
        this.coinFreezeModal = this.coinFreezeModal.bind(this)
        this.coinUnfreezeModal = this.coinUnfreezeModal.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }
    
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: [...pageLimit('userCapital',this.props.permissList),...pageLimit('walletUserCapital',this.props.permissList)]
        })
        
    }
    componentWillUnmount(){
     
    }
    //输入时 input 设置到 satte
    handleInputChange(event,check) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
 
    
    //资金类型 select
    handleChangeType(val){
        this.setState({
            fundType:val
        })
    }

    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
    }

    //table 列表请求
    requestTable(currIndex,currSize){
        const { fundType,userid,userName,moneyMin,moneyMax,freezMoneyMin,freezMoneyMax,pageIndex,pageSize,totalMoneyMin,totalMoneyMax,accountType } = this.state
        axios.post(DOMAIN_VIP+'/userCapital/query',qs.stringify({
            fundType:fundType,
            userId:userid,
            userName:userName,
            moneyMin:moneyMin,
            moneyMax:moneyMax,
            freezMoneyMin:freezMoneyMin,
            freezMoneyMax:freezMoneyMax,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize,
            totalMoneyMin,totalMoneyMax,
            accountType
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                Big.RM = 0;
                let tableList = result.data.list;

                tableList.map((item, index) => {
                    item.index = (result.data.currPage - 1) * result.data.pageSize + index + 1;
                    item.key = index;
                    item.totalAmount = new Big(item.balance).plus(item.freez)
                })
                this.setState({
                    tableList,
                    pageSize: result.data.pageSize,
                    pageTotal: result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
        axios.post(DOMAIN_VIP+'/userCapital/sum',qs.stringify({
            fundType:fundType,
            userId:userid,
            userName:userName,
            moneyMin:moneyMin,
            moneyMax:moneyMax,
            freezMoneyMin:freezMoneyMin,
            freezMoneyMax:freezMoneyMax,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize,
            totalMoneyMin,totalMoneyMax,
            accountType
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    pubalancesum:result.data[0]&&result.data[0].pubalancesum,
                    pufreezsum:result.data[0]&&result.data[0].pufreezsum,
                    totalMoney:result.data[0]&& result.data[0].allsum,
                })
            }else{
                message.warning(result.msg)
            }
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
    //重置按钮
    onResetState(){
        this.setState({
            fundType:'2',
            userid:'',
            userName:'',
            moneyMin:'',
            moneyMax:'',
            freezMoneyMin:'',
            freezMoneyMax:'',
            totalMoneyMin:'',
            totalMoneyMax:'',
            accountType:'0'
        })
    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible: false 
        })
    }
    //谷歌弹窗关闭
    onhandleCancel(){
        this.setState({
            googVisibal: false 
        })
    }
  
    // //google 验证弹窗
    modalGoogleCode(item,type,check){
        this.setState({
            googVisibal:true,
            item,
            type,
            check,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { item,type,check} = this.state
        const {googleCode,checkGoogle} = value
        let url =  check ?"/common/checkTwoGoogleCode":"/common/checkGoogleCode"
        axios.post(DOMAIN_VIP+url,qs.stringify({
            googleCode,checkGoogle
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(type == "doCharge"){
                    this.coinCharge(item)
                    console.log(item)
                }else if(type == "doDeduction"){
                    this.deduct(item)
                }else if(type == "doFreez"){
                    this.coinFreez(item)
                }else if(type == "unFreez"){
                    this.UncoinFreez(item)
                }else if (type == "tansfer") {
                    this.Transfer(item)
                }
                this.setState({
                    googVisibal: false
                })
                
            }else{
                message.warning(result.msg)
            }
        })
    }
    //充币
    coinChargeModal(item,type){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item,type,'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible:true,
            title:'系统充值',
            width:'700px',
            modalHtml:<ModalCapital item={item} type={MODALCAPITALCHARGE} handleInputChange={this.handleInputChange}/>
        })
    }
    
    //充币 按钮
    coinCharge(item){
        let self = this;
        const { fundstype,userid,username,fundstypename } = item
        const { strMoney,memo,fee } = this.state
        if(!strMoney){
            message.warning("请输入您要充值的数量")
            return false;
        }
        axios.post(DOMAIN_VIP + '/userCapital/doCharge',qs.stringify({
            fundType:fundstype,
            userId:userid,
            userName:username,
            fundTypeName:fundstypename,
            strMoney:strMoney,
            memo:memo,
            fee:fee
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                self.setState({
                    visible:false,
                    strMoney:'',
                    memo:'',
                    fee:''
                })
                message.success(result.msg)
                this.requestTable()
            }else{
                message.warning(result.msg)
            }
        })
    }
    //扣币弹窗
    coinDeductModal(item,type){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item,type,'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible:true,
            width:'700px',
            title:'系统扣除',
            modalHtml:<ModalCapital item={item} type={MODALCAPITALDEDUCT} handleInputChange={this.handleInputChange}/>
        })
    }
    //扣币
    deduct(item){
        const { fundstype,userid,username,fundstypename } = item
        const { strMoney,memo } = this.state
        if(!strMoney){
            message.warning("请输入您要扣除的数量")
            return false;
        }
        axios.post(DOMAIN_VIP+'/userCapital/doDeduction',qs.stringify({
            fundType:fundstype,
            userId:userid,
            userName:username,
            fundTypeName:fundstypename,
            strMoney:strMoney,
            memo:memo
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    strMoney:'',
                    memo:''
                })
                this.requestTable()
            }else{
                message.warning(result.msg)
            }
        })
    } 
    //冻结弹窗
    coinFreezeModal(item,type){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item,type,'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible:true,
            width:'700px',
            title:'系统冻结',
            modalHtml:<ModalCapital item={item} type={MODALCAPITALFREEZE} handleInputChange={this.handleInputChange}/>
        })
    }
    //冻结
    coinFreez(item){
        const { fundstype,userid,username,fundstypename } = item
        const { strMoney,memo } = this.state
        if(!strMoney){
            message.warning("请输入您要冻结的数量")
            return false;
        }
        axios.post(DOMAIN_VIP+'/userCapital/doFreez',qs.stringify({
            userName:username,
            userId:userid,
            fundTypeName:fundstypename,
            fundType:fundstype,
            freezMoney:strMoney,
            memo:memo
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    strMoney:'',
                    memo:''
                })
                this.requestTable()
            }else{
                message.warning(result.msg)
            }
        })
    }
    //解冻弹窗
    coinUnfreezeModal(item,type){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item,type,'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible:true,
            width:'700px',
            title:'系统解冻',
            modalHtml:<ModalCapital item={item} type={MODALCAPITALUNFREEZE} handleInputChange={this.handleInputChange}/>
        })
    }
    //解冻
    UncoinFreez(item){
        const { fundstype,userid,username,fundstypename } = item
        const { strMoney,memo } = this.state
        if(!strMoney){
            message.warning("请输入您要解冻的数量")
            return false;
        }
        axios.post(DOMAIN_VIP+'/userCapital/unFreez',qs.stringify({
            fundType:fundstype,
            userId:userid,
            userName:username,
            fundTypeName:fundstypename,
            freezMoney:strMoney,
            memo:memo
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    strMoney:'',
                    memo:''
                })
                this.requestTable()
            }else{
                message.warning(result.msg)
            }
        })
    }
    //划转资金弹窗
    coinTransferModal = (item,type) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more"  onClick={() => this.modalGoogleCode(item, type,'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible: true,
            width: '900px',
            title: '划转资金',
            modalHtml: <ModalTransfer handleInputChange={this.handleInputChange} from='币币账户' to='我的钱包' handleSelectChange = {this.handleSelectChange} item={item}/>
        })
    }
    //划转资金
    Transfer = (item) => {
        const { fundstype, userid,  } = item
        const {money,accountid} = this.state
        let self = this
        console.log(accountid)
        axios.post(DOMAIN_VIP + '/walletUserCapital/transfer', qs.stringify({
            fundsType: fundstype,
            from:'2',
            to: '1',
            userId: userid,
            amount: money,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success(result.msg)
                this.setState({
                    visible: false,
                    money: '',
                    accountid: ''
                })
                this.requestTable()
                console.log(this.state)
            } else {
                message.warning(result.msg)
            }
        })
    }
    handleSelectChange = (val) => {
        this.setState({
            accountid:val
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
      selectUser = user => {
        this.setState({
            accountType:user
        })
    }
    //用户类型
    selectUserType = (v,k) => {
        this.setState({[k]: v})
    }
    render(){
        const { googVisibal,width,showHide,fundType,pageTotal,tableList,pageIndex,pageSize,totalMoneyMin,totalMoneyMax,title,modalHtml,visible,userid,userName,moneyMin,moneyMax,freezMoneyMin,freezMoneyMax,limitBtn,pubalancesum, totalMoney, pufreezsum,accountType, } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 用户资金 > 币币用户资金
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList  fundsType={fundType} handleChange={this.handleChangeType} />
                                </div>
                                <AsyncSelect title='用户类型' paymod url={COMMON_GETUSERTYPE} value={accountType} onSelectChoose={v => this.selectUserType(v,'accountType')} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">总金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="totalMoneyMin" value={totalMoneyMin} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="totalMoneyMax" value={totalMoneyMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">可用金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="moneyMin" value={moneyMin} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="moneyMax" value={moneyMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">冻结金额：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="freezMoneyMin" value={freezMoneyMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="freezMoneyMax" value={freezMoneyMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-3 col-sm-3 col-xs-3 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button> 
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>  
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">                        
                            <div className="x_content">
                            <div className="table-responsive table-box">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    总资金：{toThousands(totalMoney,true) } &nbsp;&nbsp;&nbsp;
                                                    可用金额：{toThousands(pubalancesum,true)} &nbsp;&nbsp;&nbsp;
                                                    冻结保证金：{toThousands(pufreezsum,true) }
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>

                                    <Table
                                        dataSource={tableList}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{emptyText:'暂无数据'}}
                                        // scroll={pageSize != 10 ? { y: 500 } : {}}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            showSizeChanger: true,
                                            showQuickJumper: true,
                                            pageSizeOptions:PAGRSIZE_OPTIONS,
                                            defaultPageSize:PAGESIZE_50
                                        }}>
                                        <Column title='序号' dataIndex='index' key='index'render={(text)=>(
                                            <span>{text}</span>
                                        )} />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypeName' />
                                        <Column title='用户编号' dataIndex='userid' key='userId' />
                                        <Column title='用户类型' dataIndex='accountTypeName' key='accountTypeName' />
                                        <Column title='总金额' dataIndex='totalAmount' key='totalAmount'  className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='可用金额' dataIndex='balance' key='availableBalance'  className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='冻结金额' dataIndex='freez' key='amountFrozen'  className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                                        <Column title='操作' dataIndex='op' key='op' render={(text,record)=>{
                                            return (<div>
                                                {limitBtn.includes('transfer')?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinTransferModal(record,'tansfer')}>划转资金</a>:'' }
                                                {/* <a className="mar10" href="javascript:void(0)" onClick={() => this.coinChargeModal(record,'doCharge')}>充{record.fundstypename}</a>
                                                {limitBtn.indexOf('doCharge')>-1?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinChargeModal(record,'doCharge')}>充{record.fundstypename}</a>:''}
                                            {limitBtn.indexOf('doDeduction')>-1?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinDeductModal(record,'doDeduction')}>扣{record.fundstypename}</a>:''}
                                            {
                                               limitBtn.indexOf('doFreez')>-1?(record.balance>0&&<a className="mar10" href="javascript:void(0)" onClick={()=>this.coinFreezeModal(record,'doFreez')}>冻结可用资金</a>):''
                                             }
                                            {
                                               limitBtn.indexOf('unFreez')>-1?(record.freez>0&&<a className="mar10" href="javascript:void(0)" onClick={()=>this.coinUnfreezeModal(record,'unFreez')}>解冻冻结资金</a>):''
                                             } */}
                                            </div>)
                                        }} />
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width={width}
                >
                    {modalHtml}
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='BIBIUM'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                />
            </div>
        )
    }

}