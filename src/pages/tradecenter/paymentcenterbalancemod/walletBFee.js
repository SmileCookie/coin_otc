import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT, PAGRSIZE_OPTIONS20, TIMEFORMAT_ss } from '../../../conf'
import { Button, DatePicker, Tabs, Pagination, Select, Table, message } from 'antd'
import { toThousands, tableScroll } from '../../../utils'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { Column } = Table

export default class WalletBFee extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            walletType: '',
            checkTimeStart: "",
            checkTimeEnd: "",
            time: [],
            heightStart: "",
            heightEnd: "",
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: 0,
            tableSource: [],

        }
        this.clickHide = this.clickHide.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onResetState = this.onResetState.bind(this)
    }
    componentDidMount() {

    }
    componentWillReceiveProps() {

    }


    requestTable(currIndex, currSize) {
        const { checkTimeStart, walletType, checkTimeEnd, time, heightStart, heightEnd, pageIndex, pageSize } = this.state
        axios.post(DOMAIN_VIP + '/walletBill/queryBList', qs.stringify({
            confirmStart: checkTimeStart, walletType, confirmEnd: checkTimeEnd, heightStart, heightEnd,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.list || [];
                for (let i = 0, length = tableSource.length; i < length; i++) {
                    tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].txId
                }
                this.setState({
                    tableSource: tableSource,
                    pageTotal: result.data.totalCount
                })
            } else {
                message.error(result.msg)
            }
        })
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page,
            pageSize
        })
        this.requestTable(page, pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.requestTable(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    onResetState() {
        this.setState({
            walletType: '',
            checkTimeEnd: '',
            checkTimeStart: '',
            heightStart: '',
            heightEnd: '',
            time: []
        })
    }
    //充值开始时间
    onChangeTime(date, dateString) {
        this.setState({
            checkTimeStart: dateString[0],
            checkTimeEnd: dateString[1],
            time: date
        })
    }
    //点击收起
    clickHide() {
        let { showHide, xheight, pageSize } = this.state;
        if (showHide && pageSize > 10) {
            this.setState({
                showHide: !showHide,
                height: xheight,
            })
        } else {
            this.setState({
                showHide: !showHide,
                height: 0
            })
        }
        // this.setState({
        //     showHide: !showHide,
        // })
    }
    //资金类型 select
    handleChangeType(val) {
        this.setState({
            walletType: val
        })
    }
    inquery = () => {
        this.setState({
            pageIndex:PAGEINDEX
        },()=>this.requestTable())
    }
    render() {
        const { showHide, checkTimeStart, tableSource, walletType, checkTimeEnd, time, heightStart, heightEnd, pageIndex, pageSize, pageTotal } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 支付中心对账 > B钱包对账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className=" col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">钱包类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={walletType}
                                                style={{ width: SELECTWIDTH }}
                                                onChange={this.handleChangeType}
                                            // filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                            // showSearch
                                            >
                                                <Option value=''>请选择</Option>
                                                <Option value='1'>热充钱包</Option>
                                                <Option value='2'>冷钱包</Option>
                                                <Option value='3'>热提钱包</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">区块高度：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="heightStart" value={heightStart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="heightEnd" value={heightEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">确认时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeTime}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.inquery()}>查询</Button>
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
                                        dataSource={tableSource}
                                        bordered
                                        scroll={{ x: 1920 }}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
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
                                        <Column title='交易流水号' dataIndex='txId' key='txId' />
                                        <Column title='资金类型' dataIndex='fundsType' key='fundsType ' />
                                        <Column title='发送方' dataIndex='sendWallet' key='' />
                                        <Column title='接收方' dataIndex='receiveWallet' key='' />
                                        <Column className='moneyGreen' title='交易金额' dataIndex='txAmount' key='' render={text => toThousands(text, true)} />
                                        <Column className='moneyGreen' title='网络费' dataIndex='fee' key='' render={text => toThousands(text, true)} />
                                        <Column className='moneyGreen' title='余额' dataIndex='walBalance' key='' render={text => toThousands(text, true)} />
                                        <Column title='区块高度' dataIndex='blockHeight' key='' />
                                        <Column title='确认时间' dataIndex='configTime' key='' render={text => text ? moment(text).format(TIMEFORMAT_ss) : '--'} />
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