/**期货账户对账 */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH,PAGRSIZE_OPTIONS} from '../../../conf'
import { Button, DatePicker, Select, Table,message } from 'antd'
import { toThousands } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;

export default class Futures extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            fundsType: "0",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: '50',
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
        }
        this.clickHide = this.clickHide.bind(this);
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.requestTable = this.requestTable.bind(this);
        this.handleChangeType = this.handleChangeType.bind(this);
        this.onResetState = this.onResetState.bind(this);
        this.changPageNum = this.changPageNum.bind(this);
        this.onShowSizeChange = this.onShowSizeChange.bind(this);
    }
    componentDidMount() {
        this.requestTable()
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
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
            fundsType: "0",
            begin: "",
            end: "",
            time: null,
        })
    }
    requestTable(currIndex, currSize) {
        const { fundsType, begin, end, pageIndex, pageSize } = this.state;
        axios.post(DOMAIN_VIP+"/futuresreconciliation/list", qs.stringify({
            reportdateStart: begin, 
            reportdateEnd: end, 
            fundstype: fundsType,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                Big.RM = 0;
                let tableList = result.data.list;
                tableList.map((item, index) => {
                    item.index = (result.data.currPage-1)*result.data.pageSize+index+1;
                    item.key = item.id;
                    item.reportdate = moment(item.reportdate).format('YYYY-MM-DD');
                    item.platformReconciliation = new Big(item.shiftto)
                    .minus(item.rollout)
                    .minus(item.balance)
                    .minus(item.dealcommission)
                    .plus(item.profitorloss)
                    .minus(item.insurancefund)
                    .minus(item.insuranceretained);
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
    }
    render() {
        Big.RM = 0;
        const { showHide, fundsType, tableList, time, pageIndex, pageSize, pageTotal, submitTimeOfStart, submitTimeOfEnd } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 交易平台对账 > 期货账户对账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对账日期：</label>
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

                                    <Table
                                        dataSource={tableList}
                                        bordered={true}
                                        locale={{emptyText:'暂无数据'}}
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
                                            pageSizeOptions:PAGRSIZE_OPTIONS
                                        }}>
                                            <Column title= '序号' dataIndex= 'index' key= 'index' />
                                            <Column title= '资金类型' dataIndex= 'fundstypeName' key= 'fundstypeName' />
                                            <Column title= {<React.Fragment>平台对账<br/>(E-F)-(G+H-I+J+K)</React.Fragment>} dataIndex= 'platformReconciliation' key= 'platformReconciliation' className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= {<React.Fragment>用户转入<br/>(E)</React.Fragment>} dataIndex= 'shiftto' key= 'shiftto' className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= {<React.Fragment>用户转出<br/>(F)</React.Fragment>} dataIndex= 'rollout' key= 'rollout' className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= {<React.Fragment>期货账户余额<br/>(G)</React.Fragment>} dataIndex= 'balance' key= 'balance' className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= {<React.Fragment>成交佣金<br/>(H)</React.Fragment>} dataIndex= 'dealcommission' key= 'dealcommission' className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= {<React.Fragment>交易盈亏<br/>(I)</React.Fragment>} dataIndex= 'profitorloss' key= 'profitorloss' className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= {<React.Fragment>保险基金(展示)<br/>(J)</React.Fragment>} dataIndex= 'insurancefund' key= 'insurancefund' className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= {<React.Fragment>保险留存<br/>(K)</React.Fragment>} dataIndex= 'insuranceretained' key= 'insuranceretained' className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= {<React.Fragment>资金费用差额<br/>(L)</React.Fragment>} dataIndex= 'balancefees' key= 'balancefees' className= "moneyGreen" render={(text)=>toThousands(text,true)} />
                                            <Column title= '对账日期' dataIndex= 'reportdate' key= 'reportdate' />
        
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