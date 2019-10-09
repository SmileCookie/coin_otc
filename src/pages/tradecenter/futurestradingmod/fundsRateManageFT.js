import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Modal,Table } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,PAGRSIZE_OPTIONS,PAGESIZE_50,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import HandicapMarket from '../../common/select/handicapMarketFT'
import PositionDiretion from '../../common/select/positionDiretion'
import { toThousands } from '../../../utils'
const { RangePicker } = DatePicker
const Option = Select.Option
const {Column} = Table

export default class FundsRateManageFT extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            tableSource:[],
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            time:[],
            userId:'',
            tradeId:'',
            futuresId:'',
            username:'',
            type:'',
            feeMin:'',
            feeMax:'',
            tradeAmountMin:'',
            tradeAmountMax:'',
            tradeTimeMin:'',
            tradeTimeMax:'',
            feesum:0,
            pricesum:0,
            tradeamountsum:0,
            batch:'',
            side:''
        }
    }
    componentDidMount(){
        this.requestTable()
    }
    componentWillUnmount(){

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
            userId:'',
            tradeId:'',
            futuresId:'',
            username:'',
            type:'',
            feeMin:'',
            feeMax:'',
            tradeAmountMin:'',
            tradeAmountMax:'',
            tradeTimeMin:'',
            tradeTimeMax:'',
            batch:'',
        })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name] : value
        })
    }
    //交易类型
    selectBusiness = val => {
        this.setState({
            type:val
        })
    }
    //市场
    handleSelectMarket = val => {
        this.setState({
            futuresId:val
        })
    }
    selectSide = val => {
        this.setState({
            side:val
        })
    }
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        // console.log(date, dateString);
        this.setState({
            tradeTimeMin:dateString[0]&&moment(dateString[0]).format('x'),
            tradeTimeMax:dateString[1]&&moment(dateString[1]).format('x'),
            time:date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pagination,pageSize,userId,username,futuresId,tradeId,tradeAmountMax,tradeAmountMin,tradeTimeMax,tradeTimeMin,type,feeMax,feeMin,batch,side } = this.state
        if(batch!=''&&(!/^[0-9]*$/g.test(batch)||batch.length!=10)){
            message.warning('请输入正确的收取批次格式！')
            return false;
        }
        axios.post(DOMAIN_VIP+'/fundsRate/query',qs.stringify({
            userId,username,futuresId,tradeId,tradeAmountMax,tradeAmountMin,tradeTimeMax,tradeTimeMin,type,feeMax,feeMin,batch,side,
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
                pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource:tableSource,
                    pagination,
                })
            }else{
                message.warning(result.msg);
            }
        })

        axios.post(DOMAIN_VIP+'/fundsRate/sum',qs.stringify({
            userId,username,futuresId,tradeId,tradeAmountMax,tradeAmountMin,tradeTimeMax,tradeTimeMin,type,feeMax,feeMin,batch,side,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    feesum:result.data[0]&&result.data[0].feesum,
                    pricesum:result.data[0]&&result.data[0].pricesum,
                    tradeamountsum:result.data[0]&&result.data[0].tradeamountsum
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
    sorter = (pagination, filters, sorter) =>{
        // console.log(pagination, filters, sorter&&sorter.order&&sorter.order.slice(0,3)?sorter.order.slice(0,3):'')
        console.log(sorter)
    }
    render(){
        const { showHide,tableSource,pagination,pageIndex,pageSize,pageTotal,time,userId,username,futuresId,tradeId,tradeAmountMax,tradeAmountMin,type,feeMax,feeMin,tradeamountsum,feesum,pricesum,batch,side} = this.state
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 数据中心 > 期货交易中心 > 资金费用管理
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={futuresId} handleChange={this.handleSelectMarket} />
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='username' value={username} onChange={this.handleInputChange}/>
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <PositionDiretion title='持仓方向' sideType={side}  handleChange={this.selectSide}/>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">持仓ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='tradeId' value={tradeId} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">收取批次:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='batch' placeholder='格式: 2018111312' value={batch} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">//去掉
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={type} onChange={this.selectBusiness}>
                                                <Option key='0' value={0}>强平增加</Option>
                                                <Option key='1' value={1}>穿仓减少</Option>
                                                <Option key='2' value={2}>收益转出</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div> */}
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">持仓数量:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text"  className="form-control" name='tradeAmountMin' value={tradeAmountMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='tradeAmountMax' value={tradeAmountMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">资金费用:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='feeMin' value={feeMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='feeMax' value={feeMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">记账时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
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
                                    <table style={{margin:'0'}} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{textAlign:'left'}} colSpan="17" className="column-title">
                                                    持仓数量：{toThousands(tradeamountsum,true)},合约价值：{toThousands(pricesum,true)},资金费用：{toThousands(feesum,true)}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table dataSource={tableSource} bordered pagination={pagination} onChange={this.sorter} locale={{emptyText:'暂无数据'}} >
                                        <Column title='序号'  dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='期货市场'  dataIndex='futuresid' key='futuresid'/>
                                        <Column title='用户编号' dataIndex='userid' key='userid'/>
                                        <Column title='持仓ID' dataIndex='tradeid' key='tradeid'/>
                                        <Column title='持仓方向' dataIndex='side' key='side' render={(text)=>(
                                            <span>{text==1?'做多':'做空'}</span>
                                        )}/>{/* 多手获得、手空支付、多手支付、空手获得*/}
                                        <Column title='持仓数量' sorter dataIndex='tradeamount' key='tradeamount'/>
                                        <Column title='成交价格' dataIndex='tradeprice' key='tradeprice' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='合约价值'  dataIndex='pr' key='pr' render={(text,record)=>(
                                            <span>{toThousands((record.tradeamount/record.tradeprice).toFixed(9),true)}</span>
                                        )}/>
                                        <Column title='资金费率'  dataIndex='feeratio' key='feeratio'/>
                                        <Column title='资金费用' className='moneyGreen' sorter dataIndex='fee' key='fee' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='收取批次' dataIndex='tradetime' key='shouqupici' render={(text)=>(
                                            <span>{text?moment(text).format('YYYYMMDDHH'):'--'}</span>
                                        )}/>
                                        <Column title='记账时间' dataIndex='tradetime' key='tradetime' render={(text)=>(
                                            <span>{text?moment(text).format(TIMEFORMAT):'--'}</span>
                                        )}/>
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