//强平单
import React,{Component} from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,DatePicker,Tabs,Pagination,Select,message,Table,Modal } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20,PAGESIZE_50} from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import FundsTypeList from '../../common/select/fundsTypeList'
import Popwindows from '../modal/popwindows'
import {toThousands} from '../../../utils'
import StateSelect   from '../modal/stateselect'


const Big = require('big.js')
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
class Strongflat extends Component{
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
            modalHtml:'',
            width:'',
            title:'',
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
            createtimeS:'',
            createtimeE:'',
            conversionS :'',
            conversionE:'',
            userid:'',
            state:'0',
            moneyMin:'',
            moneyMax:'',
            freezMoneyMin:'',
            freezMoneyMax:'',
            tableList:[],
            strMoney:'',
            memo:'',
            googleCode:'',
            checkGoogle:'',
            totalMoneyMin:'',
            totalMoneyMax:'',
            limitBtn: [],
            check:'',
            item:{},
            type:'',
        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleChangeState = this.handleChangeState.bind(this)
        // this.handleChangeType = this.handleChangeType.bind(this);
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    async componentWillMount(){
        await this.requestTable()
    }
    async componentDidMount(){
        await this.requestTable()
         
    }
    componentDidUpdate(){

    }
    onChangeCheckTime(date, dateString) {
        this.setState({
            createtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            createtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            time:date
        })
       
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        
        const { pageIndex,pageSize,createtimeS,createtimeE,conversionS,conversionE,userid,pagination,state} = this.state
        axios.post(DOMAIN_VIP+'/coinLargeAccount/list',qs.stringify({
            createtimeS,createtimeE,conversionS,conversionE,userid,state,
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
    
    requestState(parameter,el){
        let id = parameter,self = this;
        Modal.confirm({
            title:'你确定要标记吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                axios.post(DOMAIN_VIP+'/coinLargeAccount/update',qs.stringify({id,state:el})).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        self.requestTable()
                    } 
                })
            },
            onCancel(){
                console.log('Cancel')
            } 
        })
    }
       
    requestModal(userid,item){
        const { fundType,userName,moneyMin,moneyMax,freezMoneyMin,freezMoneyMax,pageIndex,pageSize,totalMoneyMin,totalMoneyMax } = this.state
        axios.post(DOMAIN_VIP+'/userCapital/query',qs.stringify({
            fundType:fundType,
            userId:userid,
            userName:userName,
            moneyMin:moneyMin,
            moneyMax:moneyMax,
            freezMoneyMin:freezMoneyMin,
            freezMoneyMax:freezMoneyMax,
            pageIndex:pageIndex,
            pageSize:pageSize,
            totalMoneyMin,totalMoneyMax
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                Big.RM = 0;
               
                let tableList = result.data.list;
                // console.log(tableList)
                tableList.map((item, index) => {
                    item.index = (result.data.currPage - 1) * result.data.pageSize + index + 1;
                    item.key = index;
                    item.totalAmount = new Big(item.balance).plus(item.freez)
                })
                let mtitle = '用户资产明细'
                this.setState({
                    title:mtitle,
                    pageSize: result.data.pageSize,
                    pageTotal: result.data.totalCount,
                    visible:true,
                    width:'1200px',
                    modalHtml:<Popwindows {...this.state}  item = {item}list={tableList}/>
                })
            }else{
                message.warning(result.msg)
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
            createtimeS:'',
            createtimeE:'',
            conversionS :'',
            conversionE:'',
            userid:'',
            pageIndex:PAGEINDEX,
            state:'0'
        },()=>{
            this.requestTable()
        })
    }
    //输入时 input 设置到 state
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    handleChangeType(value) {
        this.setState({
            fundsType:value
        })
    }
    selectFundsType = v => {
        this.setState({
           fundstype:v
        })
    }
    handleChangeState(value){
        this.setState({
            state:value
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;

            this.setState({
                showHide: !showHide,
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
    saveFormRef = formRef => {
        this.formRef = formRef
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
        const { item,type } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code === 0){
                //改变操作符状态
                
                
            }else{
                message.warning(result.msg)
            }
        })
    }
    commonCheckModal = (item, type) => {
    let self = this, mtitle;
    if(item === 1){
        mtitle = '您确定要冻结该帐户吗？'
        
    }
    if(item === 2){
        mtitle = '您确定要限制该帐户提现功能吗？'
       
    }
    if(item === 3){
        mtitle = '您确定要冻结该帐户吗'
       
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
     //弹框 
     onBookModal =async (type,item) => {
       
        let userid = item.userid
        await this.requestModal(userid,item)
    }
    //弹框关闭
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    //编辑弹框
    //google弹窗关闭
    onhandleCancel = () =>{
        this.setState({
            googVisibal:false
        })
    }
    
    
    render(){
        const {time,pageSize,showHide,tableSource,pagination,googVisibal,check, conversionS, conversionE,userid,visible,title,width,pageIndex} = this.state
        return (
            <div className='right-con '>
                 <div className="page-title">
                    当前位置：风控管理 > 期货交易业务 > 大额账户监控
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
                                        <input type="text" className="form-control" name='userid' value={userid} onChange={this.handleInputChange}/>
                                   </div>
                               </div>
                           </div>
                           
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label">金额区间：</label>
                                   <div className="col-sm-8">
                                        <div className="left col-sm-5 sm-box">
                                            <input type="text" className="form-control" name="conversionS" value={conversionS} onChange={this.handleInputChange} />
                                        </div>
                                        <div className="left line34">-</div>
                                        <div className="left col-sm-5 sm-box">
                                            <input type="text" className="form-control" name="conversionE" value={conversionE}  onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                               </div>
                           </div>
                           <StateSelect value={this.state.state} changeState={(val)=>this.handleChangeState(val)}/>
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label">报警时间</label>
                                   <div className="col-sm-8">
                                        <RangePicker showTime={{
                                                    defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                        format="YYYY-MM-DD HH:mm:ss"
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
                                        <Column title='序号' dataIndex='index'key='index' render={(parameter)=>{
                                            return (
                                                    <span className='span1'>{parameter}</span>
                                                    )
                                                }}/>
                                        <Column title='用户编号' dataIndex='userid' key='userid' />
                                        <Column title='资产折算(BTC)' 
                                                dataIndex='conversion' 
                                                key='conversion' 
                                                render={(parameter,obj)=> {
                                                    return(
                                                        <div style={{textAlign:'right'}}>
                                                            <a href='javascript:void(0);' className='moneyGreen' onClick={()=> this.onBookModal(parameter,obj)}>{toThousands(parameter,true)}</a>
                                                        </div>
                                                    )
                                                        }}/>
                                        <Column title='报警时间' dataIndex='datetime' key='datetime' render = {(parameter)=>{
                                            return parameter?moment(parameter).format(TIMEFORMAT_ss):'--'
                                        }}/>
                                        <Column title='状态' dataIndex='state' key='state' render={text=>text==1?'已解决':'未解决'} />
                                        <Column title='操作'  dataIndex='id'key='id' render = {(parameter,obj)=>{
                                             return (
                                                <div>
                                                   {obj.state==0?<a href='javascript:void(0);' onClick={()=> this.requestState(parameter,1)}>标记</a>:''}
                                                </div>
                                                    )
                                            }}/>
                                    </Table>
                                </div>
                            </div>
                        </div>
                </div>
        
            </div>
            <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer = {null}
                    >
                    {this.state.modalHtml}            
                </Modal>
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
export default Strongflat

