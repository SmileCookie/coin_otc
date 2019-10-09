// import React from 'react'
// import axios from '../../../utils/fetch'
// import qs from 'qs'
// import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,MODALCAPITALCHARGE,MODALCAPITALDEDUCT,MODALCAPITALFREEZE,MODALCAPITALUNFREEZE } from '../../../conf'
// import SelectAType from '../select/selectAType'
// import ModalCapital from './modal/modalCapital'
// import { toThousands,pageLimit,tableScroll } from '../../../utils'
// import GoogleCode from '../../common/modal/googleCode'
// import {Modal, Button ,Pagination,message } from 'antd'
// const BigNumber = require('big.js')

// export default class UserCapitalOTC extends React.Component{

//     constructor(props) {
//         super(props)
//         this.state = {
//             showHide:true,
//             coinTypeId:'0',
//             userid:'',
//             userName:'',
//             moneyMin:'',
//             moneyMax:'',
//             freezMoneyMin:'',
//             freezMoneyMax:'',
//             pageIndex:PAGEINDEX,
//             pageSize:PAGESIZE,
//             tableList:[],
//             pageTotal:0,
//             modalHtml:'',
//             visible:false,
//             title:'',
//             strMoney:'',
//             memo:'',
//             width:'',
//             totalMoneyMin:'',
//             totalMoneyMax:'',
//             strFundsComm:'',
//             checkGoogle:'',
//             googleCode:'',
//             limitBtn: [],
//             googVisibal:false,
//             item:{},
//             type:'',
//             check:'',
//             height:0,
//             tableScroll:{
//                 tableId:'USCITOTC',
//                 x_panelId:'USCITOTCX',
//                 defaultHeight:500,
//             }
//         }
//         this.handleInputChange = this.handleInputChange.bind(this)
//         this.requestTable = this.requestTable.bind(this)
//         this.handleChangeType = this.handleChangeType.bind(this)
//         this.changPageNum = this.changPageNum.bind(this)
//         this.onShowSizeChange = this.onShowSizeChange.bind(this)
//         this.onResetState = this.onResetState.bind(this)
//         this.coinCharge = this.coinCharge.bind(this)
//         this.deduct = this.deduct.bind(this)
//         this.coinFreez = this.coinFreez.bind(this)
//         this.UncoinFreez = this.UncoinFreez.bind(this)
//         this.coinChargeModal = this.coinChargeModal.bind(this)
//         this.handleCancel = this.handleCancel.bind(this)
//         this.coinDeductModal = this.coinDeductModal.bind(this)
//         this.coinFreezeModal = this.coinFreezeModal.bind(this)
//         this.coinUnfreezeModal = this.coinUnfreezeModal.bind(this)
//         this.inquireBtn = this.inquireBtn.bind(this)
//         this.clickHide = this.clickHide.bind(this)
//         this.modalGoogleCode = this.modalGoogleCode.bind(this)
//         this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
//         this.handleCreate = this.handleCreate.bind(this)
//         this.saveFormRef = this.saveFormRef.bind(this)
//         this.onhandleCancel = this.onhandleCancel.bind(this)
//         this.getHeight = this.getHeight.bind(this)
//     }
    
//     componentDidMount(){
//         this.requestTable()
//         this.setState({
//             limitBtn: pageLimit('otcUserCapital', this.props.permissList)
//         })
//         tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
//     }
//     componentWillUnmount(){
//         tableScroll(`#${this.state.tableScroll.tableId}`)
//     }
//     getHeight(xheight){
//         this.setState({
//             xheight
//         })
//     }
//     //输入时 input 设置到 satte
//     handleInputChange(event,check) {
//         const target = event.target;
//         const value = target.type === 'checkbox' ? target.checked : target.value;
//         const name = target.name;
//         this.setState({
//             [name]: value
//         });
        
//     }
        
//     //资金类型 select
//     handleChangeType(val){
//         this.setState({
//             coinTypeId:val
//         })
//     }

//     //查询 按钮
//     inquireBtn(){
//         this.setState({
//             pageIndex:PAGEINDEX
//         },() => this.requestTable())
//     }

