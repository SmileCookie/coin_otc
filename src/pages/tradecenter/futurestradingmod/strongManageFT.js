import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Table } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,PAGRSIZE_OPTIONS20} from '../../../conf'
import HandicapMarket from '../../common/select/handicapMarketFT'
const { RangePicker} = DatePicker
const Option = Select.Option
const {Column} = Table

export default class StrongManageFT extends React.Component{
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
            tableList:[]
        }
    }
    componentDidMount(){
        // this.requestTable()
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
            pageSize:PAGESIZE
        },()=>this.requestTable())       
    }
    resetState = () => {
        this.setState({

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
            user:val
        })
    }
    //交易类型
    selectBusiness = val => {
        this.setState({

        })
    }
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    //是否穿仓
    handleWearHouse = val => {
        this.setState({

        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize} = this.state
        axios.post(DOMAIN_VIP+'',qs.stringify({

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
                pagination.onChange = self.changPageNum;
                pagination.onShowSizeChange = self.onShowSizeChange
                self.setState({
                    tableSource:tableSource,
                    pagination,
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
    sorter = (pagination, filters, sorter) =>{
        // console.log(pagination, filters, sorter&&sorter.order&&sorter.order.slice(0,3)?sorter.order.slice(0,3):'')
        console.log(sorter)
    }
    render(){
        const { showHide,tableSource,pagination,pageIndex,pageSize,pageTotal,time,width,modalHtml,visible,title,tableList } = this.state
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 数据中心 > 期货交易中心 > 强平管理
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={''} handleChange={this.handleSelectMarket} />
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={''} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" value={''} onChange={this.handleInputChange}/>
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">持仓ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='' className="form-control" value={''} onChange={this.handleInputChange}/>
                                            {/* <b className="icon-fuzzy">%</b> */}
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">成交编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='' className="form-control" value={''} onChange={this.handleInputChange}/>
                                            {/* <b className="icon-fuzzy">%</b> */}
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={''} onChange={this.selectBusiness}>
                                                <Option key='0' value={0}>多平</Option>
                                                <Option key='1' value={1}>空平</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">是否穿仓:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={''} onChange={this.handleWearHouse}>
                                                <Option key='0' value={0}>否</Option>
                                                <Option key='1' value={1}>是</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">爆仓数量:</label>
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
                                        <label className="col-sm-3 control-label">爆仓价格:</label>
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
                                        <label className="col-sm-3 control-label">盈亏:</label>
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
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保险基金:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" value={''} onChange={this.handleInputChange}/>
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">爆仓时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker 
                                                showTime={{
                                                    defaultValue:[moment('00:00:00','HH:mm,ss'),moment('23:59:59','HH:mm,ss')]
                                                }}
                                                format={{TIMEFORMAT_ss}}
                                                onChange={this.onChangeCheckTime}
                                                value={time}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        <Button type="primary" onClick={this.resetState}>暂时没有对接口</Button>
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
                                                    爆仓数量：价值：保证金：盈亏：保证基金：
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <Table tableSource={tableSource} bordered pagination={pagination} onChange={this.sorter} locale={{emptyText:'暂无数据'}} >
                                        <Column title='序号'  dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='期货市场'  dataIndex='' key=''/>
                                        <Column title='用户编号' dataIndex='' key=''/>
                                        <Column title='成交编号' dataIndex='' key=''/>
                                        <Column title='持仓ID' dataIndex='' key=''/>
                                        <Column title='交易类型' dataIndex='' key=''/>
                                        <Column title='爆仓数量' dataIndex='' key=''/>
                                        <Column title='价值(BTC)' sorter dataIndex='' key=''/>
                                        <Column title='持仓价格' dataIndex='' key=''/>
                                        <Column title='标记价格' dataIndex='' key=''/>
                                        <Column title='成交价格' sorter dataIndex='' key=''/>
                                        <Column title='爆仓价格' sorter dataIndex='' key=''/>
                                        <Column title='保证金' sorter dataIndex='' key=''/>
                                        <Column title='盈亏' sorter dataIndex='' key=''/>
                                        <Column title='保险基金' sorter dataIndex='' key=''/>
                                        <Column title='爆仓时间'  dataIndex='' key=''/>
                                        <Column title='是否穿仓'  dataIndex='' key=''/>
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