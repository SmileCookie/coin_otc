import React, { Component } from 'react'
import axios from '../../../utils/fetch';
import qs from 'qs';
import moment from 'moment'
import { Table, message, DatePicker, Button, Select } from 'antd'
import { PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, PAGRSIZE_OPTIONS20, DOMAIN_VIP, SELECTWIDTH } from '../../../conf'
import MarketList from '../../common/select/marketrequests'
import { toThousands } from '../../../utils'
const Column = Table.Column
const { RangePicker } = DatePicker
const Option = Select.Option

export default class CoinMarketReport extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            tableSource: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            time: [],
            entrustmarket: '',
            createtimeS: '',
            createtimeE: '',
            market: '',
            orientation: '',
            numTotal:{}
        }
    }
    componentDidMount() {
        this.requestTable()
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
            time: [],
            entrustmarket: '',
            createtimeS: '',
            createtimeE: '',
            market: '',
            orientation: ''
        })
    }
    //市场
    handleSelectMarket = val => {
        this.setState({
            entrustmarket: val
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex, pageSize, entrustmarket, createtimeS, createtimeE, market, orientation } = this.state
        axios.post(DOMAIN_VIP + '/coinQtMarket/list', qs.stringify({
            entrustmarket, createtimeS, createtimeE, market,
            // orientation,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.page.list;
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.data.page.currPage - 1) * result.data.page.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id
                }
                let { buyerNumbers,buyerTotalMoney,buyerFees,sellerNumbers,sellerTotalMoney } = result.data
                this.setState({
                    tableSource: tableSource,
                    pageTotal: result.data.page.totalCount,
                    numTotal:{
                        buyerNumbers,buyerTotalMoney,buyerFees,sellerNumbers,sellerTotalMoney
                    }

                })
            } else {
                message.warning(result.msg);
            }
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
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        // console.log(date, dateString);
        this.setState({
            createtimeS: dateString[0] && moment(dateString[0]).format('x'),
            createtimeE: dateString[1] && moment(dateString[1]).format('x'),
            time: date
        })
    }
    selectMarket = market => { this.setState({ market }) }
    selectOrientation = orientation => { this.setState({ orientation }) }
    render() {
        const { showHide, tableSource, pageIndex, pageSize, pageTotal, time, entrustmarket, market, orientation,numTotal } = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 报表中心 > 币币报表 > 币币市场报表
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <MarketList market={entrustmarket} title='交易盘口' underLine={true} col='3' handleChange={this.handleSelectMarket} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className='col-sm-3 control-label' >市场：</label>
                                        <div className="col-sm-8">
                                            <Select value={market} style={{ width: SELECTWIDTH }} onChange={this.selectMarket}>
                                                <Option value=''>请选择</Option>
                                                <Option value='BTC'>BTC</Option>
                                                <Option value='USDT'>USDT</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className='col-sm-3 control-label' >方向：</label>
                                        <div className="col-sm-8">
                                            <Select value={orientation} style={{ width: SELECTWIDTH }} onChange={this.selectOrientation}>
                                                <Option value=''>请选择</Option>
                                                <Option value={20}>买入</Option>
                                                <Option value={21}>卖出</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">时间筛选:</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm,ss'), moment('23:59:59', 'HH:mm,ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
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
                                    {<table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="8" className="column-title">
                                                买总交易量：{toThousands(numTotal.buyerNumbers,true)}，
                                                买总交易额：{toThousands(numTotal.buyerTotalMoney,true)}，
                                                交易手续费：{toThousands(numTotal.buyerFees,true)}，
                                                卖总交易量：{toThousands(numTotal.sellerNumbers,true)}，
                                                卖总交易金额：{toThousands(numTotal.sellerTotalMoney,true)}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>}
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
                                        onChange={this.sorter}
                                        locale={{ emptyText: '暂无数据' }}
                                    >
                                        <Column title='序号' dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )} />
                                        <Column title='交易盘口' dataIndex='entrustmarket' key='entrustmarket' />
                                        {/* <Column title='方向' dataIndex='orientationName' key='orientationName' /> */}
                                        <Column title='买交易量' dataIndex='numbers' key='numbers' />
                                        {/* <Column title='卖出交易量' dataIndex='' key='3' /> */}
                                        <Column title='买交易金额' className='moneyGreen' dataIndex='totalmoney' key='totalmoney' render={text => toThousands(text, true)} />
                                        <Column title='卖交易量' dataIndex='sellnumbers' key='sellnumbers' />
                                        {/* <Column title='卖出交易量' dataIndex='' key='3' /> */}
                                        <Column title='卖交易金额' className='moneyGreen' dataIndex='selltotalmoney' key='selltotalmoney' render={text => toThousands(text, true)} />
                                        <Column title='手续费' className='moneyGreen' dataIndex='transactionFees' key='transactionFees' render={text => toThousands(text, true)} />
                                        {/* <Column title='卖出交易金额' className='moneyGreen' dataIndex='' key='5' render={text=>toThousands(text,true)} /> */}
                                        <Column title='日期' dataIndex='datetime' key='datetime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : ''} />
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