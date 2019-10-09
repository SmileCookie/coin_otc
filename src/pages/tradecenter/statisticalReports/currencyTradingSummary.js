/**数据中心 》 统计报表 》 币币报表 》 币币交易汇总表 */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH ,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button, DatePicker, Select, Table, Modal,message } from 'antd'
import { toThousands } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;
export default class CurrencyTradingSummary extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            accountType: [<Option key='0' value='0'>请选择</Option>, <Option key='1' value='1'>用户帐户</Option>, <Option key='2' value='2'>刷量帐户</Option>],
            accountTypeVal: "0",
            fundsType: '0',
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
            userId: "",
            userName: "",
            sortType: "",
            profitlossmin: "",//保值盈亏
            profitlossmax: "",
            numbertransactionsmin: "",//交易人数
            numbertransactionsmax: "",//交易人数
            usertransactionmin: "",//用户交易量
            usertransactionmax: "",
            userfeemin: "", //用户手续费
            userfeemax: "",
            brushvolumemin: "", //刷量交易量
            brushvolumemax: "",
            brushchargemin: "", //刷量手续费
            brushchargemax: "",
            usertransactionsum: "",
            brushvolumesum: "",
            userfeesum: "",
            brushchargesum: "",
            numbertransactionssum: "",
            hedgingsum: "",
            profitlosssum: "",
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
        this.handleAccountChangeType = this.handleAccountChangeType.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
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
    handleAccountChangeType(value) {
        this.setState({
            accountTypeVal: value
        })
    }
    onResetState() {
        this.setState({
            fundsType: "0",
            time: null,
            profitlossmin: "",//保值盈亏
            profitlossmax: "",
            numbertransactionsmin: "",//交易人数
            numbertransactionsmax: "",//交易人数
            usertransactionmin: "",//用户交易量
            usertransactionmax: "",
            userfeemin: "", //用户手续费
            userfeemax: "",
            brushvolumemin: "", //刷量交易量
            brushvolumemax: "",
            brushchargemin: "", //刷量手续费
            brushchargemax: "",
            begin: "",
            end: "",
            time:[]
        })
    }
    requestSort(type) {
        this.setState({ sortType: type }, () => this.requestTable())
    }
    requestTable(currIndex, currSize) {
        const {
            fundsType,
            pageIndex,
            pageSize,
            begin,
            end,
            sortType,
            profitlossmin,//保值盈亏
            profitlossmax,
            numbertransactionsmin ,//交易人数
            numbertransactionsmax ,//交易人数
            usertransactionmin,//用户交易量
            usertransactionmax,
            userfeemin, //用户手续费
            userfeemax,
            brushvolumemin, //刷量交易量
            brushvolumemax,
            brushchargemin, //刷量手续费
            brushchargemax,
        } = this.state

        const parameter = {
            fundstype: fundsType,
            begintime: begin,
            endtime: end,
            sortType,
            profitlossmin,//保值盈亏
            profitlossmax,
            numbertransactionsmin ,//交易人数
            numbertransactionsmax ,//交易人数
            usertransactionmin,//用户交易量
            usertransactionmax,
            userfeemin, //用户手续费
            userfeemax,
            brushvolumemin, //刷量交易量
            brushvolumemax,
            brushchargemin, //刷量手续费
            brushchargemax,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        };
        axios.post(DOMAIN_VIP + "/transactionAll/query", qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {

                Big.RM = 0;
                let tableList = result.data.list;

                tableList.map((item, index) => {
                    item.index = index + 1;
                    item.key = item.id;
                    item.trading = moment(item.trading).format('YYYY-MM-DD');
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
        axios.post(DOMAIN_VIP + "/transactionAll/sum", qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {

                Big.RM = 0;
                this.setState({
                    usertransactionsum: result.data[0]&&result.data[0].usertransactionsum,
                    brushvolumesum: result.data[0]&&result.data[0].brushvolumesum,
                    userfeesum: result.data[0]&&result.data[0].userfeesum,
                    brushchargesum: result.data[0]&&result.data[0].brushchargesum,
                    numbertransactionssum: result.data[0]&&result.data[0].numbertransactionssum,
                    hedgingsum: result.data[0]&&result.data[0].hedgingsum,
                    profitlosssum: result.data[0]&&result.data[0].profitlosssum,
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCancel() {
        this.setState({ visible: false })
    }
    footer() {

    }
    render() {
        Big.RM = 0;
        const {
            showHide,
            fundsType,
            tableList,
            pageIndex,
            pageSize,
            pageTotal,
            usertransactionsum,
            brushvolumesum,
            userfeesum,
            brushchargesum,
            numbertransactionssum,
            hedgingsum,
            profitlosssum,
            time,
            profitlossmin,
            profitlossmax,
            numbertransactionsmin ,//交易人数
            numbertransactionsmax ,//交易人数
            usertransactionmin,
            usertransactionmax,
            userfeemin,
            userfeemax,
            brushvolumemin,
            brushvolumemax,
            brushchargemin,
            brushchargemax,
        } = this.state;

        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 币币报表 > 币币交易汇总表 
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList title='资金类型' fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户交易量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="usertransactionmin" value={usertransactionmin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="usertransactionmax" value={usertransactionmax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">做市交易量：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="brushvolumemin" value={brushvolumemin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="brushvolumemax" value={brushvolumemax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户手续费：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="userfeemin" value={userfeemin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="userfeemax" value={userfeemax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">做市手续费：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="brushchargemin" value={brushchargemin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="brushchargemax" value={brushchargemax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易人数：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="numbertransactionsmin" value={numbertransactionsmin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="numbertransactionsmax" value={numbertransactionsmax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保值盈亏：</label>
                                        <div className="col-sm-8 ">
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="profitlossmin" value={profitlossmin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="profitlossmax" value={profitlossmax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">报表日期：</label>
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
                                <div className="table-responsive">
                                    <table style={{margin:'0'}} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                                <tr className="headings">
                                                    <th style={{textAlign:'left'}} colSpan="9" className="column-title">
                                                    用户交易量：{ toThousands(usertransactionsum,true)},&nbsp;&nbsp;&nbsp;&nbsp;
                                                    做市交易量：{ toThousands(brushvolumesum,true)},&nbsp;&nbsp;&nbsp;&nbsp;
                                                    用户手续费：{ toThousands(userfeesum,true)},&nbsp;&nbsp;&nbsp;&nbsp;
                                                    刷量手续费：{ toThousands(brushchargesum,true) },&nbsp;&nbsp;&nbsp;&nbsp;
                                                    交易人数：{ toThousands(numbertransactionssum,true) },&nbsp;&nbsp;&nbsp;&nbsp;
                                                    保值总量：{ toThousands(hedgingsum,true)},&nbsp;&nbsp;&nbsp;&nbsp;
                                                    保值盈亏：{ toThousands(profitlosssum,true)}
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
                                            pageSizeOptions:PAGRSIZE_OPTIONS20,
                                            defaultPageSize:PAGESIZE,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }}>
                                        <Column title='序号' dataIndex='index' key='index' render={(text) => {
                                            return <span>{text}</span>
                                        }} />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                                        <Column title='用户交易量' dataIndex='usertransaction' key='usertransaction' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='做市交易量' dataIndex='brushvolume' key='brushvolume' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='用户手续费' dataIndex='userfee' key='userfee' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='交易人数' dataIndex='numbertransactions' key='numbertransactions' sorter="true" />
                                        <Column title='保值总量' dataIndex='hedging' key='hedging' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='保值盈亏' dataIndex='profitloss' key='profitloss' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='做市手续费' dataIndex='brushcharge' key='brushcharge' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='报表日期' dataIndex='trading' key='trading' />
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