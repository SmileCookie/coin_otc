import React, { Component } from 'react'
import axios from '../../../utils/fetch';
import qs from 'qs';
import moment from 'moment'
import { Table,message,DatePicker,Button , Row, Col,Input,Select} from 'antd'
import { PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,PAGRSIZE_OPTIONS20,DOMAIN_VIP } from '../../../conf'
import MarketList from '../../common/select/marketrequests'
import {toThousands} from '../../../utils'
const Column  = Table.Column
const Option = Select.Option;
const { RangePicker} = DatePicker

export default class AccountInfor extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            tableSource: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            time:[],
            entrustmarket:'',
            createtimeS:'',
            createtimeE:''
        }
    }
    componentDidMount() {
        this.requestTable()
    }
    clickHide = () => {
        this.setState({
            showHide:!this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    resetState = () => {
        this.setState({
            time:[],
            entrustmarket:'',
            createtimeS:'',
            createtimeE:''
        })
    }
    //市场
    handleSelectMarket = val => {
        this.setState({
            entrustmarket:val
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize,entrustmarket,createtimeS,createtimeE} = this.state
        axios.post(DOMAIN_VIP+'/coinQtMarket/list',qs.stringify({
            entrustmarket,createtimeS,createtimeE,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    tableSource:tableSource,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    onChangePageNum = (pageIndex,pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        // console.log(date, dateString);
        this.setState({
            createtimeS:dateString[0]&&moment(dateString[0]).format('x'),
            createtimeE:dateString[1]&&moment(dateString[1]).format('x'),
            time:date
        })
    }
    render() {
        const { showHide, tableSource, pageIndex, pageSize, pageTotal,time,entrustmarket } = this.state
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
                                <div >
                                    <Row>
                                        <Col span={7}>
                                        <Row>
                                            <Col span={5}>
                                                    <label className="control-label normalLabel">用户编号:</label>
                                            </Col>
                                            <Col span={19}>
                                                    <input type="text" className="form-control normalInputWidth" name="username" ></input>
                                            </Col>
                                        </Row>
                                        </Col>
                                        <Col span={7}>
                                            <Row>
                                                <Col span={4}>
                                                    <label className="control-label normalLabel">盈亏:</label>
                                                </Col>
                                                <Col span={20}>
                                                    <Select placeholder="请选择" style={{ width: 216 }}>
                                                        <Option value="jack">Jack</Option>
                                                        <Option value="lucy">Lucy</Option>
                                                    </Select>
                                                </Col>
                                        </Row>
                                        </Col>
                                        <Col span={6}>
                                            <Row>
                                                <Col span={4}>
                                                    <label className="control-label normalLabel">时间：</label>
                                                </Col>
                                                <Col span={20}>
                                                    <RangePicker
                                                        showTime={{
                                                            defaultValue: [moment('00:00:00', 'HH:mm,ss'), moment('23:59:59', 'HH:mm,ss')]
                                                        }}
                                                        style={{width:'216px'}}
                                                        format={ TIMEFORMAT_ss }
                                                        placeholder={['Start Time', 'End Time']}
                                                        onChange={this.onChangeCheckTime}
                                                        value={time} />
                                                </Col>
                                            </Row>
                                        </Col>
                                        <Col span={4} className="right">
                                            <div className="form-group right">
                                                <Button type="primary" onClick={this.inquiry}>查询</Button>
                                                <Button type="primary" onClick={this.resetState}>重置</Button>
                                            </div>
                                        </Col>
                                    </Row>
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
                                        <Column title='用户编号' dataIndex='code' key='code' />
                                        <Column title='账户余额' dataIndex='money' key='money' />
                                        <Column title='盈亏资金(USDT)' dataIndex='nums_1' key='nums_1' />
                                        <Column title='盈亏比例' dataIndex='nums_2' key='nums_2' />
                                        <Column title='提现资金(USDT )' dataIndex='nums_3' key='nums_3' />
                                        <Column title='资金停留时间（天）' dataIndex='nums_4' key='nums_4' />
                                        <Column title='时间' dataIndex='nums_5' key='nums_5' />
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