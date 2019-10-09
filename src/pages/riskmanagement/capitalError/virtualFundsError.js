//虚拟资金异常
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


export default class  VirtualFundsError extends Component {
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
            fundstype:'0'
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
        const { pageIndex,pageSize,entrustmarket,createtimeS,createtimeE,userid,fundstype} = this.state
        axios.post(DOMAIN_VIP+'/coinChangeInvented/list',qs.stringify({
             fundstype,
             userid,
             createtimeS,
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
        const { showHide, tableSource, pageIndex, pageSize, pageTotal,time,entrustmarket,fundstype,userid } = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 风控管理 > 资金异常 > 虚拟资金异常
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide&&<div className="x_panel"> 
                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                         <FundsTypeList title='币种：' fundsType={fundstype} handleChange={this.selectFundsType} />
                                    </div>
                                    {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <FundsTypeList title='提现币种' fundsType={fundstype} handleChange={this.selectFundsType} />
                                    </div> */}
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
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
                                        <Column title='币种'  dataIndex='fundstypename' key='fundstypename' />
                                        <Column title='平台用户总余额' dataIndex='useramount' className="moneyGreen" key='useramount' />
                                        <Column title='区块钱包总余额' className="moneyGreen" dataIndex='walletamount' key='walletamount' />
                                        <Column title='补充金额' className="moneyGreen" dataIndex='addamount' key='addamount'  />
                                        <Column title='时间'  dataIndex='createtime' key='createtime'  />
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