//     //table 列表请求
//     requestTable(currIndex,currSize){
//         const { coinTypeId,userid,userName,moneyMin,moneyMax,freezMoneyMin,freezMoneyMax,pageIndex,pageSize,totalMoneyMin,totalMoneyMax } = this.state
//         axios.post(DOMAIN_VIP+'/otcUserCapital/query',qs.stringify({
//             coinTypeId:coinTypeId,
//             userId:userid,
//             userName:userName,
//             moneyMin:moneyMin,
//             moneyMax:moneyMax,
//             freezMoneyMin:freezMoneyMin,
//             freezMoneyMax:freezMoneyMax,
//             pageIndex:currIndex||pageIndex,
//             pageSize:currSize||pageSize,
//             totalMoneyMin,totalMoneyMax
//         })).then(res => {
//             const result = res.data;
//             console.log(result.data)
//             if(result.code == 0){
//                 this.setState({
//                     tableList:result.data.list,
//                     pageTotal:result.data.totalCount
//                 })
//             }else{
//                 message.warning(result.msg)
//             }
//         })
//     }
//     //点击分页
//     changPageNum(page,pageSize){
//         this.requestTable(page,pageSize)
//         this.setState({
//             pageIndex:page,
//             pageSize:pageSize
//         })
//     }
//     //分页的 pagesize 改变时
//     onShowSizeChange(current,size){
//         this.requestTable(current,size)
//         this.setState({
//             pageIndex:current,
//             pageSize:size
//         })
//     }
//     //重置按钮
//     onResetState(){
//         this.setState({
//             coinTypeId:'',
//             userid:'',
//             userName:'',
//             moneyMin:'',
//             moneyMax:'',
//             freezMoneyMin:'',
//             freezMoneyMax:'',
//             totalMoneyMin:'',
//             totalMoneyMax:''
//         })
//     }
//     //关闭弹窗
//     handleCancel(){
//         this.setState({
//             visible: false 
//         })
//     }
 
//     //google 验证弹窗
//     modalGoogleCode(item,type,check){
//         this.setState({
//             googVisibal:true,
//             item,
//             type,
//             check,
//         })
//     }

//     //google 按钮
//     modalGoogleCodeBtn(value){
//         const { item,type,check } = this.state
//         const {googleCode,checkGoogle} = value
//         let url =  check ?"/common/checkTwoGoogleCode":"/common/checkGoogleCode"
//         axios.post(DOMAIN_VIP+url,qs.stringify({
//             googleCode,checkGoogle
//         })).then(res => {
//             const result = res.data
//             if(result.code == 0){
//                 if(type == "doCharge"){
//                     this.coinCharge(item)
//                 }else if(type == "doDeduction"){
//                     this.deduct(item)
//                 }else if(type == "doFreez"){
//                     this.coinFreez(item)
//                 }else if(type == "unFreez"){
//                     this.UncoinFreez(item)
//                 }
//                 this.setState({
//                     googVisibal:false
//                 })
                
//             }else{
//                 message.warning(result.msg)
//             }
//         })
//     }
//     //充币
//     coinChargeModal(item, type){
//         this.footer = [
//             <Button key="back" onClick={this.handleCancel}>取消</Button>,
//             <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item, type,'check')}>
//                 确认
//             </Button>,
//         ]
//         this.setState({
//             visible:true,
//             title:'系统充值',
//             width:'700px',
//             modalHtml:<ModalCapital item={item} type={MODALCAPITALCHARGE} handleInputChange={this.handleInputChange}/>
//         })
//     }
    
