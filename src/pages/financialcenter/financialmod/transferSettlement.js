// /**数据中心 》 资金中心 》 资金划转  */
// import React from 'react'
// import ReactDOM from 'react-dom'
// import axios from '../../../utils/fetch'
// import qs from 'qs'
// import moment from 'moment'
// import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT,SELECTWIDTH } from '../../../conf'
// import { Select,Modal,Button,Table,Pagination,message,DatePicker,Checkbox } from 'antd'
// import { toThousands,pageLimit } from '../../../utils'
// import SelectAType from '../select/selectAType'
// import GoogleCode from '../../common/modal/googleCode'
// import {tableScroll} from '../../../utils/index'
// const Option = Select.Option;
// const { MonthPicker, RangePicker, WeekPicker } = DatePicker;

// export default class TransferSettlement extends React.Component{
//     constructor(props){
//         super(props)
//         this.state = {
//             showHide:true,
//             visible:false,
//             googVisibal:false,
//             pageIndex:PAGEINDEX,
//             pageSize:PAGESIZE,
//             tableList:[],
//             pageTotal:0,
//             modalHtml:'',
//             dst:0,
//             src:0,
//             fundType:'0',
//             amountMin:'',
//             amountMax:'',
//             uid:'',
//             Stime:'',
//             Etime:'',
//             time:[],
//             loading:false,
//             width:'',
//             height:0,
//             tableScroll:{
//                 tableId:'TSFLT',
//                 x_panelId:'TSFLTx',
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
//         this.handleChangeSelect = this.handleChangeSelect.bind(this)
//         // this.setHeight = this.setHeight.bind(this)
//         this.getHeight = this.getHeight.bind(this)
//         this.sort = this.sort.bind(this)
//         this.upSort = this.upSort.bind(this)
//         this.downSort = this.downSort.bind(this)

