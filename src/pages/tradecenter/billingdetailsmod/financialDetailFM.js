import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { PAGEINDEX, PAGESIZE, PAGRSIZE_OPTIONS20, TIME_PLACEHOLDER, TIMEFORMAT_ss, SHOW_TIME_DEFAULT, TIMEFORMAT } from '../../../conf'
import SelectBType from '../select/selectBType'
import FundsTypeList from '../../common/select/fundsTypeList'
import moment from 'moment'
import { Button, DatePicker, Tabs, Modal, Select, message, Table, Pagination} from 'antd'
import { toThousands, pageLimit, islessBodyWidth } from '../../../utils'
import { CommonHedgeResults, JudgeHedgeResults } from '../../common/select/commonHedgeResults'
import CommonTable from '../../common/table/commonTable'
import Decorator from '../../decorator'
const { MonthPicker, RangePicker, } = DatePicker;
const Option = Select.Option;

@Decorator()
export default class FinancialDetailFM extends React.Component{

    constructor(props){
        super(props)
        this.defaultState = {
            fundsType:'0',
            type:'0',
            id:'',
            userName:'',
            userId:'',
            time: [],
        }
        this.state = {
            ...this.defaultState,
            hedgeResults: {}
        }
        this.handleChangeFundsType = this.handleChangeFundsType.bind(this)
        this.handleChangeBillType = this.handleChangeBillType.bind(this)
    }

	componentDidMount() {
        this.requestTable()
    }
// 查询按钮
    inquireBtn() {

        this.setState({
            pageIndex: 1
        },()=>{

            this.requestTable()
        })
        

    }

    requestTable = async (currIndex, currSize, currId) => {
        const { pageIndex, pageSize, time, fundsType, type, id, userId, userName, } = this.state
        let params = {
        	id, 
        	fundstype: fundsType === '0' ? '' : fundsType,
        	type: type === '0' ? '': type,
        	createTimeStart: time.length ? moment(time[0]).format('YYYY-MM-DD HH:mm:ss') : '',
        	createTimeEnd: time.length ? moment(time[1]).format('YYYY-MM-DD HH:mm:ss') : '',
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize,
            userid:currId||userId,
            username: userName
        }
		const result = await this.request({ url: '/financialBill/list', type: 'post' }, params)
        this.setState({
            tableSource: result.list,
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })
    }
    // 资金类型
    handleChangeFundsType(value) {
        this.setState({
            fundsType: value
        })
    }
    //账单类型 Select
    handleChangeBillType(val){
        this.setState({
            type:val
        })
    }
    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '资金类型', dataIndex: 'coinTypeName' },
            { title: '用户编号', dataIndex: 'userid' },
            { title: '账单流水号', dataIndex: 'id' },
            { title: '账单类型', dataIndex: 'typeName', },
            { title: '记账时间', dataIndex: 'createtime', render: text => text ? moment(text).format(TIMEFORMAT) : '' },
            { title: '发生额', dataIndex: 'amount', render: (text)=>toThousands(text,true) },
            { title: '手续费', dataIndex: 'fees', render: (text)=>toThousands(text,true) },
            { title: '余额', dataIndex: 'balance', render: (text)=>toThousands(text,true) },
            { title: '备注', dataIndex: 'remark' },
        ]
    }

    render(){
        const { showHide, fundsType, type, id, userName, userId, time, pageIndex, pageSize, pageTotal, tableSource } = this.state;
        return(
            <div className="right-con">
				<div className="page-title">
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList  fundsType={fundsType} handleChange={this.handleChangeFundsType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-2 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-2 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-2 control-label">流水编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="id" value={id} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <SelectBType billType={type} col='3' handleChange={this.handleChangeBillType}></SelectBType>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">记账时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: SHOW_TIME_DEFAULT
                                                }}
                                                format={TIMEFORMAT_ss}
                                                placeholder={TIME_PLACEHOLDER}
                                                onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-4 col-sm-4 col-xs-4 right marTop">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.inquireBtn()}>查询</Button> 
                                        <Button type="primary" onClick={this.resetState}>重置</Button>  
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <CommonTable
                                        dataSource={tableSource}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSize: pageSize,
                                                current: pageIndex
                                            }
                                        }
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}
                                        // scroll={islessBodyWidth() ? { x: 1800 } : {}}
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