//     //充币 按钮
//     coinCharge(item){
//         let self = this;
//         const { cointypeid,userid,userName,fundstypename } = item
//         const { strMoney,memo,strFundsComm } = this.state
//         if(!strMoney){
//             message.warning("请输入您要充值的数量")
//             return false;
//         }
//         axios.post(DOMAIN_VIP + '/otcUserCapital/doCharge',qs.stringify({
//             coinTypeId:cointypeid,
//             userId:userid,
//             userName:userName,
//             strFundsComm:strFundsComm,
//             strMoney:strMoney,
//             memo:memo
//         })).then(res => {
//             const result = res.data;
//             if(result.code == 0){
//                 self.setState({
//                     visible:false,
//                     strMoney:'',
//                     memo:'',
//                     fee:''
//                 })
//                 message.success(result.msg)
//                 this.requestTable()
//             }else{
//                 message.warning(result.msg)
//             }
//         })
//     }
//     //扣币弹窗
//     coinDeductModal(item, type){
//         this.footer = [
//             <Button key="back" onClick={this.handleCancel}>取消</Button>,
//             <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item, type,'check')}>
//                 确认
//             </Button>,
//         ]
//         this.setState({
//             visible:true,
//             width:'700px',
//             title:'系统扣除',
//             modalHtml:<ModalCapital item={item} type={MODALCAPITALDEDUCT} handleInputChange={this.handleInputChange}/>
//         })
//     }
//     //扣币
//     deduct(item){
//         const { cointypeid,userid } = item
//         const { strMoney,memo } = this.state
//         if(!strMoney){
//             message.warning("请输入您要扣除的数量")
//             return false;
//         }
//         axios.post(DOMAIN_VIP+'/otcUserCapital/doDeduction',qs.stringify({
//             coinTypeId:cointypeid,
//             userId:userid,
//             strMoney:strMoney,
//             memo:memo
//         })).then(res => {
//             const result = res.data;
//             if(result.code == 0){
//                 message.success(result.msg)
//                 this.setState({
//                     visible:false,
//                     strMoney:'',
//                     memo:''
//                 })
//                 this.requestTable()
//             }else{
//                 message.warning(result.msg)
//             }
//         })
//     } 
//     //冻结弹窗
//     coinFreezeModal(item, type){
//         this.footer = [
//             <Button key="back" onClick={this.handleCancel}>取消</Button>,
//             <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item, type,'check')}>
//                 确认
//             </Button>,
//         ]
//         this.setState({
//             visible:true,
//             width:'700px',
//             title:'系统冻结',
//             modalHtml:<ModalCapital item={item} type={MODALCAPITALFREEZE} handleInputChange={this.handleInputChange}/>
//         })
//     }
//     //冻结
//     coinFreez(item){
//         const { cointypeid,userid } = item
//         const { strMoney,memo } = this.state
//         if(!strMoney){
//             message.warning("请输入您要冻结的数量")
//             return false;
//         }
//         axios.post(DOMAIN_VIP+'/otcUserCapital/doFreez',qs.stringify({
//             coinTypeId:coinTypeId,
//             userId:userId,
//             strMoney:strMoney,
//             memo:memo
//         })).then(res => {
//             const result = res.data;
//             if(result.code == 0){
//                 message.success(result.msg)
//                 this.setState({
//                     visible:false,
//                     strMoney:'',
//                     memo:''
//                 })
//                 this.requestTable()
//             }else{
//                 message.warning(result.msg)
//             }
//         })
//     }
//     //解冻弹窗
//     coinUnfreezeModal(item, type){
//         this.footer = [
//             <Button key="back" onClick={this.handleCancel}>取消</Button>,
//             <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item, type,'check')}>
//                 确认
//             </Button>,
//         ]
//         this.setState({
//             visible:true,
//             width:'700px',
//             title:'系统解冻',
//             modalHtml:<ModalCapital item={item} type={MODALCAPITALUNFREEZE} handleInputChange={this.handleInputChange}/>
//         })
//     }
//     //解冻
//     UncoinFreez(item){
//         const { cointypeid,userid } = item
//         const { strMoney,memo } = this.state
//         if(!strMoney){
//             message.warning("请输入您要解冻的数量")
//             return false;
//         }
//         axios.post(DOMAIN_VIP+'/otcUserCapital/unFreez',qs.stringify({
//             coinTypeId:cointypeid,
//             userId:userid,
//             strMoney:strMoney,
//             memo:memo
//         })).then(res => {
//             const result = res.data;
//             if(result.code == 0){
//                 message.success(result.msg)
//                 this.setState({
//                     visible:false,
//                     strMoney:'',
//                     memo:''
//                 })
//                 this.requestTable()
//             }else{
//                 message.warning(result.msg)
//             }
//         })
//     }

