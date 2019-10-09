// import React from 'react'
// import ReactDOM from 'react-dom'
// import axios from '../../../utils/fetch'
// import qs from 'qs'
// import moment from 'moment'
// import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT,SELECTWIDTH } from '../../../conf'
// import { Select,Modal,Button,Table,Pagination,message,DatePicker } from 'antd'
// import { toThousands,pageLimit } from '../../../utils'
// import SelectAType from '../select/selectAType'
// import GoogleCode from '../../common/modal/googleCode'
// import {tableScroll} from '../../../utils/index'
// const BigNumber = require('big.js')
// const confirm = Modal.confirm;
// const Option = Select.Option;
// const { Column} = Table
// const { MonthPicker, RangePicker, WeekPicker } = DatePicker;

// export default class UserCapitalSummary extends React.Component{
//     constructor(props){
//         super(props)
//         this.state = {
//             showHide:true,
//             visible:false,
//             googVisibal:false,
//             pageIndex:PAGEINDEX,
//             pageSize:PAGESIZE,
//             tableList:[],
//             userId:"",
//             fundType:'0',
//             pageTotal:0,
//             modalHtml:'',
//             loading:false,
//             width:'',
//             height:0,
//             tableScroll:{
//                 tableId:'USRCITLSMY',
//                 x_panelId:'USRCITLSMYX',
//                 defaultHeight:500,
//             }
//         }
//         this.handleInputChange = this.handleInputChange.bind(this)
//         this.onResetState = this.onResetState.bind(this)
//         this.requestTable = this.requestTable.bind(this)
//         this.inquireBtn = this.inquireBtn.bind(this)
//         this.clickHide = this.clickHide.bind(this)
//         this.changPageNum = this.changPageNum.bind(this)
//         this.onShowSizeChange = this.onShowSizeChange.bind(this)
//         this.handleCancel = this.handleCancel.bind(this)
//         this.onChangeTime = this.onChangeTime.bind(this)
//         this.handleChangeType = this.handleChangeType.bind(this)
//         //this.handleChangeSelect = this.handleChangeSelect.bind(this)
//         // this.setHeight = this.setHeight.bind(this)
//         this.getHeight = this.getHeight.bind(this)

//     }
//     componentDidMount(){
//         //this.requestTable()
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
//     //查询 按钮
//     inquireBtn(){
//         this.setState({
//             pageIndex:PAGEINDEX
//         })
//         this.requestTable(PAGEINDEX,PAGESIZE)
//     }
//     //点击收起
//     clickHide() {
//         let { showHide,xheight,pageSize } = this.state;
//         if(showHide&&pageSize>10){
//             this.setState({
//                 showHide: !showHide,
//                 height:xheight,
//             })
//         }else{
//             this.setState({
//                 showHide: !showHide,
//                 height:0
//             })
//         }
//         // this.setState({
//         //     showHide: !showHide,
//         // })
//     }

//     //重置状态
//     onResetState(){
//         this.setState({
//             userId:'',
//             fundType:'0',

