import React,{Component} from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,DatePicker,Tabs,Pagination,Select,message,Table,Modal } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20,PAGESIZE_20} from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import  MarketRequests  from './market/marketselect'
import {toThousands} from '../../../utils'


const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
class Tradealertment extends Component{
    constructor(props){
        super(props);
        this.state ={
            googVisibal:false,
            visible:false,
            isreLoad:false,
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            tableSource:[],
            time:[],
            createtimeS:"",
            createtimeE:"",
            transrecordid:'',
            entrustuserid:'',
            entrustqtuserid:'',
            entrustmarket:'',
        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this)
        this.handleChangeSelect = this.handleChangeSelect.bind(this)
        this.handleChange=this.handleChange.bind(this)
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    componentDidMount(){
        this.stateUpdate()
    }
    componentDidUpdate(){

    }
    stateUpdate = () => {
        axios.post(DOMAIN_VIP+'/qttransrecord/view',qs.stringify({//交易预警
        })).then(res => {
            const result = res.data;
            // console.log(result)
            if(result.code == 0){
                this.requestTable()
            }else{
                message.warning(result.msg);
            }
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {        
        const { pageIndex,pageSize,pagination,entrustmarket,entrustqtuserid,createtimeS,createtimeE,transrecordid,entrustuserid,} = this.state
        axios.post(DOMAIN_VIP+'/qttransrecord/list',qs.stringify({
            entrustmarket,createtimeS,createtimeE,transrecordid,entrustqtuserid,entrustuserid,entrustmarket,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize,    
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
    //下拉菜单请求数据
    requestState(){
        
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
     //查询按钮
     inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    //重置按钮
    resetState = () => {
        this.setState({
            time:[],
            createtimeS:"",
            createtimeE:"",
            transrecordid:'',
            entrustmarket:'',
            entrustuserid:'',
            entrustmarket:'',
            entrustqtuserid:'',
            pageIndex:PAGEINDEX

        },()=>{
            this.requestTable()
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
            
            this.setState({
                showHide: !showHide,
            })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name] : value,
        })
    }
    handleCreate = () =>{
        const form = this.formRef.props.form;
        form.validateFields((err, valus) => {
            if(err){
                return;
            }
            form.resetFields();
            this.modalGoogleCodeBtn(valus)
        })
    }
    handleChangeSelect(value) {
        this.setState({
            entrustmarket:value
        })
    }
    handleChange(value){
        this.setState({
            state:value
        })
    }
    saveFormRef = formRef => {
        this.formRef = formRef
    }
    onChangeCheckTime(date, dateString) {
        this.setState({
            createtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            createtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            time:date
        })
       
    }
    
    commonCheckModal = (item, type) => {
    let self = this, mtitle;
    if(item === 2){
        mtitle = '您确定要限制帐户体现功能吗？'
        
    }else if(item === 3){
        mtitle = '您确定要冻结该帐户吗？'
       
    }else if(item === 4){
        mtitle = '您确定要冻结该帐户吗？'
       
    }else{
       return;
    }
    Modal.confirm({
        title: mtitle,
        okText: '确定',
        okType: 'more',
        cancelText: '取消',
        onOk() {
            self.modalGoogleCode(item,type);
            
        },
        onCancel() {
            console.log('Cancel');
        },
    });
}
    //google弹窗关闭
    onhandleCancel = () =>{
        this.setState({
            googVisibal:false
        })
    }
    //google 验证弹窗
    modalGoogleCode = (item,type) => {
        this.setState({
            googVisibal:true,
            item,
            googletype:type
        })
    }
    //google按钮
    modalGoogleCodeBtn = (value) => {
        const { item,googletype } = this.state
        const {googleCode} = value
        let oDiv = document.getElementById('myChange');
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                oDiv.innerText = '取消提现'
                this.setState({
                    googVisibal:false
                })
                
            }else{
                message.warning(result.msg)
            }
        })
    }
    
    render(){
        const {time,pageSize,showHide,tableSource,pagination,googVisibal,check,entrustmarket,entrustqtuserid,entrustuserid,transrecordid,pageIndex} = this.state
        return (
            <div className='right-con '>
                 <div className="page-title">
                    当前位置：风控管理 > 币币交易业务 > 交易预警
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
            <div className="row">
               <div className="col-md-12 col-sm-12 col-xs-12">
               {showHide &&<div className="x_panel">
                       <div className="x_content">
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label">用户编号：</label>
                                   <div className="col-sm-8">
                                        <input type="text" className="form-control" name='entrustuserid' value={entrustuserid} onChange={this.handleInputChange}/>
                                   </div>
                               </div>
                           </div>
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <MarketRequests market={entrustmarket}  handleChange={this.handleChangeSelect} col = '3'/>
                           </div>
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label">刷量编号</label>
                                   <div className="col-sm-8">
                                        <input type="text" className="form-control" name='entrustqtuserid' value={entrustqtuserid} onChange={this.handleInputChange}/>
                                   </div>
                               </div>
                           </div>
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label">成交编号</label>
                                   <div className="col-sm-8">
                                        <input type="text" className="form-control" name='transrecordid' value={transrecordid} onChange={this.handleInputChange}/>
                                   </div>
                               </div>
                           </div>
                           
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label">报警时间</label>
                                   <div className="col-sm-8">
                                         <RangePicker 
                                            showTime={{
                                                defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                            }}
                                            format={TIMEFORMAT_ss}
                                            placeholder={['Start Time', 'End Time']}
                                            onChange={this.onChangeCheckTime }
                                            value={time}
                                        />
                                   </div>
                               </div>
                           </div>
                           <div className="col-md-4 col-sm-4 col-xs-4 right">
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
                                    <Table dataSource={tableSource} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}
                                     onChange={this.sorter}
                                        >
                                        <Column title='序号' dataIndex='index'key='index' />
                                        <Column title='成交编号' dataIndex='transrecordid' key='transrecordid' />
                                        <Column title='交易市场' dataIndex='entrustmarket' key='entrustmarket' render={(parameter)=>{
                                            
                                            return parameter.toUpperCase()
                                        }}/>
                                        <Column title='交易类型' dataIndex='entrusttype' key='entrusttype' render={(parameter)=> parameter===1?'买入':'卖出'}/>
                                        <Column title='刷量账号编号' dataIndex='entrustqtuserid' key='entrustqtuserid'/>
                                        <Column title='用户编号' dataIndex='entrustuserid' key='entrustuserid' />
                                        <Column title='成交价格' dataIndex='entrustprice' key='entrustprice' className='moneyGreen' render={(parameter)=> toThousands(parameter,true)}/>
                                        <Column title='成交数量' dataIndex='entrustnum' key='entrustnum' />
                                        <Column title='成交总价' dataIndex='entrustValue' key='entrustValue' className='moneyGreen' render={(parameter)=> toThousands(parameter,true)}/>
                                        <Column title='报警时间' dataIndex='addtime' key='addtime' render = {(parameter)=>{
                                            return parameter?moment(parameter).format(TIMEFORMAT_ss):'--'
                                        }}/>
                                    </Table>
                                </div>
                            </div>
                        </div>
                </div>
        
            </div>
            <GoogleCode 
                wrappedComponentRef={this.saveFormRef}
                check={check}
                handleInputChange={this.handleInputChange}
                mid='CAE'
                visible={googVisibal}
                onCancel={this.onhandleCancel}
                onCreate={this.handleCreate}/>
       </div>
        )
    }
}
export default Tradealertment
