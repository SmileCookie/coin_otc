// /**区块钱包对账 */
// import React from 'react'
// import axios from '../../../utils/fetch'
// import qs from 'qs'
// import moment from 'moment'
// import { PAGEINDEX, DOMAIN_VIP, SELECTWIDTH,TIMEFORMAT_ss } from '../../../conf'
// import { Button, DatePicker, Select, Table } from 'antd'
// import { toThousands } from '../../../utils'
// const { RangePicker } = DatePicker;
// const Big = require('big.js')
// const Option = Select.Option;
// const { Column } = Table;

// export default class Wallet extends React.Component {
//     constructor(props) {
//         super(props)
//         this.state = {
//             showHide: true,
//             accountType: [<Option key='0' value='0'>请选择</Option>],
//             fundsType: "0",
//             tableList: [],
//             pageIndex: PAGEINDEX,
//             pageSize: '20',
//             pageTotal: 0,
//             begin: "",
//             end: "",
//             time: null,
//             moneyTypeInterface: DOMAIN_VIP + '/common/queryAttr',
//             tableDataInterface: DOMAIN_VIP + '/walletaccount/query'
//         }
//         this.clickHide = this.clickHide.bind(this);
//         this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
//         this.requestList = this.requestList.bind(this);
//         this.requestTable = this.requestTable.bind(this);
//         this.handleChangeType = this.handleChangeType.bind(this);
//         this.onResetState = this.onResetState.bind(this);
//         this.changPageNum = this.changPageNum.bind(this);
//         this.onShowSizeChange = this.onShowSizeChange.bind(this);
//     }
//     componentDidMount() {
//         this.requestTable()
//         this.requestList()
//     }
//     inquiry = () => {
//         this.setState({
//             pageIndex:PAGEINDEX,
//         },()=>this.requestTable())       
//     }
//     //时间控件
//     onChangeCheckTime(date, dateString) {
//         this.setState({
//             begin: dateString[0],
//             end: dateString[1],
//             time: date
//         })
//     }
//     //点击分页
//     changPageNum(page,pageSize) {

//         this.setState({
//             pageIndex: page
//         }, () => this.requestTable(page,pageSize))

//     }
//     //分页的 pagesize 改变时
//     onShowSizeChange(current, size) {
//         this.setState({
//             pageIndex: current,
//             pageSize: size
//         }, () => this.requestTable(current,size))
//     }
//     //点击收起
//     clickHide() {
//         let { showHide } = this.state;
//         this.setState({
//             showHide: !showHide
//         })
//     }
//     handleChangeType(value) {
//         this.setState({
//             fundsType: value
//         })
//     }
//     onResetState() {
//         this.setState({
//             fundsType: "0",
//             begin: "",
//             end: "",
//             time: null,
//         })
//     }
//     requestList() {
//         const { moneyTypeInterface } = this.state;
//         axios.get(moneyTypeInterface).then(res => {
//             const result = res.data;
//             let accountTypeArr = this.state.accountType;
//             if (result.code == 0) {
//                 for (let i = 0; i < result.data.length; i++) {
//                     accountTypeArr.push(<Option key={result.data[i].paracode} value={result.data[i].paracode}>{result.data[i].paravalue}</Option>)
//                 }
//                 this.setState({
//                     accountType: accountTypeArr
//                 })
//             }
//         })
//     }
//     requestTable(currIndex, currSize) {
//         const { fundsType, begin, end, pageIndex, pageSize, tableDataInterface } = this.state
//         axios.post(tableDataInterface, qs.stringify({
//             begin,
//             end,
//             fundsType,
//             pageIndex: currIndex || pageIndex,
//             pageSize: currSize || pageSize
//         })).then(res => {
//             const result = res.data;
//             if (result.code == 0) {
//                 Big.RM = 0;
//                 let tableList = result.data.list;
//                 tableList.map((item, index) => {
//                     item.index = (result.data.currPage-1)*result.data.pageSize+index+1;
//                     item.key = item.id;
//                     item.key = index;
//                     for (let key in item) {
//                         item.index = index + 1;
//                         //热提查询余额
//                         if (key == "hotextractbalance") {
//                             item["queryhotextractbalance"] = item[key];
//                             continue
//                         }
//                         //热充查询余额
//                         if (key == "hotrechargebalance") {
//                             item["queryhotrechargebalance"] = item[key];
//                             continue
//                         }
//                         //冷钱包查询余额
//                         if (key == "coldbalance") {
//                             item["querycoldbalance"] = item[key];
//                             continue
//                         }
//                         item[key] = item[key];
//                     }
//                     //热充钱包余额
//                     item.hotrechargebalance = new Big( item.hotuserrecharge).minus( item.hotrechargetocold).minus( item.hotrechargetocoldfee)
//                     //热充对账
//                     item.hotrechargereconciliation = new Big( item.queryhotrechargebalance).minus( item.hotrechargebalance)
//                     //冷钱包余额
//                     item.coldbalance = new Big( item.hotrechargetocold).minus( item.coldtohotextract).minus( item.coldtohotextractfee)
//                     //冷钱包对账
//                     item.coldreconciliation = new Big( item.querycoldbalance).minus( item.coldbalance)
//                     //热提余额
//                     item.hotwithdrawalextractbalance = new Big( item.coldtohotextract).minus( item.userextract).minus( item.extractfee)
//                     //热提对账
//                     item.hotextractreconciliation = new Big( item.queryhotextractbalance).minus( item.hotwithdrawalextractbalance)
//                 })
//                 this.setState({
//                     tableList,
//                     pageSize: result.data.pageSize,
//                     pageTotal: result.data.totalCount
//                 })
//             }
//         })
//     }
//     render() {
//         Big.RM = 0;
//         const { showHide, accountType, fundsType, tableList, time, pageIndex, pageTotal } = this.state
//         return (
//             <div className="right-con">
//                 <div className="page-title">
//                     当前位置：对账中心 > 钱包对账 > 区块钱包对账
//                     <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
//                 </div>
//                 <div className="clearfix"></div>
//                 <div className="row">
//                     <div className="col-md-12 col-sm-12 col-xs-12">
//                         {showHide && <div className="x_panel">
//                             <div className="x_content">

