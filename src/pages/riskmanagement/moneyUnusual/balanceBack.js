//提现后账户余额为X
import React, { Component } from 'react'
import axios from '../../../utils/fetch';
import qs from 'qs';
import moment from 'moment'
import FundsTypeList from '../../common/select/fundsTypeList'
import { Table,message,DatePicker,Button , Row, Col,Input,Select,Modal} from 'antd'
import { PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,PAGRSIZE_OPTIONS20,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT } from '../../../conf'
const Column  = Table.Column
const Option = Select.Option;
const confirm = Modal.confirm;
const { RangePicker} = DatePicker

export default class  BlanceBack extends Component {
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
            createtimeE:'',
            userid:'',
            fundsType:"0",
            money_min:'',
            money_max:'',
            status:'0',
        }
        console.log(props)
        this.addInforPage = this.addInforPage.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.fillterItem  = this.fillterItem.bind(this)
        this.changeStatus = this.changeStatus.bind(this)
        // this.confirm      = this.confirm.bind(this)
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
            createtimeE:'',
            fundsType:"0",
            userid:'',
            money_min:'',
            money_max:'',
            status:'0'
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
            name:"提现审核",
            url:"/systemcenter/paymentMod/withdrawApprove"
        }
        _this.add(_obj)
    }
    selectFundsType = v => {
        this.setState({
            fundsType:v
        })
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
        const { pageIndex,pageSize,entrustmarket,createtimeS,createtimeE,userid,fundsType,money_min,money_max,status} = this.state
        axios.post(DOMAIN_VIP+'/coinChangeBalance/list',qs.stringify({
             userid,
            //  fundstype:fundsType,
             starttime:createtimeS,
             endtime:createtimeE,
             startamount:money_min,
             endamount:money_max,
             state:status,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            console.log(res)
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id,
                    tableSource[i].createtime = moment(tableSource[i].createtime).format(TIMEFORMAT)
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

     //input 信息改变
     handleChangeInput = (e) => {
        const target = e.target;
            const value = target.type === 'checkbox' ? target.checked : target.value;
            const name = target.name;
            this.setState({
                [name]: value
            });
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
    //状态查询
    handleChangeSelect = (e) =>{
        
        this.setState({
            status:e
        },() => console.log(this.state.status))
    }

    changeStatus(id){
        const {status} = this.state;
        axios.post(DOMAIN_VIP+'/coinChangeBalance/update',qs.stringify({id})).then((res) =>{
                let result = res.data  
                if(result.code == 0){
                    if(status == ' '){
                        console.log('okokoko')
                        this.fillterItem(0,id)
                    }else{
                        this.fillterItem(1,id)
                    }
                    
                }else{
                    message.warning(result.msg);
                }
            })
    }
    //过滤 
    fillterItem(type,id){
        let {tableSource} = this.state;
        if(type == 0){
            tableSource.map((item,index) =>{
                if(item.id == id){
                    item.state = 1
                }
            })
            this.setState({
                tableSource, 
            })
        }
        if(type == 1){
            let _Obj = tableSource.filter((item,index) =>{
                return item.id !== id
            })
            this.setState({
                tableSource:_Obj
            })
        }
        
    }


    confirm = (item) =>{
        const {id,state} = item;
        let self = this;
        if(state == 0){
            confirm({
                title: '确定要解决吗？',
                onOk() {self.changeStatus(id)},
                okText:'确定',
                okType:'danger',
                cancelText:'取消',
                onCancel() {},
              });
        }
        
    }
    oneKeySolve = () => {
        let self = this
        confirm({
            title: '确定要解决吗？',
            onOk() {
                axios.post(DOMAIN_VIP+'/coinChangeBalance/updateall').then((res) =>{
                    let result = res.data  
                    if(result.code == 0){
                        self.requestTable()
                        
                    }else{
                        message.warning(result.msg);
                    }
                })
            },
            okText:'确定',
            okType:'danger',
            cancelText:'取消',
            onCancel() {},
          });

    }
    render() {
        const { showHide,fundsType, tableSource, pageIndex, pageSize, pageTotal,time,entrustmarket ,userid,money_min,money_max,status} = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 风控管理 > 充提异常账户 > 提现后账户余额为X
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
                                    {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                         <FundsTypeList title='充值币种' fundsType={fundsType} handleChange={this.selectFundsType} />
                                    </div> */}
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
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">解决状态:</label>
                                            <div className="col-sm-8">
                                                <Select  style={{ width: SELECTWIDTH }} value={status}  onChange={this.handleChangeSelect}>
                                                    <Option value="0">未解决</Option>
                                                    <Option value="1">解决</Option>
                                                    <Option value=" ">全部</Option>                              
                                                </Select>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-4 col-sm-4 col-xs-4 right">
                                        <div className="right">
                                            <Button type="primary" onClick={() => this.clickInquireState()}>查询</Button>
                                            <Button type="primary" onClick={() =>this.resetState()}>重置</Button>
                                            {<Button disabled={status== '0'&&tableSource.length > 0 ? false : true} type="primary" onClick={() =>this.oneKeySolve()}>一键解决</Button>}
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
                                        <Column  title='序号' dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )} />
                                        <Column title='用户ID'  dataIndex='userid' key='userid' />
                                        <Column title='币币账户（BTC）' className="moneyGreen" dataIndex='amountbtc' key='amountbtc' />
                                        <Column title='发生时间'  dataIndex='createtime' key='createtime' />
                                        <Column title='操作'  dataIndex='state'  key='state' render={(type,item) =>(
                                            <div>
                                                 <a onClick={this.addInforPage} href="javascript:;">查看</a>
                                                 <a  style={{paddingLeft:'10px'}} onClick={() =>this.confirm(item)} href="javascript:;">{type == 1?'解决':'未解决'}</a>
                                            </div>
                                           
                                        )} />
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



