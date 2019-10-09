/**交易平台钱包对账 */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, DOMAIN_VIP, SELECTWIDTH,TIMEFORMAT_ss ,PAGRSIZE_OPTIONS} from '../../../conf'
import { Button, DatePicker, Select, Table,Modal,message } from 'antd'
import { toThousands,pageLimit } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
import SelectState from '../../../components/select/selectState'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;

export default class TradingPlatformWallet extends React.Component {
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
            state:'',
            tableDataInterface: DOMAIN_VIP + '/generalledger/list',
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
        this.setState({
            limitBtn: pageLimit('generalledger', this.props.permissList)
        },()=>console.log(this.state.limitBtn))
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
            state:''
        })
    }
    requestTable(currIndex, currSize) {
        const { state,fundsType, begin, end, pageIndex, pageSize, tableDataInterface } = this.state
        axios.post(tableDataInterface, qs.stringify({
            reportdateStart:begin,state,
            reportdateEnd:end,
            fundstype:fundsType,
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
                    // item.platformBalanceReconciliation = new Big(item.recharge)
                    // .minus(item.withdraw)
                    // .plus(item.sysrecharge)
                    // .minus(item.sysdeduction)
                    // .minus(item.balance)
                    // .minus(item.rollout)
                    // .plus(item.shiftto)
                    // .plus(item.syssort)
                    // .plus(item.activityreward)
                    // .plus(item.doubleactivity);
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
    changeState=(val)=>{
        this.setState({
            state:val
        })
    }
    updateStatus = id => {
        let self = this;
        Modal.confirm({
            title:'你确定要改变状态吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                axios.post(DOMAIN_VIP+'/generalledger/update',qs.stringify({id})).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        self.requestTable()
                    } 
                })
            },
            onCancel(){
                console.log('Cancel')
            } 
        })
    }
    render() {
        Big.RM = 0;
        const { state,showHide,fundsType, tableList, time, pageIndex, pageTotal,pageSize } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 交易平台对账 > 交易平台钱包对账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList  fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对账日期：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectState state={this.state.state} changeState={this.changeState} />
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
                                        rowKey="index"
                                        // scroll={pageSize != 10 ? { y: 500 } : {}}
                                        scroll={{x:2000}}
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

                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='资金类型' dataIndex='fundstypeName' key='fundstypeName' />
                                        <Column title={<React.Fragment>平台钱包账户对账<br/>(D-E+F-G)-(H+i-J-K-L-M)</React.Fragment>} dataIndex='difference' key='platformBalanceReconciliation' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>用户充值<br/>(D)</React.Fragment>} dataIndex='recharge' key='recharge' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>用户提现<br/>(E)</React.Fragment>} dataIndex='withdraw' key='withdraw' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>系统充值<br/>(F)</React.Fragment>} dataIndex='sysrecharge' key='sysrecharge' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>系统扣除<br/>(G)</React.Fragment>} dataIndex='sysdeduction' key='sysdeduction' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>平台钱包账户余额<br/>(H)</React.Fragment>} dataIndex='balance' key='balance' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>钱包转出<br/>(I)</React.Fragment>} dataIndex='rollout' key='rollout' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>钱包转入<br/>(J)</React.Fragment>} dataIndex='shiftto' key='shiftto' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>系统分发<br/>(K)</React.Fragment>} dataIndex='syssort' key='syssort' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>活动奖励<br/>(L)</React.Fragment>} dataIndex='activityreward' key='activityreward' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>活动翻倍<br/>(M)</React.Fragment>} dataIndex='doubleactivity' key='doubleactivity' className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title={<React.Fragment>内部调账正</React.Fragment>} dataIndex='internaladjustmentpositive' key='internaladjustmentpositive' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>内部调账负</React.Fragment>} dataIndex='internaladjustmentnegative' key='internaladjustmentnegative' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>外部调账正</React.Fragment>} dataIndex='externaladjustmentpositive' key='externaladjustmentpositive' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title={<React.Fragment>外部调账负</React.Fragment>} dataIndex='externaladjustmentnegative' key='externaladjustmentnegative' className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        
                                        <Column title='对账日期' dataIndex='reportdate' key='reportdate' />
                                        <Column title="状态" dataIndex="state" key="sss" render={(state,record) => state == 0 ? '正常' : <a href='javascript:void(0);' onClick={()=>this.updateStatus(record.id)} >异常</a>} />
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