/**数据中心 》 资金中心 》 用户资金 》 期货用户资金  */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH,PAGESIZE_50,PAGRSIZE_OPTIONS } from '../../../conf'
import { Button, DatePicker, Select, Table,message } from 'antd'
import { toThousands } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;

export default class FuturesUserMoney extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            fundsType: "2",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE_50,
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
            userId: "",
            userName: "",
            pufbalancesum: "",
            tradingFrozen: "",
            advertisingFrozen: "",
            totalMoney: "",
            moneyMin: '',
            moneyMax: '',
            totalMoneyMin: '',
            totalMoneyMax: '',
            freezMoneyMin: "",
            freezMoneyMax: "",
            tableDataInterface: DOMAIN_VIP + "/userFutures/query",
            summaryDataInterface: DOMAIN_VIP + "/userFutures/sum"
        }
        this.clickHide = this.clickHide.bind(this);
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.requestTable = this.requestTable.bind(this);
        this.handleChangeType = this.handleChangeType.bind(this);
        this.onResetState = this.onResetState.bind(this);
        this.changPageNum = this.changPageNum.bind(this);
        this.onShowSizeChange = this.onShowSizeChange.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.requestSort = this.requestSort.bind(this);
        this.handleChangeTable = this.handleChangeTable.bind(this);
    }
    componentDidMount() {
        this.requestTable()
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    handleChangeTable(pagination, filters, sorter) {

        console.log(sorter);
        // sorter.field
        // sorter.order
        // this.setState({
        //     sortType: sorter.order
        // }, () => this.requestTable());
    }
    handleInputChange(e) {
        const target = e.target;
        const value = target.value;
        const name = target.name
        let json = new Object();
        json[name] = value;
        this.setState(json);
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            begin: dateString[0],
            end: dateString[1],
            time: date
        })
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestTable(page,pageSize))

    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requestTable(current,size))
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    handleChangeType(value) {
        this.setState({
            fundsType: value
        })
    }
    onResetState() {
        this.setState({
            fundsType: "2",
            time: null,
            userId: "",
            userName: "",
            moneyMin: '',
            moneyMax: '',
            freezMoneyMin: "",
            freezMoneyMax: "",
            totalMoneyMin: '',
            totalMoneyMax: '',
        })
    }
    requestSort(type) {
        this.setState({ sortType: type }, () => this.requestTable())
    }
    requestTable(currIndex, currSize) {

        const {
            summaryDataInterface,
            tableDataInterface,
            fundsType,
            pageIndex,
            pageSize,
            userId,
            moneyMin,
            moneyMax,
            freezMoneyMin,
            freezMoneyMax,
            totalMoneyMin,
            totalMoneyMax,
            userName
        } = this.state
        const parameter = {
            userId,
            userName,
            fundType: fundsType,
            moneyMin,
            moneyMax,
            freezMoneyMin,
            freezMoneyMax,
            totalMoneyMin,
            totalMoneyMax,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }

        axios.post(tableDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {

                Big.RM = 0;
                let tableList = result.data.list;

                tableList.map((item, index) => {
                    item.index = index + 1;
                    item.key = item.id;
                    item.freezeSecurity = new Big(item.freez).plus(item.positionfreez);
                    item.totalAmount = new Big(item.balance).plus(item.freez).plus(item.positionfreez);
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
        axios.post(summaryDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    pufbalancesum:result.data[0]&&result.data[0].pufbalancesum,
                    freezallsum:result.data[0]&&result.data[0].freezallsum,
                    
                    totalMoney: result.data[0]&&result.data[0].allsum,

                })
            //     Big.RM = 0;
            //     let dataKey = {
            //         "pufbalancesum": null, //可用余额总和
            //         "puffreezsum": null,  //冻结余额总和
            //         "pufpositionfreezsum": null, //持仓冻结总和
            //     }
            //     let json = new Object();
            //     for (let key in dataKey) {
            //         json[key] = result.data[0][key]
            //     }
            //     json.freezallsum = new Big(json.puffreezsum).plus(json.pufpositionfreezsum);
            //     json.totalMoney = new Big(json.pufbalancesum).plus(json.pufpositionfreezsum).plus(json.puffreezsum);
            //     delete json.puffreezsum;
            //     delete json.pufpositionfreezsum;

            //     this.setState(json)
            }else{
                message.warning(result.msg)
            }
        })
    }
    render() {
        Big.RM = 0;
        const { showHide, pufbalancesum, totalMoney, freezallsum, fundsType, tableList, pageIndex, pageSize, pageTotal, } = this.state
        const { userId, userName, moneyMin, moneyMax, freezMoneyMin, freezMoneyMax, totalMoneyMin, totalMoneyMax } = this.state;



        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 用户资金 > 期货用户资金  
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList  fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">总金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="totalMoneyMin" value={totalMoneyMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="totalMoneyMax" value={totalMoneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">可用金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMin" value={moneyMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMax" value={moneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">冻结保证金：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="freezMoneyMin" value={freezMoneyMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="freezMoneyMax" value={freezMoneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">

                                <div className="table-responsive table-box">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    总资金：{toThousands(totalMoney,true,true)} &nbsp;&nbsp;&nbsp;
                                                    冻结保证金：{toThousands(freezallsum,true,true)}  &nbsp;&nbsp;&nbsp;
                                                    可用金额：{toThousands(pufbalancesum,true,true)}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table
                                        dataSource={tableList}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{emptyText:'暂无数据'}}
                                        // scroll={pageSize>10?{ y: 500 }:{}}
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

                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                                        <Column title='用户编号' dataIndex='userid' key='userid' />
                                        <Column title='总金额' dataIndex='totalAmount' key='totalAmount' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='可用金额' dataIndex='balance' key='balance' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='冻结保证金' dataIndex='freezeSecurity' key='freezeSecurity' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                    </Table>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        )

    }
}