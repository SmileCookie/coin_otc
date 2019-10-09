import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Modal,Table,Tabs } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,PAGRSIZE_OPTIONS,PAGESIZE_50,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { toThousands } from '../../../utils'
import SelectAType from '../select/selectAType'
import SelectBType from '../select/selectBType'
import HandicapMarket from '../../common/select/handicapMarketFT'
const { RangePicker } = DatePicker
const Option = Select.Option
const { Column } = Table
const TabPane = Tabs.TabPane;

export default class CapitalChangeRecordFT extends React.Component{
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
            fundstype:'0',
            market:'',
            id:'',
            businessid:'',
            type:'0',
            feesStart:'',
            feesEnd:'',
            amountStart:'',
            amountEnd:'',  
            balanceStart:'',
            balanceEnd:'',
            username:'',
            createtimeS:'',
            createtimeE:'',
            typeName:1,
            tableSourceNow:[],
            tableSourceAgo:[],
            pageTotalAgo:0,
            pageTotalNow:0,
            userid:''

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
            fundstype:'0',
            market:'',
            id:'',
            businessid:'',
            type:'0',
            feesStart:'',
            feesEnd:'',
            amountStart:'',
            amountEnd:'',  
            balanceStart:'',
            balanceEnd:'',
            username:'',
            createtimeS:'',
            createtimeE:'',
            userid:''
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
    //业务类型
    selectBusinessType = v => {
        this.setState({
            type:v
        })
    }
    //资金类型
    handleSelectFundsType = v => {
        this.setState({
            fundstype:v
        })
    }
    //市场
    handleSelectMarket = v => {
        this.setState({
            market:v
        })
    }
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            createtimeS:dateString[0]&&moment(dateString[0]).format('x'),
            createtimeE:dateString[1]&&moment(dateString[1]).format('x'),
            time:date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize,pagination,fundstype,market,id,businessid,type,feesEnd,feesStart,amountEnd,amountStart,balanceEnd,balanceStart,username,createtimeE,createtimeS,typeName,userid } = this.state
        axios.post(DOMAIN_VIP+'/billFutures/list',qs.stringify({
            fundstype,market,id,businessid,type,feesEnd,feesStart,amountEnd,amountStart,balanceEnd,balanceStart,username,createtimeE,createtimeS,typeName,userid,
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
                // pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange;
                pagination.defaultPageSize = result.data.pageSize;
                if(typeName==1){
                    this.setState({
                        tableSourceNow:tableSource,
                        pagination,
                        pageTotalNow:result.data.totalCount
                    })
                }else{
                    this.setState({
                        tableSourceAgo:tableSource,
                        pagination,
                        pageTotalAgo:result.data.totalCount
                    })
                }
                // this.setState({
                //     tableSource:tableSource,
                //     pagination,
                // })
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
    //tabs 回调
    callbackTabs= key =>{
        const { pageIndex , pageSize } = this.state
        this.setState({
            typeName:key,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE
        })
        // this.requestTable(pageIndex,pageSize,this.props.curId,key)
    }
    render(){
        const { typeName,tableSourceAgo,tableSourceNow,pageTotalAgo,pageTotalNow,showHide,pagination,pageIndex,pageSize,pageTotal,time,fundstype,market,id,businessid,type,feesEnd,feesStart,amountEnd,amountStart,balanceEnd,balanceStart,username,createtimeE,createtimeS,userid } = this.state
        const tableSource = typeName==1?tableSourceNow:tableSourceAgo;
        pagination.total = typeName==1?pageTotalNow:pageTotalAgo;
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 财务中心 > 期货流水明细 > 资金变动记录
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <form className='x_content' onSubmit={this.inquiry}>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={market} handleChange={this.handleSelectMarket} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectAType  findsType={fundstype} handleChange={this.handleSelectFundsType}/>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name="username" className="form-control" value={username} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name="userid" className="form-control" value={userid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">账单流水号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='id' className="form-control" value={id} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">业务ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='businessid' className="form-control" value={businessid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectBType title='业务类型:' col='3' billType={type} handleChange={this.selectBusinessType} />
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">费用:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='feesStart' className="form-control" value={feesStart} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='feesEnd' className="form-control" value={feesEnd} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发生金额:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='amountStart' className="form-control" value={amountStart} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='amountEnd' className="form-control" value={amountEnd} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">账户余额:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name="balanceStart" className="form-control" value={balanceStart} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name="balanceEnd" className="form-control" value={balanceEnd} onChange={this.handleInputChange}/>
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
                            </form>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <Tabs onChange={this.callbackTabs}>
                                    <TabPane tab="最近3天账单" key="1"></TabPane>
                                    <TabPane tab="3天前账单" key="2"></TabPane>
                                </Tabs>
                                <div className="table-responsive">
                                    <Table dataSource={tableSource} bordered pagination={pagination} onChange={this.sorter} locale={{emptyText:'暂无数据'}} >
                                        <Column title='序号'  dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='资金类型' dataIndex='fundstypeName' key='fundstypeName'/>
                                        <Column title='用户编号'  dataIndex='userid' key='userid'/>
                                        <Column title='期货市场' dataIndex='market' key='market'/>
                                        <Column title='业务ID' dataIndex='businessid' key='businessid'/>
                                        <Column title='业务类型' dataIndex='typeName' key='typeName'/>{/* 转入、转出、平仓、开仓、资金费用*/}
                                        <Column title='发生金额' className='moneyGreen' sorter dataIndex='amount' key='amount' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='账户余额' className='moneyGreen' sorter dataIndex='balance' key='balance' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='费用' className='moneyGreen' sorter dataIndex='fees' key='fees' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='账单流水号' dataIndex='id' key='id'/>
                                        <Column title='记账时间'  dataIndex='createtime' render={(text)=>(
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