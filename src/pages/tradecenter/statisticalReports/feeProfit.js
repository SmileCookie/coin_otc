/**数据中心 》 统计报表 》 币币报表 》 币币收益 */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,SELECTWIDTH } from '../../../conf'
import SelectAType from '../select/selectAType'
import SelectBType from '../select/selectBType'
import moment from 'moment'
import { toThousands } from '../../../utils'
import { DatePicker,Select,Modal, Button ,Table,Tabs,Pagination,message } from 'antd'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const Big = require('big.js')
const { Column } = Table;

export default class FeeProfit extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            fundsType:'0',
            showHide:true,
            feeType:'',
            surverType:'1',
            tableList:[],
            totalAmount:[],
            startDate:'',
            endDate:'',
            time:[]
        }

        this.clickHide = this.clickHide.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleFundChange = this.handleFundChange.bind(this)
        this.handleChangeSurver = this.handleChangeSurver.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleChangeFee = this.handleChangeFee.bind(this)
        this.changPageNum = this.changPageNum.bind(this);
        this.onShowSizeChange = this.onShowSizeChange.bind(this);
    }

    componentDidMount(){
        // this.requestTable()
    }
    
    requestTable(){
        const { feeType,startDate,endDate,fundsType,surverType } = this.state
        axios.post(DOMAIN_VIP+"/feeProfit/query",qs.stringify({
            feeType,startDate,endDate,fundsType,surverType,
            loadFlag:1
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                let totalAmount = []
                let i = 0;
                result.data.map((item,index)=>{
                    item.index = index + 1
                })
                for(let k in result.totalAmount){
                    totalAmount[i] = {}
                    totalAmount[i].index = i + 1;
                    totalAmount[i].typeName = k
                    totalAmount[i].amount = result.totalAmount[k]
                    i++;                 
                }
                this.setState({
                    tableList:result.data,
                    totalAmount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //时间控件
    onChangeTime(date, dateString) {
        this.setState({
            startDate:dateString[0],
            endDate:dateString[1],
            time:date
        })
    }      
    //手续费收益
    handleChangeFee(val){
        this.setState({
            feeType:val
        })
    }
    //统计类型
    handleChangeSurver(val){
        this.setState({
            surverType:val
        })
    }  
    //资金类型
    handleFundChange(val){
        this.setState({
            fundsType:val
        })
    }

    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    //重置状态
    onResetState(){
        this.setState({
            fundsType:'0',
            feeType:'',
            surverType:'1',
            startDate:'',
            endDate:'',
            time:[]
        })
    }

    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestTable())

    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requestTable())
    }
    render(){
        const { fundsType,showHide,feeType,surverType,tableList,time,totalAmount } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 币币报表 > 币币收益
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div className="x_panel"> 
                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <SelectAType findsType={fundsType} col='3' handleChange={this.handleFundChange}></SelectAType>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">费用类型：</label>
                                            <div className="col-sm-8">
                                                <Select value={feeType} style={{ width: SELECTWIDTH }} onChange={this.handleChangeFee}>
                                                    <Option value="">请选择</Option>
                                                    <Option value="3">提现手续费</Option>
                                                    <Option value="1">交易手续费</Option>
                                                    <Option value="2">借贷手续费</Option>
                                                </Select>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">统计类型：</label>
                                            <div className="col-sm-8">
                                                <Select value={surverType} style={{ width: SELECTWIDTH }} onChange={this.handleChangeSurver}>
                                                    <Option value="4">年</Option>
                                                    <Option value="3">月</Option>
                                                    <Option value="2">周</Option>
                                                    <Option value="1">日</Option>                                                    
                                                </Select>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">统计时间：</label>
                                            <div className="col-sm-8">
                                                <RangePicker onChange={this.onChangeTime} value={time} />
                                            </div>
                                        </div>
                                    </div>

                                    <div className="col-md-4 col-sm-4 col-xs-4 right">
                                        <div className="right">
                                            <Button type="primary" onClick={this.requestTable}>查询</Button>
                                            <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        </div>
                                    </div>

                                </div>
                            </div>
                        }
                        
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="col-md-8 col-sm-8 col-xs-8">
                                    <div className="table-responsive">
                                        <Table
                                            dataSource={tableList}
                                            bordered={true}
                                            locale={{emptyText:'暂无数据'}}>
                                            <Column title='序号' dataIndex='index' key='index' />
                                            <Column title='统计日期' dataIndex='timestr' key='timestr'/>
                                            <Column title='资金类型' dataIndex='currency' key='currency'  />
                                            <Column title='费用类型' dataIndex='typeName' key='typeName'/>
                                            <Column title='统计金额' dataIndex='amount' key='amount' sorter="true" className= "moneyGreen" render={(text)=>{
                                                return <React.Fragment>{toThousands(text)}</React.Fragment>
                                            }} />
                                        
                                        </Table>
                                    </div>
                                </div>

                                <div className="col-md-4 col-sm-4 col-xs-4">
                                    <div className="table-responsive">
                                        <Table
                                            dataSource={totalAmount}
                                            bordered={true}
                                            locale={{emptyText:'暂无数据'}}
                                            >
                                            <Column title='序号' dataIndex='index' key='index' />
                                            <Column title='资金类型' dataIndex='typeName' key='typeName'/>
                                            <Column title='合计金额' dataIndex='amount' key='amount' sorter="true" className= "moneyGreen" render={(text)=>{
                                                return <React.Fragment>{toThousands(text,true)}</React.Fragment>
                                            }} />
                                        
                                        </Table>
                                    </div>
                                </div>

                            </div>
                        </div>

                    </div>
                </div>
            </div>
        )
    }

}


























