
import { PAGEINDEX, PAGESIZE, SELECTWIDTH, PAGRSIZE_OPTIONS20, TIMEFORMAT_ss } from '../../../conf'
import { Button, DatePicker, Select, } from 'antd'
import { toThousands } from '../../../utils'
import CommonTable from '../../common/table/commonTable'
import Decorator from '../../decorator'
import React from 'react'
import HedgeMarketList from "../../common/select/hedgeMarketList";
import PlatformsList from "../../common/select/platformsList";
import AccountList from "../../common/select/accountList";
const { RangePicker, } = DatePicker;
const Option = Select.Option;

@Decorator()
class RecordHedgedTrading extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            tradeMarket: '',
            platform: '',
            account: '',
            direction: '',
            time: [],
            accountList: []
        }
        this.state = {
           
            
            ...this.defaultState,
            tradeNumberSum: '',// 总量
            tradeVolumeSum: ''// 总金额
        }
    }
    componentDidMount() {
        this.requestTable();
        console.log(this)
        // this.cooo()
    }
    requestTable = async (currIndex, currSize) => {
        let {tradeMarket, platform,account,direction,time,pageIndex,pageSize} = this.state;
        let dealTimeS = time.length > 0 ? moment(time[0]).format('x'): '';
        let dealTime = time.length > 0 ? moment(time[1]).format('x'): '';
        pageIndex = currIndex || pageIndex;
        pageSize = currSize || pageSize;
        let data = await this.request({url: '/qtTransactionRecord/list', type: 'post'}, {tradeMarket, platform,account,direction,pageIndex,pageSize,dealTimeS,dealTime});
        let dataTotal = await this.request({url: '/qtTransactionRecord/sum', type: 'post'}, {tradeMarket, platform,account,direction,dealTimeS,dealTime});
        this.setState({
            dataSource: data.list || [],
            pageTotal: data.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,
            tradeNumberSum: dataTotal.tradeNumberSum,
            tradeVolumeSum: dataTotal.tradeVolumeSum
        })

    };
    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '平台', dataIndex: 'platform' },
            { title: '账号', dataIndex: 'account' },
            { title: '方向', dataIndex: 'direction', render: text => text == 1 ? <span style={{ color: '#008000'}}>买</span> : <span style={{ color: '#CC0000'}}>卖</span> },
            { title: '交易市场', dataIndex: 'tradeMarket' },
            { title: '成交时间', dataIndex: 'dealTime', render: text => text ? moment(text).format(TIMEFORMAT_ss) : '' },
            { title: '成交价', dataIndex: 'transactionPrice', render: text => toThousands(text, true) },
            { title: '成交金额', dataIndex: 'tradeVolume',  render: text => toThousands(text, true) },
            { title: '成交量', dataIndex: 'tradeNumber' },
            { title: '手续费', dataIndex: 'serviceCharge',  render: text => toThousands(text, true) },
        ]
    };
    exportExcel=async ()=> {
        let {tradeMarket, platform,account,direction,time} = this.state;
        let dealTimeS = time.length > 0 ? moment(time[0]).format('x'): '';
        let dealTime = time.length > 0 ? moment(time[1]).format('x'): '';
        let data =  await this.request({url: '/qtTransactionRecord/excel', type: 'post'}, {tradeMarket, platform,account,direction,dealTimeS,dealTime});
        window.open(data);
    };
    render() {
        const { time, pageIndex, pageSize, pageTotal, dataSource,direction,tradeMarket,platform,account,showHide,tradeNumberSum,tradeVolumeSum } = this.state;
        return (
            <div className="right-con">
                <div className="page-title">
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && (
                            <div className="x_panel">
                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <HedgeMarketList market={tradeMarket} col='3' title='市场' handleChange={val=>this.onSelectChoose(val, 'tradeMarket')} />
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                         <PlatformsList platform={platform} col='3' title='平台' handleChange={val=>this.onSelectChoose(val, 'platform')}/>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <AccountList account={account} col='3' platform={platform} handleChange={val=>this.onSelectChoose(val, 'account')}/>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">方向：</label>
                                            <div className="col-sm-8 ">
                                                <Select value={direction} style={{ width: SELECTWIDTH }} onChange={val=>this.onSelectChoose(val, 'direction')}>
                                                    <Option value="">请选择</Option>
                                                    <Option value={1}>买</Option>
                                                    <Option value={0}>卖</Option>
                                                </Select>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">时间：</label>
                                            <div className="col-sm-8">
                                                <RangePicker
                                                    showTime={{
                                                        defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                    }}
                                                    format="YYYY-MM-DD HH:mm:ss"
                                                    placeholder={['Start Time', 'End Time']}
                                                    onChange={(date,dateString)=>this.onChangeCheckTime(date,dateString,'time')}
                                                    value={time}
                                                />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-6 col-sm-6 col-xs-6 right">
                                        <div className="right">
                                            <Button type="primary" onClick={()=>this.requestTable(PAGEINDEX)}>查询</Button>
                                            <Button type="primary" onClick={this.resetState}>重置</Button>
                                            <Button type="primary" disabled={!(dataSource.length > 0)} onClick={this.exportExcel}>导出</Button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                        <tr className="headings">
                                            <th style={{ textAlign: 'left' }} colSpan="8" className="column-title">
                                                总量：{tradeNumberSum}，
                                                总金额：{toThousands(tradeVolumeSum,true)}
                                            </th>
                                        </tr>
                                        </thead>
                                    </table>
                                    <CommonTable
                                        dataSource={dataSource}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSize: pageSize,
                                                current: pageIndex

                                            }
                                        }
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
export default RecordHedgedTrading