//     //点击收起
//     clickHide() {
//         let { showHide,xheight,pageSize } = this.state;
//             if(showHide&&pageSize>10){
//                 this.setState({
//                     showHide: !showHide,
//                     height:xheight,
//                 })
//             }else{
//                 this.setState({
//                     showHide: !showHide,
//                     height:0
//                 })
//             }
//             // this.setState({
//             //     showHide: !showHide,
//             // }
//     }
//     handleCreate(){
//         const form = this.formRef.props.form;
//         form.validateFields((err, values) => {
//           if (err) {
//             return;
//           }
//           form.resetFields();
//           this.modalGoogleCodeBtn(values)
//         });
//       }
//       saveFormRef(formRef){
//         this.formRef = formRef;
//       }
//         //谷歌弹窗关闭
//     onhandleCancel(){
//         this.setState({
//             googVisibal: false 
//         })
//     }
    
//     render(){
//         const { width,showHide,coinTypeId,pageTotal,tableList,pageIndex,pageSize,totalMoneyMin,totalMoneyMax,title,modalHtml,visible,userid,userName,moneyMin,moneyMax,freezMoneyMin,freezMoneyMax,limitBtn } = this.state
//         return(
//             <div className="right-con">
//                 <div className="page-title">
//                     当前位置：财务中心 > 用户资金 > 法币用户资金
//                     <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
//                 </div>
//                 <div className="clearfix"></div>
//                 <div className="row">
//                     <div className="col-md-12 col-sm-12 col-xs-12">
//                         {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
//                             <div className="x_content">
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <SelectAType findsType={coinTypeId} col='3' handleChange={this.handleChangeType}></SelectAType>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">用户编号：</label>
//                                         <div className="col-sm-8">
//                                             <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange} />
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">用户名：</label>
//                                         <div className="col-sm-8">
//                                             <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
//                                             <b className="icon-fuzzy">%</b>
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">总金额：</label>
//                                         <div className="col-sm-8 ">
//                                             <div className="left col-sm-5 sm-box">
//                                                 <input type="text" className="form-control" name="totalMoneyMin" value={totalMoneyMin} onChange={this.handleInputChange} />
//                                             </div>
//                                             <div className="left line34 pad10 col-sm-2">
//                                                 至
//                                             </div>
//                                             <div className="left col-sm-5 sm-box">
//                                                 <input type="text" className="form-control" name="totalMoneyMax" value={totalMoneyMax} onChange={this.handleInputChange} />
//                                             </div>
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">可用金额：</label>
//                                         <div className="col-sm-8 ">
//                                             <div className="left col-sm-5 sm-box">
//                                                 <input type="text" className="form-control" name="moneyMin" value={moneyMin} onChange={this.handleInputChange} />
//                                             </div>
//                                             <div className="left line34 pad10 col-sm-2">
//                                                 至
//                                             </div>
//                                             <div className="left col-sm-5 sm-box">
//                                                 <input type="text" className="form-control" name="moneyMax" value={moneyMax} onChange={this.handleInputChange} />
//                                             </div>
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">冻结金额：</label>
//                                         <div className="col-sm-8">
//                                             <div className="left col-sm-5 sm-box">
//                                                 <input type="text" className="form-control" name="freezMoneyMin" value={freezMoneyMin} onChange={this.handleInputChange}/>
//                                             </div>
//                                             <div className="left line34 pad10 col-sm-2">
//                                                 至
//                                             </div>
//                                             <div className="left col-sm-5 sm-box">
//                                                 <input type="text" className="form-control" name="freezMoneyMax" value={freezMoneyMax} onChange={this.handleInputChange} />
//                                             </div>
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-md-3 col-sm-3 col-xs-3 right">
//                                     <div className="right">
//                                         <Button type="primary" onClick={this.inquireBtn}>查询</Button> 
//                                         <Button type="primary" onClick={this.onResetState}>重置</Button>  
//                                     </div>
//                                 </div>
//                             </div>
//                         </div>}
//                         <div className="x_panel">                        
//                             <div className="x_content">
//                                 <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
//                                     <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
//                                         <thead>
//                                             <tr className="headings">
//                                                 <th className="column-title">序号</th>
//                                                 <th className="column-title">资金类型</th>
//                                                 <th className="column-title">用户编号</th>
//                                                 <th className="column-title">用户名</th>
//                                                 <th className="column-title">总数量</th>
//                                                 <th className="column-title">可用金额</th>
//                                                 <th className="column-title">冻结总金额</th>
//                                                 <th className="column-title">提现冻结金额</th>
//                                                 <th className="column-title">广告冻结</th>
//                                                 <th className="column-title">交易冻结</th>
//                                                 {/* <th className="column-title">操作</th>                                               */}
//                                             </tr>
//                                         </thead>
//                                         <tbody>
//                                             {
//                                                 tableList.length>0?
//                                                 tableList.map((item,index) => {
//                                                     BigNumber.RM = 0;
//                                                     return (
//                                                         <tr key={index}>
//                                                             <td>{(pageIndex-1)*pageSize+index+1}</td>
//                                                             <td>{item.coinTypeName}</td>                                                         
//                                                             <td>{item.userid}</td>
//                                                             <td>{item.username}</td>
//                                                             {/* <td>{toThousands(new BigNumber(item.balance).plus(item.freez).toFixed())}</td> */}
//                                                             <td>{toThousands(new BigNumber(item.balance).plus(item.freeznTotal).plus(item.frozenwithdraw).plus(item.frozenfee).plus(item.frozentrade).toFixed())}</td>
//                                                             <td>{toThousands(new BigNumber(item.balance).toFixed())}</td>
//                                                             <td>{toThousands(new BigNumber(item.freeznTotal).plus(item.frozenwithdraw).plus(item.frozenfee).plus(item.frozentrade).toFixed())}</td>
//                                                             <td>{toThousands(new BigNumber(item.frozenwithdraw).toFixed())}</td>
//                                                             <td>{toThousands(new BigNumber(item.frozenfee).toFixed())}</td>
//                                                             <td>{toThousands(new BigNumber(item.frozentrade).toFixed())}</td>
//                                                             {/* <td>
//                                                                 {limitBtn.indexOf('doCharge')>-1?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinChargeModal(item,'doCharge')}>充值{item.coinTypeName}</a>:''}
//                                                                 {limitBtn.indexOf('doDeduction')>-1? <a className="mar10" href="javascript:void(0)" onClick={() => this.coinDeductModal(item,'doDeduction')}>扣{item.coinTypeName}</a>:''}
//                                                                 {
//                                                                     limitBtn.indexOf('doFreez')>-1?(item.balance>0&&<a className="mar10" href="javascript:void(0)" onClick={()=>this.coinFreezeModal(item,'doFreez')}>冻结可用资金</a>):''
//                                                                 }
//                                                                 {
//                                                                     limitBtn.indexOf('unFreez')>-1?(new BigNumber(item.freeznTotal).plus(item.frozenwithdraw).plus(item.frozenfee).plus(item.frozentrade).toFixed()>0&&<a className="mar10" href="javascript:void(0)" onClick={()=>this.coinUnfreezeModal(item,'unFreez')}>解冻冻结资金</a>):''
//                                                                 }                                                             
//                                                             </td> */}

