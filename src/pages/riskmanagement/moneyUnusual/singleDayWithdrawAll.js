//单日累计提现
import React, { Component } from 'react'
import axios from '../../../utils/fetch';
import qs from 'qs';
import moment from 'moment'
import FundsTypeList from '../../common/select/fundsTypeList'
import { Table,message,DatePicker,Button , Row, Col,Input,Select} from 'antd'
import { PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,PAGRSIZE_OPTIONS20,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT} from '../../../conf'
const Column  = Table.Column
const Option = Select.Option;
const { RangePicker} = DatePicker


export default class  SingleDay extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            userid:'',
            tableSource: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            time:[],
            entrustmarket:'',
            createtimeS:'',
            createtimeE:'',
            fundstype:'0',
            money_min:'',
            money_max:''
        }
        this.requestTable = this.requestTable.bind(this)
        this.addInforPage = this.addInforPage.bind(this)
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
            userid:'',
            time:[],
            entrustmarket:'',
            createtimeS:'',
            createtimeE:'',
            fundstype:'0',
            money_min:'',
            money_max:'',
        })
    }

    selectFundsType = v => {
        this.setState({
           fundstype:v
        })
    }
    //市场
    handleSelectMarket = val => {
        this.setState({
            entrustmarket:val
        })
    }
     //跳转详情提现审核
     addInforPage(){
        const {_this} = this.props;
        let _obj =  {
            key:200300030000,
            name:"提现审核",
            url:"/systemcenter/paymentMod/withdrawApprove"
        }
        _this.add(_obj)
    }
    //input 信息改变
    handleChangeInput = (e) => {
        const target = e.target;
            const value = target.type === 'checkbox' ? target.checked : target.value;
            const name = target.name;
            this.setState({
                [name]: value
            });
    }
    //查询
    clickInquireState = () => {
        this.setState({
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
        })
        this.requestTable(PAGEINDEX, PAGESIZE);
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize,entrustmarket,createtimeS,createtimeE,userid,fundstype,money_min,money_max} = this.state
        axios.post(DOMAIN_VIP+'/coinChangeDaywithdrawal/list',qs.stringify({
            // fundstype:2,
            // entrustmarket,createtimeS,createtimeE,
             userid,
             createtimeS,
             amountS:money_min,
             amountE:money_max,
             createtimeE,
             pageIndex:currentIndex||pageIndex,
             pageSize:currentSize||pageSize
        })).then(res => {
            // console.log(res)
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id;
                    tableSource[i].createtime = moment(tableSource[i].createtime).format(TIMEFORMAT)
                }
                this.setState({
                    tableSource:tableSource,
                    pageTotal:result.data.totalCount
                },() => console.log(this.state.tableSource))
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
        const { showHide, tableSource, pageIndex, pageSize, pageTotal,time,entrustmarket,fundstype,userid ,money_min,money_max} = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 风控管理 > 充提异常账户 > 单日累计提现
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide&&<div className="x_panel"> 
                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">用户编号:</label>
                                            <div className="col-sm-8">
                                                <input type="text" className="form-control"  value={userid} name="userid" onChange={this.handleChangeInput}/>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">提现金额:</label>
                                                <div className="col-sm-8">
                                                    <div className="col-sm-4 left sm-box">
                                                        <input type="text" className="form-control" name='money_min' value={money_min} onChange={this.handleChangeInput}/>
                                                    </div>
                                                    <div className="left line34">-</div>
                                                    <div className="col-sm-4 left sm-box">
                                                        <input type="text" className="form-control" name='money_max' value={money_max} onChange={this.handleChangeInput}/>
                                                    </div>
                                                </div>
                                            </div>
                                    </div>
                                    {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <FundsTypeList title='提现币种' fundsType={fundstype} handleChange={this.selectFundsType} />
                                    </div> */}
                                    <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">时间筛选:</label>
                                            <div className="col-sm-8">
                                                <RangePicker 
                                                 showTime={{
                                                    defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                  }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                               onChange={this.onChangeCheckTime }
                                               value={time}/>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-4 col-sm-4 col-xs-4 right">
                                        <div className="right">
                                            <Button type="primary" onClick={() => this.clickInquireState()}>查询</Button>
                                            <Button type="primary" onClick={() =>this.resetState()}>重置</Button>
                                        </div>
                                    </div>

                                </div>
                            </div>
                        }
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
                                        <Column title='序号' dataIndex='index'  render={(text) => (
                                            <span>{text}</span>
                                        )} />
                                        <Column title='用户编号'  dataIndex='userid' key='userid' />
                                        <Column title='累计提现额（日）' dataIndex='amount' className="moneyGreen" key='amount' />
                                        <Column title='累计提现次数（日)'  dataIndex='number' key='number' />
                                        <Column title='发生时间'  dataIndex='createtime' key='createtime'  />
                                        <Column  title='详情'   key='nums_5' render={() =>(
                                            <a onClick={this.addInforPage} href="javascript:;">查看</a>
                                        )}  />
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