//     }
//     componentDidMount(){
//         this.requestTable()
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
//             dst:'',
//             src:'',
//             amountMax:'',
//             amountMin:'',
//             Stime:'',
//             Etime:'',
//             time:[],
//             fundType:'',
//             uid:'',
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
//     //select选择框
//     handleChangeSelect(val,type){
//         if(type == 'dst'){//目的地
//             this.setState({
//                 dst:val
//             })
//         }else{//来源
//             this.setState({
//                 src:val
//             })
//         }
//     }
//     //table 请求
//     requestTable(currentIndex,currentSize){
//         const {dst,uid,fundType,pageIndex,pageSize,amountMax,amountMin,Stime,Etime,src,} = this.state
//         axios.post(DOMAIN_VIP+"/fundTransferLog/query",qs.stringify({
//             dst,uid,src,amountMax,amountMin,Stime,Etime,fundType,
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
//         axios.post(DOMAIN_VIP+"/fundTransferLog/sum",qs.stringify({
//             dst,uid,src,amountMax,amountMin,Stime,Etime,fundType,
//             pageIndex:currentIndex||pageIndex,
//             pageSize:currentSize||pageSize
//         })).then(res => {
//             const result = res.data;
//             if(result.code ==0 ){
//                 this.setState({
//                     fAmountSum:result.data[0].fAmountSum
//                 })
//             }else{
//                 message.warning(result.msg)
//             }
//         })
//     }
//     sort(type){
//         if(type=='up'){
//             this.setState({
//                 tableList:this.state.tableList.sort(this.upSort(this.state.tableList,'amount')),
//             })
//         }else{
//             this.setState({
//                 tableList:this.state.tableList.sort(this.downSort(this.state.tableList,'amount')),
//             })
//         }   
//     }
//     upSort(tableList,propertyName) {
//         //console.log(this.state.tableList)
//         //console.log(this.state.tableList[0][propertyName])
//         if ((typeof tableList[0][propertyName]) != "number") {
//             return (object1, object2) => {
//                 var value1 = object1[propertyName];
//                 var value2 = object2[propertyName];
//                 return value1.localeCompare(value2);
//             }
//         }
//         else {
//             return (object1, object2) => {
//                 var value1 = object1[propertyName];
//                 var value2 = object2[propertyName];
//                 return value1 - value2;
//             }
//         }
//     } 
//     downSort(tableList,propertyName) {
//         if ((typeof tableList[0][propertyName]) != "number") {
//             return (object1, object2) => {
//                 var value1 = object1[propertyName];
//                 var value2 = object2[propertyName];
//                 return value2.localeCompare(value1);
//             }
//         }
//         else {
//             return (object1, object2) => {
//                 var value1 = object1[propertyName];
//                 var value2 = object2[propertyName];
//                 return value2 - value1;
//             }
//         }
//     }
//     render(){
//         const {tableList,showHide,dst,uid,fundType,pageIndex,pageSize,pageTotal,amountMax,amountMin,Stime,Etime,src,fAmountSum,visible,googVisibal,time,width,height} = this.state
//         return(
//             <div className="right-con">
//                 <div className="page-title">
//                     当前位置：财务中心 > 账务管理 > 划转结算
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
//                                         <label className="col-sm-3 control-label">用户ID：</label>
//                                         <div className="col-sm-8">
//                                             <input type="text" className="form-control"  name="uid" value={uid} onChange={this.handleInputChange} />
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">目的地：</label>
//                                         <div className="col-sm-8">
//                                             <Select value={dst} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'dst')}>
//                                                 <Option value={0}>请选择</Option>
//                                                 <Option value={1}>钱包</Option>
//                                                 <Option value={2}>币币</Option>
//                                                 <Option value={3}>法币</Option>
//                                             </Select>
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">来源：</label>
//                                         <div className="col-sm-8">
//                                             <Select value={src} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'src')}>
//                                                 <Option value={0}>请选择</Option>
//                                                 <Option value={1}>钱包</Option>
//                                                 <Option value={2}>币币</Option>
//                                                 <Option value={3}>法币</Option>
//                                             </Select>
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">金额：</label>
//                                         <div className="col-sm-8 ">
//                                             <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="amountMin" value={amountMin} onChange={this.handleInputChange} /></div>
//                                             <div className="left line34">-</div>
//                                             <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="amountMax" value={amountMax} onChange={this.handleInputChange} /></div>
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">审核时间：</label>
//                                         <div className="col-sm-8">
//                                         <RangePicker 
//                                         style={{width:SELECTWIDTH,fontSize:'12px'}}
//                                             showTime={{
//                                                 defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
//                                             }}
//                                             format="YYYY-MM-DD HH:mm:ss"
//                                             placeholder={['Start Time', 'End Time']}
//                                             onChange={this.onChangeTime }
//                                             value={time}
//                                         />
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
//                                             <tr className="headings">
//                                                 <th colSpan="9" className="column-title">金额总数量:&nbsp;{fAmountSum}</th>
//                                             </tr>
//                                             <tr className="headings" >
//                                                 <th className="column-title">序号</th>
//                                                 <th className="column-title">资金类型</th>
//                                                 <th className="column-title">用户编号</th>
//                                                 <th className="column-title">用户名称</th>
//                                                 <th className="column-title">目的地</th>
//                                                 <th className="column-title">来源</th>
//                                                 <th className="column-title"><div style={{float:'left'}}>金额</div><div className='triangle_border'><a href='javascript:void(0);' className='triangle_border_up' onClick={()=>this.sort('up')}></a><a href='javascript:void(0);' className='triangle_border_down' onClick={()=>this.sort('down')} ></a></div></th>
//                                                 <th className="column-title">创建时间</th>
//                                                 {/* <th style={{width:'17px',padding:"0",margin:'0',border:'0'}}></th>                                             */}
//                                             </tr>
//                                         </thead>
//                                         <tbody>
//                                             {    
//                                                 tableList.length>0?tableList.map((item,index) => {
//                                                     return (
//                                                         <tr key={index} >
//                                                             {/* <td><CheckboxGroup Option={tableList} onChange={this.checkAll}/></td> */}
//                                                             <td>{(pageIndex-1)*pageSize+index+1}</td>
//                                                             <td>{item.fundName}</td>
//                                                             <td>{item.uid}</td>
//                                                             <td>{item.userName}</td>
//                                                             <td>{item.dstName}</td>
//                                                             <td>{item.srcName}</td>
//                                                             <td>{toThousands(item.amount)}</td>
//                                                             <td>{moment(item.time).format(TIMEFORMAT)}</td>                                             
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