//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">资金类型：</label>
//                                         <div className="col-sm-9">
//                                             <Select value={fundsType} style={{ width: SELECTWIDTH }} onChange={this.handleChangeType} >
//                                                 {accountType}
//                                             </Select>
//                                         </div>
//                                     </div>
//                                 </div>
//                                 <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
//                                     <div className="form-group">
//                                         <label className="col-sm-3 control-label">时间筛选：</label>
//                                         <div className="col-sm-8">
//                                             <RangePicker
//                                                 showTime={{
//                                                     defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
//                                                 }}
//                                                 format={TIMEFORMAT_ss}
//                                                 placeholder={['Start Time', 'End Time']}
//                                                 onChange={this.onChangeCheckTime}
//                                                 value={time}
//                                             />
//                                         </div>
//                                     </div>
//                                 </div>

//                                 <div className="col-md-4 col-sm-4 col-xs-4 right">
//                                     <div className="right">
//                                         <Button type="primary" onClick={this.inquiry}>查询</Button>
//                                         <Button type="primary" onClick={this.onResetState}>重置</Button>
//                                     </div>
//                                 </div>
//                             </div>
//                         </div>
//                         }

//                         <div className="x_panel">
//                             <div className="x_content">
//                                 <div className="table-responsive">

//                                     <Table
//                                         dataSource={tableList}
//                                         scroll={{ x: 2070 }}
//                                         bordered={true}
//                                         locale={{emptyText:'暂无数据'}}
//                                         pagination={{
//                                             size: "small",
//                                             current: pageIndex,
//                                             total: pageTotal,
//                                             onChange: this.changPageNum, 
//                                             showTotal: total => `总共 ${total} 条`,
//                                             onShowSizeChange: this.onShowSizeChange,
//                                             showSizeChanger: true,
//                                             showQuickJumper: true
//                                         }}>

//                                         <Column title='序号' dataIndex='index' key='index' fixed='left' />
//                                         <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' fixed='left' />
//                                         <Column title={<React.Fragment>热充对账<br/>(G-M)</React.Fragment>} dataIndex='hotrechargereconciliation' key='hotrechargereconciliation' fixed='left' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>冷钱包对账<br/>(H-P)</React.Fragment>} dataIndex='coldreconciliation' key='coldreconciliation' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>热提对账<br/>(I-S)</React.Fragment>} dataIndex='hotextractreconciliation' key='hotextractreconciliation' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>热充钱包查询余额<br/>(G)</React.Fragment>} dataIndex='queryhotrechargebalance' key='queryhotrechargebalance' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>冷钱包查询余额<br/>(H')</React.Fragment>} dataIndex='querycoldbalance' key='querycoldbalance' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>热提钱包查询余额<br/>(I)</React.Fragment>} dataIndex='queryhotextractbalance' key='queryhotextractbalance' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>热充用户充值<br/>(J)</React.Fragment>} dataIndex='hotuserrecharge' key='hotuserrecharge' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>热充转冷<br/>(K')</React.Fragment>} dataIndex='hotrechargetocold' key='hotrechargetocold' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>热充转冷网络费<br/>(L)</React.Fragment>} dataIndex='hotrechargetocoldfee' key='hotrechargetocoldfee' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>(M)热充钱包余额<br/>(J-K-L)</React.Fragment>} dataIndex='hotrechargebalance' key='hotrechargebalance' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>冷转热提<br/>(N)</React.Fragment>} dataIndex='coldtohotextract' key='coldtohotextract' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>冷转热提网络费<br/>(O)</React.Fragment>} dataIndex='coldtohotextractfee' key='coldtohotextractfee' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>(P)冷钱包余额<br/>(K-N-O)</React.Fragment>} dataIndex='coldbalance' key='coldbalance' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>用户实际提现<br/>(Q)</React.Fragment>} dataIndex='userextract' key='userextract' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>提现网络费<br/>(R)</React.Fragment>} dataIndex='extractfee' key='extractfee' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title={<React.Fragment>(S)热提余额<br/>(N-Q-R)</React.Fragment>} dataIndex='hotwithdrawalextractbalance' key='hotwithdrawalextractbalance' className="moneyGreen" render={(text)=>toThousands(text,true)} />
//                                         <Column title='时间' dataIndex='checktime' key='checktime' />
//                                     </Table>
//                                 </div>
//                             </div>
//                         </div>

//                     </div>
//                 </div>
//             </div>
//         )

//     }
// }