//         })
//     }
//     //关闭弹窗
//     handleCancel(){
//         this.setState({
//             visible: false,
//             loading:false
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
//     //输入时 input 设置到 state
//     handleInputChange(event){
//         const target = event.target;
//         const value = target.type === 'checkbox' ? target.checked : target.value;
//         const name = target.name;
//         this.setState({
//             [name]: value
//         });
//     } 
//      //时间 select
//      onChangeTime(date, dateString){
//         this.setState({
//             Stime:dateString[0],
//             Etime:dateString[1],
//             time:date
//         })
//     }
//     //资金类型 select
//     handleChangeType(val){
//         this.setState({
//             fundType:val
//         })
//     }
//     // //select选择框
//     // handleChangeSelect(val,type){
//     //     if(type == 'dst'){//目的地
//     //         this.setState({
//     //             dst:val
//     //         })
//     //     }else{//来源
//     //         this.setState({
//     //             src:val
//     //         })
//     //     }
//     // }
//     //table 请求
//     requestTable(currentIndex,currentSize){
//         const {userId,fundType,pageIndex,pageSize} = this.state
//         axios.post(DOMAIN_VIP+"/userCapitalDetail/query",qs.stringify({
//             userId,fundType,
//             pageIndex:currentIndex||pageIndex,
//             pageSize:currentSize||pageSize
//         })).then(res => {
//             const result = res.data;
//             if(result.code ==0 ){
//                 this.setState({
//                     tableList:result.data.list,
//                     pageTotal:result.data.totalCount
//                 })
//             }else{
//                 message.warning(result.msg)
//             }
//         })
//         // axios.post(DOMAIN_VIP+"/fundTransferLog/sum",qs.stringify({
//         //     pageIndex:currentIndex||pageIndex,
//         //     pageSize:currentSize||pageSize
//         // })).then(res => {
//         //     const result = res.data;
//         //     if(result.code ==0 ){
//         //         this.setState({
//         //             fAmountSum:result.data[0].fAmountSum
//         //         })
//         //     }else{
//         //         message.warning(result.msg)
//         //     }
//         // })
//     }
//     render(){
//         const {tableList,showHide,pageIndex,pageSize,pageTotal,fundType,userId,fAmountSum,visible,googVisibal,width,height} = this.state
//         return(
//             <div className="right-con">
//                 <div className="page-title">
//                     当前位置：财务中心 > 用户资金 > 用户资金汇总
//                     <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
//                 </div>
//                 <div className="clearfix"></div>
//                 <div className="row">
//                     <div className="col-md-12 col-sm-12 col-xs-12">
//                         {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
//                             <div className="x_content">
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <SelectAType findsType={fundType} col='3' handleChange={this.handleChangeType}></SelectAType>                                   
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">用户编号：</label>
//                                         <div className="col-sm-8">
//                                             <input type="text" className="form-control"  name="userId" value={userId} onChange={this.handleInputChange} />
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-md-12 col-sm-12 col-xs-12 marTop">
//                                     <div className="right">
//                                         <Button type="primary" onClick={this.inquireBtn}>查询</Button>
//                                         <Button type="primary" onClick={this.onResetState}>重置</Button>
//                                     </div>
//                                 </div>
//                             </div>
//                         </div>}
//                         <div className="x_panel">    
//                             <div className="x_content">
//                                 <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto" >
//                                     <table  className="table table-striped jambo_table bulk_action table-linehei table_scroll ">
//                                         <thead>
//                                             {/* <tr className="headings">
//                                                 <th colSpan="8" className="column-title">金额总数量:&nbsp;{fAmountSum}</th>
//                                             </tr> */}
//                                             <tr className="headings" >
//                                                 <th className="column-title">序号</th>
//                                                 <th className="column-title">币种</th>
//                                                 <th className="column-title">用户编号</th>
//                                                 <th className="column-title">总资金</th>
//                                                 <th className="column-title">钱包账户</th>
//                                                 <th className="column-title">合约账户</th>
//                                                 <th className="column-title">币币账户</th>
//                                                 <th className="column-title">OTC账户</th>
//                                                 <th className="column-title">可用资金</th>
//                                                 <th className="column-title">冻结资金</th>                                           
//                                             </tr>
//                                         </thead>
//                                         <tbody>
//                                             {    
//                                                 tableList.length>0?tableList.map((item,index) => {
//                                                     BigNumber.RM = 0;
//                                                     return (
//                                                         <tr key={index} >
//                                                             <td>{(pageIndex-1)*pageSize+index+1}</td>
//                                                             <td>{item.fundstypename}</td>
//                                                             <td>{item.userId}</td>
//                                                             <td>{toThousands(new BigNumber(item.puwBalance).plus(item.puBalance).plus(item.puoBalance).plus(item.puFreez).plus(item.puofrozenfee).plus(item.puofrozentrade).plus(item.puwFreez).plus(item.puofrozenwithdraw).toFixed())}</td>
//                                                             <td>{toThousands(new BigNumber(item.puwBalance).plus(item.puwFreez).toFixed())}</td>
//                                                             <td>--</td>
//                                                             <td>{toThousands(new BigNumber(item.puBalance).plus(item.puFreez).toFixed())}</td>
//                                                             <td>{toThousands(new BigNumber(item.puoBalance).plus(item.puofrozenfee).plus(item.puofrozentrade).plus(item.puofrozenwithdraw).toFixed())}</td>
//                                                             <td>{toThousands(new BigNumber(item.puwBalance).plus(item.puBalance).plus(item.puoBalance).toFixed())}</td>
//                                                             <td>{toThousands(new BigNumber(item.puFreez).plus(item.puofrozenfee).plus(item.puofrozentrade).plus(item.puwFreez).plus(item.puofrozenwithdraw).toFixed())}</td>                                             
//                                                         </tr>
//                                                     )
//                                                 }):<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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
//                                                 onChange={this.changPageNum}
//                                                 showTotal={total => `总共 ${total} 条`}
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
//                     title={this.state.title}
//                     onOk={this.handleOk}
//                     onCancel={this.handleCancel}
//                     footer={this.footer}
//                     width={width}
//                 >
//                     {this.state.modalHtml}
//                 </Modal>
//                 <GoogleCode
//                     wrappedComponentRef={this.saveFormRef}
//                     check={this.state.check}
//                     handleInputChange = {this.handleInputChange}
//                     mid='TFS'
//                     visible={this.state.googVisibal}
//                     onCancel={this.onhandleCancel}
//                     onCreate={this.handleCreate}
//                 />
//             </div>
//         )
//     }
// }