//                                                         </tr>
//                                                     )    
//                                                 })  
//                                                 :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
//                                             }
//                                         </tbody>
//                                     </table>
//                                 </div>
//                                 <div className="pagation-box">
//                                 {
//                                     pageTotal>0 && <Pagination
//                                                 size="small"
//                                                 current={pageIndex}
//                                                 total={pageTotal}
//                                                 showTotal={total => `总共 ${total} 条`}
//                                                 onChange={this.changPageNum}
//                                                 onShowSizeChange={this.onShowSizeChange}
//                                                 showSizeChanger
//                                                 showQuickJumper />
//                                 }
//                                 </div>
//                             </div>
//                         </div>
//                     </div>
//                 </div>
//                 <Modal
//                     visible={visible}
//                     title={title}
//                     onCancel={this.handleCancel}
//                     footer={this.footer}
//                     width={width}
//                 >
//                     {modalHtml}
//                 </Modal>
//                 <GoogleCode
//                  wrappedComponentRef={this.saveFormRef}
//                  check={this.state.check}
//                  handleInputChange = {this.handleInputChange}
//                  mid='UCO'
//                 visible={this.state.googVisibal}
//                  onCancel={this.onhandleCancel}
//                  onCreate={this.handleCreate}
//                 />  
//             </div>
//         )
//     }

// }






























































