import React,{Component} from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,DatePicker,Tabs,Pagination,Select,message,Table,Modal } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import MarketRequests from '../../common/select/marketrequests'
import {toThousands,pageLimit} from '../../../utils'
import StateSelect   from '../modal/stateselect'
// import './gongong.js'
//大额挂单账户
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
const confirm = Modal.confirm
class Keyinformationchange extends Component{
    constructor(props){
        super(props);
        this.state ={
            googVisibal:false,
            visible:false,
            pageIndex :PAGEINDEX,
            pageSize:PAGESIZE,
            tableSource:[],
            showHide:true,
            isreLoad:false,
            aList:['取消限制交易','取消限制提现','取消冻结账户'],
            unchanged:['限制交易','限制提现','冻结账户'],
            store:['限制交易','限制提现','冻结账户'],
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE,
                pageSize:PAGESIZE,
                pageTotal:DEFAULTVALUE,
                onShowSizeChange: this.onShowSizeChange,
                onChange: this.changPageNum,
                // 
                
                // current:this.pageIndex,
            },
            time:[],
            createtimeS:"",
            createtimeE:"",
            numbersS:'',
            numbersE:'',
            userid:'',
            entrustmarket:'',
            state:'0',
            RowKeys: [],
            types:'',
            limitBtn:[]
        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this)
        this.handleChangeSelect = this.handleChangeSelect.bind(this)
        this.handleChangeState = this.handleChangeState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    componentDidMount(){
        this.setState ({
            limitBtn : pageLimit('coinLargeOrder',this.props.permissList)
        })
        this.requestTable()
       
    }
    componentWillUpdate(){
        
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
        
        const { pageIndex,pageSize,pagination,undstype,amountS,amountE,configtimeS,configtimeE,type,entrustmarket,sellerId,puchaserId,createtimeS,createtimeE,userid,numbersS,numbersE,state,types} = this.state
        axios.post(DOMAIN_VIP+'/coinLargeOrder/list',qs.stringify({
            entrustmarket,amountS,amountE,configtimeS,configtimeE,type,sellerId,puchaserId,createtimeS,createtimeE,userid,numbersS,numbersE,state,types,
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
                pagination.onShowSizeChange = this.onShowSizeChange;
                pagination.pageSize=result.data.pageSize
                this.setState({
                    tableSource:tableSource,
                    pagination,
                },)
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
                axios.post(DOMAIN_VIP+'/coinLargeOrder/update',qs.stringify({id,state:el})).then(res => {
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
            createtimeS:"",
            createtimeE:"",
            numbersS:'',
            numbersE:'',
            userid:'',
            entrustmarket:'',
            time:[],
            state:'0',
            pageIndex:PAGEINDEX,
            types:''
        },()=>{
            this.requestTable()
        })
    }
    //点击收起
    clickHide() {
        let { showHide,pageSize } = this.state;
           
            this.setState({
                showHide: !showHide,
            })
    }
    //点击分页
    
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestTable(page,pageSize))

    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requestTable(current,size))
    }

    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name] : value,
        })
    }
    handleChangeSelect(value) {
        this.setState({
            entrustmarket:value
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
    handleChangeState(value){
        this.setState({
            state:value,
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
        const { item,googletype } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code === 0){
                //改变操作符状态
                // const oldList = this.state.store
                // let oAs = document.getElementsByClassName('mar10')
                // console.log(oAs.length)
                // for(let i = 0, len = oAs.length - 1;i < len; i++ ){
                //     let newString =this.state.unchanged[item-1].slice(0,2)
                //     if(newString === '取消' && item-1 === i){
                //         this.state.unchanged.splice(i,1,oldList[i]);
                //         this.forceUpdate();
                //     }
                //     if(item-1 === i && newString !== '取消'){
                //         let newList = this.state.aList[item-1]
                //         let newUnchanged = this.state.unchanged
                //         newUnchanged.splice(i,1,newList)
                //        this.setState({
                //            unchanged:newUnchanged
                //        })
                //     }
                // }
                this.setState({
                    googVisibal:false
                })
                
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
    //google弹窗关闭
    onhandleCancel = () =>{
        this.setState({
            googVisibal:false
        })
    }
    onSelectChange = (selectedRowKeys) => {
        this.setState({
            RowKeys: selectedRowKeys
        });
    };
    batchMarking = () => {
        let self = this;
        Modal.confirm({
            title: '你确定要批量标记选中的数据吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + '/coinLargeOrder/updateall', qs.stringify({idlist:self.state.RowKeys.toString()})).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.setState({
                            RowKeys: []
                        }, ()=> {
                            self.requestTable();
                        });
                    }
                })
            },
            onCancel() {
            }
        })
    }
    selectOrientation = types => {this.setState({types})}
    render(){
        const {time,showHide,tableSource,pagination,googVisibal,check,numbersS,numbersE,userid,entrustmarket,pageIndex,RowKeys,types,limitBtn} = this.state
        return (
            <div className='right-con '>
                 <div className="page-title">
                    当前位置：风控管理 > 币币交易业务 > 大额挂单账户
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
            <div className="row">
               <div className="col-md-12 col-sm-12 col-xs-12">
               {showHide &&<div   className="x_panel">
                       <div className="x_content">
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label" htmlFor='userID'>用户编号：</label>
                                   <div className="col-sm-8">
                                        <input type="text" className="form-control" name='userid' value={userid} onChange ={this.handleInputChange} id='userID'/>
                                   </div>
                               </div>
                           </div>
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <MarketRequests market={entrustmarket}  handleChange={this.handleChangeSelect} col='3'/>
                           </div>
                           
                           
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label">委托数量：</label>
                                   <div className="col-sm-8">
                                        <div className="left col-sm-5 sm-box">
                                            <input type="text" className="form-control" name="numbersS" value={numbersS} onChange={this.handleInputChange} />
                                        </div>
                                        <div className="left line34">-</div>
                                        <div className="left col-sm-5 sm-box">
                                            <input type="text" className="form-control" name="numbersE" value={numbersE}  onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                               </div>
                           </div>
                            <StateSelect value={this.state.state} changeState={(val)=>this.handleChangeState(val)}/>
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className='col-sm-3 control-label' >交易类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={types} style={{ width: SELECTWIDTH }} onChange={this.selectOrientation}>
                                                <Option value=''>请选择</Option>
                                                <Option value={1}>买入</Option>
                                                <Option value={0}>卖出</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
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
                                        {limitBtn.indexOf('updateall') > -1 &&<Button type="primary" disabled={!RowKeys.length} onClick={this.batchMarking}>批量标记</Button>}
                                    </div>
                            </div>
                            
                       </div>
                   </div>}
                   <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table dataSource={tableSource} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}
                                           rowSelection={{
                                               selectedRowKeys: RowKeys,
                                               onChange: this.onSelectChange,
                                           }}
                                        >
                                        <Column title='序号' dataIndex='index'key='index' />
                                        <Column title='用户编号' dataIndex='userid' key='userid' />
                                        <Column title='交易市场' dataIndex='entrustmarket' key='entrustmarket' />
                                        <Column title='交易类型' dataIndex='types' key='types' render={text=>text ==0?'卖出':'买入'} />
                                        <Column title='委托均价' dataIndex='unitprice' key='unitprice' className='moneyGreen' render={(parameter)=> toThousands(parameter,true)}/>
                                        <Column title='委托数量' dataIndex='numbers' key='numbers' />
                                        <Column title='委托总额' dataIndex='totalmoney' key='totalmoney' className='moneyGreen' render={(parameter)=> toThousands(parameter,true)}/>
                                        <Column title='报警时间' dataIndex='createtime' key='createtime' render = {(parameter)=>{
                                            return parameter?moment(parameter).format(TIMEFORMAT_ss):'--'
                                        }}/>
                                        <Column title='状态' dataIndex='state' key='state' render={text=>text==1?'已解决':'未解决'} />
                                        <Column title='操作' dataIndex='id'key='id' render = {(parameter,record)=>{
                                           return (
                                               <div>
                                                   {limitBtn.indexOf('update') > -1 &&record.state==0?<a href='javascript:void(0);' onClick={()=> this.requestState(parameter,1)}>标记</a>:''}
                                               </div>
                                           )
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
export default Keyinformationchange

