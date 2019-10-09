/**数据中心 》 资金中心 》 资金划转  */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH,PAGRSIZE_OPTIONS,TIMEFORMAT_ss } from '../../../conf'
import { Button, DatePicker, Select, Table,message } from 'antd'
import { toThousands } from '../../../utils';
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;

export default class FundsTransfer extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            fundsType: "2",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: 50,
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
            userId: "",
            dst: "0",
            src: "0",
            amountMin: "",
            amountMax: "",
            userName: "",
            transferAmountTotal:'',
            sortType:"",
            summaryDataInterface:DOMAIN_VIP + "/fundTransferLog/sum",
            tableDataInterface:DOMAIN_VIP + "/fundTransferLog/query",
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
        this.handleDstChangeType = this.handleDstChangeType.bind(this);
        this.handleSrcChangeType = this.handleSrcChangeType.bind(this);
    }
    componentDidMount() {
        this.requestTable()
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    handleChangeTable(pagination, filters, sorter){
        this.setState({
            sortType: sorter.order
        },()=>this.requestTable());
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
    handleSrcChangeType(value) {
        this.setState({
            src: value
        })
    }
    handleDstChangeType(value) {
        this.setState({
            dst: value
        })
    }
    onResetState() {
        this.setState({
            fundsType: "2",
            begin: "",
            end: "",
            time: null,
            userId: "",
            dst: "",
            src: "",
            amountMin: "",
            amountMax: "",
            userName: "",
        })
    }
    requestSort(type){
        this.setState({sortType:type}, () => this.requestTable())
    }
    requestTable(currIndex, currSize) {
        const {
            summaryDataInterface,
            tableDataInterface,
            fundsType,
            begin,
            end,
            pageIndex,
            pageSize,
            userId, 
            dst,
            src,
            amountMin,
            amountMax,
            userName,
            sortType
        } = this.state;
        const parameter = {
            fundtype:fundsType==0?"":fundsType,
            Stime:begin,
            Etime:end,
            uid:userId, 
            dst:dst==0?"":dst,
            src:src==0?"":src,
            amountMin,
            amountMax,
            userName,
            sortType,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,
        };
        
        axios.post(tableDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {

                Big.RM = 0;
                let tableList = result.data.list;

                tableList.map((item, index) => {
                    item.index = (result.data.currPage - 1) * result.data.pageSize + index + 1;
                    item.key = item.id;
                    item.time = moment(item.time).format(TIMEFORMAT_ss);
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
                    transferAmountTotal:result.data[0]&&result.data[0].fAmountSum
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    render() {
        Big.RM = 0;
        const {
            showHide,
            fundsType,
            tableList,
            time,
            transferAmountTotal,
            pageIndex,
            pageSize,
            pageTotal,
            userId, 
            amountMin,
            amountMax,
            userName,
            src,
            dst
        } = this.state

        let columns = []
        return (
            <div className="right-con">
                <div className="page-title">
                当前位置：数据中心 > 资金中心 > 账单明细
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
                                        <label className="col-sm-3 control-label">转出帐户：</label>
                                        <div className="col-sm-8 ">
                                            <Select value={src} style={{ width: SELECTWIDTH }} onChange={this.handleSrcChangeType} >
                                                <Option key="0" value="0">请选择</Option>
                                                <Option key="1" value="1">钱包</Option>
                                                <Option key="2" value="2">币币</Option>
                                                <Option key="3" value="3">法币</Option>
                                                <Option key="4" value="4">期货</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">转入帐户：</label>
                                        <div className="col-sm-8 ">
                                            <Select value={dst} style={{ width: SELECTWIDTH }} onChange={this.handleDstChangeType} >
                                                <Option key="0" value="0">请选择</Option>
                                                <Option key="1" value="1">钱包</Option>
                                                <Option key="2" value="2">币币</Option>
                                                <Option key="3" value="3">法币</Option>
                                                <Option key="4" value="4">期货</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">划转金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="amountMin" value={amountMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="amountMax" value={amountMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">划转时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />
                                        </div>
                                    </div>
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
                                                    划转金额：{toThousands(transferAmountTotal,true)}
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
                                            current: pageIndex,
                                            pageSize: pageSize,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            showSizeChanger: true,
                                            showQuickJumper: true,
                                            pageSizeOptions:PAGRSIZE_OPTIONS
                                        }}>
                                            <Column title= '序号'  dataIndex= 'index'  key= 'index' />
                                            <Column title= '资金类型'  dataIndex= 'fundName'  key= 'fundName' />
                                            <Column title= '用户编号'  dataIndex= 'uid'  key= 'uid' />
                                            <Column title= '转出帐户'  dataIndex= 'srcName'  key= 'srcName' />
                                            <Column title= '转入帐户'  dataIndex= 'dstName'  key= 'dstName' />
                                            <Column title= '划转金额'  dataIndex= 'amount'  key= 'amount'  sorter="true"  className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= '划转时间'  dataIndex= 'time'  key= 'time' />
                                            <Column title= '备注'  dataIndex= 'note'  key= 'note' />
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