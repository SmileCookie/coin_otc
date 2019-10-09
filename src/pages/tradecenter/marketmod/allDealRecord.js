import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { message, Modal, Table, Button,DatePicker } from 'antd'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, SELECTWIDTH, PAGRSIZE_OPTIONS20,DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss, TIMEFORMAT } from '../../../conf'
import MarketList from '../../common/select/marketrequests'
import { toThousands } from '../../../utils'
const { Column } = Table
const { RangePicker } = DatePicker;

export default class AllDealRecord extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            market: '',
            userId: '',
            tableSource:[],
            time:[],
            startTime:'',
            endTime:'',
            days30:0
        }
    }
    componentDidMount() {
        let before30 = moment().day(moment().day() - 29)
        let now = moment()
        this.setState({
            startTime:moment().day(moment().day() - 29).startOf('day').valueOf(),
            endTime:moment().format('x'),
            time:[before30,now]
        },()=> this.requestTable())
        console.log(before30)
       
    }
    clickHide = () => {
        this.setState({
            showHide: !this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => this.requestTable())
    }
    resetState = () => {
        this.setState({
            market:'',
            userId:''
        })
    }
    onChangePageNum = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    handleChange = (value) => {
        this.setState({
            market: value
        })
    }
    //输入时 input 设置到 state
    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    time_onChange = (date,dateString) => {
        // console.log(date,dateString,+moment(dateString[0]).format('x'))
        let days30 = Number(moment(dateString[1]).format('x')) - Number(moment(dateString[0]).format('x'));
        
        let startTime = dateString[0]&&moment(dateString[0]).format('x')
        let endTime = dateString[1]&&moment(dateString[1]).format('x')
        console.log(startTime,endTime)
        this.setState({
            startTime:startTime,
            endTime,
            time:date,
            days30
        })

    }
    requestTable = () => {
        const { market ,userId,startTime,endTime,days30} = this.state
        let limit = 30 * 24 * 60 * 60 * 1000;
        if(days30 - limit > 0){
            message.warning('时间区间请选择30天以内！');
            return false
        }
        axios.post(DOMAIN_VIP + '/dealRecord/getAllTransrecord', qs.stringify({
            market,userId,startTime,endTime:Number(endTime)+999
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data || [];
                for (let i = 0; i < tableSource.length; i++) {
                    // tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].index = i +1
                    tableSource[i].key = tableSource[i].transrecordid
                }
                this.setState({
                    tableSource: tableSource,
                    pageTotal: result.data.totalCount
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    render(){
        const {showHide,tableSource,pageIndex,pageSize,pageTotal,market,userId, time} = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置：数据中心 > 盘口管理 > 成交记录
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                            <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                <MarketList market={market} col='3' handleChange={this.handleChange}></MarketList>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.time_onChange}
                                                value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>                                   
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table
                                        dataSource={tableSource}
                                        bordered
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.onChangePageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            pageSizeOptions: PAGRSIZE_OPTIONS20,
                                            defaultPageSize: PAGESIZE,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }}
                                        locale={{ emptyText: '暂无数据' }}
                                    >
                                        <Column title='序号' dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )} />
                                        <Column title='成交编号' dataIndex='transrecordid' key='' />
                                        <Column title='交易市场' dataIndex='market' key='' />
                                        <Column title='买家用户编号' dataIndex='useridbuy' key='' />
                                        <Column title='买家委托编号' dataIndex='entrustidbuy' key='' />
                                        <Column title='卖家用户编号' dataIndex='useridsell' key='' />
                                        <Column title='卖家委托编号' dataIndex='entrustidsell' key='' />
                                        <Column className='moneyGreen' title='成交单价' dataIndex='unitprice' key='' render={text => toThousands(text,true)} />
                                        <Column title='成交数量' dataIndex='numbers' key='' />
                                        <Column className='moneyGreen' title='成交总金额' dataIndex='totalprice' key='' render={text => toThousands(text,true)}  />
                                        <Column title='委托类型' dataIndex='types' key='' render={text => text > 0 ? '买入' : '卖出'} />
                                        <Column title='成交时间' dataIndex='times' key='' render={text => text ? moment(text).format(TIMEFORMAT_ss) :'--' } />
                                        <Column title='状态' dataIndex='status' key='' render={text => {
                                            switch (text) {
                                                case 0:
                                                return '未完全成交'
                                                case 1:
                                                return '取消'
                                                case 2:
                                                return '交易成功'
                                                default:
                                                return '未完全成交'
                                            }
                                        }} />
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


