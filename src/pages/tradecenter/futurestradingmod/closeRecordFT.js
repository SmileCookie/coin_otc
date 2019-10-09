import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Modal,Table } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { toThousands } from '../../../utils'
// import ModalMochikuraDetailsFT from './modal/modalMochikuraDetailsFT'
import HandicapMarket from '../../common/select/handicapMarketFT'
const { RangePicker} = DatePicker
const Option = Select.Option
const {Column} = Table

export default class CloseRecordFT extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            time:[],
            visible:false,
            modalHtml:'',
            title:'',
            width:'',
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
            userid:'',
            futuresid:'',
            createtimeS:'',
            createtimeE:'',
            amountS:'',
            amountE:'',
            realizedpnlS:'',
            realizedpnlE:'',
            commissionadvanced:'',
            leveragetype:'',
            side:'',
            totalValue:0,
            totalAmount:0,
            totalRealizedpnl:0,
            totalCommissionadvanced:0,
            id:''
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
            userid:'',
            futuresid:'',
            createtimeS:'',
            createtimeE:'',
            amountS:'',
            amountE:'',
            realizedpnlS:'',
            realizedpnlE:'',
            commissionadvanced:'',
            leveragetype:'',
            side:'',
            time:[],
            id:''
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
    //市场
    handleSelectMarket = val => {
        this.setState({
            futuresid:val
        })
    }
    //平仓方向
    handleMochikuraType = val => {
        this.setState({
            side:val
        })
    }
    //杠杆类型
    selectLever = val => {
        this.setState({
            leveragetype:val
        })
    }
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize,pagination,userid,futuresid,createtimeS,createtimeE,amountS,amountE,realizedpnlS,realizedpnlE,leveragetype,side,commissionadvanced,id } = this.state
        axios.post(DOMAIN_VIP+'/positionChangeRecord/closedList',qs.stringify({
            userid,futuresid,createtimeS,createtimeE,amountS,amountE,realizedpnlS,realizedpnlE,leveragetype,side,commissionadvanced,id,
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
                    totalValue:result.totalValue,
                    totalAmount:result.totalAmount,
                    totalRealizedpnl:result.totalRealizedpnl,
                    totalCommissionadvanced:result.totalCommissionadvanced
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
    checkDetail = item => {
        this.footer= [
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        this.setState({
            visible:true,
            title:"详情",
            width:'1200px',
            modalHtml:''
        })
    }
    sorter = (pagination, filters, sorter) =>{
        // console.log(pagination, filters, sorter&&sorter.order&&sorter.order.slice(0,3)?sorter.order.slice(0,3):'')
        console.log(sorter)
    }
    render(){
        const { showHide,tableSource,pagination,time,width,modalHtml,visible,title,userid,futuresid,amountS,amountE,realizedpnlS,realizedpnlE,leveragetype,side,commissionadvanced,id,totalValue,totalAmount,totalRealizedpnl,totalCommissionadvanced } = this.state
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 数据中心 > 期货交易中心 > 盘口管理 > 平仓记录
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={futuresid} handleChange={this.handleSelectMarket} />
                                </div>                            
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">平仓编号:</label>{/* 新增*/}
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="id" value={id} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">//去掉
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='' value={''} onChange={this.handleInputChange}/>
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div> */}
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">//去掉
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">平仓ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='' className="form-control" value={''} onChange={this.handleInputChange}/>
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">平仓方向:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={side}  onChange={this.handleMochikuraType}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='0' value={0}>做空平仓</Option>
                                                <Option key='1' value={1}>做多平仓</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">杠杆类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={leveragetype} onChange={this.selectLever}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='0' value={'0'}>逐仓</Option>
                                                <Option key='1' value={'1'}>全仓</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">平仓佣金:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="commissionadvanced" value={commissionadvanced} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">平仓数量:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='amountS' className="form-control" value={amountS} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='amountE' className="form-control" value={amountE} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">平仓盈亏:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='realizedpnlS' className="form-control" value={realizedpnlS} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='realizedpnlE' className="form-control" value={realizedpnlE} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">已实现盈亏:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='' className="form-control" value={''} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='' className="form-control" value={''} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">未实现盈亏:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='' className="form-control" value={''} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" name='' className="form-control" value={''} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">平仓时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker 
                                                showTime={{
                                                    defaultValue:[moment('00:00:00','HH:mm,ss'),moment('23:59:59','HH:mm,ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onChangeCheckTime}
                                                value={time}/>
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
                                                    平仓价值：{toThousands(totalValue,true)},平仓数量：{totalAmount},平仓盈亏：{toThousands(totalRealizedpnl,true)}平仓佣金：{toThousands(totalCommissionadvanced,true)}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table dataSource={tableSource} bordered pagination={pagination} onChange={this.sorter} locale={{emptyText:'暂无数据'}}>
                                        <Column title='序号' dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='期货市场' dataIndex='futuresid' key='futuresid'/>
                                        <Column title='用户编号' dataIndex='userid' key='userid'/>
                                        <Column title='平仓编号' dataIndex='businessid' key='businessid'/>
                                        <Column title='平仓方向' dataIndex='side' key='side' render={(text)=>(
                                            <span>{text==1?'做多平仓':'做空平仓'}</span>
                                        )}/>
                                        <Column title='平仓价值' className='moneyGreen' dataIndex='value' key='value' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='平仓数量' sorter dataIndex='amount' key='amount'/>
                                        <Column title='平仓价格' className='moneyGreen' dataIndex='closePrice' key='closePrice' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        {/* <Column title='强平价格' sorter dataIndex='' key=''/> */}
                                        <Column title='杠杆倍数' dataIndex='leverage' key='leverage'/>
                                        <Column title='杠杆类型' dataIndex='leveragetype' key='leveragetype' render={(text)=>{
                                            switch(text){
                                                case 1:
                                                    return '全仓'
                                                    break;
                                                case 0:
                                                    return '逐仓'
                                                    break;
                                                default:
                                                    return '--'
                                                    break;

                                            }
                                        }}/>
                                        {/* <Column title='平仓保证金' dataIndex='' key=''/> */}
                                        {/* <Column title='平仓佣金预收' dataIndex='' key=''/> */}
                                        <Column title='平仓盈亏' className='moneyGreen' sorter dataIndex='realizedpnl' key='realizedpnl' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='平仓佣金' className='moneyGreen' sorter dataIndex='commissionadvanced' key='commissionadvanced' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='平仓时间' dataIndex='createtime' key='createtime' render={(text)=>(
                                            <span>{text?moment(text).format(TIMEFORMAT):"--"}</span>
                                        )}/>
                                        <Column title='详情' dataIndex='' key='' render={(text,record)=>(
                                            <a href='javascript:void(0);' onClick={()=>this.checkDetail(record)}>一键追踪</a>
                                        )}/>
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    width={width}
                    title={title}
                    onOk={this.handleOk}
                    footer={this.footer}
                    onCancel={this.handleCancel}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}