import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Table } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { toThousands } from '../../../utils'
const { RangePicker} = DatePicker
const {Column} = Table

export default class LeverAdjustRecordFt extends React.Component{
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
                // hideOnSinglePage:true,
                size:'small',
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            userid:'',
            positionid:'',
            username:'',
            createtimeS:'',
            createtimeE:'',
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
            positionid:'',
            username:'',
            createtimeS:'',
            createtimeE:'',
            time:[]
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
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize,pagination,userid,username,positionid,createtimeE,createtimeS} = this.state
        axios.post(DOMAIN_VIP+'/leverageRecord/list',qs.stringify({
            userid,username,positionid,createtimeE,createtimeS,
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
        this.setState({
            createtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            createtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            time:date
        })
    }
    render(){
        const { showHide,tableSource,pagination,time,width,modalHtml,visible,userid,username,positionid } = this.state
        let clientWidth = document.body.clientWidth
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 财务中心 > 期货流水明细 > 杠杆调整记录
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='username' className="form-control" value={username} onChange={this.handleInputChange}/>
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-3 col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">业务ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='' className="form-control" value={''} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">持仓ID:</label>
                                        <div className="col-sm-8">
                                            <input type="text" name='positionid' className="form-control" value={positionid} onChange={this.handleInputChange}/>
                                            {/* <b className="icon-fuzzy">%</b> */}
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">调整时间:</label>
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
                                    <Table dataSource={tableSource} bordered pagination={pagination} scroll={{x:clientWidth<1700&&1390}} locale={{emptyText:'暂无数据'}}>
                                        <Column title='序号' fixed={clientWidth<1700&&'left'} dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='用户编号' fixed={clientWidth<1700&&'left'} dataIndex='userid' key='userid'/>
                                        <Column title='持仓ID' fixed={clientWidth<1700&&'left'} dataIndex='positionid' key='positionid'/>
                                        {/* <Column title='资金类型' dataIndex='' key=''/> */}
                                        {/* <Column title='业务ID' dataIndex='' key=''/> */}
                                        <Column title='调整后杠杆类型' dataIndex='leveragetype' key='leveragetype' render={(text)=>(
                                            <span>{text?'全仓':'逐仓'}</span>
                                        )}/>
                                        <Column title='调整后杠杆数' dataIndex='leverage' key='leverage'/>
                                        <Column title='调整后持仓保证金' className='moneyGreen' dataIndex='positionmargin' key='positionmargin' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='调整后平仓佣金' className='moneyGreen' dataIndex='commissionadvanced' key='commissionadvanced' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='持仓保证金差额' className='moneyGreen'dataIndex='margindiff' key='margindiff' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='平仓佣金差额' className='moneyGreen' dataIndex='commissiondiff' key='commissiondiff' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='抹平资金费用' className='moneyGreen' dataIndex='fundingcost' key='fundingcost' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='追加保证金' className='moneyGreen' dataIndex='margincall' key='margincall' render={(text)=>(
                                            <span>{toThousands(text,true)}</span>
                                        )}/>
                                        <Column title='调整时间' dataIndex='createtime' key='createtime' render={(text)=>(
                                            <span>{text?moment(text).format(TIMEFORMAT):'--'}</span>
                                        )}/>
                                        {/* <Column title='修改时间' dataIndex='' key='' render={(text)=>(
                                            <span>{text?moment(text).format(TIMEFORMAT):'--'}</span>
                                        )}/>
                                        <Column title='调整状态' dataIndex='' key=''/> */}
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